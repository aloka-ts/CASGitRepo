package com.genband.m5.maps.ide.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;

import com.genband.m5.maps.common.CPFConstants;


public class CPFScreen implements java.io.Serializable{
	
	public CPFScreen(){
		
	}

	/**
	 * @uml.property  name="baseEntity"
	 */
	private ModelEntity baseEntity;

	/**
	 * @uml.property  name="interfaceType"
	 */
	private List<CPFConstants.InterfaceType> interfaceType;

	/**
	 * @uml.property  name="jspName"
	 */
	private String jspName;
	
	private IFile webServiceRef;
	
	private CPFPortlet portletRef;
	
	private WebServiceInfo webServiceInfo;

	//private Map<CPFScreen,IFile> nestedJspNames;
	private Map<RelationKey, CPFScreen> nestedJspNames;

	/**
	 * @uml.property  name="mappedRoles"
	 */
	private Map<CPFConstants.OperationType, List<String>> mappedRoles;

	/**
	 * @uml.property  name="preference"
	 */
	private CPFPortletPreference preference;

	/**
	 * @uml.property  name="selectedAttributes"
	 */
	private List<CPFAttribute> selectedAttributes;

	/**
	 * @uml.property  name="selectedOtherEntities"
	 */
	private List<ModelAttribute> selectedOtherEntities;
	
	private Map<RelationKey, List<CPFAttribute>> nestedAttributes;

	/**
	 * @uml.property  name="viewType"
	 */
	private CPFConstants.ViewType viewType = com.genband.m5.maps.common.CPFConstants.ViewType.LIST;
	
	/**
	 * For listing page a predicate expression
	 */
	private String extraListPredicate = null;
	
	
	private java.util.List<CPFConstants.OperationType> actionsSupported;
	
	private Integer[] operationIdPool;

	/**
	 * Getter of the property <tt>baseEntity</tt>
	 * @return  Returns the baseEntity.
	 * @uml.property  name="baseEntity"
	 */
	public ModelEntity getBaseEntity() {
		return baseEntity;
	}

	/**
	 * Getter of the property <tt>interfaceType</tt>
	 * @return  Returns the interfaceType.
	 * @uml.property  name="interfaceType"
	 */
	public List<CPFConstants.InterfaceType> getInterfaceType() {
		return interfaceType;
	}

	/**
	 * Getter of the property <tt>jspName</tt>
	 * @return  Returns the jspName.
	 * @uml.property  name="jspName"
	 */
	public String getJspName() {
		return jspName;
	}

	/**
	 * Getter of the property <tt>mappedRoles</tt>
	 * @return  Returns the mappedRoles.
	 * @uml.property  name="mappedRoles"
	 */
	public Map<CPFConstants.OperationType, List<String>> getMappedRoles() {
		return mappedRoles;
	}

	/**
	 * Getter of the property <tt>preference</tt>
	 * @return  Returns the preference.
	 * @uml.property  name="preference"
	 */
	public CPFPortletPreference getPreference() {
		return preference;
	}

	/**
	 * Getter of the property <tt>selectedAttributes</tt>
	 * It returns attributes selected from the baseEntity
	 * @return  Returns the selectedAttributes.
	 * @uml.property  name="selectedAttributes"
	 */
	public List<CPFAttribute> getSelectedAttributes() {
		return selectedAttributes;
	}

	/**
	 * Getter of the property <tt>selectedOtherEntities</tt>
	 * @return  Returns the selectedOtherEntities.
	 * @uml.property  name="selectedOtherEntities"
	 */
	public List<ModelAttribute> getSelectedOtherEntities() {
		return selectedOtherEntities;
	}

	/**
	 * Getter of the property <tt>viewType</tt>
	 * @return  Returns the viewType.
	 * @uml.property  name="viewType"
	 */
	public CPFConstants.ViewType getViewType() {
		return viewType;
	}

	/**
	 * Setter of the property <tt>baseEntity</tt>
	 * @param baseEntity  The baseEntity to set.
	 * @uml.property  name="baseEntity"
	 */
	public void setBaseEntity(ModelEntity baseEntity) {
		this.baseEntity = baseEntity;
	}

	/**
	 * Setter of the property <tt>interfaceType</tt>
	 * @param interfaceType  The interfaceType to set.
	 * @uml.property  name="interfaceType"
	 */
	public void setInterfaceType(List<CPFConstants.InterfaceType> interfaceType) {
		this.interfaceType = interfaceType;
	}

