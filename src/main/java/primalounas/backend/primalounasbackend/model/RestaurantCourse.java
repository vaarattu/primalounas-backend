package primalounas.backend.primalounasbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name="Course")
@Table(name="course")
public class RestaurantCourse {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String price;

    private String type;

    @ElementCollection
    private List<FoodTags> foodTags;
}