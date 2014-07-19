package com.houseWang.tssAPI.module;

public class TSS {
	
	private static TSS instance = null;
	
	private String userName = null;
	private String password = null;
	private String sessionId = null;
	
	private TSS() {
		
	}
	
	public static TSS getInstance() {
		if(instance != null) {
			instance = new TSS();
		}
		return instance;
	}
	
	
	
}
