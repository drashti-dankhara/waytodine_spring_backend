package com.waytodine.waytodine.service;

import com.waytodine.waytodine.dto.CartItemResponseDTO;
import com.waytodine.waytodine.model.Cart;
import com.waytodine.waytodine.model.MenuItem;
import com.waytodine.waytodine.model.Restaurant;
import com.waytodine.waytodine.model.User;
import com.waytodine.waytodine.repository.CartRepository;
import com.waytodine.waytodine.repository.MenuItemRepository;
import com.waytodine.waytodine.repository.RestaurantRepository;
import com.waytodine.waytodine.repository.UserRepository;
import com.waytodine.waytodine.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    public ApiResponse addToCart(Long userId, Long restaurantId, Long itemId, Integer quantity) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        Optional<Cart> existingCartItem = cartRepository.findByUser_UserIdAndMenuItem_ItemIdAndStatus(userId, itemId, 1);

        if (existingCartItem.isPresent()) {
            return new ApiResponse("Item already added to cart.", null, false);
        }

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setRestaurant(restaurant);
        cart.setMenuItem(menuItem);
        cart.setQuantity(quantity);
        cart.setTotalPrice(menuItem.getPrice() * quantity);

        Cart savedCart = cartRepository.save(cart);

        return new ApiResponse("Item added to cart successfully.", null, true);
    }

    public Cart updateCart(Long cartId, Integer quantity, Long userId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cart.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("User is not authorized to update this cart item.");
        }

        cart.setQuantity(quantity);
        cart.setTotalPrice(cart.getMenuItem().getPrice() * quantity);

        return cartRepository.save(cart);
    }

    public Cart updateCartStatus(Long cartId, Long userId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cart.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("User is not authorized to update this cart item.");
        }

        cart.setStatus(2);

        return cartRepository.save(cart);
    }

    public List<CartItemResponseDTO> getCartItemsByUserId(Long userId) {
        List<Cart> cartItems = cartRepository.findByUser_UserIdAndStatus(userId, 1);
        List<CartItemResponseDTO> responseDTOs = new ArrayList<>();

        for (Cart cart : cartItems) {
            MenuItem menuItem = menuItemRepository.findById(cart.getMenuItem().getItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));
            responseDTOs.add(new CartItemResponseDTO(
                    cart.getCartId(),
                    cart.getUser().getUserId(),
                    cart.getRestaurant().getRestaurantId(),
                    cart.getMenuItem().getItemId(),
                    cart.getQuantity(),
                    menuItem.getName(),
                    menuItem.getPrice(),
                    menuItem.getDescription(),
                    cart.getTotalPrice(),
                    menuItem.getItemImage(),
                    cart.getRestaurant().getRestaurantDetails().getCurrentOfferDiscountRate()
            ));
        }
        return responseDTOs;
    }

    public boolean removeCartItem(Long cartId, Long userId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cart.getUser().getUserId().equals(userId)) {
            return false;
        }

        cartRepository.delete(cart);
        return true;
    }

    public boolean removeAllCartItems(Long userId) {
        List<Cart> userCartItems = cartRepository.findByUser_UserIdAndStatus(userId, 1);
        if (!userCartItems.isEmpty()) {
            cartRepository.deleteAll(userCartItems);
            return true;
        }
        return false;
    }
}