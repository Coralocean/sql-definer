package com.example.sqldefinertest.po;


import com.coralocean.sqldefiner.mock.Mock;
import lombok.Data;

import java.util.List;

@Data
public class User {
    @Mock(length = 2, level = 1, property = "user.id")
    private Long id;

    @Mock(length = 2, level = 1, property = "user.name")
    private List<String> names;

    @Mock(level = 1, property = "user.age")
    private Integer age;
}
