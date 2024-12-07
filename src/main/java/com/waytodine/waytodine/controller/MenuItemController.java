package com.waytodine.waytodine.controller;

import com.waytodine.waytodine.model.MenuItem;
import com.waytodine.waytodine.service.MenuItemService;
import com.waytodine.waytodine.util.ApiResponse;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @GetMapping("/get-all-items")
    public ApiResponse getAllActiveMenuItems() {
        List<MenuItem> activeMenuItems = menuItemService.getAllActiveMenuItems(null);
        if (activeMenuItems.isEmpty()) {
            return new ApiResponse("No active menu items found", null, false);
        }
        return new ApiResponse("Menu items retrieved successfully", activeMenuItems, true);
    }

    @GetMapping("/get-all-items-by-name/{name}")
    public ApiResponse getAllActiveMenuItemsByName(@PathVariable("name") String name) {
        List<MenuItem> activeMenuItems = menuItemService.getAllActiveMenuItems(name);
        if (activeMenuItems.isEmpty()) {
            return new ApiResponse("No active menu items found", null, false);
        }
        return new ApiResponse("Menu items retrieved successfully", activeMenuItems, true);
    }

    @GetMapping("/get-items-by-category/{categoryId}")
    public ApiResponse getMenuItemsByCategory(@PathVariable Long categoryId) {
        List<MenuItem> itemsByCategory = menuItemService.getMenuItemsByCategory(categoryId);
        if (itemsByCategory.isEmpty()) {
            return new ApiResponse("No menu items found for the given category", null, false);
        }
        return new ApiResponse("Menu items for the given category retrieved successfully", itemsByCategory, true);
    }

    @GetMapping("/get-items-restaurant/{restaurantId}")
    public ApiResponse getMenuItemsByRestaurant(@PathVariable Long restaurantId) {
        List<MenuItem> itemsByRestaurant = menuItemService.getMenuItemsByRestaurant(restaurantId);
        if (itemsByRestaurant.isEmpty()) {
            return new ApiResponse("No menu items found for the given restaurant", null, false);
        }
        return new ApiResponse("Menu items for the given restaurant retrieved successfully", itemsByRestaurant, true);
    }

    @GetMapping("/get-items-restaurant-category/{restaurantId}/{categoryId}")
    public ApiResponse getMenuItemsByRestaurantAndCategory(@PathVariable Long restaurantId,@PathVariable Long categoryId) {
        List<MenuItem> itemsByRestaurant = menuItemService.getMenuItemsByRestaurantAndCategory(restaurantId,categoryId);
        if (itemsByRestaurant.isEmpty()) {
            return new ApiResponse("No menu items found for the given restaurant and category", null, false);
        }
        return new ApiResponse("Menu items for the given restaurant and category retrieved successfully", itemsByRestaurant, true);
    }

//    @GetMapping("/get-items-sorted-filtered")
//    public ApiResponse getMenuItemsWithSortingAndFiltering(@RequestParam(value = "sort", required = false) Integer sort,
//                                                           @RequestParam(value = "filter", required = false) Integer filter) {
//        List<MenuItem> sortedAndFilteredItems = menuItemService.getMenuItemsWithSortingAndFiltering(sort, filter);
//        if (sortedAndFilteredItems.isEmpty()) {
//            return new ApiResponse("No menu items found with the specified sorting and filtering", null, false);
//        }
//        return new ApiResponse("Menu items retrieved successfully with specified sorting and filtering", sortedAndFilteredItems, true);
//    }

    @GetMapping("/get-items-sorted-filtered")
    public ApiResponse getMenuItems(@RequestParam(value = "sort", required = false) Integer sort,
                                    @RequestParam(value = "filter", required = false) Integer filter,
                                    @RequestParam(value = "searchString", required = false) String searchString) {
        List<MenuItem> menuItems = menuItemService.getMenuItemsWithSortingAndFiltering(sort, filter, searchString);

        if (menuItems.isEmpty()) {
            return new ApiResponse("No menu items found with the specified criteria", null, false);
        }

        return new ApiResponse("Menu items retrieved successfully with specified criteria", menuItems, true);
    }

}
