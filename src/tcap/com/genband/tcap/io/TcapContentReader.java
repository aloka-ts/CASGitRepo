package com.genband.tcap.io;

import java.io.InputStream;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;

import com.genband.tcap.xjc.DialogueReqEventType;
import com.genband.tcap.xjc.DialogueIndEventType;
import com.genband.tcap.xjc.ComponentIndEventType;
import com.genband.tcap.xjc.StateReqEventType;
import com.genband.tcap.xjc.StateIndEventType;
import com.genband.tcap.xjc.LocalCancelIndEventType;
import com.genband.tcap.xjc.RejectIndEventType;

import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.DialogueIndEvent;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.component.LocalCancelIndEvent;
import jain.protocol.ss7.tcap.component.RejectIndEvent;

import jain.protocol.ss7.sccp.StateReqEvent;
import jain.protocol.ss7.sccp.StateIndEvent;

import jain.protocol.ss7.tcap.DialogueReqEvent;


import com.genband.tcap.xjc.TcapType;

public class TcapContentReader
{
	static private JAXBContext jc = null;
	static private Unmarshaller unmarshaller = null;

	static public synchronized TcapType unmarshal(InputStream is) throws TcapContentReaderException
	{
		try
		{
			JAXBElement<TcapType> ttElement = (JAXBElement<TcapType>)getUnmarshaller().unmarshal(is);
			TcapType tt = ttElement.getValue();
			return tt;
		}
		catch (JAXBException e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public synchronized List<Object> produceJainTcap(InputStream is) throws TcapContentReaderException
	{
		TcapType tt = unmarshal(is);
		LinkedList<Object> ll = new LinkedList<Object>();
		DialogueIndEventType dialogueIndEvent = tt.getDialogueIndEvent();
		if (dialogueIndEvent != null)
		{
			DialogueIndEvent die = dialogueIndEvent.getDialogueIndEventInterface();

			ll.add(die);

			List<ComponentIndEventType> components = dialogueIndEvent.getComponentIndEvent();
			Iterator iComponent = components.iterator();
			while (iComponent.hasNext())
			{
				ComponentIndEventType ciet = (ComponentIndEventType)iComponent.next();
				ComponentIndEvent cie = ciet.getComponentIndEventInterface();
				ll.add(cie);
			}
			return ll;
		}
		StateIndEventType stateIndEvent = tt.getStateIndEvent();
		if (stateIndEvent != null)
		{
			StateIndEvent sie = stateIndEvent.getStateIndEventInterface();
			ll.add(sie);
			return ll;
		}
		LocalCancelIndEventType localCancelIndEvent = tt.getLocalCancelIndEvent();
		if (localCancelIndEvent != null)
		{
			LocalCancelIndEvent lcie = localCancelIndEvent.getLocalCancelIndEventInterface();
			ll.add(lcie);
			return ll;
		}
		RejectIndEventType rejectIndEvent = tt.getRejectIndEvent();
		if (rejectIndEvent != null)
		{
			RejectIndEvent rie = rejectIndEvent.getRejectIndEventInterface();
			ll.add(rie);
			return ll;
		}
		return ll;
	}

	static private Unmarshaller getUnmarshaller() throws TcapContentReaderException
	{
		try
		{
			if (jc == null)
			{
				jc = JAXBContext.newInstance("com.genband.tcap.xjc", TcapContentReader.class.getClassLoader());
				unmarshaller = jc.createUnmarshaller();
			}
			return unmarshaller;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	private TcapContentReader() {}
}
