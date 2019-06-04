package com.sidney.coin.logstash;

import net.logstash.logback.appender.LogstashTcpSocketAppender;

public class StashMessageWriter {

    private LogstashTcpSocketAppender appender;

    public StashMessageWriter(LogstashTcpSocketAppender appender) {
        this.appender = appender;
    }

    public void write(TraceMessageEvent messageEvent) {
        if (appender != null) {
            appender.doAppend(messageEvent);
        }
    }

}
