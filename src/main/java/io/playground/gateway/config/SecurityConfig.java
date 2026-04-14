package io.playground.gateway.config;

import io.playground.gateway.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    public final static String[] excludedPaths = new String[]{
            "/auth/**",
            "/chat/*.html",
            "/chat/stomp"
    };
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PathPatternParser pathPatternParser() {
        return new PathPatternParser();
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);

        return serverHttpSecurity
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(excludedPaths).permitAll()
                        .anyExchange().permitAll()
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
