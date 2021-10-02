package primalounas.backend.primalounasbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import primalounas.backend.primalounasbackend.model.CourseVote;
import primalounas.backend.primalounasbackend.services.RestaurantMenuService;
import org.springframework.http.ResponseEntity;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1")
public class RestaurantMenuController {

	@Autowired
	private RestaurantMenuService restaurantMenuService;

	@GetMapping(value = "/menu")
	public ResponseEntity<Object> getMenu() {
		try {
			return ResponseEntity.ok(this.restaurantMenuService.getCurrentWeek());
		} catch (Exception ex){
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@GetMapping(value = "/all")
	public ResponseEntity<Object> getAll() {
		try {
			return ResponseEntity.ok(this.restaurantMenuService.getAllWeeks());
		} catch (Exception ex){
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@GetMapping(value = "/frequent")
	public ResponseEntity<Object> getFrequent() {
		try {
			return ResponseEntity.ok(this.restaurantMenuService.getFrequentCourses());
		} catch (Exception ex){
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@PostMapping(value = "/vote")
	public ResponseEntity<Object> postVoteCourse(@RequestBody String body) {
		String errorText = "";
		List<CourseVote> courseVotes;
		try {
			ObjectMapper mapper = new ObjectMapper();
			CourseVote[] votes = mapper.readValue(body, CourseVote[].class);
			courseVotes = Arrays.asList(votes);
			this.restaurantMenuService.updateCourseVotes(courseVotes);
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorText);
		}
	}

	@GetMapping(value = "/votes")
	public ResponseEntity<Object> getAllVotes() {
		try {
			return ResponseEntity.ok(this.restaurantMenuService.getAllVotes());
		} catch (Exception ex){
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}
}
