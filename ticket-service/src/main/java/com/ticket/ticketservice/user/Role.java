package com.ticket.ticketservice.user;

import jakarta.persistence.*;

@Entity @Table(name = "roles")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String name; // USER / MANAGER / ADMIN

    public Role() {}
    public Role(String name){ this.name = name; }

    public Long getId(){ return id; }
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
}
