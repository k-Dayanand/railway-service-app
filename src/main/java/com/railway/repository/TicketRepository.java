package com.railway.repository;

import com.railway.model.Ticket;
import com.railway.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByUserOrderByBookedAtDesc(User user);

    List<Ticket> findAllByOrderByBookedAtDesc();

    long countByStatus(Ticket.TicketStatus status);

    @Query("SELECT SUM(t.amount) FROM Ticket t WHERE t.status = 'BOOKED'")
    Double sumRevenue();
}
