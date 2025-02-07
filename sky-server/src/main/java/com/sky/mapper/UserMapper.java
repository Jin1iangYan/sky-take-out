package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openId openid
     * @return 用户对象
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String openId);

    /**
     * 插入用户
     * @param user 用户对象
     */
    void insert(User user);
}
