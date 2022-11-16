package com.baypackets.ase.control;

public class AseModes
{
	public static final short NON_FT			= 1;
	public static final short FT_ONE_PLUS_ONE 	= 2;
	public static final short FT_N_PLUS_K		= 3;
	public static final short HA				= 4;
	public static final short FT_N_PLUS		= 5;
	public static final short FT_N_PLUS_LITE	= 6;
	
	public static boolean isFtMode(short mode){
		return (mode == FT_ONE_PLUS_ONE || mode == FT_N_PLUS_K);
	}
}
