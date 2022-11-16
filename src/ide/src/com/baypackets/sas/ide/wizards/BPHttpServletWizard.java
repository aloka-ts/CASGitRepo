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
