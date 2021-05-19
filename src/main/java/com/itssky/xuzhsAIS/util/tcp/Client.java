package com.itssky.xuzhsAIS.util.tcp;

import com.itssky.xuzhsAIS.service.ChuanbsswzService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client
{
  private static final Logger log = LoggerFactory.getLogger(Client.class);
  private String host;
  private int port;
  private ChuanbsswzService chuanbsswzService;
  
  public Client(String host, int port, ChuanbsswzService chuanbsswzService)
  {
    this.host = host;
    this.port = port;
    this.chuanbsswzService = chuanbsswzService;
  }
  
  private ThreadLocal<Socket> threadConnect = new ThreadLocal();
  private Socket client;
  private OutputStream outStr = null;
  private InputStream inStr = null;
  
  public void connect()
    throws UnknownHostException, IOException
  {
    Thread tRecv = new Thread(new RecvThread(this.host, this.port));
    Thread tKeep = new Thread(new KeepThread(this.host, this.port));
    this.client = ((Socket)this.threadConnect.get());
    if (this.client == null)
    {
      this.client = new Socket(this.host, this.port);
      log.info("IP为：" + this.host + ",端口为：" + this.port + ",TCP连接成功！");
      this.threadConnect.set(this.client);
      tKeep.start();
      tRecv.start();
    }
    this.outStr = this.client.getOutputStream();
    this.inStr = this.client.getInputStream();
  }
  
  public void disconnect()
  {
    try
    {
      this.outStr.close();
      this.inStr.close();
      this.client.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public void main(String[] args) {}
  
  public class KeepThread
    implements Runnable
  {
    private String host;
    private int port;
    
    public KeepThread(String host, int port)
    {
      this.host = host;
      this.port = port;
    }
    
    public void run()
    {
      try
      {
        Client.log.info("IP为：" + this.host + ",端口为：" + this.port + ",开始发送心跳包！");
        for (;;)
        {
          try
          {
            Thread.sleep(10000L);
          }
          catch (InterruptedException e)
          {
            Client.log.warn("IP为：" + this.host + ",端口为：" + this.port + ",发送心跳数据包失败！");
            e.printStackTrace();
          }
          Client.log.info("来自IP为:" + this.host + "，端口为:" + this.port + ",持续发送心跳数据包");
          Client.this.outStr.write(("from HOST:" + this.host + ",PORT:" + this.port + ",send heart beat data package !").getBytes());
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  public class RecvThread
    implements Runnable
  {
    private String host;
    private int port;
    
    public RecvThread(String host, int port)
    {
      this.host = host;
      this.port = port;
    }
    
    long startTime_second = System.currentTimeMillis();
    long startTime_hour = System.currentTimeMillis();
    private List<String> list_second = new ArrayList();
    private List<String> list_hour = new ArrayList();
    
    public void run()
    {
      try
      {
        Client.log.info("IP为：" + this.host + ",端口为：" + this.port + ",开始接收数据！");
        for (;;)
        {
          byte[] b = new byte[1024];
          int r = Client.this.inStr.read(b);
          if (r > -1)
          {
            String str = new String(b);
            Client.log.info("接收自IP:" + this.host + ",端口为:" + this.port + "的数据为：" + str);
            this.list_second.add(str);
            this.list_hour.add(str);
          }
          long stopTime_second = System.currentTimeMillis();
          long stopTime_hour = System.currentTimeMillis();
          
          long intervalTime_second = (stopTime_second - this.startTime_second) / 1000L;
          if (intervalTime_second >= 10L)
          {
            Client.this.chuanbsswzService.batchAddData(this.list_second, this.host, this.port, "second");
            this.startTime_second = System.currentTimeMillis();
            this.list_second = new ArrayList();
          }
          long intervalTime_hour = (stopTime_hour - this.startTime_hour) / 1000L;
          if (intervalTime_hour >= 60L)
          {
            Client.this.chuanbsswzService.batchAddData(this.list_hour, this.host, this.port, "hour");
            this.startTime_hour = System.currentTimeMillis();
            this.list_hour = new ArrayList();
          }
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }
}
