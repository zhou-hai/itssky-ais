package com.itssky.xuzhsAIS.dao;

import com.itssky.xuzhsAIS.entity.PageData;

public abstract interface GuzjlMapper
{
  public abstract int add(PageData paramPageData);
  
  public abstract int updateGuzjl(PageData paramPageData);
  
  public abstract String getJizIdByIP(String paramString);
  
  public abstract PageData getGuzStopTimeByIP(String paramString);
  
  public abstract int updateJizzt(PageData paramPageData);
  
  /**
   * 根据船舶mmsi获取船舶信息
   * @param paramString
   * @return
   */
  public abstract PageData getChuanbInfoByMmsi(PageData paramPageData);
  
  /**
   * 根据获取的ais信息添加船舶历史数据(船舶历史数据表有触发器，会默认同步船舶实时位置信息表)
   * @param paramPageData
   * @return
   */
  public abstract int addChuanblswzByAis(PageData paramPageData);
  
  /**
   * 根据获取的ais信息添加船舶基本信息(存在获取到的ais信息不在省局船舶基本信息库中的情况)
   * @param paramPageData
   * @return
   */
  public abstract int addChuanbjbxxByAis(PageData paramPageData);
}
