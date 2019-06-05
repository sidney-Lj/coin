package com.sidney.coin.fcoin.component.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class SymbolsResponse extends FCoinGetResponse {

    private Symbols [] data;

    public class Symbols{
        private String name;

        private String base_currency;

        private String quote_currency;

        private BigDecimal price_decimal;

        private BigDecimal amount_decimal;
    }
}
