package com.ticket.ticketservice.repository;

import com.ticket.ticketservice.entity.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketHistoryRepo extends JpaRepository<TicketHistory, Long> {}
