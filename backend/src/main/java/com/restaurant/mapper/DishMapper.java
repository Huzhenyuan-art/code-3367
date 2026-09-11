package com.restaurant.mapper;

import com.restaurant.entity.Dish;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DishMapper {

    @Select("SELECT * FROM dishes ORDER BY created_at DESC")
    List<Dish> findAll();

    @Select("SELECT * FROM dishes WHERE status = 'ON_SHELF' ORDER BY created_at DESC")
    List<Dish> findOnShelf();

    @Select("SELECT * FROM dishes WHERE id = #{id}")
    Dish findById(Long id);

    @Insert("INSERT INTO dishes (dish_name, price, description, status) VALUES (#{dishName}, #{price}, #{description}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Dish dish);

    @Update("UPDATE dishes SET dish_name = #{dishName}, price = #{price}, description = #{description} WHERE id = #{id}")
    int update(Dish dish);

    @Update("UPDATE dishes SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Delete("DELETE FROM dishes WHERE id = #{id}")
    int deleteById(Long id);
}
