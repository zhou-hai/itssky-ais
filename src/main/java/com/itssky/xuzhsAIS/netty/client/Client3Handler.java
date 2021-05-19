package com.itssky.xuzhsAIS.netty.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import com.itssky.xuzhsAIS.entity.PageData;
import com.itssky.xuzhsAIS.netty.Middleware;
import com.itssky.xuzhsAIS.service.ChuanbsswzService;
import com.itssky.xuzhsAIS.util.IDUtils;
import dk.tbsalling.aismessages.ais.messages.AISMessage;
import dk.tbsalling.aismessages.ais.messages.types.ShipType;
import dk.tbsalling.aismessages.nmea.messages.NMEAMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * 客户端控制器
 *
 * @author zhaohualuo
 * @date 2019/12/19
 **/
public class Client3Handler extends Middleware{
	
	private static final Logger log = LoggerFactory.getLogger(Client3Handler.class);

	private Client client;

    public Client3Handler(Client client) {
        super(client.getHost());
        this.client = client;
    }

    @Override
    protected void handlerData(ChannelHandlerContext ctx, Object msg,String host) {
    	try {
            AISMessage aISMessage = AISMessage.create(NMEAMessage.fromString(msg.toString().trim().replaceAll("\n","")));
            int messageType = aISMessage.getMessageType().getCode();
            String mmsi = String.valueOf(aISMessage.getSourceMmsi().getMMSI());
            Map<String, Object> dataFields = aISMessage.dataFields();
//            System.out.println(aISMessage.getMessageType().getCode());
//            System.out.println(aISMessage.getSourceMmsi().getMMSI());
//            System.out.println(aISMessage.dataFields());
            
            switch(messageType){
            	//1、2、3、18、19-动态船舶数据
	            case 1 :
	            case 2 :
	            case 3 :
	            case 18 :
	            case 19 :
	            	dealDynamicInfo(mmsi,dataFields);
	               break; 
	            //5、24-A、24-B-静态船舶数据
	            case 5 :
	            case 24 :
	            	dealStaticInfo(mmsi,dataFields);
		           break; 
	            default : 
	            	log.info("地址：" + name + "client  收到数据： " + msg+",消息类型为："+messageType+",不在解析范围内，忽略处理。");
	        }
            
        } catch (Throwable t) {
        	t.printStackTrace();
        	log.error("地址：" + name + "client  收到数据： " + msg+",解析失败！"+t );
        }
    }
    
    /**
     * 处理船舶动态数据-本步骤里面应该包含将获取到的船舶数据存入VITSAIS.B_HXJK_CHUANBSSWZAIS，
     * 为的是调用北京视酷的接口，获取船舶基本信息，但是北京视酷的接口因徐州海事服务器外网禁用，故暂时不存了
     * @param mmsi
     * @param dataFields
     * @return
     */
    int dealDynamicInfo(String mmsi,Map<String, Object> dataFields) {
    	int total = 0;
    	try {
	    	PageData out = new PageData();
	    	PageData param = new PageData();
	    	//纬度
	    	double latitude = StringUtils.isEmpty(dataFields.get("latitude")) ? 0 :  Double.valueOf(dataFields.get("latitude").toString());
	    	//经度
	    	double longitude = StringUtils.isEmpty(dataFields.get("longitude")) ? 0 :  Double.valueOf(dataFields.get("longitude").toString());
	    	//航速
	    	double speedOverGround = StringUtils.isEmpty(dataFields.get("speedOverGround")) ? 0 : Double.valueOf(dataFields.get("speedOverGround").toString());
	    	//实际航向
	    	int trueHeading = StringUtils.isEmpty(dataFields.get("trueHeading")) ? 0 : Integer.valueOf(dataFields.get("trueHeading").toString());
	    	out.put("MMSI", mmsi);
	    	out.put("JINGD", longitude);
	    	out.put("WEID", latitude);
	    	out.put("HANGX", trueHeading);
	    	out.put("HANGS", speedOverGround);
	    	param.put("mmsi", mmsi);
	    	
	    	String mmsi_out = mmsi;
	    	
	    	//1、根据mmsi匹配船舶基本信息（因业务系统需要，船舶识别号必须要有，否则业务执行不下去）
	    	ChuanbsswzService chuanbsswzService = client.getChuanbsswzService();
	    	PageData info = chuanbsswzService.getChuanbInfoByMmsi(param);
	    	if(info != null) {
	    		out.put("CHUANBSBH", info.get("CHUANBSBH"));
	    		out.put("CHUANBDJH", info.get("CHUANBDJH"));
	        	out.put("CHUANBMC", info.get("CHUANBMC"));
	        	out.put("CHUANBYWMC", info.get("CHUANBYWMC"));
	        	out.put("CHUANBZL", info.get("CHUANBZL"));
	    	}else {
	    		mmsi_out = mmsi + "_@nomatch";
	    		//如果匹配不到船舶基本信息，则记录下来
	    		//本来这里应该是根据mmsi去请求北京视酷接口，获取船舶基本信息存入船舶基本信息表中的
	    		log.error("地址：" + name + "client  ，将实时信息为： " + out + " ，匹配不到船舶基本信息，采用船舶mmsi当成船舶识别号存入！");
	    		//2021年4月29日中午由谭总和丁帅讨论，暂时先把船舶mmsi当成船舶识别号存入基本信息表中
	    		out.put("CHUANBSBH", mmsi_out);
	        	out.put("CHUANBMC", "-");
	        	out.put("CHUANBYWMC", "-");
	        	out.put("CHUANBZL", "11");//默认都设置成其他
	        	
	        	PageData jbxx = new PageData();
	        	jbxx.put("CHUANBSBH", mmsi_out);
	        	jbxx.put("CHUANBMC", "-");
	        	jbxx.put("CHUANBZL", "11");
	        	jbxx.put("MMSIBH", mmsi);
	        	
	        	//2、添加船舶基本信息(如果不添加基本信息，则地图上虽然会展示船舶定位，但是业务执行不了)
	        	chuanbsswzService.addChuanbjbxxByAis(jbxx);
	    	}
        	//2、向船舶历史位置表中插入数据
        	total = chuanbsswzService.addChuanblswzByAis(out);
        	if(total > 0 ) {
        		log.info("地址：" + name + "client  ，将mmsi为： " + mmsi_out + " ，船舶动态实时位置信息-入库成功！");
        	}else {
        		log.error("地址：" + name + "client  ，将mmsi为： " + mmsi_out + " ，船舶动态实时位置信息-入库失败！");
        	}
    	} catch (Throwable t) {
        	t.printStackTrace();
        	log.error("地址：" + name + "client  ，mmsi为： " + mmsi + " ，操作船舶动态数据库异常！"+t);
        }
    	
    	return total;
    }
    
