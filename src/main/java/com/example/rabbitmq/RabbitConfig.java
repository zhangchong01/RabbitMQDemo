package com.example.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.CacheMode;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhangChong created on 2019/08/24
 **/
@Configuration
public class RabbitConfig {
    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        // 设置虚拟主机
        factory.setVirtualHost(RabbitConstant.VIRTUAL_HOST);
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

    /*******死信队列*******/
    @Bean(name = "deadQueue")
    public Queue deadQueue() {
        Map<String, Object> args = new HashMap<>();
        // 设置队列长度
        // args.put("x-max-length", 1000);
        args.put("x-dead-letter-exchange", RabbitConstant.DEAD_EXCHANGE);
        // 持久化队列
        return new Queue(RabbitConstant.DEAD_QUEUE, true, false, false, args);
    }

    @Bean(name = "deadExchange")
    public TopicExchange deadExchange() {
        return new TopicExchange(RabbitConstant.DEAD_EXCHANGE);
    }

    @Bean(name = "deadBind")
    public Binding deadBind(@Qualifier(value = "deadQueue") Queue queue, @Qualifier(value = "deadExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(RabbitConstant.DEAD_ROUTING_KEY);
    }

    /*******死信队列*******/

    @Bean(name = "demoQueue")
    public Queue demoQueue() {
        return new Queue(RabbitConstant.QUEUE, true, false, false);
    }

    @Bean(name = "demoExchange")
    public TopicExchange demoExchange() {
        return new TopicExchange(RabbitConstant.EXCHANGE);
    }

    @Bean(name = "demoBind")
    public Binding demoBind(@Qualifier(value = "demoQueue") Queue queue, @Qualifier(value = "demoExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(RabbitConstant.ROUTING_KEY);
    }
}
