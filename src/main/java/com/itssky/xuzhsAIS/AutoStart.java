package com.itssky.xuzhsAIS;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.itssky.xuzhsAIS.entity.PageData;
import com.itssky.xuzhsAIS.netty.client.Client;
import com.itssky.xuzhsAIS.service.ChuanbsswzService;

/**
 * 描述:项目启动,自动执行类
 * @author zhouh
 * @create 2018-09-29 14:43
 */
@Component
public class AutoStart implements CommandLineRunner{
	
	private static final Logger log = LoggerFactory.getLogger(AutoStart.class);
	
	@Autowired
    private ChuanbsswzService chuanbsswzService ;
	
    @Override
    public void run(String... strings) throws Exception {
    	log.info("系统启动，自动执行初始化Tcp-Socket连接！");
    	List<PageData> list = chuanbsswzService.getJizInfoList();
        for (int i=0;i<list.size();i++){
        	final int j=i; 
        	final String ip = list.get(j).getString("B_VC_JIZIP");
        	final int port = Integer.valueOf(list.get(j).get("B_NB_JIZAISDK").toString());
        	final Client client = new Client();
        	Thread t = new Thread() {
        		@Override
        		public void run(){
        			synMethod(client,ip,port);
	      		}
        	};
        	t.start();
        }
    }
    
    //synchronized
    void synMethod(Client client,String ip,int port) {
    	client.initClient(ip, port,chuanbsswzService);
		log.info(ip+client.toString());
        client.start();
        client.sendData();
		//通信结束，关闭客户端
        client.close();
    }
}
