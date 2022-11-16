/**
 * Filename:	CustomAVPMap.java
 * Created On:	30-Sept-2006
 */
package com.baypackets.ase.ra.ro;

/**
 * This is a utility interface and represents a Diameter AVP list. This
 * is implemented by all interfaces and classes which contain an AVP list.
 *
 * @author Neeraj Jain
 */
public interface CustomAVPMap {
	/**
	 * This method returns all the new AVP codes present in map.
	 *
	 * @return integer array of new AVP codes
	 */
	public int[] getCustomAVPCodes();

	/**
	 * This method is used to access value corresponding to given AVP code;
	 *
	 * @param code AVP code whose value is to be accesses
	 *
	 * @return AVP value, if present
	 *         null, if not present
	 */
	public CustomAVP getCustomAVP(int code);

	/**
	 * This method sets value of given AVP in list.
	 *
	 * @param code AVP code to be set
	 * @param value AVP value to be set
	 *
	 * @return replaced AVP value, if any
	 *         null, if no value was previous present
	 *
	 * @throws IllegalStateException if this AVP list is not modifiable
	 */
	public CustomAVP setCustomAVP(int code, CustomAVP value);

	/**
	 * This method removes given AVP from the list.
	 *
	 * @param code AVP code to be removed
	 *
	 * @return Custom AVP which was removed
	 *         null, if no object was found
	 *
	 * @throws IllegalStateException if this AVP list is not modifiable
	 */
	public CustomAVP removeCustomAVP(int code);
}
