package com.ticket.ticketservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity           // ← 新增
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login","/css/**","/js/**").permitAll()
                        .requestMatchers("/dashboard/user/**").hasRole("USER")
                        .requestMatchers("/dashboard/manager/**").hasRole("MANAGER")
                        .requestMatchers("/dashboard/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(f -> f.loginPage("/login").permitAll().defaultSuccessUrl("/post-login", true))
                .logout(l -> l.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll())
                .build();
    }
}
