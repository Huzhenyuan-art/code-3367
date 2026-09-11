package com.restaurant.mapper;

import com.restaurant.dto.FavoriteVO;
import com.restaurant.entity.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    @Select("SELECT * FROM favorites WHERE user_id = #{userId} AND dish_id = #{dishId}")
    Favorite findByUserIdAndDishId(@Param("userId") Long userId, @Param("dishId") Long dishId);

    @Insert("INSERT INTO favorites (user_id, dish_id) VALUES (#{userId}, #{dishId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Favorite favorite);

    @Delete("DELETE FROM favorites WHERE user_id = #{userId} AND dish_id = #{dishId}")
    int deleteByUserIdAndDishId(@Param("userId") Long userId, @Param("dishId") Long dishId);

    @Select("SELECT dish_id FROM favorites WHERE user_id = #{userId}")
    List<Long> findDishIdsByUserId(Long userId);

    @Select("SELECT f.id AS favorite_id, f.dish_id, d.dish_name, d.price, d.description, d.status, f.created_at " +
            "FROM favorites f JOIN dishes d ON f.dish_id = d.id " +
            "WHERE f.user_id = #{userId} ORDER BY f.created_at DESC LIMIT #{offset}, #{size}")
    List<FavoriteVO> findPageByUserId(@Param("userId") Long userId, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM favorites WHERE user_id = #{userId}")
    int countByUserId(Long userId);
}
