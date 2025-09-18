package com.ticket.ticketservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    @GetMapping("/user")    public String user()    { return "dash-user"; }
    @GetMapping("/manager") public String manager() { return "dash-manager"; }
    @GetMapping("/admin")   public String admin()   { return "dash-admin"; }
}
