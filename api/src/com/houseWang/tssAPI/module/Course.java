package com.houseWang.tssAPI.module;

import java.util.ArrayList;

public class Course {
	private String cid;
	private String cname;
	private String term;
	private String modifyTime;
	private ArrayList<User> teachers = new ArrayList<User>();
	private ArrayList<User> assistents = new ArrayList<User>();

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public ArrayList<User> getTeachers() {
		return teachers;
	}

	public void setTeachers(ArrayList<User> teachers) {
		this.teachers = teachers;
	}

	public ArrayList<User> getAssistents() {
		return assistents;
	}

	public void setAssistents(ArrayList<User> assistents) {
		this.assistents = assistents;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Override
	public String toString() {
		String ts = "";
		for (User u : teachers) {
			ts += u.getUname() + " ";
		}
		String as = "";
		for (User u : assistents) {
			as += u.getUname() + " ";
		}
		return "Course [cid=" + cid + ", cname=" + cname + ", term=" + term
				+ ", modifyTime=" + modifyTime + ", teachers=" + ts
				+ ", assistents=" + as + "]";
	}

}
