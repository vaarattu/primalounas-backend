package primalounas.backend.primalounasbackend.services;

import primalounas.backend.primalounasbackend.model.RestaurantDay;

import java.util.List;

public interface RestaurantMenuService {
    public List<RestaurantDay> getAll();
    public RestaurantDay add(RestaurantDay restaurantDay);
}
