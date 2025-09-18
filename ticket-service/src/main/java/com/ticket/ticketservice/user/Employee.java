package com.ticket.ticketservice.user;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "employees")
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true, length = 128)
    private String email;

    @Column(nullable = false)
    private String password; // 存BCrypt

    private String department;
    private String project;
    private Long managerId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_roles",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();

    // getters/setters 省略可用 Lombok；这里直接保留 IDE 生成的
    public Long getId(){ return id; }
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }
    public String getPassword(){ return password; }
    public void setPassword(String password){ this.password = password; }
    public String getDepartment(){ return department; }
    public void setDepartment(String department){ this.department = department; }
    public String getProject(){ return project; }
    public void setProject(String project){ this.project = project; }
    public Long getManagerId(){ return managerId; }
    public void setManagerId(Long managerId){ this.managerId = managerId; }
    public List<Role> getRoles(){ return roles; }
    public void setRoles(List<Role> roles){ this.roles = roles; }
}
