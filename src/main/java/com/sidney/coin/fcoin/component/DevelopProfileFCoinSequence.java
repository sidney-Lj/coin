package com.sidney.coin.fcoin.component;

import com.sidney.coin.fcoin.component.properties.FCoinProperties;
import com.sidney.coin.helper.NetTools;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.Inet4Address;

public class DevelopProfileFCoinSequence extends FCoinSequence {

    private String ip;
    private String port = "8080";

    public DevelopProfileFCoinSequence(RedisTemplate<String, String> redisTemplate, FCoinProperties fCoinProperties) {
        super(redisTemplate, fCoinProperties);
        this.ip = NetTools.getLocalAddress(address -> address instanceof Inet4Address).getHostAddress();
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String getRequestNo() {
        String finalIp = this.ip.replaceAll("\\.", "_");
        return finalIp + "-" + port + "-" + super.getRequestNo();
    }

    @Override
    public String getBatchNo() {
        String finalIp = this.ip.replaceAll("\\.", "_");
        return finalIp + "-" + port + "-" + super.getBatchNo();
    }

}
