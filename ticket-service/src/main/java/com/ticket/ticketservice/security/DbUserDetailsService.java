package com.ticket.ticketservice.security;

import com.ticket.ticketservice.user.Employee;
import com.ticket.ticketservice.user.EmployeeRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbUserDetailsService implements UserDetailsService {
    private final EmployeeRepo repo;
    public DbUserDetailsService(EmployeeRepo repo){ this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee e = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("not found"));

        // 关键：这里用 List<SimpleGrantedAuthority>
        List<SimpleGrantedAuthority> auth = e.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                .toList();

        return new org.springframework.security.core.userdetails.User(
                e.getEmail(), e.getPassword(), auth
        );
    }
}
