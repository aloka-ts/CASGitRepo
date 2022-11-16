package com.baypackets.sas.ide.wizards;

public class BPSipListenerWizard  extends BPClassWizard {

	public void addPages() {
		BPSipListenerPage listenerPage = new BPSipListenerPage();
		listenerPage.init(super.getSelection());
		super.setFirstPage(listenerPage);
		this.addPage(listenerPage);
	}
}
