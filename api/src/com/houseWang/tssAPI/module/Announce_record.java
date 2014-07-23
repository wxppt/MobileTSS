package com.houseWang.tssAPI.module;

public class Announce_record {
	private String timeStr;
	private String title;
	private String content;
	public Announce_record(String ts, String ti, String con){
		timeStr=ts;
		title=ti;
		content=con;
	}
	@Override
	public String toString(){
		return timeStr+" "+title+" "+content+"\n";
	}
	
	public String getTimeStr() {
		return timeStr;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getContent() {
		return content;
	}
//	public void setTimeStr(String timeStr) {
//		this.timeStr = timeStr;
//	}
//
//	public void setTitle(String title) {
//		this.title = title;
//	}
//
//	public void setContent(String content) {
//		this.content = content;
//	}
}
