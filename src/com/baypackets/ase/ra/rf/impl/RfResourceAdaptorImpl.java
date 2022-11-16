package com.baypackets.ase.ra.rf.impl;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.ra.rf.RfResourceAdaptor;
import com.baypackets.ase.ra.rf.RfResourceException;
import com.baypackets.ase.ra.rf.RfStackInterface;
import com.baypackets.ase.ra.rf.RfResponse;
import com.baypackets.ase.ra.rf.RfRequest;
//import com.baypackets.ase.ra.rf.RfSession;
import com.baypackets.ase.ra.rf.impl.RfResourceFactoryImpl;
import com.baypackets.ase.ra.rf.impl.RfStackInterfaceFactory;
import com.baypackets.ase.resource.ResourceEvent;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.replication.ReplicationEvent;

public class RfResourceAdaptorImpl implements RfResourceAdaptor 
{
	private static Logger logger = Logger.getLogger(RfResourceAdaptorImpl.class);
	private static ResourceContext context = null;
	private static RfStackInterface stackInterface = null;
	private short role = RfResourceAdaptor.ROLE_ACTIVE;
	private boolean sendMessage = true;
	private boolean dfnUp = true;
	private boolean peerDfnUp = true;


	public RfResourceAdaptorImpl() 
	{
		super();
	}

	public void init(ResourceContext context) throws ResourceException 
	{
		this.context = context;		
		((RfResourceFactoryImpl)this.context.getResourceFactory()).init(this.context);
		
		// Get configuration role
		this.role = context.getCurrentRole();
		if(logger.isDebugEnabled()) {
			logger.debug("The system is " + (this.role == RfResourceAdaptor.ROLE_ACTIVE ? "active" : "standby"));
		
			// Load and initialize Rf stack interface
			if(logger.isDebugEnabled())
				logger.debug("init(): load stack interface.");
		}

		try 
		{
			this.stackInterface = RfStackInterfaceFactory.getInstance().loadStackInterface(context, this);
		}
		catch (Throwable t) 
		{
			logger.error("init(): " + t);
			throw new ResourceException(t.toString());
		}

		try 
		{
			if(logger.isDebugEnabled())
				logger.debug("init(): initialize stack interface...");
			this.stackInterface.init(context);
			if(logger.isDebugEnabled())
				logger.debug("init(): stack interface is initialized.");
		} 
		catch (Exception ex) 
		{
			logger.error("Rf stack interface initialization failed: " + ex);
			throw new ResourceException(ex);
		}

	}

	public void start() throws ResourceException 
	{
		if (this.role != RfResourceAdaptor.ROLE_ACTIVE) 
		{
			if(logger.isInfoEnabled())
				logger.info("Standby...");
			return;
		}
		if(logger.isDebugEnabled())
			logger.debug("Start stack interface...");
		try 
		{
			this.stackInterface.start();
		} 
		catch (Exception ex) 
		{
			logger.error("Rf stack interface start() failed: " + ex);
			return;
			//throw new ResourceException(ex);
		}
		if(logger.isDebugEnabled())
			logger.debug("Stack interface is started.");

	}

	public void stop() throws ResourceException 
	{
		try 
		{
			this.stackInterface.stop();
			if(logger.isInfoEnabled())
				logger.info("Stack Interface successfully stopped");
		} 
		catch (Exception ex) 
		{
			logger.error("Rf stack interface stop()n failed: " + ex);
			throw new ResourceException(ex);
		}

	}

	public void configurationChanged(String arg0, Object arg1) throws ResourceException
	{
		// TODO Auto-generated method stub

	}

	public void roleChanged(String clusterId, String subsystemId, short role) 
	{
		if(logger.isDebugEnabled())
			logger.debug("roleChanged(): role is changed to " + role);
		short preRole = this.role;
		this.role = role;
		if (preRole != ROLE_ACTIVE && role == ROLE_ACTIVE) 
		{
			try 
			{
				this.start();
			} 
			catch (Exception e) 
			{
				logger.error("roleChanged(): " + e);
			}
		}
	}

	public void sendMessage(SasMessage message) throws IOException 
	{
		if(logger.isDebugEnabled())
			logger.debug("Sending message: " + message);
		try 
		{
			if (message instanceof RfRequest) 
			{
				if(getSendMessage()) 
				{
					//((RfSession)((RfRequest)message).getSession()).justTesting();
					replicate((RfRequest)message);
					this.stackInterface.handleRequest((RfRequest)message);
				} else {
					if(logger.isDebugEnabled())
						logger.debug("CDF is disconnected. So not sending Request");
				}
			} 
			else if (message instanceof RfResponse) 
			{
				this.stackInterface.handleResponse((RfResponse)message);
			} 
			else 
			{
				logger.error("Message dropped: not a Rf message.");
			}
		} 
		catch (RfResourceException ex) 
		{
			logger.error("sendMessage() failed " + ex);			
		}

	}

