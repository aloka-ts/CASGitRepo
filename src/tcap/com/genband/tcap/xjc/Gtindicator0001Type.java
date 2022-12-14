//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.12.10 at 09:51:58 AM CST 
//


package com.genband.tcap.xjc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.genband.tcap.xjc.Gtindicator0001Type;


/**
 * <p>Java class for gtindicator0001-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gtindicator0001-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{}global-title-group"/>
 *       &lt;/sequence>
 *       &lt;attribute name="encoding-scheme">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="ES_UNKNOWN"/>
 *             &lt;enumeration value="ES_ODD"/>
 *             &lt;enumeration value="ES_EVEN"/>
 *             &lt;enumeration value="ES_NATIONAL_SPECIFIC"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="nature-of-addr-ind">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="NA_UNKNOWN"/>
 *             &lt;enumeration value="NA_SUBSCRIBER"/>
 *             &lt;enumeration value="NA_RESERVED"/>
 *             &lt;enumeration value="NA_NATIONAL_SIGNIFICANT"/>
 *             &lt;enumeration value="NA_INTERNATIONAL"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="numbering-plan">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="NP_UNKNOWN"/>
 *             &lt;enumeration value="NP_ISDN_TEL"/>
 *             &lt;enumeration value="NP_GENERIC"/>
 *             &lt;enumeration value="NP_DATA"/>
 *             &lt;enumeration value="NP_TELEX"/>
 *             &lt;enumeration value="NP_MARITIME_MOBILE"/>
 *             &lt;enumeration value="NP_LAND_MOBILE"/>
 *             &lt;enumeration value="NP_ISDN_MOBILE"/>
 *             &lt;enumeration value="NP_NETWORK"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="translation-type" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gtindicator0001-type", propOrder = {
    "addressInformation"
})
public class Gtindicator0001Type extends Gtindicator0001Interface {

    @XmlElement(name = "address-information", type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    protected byte[] addressInformation;
    @XmlAttribute(name = "encoding-scheme")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String encodingScheme;
    @XmlAttribute(name = "nature-of-addr-ind")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String natureOfAddrInd;
    @XmlAttribute(name = "numbering-plan")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String numberingPlan;
    @XmlAttribute(name = "translation-type")
    protected Byte translationType;

    /**
     * Gets the value of the addressInformation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public byte[] getAddressInformation() {
        return addressInformation;
    }

    /**
     * Sets the value of the addressInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressInformation(byte[] value) {
        this.addressInformation = ((byte[]) value);
    }

    /**
     * Gets the value of the encodingScheme property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncodingScheme() {
        return encodingScheme;
    }

    /**
     * Sets the value of the encodingScheme property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncodingScheme(String value) {
        this.encodingScheme = value;
    }

    /**
     * Gets the value of the natureOfAddrInd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNatureOfAddrInd() {
        return natureOfAddrInd;
    }

    /**
     * Sets the value of the natureOfAddrInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNatureOfAddrInd(String value) {
        this.natureOfAddrInd = value;
    }

    /**
     * Gets the value of the numberingPlan property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberingPlan() {
        return numberingPlan;
    }

    /**
     * Sets the value of the numberingPlan property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberingPlan(String value) {
        this.numberingPlan = value;
    }

    /**
     * Gets the value of the translationType property.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Byte getTranslationType() {
        return translationType;
    }

    /**
     * Sets the value of the translationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setTranslationType(Byte value) {
        this.translationType = value;
    }

}
