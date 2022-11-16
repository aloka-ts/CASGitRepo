/*
 * OutboundGatewayImpl.java
 *
 */
package com.baypackets.ase.externaldevice.outboundgateway;

import com.baypackets.ase.externaldevice.ExternalDeviceImpl;
import com.baypackets.ase.sbb.OutboundGateway;
import com.baypackets.ase.util.StringManager;

/**
 * The implementation of the OutboundGateway interface.
 */
public class OutboundGatewayImpl extends ExternalDeviceImpl implements OutboundGateway, java.io.Serializable {

    private static StringManager _strings = StringManager.getInstance(OutboundGatewayImpl.class.getPackage());
    private static final long serialVersionUID = 409140336094204825L;
    private String groupId="Default";
	private int isRemote;
    
    public OutboundGatewayImpl() {
        super();
        setPort(5060);
    }
      
    /* (non-Javadoc)
     * @see com.baypackets.ase.sbb.OutboundGateway#getGroupId()
     */
    @Override
    public String getGroupId() {
		return groupId;
	}

    /* (non-Javadoc)
     * @see com.baypackets.ase.sbb.OutboundGateway#setGroupId(java.lang.String)
     */
    @Override
	public void setGroupId(String groupId) {
		if(groupId==null || groupId.trim().isEmpty()){
			throw new IllegalArgumentException("Group Id is NULL or empty");
		}
		this.groupId = groupId;
	}
    
    public int getIsRemote() {
		return isRemote;
	}

	public void setIsRemote(int isRemote) {
		this.isRemote = isRemote;
	}


	/**
     * Returns a string representation of this OutboundGateway object.
     * @return Human readable String for this object.
     */
    public String toString() {
        Object[] params = new Object[8];
        params[0] = this.getId();
        params[1] = this.getGroupId();
        params[2] = this.getHost() != null ? this.getHost().getHostAddress() : "";
        params[3] = String.valueOf(this.getPort());

        if (this.getState() == STATE_ACTIVE) {
            params[4] = _strings.getString("OutboundGatewayImpl.STATE_ACTIVE");
        } else if (this.getState() == STATE_DOWN) {
            params[4] = _strings.getString("OutboundGatewayImpl.STATE_DOWN");
        } else {
            params[4] = _strings.getString("OutboundGatewayImpl.STATE_SUSPECT");
        }
        if (this.getHeartbeatUri() != null) {
            params[5] = this.getHeartbeatUri();
        } else {
            params[5] = "";
        }
        params[6] = (this.isHeartbeatEnabled()) ?
            _strings.getString("OutboundGatewayImpl.ON") :
            _strings.getString("OutboundGatewayImpl.OFF");

        params[7] = (this.getPriority() == java.lang.Integer.MAX_VALUE) ?
            "-" :
            String.valueOf(this.getPriority());

        return _strings.getString("OutboundGatewayImpl.toString", params);
    }
}
