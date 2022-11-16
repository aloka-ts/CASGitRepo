package com.genband.tcap.parser;


public class TcapContentReaderException extends Exception
{
	public TcapContentReaderException(Exception e)
	{
		super(e);
	}
	
	public TcapContentReaderException(String msg){
		super(msg);
	}
}