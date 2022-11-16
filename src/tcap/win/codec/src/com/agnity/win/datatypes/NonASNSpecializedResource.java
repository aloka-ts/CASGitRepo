package com.agnity.win.datatypes;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.SpecializedResource;
import com.agnity.win.enumdata.ResourceTypeEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for SpecializedResource
 *  @author Supriya Jain
 */
public class NonASNSpecializedResource {
	private static Logger logger = Logger
			.getLogger(NonASNSpecializedResource.class);
	LinkedList<ResourceTypeEnum> resourceType;

	public LinkedList<ResourceTypeEnum> getResourceType() {
		return resourceType;
	}

	public void setResourceType(LinkedList<ResourceTypeEnum> resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * This function will encode NonASNSpecializedResource
	 * 
	 * @param list
	 *            of Specialized Resource Types
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeResourceType(
			LinkedList<ResourceTypeEnum> resourceType)
			throws InvalidInputException {
		logger.info("encodeResourceType");
		if (resourceType == null) {
			logger.error("encodeResourceType: InvalidInputException(resourceType is null)");
			throw new InvalidInputException("resourceType is null");
		}
		int i = 0;
		byte[] myParams = new byte[resourceType.size()];
		// every byte represents an Specialized Resource ,decoding gives list of
		// Specialized Resources
		for (ResourceTypeEnum rt : resourceType) {
			myParams[i++] = (byte) (rt.getCode());
		}
		if (logger.isDebugEnabled())
			logger.debug("encodeResourceType: Encoded : "
					+ Util.formatBytes(myParams));
		logger.info("encodeResourceType");
		return myParams;
	}
	
	/**
	 * This function will encode NonASN SpecializedResource to ASN SpecializedResource object
	 * @param nonASNSpecializedResource
	 * @return SpecializedResource
	 * @throws InvalidInputException
	 */
	public static SpecializedResource encodeResourceType(NonASNSpecializedResource nonASNSpecializedResource)
			throws InvalidInputException {
		
		logger.info("Before encodeSpecializedResource : nonASN to ASN");
		SpecializedResource SpecializedResource = new SpecializedResource();
		SpecializedResource.setValue(encodeResourceType(nonASNSpecializedResource.getResourceType()));
		logger.info("After encodeSpecializedResource : nonASN to ASN");
		return SpecializedResource;
	}

	/**
	 * This function will decode NonASNSpecializedResource
	 * 
	 * @param data
	 *            bytes to be decoded
	 * @return object of NonASNSpecializedResource
	 * @throws InvalidInputException
	 */
	public static NonASNSpecializedResource decodeResourceType(byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeResourceType: Input--> data:"
					+ Util.formatBytes(data));
		if (data == null || data.length == 0) {
			logger.error("decodeResourceType: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNSpecializedResource spRes = new NonASNSpecializedResource();
		spRes.resourceType = new LinkedList<ResourceTypeEnum>();
		// every decoded byte represents an action
		for (int i = 0; i < data.length; i++) {
			spRes.resourceType.add(ResourceTypeEnum.fromInt(data[i] & 0xFF));
		}
		if (logger.isDebugEnabled())
			logger.debug("decodeResourceType: Output<--" + spRes.toString());
		logger.info("decodeResourceType");
		return spRes;
	}

	public String toString() {

		String obj = "resourceType :" + resourceType;
		return obj;
	}

}
