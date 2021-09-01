package primalounas.backend.primalounasbackend.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import primalounas.backend.primalounasbackend.model.RestaurantDay;
import primalounas.backend.primalounasbackend.repositories.RestaurantMenuRepository;
import primalounas.backend.primalounasbackend.services.RestaurantMenuService;

@Service
@CacheConfig(cacheNames="restaurantMenu")
public class RestaurantMenuServiceImpl implements RestaurantMenuService {

    @Autowired
    private RestaurantMenuRepository restaurantMenuRepository;

    @Cacheable
    @Override
    public List<RestaurantDay> getAll() {
        //waitSomeTime();
        return this.restaurantMenuRepository.findAll();
    }

    @CacheEvict(allEntries=true)
    @Override
    public RestaurantDay add(RestaurantDay restaurantDay) {
        return this.restaurantMenuRepository.save(restaurantDay);
    }
}
