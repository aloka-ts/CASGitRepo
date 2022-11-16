/*
 * FileBasedGwDAOFactory.java
 *
 */
package com.baypackets.ase.externaldevice.outboundgateway;

import com.baypackets.ase.util.Constants;
import java.io.File;

/**
 * Factory class to return the correct Outbound Gateway DAO object for File Based DAO
 */
public class FileBasedGwDAOFactory extends OutboundGatewayDAOFactory {
    
    /**
     * Instantiates and returns a FileBasedGwDAO object.
     * @return OutboundGatewayDAO object
     * @see com.baypackets.ase.externaldevice.outboundgateway.FileBasedGwDAO
     */
    public OutboundGatewayDAO getOutboundGatewayDAO() {        
        return new FileBasedGwDAO(new File(Constants.ASE_HOME, Constants.FILE_OUTBOUND_GATEWAY_CONFIG).toURI());        
    }
    
}
