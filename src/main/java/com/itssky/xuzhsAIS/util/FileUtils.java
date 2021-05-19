package com.itssky.xuzhsAIS.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileUtils
{
  public static boolean createTxtFile(List<Object[]> rows, String filePath, String fileName)
  {
    boolean flag = true;
    try
    {
      String fullPath = filePath + File.separator + fileName + ".txt";
      
      File file = new File(fullPath);
      if (file.exists()) {
        file.delete();
      }
      file = new File(fullPath);
      file.createNewFile();
      

      NumberFormat formatter = NumberFormat.getNumberInstance();
      formatter.setMaximumFractionDigits(10);
      

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      

      PrintWriter pfp = new PrintWriter(file, "UTF-8");
      for (Object[] rowData : rows)
      {
        StringBuffer thisLine = new StringBuffer("");
        for (int i = 0; i < rowData.length; i++)
        {
          Object obj = rowData[i];
          

          String field = "";
          if (null != obj)
          {
            if (obj.getClass() == String.class) {
              field = (String)obj;
            } else if ((obj.getClass() == Double.class) || (obj.getClass() == Float.class)) {
              field = formatter.format(obj);
            } else if ((obj.getClass() == Integer.class) || (obj.getClass() == Long.class) || 
              (obj.getClass() == Short.class) || (obj.getClass() == Byte.class)) {
              field = field + obj;
            } else if (obj.getClass() == Date.class) {
              field = sdf.format(obj);
            }
          }
          else {
            field = " ";
          }
          if (i < rowData.length - 1) {
            thisLine.append(field).append("\t");
          } else {
            thisLine.append(field);
          }
        }
        pfp.print(thisLine.toString() + "\n");
      }
      pfp.close();
    }
    catch (Exception e)
    {
      flag = false;
      e.printStackTrace();
    }
    return flag;
  }
  
  public static boolean createXlsFile(List<Object[]> rows, String filePath, String fileName)
  {
    boolean flag = true;
    try
    {
      XSSFWorkbook wb = new XSSFWorkbook();
      

      XSSFSheet sheet = wb.createSheet(fileName);
      for (int i = 0; i < rows.size(); i++)
      {
        Object[] rowData = (Object[])rows.get(i);
        XSSFRow row = sheet.createRow(i);
        for (int j = 0; j < rowData.length; j++)
        {
          XSSFCell cell = row.createCell(j);
          if (rowData[j].getClass() == String.class) {
            cell.setCellValue((String)rowData[j]);
          } else if (rowData[j].getClass() == Double.TYPE) {
            cell.setCellValue(((Double)rowData[j]).doubleValue());
          } else if (rowData[j].getClass() == Integer.TYPE) {
            cell.setCellValue(((Integer)rowData[j]).intValue());
          }
        }
      }
      String fullPath = filePath + File.separator + fileName + ".xls";
      File file = new File(fullPath);
      if (!file.getParentFile().exists()) {
        file.getParentFile().mkdirs();
      }
      if (file.exists()) {
        file.delete();
      }
      file = new File(fullPath);
      file.createNewFile();
      FileOutputStream fileOut = new FileOutputStream(file);
      wb.write(fileOut);
      fileOut.close();
    }
    catch (Exception e)
    {
      flag = false;
      e.printStackTrace();
    }
    return flag;
  }
  
  public static boolean createSingleTxtFile(String content, String filePath, String fileName)
  {
	  boolean flag = true;
    BufferedWriter out = null;
    try
    {
      File file = createFile(filePath + fileName + ".txt");
      out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
      if ((!file.exists()) && (!file.isDirectory())) {
        file.mkdirs();
      }
      out.write(content + "\r\n");
      return flag;
    }
    catch (IOException e)
    {
      flag = false;
      e.printStackTrace();
    }
    finally
    {
      try
      {
        if (out != null) {
          out.close();
        }
      }
      catch (IOException e)
      {
        flag = false;
        e.printStackTrace();
      }
    }
	return flag;
  }
  
  public static File createFile(String path)
    throws IOException
  {
    File file = new File(path);
    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }
    file.createNewFile();
    return file;
  }
  
  public static void clearInfoForFile(String fileName)
  {
    File file = new File(fileName);
    try
    {
      if (!file.exists()) {
        createFile(fileName);
      }
      FileWriter fileWriter = new FileWriter(file);
      fileWriter.write("");
      fileWriter.flush();
      fileWriter.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args)
  {
    clearInfoForFile("d:/AIS/test/ais_5min.txt");
  }
}
