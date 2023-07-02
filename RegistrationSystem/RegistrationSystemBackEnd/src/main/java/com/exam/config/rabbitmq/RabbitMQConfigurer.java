package com.exam.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * RabbitMQ 六种模式
 * 简单模式：一个队列一个消费者，类似直接指定路由发消息（无交换机）
 * 工作队列模式：一个队列多个消费者，也是对直接指定路由的应用
 * 发布订阅模式：对Fanout交换机的应用，无需绑定路由，发送到交换机全有
 * 路由模式：对Direct交换机的应用，需要绑定路由，，路由完全匹配才能发送
 * Topic模式：绑定路由的时候，可以使用通配符.#一个或多个.*一个，发送消息到队列时，指定topic交换机，路由能匹配上就可以最灵活了
 * RPC：不是消息队列远程调用
 *
 * @author gaoge
 * @since 2023/2/9 16:20
 */
@Configuration
public class RabbitMQConfigurer {
    @Bean("connectionFactory")
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("82.157.42.25:5672");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean("rabbitMessenger")
    public RabbitTemplate rabbitMessenger(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean("oneQueue")
    public Queue oneQueue() {
        HashMap<String, Object> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("gaoge", "高歌");
        return new Queue("oneQueue",true,false,false);
    }

    @Bean("twoQueue")
    public Queue twoQueue() {
        HashMap<String, Object> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("gaoge", "高歌");
        return new Queue("twoQueue",true,false,false);
    }

    @Bean("oneExchange")
    public TopicExchange oneExchange() {
        return new TopicExchange("oneExchange", true, false);
    }

    /**
     *直接的：绑定交换机没用，直接用队列名当路由就可以访问
     * @return
     */
    @Bean("twoExchange")
    public DirectExchange twoExchange() {
        return new DirectExchange("twoExchange", true, false);
    }

    @Bean("threeExchange")
    public FanoutExchange threeExchange() {
        return new FanoutExchange("threeExchange");
    }


    @Bean("oneBinding")
    public Binding oneBinding(@Qualifier("oneQueue") Queue oneQueue,
                              @Qualifier("oneExchange") TopicExchange exchange) {
        return BindingBuilder.bind(oneQueue).to(exchange).with("gaoge");
    }


    @Bean("twoBinding")
    public Binding twoBinding(@Qualifier("twoQueue") Queue twoQueue,
                              @Qualifier("oneExchange") TopicExchange exchange) {
        return BindingBuilder.bind(twoQueue).to(exchange).with("gaoge.#");
    }

    /**
     *直接的：绑定交换机没用，直接用队列名当路由就可以访问
     * @return
     */
    @Bean("threeBinding")
    public Binding threeBinding(@Qualifier("oneQueue") Queue oneQueue,
                              @Qualifier("twoExchange") DirectExchange exchange) {
        return BindingBuilder.bind(oneQueue).to(exchange).with("111");
    }

    @Bean("fourBinding")
    public Binding fourBinding(@Qualifier("oneQueue") Queue oneQueue,
                                @Qualifier("threeExchange") FanoutExchange exchange) {
        return BindingBuilder.bind(oneQueue).to(exchange);
    }

    @Bean("fiveBinding")
    public Binding fiveBinding(@Qualifier("twoQueue") Queue twoQueue,
                               @Qualifier("threeExchange") FanoutExchange exchange) {
        return BindingBuilder.bind(twoQueue).to(exchange);
    }




    @Bean("oneMessageListenerContainer")
    public MessageListenerContainer oneMessageListenerContainer(@Qualifier("connectionFactory") ConnectionFactory connectionFactory,
                                                                @Qualifier("oneChannelAwareMessageListener") OneChannelAwareMessageListener oneChannelAwareMessageListener ){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setExposeListenerChannel(true);
        container.setQueueNames("oneQueue","twoQueue");
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setMessageListener(oneChannelAwareMessageListener);
        return container;
    }
}
