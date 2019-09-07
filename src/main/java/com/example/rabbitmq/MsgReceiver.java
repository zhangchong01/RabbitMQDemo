package com.example.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MsgReceiver {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private BusinessService businessService;

    @RabbitListener(queues = RabbitConstant.QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void receive(Message message, Channel channel, Map content) throws Exception {
        log.info("消费消息:{}", message);
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();
        String messageId = messageProperties.getMessageId();

        if (messageProperties.isRedelivered() || hasDuplicateKey(messageId)) {
            // 拒绝消息
            channel.basicReject(deliveryTag, false);
            return;
        }

        try {
            businessService.processMessage(content);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("exception:{}", e);
            channel.basicNack(deliveryTag, false, false);
            transferToDeadQueue(messageProperties.getReceivedRoutingKey(), content);
        } finally {
            saveMsgIdToRDS(messageId, content);
        }
    }

    private Boolean hasDuplicateKey(String messageId) {
        if (StringUtils.isEmpty(messageId)) {
            return Boolean.TRUE;
        }
        return (null != redisTemplate.opsForValue().get(messageId));
    }


    private void saveMsgIdToRDS(String messageId, Map msgBody) {
        redisTemplate.opsForValue().setIfAbsent(messageId, msgBody, 1, TimeUnit.DAYS);
    }

    private void transferToDeadQueue(String routingKey, Map msgBody) {
        // 设置消息入队失败回调
        MessageProperties messageProperties = new MessageProperties();
        // 开启消息持久化
        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        messageProperties.setReceivedDeliveryMode(MessageDeliveryMode.PERSISTENT);

        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        Message message = messageConverter.toMessage(msgBody, messageProperties);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.send(RabbitConstant.DEAD_EXCHANGE, "dead." + routingKey, message);
    }
}

