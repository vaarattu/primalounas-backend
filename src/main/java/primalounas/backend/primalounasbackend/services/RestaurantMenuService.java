package primalounas.backend.primalounasbackend.services;

import primalounas.backend.primalounasbackend.model.FrequentCourse;
import primalounas.backend.primalounasbackend.model.RestaurantWeek;

import java.util.HashMap;
import java.util.List;

public interface RestaurantMenuService {
    List<RestaurantWeek> getAllWeeks();
    List<RestaurantWeek> getCurrentWeek();
    List<FrequentCourse> getFrequentCourses();
    RestaurantWeek getWeekByIdentifier(int weekIdentifier);
    RestaurantWeek getWeekById(long id);
    RestaurantWeek addNewWeek(RestaurantWeek restaurantWeek);
}
