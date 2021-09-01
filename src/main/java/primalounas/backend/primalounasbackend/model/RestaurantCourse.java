package primalounas.backend.primalounasbackend.model;

import java.util.List;
import lombok.*;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class RestaurantCourse {

    private String name;

    private String price;

    private String type;

    @ElementCollection
    private List<FoodTags> tags;
}
