//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.12.10 at 09:51:58 AM CST 
//


package com.genband.tcap.xjc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import com.genband.tcap.xjc.GlobalTitleType;
import com.genband.tcap.xjc.Gtindicator0001Type;
import com.genband.tcap.xjc.Gtindicator0010Type;
import com.genband.tcap.xjc.Gtindicator0011Type;
import com.genband.tcap.xjc.Gtindicator0100Type;


/**
 * <p>Java class for global-title-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="global-title-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="gtindicator0001" type="{}gtindicator0001-type"/>
 *         &lt;element name="gtindicator0100" type="{}gtindicator0100-type"/>
 *         &lt;element name="gtindicator0011" type="{}gtindicator0011-type"/>
 *         &lt;element name="gtindicator0010" type="{}gtindicator0010-type"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "global-title-type", propOrder = {
    "gtindicator0001",
    "gtindicator0100",
    "gtindicator0011",
    "gtindicator0010"
})
public class GlobalTitleType extends GlobalTitleInterface {

    protected Gtindicator0001Type gtindicator0001;
    protected Gtindicator0100Type gtindicator0100;
    protected Gtindicator0011Type gtindicator0011;
    protected Gtindicator0010Type gtindicator0010;

    /**
     * Gets the value of the gtindicator0001 property.
     * 
     * @return
     *     possible object is
     *     {@link Gtindicator0001Type }
     *     
     */
    public Gtindicator0001Type getGtindicator0001() {
        return gtindicator0001;
    }

    /**
     * Sets the value of the gtindicator0001 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gtindicator0001Type }
     *     
     */
    public void setGtindicator0001(Gtindicator0001Type value) {
        this.gtindicator0001 = value;
    }

    /**
     * Gets the value of the gtindicator0100 property.
     * 
     * @return
     *     possible object is
     *     {@link Gtindicator0100Type }
     *     
     */
    public Gtindicator0100Type getGtindicator0100() {
        return gtindicator0100;
    }

    /**
     * Sets the value of the gtindicator0100 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gtindicator0100Type }
     *     
     */
    public void setGtindicator0100(Gtindicator0100Type value) {
        this.gtindicator0100 = value;
    }

    /**
     * Gets the value of the gtindicator0011 property.
     * 
     * @return
     *     possible object is
     *     {@link Gtindicator0011Type }
     *     
     */
    public Gtindicator0011Type getGtindicator0011() {
        return gtindicator0011;
    }

    /**
     * Sets the value of the gtindicator0011 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gtindicator0011Type }
     *     
     */
    public void setGtindicator0011(Gtindicator0011Type value) {
        this.gtindicator0011 = value;
    }

    /**
     * Gets the value of the gtindicator0010 property.
     * 
     * @return
     *     possible object is
     *     {@link Gtindicator0010Type }
     *     
     */
    public Gtindicator0010Type getGtindicator0010() {
        return gtindicator0010;
    }

    /**
     * Sets the value of the gtindicator0010 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gtindicator0010Type }
     *     
     */
    public void setGtindicator0010(Gtindicator0010Type value) {
        this.gtindicator0010 = value;
    }

}
