package com.houseWang.tssAPI.module;

public class Courseware {
	public static final int FILE = 0;
	public static final int FOLDER = 1;
	private String name;
	private int type;
	private int size;
	private String time;
	private String path;
	private String url;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Courseware [name=" + name + ", type=" + type + ", size=" + size
				+ ", time=" + time + ", path=" + path + ", url=" + url + "]";
	}

}
