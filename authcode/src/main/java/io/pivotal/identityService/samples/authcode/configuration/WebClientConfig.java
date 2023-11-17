package io.pivotal.identityService.samples.authcode.configuration;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${RESOURCE_URL}")
    private String resourceServerUrl;

    @Bean
    public WebClient webClient(ClientRegistrationRepository clientRegistrationRepository,
                               OAuth2AuthorizedClientRepository authorizedClientRepository) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(
                        clientRegistrationRepository,
                        authorizedClientRepository);
        oauth2.setDefaultOAuth2AuthorizedClient(true);
        return WebClient.builder()
                .baseUrl(resourceServerUrl)
                .apply(oauth2.oauth2Configuration())
                .build();
    }


    /**
     * Hack: do the same as Spring Boot autoconfiguration, but support newer ClientAuthenticationMethod
     *
     * @param properties -
     * @return -
     * @see <a href="https://github.com/spring-cloud/spring-cloud-bindings/pull/106">Spring Cloud Bindings PR</a>
     */
    @Bean
    public ClientRegistrationRepository getClientRegistrations(OAuth2ClientProperties properties) {
        Map<String, ClientRegistration> clientRegistrations =
                OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties);

        for (Map.Entry<String, ClientRegistration> entry : clientRegistrations.entrySet()) {
            ClientRegistration clientRegistration = entry.getValue();
            ClientAuthenticationMethod clientAuthenticationMethod = transformBoot3ClientAuthenticationMethod(clientRegistration.getClientAuthenticationMethod());
            ClientRegistration newClientRegistration = ClientRegistration
                    .withClientRegistration(clientRegistration)
                    .clientAuthenticationMethod(clientAuthenticationMethod)
                    .build();
            clientRegistrations.put(entry.getKey(), newClientRegistration);
        }

        return new InMemoryClientRegistrationRepository(clientRegistrations);
    }

    private ClientAuthenticationMethod transformBoot3ClientAuthenticationMethod(ClientAuthenticationMethod clientAuthenticationMethod) {
        if (clientAuthenticationMethod.getValue().equalsIgnoreCase("client_secret_basic")) {
            return ClientAuthenticationMethod.BASIC;
        }

        if (clientAuthenticationMethod.getValue().equalsIgnoreCase("client_secret_post")) {
            return ClientAuthenticationMethod.POST;
        }
        return clientAuthenticationMethod;
    }
}
