package com.sidney.coin.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("zk")
@Setter
@Getter
public class ZookeeperProperties {
    private String connections;
    private int connectionTimeoutMs = 3000;
    private int sessionTimeoutMs = 5000;
    private int maxRetries = 3;
    private int baseSleepTimeMs = 1000;
}
