package com.baypackets.ase.control;

import java.util.EventObject;

import com.baypackets.ase.channel.*;
import com.baypackets.ase.util.AseStrings;

public class PeerStateChangeEvent extends EventObject
{
	public static final int PR_DOWN_CLOSE_CONN		= 1;
	public static final int PR_UP_OPEN_CONN			= 2;
	public static final int PR_READY				= 3;
	
	public static final int PR_READY_START_REPL		= 4;
	public static final int PR_READY_EXPECT_REPL	= 5;

	public static final int PR_NOT_READY_STOP_REPL		= 6;
	public static final int PR_NOT_READY_NO_OP		= 7;
	public static final int PR_NOT_READY_DONT_EXPECT_REPL	= 8;

	public static final int PR_TM_OUT_SYNC			= 9;
	public static final int PR_TM_IN_SYNC			= 10;

	/*---------To begin with we may just implement till here
	 * and worry about finer grained messages later, ie we
	 * just treat all *_STOP_REPL as PR_NOT_READY_STOP_REPL
	 * */
	public static final int PR_VER_CHANGE_NO_OP		= 11;
	public static final int PR_VER_CHANGE_STOP_REPL		= 12;
	public static final int PR_VER_CHANGE_DONT_EXPECT_REPL	= 13;


	public static final int PR_MODE_CHANGE_NO_OP		= 14;
	public static final int PR_MODE_CHANGED_STOP_REPL 	= 15;
	public static final int PR_MODE_CHANGED_DONT_EXPECT_REPL= 16;

	public static final int PR_ROLE_CHANGED_NO_OP		= 17;
	public static final int PR_ROLE_CHANGED_STOP_REPL	= 18;
	public static final int PR_ROLE_CHANGED_DONT_EXPECT_REPL= 19;


	private AseSubsystem subsystem;
	private int event_id;
	private String event_data;
	private boolean doReplication;
	
	public PeerStateChangeEvent 
          (AseSubsystem subsystem, int event_id, String event_data, PeerStateChangeSource source)
	{
		super (source);
		this.subsystem = subsystem;
		this.event_id = event_id;
		this.event_data = event_data;
		this.setDoReplication(true);
	}

	public AseSubsystem getSubsystem()
	{
		return subsystem;
	}

	public int getEventId()
	{
		return event_id;
	}

	public String getEventData()
	{
		return event_data;
	}
	
	/**
	 * @param doReplication the doReplication to set
	 */
	public void setDoReplication(boolean doReplication) {
		this.doReplication = doReplication;
	}

	/**
	 * @return the doReplication
	 */
	public boolean isDoReplication() {
		return doReplication;
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("PeerStateChangeEvent [");
		buffer.append(subsystem);
		buffer.append(AseStrings.COMMA);
		buffer.append(event_id);
		buffer.append(AseStrings.COMMA);
		buffer.append(event_data);
		buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
		return buffer.toString();
	}
}
