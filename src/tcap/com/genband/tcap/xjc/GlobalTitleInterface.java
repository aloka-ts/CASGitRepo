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

public abstract class /*generated*/ GlobalTitleInterface
{
	public GlobalTitle getGlobalTitleInterface() throws TcapContentReaderException
	{
		try
		{
			if (getGtindicator0001() != null)
				return getGtindicator0001().getGtindicator0001Interface();
			if (getGtindicator0100() != null)
				return getGtindicator0100().getGtindicator0100Interface();
			if (getGtindicator0011() != null)
				return getGtindicator0011().getGtindicator0011Interface();
			if (getGtindicator0010() != null)
				return getGtindicator0010().getGtindicator0010Interface();
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
		return null;
	}

	static public GlobalTitleType produceJAXB(GlobalTitle iGlobalTitle) throws TcapContentWriterException
	{
		GlobalTitleType iGlobalTitleType = new GlobalTitleType();
		if (iGlobalTitle instanceof GTIndicator0001)
		{
			iGlobalTitleType.setGtindicator0001(Gtindicator0001Type.produceJAXB((GTIndicator0001)iGlobalTitle));
		}
		if (iGlobalTitle instanceof GTIndicator0100)
		{
			iGlobalTitleType.setGtindicator0100(Gtindicator0100Type.produceJAXB((GTIndicator0100)iGlobalTitle));
		}
		if (iGlobalTitle instanceof GTIndicator0011)
		{
			iGlobalTitleType.setGtindicator0011(Gtindicator0011Type.produceJAXB((GTIndicator0011)iGlobalTitle));
		}
		if (iGlobalTitle instanceof GTIndicator0010)
		{
			iGlobalTitleType.setGtindicator0010(Gtindicator0010Type.produceJAXB((GTIndicator0010)iGlobalTitle));
		}
		return iGlobalTitleType;
	}

	public abstract Gtindicator0001Type getGtindicator0001();
	public abstract Gtindicator0100Type getGtindicator0100();
	public abstract Gtindicator0011Type getGtindicator0011();
	public abstract Gtindicator0010Type getGtindicator0010();
}
