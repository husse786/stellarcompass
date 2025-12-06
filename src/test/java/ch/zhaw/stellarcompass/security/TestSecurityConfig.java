package ch.zhaw.stellarcompass.security;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

@TestConfiguration
public class TestSecurityConfig {

    // Constants for tests
    public static final String ADMIN = "Bearer admin-token";
    public static final String STUDENT = "Bearer student-token";
    public static final String MENTOR = "Bearer mentor-token";

    @Bean
    public JwtDecoder jwtDecoder() {
        return new JwtDecoder() {
            @Override
            public Jwt decode(String token) throws JwtException {
                // We simulate different users based on the "token" string
                if (token.equals("admin-token")) {
                    return createJwt("auth0|admin", "admin@stellar.com", "admin");
                } else if (token.equals("student-token")) {
                    return createJwt("auth0|student", "student@stellar.com", "student");
                } else if (token.equals("mentor-token")) {
                    return createJwt("auth0|mentor", "mentor@stellar.com", "mentor");
                }
                throw new JwtException("Invalid token");
            }
        };
    }

    private Jwt createJwt(String sub, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", sub);
        claims.put("email", email);
        claims.put("user_roles", Arrays.asList(role)); //Important: list of roles

        return new Jwt(
                tokenFromSub(sub),
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                claims);
    }
    
    private String tokenFromSub(String sub) {
        return sub + "-token";
    }
}