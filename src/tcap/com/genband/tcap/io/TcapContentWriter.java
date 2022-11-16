package com.genband.tcap.io;

import java.io.OutputStream;

import java.util.List;
import java.util.Iterator;

import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.DialogueIndEvent;
import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.ComponentIndEvent;

import jain.protocol.ss7.sccp.StateReqEvent;
import jain.protocol.ss7.sccp.StateIndEvent;

import com.genband.tcap.xjc.DialogueReqEventType;
import com.genband.tcap.xjc.DialogueIndEventType;
import com.genband.tcap.xjc.ComponentReqEventType;
import com.genband.tcap.xjc.ComponentIndEventType;
import com.genband.tcap.xjc.StateReqEventType;
import com.genband.tcap.xjc.StateIndEventType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;

import com.genband.tcap.xjc.TcapType;


/**
 *	This class facilitates writing of JainTcap content to OutputStreams
 * <UL>
 * <LI> It can support writing TcapType instances (which are produced by the JAXB compilation
 *  of the JainTcap.xsd). </LI>
 * <LI> It can support also support writing of JainTcap objects (first marshalling the objects to
 * compliant JainTcap.xsd instances.
 * </LI>
 * </UL>
 */
public class TcapContentWriter
{
	static private JAXBContext jc = null;
	static private Marshaller marshaller = null;

	static public synchronized void marshal(TcapType tt, OutputStream os) throws TcapContentWriterException
	{
		try
		{
			getMarshaller().marshal(tt, os);
		}
		catch (JAXBException e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	static public synchronized void marshal(DialogueReqEvent dre, List<ComponentReqEvent> components, OutputStream os) throws TcapContentWriterException
	{
		TcapType outtt = new TcapType();
		outtt.setDialogueReqEvent(DialogueReqEventType.produceJAXB(dre));

    if (components != null) {
		  Iterator<ComponentReqEvent> iComponent = components.iterator();
		  while (iComponent.hasNext())
		  {
		  	ComponentReqEvent cre = iComponent.next();
		  	List<ComponentReqEventType> builtcomponents = outtt.getDialogueReqEvent().getComponentReqEvent();
		  	builtcomponents.add(ComponentReqEventType.produceJAXB(cre));
		  }
}
		  marshal(outtt, os);
    
	}

	static public synchronized void marshal(DialogueIndEvent die, List<ComponentIndEvent> components, OutputStream os) throws TcapContentWriterException
	{
		TcapType outtt = new TcapType();
		outtt.setDialogueIndEvent(DialogueIndEventType.produceJAXB(die));

		Iterator<ComponentIndEvent> iComponent = components.iterator();
		while (iComponent.hasNext())
		{
			ComponentIndEvent cie = iComponent.next();
			List<ComponentIndEventType> builtcomponents = outtt.getDialogueIndEvent().getComponentIndEvent();
			builtcomponents.add(ComponentIndEventType.produceJAXB(cie));
		}
		marshal(outtt, os);
	}

	static public synchronized void marshal(StateReqEvent sre, OutputStream os) throws TcapContentWriterException
	{
		TcapType outtt = new TcapType();
		outtt.setStateReqEvent(StateReqEventType.produceJAXB(sre));
		marshal(outtt, os);
	}

	static public synchronized void marshal(StateIndEvent sie, OutputStream os) throws TcapContentWriterException
	{
		TcapType outtt = new TcapType();
		outtt.setStateIndEvent(StateIndEventType.produceJAXB(sie));
		marshal(outtt, os);
	}

	static private Marshaller getMarshaller() throws TcapContentWriterException
	{
		try
		{
			if (jc == null)
			{
				jc = JAXBContext.newInstance("com.genband.tcap.xjc", TcapContentWriter.class.getClassLoader());
				marshaller = jc.createMarshaller();
				marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			}
			return marshaller;
		}
		catch (Exception e)
		{
			jc = null;
			throw new TcapContentWriterException(e);
		}
	}

	private TcapContentWriter() {}
}
