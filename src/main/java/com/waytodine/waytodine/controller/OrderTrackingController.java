package com.waytodine.waytodine.controller;

import com.waytodine.waytodine.model.Order;
import com.waytodine.waytodine.model.OrderTracking;
import com.waytodine.waytodine.service.OrderTrackingService;
import com.waytodine.waytodine.websocket.VehicleTrackingWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicle-tracking")
public class OrderTrackingController {

    @Autowired
    private OrderTrackingService orderTrackingService;

    @Autowired
    private VehicleTrackingWebSocketHandler webSocketHandler;

    @PostMapping("/track")
    public OrderTracking trackVehicle(@RequestBody OrderTracking vehicleTracking) {
        OrderTracking savedTracking = orderTrackingService.trackOrder(vehicleTracking);
        String locationMessage = "Driver " + vehicleTracking.getDeliveryPerson().getDeliveryPersonId() + " is at location: " + vehicleTracking.getLocation();
        webSocketHandler.sendMessageToAll(locationMessage);
        return savedTracking;
    }
}
