package com.houseWang.tssAPI.exception;

public class NotLoginException extends Exception {

	@Override
	public String getMessage() {
		return "You should login first";
	}
}
