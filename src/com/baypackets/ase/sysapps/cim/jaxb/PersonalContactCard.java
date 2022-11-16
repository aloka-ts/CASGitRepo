package com.baypackets.ase.sysapps.cim.jaxb;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxUsername",
		"firstName",
		"lastName",
		"address",
		"city",
		"state",
		"country",
		"contact1",
		"contact2",
		"company",
		"department",
		"designation",
		"gender",
		"dob",
		"email1",
		"email2",
		"status"
})

@XmlRootElement(name="PCC")
public class PersonalContactCard {

	@XmlElement(name = "AconyxUsername")
	private String aconyxUsername;
	
	@XmlElement(name = "FirstName")
	private String firstName;
	
	@XmlElement(name = "LastName")
	private String lastName;
	
	@XmlElement(name = "Address")
	private String address;
	
	@XmlElement(name = "City")
	private String city;
	
	@XmlElement(name = "State")
	private String state;
	
	@XmlElement(name = "Country")
	private String country;
	
	@XmlElement(name = "Contact1")
	private String contact1;
	
	@XmlElement(name = "Contact2")
	private String contact2;
	
	@XmlElement(name = "Company")
	private String company;
	
	@XmlElement(name = "Department")
	private String department;
	
	@XmlElement(name = "Designation")
	private String designation;
	
	@XmlElement(name = "Gender")
	private String gender;
	
	@XmlElement(name = "DOB")
	private String dob;
	
	@XmlElement(name = "Email1")
	private String email1;
	
	@XmlElement(name = "Email2")
	private String email2;

	@XmlElement(name = "Status")
	private String status;
	
	public PersonalContactCard(){}
	/**
	 * @param aconyxUsername
	 * @param firstName
	 * @param lastName
	 * @param address
	 * @param city
	 * @param state
	 * @param country
	 * @param contact1
	 * @param contact2
	 * @param company
	 * @param department
	 * @param designation
	 * @param gender
	 * @param dob
	 * @param email1
	 * @param email2
	 */
	public PersonalContactCard(String aconyxUsername, String firstName,
			String lastName, String address, String city, String state,
			String country, String contact1, String contact2, String company,
			String department, String designation, String gender, String dob,
			String email1, String email2, String status) {
		this.aconyxUsername = aconyxUsername;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.state = state;
		this.country = country;
		this.contact1 = contact1;
		this.contact2 = contact2;
		this.company = company;
		this.department = department;
		this.designation = designation;
		this.gender = gender;
		this.dob = dob;
		this.email1 = email1;
		this.email2 = email2;
		this.status = status;
	}

	/**
	 * @return the aconyxUsername
	 */
	public String getAconyxUsername() {
		return aconyxUsername;
	}

	/**
	 * @param aconyxUsername the aconyxUsername to set
	 */
	public void setAconyxUsername(String aconyxUsername) {
		this.aconyxUsername = aconyxUsername;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the contact1
	 */
	public String getContact1() {
		return contact1;
	}

	/**
	 * @param contact1 the contact1 to set
	 */
	public void setContact1(String contact1) {
		this.contact1 = contact1;
	}

	/**
	 * @return the contact2
	 */
	public String getContact2() {
		return contact2;
	}

	/**
	 * @param contact2 the contact2 to set
	 */
	public void setContact2(String contact2) {
		this.contact2 = contact2;
	}

	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * @return the department
	 */
	public String getDepartment() {
		return department;
	}

	/**
	 * @param department the department to set
	 */
	public void setDepartment(String department) {
		this.department = department;
	}

	/**
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the dob
	 */
	public String getDob() {
		return dob;
	}

	/**
	 * @param dob the dob to set
	 */
	public void setDob(String dob) {
		this.dob = dob;
	}

	/**
	 * @return the email1
	 */
	public String getEmail1() {
		return email1;
	}

	/**
	 * @param email1 the email1 to set
	 */
	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	/**
	 * @return the email2
	 */
	public String getEmail2() {
		return email2;
	}

	/**
	 * @param email2 the email2 to set
	 */
	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	public void setPCCField(String name,String val){
		if(name!=null){
			if(name.equals("FirstName"))
				this.setFirstName(val);
			else if (name.equals("FirstName"))
				this.setFirstName(val); 
			else if (name.equals("LastName"))
				this.setLastName(val); 
			else if (name.equals("Address"))
				this.setAddress(val); 
			else if (name.equals("City"))
				this.setCity(val); 
			else if (name.equals("State"))
				this.setState(val); 
			else if (name.equals("Country"))
				this.setCountry(val); 
			else if (name.equals("Contact1"))
				this.setContact1(val); 
			else if (name.equals("Contact2"))
				this.setContact2(val); 
			else if (name.equals("Company"))
				this.setCompany(val); 
			else if (name.equals("Department"))
				this.setDepartment(val); 
			else if (name.equals("Designation"))
				this.setDesignation(val); 
			else if (name.equals("Gender"))
				this.setGender(val); 
			else if (name.equals("DOB"))
				this.setDob(val); 
			else if (name.equals("Email1"))
				this.setEmail1(val); 
			else if (name.equals("Email2"))
				this.setEmail2(val); 
		}
	}
	
}
