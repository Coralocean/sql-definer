package com.example.sqldefinertest.config;

import com.coralocean.sqldefiner.dispatcher.ExplainDispatcher;
import com.coralocean.sqldefiner.dispatcher.MybatisDispatcher;
import com.coralocean.sqldefiner.mock.MockDataGenerator;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.sql.DataSource;

@Configuration
public class MybatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier(value = "readWriteDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactory.setMapperLocations(resourcePatternResolver.getResources("classpath:mapper/*.xml"));
        return sqlSessionFactory.getObject();
    }

    @Bean
    public SqlSessionTemplate sessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public MybatisDispatcher mybatisDispatcher(SqlSessionFactory sqlSessionFactory, MockDataGenerator mockDataGenerator) {
        return new MybatisDispatcher(sqlSessionFactory, "mapper");
    }

    @Bean
    public ExplainDispatcher explainDispatcher(@Qualifier(value = "readWriteDataSource") DataSource dataSource) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/"); // 设置模板文件所在的目录
        templateResolver.setSuffix(".html"); // 设置模板文件的后缀
        templateResolver.setTemplateMode(TemplateMode.HTML); // 设置模板文件的类型为HTML
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return new ExplainDispatcher(dataSource,templateEngine, "/opt/logs/analysis.html");
    }
}
