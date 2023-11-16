package com.coralocean.sqldefiner.mock;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mock {
    int length() default 1;
    // 生成随机的模拟数据
    int level() default 0;

    String property() default "";
}
