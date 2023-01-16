package com.mvpmatch.vending.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange()
                .pathMatchers("/info", "/health").permitAll()
                .pathMatchers(HttpMethod.POST, "/user").permitAll()
                .pathMatchers(HttpMethod.POST, "/user/reset", "/user/deposit").hasRole("buyer")
                .pathMatchers(HttpMethod.GET, "/product").permitAll()
                .pathMatchers("/product").hasRole("seller")
                .pathMatchers(HttpMethod.POST, "/product/buy").hasRole("buyer")
                .anyExchange().authenticated()
                .and()
                .httpBasic()
                .and()
                .formLogin().disable()
                .csrf().disable()
                .build();
    }
}
