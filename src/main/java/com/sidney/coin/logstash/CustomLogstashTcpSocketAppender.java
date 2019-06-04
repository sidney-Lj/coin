package com.sidney.coin.logstash;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.sidney.coin.helper.ApplicationContextHelper;
import com.sidney.coin.helper.PrincipalFrameworkContext;
import net.logstash.logback.appender.LogstashTcpSocketAppender;

public class CustomLogstashTcpSocketAppender extends LogstashTcpSocketAppender {

    private Tracer tracer;

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        if (iLoggingEvent instanceof TraceMessageEvent) {
            super.append(iLoggingEvent);
            return;
        }
        if (tracer == null && ApplicationContextHelper.getApplicationContext() != null) {
            Tracing tracing = ApplicationContextHelper.getBean(Tracing.class);
            tracer = tracing.tracer();
        }
        Span span;
        String userId = PrincipalFrameworkContext.getContext().getUserId();
        String sessionId = PrincipalFrameworkContext.getContext().getSessionId();
        String userIp = PrincipalFrameworkContext.getContext().getUserIp();
        if (tracer != null && (span = tracer.currentSpan()) != null) {
            String traceIdAndSpanId = span.context().toString();
            String[] split = traceIdAndSpanId.split("/");
            LoggingMessageEvent event = new LoggingMessageEvent(iLoggingEvent, userId, sessionId, userIp, split[0], split[1]);
            super.append(event);
        } else {
            String traceId = CurrentTrace.getCurrentTraceId();
            String spanId = CurrentTrace.getCurrentSpanId();
            LoggingMessageEvent event = new LoggingMessageEvent(iLoggingEvent, userId, sessionId, userIp, traceId, spanId);
            super.append(event);
        }
    }

}
