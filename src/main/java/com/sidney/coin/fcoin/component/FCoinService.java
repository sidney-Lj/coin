package com.sidney.coin.fcoin.component;

import com.sidney.coin.fcoin.component.response.CurrenciesResponse;
import com.sidney.coin.fcoin.component.response.ServerTimeResponse;
import com.sidney.coin.fcoin.component.response.SymbolsResponse;

import java.util.concurrent.CompletableFuture;

public interface FCoinService {

    CompletableFuture<ServerTimeResponse> serverTime();

    CompletableFuture<CurrenciesResponse> currencies();

    CompletableFuture<SymbolsResponse> symbols();
}
