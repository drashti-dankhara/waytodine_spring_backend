package com.waytodine.waytodine.controller;

import com.waytodine.waytodine.model.Feedback;
import com.waytodine.waytodine.model.Restaurant;
import com.waytodine.waytodine.model.User;
import com.waytodine.waytodine.repository.RestaurantRepository;
import com.waytodine.waytodine.repository.UserRepository;
import com.waytodine.waytodine.service.FeedbackService;
import com.waytodine.waytodine.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.waytodine.waytodine.util.JwtUtil;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @PostMapping("/add-feedback")
    public ResponseEntity<ApiResponse> addFeedback(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> requestBody) {
        try {
            Long userId = jwtUtil.extractUserId(token);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse("Invalid or missing token.", null, false));
            }

            User user = userRepository.findByUserId(userId).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("User not found.", null, false));
            }
            Long restaurantId = ((Number) requestBody.get("restaurantId")).longValue();
            Integer rating = (Integer) requestBody.get("rating");
            String review = (String) requestBody.get("review");
            Restaurant restaurant = restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));


            // Create a new Feedback instance and set its properties
            Feedback feedback = new Feedback();
            feedback.setRestaurant(restaurant);
            feedback.setCustomer(user);
            feedback.setRating(rating);
            feedback.setReview(review);

            Feedback savedFeedback = feedbackService.addFeedback(feedback);

            return ResponseEntity.ok(new ApiResponse("Feedback added successfully", savedFeedback, true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An error occurred while adding the feedback.", null, false));
        }
    }

    @GetMapping("/get-all-feedback/{restaurantId}")
    public ApiResponse getFeedbackByRestaurantId(@PathVariable Long restaurantId) {
        List<Feedback> feedbackList = feedbackService.getFeedbackByRestaurantId(restaurantId);
        if (feedbackList.isEmpty()) {
            return new ApiResponse("No feedback found for the given restaurant ID", null, false);
        }
        return new ApiResponse("Feedback for the given restaurant ID retrieved successfully", feedbackList, true);
    }
}
