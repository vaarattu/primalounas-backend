package primalounas.backend.primalounasbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import primalounas.backend.primalounasbackend.model.RestaurantWeek;
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
		RestaurantWeek week = null;
		String errorText = "";
		try {
			week = this.restaurantMenuService.getCurrentWeek();
		} catch (Exception ex){
			errorText = ex.getMessage();
		}

		if (week != null && errorText.equals("")){
			return ResponseEntity.ok(week);
		}
		else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorText);
		}
	}

	@GetMapping(value = "/all")
	public ResponseEntity<Object> fetchAll() {
		List<RestaurantWeek> weeks = null;
		String errorText = "";
		try {
			weeks = this.restaurantMenuService.getAllWeeks();
		} catch (Exception ex){
			errorText = ex.getMessage();
		}

		if (weeks != null && errorText.equals("")){
			return ResponseEntity.ok(weeks);
		}
		else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorText);
		}
	}
}
