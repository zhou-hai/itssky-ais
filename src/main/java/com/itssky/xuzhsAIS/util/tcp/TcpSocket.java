package com.itssky.xuzhsAIS.util.tcp;

import com.itssky.xuzhsAIS.service.ChuanbsswzService;
import java.io.IOException;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpSocket
{
  private static final Logger log = LoggerFactory.getLogger(TcpSocket.class);
  
  public void initTCPConnect(ChuanbsswzService chuanbsswzService)
  {
    init(com.itssky.xuzhsAIS.common.Constant.TCP_SOCKET_HOST_ARR[0], 8001, chuanbsswzService);
    init(com.itssky.xuzhsAIS.common.Constant.TCP_SOCKET_HOST_ARR[0], 9001, chuanbsswzService);
  }
  
  private void init(String host, int port, ChuanbsswzService chuanbsswzService)
  {
    log.info("开始初始化IP为：" + host + ",端口为：" + port + "的TCP连接...");
    Client client = new Client(host, port, chuanbsswzService);
    try
    {
      client.connect();
    }
    catch (UnknownHostException e)
    {
      log.warn("IP为：" + host + ",端口为：" + port + ",TCP连接失败！");
      e.printStackTrace();
    }
    catch (IOException e)
    {
      log.warn("IP为：" + host + ",端口为：" + port + ",TCP连接失败！");
      e.printStackTrace();
    }
  }
}