	/**
	 * Setter of the property <tt>jspName</tt>
	 * @param jspName  The jspName to set.
	 * @uml.property  name="jspName"
	 */
	public void setJspName(String jspName) {
		this.jspName = jspName;
	}

	/**
	 * Setter of the property <tt>mappedRoles</tt>
	 * @param mappedRoles  The mappedRoles to set.
	 * @uml.property  name="mappedRoles"
	 */
	public void setMappedRoles(Map<CPFConstants.OperationType, List<String>> mappedRoles) {
		this.mappedRoles = mappedRoles;
	}

	/**
	 * Setter of the property <tt>preference</tt>
	 * @param preference  The preference to set.
	 * @uml.property  name="preference"
	 */
	public void setPreference(CPFPortletPreference preference) {
		this.preference = preference;
	}

	/**
	 * Setter of the property <tt>selectedAttributes</tt>
	 * @param selectedAttributes  The selectedAttributes to set.
	 * @uml.property  name="selectedAttributes"
	 */
	public void setSelectedAttributes(List<CPFAttribute> selectedAttributes) {
		this.selectedAttributes = selectedAttributes;
	}

	/**
	 * Setter of the property <tt>selectedOtherEntities</tt>
	 * @param selectedOtherEntities  The selectedOtherEntities to set.
	 * @uml.property  name="selectedOtherEntities"
	 */
	public void setSelectedOtherEntities(List<ModelAttribute> selectedOtherEntities) {
		this.selectedOtherEntities = selectedOtherEntities;
	}

	/**
	 * Setter of the property <tt>viewType</tt>
	 * @param viewType  The viewType to set.
	 * @uml.property  name="viewType"
	 */
	public void setViewType(CPFConstants.ViewType viewType) {
		this.viewType = viewType;
	}

	public String getExtraListPredicate() {
		return extraListPredicate;
	}

	public void setExtraListPredicate(String extraListPredicate) {
		this.extraListPredicate = extraListPredicate;
	}

	public Map<RelationKey, CPFScreen> getNestedJspNames() {
		return nestedJspNames; //TODO: process all attrib data to see if nesting needed.
	}

	public void setNestedJspNames(Map<RelationKey, CPFScreen> nestedJspNames) {
		this.nestedJspNames = nestedJspNames;
	}

	/** 
	 * @return a map of relation property name (from base entity) and list of selected attributes other than from 
	 * those of baseEntity such as E.g. <Project, <name, id, startDate, endDate>>,<x, <y1, y2>> etc 
	 */
	public Map<RelationKey, List<CPFAttribute>> getNestedAttributes() {
		return nestedAttributes;
	}

	public void setNestedAttributes(
			Map<RelationKey, List<CPFAttribute>> nestedAttributes) {
		this.nestedAttributes = nestedAttributes;
	}

	public IFile getWebServiceRef() {
		return webServiceRef;
	}

	public CPFPortlet getPortletRef() {
		return portletRef;
	}

	public void setPortletRef(CPFPortlet portletRef) {
		this.portletRef = portletRef;
	}

	public void setWebServiceRef(IFile webServiceRef) {
		this.webServiceRef = webServiceRef;
	}

	public WebServiceInfo getWebServiceInfo() {
		return webServiceInfo;
	}

	public void setWebServiceInfo(WebServiceInfo webServiceInfo) {
		this.webServiceInfo = webServiceInfo;
	}

	public java.util.List<CPFConstants.OperationType> getActionsSupported() {
		return actionsSupported;
	}

	public void setActionsSupported(
			java.util.List<CPFConstants.OperationType> actionsSupported) {
		this.actionsSupported = actionsSupported;
	}
	
	public Integer[] getOperationIdPool() {
		return operationIdPool;
	}

	public void setOperationIdPool(Integer[] operationIdPool) {
		this.operationIdPool = operationIdPool;
		//process and update role and op map
	}

	Map<Integer, String[]> temp = new HashMap<Integer, String[]> ();
	public Map<Integer, String[]> getOperationRoleMap (CPFConstants.OperationType o) {

    	List<String> opRolesList = mappedRoles.get (o);
    	String[] opRoles =  new String[opRolesList.size()];
    	int i = 0;
    	for (Iterator<String> it = opRolesList.iterator(); it.hasNext();) {
			opRoles [i++] = it.next();			
		}
    	Integer opId = -1;
    	if (operationIdPool != null
    			&& operationIdPool.length > 0) {
    		opId = operationIdPool[0];
    	}
    	temp.put(opId, opRoles);
    	return temp;
    }


}
