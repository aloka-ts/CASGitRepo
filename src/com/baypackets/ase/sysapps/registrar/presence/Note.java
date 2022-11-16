/****
 Copyright(c) 2013 Agnity,Inc. All rights reserved.
 
 This is proprietary source code of Agnity,Inc.
 
 Agnity,Inc. retains all intellectual property rights associated with this source code. Use is subjected to license terms.
 
 This source code contains trade secrets owned by Agnity,Inc.
 
 Confidentiality of this computer program must be maintained at all times, unless explicitly authorized by Agnity, Inc.
 ****/
package com.baypackets.ase.sysapps.registrar.presence;


/**
 * This class represents Note element in presence xml document.
 * @author abaxi
 */
public class Note {

    /**
	 * @param value
	 * @param lang
	 */
	public Note(String value, String lang) {
		this.value = value;
		this.lang = lang;
	}
	
	public Note(){
		
	}


    protected String value;
	
  
    protected String lang;

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *    
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *    
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the lang property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *    
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *    
     */
    public void setLang(String value) {
        this.lang = value;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Note [lang=" + lang + ", value=" + value + "]";
	}

}
