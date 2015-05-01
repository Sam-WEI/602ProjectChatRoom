package com.skwei;

import java.io.Serializable;

public class DataObject implements Serializable{
	private String message;

	public DataObject() {
	}

	public DataObject(String message) {
		setMessage(message);
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
