package com.restaurant.service;

import com.restaurant.entity.Dish;
import com.restaurant.mapper.DishMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {

    private final DishMapper dishMapper;

    public DishService(DishMapper dishMapper) {
        this.dishMapper = dishMapper;
    }

    public List<Dish> getAllDishes() {
        return dishMapper.findAll();
    }

    public Dish getDishById(Long id) {
        Dish dish = dishMapper.findById(id);
        if (dish == null) {
            throw new RuntimeException("菜品不存在");
        }
        return dish;
    }

    public Dish addDish(Dish dish) {
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

    public void deleteDish(Long id) {
        Dish dish = dishMapper.findById(id);
        if (dish == null) {
            throw new RuntimeException("菜品不存在");
        }
        dishMapper.deleteById(id);
    }
}
