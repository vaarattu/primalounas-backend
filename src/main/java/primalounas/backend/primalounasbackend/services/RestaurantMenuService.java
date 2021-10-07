package primalounas.backend.primalounasbackend.services;

import primalounas.backend.primalounasbackend.model.CourseVote;
import primalounas.backend.primalounasbackend.model.FrequentCourse;
import primalounas.backend.primalounasbackend.model.RestaurantCourse;
import primalounas.backend.primalounasbackend.model.RestaurantWeek;

import java.util.List;

public interface RestaurantMenuService {

    List<RestaurantWeek> getAllWeeks();
    List<RestaurantWeek> getCurrentWeek();

    RestaurantWeek getWeekById(long id);
    RestaurantWeek addNewWeek(RestaurantWeek restaurantWeek);

    List<RestaurantCourse> getAllCourses();
    List<CourseVote> updateCourseVotes(List<CourseVote> courseVotes);

    List<FrequentCourse> getFrequentCourses();
}
