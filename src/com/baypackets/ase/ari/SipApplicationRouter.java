package com.baypackets.ase.ari;

import java.util.List;
import java.io.Serializable;

import javax.servlet.sip.SipServletRequest;

public interface SipApplicationRouter {

    /**
     * Initializes the SipApplicationRouter.
     *
     */
    public void init();

    /**
     * Container notifies application router that new applications are deployed
     *
     * @param newlyDeployedApplicationNames - A list of names of the newly added applications
     */
    public void applicationDeployed(List<String> newlyDeployedApplicationNames);

    /**
     * Container notifies application router that some applications are undeployed
     *
     * @param undeployedApplicationNames - A list of names of the undeployed applications
     */
    public void applicationUndeployed(List<String> undeployedApplicationNames);

    /**
     * Container notifies application router that some applications are undeployed
     *
     * @param initialRequest - The initial request for which the container is asking
     *    for application selection. The request must not be modified by the AR.
     *    It is recommended that the implementations explicitly disallow any mutation
     *    action by throwing appropriate RuntimeException like IllegalStateException.
     * @param region - Which region the application selection process is in
     * @param directive - The routing directive used in creating this request. If
     *    this is a request received externally, directive is NEW.
     * @param stateInfo - If this request is relayed from a previous request by an
     *    application, this is the stored state the application router returned earlier
     *    when invoked to handle the previous request.
     *
     * @return  Next application routing info.
     */
   /** public SipApplicationRouterInfo getNextApplication(SipServletRequest initialRequest,
        SipApplicationRoutingRegion region,
        SipApplicationRoutingDirective directive,
        Serializable stateInfo) throws NullPointerException;
**/
}
