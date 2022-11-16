package com.baypackets.ase.ra.diameter.rf;
import java.util.Date;

import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
import com.baypackets.ase.ra.diameter.rf.avp.AccountingApplicationIdAvp;
import com.baypackets.ase.ra.diameter.rf.avp.AccountingInterimIntervalAvp;
import com.baypackets.ase.ra.diameter.rf.avp.AccountingRecordNumberAvp;
import com.baypackets.ase.ra.diameter.rf.avp.AccountingRecordTypeAvp;
import com.baypackets.ase.ra.diameter.rf.avp.EventTimestampAvp;
import com.baypackets.ase.ra.diameter.rf.avp.OriginStateIdAvp;
import com.baypackets.ase.ra.diameter.rf.avp.ProxyInfoAvp;
import com.baypackets.ase.ra.diameter.rf.avp.RouteRecordAvp;
import com.baypackets.ase.ra.diameter.rf.avp.ServiceContextIdAvp;
import com.baypackets.ase.ra.diameter.rf.avp.ServiceInformationAvp;
import com.baypackets.ase.ra.diameter.rf.avp.UserNameAvp;
import com.baypackets.ase.ra.diameter.rf.enums.AccountingRecordTypeEnum;
import com.baypackets.ase.resource.ResourceException;

public interface RfAccountingRequest extends RfRequest {

	public int getCommandCode();

	public long getApplicationId();

	public String getName();

	//public Standard getStandard();

	public ValidationRecord validate();

	public RfAccountingResponse createAnswer(long l) throws RfResourceException;

	public RfAccountingResponse createAnswer(long l, long l1) throws RfResourceException;

	public String getSessionId();

	public String getOriginHost() throws ResourceException;

	public String getOriginRealm() throws ResourceException;

	public String getDestinationRealm() throws ResourceException;

	public AccountingRecordTypeAvp addAccountingRecordType(
			AccountingRecordTypeEnum enumaccountingrecordtype)
	throws RfResourceException;

	public int getAccountingRecordType() throws RfResourceException;

	public AccountingRecordTypeEnum getEnumAccountingRecordType()
	throws RfResourceException;

	public AccountingRecordNumberAvp addAccountingRecordNumber(long l)
	throws RfResourceException;

	public long getAccountingRecordNumber() throws RfResourceException;

	public AccountingApplicationIdAvp addAccountingApplicationId(long l)
	throws RfResourceException;

	public long getAccountingApplicationId() throws RfResourceException;

	public UserNameAvp addUserName(String s) throws RfResourceException;

	public String getUserName() throws RfResourceException;

	public AccountingInterimIntervalAvp addAccountingInterimInterval(long l)
	throws RfResourceException;

	public long getAccountingInterimInterval() throws RfResourceException;

	public OriginStateIdAvp addOriginStateId(long l) throws RfResourceException;

	public long getOriginStateId() throws RfResourceException;

	public EventTimestampAvp addEventTimestamp(Date date)
	throws RfResourceException;

	public Date getEventTimestamp() throws RfResourceException;

	public ProxyInfoAvp addGroupedProxyInfo() throws RfResourceException;

	public ProxyInfoAvp[] getGroupedProxyInfos() throws RfResourceException;

	public RouteRecordAvp addRouteRecord(String s) throws RfResourceException;

	public String[] getRouteRecords() throws RfResourceException;

	public ServiceContextIdAvp addServiceContextId(String s)
	throws RfResourceException;

	public String getServiceContextId() throws RfResourceException;

	public ServiceInformationAvp addGroupedServiceInformation()
	throws RfResourceException;

	public ServiceInformationAvp getGroupedServiceInformation()
	throws RfResourceException;

	public static final int code = 271;
	public static final String name = "ACR";
	//public static final Standard standard;
	public static final long applicationId = 3L;

}