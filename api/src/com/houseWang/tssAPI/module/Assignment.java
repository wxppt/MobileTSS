package com.houseWang.tssAPI.module;

public class Assignment {
	public static enum Status{
		DDL_OVER("Deadline is over"),
		HAS_SUBMITTED("Has submitted homework!"),
		HAS_NOT_SUBMITTED("Has not submitted homework!"),
		PERMISSION_DENIED("Can not check the status!");
		 private String context;
	     public String getContext(){
	    	 return this.context;
	     }
	     private Status(String context){
	    	 this.context = context;
	     }
	}
	private String id;
	private String deadline;
	private Courseware file;
	private String content;
	private Status status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public Courseware getFile() {
		return file;
	}

	public void setFile(Courseware file) {
		this.file = file;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
