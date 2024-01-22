package io.pivotal.identityService.samples.authcodeclientcredentials.configuration;

import io.pivotal.identityService.samples.authcodeclientcredentials.security.UaaLogoutSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http, UaaLogoutSuccessHandler uaaLogoutSuccessHandler) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/", "/client/todos", "/client/todos/*").permitAll();
                    authorize.anyRequest().authenticated();
                })
                .oauth2Login(login -> {
                    login.loginPage("/oauth2/authorization/ssoauthorizationcode");
                    login.failureUrl("/login?error");
                    login.permitAll();
                })
                .logout(logout -> logout.logoutSuccessHandler(uaaLogoutSuccessHandler))
                .build();
    }

}