	public void processed(SasMessage arg0) 
	{
		// TODO Auto-generated method stub

	}

	public void failed(SasMessage arg0, AseInvocationFailedException arg1) 
	{
		// TODO Auto-generated method stub

	}

	public void deliverRequest(RfRequest request) throws ResourceException 
	{
		if(logger.isDebugEnabled())
			logger.debug("deliverRequest(): request["  + request.getRequestType() + "]");
		if (this.context != null) 
		{
			if(logger.isDebugEnabled())
				logger.debug("deliverRequest(): call context.");
			this.context.deliverMessage(request, true);
		} 
		else 
		{
			if(logger.isDebugEnabled())
				logger.debug("deliverRequest(): call request handler.");
		}

	}

	public void deliverResponse(RfResponse response) throws ResourceException 
	{
		if(logger.isDebugEnabled())
			logger.debug("deliverResponse(): response["  + response.getType() + "]");
		if (this.context != null) 
		{
			if(logger.isDebugEnabled())
				logger.debug("deliverResponse(): call context.");
			this.context.deliverMessage(response, true);
		} 
		else 
		{
			if(logger.isDebugEnabled())
				logger.debug("deliverResponse(): call response handler.");
		}

	}

	/*public void deliverData(Object obj) throws ResourceException 
	{
		logger.debug("Deliver Data  ==== >"+obj);
		if (this.context != null) 
		{
			logger.debug("deliverData(): call context.");
			this.context.deliverData(obj, true);
		} 
		else 
		{
			logger.debug("deliverData(): call data handler.");
		}

	}*/

	public void deliverEvent(ResourceEvent event) throws ResourceException 
	{
		if(logger.isDebugEnabled())
			logger.debug("deliverEvent(): event[" + event.getType() + "]");

		if(event.getType().equals("RF_NOTIFY_DISCONNECT_PEER_REQUEST"))
		{
			if(logger.isDebugEnabled())
				logger.debug(": RF_NOTIFY_DISCONNECT_PEER_REQUEST event received");
			setSendMessage(false);
		}
		else if (event.getType().equals("RF_DISCONNECT_PEER_RESPONSE"))
		{
			if(logger.isDebugEnabled())
				logger.debug(": RF_DISCONNECT_PEER_RESPONSE event received");
			setSendMessage(false);
		}

		else if (event.getType().equals("RF_NOTIFY_DFN_DOWN"))
		{
			if(logger.isDebugEnabled())
				logger.debug(": RF_NOTIFY_DFN_DOWN event received");
			setDfnUp(false);
		}

		else if (event.getType().equals("RF_NOTIFY_PEER_UP"))
		{
			if(logger.isDebugEnabled())
				logger.debug(": RF_NOTIFY_PEER_UP event received");
			setPeerDfnUp(true);
		}

		else if (event.getType().equals("RF_NOTIFY_PEER_DOWN"))
		{
			if(logger.isDebugEnabled())
				logger.debug(": RF_NOTIFY_PEER_DOWN event received");
			setPeerDfnUp(false);
		}

		else if (event.getType().equals("RF_NOTIFY_UNEXPECTED_MESSAGE"))
		{
			if(logger.isDebugEnabled())
				logger.debug(": RF_NOTIFY_UNEXPECTED_MESSAGE event received");
		}

		else if (event.getType().equals("RF_NOTIFY_FAILOVER"))
		{
			if(logger.isDebugEnabled())
				logger.debug(": RF_NOTIFY_FAILOVER event received");
		}

		else
			;

		if (this.context != null) 
		{
			if(logger.isDebugEnabled())
				logger.debug("deliverEvent(): call context.");
			this.context.deliverEvent(event, true);
		} 
		else 
		{
			if(logger.isDebugEnabled())
				logger.debug("deliverEvent(): call event handler.");
		}
		
	}
	private synchronized void setSendMessage(boolean fl)
	{
		sendMessage = fl;
	}
	private boolean getSendMessage()
	{
		return sendMessage;
	}

	private synchronized void setDfnUp(boolean fl)
	{
		dfnUp = fl;
	}
	private boolean getDfnUp()
	{
		return dfnUp;
	}

	private synchronized void setPeerDfnUp(boolean fl)
	{
		peerDfnUp = fl;
	}
	private boolean getPeerDfnUp()
	{
		return peerDfnUp;
	}

	private void replicate( RfRequest request ) 
	{
		ReplicationEvent event = new ReplicationEvent(request.getSession() , "RESOURCE");
		((RfSession)request.getSession()).sendReplicationEvent(event);
	}

	public static ResourceContext getResourceContext() {
		 return RfResourceAdaptorImpl.context;
	}	
	
	public static RfStackInterface getStackInterface() {
		return RfResourceAdaptorImpl.stackInterface;
	}

}
