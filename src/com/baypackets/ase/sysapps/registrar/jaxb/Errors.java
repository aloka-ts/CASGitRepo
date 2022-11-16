package com.baypackets.ase.sysapps.registrar.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"error"
})
@XmlRootElement(name = "Error-List")
public class Errors {

	@XmlElement(name = "Error")
	List<Errors.Error> error;

	public List<Errors.Error> getError() {
		if (error == null) {
			error = new ArrayList<Errors.Error>();
		}
		return this.error;
	}
	/**
	 * This method adds an error to errorlist with given errorCode and description
	 * @param errorCode
	 * @param description
	 */
	public void addError(String errorCode,String description){
		if(errorCode!=null && description!=null){
		Errors.Error error = new Errors.Error();
		error.setErrorCode(errorCode);
		error.setDescription(description);
		this.getError().add(error);
		}
	}
	
	/**
	 * This constructor creates an object of Errors class and adds a error in 
	 * it with given errorCode and description  
	 */
	public Errors(String errorCode,String description) {
		this.addError(errorCode, description);
	}
	
	public Errors() {
		super();
	}



	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {
			"errorCode",
			"description"
	})
	public static class Error {


		@XmlElement(name = "ErrorCode")
		private String errorCode;

		@XmlElement(name = "Description")
		private String description;

		
		public String getErrorCode() {
			return errorCode;
		}

		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String errorDescription) {
			this.description = errorDescription;
		}

	}

}
