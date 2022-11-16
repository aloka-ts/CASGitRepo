package com.genband.m5.maps.common.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.genband.m5.maps.common.Weak;

@Entity
@Weak (parentName="User")
public class ContactInfo implements Serializable {

	private Long id;
	
//	private Map<ContactType, String> info;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

//	public Map<ContactType, String> getInfo() {
//		return info;
//	}

//	public void setInfo(Map<ContactType, String> info) {
//		this.info = info;
//	}
	
}
