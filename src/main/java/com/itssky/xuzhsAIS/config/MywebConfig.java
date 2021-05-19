package com.itssky.xuzhsAIS.config;

import com.itssky.xuzhsAIS.config.interceptor.MyInterceptor;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MywebConfig
  implements WebMvcConfigurer
{
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> arg0) {}
  
  public void addCorsMappings(CorsRegistry arg0) {}
  
  public void addFormatters(FormatterRegistry arg0) {}
  
  public void addInterceptors(InterceptorRegistry arg0)
  {
    arg0.addInterceptor(new MyInterceptor()).addPathPatterns(new String[] { "/asd/**" });
  }
  
  public void addResourceHandlers(ResourceHandlerRegistry arg0) {}
  
  public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> arg0) {}
  
  public void addViewControllers(ViewControllerRegistry arg0)
  {
    arg0.addViewController("/zxc/foo").setViewName("foo");
  }
  
  public void configureAsyncSupport(AsyncSupportConfigurer arg0) {}
  
  public void configureContentNegotiation(ContentNegotiationConfigurer arg0) {}
  
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer arg0) {}
  
  public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> arg0) {}
  
  public void configureMessageConverters(List<HttpMessageConverter<?>> arg0) {}
  
  public void configurePathMatch(PathMatchConfigurer arg0) {}
  
  public void configureViewResolvers(ViewResolverRegistry arg0) {}
  
  public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> arg0) {}
  
  public void extendMessageConverters(List<HttpMessageConverter<?>> arg0) {}
  
  public MessageCodesResolver getMessageCodesResolver()
  {
    return null;
  }
  
  public Validator getValidator()
  {
    return null;
  }
  
}
