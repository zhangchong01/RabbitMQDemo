package com.example.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyBean {
    @Bean
    public Queue queue() {
        // 队列持久化
        Queue queue = new Queue(RABBIT.QUEUE, true);
        return queue;
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(RABBIT.EXCHANGE);
    }

    @Bean
    public Binding bind(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(RABBIT.ROUTING_KEY);
    }

    @RabbitListener(queues = RABBIT.QUEUE, concurrency = "8")
    public void processMessage(Message message, Channel channel, String content) throws Exception {
        log.debug("receive msg:", message);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            // int i = 1 / 0;
            channel.basicAck(deliveryTag, false);
            log.info(content);
        } catch (Exception e) {
            // channel.basicNack(deliveryTag, false, false);
            // 拒绝消息
            channel.basicReject(deliveryTag, false);
            throw e;
        }
    }
}

