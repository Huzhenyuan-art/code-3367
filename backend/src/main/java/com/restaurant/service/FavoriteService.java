package com.restaurant.service;

import com.restaurant.dto.FavoriteVO;
import com.restaurant.entity.Dish;
import com.restaurant.entity.Favorite;
import com.restaurant.mapper.DishMapper;
import com.restaurant.mapper.FavoriteMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final DishMapper dishMapper;

    public FavoriteService(FavoriteMapper favoriteMapper, DishMapper dishMapper) {
        this.favoriteMapper = favoriteMapper;
        this.dishMapper = dishMapper;
    }

    public void addFavorite(Long userId, Long dishId) {
        Dish dish = dishMapper.findById(dishId);
        if (dish == null) {
            throw new RuntimeException("菜品不存在");
        }

        if (favoriteMapper.findByUserIdAndDishId(userId, dishId) != null) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setDishId(dishId);
        favoriteMapper.insert(favorite);
    }

    public void removeFavorite(Long userId, Long dishId) {
        favoriteMapper.deleteByUserIdAndDishId(userId, dishId);
    }

    public List<Long> getFavoriteDishIds(Long userId) {
        return favoriteMapper.findDishIdsByUserId(userId);
    }

    public Map<String, Object> getFavoritePage(Long userId, int page, int size) {
        if (page < 1) {
            page = 1;
        }
        if (size < 1 || size > 100) {
            size = 10;
        }

        int offset = (page - 1) * size;
        List<FavoriteVO> list = favoriteMapper.findPageByUserId(userId, offset, size);
        int total = favoriteMapper.countByUserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("list", list);
        return result;
    }
}
