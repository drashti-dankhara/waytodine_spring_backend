package com.waytodine.waytodine.service;

import com.stripe.Stripe;
import com.waytodine.waytodine.model.Cart;
import com.waytodine.waytodine.model.Order;
import com.waytodine.waytodine.model.Restaurant;
import com.waytodine.waytodine.model.User;
import com.waytodine.waytodine.repository.CartRepository;
import com.waytodine.waytodine.repository.OrderRepository;
import com.waytodine.waytodine.repository.RestaurantRepository;
import com.waytodine.waytodine.repository.UserRepository;
import com.waytodine.waytodine.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.stripe.model.checkout.Session;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    public Order createOrder(Long userId, Long restaurantId, Long amount, Long discount,String pickupLocation,String dropoffLocation,String pickupCity,String dropoffCity) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setAmount(amount);
        order.setDiscount(discount);
        order.setPickupLocation(pickupLocation);
        order.setDropoffLocation(dropoffLocation);
        order.setPickupCity(pickupCity);
        order.setDropoffCity(dropoffCity);

        order = orderRepository.save(order); // Persist the order in the database



        return order;
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUser_UserId(userId);
    }

    public Order updateOrderStatus(Long orderId, int newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPaymentStatus(newStatus);

        order= orderRepository.save(order);

        List<Cart> cartItems = cartRepository.findByUser_UserIdAndRestaurant_RestaurantIdAndStatusAndOrderIsNull(order.getUser().getUserId(), order.getRestaurant().getRestaurantId(), 1);

        // Update the orderId for each cart item and change status to "ordered" (status = 2)
        for (Cart cartItem : cartItems) {
            cartItem.setOrder(order); // The order is now saved and can be set
            cartItem.setStatus(2); // Mark as ordered
            cartRepository.save(cartItem); // Save the updated cart item
        }

        return order;
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
