package com.itssky.xuzhsAIS.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itssky.xuzhsAIS.netty.entity.Model;
import com.itssky.xuzhsAIS.netty.entity.TypeData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 公用控制器
 *
 * @author zhaohualuo
 * @date 2019/12/19
 **/
public abstract class Middleware extends ChannelInboundHandlerAdapter {
	
	private static final Logger log = LoggerFactory.getLogger(ChannelInboundHandlerAdapter.class);

	//host
	protected String name;
    //记录次数
    private int heartbeatCount = 0;

    //获取server and client 传入的值
    public Middleware(String name) {
        this.name = name;
    }
    /**
     *继承ChannelInboundHandlerAdapter实现了channelRead就会监听到通道里面的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
//        Model m = (Model) msg;
//        int type = m.getType();
//        switch (type) {
//            case 1:
//                sendPongMsg(ctx);
//                break;
//            case 2:
//                System.out.println(name + " get  pong  msg  from" + ctx.channel().remoteAddress());
//                break;
//            case 3:
//                handlerData(ctx,msg);
//                break;
//            default:
//                break;
//        }
    	handlerData(ctx,msg,name);
    }

    protected abstract void handlerData(ChannelHandlerContext ctx,Object msg,String host);

    protected void sendPingMsg(ChannelHandlerContext ctx){
        Model model = new Model();

        model.setType(TypeData.PING);

        ctx.channel().writeAndFlush(model);

        heartbeatCount++;

        log.debug("地址：" + name + " send ping msg to " + ctx.channel().remoteAddress() + ",count :" + heartbeatCount);
    }

//    private void sendPongMsg(ChannelHandlerContext ctx) {
//
//        Model model = new Model();
//
//        model.setType(TypeData.PONG);
//
//        ctx.channel().writeAndFlush(model);
//
//        heartbeatCount++;
//
//        System.out.println(name +" send pong msg to "+ctx.channel().remoteAddress() +" , count :" + heartbeatCount);
//    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        IdleStateEvent stateEvent = (IdleStateEvent) evt;

        switch (stateEvent.state()) {
            case READER_IDLE:
                handlerReaderIdle(ctx);
                break;
            case WRITER_IDLE:
                handlerWriterIdle(ctx);
                break;
            case ALL_IDLE:
                handlerAllIdle(ctx);
                break;
            default:
                break;
        }
    }

    protected void handlerAllIdle(ChannelHandlerContext ctx) {
        //log.error("地址：" + name + "---ALL_IDLE---");
    }

    protected void handlerWriterIdle(ChannelHandlerContext ctx) {
        //log.error("地址：" + name + "---WRITER_IDLE---");
    }

    protected void handlerReaderIdle(ChannelHandlerContext ctx) {
        //log.error("地址：" + name + "---READER_IDLE---");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("地址：" + name + " ---"+ctx.channel().remoteAddress() +"----- 上线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	log.debug("地址：" + name + " ---"+ctx.channel().remoteAddress() +"----- 离线");
    }

	
}
