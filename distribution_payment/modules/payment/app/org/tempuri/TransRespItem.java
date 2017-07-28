
package org.tempuri;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>TransRespItem complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="TransRespItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SerialNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="Amount" type="{http://www.w3.org/2001/XMLSchema}decimal" form="qualified"/>
 *         &lt;element name="CurrencyType" type="{http://tempuri.org/}CurrencyType" form="qualified"/>
 *         &lt;element name="MerchantOrderId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="Payer" type="{http://tempuri.org/}Payer" minOccurs="0" form="qualified"/>
 *         &lt;element name="ToPayer" type="{http://tempuri.org/}Payer" minOccurs="0" form="qualified"/>
 *         &lt;element name="PayTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" form="qualified"/>
 *         &lt;element name="PayChannel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="Ext" type="{http://tempuri.org/}ArrayOfKvp" minOccurs="0" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransRespItem", propOrder = {
    "serialNo",
    "amount",
    "currencyType",
    "merchantOrderId",
    "payer",
    "toPayer",
    "payTime",
    "payChannel",
    "ext"
})
public class TransRespItem {

    @XmlElement(name = "SerialNo")
    protected String serialNo;
    @XmlElement(name = "Amount", required = true)
    protected BigDecimal amount;
    @XmlElement(name = "CurrencyType", required = true)
    protected CurrencyType currencyType;
    @XmlElement(name = "MerchantOrderId")
    protected String merchantOrderId;
    @XmlElement(name = "Payer")
    protected Payer payer;
    @XmlElement(name = "ToPayer")
    protected Payer toPayer;
    @XmlElement(name = "PayTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar payTime;
    @XmlElement(name = "PayChannel")
    protected String payChannel;
    @XmlElement(name = "Ext")
    protected ArrayOfKvp ext;

    /**
     * 获取serialNo属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * 设置serialNo属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerialNo(String value) {
        this.serialNo = value;
    }

    /**
     * 获取amount属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 设置amount属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAmount(BigDecimal value) {
        this.amount = value;
    }

    /**
     * 获取currencyType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    /**
     * 设置currencyType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setCurrencyType(CurrencyType value) {
        this.currencyType = value;
    }

    /**
     * 获取merchantOrderId属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    /**
     * 设置merchantOrderId属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMerchantOrderId(String value) {
        this.merchantOrderId = value;
    }

    /**
     * 获取payer属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Payer }
     *     
     */
    public Payer getPayer() {
        return payer;
    }

    /**
     * 设置payer属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Payer }
     *     
     */
    public void setPayer(Payer value) {
        this.payer = value;
    }

    /**
     * 获取toPayer属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Payer }
     *     
     */
    public Payer getToPayer() {
        return toPayer;
    }

    /**
     * 设置toPayer属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Payer }
     *     
     */
    public void setToPayer(Payer value) {
        this.toPayer = value;
    }

    /**
     * 获取payTime属性的值。
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPayTime() {
        return payTime;
    }

    /**
     * 设置payTime属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPayTime(XMLGregorianCalendar value) {
        this.payTime = value;
    }

    /**
     * 获取payChannel属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPayChannel() {
        return payChannel;
    }

    /**
     * 设置payChannel属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPayChannel(String value) {
        this.payChannel = value;
    }

    /**
     * 获取ext属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfKvp }
     *     
     */
    public ArrayOfKvp getExt() {
        return ext;
    }

    /**
     * 设置ext属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfKvp }
     *     
     */
    public void setExt(ArrayOfKvp value) {
        this.ext = value;
    }

}
