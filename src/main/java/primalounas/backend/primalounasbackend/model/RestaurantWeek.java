package primalounas.backend.primalounasbackend.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name="Week")
@Table(name="week")
public class RestaurantWeek {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private long id;

    private Date saveDate;

    private String weekName;

    @OneToMany(cascade = CascadeType.ALL)
    private List<RestaurantDay> days;
}
