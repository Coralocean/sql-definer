package com.example.sqldefinertest;

import com.coralocean.sqldefiner.SqlDefender;
import com.example.sqldefinertest.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class SqldefinerTestApplicationTests {

    @Resource
    private SqlDefender defender;

    @Test
    void contextLoads() {
    }

    @Test
    public void test(){
        defender.audit(UserMapper.class);
    }

}
