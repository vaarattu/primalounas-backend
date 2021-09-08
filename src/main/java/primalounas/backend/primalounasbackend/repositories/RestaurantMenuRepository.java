package primalounas.backend.primalounasbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import primalounas.backend.primalounasbackend.model.RestaurantWeek;

@Repository
public interface RestaurantMenuRepository extends JpaRepository<RestaurantWeek, Long> {

    @Query(value = "SELECT * FROM Week w WHERE w.week_identifier = :weekIdentifier", nativeQuery = true)
    RestaurantWeek findWeekByWeekIdentifierParamNative(@Param("weekIdentifier") Integer weekIdentifier);


}