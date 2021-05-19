package com.itssky.xuzhsAIS.config.interceptor;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

public class MyInterceptor
  implements HandlerInterceptor
{
  public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
    throws Exception
  {
    System.out.println("preHandle被调用");
    Map map = (Map)httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    System.out.println(map.get("name"));
    System.out.println(httpServletRequest.getParameter("username"));
    if (map.get("name").equals("zhangsan")) {
      return true;
    }
    PrintWriter printWriter = httpServletResponse.getWriter();
    printWriter.write("please login again!");
    return false;
  }
  
  public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView)
    throws Exception
  {
    System.out.println("postHandle被调用");
  }
  
  public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e)
    throws Exception
  {
    System.out.println("afterCompletion被调用");
  }
}
