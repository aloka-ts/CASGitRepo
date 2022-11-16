package com.genband.ase.alcx.JainTcapInterface;

import java.io.Serializable;

import java.util.TreeMap;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Collection;

import java.net.URL;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.genband.ase.alc.alcml.ALCServiceInterface.*;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceActionExecutionException;

import com.genband.ase.alc.alcml.jaxb.*;
import com.genband.ase.alc.alcml.jaxb.xjc.*;

import jain.*;
import jain.protocol.ss7.*;
import jain.protocol.ss7.tcap.*;
import jain.protocol.ss7.tcap.dialogue.*;
import jain.protocol.ss7.tcap.component.*;
import jain.protocol.ss7.sccp.*;
import jain.protocol.ss7.sccp.management.*;

import com.genband.tcap.io.*;
import com.genband.tcap.provider.TcapSession;
import com.genband.tcap.xjc.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import jain.protocol.ss7.tcap.DialogueIndEvent;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
@ALCMLActionClass(
         name="Jain TCAP ALC Extensions"
		 )
public class JainTcapInterface extends ALCServiceInterfaceImpl implements Serializable, JainTcapListener, ServiceDefinitionListener
{
	static Logger logger = Logger.getLogger(JainTcapInterface.class.getName());

	private static String Name = new String("Jain TCAP");

    public String getServiceName() { return Name; }

	static public void Initialize()
	{
	}

	public JainTcapInterface()
	{
	}

	public JainTcapInterface(String namespace, String serviceContextNameSpace, SccpUserAddress address)
	{
		this.namespace = namespace;
		this.address = address;
		this.serviceContextNameSpace = serviceContextNameSpace;
		ServiceDefinition.addServiceDefinitionListener(this);
		userNamespaceMapping.put(namespace, this);
		try
		{
			getProvider().addJainTcapListener(this, address);
		}
		catch (Exception e)
		{
			logger.log(Level.ERROR, "Rut-row", e);
		}
	}

	static public void RemoveAllListeners()
	{
		synchronized (userNamespaceMapping)
		{
			Collection<JainTcapInterface> values = userNamespaceMapping.values();
			Iterator<JainTcapInterface> iter = values.iterator();
			while (iter.hasNext())
			{
				JainTcapInterface jti = iter.next();
				try
				{
					jti.getProvider().removeJainTcapListener(jti);
				}
				catch (ListenerNotRegisteredException e)
				{
				}
				catch (IOException e)
				{
				}
			}
			userNamespaceMapping = Collections.synchronizedMap(new TreeMap<String, JainTcapInterface>());
		}
	}

	/* PROVIDER INTERFACES */
	@ALCMLActionMethod( name="get-tcap-dialogue-id", isAtomic=true, help="gets a tcap dialogue id\n")
	public void gettcapdialogueid(ServiceContext sContext,
						@ALCMLMethodParameter(	name="results-in",
												asAttribute=true,
												help="Where to put results into.\n")
													String ResultsIn) throws ServiceActionExecutionException
	{
		try
		{
			sContext.setAttribute(ResultsIn, Integer.toString(getProvider().getNewDialogueId()));
		}
		catch (Exception e)
		{
			logger.log(Level.ERROR, "Rut-row", e);
		}
		sContext.ActionCompleted();
	}



	@ALCMLActionMethod( name="add-jain-listener", isAtomic=true, help="adds tcap listener\n")
	public void addJainListener(ServiceContext sContext,
			@ALCMLMethodParameter(	name="name",
									asAttribute=true,
									required=true,
									help="naming prefix.\n")
										String name,
			@ALCMLMethodParameter(	name="ssn",
									asAttribute=true,
									required=true,
									help="ssn to listen on.\n")
										Integer ssn,
			@ALCMLMethodParameter(	name="member",
									asAttribute=true,
									required=true,
									help="point code member.\n")
										Integer member,
			@ALCMLMethodParameter(	name="cluster",
									asAttribute=true,
									required=true,
									help="point code cluster.\n")
										Integer cluster,
			@ALCMLMethodParameter(	name="zone",
									asAttribute=true,
									required=true,
									help="point code zone.\n")
										Integer zone

										)
										throws ServiceActionExecutionException
	{
		new JainTcapInterface(name, sContext.getNameSpace(), new SccpUserAddress(new SubSystemAddress(new SignalingPointCode(member, cluster, zone), ssn.shortValue())));
		sContext.ActionCompleted();
	}

