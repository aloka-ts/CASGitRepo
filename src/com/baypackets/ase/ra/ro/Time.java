/**
 * Filename:	Time.java
 * Created On:	03-Oct-2006
 */

package com.baypackets.ase.ra.ro;

/**
 * This class represents data type <code>Time</code> as per Diameter RFC 3588.
 * This repersents the seconds since 0h on 01 jan 1900 in C.U.T.
 *
 * @author Neeraj Jain
 */

public final class Time
{
	private int _time;

	/**
	 * creates a new instance of data type >code>Time</code>.
	 *
	 */

	public Time(int time) {
		this._time = time;
	}

	/**
	 * This method returns the time in int since 0h on 01 jan 1900 in C.U.T.
	 *
	 * @return <code>int<code> object contining time in seconds.
	 */	

	public int get() {
		return this._time;
	}

	/**
	 * This method sets the time in seconds.
	 *
	 * @param time - <code>int</code> object containing time in seconds.
	 */

	public void set(int time) {
		this._time = time;
	}

	/**
	 * This method compares a given time with the time already set.
 	 *
	 * @return <code>boolean</code> - true if given time is same
	 * 								  false if given time is not same.
	 *
	 */

	public boolean equals(int other) {
		return this._time == other;
	}

	/**
	 * This method returns the set int time in String format.
	 *
	 */

	public String toString() {
		return Integer.toString(this._time);
	}
}
