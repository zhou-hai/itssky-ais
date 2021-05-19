package com.itssky.xuzhsAIS.netty.entity;

import java.io.Serializable;

import org.msgpack.annotation.Message;

/**
 * 消息类型分离器
 *
 * @author zhouh
 * @date 2019/12/19
 **/
@Message
public class Model implements Serializable{

	private static final long serialVersionUID = 1L;

    //类型
    private int type;

    //内容
    private String body;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Model{" +
                "type=" + type +
                ", body='" + body + '\'' +
                '}';
    }
	
}
