/*
 * Created on Mar 15, 2005
 *
 */
package com.baypackets.ase.control;

import java.util.ArrayList;
import com.baypackets.ase.channel.AseSubsystem;

/**
 * @author Dana
 * <p>This class implements AseRoleResolver interface. 
 * It performs role resolution based on the rules defined by hybrid mode.
 * Currently, hybrid mode is not supported yet.
 * </p>
 */
public class HybridRoleResolver implements AseRoleResolver {
	
	public void initialize() {
		
	}
	
	public void setPartitionTable(AsePartitionTable partitionTable) {
		
	}

	/*	
	public ArrayList resolveRole() {
		return null;
	}
	
	public ArrayList resolveRole(PartitionInfo conflicPartition) {
		return null;
	}
	
	public ArrayList resolveRole(short role) {
		return null;
	}*/

	public ArrayList resolveRole( AseSubsystem ase) { 
		return null;
	}
	
	public void incrementStandbySlots()
        {

        }

	public void releaseActive2( String fip)
	{

	}
}
