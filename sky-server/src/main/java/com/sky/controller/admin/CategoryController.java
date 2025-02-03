package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     */
    @ApiOperation("新增分类")
    @PostMapping
    public Result<String> addCategory(@RequestBody CategoryDTO category) {
        categoryService.addCategory(category);
        return Result.success("新增分类成功");
    }

    /**
     * 修改分类
     */
    @ApiOperation("修改分类")
    @PutMapping
    public Result<String> updateCategory(@RequestBody CategoryDTO category) {
        categoryService.updateCategory(category);
        return Result.success("分类修改成功");
    }

    /**
     * 分类分页查询
     */
    @ApiOperation("分类分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO queryDTO) {
        PageResult pageResult = categoryService.page(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用/禁用分类
     */
    @ApiOperation("启用、禁用分类")
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable("status") Integer status,
                                  @RequestParam("id") Long categoryId) {
        categoryService.updateStatus(categoryId, status);
        return Result.success("分类状态更新成功");
    }

    /**
     * 根据 id 删除分类
     */
    @ApiOperation("根据id删除分类")
    @DeleteMapping
    public Result<String> deleteCategory(@RequestParam("id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return Result.success("删除分类成功");
    }

    /**
     * 根据类型查询分类
     */
    @ApiOperation("根据类型查询分类")
    @GetMapping("/list")
    public Result<List<Category>> listCategory(@RequestParam(value = "type", required = false) Integer type) {
        List<Category> list = categoryService.findByType(type);
        return Result.success(list);
    }
}