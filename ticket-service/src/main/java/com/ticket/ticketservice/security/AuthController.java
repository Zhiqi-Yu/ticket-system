package com.ticket.ticketservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/login")           // 渲染 templates/login.html
    public String login(){ return "login"; }

    @GetMapping("/post-login")      // 登录成功后按角色分流
    public String postLogin(Authentication auth){
        boolean admin   = auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
        boolean manager = auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_MANAGER"));
        if (admin)   return "redirect:/dashboard/admin";
        if (manager) return "redirect:/dashboard/manager";
        return "redirect:/dashboard/user";
    }
}
