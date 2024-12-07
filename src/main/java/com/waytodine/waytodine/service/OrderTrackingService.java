package com.waytodine.waytodine.service;

import com.waytodine.waytodine.model.OrderTracking;
import com.waytodine.waytodine.repository.OrderTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderTrackingService {

    @Autowired
    private OrderTrackingRepository orderTrackingRepository;


    public OrderTracking trackOrder(OrderTracking orderTracking) {
        return orderTrackingRepository.save(orderTracking);
    }

    public List<OrderTracking> getTrackingByOrderId(int orderId) {
        return orderTrackingRepository.findByOrderOrderId(orderId);
    }

    public List<OrderTracking> getTrackingByDeliveryPersonId(int deliveryPersonId) {
        return orderTrackingRepository.findByDeliveryPersonDeliveryPersonId(deliveryPersonId);
    }

    public List<OrderTracking> getTrackingByOrderAndDeliveryPerson(int orderId, int deliveryPersonId) {
        return orderTrackingRepository.findByOrderOrderIdAndDeliveryPersonDeliveryPersonId(orderId, deliveryPersonId);
    }
}
