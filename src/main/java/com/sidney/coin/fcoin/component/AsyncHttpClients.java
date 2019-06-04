package com.sidney.coin.fcoin.component;

import brave.http.HttpTracing;
import brave.httpasyncclient.TracingHttpAsyncClientBuilder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.sidney.coin.fcoin.component.utils.FacePlusMultipartEntityBuilder;
import com.sidney.coin.fcoin.component.properties.AsyncHttpClientProperties;
import com.sidney.coin.fcoin.component.utils.FormUploadFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class AsyncHttpClients {
    private static final Logger logger = LoggerFactory.getLogger(AsyncHttpClients.class);

    private CloseableHttpAsyncClient client;

    public AsyncHttpClients(AsyncHttpClientProperties properties, HttpTracing httpTracing) {
        int connectTimeout = properties.getConnectTimeOut() < 0 ? 0 : properties.getConnectTimeOut();
        IOReactorConfig config = IOReactorConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSoTimeout(properties.getSoTimeout())
                .setIoThreadCount(properties.getIoThreadCount() < 1 ? Runtime.getRuntime().availableProcessors() * 2 : properties.getIoThreadCount())
                .build();
        TrustManager[] trustAllCerts = new TrustManager[]{new RelaxTrustManager()};
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, null);
            client = TracingHttpAsyncClientBuilder.create(httpTracing)
                    .setDefaultIOReactorConfig(config)
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier((urlHostName, sslSession) -> true)
                    .setMaxConnPerRoute(properties.getMaxConnPerRoute())
                    .setMaxConnTotal(properties.getMaxConnTotal())
                    .build();
            client.start();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("start up CloseableHttpAsyncClient error", e);
        }
    }

    public void destroy() throws IOException {
        client.close();
    }

    public Future<HttpResponse> formPost(String url, Map<String, Object> headers, Map<String, Object> params, FutureCallback<HttpResponse> callback) {
        List<BasicNameValuePair> formPairs = createFormPairs(params);
        HttpPost httpPost = createPost(url, formPairs);
        addHeaders(httpPost, headers);
        logger.debug("=========> form post: url={} params={}", url, formPairs);
        return client.execute(httpPost, callback);
    }

    /**
     * 表单提交 POST
     *
     * @param url
     * @param params
     * @param callback
     * @return
     */
    public Future<HttpResponse> formPost(String url, Map<String, Object> params, FutureCallback<HttpResponse> callback) {
        return formPost(url, null, params, callback);
    }

    private HttpPost createPost(String url, List<BasicNameValuePair> formPairs) {
        HttpPost httpPost = new HttpPost(url);
        if (!formPairs.isEmpty()) {
            UrlEncodedFormEntity formEntity;
            try {
                formEntity = new UrlEncodedFormEntity(formPairs, "utf-8");
            } catch (UnsupportedEncodingException e) {
                logger.error("form request error:{}", e.getMessage());
                throw new RuntimeException("form request error", e);
            }
            httpPost.setEntity(formEntity);
        }
        return httpPost;
    }

    private List<BasicNameValuePair> createFormPairs(Map<String, Object> params) {
        List<BasicNameValuePair> formPairs = new ArrayList<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
            formPairs.add(pair);
        }
        return formPairs;
    }


    /**
     * 文件上传 POST
     *
     * @param url
     * @param params
     * @param callback
     * @return
     */
    public Future<HttpResponse> fileUploadPost(String url, Map<String, Object> params, Map<String, byte[]> files, Map<String, String> fileNames, FutureCallback<HttpResponse> callback) {
        HttpPost httpPost = new HttpPost(url);
        /*
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);



        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                multipartEntityBuilder.addPart(entry.getKey(), new StringBody(entry.getValue().toString(), ContentType.create("text/plain", Consts.UTF_8)));
            }
        }
        if (files != null && files.size() > 0) {
            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                multipartEntityBuilder.addBinaryBody(entry.getKey(), entry.getValue(),ContentType.APPLICATION_OCTET_STREAM.withCharset("UTF-8"), fileNames.get(entry.getKey()));
            }
        }
        HttpEntity reqEntity=multipartEntityBuilder.build();

        */

        FacePlusMultipartEntityBuilder builder = FacePlusMultipartEntityBuilder.create();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                builder.addPart(entry.getKey(), new StringBody(entry.getValue().toString(), ContentType.create("text/plain", Consts.UTF_8)));
            }
        }
        if (files != null && files.size() > 0) {
            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                builder.addBinaryBody(entry.getKey(), entry.getValue(), ContentType.APPLICATION_OCTET_STREAM.withCharset("UTF-8"), fileNames.get(entry.getKey()));
            }
        }
        HttpEntity reqEntity = builder.build();
        httpPost.setEntity(reqEntity);

        return client.execute(httpPost, callback);
    }

    public Future<HttpResponse> fileUploadPost2(String url, Map<String, Object> params, List<FormUploadFile> fileList, FutureCallback<HttpResponse> callback) {
        HttpPost httpPost = new HttpPost(url);

        FacePlusMultipartEntityBuilder builder = FacePlusMultipartEntityBuilder.create();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                builder.addPart(entry.getKey(), new StringBody(entry.getValue().toString(), ContentType.create("text/plain", Consts.UTF_8)));
            }
        }

        if (fileList != null && fileList.size() > 0) {
            for (FormUploadFile file : fileList) {
                if (StringUtils.isBlank(file.getFileName()) || StringUtils.isBlank(file.getFormParamName()) || file.getFileContent() == null || file.getFileContent().length == 0) {
                    continue;
                }

                builder.addBinaryBody(file.getFormParamName(), file.getFileContent(), ContentType.APPLICATION_OCTET_STREAM.withCharset("UTF-8"), file.getFileName());
            }
        }

        HttpEntity reqEntity = builder.build();
        httpPost.setEntity(reqEntity);

        return client.execute(httpPost, callback);
    }

    /**
     * GET请求
     *
     * @param url
     * @param callback
     * @return
     */
    public Future<HttpResponse> getRequest(String url, FutureCallback<HttpResponse> callback) {
        HttpGet httpGet = new HttpGet(url);
        logger.debug("=========> get request: url={} ", url);
        return client.execute(httpGet, callback);
    }


    /**
     * json post请求
     *
     * @param url
     * @param params
     * @param callback
     * @return
     */
    public Future<HttpResponse> restPost(String url, Object params, FutureCallback<HttpResponse> callback) {
        String jsonString = JSON.toJSONString(params);
        Map<String, Object> headers = new HashMap<>(1);
        headers.put("Content-Type", "application/json;charset=utf-8");
        return rawPost(url, headers, jsonString, callback);
    }

    /**
     * json post请求
     *
     * @param url
     * @param headers
     * @param jsonBody
     * @param callback
     * @return
     */
    public Future<HttpResponse> rawPost(String url, Map<String, Object> headers, String jsonBody, FutureCallback<HttpResponse> callback) {
        HttpPost httpPost = new HttpPost(url);
        addHeaders(httpPost, headers);
        httpPost.setEntity(new StringEntity(jsonBody, "utf-8"));
        String contentType = "";
        Header contentTypeHeader = httpPost.getFirstHeader("Content-Type");
        if (contentTypeHeader != null) {
            contentType = contentTypeHeader.getValue();
        }
        logger.debug("=========> raw post: Content-Type={} url={} params={}", contentType, url, jsonBody);
        return client.execute(httpPost, callback);
    }

    private void addHeaders(HttpPost httpPost, Map<String, Object> headers) {
        if (headers != null && !headers.isEmpty()) {
            Set<Map.Entry<String, Object>> entrySet = headers.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                String key = entry.getKey();
                String value = entry.getValue().toString();
                httpPost.addHeader(key, value);
            }
        }
    }

    public String responseForPlain(String charset, HttpResponse response) {
        String content;
        try {
            content = EntityUtils.toString(response.getEntity(), charset);
        } catch (IOException e) {
            throw new RuntimeException("read response result error", e);
        }
        logger.debug("<========= result={}", content);
        return content;
    }

    public String futureForPlain(Future<HttpResponse> future, String charset) {
        String content;
        try {
            HttpResponse response = future.get();
            content = responseForPlain(charset, response);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("read future result error", e);
        }
        return content;
    }

    public JSONObject responseForJSON(HttpResponse response) throws IOException {
        String plain = responseForPlain("utf-8", response);
        return JSON.parseObject(plain, Feature.OrderedField);
    }

    public JSONObject futureForJSON(Future<HttpResponse> future) {
        return JSON.parseObject(futureForPlain(future, "utf-8"), Feature.OrderedField);
    }

    public <T> T futureForObject(Future<HttpResponse> future, Class<T> clazz) {
        JSONObject json = futureForJSON(future);
        return JSON.toJavaObject(json, clazz);
    }

    public Future<HttpResponse> fileUploadPost2(String requestUrl, Map<String, String> headerMap, Map<String, Object> params, Map<String, byte[]> files, Map<String, String> fileNames, FutureCallback<HttpResponse> callback) {
        HttpPost httpPost = new HttpPost(requestUrl);

        if (headerMap != null && headerMap.size() > 0) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        FacePlusMultipartEntityBuilder builder = FacePlusMultipartEntityBuilder.create();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                builder.addPart(entry.getKey(), new StringBody(entry.getValue().toString(), ContentType.create("text/plain", Consts.UTF_8)));
            }
        }
        if (files != null && files.size() > 0) {
            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                builder.addBinaryBody(entry.getKey(), entry.getValue(), ContentType.APPLICATION_OCTET_STREAM.withCharset("UTF-8"), fileNames.get(entry.getKey()));
            }
        }
        HttpEntity reqEntity = builder.build();
        httpPost.setEntity(reqEntity);

        return client.execute(httpPost, callback);
    }

    class RelaxTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

    }

}
