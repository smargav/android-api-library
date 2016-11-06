package com.smargav.api.execptions;

import com.google.gson.GsonBuilder;

public class NetException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object object;

	public NetException() {

	}

	public NetException(String msg) {
		super(msg);
	}

	public NetException(String msg, Object printObject) {
		super(msg);
		object = printObject;
	}

	public void print() {
		System.out.println(new GsonBuilder().setPrettyPrinting().create()
				.toJson(object));
		printStackTrace();

	}
}
