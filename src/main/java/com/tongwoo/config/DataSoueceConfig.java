package com.tongwoo.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
public class DataSoueceConfig {
//    @Bean("druidDataSource")
    private static DataSoueceConfig dataSoueceConfig;
    private static DruidDataSource dataSource;

    private DataSoueceConfig(){
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/ltest?characterEncoding=utf8&serverTimezone=UTC");//10.74.27.195:3306/bike
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        //初始连接数(默认值0)
        dataSource.setInitialSize(8);
        //最小连接数(默认值0)
        dataSource.setMinIdle(8);
        //最大连接数(默认值8,注意"maxIdle"这个属性已经弃用)
        dataSource.setMaxActive(32);

        dataSource.setInitialSize(5);

        //报错[ERROR] - testWhileIdle is true, validationQuery not set解决
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setValidationQuery("SELECT 1");

    }

    /**
     * @Author linyuanqing
     * @Description 
     * @Date 2019/6/24
     * @Param 
     * *@return com.tongwoo.config.DataSoueceConfig
     **/
    public static DataSoueceConfig getInstance() {
        if (dataSoueceConfig == null) {
            synchronized (DataSoueceConfig.class)
            {
                if (dataSoueceConfig == null)
                {
                    dataSoueceConfig = new DataSoueceConfig();
                }

            }
        }
        return dataSoueceConfig;
    }

    public DruidPooledConnection getConnection() throws SQLException
    {
        DruidPooledConnection connection = dataSource.getConnection();
        return connection;
    }
}
