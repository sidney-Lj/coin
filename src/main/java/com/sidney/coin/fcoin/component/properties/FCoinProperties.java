package com.sidney.coin.fcoin.component.properties;

import com.sidney.coin.properties.RSASignatureProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("fcoin")
@Setter
@Getter
public class FCoinProperties extends RSASignatureProperties {
    private String platformNo;  //平台编号
    private int keySerial = 1;
    private String directUrl;   //直连接口地址
    private String publicUrl;  //公开接口地址
    private String downloadUrl; //下载(对账文件)地址
    private String uploadUrl; //文件上传地址
    private String notifyUrl; //后台通知地址
    private String callbackUrl; //服务端浏览器回调接受地址

}
