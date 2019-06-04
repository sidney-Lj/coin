package com.sidney.coin.fcoin.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.sidney.coin.fcoin.component.properties.FcoinProperties;
import com.sidney.coin.fcoin.component.enums.FcoinGatewayServiceNameEnum;
import com.sidney.coin.fcoin.component.enums.FcoinServiceNameEnum;
import com.sidney.coin.fcoin.component.enums.FcoinUploadServiceNameEnum;
import com.sidney.coin.fcoin.component.exception.FcoinException;
import com.sidney.coin.fcoin.component.exception.VerifyFailedException;
import com.sidney.coin.fcoin.component.request.*;
import com.sidney.coin.fcoin.component.response.FcoinGatewayResponse;
import com.sidney.coin.fcoin.component.response.FcoinResponse;
import com.sidney.coin.fcoin.component.serializer.Number2StringFilter;
import com.sidney.coin.fcoin.component.utils.FormUploadFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FcoinClient {
    private static final Logger logger = LoggerFactory.getLogger(FcoinClient.class);

    private FcoinProperties properties;
    private RSASignature rsaSignature;
    private AsyncHttpClients asyncHttpClients;
    private final RedisTemplate<String, String> redisTemplate;
    private ValueFilter filter = new Number2StringFilter();

    public FcoinClient(FcoinProperties properties, RSASignature rsaSignature, AsyncHttpClients asyncHttpClients, RedisTemplate<String, String> redisTemplate) {
        this.properties = properties;
        this.rsaSignature = rsaSignature;
        this.asyncHttpClients = asyncHttpClients;
        this.redisTemplate = redisTemplate;
    }

    <T extends FcoinResponse> CompletableFuture<T> post(FcoinServiceNameEnum serviceName, FcoinDirectRequest request, final Class<T> responseClass) {
        Map<String, Object> req = buildRequest(serviceName.name(), JSON.toJSONString(request, SerializerFeature.UseSingleQuotes));
        return doPost(request.getRequestNo(), req, responseClass);
    }

    <T extends FcoinResponse> CompletableFuture<T> batchPost(FcoinServiceNameEnum serviceName, FcoinBatchRequest batchRequest, final Class<T> responseClass) {
        Map<String, Object> req = buildRequest(serviceName.name(), JSON.toJSONString(batchRequest, SerializerFeature.UseSingleQuotes));
        return doPost(batchRequest.getBatchNo(), req, responseClass);
    }

    public CompletableFuture<File> download(FcoinServiceNameEnum serviceName, FcoinDownloadRequest request, String tempFile) {
        Map<String, Object> req = buildRequest(serviceName.name(), JSON.toJSONString(request, SerializerFeature.UseSingleQuotes));
        return doDownload(req, tempFile);
    }

    <T extends FcoinResponse> CompletableFuture<T> upload(FcoinUploadServiceNameEnum serviceName, FcoinUploadRequest request, List<FormUploadFile> fileList, final Class<T> responseClass) {
        Map<String, Object> params = buildRequest(serviceName.name(), JSON.toJSONString(request, SerializerFeature.UseSingleQuotes));
        return doUpload(request.getRequestNo(), params, fileList, responseClass);
    }

    private <T extends FcoinResponse> CompletableFuture<T> doUpload(final String reqNo, Map<String, Object> params, List<FormUploadFile> fileList, Class<T> responseClass) {
        logger.info("doUpload Fcoin request====>requestNo/batchNo={},params={}", reqNo, params);
        final CompletableFuture<T> future = new CompletableFuture<>();

        asyncHttpClients.fileUploadPost2(properties.getUploadUrl(), params, fileList, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                try {
                    JSONObject json = _responseForJSON(result);
                    T t = json.toJavaObject(responseClass);
                    future.complete(t);
                } catch (Exception e) {
                    logger.error("doUpload parse Fcoin response error,requestNo/batchNo={}", reqNo, e);
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void failed(Exception ex) {
                logger.error("doUpload request Fcoin error,requestNo/batchNo={}", reqNo, ex);
                future.completeExceptionally(ex);
            }

            @Override
            public void cancelled() {
                future.cancel(true);
            }
        });

        return future;
    }

    private CompletableFuture<File> doDownload(Map<String, Object> req, String filePath) {
        final CompletableFuture<File> future = new CompletableFuture<File>();
        asyncHttpClients.formPost(properties.getDownloadUrl(), req, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                try {
                    if(Objects.isNull(result.getEntity().getContentType())){
                        File newFile = new File(filePath);
                        FileUtils.copyInputStreamToFile(result.getEntity().getContent(), newFile);
                        future.complete(newFile);
                        return;
                    }else {
                        JSONObject json = FcoinClient.this._responseForJSON(result);
                        future.completeExceptionally(new Exception(json.getString("errorCode") + ":" + json.getString("errorMessage")));
                        return;
                    }

                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void failed(Exception ex) {
                future.completeExceptionally(ex);
            }

            @Override
            public void cancelled() {
                future.cancel(true);
            }
        });
        return future;
    }

    public FcoinGatewayResponse buildPageParams(FcoinGatewayServiceNameEnum serviceName, FcoinGatewayRequest request) {
        //统一设置前端页面回跳地址
        if (!StringUtils.isEmpty(request.getNiiwooRedirectUrl())) {
            redisTemplate.opsForValue().set(request.getRequestNo(), request.getNiiwooRedirectUrl(), 30, TimeUnit.MINUTES);
        }
        //服务端接受浏览器回跳地址
        request.setRedirectUrl(properties.getCallbackUrl());
        FcoinGatewayResponse response = new FcoinGatewayResponse();
        response.setAction(properties.getGatewayUrl());
        Map<String, Object> req = buildRequest(serviceName.name(), JSON.toJSONString(request, filter, SerializerFeature.UseSingleQuotes));
        response.setParams(req);
        return response;
    }

    private <T extends FcoinResponse> CompletableFuture<T> doPost(final String reqNo, Map<String, Object> req, Class<T> responseClass) {
        logger.info("Fcoin request====>requestNo/batchNo={},params={}", reqNo, req);
        final CompletableFuture<T> future = new CompletableFuture<>();
        asyncHttpClients.formPost(properties.getDirectUrl(), req, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                try {
                    JSONObject json = _responseForJSON(result);
                    T t = json.toJavaObject(responseClass);
                    future.complete(t);
                } catch (Exception e) {
                    logger.error("parse Fcoin response error,requestNo/batchNo={}", reqNo, e);
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void failed(Exception ex) {
                logger.error("request Fcoin error,requestNo/batchNo={}", reqNo, ex);
                future.completeExceptionally(ex);
            }

            @Override
            public void cancelled() {
                future.cancel(true);
            }
        });
        return future;
    }

    private Map<String, Object> buildRequest(String serviceName, String jsonReqData) {
        JSONObject jsonObject = JSON.parseObject(jsonReqData);
        Map<String, Object> req = new HashMap<>(6);
        req.put("serviceName", serviceName);
        req.put("platformNo", properties.getPlatformNo());

        if (!StringUtils.isEmpty(jsonObject.getString("userDevice"))) {
            req.put("userDevice", jsonObject.getString("userDevice"));
            jsonObject.remove("userDevice");
        }

        jsonReqData = JSON.toJSONString(jsonObject, SerializerFeature.UseSingleQuotes);
        req.put("reqData", jsonReqData);
        req.put("keySerial", properties.getKeySerial());
        req.put("sign", rsaSignature.sign(jsonReqData));
        return req;
    }

    private JSONObject _responseForJSON(HttpResponse response) {
        String plain = asyncHttpClients.responseForPlain("utf-8", response);
        logger.info("Fcoin response<====result={}", plain);
        Header[] signs = response.getHeaders("sign");
        JSONObject json = JSON.parseObject(plain);
        //        check(json);
        //懒猫接口非成功情况下没有签名
        if ("0".equals(json.getString("code")) && "SUCCESS".equals(json.getString("status"))) {
            verify(plain, signs);
        }
        return json;
    }

    private void check(JSONObject res) {
        if (!"0".equals(res.getString("code")) || !"SUCCESS".equals(res.getString("status"))) {
            String errorCode = res.getString("errorCode");
            String errorMessage = res.getString("errorMessage");
            throw new FcoinException("Fcoin" + errorCode, errorMessage);
        }
    }

    private void verify(String unsigned, Header[] headers) {
        String signed = null;
        if (headers != null && headers.length > 0) {
            Header header = headers[0];
            signed = header.getValue();
        }
        if (signed == null) {
            throw new VerifyFailedException("verify Fcoin respose error: sign is null");
        }
        boolean verify = rsaSignature.verify(signed, unsigned);
        if (!verify) {
            throw new VerifyFailedException("verify Fcoin respose error, signed = " + signed + ", unsigned = " + unsigned);
        }
    }

}
