package primalounas.backend.primalounasbackend.model;

import javax.persistence.*;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name="Day")
@Table(name="day")
public class RestaurantDay {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private long id;

    private String day;
/*
    @ManyToOne
    private RestaurantWeek week;
    */
    @OneToMany(cascade = CascadeType.ALL)
    private List<RestaurantCourse> courses;
}
