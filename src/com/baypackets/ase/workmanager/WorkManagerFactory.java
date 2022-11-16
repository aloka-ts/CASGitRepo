//***********************************************************************************
//GENBAND, Inc. Confidential and Proprietary
//
//This work contains valuable confidential and proprietary
//information.
//Disclosure, use or reproduction without the written authorization of
//GENBAND, Inc. is prohibited. This unpublished work by GENBAND, Inc.
//is protected by laws of United States and other countries.
//If publication of work should occur the following notice shall
//apply:
//
//"Copyright 2007 GENBAND, Inc. All right reserved."
//***********************************************************************************


//***********************************************************************************
//
//      File:   WorkManagerFactory.java
//
//      Desc:   This file implements javax.naming.spi.ObjectFactory interface
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Somesh Kr. Srivastava           23/10/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.workmanager;

import javax.naming.*;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

import com.baypackets.ase.container.*;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This is an object factory that when given a reference for a WorkManagerImpl
 * object, will create an instance of the corresponding WorkManagerImpl.
 *
 * @author Somesh Kumar Srivastava
 *
 */

public class WorkManagerFactory implements ObjectFactory {
 	
	static Logger _logger=Logger.getLogger(WorkManagerFactory.class);
	
	public WorkManagerFactory() {
	}


	public Object getObjectInstance(Object obj, Name name, Context ctx, Hashtable env) throws Exception{

		if (obj instanceof Reference) {
			Reference ref = (Reference)obj;
			if(_logger.isDebugEnabled())
			_logger.debug("Hash code of Reference " + ref.hashCode());

			if (ref.getClassName().equals(WorkManagerImpl.class.getName())) {
				RefAddr wmName = ref.get("_workManager");
				RefAddr appId = ref.get("_appId");
				if(_logger.isDebugEnabled())
				_logger.debug("Hash code of RefAddr " + wmName.hashCode());

				if(wmName != null) {
					Object WorkManagerObj = new WorkManagerImpl((String)wmName.getContent(), (String)appId.getContent());
					((WorkManagerImpl)WorkManagerObj).initialize();	

					// Add this WorkManager instance to AseContext
					AseHost host = (AseHost)Registry.lookup(Constants.NAME_HOST);
					AseContext aseCtxt = (AseContext)host.findChild((String)appId.getContent());
					aseCtxt.addWmToList((WorkManagerImpl)WorkManagerObj);
					if(_logger.isDebugEnabled())
					_logger.debug("Hash code of WorkManager Obj being returned " + WorkManagerObj.hashCode());
					return WorkManagerObj;
				}
			}
		}

		return null;
	}
}
