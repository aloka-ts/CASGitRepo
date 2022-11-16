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
package com.baypackets.sas.ide.wizards;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.IdeUtils;

public class BPSipServletWizard extends BPClassWizard {

	public void addPages() {
		BPSipServletPage sipServletPage = new BPSipServletPage();
		sipServletPage.init(super.getSelection());
		
		
		//set first page
		super.setFirstPage(sipServletPage);
		this.addPage(sipServletPage);
		
		//second page added by reeta
		if (!IdeUtils.is289ProjectNature(sipServletPage.getJavaProject()
				.getProject())) {
			AddSipMappingAndInitParams param = new AddSipMappingAndInitParams(this, sipServletPage);
			param.init();
			super.setSipSecondPage(param);
			this.addPage(param);
			// added by reeta
			sipServletPage.setNextSipPage(param);
		}else{
			SasPlugin.getDefault().log("creating Init param page for JSR289ProjectNature  ");
			AddSip289InitParams param = new AddSip289InitParams(this, sipServletPage);
			param.init();
			super.setSip289SecondPage(param);
			this.addPage(param);
			// added by reeta
			sipServletPage.setNextSip289Page(param);
			
		}
		
		
		
		
		
		
	}
}
