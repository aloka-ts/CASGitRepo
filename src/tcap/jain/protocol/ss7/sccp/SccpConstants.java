package jain.protocol.ss7.sccp;

public class SccpConstants {
    private SccpConstants(){
    }

    /**
    * Used in N-STATE indication and request.
    */
    public static final int USER_IN_SERVICE = 1;

    /**
    * Used in N-STATE indication and request.
    */
    public static final int USER_OUT_OF_SERVICE = 0;

    /**
    * Used in N-PCSTATE indication.
    */
    public static final int DESTINATION_ACCESSIBLE = 1;

    /**
    * Used in N-PCSTATE indication.
    */
    public static final int DESTINATION_INACCESSIBLE = 0;

    /**
    * Used in N-PCSTATE indication.
    */
    public static final int DESTINATION_CONGESTED = 2;

    /**
    * Used in N-PCSTATE indication.
    */
    public static final int DESTINATION_CONGESTION_ABATEMENT = 3;

}