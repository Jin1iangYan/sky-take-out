package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Slf4j
@RestController
@Api(tags = "菜品相关接口")
@RequestMapping("admin/dish")
public class DishController {
    @Autowired
    DishService dishService;

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result<?> save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品: {}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分类/分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询: {}", dishPageQueryDTO);
        PageResult resultResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(resultResult);
    }

    @DeleteMapping()
    @ApiOperation("批量删除菜品")
    public Result<?> delete(@RequestParam ArrayList<Long> ids) {
        log.info("批量删除菜品: {}", ids);
        dishService.deleteDishesByIds(ids);
        return Result.success();
    }
}
