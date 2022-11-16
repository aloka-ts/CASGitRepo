package com.agnity.simulator.callflowadaptor.element.type;

import org.apache.log4j.Logger;

import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.utils.Constants;

public class IfNode extends Node {
	private static Logger logger = Logger.getLogger(IfNode.class);
	private static final String EQUALS_TO="=";
	private static final String NOT_EQUALS_TO="!=";
	private static final String NULL="null";
	
	private String condType;
	private String[] condValues;
	private String condOperator;
	private int nextNodeId;


	public IfNode(){
		super(Constants.IF_NODE);
		this.setAction(Constants.NO_ACTION);
	}
	
	
	
	public void setCondType(String condType) {
		this.condType = condType;
	}
	public String getCondType() {
		return condType;
	}
	public void setCondValue(String condValue) {
		//System.out.println("TESTPK "+condValue);
		this.condValues = condValue.split(",");
		for(int i=0;i<condValues.length;i++){
			//System.out.println("vvvv"+condValues[i]);
		}
	}
	public String[] getCondValue() {
		return condValues;
	}
	
	public void setNextNodeId(int nextNodeId) {
		this.nextNodeId = nextNodeId;
	}
	
	public int getNextNodeId() {
		logger.debug("Returning next node id = "+nextNodeId);
			return nextNodeId;
	}
	
	public boolean isMatched(String condValue){
		logger.debug("Inside isMatched "+this );
		boolean result=false;
		if(condValue==null && this.condOperator.equals(NULL)){
			if(condValue==null){
				result=true;
			}
		}else if(condValue!=null && this.condOperator.equals(EQUALS_TO)){
			for(int i=0;i<condValues.length;i++){
				logger.debug("Compairing to value "+condValues[i]);
				if(this.condValues[i].equals("*") || this.condValues[i].equals(condValue)){
					result=true;
					break;
				}
			}
		}else if(condValue!=null && this.condOperator.equals(NOT_EQUALS_TO)){
			result=true;
			for(int i=0;i<condValues.length;i++){
				if(this.condValues[i].equals(condValue)){
					result=false;
					break;
				}
			}
		}else if(condValue!=null && this.condOperator.equals(NULL)){
			if(condValue==null || condValue.length()==0){
				result=true;
			}
		}
		
		return result;
	}

	public void setCondOperator(String condOperator) {
		System.out.println("OPERATOR "+condOperator);
		this.condOperator = condOperator;
	}

	public String getCondOperator() {
		return condOperator;
	}
}

