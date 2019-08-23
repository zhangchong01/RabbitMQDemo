package com.example.rabbitmq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangChong created on 2019/08/24
 **/
@Configuration
public class RabbitConf {
    @Bean
    public CachingConnectionFactory cachingConnectionFactory(){
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setPublisherConfirms(true);
        factory.setPublisherReturns(true);
        factory.setChannelCacheSize(8);
        return factory;
    }

//    @Bean
//    public RabbitAdmin rabbitAdmin(CachingConnectionFactory cachingConnectionFactory) {
//        return new RabbitAdmin(connectionFactory);
//    }

    @Bean
    public MessageConverter messageConverter() {
        return new ContentTypeDelegatingMessageConverter(new Jackson2JsonMessageConverter());
    }

//    @Bean
//    public RabbitTemplate rabbitTemplate(CachingConnectionFactory cachingConnectionFactory, MessageConverter messageConverter) {
//        RabbitTemplate template = new RabbitTemplate(cachingConnectionFactory);
//        template.setMandatory(true);
//        template.setMessageConverter(messageConverter);
//        return template;
//    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(CachingConnectionFactory cachingConnectionFactory, MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
}