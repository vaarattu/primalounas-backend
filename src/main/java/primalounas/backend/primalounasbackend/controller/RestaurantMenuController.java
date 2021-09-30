package primalounas.backend.primalounasbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import primalounas.backend.primalounasbackend.model.CourseVote;
import primalounas.backend.primalounasbackend.model.FrequentCourse;
import primalounas.backend.primalounasbackend.model.RestaurantWeek;
import primalounas.backend.primalounasbackend.services.RestaurantMenuService;
import org.springframework.http.ResponseEntity;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1")
public class RestaurantMenuController {

	@Autowired
	private RestaurantMenuService restaurantMenuService;

	@GetMapping(value = "/menu")
	public ResponseEntity<Object> getMenu() {
		List<RestaurantWeek> weeks = new ArrayList<>();
		String errorText = "";
		try {
			weeks = this.restaurantMenuService.getCurrentWeek();
		} catch (Exception ex){
			errorText = ex.getMessage();
		}

		if (errorText.equals("")){
			return ResponseEntity.ok(weeks);
		}
		else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorText);
		}
	}

	@GetMapping(value = "/all")
	public ResponseEntity<Object> getAll() {
		List<RestaurantWeek> weeks = new ArrayList<>();
		String errorText = "";
		try {
			weeks = this.restaurantMenuService.getAllWeeks();
		} catch (Exception ex){
			errorText = ex.getMessage();
		}

		if (errorText.equals("")){
			return ResponseEntity.ok(weeks);
		}
		else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorText);
		}
	}

	@GetMapping(value = "/frequent")
	public ResponseEntity<Object> getFrequent() {
		List<FrequentCourse> courses = new ArrayList<>();
		String errorText = "";
		try {
			courses = this.restaurantMenuService.getFrequentCourses();
		} catch (Exception ex){
			errorText = ex.getMessage();
		}

		if (errorText.equals("")){
			return ResponseEntity.ok(courses);
		}
		else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorText);
		}
	}

	@GetMapping(value = "/voted")
	public ResponseEntity<Object> getCourseVotes() {
		List<CourseVote> courseVotes = new ArrayList<>();
		String errorText = "";
		try {
			courseVotes = this.restaurantMenuService.getAllCourseVotes();
		} catch (Exception ex){
			errorText = ex.getMessage();
		}

		if (errorText.equals("")){
			return ResponseEntity.ok(courseVotes);
		}
		else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorText);
		}
	}

	@PostMapping(value = "/vote")
	public ResponseEntity<Object> postVoteCourse(@RequestBody CourseVote courseVote) {
		String errorText = "";
		try {
			courseVote = this.restaurantMenuService.updateCourseVote(courseVote);
		} catch (Exception ex){
			errorText = ex.getMessage();
		}

		if (errorText.equals("")){
			return ResponseEntity.ok(courseVote);
		}
		else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorText);
		}
	}
}
