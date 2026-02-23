package com.railway.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
@Entity
@Table(name = "trains")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainId;

    @NotBlank(message = "Train name is required")
    @Column(nullable = false)
    private String trainName;

    @NotBlank(message = "Train number is required")
    @Column(nullable = false, unique = true)
    private String trainNumber;

    @NotBlank(message = "Source is required")
    @Column(nullable = false)
    private String source;

    @NotBlank(message = "Destination is required")
    @Column(nullable = false)
    private String destination;

    @NotBlank(message = "Departure time is required")
    private String departureTime;

    @NotBlank(message = "Arrival time is required")
    private String arrivalTime;

   
    @Builder.Default
    private int totalSeats = 0;

    @Builder.Default private int availableSeats = 0;
    @Builder.Default private int sleeperSeats   = 0;
    @Builder.Default private int acSeats        = 0;
    @Builder.Default private int generalSeats   = 0;

    @DecimalMin("0.0") @Builder.Default private double fareSleeper = 0;
    @DecimalMin("0.0") @Builder.Default private double fareAC      = 0;
    @DecimalMin("0.0") @Builder.Default private double fareGeneral = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticket> tickets;
    
    @PrePersist
@PreUpdate
public void calculateSeats() {
    this.totalSeats = sleeperSeats + acSeats + generalSeats;
    this.availableSeats = totalSeats;
}

    public enum Status { ACTIVE, INACTIVE }

    public double getFareForType(String seatType) {
        return switch (seatType) {
            case "Sleeper" -> fareSleeper;
            case "AC"      -> fareAC;
            default        -> fareGeneral;
        };
    }

    public int getAvailableForType(String seatType) {
        return switch (seatType) {
            case "Sleeper" -> sleeperSeats;
            case "AC"      -> acSeats;
            default        -> generalSeats;
        };
    }
}
