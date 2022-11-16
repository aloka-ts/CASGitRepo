package com.baypackets.ase.container;

/**
 * This class saves information about class and its annotated method.Currently
 * used for SipApplicationKey annotation only
 * 
 * @author averma
 * 
 */
public class AseAnnotationInfo {

	private Class<?> annotatedClass;

	private String methodName;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?> getAnnotatedClass() {
		return annotatedClass;
	}

	public void setAnnotatedClass(Class<?> annotatedClass) {
		this.annotatedClass = annotatedClass;
	}

}