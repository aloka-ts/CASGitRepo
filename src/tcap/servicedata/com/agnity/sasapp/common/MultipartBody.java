package com.agnity.sasapp.common;

import java.io.Serializable;

public class MultipartBody implements Serializable {
	private String	contentType;
	private String	contentDisposition;
	private byte[]	content;

	public MultipartBody(byte[] content, String contentType) {
		this.content = content;
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getContentDisposition() {
		return contentDisposition;
	}

	public void setContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
	}
	
}
