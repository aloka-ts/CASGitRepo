package com.genband.tcap.parser;

public class TcapContentWriterException extends Exception
{
	public TcapContentWriterException(Exception e)
	{
		super(e);
	}
	public TcapContentWriterException(String message, Throwable t)
	{
		super(message, t);
	}
}