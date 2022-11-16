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

public abstract class /*generated*/ TcapUserAddressInterface
{
	private TcapUserAddress iTcapUserAddressType = null;
	public TcapUserAddress getTcapUserAddressInterface() throws TcapContentReaderException
	{
		try
		{
			if (iTcapUserAddressType == null)
			{
				iTcapUserAddressType = new TcapUserAddress(this);
			}
			return iTcapUserAddressType;
		}
		catch (Exception e)
		{
			throw new TcapContentReaderException(e);
		}
	}

	static public TcapUserAddressType produceJAXB(TcapUserAddress iTcapUserAddress) throws TcapContentWriterException
	{
		try
		{
			TcapUserAddressType iTcapUserAddressType = new TcapUserAddressType();
			return iTcapUserAddressType;
		}
		catch (Exception e)
		{
			throw new TcapContentWriterException(e);
		}
	}

}
