package primalounas.backend.primalounasbackend.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import primalounas.backend.primalounasbackend.model.RestaurantWeek;
import primalounas.backend.primalounasbackend.repositories.RestaurantMenuRepository;
import primalounas.backend.primalounasbackend.services.RestaurantMenuService;
import primalounas.backend.primalounasbackend.util.Common;

@CacheConfig(cacheNames = {"allWeeks", "currentWeek"})
@Service
@Slf4j
public class RestaurantMenuServiceImpl implements RestaurantMenuService {

    @Autowired
    private RestaurantMenuRepository restaurantMenuRepository;

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
        return this.restaurantMenuRepository.saveAndFlush(restaurantWeek);
    }
}
