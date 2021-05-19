package com.itssky.xuzhsAIS.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * spring上下文获取springbean
 * @author zhouh
 *
 */
@Configuration
public class SpringBeanUtil implements ApplicationContextAware{
	private static ApplicationContext applicationContext = null;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtil.applicationContext = applicationContext;
    }
    public static Object getBeanByName(String beanName){
        if(applicationContext == null){
            return null;
        }
        return applicationContext.getBean(beanName);
    }
    public static <T> T getBean(Class<T> type) {
        return applicationContext.getBean(type);
    }
    
    public static <T> T getBean(Class<T> type,String host) {
        return applicationContext.getBean(type,host);
    }
    
    @Scope("prototype")
    public static <T> T getBean(String name,Object[] args) {
        return (T) applicationContext.getBean(name,args);
    }

}
