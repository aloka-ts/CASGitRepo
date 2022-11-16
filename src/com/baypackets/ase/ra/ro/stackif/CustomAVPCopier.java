/**
 * Filename:	CustomAVPCopier.java
 * Created On:	13-10-2006
 */

package com.baypackets.ase.ra.ro.stackif;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.ro.*;

public class CustomAVPCopier {
	private static Logger logger = Logger.getLogger(CustomAVPCopier.class);

	/**
	 * Copy from SAS object to stack object.
	 */
	public static com.condor.diaCommon.AAACustomAvpInfo createAVPInfo(CustomAVPMapImpl custAvpMap) {
		if(custAvpMap == null) {
			return null;
		}

		com.condor.diaCommon.AAACustomAvpInfo stackInfo =
										new com.condor.diaCommon.AAACustomAvpInfo();
		int[] caCodes = custAvpMap.getCustomAVPCodes();
		int numOfAvp = caCodes.length;

		// Intialize stack's Custom AVP Info object
		stackInfo.initCustomAvpTable(numOfAvp);

		// Now copy Custom AVPs from map one-by-one
		for(int idx = 0; idx < numOfAvp; ++idx) {
			CustomAVP ca = custAvpMap.getCustomAVP(caCodes[idx]);
			stackInfo.setCustomAvp(copyCustomAVP(ca), idx);
		}

		return stackInfo;
	}

	private static com.condor.diaCommon.AAACustomAvp copyCustomAVP(CustomAVP custAvp) {
		com.condor.diaCommon.AAACustomAvp stackAvp = new com.condor.diaCommon.AAACustomAvp();

		if(!custAvp.isGrouped()) {
			// Single Custom AVP
			com.condor.diaCommon.AAACustomSingleAVP singleAvp =
										new com.condor.diaCommon.AAACustomSingleAVP();
			singleAvp.setAvpCode(custAvp.getAVPCode());
			singleAvp.setAvpFlag(custAvp.getAVPFlag());
			singleAvp.setVendorId(custAvp.getVendorId());

			switch(custAvp.getAVPType()) {
				case CustomAVP.DATA_TYPE_GROUPED:
					logger.error("Must never come here. Error in code!");
					break;

				case CustomAVP.DATA_TYPE_DOUBLE:
					singleAvp.setDoubleAvpData(((Double)custAvp.getAVPData()).doubleValue());
					singleAvp.setIsDoubleValue(true);
					break;

				case CustomAVP.DATA_TYPE_FLOAT:
					singleAvp.setFloatAvpData(((Float)custAvp.getAVPData()).floatValue());
					singleAvp.setIsFloatValue(true);
					break;

				case CustomAVP.DATA_TYPE_INT:
					singleAvp.setIntAvpData(((Integer)custAvp.getAVPData()).intValue());
					singleAvp.setIsIntValue(true);
					break;

				case CustomAVP.DATA_TYPE_LONG:
					singleAvp.setLongAvpData(((Long)custAvp.getAVPData()).longValue());
					singleAvp.setIsLongValue(true);
					break;

				case CustomAVP.DATA_TYPE_STRING:
					singleAvp.setStrAvpData((String)custAvp.getAVPData());
					singleAvp.setIsStringValue(true);
					break;

				case CustomAVP.DATA_TYPE_DIAMIDENT:
					singleAvp.setStrAvpData(((DiamIdent)custAvp.getAVPData()).get());
					singleAvp.setIsStringValue(true);
					break;

				case CustomAVP.DATA_TYPE_ADDRESS:
					singleAvp.setStrAvpData(((Address)custAvp.getAVPData()).get());
					singleAvp.setIsStringValue(true);
					break;

				case CustomAVP.DATA_TYPE_IPFILTERRULE:
					singleAvp.setStrAvpData(((IPFilterRule)custAvp.getAVPData()).toString());
					singleAvp.setIsStringValue(true);
					break;

				default:
					logger.error("Must never come here. Error in code!");
			}

			stackAvp.setSingleAvp(singleAvp);
		} else {
			// Grouped Custom AVP
			GroupedCustomAVP grpdCustAvp = (GroupedCustomAVP)custAvp;
			com.condor.diaCommon.AAACustomGrpAvp stackGrpAvp =
											new com.condor.diaCommon.AAACustomGrpAvp();

			stackGrpAvp.setGrpAvpCode(grpdCustAvp.getAVPCode());
			stackGrpAvp.setGrpAvpFlag(grpdCustAvp.getAVPFlag());
			stackGrpAvp.setGrpVId(grpdCustAvp.getVendorId());
			int[] caCodes = grpdCustAvp.getCustomAVPCodes();
			int numOfAvp = caCodes.length;

			stackGrpAvp.initSingleAvp(numOfAvp);
			for(int idx = 0; idx < numOfAvp; ++idx) {
				CustomAVP ca = grpdCustAvp.getCustomAVP(caCodes[idx]);

				if(ca.isGrouped()) {
					logger.error("Nested grouped AVPs are not supported by stack");
					throw new IllegalArgumentException(
									"Nested grouped AVPs are not supported by stack");
				}

				stackGrpAvp.setSingleAvp(copyCustomAVP(ca).getSingleAvp(), idx);
			}

			stackAvp.setGroupedpAvp(stackGrpAvp);
		}

		return stackAvp;
	}

