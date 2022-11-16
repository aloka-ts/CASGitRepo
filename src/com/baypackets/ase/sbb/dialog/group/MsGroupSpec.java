/**
 * MsDialogGroupSpec.java
 *
 * 
 * @author Amit Baxi
 */
package com.baypackets.ase.sbb.dialog.group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.baypackets.ase.sbb.MsSendSpec;
/**
 * This class is created for MSML Dialog Group Package support for rfc 5707.
 *	
 */
public class MsGroupSpec implements Serializable {
	
	public static final String PARALLEL="parallel";
	public static final String SERIAL="serial";
	public static final String FULL_DUPLEX="fullduplex";
	// constants for group event
	public static final String GROUP_EVENT_TERMINATE="terminate";
	private ArrayList specs = new ArrayList();
	private String groupTopology;
	private String groupId;
	private MsSendSpec groupexitSend;
	private ArrayList<MsGroupSpec> groups = new ArrayList<MsGroupSpec>();
	
	/**
	 * This method sets topology attribute of group element.
	 * @param groupTopology the groupTopology to set
	 */
	public void setGroupTopology(String groupTopology) {
		this.groupTopology = groupTopology;
	}

	/**
	 * This method returns topology attribute of group element.
	 * @return the groupTopology
	 */
	public String getGroupTopology() {
		return groupTopology;
	}
	/**
	 * This method sets id attribute of group element.Optional attribute.
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * This method returns id attribute of group element.Optional attribute.
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}
	
	/**
	 * This method sets send tag to be nested in groupexit element.
	 * @param group_send the groupexit send to set
	 */
	public void setGroupexitSendTag(MsSendSpec groupexit_send) {
		this.groupexitSend = groupexit_send;
	}
	
	/**
	 * This method sets send tag to be nested in groupexit element.
	 */
	public void setGroupexitSendTag(String target,String event,String namelist) {
		this.groupexitSend = new MsSendSpec(target, event, namelist);
	}
	
	/**
	 * This method returns send tag to be nested in groupexit element.
	 * @return the groupexit send of dialog
	 */
	public MsSendSpec getGroupexitSendTag() {
		return groupexitSend;
	}
	/**
	 * This method adds media server specs for group element in sepcs list.
	 *  @param spec the specs to add
	 */
	public void addMediaServerSpec(Object spec){
		if(spec != null){
			this.specs.add(spec);
		}
	}
	/**
	 * This method returns iterator of group specs list.
	 *  @return iterator of specs list
	 */
	public Iterator getSpecs(){
		return this.specs.iterator();
	}
	/**
	 * This method clears media server specs for group element from spec list.
	 */
	public void clearSpecs(){
		this.specs.clear();
	}
	/**
	 * This method adds media server group spec to be nested in this group spec.
	 *  @param spec the groups to add
	 */
	public void addGroupSpec(MsGroupSpec spec){
		if(spec != null){
			this.groups.add(spec);
		}
	}
	/**
	 * This method returns iterator of group specs list to be nested in this group spec.
	 *  @return iterator of groups specs list
	 */
	public Iterator getGroupSpecs(){
		return this.groups.iterator();
	}
	/**
	 * This method clears group specs list to be nested in this group spec.
	 */ 
	public void clearGroupSpecs(){
		this.groups.clear();
	}
}
