/*
 * CallTransferControllerImpl.java
 *
 * Created on Oct 26, 2005
 */


package com.baypackets.ase.sbb.calltransfer;


import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;


import com.baypackets.ase.sbb.CallTransferController;
import com.baypackets.ase.sbb.ConnectException;
import com.baypackets.ase.sbb.DisconnectException;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.impl.SBBImpl;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.util.Constants;


public class CallTransferControllerImpl extends SBBImpl implements CallTransferController
{
	private static final long serialVersionUID = 201197038547243974L;
        /** Logger element */
	private static Logger logger = Logger.getLogger(CallTransferControllerImpl.class.getName());
	
	/**
	* Constructor
	*/
	public CallTransferControllerImpl()
	{	
		super();
	}


	/** This method is invoked to trasnfer an existing call leg to an address.
	* This method requires that A-party of this call is already connected.
        * @param sessionA - The <code>SipSession</code> object corresponding to
        * established call leg with A-party.
        * @param addressB - The address of the B-party 
        * @throws IllegalArgumentException if sessionA or addressB is null.
        * @throws IllegalStateException if sessionA is not in CONFIRMED state
        * @throws ConnectException if an error occurs while performing CallTransfer.
        */




        public void transfer(SipSession sessionA, Address addressB) throws ConnectException, IllegalStateException, IllegalArgumentException
	{
		if (logger.isDebugEnabled())
			logger.debug("transfer(SipSession, Address): enter");

		if(!isValid())
		{
			throw new IllegalStateException("SBB not valid");
		}

		//
                // Verify arguments
                //
		if(sessionA == null) 
		{
			logger.error("transfer: Argument sessionA is null");
			throw new IllegalArgumentException("Argument sessionA is null");
		}

		if(addressB == null) 
		{
			logger.error("transfer: Argument addressB is null");
			throw new IllegalArgumentException("Argument addressB is null");
		}

                //
                // Verify states
                //
		int stateA = ((Integer)sessionA.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
		if(stateA != Constants.STATE_CONFIRMED) 
		{
			logger.error("sessionA is not in CONFIRMED state");
			throw new IllegalStateException("sessionA is not in CONFIRMED state");
		}

		// Add partyA
		this.addA(sessionA);

		//
		// transfer to party B
		//
		CallTransferHandler callTransferHandler = new CallTransferHandler(addressB);
		this.addSBBOperation(callTransferHandler);
		callTransferHandler.setOperationContext(this);
		try 
		{
			callTransferHandler.start();
		} catch(ProcessMessageException pme) 
		{
			logger.error("transfer: Starting transfer to B", pme);
			throw new ConnectException(pme.getMessage());
		}
		if (logger.isDebugEnabled())
			logger.debug("transfer(SipSession, Address): exit");
	}
}
