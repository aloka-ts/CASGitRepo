package com.baypackets.ase.ra.http.web;

import com.baypackets.ase.ra.http.message.HttpRequest;

public class HttpReaderTask implements Runnable {

	
	private HttpRequest request;
	private WebManager webManager = WebManager.getInstance();
	@Override
	public void run() {
		webManager.handleRequest(request);
	}
	
	
	public HttpRequest getRequest() {
		return request;
	}
	public void setRequest(HttpRequest request) {
		this.request = request;
	}
	

}