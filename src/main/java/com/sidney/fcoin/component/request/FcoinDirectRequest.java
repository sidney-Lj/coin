package com.sidney.fcoin.component.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public abstract class FcoinDirectRequest {
    /**
     * 请求流水号
     */
    @Setter
    @Getter
    protected String requestNo;
    /**
     * 时间戳参数
     */
    @JSONField(format = "yyyyMMddHHmmss")
    @Getter
    protected Date timestamp = new Date();
}
