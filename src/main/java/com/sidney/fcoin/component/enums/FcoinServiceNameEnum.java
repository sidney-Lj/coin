package com.sidney.fcoin.component.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FcoinServiceNameEnum {
    SERVER_TIME("查询服务器时间"),
    CURRENCIES("查询可用币种"),
    SYMBOLS("查询可用交易对"),
    MARKET("行情"),

    ORDERS("创建新的订单");

    private String desc;

}
