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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.genband.tcap.xjc.NstateReqEventType;
import com.genband.tcap.xjc.SccpUserAddressType;


/**
 * <p>Java class for nstate-req-event-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="nstate-req-event-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{}state-req-event-group"/>
 *         &lt;element name="affected-user" type="{}sccp-user-address-type"/>
 *       &lt;/sequence>
 *       &lt;attribute name="user-status" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="USER_OUT_OF_SERVICE"/>
 *             &lt;enumeration value="USER_IN_SERVICE"/>
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
@XmlType(name = "nstate-req-event-type", propOrder = {
    "affectedUser"
})
public class NstateReqEventType extends NstateReqEventInterface {

    @XmlElement(name = "affected-user")
    protected SccpUserAddressType affectedUser;
    @XmlAttribute(name = "user-status", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String userStatus;

    /**
     * Gets the value of the affectedUser property.
     * 
     * @return
     *     possible object is
     *     {@link SubSystemAddressType }
     *     
     */
    public SccpUserAddressType getAffectedUser() {
        return affectedUser;
    }

    /**
     * Sets the value of the affectedUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link SccpUserAddressType }
     *     
     */
    public void setAffectedUser(SccpUserAddressType value) {
        this.affectedUser = value;
    }

    /**
     * Gets the value of the userStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserStatus() {
        return userStatus;
    }

    /**
     * Sets the value of the userStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserStatus(String value) {
        this.userStatus = value;
    }

}
