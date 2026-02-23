package com.railway.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.SUCCESS;

    private String transactionRef;

    @Column(name = "paid_at", updatable = false)
    @Builder.Default
    private LocalDateTime paidAt = LocalDateTime.now();

    public enum PaymentMethod { UPI, DEBIT_CARD, CREDIT_CARD, NET_BANKING }
    public enum PaymentStatus { SUCCESS, FAILED, REFUNDED }
}
