/****
 Copyright(c) 2013 Agnity,Inc. All rights reserved.
 
 This is proprietary source code of Agnity,Inc.
 
 Agnity,Inc. retains all intellectual property rights associated with this source code. Use is subjected to license terms.
 
 This source code contains trade secrets owned by Agnity,Inc.
 
 Confidentiality of this computer program must be maintained at all times, unless explicitly authorized by Agnity, Inc.
 ****/
package com.baypackets.ase.sysapps.registrar.presence;

/**
 * This class represents Tuple element in presence xml document.
 * @author abaxi
 */
public class Tuple {
	
	
	private String tupleId;
	private String basic;
	private Note note;
	
	

	public Tuple() {}

	/**
	 * @param tupleId
	 * @param basic
	 * @param note
	 */
	public Tuple(String tupleId, String basic, Note note) {
		this.tupleId = tupleId;
		this.basic = basic;
		this.note = note;
	}



	/**
	 * @param basic the basic to set
	 */
	public void setBasic(String basic) {
		this.basic = basic;
	}



	/**
	 * @return the basic
	 */
	public String getBasic() {
		return basic;
	}



	/**
	 * @param tupleId the tupleId to set
	 */
	public void setTupleId(String tupleId) {
		this.tupleId = tupleId;
	}



	/**
	 * @return the tupleId
	 */
	public String getTupleId() {
		return tupleId;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Tuple [basic=" + basic + ", note=" + note + ", tupleId="
				+ tupleId + "]";
	}
	
	
}
