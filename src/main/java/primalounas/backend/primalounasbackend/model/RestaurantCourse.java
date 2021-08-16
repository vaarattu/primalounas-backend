package primalounas.backend.primalounasbackend.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RestaurantCourse {
    private String name;
    private String price;
    private String type;
    private List<String> flags;
}
