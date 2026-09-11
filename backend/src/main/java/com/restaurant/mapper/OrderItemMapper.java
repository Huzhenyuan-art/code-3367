package com.restaurant.mapper;

import com.restaurant.entity.OrderItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OrderItemMapper {

    @Insert("INSERT INTO order_items (order_id, dish_id, dish_name, price, quantity) VALUES (#{orderId}, #{dishId}, #{dishName}, #{price}, #{quantity})")
    int insert(OrderItem orderItem);

    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItem> findByOrderId(Long orderId);

    int batchInsert(List<OrderItem> items);
}
