package com.chen.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.chen.exception.ModelBusinessException;
import com.chen.exception.NormalBusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.chen.constant.TencentConstant.*;

public class TencentMapUtil {


    //硬性腾讯要求，请求参数按ASCII排序，最好是TreeMap
    public static String generateAddress(Map<String, String> paramMap, String secretKey) {

        try {
            StringBuilder paramStrBuilder = new StringBuilder();
            //此遍历用于加密
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                if (paramStrBuilder.length() > 0) {
                    paramStrBuilder.append("&");
                }

                paramStrBuilder
                        .append(entry.getKey())
                        .append("=")
                        .append(entry.getValue());
            }

            String paramStr = paramStrBuilder.toString();
            String stringToSign = MAP_URI + "?" + paramStr + secretKey;
            //生成加密
            String sign = DigestUtils.md5Hex(stringToSign);
            paramMap.put("sig", sign);
            //此遍历用于加入路径参数
            URIBuilder uriBuilder = new URIBuilder(MAP_URL);
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
            URI requestUrl = uriBuilder.build();
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(requestUrl.toString());
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Accept", "application/json");

            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            String json = IoUtil.readUtf8(inputStream);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(json).get("result").get(TENCENT_ADDRESS);

            return node.asText();
        } catch (Exception e) {
            throw new NormalBusinessException(CONNECT_TENCENT_MAP_ERR);
        }
    }

    public static String parseAddress(Map<String, String> paramMap, String secretKey) {

        try {
            StringBuilder paramStrBuilder = new StringBuilder();
            //此遍历用于加密
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                if (paramStrBuilder.length() > 0) {
                    paramStrBuilder.append("&");
                }

                paramStrBuilder
                        .append(entry.getKey())
                        .append("=")
                        .append(entry.getValue());
            }

            String paramStr = paramStrBuilder.toString();
            String stringToSign = MAP_URI + "?" + paramStr + secretKey;
            //生成加密
            String sign = DigestUtils.md5Hex(stringToSign);
            paramMap.put("sig", sign);
            //此遍历用于加入路径参数
            URIBuilder uriBuilder = new URIBuilder(MAP_URL);
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
            URI requestUrl = uriBuilder.build();
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(requestUrl.toString());
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Accept", "application/json");

            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            String json = IoUtil.readUtf8(inputStream);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(json).get("result").get(TENCENT_LOCATION);

            String lat = node.get("lat").asText();
            String lng = node.get("lng").asText();

            return lat + "," + lng;
        } catch (Exception e) {
            throw new NormalBusinessException(CONNECT_TENCENT_MAP_ERR);
        }
    }
}
