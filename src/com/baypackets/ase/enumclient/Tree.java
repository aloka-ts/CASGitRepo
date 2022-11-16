package com.baypackets.ase.enumclient;

import java.util.Vector;
import java.util.List;
import java.util.Iterator;
import org.apache.log4j.Logger;

public class Tree { 

	//private static TreeNode root;
	private TreeNode root;
	//Ashish for testing purpose
	private int counter=0 ;
    transient private static Logger m_logger =
          Logger.getLogger(Tree.class);

	public Tree () {
		root = null;
	}

	public Tree(String str ) {
		root = new TreeNode(str) ;
		root.setTerminal(false);
	}

	public synchronized boolean isTreeProcessingOver() {
        if(m_logger.isDebugEnabled() )
            m_logger.debug(" Entered : isTreeProcessingOver " ) ;	
		boolean bool = isNodeProcessingOver(root);
        if(m_logger.isDebugEnabled() )
            m_logger.debug(" Exiting  : isTreeProcessingOver "  + " returning : " + bool ) ;
		return bool ;
	}
	
	public boolean isRoot( TreeNode node ) {
		if ( root == node ) 
			return true;
		else
			return false;
	}
	
	public TreeNode getRoot() {
		return root ; 
	}

	public synchronized boolean isNodeProcessingOver(TreeNode node) {
		if ( node == null ) {
            return true;
        }
		boolean bool = true;
		
		if (node.getChildren() != null ) {
            Iterator iter = ((Vector)node.getChildren()).iterator();
            while( bool && iter.hasNext() ) {
					 bool = isNodeProcessingOver( (TreeNode)iter.next() )  ;
					 if ( !bool )
						return false;
            }
			
        }
		if ( node != root && node.getChildren() == null && !node.isTerminal()) {
			if( !node.isCompleted() ) {
				return false;
			}
		}
		return true;
	}

		
	public synchronized void makeList(List uriList) { 
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Entered : makeList" ) ;
		 makeList(root ,uriList)  ;
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Exiting : makeList" ) ;
	}

	public void makeList(TreeNode node , List uriList) {
		if ( node == null ) 
			return; 
		
		if (node.getChildren() != null ) { 
			Iterator iter = ((Vector)node.getChildren()).iterator();
			while( iter.hasNext() ) { 
				makeList((TreeNode)iter.next() , uriList);
			}
		}
		if( node.isTerminal() && node.getChildren() == null ) {
            uriList.add(node.getData() ) ;
		}
	}

	public void inorder() {
		inorder(root);
	}

	public int getCounter() {
        return counter;
    }

	public void inorder(TreeNode node) {
		if( node == null )
			return;
		if ( node.isTerminal() )
            counter++;
		//System.out.println(node.getData() );
		if (node.getChildren() != null ) {
			Iterator iter = ((Vector)node.getChildren()).iterator();
			while(iter.hasNext() ) {
				inorder((TreeNode)iter.next() );
		}	}
	}
		
}
