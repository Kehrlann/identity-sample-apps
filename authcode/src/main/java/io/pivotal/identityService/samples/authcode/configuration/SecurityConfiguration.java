package io.pivotal.identityService.samples.authcode.configuration;

import io.pivotal.identityService.samples.authcode.security.UaaLogoutSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
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
                    authorize.requestMatchers("/").permitAll();
                    authorize.anyRequest().authenticated();
                })
                .oauth2Login(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessHandler(uaaLogoutSuccessHandler))
                .build();
    }
}
