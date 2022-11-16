//Author@Reeta Aggarwal
package com.baypackets.sas.ide.wizards;

public class BPRAMessageHandlerWizard extends BPClassWizard {
	
	public void addPages() {
		
		BPRAMessageHandlerPage httpServletPage = new BPRAMessageHandlerPage();
		httpServletPage.init(super.getSelection());
		super.setFirstPage(httpServletPage);
		this.addPage(httpServletPage);

		
	}

}
