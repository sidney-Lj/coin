package com.sidney.fcoin.component.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public abstract class FcoinBatchRequest {
    /**
     * 批次号
     */
    @Setter
    @Getter
    protected String batchNo;
    /**
     * 时间戳参数
     */
    @JSONField(format = "yyyyMMddHHmmss")
    @Getter
    protected Date timestamp = new Date();
}
