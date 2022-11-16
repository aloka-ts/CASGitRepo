/****
 Copyright(c) 2013 Agnity,Inc. All rights reserved.
 
 This is proprietary source code of Agnity,Inc.
 
 Agnity,Inc. retains all intellectual property rights associated with this source code. Use is subjected to license terms.
 
 This source code contains trade secrets owned by Agnity,Inc.
 
 Confidentiality of this computer program must be maintained at all times, unless explicitly authorized by Agnity, Inc.
 ****/
package com.baypackets.ase.sysapps.registrar.presence;

/**
 * This class represents person element in presence xml document.
 * @author abaxi
 */

public class Person {
	
	/**
	 * @param personId
	 * @param note
	 * @param activities
	 */
	public Person(String personId, String activities, String activitiesVal, Note note) {
		this.personId = personId;
		this.note = note;
		this.activities = activities;
		this.activitiesVal=activitiesVal;
	}

	public Person(){}

	
	private String personId;
		
	
	private Note note;
	
	
	private String activities;
	
	private String activitiesVal;
	 
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


	/**
	 * @param personId the personId to set
	 */
	public void setPersonId(String personId) {
		this.personId = personId;
	}


	/**
	 * @return the personId
	 */
	public String getPersonId() {
		return personId;
	}

	/**
	 * @param activities the activities to set
	 */
	public void setActivities(String activities) {
		this.activities = activities;
	}

	/**
	 * @return the activities
	 */
	public String getActivities() {
		return activities;
	}

	/**
	 * @return the activitiesVal
	 */
	public String getActivitiesVal() {
		return activitiesVal;
	}

	/**
	 * @param activitiesVal the activitiesVal to set
	 */
	public void setActivitiesVal(String activitiesVal) {
		this.activitiesVal = activitiesVal;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Person [personId=" + personId + ", note=" + note
				+ ", activities=" + activities + ", activitiesVal="
				+ activitiesVal + "]";
	}

	
}
