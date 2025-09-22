package com.ticket.ticketservice.model;
public class CreateTicketRequest {
    public String title;
    public String description;
    public TicketPriority priority = TicketPriority.MEDIUM;
    public String category;
}
