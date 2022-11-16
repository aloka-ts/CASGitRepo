package com.baypackets.ase.util;

import java.io.ByteArrayOutputStream;

public class AseByteArrayOutputStream extends ByteArrayOutputStream{

	public AseByteArrayOutputStream() {
		super();
	}
	
	AseByteArrayOutputStream(int size){
		super(size);
		
	}
	
	
	public void copyStreamToByteArray(byte[] arr, int offset, int length){
		System.arraycopy(buf,0, arr, offset, length);
	}
}
