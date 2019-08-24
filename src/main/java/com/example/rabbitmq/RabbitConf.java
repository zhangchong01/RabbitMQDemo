package com.example.rabbitmq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.CacheMode;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangChong created on 2019/08/24
 **/
@Configuration
public class RabbitConf {
    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        // 设置虚拟主机
        factory.setVirtualHost(RABBIT.VIRTUAL_HOST);
        // 消息送达交换机确认
        factory.setPublisherConfirms(true);
        factory.setPublisherReturns(true);
        // 设置缓存模式
        factory.setCacheMode(CacheMode.CHANNEL);
        // 设置缓存数
        factory.setChannelCacheSize(8);
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(CachingConnectionFactory cachingConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
        // 消息转换器
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        // ack模式
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // 消费者并发数
        factory.setConcurrentConsumers(8);
        // 预取消息数
        factory.setPrefetchCount(8);
        return factory;
    }
}
