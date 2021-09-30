package primalounas.backend.primalounasbackend.model;

import javax.persistence.*;

import lombok.*;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="days")
public class RestaurantDay {

    @Id
    private long id;

    private String day;

    @ManyToMany
    private List<RestaurantCourse> courses;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantDay that = (RestaurantDay) o;
        return day.equals(that.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day);
    }
}
