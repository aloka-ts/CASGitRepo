package com.agnity.map.datatypes;

import java.util.Collection;

import com.agnity.map.enumdata.CamelCapabilityHandlingMapEnum;

public class PDPContextInfoListMap {
	private Collection<PDPContextInfoMap> pdpContextInfoMapList;

	public Collection<PDPContextInfoMap> getPdpContextInfoMapList() {
		return pdpContextInfoMapList;
	}

	public void setPdpContextInfoMapList(Collection<PDPContextInfoMap> pdpContextInfoMapList) {
		this.pdpContextInfoMapList = pdpContextInfoMapList;
	}
	
	
	public void setPDPContextInfoMapList(
			Collection<PDPContextInfoMap> pdpContextInfoMapList) {
		this.pdpContextInfoMapList = pdpContextInfoMapList;
	}


	public void initValue() {
                setValue(new java.util.LinkedList<PDPContextInfoMap>());
            }

	public void setValue(java.util.Collection<PDPContextInfoMap> value) {
                this.pdpContextInfoMapList = value;
            }

    public java.util.Collection<PDPContextInfoMap> getValue() {
        return this.pdpContextInfoMapList;
    }

	public PDPContextInfoListMap(
			Collection<PDPContextInfoMap> value) {
		this.pdpContextInfoMapList = value;
	}	


        public PDPContextInfoListMap() {
             
        }

		@Override
		public String toString() {
			return "PDPContextInfoListMap [pdpContextInfoMapList=" + pdpContextInfoMapList + "]";
		}
        
        

}
