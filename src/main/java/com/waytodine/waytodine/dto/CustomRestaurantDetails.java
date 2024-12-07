package com.waytodine.waytodine.dto;

import java.util.*;

public class CustomRestaurantDetails {

    private Long restaurantId;
    private String name;
    private String bannerImage;
    private String description;
    private List<String> specialities;
    private Double currentOfferDiscountRate;
    private String openingHoursWeekdays;
    private String openingHoursWeekends;
    private String mission;
    private String location;
    private String city;
    private String country;
    private String phoneNumber;
    private String email;
    private Double averageRating;
    private int totalReviews;
    private List<categoryWrapperClass> categories;


    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSpecialities() {
        return specialities;
    }

    public void setSpecialities(List<String> specialities) {
        this.specialities = specialities;
    }

    public Double getCurrentOfferDiscountRate() {
        return currentOfferDiscountRate;
    }

    public void setCurrentOfferDiscountRate(Double currentOfferDiscountRate) {
        this.currentOfferDiscountRate = currentOfferDiscountRate;
    }

    public String getOpeningHoursWeekdays() {
        return openingHoursWeekdays;
    }

    public void setOpeningHoursWeekdays(String openingHoursWeekdays) {
        this.openingHoursWeekdays = openingHoursWeekdays;
    }

    public String getOpeningHoursWeekends() {
        return openingHoursWeekends;
    }

    public void setOpeningHoursWeekends(String openingHoursWeekends) {
        this.openingHoursWeekends = openingHoursWeekends;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public List<categoryWrapperClass> getCategories() {
        return categories;
    }

    public void setCategories(List<categoryWrapperClass> categories) {
        this.categories = categories;
    }
}

