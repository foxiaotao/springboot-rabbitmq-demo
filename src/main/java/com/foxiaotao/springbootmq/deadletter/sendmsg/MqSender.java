package com.foxiaotao.springbootmq.deadletter.sendmsg;


import com.foxiaotao.springbootmq.deadletter.model.Order;
import com.foxiaotao.springbootmq.deadletter.util.JSONTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Slf4j
@Controller
@RequestMapping("test")
public class MqSender {

    @Resource(name = "orderRabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.mq.exchange}")
    private String orderExchange;

    @Value("${spring.mq.routingKey}")
    private String orderRoutingKey;

    /**
     * 发送带有过期时间的消息
     */
    @GetMapping("/sendDlx")
    public void sendDlx() {
        Order order = new Order();
        order.setItemId(1);
        order.setStatus(1);
        log.info("send msg" + System.currentTimeMillis());
        rabbitTemplate.convertAndSend(orderExchange, orderRoutingKey,
                JSONTools.serialize(order), message -> {
                    message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    message.getMessageProperties().setExpiration("30000");
                    return message;
                });
    }

}
