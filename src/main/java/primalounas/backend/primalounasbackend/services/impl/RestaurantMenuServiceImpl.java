package primalounas.backend.primalounasbackend.services.impl;

import java.util.List;
import java.util.Optional;

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
public class RestaurantMenuServiceImpl implements RestaurantMenuService {

    @Autowired
    private RestaurantMenuRepository restaurantMenuRepository;

    @Cacheable("allWeeks")
    @Override
    public List<RestaurantWeek> getAllWeeks() {
        return this.restaurantMenuRepository.findAll();
    }

    @Cacheable("currentWeek")
    @Override
    public RestaurantWeek getCurrentWeek() {
        Optional<RestaurantWeek> nextWeek = this.restaurantMenuRepository.findById(Common.NextWeekIdentifier());
        Optional<RestaurantWeek> thisWeek = this.restaurantMenuRepository.findById(Common.CurrentWeekIdentifier());
        return nextWeek.orElseGet(thisWeek::get);
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
