/*
 * Created on Jan 18, 2005
 *
 */
package com.baypackets.ase.ocm;

/**
 * @author Dana
 *
 * This class provides rule of how to get begining time and ending time
 */
public class TimeMeasurementRule {
	private Rule beginingRule;
	private Rule endingRule;
	private long targetTime = 3000;
	private float weight = 1.0f;
	
	public TimeMeasurementRule(long targetTime, float weight,
			int beginingSessionIndex, int beginingMsgIndex,
			int endingSessionIndex, int endingMsgIndex) {
		this.targetTime =targetTime;
		beginingRule = new Rule(beginingSessionIndex, beginingMsgIndex);
		endingRule = new Rule(endingSessionIndex, endingMsgIndex);
	}
	
	/**
	 * This construcctor provides default rule
	 *
	 */
	public TimeMeasurementRule() {
		beginingRule = new Rule(0, 0);
		endingRule = new Rule(0, 1);
	}
	
	public long getTargetTime() {
		return targetTime;
	}
	
	public float getWeight() {
		return weight;
	}
	
	public void setTargetTime(long targetTime) {
		this.targetTime = targetTime;
	}
	
	public void setTargetTime(String time){
		this.setTargetTime(Long.parseLong(time));
	}
	
	public void setWeight(float weight) {
		this.weight = weight;
	}

	public void setWeight(String weight) {
		this.setWeight(Float.parseFloat(weight));
	}
	
	public void setBeginSessionIndex(String index){
		this.beginingRule.sessionIndex = Integer.parseInt(index);
	}
	
	public void setBeginMessageIndex(String index){
		this.beginingRule.msgIndex = Integer.parseInt(index);
	}
	
	public void setEndSessionIndex(String index){
		this.endingRule.sessionIndex = Integer.parseInt(index);
	}
	
	public void setEndMessageIndex(String index){
		this.endingRule.msgIndex = Integer.parseInt(index);
	}
	
	public Rule getBeginingRule() {
		return beginingRule;
	}
	
	public Rule getEndingRule() {
		return endingRule;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer("target=" + targetTime + "; ");
		buf.append("start time: " + beginingRule);
		buf.append("end time: " + endingRule);
		return buf.toString();
	}
	
	public class Rule {
		private int sessionIndex;
		private int msgIndex;
		
		public Rule(){
			
		}
		
		public Rule(int sessionIndex, int msgIndex) {
			this.sessionIndex = sessionIndex;
			this.msgIndex = msgIndex;
		}
		
		public int getSessionIndex() {
			return sessionIndex;
		}
		
		public int getMsgIndex() {
			return msgIndex;
		}
		
		public String toString() {
			return "session=" + sessionIndex + " msg=" + msgIndex + "; ";
		}
	}

}
