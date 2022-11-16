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
package com.agnity.mphdata.common;

import java.io.Serializable;

public enum State implements Serializable {
	INIT, CONN_IN_PROGRESS, CONNECTED, TERMINATED, MS_CONN_IN_PROGRESS, 
	MS_CONNECTED, MS_PLAY, MS_PLAY_COLLECT, MS_PLAY_RECORD, MS_DISCONNECTED,TERMINATION_IN_PROGRESS
}
