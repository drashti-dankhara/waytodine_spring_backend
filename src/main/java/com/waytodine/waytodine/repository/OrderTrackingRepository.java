package com.waytodine.waytodine.repository;

import com.waytodine.waytodine.model.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Integer> {

    @Query("SELECT ot FROM OrderTracking ot WHERE ot.order.orderId = :orderId AND ot.deliveryPerson.deliveryPersonId = :deliveryPersonId")
    List<OrderTracking> findByOrderAndDeliveryPerson(int orderId, int deliveryPersonId);

    @Query("SELECT ot FROM OrderTracking ot WHERE ot.order.orderId = :orderId")
    List<OrderTracking> findByOrder(int orderId);

    @Query("SELECT ot FROM OrderTracking ot WHERE ot.deliveryPerson.deliveryPersonId = :deliveryPersonId")
    List<OrderTracking> findByDeliveryPerson(int deliveryPersonId);
}

