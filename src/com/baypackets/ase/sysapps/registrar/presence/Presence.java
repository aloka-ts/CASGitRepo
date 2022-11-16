/****
 Copyright(c) 2013 Agnity,Inc. All rights reserved.
 
 This is proprietary source code of Agnity,Inc.
 
 Agnity,Inc. retains all intellectual property rights associated with this source code. Use is subjected to license terms.
 
 This source code contains trade secrets owned by Agnity,Inc.
 
 Confidentiality of this computer program must be maintained at all times, unless explicitly authorized by Agnity, Inc.
 ****/
package com.baypackets.ase.sysapps.registrar.presence;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents presence element in presence xml document.
 * @author abaxi
 */
public class Presence {
	private String sipIfTag;
	private String entity;
	private List<Tuple> tupleList;
	private List<Person> personList;
	private Note note;
	private int expires;
	
	/**
	 * 
	 */
	public Presence() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param tupleList
	 * @param personList
	 * @param note
	 */
	public Presence(String ifSipTag,String entity,List<Tuple> tupleList, List<Person> personList, Note note,int expires) {
		this.setSipIfTag(sipIfTag);
		this.setEntity(entity);
		this.tupleList = tupleList;
		this.personList = personList;
		this.note = note;
		this.expires=expires;
	}
	/**
	 * @param tupleList the tupleList to set
	 */
	public void setTupleList(List<Tuple> tupleList) {
		this.tupleList = tupleList;
	}
	/**
	 * @return the tupleList
	 */
	public List<Tuple> getTupleList() {
		return tupleList;
	}
	/**
	 * @param personList the personList to set
	 */
	public void setPersonList(List<Person> personList) {
		this.personList = personList;
	}
	/**
	 * @return the personList
	 */
	public List<Person> getPersonList() {
		return personList;
	}
	/**
	 * @param note the note to set
	 */
	public void setNote(Note note) {
		this.note = note;
	}
	/**
	 * @return the note
	 */
	public Note getNote() {
		return note;
	}
	public void addTuple(Tuple tuple) {
		if(tuple!=null){
		if(this.tupleList==null)
			tupleList=new LinkedList<Tuple>();
		tupleList.add(tuple);
		}
	}
	
	public void addPerson(Person person) {
		if(person!=null){
		if(this.personList==null)
			personList=new LinkedList<Person>();
		personList.add(person);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Presence [entity=" + entity + ", note=" + note
				+ ", personList=" + personList + ", tupleList=" + tupleList
				+ "]";
	}
	/**
	 * @param entity the entity to set
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}
	/**
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}
	/**
	 * @param sipIfTag the sipIfTag to set
	 */
	public void setSipIfTag(String sipIfTag) {
		this.sipIfTag = sipIfTag;
	}
	/**
	 * @return the sipIfTag
	 */
	public String getSipIfTag() {
		return sipIfTag;
	}
	/**
	 * @param expires the expires to set
	 */
	public void setExpires(int expires) {
		this.expires = expires;
	}
	/**
	 * @return the expires
	 */
	public int getExpires() {
		return expires;
	}
	
	
}
