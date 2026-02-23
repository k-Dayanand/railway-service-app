package com.railway.config;

import com.railway.model.Train;
import com.railway.model.User;
import com.railway.repository.TrainRepository;
import com.railway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer — runs on startup to seed default admin + trains.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository  userRepository;
    private final TrainRepository trainRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
public void run(String... args) {
    try {
        seedAdmin();
        seedTrains();
    } catch (Exception e) {
        log.error("Data initialization skipped: {}", e.getMessage());
    }
}

    private void seedAdmin() {
        if (!userRepository.existsByEmail("admin@railway.com")) {
            User admin = User.builder()
                .name("Administrator")
                .email("admin@railway.com")
                .password(passwordEncoder.encode("admin123"))
                .phone("9999999999")
                .role(User.Role.ADMIN)
                .build();
            userRepository.save(admin);
            log.info("✅ Default admin created: admin@railway.com / admin123");
        }
    }

    private void seedTrains() {
        if (trainRepository.count() == 0) {
            trainRepository.save(Train.builder()
                .trainName("Rajdhani Express").trainNumber("12301")
                .source("Delhi").destination("Mumbai")
                .departureTime("16:00").arrivalTime("08:00")
                .sleeperSeats(150).acSeats(100).generalSeats(50)
                .totalSeats(300).availableSeats(300)
                .fareSleeper(800).fareAC(1500).fareGeneral(300)
                .status(Train.Status.ACTIVE).build());

            trainRepository.save(Train.builder()
                .trainName("Shatabdi Express").trainNumber("12009")
                .source("Chennai").destination("Bengaluru")
                .departureTime("06:00").arrivalTime("11:00")
                .sleeperSeats(120).acSeats(80).generalSeats(50)
                .totalSeats(250).availableSeats(250)
                .fareSleeper(600).fareAC(1200).fareGeneral(200)
                .status(Train.Status.ACTIVE).build());

            trainRepository.save(Train.builder()
                .trainName("Duronto Express").trainNumber("12213")
                .source("Kolkata").destination("Delhi")
                .departureTime("20:00").arrivalTime("14:00")
                .sleeperSeats(160).acSeats(90).generalSeats(50)
                .totalSeats(300).availableSeats(300)
                .fareSleeper(900).fareAC(1800).fareGeneral(350)
                .status(Train.Status.ACTIVE).build());

            trainRepository.save(Train.builder()
                .trainName("Vande Bharat Express").trainNumber("22435")
                .source("Delhi").destination("Varanasi")
                .departureTime("06:00").arrivalTime("14:00")
                .sleeperSeats(0).acSeats(200).generalSeats(0)
                .totalSeats(200).availableSeats(200)
                .fareSleeper(0).fareAC(1600).fareGeneral(0)
                .status(Train.Status.ACTIVE).build());

            trainRepository.save(Train.builder()
                .trainName("Garib Rath Express").trainNumber("12909")
                .source("Ahmedabad").destination("Mumbai")
                .departureTime("23:00").arrivalTime("07:00")
                .sleeperSeats(140).acSeats(80).generalSeats(60)
                .totalSeats(280).availableSeats(280)
                .fareSleeper(500).fareAC(900).fareGeneral(180)
                .status(Train.Status.ACTIVE).build());

            log.info("✅ 5 sample trains seeded");
        }
    }
}
