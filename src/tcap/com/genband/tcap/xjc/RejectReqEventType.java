//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.12.10 at 09:51:58 AM CST 
//


package com.genband.tcap.xjc;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.genband.tcap.xjc.ParametersType;
import com.genband.tcap.xjc.RejectReqEventType;


/**
 * <p>Java class for reject-req-event-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reject-req-event-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{}component-req-event-group"/>
 *         &lt;element name="parameters" type="{}parameters-type" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="dialogue-id" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="invoke-id" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="link-id" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="problem" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="PROBLEM_CODE_BADLY_STRUCTURED_COMPONENT"/>
 *             &lt;enumeration value="PROBLEM_CODE_MISTYPED_COMPONENT"/>
 *             &lt;enumeration value="PROBLEM_CODE_UNRECOGNISED_COMPONENT"/>
 *             &lt;enumeration value="PROBLEM_CODE_DUPLICATE_INVOKE_ID"/>
 *             &lt;enumeration value="PROBLEM_CODE_INITIATING_RELEASE"/>
 *             &lt;enumeration value="PROBLEM_CODE_LINKED_RESPONSE_UNEXPECTED"/>
 *             &lt;enumeration value="PROBLEM_CODE_MISTYPED_PARAMETER"/>
 *             &lt;enumeration value="PROBLEM_CODE_RESOURCE_LIMITATION"/>
 *             &lt;enumeration value="PROBLEM_CODE_UNEXPECTED_LINKED_OPERATION"/>
 *             &lt;enumeration value="PROBLEM_CODE_UNRECOGNIZED_INVOKE_ID"/>
 *             &lt;enumeration value="PROBLEM_CODE_UNRECOGNIZED_LINKED_ID"/>
 *             &lt;enumeration value="PROBLEM_CODE_UNRECOGNIZED_OPERATION"/>
 *             &lt;enumeration value="PROBLEM_CODE_RETURN_RESULT_UNEXPECTED"/>
 *             &lt;enumeration value="PROBLEM_CODE_RETURN_ERROR_UNEXPECTED"/>
 *             &lt;enumeration value="PROBLEM_CODE_UNRECOGNIZED_ERROR"/>
 *             &lt;enumeration value="PROBLEM_CODE_BADLY_STRUCTURED_TRANSACTION"/>
 *             &lt;enumeration value="PROBLEM_CODE_INCORRECT_TRANSACTION"/>
 *             &lt;enumeration value="PROBLEM_CODE_PERMISSION_TO_RELEASE"/>
 *             &lt;enumeration value="PROBLEM_CODE_RESOURCE_UNAVAILABLE"/>
 *             &lt;enumeration value="PROBLEM_CODE_UNASSIGNED_RESPONDING_ID"/>
 *             &lt;enumeration value="PROBLEM_CODE_UNRECOGNIZED_PACKAGE_TYPE"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="problem-type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="PROBLEM_TYPE_GENERAL"/>
 *             &lt;enumeration value="PROBLEM_TYPE_INVOKE"/>
 *             &lt;enumeration value="PROBLEM_TYPE_RETURN_RESULT"/>
 *             &lt;enumeration value="PROBLEM_TYPE_RETURN_ERROR"/>
 *             &lt;enumeration value="PROBLEM_TYPE_TRANSACTION"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="reject-type">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="REJECT_TYPE_USER"/>
 *             &lt;enumeration value="REJECT_TYPE_REMOTE"/>
 *             &lt;enumeration value="REJECT_TYPE_LOCAL"/>
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
@XmlType(name = "reject-req-event-type", propOrder = {
    "parameters"
})
public class RejectReqEventType extends RejectReqEventInterface {

    protected ParametersType parameters;
    @XmlAttribute(name = "dialogue-id")
    protected BigInteger dialogueId;
    @XmlAttribute(name = "invoke-id")
    protected BigInteger invokeId;
    @XmlAttribute(name = "link-id")
    protected BigInteger linkId;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String problem;
    @XmlAttribute(name = "problem-type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String problemType;
    @XmlAttribute(name = "reject-type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String rejectType;

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link ParametersType }
     *     
     */
    public ParametersType getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParametersType }
     *     
     */
    public void setParameters(ParametersType value) {
        this.parameters = value;
    }

    /**
     * Gets the value of the dialogueId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDialogueId() {
        return dialogueId;
    }

    /**
     * Sets the value of the dialogueId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDialogueId(BigInteger value) {
        this.dialogueId = value;
    }

    /**
     * Gets the value of the invokeId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getInvokeId() {
        return invokeId;
    }

    /**
     * Sets the value of the invokeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setInvokeId(BigInteger value) {
        this.invokeId = value;
    }

    /**
     * Gets the value of the linkId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLinkId() {
        return linkId;
    }

    /**
     * Sets the value of the linkId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLinkId(BigInteger value) {
        this.linkId = value;
    }

    /**
     * Gets the value of the problem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProblem() {
        return problem;
    }

    /**
     * Sets the value of the problem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProblem(String value) {
        this.problem = value;
    }

    /**
     * Gets the value of the problemType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProblemType() {
        return problemType;
    }

    /**
     * Sets the value of the problemType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProblemType(String value) {
        this.problemType = value;
    }

    /**
     * Gets the value of the rejectType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRejectType() {
        return rejectType;
    }

    /**
     * Sets the value of the rejectType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRejectType(String value) {
        this.rejectType = value;
    }

}
