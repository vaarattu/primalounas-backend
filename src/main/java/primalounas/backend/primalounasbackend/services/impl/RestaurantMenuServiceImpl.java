package primalounas.backend.primalounasbackend.services.impl;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import primalounas.backend.primalounasbackend.model.*;
import primalounas.backend.primalounasbackend.repositories.CourseVoteRepository;
import primalounas.backend.primalounasbackend.repositories.RestaurantCourseRepository;
import primalounas.backend.primalounasbackend.repositories.RestaurantDayRepository;
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
    private RestaurantDayRepository restaurantDayRepository;

    @Autowired
    private RestaurantCourseRepository restaurantCourseRepository;

    @Autowired
    private CourseVoteRepository courseVoteRepository;

    @Cacheable("allWeeks")
    @Override
    public List<RestaurantWeek> getAllWeeks() {
        log.info("[DB] Loading all restaurant weeks from database.");
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
        log.info("[DB] Loading most frequent occurring courses from database.");
        List<RestaurantDay> days = this.restaurantDayRepository.findAll();
        HashMap<String, Integer> frequentCoursesMap = new HashMap<>();

        for (RestaurantDay day : days) {
            for (RestaurantCourse course : day.getCourses()) {
                if (frequentCoursesMap.containsKey(course.getName())) {
                    frequentCoursesMap.put(course.getName(), frequentCoursesMap.get(course.getName()) + 1);
                } else {
                    frequentCoursesMap.put(course.getName(), 1);
                }
            }
        }

        List<FrequentCourse> frequentCourses = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : frequentCoursesMap.entrySet()) {
            frequentCourses.add(new FrequentCourse(entry.getKey(), entry.getValue()));
        }

        frequentCourses.sort(Comparator.comparingInt(FrequentCourse::getCount));
        Collections.reverse(frequentCourses);

        return frequentCourses;
    }

    @Override
    public RestaurantWeek getWeekById(long id) {
        Optional<RestaurantWeek> week = this.restaurantMenuRepository.findById(id);
        return week.isEmpty() ? null : week.get();
    }

    @CacheEvict(allEntries=true)
    @Override
    public RestaurantWeek addNewWeek(RestaurantWeek restaurantWeek) {
        log.info("[DB] Saving new week to database with id: " + restaurantWeek.getId() + ".");

        for (RestaurantDay day : restaurantWeek.getDays()) {
            for (RestaurantCourse course : day.getCourses()) {
                course.setId(0);

                CourseVote vote = new CourseVote();
                vote.setLikes(0);
                vote.setDislikes(0);
                vote.setVotes(0);
                vote.setRanked(0);
                vote.setCourse(course);
                course.setCourseVote(vote);

                RestaurantCourse dbCourse = this.restaurantCourseRepository.findCourseByName(course.getName());
                if (dbCourse == null) {
                    course.setId(this.restaurantCourseRepository.save(course).getId());
                }
                else {
                    course.setId(dbCourse.getId());
                }
            }
            this.restaurantDayRepository.save(day);
        }

        return this.restaurantMenuRepository.saveAndFlush(restaurantWeek);
    }

    @Override
    public List<RestaurantCourse> getAllCourses() {
        log.info("[DB] Loading restaurant courses all from database.");
        return this.restaurantCourseRepository.findAll();
    }

    @Override
    public List<CourseVote> updateCourseVotes(List<CourseVote> courseVotes) {
        log.info("[DB] Updating votes for " + courseVotes.size() + " courses.");
        List<CourseVote> votesInDB = this.courseVoteRepository.findAll();

        for(CourseVote voteDB : votesInDB) {
            for(CourseVote vote : courseVotes) {
                if (voteDB.getId() == vote.getId()){
                    voteDB.setVotes(voteDB.getVotes() + vote.getVotes());
                    voteDB.setRanked(voteDB.getRanked() + vote.getRanked());
                    voteDB.setLikes(voteDB.getLikes() + vote.getLikes());
                    voteDB.setDislikes(voteDB.getDislikes() + vote.getDislikes());
                }
            }
        }

        return this.courseVoteRepository.saveAll(votesInDB);
    }
}
