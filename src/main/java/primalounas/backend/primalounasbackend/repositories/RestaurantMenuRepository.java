package primalounas.backend.primalounasbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import primalounas.backend.primalounasbackend.model.RestaurantWeek;

import java.util.List;

public interface RestaurantMenuRepository extends JpaRepository<RestaurantWeek, Long> {

    List<RestaurantWeek> findByWeekIdentifier(int weekIdentifier);
}