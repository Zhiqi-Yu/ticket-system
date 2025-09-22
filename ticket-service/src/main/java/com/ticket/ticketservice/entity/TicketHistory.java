package com.ticket.ticketservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ticket.ticketservice.model.TicketStatus;
import com.ticket.ticketservice.entity.Employee;
import jakarta.persistence.*;
import java.util.Date;

@Entity @Table(name="ticket_history")
public class TicketHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) @JsonIgnore private Ticket ticket;
    @ManyToOne private Employee actionBy;

    @Enumerated(EnumType.STRING)
    private TicketStatus action;

    @Temporal(TemporalType.TIMESTAMP)
    private Date actionAt = new Date();

    @Column(length=1000) private String comments;

    // getters/setters
    public Long getId(){ return id; }
    public Ticket getTicket(){ return ticket; }
    public void setTicket(Ticket ticket){ this.ticket = ticket; }
    public Employee getActionBy(){ return actionBy; }
    public void setActionBy(Employee actionBy){ this.actionBy = actionBy; }
    public TicketStatus getAction(){ return action; }
    public void setAction(TicketStatus action){ this.action = action; }
    public Date getActionAt(){ return actionAt; }
    public void setActionAt(Date actionAt){ this.actionAt = actionAt; }
    public String getComments(){ return comments; }
    public void setComments(String comments){ this.comments = comments; }
}
