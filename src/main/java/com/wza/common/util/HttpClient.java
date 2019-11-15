package com.wza.common.util;


import com.wza.module.service.LoginService;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpClient {

    public static String get(String url) {
        String result = null;
        try {
            URL reqUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) reqUrl.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            InputStreamReader in = new InputStreamReader(httpURLConnection.getInputStream());
            BufferedReader reader = new BufferedReader(in);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString();
            reader.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static final int TIMEOUT = 45000;
    private static final String ENCODING = "UTF-8";

    /**
     * 创建HTTP连接
     *
     * @param url              地址
     * @param method           方法
     * @param headerParameters 头信息参数
     * @param body             请求内容
     */
    private static HttpURLConnection createConnection(String url,
                                                      String method, Map<String, String> headerParameters, String body)
            throws Exception {
        URL Url = new URL(url);
        trustAllHttpsCertificates();
        HttpURLConnection httpConnection = (HttpURLConnection) Url
                .openConnection();
        // 设置请求时间
        httpConnection.setConnectTimeout(TIMEOUT);
        // 设置 header
        if (headerParameters != null) {
            for (String key : headerParameters.keySet()) {
                httpConnection.setRequestProperty(key,
                        headerParameters.get(key));
            }
        }

        httpConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded;charset=" + ENCODING);

        // 设置请求方法
        httpConnection.setRequestMethod(method);
        httpConnection.setDoOutput(true);
        httpConnection.setDoInput(true);
        // 写query数据流
        if (!(body == null || body.trim().equals(""))) {
            OutputStream writer = httpConnection.getOutputStream();
            try {
                writer.write(body.getBytes(ENCODING));
            } finally {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            }
        }

        // 请求结果
        int responseCode = httpConnection.getResponseCode();
        if (responseCode != 200) {
            throw new Exception(responseCode
                    + ":"
                    + inputStream2String(httpConnection.getErrorStream(),
                    ENCODING));
        }

        return httpConnection;
    }

    /**
     * POST请求
     *
     * @param address 请求地址
     * @param header  头部参数
     */
    public static String post(String address,
                              Map<String, String> header, Map<String, String> body) throws Exception {

        return proxyHttpRequest(address, "POST", header,
                getRequestBody(body));
    }

    /**
     * GET请求
     *
     * @param url              地址
     * @param headerParameters 参数
     */
    public static String get(String url,
                             Map<String, String> header, Map<String, String> headerParameters) throws Exception {

        return proxyHttpRequest(url + "?"
                + getRequestBody(headerParameters), "GET", header, null);
    }

    /**
     * HTTP请求
     *
     * @param address          地址
     * @param method           方法
     * @param headerParameters 头信息
     * @param body             请求内容
     */
    public static String proxyHttpRequest(String address, String method,
                                          Map<String, String> headerParameters, String body) throws Exception {
        String result = null;
        HttpURLConnection httpConnection = null;

        try {
            httpConnection = createConnection(address, method,
                    headerParameters, body);
            String encoding = "UTF-8";
            if (httpConnection.getContentType() != null
                    && httpConnection.getContentType().contains("charset=")) {
                encoding = httpConnection.getContentType()
                        .substring(
                                httpConnection.getContentType().indexOf(
                                        "charset=") + 8);
            }
            result = inputStream2String(httpConnection.getInputStream(),
                    encoding);

        } catch (Exception e) {
            // logger.info("HTTPproxy error: {}", e.getMessage());
            throw e;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return result;
    }

    /**
     * 将参数化为 body
     */
    public static String getRequestBody(Map<String, String> params) {
        return getRequestBody(params, true);
    }

    /**
     * 将参数化为 body
     */
    public static String getRequestBody(Map<String, String> params,
                                        boolean urlEncode) {
        StringBuilder body = new StringBuilder();
        if (params == null) {
            return "1=1";
        }
        for (Iterator<String> iteratorHeader = params.keySet().iterator(); iteratorHeader.hasNext(); ) {
            String key = iteratorHeader.next();
            String value = params.get(key);

            if (urlEncode) {
                try {
                    body.append(key).append("=").append(URLEncoder.encode(value, ENCODING)).append("&");
                } catch (UnsupportedEncodingException e) {
                    // e.printStackTrace();
                }
            } else {
                body.append(key).append("=").append(value).append("&");
            }
        }

        if (body.length() == 0) {
            return "";
        }
        return body.substring(0, body.length() - 1);
    }

    /**
     * 读取inputStream 到 string
     */
    private static String inputStream2String(InputStream input, String encoding)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input,
                encoding));
        StringBuilder result = new StringBuilder();
        String temp = null;
        while ((temp = reader.readLine()) != null) {
            result.append(temp);
        }

        return result.toString();

    }


    /**
     * 设置 https 请求
     */
    private static void trustAllHttpsCertificates() throws Exception {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String str, SSLSession session) {
                return true;
            }
        });
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                .getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
                .getSocketFactory());
    }


    //设置 https 请求证书
    static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }


    }
}
