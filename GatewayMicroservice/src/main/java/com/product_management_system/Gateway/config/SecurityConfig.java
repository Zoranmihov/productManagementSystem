package com.product_management_system.Gateway.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final RouteConfig routeConfig;

    public SecurityConfig(AuthenticationManager authenticationManager,
                          SecurityContextRepository securityContextRepository,
                          RouteConfig routeConfig) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.routeConfig = routeConfig;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.securityMatcher(ServerWebExchangeMatchers.anyExchange())
            .authorizeExchange(exchange -> {
                // Allow all OPTIONS requests (CORS preflight)
                exchange.pathMatchers(HttpMethod.OPTIONS).permitAll();

                // Public routes
                routeConfig.getPublicRoutes().forEach((service, routes) -> {
                    if (routes != null) {
                        routes.forEach(route -> exchange.pathMatchers(route).permitAll());
                    }
                });

                // Role-based protected routes
                routeConfig.getProtectedRoutes().forEach((service, roleBasedRoutes) -> {
                    if (roleBasedRoutes != null) {
                        roleBasedRoutes.forEach((role, routes) -> {
                            if ("ADMIN".equals(role)) {
                                routes.forEach(route -> exchange.pathMatchers(route).hasRole("ADMIN"));
                            } else if ("USER".equals(role)) {
                                routes.forEach(route -> exchange.pathMatchers(route).hasAnyRole("USER", "ADMIN"));
                            }
                        });
                    }
                });

                // Default fallback
                exchange.anyExchange().permitAll(); // TODO: tighten for production
            })
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(unauthorizedEntryPoint()))
            .authenticationManager(authenticationManager)
            .securityContextRepository(securityContextRepository)
            .build();
    }

    @Bean
    public ServerAuthenticationEntryPoint unauthorizedEntryPoint() {
        return (exchange, e) -> {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().remove(HttpHeaders.WWW_AUTHENTICATE);
            return Mono.empty();
        };
    }

    @Bean
    public CorsWebFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("http://localhost:8083")); // Adjust for frontend
        config.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
