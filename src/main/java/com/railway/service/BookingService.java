package com.railway.service;

import com.railway.dto.BookingDTO;
import com.railway.model.*;
import com.railway.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final TicketRepository  ticketRepository;
    private final TrainRepository   trainRepository;
    private final PaymentRepository paymentRepository;

    /** Book a ticket — full transactional flow */
    @Transactional
    public Ticket bookTicket(User user, BookingDTO dto, String paymentMethod) {
        Train train = trainRepository.findById(dto.getTrainId())
            .orElseThrow(() -> new RuntimeException("Train not found"));

        String seatType = dto.getSeatType();
        int available   = train.getAvailableForType(seatType);

        if (available <= 0)
            throw new RuntimeException("No seats available for seat type: " + seatType);

        // Deduct seat
        switch (seatType) {
            case "Sleeper" -> train.setSleeperSeats(train.getSleeperSeats() - 1);
            case "AC"      -> train.setAcSeats(train.getAcSeats() - 1);
            default        -> train.setGeneralSeats(train.getGeneralSeats() - 1);
        }
        train.setAvailableSeats(train.getAvailableSeats() - 1);
        trainRepository.save(train);

        // Generate seat number
        String prefix    = seatType.equals("Sleeper") ? "SL" : seatType.equals("AC") ? "AC" : "GN";
        long   booked    = ticketRepository.countByStatus(Ticket.TicketStatus.BOOKED);
        String seatNum   = prefix + String.format("%03d", booked + 1);

        double fare      = train.getFareForType(seatType);

        Ticket ticket = Ticket.builder()
            .user(user)
            .train(train)
            .passengerName(dto.getPassengerName())
            .passengerAge(dto.getPassengerAge())
            .passengerGender(Ticket.Gender.valueOf(dto.getPassengerGender()))
            .seatType(Ticket.SeatType.valueOf(seatType))
            .seatNumber(seatNum)
            .journeyDate(LocalDate.parse(dto.getJourneyDate()))
            .amount(fare)
            .status(Ticket.TicketStatus.BOOKED)
            .build();

        ticket = ticketRepository.save(ticket);

        // Simulate payment (95% success)
        boolean success = Math.random() > 0.05;
        Payment payment = Payment.builder()
            .ticket(ticket)
            .amount(fare)
            .paymentMethod(Payment.PaymentMethod.valueOf(paymentMethod))
            .paymentStatus(success ? Payment.PaymentStatus.SUCCESS : Payment.PaymentStatus.FAILED)
            .transactionRef("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .build();

        paymentRepository.save(payment);

        if (!success) {
            cancelTicketInternal(ticket, train);
            throw new RuntimeException("Payment failed. Please try again.");
        }

        return ticket;
    }

    /** Get tickets for a user */
    public List<Ticket> getUserTickets(User user) {
        return ticketRepository.findByUserOrderByBookedAtDesc(user);
    }

    /** Get ALL tickets (admin) */
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAllByOrderByBookedAtDesc();
    }

    /** Cancel a ticket */
    @Transactional
    public double cancelTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getStatus() == Ticket.TicketStatus.CANCELLED)
            throw new RuntimeException("Ticket already cancelled");

        Train train = ticket.getTrain();
        double refund = cancelTicketInternal(ticket, train);

        // Record refund
        paymentRepository.findByTicketTicketId(ticketId).ifPresent(p -> {
            p.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
            paymentRepository.save(p);
        });

        return refund;
    }

    private double cancelTicketInternal(Ticket ticket, Train train) {
        ticket.setStatus(Ticket.TicketStatus.CANCELLED);
        ticketRepository.save(ticket);

        // Restore seat
        switch (ticket.getSeatType().name()) {
            case "Sleeper" -> train.setSleeperSeats(train.getSleeperSeats() + 1);
            case "AC"      -> train.setAcSeats(train.getAcSeats() + 1);
            default        -> train.setGeneralSeats(train.getGeneralSeats() + 1);
        }
        train.setAvailableSeats(train.getAvailableSeats() + 1);
        trainRepository.save(train);

        // Refund: 80% if ≥24 hrs, else 50%
        long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(
            LocalDate.now(), ticket.getJourneyDate());
        return daysLeft >= 1
            ? ticket.getAmount() * 0.80
            : ticket.getAmount() * 0.50;
    }

    public long countBookings()  { return ticketRepository.countByStatus(Ticket.TicketStatus.BOOKED); }
    public double totalRevenue() { Double r = ticketRepository.sumRevenue(); return r == null ? 0 : r; }
}
