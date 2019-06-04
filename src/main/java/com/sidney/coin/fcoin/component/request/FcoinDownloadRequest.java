package com.sidney.coin.fcoin.component.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class FcoinDownloadRequest {
    /**
     * 对账文件日期
     */
    @JSONField(format = "yyyyMMdd")
    @Getter
    protected Date fileDate;

    /**
     * 时间戳参数
     */
    @JSONField(format = "yyyyMMddHHmmss")
    protected Date timestamp = new Date();

    /**
     * 平台编号
     */
    protected String platformNo;
}
