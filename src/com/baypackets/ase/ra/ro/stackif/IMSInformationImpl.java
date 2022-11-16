/**
 * Filename:	IMSInformationImpl.java
 * Created On:	12-Oct-2006
 */

package com.baypackets.ase.ra.ro.stackif;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.io.Serializable;

import com.baypackets.ase.ra.ro.IMSInformation;
import com.baypackets.ase.ra.ro.Address;
import com.baypackets.ase.ra.ro.EventType;
import com.baypackets.ase.ra.ro.TimeStamps;
import com.baypackets.ase.ra.ro.InterOperatorIdentifier;
import com.baypackets.ase.ra.ro.ServerCapabilities;
import com.baypackets.ase.ra.ro.TrunkGroupId;
import com.baypackets.ase.ra.ro.MessageBody;
import com.baypackets.ase.ra.ro.ApplicationServerInformation;
import com.baypackets.ase.ra.ro.SDPMediaComponent;
import com.baypackets.ase.ra.ro.util.UnmodifiableIterator;

public class IMSInformationImpl implements IMSInformation , Serializable {

	private com.condor.chargingcommon.ImsInfo _imsInfo;
	private boolean _readonly = false;

	private EventTypeImpl _evtType;
	private TimeStampsImpl _timeStamps;
	private InterOperatorIdentifierImpl _ioId;
	private ServerCapabilitiesImpl _servCaps;
	private TrunkGroupIdImpl _tgId;
	private MessageBodyImpl _msgBody;
	private List _asInfoList;
	private List _sessDescList;
	private List _medCompList;

	public IMSInformationImpl() {
		this._imsInfo = new com.condor.chargingcommon.ImsInfo();
	}

	public IMSInformationImpl(com.condor.chargingcommon.ImsInfo imsInfo) {
		this._imsInfo = imsInfo;

		if(this._imsInfo.getIsEventTypePresent()) {
			this._evtType = new EventTypeImpl(this._imsInfo.getEventType());
		}

		if(this._imsInfo.getIsTimeStampPresent()) {
			this._timeStamps = new TimeStampsImpl(this._imsInfo.getTimeStamp());
		}

		if(this._imsInfo.getIsIntrOprtrIdtfrPresent()) {
			this._ioId = new InterOperatorIdentifierImpl(this._imsInfo.getIntrOprtrIdtfr());
		}

		if(this._imsInfo.getIsServerCapabilityPresent()) {
			this._servCaps = new ServerCapabilitiesImpl(this._imsInfo.getServerCapability());
		}

		if(this._imsInfo.getIsTrunkGroupIdPresent()) {
			this._tgId = new TrunkGroupIdImpl(this._imsInfo.getTrunkGroupId());
		}

		if(this._imsInfo.getIsMessageBodyPresent()) {
			this._msgBody = new MessageBodyImpl(this._imsInfo.getMessageBody());
		}

		int num = 0;

		// Copy Application Server Information list
		num = (new Byte(this._imsInfo.getNoOfAppServInfo())).intValue();
		if(num > 0) {
			this._asInfoList = new LinkedList();
			for(int idx = 0; idx < num; ++idx) {
				this._asInfoList.add(
					new ApplicationServerInformationImpl(this._imsInfo.getAppServInfo(idx)));
			}
		}

		// Copy SDP Session Description list
		num = (new Byte(this._imsInfo.getNoOfSDPSesDscptn())).intValue();
		if(num > 0) {
			this._sessDescList = new LinkedList();
			for(int idx = 0; idx < num; ++idx) {
				this._sessDescList.add(this._imsInfo.getSdpSesDscptn(idx));
			}
		}

		// Copy SDP Media Component list
		num = (new Byte(this._imsInfo.getNoOfSDPMediaCmpnt())).intValue();
		if(num > 0) {
			this._medCompList = new LinkedList();
			for(int idx = 0; idx < num; ++idx) {
				this._medCompList.add(
					new SDPMediaComponentImpl(this._imsInfo.getSdpMediaCmpnt(idx)));
			}
		}

		this._readonly = true;
	}

	public EventType getEventType() {
		return this._evtType;
	}
	
	public void setEventType(EventType et) {
		this.checkReadOnly();

		this._evtType = (EventTypeImpl)et;
	}

	public short getRoleOfNode() {
		return (short)this._imsInfo.getRoleOfNode();
	}

	public void setRoleOfNode(short role) {
		this.checkReadOnly();

		this._imsInfo.setRoleOfNode(role);
	}

	public short getNodeFunctionality() {
		return (short)this._imsInfo.getNodeFnclty();
	}

