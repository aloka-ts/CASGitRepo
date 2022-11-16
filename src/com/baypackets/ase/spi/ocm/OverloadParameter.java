package com.baypackets.ase.spi.ocm;

/**
This class contains the attributes like ID, type, current value and the maximum value configured for the parameter, etc.
*/
public interface OverloadParameter {

	/**
	Definition of the various Overload Parameter Types.
	*/
	public static enum Type{
		/**
		CPU Usage Parameter Type. Parameter will contain the % of the current CPU Usage by all the processes.
		*/
		CPU_USAGE, 
		/**
		Memory Usage Parameter Type. Parameter will contain the % of the allocated memory used by this application.
		*/
		MEMORY_USAGE, 
		/**
		Protocol Session Count Type. Parameter will contain the count of the currently active protocol sessions.
		*/
		PROTOCOL_SESSION_COUNT, 
		/**
		Application Session Count Type. Parameter will contain the count of the currently active application sessions.
		*/
		APPLICATION_SESSION_COUNT, 
		/**
		Response Time Type. Parameter will contain the current average response time for the requests.
		*/
		RESPONSE_TIME,
		
	    CONTENTION_LEVEL_ONE_MEMORY_USAGE,
	    CONTENTION_LEVEL_TWO_MEMORY_USAGE ,
	    CONTENTION_LEVEL_THREE_MEMORY_USAGE ,
	    CONTENTION_LEVEL_ONE_CPU_USAGE ,
	    CONTENTION_LEVEL_TWO_CPU_USAGE ,
        CONTENTION_LEVEL_THREE_CPU_USAGE ,
	    CONTENTION_LEVEL_ONE_ACTIVE_CALLS ,
	    CONTENTION_LEVEL_TWO_ACTIVE_CALLS ,
	    CONTENTION_LEVEL_THREE_ACTIVE_CALLS,
	    NETWORK_TRANSACTIONS_PER_SECOND,
	    AGGREGATED_TRANSACTIONS_PER_SECOND,
	    NEW_CALLS_PER_SECOND
	    
	}

	/**
	Returns the ID for this Parameter.	
	*/
	public int getId();
	/**
	Returns the type of this Parameter.
	*/
	public Type getType();
	/**
	Returns the current value of this parameter.
	*/
	public float getValue();
	/**
	Returns the maximum limit configured for this parameter.
	*/
	public float getMaxLimit();
}
