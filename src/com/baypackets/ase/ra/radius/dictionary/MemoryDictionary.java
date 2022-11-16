package com.baypackets.ase.ra.radius.dictionary;
public class MemoryDictionary 
implements WritableDictionary {

	protected org.tinyradius.dictionary.MemoryDictionary stkObj=null;
	
	public MemoryDictionary(){
		stkObj=new org.tinyradius.dictionary.MemoryDictionary();
	}
	public MemoryDictionary(org.tinyradius.dictionary.MemoryDictionary stkObj){
		this.stkObj=stkObj;
	}
	public AttributeType getAttributeTypeByCode(int typeCode) {
		return new AttributeType(stkObj.getAttributeTypeByCode(typeCode));
	}
	public org.tinyradius.dictionary.MemoryDictionary getStkObj() {
		return stkObj;
	}
	/**
	 * Returns the specified AttributeType object.
	 * @param vendorCode vendor ID or -1 for "no vendor"
	 * @param typeCode attribute type code
	 * @return AttributeType or null
	 */
	public AttributeType getAttributeTypeByCode(int vendorCode, int typeCode) {
		org.tinyradius.dictionary.AttributeType attrib=stkObj.getAttributeTypeByCode(vendorCode, typeCode);
		if(attrib!=null)
			return new AttributeType(attrib);
		else 
			return null;
	}
	
	/**
	 * Retrieves the attribute type with the given name.
	 * @param typeName name of the attribute type 
	 * @return AttributeType or null
	 * @see org.tinyradius.dictionary.Dictionary#getAttributeTypeByName(java.lang.String)
	 */
	public AttributeType getAttributeTypeByName(String typeName) {
		org.tinyradius.dictionary.AttributeType attrib=stkObj.getAttributeTypeByName(typeName); 
		if(attrib!=null)
			return new AttributeType(attrib);
		else 
			return null;
	}
	
	/**
	 * Searches the vendor with the given name and returns its
	 * code. This method is seldomly used.
	 * @param vendorName vendor name
	 * @return vendor code or -1
	 * @see org.tinyradius.dictionary.Dictionary#getVendorId(java.lang.String)
	 */
	public int getVendorId(String vendorName) {
		return stkObj.getVendorId(vendorName);
	}
	
	/**
	 * Retrieves the name of the vendor with the given code from
	 * the cache.
	 * @param vendorId vendor number
	 * @return vendor name or null
	 * @see org.tinyradius.dictionary.Dictionary#getVendorName(int)
	 */
	public String getVendorName(int vendorId) {
		return stkObj.getVendorName(vendorId);
	}
	
	/**
	 * Adds the given vendor to the cache.
	 * @param vendorId vendor ID
	 * @param vendorName name of the vendor
	 * @exception IllegalArgumentException empty vendor name, invalid vendor ID
	 */
	public void addVendor(int vendorId, String vendorName) {
		stkObj.addVendor(vendorId, vendorName);
	}
	
	/**
	 * Adds an AttributeType object to the cache.
	 * @param attributeType AttributeType object
	 * @exception IllegalArgumentException duplicate attribute name/type code
	 */
	public void addAttributeType(AttributeType attributeType) {
		stkObj.addAttributeType(attributeType.getStkObj());
	}
	
		
}