	@ALCMLActionMethod( name="remove-jain-listener", isAtomic=true, help="removes tcap listener\n")
	public void addJainListener(ServiceContext sContext,
			@ALCMLMethodParameter(	name="name",
									asAttribute=true,
									required=true,
									help="naming prefix.\n")
										String name
										)
										throws ServiceActionExecutionException
	{
		synchronized (userNamespaceMapping)
		{
			JainTcapInterface jti = userNamespaceMapping.get(name);
			if (jti != null)
			{
				try
				{
					jti.getProvider().removeJainTcapListener(jti);
				}
				catch (Exception e)
				{
					logger.log(Level.ERROR, "user not registered", e);
				}
			}
		}
		sContext.ActionCompleted();
	}

	@ALCMLActionMethod( name="send-tcap-req-event", isAtomic=true, help="sends a tcap request on a dialogue\n")
	public void sendTcapReqEvent(ServiceContext sContext,
			@ALCMLMethodParameter(	name="content-url",
									asAttribute=true,
									required=true,
									help="content to send.\n")
										String contentUrl)
										throws ServiceActionExecutionException
	{
		try
		{
			URL input = new URL(contentUrl);
			InputStream is = input.openStream();
			String inputString = inputStreamToString(is);
			String resultant = ALCMLExpression.toString(sContext, inputString);
			ByteArrayInputStream bais = new ByteArrayInputStream(resultant.getBytes());

			TcapType tt = TcapContentReader.unmarshal(bais);

			DialogueReqEventType dialogueReqEvent = tt.getDialogueReqEvent();
			if (dialogueReqEvent != null)
			{
				DialogueReqEvent dre = dialogueReqEvent.getDialogueReqEventInterface();
				List<ComponentReqEventType> components = dialogueReqEvent.getComponentReqEvent();
				Iterator iComponent = components.iterator();
				while (iComponent.hasNext())
				{
					ComponentReqEventType cret = (ComponentReqEventType)iComponent.next();
					ComponentReqEvent cre = cret.getComponentReqEventInterface();
					getProvider().sendComponentReqEvent(cre);
				}
				getProvider().sendDialogueReqEvent(dre);
			}
		}
		catch (Exception e)
		{
			logger.log(Level.ERROR, "Rut-row", e);
		}
		sContext.ActionCompleted();
	}

	public JainTcapProvider getProvider()
	{
		try
		{
			JainSS7Factory fact = JainSS7Factory.getInstance();
			fact.setPathName("com.genband");
			return (JainTcapProvider)fact.createSS7Object("jain.protocol.ss7.tcap.JainTcapProviderImpl");
		}
		catch (Exception e)
		{
			logger.log(Level.ERROR, "Rut-row", e);
		}
		return null;
	}

    public void processStateIndEvent(StateIndEvent event)
    {
		try
		{
			ServiceContext sdContext = new ServiceContext();

			TcapType outtt = new TcapType();
			outtt.setStateIndEvent(StateIndEventType.produceJAXB(event));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			TcapContentWriter.marshal(outtt, baos);

			sdContext.setAttribute("content", baos.toString());
			ServiceDefinition iService = ServiceDefinition.getServiceDefinition(serviceContextNameSpace, namespace + "-process-state-ind-event");
			iService.execute(sdContext);
		}
		catch (Exception e)
		{
			logger.log(Level.ERROR, "Rut-row", e);
		}
	}

