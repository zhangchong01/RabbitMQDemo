package com.example.rabbitmq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
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
        // 消息送达交换机确认
        factory.setPublisherConfirms(true);
        factory.setPublisherReturns(true);
        factory.setChannelCacheSize(8);
        return factory;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new ContentTypeDelegatingMessageConverter(new Jackson2JsonMessageConverter());
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(CachingConnectionFactory cachingConnectionFactory, MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
        // 消息转换器
        factory.setMessageConverter(messageConverter);
        // ack模式
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // 消费者并发数
        factory.setConcurrentConsumers(8);
        // 预取消息数
        factory.setPrefetchCount(8);
        return factory;
    }
}
