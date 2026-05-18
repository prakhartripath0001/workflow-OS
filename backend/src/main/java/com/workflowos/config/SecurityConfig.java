package com.workflowos.config;

import com.workflowos.auth.handler.OAuthSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration.
 *
 * <p>All API endpoints are open (our custom auth module handles session
 * validation). Spring Security is here solely to wire up the OAuth2
 * social-login flow (/oauth2/authorization/google, /oauth2/authorization/github).</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuthSuccessHandler oAuthSuccessHandler;

    public SecurityConfig(OAuthSuccessHandler oAuthSuccessHandler) {
        this.oAuthSuccessHandler = oAuthSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — we use stateless session tokens, not cookies
            .csrf(csrf -> csrf.disable())

            // Allow all requests — our AuthService handles token validation
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())

            // Wire up OAuth2 login with our custom success handler
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuthSuccessHandler)
            );

        return http.build();
    }
}
