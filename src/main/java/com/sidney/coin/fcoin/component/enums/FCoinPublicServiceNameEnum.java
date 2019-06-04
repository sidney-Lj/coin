package com.sidney.coin.fcoin.component.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公开接口
 */
@Getter
@AllArgsConstructor
public enum FCoinPublicServiceNameEnum {
    SERVER_TIME("server-time","查询服务器时间"),
    CURRENCIES("currencies","查询可用币种"),
    SYMBOLS("symbols","查询可用交易对"),
    MARKET("market","行情");

    private String url;
    private String desc;

}
