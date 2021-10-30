package primalounas.backend.primalounasbackend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
@Entity
@Table(name = "courses")
public class RestaurantCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String type;

    @Type(type = "list-array")
    @Column(name = "tags", columnDefinition = "text[]")
    private List<String> tags;

    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @JsonManagedReference
    private CourseVote courseVote;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantCourse that = (RestaurantCourse) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}