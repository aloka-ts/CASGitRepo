package com.baypackets.ase.util.stpool;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.InitializationFailedException;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

public class AseSharedTokenPool implements MComponent ,CommandHandler {
		
	/**
	 * This method fetches the tokens from shared token pool depending upon the query configured
	 * DB 
	 */
	private Map<String,ConcurrentLinkedQueue<Token>> sharedTokenPoolMap= new ConcurrentHashMap<String,ConcurrentLinkedQueue<Token>>();
	private Map<String,ConcurrentLinkedQueue<Token>> intialSharedTokenPoolMap= null;//new ConcurrentHashMap<String,ConcurrentLinkedQueue<Token>>();
	
	/**
	 * This map is used to keep mapping of token with service id
	 */

	private Map<String,String> serviceIdTokenMapping= new ConcurrentHashMap<String,String>();
	
	private ConcurrentLinkedQueue<Token> sharedTokens= null;//new ConcurrentLinkedQueue<Token>();
	private static Logger _logger = Logger
			.getLogger(AseSharedTokenPool.class);
	private boolean enabled=true;
	
	private static final String	QRY_FETCH_SHARED_TOKEN_FOR_KEYS= "SELECT token FROM shared_token_pool WHERE token_key=?";
	
	private static final String	QRY_FETCH_SHARED_TOKENS= "SELECT token FROM shared_token_pool";
	
	private static final String	QRY_FETCH_KEYS= "SELECT distinct token_key FROM shared_token_pool";
	
	int sharedTokenPoolSizeWOKeys;
	
	
	
	private static StringManager _strings = StringManager
			.getInstance("com.baypackets.ase.util.stpool");
	
	/**
	 * Command used to display the status of all media servers to a telnet
	 * console.
	 */
	private static String STPOOL_INFO = "stpool-info".intern();
	private static String STPOOL_CLEAN = "stpool-clean".intern();
	private static String STPOOL_STATUS= "stpool-status".intern();

	private static final short CMD_STPOOL_INFO = 8;
	private static final short CMD_CLEAN_POOL = 9;
	private static final short CMD_STPOOL_TOKEN_STATUS=10;

	private static final String STPOOL_SIZE = "size";
	private static final String STPOOL_USED_SIZE = "used-size";
	

