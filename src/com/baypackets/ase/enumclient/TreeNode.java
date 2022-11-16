package com.baypackets.ase.enumclient;

import java.util.Vector;

public class TreeNode  { 
	private Vector m_children;
	private String m_data;
	private boolean m_terminal;
	private boolean m_done;
	
	TreeNode() {
		m_children = null ;
	}
	
	public TreeNode(String uri) {
		m_data = uri;
		m_children = null;
		m_terminal = true;
		m_done = false;
	}

	public void setChildren(Vector list) {
		m_children = list;
	}

	public boolean isTerminal() {
		return m_terminal;
	}
	
	public synchronized void addChild(TreeNode node ) {
		if ( node == null )
			return;
		if (m_children == null ) {
            m_children = new Vector();
			m_terminal = false;
        }
			m_children.add(node);
	}

	public synchronized void addChild( String uri , boolean terminal) {
		if (m_children == null ) {
			m_children = new Vector();
		}
		TreeNode node = new TreeNode(uri);
		node.setTerminal(terminal);
		//node.setDone(true);	
		 m_terminal = false;
		m_children.add(node);
	}

	public Vector getChildren() {
		return m_children;
	}

	public synchronized void setDone( boolean bool ) { 
		m_done = bool;
	}

	public boolean isCompleted( ) {
		return m_done;
	}

	public synchronized void setTerminal(boolean bool ) {
		m_terminal = bool;
	}

	public String getData() {
		return m_data;
	}

}
