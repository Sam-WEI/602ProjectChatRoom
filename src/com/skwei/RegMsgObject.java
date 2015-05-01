package com.skwei;

import java.io.Serializable;

public class RegMsgObject extends DataObject implements Serializable{
	
	public static final int MSGTYPE_REGISTER = 1;
	public static final int MSGTYPE_EXIT = -1;
	public static final int MSGTYPE_REGISTER_SUCCEED = 10;
	public static final int MSGTYPE_USERNAME_OCCUPIED = 11;
	private int msgType;
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	
}
