package com.sky.mapper;

import com.sky.dto.UserReportDataDTO;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openId openid
     * @return 用户对象
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String openId);

    @Select("select * from user where id = #{id}")
    User getById(Long id);

    /**
     * 插入用户
     * @param user 用户对象
     */
    void insert(User user);

    List<UserReportDataDTO> selectUserReportByDateRange(LocalDateTime begin, LocalDateTime end);
}
