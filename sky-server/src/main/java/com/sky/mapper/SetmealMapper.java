package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    List<SetmealVO> page(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteBatchByIds(List<Long> ids);

    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    Setmeal findById(Long id);
}
