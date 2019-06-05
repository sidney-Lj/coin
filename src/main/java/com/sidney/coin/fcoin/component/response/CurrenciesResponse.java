package com.sidney.coin.fcoin.component.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CurrenciesResponse extends FCoinGetResponse {
    /**
     * 币种
     */
    private String [] data;
}
