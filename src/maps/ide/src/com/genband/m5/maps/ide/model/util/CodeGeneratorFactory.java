package com.genband.m5.maps.ide.model.util;

public class CodeGeneratorFactory {

	public static CodeGenerator createJsfGenerator () {
		return new JsfCodeGenerator (); //TODO
	}
	
	public static CodeGenerator createWebServiceGenerator () {
		return new WebServiceGenerator (); //TODO
	}
}
