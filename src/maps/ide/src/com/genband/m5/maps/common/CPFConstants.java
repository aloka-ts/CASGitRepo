package com.genband.m5.maps.common;


public interface CPFConstants {

	public static String[] THEMES=new String[]{
		"industrial", "renaissance","maple","nphalanx","mission-critical"};
	
	public static String[] LAYOUTS=new String[]{
		"generic", "3columns"};
	
	public enum ViewType {
		LIST, DETAILS_VIEW
	}

	public enum NavigationType {
		NAVIGATION_TYPE_I, NAVIGATION_TYPE_II
	}
	
	
	public enum LayoutType {
		LAYOUT_TYPE_I, LAYOUT_TYPE_II
	}
	
	
	public enum InterfaceType {
		PORTLET, WEB_SERVICE
	}
	
	public enum WindowMode {
		NORMAL, MAXIMIZE, MINIMIZE
	}
	
	public enum PortletMode {
		VIEW, HELP
	}
	public enum OperationType {
		CREATE, MODIFY, DELETE, VIEW, LIST, SEARCH, SORT, ALL
	}
	
	public enum ControlType {
		TEXTBOX, RADIO, CHECKBOX, DROP_DOWN, LIST, CALENDAR, CLOCK, FONT, COLOR
	}
	
	public enum FormatType {
		TEXT, INTEGRAL, NUMERIC, CURRENCY, DATE, TIME, DATE_TIME
	}
	public enum ValidatorType {
		TEXT, INTEGRAL, NUMERIC, CURRENCY, DATE, TIME, DATE_TIME,PHONE,EMAIL
	}
	public enum AttributeDataType {
		TEXT, NUMERIC, INTEGRAL, DATE, TIMESTAMP, INTERVAL, RAW, BLOB, CLOB, BFILE, XML
	}
	public enum ResourceType {
		JAVABEAN, WEBSERVICE, PORTLETJSP
	}	

	public enum Operators {
		CONTAINS, EQUAL, LIKE, LESS_THAN_EQUAL, GREATER_THAN_EQUAL, LESS_THAN, GREATER_THAN, NOT_EQUAL, NOT_CONTAINS
	}
	
	public enum LogLevel {
		ERROR, WARNING, INFO
	}
	
	public enum RelationshipType {
		Contained, OneToOne, ManyToOne, OneToMany, ManyToMany
	}
}
