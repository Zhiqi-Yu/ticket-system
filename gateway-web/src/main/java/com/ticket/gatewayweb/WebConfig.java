package com.ticket.gatewayweb;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override public void addViewControllers(ViewControllerRegistry r) {
        r.addViewController("/").setViewName("index");
        r.addViewController("/dashboard/user").setViewName("dash-user");
        r.addViewController("/dashboard/manager").setViewName("dash-manager");
        r.addViewController("/dashboard/admin").setViewName("dash-admin");
    }
}