	/**
	 * This method is invoked to display the current status of all provisioned
	 * media servers when the user enters the command, "ms-status" from a telnet
	 * console. The following info is displayed for each media server:
	 * <ul>
	 * <li> Unique identifier
	 * <li> Vendor name
	 * <li> IP address
	 * <li> Port number of listening process
	 * <li> Availability status (i.e. UP or DOWN)
	 * <li> Interval (in seconds) for which to ping the media server.
	 * <li> HeartBeat retry count.
	 * </ul>
	 */
	@Override
	public String execute(String cmd, String[] args, InputStream in,
			OutputStream out) throws CommandFailedException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("execute() called..." + cmd);
		}
		short command = -1;

		if (cmd.equals(STPOOL_INFO)) {
			command = CMD_STPOOL_INFO;
		} else if (cmd.equals(STPOOL_CLEAN)) {
			command = CMD_CLEAN_POOL;
		}else if (cmd.equals(STPOOL_STATUS)) {
			command = CMD_STPOOL_TOKEN_STATUS;
		}

		String retValue = this.execute(command, cmd, args, in, out);
		return retValue;
	}

	
	public String execute(short cmd, String command, String[] args,
			InputStream is, OutputStream os) throws CommandFailedException {

		try {
			switch (cmd) {
			case CMD_STPOOL_INFO:

				if (args.length == 1) {

					if (args[0].equalsIgnoreCase(STPOOL_SIZE)) {
						return "Total Pool Size without key is: "
								+ getPoolSize();
					} else if (args[0].equalsIgnoreCase(STPOOL_USED_SIZE)) {
						return "Used Pool Size without key  is: "
								+ getUsedPoolSize()
								+ " \n Note: if size is -1 then pool does not exists !!";
					}else{
						return this.getUsage(STPOOL_INFO);
					}

				} else if (args.length == 2) {

					if (args[0].equalsIgnoreCase(STPOOL_SIZE)) {
						return "Total Pool Size with key " + args[1] + " is: "
								+ getPoolSize(args[1]);
					} else if (args[0].equalsIgnoreCase(STPOOL_USED_SIZE)) {
						return "Used Pool Size with key " + args[1] + " is: "
								+ getUsedPoolSize(args[1]);

					}else {
						return this.getUsage(STPOOL_INFO);
					}
				}else{
					return this.getUsage(STPOOL_INFO);
				}
				
			case CMD_CLEAN_POOL:

				if (args.length == 0) {

					 return cleanStpool();

				}else if (args.length == 1) {

					return cleanStpool(args[0]);

				} else {
					return this.getUsage(STPOOL_CLEAN);
				}	
	
			case CMD_STPOOL_TOKEN_STATUS:

				if (args.length == 1) {

					String svcId=getServiceIdForTokenValue(args[0]);
					
					if(svcId==null){
						return "No serviceId assigned found to this token "+args[0]+" it may be a free token or invalid token !!!";
					}else{
						return "This token is used by Service id "+ svcId;
					}

				}else if (args.length > 1
						&& args[0].equalsIgnoreCase("free-token")) {
					
					boolean isPushed = false;
					String svcId =null;
					
					if (args.length == 2) {

						svcId = getServiceIdForTokenValue(args[1]);

						if (svcId == null) {
							return "Token is not assigned to any service !!!";
						}

						Token token = new Token();
						token.setValue(args[1]);
						token.setUsed(true);

						isPushed = push(token, svcId);

					}
					if (args.length == 3) {

						svcId = getServiceIdForTokenValue(args[2]);

						if (svcId == null) {
							return "Token is not assigned to any service !!!";
						}

						Token token = new Token();
						token.setValue(args[2]);
						token.setUsed(true);

						isPushed = push(args[1], token, svcId);
						//
					}
					
					if (isPushed) {
						return "Token is pushed back in pool !!!";
					} else {
						return "Token could not be pushed back for serviceid "
								+ svcId;
					}

				} else {
					return this.getUsage(STPOOL_STATUS);
				}
			
			}

		} catch (Exception e) {
			_logger.error("execute" + e.toString(), e);

			return e.getMessage();
		}
		 return this.getUsage(STPOOL_INFO);
	}

	private String cleanStpool() {
		try {
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("cleanStpool" );
			}
			
			sharedTokens = fetchSharedTokens();
			sharedTokenPoolSizeWOKeys = sharedTokens.size();
			serviceIdTokenMapping.clear();
			return "Shared token pool is cleaned successfully !!!";
		} catch (Exception e) {
			_logger.error("Could not clean stpool", e);
		}

		return "Could not clean";
	}


	private String cleanStpool(String key) {

		try {
			if (_logger.isDebugEnabled()) {
				_logger.debug("cleanStpool for key "+key );
			}
			
			ConcurrentLinkedQueue<Token> genericNumberList = fetchSharedTokensForKey(key);

			sharedTokenPoolMap.put(key, genericNumberList);
			intialSharedTokenPoolMap = sharedTokenPoolMap;
			serviceIdTokenMapping.clear();

			return "Shared token pool is cleaned successfully for key " + key + "!!!";
		} catch (Exception e) {
			_logger.error("Could not clean stpool", e);
		}

		return "stpool could not be cleaned for key " + key;

	}


	@Override
	public void updateConfiguration(Pair[] arg0, OperationType arg1)
			throws UnableToUpdateConfigException {
	}

	/**
	 * This method is invoked by the EMS to update the state of this component.
	 * If the value of the given "state" parameter is LOADED, the meta data on
	 * all provisioned media servers will be read from the backing store.
	 */
	public void changeState(MComponentState state)
			throws UnableToChangeStateException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("changeState() called.  Setting component state to: "
					+ state);
		}

		try {
			if (state.getValue() == MComponentState.RUNNING) {
				this.initialize();
			}
		} catch (Exception e) {
			String msg = "Error occurred while setting component state: "
					+ e.getMessage();
			_logger.error(msg, e);
			throw new UnableToChangeStateException(msg);
		}
	}
	
	/**
	 * This method initializes this object's state using the parameters
	 * specified in the ConfigRepository singleton. It internally calls the
	 * "initialize(Properties)" method.
	 * 
	 * @see com.baypackets.slee.common.ConfigRepository
	 * @see #initialize(Properties)
	 */
	public void initialize() throws InitializationFailedException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("initialize(): Initializing component state from ConfigRepository...");
		}

		ConfigRepository config =BaseContext.getConfigRepository();
		

		String sysappEnable = (String) config
				.getValue(Constants.PROP_SYSAPP_ENABLE);

		if (sysappEnable == null
				|| !sysappEnable.trim().contains(Constants.SYSAPP_SHARED_TOKEN_POOL)) {
			_logger.info("roleChanged(): sysapp deploy properties does not contain 'stpool so not initialiging sharedtokenpool'.");
			enabled = false;
			return;
		}
		
		SharedTokenPoolDAO.init();
			
		if (_logger.isDebugEnabled()) {
				_logger.debug("AseSharedTokenPool manager is enabled loading tokens .......");
		}
		
		String stpQuery = (String) config
				.getValue(Constants.PROP_SHARED_TOKKEN_POOL_QUERY);
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("stpQuery is  ......."+stpQuery);
		}

		
		if (stpQuery != null
				&& QRY_FETCH_SHARED_TOKEN_FOR_KEYS.contains(stpQuery)) {

			List<String> keys = fetchSharedTokenPoolKeys();

			if (keys != null) {
				for (String key : keys) {

					if (_logger.isDebugEnabled()) {
						_logger.debug("fetch shared tokens for key ......."+key);
					}

					ConcurrentLinkedQueue<Token> genericNumberList = fetchSharedTokensForKey(key);

					sharedTokenPoolMap.put(key, genericNumberList);
				}
			}
			
			 intialSharedTokenPoolMap=sharedTokenPoolMap;
		} else {
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("fetch shared tokens without any keys .......");
			}

			sharedTokens = fetchSharedTokens();
			sharedTokenPoolSizeWOKeys=sharedTokens.size();
		}
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("initialize(): Registering command, " + STPOOL_INFO
					+ " with the TelnetServer...");
		}

		TelnetServer telnetServer = (TelnetServer) Registry
				.lookup(Constants.NAME_TELNET_SERVER);
		telnetServer.registerHandler(STPOOL_INFO, this, false);
		telnetServer.registerHandler(STPOOL_CLEAN, this, false);
		telnetServer.registerHandler(STPOOL_STATUS, this, false);
	}
	

	/**
	 * This method is used to fetch shared tokens from database for a specific key
	 * @param key
	 * @return
	 */
	public static ConcurrentLinkedQueue<Token> fetchSharedTokensForKey(String key) {
		Connection conn = null;
		if (_logger.isDebugEnabled()) {
			_logger.debug("fetchSharedTokensForKey ...key is "+key);
			
		}
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ConcurrentLinkedQueue<Token> sharedTokenList = null;

		for (int x = 0; x < 5; x++) {
			try {
				conn = SharedTokenPoolDAO.getConnection(5);
				if (conn == null) {
					_logger.error("fetchSharedTokensForKey() Failed to get database connection");
					return sharedTokenList;
				}
				
				stmt = conn.prepareStatement(QRY_FETCH_SHARED_TOKEN_FOR_KEYS);
				
				stmt.setQueryTimeout(100);
				stmt.setString(1, key);
				rs = stmt.executeQuery();
				sharedTokenList = new ConcurrentLinkedQueue<Token>();
				String tokenVal = null;
				while (rs.next()) {
					tokenVal = rs.getString("token");
					Token token=new Token();
					token.setValue(tokenVal);
					sharedTokenList.offer(token);
					
					if (_logger.isDebugEnabled()) {
						_logger.debug("fetchSharedTokensForKey..shared token read form db is "+tokenVal);
					}
					tokenVal = null;
				}
				//Break the retry loop on success
				if (_logger.isDebugEnabled()) {
					_logger.debug("DB operation performed in attempt " + (x + 1));
				}
				break;
			} catch (SQLException sqlEx) {
				_logger.error("Attempt " + x + "; Error while fetching tokens "
								+ sqlEx.getMessage());
				if (_logger.isInfoEnabled()) {
					_logger.info("Attempt " + x + "; Error while fetching tokens", sqlEx);
				}
			} finally {
				SharedTokenPoolDAO.cleanupResources(conn, stmt, null, rs);
			}
		}
		return sharedTokenList;
	}
	
	
	/**
	 * This method is used to fetch shared token pool tokens from db at initialization time.
	 * by default all the token are in used state initially.
	 * @return
	 */
	public static ConcurrentLinkedQueue<Token> fetchSharedTokens() {
		Connection conn = null;
		if (_logger.isDebugEnabled()) {
			_logger.debug("fetchSharedTokens..");
		}
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ConcurrentLinkedQueue<Token> sharedTokenList = null;

		for (int x = 0; x < 5; x++) {
			try {
				conn = SharedTokenPoolDAO.getConnection(5);
				if (conn == null) {
					_logger.error("fetchSharedTokens() Failed to get database connection");
					return sharedTokenList;
				}
				
				stmt = conn.prepareStatement(QRY_FETCH_SHARED_TOKENS);
				
				stmt.setQueryTimeout(100);
				rs = stmt.executeQuery();
				sharedTokenList = new ConcurrentLinkedQueue<Token>();
				String tokenVal = null;
				while (rs.next()) {
					tokenVal = rs.getString("token");
					
					if (_logger.isDebugEnabled()) {
						_logger.debug("fetchSharedTokens..shared token read form db is "+tokenVal);
					}
					Token token=new Token();
					token.setValue(tokenVal);
					sharedTokenList.offer(token);
					tokenVal = null;
				}
				//Break the retry loop on success
				if (_logger.isDebugEnabled()) {
					_logger.debug("DB operation performed in attempt " + (x + 1));
				}
				break;
			} catch (SQLException sqlEx) {
				_logger.error("Attempt " + x + "; Error while fetching Token  "
								+ sqlEx.getMessage());
				if (_logger.isInfoEnabled()) {
					_logger.info("Attempt " + x + "; Error while fetching Token", sqlEx);
				}
			} finally {
				SharedTokenPoolDAO.cleanupResources(conn, stmt, null, rs);
			}
		}
		return sharedTokenList;
	}
	
	
	
	/**
	 * This method is used to fetch shared token pool keys from db at initialization time
	 * @return
	 */
	public static List<String> fetchSharedTokenPoolKeys() {
		Connection conn = null;
		if (_logger.isDebugEnabled()) {
			_logger.debug("fetchSharedTokenPoolKeys...........");
		}
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<String> keysList = null;

		for (int x = 0; x < 5; x++) {
			try {
				conn = SharedTokenPoolDAO.getConnection(5);
				if (conn == null) {
					_logger.error("fetchSharedTokenPoolKeys() Failed to get database connection");
					return keysList;
				}
				
				stmt = conn.prepareStatement(QRY_FETCH_KEYS);
				
				stmt.setQueryTimeout(100);
				rs = stmt.executeQuery();
				keysList = new ArrayList<String>();
				String key = null;
				while (rs.next()) {
					key = rs.getString("token_key");
					keysList.add(key);
					key = null;
				}
				//Break the retry loop on success
				if (_logger.isDebugEnabled()) {
					_logger.debug("DB operation performed in attempt " + (x + 1));
				}
				break;
			} catch (SQLException sqlEx) {
				_logger.error("Attempt " + x + "; Error while fetching keys "
								+ sqlEx.getMessage());
				if (_logger.isInfoEnabled()) {
					_logger.info("Attempt " + x + "; Error fetching keys", sqlEx);
				}
			} finally {
				SharedTokenPoolDAO.cleanupResources(conn, stmt, null, rs);
			}
		}
		return keysList;
	}

	/**
	 * This method is used to push a token in the  token pool for a specific key.if the token being pushed is
	 * not used yet then this method returns false , if the token is pushed successfully then
	 * it returns true
	 * @param key
	 * @param token
	 */
	public boolean push(String key, Token token,String serviceId) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("push token for key " + key + " token " + token);
		}
		
		if(!token.isUsed()){
			if (_logger.isDebugEnabled()) {
				_logger.debug("could not push token into pool " + token +" for key"+ key+" as token is not used yet");
			}
			return false;
		}

		if (key == null) {
			return push(token,serviceId);
			
		}
		@SuppressWarnings("unchecked")
		ConcurrentLinkedQueue<Token> sharedTokenList = (ConcurrentLinkedQueue<Token>) sharedTokenPoolMap
				.get(key);
		
		if (sharedTokenList != null) {
			token.setUsed(false);
			sharedTokenList.offer(token);
			serviceIdTokenMapping.remove(token.getValue());
		}else{
			if (_logger.isDebugEnabled()) {
				_logger.debug("could not push token into pool " + token +" for key"+ key+" as token list not found for key "+key);
			}
		}
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("token pushed in pool for key " + key + " token " + token);
		}
		return true;
	}

	
	/**
	 * This method fetches a token from the pool for a specific key . if token is not available then null
	 * will be returned
	 * @param key
	 * @return
	 */
	public Token pop(String key,String serviceId) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("pop token for key " + key);
		}

		if (key == null) {
			return pop(serviceId);
		}

		@SuppressWarnings("unchecked")
		ConcurrentLinkedQueue<Token> tokenList = (ConcurrentLinkedQueue<Token>) sharedTokenPoolMap
				.get(key);
		
		Token token = null;
		if (tokenList != null) {
			token = tokenList.poll();
			if (token != null) {
				token.setUsed(true);
				serviceIdTokenMapping.put(token.getValue(), serviceId);
			} else {
				if (_logger.isDebugEnabled()) {
					_logger.debug("could not pop token from the pool for key"
							+ key);
				}
			}
		} else {
			if (_logger.isDebugEnabled()) {
				_logger.debug("could not pop token into pool " + token
						+ " for key" + key
						+ " as token list not found for key ");
			}
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("poped token from pool " + token);
		}
		return token;
	}

	/**
	 * This method is used to push a token in the pool, if the token being pushed is
	 * not used yet then this method returns false , if the token is pushed successfully then
	 * it returns true.
	 * @param token
	 * @return
	 */
	public boolean push(Token token,String serviceId) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("push token into pool " + token);
		}
		if(!token.isUsed()){
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("could not push token into pool " + token +" as token is not used yet");
			}
			return false;
		}
		token.setUsed(false);
		sharedTokens.offer(token);
		
		serviceIdTokenMapping.remove(token.getValue());
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("token pushed in pool " + token);
		}
		return true;
	}

	/**
	 * This method fetches a token from the pool . if token is not available then  null
	 *  will be returned
	 * @return
	 */
	public Token pop(String serviceId) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("pop token form pool for serviceId "+serviceId);
		}
		Token token = sharedTokens.poll();

		if(token!=null){
			token.setUsed(true);
			serviceIdTokenMapping.put(token.getValue(), serviceId);
		}else{
			if (_logger.isDebugEnabled()) {
				_logger.debug("could not pop token from the pool for serviceId "+serviceId);
			}
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("poped token is" + token);
		}
		return token;
	}
	
	/**
	 * This method is used to get pool size
	 * @return
	 */
	public int getPoolSize(){
		if (_logger.isDebugEnabled()) {
			_logger.debug("getPoolSize() returns "+sharedTokenPoolSizeWOKeys);
		}
		return sharedTokenPoolSizeWOKeys;
	}
	
	/**
	 * This method is used to get pool size for a specific key
	 * @param key
	 * @return
	 */
	public int getPoolSize(String key) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("getPoolSize() for key  " + key);
		}
		ConcurrentLinkedQueue<Token> tokenList = (ConcurrentLinkedQueue<Token>) intialSharedTokenPoolMap
				.get(key);

		int poolSize = -1;

		if (tokenList != null) {

			poolSize = tokenList.size();
			if (_logger.isDebugEnabled()) {
				_logger.debug("getPoolSize()  returns " + tokenList.size());
			}
			return poolSize;
		}
		return poolSize;
	}
	
	/**
	 * This method is used to get current used pool size
	 * @return
	 */
	public int getUsedPoolSize() {

		int usedPoolSize = -1;
		if (sharedTokens != null) {
			usedPoolSize = sharedTokens.size();
			if (_logger.isDebugEnabled()) {
				_logger.debug("getUsedPoolSize() returns " + usedPoolSize);
			}
		}
		return usedPoolSize;
	}
	
	/**
	 * This method is used to get current used pool size for a specific key
	 * @param key
	 * @return
	 */
	public int getUsedPoolSize(String key) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("getUsedPoolSize() for key " + key);
		}
		ConcurrentLinkedQueue<Token> tokenList = (ConcurrentLinkedQueue<Token>) sharedTokenPoolMap
				.get(key);

		int poolSize = -1;
		
		if (tokenList != null) {

			poolSize = tokenList.size();
			if (_logger.isDebugEnabled()) {
				_logger.debug("getUsedPoolSize() returns " + poolSize);
			}
			return poolSize;
		}
		return poolSize;

	}
	
	
	/**
	 * This method is used to get serviceId corresponding to a token value 
	 * This method can be called by any CAs component to findout which service owns this particular token currently
	 * @param tokenValue
	 * @return
	 */
	public String getServiceIdForTokenValue(String tokenValue) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("getServiceIdForTokenValue() for token  "
					+ tokenValue);
		}
		String token = null;
		try {
			token = tokenValue;
		} catch (java.lang.NumberFormatException nfe) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("getServiceIdForTokenValue() could not parse tokenValue as integer returning serviceId as null  ");
			}
			return null;
		}
		String serviceId = serviceIdTokenMapping.get(token);

		if (_logger.isDebugEnabled()) {
			_logger.debug("getServiceIdForTokenValue() returns  " + serviceId);
		}
		return serviceId;
	}

	
	@Override
		public String getUsage(String cmd) {
			return _strings.getString("AseSharedTokenPool." + cmd + "Help");
		}
	

}
