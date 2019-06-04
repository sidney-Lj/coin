package com.sidney.coin.fcoin.component;

import com.sidney.coin.fcoin.component.enums.FCoinPublicServiceNameEnum;
import com.sidney.coin.fcoin.component.response.ServerTimeResponse;

import java.util.concurrent.CompletableFuture;


public class FCoinServiceImpl implements FCoinService {

    private FCoinClient fCoinClient;

    public FCoinServiceImpl(FCoinClient fCoinClient) {
        this.fCoinClient = fCoinClient;
    }

    @Override
    public CompletableFuture<ServerTimeResponse> serverTime() {
        return fCoinClient.get(FCoinPublicServiceNameEnum.SERVER_TIME, ServerTimeResponse.class);
    }
}
