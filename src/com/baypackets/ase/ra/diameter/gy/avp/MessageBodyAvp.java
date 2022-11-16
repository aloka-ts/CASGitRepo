package com.baypackets.ase.ra.diameter.gy.avp;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.enums.FlagRuleEnum;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.OriginatorEnum;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.diameter.gy.generated.avp.AvpMessageBody;
import com.traffix.openblox.diameter.gy.generated.enums.EnumOriginator;

public class MessageBodyAvp extends AvpDiameterGrouped {

	private static Logger logger = Logger.getLogger(MessageBodyAvp.class.getName());
	public static final long vendorId = 0L;

	private AvpMessageBody stackObj;

	public MessageBodyAvp(AvpMessageBody stkObj){
		super(stkObj);
		this.stackObj=stkObj;
	}

	/**
	 * This method returns the standard code for this AVP.
	 */
	public int getCode() {
		return stackObj.getCode();
	}

	/**
	 * The standard rule for the M (mandatory) AVP header flag
	 */
	public FlagRuleEnum getMRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getMRule());
	}

	/**
	 * This method returns the name of this AVP.
	 */
	public String getName() {
		return stackObj.getName();
	}

	/**
	 * The standard rule for the P (end-to-end encryption) AVP header flag
	 */
	public FlagRuleEnum getPRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getPRule());
	}

	/**
	 * This method returns the vendor ID associated with this AVP.
	 */
	public long getVendorId() {
		return stackObj.getVendorId();
	}

	/**
	 * The standard rule for the V (vendor) AVP header flag
	 */
	public FlagRuleEnum getVRule() {
		return FlagRuleEnum.getContainerObj(stackObj.getVRule());
	}
	

	/**
	*  Adding ContentDisposition AVP of type UTF8String to the message.
	 * @throws GyResourceException 
	*/
	public ContentDispositionAvp addContentDisposition(java.lang.String value) throws GyResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addContentDisposition()");
			}
			return new ContentDispositionAvp(stackObj.addContentDisposition(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addContentDisposition ",e);
		}
	}

	/**
	*  Adding ContentLength AVP of type Unsigned32 to the message.
	 * @throws GyResourceException 
	*/
	public ContentLengthAvp addContentLength(long value) throws GyResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addContentLength()");
			}
			return new ContentLengthAvp(stackObj.addContentLength(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addContentLength ",e);
		}
	}

	/**
	*  Adding ContentType AVP of type UTF8String to the message.
	 * @throws GyResourceException 
	*/
	public ContentTypeAvp addContentType(java.lang.String value) throws GyResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addContentType()");
			}
			return new ContentTypeAvp(stackObj.addContentType(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addContentType ",e);
		}
	}

	/**
	*  Adding Originator AVP of type Enumerated to the message.
	 * @throws GyResourceException 
	*/
	public OriginatorAvp addOriginator(EnumOriginator value) throws GyResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside addOriginator()");
			}
			return new OriginatorAvp(stackObj.addOriginator(value));
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in addOriginator ",e);
		}
	}

	/**
	*  Retrieving a single UTF8String value from ContentDisposition AVPs.
	 * @throws GyResourceException 
	*/
	public java.lang.String getContentDisposition() throws GyResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getContentDisposition()");
			}
			return	stackObj.getContentDisposition();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getContentDisposition ",e);
		}
	}

	/**
	*  Retrieving a single Unsigned32 value from ContentLength AVPs.
	 * @throws GyResourceException 
	*/
	public long getContentLength() throws GyResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getContentLength()");
			}
			return	stackObj.getContentLength();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getContentLength ",e);
		}
	}

	/**
	*  Retrieving a single UTF8String value from ContentType AVPs.
	 * @throws GyResourceException 
	*/
	public java.lang.String getContentType() throws GyResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getContentType()");
			}
			return	stackObj.getContentType();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getContentType ",e);
		}
	}

	/**
	*  This method returns the EnumOriginator associated with this request.
	 * @throws GyResourceException 
	*/
	public OriginatorEnum getEnumOriginator() throws GyResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getEnumOriginator()");
			}
			return	OriginatorEnum.getContainerObj(stackObj.getEnumOriginator());
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getEnumOriginator ",e);
		}
	}

	/**
	*  Retrieving a single Enumerated value from Originator AVPs.
	 * @throws GyResourceException 
	*/
	public int getOriginator() throws GyResourceException  {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Inside getOriginator()");
			}
			return	stackObj.getOriginator();
		} catch (ValidationException e) {
			throw new GyResourceException("Exception in getOriginator ",e);
		}
	}

}