package com.restaurant.controller;

import com.restaurant.dto.CreateOrderRequest;
import com.restaurant.dto.Result;
import com.restaurant.entity.Order;
import com.restaurant.entity.OrderItem;
import com.restaurant.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public Result<?> createOrder(HttpServletRequest request, @Valid @RequestBody CreateOrderRequest orderRequest) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Order order = orderService.createOrder(userId, orderRequest);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<?> getMyOrders(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            List<Order> orders = orderService.getUserOrders(userId);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/all")
    public Result<?> getAllOrders(HttpServletRequest request) {
        try {
            String role = (String) request.getAttribute("role");
            if (!"ADMIN".equals(role)) {
                return Result.error(403, "无权限");
            }

            List<Order> orders = orderService.getAllOrders();
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{orderId}/items")
    public Result<?> getOrderItems(@PathVariable Long orderId) {
        try {
            List<OrderItem> items = orderService.getOrderItems(orderId);
            return Result.success(items);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/status")
    public Result<?> updateOrderStatus(HttpServletRequest request, @PathVariable Long orderId, @RequestBody Map<String, String> params) {
        try {
            String role = (String) request.getAttribute("role");
            if (!"ADMIN".equals(role)) {
                return Result.error(403, "无权限");
            }

            String status = params.get("status");
            if (status == null) {
                return Result.error("状态不能为空");
            }

            orderService.updateOrderStatus(orderId, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
