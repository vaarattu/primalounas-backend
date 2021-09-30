package primalounas.backend.primalounasbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import primalounas.backend.primalounasbackend.model.RestaurantDay;

public interface RestaurantDayRepository extends JpaRepository<RestaurantDay, Long> {

    @Query(value = "SELECT * FROM Days d WHERE d.name = :name", nativeQuery = true)
    RestaurantDay findDayByName(@Param("name") String name);
}