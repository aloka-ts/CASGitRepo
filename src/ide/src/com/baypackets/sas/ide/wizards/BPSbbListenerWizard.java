package com.baypackets.sas.ide.wizards;

public class BPSbbListenerWizard  extends BPClassWizard {

	public void addPages() {
		BPSbbListenerPage listenerPage = new BPSbbListenerPage();
		listenerPage.init(super.getSelection());
		super.setFirstPage(listenerPage);
		this.addPage(listenerPage);
	}
}
