package com.agnity.map.datatypes;

import java.util.Collection;

public class SSEventSpecificationMap {
	
	private Collection<AddressStringMap> ssEventSpecificationList;

	public Collection<AddressStringMap> getSsEventSpecificationList() {
		return ssEventSpecificationList;
	}
	/**
	 * @param callBarringFeatureList the callBarringFeatureList to set
	 */
	public void setSsEventSpecificationList(
			Collection<AddressStringMap> ssEventSpecificationList) {
		this.ssEventSpecificationList = ssEventSpecificationList;
	}


	public void initValue() {
                setValue(new java.util.LinkedList<AddressStringMap>());
            }

	public void setValue(java.util.Collection<AddressStringMap> value) {
                this.ssEventSpecificationList = value;
            }

            public java.util.Collection<AddressStringMap> getValue() {
                return this.ssEventSpecificationList;
            }

	public SSEventSpecificationMap(
			Collection<AddressStringMap> ssEventSpecificationList) {
		this.ssEventSpecificationList = ssEventSpecificationList;
	}	


        public SSEventSpecificationMap() {
             
        }

}
