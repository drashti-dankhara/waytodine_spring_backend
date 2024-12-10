package com.waytodine.waytodine.config;

import com.waytodine.waytodine.websocket.VehicleTrackingWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final VehicleTrackingWebSocketHandler vehicleTrackingWebSocketHandler;

    public WebSocketConfig(VehicleTrackingWebSocketHandler vehicleTrackingWebSocketHandler) {
        this.vehicleTrackingWebSocketHandler = vehicleTrackingWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(vehicleTrackingWebSocketHandler, "/ws/track")
                .setAllowedOrigins("*"); // Replace "*" with your allowed origin for production
    }


}
