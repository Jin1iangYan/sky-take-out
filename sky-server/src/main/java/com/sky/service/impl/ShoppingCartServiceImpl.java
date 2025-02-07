package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO 购物车数据传输对象
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 1. 创建购物车对象并复制DTO属性，同时设置当前用户ID
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 2. 查询购物车中是否已有相同的商品（菜品或套餐）
        List<ShoppingCart> existingItems = shoppingCartMapper.selectShoppingCart(shoppingCart);
        if (existingItems != null && !existingItems.isEmpty()) {
            // 如果存在，直接将数量+1，并更新记录
            ShoppingCart existingCart = existingItems.get(0);
            existingCart.setNumber(existingCart.getNumber() + 1);
            shoppingCartMapper.updateShoppingCart(existingCart);
            return;
        }

        // 3. 如果购物车中不存在该商品，根据商品类型（菜品或套餐）设置相关信息
        if (shoppingCart.getDishId() != null) {
            // 添加的是菜品
            Dish dish = dishMapper.findById(shoppingCart.getDishId());
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
        } else if (shoppingCart.getSetmealId() != null) {
            // 添加的是套餐
            Setmeal setmeal = setmealMapper.findById(shoppingCart.getSetmealId());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());
        }

        // 4. 设置购物车项的数量、创建时间，并插入数据库
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartMapper.insert(shoppingCart);
    }

    /**
     * 查看购物车
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        return shoppingCartMapper.selectShoppingCart(shoppingCart);
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }

    /**
     * 减少购物车
     *
     * @param shoppingCartDTO 购物车数据传输对象
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 1. 构造查询条件：当前用户及菜品/套餐ID
        ShoppingCart queryCart = new ShoppingCart();
        queryCart.setUserId(BaseContext.getCurrentId());
        if (shoppingCartDTO.getDishId() != null) {
            queryCart.setDishId(shoppingCartDTO.getDishId());
        } else if (shoppingCartDTO.getSetmealId() != null) {
            queryCart.setSetmealId(shoppingCartDTO.getSetmealId());
        }

        // 2. 查询购物车中对应的记录
        List<ShoppingCart> cartList = shoppingCartMapper.selectShoppingCart(queryCart);
        if (cartList != null && !cartList.isEmpty()) {
            ShoppingCart existingCart = cartList.get(0);
            // 3. 数量减1
            int updatedNumber = existingCart.getNumber() - 1;
            if (updatedNumber > 0) {
                // 数量大于0时，更新数量
                existingCart.setNumber(updatedNumber);
                shoppingCartMapper.updateShoppingCart(existingCart);
            } else {
                // 数量减为0或以下时，从购物车中移除该记录
                shoppingCartMapper.deleteById(existingCart.getId());
            }
        } else {
            log.warn("尝试减少购物车中不存在的商品，shoppingCartDTO：{}", shoppingCartDTO);
        }
    }
}