package com.sidney.coin.fcoin.component;

import com.sidney.coin.fcoin.component.properties.FCoinProperties;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class FCoinSequence {

    private static final String BATCH_NO_KEY = "batch-no:fcoin";
    private static final String REQ_NO_KEY = "req-no:fcoin";
    private FastDateFormat dayFormat = FastDateFormat.getInstance("yyyyMMddHHmmss");

    private final RedisTemplate<String, String> redisTemplate;
    private final FCoinProperties fCoinProperties;

    public FCoinSequence(RedisTemplate<String, String> redisTemplate, FCoinProperties fCoinProperties) {
        this.redisTemplate = redisTemplate;
        this.fCoinProperties = fCoinProperties;
    }

    public String getRequestNo() {
        return fCoinProperties.getPlatformNo() + dayFormat.format(System.currentTimeMillis()) + getDurationSeq(REQ_NO_KEY, 24);
    }

    public String getBatchNo() {
        return fCoinProperties.getPlatformNo() + dayFormat.format(System.currentTimeMillis()) + getDurationSeq(BATCH_NO_KEY, 24);
    }

    private long getDurationSeq(String key, int durationHours) {
        Long seq = redisTemplate.opsForValue().increment(key, 1);
        if (1 == seq) {
            redisTemplate.expire(key, durationHours, TimeUnit.HOURS);
        }
        return seq;
    }

}
