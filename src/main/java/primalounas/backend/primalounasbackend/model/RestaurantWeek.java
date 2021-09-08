package primalounas.backend.primalounasbackend.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="weeks")
public class RestaurantWeek {

    @Id
    private long id;

    private Date saveDate;

    private String weekName;

    @OneToMany(cascade = CascadeType.ALL)
    private List<RestaurantDay> days;
}
