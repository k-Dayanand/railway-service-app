package com.railway.repository;

import com.railway.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {

    List<Train> findBySourceIgnoreCaseAndDestinationIgnoreCaseAndStatus(
            String source, String destination, Train.Status status);

    List<Train> findByStatus(Train.Status status);

    boolean existsByTrainNumber(String trainNumber);

    @Query("SELECT COUNT(t) FROM Train t WHERE t.status = 'ACTIVE'")
    long countActive();
}
