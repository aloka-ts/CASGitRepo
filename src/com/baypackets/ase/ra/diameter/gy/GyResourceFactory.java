package com.baypackets.ase.ra.diameter.gy;

import javax.servlet.sip.SipApplicationSession;

//import com.baypackets.ase.ra.diameter.gy.enums.AccountingRecordTypeEnum;
import com.baypackets.ase.resource.DefaultResourceFactory;
import com.baypackets.ase.resource.ResourceException;

public interface GyResourceFactory extends DefaultResourceFactory {

	public CreditControlRequest createRequest (SipApplicationSession appSession,int type) throws ResourceException;

}
