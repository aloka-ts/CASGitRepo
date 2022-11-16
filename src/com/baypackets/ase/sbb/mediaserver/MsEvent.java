package com.baypackets.ase.sbb.mediaserver;

import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.MsOperationResult;

public class MsEvent extends SBBEvent {

	private MsOperationResult result;

	public MsEvent(MsOperationResult result) {
		this.result = result;
	}

	public MsOperationResult getResult() {
		return this.result;
	}

}
