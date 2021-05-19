package com.itssky.xuzhsAIS.netty.client;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.itssky.xuzhsAIS.entity.PageData;
import com.itssky.xuzhsAIS.netty.entity.Model;
import com.itssky.xuzhsAIS.netty.entity.MsgPckDecode;
import com.itssky.xuzhsAIS.netty.entity.MsgPckEncode;
import com.itssky.xuzhsAIS.netty.entity.TypeData;
import com.itssky.xuzhsAIS.service.ChuanbsswzService;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

@Component("client")
public class Client{
	
	private static final Logger log = LoggerFactory.getLogger(Client.class);

	private NioEventLoopGroup worker = new NioEventLoopGroup();

    private Channel channel;

    private Bootstrap bootstrap;

    boolean flag = true;
    
    private String host;
    
    private int port;
    
    private ChuanbsswzService chuanbsswzService ;
    
    //构造函数
    public void initClient(String host, int port,ChuanbsswzService chuanbsswzService) {
		this.host = host;
		this.port = port;
		this.chuanbsswzService = chuanbsswzService;
	}

    public void close() {
        channel.close();
        worker.shutdownGracefully();
    }

    public void start() {
        bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        // TODO Auto-generated method stub
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new IdleStateHandler(3, 3, 5));

                        pipeline.addLast(new MsgPckDecode());

                        pipeline.addLast(new MsgPckEncode());

                        pipeline.addLast(new Client3Handler(Client.this));
                    }
                });
        doConnect();
    }

    /**
     * 连接服务端 and 重连
     */
    protected void doConnect() {

        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture connect = bootstrap.connect(host, port);
        //实现监听通道连接的方法
        connect.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                	log.info("地址：" + host +"," + port + ",连接成功...");
                    channel = channelFuture.channel();
                    //连接成功，则将之前的基站故障记录设置故障结束时间
                    PageData param = new PageData();
                    param.put("IP", host);
                    chuanbsswzService.updateGuzjl(param);
                    param.put("B_NB_JIZZT", Integer.valueOf(0));
                    param.put("B_VC_JIZIP", host);
                    chuanbsswzService.updateJizzt(param);
                } else {
                	log.error("地址：" + host +"," + port + ",连接失败...");
                	//连接失败，则进行更新
                	dealData_guzjl();
                    if (flag) {
                        System.out.println("地址："+host+"," + port +"，每隔5s重连....");
                        channelFuture.channel().eventLoop().schedule(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                doConnect();
                            }
                        }, 5, TimeUnit.SECONDS);
                    }
                }
            }
        });
    }

    /**
     * 向服务端发送消息
     */
    public void sendData() {
    	//创建连接成功之前停在这里等待
        while (channel == null || !channel.isActive()) {
            log.debug("地址：" + host + "," + port + ",等待连接...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("地址：" + host + "," + port + ",连接成功,向服务端发送消息...");
        flag = true;
        Scanner sc = new Scanner(System.in);
        while (flag) {
            String nextLine = sc.nextLine();
            if ("end".equalsIgnoreCase(nextLine)) {
                flag = false;
            }
            Model model = new Model();
            model.setType(TypeData.CUSTOMER);
            model.setBody(nextLine);
            channel.writeAndFlush(model);
        }
    }
    
    //处理异常故障开始时间
    private void dealData_guzjl(){
      PageData info = chuanbsswzService.getGuzStopTimeByIP(this.host);
      if (info != null)
      {
        if (info.get("B_DT_GUZJSSJ") != null)
        {
          PageData param = new PageData();
          param.put("B_VC_JIZID", info.get("B_VC_JIZID"));
          param.put("B_VC_GUZDM", "10001");
          
          chuanbsswzService.addGuzjl(param);
          
          param.put("B_NB_JIZZT", Integer.valueOf(1));
          param.put("B_VC_JIZIP", this.host);
          chuanbsswzService.updateJizzt(param);
        }
      }
      else
      {
        PageData param = new PageData();
        param.put("B_VC_JIZID", chuanbsswzService.getJizIdByIP(this.host));
        param.put("B_VC_GUZDM", "10001");
        
        chuanbsswzService.addGuzjl(param);
        
        param.put("B_NB_JIZZT", Integer.valueOf(1));
        param.put("B_VC_JIZIP", this.host);
        chuanbsswzService.updateJizzt(param);
      }
    }
    
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public ChuanbsswzService getChuanbsswzService() {
		return chuanbsswzService;
	}

	public void setChuanbsswzService(ChuanbsswzService chuanbsswzService) {
		this.chuanbsswzService = chuanbsswzService;
	}

	@Override
	public String toString() {
		return "Client [worker=" + worker + ", channel=" + channel + ", bootstrap=" + bootstrap + ", flag=" + flag
				+ ", host=" + host + ", port=" + port + ", chuanbsswzService=" + chuanbsswzService + "]";
	}

	public static void main(String[] args) {
//        Client client = new Client("127.0.0.1", 8089);
//        client.start();
//        //client.sendData();
//		//通信结束，关闭客户端
//        client.close();
    }
	
}
