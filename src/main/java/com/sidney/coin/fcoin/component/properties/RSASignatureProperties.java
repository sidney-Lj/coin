package com.sidney.coin.fcoin.component.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Setter
@Getter
public abstract class RSASignatureProperties {
    protected boolean enabled = false;
    protected String signAlgorithm = "SHA1withRSA";
    protected KeyPrefer keyPrefer = KeyPrefer.CERTIFILE;
    @NestedConfigurationProperty
    protected StringKeyProperties stringKey;
    @NestedConfigurationProperty
    protected KeyStoreProperties keyStore;  //keyStore密码配置
    @NestedConfigurationProperty
    protected CertificateProperties thirdCertificate;   //第三方crt证书，由第三方生成，我方持有，进行数字验签

    @Getter
    @Setter
    public static class KeyStoreProperties {
        private String password;        //keystore密码
        private String fileSystemPath;  //keystore文件所在的文件系统路径
        private String classPathFile;   //类路径keystore文件
    }

    @Getter
    @Setter
    public static class CertificateProperties {
        private String fileSystemPath;  //.crt文件所在的文件系统路径
        private String classPathFile;   //类路径.crt文件
    }

    @Setter
    @Getter
    public static class StringKeyProperties {
        private String ownPriviteKey;   //我方私钥
        private String thirdPublicKey;  //第三方公钥
    }

}
