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
//Author@Reeta Aggarwal
package com.baypackets.sas.ide.wizards;

public class BPRAResourceListenerWizard extends BPClassWizard {
	
	public void addPages() {
		
		BPRAResourceListenerPage raResListenerPage = new BPRAResourceListenerPage();
		raResListenerPage.init(super.getSelection());
		super.setFirstPage(raResListenerPage);
		this.addPage(raResListenerPage);
		this.setResourceListenerPage(raResListenerPage);
		
	}

}
