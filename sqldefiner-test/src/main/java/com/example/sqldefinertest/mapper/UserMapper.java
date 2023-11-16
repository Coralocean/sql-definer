package com.example.sqldefinertest.mapper;

import com.example.sqldefinertest.po.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
//    User findUserByName(@Param("name") String name);

    User findUser(User user);
}
