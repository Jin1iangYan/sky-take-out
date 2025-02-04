package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    private final DishMapper dishMapper;
    private final DishFlavorMapper dishFlavorMapper;
    private final SetmealDishMapper setmealDishMapper;
    private final CategoryMapper categoryMapper;

    public DishServiceImpl(DishMapper dishMapper, DishFlavorMapper dishFlavorMapper, SetmealDishMapper setmealDishMapper, CategoryMapper categoryMapper) {
        this.dishMapper = dishMapper;
        this.dishFlavorMapper = dishFlavorMapper;
        this.setmealDishMapper = setmealDishMapper;
        this.categoryMapper = categoryMapper;
    }

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDTO, dish);

        // 向菜品表插入1条数据
        dishMapper.insert(dish);

        // 向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> flavor.setDishId(dish.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页/分类查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = (Page<DishVO>) dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDishesByIds(ArrayList<Long> ids) {
        // 批量查询是否存在起售状态的菜品
        List<Dish> onSaleDishes = dishMapper.findDishesByIdsAndStatus(ids, StatusConstant.ENABLE);
        if (onSaleDishes != null && !onSaleDishes.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }

        // 检查菜品是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品
        dishMapper.deleteDishesByIds(ids);

        // 删除菜品后，删除关联的口味数据
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据ID查询菜品
     * @param id 菜品id
     * @return 菜品VO
     */
    @Override
    public DishVO findByIdWithFlavor(Long id) {
        Dish dish = dishMapper.findById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);

        // 获取口味列表，放在vo
        List<DishFlavor> flavors = dishFlavorMapper.findByDishId(id);
        // 获取分类名称
        Category category = categoryMapper.findById(dish.getCategoryId());

        dishVO.setFlavors(flavors);
        dishVO.setCategoryName(category.getName());
        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO 菜品DTO
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 更新菜品信息
        dishMapper.update(dish);

        // 删除原有的口味数据
        dishFlavorMapper.deleteByDishIds(List.of(dish.getId()));

        // 插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();;
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> flavor.setDishId(dish.getId()));
        }
        dishFlavorMapper.insertBatch(flavors);
    }

    /**
     * 根据分类查询菜品
     *
     * @param categoryId 分类id
     * @return
     */
    @Override
    public List<Dish> findByCategoryId(Long categoryId) {
        List<Dish> dishes = dishMapper.selectByCategoryId(categoryId);
        return dishes;
    }
}
