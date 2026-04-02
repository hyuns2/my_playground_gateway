package io.playground.gateway.security;

import io.playground.gateway.config.SecurityConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
    private final PathPatternParser parser;
    private final JwtValidator jwtValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String currentPath = exchange.getRequest().getURI().getPath();
        for (String path : SecurityConfig.excludedPaths) {
            if (parser.parse(path).matches(
                    PathContainer.parsePath(currentPath)))
                return chain.filter(exchange);
        }

        jwtValidator.validate(
                exchange.getRequest()
                        .getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION)
        );

        return chain.filter(exchange);
    }
}
