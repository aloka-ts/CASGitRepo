package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.enumdata.MatchTypeMapEnum;

public class DestinationNumberCriteriaMap {
	private MatchTypeMapEnum matchType;
	private Collection<ISDNAddressStringMap> destinationNumberList;
	private Collection<Integer> destinationNumberLengthList;
	/**
	 * @return the matchType
	 */
	public MatchTypeMapEnum getMatchType() {
		return matchType;
	}
	/**
	 * @return the destinationNumberList
	 */
	public Collection<ISDNAddressStringMap> getDestinationNumberList() {
		return destinationNumberList;
	}
	/**
	 * @return the destinationNumberLengthList
	 */
	public Collection<Integer> getDestinationNumberLengthList() {
		return destinationNumberLengthList;
	}
	/**
	 * @param matchType the matchType to set
	 */
	public void setMatchType(MatchTypeMapEnum matchType) {
		this.matchType = matchType;
	}
	/**
	 * @param destinationNumberList the destinationNumberList to set
	 */
	public void setDestinationNumberList(
			Collection<ISDNAddressStringMap> destinationNumberList) {
		this.destinationNumberList = destinationNumberList;
	}
	/**
	 * @param destinationNumberLengthList the destinationNumberLengthList to set
	 */
	public void setDestinationNumberLengthList(
			Collection<Integer> destinationNumberLengthList) {
		this.destinationNumberLengthList = destinationNumberLengthList;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DestinationNumberCriteriaMap [matchType=" + matchType
				+ ", destinationNumberList=" + destinationNumberList
				+ ", destinationNumberLengthList="
				+ destinationNumberLengthList + "]";
	}
}
