package com.sidney.coin.fcoin.component.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServerTimeResponse extends FCoinGetResponse {
    /**
     * 服务器时间
     */
    private Long data;
}
