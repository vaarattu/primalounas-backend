package primalounas.backend.primalounasbackend.services;

import primalounas.backend.primalounasbackend.model.RestaurantWeek;

import java.util.List;

public interface RestaurantMenuService {
    List<RestaurantWeek> getAllWeeks();
    List<RestaurantWeek> getCurrentWeek();
    List<RestaurantWeek> getWeekNumber(int weekIdentifier);
    RestaurantWeek addNewWeek(RestaurantWeek restaurantWeek);
}
