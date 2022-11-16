/*
 * Created on Oct 28, 2004
 *
 */
package com.baypackets.ase.control;

/**
 * @author Ravi
 */
public interface RoleChangeListener {

	public void roleChanged(String clusterId, PartitionInfo partitionInfo);
}
