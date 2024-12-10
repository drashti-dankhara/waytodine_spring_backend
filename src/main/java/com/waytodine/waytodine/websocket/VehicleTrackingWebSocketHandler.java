package com.waytodine.waytodine.websocket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.waytodine.waytodine.model.DeliveryPerson;
import com.waytodine.waytodine.model.Order;
import com.waytodine.waytodine.model.OrderTracking;
import com.waytodine.waytodine.service.OrderTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class VehicleTrackingWebSocketHandler extends TextWebSocketHandler {

    // Maintain all connected sessions
    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    // Maintain sessions specific to order IDs
    private final ConcurrentHashMap<Integer, WebSocketSession> orderSessions = new ConcurrentHashMap<>();

    @Autowired
    private OrderTrackingService orderTrackingService;

    private final Gson gson = new Gson();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        if (attributes.containsKey("orderId")) {
            int orderId = (int) attributes.get("orderId");
            orderSessions.put(orderId, session);
        }
        sessions.add(session);
        System.out.println("WebSocket connection established: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        orderSessions.values().remove(session);
        sessions.remove(session);
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received WebSocket message: " + message.getPayload());
        try {
            // Parse the JSON message using Gson
            Map<String, String> messageData = gson.fromJson(message.getPayload(), new TypeToken<Map<String, String>>() {}.getType());

            // Extract data
            int orderId = Integer.parseInt(messageData.get("orderId"));
            int deliveryPersonId = Integer.parseInt(messageData.get("deliveryPersonId"));
            String location = messageData.get("location");

            // Add or Update the tracking record
            OrderTracking tracking = orderTrackingService.addOrUpdateTracking(orderId, deliveryPersonId, location);
            System.out.println("Tracking data added/updated: " + tracking);

            // Prepare the message to be broadcasted
            String updatedLocationMessage = gson.toJson(Map.of(
                    "orderId", orderId,
                    "deliveryPersonId", deliveryPersonId,
                    "location", location
            ));

            // Broadcast to all clients
            broadcastToAll(updatedLocationMessage);

            // Optionally broadcast only to clients tracking this specific order
            broadcastToOrder(orderId, updatedLocationMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Broadcast a message to all connected WebSocket clients.
     *
     * @param message The message to be broadcasted.
     */
    public void broadcastToAll(String message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Send a message to a specific WebSocket client tracking a specific order.
     *
     * @param orderId The ID of the order to which the message is sent.
     * @param message The message to be sent.
     * @throws Exception If the WebSocket session is closed or encounters an error.
     */
    private void broadcastToOrder(int orderId, String message) throws Exception {
        WebSocketSession session = orderSessions.get(orderId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }
}
