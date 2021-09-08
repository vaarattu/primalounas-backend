package primalounas.backend.primalounasbackend.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import primalounas.backend.primalounasbackend.model.RestaurantWeek;
import primalounas.backend.primalounasbackend.repositories.RestaurantMenuRepository;
import primalounas.backend.primalounasbackend.services.RestaurantMenuService;
import primalounas.backend.primalounasbackend.util.Common;

@Service
@CacheConfig(cacheNames="restaurantMenu")
public class RestaurantMenuServiceImpl implements RestaurantMenuService {

    @Autowired
    private RestaurantMenuRepository restaurantMenuRepository;

    //@Cacheable
    @Override
    public List<RestaurantWeek> getAllWeeks() {
        return this.restaurantMenuRepository.findAll();
    }

    //@Cacheable
    @Override
    public RestaurantWeek getCurrentWeek() {
        int weekIdentifier = Common.CurrentWeekIdentifier();
        return this.restaurantMenuRepository.findByWeekIdentifier(weekIdentifier);
    }

    //@Cacheable
    @Override
    public RestaurantWeek getWeekByIdentifier(int weekIdentifier) {
        return this.restaurantMenuRepository.findByWeekIdentifier(weekIdentifier);
    }

    @CacheEvict(allEntries=true)
    @Override
    public RestaurantWeek addNewWeek(RestaurantWeek restaurantWeek) {
        return this.restaurantMenuRepository.save(restaurantWeek);
    }
}
