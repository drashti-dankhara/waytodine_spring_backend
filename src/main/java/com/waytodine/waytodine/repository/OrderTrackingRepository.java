package com.waytodine.waytodine.repository;

import com.waytodine.waytodine.model.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Integer> {

    // Custom method to find tracking details by order ID
    List<OrderTracking> findByOrderOrderId(int orderId);

    // Custom method to find tracking details by delivery person ID
    List<OrderTracking> findByDeliveryPersonDeliveryPersonId(int deliveryPersonId);

    // Custom method to find tracking details for a specific order and delivery person
    List<OrderTracking> findByOrderOrderIdAndDeliveryPersonDeliveryPersonId(int orderId, int deliveryPersonId);
}
