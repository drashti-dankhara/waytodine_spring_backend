package com.waytodine.waytodine.service;

import com.waytodine.waytodine.model.Feedback;
import com.waytodine.waytodine.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback addFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbackByRestaurantId(Long restaurantId) {
        return feedbackRepository.findByRestaurantRestaurantId(restaurantId, Sort.by(Sort.Order.desc("createdAt")));
    }
}
