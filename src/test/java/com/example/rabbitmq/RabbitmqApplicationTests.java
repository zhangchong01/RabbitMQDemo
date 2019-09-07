package com.example.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RabbitmqApplicationTests {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendMsg() {
        log.info("send msg");

        // 设置消息入队失败回调
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                log.debug(s);
                log.debug("confirm");
            }
        });

        // 设置消息发送至交换机回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                log.debug(s);
                log.debug("return");
            }
        });

        MessageProperties messageProperties = new MessageProperties();
        // 开启消息持久化
        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        messageProperties.setReceivedDeliveryMode(MessageDeliveryMode.PERSISTENT);
        messageProperties.setMessageId(UUID.randomUUID().toString());
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        Exception exception = new Exception("msg");
        Message message = messageConverter.toMessage(exception, messageProperties);
        rabbitTemplate.send(RabbitConstant.EXCHANGE, "demo.test", message);
    }
}
