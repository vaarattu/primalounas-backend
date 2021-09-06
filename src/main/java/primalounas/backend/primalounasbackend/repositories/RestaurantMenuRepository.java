package primalounas.backend.primalounasbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import primalounas.backend.primalounasbackend.model.RestaurantWeek;

public interface RestaurantMenuRepository extends JpaRepository<RestaurantWeek, Long> {

}