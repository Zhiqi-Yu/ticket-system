package com.ticket.ticketservice.controller;

import com.ticket.ticketservice.entity.Ticket;
import com.ticket.ticketservice.entity.TicketHistory;
import com.ticket.ticketservice.model.ActionRequest;
import com.ticket.ticketservice.model.CreateTicketRequest;
import com.ticket.ticketservice.model.TicketStatus;
import com.ticket.ticketservice.service.TicketService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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



    @GetMapping(path = {"", "/"})
    public List<Ticket> list(@RequestParam(required=false) String status,
                             Authentication auth) {
        if (status != null) return service.listByStatus(TicketStatus.valueOf(status));
        return service.listMine(auth);
    }
    // 历史
    @GetMapping("/{id}/history")
    public List<TicketHistory> history(@PathVariable Long id){
        return service.historyOf(id);
    }

    // 上传附件（multipart）
    @PostMapping(path="/{id}/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Ticket upload(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        return service.uploadAttachment(id, file);
    }

    // 下载附件
    @GetMapping("/{id}/attachment")
    public ResponseEntity<Resource> download(@PathVariable Long id){
        Resource r = service.loadAttachment(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + r.getFilename() + "\"")
                .body(r);
    }

}
