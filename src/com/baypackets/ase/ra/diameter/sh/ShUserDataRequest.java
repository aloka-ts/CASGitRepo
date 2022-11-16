package com.baypackets.ase.ra.diameter.sh;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
import com.baypackets.ase.resource.ResourceException;
import fr.marben.diameter.DiameterAVP;
import fr.marben.diameter._3gpp.sh.DiameterShMessageFactory;

import java.math.BigInteger;
import java.util.ArrayList;

public interface ShUserDataRequest extends ShRequest {

    public static final String name = "UDR";
    
 // todo   /** what should be application id*/
    public static final long applicationId = 15L;

    /**
     *  Create an answer with a given result code.
     */
    public ShUserDataResponse createAnswer(String resultCode) throws ShResourceException ;

    /**
     *  Create an answer with a given vendor specific experimental result code.
     */
    public ShUserDataResponse createAnswer(long vendorId, int experimentalResultCode) throws ShResourceException ;

     /**
     *  This method returns the applicationId associated with this message.
     */
    public long getApplicationId() ;

    /**
     *  This method returns the time stamp associated with this message.
     */
    public long getTimestamp() ;

    /**
     *  Retrieving a single Unsigned32 value from AuthApplicationId AVPs.
     */
    public long getAuthApplicationId() throws ShResourceException ;

    public int getCommandCode();


    /**
     *  Retrieving a single DiameterIdentity value from DestinationHost AVPs.
     */
    public java.lang.String getDestinationHost() ;

    /**
     *  Retrieving a single DiameterIdentity value from DestinationRealm AVPs.
     */
    public java.lang.String getDestinationRealm() throws ResourceException ;

    /**
     *  This method returns the name associated with this message.
     */
    public java.lang.String getName() ;


    /**
     *  Retrieving a single DiameterIdentity value from OriginHost AVPs.
     */
    public java.lang.String getOriginHost() throws ResourceException;;

    /**
     *  Retrieving a single DiameterIdentity value from OriginRealm AVPs.
     */
    public java.lang.String getOriginRealm() throws ResourceException; ;

    /**
     *  Retrieving multiple DiameterIdentity values from RouteRecord AVPs.
     */
    public ArrayList<DiameterAVP> getRouteRecords() throws ShResourceException ;

    /**
     *  Retrieving a single UTF8String value from ServiceContextId AVPs.
     */
    public java.lang.String getServiceContextId() throws ShResourceException ;

    /**
     *  Retrieving a single UTF8String value from SessionId AVPs.
     */
    public java.lang.String getSessionId() ;

    /**
     *  This method returns the standard associated with this message.
     */
    /**
     *  Runs content validation of the message.
     */
    public ValidationRecord validate() ;

    void addServiceContextId(String value)
            throws ShResourceException;

    void addDiameterOctetStringAVP(String name,
                                   String vendorName, String value);

    void addDiameterInteger32AVP(String name, int value,
                                 String vendorName);

    void addDiameterInteger64AVP(String name, long value,
                                 String vendorName);

    void addDiameterUnsigned32AVP(String name, long value,
                                  String vendorName);

    void addDiameterUnsigned64AVP(String name,
                                  BigInteger value, String vendorName);

    void addDiameterFloat32AVP(String name, String vendorName,
                               float value);

    void addDiameterFloat64AVP(String name, String vendorName,
                               double value);

    void addDiameterGenericAVP(long avpCode, long vendorId,
                               byte[] value);

    void addDiameterOctetStringAVP(String name,
                                   String vendorName, byte[] value);

    AvpDiameterGrouped addDiameterGroupedAVP(String avpName, String vendorName);

    void addDiameterAVPs(ArrayList<DiameterAVP> groupedAvps);

    DiameterShMessageFactory getDiameterShMessageFactory();

    void setDestinationHost(String host);

    void setServiceContextId(String contextId);

}
