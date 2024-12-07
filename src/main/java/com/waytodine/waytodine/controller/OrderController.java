package com.waytodine.waytodine.controller;

import com.waytodine.waytodine.dto.PaymentResponse;
import com.waytodine.waytodine.model.Cart;
import com.waytodine.waytodine.model.Order;
import com.waytodine.waytodine.service.CartService;
import com.waytodine.waytodine.service.OrderService;
import com.waytodine.waytodine.service.PaymentService;
import com.waytodine.waytodine.util.ApiResponse;
import com.waytodine.waytodine.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestHeader("Authorization") String token,
                                                       @RequestBody Map<String, Object> requestBody) {
        try {
            // Extract user ID from the token
            Long userId = jwtUtil.extractUserId(token);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse("Invalid or missing token.", null, false));
            }

            // Extract order details from the request body
            Long restaurantId = ((Number) requestBody.get("restaurantId")).longValue();
            Long amount = ((Number) requestBody.get("amount")).longValue();
            Long discount = ((Number) requestBody.get("discount")).longValue();
            String pickupLocation = ((String)requestBody.get("pickupLocation"));
            String dropoffLocation = ((String)requestBody.get("dropoffLocation"));
            String pickupCity = ((String)requestBody.get("pickupCity"));
            String dropoffCity = ((String)requestBody.get("dropoffCity"));

            // Call service to create the order
            Order order = orderService.createOrder(userId, restaurantId, amount, discount,pickupLocation,dropoffLocation,pickupCity,dropoffCity);

            PaymentResponse res = paymentService.createPaymentLink(order);
            return ResponseEntity.ok(new ApiResponse("Order added successfully.", res, true));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An error occurred while creating the order.", null, false));
        }
    }

    @GetMapping("/get-order-by-id/{orderId}")
    public ResponseEntity<ApiResponse> getOrderById(@RequestHeader("Authorization") String token,
                                                    @PathVariable Long orderId) {
        try {
            Long userId = jwtUtil.extractUserId(token);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse("Invalid or missing token.", null, false));
            }

            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Order not found.", null, false));
            }

            // Check if the user requesting the order is authorized to view it
            if (!order.getUser().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse("You are not authorized to view this order.", null, false));
            }

            return ResponseEntity.ok(new ApiResponse("Order retrieved successfully.", order, true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An error occurred while fetching the order.", null, false));
        }
    }


    @GetMapping("/get-orders-by-user")
    public ResponseEntity<ApiResponse> getOrdersByUser(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse("Invalid or missing token.", null, false));
            }

            List<Order> orders = orderService.getOrdersByUserId(userId);
            if (orders.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse("No orders found.", orders, true));
            }

            return ResponseEntity.ok(new ApiResponse("Orders retrieved successfully.", orders, true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An error occurred while fetching orders.", null, false));
        }
    }

    @PostMapping("/update-order-status/{orderId}")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId,
            @RequestParam int newStatus) {
        try {
            // Extract user ID from the token
            Long userId = jwtUtil.extractUserId(token);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse("Invalid or missing token.", null, false));
            }

            // Call the service to update the order status
            Order updatedOrder = orderService.updateOrderStatus(orderId, newStatus);

            return ResponseEntity.ok(new ApiResponse("Order status updated successfully.", updatedOrder, true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An error occurred while updating the order status.", null, false));
        }
    }

//    @GetMapping("/check-payment-status/{sessionId}")
//    public ResponseEntity<String> checkPaymentStatus(@PathVariable String sessionId) {
//        try {
//            orderService.updatePaymentStatus(sessionId);
//            return ResponseEntity.ok("Payment status updated successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//    }

}
