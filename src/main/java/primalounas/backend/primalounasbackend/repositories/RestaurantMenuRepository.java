package primalounas.backend.primalounasbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import primalounas.backend.primalounasbackend.model.RestaurantDay;

public interface RestaurantMenuRepository extends JpaRepository<RestaurantDay, Long> {

}