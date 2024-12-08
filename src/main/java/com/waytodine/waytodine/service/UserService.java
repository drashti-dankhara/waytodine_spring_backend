package com.waytodine.waytodine.service;

import com.waytodine.waytodine.model.User;
import com.waytodine.waytodine.repository.UserRepository;
import com.waytodine.waytodine.util.ApiResponse;
import com.waytodine.waytodine.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender mailSender;

    private final ImageService imageService;

    private final Map<String, String> otpStore = new HashMap<>();

    @Autowired
    public UserService(@Lazy ImageService imageService) {
        this.imageService = imageService;
    }

    public String register(User user) {
        // Check if email or phone number already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email or phone number already in use.");
        }
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        // Generate JWT
        return jwtUtil.generateToken(user.getUserId());
    }

    public String login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return jwtUtil.generateToken(userOptional.get().getUserId());
            } else {
                throw new RuntimeException("Invalid password.");
            }
        } else {
            throw new RuntimeException("User not found.");
        }
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User updateProfile(Long userId, User userDetails, MultipartFile profilePic) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Update fields
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setPhoneNumber(userDetails.getPhoneNumber());
            user.setLocation(userDetails.getLocation());

            // Handle profile picture upload
            if (profilePic != null && !profilePic.isEmpty()) {
                String fileName = imageService.uploadImage(profilePic);
                user.setProfilePic(fileName);
            }

            return userRepository.save(user);
        }
        return null;
    }

    public String saveProfilePic(MultipartFile file) {
        // Construct the directory path based on the application's working directory
        String directory = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "uploads";

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


    public ApiResponse sendOtp(String email) {
        // Check if the email is registered
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            return new ApiResponse("Email is not registered.", null, false);
        }

        String otp = generateOtp();
        otpStore.put(email, otp); // Store OTP for the email
        sendEmail(email, otp);

        return new ApiResponse("OTP sent to your email.", null, true);
    }

    public ApiResponse verifyOtpAndUpdatePassword(String email, String otp, String newPassword) {
        String storedOtp = otpStore.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            // Remove the OTP after successful verification
            otpStore.remove(email);

            // Find the user by email
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Encode the new password
                String encodedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encodedPassword);

                // Save the updated user
                userRepository.save(user); // Save the updated user entity

                return new ApiResponse("Password updated successfully.", null, true);
            } else {
                return new ApiResponse("User not found.", null, false);
            }
        }
        return new ApiResponse("Invalid OTP.", null, false);
    }


    private String generateOtp() {
        return String.valueOf(new Random().nextInt(999999)); // Generate a 6-digit OTP
    }

    private void sendEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);
        mailSender.send(message);
    }

    public ApiResponse changePassword(String token, String oldPassword, String newPassword) {
        Long userId = jwtUtil.extractUserId(token); // Extract user ID from token

        if (userId == null) {
            return new ApiResponse("Invalid token.", null, false);
        }

        User user = userRepository.findById(userId).orElse(null); // Fetch user by ID
        if (user == null) {
            return new ApiResponse("User not found.", null, false);
        }

        // Check if the old password matches
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return new ApiResponse("Old password is incorrect.", null, false);
        }

        // Encode the new password and update
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return new ApiResponse("Password updated successfully.", null, true);
    }
}
