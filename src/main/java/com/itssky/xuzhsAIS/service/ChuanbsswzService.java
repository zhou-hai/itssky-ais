package com.itssky.xuzhsAIS.service;

import com.itssky.xuzhsAIS.common.Constant;
import com.itssky.xuzhsAIS.dao.GuzjlMapper;
import com.itssky.xuzhsAIS.dao.SelectAISInfoMapper;
import com.itssky.xuzhsAIS.entity.PageData;
import com.itssky.xuzhsAIS.util.DateUtil;
import com.itssky.xuzhsAIS.util.FileUtils;
import com.itssky.xuzhsAIS.util.StringUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChuanbsswzService
{
  private static final Logger log = LoggerFactory.getLogger(ChuanbsswzService.class);
  @Autowired
  private SelectAISInfoMapper selectAISInfoMapper;
  @Autowired
  private GuzjlMapper guzjlMapper;
  
  public boolean batchAddData(List<String> inList, String host, int port, String type)
  {
    boolean isSuccess = true;
    if (type.equals("second")) {
      FileUtils.clearInfoForFile("D:/AIS/" + DateUtil.getDateMkdirs("Other") + "ais_30s" + ".txt");
    } else if (type.equals("min")) {
      FileUtils.clearInfoForFile("D:/AIS/" + DateUtil.getDateMkdirs("Other") + "ais_5min" + ".txt");
    }
    for (String getData : inList) {
      isSuccess = addData(getData, host, port, type);
    }
    return isSuccess;
  }
  
  public boolean addData(String getData, String host, int port, String type)
  {
    log.info("获取到的数据为：" + getData + "，host为：" + host + ",端口为：" + port);
    boolean isSuccess = true;
    try
    {
      List<PageData> list = dealVdoVdmData(dealGetData(getData), host, port);
      if (list.size() != 0)
      {
        String erjzInfo = getErjzStrByAisXx(list);
        if (!StringUtils.isEmpty(erjzInfo)) {
          if (type.equals("min"))
          {
            String msg = getChuanbStaticInfoShijzByErjz(erjzInfo);
            if (!StringUtils.isEmpty(msg))
            {
              String mkdirs = "";
              String fileName = "";
              mkdirs = DateUtil.getDateMkdirs("Other");
              fileName = "ais_5min";
              FileUtils.createSingleTxtFile(msg, "D:/AIS/" + mkdirs, fileName);
              log.info("静态信息添加进文件成功！");
            }
          }
          else
          {
            String msg = getChuanbDynamicInfoShijzByErjz(erjzInfo);
            if (!StringUtils.isEmpty(msg))
            {
              String mkdirs = "";
              String fileName = "";
              if (type.equals("hour"))
              {
                mkdirs = DateUtil.getDateMkdirs("Hour");
                fileName = "ais_1hour";
              }
              else if (type.equals("second"))
              {
                mkdirs = DateUtil.getDateMkdirs("Other");
                fileName = "ais_30s";
              }
              FileUtils.createSingleTxtFile(msg, "D:/AIS/" + mkdirs, fileName);
              log.info("动态信息添加进文件成功！");
            }
          }
        }
      }
    }
    catch (Exception e)
    {
      isSuccess = false;
      log.warn("添加失败！" + e);
      e.printStackTrace();
    }
    return isSuccess;
  }
  
  private List<String> dealGetData(String getData)
  {
    return StringUtils.splitStringByRegx(getData, "!([\\s\\S]*?)\\*.{2}");
  }
  
  private List<PageData> dealVdoVdmData(List<String> list, String host, int port)
  {
    List<PageData> outList = new ArrayList();
    PageData param = new PageData();
    for (String info : list)
    {
      String[] checkArr = info.split("\\*");
      String checkValue = StringUtils.intToHex(StringUtils.getXor(StringUtils.getStringByteArr(checkArr[0].substring(1))));
      if (checkValue.equals(checkArr[1]))
      {
        String[] arr = StringUtils.splitString(info, ",");
        String[] _arr = arr[6].split("\\*");
        param = new PageData();
        param.put("B_VC_BAOWT", arr[0]);
        param.put("B_NB_BAOWZTS", arr[1]);
        param.put("B_NB_BAOWXH", arr[2]);
        param.put("B_NB_BAOWXLH", arr[3]);
        param.put("B_VC_BAOWJSXD", arr[4]);
        param.put("B_CLOB_AISXX", arr[5]);
        param.put("B_NB_TIANCW", _arr[0]);
        param.put("B_VC_BAOWJW", _arr[1]);
        param.put("B_VC_JIESHOST", host);
        param.put("B_NB_JIESPORT", Integer.valueOf(port));
        outList.add(param);
      }
    }
    return outList;
  }
  
  private String getErjzStrByAisXx(List<PageData> list)
  {
    String erjzInfo = "";
    for (PageData info : list)
    {
      String aisInfo = info.getString("B_CLOB_AISXX");
      for (int i = 0; i < aisInfo.length(); i++)
      {
        String param = "";
        if (i == aisInfo.length() - 1) {
          param = aisInfo.substring(i);
        } else {
          param = aisInfo.substring(i, i + 1);
        }
        erjzInfo = erjzInfo + this.selectAISInfoMapper.getMubzByYuanz(param);
      }
    }
    return erjzInfo;
  }
  
  private String getShijzByErjz(String erjzStr)
  {
    String msg = "";
    int xiaoxId = StringUtils.binaryToDecimal(erjzStr.substring(0, 6));
    if (StringUtils.isValid(Constant.CHUANBDTJTSJ_XIAOXID_ARR, xiaoxId))
    {
      List<Integer> list_gz = this.selectAISInfoMapper.getBitsmByXiaoxId(xiaoxId);
      int startIndex = 6;
      int index = 0;
      Iterator localIterator;
      String nameValue;
      switch (xiaoxId)
      {
      case 5: 
        for (localIterator = list_gz.iterator(); localIterator.hasNext();)
        {
          int guiz = ((Integer)localIterator.next()).intValue();
          if ((guiz == 8) && (index == 6))
          {
            int value = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
            msg = msg + value + ",";
          }
          else if ((guiz == 8) && (index == 10))
          {
            int value = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
            msg = msg + StringUtils.formatDecimals(Double.valueOf(value).doubleValue() / 10.0D, 1) + ",";
          }
          else if ((guiz == 30) && (index == 7))
          {
            nameValue = erjzStr.substring(startIndex, startIndex + guiz);
            msg = msg + dealData_Chic(nameValue);
          }
          else
          {
            msg = msg + dealData(erjzStr, startIndex, guiz, index, list_gz.size());
          }
          startIndex += guiz;
          index++;
        }
        break;
      case 19: 
        for (localIterator = list_gz.iterator(); localIterator.hasNext();)
        {
          int guiz = ((Integer)localIterator.next()).intValue();
          if ((guiz == 30) && (index == 13))
          {
            nameValue = erjzStr.substring(startIndex, startIndex + guiz);
            msg = msg + dealData_Chic(nameValue);
          }
          else
          {
            msg = msg + dealData(erjzStr, startIndex, guiz, index, list_gz.size());
          }
          startIndex += guiz;
          index++;
        }
        break;
      case 21: 
        for (localIterator = list_gz.iterator(); localIterator.hasNext();)
        {
          int guiz = ((Integer)localIterator.next()).intValue();
          msg = msg + dealData(erjzStr, startIndex, guiz, index, list_gz.size());
          startIndex += guiz;
          index++;
        }
        break;
      case 24: 
        int biaos = StringUtils.bigBinaryToDecimal(erjzStr.substring(38, 40));
        list_gz = new ArrayList();
        PageData param = new PageData();
        param.put("B_NB_XIAOXID", Integer.valueOf(xiaoxId));
        if (biaos == 0)
        {
          param.put("B_VC_XIAOXMC", "静态数据报告A部分");
          list_gz = this.selectAISInfoMapper.getBitsmByXiaoxIdAndMc(param);
          for (localIterator = list_gz.iterator(); localIterator.hasNext();)
          {
            int guiz = ((Integer)localIterator.next()).intValue();
            msg = msg + dealData(erjzStr, startIndex, guiz, index, list_gz.size());
            startIndex += guiz;
            index++;
          }
        }
        else if (biaos == 1)
        {
          param.put("B_VC_XIAOXMC", "静态数据报告B部分");
          list_gz = this.selectAISInfoMapper.getBitsmByXiaoxIdAndMc(param);
          for (localIterator = list_gz.iterator(); localIterator.hasNext();)
          {
            int guiz = ((Integer)localIterator.next()).intValue();
            if (index == 4)
            {
              nameValue = erjzStr.substring(startIndex, startIndex + guiz);
              msg = msg + dealData_Gongys(nameValue);
            }
            else if ((guiz == 30) && (index == 6))
            {
              nameValue = erjzStr.substring(startIndex, startIndex + guiz);
              msg = msg + dealData_Chic(nameValue);
            }
            else
            {
              msg = msg + dealData(erjzStr, startIndex, guiz, index, list_gz.size());
            }
            startIndex += guiz;
            index++;
          }
        }
        else
        {
          list_gz = new ArrayList();
        }
        break;
      default: 
          for (localIterator = list_gz.iterator(); localIterator.hasNext();)
          {
            int guiz = ((Integer)localIterator.next()).intValue();
          msg = msg + dealData(erjzStr, startIndex, guiz, index, list_gz.size());
          startIndex += guiz;
          index++;
        }
      }
      return xiaoxId + "," + msg;
    }
    return msg;
  }
  
  private String getChuanbDynamicInfoShijzByErjz(String erjzStr)
  {
    String msg = "";
    int xiaoxId = StringUtils.binaryToDecimal(erjzStr.substring(0, 6));
    if (StringUtils.isValid(Constant.CHUANBDTSJ_XIAOXID_ARR, xiaoxId))
    {
      String[] arr = new String[8];
      List<Integer> list_gz = this.selectAISInfoMapper.getBitsmByXiaoxId(xiaoxId);
      int startIndex = 6;
      int index = 0;
      Object localObject;
      int guiz;
      int value;
      switch (xiaoxId)
      {
      case 1: 
      case 2: 
      case 3: 
        for (localObject = list_gz.iterator(); ((Iterator)localObject).hasNext();)
        {
          guiz = ((Integer)((Iterator)localObject).next()).intValue();
          if ((index == 1) || (index == 2) || (index == 9) || (index == 10))
          {
            value = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
            if (index == 1) {
              arr[0] = (value + ",");
            } else if (index == 2) {
              arr[1] = (value + ",");
            } else if (index == 9) {
              arr[2] = (value + ",");
            } else if (index == 10) {
              arr[3] = (value + ",");
            }
          }
          else if ((index == 4) || (index == 8))
          {
            value = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
            if (index == 4) {
              arr[4] = (StringUtils.formatDecimals(Double.valueOf(value).doubleValue() / 10.0D, 1) + ",");
            } else if (index == 8) {
              arr[5] = (StringUtils.formatDecimals(Double.valueOf(value).doubleValue() / 10.0D, 1) + ",");
            }
          }
          else if ((index == 6) || (index == 7))
          {
            value = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
            if (index == 6) {
              arr[6] = (StringUtils.formatDecimals(Double.valueOf(value).doubleValue() / 600000.0D, 4) + ",");
            } else if (index == 7) {
              arr[7] = (StringUtils.formatDecimals(Double.valueOf(value).doubleValue() / 600000.0D, 4) + "");
            }
          }
          startIndex += guiz;
          index++;
        }
        break;
      case 18: 
      case 19: 
        for (localObject = list_gz.iterator(); ((Iterator)localObject).hasNext();)
        {
          guiz = ((Integer)((Iterator)localObject).next()).intValue();
          if ((index == 1) || (index == 8) || (index == 9))
          {
            value = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
            if (index == 1) {
              arr[0] = (value + ",");
            } else if (index == 8) {
              arr[2] = (value + ",");
            } else if (index == 9) {
              arr[3] = (value + ",");
            }
          }
          else if ((index == 3) || (index == 7))
          {
            value = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
            if (index == 3) {
              arr[4] = (StringUtils.formatDecimals(Double.valueOf(value).doubleValue() / 10.0D, 1) + ",");
            } else if (index == 7) {
              arr[5] = (StringUtils.formatDecimals(Double.valueOf(value).doubleValue() / 10.0D, 1) + ",");
            }
          }
          else if ((index == 5) || (index == 6))
          {
            value = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
            if (index == 5) {
              arr[6] = (StringUtils.formatDecimals(Double.valueOf(value).doubleValue() / 600000.0D, 4) + ",");
            } else if (index == 6) {
              arr[7] = (StringUtils.formatDecimals(Double.valueOf(value).doubleValue() / 600000.0D, 4) + "");
            }
          }
          arr[1] = ",";
          startIndex += guiz;
          index++;
        }
        break;
      }
      for (String info : arr) {
        msg = msg + info;
      }
      return xiaoxId + "," + msg;
    }
    return msg;
  }
  
  private String getChuanbStaticInfoShijzByErjz(String erjzStr)
  {
    String msg = "";
    int xiaoxId = StringUtils.binaryToDecimal(erjzStr.substring(0, 6));
    if (StringUtils.isValid(Constant.CHUANBJTSJ_XIAOXID_ARR, xiaoxId))
    {
      String[] arr = new String[7];
      List<Integer> list_gz = this.selectAISInfoMapper.getBitsmByXiaoxId(xiaoxId);
      int startIndex = 6;
      int index = 0;
      Iterator localIterator;
      int value;
      int biaos;
      PageData param;
      switch (xiaoxId)
      {
      case 5: 
        for (localIterator = list_gz.iterator(); localIterator.hasNext();)
        {
          int guiz = ((Integer)localIterator.next()).intValue();
          if (index == 1)
          {
            value = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
            arr[0] = (value + "$");
          }
          else if ((guiz == 42) && (index == 4))
          {
            arr[1] = (dealData_Name(erjzStr, startIndex, guiz) + "$");
          }
          else if ((guiz == 120) && (index == 5))
          {
            arr[2] = (dealData_Name(erjzStr, startIndex, guiz) + "$");
          }
          else if ((guiz == 8) && (index == 6))
          {
            value = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
            arr[3] = (dealData_Chuanblx(value) + "$");
          }
          else if ((guiz == 30) && (index == 7))
          {
            String nameValue = erjzStr.substring(startIndex, startIndex + guiz);
            nameValue = dealData_Chic(nameValue);
            arr[4] = (nameValue.split("\\*")[0] + "$");
            arr[5] = (nameValue.split("\\*")[1] + "$");
          }
          else if ((guiz == 8) && (index == 10))
          {
            value = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
            arr[6] = (StringUtils.formatDecimals(Double.valueOf(value).doubleValue() / 10.0D, 1) + "");
          }
          startIndex += guiz;
          index++;
        }
        break;
      case 24: 
        biaos = StringUtils.bigBinaryToDecimal(erjzStr.substring(38, 40));
        list_gz = new ArrayList();
        param = new PageData();
        param.put("B_NB_XIAOXID", Integer.valueOf(xiaoxId));
        if (biaos == 0)
        {
          param.put("B_VC_XIAOXMC", "静态数据报告A部分");
          list_gz = this.selectAISInfoMapper.getBitsmByXiaoxIdAndMc(param);
          for (localIterator = list_gz.iterator(); localIterator.hasNext();)
          {
            int guiz = ((Integer)localIterator.next()).intValue();
            if (index == 1)
            {
              value = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
              arr[0] = (value + "$");
            }
            else if ((guiz == 120) && (index == 3))
            {
              arr[2] = (dealData_Name(erjzStr, startIndex, guiz) + "$");
            }
            arr[1] = "$";
            arr[3] = "$";
            arr[4] = "$";
            arr[5] = "$";
            arr[6] = "";
            startIndex += guiz;
            index++;
          }
        }
        else if (biaos == 1)
        {
          param.put("B_VC_XIAOXMC", "静态数据报告B部分");
          list_gz = this.selectAISInfoMapper.getBitsmByXiaoxIdAndMc(param);
          for (localIterator = list_gz.iterator(); localIterator.hasNext();)
          {
            int guiz = ((Integer)localIterator.next()).intValue();
            if (index == 1)
            {
              value = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
              arr[0] = (value + "$");
            }
            else if ((guiz == 8) && (index == 3))
            {
              value = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
              arr[3] = (dealData_Chuanblx(value) + "$");
            }
            else if ((guiz == 42) && (index == 5))
            {
              arr[1] = (dealData_Name(erjzStr, startIndex, guiz) + "$");
            }
            else if ((guiz == 30) && (index == 6))
            {
              String nameValue = erjzStr.substring(startIndex, startIndex + guiz);
              nameValue = dealData_Chic(nameValue);
              arr[4] = (nameValue.split("\\*")[0] + "$");
              arr[5] = (nameValue.split("\\*")[1] + "$");
            }
            arr[2] = "$";
            arr[6] = "";
            startIndex += guiz;
            index++;
          }
        }
        else
        {
          list_gz = new ArrayList();
        }
        break;
      }
      for (String info : arr) {
        msg = msg + info;
      }
      return xiaoxId + "$" + msg;
    }
    return msg;
  }
  
  private String dealData(String erjzStr, int startIndex, int guiz, int index, int size)
  {
    String msg = "";
    if (index == size - 1)
    {
      int value1 = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
      msg = msg + value1;
    }
    else if ((guiz == 28) || (guiz == 27))
    {
      int value1 = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
      msg = msg + StringUtils.formatDecimals(Double.valueOf(value1).doubleValue() / 600000.0D, 4) + ",";
    }
    else if (guiz == 10)
    {
      int value1 = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
      msg = msg + StringUtils.formatDecimals(Double.valueOf(value1).doubleValue() / 10.0D, 1) + ",";
    }
    else if (guiz == 12)
    {
      int value1 = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
      msg = msg + StringUtils.formatDecimals(Double.valueOf(value1).doubleValue() / 10.0D, 1) + ",";
    }
    else if ((guiz == 120) || (guiz == 312))
    {
      msg = msg + dealData_Name(erjzStr, startIndex, guiz);
    }
    else if (guiz == 42)
    {
      msg = msg + dealData_Name(erjzStr, startIndex, guiz);
    }
    else
    {
      int value1 = StringUtils.bigBinaryToDecimal(erjzStr.substring(startIndex, startIndex + guiz));
      msg = msg + value1 + ",";
    }
    return msg;
  }
  
  private String dealData_Name(String erjzStr, int startIndex, int guiz)
  {
    String msg = "";
    String value1 = erjzStr.substring(startIndex, startIndex + guiz);
    int length = value1.length() / 6;
    for (int i = 0; i < length; i++)
    {
      String nameValue = value1.substring(i * 6, (i + 1) * 6);
      String value = this.selectAISInfoMapper.getMubzByYuanz(nameValue);
      if (value.equals("Space")) {
        value = " ";
      } else if (value.equals("@")) {
        value = "";
      }
      if (i == length - 1) {
        msg = msg + value + "";
      } else {
        msg = msg + value;
      }
    }
    return msg;
  }
  
  private String dealData_Jingwd(int guiz, int value)
  {
    String msg = "";
    if ((guiz == 28) || (guiz == 27)) {
      msg = StringUtils.formatDecimals(Double.valueOf(value).doubleValue() / 600000.0D, 4) + "";
    }
    return msg;
  }
  
  private String dealData_Chuanblx(int value)
  {
    String msg = "";
    String head = String.valueOf(value).substring(0, 1);
    String content = String.valueOf(value).substring(1);
    if ("2".equals(head)) {
      msg = "0111";
    } else if ("3".equals(head))
    {
      if ("0".equals(content)) {
        msg = "0901";
      } else if ("1".equals(content)) {
        msg = "06";
      } else if ("2".equals(content)) {
        msg = "06";
      } else if ("3".equals(content)) {
        msg = "0501";
      } else if ("4".equals(content)) {
        msg = "0501";
      } else if ("5".equals(content)) {
        msg = "0905";
      } else if ("6".equals(content)) {
        msg = "0907";
      } else if ("7".equals(content)) {
        msg = "0909";
      }
    }
    else if ("4".equals(head)) {
      msg = "11";
    } else if ("5".equals(head))
    {
      if ("0".equals(content)) {
        msg = "0902";
      } else if ("1".equals(content)) {
        msg = "";
      } else if ("2".equals(content)) {
        msg = "06";
      } else if ("3".equals(content)) {
        msg = "0901";
      } else if ("4".equals(content)) {
        msg = "11";
      } else if ("5".equals(content)) {
        msg = "09";
      } else if ("6".equals(content)) {
        msg = "11";
      } else if ("7".equals(content)) {
        msg = "11";
      } else if ("8".equals(content)) {
        msg = "11";
      }
    }
    else if ("6".equals(head)) {
      msg = "01";
    } else if ("7".equals(head)) {
      msg = "02";
    } else if ("8".equals(head)) {
      msg = "03";
    } else if ("9".equals(head)) {
      msg = "11";
    }
    return msg;
  }
  
  private String dealData_Chic(String nameValue)
  {
    String msg = "";
    int length_1 = StringUtils.bigBinaryToDecimal(nameValue.substring(0, 9));
    int length_2 = StringUtils.bigBinaryToDecimal(nameValue.substring(9, 18));
    int width_1 = StringUtils.bigBinaryToDecimal(nameValue.substring(18, 24));
    int width_2 = StringUtils.bigBinaryToDecimal(nameValue.substring(24, 30));
    msg = msg + (length_1 + length_2) + "*" + (width_1 + width_2);
    return msg;
  }
  
  private String dealData_Gongys(String nameValue)
  {
    String msg = "";
    String zhizsIdBit = nameValue.substring(0, 18);
    String zhibId_1 = this.selectAISInfoMapper.getMubzByYuanz(zhizsIdBit.substring(0, 6));
    String zhibId_2 = this.selectAISInfoMapper.getMubzByYuanz(zhizsIdBit.substring(6, 12));
    String zhibId_3 = this.selectAISInfoMapper.getMubzByYuanz(zhizsIdBit.substring(12, 18));
    int daiwmsm = StringUtils.bigBinaryToDecimal(nameValue.substring(18, 22));
    String danwxlh = nameValue.substring(22, 42);
    msg = msg + zhibId_1 + zhibId_2 + zhibId_3 + "*" + daiwmsm + "*" + danwxlh + ",";
    return msg;
  }
  
  public int addGuzjl(PageData pd)
  {
    return this.guzjlMapper.add(pd);
  }
  
  public int updateGuzjl(PageData pd)
  {
    return this.guzjlMapper.updateGuzjl(pd);
  }
  
  public PageData getGuzStopTimeByIP(String ip)
  {
    return this.guzjlMapper.getGuzStopTimeByIP(ip);
  }
  
  public int updateJizzt(PageData pd)
  {
    return this.guzjlMapper.updateJizzt(pd);
  }
  
  public List<PageData> getJizInfoList()
  {
    return this.selectAISInfoMapper.getJizInfoList();
  }
  
  public List<PageData> getYicJizInfoList()
  {
    return this.selectAISInfoMapper.getYicJizInfoList();
  }
  
  public String getJizIdByIP(String ip)
  {
    return this.guzjlMapper.getJizIdByIP(ip);
  }
  
  /**
   * 根据船舶mmsi获取船舶信息
   * @param paramString
   * @return
   */
  public  PageData getChuanbInfoByMmsi(PageData pd) {
	  return this.guzjlMapper.getChuanbInfoByMmsi(pd);
  }
  
  /**
   * 根据获取的ais信息添加船舶历史数据
   * @param paramString
   * @return
   */
  public  int addChuanblswzByAis(PageData pd) {
	  return this.guzjlMapper.addChuanblswzByAis(pd);
  }
  
  /**
   * 根据获取的ais信息添加船舶基本信息
   * @param paramString
   * @return
   */
  public  int addChuanbjbxxByAis(PageData pd) {
	  return this.guzjlMapper.addChuanbjbxxByAis(pd);
  }
  
}
