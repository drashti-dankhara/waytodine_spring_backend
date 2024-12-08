package com.waytodine.waytodine.controller;

import com.waytodine.waytodine.dto.CartItemResponseDTO;
import com.waytodine.waytodine.dto.OrderedItemsResponse;
import com.waytodine.waytodine.service.CartService;
import com.waytodine.waytodine.util.ApiResponse;
import com.waytodine.waytodine.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtil jwtUtil;

    private Long extractUserIdFromToken(String token) {
        try {
            return jwtUtil.extractUserId(token);
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<ApiResponse> addToCart(@RequestHeader("Authorization") String token,
                                                 @RequestBody Map<String, Object> requestBody) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse("Invalid or missing token.", null, false));
        }

        try {
            Long restaurantId = ((Number) requestBody.get("restaurantId")).longValue();
            Long itemId = ((Number) requestBody.get("itemId")).longValue();
            Integer quantity = (Integer) requestBody.get("quantity");

            ApiResponse response = cartService.addToCart(userId, restaurantId, itemId, quantity);
            return response.isStatus() ? ResponseEntity.ok(response) : ResponseEntity.status(409).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse("An error occurred while adding to the cart.", null, false));
        }
    }

    @PutMapping("/update-cart")
    public ResponseEntity<ApiResponse> updateCart(@RequestHeader("Authorization") String token,
                                                  @RequestBody Map<String, Object> requestBody) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse("Invalid or missing token.", null, false));
        }

        try {
            Long cartId = ((Number) requestBody.get("cartId")).longValue();
            Integer quantity = (Integer) requestBody.get("quantity");

            if (cartService.updateCart(cartId, quantity, userId) != null) {
                return ResponseEntity.ok(new ApiResponse("Cart updated successfully.", null, true));
            } else {
                return ResponseEntity.status(404)
                        .body(new ApiResponse("Cart item not found or you are not authorized.", null, false));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse("An error occurred while updating the cart.", null, false));
        }
    }

    @GetMapping("/get-cart-by-user")
    public ResponseEntity<ApiResponse> getCartItemsByUser(@RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse("Invalid or missing token.", null, false));
        }

        try {
            List<CartItemResponseDTO> cartItems = cartService.getCartItemsByUserId(userId);
            if (cartItems.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse("No items found in the cart.", cartItems, true));
            }
            return ResponseEntity.ok(new ApiResponse("Cart items retrieved successfully.", cartItems, true));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse("An error occurred while fetching cart items.", null, false));
        }
    }

    @DeleteMapping("/remove-cart-item/{cartId}")
    public ResponseEntity<ApiResponse> removeCartItem(@RequestHeader("Authorization") String token,
                                                      @PathVariable Long cartId) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse("Invalid or missing token.", null, false));
        }

        try {
            boolean isRemoved = cartService.removeCartItem(cartId, userId);
            if (!isRemoved) {
                return ResponseEntity.status(404)
                        .body(new ApiResponse("Item not found or not authorized to remove.", null, false));
            }
            return ResponseEntity.ok(new ApiResponse("Cart item removed successfully.", null, true));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse("An error occurred while removing the cart item.", null, false));
        }
    }

    @DeleteMapping("/remove-all-cart-item")
    public ResponseEntity<ApiResponse> removeAllCartItem(@RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse("Invalid or missing token.", null, false));
        }

        try {
            boolean isRemoved = cartService.removeAllCartItems(userId);
            if (!isRemoved) {
                return ResponseEntity.status(404)
                        .body(new ApiResponse("No items found or not authorized to remove items.", null, false));
            }
            return ResponseEntity.ok(new ApiResponse("All cart items removed successfully.", null, true));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse("An error occurred while removing all cart items.", null, false));
        }
    }

    @GetMapping("/get-delivered-carts")
    public ResponseEntity<ApiResponse> getDeliveredCarts(@RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse("Invalid or missing token.", null, false));
        }

        try {
            List<OrderedItemsResponse> cartItems = cartService.getDeliveredCartsByUserId(userId);
            if (cartItems.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse("No delivered items found.", cartItems, true));
            }
            return ResponseEntity.ok(new ApiResponse("Delivered items retrieved successfully.", cartItems, true));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse("An error occurred while fetching delivered carts.", null, false));
        }
    }

    @GetMapping("/get-current-orders")
    public ResponseEntity<ApiResponse> getCurrentOrderes(@RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse("Invalid or missing token.", null, false));
        }

        try {
            List<OrderedItemsResponse> cartItems = cartService.getCurrentOrderesByUserId(userId);
            if (cartItems.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse("No current ordered items found.", cartItems, true));
            }
            return ResponseEntity.ok(new ApiResponse("Current Ordered items retrieved successfully.", cartItems, true));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse("An error occurred while fetching Current Ordered items.", null, false));
        }
    }
}
