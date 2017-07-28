
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>RequestBase complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="RequestBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MerchantNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="SignType" type="{http://www.w3.org/2001/XMLSchema}int" form="qualified"/>
 *         &lt;element name="Mac" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="Remark1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="Remark2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestBase", propOrder = {
    "merchantNo",
    "signType",
    "mac",
    "remark1",
    "remark2"
})
@XmlSeeAlso({
    TransferRequest.class
})
public abstract class RequestBase {

    @XmlElement(name = "MerchantNo")
    protected String merchantNo;
    @XmlElement(name = "SignType")
    protected int signType;
    @XmlElement(name = "Mac")
    protected String mac;
    @XmlElement(name = "Remark1")
    protected String remark1;
    @XmlElement(name = "Remark2")
    protected String remark2;

    /**
     * 获取merchantNo属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMerchantNo() {
        return merchantNo;
    }

    /**
     * 设置merchantNo属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMerchantNo(String value) {
        this.merchantNo = value;
    }

    /**
     * 获取signType属性的值。
     * 
     */
    public int getSignType() {
        return signType;
    }

    /**
     * 设置signType属性的值。
     * 
     */
    public void setSignType(int value) {
        this.signType = value;
    }

    /**
     * 获取mac属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMac() {
        return mac;
    }

    /**
     * 设置mac属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMac(String value) {
        this.mac = value;
    }

    /**
     * 获取remark1属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemark1() {
        return remark1;
    }

    /**
     * 设置remark1属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemark1(String value) {
        this.remark1 = value;
    }

    /**
     * 获取remark2属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemark2() {
        return remark2;
    }

    /**
     * 设置remark2属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemark2(String value) {
        this.remark2 = value;
    }

}
