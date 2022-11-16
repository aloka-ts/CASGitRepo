/**
 * Filename:	MessageBody.java
 * Created On:	11-Oct-2006
 */

package com.baypackets.ase.ra.ro;

/**
 * This class defines the Message-Body AVP that is part of an
 * credit control request.
 *
 * Application can use it's methods to fill various fields of Inter-Operator-Identifier AVP.
 *
 * @author Neeraj Jain
 *
 */

public interface MessageBody {

    /**
     * This method is used by application to get Content-Type AVP
     *
     * @return String object containing Content-Type AVP
     */

	public String getContentType();

    /**
     * This method is used by application to get Content-Length AVP
     *
     * @return string object containing Content-Length AVP
     */

	public String getContentLength();

    /**
     * This method is used by application to get the Content-Disposition AVP
     *
     * @return Strign object containing Content-Disposition AVP
     */

	public String getContentDisposition();

    /**
     * This method is used by application to get the Originator AVP
     *
     * @return Originator AVP
     */

	public short getOriginator();

    /**
     * This method is used by application to set Content-Type AVP
     *
     * @param contentType - Content-Type AVP to be set
     */

	public void setContentType(String contentType);

    /**
     * This method is used by application to set Content-Length AVP
     *
     * @param contentLength - Content-Length AVP
     */

	public void setContentLength(String contentLength);

    /**
     * This method is used by application to set Content-Disposition AVP
     *
     * @param contentDisposition - Content-Disposition AVP to be set
     */

	public void setContentDisposition(String contentDisposition);

    /**
     * This method is used by application to set the Originator AVP
     *
     * @param orig - Originator AVP to be set
     */

	public void setOriginator(short orig);
}

