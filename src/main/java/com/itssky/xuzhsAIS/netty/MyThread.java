package com.itssky.xuzhsAIS.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.itssky.xuzhsAIS.netty.client.Client;
import com.itssky.xuzhsAIS.service.ChuanbsswzService;
import com.itssky.xuzhsAIS.util.SpringBeanUtil;
import com.itssky.xuzhsAIS.util.SpringUtil;

public class MyThread extends Thread {
	
	private static final Logger log = LoggerFactory.getLogger(MyThread.class);
	
	private String host;
	
	private int port;
	
	public MyThread(String host,int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void run() {
		Object[] args = new Object[1];
		Object[] args1 = new Object[1];
		args[0] = "11";
		args1[0] = "11";
		ChuanbsswzService chuanbsswzService = SpringBeanUtil.getBean(ChuanbsswzService.class);
		Client client = new Client();
		client.initClient(host, port,chuanbsswzService);
		log.info(host+client.toString());
        client.start();
        client.sendData();
		//通信结束，关闭客户端
        client.close();
		System.out.println("host:"+host);
	}
}
