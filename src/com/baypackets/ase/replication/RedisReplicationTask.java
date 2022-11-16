package com.baypackets.ase.replication;

import java.util.Base64;

import org.apache.log4j.Logger;

import com.agnity.redis.client.RedisWrapper;
import com.agnity.redis.exeption.RedisLettuceCommandTimeoutException;
import com.agnity.redis.exeption.RedisLettuceConnectionException;
import com.baypackets.ase.common.RedisAlarmHandler;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.redis.RedisTask;

public class RedisReplicationTask implements RedisTask {

	private static Logger logger = Logger.getLogger(RedisReplicationTask.class);
	int index = -1;
	byte[] packet = null;
	short action = ReplicationMessage.REPLICATE;
	String ctxtId = null;
	String replId = null;
	String susbsytemId = null;

	RedisWrapper redisWrapper = null;

	public RedisReplicationTask(int Qindex, String ctxtId,
			short action, byte[] packet, String replId,String subsystemId) {

		this.action=action;
		this.index = Qindex;
		this.packet = packet;
		this.ctxtId = ctxtId;
		this.replId = replId;
		this.susbsytemId = subsystemId;
		this.redisWrapper = (RedisWrapper) Registry.lookup(Constants.REDIS_WRAPPER);
	}

	@Override
	public boolean execute() {
		// TODO Auto-generated method stub
		
		boolean success=false;
		try {

			String data = Base64.getEncoder().encodeToString(packet);

			if (action == ReplicationMessage.CLEANUP) {

				if (logger.isDebugEnabled()) {
					logger.debug("Action is cleanup Remove replication data");
				}
				if (replId == null) {// for sip call replid=null

					if (logger.isDebugEnabled()) {

						logger.debug("Remove context id " + ctxtId);
					}
					redisWrapper.getListOperations().removeAllElements(
							ReplicationManagerImpl.REPL_MSG_LIST_PREFIX
									+ ctxtId);
//					redisWrapper
//							.getSetOperations()
//							.removeFrmSet(
//									ReplicationManagerImpl.REPL_CALLID_SET_NAME,
//									ctxtId);

					 redisWrapper.getListOperations().removeFromList(ReplicationManagerImpl.REPL_CALLID_SET_NAME,0L,ctxtId);

				} else {// for tcap call redid=dialogid

					if (logger.isDebugEnabled()) {

						logger.debug("Remove replication id " + replId);
					}
					redisWrapper.getListOperations().removeAllElements(
							ReplicationManagerImpl.REPL_MSG_LIST_PREFIX
									+ replId);

					 redisWrapper.getListOperations().removeFromList(ReplicationManagerImpl.REPL_CALLID_SET_NAME,0L,replId);
//					redisWrapper
//							.getSetOperations()
//							.removeFrmSet(
//									ReplicationManagerImpl.REPL_CALLID_SET_NAME,
//									replId);
				}
			} else {

				if (logger.isDebugEnabled()) {
					logger.debug("pushInList replication message to be sent is for ...."
							+ " context id is "
							+ ctxtId
							+ " replid is "
							+ replId);
				}

				if (logger.isDebugEnabled()) {
					logger.debug("pushInList packet size to be set is ...."
							+ packet.length);
				}
				if (replId == null) {

					if (logger.isDebugEnabled()) {
						logger.debug("pushInList for contextid " + ctxtId);
					}
					/**
					 * below line added now
					 * 
					 */
//					redisWrapper
//							.getSetOperations()
//							.addInSet(
//									ReplicationManagerImpl.REPL_CALLID_SET_NAME,
//									ctxtId);
					
					if(!redisWrapper.getListOperations().getListAll(ReplicationManagerImpl.REPL_CALLID_SET_NAME).contains(ctxtId)){
						if (logger.isDebugEnabled()) {
							logger.debug(" pushInList for contextid does not yet exists in list" + ctxtId);
						}
					   redisWrapper.getListOperations().pushInList(
							ReplicationManagerImpl.REPL_CALLID_SET_NAME
									, ctxtId,"LEFT");
					}else{
						if (logger.isDebugEnabled()) {
							logger.debug("donot pushInList for contextid alreadye xists in list" + ctxtId);
						}
					}

					redisWrapper.getListOperations().pushInList(
							ReplicationManagerImpl.REPL_MSG_LIST_PREFIX
									+ ctxtId, data, "RIGHT");
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("pushInList for replid " + replId);
					}
					
					if(!redisWrapper.getListOperations().getListAll(ReplicationManagerImpl.REPL_CALLID_SET_NAME).contains(replId)){
						if (logger.isDebugEnabled()) {
							logger.debug("pushInList for replid does not exists yet " + replId);
						}
					     redisWrapper.getListOperations().pushInList(
							ReplicationManagerImpl.REPL_CALLID_SET_NAME
									, replId,"LEFT");
					}else{
						if (logger.isDebugEnabled()) {
							logger.debug("donot pushInList for replid alreadye xists in list" + replId);
						}
					}
//					redisWrapper
//							.getSetOperations()
//							.addInSet(
//									ReplicationManagerImpl.REPL_CALLID_SET_NAME,
//									replId);

					redisWrapper.getListOperations().pushInList(
							ReplicationManagerImpl.REPL_MSG_LIST_PREFIX
									+ replId, data, "RIGHT");

				}
			}

			RedisAlarmHandler.redisIsAccessible(susbsytemId);

			success=true;
		} catch (RedisLettuceConnectionException e) {
			logger.error("exception while writing headrtbeat in redis " + e);

			RedisAlarmHandler.redisNotAccessible(susbsytemId);
		} catch (RedisLettuceCommandTimeoutException e) {
			logger.error("exception while writing headrtbeat in redis " + e);
			RedisAlarmHandler.redisNotAccessible(susbsytemId);
		} catch (Exception e) {
			logger.error("exception while executing redis task " + e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Leaving ..");
		}
		return success;
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return index;
	}

}
