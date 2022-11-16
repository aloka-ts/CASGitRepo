package com.baypackets.ase.ra.radius.dictionary;


public class DefaultDictionary extends MemoryDictionary{
	private static DefaultDictionary instance = null;
	private DefaultDictionary(){
		
	}
	private DefaultDictionary(org.tinyradius.dictionary.DefaultDictionary dict){
		super(dict);
	}
	
	/**
	 * Creates the singleton instance of this object
	 * and parses the classpath ressource.
	 */
	
	static {
		try {
			instance = new DefaultDictionary((org.tinyradius.dictionary.DefaultDictionary) org.tinyradius.dictionary.DefaultDictionary.getDefaultDictionary());
		} catch (Exception e) {
			throw new RuntimeException("default dictionary unavailable", e);
		}
	}
	/**
	 * Returns the singleton instance of this object.
	 * @return DefaultDictionary instance
	 */
	public static Dictionary getDefaultDictionary() {
		return instance;
	}
}
