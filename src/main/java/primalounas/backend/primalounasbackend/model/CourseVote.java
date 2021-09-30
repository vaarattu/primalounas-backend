package primalounas.backend.primalounasbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "course_votes")
public class CourseVote {

    @Id
    @Column(name = "course_id")
    private long id;

    private int likes;

    private int dislikes;

    private int votes;

    @OneToOne
    @MapsId
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private RestaurantCourse course;
}