package com.waytodine.waytodine.controller;

import com.waytodine.waytodine.dto.CustomRestaurantDetails;
import com.waytodine.waytodine.model.Category;
import com.waytodine.waytodine.model.Restaurant;
import com.waytodine.waytodine.service.RestaurantService;
import com.waytodine.waytodine.util.ApiResponse;
import com.waytodine.waytodine.util.JwtUtil;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/send-inquiry")
    public ApiResponse sendInquiry(@RequestParam("name") String name,
                                   @RequestParam("email") String email,
                                   @RequestParam("phoneNumber") String phoneNumber,
                                   @RequestParam("location") String location,
                                   @RequestParam("city") String city,
                                   @RequestParam("country") String country,
                                   @RequestParam(value = "restaurantDocument", required = false) MultipartFile restaurantDocument) {
        try {

            Restaurant restaurant = new Restaurant();
            restaurant.setName(name);
            restaurant.setEmail(email);
            restaurant.setPhoneNumber(phoneNumber);
            restaurant.setLocation(location);
            restaurant.setCity(city);
            restaurant.setCountry(country);
            restaurant.setPassword("nopassword");

            Restaurant createdRestaurant = restaurantService.sendInquiry(restaurant,restaurantDocument);
            if (createdRestaurant == null) {
                return new ApiResponse("Inquiry already sent by you.", null, false);
            }
            return new ApiResponse("Inquiry sent successfully.", createdRestaurant, true);
        } catch (Exception e) {
            return new ApiResponse("Failed to send inquiry: " + e.getMessage(), null, false);
        }
    }

    @GetMapping("/get-restaurant-inquiries/{status}")
    public ResponseEntity<ApiResponse> getRestaurantInquiries(@PathVariable("status") int status) {
        List<Restaurant> res = restaurantService.listRestaurantInquiries(status);

        if (res.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("No categories found.", null, false));
        }

        // Iterate over each restaurant in the list and update the restaurant document URL
        for (Restaurant restaurant : res) {
            // Assuming `getRestaurantDocument()` returns the document path for the restaurant
            String fullDocumentUrl =  restaurant.getRestaurantDocument();
            restaurant.setRestaurantDocument(fullDocumentUrl); // Add a setter for the full URL in your Restaurant class
        }

        return ResponseEntity.ok(new ApiResponse("Categories fetched successfully", res, true));
    }


    //    admin side ------------------------------------------------------------------------
    @PutMapping("/accept-inquiry")
    public ApiResponse acceptInquiry(@RequestBody Map<String, Long> requestBody) {
        try {
            Long restaurantId = requestBody.get("restaurantId");
            Restaurant updatedRestaurant = restaurantService.acceptInquiry(restaurantId);
            return new ApiResponse("Inquiry accepted successfully.", updatedRestaurant, true);
        } catch (Exception e) {
            return new ApiResponse("Failed to accept inquiry: " + e.getMessage(), null, false);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        try {
            String token = restaurantService.login(email, password);
            ApiResponse response = new ApiResponse("Login successful.", Map.of("token", token), true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("Login failed: " + e.getMessage(), null, false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/get-all-restaurants")
    public ResponseEntity<ApiResponse> getAllRestaurants() {
        try {
            // Retrieve the active restaurants with their details
            List<CustomRestaurantDetails> activeRestaurants = restaurantService.getActiveRestaurantsWithDetails(null);

            // Check if the list is empty and respond accordingly
            if (activeRestaurants == null || activeRestaurants.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("No restaurants found.", null, false));
            }

            // Return successful response with the list of restaurants
            return ResponseEntity.ok(new ApiResponse("Restaurants fetched successfully.", activeRestaurants, true));
        } catch (Exception e) {
            // Handle any exceptions that occur during the retrieval process
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An error occurred while fetching the restaurants.", null, false));
        }
    }

    @GetMapping("/get-all-restaurants-by-name/{name}")
    public ResponseEntity<ApiResponse> getAllRestaurantsByName(@PathVariable("name") String name) {
        try {
            // Retrieve the active restaurants with their details
            List<CustomRestaurantDetails> activeRestaurants = restaurantService.getActiveRestaurantsWithDetails(name);

            // Check if the list is empty and respond accordingly
            if (activeRestaurants == null || activeRestaurants.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("No restaurants found.", null, false));
            }

            // Return successful response with the list of restaurants
            return ResponseEntity.ok(new ApiResponse("Restaurants fetched successfully.", activeRestaurants, true));
        } catch (Exception e) {
            // Handle any exceptions that occur during the retrieval process
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An error occurred while fetching the restaurants.", null, false));
        }
    }


    @GetMapping("/get-restaurant/{id}")
    public ResponseEntity<ApiResponse> getRestaurantWithDetails(@PathVariable("id") Long restaurantId) {
        try {

            // Fetch restaurant by ID
            CustomRestaurantDetails restaurant = restaurantService.getRestaurantWithDetailsById(restaurantId);

            if (restaurant == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Restaurant not found.", null, false));
            }

            return ResponseEntity.ok(new ApiResponse("Restaurant fetched successfully.", restaurant, true));
        } catch (NoResultException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Restaurant not found.", null, false));
        } catch (Exception e) {
            System.out.println("here ==============" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An error occurred while fetching the restaurant.", null, false));
        }
    }

}