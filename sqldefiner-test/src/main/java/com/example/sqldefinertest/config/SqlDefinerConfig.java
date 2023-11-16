package com.example.sqldefinertest.config;

import com.coralocean.sqldefiner.SqlDefender;
import com.coralocean.sqldefiner.dispatcher.ExplainDispatcher;
import com.coralocean.sqldefiner.dispatcher.MybatisDispatcher;
import com.coralocean.sqldefiner.mock.MockDataGenerator;
import com.coralocean.sqldefiner.parser.MapperParser;
import com.coralocean.sqldefiner.parser.MybatisXMLParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SqlDefinerConfig {


    @Bean
    public MockDataGenerator mockDataGenerator(@Qualifier("readWriteDataSource") DataSource dataSource) {
        return new MockDataGenerator(dataSource);
    }

    @Bean
    public SqlDefender sqlDefender(MybatisDispatcher mybatisDispatcher,
                                   ExplainDispatcher explainDispatcher,
                                   MockDataGenerator mockDataGenerator) {



        return new SqlDefender(
                new MapperParser(mybatisDispatcher.getXmlScanPackage()),
                new MybatisXMLParser(mockDataGenerator),
                mybatisDispatcher,
                explainDispatcher);
    }
}
