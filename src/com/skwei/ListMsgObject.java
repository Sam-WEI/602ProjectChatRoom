package com.skwei;

import java.io.Serializable;
import java.util.ArrayList;

public class ListMsgObject extends DataObject implements Serializable{
	private ArrayList<String> userList;

	public ArrayList<String> getUserList() {
		return userList;
	}

	public void setUserList(ArrayList<String> userList) {
		this.userList = userList;
	}


}
