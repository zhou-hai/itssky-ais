package com.itssky.xuzhsAIS.netty.server;

import com.itssky.xuzhsAIS.netty.entity.MsgPckDecode;
import com.itssky.xuzhsAIS.netty.entity.MsgPckEncode;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 服务端
 *
 * @author zhouh
 * @date 2019/12/19
 **/
public class Server {
	public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(8089)
                    .childHandler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            // TODO Auto-generated method stub
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(10,3,10));
                            pipeline.addLast(new MsgPckDecode());
                            pipeline.addLast(new MsgPckEncode());
                            pipeline.addLast(new Server3Handler());
                        }
                    });
            System.out.println("start server 8089 --");
            ChannelFuture sync = serverBootstrap.bind().sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            //优雅的关闭资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
