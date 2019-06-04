package com.sidney.fcoin.component.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("fcoin")
@Setter
@Getter
public class FcoinProperties extends RSASignatureProperties {
    private String platformNo;  //平台编号
    private int keySerial = 1;
    private String directUrl;   //直连接口地址
    private String gatewayUrl;  //网关接口地址
    private String downloadUrl; //下载(对账文件)地址
    private String uploadUrl; //文件上传地址
    private String notifyUrl; //后台通知地址
    private String callbackUrl; //服务端浏览器回调接受地址
    @NestedConfigurationProperty
    private NiiWooAccount niiWooAccount = new NiiWooAccount();

    @Setter
    @Getter
    public static class NiiWooAccount {
        //private Long redPacketsUserId;     //你我金融存管红包用户
        //private Long platformFeeUserId;    //你我金融手续费用户
        //private String redPacketsAccountId; // 你我金融红包账户id
        //private String platformFeeAccountId;// 你我金融平台账户id
        private String guaranteeUserId;//上海存管（你我金融担保用户userId）

    }

}
