package com.foxiaotao.springbootmq.deadletter.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RabbitMqConfig {

    @Value("${spring.mq.host}")
    private String host;
    @Value("${spring.mq.port}")
    private int port;
    @Value("${spring.mq.virtualHost}")
    private String vhost;
    @Value("${spring.mq.username}")
    private String username;
    @Value("${spring.mq.password}")
    private String password;

    @Value("${spring.mq.exchange}")
    private String orderExchange;
    @Value("${spring.mq.queue}")
    private String orderQueue;
    @Value("${spring.mq.routingKey}")
    private String orderRoutingKey;

    @Value("${spring.mq.exchange.dlx}")
    private String dlxExchange;
    @Value("${spring.mq.queue.dlx}")
    private String dlxQueue;
    @Value("${spring.mq.routingKey.dlx}")
    private String dlxRoutingKey;


    @Bean("orderConnectionFactory")
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        connectionFactory.setChannelCacheSize(180 * 1000);
        //connectionFactory.setConnectionCacheSize(1024);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);
        connectionFactory.setPublisherReturns(false);
        connectionFactory.setPublisherConfirms(false);
        return connectionFactory;
    }


    /**
     * 申明死信队列
     * @return
     */
    @Bean("dlxExchangeBean")
    public DirectExchange dlxExchangeBean() {
        return new DirectExchange(dlxExchange);
    }

    @Bean("dlxQueueBean")
    public Queue dlxQueueBean() {
        return new Queue(dlxQueue);
    }


    @Bean
    public Binding binding(@Qualifier("dlxExchangeBean") DirectExchange dlxExchange, @Qualifier("dlxQueueBean") Queue dlxQueue) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with(dlxRoutingKey);
    }




    /**
     * 申明业务队列
     * @return
     */
    @Bean("orderExchangeBean")
    public DirectExchange orderExchangeBean() {
        return new DirectExchange(orderExchange);
    }

    @Bean("orderQueueBean")
    public Queue orderQueueBean() {
        Map<String,Object> arguments = new HashMap<>(4);
        // 绑定该队列到私信交换机
        arguments.put("x-dead-letter-exchange", dlxExchange);
        arguments.put("x-dead-letter-routing-key", dlxRoutingKey);
        return new Queue(orderQueue,true,false,false, arguments);
    }

    @Bean
    public Binding orderBinding(@Qualifier("orderExchangeBean") DirectExchange orderExchange, @Qualifier("orderQueueBean") Queue orderQueue) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(orderRoutingKey);
    }


    @Bean(name = "orderRabbitTemplate")
    public RabbitTemplate contractTemplate(@Qualifier("orderConnectionFactory") ConnectionFactory connectionFactory, @Qualifier("orderExchangeBean") DirectExchange orderExchangeBean) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange(orderExchangeBean.getName());
        return template;
    }

}
