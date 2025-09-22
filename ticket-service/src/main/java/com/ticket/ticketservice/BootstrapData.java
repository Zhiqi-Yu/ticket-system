package com.ticket.ticketservice;

import com.ticket.ticketservice.entity.Employee;
import com.ticket.ticketservice.entity.Role;
import com.ticket.ticketservice.repository.EmployeeRepo;
import com.ticket.ticketservice.repository.RoleRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BootstrapData {

    @Bean
    CommandLineRunner init(RoleRepo roleRepo, EmployeeRepo empRepo, PasswordEncoder pe){
        return args -> {
            Role rUser    = roleRepo.findByName("USER").orElseGet(() -> roleRepo.save(new Role("USER")));
            Role rManager = roleRepo.findByName("MANAGER").orElseGet(() -> roleRepo.save(new Role("MANAGER")));
            Role rAdmin   = roleRepo.findByName("ADMIN").orElseGet(() -> roleRepo.save(new Role("ADMIN")));

            if (empRepo.findByEmail("u@x.com").isEmpty()) {
                Employee u = new Employee();
                u.setName("User"); u.setEmail("u@x.com");
                u.setPassword(pe.encode("123456"));
                u.getRoles().add(rUser);
                empRepo.save(u);
            }
            if (empRepo.findByEmail("m@x.com").isEmpty()) {
                Employee m = new Employee();
                m.setName("Manager"); m.setEmail("m@x.com");
                m.setPassword(pe.encode("123456"));
                m.getRoles().add(rManager);
                empRepo.save(m);
            }
            if (empRepo.findByEmail("a@x.com").isEmpty()) {
                Employee a = new Employee();
                a.setName("Admin"); a.setEmail("a@x.com");
                a.setPassword(pe.encode("123456"));
                a.getRoles().add(rAdmin);
                empRepo.save(a);
            }
        };
    }
}
