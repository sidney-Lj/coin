package com.sidney.coin.fcoin.component;

import com.sidney.coin.fcoin.component.response.ServerTimeResponse;

import java.util.concurrent.CompletableFuture;

public interface FCoinService {

    CompletableFuture<ServerTimeResponse> serverTime();
}
