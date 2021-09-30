package primalounas.backend.primalounasbackend.services;

import primalounas.backend.primalounasbackend.model.CourseVote;
import primalounas.backend.primalounasbackend.model.FrequentCourse;
import primalounas.backend.primalounasbackend.model.RestaurantWeek;

import java.util.List;

public interface RestaurantMenuService {

    List<RestaurantWeek> getAllWeeks();
    List<RestaurantWeek> getCurrentWeek();

    RestaurantWeek getWeekById(long id);
    RestaurantWeek addNewWeek(RestaurantWeek restaurantWeek);

    List<CourseVote> getAllCourseVotes();
    CourseVote updateCourseVote(CourseVote courseVote);

    List<FrequentCourse> getFrequentCourses();
}