	public void setNodeFunctionality(short func) {
		this.checkReadOnly();

		this._imsInfo.setNodeFnclty(func);
	}

	public String getUserSessionId() {
		return this._imsInfo.getUserSessId();
	}

	public void setUserSessionId(String id) {
		this.checkReadOnly();

		this._imsInfo.setUserSessId(id);
	}

	public String getCallingPartyAddress() {
		return this._imsInfo.getCallingPrtyAdrs();
	}

	public void setCallingPartyAddress(String addr) {
		this.checkReadOnly();

		this._imsInfo.setCallingPrtyAdrs(addr);
	}

	public String getCalledPartyAddress() {
		return this._imsInfo.getCalledPrtyAdrs();
	}

	public void setCalledPartyAddress(String addr) {
		this.checkReadOnly();

		this._imsInfo.setCalledPrtyAdrs(addr);
	}

	public TimeStamps getTimeStamps() {
		return this._timeStamps;
	}

	public void setTimeStamps(TimeStamps ts) {
		this.checkReadOnly();

		this._timeStamps = (TimeStampsImpl)ts;
	}

	public InterOperatorIdentifier getInterOperatorIdentifier() {
		return this._ioId;
	}

	public void setInterOperatorIdentifier(InterOperatorIdentifier ioi) {
		this.checkReadOnly();

		this._ioId = (InterOperatorIdentifierImpl)ioi;
	}

	public String getIMSChargingIdentifier() {
		return this._imsInfo.getImsChargingID();
	}

	public void setIMSChargingIdentifier(String id) {
		this.checkReadOnly();

		this._imsInfo.setImsChargingID(id);
	}

	public Address getGGSNAddress() {
		if(this._imsInfo.getGgsnAddress() != null) {
			return new Address(this._imsInfo.getGgsnAddress());
		} else {
			return null;
		}
	}

	public void setGGSNAddress(Address addr) {
		this.checkReadOnly();

		if(addr != null) {
			this._imsInfo.setGgsnAddress(addr.get());
		} else {
			this._imsInfo.setGgsnAddress(null);
		}
	}

	public Address getServedPartyIPAddress() {
		if(this._imsInfo.getSrvdPrtyIpAdrs() != null) {
			return new Address(this._imsInfo.getSrvdPrtyIpAdrs());
		} else {
			return null;
		}
	}

	public void setServedPartyIPAddress(Address addr) {
		this.checkReadOnly();

		if(addr != null) {
			this._imsInfo.setSrvdPrtyIpAdrs(addr.get());
		} else {
			this._imsInfo.setSrvdPrtyIpAdrs(null);
		}
	}

	public ServerCapabilities getServerCapabilities() {
		return this._servCaps;
	}

	public void setServerCapabilities(ServerCapabilities servCaps) {
		this.checkReadOnly();

		this._servCaps = (ServerCapabilitiesImpl)servCaps;
	}

	public TrunkGroupId getTrunkGroupId() {
		return this._tgId;
	}

	public void setTrunkGroupId(TrunkGroupId tgId) {
		this.checkReadOnly();

		this._tgId = (TrunkGroupIdImpl)tgId;
	}

	public byte[] getBearerService() {
		if(this._imsInfo.getBearerService() != null) {
			return this._imsInfo.getBearerService().getBytes();
		} else {
			return null;
		}
	}

	public void setBearerService(byte[] bearerService) {
		this.checkReadOnly();

		if(bearerService != null) {
			this._imsInfo.setBearerService(new String(bearerService));
		} else {
			this._imsInfo.setBearerService(null);
		}
	}

	public String getServiceId() {
		return this._imsInfo.getServiceId();
	}

	public void setServiceId(String id) {
		this.checkReadOnly();

		this._imsInfo.setServiceId(id);
	}

	public String getServiceSpecificData() {
		return this._imsInfo.getServiceSpecData();
	}

	public void setServiceSpecificData(String ssData) {
		this.checkReadOnly();

		this._imsInfo.setServiceSpecData(ssData);
	}

	public MessageBody getMessageBody() {
		return this._msgBody;
	}

	public void setMessageBody(MessageBody msgBody) {
		this.checkReadOnly();

		this._msgBody = (MessageBodyImpl)msgBody;
	}

	public int getCauseCode() {
		return this._imsInfo.getCauseCode();
	}

	public void setCauseCode(int causeCode) {
		this.checkReadOnly();

		this._imsInfo.setCauseCode(causeCode);
	}

