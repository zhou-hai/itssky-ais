package com.itssky.xuzhsAIS.common;

public class Constant
{
  public static final String VDOVDM_REGX = "!([\\s\\S]*?)\\*.{2}";
  public static final String VDOVDM_SPLIT_FIRST = ",";
  public static final String VDOVDM_SPLIT_SECOND = "\\*";
  public static final String[] TCP_SOCKET_HOST_ARR = { "172.0.0.16" };
  public static final String[] CHUANBDTJTSJ_XIAOXID_ARR = { "1", "2", "3", "4", "5", "18", "19", "21", "24" };
  public static final String[] CHUANBDTSJ_XIAOXID_ARR = { "1", "2", "3", "18", "19" };
  public static final String[] CHUANBJTSJ_XIAOXID_ARR = { "5", "24" };
  public static final String[] DUANXXSF_XIAOXID_ARR = { "6", "7", "8", "12", "13", "14" };
  public static final String SAVEFILE_TXT_PATH = "D:/AIS/";
  public static final String SAVEFILE_TXT_HOUR_NAME = "ais_1hour";
  public static final String SAVEFILE_TXT_SECOND_NAME = "ais_30s";
  public static final String SAVEFILE_TXT_MIN_NAME = "ais_5min";
  public static final int INIT_SOCKET_CONNECT_TIME = 5000;
  public static final int INIT_SOCKET_RECONNECT_TIME = 5000;
  public static final int INIT_SOCKET_REPEAT_TIME = 10000;
  public static final int TIME_INTERVAL_SECOND = 30;
  public static final int TIME_INTERVAL_HOUR = 1;
  public static final int TIME_INTERVAL_MIN = 5;
}
