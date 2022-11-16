/* SimpleRoleResolver.java
 * Created on Mar 15, 2005
 *
 */
package com.baypackets.ase.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.agnity.redis.client.RedisWrapper;
import com.agnity.redis.exeption.RedisLettuceCommandTimeoutException;
import com.agnity.redis.exeption.RedisLettuceConnectionException;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.channel.AseSubsystem;
import com.baypackets.ase.startup.AseMain;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.common.RedisAlarmHandler;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

/**
 * @author Dana
 * <p>This class implements AseRoleResolver interface. 
 * It performs role resolution based on the rules defined by simple mode.
 * </p>
 */
public class SimpleRoleResolver implements AseRoleResolver {
	private static final Logger logger = Logger.getLogger(SimpleRoleResolver.class);

	private int totalActiveSubsystems;
	private boolean ftMode = true;
	private boolean lastStandby = false;
	private AsePartitionTable partitionTable;
	private AseSubsystem self;
	//role properties of this subsystem
	private String selfId = "self";
	private int activeSlots = 1;
	private ClusterManager clusterMgr = null;
	private RedisWrapper redisWrapper=null;
	private String peerId=null;
	ControlManager controlMgr = null;
	private int  swicthoverRetry=1;

	private long switchoverRetryInterval=2000;//ms;

	public void initialize() {
		 controlMgr = (ControlManager)Registry.lookup(Constants.NAME_CONTROL_MGR);
		this.selfId = controlMgr.getSelfInfo().getId();
		this.ftMode = (controlMgr.getSelfInfo().getMode() != AseModes.NON_FT)? true : false;
		this.self = controlMgr.getSelfInfo();

		ConfigRepository repository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String value = null;

		clusterMgr = (ClusterManager) Registry.lookup(Constants.NAME_CLUSTER_MGR);
		redisWrapper = (RedisWrapper) Registry.lookup(Constants.REDIS_WRAPPER);
		try {
			//calculate total active subsystems by counting the number of peers
			StringTokenizer ips = new StringTokenizer(repository.getValue(Constants.OID_PEER_SUBSYS_IP), ", ");
			if (ips.countTokens() > 0) {
				setTotalActiveSubsystems(ips.countTokens());
			} else {
				setTotalActiveSubsystems(1);
			}
		}catch (Exception ex) {
			logger.error(ex);			
		}
		try {
			value = repository.getValue(Constants.PROP_LAST_MEMBER_STANDBY);
			if (value != null) {
				setLastStandby(value.equals(AseStrings.TRUE_SMALL)? true : false);
			}
		} catch (Exception ex) {
			logger.error(ex);			
		}
		
		peerId=repository.getValue(Constants.PEER_SUBSYSTEM_NAME);
	
		String retrycount = repository.getValue(Constants.ROLE_RESOLVE_RETRY);
		if (retrycount != null && !retrycount.isEmpty()) {
			swicthoverRetry = Integer.parseInt(retrycount);
		}
		
		String retryInt = repository.getValue(Constants.ROLE_RESOLVE_RETRY_INTERVAL);
		if (retryInt != null && !retryInt.isEmpty()) {
			switchoverRetryInterval = Long.parseLong(retryInt);
		}
		
	}


	public void setPartitionTable(AsePartitionTable partitionTable) {
		this.partitionTable = partitionTable;
		this.partitionTable.setMaxPartitions(this.totalActiveSubsystems);
	}

	/**
	 * If lastStandby is set to true, the last subsystem coming up within
	 * the cluster takes standby role.
	 * @param lastStandby
	 */
	protected void setLastStandby(boolean lastStandby) {
		this.lastStandby = lastStandby;
	}
	protected void setTotalSubsystems(int totalSubsystems) {
		setTotalActiveSubsystems(totalSubsystems -1);
	}

