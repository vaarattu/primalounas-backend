package primalounas.backend.primalounasbackend.services;

import primalounas.backend.primalounasbackend.model.RestaurantWeek;

import java.util.List;

public interface RestaurantMenuService {
    List<RestaurantWeek> getAll();
    RestaurantWeek add(RestaurantWeek restaurantWeek);
}
