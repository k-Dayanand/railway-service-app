package com.railway.service;

import com.railway.model.Train;
import com.railway.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainService {

    private final TrainRepository trainRepository;

    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    public List<Train> getActiveTrains() {
        return trainRepository.findByStatus(Train.Status.ACTIVE);
    }

    public Train getTrainById(Long id) {
        return trainRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Train not found with id: " + id));
    }

    public List<Train> searchTrains(String source, String destination) {
        return trainRepository.findBySourceIgnoreCaseAndDestinationIgnoreCaseAndStatus(
            source, destination, Train.Status.ACTIVE);
    }

    public Train saveTrain(Train train) {
        // Auto-calculate total/available seats
        train.setTotalSeats(train.getSleeperSeats() + train.getAcSeats() + train.getGeneralSeats());
        train.setAvailableSeats(train.getTotalSeats());
        return trainRepository.save(train);
    }

    public Train updateTrain(Long id, Train updated) {
        Train existing = getTrainById(id);
        existing.setTrainName(updated.getTrainName());
        existing.setTrainNumber(updated.getTrainNumber());
        existing.setSource(updated.getSource());
        existing.setDestination(updated.getDestination());
        existing.setDepartureTime(updated.getDepartureTime());
        existing.setArrivalTime(updated.getArrivalTime());
        existing.setSleeperSeats(updated.getSleeperSeats());
        existing.setAcSeats(updated.getAcSeats());
        existing.setGeneralSeats(updated.getGeneralSeats());
        existing.setTotalSeats(updated.getSleeperSeats() + updated.getAcSeats() + updated.getGeneralSeats());
        existing.setFareSleeper(updated.getFareSleeper());
        existing.setFareAC(updated.getFareAC());
        existing.setFareGeneral(updated.getFareGeneral());
        existing.setStatus(updated.getStatus());
        return trainRepository.save(existing);
    }

    public void deleteTrain(Long id) {
        trainRepository.deleteById(id);
    }

    public long countActiveTrains() {
        return trainRepository.countActive();
    }
}
