package com.genband.m5.maps.ide.sitemap.util;
import java.util.List;

import com.genband.m5.maps.ide.model.CPFPortlet;
/*
 * This is just to customize the palette(view).
 */
public class PortletInfo{
	private String name;
	private int iconType;
	private String ToopTip;
	private List<String> portletListRoles;
	private CPFPortlet cpfPortlet;
	public PortletInfo() { }
	
	public PortletInfo(String name , int iconType) {
		this.name = name ;
		this.iconType = iconType;
	}
	public PortletInfo(String name , int iconType , String ToolTip) {
		this.name = name ;
		this.iconType = iconType;
		this.ToopTip = ToolTip;
	}
	
	public PortletInfo(String name , int iconType , String ToolTip,List<String> portletListRoles) {
		this.name = name ;
		this.iconType = iconType;
		this.ToopTip = ToolTip;
		this.portletListRoles=portletListRoles;
	}
	public PortletInfo(String name , int iconType , String ToolTip,List<String> portletListRoles, CPFPortlet cpfPortlet) {
		this.name = name ;
		this.iconType = iconType;
		this.ToopTip = ToolTip;
		this.portletListRoles=portletListRoles;
		this.cpfPortlet = cpfPortlet;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIconType() {
		return iconType;
	}

	public void setIconType(int iconType) {
		this.iconType = iconType;
	}

	public String getToopTip() {
		return ToopTip;
	}

	public void setToopTip(String toopTip) {
		ToopTip = toopTip;
	}

	public List<String> getPortletListRoles() {
		return portletListRoles;
	}

	public void setPortletListRoles(List<String> portletListRoles) {
		this.portletListRoles = portletListRoles;
	}

	public CPFPortlet getCpfPortlet() {
		return cpfPortlet;
	}

	public void setCpfPortlet(CPFPortlet cpfPortlet) {
		this.cpfPortlet = cpfPortlet;
	}
}
