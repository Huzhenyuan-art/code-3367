package com.restaurant.mapper;

import com.restaurant.entity.Dish;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DishMapper {

    @Select("SELECT * FROM dishes ORDER BY created_at DESC")
    List<Dish> findAll();

    @Select("SELECT * FROM dishes WHERE id = #{id}")
    Dish findById(Long id);

    @Insert("INSERT INTO dishes (dish_name, price, description) VALUES (#{dishName}, #{price}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Dish dish);

    @Update("UPDATE dishes SET dish_name = #{dishName}, price = #{price}, description = #{description} WHERE id = #{id}")
    int update(Dish dish);

    @Delete("DELETE FROM dishes WHERE id = #{id}")
    int deleteById(Long id);
}
