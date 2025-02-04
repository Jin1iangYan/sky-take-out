package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    private final SetmealMapper setmealMapper;
    private final SetmealDishMapper setmealDishMapper;

    public SetmealServiceImpl(SetmealMapper setmealMapper, SetmealDishMapper setmealDishMapper) {
        this.setmealMapper = setmealMapper;
        this.setmealDishMapper = setmealDishMapper;
    }

    /**
     * 修改套餐信息，同时更新关联的菜品信息。
     *
     * @param setmealDTO 套餐数据传输对象，包含套餐基本信息和关联菜品信息
     */
    @Transactional
    @Override
    public void updateSetmealWithSetmealDishes(SetmealDTO setmealDTO) {
        // 实现修改套餐信息的逻辑
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 更新setmeal表
        setmealMapper.update(setmeal);

        // 更新关联表
            // 先根据套餐id删除全部关联
        setmealDishMapper.deleteBatchBySetmealId(setmeal.getId());
            // 再重新插入
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmeal.getId()));
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 分页查询套餐列表。
     *
     * @param setmealPageQueryDTO 套餐分页查询数据传输对象，包含查询条件和分页信息
     * @return 分页查询结果，包含套餐列表和分页信息
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = (Page<SetmealVO>) setmealMapper.page(setmealPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 更新套餐的起售或停售状态。
     *
     * @param setmealId 套餐ID
     * @param status    状态值（1：起售，0：停售）
     */
    @Override
    public void updateSetmealStatus(Long setmealId, Integer status) {
        Setmeal setmeal = new Setmeal();
        setmeal.setId(setmealId);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }

    /**
     * 批量删除套餐。
     *
     * @param ids 套餐ID列表
     */
    @Override
    @Transactional
    public void deleteSetmealByIds(List<Long> ids) {
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.findById(id);
            if(StatusConstant.ENABLE == setmeal.getStatus()){
                //起售中的套餐不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        setmealMapper.deleteBatchByIds(ids);
        // 删除相关的setmeal_dish
        for (Long id : ids) {
            setmealDishMapper.deleteBatchBySetmealId(id);
        }
    }

    /**
     * 添加新套餐，并关联菜品信息。
     *
     * @param setmealDTO 套餐数据传输对象，包含套餐基本信息和关联菜品信息
     */
    @Transactional
    @Override
    public void addSetmealWithSetmealDishes(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmeal.getId()));
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 根据ID查询套餐详情。
     *
     * @param id 套餐ID
     * @return 套餐视图对象，包含套餐基本信息和关联菜品信息
     */
    @Override
    public SetmealVO findSetmealById(Long id) {
        Setmeal setmeal = setmealMapper.findById(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);

        // 查询setmeal_dish信息
        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }
}