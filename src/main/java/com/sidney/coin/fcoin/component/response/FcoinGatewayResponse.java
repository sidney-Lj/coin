package com.sidney.coin.fcoin.component.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Setter
@Getter
public class FcoinGatewayResponse implements Serializable {
    private String action;
    private Map<String, Object> params;
}
