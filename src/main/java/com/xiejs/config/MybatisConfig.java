package com.xiejs.config;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by Administrator on 2017/5/11.
 *
 * @author xiejs
 */
@Configuration
@ConfigurationProperties(prefix = "mysql")
public class MybatisConfig {
    private String driverClassName;
    private String url;
    private String username;
    private String password;

    public MybatisConfig() {
    }

    @Bean
    public DataSource dataSource() {
        DataSource dataSource = this.getTomcatPoolingDataSource();
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(this.dataSource());
    }

    private DataSource getTomcatPoolingDataSource() {
        DruidXADataSource dataSource = new DruidXADataSource();
        dataSource.setDriverClassName(driverClassName.trim());
        dataSource.setUrl(url.trim());
        dataSource.setUsername(username.trim());
        dataSource.setPassword(password.trim());
        dataSource.setInitialSize(5); // 连接池启动时创建的初始化连接数量（默认值为0）
        dataSource.setMaxActive(20); // 连接池中可同时连接的最大的连接数
        //dataSource.setMaxIdle(12); // 连接池中最大的空闲的连接数，超过的空闲连接将被释放，如果设置为负数表示不限
        dataSource.setMinIdle(0); // 连接池中最小的空闲的连接数，低于这个数量会被创建新的连接
        dataSource.setMaxWait(60000); // 最大等待时间，当没有可用连接时，连接池等待连接释放的最大时间，超过该时间限制会抛出异常，如果设置-1表示无限等待
        dataSource.setRemoveAbandonedTimeout(180); // 超过时间限制，回收没有用(废弃)的连接
        dataSource.setRemoveAbandoned(true); // 超过removeAbandonedTimeout时间后，是否进 行没用连接（废弃）的回收
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);
        dataSource.setTestWhileIdle(true);
        dataSource.setTimeBetweenEvictionRunsMillis(1000 * 60 * 30); // 检查无效连接的时间间隔 设为30分钟
        return dataSource;
    }

    @Bean(
            name = {"transactionManager"}
    )
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(this.dataSource());
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

}