	/**
	 * Copy from stack object to SAS object.
	 */
	public static CustomAVPMapImpl readAvpInfo(com.condor.diaCommon.AAACustomAvpInfo caInfo) {
		if(caInfo == null) {
			return null;
		}

		CustomAVPMapImpl avpMap = new CustomAVPMapImpl();

		for(int idx = 0; idx < caInfo.getNoOfCustomAvp(); ++idx) {
			CustomAVP avp = readCustomAvp(caInfo.getCustomAvp(idx));
			avpMap.setCustomAVP(avp.getAVPCode(), avp);
		}

		return avpMap;
	}

	private static CustomAVP readCustomAvp(com.condor.diaCommon.AAACustomAvp ca) {
		if(ca.getIsGrpAvpPresent()) {
			// Grouped AVP
			com.condor.diaCommon.AAACustomGrpAvp grouped = ca.getGroupedAvp();

			GroupedCustomAVP gcAvp = new GroupedCustomAVP(	grouped.getGrpAvpCode(),
															grouped.getGrpAvpFlag(),
															grouped.getGrpVId());

			for(int idx = 0; idx < grouped.getNumberOfSingleAvp(); ++idx) {
				com.condor.diaCommon.AAACustomSingleAVP single = grouped.getSingleAvp(idx);
				Object data;

				if(single.getIsDoubleValue()) {
					data = new Double(single.getDoubleAvpData());
				} else if(single.getIsFloatValue()) {
					data = new Float(single.getFloatAvpData());
				} else if(single.getIsIntValue() || single.getIsSIntValue()) {
					data = new Integer(single.getIntAvpData());
				} else if(single.getIsLongValue() || single.getIsSLongValue()) {
					data = new Long(single.getLongAvpData());
				} else if(single.getIsStringValue()) {
					data = single.getStrAvpData();
				} else {
					logger.error("No custom AVP data type given by stack");
					throw new IllegalArgumentException("No custom AVP data type given by stack");
				}

				CustomAVP avp = new SingleCustomAVP(	single.getAvpCode(),
														single.getAvpFlag(),
														data,
														single.getVendorId());

				gcAvp.setCustomAVP(avp.getAVPCode(), avp);
			}

			return gcAvp;
		} else {
			// Single AVP
			com.condor.diaCommon.AAACustomSingleAVP single = ca.getSingleAvp();
			Object data;

			if(single.getIsDoubleValue()) {
				data = new Double(single.getDoubleAvpData());
			} else if(single.getIsFloatValue()) {
				data = new Float(single.getFloatAvpData());
			} else if(single.getIsIntValue() || single.getIsSIntValue()) {
				data = new Integer(single.getIntAvpData());
			} else if(single.getIsLongValue() || single.getIsSLongValue()) {
				data = new Long(single.getLongAvpData());
			} else if(single.getIsStringValue()) {
				data = single.getStrAvpData();
			} else {
				logger.error("No custom AVP data type given by stack");
				throw new IllegalArgumentException("No custom AVP data type given by stack");
			}

			return new SingleCustomAVP(	single.getAvpCode(),
										single.getAvpFlag(),
										data,
										single.getVendorId());
		}
	}
}

