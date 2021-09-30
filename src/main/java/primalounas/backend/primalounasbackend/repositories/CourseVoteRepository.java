package primalounas.backend.primalounasbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import primalounas.backend.primalounasbackend.model.CourseVote;

@Repository
public interface CourseVoteRepository extends JpaRepository<CourseVote, Long> {
}