package primalounas.backend.primalounasbackend.model;

import javax.persistence.*;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "restaurantDay", schema = "public")
public class RestaurantDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private long id;

    private String day;

    @ElementCollection
    private List<RestaurantCourse> courses;
}
