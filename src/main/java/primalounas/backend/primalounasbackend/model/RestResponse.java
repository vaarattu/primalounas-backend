package primalounas.backend.primalounasbackend.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestResponse {
    private int responseCode;
    private String errorText;
    private List<RestaurantDay> items;
}