package com.genband.m5.maps.common;

public class CommonUtil {

	public int trObjectToPrimitive (Object o, int i) {
		return ((Integer) o).intValue();
	}
	public short trObjectToPrimitive (Object o, short s) {
		return ((Short) o).shortValue();
	}
	public long trObjectToPrimitive (Object o, long l) {
		return ((Long) o).longValue();
	}
	public byte trObjectToPrimitive (Object o, byte b) {
		return ((Byte) o).byteValue();
	}
	public float trObjectToPrimitive (Object o, float f) {
		return ((Float) o).floatValue();
	}
	public double trObjectToPrimitive (Object o, double d) {
		return ((Double) o).doubleValue();
	}
	public char trObjectToPrimitive (Object o, char c) {
		return ((Character) o).charValue();
	}
	public boolean trObjectToPrimitive (Object o, boolean b) {
		return ((Boolean) o).booleanValue();
	}
	
	public static Object getWrapperForPrimitive (int i) {
		return new Integer (i);
	}
	public static Object getWrapperForPrimitive (short s) {
		return new Short (s);
	}
	public static Object getWrapperForPrimitive (long l) {
		return new Long (l);
	}
	public static Object getWrapperForPrimitive (byte b) {
		return new Byte (b);
	}
	public static Object getWrapperForPrimitive (float f) {
		return new Float (f);
	}
	public static Object getWrapperForPrimitive (double d) {
		return new Double (d);
	}
	public static Object getWrapperForPrimitive (char c) {
		return new Character (c);
	}
	public static Object getWrapperForPrimitive (boolean b) {
		return new Boolean (b);
	}
}
