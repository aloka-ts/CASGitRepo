/*
 * OutboundGatewayDAOFactory.java
 *
 */
package com.baypackets.ase.externaldevice.outboundgateway;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;


/**
 * This is an abstract class for obtaining OutboundGatewayDAO objects.
 *
 */
public abstract class OutboundGatewayDAOFactory {
        
    /**
     * Returns a singleton instance of this abstract factory class.
     *
     * @return returns the instance of the this class
     */
    public static OutboundGatewayDAOFactory getInstance() {
        return (OutboundGatewayDAOFactory)Registry.lookup(Constants.NAME_OBGW_DAO_FACTORY);
    }
    
    /**
     * Creates and returns an implementation of the OutboundGatewayDAO
     * interface.
     *
     * @return Outbound Gateway DAO implementation
     */
    public abstract OutboundGatewayDAO getOutboundGatewayDAO();
    
}
