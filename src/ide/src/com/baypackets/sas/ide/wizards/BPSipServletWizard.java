package com.baypackets.sas.ide.wizards;

public class BPSipServletWizard extends BPClassWizard {

	public void addPages() {
		BPSipServletPage sipServletPage = new BPSipServletPage();
		sipServletPage.init(super.getSelection());
		
		//set first page
		super.setFirstPage(sipServletPage);
		this.addPage(sipServletPage);
		
		//second page added by reeta
		AddSipMappingAndInitParams param=new AddSipMappingAndInitParams(this);
		param.init();
		
		super.setSipSecondPage(param);
		this.addPage(param);
		//added by reeta
		sipServletPage.setAddPametersPage(param);
		
		
		
		
	}
}
