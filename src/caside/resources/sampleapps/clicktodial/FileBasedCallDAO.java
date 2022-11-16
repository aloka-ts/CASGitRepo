package com.baypackets.clicktodial.util;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.log4j.Logger;

/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
public class FileBasedCallDAO
implements CallDAO
{
	private static Logger _logger = Logger.getLogger(FileBasedCallDAO.class);
	private String dbDir;
 
	public FileBasedCallDAO(String dbDir)
	{
		this.dbDir = dbDir;
	}
 
	public void persist(Call call)
	{
		try
		{
			File file = new File(this.dbDir, call.getCallID() + ".ser");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(call);
			oos.close();
		} catch (Exception e) {
			_logger.error(e.toString(), e);
			throw new RuntimeException(e.toString());
		}
	}
 
	public Call findByID(String callID)
	{
		try
		{
			File file = new File(this.dbDir, callID + ".ser");
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			Call call = (Call)ois.readObject();
			ois.close();
			return call;
		} catch (Exception e) {
			_logger.error(e.toString(), e);
			throw new RuntimeException(e.toString());
		}
	}
}
 