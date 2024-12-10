package com.waytodine.waytodine.controller;

import com.waytodine.waytodine.model.OrderTracking;
import com.waytodine.waytodine.service.OrderTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
public class OrderTrackingController {

    @Autowired
    private OrderTrackingService orderTrackingService;

    /**
     * Get tracking details for a specific order and delivery person.
     *
     * @param orderId          the ID of the order
     * @param deliveryPersonId the ID of the delivery person
     * @return tracking details
     */
    @GetMapping("/order/{orderId}/delivery-person/{deliveryPersonId}")
    public ResponseEntity<List<OrderTracking>> getTrackingDetails(
            @PathVariable int orderId,
            @PathVariable int deliveryPersonId) {
        List<OrderTracking> trackingDetails = orderTrackingService.getTrackingByOrderAndDeliveryPerson(orderId, deliveryPersonId);
        return ResponseEntity.ok(trackingDetails);
    }

    /**
     * Get all tracking records for a specific order.
     *
     * @param orderId the ID of the order
     * @return list of tracking records
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderTracking>> getTrackingForOrder(@PathVariable int orderId) {
        List<OrderTracking> trackingRecords = orderTrackingService.getTrackingByOrder(orderId);
        return ResponseEntity.ok(trackingRecords);
    }

    /**
     * Get all tracking records for a specific delivery person.
     *
     * @param deliveryPersonId the ID of the delivery person
     * @return list of tracking records
     */
    @GetMapping("/delivery-person/{deliveryPersonId}")
    public ResponseEntity<List<OrderTracking>> getTrackingForDeliveryPerson(@PathVariable int deliveryPersonId) {
        List<OrderTracking> trackingRecords = orderTrackingService.getTrackingByDeliveryPerson(deliveryPersonId);
        return ResponseEntity.ok(trackingRecords);
    }
}
