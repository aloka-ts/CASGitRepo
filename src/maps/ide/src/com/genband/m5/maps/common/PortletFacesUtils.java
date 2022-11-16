package com.genband.m5.maps.common;

import javax.faces.context.FacesContext;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

public final class PortletFacesUtils {     
    private PortletFacesUtils() {
    }     
    
    public static PortletRequest getPortletRequest (FacesContext context) throws Exception {        
        Object request = context.getExternalContext().getRequest();
        if (request instanceof PortletRequest) {             
            return (PortletRequest) request;
        } else {
            throw new Exception ("Portlet run outside the portal environment or portlet request class is not generic");
        }
    }

    public static PortletResponse getPortletResponse (FacesContext context) throws Exception {
        Object response = context.getExternalContext().getResponse();
        if (response instanceof PortletResponse) {
            return (PortletResponse) response;
        } else {
            throw new Exception ("Portlet run outside the portal environment or portlet response class is not generic");
        }
    }

    public static PortletPreferences getPortletPreferences (FacesContext context) throws Exception {
        PortletRequest request = getPortletRequest(context);
        return request.getPreferences();
    }
}