    public void addUserAddress(SccpUserAddress userAddress)
    {
    }

    public void addUserAddress(TcapUserAddress userAddress)
    {
    }

    public SccpUserAddress[] getUserAddressList()
    {
		SccpUserAddress returnValue[] = new SccpUserAddress[1];
		returnValue[0] = address;
        return returnValue;
    }

    public void processComponentIndEvent(ComponentIndEvent event)
    {
		//ServiceDefinition sd = ServiceDefinition.getServiceDefinition(namespace + "-process-component-ind-event");
		//if (sd != null)
		//{
		//	ServiceContext sdContext = new ServiceContext();
		try
		{
			LinkedList<ComponentIndEventType> ll_cie = (LinkedList<ComponentIndEventType>)threadLocal.get();
			ll_cie.add(ComponentIndEventType.produceJAXB(event));
		}
		catch (Exception e)
		{
			logger.log(Level.ERROR, "Rut-row", e);
		}


		//	TcapType outtt = new TcapType();
		//	outtt.setComponentIndEvent();
		//	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//	TcapContentWriter.marshal(outtt, baos);
		//	sdContext.setAttribute("content", baos.toString());
		//	sd.execute(sdContext);
		//}
	}

    public void processDialogueIndEvent(DialogueIndEvent event)
    {
		try
		{
			ServiceContext sdContext = new ServiceContext();

			TcapType outtt = new TcapType();
			DialogueIndEventType die = DialogueIndEventType.produceJAXB(event);
			LinkedList<ComponentIndEventType> ll_cie = (LinkedList<ComponentIndEventType>)threadLocal.get();
			Iterator<ComponentIndEventType> iter = ll_cie.iterator();
			while (iter.hasNext())
			{
				die.getComponentIndEvent().add(iter.next());
			}
			ll_cie.clear();
			outtt.setDialogueIndEvent(die);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			TcapContentWriter.marshal(outtt, baos);

			sdContext.setAttribute("content", baos.toString());
			ServiceDefinition iService = ServiceDefinition.getServiceDefinition(serviceContextNameSpace, namespace + "-process-dialogue-ind-event");
			iService.execute(sdContext);
		}
		catch (Exception e)
		{
			logger.log(Level.ERROR, "Rut-row", e);
		}
    }

    public void processTcapError(TcapErrorEvent error)
    {
	logger.log(Level.DEBUG, "Received processTcapError:"+ error);
    }

    public void removeUserAddress(SccpUserAddress userAddress)
    {
    }

    public void removeUserAddress(TcapUserAddress userAddress)
    {
    }

    private String inputStreamToString(InputStream in) throws IOException
    {
    	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
    	StringBuilder stringBuilder = new StringBuilder();
    	String line = null;

    	while ((line = bufferedReader.readLine()) != null) {
    		stringBuilder.append(line + "\n");
    	}

    	bufferedReader.close();
    	return stringBuilder.toString();
    }

	public void ServiceNamespaceAdded(String namespace)
	{
	}

	public void ServiceNamespaceRemoved(String namespace)
	{
		if (namespace.equals(serviceContextNameSpace))
		{
			try
			{
				getProvider().removeJainTcapListener(this);
			}
			catch (Exception lnre)
			{
				logger.log(Level.DEBUG, "Jain Listener not registered. ", lnre);
			}
		}
	}
	
	 public SccpUserAddress[] processRSNUniDirIndEvent(TcapSession tcapSession, DialogueIndEvent event){
		 return null;
	 }

	String namespace;
	String serviceContextNameSpace = null;
	SccpUserAddress address;
	private static ThreadLocal threadLocal = new ThreadLocal(){
		protected Object initialValue(){
			return new  LinkedList<ComponentIndEventType>();
		}
	};

	static private Map<String, JainTcapInterface> userNamespaceMapping = Collections.synchronizedMap(new TreeMap<String, JainTcapInterface>());
}
