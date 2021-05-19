package com.itssky.xuzhsAIS.util;

import java.io.PrintStream;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import dk.tbsalling.aismessages.nmea.exceptions.NMEAParseException;

public class IDUtils
{
  private static byte[] lock = new byte[0];
  private static final long w = 1L;
  
  public static String createID()
  {
    long r = 0L;
    synchronized (lock)
    {
      r = (long) ((Math.random() + 1.0D) * 1.0D);
    }
    return System.currentTimeMillis() + String.valueOf(r).substring(1);
  }
  
  public static String generateUUID()
  {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }
  
  /**
   * 生成16位不重复的随机数，含数字+大小写
   * @return
   */
  public static String getGUID() {
      StringBuilder uid = new StringBuilder();
      //产生16位的强随机数
      Random rd = new SecureRandom();
      for (int i = 0; i < 16; i++) {
          //产生0-2的3位随机数
          int type = rd.nextInt(3);
          switch (type){
              case 0:
                  //0-9的随机数
                  uid.append(rd.nextInt(10));
                  break;
              case 1:
                  //ASCII在65-90之间为大写,获取大写随机
                  uid.append((char)(rd.nextInt(25)+65));
                  break;
              case 2:
                  //ASCII在97-122之间为小写，获取小写随机
                  uid.append((char)(rd.nextInt(25)+97));
                  break;
              default:
                  break;
          }
      }
      return uid.toString();
  }
  
  public static void main(String[] args)
  {
	  
	  String nmeaMessageRegExp = "^!.*\\*[0-9A-Fa-f]{2}$";
	  String rawMessage = "!AIVDO,1,1,,,430FrSQvE>2T?`LViPCRGFo00000,0*3C";
      System.out.println(rawMessage.matches(nmeaMessageRegExp));
  }
}
