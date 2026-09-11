package com.restaurant.controller;

import com.restaurant.dto.Result;
import com.restaurant.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{dishId}")
    public Result<?> addFavorite(@PathVariable Long dishId, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            favoriteService.addFavorite(userId, dishId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{dishId}")
    public Result<?> removeFavorite(@PathVariable Long dishId, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            favoriteService.removeFavorite(userId, dishId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/ids")
    public Result<List<Long>> getFavoriteDishIds(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return Result.success(favoriteService.getFavoriteDishIds(userId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<Map<String, Object>> getMyFavorites(HttpServletRequest request,
                                                      @RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return Result.success(favoriteService.getFavoritePage(userId, page, size));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
