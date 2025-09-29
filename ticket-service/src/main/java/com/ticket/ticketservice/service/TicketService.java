package com.ticket.ticketservice.service;

import com.ticket.ticketservice.entity.Ticket;
import com.ticket.ticketservice.entity.TicketHistory;
import com.ticket.ticketservice.model.*;
import com.ticket.ticketservice.repository.TicketHistoryRepo;
import com.ticket.ticketservice.repository.TicketRepo;
import com.ticket.ticketservice.entity.Employee;
import com.ticket.ticketservice.repository.EmployeeRepo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TicketService {

    private final FileStorageService fileStorageService;

    private final TicketRepo ticketRepo;
    private final TicketHistoryRepo historyRepo;
    private final EmployeeRepo employeeRepo;

    public TicketService(TicketRepo t, TicketHistoryRepo h, EmployeeRepo e, FileStorageService fs) {
        this.ticketRepo = t; this.historyRepo = h; this.employeeRepo = e; this.fileStorageService = fs;
    }

    private Employee me(Authentication auth){
        return employeeRepo.findByEmail(auth.getName()).orElseThrow();
    }

    private void history(Ticket t, Employee by, TicketStatus to, String comments){
        TicketHistory h = new TicketHistory();
        h.setTicket(t); h.setActionBy(by); h.setAction(to); h.setComments(comments);
        historyRepo.save(h);
    }

    @PreAuthorize("hasRole('USER')")
    public Ticket create(CreateTicketRequest req, Authentication auth){
        Employee creator = me(auth);
        Ticket t = new Ticket();
        t.setTitle(req.title);
        t.setDescription(req.description);
        t.setPriority(req.priority);
        t.setCategory(req.category);
        t.setCreatedBy(creator);
        t.setStatus(TicketStatus.OPEN);
        Ticket saved = ticketRepo.save(t);
        history(saved, creator, TicketStatus.OPEN, "created");
        return saved;
    }

    @PreAuthorize("hasRole('MANAGER')")
    public Ticket approve(Long id, ActionRequest req, Authentication auth){
        Ticket t = ticketRepo.findById(id).orElseThrow();
        t.setStatus(TicketStatus.APPROVED);
        t.setUpdatedAt(new Date());
        history(t, me(auth), TicketStatus.APPROVED, req.comments);
        return t;
    }

    @PreAuthorize("hasRole('MANAGER')")
    public Ticket reject(Long id, ActionRequest req, Authentication auth){
        Ticket t = ticketRepo.findById(id).orElseThrow();
        t.setStatus(TicketStatus.REJECTED);
        t.setUpdatedAt(new Date());
        history(t, me(auth), TicketStatus.REJECTED, req.comments);
        return t;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Ticket resolve(Long id, ActionRequest req, Authentication auth){
        Ticket t = ticketRepo.findById(id).orElseThrow();
        t.setStatus(TicketStatus.RESOLVED);
        t.setUpdatedAt(new Date());
        history(t, me(auth), TicketStatus.RESOLVED, req.comments);
        return t;
    }

    @PreAuthorize("hasRole('USER')")
    public Ticket close(Long id, ActionRequest req, Authentication auth){
        Ticket t = ticketRepo.findById(id).orElseThrow();
        t.setStatus(TicketStatus.CLOSED);
        t.setUpdatedAt(new Date());
        history(t, me(auth), TicketStatus.CLOSED, req.comments);
        return t;
    }

    @PreAuthorize("hasRole('USER')")
    public Ticket reopen(Long id, ActionRequest req, Authentication auth){
        Ticket t = ticketRepo.findById(id).orElseThrow();
        t.setStatus(TicketStatus.REOPENED);
        t.setUpdatedAt(new Date());
        history(t, me(auth), TicketStatus.REOPENED, req.comments);
        return t;
    }

    @PreAuthorize("isAuthenticated()")
    public Ticket get(Long id){ return ticketRepo.findById(id).orElseThrow(); }


    // 已登录用户的我的工单
    @PreAuthorize("isAuthenticated()")
    public List<Ticket> listMine(Authentication auth){
        return ticketRepo.findByCreatedByEmail(auth.getName());
    }

    // 按状态列出
    @PreAuthorize("isAuthenticated()")
    public List<Ticket> listByStatus(TicketStatus status){
        return ticketRepo.findByStatus(status);
    }

    // 历史
    @PreAuthorize("isAuthenticated()")
    public List<TicketHistory> historyOf(Long id){
        Ticket t = ticketRepo.findById(id).orElseThrow();
        return t.getHistory();
    }

    // 上传附件
    @PreAuthorize("hasRole('USER')")
    public Ticket uploadAttachment(Long id, MultipartFile file) throws IOException {
        Ticket t = ticketRepo.findById(id).orElseThrow();
        String path = fileStorageService.save(file, id);
        t.setAttachmentPath(path);
        t.setUpdatedAt(new Date());
        return t; // 事务结束会 flush
    }

    // 供下载使用
    @PreAuthorize("isAuthenticated()")
    public Resource loadAttachment(Long id){
        Ticket t = ticketRepo.findById(id).orElseThrow();
        if (t.getAttachmentPath()==null) throw new IllegalStateException("no attachment");
        return fileStorageService.loadAsResource(t.getAttachmentPath());
    }

}
