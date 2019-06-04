package com.sidney.coin.fcoin.component.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServerTimeResponse extends FCoinResponse {
    /**
     * 原换卡请求流水号
     */
    private String originalRequestNo;
}
