package com.example.rabbitmq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author ZhangChong created on 2019/10/21
 **/
@Component
public class MsgSender implements InitializingBean {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private CachingConnectionFactory cachingConnectionFactory;

    @Override
    public void afterPropertiesSet() {
        rabbitTemplate.setConnectionFactory(cachingConnectionFactory);
        rabbitTemplate.setMandatory(true);
    }

    public void send(String exchange, String routingKey, Object msg) {
        MessageProperties messageProperties = new MessageProperties();
        // 开启消息持久化
        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        messageProperties.setReceivedDeliveryMode(MessageDeliveryMode.PERSISTENT);
        messageProperties.setMessageId(UUID.randomUUID().toString());

        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        Message message = messageConverter.toMessage(msg, messageProperties);
        rabbitTemplate.send(exchange, routingKey, message);
    }

    public void sendWithCallBack(String exchange, String routingKey, Object msg,
                                 ConfirmCallback confirmCallback, ReturnCallback returnCallback) {
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);

        send(exchange, routingKey, msg);
    }
}
