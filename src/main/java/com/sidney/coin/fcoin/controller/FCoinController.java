package com.sidney.coin.fcoin.controller;

import com.sidney.coin.fcoin.component.FCoinService;
import com.sidney.coin.fcoin.component.response.CurrenciesResponse;
import com.sidney.coin.fcoin.component.response.ServerTimeResponse;
import com.sidney.coin.fcoin.component.response.SymbolsResponse;
import com.sidney.coin.fcoin.vo.response.ServerTimeResponseVO;
import com.sidney.coin.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/fcoin")
@Api(tags = "fcoin", description = "/fcoin")
public class FCoinController {

    @Autowired
    private FCoinService fCoinService;

    @GetMapping("/serverTime")
    @ApiOperation("serverTime")
    public Result<ServerTimeResponseVO> serverTime() throws ExecutionException, InterruptedException {
        CompletableFuture<ServerTimeResponse> future = fCoinService.serverTime();
        ServerTimeResponse serverTimeResponse = future.get();
        ServerTimeResponseVO responseVO = new ServerTimeResponseVO();
        responseVO.setServerTime(serverTimeResponse.getData());
        return Result.with(responseVO);
    }

    @GetMapping("/currencies")
    @ApiOperation("currencies")
    public Result<CurrenciesResponse> currencies() throws ExecutionException, InterruptedException {
        CompletableFuture<CurrenciesResponse> future = fCoinService.currencies();
        CurrenciesResponse currenciesResponse = future.get();
        return Result.with(currenciesResponse);
    }

    @GetMapping("/symbols")
    @ApiOperation("symbols")
    public Result<SymbolsResponse> symbols() throws ExecutionException, InterruptedException {
        CompletableFuture<SymbolsResponse> future = fCoinService.symbols();
        SymbolsResponse symbolsResponse = future.get();
        return Result.with(symbolsResponse);
    }

}
