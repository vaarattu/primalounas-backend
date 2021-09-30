package primalounas.backend.primalounasbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import primalounas.backend.primalounasbackend.model.RestaurantCourse;

@Repository
public interface RestaurantCourseRepository extends JpaRepository<RestaurantCourse, Long> {

    @Query(value = "SELECT * FROM Courses c WHERE c.name = :name", nativeQuery = true)
    RestaurantCourse findCourseByName(@Param("name") String name);
}