package com.itssky.xuzhsAIS.config;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration
@EnableTransactionManagement
public class MybatisConfig
  implements TransactionManagementConfigurer
{
  @Autowired
  private DataSource dataSource;
  
  @Bean(name={"sqlSessionFactory"})
  public SqlSessionFactory sqlSessionFactoryBean()
  {
    SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
    bean.setDataSource(this.dataSource);
    try
    {
      return bean.getObject();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
  @Bean
  public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory)
  {
    return new SqlSessionTemplate(sqlSessionFactory);
  }
  
  public PlatformTransactionManager annotationDrivenTransactionManager()
  {
    return new DataSourceTransactionManager(this.dataSource);
  }
}
