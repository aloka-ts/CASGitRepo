//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.12.10 at 09:51:58 AM CST 
//


package com.genband.tcap.xjc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import com.genband.tcap.xjc.ComponentIndEventType;
import com.genband.tcap.xjc.ErrorIndEventType;
import com.genband.tcap.xjc.InvokeIndEventType;
import com.genband.tcap.xjc.LocalCancelIndEventType;
import com.genband.tcap.xjc.RejectIndEventType;
import com.genband.tcap.xjc.ResultIndEventType;


/**
 * <p>Java class for component-ind-event-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="component-ind-event-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="error-ind-event" type="{}error-ind-event-type"/>
 *         &lt;element name="reject-ind-event" type="{}reject-ind-event-type"/>
 *         &lt;element name="local-cancel-ind-event" type="{}local-cancel-ind-event-type"/>
 *         &lt;element name="invoke-ind-event" type="{}invoke-ind-event-type"/>
 *         &lt;element name="result-ind-event" type="{}result-ind-event-type"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "component-ind-event-type", propOrder = {
    "errorIndEvent",
    "rejectIndEvent",
    "localCancelIndEvent",
    "invokeIndEvent",
    "resultIndEvent"
})
public class ComponentIndEventType extends ComponentIndEventInterface {

    @XmlElement(name = "error-ind-event")
    protected ErrorIndEventType errorIndEvent;
    @XmlElement(name = "reject-ind-event")
    protected RejectIndEventType rejectIndEvent;
    @XmlElement(name = "local-cancel-ind-event")
    protected LocalCancelIndEventType localCancelIndEvent;
    @XmlElement(name = "invoke-ind-event")
    protected InvokeIndEventType invokeIndEvent;
    @XmlElement(name = "result-ind-event")
    protected ResultIndEventType resultIndEvent;

    /**
     * Gets the value of the errorIndEvent property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorIndEventType }
     *     
     */
    public ErrorIndEventType getErrorIndEvent() {
        return errorIndEvent;
    }

    /**
     * Sets the value of the errorIndEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorIndEventType }
     *     
     */
    public void setErrorIndEvent(ErrorIndEventType value) {
        this.errorIndEvent = value;
    }

    /**
     * Gets the value of the rejectIndEvent property.
     * 
     * @return
     *     possible object is
     *     {@link RejectIndEventType }
     *     
     */
    public RejectIndEventType getRejectIndEvent() {
        return rejectIndEvent;
    }

    /**
     * Sets the value of the rejectIndEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link RejectIndEventType }
     *     
     */
    public void setRejectIndEvent(RejectIndEventType value) {
        this.rejectIndEvent = value;
    }

    /**
     * Gets the value of the localCancelIndEvent property.
     * 
     * @return
     *     possible object is
     *     {@link LocalCancelIndEventType }
     *     
     */
    public LocalCancelIndEventType getLocalCancelIndEvent() {
        return localCancelIndEvent;
    }

    /**
     * Sets the value of the localCancelIndEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalCancelIndEventType }
     *     
     */
    public void setLocalCancelIndEvent(LocalCancelIndEventType value) {
        this.localCancelIndEvent = value;
    }

    /**
     * Gets the value of the invokeIndEvent property.
     * 
     * @return
     *     possible object is
     *     {@link InvokeIndEventType }
     *     
     */
    public InvokeIndEventType getInvokeIndEvent() {
        return invokeIndEvent;
    }

    /**
     * Sets the value of the invokeIndEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link InvokeIndEventType }
     *     
     */
    public void setInvokeIndEvent(InvokeIndEventType value) {
        this.invokeIndEvent = value;
    }

    /**
     * Gets the value of the resultIndEvent property.
     * 
     * @return
     *     possible object is
     *     {@link ResultIndEventType }
     *     
     */
    public ResultIndEventType getResultIndEvent() {
        return resultIndEvent;
    }

    /**
     * Sets the value of the resultIndEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultIndEventType }
     *     
     */
    public void setResultIndEvent(ResultIndEventType value) {
        this.resultIndEvent = value;
    }

}
