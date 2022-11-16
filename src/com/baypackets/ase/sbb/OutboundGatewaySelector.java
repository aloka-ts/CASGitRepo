/*
 * OutboundGatewaySelector.java
 *
 */
package com.baypackets.ase.sbb;

/**
 * The OutboundGatewaySelector interface defines the APIs for selecting the 
 * outbound gateways during runtime, based on the outbound gateway status, attributes, etc
 * 
 * <p>
 * The selectXXX methods always returns a outbound gateway that is in the ACTIVE state.
 * If the selection criteria returns more than one ACTIVE outbound gateways,
 * the implementation selects one of the available outbound gateways using the
 * configured load balancing algorithm. 
 * If the criteria does not match any ACTIVE outbound gateway, NULL is returned.
 * 
 *<p>
 *	The Outbound Gateway Manager object is available as an attribute
 *	through the ServletContext interface with the name 
 *	<code>"com.baypackets.ase.sbb.OutboundGatewaySelector"</code>.
 *	Any application can get the Outbound Gateway instance as follows:
 *<pre>	
 *<code>
 *	 OutboundGatewaySelector obgwSelector = (OutboundGatewaySelector) getServletContext().getAttribute(OutboundGatewaySelector.class.getName());
 *</code>
 *</pre>	 
 *
 */
public interface OutboundGatewaySelector {

    /**
     * Return an indication if outbound gateway processing is needed
     *
     * @return true if outbound gateway processing is needed
     */
    public boolean processingActive();

    /**
     * Selects an ACTIVE outbound gateway.
     *
     * <p>
     * Returns NULL if there are no outbound gateways are available
     * </p>
     * 
     * @return The matching Outbound Gateway object or NULL.
     */
    public OutboundGateway select();
    
    /**
     * Selects an ACTIVE outbound gateway from same group.
     *
     * <p>
     * Returns NULL if there are no outbound gateways are available in group specified by groupId
     * </p>
     * 
     * @return The matching Outbound Gateway object or NULL.
     */
    public OutboundGateway selectFromGroup(String groupId);
    
    /**
     * Selects an ACTIVE outbound gateway from same group except outbound gateway specified by outboundgatewayId parameter.
     *
     * <p>
     * Returns NULL if there are no outbound gateways are available in group specified by groupId
     * </p>
     * 
     * @return The matching Outbound Gateway object or NULL.
     */
    public OutboundGateway selectFromGroupExcept(String groupId,String outboundgatewayId);

}

