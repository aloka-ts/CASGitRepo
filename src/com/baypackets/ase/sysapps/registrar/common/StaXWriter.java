/****
 Copyright(c) 2013 Agnity,Inc. All rights reserved.
 
 This is proprietary source code of Agnity,Inc.
 
 Agnity,Inc. retains all intellectual property rights associated with this source code. Use is subjected to license terms.
 
 This source code contains trade secrets owned by Agnity,Inc.
 
 Confidentiality of this computer program must be maintained at all times, unless explicitly authorized by Agnity, Inc.
 ****/
package com.baypackets.ase.sysapps.registrar.common;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;

import org.apache.log4j.Logger;


import com.baypackets.ase.sysapps.registrar.presence.*;


public class StaXWriter extends Constants {
	private static Logger logger = Logger.getLogger(StaXWriter.class);
	
	public static String writeXML(Presence presence) throws XMLStreamException,Exception {
		// Create a XMLOutputFactory
		  if(logger.isDebugEnabled())
			  logger.debug("Inside writeXML() method:"+presence);
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		// Create XMLEventWriter
		ByteArrayOutputStream xmlByteStream=new ByteArrayOutputStream();
		XMLEventWriter eventWriter = outputFactory
				.createXMLEventWriter(xmlByteStream);
		// Create a EventFactory
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		// Create and write Start Tag
		StartDocument startDocument = eventFactory.createStartDocument();
		eventWriter.add(startDocument);

		// Create config open tag
		StartElement presenceStartElement = eventFactory.createStartElement("",
				"", ELEMENT_PRESENCE);
		eventWriter.add(presenceStartElement);
		eventWriter.add(eventFactory.createAttribute("xmlns", "urn:ietf:params:xml:ns:pidf"));
		eventWriter.add(eventFactory.createAttribute("xmlns:dm", "urn:ietf:params:xml:ns:pidf:data-model"));
		eventWriter.add(eventFactory.createAttribute("xmlns:rpid", "urn:ietf:params:xml:ns:pidf:rpid"));
		eventWriter.add(eventFactory.createAttribute(ATTRIB_ENTITY,presence.getEntity()));

		// Write the different nodes

		for(Tuple tuple:presence.getTupleList()){
			createTupleNode(eventFactory,eventWriter,tuple);
		}

		for(Person person:presence.getPersonList()){
			createPersonNode(eventFactory,eventWriter,person);
		}
		if(presence.getNote()!=null){
			createNoteNode(eventFactory,eventWriter,"","",presence.getNote());
		}
		eventWriter.add(eventFactory.createEndElement("", "", ELEMENT_PRESENCE));
		//eventWriter.add(end);
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();
		 if(logger.isDebugEnabled())
			  logger.debug("Exiting writeXML() method:"+xmlByteStream.toString());
		return xmlByteStream.toString();
	}


	private static void createPersonNode(XMLEventFactory eventFactory,XMLEventWriter eventWriter, Person person) throws XMLStreamException {
		StartElement tupleStartElement = eventFactory.createStartElement("dm","",ELEMENT_PERSON );
		StartElement actElement = eventFactory.createStartElement("rpid", "", ELEMENT_ACTIVITIES);
		eventWriter.add(tupleStartElement);
		eventWriter.add(eventFactory.createAttribute(ATTRIB_ID, person.getPersonId()));
		eventWriter.add(actElement);
		createNode(eventWriter, "rpid", "", person.getActivities(),person.getActivitiesVal());
		eventWriter.add(eventFactory.createEndElement("rpid", "", ELEMENT_ACTIVITIES));
		if(person.getNote()!=null){
			createNoteNode(eventFactory,eventWriter,"dm","",person.getNote());
		}
		eventWriter.add(eventFactory.createEndElement("dm", "", ELEMENT_PERSON));

	}

	private static void createNoteNode(XMLEventFactory eventFactory, XMLEventWriter eventWriter,String prifix,String nameSpaceUri,Note note) throws XMLStreamException {

		StartElement noteStartElement = eventFactory.createStartElement(prifix,nameSpaceUri,ELEMENT_NOTE );		   
		eventWriter.add(noteStartElement);
		if(note.getLang()!=null)
			eventWriter.add(eventFactory.createAttribute(ATTRIB_LANG, note.getLang()));
		if(note.getValue()!=null){
			Characters characters = eventFactory.createCharacters(note.getValue());
			eventWriter.add(characters);
		}		   
		eventWriter.add(eventFactory.createEndElement(prifix,nameSpaceUri, ELEMENT_NOTE));		   

	}

	private static void createTupleNode(XMLEventFactory eventFactory, XMLEventWriter eventWriter, Tuple tuple) throws XMLStreamException {

		StartElement tupleStartElement = eventFactory.createStartElement("","",ELEMENT_TUPLE );
		StartElement statusElement = eventFactory.createStartElement("", "", ELEMENT_STATUS);

		eventWriter.add(tupleStartElement);
		eventWriter.add(eventFactory.createAttribute(ATTRIB_ID, tuple.getTupleId()));
		eventWriter.add(statusElement);
		createNode(eventWriter, "", "", ELEMENT_BASIC, tuple.getBasic());			    
		eventWriter.add(eventFactory.createEndElement("", "", ELEMENT_STATUS));
		if(tuple.getNote()!=null){
			createNoteNode(eventFactory,eventWriter,"","",tuple.getNote());
		}
		eventWriter.add(eventFactory.createEndElement("", "", ELEMENT_TUPLE));
	}

	private static void createNode(XMLEventWriter eventWriter,String prifix,String nameSpaceUri,String name,
			String value) throws XMLStreamException {
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		// Create Start node
		StartElement sElement = eventFactory.createStartElement(prifix, nameSpaceUri, name);
		eventWriter.add(sElement);
		// Create Content
		if(value!=null){
			Characters characters = eventFactory.createCharacters(value);
			eventWriter.add(characters);
		}
		// Create End node
		EndElement eElement = eventFactory.createEndElement(prifix, nameSpaceUri, name);
		eventWriter.add(eElement);
	}
	
	public static void main(String args[]) {
		try {
			String xmlContent="<?xml version=\"1.0\"?><presence xmlns=\"urn:ietf:params:xml:ns:pidf\" xmlns:dm=\"urn:ietf:params:xml:ns:pidf:data-model\" xmlns:rpid=\"urn:ietf:params:xml:ns:pidf:rpid\" entity=\"sip:amit.baxi@10.32.\"><tuple id=\"ta5086c2f\"><status><basic>open</basic></status></tuple><dm:person id=\"pd97a4a07\"><rpid:activities><rpid:on-the-phone></rpid:on-the-phone></rpid:activities><dm:note>On the Phone</dm:note></dm:person></presence>";
			Presence presence = StaXParser.parseXML(xmlContent);
			System.out.println(presence.toString());
			String xml=StaXWriter.writeXML(presence);
			System.out.println(xml);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}   
	}

}