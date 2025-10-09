package com.ticket.gatewayweb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GatewayPageController {
    @GetMapping("/")       public String index()  { return "index"; }
    @GetMapping("/login")  public String login()  { return "login"; }
    @GetMapping("/user")   public String user()   { return "dash-user"; }
    @GetMapping("/manager")public String manager(){ return "dash-manager"; }
    @GetMapping("/admin")  public String admin()  { return "dash-admin"; }
}
