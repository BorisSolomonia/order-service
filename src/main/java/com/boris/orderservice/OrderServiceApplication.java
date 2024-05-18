package com.boris.orderservice;

import com.boris.orderservice.external.intercept.CustomOAuth2AuthorizedClientManager;
import com.boris.orderservice.external.intercept.RestTemplateInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;

@SpringBootApplication
@EnableFeignClients
@EnableWebSecurity
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Autowired
	ClientRegistrationRepository clientRegistrationRepository;
	@Autowired
	OAuth2AuthorizedClientRepository authorizedClientRepository;

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		RestTemplate restTemplate = new RestTemplate();
		restTemplate
				.setInterceptors(Arrays
						.asList(new RestTemplateInterceptor(
								clientManager(
										clientRegistrationRepository,
										authorizedClientRepository
								))));
		return restTemplate;
	}

	@Bean
	public CustomOAuth2AuthorizedClientManager clientManager(ClientRegistrationRepository clientRegistrationRepository,
															 OAuth2AuthorizedClientRepository authorizedClientRepository){

		OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder
				.builder()
				.clientCredentials()
				.build();

		CustomOAuth2AuthorizedClientManager clientManager =
				new CustomOAuth2AuthorizedClientManager(clientRegistrationRepository,
						authorizedClientRepository);
		clientManager.setAuthorizedClientProvider(authorizedClientProvider);

		return clientManager;

	}

}
