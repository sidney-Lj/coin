package com.sidney.coin.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(AsyncHttpClientProperties.PREFIX)
@Setter
@Getter
public class AsyncHttpClientProperties {
    public static final String PREFIX = "async-http-client";

    private boolean enabled = true;
    private int ioThreadCount = Runtime.getRuntime().availableProcessors() * 2;
    private int connectTimeOut = 60000;
    private int soTimeout = 0;
    private int maxConnPerRoute = 100;
    private int maxConnTotal = 300;

}
