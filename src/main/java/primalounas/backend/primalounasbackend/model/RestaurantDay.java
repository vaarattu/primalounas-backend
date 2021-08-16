package primalounas.backend.primalounasbackend.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RestaurantDay {
    private String day;
    private List<RestaurantCourse> courses;
}
