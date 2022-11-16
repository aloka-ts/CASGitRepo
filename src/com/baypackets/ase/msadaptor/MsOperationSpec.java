package com.baypackets.ase.msadaptor;

import java.io.Serializable;

/**
 * The MsOperationSpec class is the base class for the Media Server Operation specification.
 * It defines the ID for the media server operation.
 *
 */
public class MsOperationSpec implements Serializable {
	private static final long serialVersionUID = 284547098438542L;
	String id;
	String connectionId;

	/**
	 * ID for the Media Server Connection.
	 * @return ID for this Media Server Connection.
	 */
	public String getConnectionId() {
		return connectionId;
	}

	/**
	 * Sets the Media Server Connection ID
	 * @param connectionId ID for the Media Server Connection.
	 */
	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}

	/**
	 * ID for the Media Server operation.
	 * @return ID for this media server operation.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the ID for the Media Server operation.
	 * @param id ID for the media server operation.
	 */
	public void setId(String id) {
		this.id = id;
	}
}
