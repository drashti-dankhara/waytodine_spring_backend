package com.waytodine.waytodine.model;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


@Entity
@Table(name = "restaurant_details")
public class RestaurantDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_details_id")
    private Long restaurantDetailsId;

    @Column(name = "current_offer_discount_rate")
    private Double currentOfferDiscountRate;

    @Column(name = "opening_hours_weekdays")
    private String openingHoursWeekdays; // e.g., "Monday to Friday: 9:00 AM - 5:00 PM"

    @Column(name = "opening_hours_weekends")
    private String openingHoursWeekends; // e.g., "Saturday to Sunday: 10:00 AM - 6:00 PM"

    @Column(name = "specialities")
    private String specialities; // A comma-separated list or JSON representation of specialities

    @Column(name = "mission")
    private String mission;

    @Column(name = "banner_image")
    private String bannerImage; // URL or path to the banner image

    @Column(name = "description")
    private String description;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "restaurant_id", nullable = false)
    private Restaurant restaurant;


    // Getters and setters
    public Long getRestaurantDetailsId() {
        return restaurantDetailsId;
    }

    public void setRestaurantDetailsId(Long restaurantDetailsId) {
        this.restaurantDetailsId = restaurantDetailsId;
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

    public String getSpecialities() {
        return specialities;
    }

    public void setSpecialities(String specialities) {
        this.specialities = specialities;
    }

    public List<String> getSpecialitiesList() {
        if (specialities != null && !specialities.isEmpty()) {
            return Arrays.asList(specialities.split(","));
        }
        return null;
    }

    public void setSpecialitiesList(List<String> specialitiesList) {
        if (specialitiesList != null) {
            this.specialities = String.join(",", specialitiesList);
        } else {
            this.specialities = null;
        }
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
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

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
