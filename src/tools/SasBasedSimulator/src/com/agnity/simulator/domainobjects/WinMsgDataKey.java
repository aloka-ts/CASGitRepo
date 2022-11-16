package com.agnity.simulator.domainobjects;

public class WinMsgDataKey{
	
	private int CallId;
	private String WinMsg;
	
	public WinMsgDataKey(int callId, String winMsg) {
		super();
		CallId = callId;
		WinMsg = winMsg;
	}
	
	@Override
	public boolean equals(Object WinKey) {
		if(WinKey instanceof WinMsgDataKey){
			WinMsgDataKey winMsgDataKey = (WinMsgDataKey)WinKey;
			if((this.CallId==winMsgDataKey.CallId)&&(this.WinMsg.equals(winMsgDataKey.WinMsg)))
				return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hashValue = this.CallId+this.WinMsg.length();
		return hashValue;
	}
	
}