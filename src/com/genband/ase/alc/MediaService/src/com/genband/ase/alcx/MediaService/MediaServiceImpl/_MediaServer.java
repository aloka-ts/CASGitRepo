package com.genband.ase.alcx.MediaService.MediaServiceImpl;

import java.net.InetAddress;
import java.net.URI;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.Iterator;

import com.baypackets.ase.sbb.MediaServer;

import com.genband.ase.alc.alcml.jaxb.ServiceContext;

public class _MediaServer implements MediaServer
{
    public _MediaServer(ServiceContext CurrentServiceContext)
    {
            this.sContext = CurrentServiceContext;
    }

    public void setHeartbeatUri(String uri) { }
    public String getHeartbeatUri() { return null; }
    public boolean isHeartbeatEnabled() { return false; }
    public void disableHeartbeat() { }
    public void enableHeartbeat() { }
    public int getPriority() { return 0; }
    public int getCapabilities() { return 0; }
    public int getState() { return 0xdeadbeef; }

    public InetAddress getHost() { try { return InetAddress.getByName((String)sContext.getAttribute("MEDIA_SERVER_IP")); } catch (Exception e) { System.out.println(e); } return null; }
    public String getId() { return null; }
    public String getName() { return null; }
    public int getPort() { return Integer.parseInt((String)sContext.getAttribute("MEDIA_SERVER_PORT")); }
    public boolean isActive() { return true; }
    public boolean isCapable(int capabilities) { return true; }
    public URI getAnnouncementBaseURI() { 
    	
    	URI uri=null;
    	if(sContext.getAttribute("rootAnnouncement")!=null){
			try {
				uri =new URI((String)sContext.getAttribute("rootAnnouncement"));
				return uri;
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return uri;
    	}
    public String getAdaptorClassName() { return null; }
    public URI getRecordingBaseURI() { 
    	URI uri=null;
    	if(sContext.getAttribute("rootRecordingPath")!=null)
			try {
				uri =new URI((String)sContext.getAttribute("rootRecordingPath"));
				return uri;
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return uri; }
    
    
    public Iterator getSupportedAttributes() { return null; }
    public Iterator getAttributeNames() { return null; }
    public Object getAttribute(String name) { return null; }
    public void setAttribute(String name, Object value) {  }
    private ServiceContext sContext = null;
}
