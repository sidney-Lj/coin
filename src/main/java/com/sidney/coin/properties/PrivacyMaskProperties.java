package com.sidney.coin.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties("privacy-mask")
public class PrivacyMaskProperties {
    private String aesKey = "D/6ZGt4Lq/pcPbQ8O1q52A==";
}
