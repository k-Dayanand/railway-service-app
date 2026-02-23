package com.railway.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @Column(nullable = false)
    private String passengerName;

    private int passengerAge;

    @Enumerated(EnumType.STRING)
    private Gender passengerGender = Gender.Male;

    private LocalDate journeyDate;
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SeatType seatType = SeatType.General;

    private double amount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TicketStatus status = TicketStatus.BOOKED;

    @Column(name = "booked_at", updatable = false)
    @Builder.Default
    private LocalDateTime bookedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    public enum Gender       { Male, Female, Other }
    public enum SeatType     { Sleeper, AC, General }
    public enum TicketStatus { BOOKED, CANCELLED }
}
