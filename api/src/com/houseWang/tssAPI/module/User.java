package com.houseWang.tssAPI.module;

public class User {
	private String uid;
	private String uname;
	
//	wsy 7.23
	public User(String uid, String uname){
		this.uid=uid;
		this.uname=uname;
	}
	
	@Override
	public String toString(){
		return uname+": "+uid;
	}
	
	public User(){}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

}
