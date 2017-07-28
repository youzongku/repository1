
package org.tempuri;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>TransReqItem complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="TransReqItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Amount" type="{http://www.w3.org/2001/XMLSchema}decimal" form="qualified"/>
 *         &lt;element name="Currency" type="{http://tempuri.org/}CurrencyType" form="qualified"/>
 *         &lt;element name="NotifyUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="NotifyUrlType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="MerchantOrderId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="ProductNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="ProductDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="Payer" type="{http://tempuri.org/}Payer" minOccurs="0" form="qualified"/>
 *         &lt;element name="ToPayer" type="{http://tempuri.org/}Payer" minOccurs="0" form="qualified"/>
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
@XmlType(name = "TransReqItem", propOrder = {
    "amount",
    "currency",
    "notifyUrl",
    "notifyUrlType",
    "merchantOrderId",
    "productNo",
    "productDesc",
    "payer",
    "toPayer",
    "ext"
})
public class TransReqItem {

    @XmlElement(name = "Amount", required = true)
    protected BigDecimal amount;
    @XmlElement(name = "Currency", required = true)
    protected CurrencyType currency;
    @XmlElement(name = "NotifyUrl")
    protected String notifyUrl;
    @XmlElement(name = "NotifyUrlType")
    protected String notifyUrlType;
    @XmlElement(name = "MerchantOrderId")
    protected String merchantOrderId;
    @XmlElement(name = "ProductNo")
    protected String productNo;
    @XmlElement(name = "ProductDesc")
    protected String productDesc;
    @XmlElement(name = "Payer")
    protected Payer payer;
    @XmlElement(name = "ToPayer")
    protected Payer toPayer;
    @XmlElement(name = "Ext")
    protected ArrayOfKvp ext;

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
     * 获取currency属性的值。
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getCurrency() {
        return currency;
    }

    /**
     * 设置currency属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setCurrency(CurrencyType value) {
        this.currency = value;
    }

    /**
     * 获取notifyUrl属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotifyUrl() {
        return notifyUrl;
    }

    /**
     * 设置notifyUrl属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotifyUrl(String value) {
        this.notifyUrl = value;
    }

    /**
     * 获取notifyUrlType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotifyUrlType() {
        return notifyUrlType;
    }

    /**
     * 设置notifyUrlType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotifyUrlType(String value) {
        this.notifyUrlType = value;
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
     * 获取productNo属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductNo() {
        return productNo;
    }

    /**
     * 设置productNo属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductNo(String value) {
        this.productNo = value;
    }

    /**
     * 获取productDesc属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductDesc() {
        return productDesc;
    }

    /**
     * 设置productDesc属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductDesc(String value) {
        this.productDesc = value;
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
