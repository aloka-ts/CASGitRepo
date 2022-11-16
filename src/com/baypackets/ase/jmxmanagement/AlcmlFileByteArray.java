package com.baypackets.ase.jmxmanagement;
import java.io.*;

public class AlcmlFileByteArray implements Serializable
{

	private byte[] bytes ;
	private String fileName;
	public byte[] getByteArray()
	{
		return bytes;
	}

	public void setByteArray(byte[] bytesArray)
	{
		bytes = bytesArray;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
		

