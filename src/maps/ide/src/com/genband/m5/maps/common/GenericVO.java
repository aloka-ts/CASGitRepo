package com.genband.m5.maps.common;

import java.util.List;

public class GenericVO {

	String entity;
	String primaryKey;
	String organizationRefKey;
	List<Datum> attributes;
	
	List<GenericVO> dependentEntities;
}


