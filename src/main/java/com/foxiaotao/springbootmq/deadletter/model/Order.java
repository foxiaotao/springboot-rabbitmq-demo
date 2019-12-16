package com.foxiaotao.springbootmq.deadletter.model;

import lombok.Data;

@Data
public class Order {
    private Integer itemId;

    private Integer status;
}
