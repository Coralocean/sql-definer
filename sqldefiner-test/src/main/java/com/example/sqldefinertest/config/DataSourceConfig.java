package com.example.sqldefinertest.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean("readWriteDataSource")
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/trade?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("12345678");
        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setMinimumIdle(5);
        hikariConfig.setIdleTimeout(5000);
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        ;;
        return dataSource;

    }

}
