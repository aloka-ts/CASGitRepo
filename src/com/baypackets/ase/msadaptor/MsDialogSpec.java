package com.baypackets.ase.msadaptor;

import java.util.ArrayList;
import java.util.Iterator;

import com.baypackets.ase.sbb.dialog.group.MsGroupSpec;
import com.baypackets.ase.sbb.MsSendSpec;

public class MsDialogSpec extends MsOperationSpec {
	private static final long serialVersionUID = 2838014685429010L;

	public static final int OP_CODE_DIALOG_START = 1;
	public static final int OP_CODE_DIALOG_END = 2;	
	private ArrayList specs = new ArrayList();	
	int operation=OP_CODE_DIALOG_START;//By Default value 
	//private String name;<<-- mapped with ->>Id
	private String mark;
	private String src;
	private String type;
	private MsSendSpec sendTag;
	private MsGroupSpec group; 
	private String exitNameList;
	private String disconnectNameList;
	
	public void addMediaServerSpec(Object spec){
		if(spec != null){
			this.specs.add(spec);
		}
	}
	
	public Iterator getSpecs(){
		return this.specs.iterator();
	}
	
	public void clearSpecs(){
		this.specs.clear();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.setId(name);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.getId();
	}

	/**
	 * @param mark the mark to set
	 */
	public void setMark(String mark) {
		this.mark = mark;
	}

	/**
	 * @return the mark
	 */
	public String getMark() {
		return mark;
	}

	/**
	 * @param src the src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}

	/**
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.setConnectionId(target);
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return this.getConnectionId();
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param sendTag the sendTag to set
	 */
	public void setSendTag(MsSendSpec sendTag) {
		this.sendTag = sendTag;
	}
	
	/**
	 * This method will set send tag values for a dialog
	 */
	public void setSendTag(String target,String event,String namelist) {
		this.sendTag = new MsSendSpec(target, event, namelist);
	}
	
	/**
	 * @return the sendTag of dialog
	 */
	public MsSendSpec getSendTag() {
		return sendTag;
	}
	public void setOperation(int operation){
		this.operation = operation;	
	}
	
	public int getOperation(){
		return this.operation;
	}

	/**
	 * This method sets group element and its child for dialogstart msml element.
	 * @param group the group to set
	 */
	public void setGroup(MsGroupSpec group) {
		this.group = group;
	}

	/**
	 *  This method returns group element and its child for dialogstart msml element.
	 * @return the group
	 */
	public MsGroupSpec getGroup() {
		return group;
	}

	/**
	 * This method sets namelist attribute of exit element for dialogstart msml element
	 * @param exitNameList the exitNameList to set
	 */
	public void setExitNameList(String exitNameList) {
		this.exitNameList = exitNameList;
	}

	/**
	 *  This method returns namelist attribute of exit element for dialogstart msml element
	 * @return the exitNameList
	 */
	public String getExitNameList() {
		return exitNameList;
	}

	/**
	 *  This method sets namelist attribute of disconnect element for dialogstart msml element
	 * @param disconnectNameList the disconnectNameList to set
	 */
	public void setDisconnectNameList(String disconnectNameList) {
		this.disconnectNameList = disconnectNameList;
	}

	/**
	 *  This method returns namelist attribute of disconnect element for dialogstart msml element
	 * @return the disconnectNameList
	 */
	public String getDisconnectNameList() {
		return disconnectNameList;
	}

	
}
