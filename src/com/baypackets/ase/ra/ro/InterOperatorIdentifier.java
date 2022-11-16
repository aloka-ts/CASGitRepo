/**
 * Filename:	InterOperatorIdentifier.java
 * Created On:	11-Oct-2006
 */

package com.baypackets.ase.ra.ro;

/**
 * This class defines the Inter-Operator-Identifier AVP that is part of an 
 * credit control request.
 *
 * Application can use it's methods to fill various fields of Inter-Operator-Identifier AVP.
 *
 * @author Neeraj Jain
 *
 */

public interface InterOperatorIdentifier {
	
    /**
     * This method is used by application to get Originating-IOI AVP
     *
     * @return String object containing Originating-IOI AVP
     */

	public String getOriginatingIOI();

    /**
     * This method is used by application to get Terminating-IOI AVP
     *
     * @return String object containing Terminating-IOI AVP
     */

	public String getTerminatingIOI();

    /**
     * This method is used by application to set Originating-IOI AVP
     *
     * @param origIOI - Originating-IOI AVP to be set
     */

	public void setOriginatingIOI(String origIOI);

    /**
     * This method is used by application to set Terminating-IOI AVP
     *
     * @param termIOI - Terminating-IOI AVP to be set
     */

	public void setTerminatingIOI(String termIOI);
}

