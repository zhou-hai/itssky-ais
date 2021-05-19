package com.itssky.xuzhsAIS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;

@Controller
@EnableAutoConfiguration
@ComponentScan(basePackages={"com.itssky"})
@EnableScheduling
public class Execute
{
  
  public static void main(String[] args)
    throws Exception
  {
    SpringApplication.run(Execute.class, args);
  }
}
