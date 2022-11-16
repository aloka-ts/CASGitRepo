package com.genband.m5.maps.common;



public class AuxiliaryDetails {
	
	private Integer pageSize = 10; //default
	
	private Integer rowNumber = 1;		
	
	private String orderBy;
	
	private boolean ascending = true;
	
	private boolean search = false;
	
	private SearchInfo searchInfo;

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(Integer rowNumber) {
		this.rowNumber = rowNumber;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}
	
	public static class SearchInfo {

		private boolean searechCaseSensitive = true;
		private String searchKey;
		private CPFConstants.Operators operator;
		private Object searchValue;
		public boolean isSearechCaseSensitive() {
			return searechCaseSensitive;
		}
		public void setSearechCaseSensitive(boolean searechCaseSensitive) {
			this.searechCaseSensitive = searechCaseSensitive;
		}
		public String getSearchKey() {
			return searchKey;
		}
		public void setSearchKey(String searchKey) {
			this.searchKey = searchKey;
		}
		public CPFConstants.Operators getOperator() {
			return operator;
		}
		public void setOperator(CPFConstants.Operators operator) {
			this.operator = operator;
		}
		public Object getSearchValue() {
			return searchValue;
		}
		public void setSearchValue(Object searchValue) {
			this.searchValue = searchValue;
		}
		
	}

	public SearchInfo getSearchInfo() {
		return searchInfo;
	}

	public void setSearchInfo(SearchInfo searchInfo) {
		this.searchInfo = searchInfo;
	}

	public boolean isSearch() {
		return search;
	}

	public void setSearch(boolean search) {
		this.search = search;
	}

}
