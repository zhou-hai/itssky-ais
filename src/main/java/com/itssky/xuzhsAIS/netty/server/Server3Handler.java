package com.itssky.xuzhsAIS.netty.server;

import com.itssky.xuzhsAIS.netty.Middleware;
import com.itssky.xuzhsAIS.netty.entity.Model;
import com.itssky.xuzhsAIS.netty.entity.TypeData;

import io.netty.channel.ChannelHandlerContext;

/**
 * 服务端控制器
 *
 * @author zhouh
 * @date 2019/12/19
 **/
public class Server3Handler extends Middleware{
	public Server3Handler() {
        super("server");
        // TODO Auto-generated constructor stub
    }
    @Override
    protected void handlerData(ChannelHandlerContext ctx, Object msg,String host) {
        // TODO Auto-generated method stub
        Model model  = (Model) msg;
        System.out.println("server 接收数据 ： " +  model.toString());
        model.setType(TypeData.CUSTOMER);
        model.setBody("client你好，server已接收到数据："+model.getBody());
        ctx.channel().writeAndFlush(model);
        System.out.println("server 发送数据： " + model.toString());
    }
    @Override
    protected void handlerReaderIdle(ChannelHandlerContext ctx) {
        // TODO Auto-generated method stub
        super.handlerReaderIdle(ctx);
        System.err.println(" ---- client "+ ctx.channel().remoteAddress().toString() + " reader timeOut, --- close it");
        ctx.close();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        System.err.println( name +"  exception" + cause.toString());
    }

}
