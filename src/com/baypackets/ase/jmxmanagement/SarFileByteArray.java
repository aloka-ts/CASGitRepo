package com.baypackets.ase.jmxmanagement;
import java.io.*;

public class SarFileByteArray implements Serializable
{
	private static final long serialVersionUID = 8075814149407704L;
	private byte[] bytes ;
	public byte[] getByteArray()
	{
		return bytes;
	}

	public void setByteArray(byte[] bytesArray)
	{
		bytes = bytesArray;
	}
}
		
