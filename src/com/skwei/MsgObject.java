package com.skwei;

import java.io.Serializable;

public class MsgObject extends DataObject implements Serializable{
	private String toWhom;//if null, say to all.
	private String fromWhom;
	private long timeStamp;
	
	public String getToWhom() {
		return toWhom;
	}

	public void setToWhom(String privateToWhom) {
		this.toWhom = privateToWhom;
	}

	public String getFromWhom() {
		return fromWhom;
	}

	public void setFromWhom(String from) {
		this.fromWhom = from;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
}
