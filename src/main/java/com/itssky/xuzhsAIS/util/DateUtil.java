package com.itssky.xuzhsAIS.util;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil
{
  private static final SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
  private static final SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
  private static final SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
  private static final SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
  private static final SimpleDateFormat sdfMin = new SimpleDateFormat("mm");
  private static final SimpleDateFormat sdfSs = new SimpleDateFormat("ss");
  private static final SimpleDateFormat sdfYearMonth = new SimpleDateFormat("yyyy-MM");
  private static final SimpleDateFormat sdfYearMonthDay = new SimpleDateFormat("yyyy-MM-dd");
  private static final SimpleDateFormat sdfYearMonthDayTwo = new SimpleDateFormat("yyyyMMdd");
  private static final SimpleDateFormat sdfYearMonthDayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
  public static String getYear()
  {
    return sdfYear.format(new Date());
  }
  
  public static String getMonth()
  {
    return sdfMonth.format(new Date());
  }
  
  public static String getDay()
  {
    return sdfDay.format(new Date());
  }
  
  public static String getMin()
  {
    return sdfMin.format(new Date());
  }
  
  public static String getSs()
  {
    return sdfSs.format(new Date());
  }
  
  public static String getHour()
  {
    return sdfHour.format(new Date());
  }
  
  public static String getYearMonth()
  {
    return sdfYearMonth.format(new Date());
  }
  
  public static String getYearMonthDay()
  {
    return sdfYearMonthDay.format(new Date());
  }
  
  public static String getDays()
  {
    return sdfYearMonthDayTwo.format(new Date());
  }
  
  public static String getTime()
  {
    return sdfYearMonthDayTime.format(new Date());
  }
  
  public static Date fomatDate(String date)
  {
    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    try
    {
      return fmt.parse(date);
    }
    catch (ParseException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static String getDateMkdirs(String type)
  {
    String value = "";
    switch (type)
    {
    case "Min": 
      value = getYear() + "/" + getMonth() + "/" + getDay() + "/" + getHour() + "/" + getMin() + "/";
      break;
    case "Hour": 
      value = getYear() + "/" + getMonth() + "/" + getDay() + "/" + getHour() + "/";
      break;
    case "Day": 
      value = getYear() + "/" + getMonth() + "/" + getDay() + "/";
      break;
    case "Month": 
      value = getYear() + "/" + getMonth() + "/";
      break;
    case "Year": 
      value = getYear() + "/";
      break;
    default: 
      value = "";
    }
    return value;
  }
  
  public static String getStringDateByString(String time)
  {
    SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
    String sDate = null;
    try
    {
      Date date = sdf1.parse(time);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      sDate = sdf.format(date);
    }
    catch (ParseException e)
    {
      e.printStackTrace();
    }
    return sDate;
  }
  
  public static void main(String[] args)
  {
    System.out.println(getDateMkdirs("Min"));
  }
}
