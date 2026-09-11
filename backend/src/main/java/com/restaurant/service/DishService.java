package com.restaurant.service;

import com.restaurant.entity.Dish;
import com.restaurant.mapper.DishMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {

    public static final String STATUS_ON_SHELF = "ON_SHELF";
    public static final String STATUS_OFF_SHELF = "OFF_SHELF";

    private final DishMapper dishMapper;

    public DishService(DishMapper dishMapper) {
        this.dishMapper = dishMapper;
    }

    public List<Dish> getAllDishes() {
        return dishMapper.findAll();
    }

    public List<Dish> getOnShelfDishes() {
        return dishMapper.findOnShelf();
    }

    public Dish getDishById(Long id) {
        Dish dish = dishMapper.findById(id);
        if (dish == null) {
            throw new RuntimeException("菜品不存在");
        }
        return dish;
    }

    public Dish addDish(Dish dish) {
        dish.setStatus(STATUS_ON_SHELF);
        dishMapper.insert(dish);
        return dish;
    }

    public Dish updateDish(Long id, Dish dish) {
        Dish existingDish = dishMapper.findById(id);
        if (existingDish == null) {
            throw new RuntimeException("菜品不存在");
        }

        dish.setId(id);
        dishMapper.update(dish);
        return dish;
    }

    public Dish updateDishStatus(Long id, String status) {
        if (!STATUS_ON_SHELF.equals(status) && !STATUS_OFF_SHELF.equals(status)) {
            throw new RuntimeException("无效的菜品状态");
        }

        Dish dish = dishMapper.findById(id);
        if (dish == null) {
            throw new RuntimeException("菜品不存在");
        }

        dishMapper.updateStatus(id, status);
        dish.setStatus(status);
        return dish;
    }

    public void deleteDish(Long id) {
        Dish dish = dishMapper.findById(id);
        if (dish == null) {
            throw new RuntimeException("菜品不存在");
        }
        dishMapper.deleteById(id);
    }
}
