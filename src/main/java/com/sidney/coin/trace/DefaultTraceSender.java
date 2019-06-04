package com.sidney.coin.trace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zipkin2.Call;
import zipkin2.Span;
import zipkin2.codec.Encoding;
import zipkin2.codec.SpanBytesDecoder;
import zipkin2.reporter.Sender;

import java.util.List;

/**
 * Created by zhangwanli on 2017/10/15.
 */
public class DefaultTraceSender extends Sender {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Encoding encoding() {
        return Encoding.JSON;
    }

    @Override
    public int messageMaxBytes() {
        return 5 * 1024 * 1024;
    }

    @Override
    public int messageSizeInBytes(List<byte[]> encodedSpans) {
        return encoding().listSizeInBytes(encodedSpans);
    }

    @Override
    public Call<Void> sendSpans(List<byte[]> encodedSpans) {
        Span span;
        for (byte[] encodedSpan : encodedSpans) {
            span = SpanBytesDecoder.JSON_V2.decodeOne(encodedSpan);
            logger.info("span:{}", span);
        }
        return Call.create(null);
    }

}
