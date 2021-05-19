package com.itssky.xuzhsAIS.netty.entity;

/**
 * 配置项
 * @author zhouh
 *
 */
public interface TypeData {

    //客户端代码
    byte PING = 1;

    //服务端代码
    byte PONG = 2;

    //顾客
    byte CUSTOMER = 3;
	
}
