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

public class BPHttpServletWizard extends BPClassWizard {
	
	public void addPages() {
		
		BPHttpServletPage httpServletPage = new BPHttpServletPage();
		httpServletPage.init(super.getSelection());
		
		AddHttpMappingAndInitParams param=new AddHttpMappingAndInitParams(this);
		param.init();
		
		super.setFirstPage(httpServletPage);
		this.addPage(httpServletPage);
		httpServletPage.setAddPametersPage(param);
		
		super.setHttpSecondPage(param);
		this.addPage(param);
		
	}

}
