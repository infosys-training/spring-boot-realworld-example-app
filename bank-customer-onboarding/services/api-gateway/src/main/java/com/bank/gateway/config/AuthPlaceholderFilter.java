package com.bank.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Placeholder authentication filter.
 * Can be enabled later to add JWT validation at the gateway level.
 * Currently passes all requests through without authentication.
 */
@Component
@Slf4j
public class AuthPlaceholderFilter implements GlobalFilter, Ordered {

    private static final boolean AUTH_ENABLED = false;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (AUTH_ENABLED) {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            log.debug("Auth header present: {}", authHeader != null);
            // JWT validation logic would go here
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
