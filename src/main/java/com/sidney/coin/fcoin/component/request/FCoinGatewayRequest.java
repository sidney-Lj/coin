package com.sidney.coin.fcoin.component.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.sidney.coin.fcoin.component.enums.UserDeviceEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public abstract class FCoinGatewayRequest {
    /**
     * 平台用户编号
     */
    protected String platformUserNo;
    /**
     * 请求流水号
     */
    protected String requestNo;
    /**
     * 服务端页面回跳中转URL,组件配置统一设置，无需手动设置该参数
     */
    @Deprecated
    protected String redirectUrl;
    /**
     * 你我金融前端跳转地址，业务需手动设置该参数
     */
    @JSONField(serialize = false)
    protected String niiwooRedirectUrl;

    /**
     * 终端类型
     */
    protected UserDeviceEnum userDevice;

    /**
     * 超过此时间页面过期
     */
    @JSONField(format = "yyyyMMddHHmmss")
    protected Date expired;
    /**
     * 时间戳参数
     */
    @JSONField(format = "yyyyMMddHHmmss")
    protected Date timestamp = new Date();
}
