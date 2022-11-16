/*******************************************************************************
 *   Copyright (c) 2020 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.baypackets.ase.startup;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.agnity.redis.client.RedisWrapper;
import com.agnity.redis.pubsub.listener.RedisMessageListener;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.control.ClusterManager;
import com.baypackets.ase.control.ControlManager;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ObjectPairImpl;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

public class AsePubSubPropsListener implements
		RedisMessageListener<String, String> {

	private String channelSub = null;

	private static Logger logger = Logger
			.getLogger(AsePubSubPropsListener.class);

	Object[] components = null;
	RedisWrapper redisWrapper=null;
	ClusterManager clusterMgr=null;
	

	public AsePubSubPropsListener(String channel, Object[] components,RedisWrapper wrapper) {
		channelSub = channel;
		this.components = components;
		this.redisWrapper=wrapper;
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("AsePubSubPropsListener constructor called with channel and components"
					+ channel);
		}

	}

	public AsePubSubPropsListener(String channel,RedisWrapper wrapper) {
		channelSub = channel;
		this.redisWrapper=wrapper;
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("AsePubSubPropsListener constructor called with channel "
					+ channel);
		}

	}
	/**
	 * This method is used to process a message received on a channel
	 */
	@Override
	public void message(String channel, String message) {
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("Entering message(channel,message) -->Channel : " + channel + " :  message :  " + message
					+ " :  time : " + LocalDateTime.now());
		}

			if (channel.equals(AseMain.propertyChannelName)) {

				if (message != null && !message.isEmpty()) {
					String[] props = message.split(":");
					if (props.length == 2) {
						if (logger.isEnabledFor(Level.INFO)) {
							logger.info("set : " + props[0] + " :  value :  "
									+ props[1]);
						}
						if (props[0] != null && !props[0].isEmpty()
								&& props[1] != null && !props[1].isEmpty()) {
							Pair[] pairs = new Pair[1];
							pairs[0] = new ObjectPairImpl();
							pairs[0].setFirst(props[0]);
							pairs[0].setSecond(props[1]);

							try {

								BaseContext.getConfigRepository().setValue(
										props[0], props[1]);

								if (logger.isEnabledFor(Level.INFO)) {
									logger.info("update in redis db "
											+ AseMain.propertyHashes);
								}
								redisWrapper.getHashOperations().addInHashes(
										AseMain.propertyHashes, props[0],
										props[1]);

								updateConfiguration(pairs, new OperationType(
										OperationType.MODIFY));

							} catch (UnableToUpdateConfigException e) {
								logger.error(" UnableToUpdateConfigException thrown !!!!!!!"
										+ e);
							} catch (Exception e) {
								logger.error(" Excpetion thrown !!!!!!!" + e);
							}

							if (logger.isEnabledFor(Level.INFO)) {
								logger.info("get updated value: "
										+ props[0]
										+ " :  value :  "
										+ BaseContext.getConfigRepository()
												.getValue(props[0]));
							}
						}
					}
				}
			} else if (channel.equals(AseMain.standbyNotiChannelName)) {
				
				if (logger.isEnabledFor(Level.INFO)) {
					logger.info("Standby notified that it has come up " + message);
				}
				  clusterMgr = (ClusterManager) Registry.lookup(Constants.NAME_CLUSTER_MGR);
				  clusterMgr.standbySubsystemUp(message);
				
			}
		
	}

	/**
	 * This method is used to handle a message with sepcific pattern on a channel
	 */
	@Override
	public void message(String pattern, String channel, String message) {

		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("Entering message(pattern,channel,message) -->Pattern : " + pattern + "Channel : " + channel
					+ " :  message :  " + message + " :  time : "
					+ LocalDateTime.now());
		}
		if (channel.equals(AseMain.propertyChannelName)) {

			if (message != null && !message.isEmpty()) {
				String[] props = message.split(":");
				if (props.length == 2) {
					if (logger.isEnabledFor(Level.INFO)) {
						logger.info("set : " + props[0] + " :  value :  "
								+ props[1]);
					}
					if (props[0] != null && !props[0].isEmpty()
							&& props[1] != null && !props[1].isEmpty()) {
						Pair[] pairs = new Pair[1];
						pairs[0] = new ObjectPairImpl();
						pairs[0].setFirst(props[0]);
						pairs[0].setSecond(props[1]);

						try {

							BaseContext.getConfigRepository().setValue(
									props[0], props[1]);

							if (logger.isEnabledFor(Level.INFO)) {
								logger.info("update in redis db "
										+ AseMain.propertyHashes);
							}
							redisWrapper.getHashOperations().addInHashes(
									AseMain.propertyHashes, props[0],
									props[1]);

							updateConfiguration(pairs, new OperationType(
									OperationType.MODIFY));

						} catch (UnableToUpdateConfigException e) {
							logger.error(" UnableToUpdateConfigException thrown !!!!!!!"
									+ e);
						} catch (Exception e) {
							logger.error(" Excpetion thrown !!!!!!!" + e);
						}

						if (logger.isEnabledFor(Level.INFO)) {
							logger.info("get updated value: "
									+ props[0]
									+ " :  value :  "
									+ BaseContext.getConfigRepository()
											.getValue(props[0]));
						}
					}
				}
			}
		} else if (channel.equals(AseMain.standbyNotiChannelName)) {
			
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info("Standby notified that it has come up " );
			}
			  clusterMgr = (ClusterManager) Registry.lookup(Constants.NAME_CLUSTER_MGR);
			  clusterMgr.standbySubsystemUp(message);
			
			

		}

	}

	@Override
	public void subscribed(String channel, long count) {

		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("subscribed for Channel : " + channel + " :  count :  " + count
					+ " :  time : " + LocalDateTime.now());
		}

	}

	@Override
	public void psubscribed(String pattern, long count) {

		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("psubscribed for pattern : " + pattern + " :  count :  " + count
					+ " :  time : " + LocalDateTime.now());
		}

	}

	@Override
	public void unsubscribed(String channel, long count) {

		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("unsubscribed for Channel : " + channel + " :  count :  " + count
					+ " :  time : " + LocalDateTime.now());
		}

	}

	@Override
	public void punsubscribed(String pattern, long count) {

		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("punsubscribed pattern : " + pattern + " :  count :  " + count
					+ " :  time : " + LocalDateTime.now());
		}

	}

	public void updateConfiguration(Pair[] configdata, OperationType opTypeValue)
			throws UnableToUpdateConfigException {

		if (configdata != null) {
			for (int i = 0; i < configdata.length; i++) {
				String paramName = configdata[i].getFirst().toString();
				String paramValue = configdata[i].getSecond().toString();
				if (logger.isEnabledFor(Level.INFO)) {
					logger.info("updateConfiguration : name " + paramName
							+ " :  paramValue :  " + paramValue);
				}
			}// @end for loop on config data
		}// @end if configdata not null

		Object[] objArray = components;
		if (objArray != null && objArray.length > 0) {

			java.util.List<Object> list = Arrays.asList(objArray);

			Iterator iterator = list.iterator();

			while (iterator.hasNext()) {
				Object com = iterator.next();
				if (com instanceof MComponent) {
					MComponent comp = (MComponent) com;

					comp.updateConfiguration(configdata, opTypeValue);
				} else {
					continue;
				}
			}
		}
	}

}
