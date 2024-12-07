package com.waytodine.waytodine.repository;

import com.waytodine.waytodine.model.Feedback;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByRestaurantRestaurantId(Long restaurantId, Sort sort);
}
