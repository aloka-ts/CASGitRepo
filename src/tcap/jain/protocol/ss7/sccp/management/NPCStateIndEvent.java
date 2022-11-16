package jain.protocol.ss7.sccp.management;

import jain.*;
import jain.protocol.ss7.*;
import jain.protocol.ss7.sccp.*;

public final class NPCStateIndEvent extends StateIndEvent {

    /**
    * Constructs a new N-PCState indication Event, with the Event Source and
    * the affected DPC and the signaling point status.
    *
    * @param  source         the Event Source supplied to the constructor
    * @param  affectedDpc    the Operation supplied to the constructor
    * @param  signalingPointStatus  the Last Component supplied to the constructor
    */
    public NPCStateIndEvent(Object source, SignalingPointCode affectedDpc, SignalingPointCode ownPointCode, int signalingPointStatus) {
        super(source);
        setAffectedDpc(affectedDpc);
		setOwnPointCode(ownPointCode);
        setSignalingPointStatus(signalingPointStatus);
    }

	public NPCStateIndEvent(Object source) {super(source);}

    /**
    * Sets the affected DPC parameter of the N PCState indication.
    *
    *
    * @param  affectedDpc The new affected DPC value
    */
    public void setAffectedDpc(SignalingPointCode affectedDpc) {
        m_affectedDpc = affectedDpc;
    }

    public void setOwnPointCode(SignalingPointCode ownPointCode) {
        m_ownPointCode = ownPointCode;
    }
    /**
    * Gets the affected DPC parameter of the N PCState indication.
    *
    *
    * @return the new affected DPC value
    */
    public SignalingPointCode getAffectedDpc() {
        return m_affectedDpc;
    }

    public SignalingPointCode getOwnPointCode() {
        return m_ownPointCode;
    }

    /**
    * Sets the signaling point status parameter of the N PCState indication.
    *
    *
    * @param  signalingPointStatus The new user status value
    *      <UL>
    *        <LI> DESTINATION_CONGESTED - Indicates
    *        <LI> DESTINATION_INACCESSIBLE - Indicates
    *        <LI> DESTINATION_ACCESSIBLE - Indicates
    *      </UL>
    */
    public void setSignalingPointStatus(int signalingPointStatus) {
        m_signalingPointStatus = signalingPointStatus;
    }

    /**
    * Gets the signaling point status parameter of the N PCState indication.
    *
    *
    * @return the new signaling point status value
    */
    public int getSignalingPointStatus() {
        return m_signalingPointStatus;
    }

    private SignalingPointCode m_affectedDpc = null;
    private SignalingPointCode m_ownPointCode = null;
    private int m_signalingPointStatus = 0;

    public String toString() {
    	return "Affected DPC = " + m_affectedDpc + ", Own Point Code = " + m_ownPointCode + ", Signalling Point Status = " + m_signalingPointStatus;
    }
}