	// Application Server Information
	public Iterator getApplicationServerInformations() {
		if(this._asInfoList != null) {
			return new UnmodifiableIterator(this._asInfoList.iterator());
		} else {
			return null;
		}
	}

	public void addApplicationServerInformation(ApplicationServerInformation asInfo) {
		this.checkReadOnly();

		if(this._asInfoList == null) {
			this._asInfoList = new LinkedList();
		}

		this._asInfoList.add(asInfo);
	}

	public boolean removeApplicationServerInformation(ApplicationServerInformation asInfo) {
		this.checkReadOnly();

		if(this._asInfoList != null) {
			return this._asInfoList.remove(asInfo);
		} else {
			return false;
		}
	}

	// SDP Session Description
	public Iterator getSDPSessionDescriptions() {
		if(this._sessDescList != null) {
			return new UnmodifiableIterator(this._sessDescList.iterator());
		} else {
			return null;
		}
	}

	public void addSDPSessionDescription(String sdpDesc) {
		this.checkReadOnly();

		if(this._sessDescList == null) {
			this._sessDescList = new LinkedList();
		}

		this._sessDescList.add(sdpDesc);
	}

	public boolean removeSDPSessionDescription(String sdpDesc) {
		this.checkReadOnly();

		if(this._sessDescList != null) {
			return this._sessDescList.remove(sdpDesc);
		} else {
			return false;
		}
	}

	// SDP Media Components
	public Iterator getSDPMediaComponents() {
		if(this._medCompList != null) {
			return new UnmodifiableIterator(this._medCompList.iterator());
		} else {
			return null;
		}
	}

	public void addSDPMediaComponent(SDPMediaComponent medComp) {
		this.checkReadOnly();

		if(this._medCompList == null) {
			this._medCompList = new LinkedList();
		}
		
		this._medCompList.add(medComp);
	}

	public boolean removeSDPMediaComponent(SDPMediaComponent medComp) {
		this.checkReadOnly();

		if(this._medCompList != null) {
			return this._medCompList.remove(medComp);
		} else {
			return false;
		}
	}

	public com.condor.chargingcommon.ImsInfo getStackImpl() {
		if(this._readonly) {
			return this._imsInfo;
		}

		this._readonly = true;

		// Copy Event-Type
		if(this._evtType != null) {
			this._imsInfo.setEventType(this._evtType.getStackImpl());
		}

		// Copy Time-Stamps
		if(this._timeStamps != null) {
			this._imsInfo.setTimeStamp(this._timeStamps.getStackImpl());
		}

		// Copy Inter-Operator-Identifier
		if(this._ioId != null) {
			this._imsInfo.setIntrOprtrIdtfr(this._ioId.getStackImpl());
		}

		// Copy Server-Capabilities
		if(this._servCaps != null) {
			this._imsInfo.setServerCapability(this._servCaps.getStackImpl());
		}

		// Copy Trunk-Group-Id
		if(this._tgId != null) {
			this._imsInfo.setTrunkGroupId(this._tgId.getStackImpl());
		}

		// Copy Message-Body
		if(this._msgBody != null) {
			this._imsInfo.setMessageBody(this._msgBody.getStackImpl());
		}

		// Copy Application-Server-Information
		if(this._asInfoList != null) {
			this._imsInfo.initAppServerInfo((byte)this._asInfoList.size());

			int index = 0;
			Iterator iter = this._asInfoList.iterator();
			while(iter.hasNext()) {
				ApplicationServerInformationImpl asi = (ApplicationServerInformationImpl)iter.next();
				this._imsInfo.setAppServInfo(asi.getStackImpl(), index++);
			}
		}

		// Copy SDP-Session-Description
		if(this._sessDescList != null) {
			this._imsInfo.initSdpSessionDescription((byte)this._sessDescList.size());

			int index = 0;
			Iterator iter = this._sessDescList.iterator();
			while(iter.hasNext()) {
				String sd = (String)iter.next();
				this._imsInfo.setSdpSesDscptn(sd, index++);
			}
		}

		// Copy SDP-Media-Component
		if(this._medCompList != null) {
			this._imsInfo.initSdpMediaComponent((byte)this._medCompList.size());

			int index = 0;
			Iterator iter = this._medCompList.iterator();
			while(iter.hasNext()) {
				SDPMediaComponentImpl smc = (SDPMediaComponentImpl)iter.next();
				this._imsInfo.setSdpMediaCmpnt(smc.getStackImpl(), index++);
			}
		}

		return this._imsInfo;
	}

	private void checkReadOnly() {
		if(this._readonly) {
			throw new IllegalStateException("Cannot modify this message field");
		}
	}
}

