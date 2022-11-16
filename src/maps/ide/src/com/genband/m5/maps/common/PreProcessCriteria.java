package com.genband.m5.maps.common;


public class PreProcessCriteria {
	private AuxiliaryDetails auxiliaryDetails = new AuxiliaryDetails ();
	public AuxiliaryDetails preProcess (AuxiliaryDetails auxiliaryDetails) {
		
		this.auxiliaryDetails.setAscending(auxiliaryDetails.isAscending());
		this.auxiliaryDetails.setPageSize(auxiliaryDetails.getPageSize());
		this.auxiliaryDetails.setRowNumber(auxiliaryDetails.getRowNumber());
		this.auxiliaryDetails.setSearch(auxiliaryDetails.isSearch());
		this.auxiliaryDetails.setOrderBy(SearchAndReplace (auxiliaryDetails.getOrderBy()));
		AuxiliaryDetails.SearchInfo searchInfo = new AuxiliaryDetails.SearchInfo ();
		if(auxiliaryDetails.getSearchInfo() != null) {
			searchInfo.setOperator(auxiliaryDetails.getSearchInfo().getOperator());
			searchInfo.setSearchValue(auxiliaryDetails.getSearchInfo().getSearchValue());
			searchInfo.setSearechCaseSensitive(auxiliaryDetails.getSearchInfo().isSearechCaseSensitive());
			searchInfo.setSearchKey(SearchAndReplace(auxiliaryDetails.getSearchInfo().getSearchKey()));
		} else {
			searchInfo = null;
		}
		this.auxiliaryDetails.setSearchInfo(searchInfo);
		return this.auxiliaryDetails;
	}
	private String SearchAndReplace (String expression){
		if(expression != null) {
			String newExpression = new String (expression);
			String temp = newExpression.substring (newExpression.indexOf("_"), newExpression.indexOf("."));
			newExpression = newExpression.replace(temp, "");
			return newExpression;
		} 
		return null;
	}
}
