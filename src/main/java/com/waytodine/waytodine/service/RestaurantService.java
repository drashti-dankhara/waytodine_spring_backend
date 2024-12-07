package com.waytodine.waytodine.service;

import com.waytodine.waytodine.dto.CustomRestaurantDetails;
import com.waytodine.waytodine.dto.categoryWrapperClass;
import com.waytodine.waytodine.model.Category;
import com.waytodine.waytodine.model.Restaurant;
import com.waytodine.waytodine.model.RestaurantDetails;
import com.waytodine.waytodine.model.User;
import com.waytodine.waytodine.repository.RestaurantRepository;
import com.waytodine.waytodine.util.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public Restaurant sendInquiry(Restaurant restaurant, MultipartFile restaurantDocument) {
        Optional<Restaurant> existingInquiry = restaurantRepository.findByEmailAndStatus(restaurant.getEmail(), 0);
        if (existingInquiry.isPresent()) {
            return null;
        }
        restaurant.setStatus(0); // Set status to inquiry stage
        if (restaurantDocument != null && !restaurantDocument.isEmpty()) {
            String fileName = saveDocument(restaurantDocument);
            restaurant.setRestaurantDocument(fileName);
        }
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> listRestaurantInquiries(int status){
        return restaurantRepository.findByStatus(status);
    }

    public String saveDocument(MultipartFile file) {
        String directory = "E:\\WayToDine\\Backend\\waytodine\\src\\main\\resources\\static\\uploads";

        // Create directory if it doesn't exist
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs(); // Create the directory and any non-existent parent directories
        }

        // Define a unique file name
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = directory + File.separator + fileName;

        // Save the file
        try {
            file.transferTo(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions
            throw new RuntimeException("Failed to save the profile picture.");
        }

        return fileName; // Return the file name for the database
    }


    public Restaurant acceptInquiry(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RuntimeException("Restaurant not found"));
        restaurant.setStatus(1); // Update status to accepted
        String passString = generateRandomPassword();
        restaurant.setPassword(passwordEncoder.encode(passString));
        restaurantRepository.save(restaurant);
        sendEmail(restaurant.getEmail(), "Your inquiry is accepted", "Your password is: " +  passString);
        return restaurant;
    }

    private String generateRandomPassword() {
        int length = 8;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public String login(String email, String password) {
        Optional<Restaurant> userOptional = restaurantRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            Restaurant restaurant = userOptional.get();
            if (restaurant.getStatus() == 1) {
            if (passwordEncoder.matches(password, restaurant.getPassword())) {
                return jwtUtil.generateToken(userOptional.get().getRestaurantId());
            } else {
                throw new RuntimeException("Invalid Credentials !!");
            }
            }
            else
            {
                throw new RuntimeException("Restaurant not found or not eligible to log in.");
            }
        } else {
            throw new RuntimeException("Invalid Credentials !!");
        }
    }

    public List<CustomRestaurantDetails> getActiveRestaurantsWithDetails(String searchString) {
        // Base JPQL query to fetch active restaurants with their details
        String jpql = "SELECT r FROM Restaurant r LEFT JOIN FETCH r.restaurantDetails WHERE r.status = 1";

        // Append search condition if searchString is provided
        if (searchString != null && !searchString.trim().isEmpty()) {
            jpql += " AND LOWER(r.name) LIKE LOWER(:searchString)";
        }

        // Execute the query and retrieve the list of active restaurants
        var query = entityManager.createQuery(jpql, Restaurant.class);

        // Set parameter if searchString is provided
        if (searchString != null && !searchString.trim().isEmpty()) {
            query.setParameter("searchString", "%" + searchString + "%");
        }

        List<Restaurant> restaurants = query.getResultList();
        // Initialize the list to hold custom restaurant details
        List<CustomRestaurantDetails> restaurantDetailsList = new ArrayList<>();

        // Iterate over each restaurant to populate custom details
        for (Restaurant restaurant : restaurants) {
            CustomRestaurantDetails restaurantDetails = new CustomRestaurantDetails();

            // Populate basic restaurant details with null checks
            restaurantDetails.setRestaurantId(restaurant.getRestaurantId());
            restaurantDetails.setName(restaurant.getName());
            restaurantDetails.setLocation(restaurant.getLocation());
            restaurantDetails.setCity(restaurant.getCity());
            restaurantDetails.setCountry(restaurant.getCountry());
            restaurantDetails.setPhoneNumber(restaurant.getPhoneNumber());
            restaurantDetails.setEmail(restaurant.getEmail());
            restaurantDetails.setAverageRating(3.5); // Default value
            restaurantDetails.setTotalReviews(5);   // Default value

            // Populate additional details if present
            RestaurantDetails detailsEntity = restaurant.getRestaurantDetails();
            if (detailsEntity != null) {
                restaurantDetails.setBannerImage(detailsEntity.getBannerImage());
                restaurantDetails.setDescription(detailsEntity.getDescription());
                restaurantDetails.setSpecialities(detailsEntity.getSpecialitiesList());
                restaurantDetails.setCurrentOfferDiscountRate(detailsEntity.getCurrentOfferDiscountRate());
                restaurantDetails.setOpeningHoursWeekdays(detailsEntity.getOpeningHoursWeekdays());
                restaurantDetails.setOpeningHoursWeekends(detailsEntity.getOpeningHoursWeekends());
                restaurantDetails.setMission(detailsEntity.getMission());
            }

            // Fetch and set categories for the current restaurant
            List<Object[]> result = restaurantRepository.findCategoryIdAndNameByRestaurantId(restaurant.getRestaurantId());
            if (result != null && !result.isEmpty()) {
                List<categoryWrapperClass> categories = result.stream()
                        .filter(row -> row != null && row.length >= 2 && row[0] != null && row[1] != null)
                        .map(row -> new categoryWrapperClass((Long) row[0], (String) row[1]))
                        .collect(Collectors.toList());
                restaurantDetails.setCategories(categories);
            } else {
                restaurantDetails.setCategories(null);
            }

            // Add the custom restaurant details object to the list
            restaurantDetailsList.add(restaurantDetails);
        }

        // Return the complete list of custom restaurant details
        return restaurantDetailsList;
    }


    public CustomRestaurantDetails getRestaurantWithDetailsById(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));

        RestaurantDetails detailsEntity = restaurant.getRestaurantDetails();

        // Create and populate CustomRestaurantDetails object
        CustomRestaurantDetails restaurantDetails = new CustomRestaurantDetails();

        // Check and set fields from Restaurant entity
        restaurantDetails.setRestaurantId(restaurant.getRestaurantId() != null ? restaurant.getRestaurantId() : null);
        restaurantDetails.setName(restaurant.getName() != null ? restaurant.getName() : null);
        restaurantDetails.setLocation(restaurant.getLocation() != null ? restaurant.getLocation() : null);
        restaurantDetails.setCity(restaurant.getCity() != null ? restaurant.getCity() : null);
        restaurantDetails.setCountry(restaurant.getCountry() != null ? restaurant.getCountry() : null);
        restaurantDetails.setPhoneNumber(restaurant.getPhoneNumber() != null ? restaurant.getPhoneNumber() : null);
        restaurantDetails.setEmail(restaurant.getEmail() != null ? restaurant.getEmail() : null);
        restaurantDetails.setAverageRating(3.5); // Default value
        restaurantDetails.setTotalReviews(5);   // Default value

        // Populate fields from detailsEntity if not null
        if (detailsEntity != null) {
            restaurantDetails.setBannerImage(detailsEntity.getBannerImage() != null ? detailsEntity.getBannerImage() : null);
            restaurantDetails.setDescription(detailsEntity.getDescription() != null ? detailsEntity.getDescription() : null);
            restaurantDetails.setSpecialities(detailsEntity.getSpecialitiesList() != null ? detailsEntity.getSpecialitiesList() : null);
            restaurantDetails.setCurrentOfferDiscountRate(detailsEntity.getCurrentOfferDiscountRate() != null ? detailsEntity.getCurrentOfferDiscountRate() : null);
            restaurantDetails.setOpeningHoursWeekdays(detailsEntity.getOpeningHoursWeekdays() != null ? detailsEntity.getOpeningHoursWeekdays() : null);
            restaurantDetails.setOpeningHoursWeekends(detailsEntity.getOpeningHoursWeekends() != null ? detailsEntity.getOpeningHoursWeekends() : null);
            restaurantDetails.setMission(detailsEntity.getMission() != null ? detailsEntity.getMission() : null);
        }

        // Fetch distinct categories
        List<Object[]> result = restaurantRepository.findCategoryIdAndNameByRestaurantId(restaurantId);

        if (result != null && !result.isEmpty()) {
            List<categoryWrapperClass> categories = result.stream()
                    .filter(row -> row != null && row.length >= 2 && row[0] != null && row[1] != null)
                    .map(row -> new categoryWrapperClass((Long) row[0], (String) row[1]))
                    .collect(Collectors.toList());
            restaurantDetails.setCategories(categories);
        } else {
            restaurantDetails.setCategories(null);
        }
        return restaurantDetails;
    }


//    public List<Restaurant> getActiveRestaurantsWithDetails(String searchString) {
//        // Base JPQL query
//        String jpql = "SELECT r FROM Restaurant r LEFT JOIN FETCH r.restaurantDetails WHERE r.status = 1";
//
//        // Append search condition if searchString is not null or empty
//        if (searchString != null && !searchString.trim().isEmpty()) {
//            jpql += " AND LOWER(r.name) LIKE LOWER(:searchString)";
//        }
//
//        // Create query
//        var query = entityManager.createQuery(jpql, Restaurant.class);
//
//        // Set parameter if searchString is provided
//        if (searchString != null && !searchString.trim().isEmpty()) {
//            query.setParameter("searchString", "%" + searchString + "%");
//        }
//
//        return query.getResultList();
//    }


}