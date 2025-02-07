package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Api(tags = "套餐管理相关接口")
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @ApiOperation("套餐分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("新增套餐")
    @PostMapping
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")
    public Result<String> addSetmealWithSetmealDishes(@RequestBody SetmealDTO setmealDTO) {
        setmealService.addSetmealWithSetmealDishes(setmealDTO);
        return Result.success("新增套餐成功");
    }

    @ApiOperation("修改套餐")
    @PutMapping
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<String> updateSetmealWithSetmealDishes(@RequestBody SetmealDTO setmealDTO) {
        setmealService.updateSetmealWithSetmealDishes(setmealDTO);
        return Result.success("修改套餐成功");
    }

    @ApiOperation("套餐起售、停售")
    @PostMapping("/status/{status}")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<String> updateSetmealStatus(@PathVariable Integer status,
                                              @RequestParam Long id) {
        setmealService.updateSetmealStatus(id, status);
        return Result.success("更新套餐状态成功");
    }

    @ApiOperation("批量删除套餐")
    @DeleteMapping
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<String> deleteSetmealByIds(@RequestParam List<Long> ids) {
        setmealService.deleteSetmealByIds(ids);
        return Result.success("删除套餐成功");
    }

    @ApiOperation("根据id查询套餐")
    @GetMapping("/{id}")
    public Result<SetmealVO> findSetmealById(@PathVariable Long id) {
        SetmealVO setmealVO = setmealService.findSetmealById(id);
        return Result.success(setmealVO);
    }
}