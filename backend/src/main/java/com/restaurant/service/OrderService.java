package com.restaurant.service;

import com.restaurant.dto.CreateOrderRequest;
import com.restaurant.entity.Dish;
import com.restaurant.entity.Order;
import com.restaurant.entity.OrderItem;
import com.restaurant.mapper.DishMapper;
import com.restaurant.mapper.OrderItemMapper;
import com.restaurant.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final DishMapper dishMapper;
    private final UserService userService;

    public OrderService(OrderMapper orderMapper, OrderItemMapper orderItemMapper,
                       DishMapper dishMapper, UserService userService) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.dishMapper = dishMapper;
        this.userService = userService;
    }

    @Transactional
    public Order createOrder(Long userId, CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("订单不能为空");
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Dish dish = dishMapper.findById(itemReq.getDishId());
            if (dish == null) {
                throw new RuntimeException("菜品不存在: " + itemReq.getDishId());
            }
            if (!DishService.STATUS_ON_SHELF.equals(dish.getStatus())) {
                throw new RuntimeException("菜品已下架: " + dish.getDishName());
            }

            BigDecimal itemTotal = dish.getPrice().multiply(new BigDecimal(itemReq.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setDishId(dish.getId());
            orderItem.setDishName(dish.getDishName());
            orderItem.setPrice(dish.getPrice());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItems.add(orderItem);
        }

        userService.deductBalance(userId, totalPrice);

        String orderNo = generateOrderNo();
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setStatus("PENDING");

        orderMapper.insert(order);

        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
        }
        orderItemMapper.batchInsert(orderItems);

        return order;
    }

    public List<Order> getUserOrders(Long userId) {
        return orderMapper.findByUserId(userId);
    }

    public List<Order> getAllOrders() {
        return orderMapper.findAll();
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemMapper.findByOrderId(orderId);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!"PENDING".equals(status) && !"COMPLETED".equals(status)) {
            throw new RuntimeException("无效的订单状态");
        }

        orderMapper.updateStatus(orderId, status);
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 10000);
        return timestamp + String.format("%04d", random);
    }
}
