package com.baypackets.sas.ide.util;

import java.net.InetAddress;
import com.baypackets.sas.ide.SasPlugin;



//This class maintains the status of the CAS engine running 
public class StatusASE 
{
	
	private static StatusASE instance = null;
	
	private static String host = "127.0.0.1";	
	private boolean isEmbeddedRunning = false;
	private static int AttachedMachine =0;	//1 Local CAS //2 Different CAS //0 Not Attached
	private static int PORT = 0;	


	private StatusASE()
	{
		SasPlugin.getDefault().log("Status of the Servlet Engine");
		PORT = SasPlugin.getPORT();
	}

	public boolean isEmbeddedRunning()
	{
		return isEmbeddedRunning;
	}

	public int getPORT()
	{
		return PORT;
	}

	public void setPORT(int port)
	{
		this.PORT = port;
	}

	public void setEmbeddedRunning(boolean flag)
	{
		this.isEmbeddedRunning = flag;
	}
	
	public static synchronized StatusASE getInstance()
	{
		
		if(instance==null)
		{
			instance = new StatusASE();
		}
		return instance;
		
		
	}
	

	public void setAddress(String addr)
	{
		String address = addr.trim().toString();
		String addrs = null;
		
		try
		{
			addrs = InetAddress.getByName(address).toString();
			
			if(addrs==null)
			{
				host = address;
			}
			else
			{
				
				int index = addrs.lastIndexOf('/');
				
				host = addrs.substring(index+1);
			}
		}
		catch(Exception e)
		{
			host = address;
			
		}
			
		
	}

	public String getAddress()
	{
		return host;
	}


	public void setAttach(int i)
	{
		AttachedMachine = i;
	}
	
	public int getAttach()
	{
		return AttachedMachine;
	}
	
	
}
