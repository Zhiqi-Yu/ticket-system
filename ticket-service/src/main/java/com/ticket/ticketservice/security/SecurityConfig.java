package com.ticket.ticketservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/login","/css/**","/js/**").permitAll()
                        .requestMatchers("/dashboard/user/**").hasRole("USER")
                        .requestMatchers("/dashboard/manager/**").hasRole("MANAGER")
                        .requestMatchers("/dashboard/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(f->f.loginPage("/login").permitAll().defaultSuccessUrl("/post-login", true))
                .logout(l->l.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll())
                .build();
    }
}

@Controller
class AuthController {
    @GetMapping("/login") public String login(){ return "login"; }
    @GetMapping("/post-login")
    public String postLogin(Authentication auth){
        boolean admin   = auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
        boolean manager = auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_MANAGER"));
        if (admin)   return "redirect:/dashboard/admin";
        if (manager) return "redirect:/dashboard/manager";
        return "redirect:/dashboard/user";
    }
}
