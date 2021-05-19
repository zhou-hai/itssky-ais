package com.itssky.xuzhsAIS.dao;

import com.itssky.xuzhsAIS.entity.PageData;
import java.util.List;

public abstract interface SelectAISInfoMapper
{
  public abstract String getMubzByYuanz(String paramString);
  
  public abstract List<Integer> getBitsmByXiaoxId(int paramInt);
  
  public abstract List<Integer> getBitsmByXiaoxIdAndMc(PageData paramPageData);
  
  public abstract List<PageData> getJizInfoList();
  
  public abstract List<PageData> getYicJizInfoList();
}
