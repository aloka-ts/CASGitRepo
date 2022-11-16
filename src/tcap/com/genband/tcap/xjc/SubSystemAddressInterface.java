/**********************************************************************
 * This class is automatically generated on Mon Dec 10 09:51:48 2007
 * (c) Genband, 2007
 *********************************************************************/

package com.genband.tcap.xjc;
import jain.protocol.ss7.tcap.dialogue.*;
import jain.protocol.ss7.tcap.component.*;
import jain.protocol.ss7.tcap.*;
import jain.protocol.ss7.sccp.*;
import jain.protocol.ss7.sccp.management.*;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.SignalingPointCode;
import jain.protocol.ss7.SubSystemAddress;
import jain.protocol.ss7.AddressConstants;
import com.genband.tcap.io.TcapContentReaderException;
import com.genband.tcap.io.TcapContentWriterException;
import java.math.BigInteger;
import java.util.List;

public abstract class /*generated*/ SubSystemAddressInterface
{
	private SubSystemAddress iSubSystemAddressType = null;
	public SubSystemAddress getSubSystemAddressInterface() throws TcapContentReaderException
	{
		try
		{
			if (iSubSystemAddressType == null)
			{
				iSubSystemAddressType = new SubSystemAddress(this);
				iSubSystemAddressType.setSubSystemNumber(getSubSystemNumber().intValue());
				iSubSystemAddressType.setSignalingPointCode(getSignalingPointCode().getSignalingPointCodeInterface());
			}
			return iSubSystemAddressType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public SubSystemAddressType produceJAXB(SubSystemAddress iSubSystemAddress) throws TcapContentWriterException
	{
		try
		{
			SubSystemAddressType iSubSystemAddressType = new SubSystemAddressType();
			iSubSystemAddressType.setSubSystemNumber(BigInteger.valueOf(iSubSystemAddress.getSubSystemNumber()));
			iSubSystemAddressType.setSignalingPointCode(SignalingPointCodeType.produceJAXB(iSubSystemAddress.getSignalingPointCode()));
			return iSubSystemAddressType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract BigInteger getSubSystemNumber();
	public abstract SignalingPointCodeType getSignalingPointCode();
}
