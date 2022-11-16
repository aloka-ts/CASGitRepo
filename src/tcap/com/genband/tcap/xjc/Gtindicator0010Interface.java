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

public abstract class /*generated*/ Gtindicator0010Interface
{
	private GTIndicator0010 iGTIndicator0010Type = null;
	public GTIndicator0010 getGtindicator0010Interface() throws TcapContentReaderException
	{
		try
		{
			if (iGTIndicator0010Type == null)
			{
				iGTIndicator0010Type = new GTIndicator0010(this);
				iGTIndicator0010Type.setTranslationType(getTranslationType());
				if (getAddressInformation() != null)
					iGTIndicator0010Type.setAddressInformation(getAddressInformation());
			}
			return iGTIndicator0010Type;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public Gtindicator0010Type produceJAXB(GTIndicator0010 iGtindicator0010) throws TcapContentWriterException
	{
		try
		{
			Gtindicator0010Type iGtindicator0010Type = new Gtindicator0010Type();
			iGtindicator0010Type.setTranslationType(iGtindicator0010.getTranslationType());
			if (iGtindicator0010.isAddressInformationPresent())
				iGtindicator0010Type.setAddressInformation(iGtindicator0010.getAddressInformation());
			return iGtindicator0010Type;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

	public abstract byte getTranslationType();
	public abstract byte[] getAddressInformation();
}
