package server.database;

import commons.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository< Activity, Long>{

    @Query("select a from Activity a where a.consumption_in_wh = ?1")
    List<Activity> findByConsumption(Long consumption);

    @Query("select a from Activity a where a.consumption_in_wh > ?1 AND a.consumption_in_wh < ?2")
    List<Activity> findByConsumption(Long lowerBound, Long upperBound);

}
