package com.sidney.coin.configuration;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.propagation.CurrentTraceContext;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import com.sidney.coin.component.AsyncHttpClients;
import com.sidney.coin.helper.ApplicationContextHelper;
import com.sidney.coin.helper.BeanFactoryHelper;
import com.sidney.coin.helper.ReflectHelper;
import com.sidney.coin.logstash.StashMessageWriter;
import com.sidney.coin.properties.AsyncHttpClientProperties;
import com.sidney.coin.properties.LoggingProperties;
import com.sidney.coin.properties.PrivacyMaskProperties;
import com.sidney.coin.trace.StashTraceSender;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableConfigurationProperties({AsyncHttpClientProperties.class, PrivacyMaskProperties.class, LoggingProperties.class})
public class CoinBaseConfiguration implements ApplicationListener<ApplicationReadyEvent>, EnvironmentPostProcessor, EnvironmentAware {
    private static final Logger logger = LoggerFactory.getLogger(CoinBaseConfiguration.class);
    private Environment environment;

    @Autowired
    private LoggingProperties loggingProperties;
    @Autowired
    private AsyncHttpClientProperties asyncHttpClientProperties;
    @Autowired
    private PrivacyMaskProperties privacyMaskProperties;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("========= application {} is startup =========", getApplicationName());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("========= application {} will shutdown =========", getApplicationName())));
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String applicationName = environment.getProperty("spring.application.name", "unknown-application");
        System.setProperty("spring.application.name", applicationName);
    }

    @Bean
    public static BeanFactoryHelper beanFactoryHelper() {
        return new BeanFactoryHelper();
    }

    @Bean
    public ApplicationContextHelper applicationContextHelper() {
        return new ApplicationContextHelper();
    }

    @Bean(destroyMethod = "destroy")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = AsyncHttpClientProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public AsyncHttpClients asyncHttpClients(HttpTracing httpTracing) {
        return new AsyncHttpClients(asyncHttpClientProperties, httpTracing);
    }


    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    AsyncReporter<Span> spanReporter(Sender sender) {
        return AsyncReporter.create(sender);
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public Sender sender() {
        LoggingProperties.Trace trace = loggingProperties.getStash().getTrace();
        return new StashTraceSender(stashMessageWriter(), trace);
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public Tracing tracing(Sender sender) {
        String applicationName = getApplicationName();
        String profiles = getCurrentProfiles();
        return Tracing.newBuilder()
                .localServiceName(applicationName + ":" + profiles)
                .currentTraceContext(CurrentTraceContext.Default.create())
                .spanReporter(spanReporter(sender)).build();
    }

    private String getApplicationName() {
        return environment.getProperty("spring.application.name", "unknown-application");
    }

    @Bean
    @ConditionalOnMissingBean
    HttpTracing httpTracing(Sender sender) {
        return HttpTracing.create(tracing(sender));
    }

    @Bean
    @ConditionalOnMissingBean
    public StashMessageWriter stashMessageWriter() {
        return new StashMessageWriter(lookupStashAppender());
    }

    @SuppressWarnings("unchecked")
    private LogstashTcpSocketAppender lookupStashAppender() {
        LoggerContext loggerContext = (LoggerContext) ReflectHelper.getFieldValue(logger, "loggerContext");
        Logger root = (Logger) ReflectHelper.getFieldValue(loggerContext, "root");
        AppenderAttachableImpl<ILoggingEvent> aai = (AppenderAttachableImpl<ILoggingEvent>) ReflectHelper.getFieldValue(root, "aai");
        List<Appender<?>> appenderList = (List<Appender<?>>) ReflectHelper.getFieldValue(aai, "appenderList");
        LogstashTcpSocketAppender appender = null;
        for (Appender<?> appd : appenderList) {
            if (appd instanceof LogstashTcpSocketAppender) {
                appender = (LogstashTcpSocketAppender) appd;
                break;
            }
        }
        return appender;
    }

    private String getCurrentProfiles() {
        String result;
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length == 0) {
            result = Arrays.toString(environment.getDefaultProfiles());
        } else {
            result = Arrays.toString(activeProfiles);
        }
        return result;
    }

}
