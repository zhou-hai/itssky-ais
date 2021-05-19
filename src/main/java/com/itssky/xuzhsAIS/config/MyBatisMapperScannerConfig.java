package com.itssky.xuzhsAIS.config;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter({MybatisConfig.class})
public class MyBatisMapperScannerConfig
{
  @Bean
  public MapperScannerConfigurer mapperScannerConfigurer()
  {
    MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
    
    mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
    
    mapperScannerConfigurer.setBasePackage("com.itssky.xuzhsAIS.dao");
    return mapperScannerConfigurer;
  }
}
