package ch.zhaw.stellarcompass.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
    // https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for REST API with JWT
            .authorizeHttpRequests(authorize -> authorize
            // Permit all requests to root, static resources, and public endpoints
                .requestMatchers("/", "/assets/**", "/static/**").permitAll()
            // Require authentication for all API endpoints
                .requestMatchers("/api/**").authenticated()
            // Permit all other requests (e.g., frontend routes)
                .anyRequest().permitAll()           
            )
            .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}