    /**
     * 处理静态信息
     * @return
     */
    int dealStaticInfo(String mmsi,Map<String, Object> dataFields) {
    	int total = 0;
    	try {
	    	PageData out = new PageData();
	    	PageData param = new PageData();
	    	//船舶英文名称
	    	String shipName = StringUtils.isEmpty(dataFields.get("shipName")) ? "-" :  dataFields.get("shipName").toString();
	    	//呼号
	    	String callsign = StringUtils.isEmpty(dataFields.get("callsign")) ? "-" :  dataFields.get("callsign").toString();
	    	//船舶类型
	    	String shipType = StringUtils.isEmpty(dataFields.get("shipType")) ? "-" :  dataFields.get("shipType").toString();
	    	
	    	//1、根据mmsi匹配船舶基本信息（确定数据库里面是否存在改船基本信息）
	    	ChuanbsswzService chuanbsswzService = client.getChuanbsswzService();
	    	param.put("mmsi", mmsi);
	    	PageData info = chuanbsswzService.getChuanbInfoByMmsi(param);
	    	if(info == null) {
	    		
	    		for (ShipType b : ShipType.values()) {
					if (shipType.equals(b.getValue())) {
						shipType = b.getCode().toString();
					}
				}
	    		
	    		//船舶类型转成自定义类型
	    		if(!"".equals(shipType)) {
	    			if(shipType.substring(0,1).equals("6")) {
	    				shipType = "01";
	    			}else if(shipType.substring(0,1).equals("7")) {
	    				shipType = "02";
	    			}else if(shipType.substring(0,1).equals("8")) {
	    				shipType = "03";
	    			}else if(shipType.equals("52") || shipType.equals("31") || shipType.equals("32")) {
	    				shipType = "06";
	    			}else if(shipType.equals("55")) {
	    				shipType = "09";
	    			}else {
	    				shipType = "11";
	    			}
	    		}
	    		
	    		String chuanbsbh = "CN" + IDUtils.getGUID() + "_@3";
	        	out.put("CHUANBSBH", chuanbsbh);
	        	out.put("CHUANBYWMC", shipName);
	        	out.put("CHUANBMC", "(无船名)");
	        	out.put("HUH", callsign);
	        	out.put("CHUANBZL", shipType);
	        	out.put("MMSIBH", mmsi);
	        	
	        	//2、添加船舶基本信息
	        	total = chuanbsswzService.addChuanbjbxxByAis(out);
	        	if(total > 0 ) {
	        		log.info("地址：" + name + "client  ，将mmsi为： " + mmsi + " ，船舶静态信息-入库成功！");
	        	}else {
	        		log.error("地址：" + name + "client  ，将mmsi为： " + mmsi + " ，船舶静态信息-入库失败！");
	        	}
	    	}
        } catch (Throwable t) {
        	t.printStackTrace();
        	log.error("地址：" + name + "client  ，mmsi为： " + mmsi + " ，操作船舶基本数据库异常！"+t);
        }
    	return total;
    }
    
    @Override
    protected void handlerAllIdle(ChannelHandlerContext ctx) {
        // TODO Auto-generated method stub
        super.handlerAllIdle(ctx);
        sendPingMsg(ctx);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        super.channelInactive(ctx);
        client.doConnect();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	log.error("地址：" + name + "exception :"+ cause.toString());
    }

	
}
