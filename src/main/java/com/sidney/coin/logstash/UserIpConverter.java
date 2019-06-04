package com.sidney.coin.logstash;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class UserIpConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        if (event instanceof LoggingMessageEvent) {
            return ((LoggingMessageEvent) event).getUserIp();
        }
        return "";
    }

}
