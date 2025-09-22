package com.ticket.ticketservice.controller;

import com.ticket.ticketservice.entity.Ticket;
import com.ticket.ticketservice.model.ActionRequest;
import com.ticket.ticketservice.model.CreateTicketRequest;
import com.ticket.ticketservice.service.TicketService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService service;
    public TicketController(TicketService s){ this.service = s; }

    @PostMapping
    public Ticket create(@RequestBody CreateTicketRequest req, Authentication auth){
        return service.create(req, auth);
    }

    @GetMapping("/{id}")
    public Ticket get(@PathVariable Long id){ return service.get(id); }

    @PostMapping("/{id}/approve")
    public Ticket approve(@PathVariable Long id, @RequestBody ActionRequest req, Authentication auth){
        return service.approve(id, req, auth);
    }

    @PostMapping("/{id}/reject")
    public Ticket reject(@PathVariable Long id, @RequestBody ActionRequest req, Authentication auth){
        return service.reject(id, req, auth);
    }

    @PostMapping("/{id}/resolve")
    public Ticket resolve(@PathVariable Long id, @RequestBody ActionRequest req, Authentication auth){
        return service.resolve(id, req, auth);
    }

    @PostMapping("/{id}/close")
    public Ticket close(@PathVariable Long id, @RequestBody ActionRequest req, Authentication auth){
        return service.close(id, req, auth);
    }

    @PostMapping("/{id}/reopen")
    public Ticket reopen(@PathVariable Long id, @RequestBody ActionRequest req, Authentication auth){
        return service.reopen(id, req, auth);
    }
}