	protected void setTotalActiveSubsystems(int totalActiveSubsystems) {
		this.totalActiveSubsystems = totalActiveSubsystems;

		if (this.partitionTable != null) {
			this.partitionTable.setMaxPartitions(this.totalActiveSubsystems);
		} else {
			if (logger.isInfoEnabled()) {

			logger.info(" partitiontable is null. " ) ;
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("Total active subsystems is " + this.totalActiveSubsystems ) ;
		}
	}

	/**
	 * This method is called whenever need to resolve role
	 * @return ArrayList of PartitionInfo
	 */
	public synchronized ArrayList resolveRole( AseSubsystem downPeer) {
		ArrayList roles = null;
		 if ( self.getMode() == AseModes.FT_ONE_PLUS_ONE|| self.getMode()==AseModes.FT_N_PLUS||self.getMode()==AseModes.FT_N_PLUS_LITE ) { 
			if (logger.isInfoEnabled()) {
				logger.info(" Mode is " +self.getMode()) ;
			}
			roles = resolveRoleOnePlusOne(downPeer);
		}else if (!ftMode) {
			// In non-FT no peer will be there
			roles = decideActiveRole(null);
		} else {
			if (logger.isInfoEnabled()) {
				logger.info(" Mode is N+1 " ) ;
			}
			roles = resolveRoleNPlusOne(downPeer);
		}
		return roles;
	}

	/** 
	 *	resolves the role incase of 1+1 
	 *	@param downPeer
	 * 	@return 
	 */	
	private synchronized ArrayList resolveRoleOnePlusOne(AseSubsystem downPeer ) { 
		ArrayList roles = decideRole(downPeer);
//		if ( !partitionTable.hasActiveMembers() ) { 
//			roles  = decideActiveRole(downPeer);
//		} else { 
//			roles = decideStandbyRole();
//		}

		return roles;
	}
	/** 
	 *	resolves the role incase of N+1 
	 *	@param downPeer
	 *	@return 
	 */
	private synchronized ArrayList resolveRoleNPlusOne(AseSubsystem downPeer ) {
		ArrayList roles = null;
		if ( !lastStandby ) {
			if ( !partitionTable.hasStandbyMembers() 
					&& partitionTable.hasActiveMembers() ) { 
				roles = decideStandbyRole();
			} else {
				roles = decideActiveRole(downPeer);
			}
		} else { 
			if ( partitionTable.getActiveMemberCount() == partitionTable.getMaxPartitions() ) { 
				roles = decideStandbyRole();
			} else  { 
				roles = decideActiveRole(downPeer);
			}
		}

		return roles;
	}

	
	/**
	 * Decide whether be able to take active role in any partition
	 * @return
	 */
//	ArrayList decideRole(AseSubsystem downPeer) {
//		if (logger.isInfoEnabled()) {
//			logger.info("decideRole..for "+self.getId());
//		}
//		ArrayList roles = new ArrayList();
//		
//		try {
//
//			Set<String> activeInstances = redisWrapper.getSetOperations()
//					.getAllMemberFrmSet(ClusterManager.ACTIVE_CAS_INSTANCES);
//
//			if (activeInstances==null ||activeInstances.isEmpty()) {
//				
//				if (logger.isInfoEnabled()) {
//					logger.info("Active CAS instances are null Decide Active role");
//				}
//				
//				roles = decideActiveRole(downPeer);
//			} else {
//
//				String timestamp = redisWrapper.getHashOperations()
//						.getHashValue(ClusterManager.CAS_INSTANCE_HEARTBEAT, peerId);
//				
//				if (logger.isInfoEnabled()) {
//					logger.info("timestamp ...for peer  "+peerId+" is "+timestamp);
//				}
//				
//				if (timestamp != null) {
//					long timestam = Long.parseLong(timestamp);
//					long currentTime = System.currentTimeMillis();
//					
//					if (logger.isInfoEnabled()) {
//						logger.info("timestamp ...diff is "+(currentTime - timestam) +"ms");
//					}
//
//					if ((currentTime - timestam) > (2 * 1000)) {
//
//						if (logger.isInfoEnabled()) {
//							logger.info("timestamp ...diff is more than 2 secs so wait for 1 more secs");
//						}
//						Thread.currentThread().sleep(1000);
//
//						timestamp = redisWrapper.getHashOperations()
//								.getHashValue(
//										ClusterManager.CAS_INSTANCE_HEARTBEAT,
//										peerId);
//						timestam = Long.parseLong(timestamp);
//						currentTime = System.currentTimeMillis();
//
//						if ((currentTime - timestam) > (2 * 1000)) {
//
//							if (logger.isInfoEnabled()) {
//								logger.info("timestamp ...diff is still more than 2 secs so decide active role");
//							}
//							roles = decideActiveRole(downPeer);
//						}
//
//					} else {
//
//						if (logger.isInfoEnabled()) {
//							logger.info("timestamp diff is less than 2 secs... wait for 1 more secs");
//						}
//						Thread.currentThread().sleep(1000);
//
//						timestamp = redisWrapper.getHashOperations()
//								.getHashValue(
//										ClusterManager.CAS_INSTANCE_HEARTBEAT,
//										peerId);
//						timestam = Long.parseLong(timestamp);
//						currentTime = System.currentTimeMillis();
//
//						if (logger.isInfoEnabled()) {
//							logger.info("timestamp ...diff is "
//									+ (currentTime - timestam));
//						}
//						if ((currentTime - timestam) < (2 * 1000)) {
//
//							if (logger.isInfoEnabled()) {
//								logger.info("timestamp diff is less than 2 secs... so decide standby role");
//							}
//							roles = decideStandbyRole();
//						}
//					}
//				}else {
//					
//					if (logger.isInfoEnabled()) {
//						logger.info("decide active role as timestamp also not found");
//					}
//					roles = decideActiveRole(downPeer);
//				}
//
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return roles;		
//	}
	
	
	ArrayList decideRole(AseSubsystem downPeer) {
		if (logger.isInfoEnabled()) {
			logger.info("decideRole..for "+self.getId());
		}
		ArrayList roles = new ArrayList();
		
		try {
			
			String heartbeatEnabled = BaseContext.getConfigRepository().getValue(
					Constants.PROP_HEARTBEAT_ENABLED);

			if ("1".equals(heartbeatEnabled)) {
				
				if (logger.isInfoEnabled()) {
					logger.info("decideRole..as per   "+ ClusterManager.ACTIVE_CAS_INSTANCES+" in redis ");
				}

			Set<String> activeInstances = redisWrapper.getSetOperations()
					.getAllMemberFrmSet(ClusterManager.ACTIVE_CAS_INSTANCES);

			if (activeInstances==null ||activeInstances.isEmpty()) {
				
				if (logger.isInfoEnabled()) {
					logger.info("Active CAS instances are empty Decide Active role");
				}
				
				roles = decideActiveRole(downPeer);
			} else {
				
				String currentActiveId = null;
				while (activeInstances.iterator().hasNext()) {
					currentActiveId = activeInstances.iterator().next();
					break;
				}
				
				if (logger.isInfoEnabled()) {
					logger.info("Current Active CAS id is "+currentActiveId +" and self id is "+ selfId);
				}
				//if self was current active then become active again no need to chekc for peer 
				if (selfId.equals(currentActiveId)) {
					
					if (logger.isInfoEnabled()) {
						logger.info("selfId is equal to current Active id so decide Active role");
					}
					roles = decideActiveRole(downPeer);
					return roles;
					
				} else {
					String timestamp = redisWrapper.getHashOperations()
							.getHashValue(
									ClusterManager.CAS_INSTANCE_HEARTBEAT,
									peerId);

					if (logger.isInfoEnabled()) {
						logger.info("timestamp ...for peer  " + peerId + " is "
								+ timestamp);
					}

					if (timestamp != null) {
						long timestam = Long.parseLong(timestamp);
						

					if (logger.isInfoEnabled()) {
							logger.info("timestamp ...found is is "+ timestam +" sleep for "+switchoverRetryInterval +"ms and try again ");
						}

							Thread.currentThread().sleep(switchoverRetryInterval);

							String timestampnew = redisWrapper
									.getHashOperations()
									.getHashValue(
											ClusterManager.CAS_INSTANCE_HEARTBEAT,
											peerId);
							long timestamnew = Long.parseLong(timestampnew);
							
							if (logger.isInfoEnabled()) {
								logger.info("lasttime stamp ..."+timestam +" Current ts " +timestamnew  + "diff is "+ (timestamnew - timestam));
							}
							
							if ((timestamnew - timestam)==0){// > (2 * 1000)) {
								
								if (logger.isInfoEnabled()) {
									logger.info("timestamp ...diff is zero when peer updated so check for one more retry if available");//still more than 2 secs s
								}
								
								for(int i=0;i<=swicthoverRetry;i++){
								
									
									if (logger.isInfoEnabled()) {
										logger.info("retry after "+ switchoverRetryInterval);//still more than 2 secs s
									}
									Thread.currentThread().sleep(switchoverRetryInterval);

									timestampnew = redisWrapper
											.getHashOperations()
											.getHashValue(
													ClusterManager.CAS_INSTANCE_HEARTBEAT,
													peerId);
									timestamnew = Long.parseLong(timestampnew);
									
									if (logger.isInfoEnabled()) {
										logger.info("lasttime stamp ..."+timestam +" Current ts " +timestamnew  + "diff is "+ (timestamnew - timestam) +" swicthoverRetry "+swicthoverRetry);
									}
									
									if ((timestamnew - timestam)==0){
										continue;
									}else{
										break;
									}
								}
								if ((timestamnew - timestam)==0){
									if (logger.isInfoEnabled()) {
										logger.info("timestamp ...diff is zero when peer updated so decide active role");//still more than 2 secs s
									}
									return (roles = decideActiveRole(downPeer));
								}
								
							}
							if (timestamnew>timestam){
								
								if (logger.isInfoEnabled()) {
									logger.info("timestamp diff found b/w timestamps updated by peer.. so decide standby role");
								}
								roles = decideStandbyRole();
							}else{
								
								if (logger.isInfoEnabled()) {
									logger.info("could not decide role as per timestamps");//still more than 2 secs s
								}
							}
					} else {

						if (logger.isInfoEnabled()) {
							logger.info("decide active role as timestamp not found");
						}
						roles = decideActiveRole(downPeer);
					}
				}

			}
			}else{
				
				if (logger.isInfoEnabled()) {
					logger.info("heartbeat is disabled decide active role");
				}
				roles = decideActiveRole(downPeer);
			}
			RedisAlarmHandler.redisIsAccessible(selfId);
			
		} catch(RedisLettuceConnectionException e){
			logger.error("exception while writing headrtbeat in redis " +e );
			
			RedisAlarmHandler.redisNotAccessible(selfId);
		}catch(RedisLettuceCommandTimeoutException e){
			logger.error("exception while writing headrtbeat in redis " +e );
			RedisAlarmHandler.redisNotAccessible(selfId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(" Exception occured "+ e);
			e.printStackTrace();
		}

		return roles;		
	}


	/**
	 * Decide whether be able to take active role in any partition
	 * @return
	 */
	ArrayList decideActiveRole( AseSubsystem downPeer) {
		if (logger.isInfoEnabled()) {
			logger.info("Decide active role...");
		}
		ArrayList roles = new ArrayList();

		for (int i = 0; i < this.partitionTable.size(); i++) {
			AsePartition partition = (AsePartition)partitionTable.getPartition(i);
			PartitionInfo role = decideActiveRole(partition, downPeer);
			if (role != null) {
				roles.add(role);
			}
		}

		try {
			
			String heartbeatEnabled = BaseContext.getConfigRepository().getValue(
					Constants.PROP_HEARTBEAT_ENABLED);

			if ("1".equals(heartbeatEnabled)) {
				redisWrapper.getHashOperations().addInHashes(
						ClusterManager.CAS_INSTANCE_HEARTBEAT,
						this.self.getId(), "" + System.currentTimeMillis());

				if (controlMgr.getSelfInfo().getMode() == AseModes.FT_N_PLUS_LITE) {
					Set<String> allactivemembers = redisWrapper
							.getSetOperations().getAllMemberFrmSet(
									ClusterManager.ACTIVE_CAS_INSTANCES);

					for (String activecas : allactivemembers) {

						if (logger.isInfoEnabled()) {
							logger.info("remove existing CAS instances as it is N+ lite mode..."
									+ activecas);
						}
						redisWrapper.getSetOperations().removeFrmSet(
								ClusterManager.ACTIVE_CAS_INSTANCES, activecas);
					}

				}

				redisWrapper.getSetOperations().addInSet(
						ClusterManager.ACTIVE_CAS_INSTANCES, this.self.getId());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("xception while writing headrtbeat in redis");
			e.printStackTrace();
		}
		if (roles.size() == 0) {
			clusterMgr.setRoleResolvedToActive(false);
		} else {
			clusterMgr.setRoleResolvedToActive(true);
		}

		return roles;		
	}

	/**
	 * Decide whether be able to take active role in this partition
	 * @param partition
	 * @return
	 */
	private PartitionInfo decideActiveRole(AsePartition partition, AseSubsystem downPeer) {

		if (logger.isInfoEnabled()) {
			logger.info(" activeSlots="+activeSlots+" !partition.hasMember(AseRoles.ACTIVE)="+!partition.hasMember(AseRoles.ACTIVE)+" partition.isMember(selfId, AseRoles.STANDBY)="+partition.isMember(selfId, AseRoles.STANDBY));
		}

		if (activeSlots > 0 && !partition.hasMember(AseRoles.ACTIVE)) {
			if (partition.isMember(selfId, AseRoles.STANDBY)) {
				if (logger.isInfoEnabled()) {
					logger.info("Role is STANDBY_TO_ACTIVE for " + partition.getFloatingIp());
				}
				// Try to takeover FIP here, If you fail, return null
				// detailed behavior in failure scenario is to be worked out
				int peerEmsId=downPeer.getEmsSubsystemId();
				try {
					clusterMgr.getLoadBalancer().takeoverFIP( peerEmsId , partition.getFloatingIp());
				}
				catch( Exception ex)
				{
					logger.error(" ERROR while taking over FIP : "+partition.getFloatingIp()+"  from peerEmsId="+peerEmsId);
					return null;
				}
				partition.setMember(selfId, AseRoles.ACTIVE);
				if (logger.isInfoEnabled()) {

				logger.info(" resolveRole(), STANDBY_TO_ACTIVE ");
				}
				activeSlots--;
				return new PartitionInfo(partition.getFloatingIp(), 
						selfId, AseRoles.STANDBY_TO_ACTIVE);					
			} else {
				// Try to get the FIP here, If you fail, return null
				// detailed behavior in failure scenario is to be worked out
				String fip;
				try {
					fip = clusterMgr.getLoadBalancer().getFIP( );
					if( fip == null )
					{
						logger.error(" Could not get FIP from LoadBalancer, returning null ");
						return null;
					}
				}// end try
				catch( Exception ex)
				{
					logger.error(" ERROR while getting FIP from LoadBalancer ");
					return null;
				}
				partition.setFloatingIp( fip );
				partition.setMember(selfId, AseRoles.ACTIVE);
				activeSlots--;
				if (logger.isInfoEnabled()) {
					logger.info("Role is ACTIVE for " + partition.getFloatingIp());
				}
				return new PartitionInfo(partition.getFloatingIp(), 
						selfId, AseRoles.ACTIVE);
			}
		}
		return null;
	}

	/**
	 * decide whether be able to take standby role for each partition
	 * @return
	 */
	private ArrayList decideStandbyRole() {
		if (logger.isInfoEnabled()) {
			logger.info("Decide standby role... in partitionTable "+partitionTable.size());
		}
		ArrayList roles = new ArrayList();
		for (int i = 0; i < partitionTable.size(); i++) {
			AsePartition partition = (AsePartition)partitionTable.getPartition(i);
			// @Siddharth 
			// the 2nd condition below signifies the case : An active has gone down and 
			// its standby is unable to takeover the fip for "any" reason
			// The standby will release itself from standby role in this partition
			// but not from the second one
			
			if (logger.isInfoEnabled()) {
				logger.info("Partition available in  partitionTable "+partition +"activeSlots "+ activeSlots);
			}
			if (activeSlots <= 0 || !partition.hasMember(AseRoles.ACTIVE) ) { //self active
				if (partition.isMember(selfId, AseRoles.STANDBY)) {
					//release self from standby role
					partition.setMember(null, AseRoles.STANDBY);
					if (logger.isInfoEnabled()) {

					logger.info(" resolveRole(), RELEASE_STANDBY ");
					}
					roles.add(new PartitionInfo(partition.getFloatingIp(), 
							selfId, AseRoles.RELEASE_STANDBY));
					if (logger.isInfoEnabled()) {
						logger.info("Role is RELEASE_STANDBY for " + partition.getFloatingIp());
					}
				}else{
					
					String fip=null;
					try {
						 fip = clusterMgr.getLoadBalancer().getFIP( );
						if( fip == null )
						{
							logger.error(" Could not get FIP from LoadBalancer, returning null ");
							return null;
						}
					}catch( Exception ex)
					{
						logger.error(" ERROR while getting FIP from LoadBalancer ");
						return null;
					}
					if (logger.isInfoEnabled()) {

						logger.info(" Take standby role  "+fip);
						}
					partition.setMember(selfId, AseRoles.STANDBY);
					partition.setMember(peerId, AseRoles.ACTIVE);
					partition.setFloatingIp(fip);
					roles.add(new PartitionInfo(fip,
							selfId, AseRoles.STANDBY));
					if (logger.isInfoEnabled()) {
						logger.info("Role is STANDBY for " + partition.getFloatingIp());
					}
				}
			} else {
				//take standby role only for partition that has active memeber
				if (//partition.hasMember(AseRoles.ACTIVE) && 
						!partition.hasMember(AseRoles.STANDBY) ) { 
					partition.setMember(selfId, AseRoles.STANDBY);
					partition.setMember(peerId, AseRoles.ACTIVE);
					roles.add(new PartitionInfo(partition.getFloatingIp(),
							selfId, AseRoles.STANDBY));
					if (logger.isInfoEnabled()) {
						logger.info("Role is STANDBY for " + partition.getFloatingIp());
					}
				}
			}
		}
		if(roles.size() == 0)	{
			clusterMgr.setRoleResolvedToStandby(false);	
		}else	{
			clusterMgr.setRoleResolvedToStandby(true);	
			
		
		}
		return roles;
	}

}
