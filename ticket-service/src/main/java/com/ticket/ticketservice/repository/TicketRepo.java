package com.ticket.ticketservice.repository;

import com.ticket.ticketservice.entity.Ticket;
import com.ticket.ticketservice.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TicketRepo extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreatedByEmail(String email);
    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByStatusInOrderByUpdatedAtDesc(Collection<TicketStatus> statuses);

}
