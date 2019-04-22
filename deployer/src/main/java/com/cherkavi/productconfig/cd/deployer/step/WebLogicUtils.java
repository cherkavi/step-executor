package com.cherkavi.productconfig.cd.deployer.step;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

class WebLogicUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebLogicUtils.class);

    private WebLogicUtils(){
    }

    static ResponseEntity<String> exchange(String host, String login, String password, HttpMethod method, String urlPrefix, String body) {
        String url = String.format("http://%s/management/wls/latest/deployments"+((urlPrefix==null)? StringUtils.EMPTY:urlPrefix), host);
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        if(body!=null){
            headers.add("Content-Type", "application/json");
        }
        headers.add("X-Requested-By", login);
        headers.add("Authorization", "Basic "+base64encode(login+":"+password)); // --user weblogic:weblogic1
        try {
            RequestEntity<String> requestEntity;
            if(body!=null){
                requestEntity = new RequestEntity<>(body, headers, method, new URI(url));
            }else{
                requestEntity = new RequestEntity<>(body, headers, method, new URI(url));
            }
            return new RestTemplateBuilder().errorHandler(new SkipNotFound()).build().exchange(requestEntity, String.class);
        } catch (URISyntaxException e) {
            LOGGER.error("can't recognize url: "+url);
            throw new IllegalStateException("");
        }
    }

    private static String base64encode(String s) {
        return Base64.encodeBase64String(s.getBytes());
    }

    private static class SkipNotFound implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
            return clientHttpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR;
        }
        @Override
        public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
            if (clientHttpResponse.getStatusCode().is4xxClientError()){
                return;
            }
            throw new RestClientException(IOUtils.toString(clientHttpResponse.getBody(), "utf-8"));
        }
    }
}
