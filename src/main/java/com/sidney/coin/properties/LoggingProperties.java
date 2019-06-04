package com.sidney.coin.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("logging")
@Setter
@Getter
public class LoggingProperties {
    private String contextName = "";
    @NestedConfigurationProperty
    private Stash stash = new Stash();
    @NestedConfigurationProperty
    private RollingPolicy rollingPolicy = new RollingPolicy();

    @Setter
    @Getter
    public static class RollingPolicy {
        private String maxFileSize = "200MB";
        private String maxHistory = "500";
        private String totalSizeCap = "100GB";
    }

    @Setter
    @Getter
    public static class Stash {
        private String destination;
        private String type = "";
        @NestedConfigurationProperty
        private Trace trace = new Trace();
    }

    @Setter
    @Getter
    public static class Trace {
        private boolean enabled = true;
    }

}
