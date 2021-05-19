package com.itssky.xuzhsAIS.netty.entity;

import java.nio.charset.StandardCharsets;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * 解码器
 *
 * @author zhouh
 * @date 2019/12/19
 **/
public class MsgPckDecode extends MessageToMessageDecoder<ByteBuf>{

	@Override
	 protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        list.add(new String(data, StandardCharsets.UTF_8).trim().replaceAll("\n",""));
    }
	
}
