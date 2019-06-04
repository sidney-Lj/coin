package com.sidney.coin.fcoin.configuration;

import com.google.common.base.Preconditions;
import com.sidney.coin.annotation.ProfileSpecified;
import com.sidney.coin.component.AsyncHttpClients;
import com.sidney.coin.fcoin.component.*;
import com.sidney.coin.fcoin.component.properties.FCoinProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class FCoinConfiguration {

    @Configuration
    @ConditionalOnProperty(prefix = "fcoin", name = "enabled", havingValue = "true")
    @EnableConfigurationProperties({FCoinProperties.class})
    public static class LanMaoServiceConfiguration {

        @Autowired
        private FCoinProperties properties;

        @Bean
        @ConditionalOnMissingBean
        public FCoinClient lanMaoClient(AsyncHttpClients asyncHttpClients, RedisTemplate<String, String> redisTemplate) {
            Preconditions.checkArgument(StringUtils.isNotBlank(properties.getPlatformNo()), "fcoin.platformNo not specified");
            Preconditions.checkNotNull(asyncHttpClients, "AsyncHttpClients is not enabled");
            Preconditions.checkNotNull(redisTemplate, "RedisTemplate is not configured");
            return new FCoinClient(properties, fCoinSignature(), asyncHttpClients, redisTemplate);
        }

        @Bean
        @ConditionalOnBean({FCoinClient.class})
        public FCoinService fCoinService(FCoinClient lanMaoClient) {
            return new FCoinServiceImpl(lanMaoClient);
        }

        @Bean
        @ConditionalOnMissingBean
        public FCoinSignature fCoinSignature() {
            return new FCoinSignature(properties);
        }

    }

    @Configuration
    @EnableConfigurationProperties({FCoinProperties.class})
    public static class LanMaoSequenceConfiguration {

        @Autowired
        private FCoinProperties properties;
        @Autowired
        private Environment environment;

        @Bean
        @ProfileSpecified
        @Profile({"test", "experience", "production"})
        @ConditionalOnMissingBean
        public FCoinSequence lanMaoSequence(RedisTemplate<String, String> redisTemplate) {
            return new FCoinSequence(redisTemplate, properties);
        }

        @Bean
        @ProfileSpecified
        @Profile({"default", "dev-online"})
        @ConditionalOnMissingBean
        public FCoinSequence devFCoinSequence(RedisTemplate<String, String> redisTemplate) {
            DevelopProfileFCoinSequence fCoinSequence = new DevelopProfileFCoinSequence(redisTemplate, properties);
            String ip = environment.getProperty("fcoin.notify.ip");
            String port = environment.getProperty("fcoin.notify.port");
            if (StringUtils.isNotBlank(ip)) {
                fCoinSequence.setIp(ip);
            }
            if (StringUtils.isNotBlank(port)) {
                fCoinSequence.setPort(port);
            }
            return fCoinSequence;
        }

    }

}
