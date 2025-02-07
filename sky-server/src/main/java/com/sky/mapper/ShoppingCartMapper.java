package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 动态条件查询
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> selectShoppingCart(ShoppingCart shoppingCart);

    /**
     * 修改购物车
     * @param shoppingCart
     */
    void updateShoppingCart(ShoppingCart shoppingCart);

    void insert(ShoppingCart shoppingCart);

    /**
     * 根据用户id删除购物车数据
     * @param userId
     */
    void deleteByUserId(Long userId);

    void deleteById(Long id);
}
