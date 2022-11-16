/*
 * Created on Mar 15, 2005
 *
 */
package com.baypackets.ase.control;

import java.util.ArrayList;
import com.baypackets.ase.channel.AseSubsystem;

/**
 * @author Dana
 * <p>This interface defines a common interface 
 * for all the classes that provide role resolution. 
 * </p>
 */
public interface AseRoleResolver {
	/**
	 * This PartitionTable will be used for role resolve
	 * @param partitionTable
	 */
	public void setPartitionTable(AsePartitionTable partitionTable);
	
	public void initialize();
	

	public ArrayList resolveRole( AseSubsystem ase);
		
	
	/** This method is called when a member decide to take active role in cluster
	 *	@param downPeer AseSubsystem which went down
 	 */
	//public void decideActiveRole(AseSubsystem downPeer);

	/**	This method is called when a member decide to take standby role in cluster
	 */
	//public void decideStandbyRole();

}
