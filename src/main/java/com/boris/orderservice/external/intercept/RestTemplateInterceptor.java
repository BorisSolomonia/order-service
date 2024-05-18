package com.boris.orderservice.external.intercept;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import java.io.IOException;

public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private OAuth2AuthorizedClientManager authorizedClientManager;

    public RestTemplateInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        //System.out.println("Hereiiiiiis 0 "+ request.getHeaders());

        request.getHeaders().add("Authorization", "Bearer " +
                authorizedClientManager
                        .authorize(OAuth2AuthorizeRequest.withClientRegistrationId("okta")
                                .principal("internal")
                                .build())
                        .getAccessToken()
                        .getTokenValue());

        return execution.execute(request, body);
    }


}
