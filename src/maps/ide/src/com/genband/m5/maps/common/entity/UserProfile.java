package com.genband.m5.maps.common.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class UserProfile implements Serializable {

	private Long id;

	protected Date activationDate = new Date(System.currentTimeMillis());
	protected Date expirationDate = Date.valueOf("2099-01-01");
	protected String localeStr = "en_US";
	
	@Transient
	private String userLanguage;
	@Transient
	private String userCountry;
	@Transient
	private String variant;
	@Transient
	private Locale locale;
	
	public String getLocaleStr() {
		return localeStr;
	}
	public void setLocaleStr(String localeStr) {
		this.localeStr = localeStr;
	}
	public String getUserLanguage() {
		return userLanguage;
	}
	public void setUserLanguage(String userLanguage) {
		this.userLanguage = userLanguage;
	}
	public String getUserCountry() {
		return userCountry;
	}
	public void setUserCountry(String userCountry) {
		this.userCountry = userCountry;
	}
	public String getVariant() {
		return variant;
	}
	public void setVariant(String variant) {
		this.variant = variant;
	}
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getActivationDate() {
		return activationDate;
	}
	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	public Locale getLocale() {
		if (localeStr == null || localeStr.trim().equals(""))
			return null;
		
		if (locale == null) {
			String[] tokens = localeStr.split("_", 3);
			setUserLanguage(tokens[0]);
			setUserCountry(tokens[1]);
			setVariant(tokens[2]);
			locale = new Locale (tokens[0], tokens[1], tokens[2]);
		}
		return locale;
		
	}
	public void setLocale(Locale l) {
		setUserLanguage(l.getLanguage());
		setUserCountry(l.getCountry());
		setVariant(l.getVariant());
		locale = l;
		setLocaleStr(l.toString());
	}
	
	
}
