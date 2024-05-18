package com.boris.orderservice.external.intercept;

import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

import static org.springframework.data.repository.util.ClassUtils.ifPresent;

@Configuration
@Log4j2
public class OAuthRequestInterceptor implements RequestInterceptor {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;


    @PostConstruct
    public void logClientRegistrations() {
        System.out.println("sssssssssssssssssssssssssssssssssssssss1111111");
        log.info("the info iiiis {}", clientRegistrationRepository.findByRegistrationId("okta").getClientId());
        log.info("the info iiiis {}", clientRegistrationRepository.findByRegistrationId("okta").getRegistrationId());
        log.info("the info iiiis {}", clientRegistrationRepository.findByRegistrationId("okta").getClientSecret());
        log.info("the info iiiis {}", clientRegistrationRepository.findByRegistrationId("okta").getProviderDetails().getUserInfoEndpoint().getUri());
        log.info("the info iiiis {}", clientRegistrationRepository.findByRegistrationId("okta").getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
        log.info("the info iiiis {}", clientRegistrationRepository.findByRegistrationId("okta").getProviderDetails().getUserInfoEndpoint().getAuthenticationMethod());
        log.info("the info iiiis {}", clientRegistrationRepository.findByRegistrationId("okta").getProviderDetails().getUserInfoEndpoint().getUri());
        log.info("the info iiiis {}", clientRegistrationRepository.findByRegistrationId("okta").getClientName());
        log.info("the info iiiis {}", clientRegistrationRepository.findByRegistrationId("okta").getAuthorizationGrantType());
        log.info("the info iiiis {}", clientRegistrationRepository.findByRegistrationId("okta").getScopes());
    }

    @Autowired
    private OAuth2AuthorizedClientManager authorizedClientManager;



    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        String accessToken = jwtAuth.getToken().getTokenValue();
        System.out.println("Hereiiiiiis 0 "+ accessToken);
//        template.header("Authorization", "Bearer " +
//                Objects.requireNonNull(authorizedClientManager
//                                .authorize(OAuth2AuthorizeRequest
//                                        .withClientRegistrationId("okta")
//                                        .principal("internal")
//                                        //.principal(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
//                                        .build()))
//                        .getAccessToken()
//                        .getTokenValue());
        template.header("Authorization", "Bearer " + accessToken);
    }

}