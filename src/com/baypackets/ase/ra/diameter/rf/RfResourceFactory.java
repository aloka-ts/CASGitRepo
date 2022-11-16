package com.baypackets.ase.ra.diameter.rf;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.ra.diameter.rf.enums.AccountingRecordTypeEnum;
import com.baypackets.ase.resource.DefaultResourceFactory;
import com.baypackets.ase.resource.ResourceException;

public interface RfResourceFactory extends DefaultResourceFactory {

	//public ShRequest createRequest(SipApplicationSession appSession, int type) throws ResourceException;

	public RfAccountingRequest createRequest (SipApplicationSession appSession,int type) throws ResourceException;

}
