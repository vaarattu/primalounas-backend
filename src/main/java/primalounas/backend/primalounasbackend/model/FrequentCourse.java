package primalounas.backend.primalounasbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FrequentCourse {
    private String name;
    private int count;
}
