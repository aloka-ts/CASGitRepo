package com.genband.m5.maps.common.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.genband.m5.maps.common.Weak;


/**
 * @author  syed.sarwar
 */
@Entity
@Weak(parentName="User")
public class UserAddress implements Serializable {

	private static final long serialVersionUID = -8849059769998428140L;
	protected Long id;
	protected String streetAddress1;
	protected String streetAddress2;
	protected String city;
	protected String state;
	protected String zip;
	
	protected Country country;
	
	private User user;
	
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(cascade={CascadeType.PERSIST,CascadeType.REMOVE})
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * @return  the city
	 * @uml.property  name="city"
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city  the city to set
	 * @uml.property  name="city"
	 */
	public void setCity(String city) {
		this.city = city;
	}
	
	/**
	 * @return  the country
	 * @uml.property  name="country"
	 */
	@ManyToOne(cascade={CascadeType.ALL}, targetEntity=Country.class)
	@JoinColumn(name="country")
	public Country getCountry() {
		return country;
	}
	/**
	 * @param country  the country to set
	 * @uml.property  name="country"
	 */
	public void setCountry(Country country) {
		this.country = country;
	}
	
	/**
	 * @return  the state
	 * @uml.property  name="state"
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state  the state to set
	 * @uml.property  name="state"
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return  the streetAddress1
	 * @uml.property  name="streetAddress1"
	 */
	public String getStreetAddress1() {
		return streetAddress1;
	}
	/**
	 * @param streetAddress1  the streetAddress1 to set
	 * @uml.property  name="streetAddress1"
	 */
	public void setStreetAddress1(String streetAddress1) {
		this.streetAddress1 = streetAddress1;
	}
	/**
	 * @return  the streetAddress2
	 * @uml.property  name="streetAddress2"
	 */
	public String getStreetAddress2() {
		return streetAddress2;
	}
	/**
	 * @param streetAddress2  the streetAddress2 to set
	 * @uml.property  name="streetAddress2"
	 */
	public void setStreetAddress2(String streetAddress2) {
		this.streetAddress2 = streetAddress2;
	}
	/**
	 * @return  the zip
	 * @uml.property  name="zip"
	 */
	public String getZip() {
		return zip;
	}
	/**
	 * @param zip  the zip to set
	 * @uml.property  name="zip"
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}
	
}

