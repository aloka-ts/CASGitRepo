package com.genband.m5.maps.common;

import java.io.Serializable;

public class Criteria implements Serializable {

	private static final long serialVersionUID = -4370320374339007400L;

    public String baseEntityName;

    public String basePrimaryKey;

    public Long basePrimaryKeyValue;


    public String fields;

    public String from;

    public String where;

    //public String orderBy;
    
    private AuxiliaryDetails searchDetails;

	public String getBaseEntityName() {
		return baseEntityName;
	}

	public void setBaseEntityName(String baseEntityName) {
		this.baseEntityName = baseEntityName;
	}

	public String getBasePrimaryKey() {
		return basePrimaryKey;
	}

	public void setBasePrimaryKey(String basePrimaryKey) {
		this.basePrimaryKey = basePrimaryKey;
	}

	public Long getBasePrimaryKeyValue() {
		return basePrimaryKeyValue;
	}

	public void setBasePrimaryKeyValue(Long basePrimaryKeyValue) {
		this.basePrimaryKeyValue = basePrimaryKeyValue;
	}



	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	/*public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}*/

	public AuxiliaryDetails getSearchDetails() {
		return searchDetails;
	}

	public void setSearchDetails(AuxiliaryDetails searchDetails) {
		this.searchDetails = searchDetails;
	}
	
	public void reset (int pageSize) {
		
		AuxiliaryDetails a = new AuxiliaryDetails ();
		a.setPageSize(pageSize);
		this.setSearchDetails (a);
	}


}
