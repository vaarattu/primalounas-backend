package primalounas.backend.primalounasbackend.model;

import javax.persistence.*;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="days")
public class RestaurantDay {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String day;

    @OneToMany(cascade = CascadeType.ALL)
    private List<RestaurantCourse> courses;
}
