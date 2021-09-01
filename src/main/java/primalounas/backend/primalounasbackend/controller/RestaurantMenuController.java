package primalounas.backend.primalounasbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import primalounas.backend.primalounasbackend.model.RestaurantDay;
import primalounas.backend.primalounasbackend.services.RestaurantMenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1")
public class RestaurantMenuController {

	@Autowired
	private RestaurantMenuService restaurantMenuService;

	@GetMapping(value = "/menu")
	public ResponseEntity<Object> fetchMenu() {
		List<RestaurantDay> items = this.restaurantMenuService.getAll();
		/*
		if (restResponse.getResponseCode() == 1){
			return ResponseEntity.ok(restResponse.getItems());
		}
		else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(restResponse.getErrorText());
		}
		*/
		return ResponseEntity.ok(items);
	}

}
