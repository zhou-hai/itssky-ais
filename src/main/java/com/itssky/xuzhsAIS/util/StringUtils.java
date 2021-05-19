package com.itssky.xuzhsAIS.util;

import com.itssky.xuzhsAIS.entity.PageData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils
{
  public static boolean isEmpty(String str)
  {
    return (str == null) || (str.length() == 0) || ("null".equals(str));
  }
  
  public static boolean hasText(String str)
  {
    return (str != null) && (str.trim().length() > 0);
  }
  
  public static boolean isEmpty(Collection<?> col)
  {
    return (col == null) || (col.isEmpty());
  }
  
  public static boolean notEmpty(Collection<?> col)
  {
    return !isEmpty(col);
  }
  
  public static String trim(String str)
  {
    if (str == null) {
      return "";
    }
    return str.trim();
  }
  
  public static List<PageData> dealClobList(List<PageData> list, String field)
  {
    for (PageData entity : list)
    {
      String jwd = null;
      try
      {
        jwd = ClobToString((Clob)entity.get(field));
        entity.remove(field);
        entity.put(field, jwd);
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    return list;
  }
  
  public static String ClobToString(Clob clob)
    throws SQLException, IOException
  {
    String reString = "";
    if ((clob != null) && (clob.length() != 0L))
    {
      Reader is = clob.getCharacterStream();
      BufferedReader br = new BufferedReader(is);
      String s = br.readLine();
      StringBuffer sb = new StringBuffer();
      while (s != null)
      {
        sb.append(s);
        s = br.readLine();
      }
      reString = sb.toString();
    }
    return reString;
  }
  
  public static List<String> splitStringByRegx(String value, String delimeter)
  {
    List<String> list = new ArrayList();
    Matcher m = Pattern.compile(delimeter).matcher(value);
    while (m.find()) {
      for (int i = 0; i <= m.groupCount(); i++) {
        if (i == 0) {
          list.add(m.group(0));
        }
      }
    }
    return list;
  }
  
  public static String[] splitString(String value, String delimeter)
  {
    return value.split(delimeter);
  }
  
  public static byte[] getStringByteArr(String value)
  {
    return value.getBytes();
  }
  
  public static int getXor(byte[] datas)
  {
    byte temp = datas[0];
    for (int i = 1; i < datas.length; i++) {
      temp = (byte)(temp ^ datas[i]);
    }
    return temp;
  }
  
  public static String intToHex(int n)
  {
    StringBuilder sb = new StringBuilder(8);
    
    char[] b = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    while (n != 0)
    {
      sb = sb.append(b[(n % 16)]);
      n /= 16;
    }
    String a = sb.reverse().toString();
    return a;
  }
  
  public static int bigBinaryToDecimal(String binarySource)
  {
    if ((binarySource.startsWith("0")) && 
      (binarySource.length() > 1)) {
      binarySource = binarySource.substring(1);
    }
    BigInteger bi = new BigInteger(binarySource, 2);
    return Integer.parseInt(bi.toString());
  }
  
  public static int binaryToDecimal(String value)
  {
    int binaryNumber = 0;
    int decimal = 0;
    int p = 0;
    if ((value.startsWith("0")) && 
      (value.length() > 1)) {
      value = value.substring(1);
    }
    if (value.length() >= 10)
    {
      BigDecimal a = new BigDecimal(value);
      binaryNumber = a.intValue();
      while (binaryNumber != 0)
      {
        int temp = binaryNumber % 10;
        decimal = (int)(decimal + temp * Math.pow(2.0D, p));
        binaryNumber /= 10;
        p++;
      }
    }
    else
    {
      binaryNumber = Integer.valueOf(value).intValue();
      while (binaryNumber != 0)
      {
        int temp = binaryNumber % 10;
        decimal = (int)(decimal + temp * Math.pow(2.0D, p));
        binaryNumber /= 10;
        p++;
      }
    }
    return decimal;
  }
  
  public static boolean isValid(String[] arr, int targetValue)
  {
    String value = String.valueOf(targetValue);
    return Arrays.asList(arr).contains(value);
  }
  
  public static double formatDecimals(double num, int len)
  {
    String lenStr = "#.";
    for (int i = 0; i < len; i++) {
      lenStr = lenStr + "#";
    }
    DecimalFormat dFormat = new DecimalFormat(lenStr);
    String yearString = dFormat.format(num);
    String regex = "^(\\d+)(\\.0)$";
    String result = yearString.replaceAll(regex, "$1");
    Double temp = Double.valueOf(result);
    return temp.doubleValue();
  }
  
  public static void main(String[] args)
  {
    String value = "AIVDM,2,1,0,A,58wt8Ui`g??r21`7S=:22058<v05Htp000000015>8OA;0sk,0";
    System.out.println(intToHex(getXor(getStringByteArr(value))));
    System.out.println(String.valueOf(34).substring(1));
  }
}
