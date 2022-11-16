package com.baypackets.sas.ide.editor.model;

import org.w3c.dom.Node;

public interface ModelListener {

	public static final int ADD = 1;
	public static final int MODIFY = 2;
	public static final int REMOVE = 3;
	
	public void modelChanged(int action, Node data);
}
