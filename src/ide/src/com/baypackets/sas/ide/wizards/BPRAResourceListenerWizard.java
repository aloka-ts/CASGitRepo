//Author@Reeta Aggarwal
package com.baypackets.sas.ide.wizards;

public class BPRAResourceListenerWizard extends BPClassWizard {
	
	public void addPages() {
		
		BPRAResourceListenerPage httpServletPage = new BPRAResourceListenerPage();
		httpServletPage.init(super.getSelection());
		super.setFirstPage(httpServletPage);
		this.addPage(httpServletPage);

		
	}

}
