package com.sidney.coin.fcoin.component.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public abstract class FCoinUploadRequest {

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
