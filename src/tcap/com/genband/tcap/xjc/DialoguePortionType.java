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
import com.genband.tcap.xjc.DialoguePortionType;


/**
 * <p>Java class for dialogue-portion-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dialogue-portion-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="app-context-name" type="{http://www.w3.org/2001/XMLSchema}hexBinary" minOccurs="0"/>
 *         &lt;element name="user-information" type="{http://www.w3.org/2001/XMLSchema}hexBinary" minOccurs="0"/>
 *         &lt;element name="security-context-information" type="{http://www.w3.org/2001/XMLSchema}hexBinary" minOccurs="0"/>
 *         &lt;element name="confidentiality-information" type="{http://www.w3.org/2001/XMLSchema}hexBinary" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="app-context-identifier">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="APPLICATION_CONTEXT_INTEGER"/>
 *             &lt;enumeration value="APPLICATION_CONTEXT_OBJECT"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="protocol-version">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="DP_PROTOCOL_VERSION_ANSI_96"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="security-context-identifier">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="SECURITY_CONTEXT_INTEGER"/>
 *             &lt;enumeration value="SECURITY_CONTEXT_OBJECT"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dialogue-portion-type", propOrder = {
    "appContextName",
    "userInformation",
    "securityContextInformation",
    "confidentialityInformation"
})
public class DialoguePortionType extends DialoguePortionInterface {

    @XmlElement(name = "app-context-name", type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    protected byte[] appContextName;
    @XmlElement(name = "user-information", type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    protected byte[] userInformation;
    @XmlElement(name = "security-context-information", type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    protected byte[] securityContextInformation;
    @XmlElement(name = "confidentiality-information", type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    protected byte[] confidentialityInformation;
    @XmlAttribute(name = "app-context-identifier")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String appContextIdentifier;
    @XmlAttribute(name = "protocol-version")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String protocolVersion;
    @XmlAttribute(name = "security-context-identifier")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String securityContextIdentifier;

    /**
     * Gets the value of the appContextName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public byte[] getAppContextName() {
        return appContextName;
    }

    /**
     * Sets the value of the appContextName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppContextName(byte[] value) {
        this.appContextName = ((byte[]) value);
    }

    /**
     * Gets the value of the userInformation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public byte[] getUserInformation() {
        return userInformation;
    }

    /**
     * Sets the value of the userInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserInformation(byte[] value) {
        this.userInformation = ((byte[]) value);
    }

    /**
     * Gets the value of the securityContextInformation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public byte[] getSecurityContextInformation() {
        return securityContextInformation;
    }

    /**
     * Sets the value of the securityContextInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecurityContextInformation(byte[] value) {
        this.securityContextInformation = ((byte[]) value);
    }

    /**
     * Gets the value of the confidentialityInformation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public byte[] getConfidentialityInformation() {
        return confidentialityInformation;
    }

    /**
     * Sets the value of the confidentialityInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfidentialityInformation(byte[] value) {
        this.confidentialityInformation = ((byte[]) value);
    }

    /**
     * Gets the value of the appContextIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppContextIdentifier() {
        return appContextIdentifier;
    }

    /**
     * Sets the value of the appContextIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppContextIdentifier(String value) {
        this.appContextIdentifier = value;
    }

    /**
     * Gets the value of the protocolVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Sets the value of the protocolVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtocolVersion(String value) {
        this.protocolVersion = value;
    }

    /**
     * Gets the value of the securityContextIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecurityContextIdentifier() {
        return securityContextIdentifier;
    }

    /**
     * Sets the value of the securityContextIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecurityContextIdentifier(String value) {
        this.securityContextIdentifier = value;
    }

}
