package com.sidney.coin.fcoin.component;

import com.sidney.coin.component.RSASignature;
import com.sidney.coin.properties.RSASignatureProperties;

public class FCoinSignature extends RSASignature {

    public FCoinSignature(RSASignatureProperties properties) {
        super(properties);
    }

    @Override
    protected void initWithStringKey(RSASignatureProperties properties) {
        super.initWithStringKey(properties);
    }

}
