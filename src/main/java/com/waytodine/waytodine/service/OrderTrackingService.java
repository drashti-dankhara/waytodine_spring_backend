package com.waytodine.waytodine.service;

import com.waytodine.waytodine.model.DeliveryPerson;
import com.waytodine.waytodine.model.Order;
import com.waytodine.waytodine.model.OrderTracking;
import com.waytodine.waytodine.repository.OrderTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderTrackingService {

    @Autowired
    private OrderTrackingRepository orderTrackingRepository;

    public OrderTracking addOrUpdateTracking(int orderId, int deliveryPersonId, String location) {
        // Check if tracking data exists for the order and delivery person
        List<OrderTracking> existingTrackings = orderTrackingRepository.findByOrderAndDeliveryPerson(orderId, deliveryPersonId);

        OrderTracking orderTracking;
        if (!existingTrackings.isEmpty()) {
            // Update existing tracking record
            orderTracking = existingTrackings.get(0); // Assuming only one record exists
            orderTracking.setLocation(location);
        } else {
            // Create a new tracking record
            orderTracking = new OrderTracking();

            // Initialize and set Order object
            Order order = new Order();
            order.setOrderId((long) orderId); // Set order ID
            orderTracking.setOrder(order);

            // Initialize and set DeliveryPerson object
            DeliveryPerson deliveryPerson = new DeliveryPerson();
            deliveryPerson.setDeliveryPersonId((long) deliveryPersonId); // Set delivery person ID
            orderTracking.setDeliveryPerson(deliveryPerson);

            // Set location
            orderTracking.setLocation(location);
        }

        // Save the tracking data
        return orderTrackingRepository.save(orderTracking);
    }

    public List<OrderTracking> getTrackingByOrderAndDeliveryPerson(int orderId, int deliveryPersonId) {
        return orderTrackingRepository.findByOrderAndDeliveryPerson(orderId, deliveryPersonId);
    }

    public List<OrderTracking> getTrackingByOrder(int orderId) {
        return orderTrackingRepository.findByOrder(orderId);
    }

    public List<OrderTracking> getTrackingByDeliveryPerson(int deliveryPersonId) {
        return orderTrackingRepository.findByDeliveryPerson(deliveryPersonId);
    }

    public void trackOrder(OrderTracking orderTracking) {
        orderTrackingRepository.save(orderTracking);
    }
}