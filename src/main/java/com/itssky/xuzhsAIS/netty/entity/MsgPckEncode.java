package com.itssky.xuzhsAIS.netty.entity;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 *
 * @author zhouh
 * @date 2019/12/19
 **/
public class MsgPckEncode extends MessageToByteEncoder<Object>{

	@Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf buf)
            throws Exception {
        // TODO Auto-generated method stub
        MessagePack pack = new MessagePack();

        byte[] write = pack.write(msg);

        buf.writeBytes(write);
    }
}
