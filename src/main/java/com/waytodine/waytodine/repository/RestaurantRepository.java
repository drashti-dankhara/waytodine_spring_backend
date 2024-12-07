package com.waytodine.waytodine.repository;

import com.waytodine.waytodine.dto.categoryWrapperClass;
import com.waytodine.waytodine.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByEmail(String email);
    List<Restaurant> findByStatus(int status);

    Optional<Restaurant> findByEmailAndStatus(String email, int status);

    @Query("SELECT DISTINCT c.categoryId, c.name FROM MenuItem m JOIN m.category c WHERE m.restaurant.restaurantId = :restaurantId")
    List<Object[]> findCategoryIdAndNameByRestaurantId(Long restaurantId);
}
