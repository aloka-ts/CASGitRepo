/****
 Copyright(c) 2013 Agnity,Inc. All rights reserved.
 
 This is proprietary source code of Agnity,Inc.
 
 Agnity,Inc. retains all intellectual property rights associated with this source code. Use is subjected to license terms.
 
 This source code contains trade secrets owned by Agnity,Inc.
 
 Confidentiality of this computer program must be maintained at all times, unless explicitly authorized by Agnity, Inc.
 ****/
package com.baypackets.ase.sysapps.registrar.common;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.registrar.presence.Note;
import com.baypackets.ase.sysapps.registrar.presence.Person;
import com.baypackets.ase.sysapps.registrar.presence.Presence;
import com.baypackets.ase.sysapps.registrar.presence.Tuple;

public class StaXParser extends Constants {

	 private static Logger logger = Logger.getLogger(StaXParser.class);
	 
  @SuppressWarnings({ "unchecked" })
  public static Presence parseXML(String xmlContent) throws XMLStreamException,Exception {
	  if(logger.isDebugEnabled())
		  logger.debug("Inside parseXML() method:"+xmlContent);
	  Presence presence=new Presence();
		  // First create a new XMLInputFactory
		  XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		  // Setup a new eventReader
		  InputStream in = new ByteArrayInputStream(xmlContent.getBytes());
		  XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
		  // Read the XML document
		  Tuple tuple = null;
		  Person person=null;
		  String currentElement=null;
		  while (eventReader.hasNext()) {
			  XMLEvent event = eventReader.nextEvent();

			  if (event.isStartElement()) {
				  StartElement startElement = event.asStartElement();

				  if (startElement.getName().getLocalPart() == (ELEMENT_PRESENCE)) {
					  Iterator<Attribute> attributes = startElement
					  .getAttributes();
					  while (attributes.hasNext()) {
						  Attribute attribute = attributes.next();
						  if (attribute.getName().toString().equals(ATTRIB_ENTITY)) {
							  presence.setEntity(attribute.getValue());
						  }

					  }
					  continue;        	  
				  }

				  // If we have a item element we create a new item
				  if (startElement.getName().getLocalPart() == (ELEMENT_TUPLE)) {
					  currentElement=ELEMENT_TUPLE;
					  tuple = new Tuple();
					  // We read the attributes from this tag and add the date
					  // attribute to our object
					  Iterator<Attribute> attributes = startElement
					  .getAttributes();
					  while (attributes.hasNext()) {
						  Attribute attribute = attributes.next();
						  if (attribute.getName().toString().equals(ATTRIB_ID)) {
							  tuple.setTupleId(attribute.getValue());
						  }

					  }
					  continue;
				  }


				  if (event.asStartElement().getName().getLocalPart()
						  .equals(ELEMENT_BASIC)) {
					  event = eventReader.nextEvent();
					  tuple.setBasic(event.asCharacters().getData());
					  continue;
				  }

				  if (event.asStartElement().getName().getLocalPart()
						  .equals(ELEMENT_PERSON)) {
					  currentElement=ELEMENT_PERSON;
					  person=new Person();
					  Iterator<Attribute> attributes = startElement.getAttributes();
					  while (attributes.hasNext()) {
						  Attribute attribute = attributes.next();
						  if (attribute.getName().toString().equals(ATTRIB_ID)) {
							  person.setPersonId(attribute.getValue());
						  }
					  }
					  continue;
				  }

				  if (event.asStartElement().getName().getLocalPart()
						  .equals(ELEMENT_ACTIVITIES)) {
					  currentElement=ELEMENT_ACTIVITIES;
					  continue;
				  }

				  if(currentElement==ELEMENT_ACTIVITIES){
					  String activities= event.asStartElement().getName().getLocalPart();
					  person.setActivities(activities);
					  if(activities.contains("other")){
						  event = eventReader.nextEvent();
					  	  person.setActivitiesVal(event.asCharacters().getData());
					  	continue;
					  }
				  }

				  if (event.asStartElement().getName().getLocalPart()
						  .equals(ELEMENT_NOTE)) {
					  Note note=new Note();
					  event = eventReader.nextEvent();
					  note.setValue(event.asCharacters().getData());
					  Iterator<Attribute> attributes = startElement.getAttributes();
					  while (attributes.hasNext()) {
						  Attribute attribute = attributes.next();
						  if (attribute.getName().toString().equals(ATTRIB_LANG)) {
							  note.setLang(attribute.getValue());
						  }
					  }
					  if(ELEMENT_TUPLE.equals(currentElement)){
						  tuple.setNote(note);
					  }
					  if(ELEMENT_PERSON.equals(currentElement)){
						  person.setNote(note);
					  }else {
						  presence.setNote(note);
					  }
					  continue;
				  }

			  }
			  // If we reach the end of an item element we add it to the list
			  if (event.isEndElement()) {
				  EndElement endElement = event.asEndElement();
				  if (endElement.getName().getLocalPart() == (ELEMENT_ACTIVITIES)) 
					  currentElement=ELEMENT_PERSON;
				  if (endElement.getName().getLocalPart() == (ELEMENT_TUPLE)) {
					  currentElement=ELEMENT_PRESENCE;
					  presence.addTuple(tuple);        	  
				  }
				  if (endElement.getName().getLocalPart() == (ELEMENT_PERSON)) {
					  currentElement=ELEMENT_PRESENCE;
					  presence.addPerson(person);
				  }	  
			  }
		  }
		  if(logger.isDebugEnabled())
			  logger.debug("Exiting parseXML() method:"+presence);
	  return presence;
  }

} 