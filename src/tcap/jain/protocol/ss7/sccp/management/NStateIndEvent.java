package jain.protocol.ss7.sccp.management;

import jain.*;
import jain.protocol.ss7.*;
import jain.protocol.ss7.sccp.*;

public final class NStateIndEvent extends StateIndEvent {

    /**
    * Constructs a new N-State indication Event, with the Event Source and
    * the affected user and the user status being supplied.
    *
    * @param  source         the Event Source supplied to the constructor
    * @param  affectedUser   the affected user value
    * @param  userStatus     the user status parameter of the N State indication
    */
    public NStateIndEvent(Object source, SccpUserAddress affectedUser, int userStatus) {
        super(source);
        setAffectedUser(affectedUser);
        setUserStatus(userStatus);
    }

	/* GB */
	public NStateIndEvent(Object source) {super(source);}
	/* GB */
    /**
    * Sets the affected user parameter of the N State indication.
    *
    *
    * @param  affectedUser The new affected user value
    */
    public void setAffectedUser(SccpUserAddress affectedUser) {
        m_affectedUser = affectedUser;
    }

    /**
    * Gets the affected user parameter of the N State indication.
    *
    *
    * @return the new affected user value
    */
    public SccpUserAddress getAffectedUser() {
        return m_affectedUser;
    }

    /**
    * Sets the user status parameter of the N State indication.
    *
    *
    * @param  userStatus The new user status value
    *      <UL>
    *        <LI> USER_OUT_OF_SERVICE - Indicates
    *        <LI> USER_IN_SERVICE - Indicates
    *      </UL>
    */
    public void setUserStatus(int userStatus) {
        m_userStatus = userStatus;
    }

    /**
    * Gets the user status parameter of the N State indication.
    *
    *
    * @return the new user status value
    */
    public int getUserStatus() {
        return m_userStatus;
    }

    private SccpUserAddress m_affectedUser = null;
    private int m_userStatus = 0;
    
    public String toString() {
    	return "Affected User = " + m_affectedUser + ", User Status = " + m_userStatus;
    }
}