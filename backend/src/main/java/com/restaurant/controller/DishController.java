package com.restaurant.controller;

import com.restaurant.dto.DishRequest;
import com.restaurant.dto.Result;
import com.restaurant.entity.Dish;
import com.restaurant.service.DishService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping("/list")
    public Result<List<Dish>> getOnShelfDishes() {
        try {
            List<Dish> dishes = dishService.getOnShelfDishes();
            return Result.success(dishes);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/all")
    public Result<List<Dish>> getAllDishes() {
        try {
            List<Dish> dishes = dishService.getAllDishes();
            return Result.success(dishes);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Dish> getDishById(@PathVariable Long id) {
        try {
            Dish dish = dishService.getDishById(id);
            return Result.success(dish);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result<Dish> addDish(@Valid @RequestBody DishRequest request) {
        try {
            Dish dish = new Dish();
            dish.setDishName(request.getDishName());
            dish.setPrice(request.getPrice());
            dish.setDescription(request.getDescription());

            Dish savedDish = dishService.addDish(dish);
            return Result.success(savedDish);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Dish> updateDish(@PathVariable Long id, @Valid @RequestBody DishRequest request) {
        try {
            Dish dish = new Dish();
            dish.setDishName(request.getDishName());
            dish.setPrice(request.getPrice());
            dish.setDescription(request.getDescription());

            Dish updatedDish = dishService.updateDish(id, dish);
            return Result.success(updatedDish);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public Result<Dish> updateDishStatus(@PathVariable Long id, @RequestBody Map<String, String> params,
                                         HttpServletRequest request) {
        try {
            String role = (String) request.getAttribute("role");
            if (!"ADMIN".equals(role)) {
                return Result.error(403, "无权限");
            }

            Dish dish = dishService.updateDishStatus(id, params.get("status"));
            return Result.success(dish);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteDish(@PathVariable Long id) {
        try {
            dishService.deleteDish(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
