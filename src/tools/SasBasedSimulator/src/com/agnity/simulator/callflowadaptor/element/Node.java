package com.agnity.simulator.callflowadaptor.element;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {

	private String type;
	private int nodeId;
	private int prevNodeId[];

	private String action;
	
	private int sipLeg;
	private boolean isChildType;
	private boolean isInitial;
	

	private List<Node> childNodes;

	private List<Node> subElements;
	
	public Node(String type) {
		this.setType(type);
		childNodes = new ArrayList<Node>();
		subElements=new ArrayList<Node>();
		setChildType(false);
	}

	public Node(String type, boolean isChild) {
		setType(type);
		setChildType(isChild);

		childNodes = new ArrayList<Node>();
		subElements=new ArrayList<Node>();
		setNodeId(-2);
		setPrevNodeIds(null);
	}

	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public int getNodeId() {
		return nodeId;
	}
	//	public void setPrevNodeId(int prevNodeId) {
	//		this.prevNodeId = prevNodeId;
	//	}
	public void setPrevNodeIds(String prevNodes) {
		if(prevNodes == null){
			prevNodeId=null;
			return;
		}
		String[] nodes = prevNodes.split(",");
		this.prevNodeId = new int[nodes.length];
		for(int i=0;i<nodes.length;i++){
			this.prevNodeId[i]=Integer.parseInt(nodes[i]);
		}
	}
	public int[] getPrevNodeIds() {
		return prevNodeId;
	}
	//	public int getPrevNodeId() {
	//		return prevNodeId;
	//	}

	public void addChildNode(Node child){
		this.childNodes.add(child);
	}

	public List<Node> getChildNodes(){
		return this.childNodes;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param sipLeg identifier in B2B mode
	 */
	public void setSipLeg(int sipLeg) {
		this.sipLeg = sipLeg;
	}
	/**
	 * @return the sipLeg
	 */
	public int getSipLeg() {
		return sipLeg;
	}
	
	/**
	 * @param isChildType the isChildType to set
	 */
	public void setChildType(boolean isChildType) {
		this.isChildType = isChildType;
	}

	/**
	 * @return the isChildType
	 */
	public boolean isChildType() {
		return isChildType;
	}

	/**
	 * @param subElements the subElements to set
	 */
	public void addSubElements(Node subElement) {
		subElements.add(subElement);
	}

	/**
	 * @return the subElements
	 */
	public List<Node> getSubElements() {
		return subElements;
	}

	@Override
	public String toString() {
		StringBuilder builder= new StringBuilder();
		//add type
		builder.append(" Node Type='");
		builder.append(getType());
		builder.append("'");
		//add ID
		builder.append(" ID='");
		builder.append(getNodeId());
		builder.append("'");
		//add prevnodeId
		builder.append(" PrevNodeIds='");
		builder.append(getPrevNodeIds());
		builder.append("'");
		//add Action
		builder.append(" Action='");
		builder.append(getAction());
		
		builder.append("'");
		//add Action
		builder.append(" SIP Leg='");
		builder.append(getSipLeg());
		
		builder.append("'");
//		//add Timeout
//		builder.append(" Timeout='");
//		builder.append(getTimeout());
//		builder.append("'");
//		//add TimeoutAction
//		builder.append(" DEFAULT_TCAP_SESSION_TIMEOUT ACtion node='");
//		builder.append(getTimerActionNode());
//		builder.append("'\n");
//		//add subelments
//		builder.append(" Subelemnts='");
//		builder.append(getSubElements());
//		builder.append("'\n");
//		//add child
//		builder.append(" Childs='");
//		builder.append(getChildNodes());
		builder.append("\n");
		
		return builder.toString();
	}

	/**
	 * @param isInitial the isInitial to set
	 */
	public void setInitial(boolean isInitial) {
		this.isInitial = isInitial;
	}

	/**
	 * @return the isInitial
	 */
	public boolean isInitial() {
		return isInitial;
	}


}
