package com.baypackets.ase.sbb;

import java.io.Serializable;

/**
 * The MsVarAnnouncement class provides the accessor and mutator methods for defining the 
 * attributes of a variable announcement.
 *  The attributes include type, sub type, value and the language for playing these announcements.
 */
public class MsVarAnnouncement implements Serializable {
	private static final long serialVersionUID = 28107033403242439L;
	private String type;
	private String subType;
	private String value;
	private String language;

	/**
	 * Returns the language used for playing these announcements.
	 * @return Language for playing this variable announcement.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language for playing this variable announcement.
	 * When this language is not provided, the language specified in 
	 * the Play Operation Specification would be used.
	 * 
	 * <p>
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, it will be ignored and the media server default (if any) will be used.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param language Language for playing the announcement.
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Returns the Sub type of the variable announcement.
	 * 
	 * @return Sub type for the variable announcement.
	 */
	public String getSubType() {
		return subType;
	}

	/**
	 * Sets the Sub type for this varaible announcements.
	 * The valid values would be media server specific.
	 * 
	 * <p>
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, the SBB may through an exception while constructing the message.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param subType sub type for this variable announcement.
	 */
	public void setSubType(String subType) {
		this.subType = subType;
	}

	/**
	 * Returns the type of this variable announcement.
	 * @return Type of this variable announcement.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type for this variable announcement.
	 * This valid values for this would be media server specific.
	 * 
	 * <p>
	 * In case of the specified value is not within the range as specified by the
	 * Media Server, the SBB may through an exception during the play operation is invoked.
	 *
	 * <p>
	 * See the media server documentation for the acceptable range of values for this attribute.
	 * 
	 * @param type Type of this variable announcement.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the value that will be played as part of this variable announcement.
	 * @return Value that will be played as part of this announcement.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value for this variable announcement.
	 * The value format would be specified as defined by the media server.
	 * 
	 * If the value is not set, the SBB may throw an exception while the play operation is invoked.
	 * 
	 * @param value Value to be played as part of this variable announcement.
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
