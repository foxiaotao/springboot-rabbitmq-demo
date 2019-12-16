package com.foxiaotao.springbootmq.deadletter.receive;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ReceMsg {


    @RabbitListener(queues = "${spring.mq.queue.dlx}")
    public void dlxListener(Message message, Channel channel) throws IOException {
        System.out.println(new String(message.getBody()));

        //对消息进行业务处理....
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
