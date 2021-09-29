package primalounas.backend.primalounasbackend.services.impl;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import primalounas.backend.primalounasbackend.model.FrequentCourse;
import primalounas.backend.primalounasbackend.model.RestaurantCourse;
import primalounas.backend.primalounasbackend.model.RestaurantDay;
import primalounas.backend.primalounasbackend.model.RestaurantWeek;
import primalounas.backend.primalounasbackend.repositories.RestaurantCourseRepository;
import primalounas.backend.primalounasbackend.repositories.RestaurantMenuRepository;
import primalounas.backend.primalounasbackend.services.RestaurantMenuService;
import primalounas.backend.primalounasbackend.util.Common;

@CacheConfig(cacheNames = {"allWeeks", "currentWeek", "frequentCourses"})
@Service
@Slf4j
public class RestaurantMenuServiceImpl implements RestaurantMenuService {

    @Autowired
    private RestaurantMenuRepository restaurantMenuRepository;

    @Autowired
    private RestaurantCourseRepository restaurantCourseRepository;

    @Cacheable("allWeeks")
    @Override
    public List<RestaurantWeek> getAllWeeks() {
        log.info("[DB] Loading all from database.");
        return this.restaurantMenuRepository.findAll();
    }

    @Cacheable("currentWeek")
    @Override
    public List<RestaurantWeek> getCurrentWeek() {
        log.info("[DB] Loading current and next weeks from database.");

        List<RestaurantWeek> weeks = new ArrayList<>();

        Optional<RestaurantWeek> nextWeek = this.restaurantMenuRepository.findById(Common.NextWeekIdentifier());
        if(nextWeek.isPresent()){
            weeks.add(nextWeek.get());
            return weeks;
        }

        Optional<RestaurantWeek> thisWeek = this.restaurantMenuRepository.findById(Common.CurrentWeekIdentifier());
        if(thisWeek.isPresent()){
            weeks.add(thisWeek.get());
            return weeks;
        }

        return weeks;
    }

    @Cacheable("frequentCourses")
    @Override
    public List<FrequentCourse> getFrequentCourses() {
        log.info("[DB] Loading frequent courses from database.");
        List<RestaurantWeek> weeks = this.restaurantMenuRepository.findAll();
        HashMap<String, Integer> frequentCoursesMap = new HashMap<>();

        for (RestaurantWeek week : weeks) {
            for(RestaurantDay day : week.getDays()) {
                for (RestaurantCourse course : day.getCourses()) {
                    if (frequentCoursesMap.containsKey(course.getName())){
                        frequentCoursesMap.put(course.getName(), frequentCoursesMap.get(course.getName()) + 1);
                    }
                    else {
                        frequentCoursesMap.put(course.getName(), 1);
                    }
                }
            }
        }

        List<FrequentCourse> frequentCourses = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : frequentCoursesMap.entrySet()){
            frequentCourses.add(new FrequentCourse(entry.getKey(), entry.getValue()));
        }

        frequentCourses.sort(Comparator.comparingInt(FrequentCourse::getCount));
        Collections.reverse(frequentCourses);

        return frequentCourses;
    }

    @Override
    public RestaurantWeek getWeekByIdentifier(int weekIdentifier) {
        return this.restaurantMenuRepository.findWeekByWeekIdentifierParamNative(weekIdentifier);
    }

    @Override
    public RestaurantWeek getWeekById(long id) {
        Optional<RestaurantWeek> week = this.restaurantMenuRepository.findById(id);
        return week.isEmpty() ? null : week.get();
    }

    @CacheEvict(allEntries=true)
    @Override
    public RestaurantWeek addNewWeek(RestaurantWeek restaurantWeek) {
        log.info("[DB] Saving new week to database with id " + restaurantWeek.getId() + ".");
/*
        List<RestaurantCourse> courses = this.restaurantCourseRepository.findAll();

        for (RestaurantDay day : restaurantWeek.getDays()) {
            for (RestaurantCourse course : day.getCourses()) {
                if (courses.contains(course)){
                    course.setId(courses.get(courses.indexOf(course)).getId());
                }
                else{
                    courses.add(course);
                }
            }
        }
*/
        return this.restaurantMenuRepository.saveAndFlush(restaurantWeek);
    }
}
