
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>transferResponse complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="transferResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://tempuri.org/}ResponseBase">
 *       &lt;sequence>
 *         &lt;element name="MerchantId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="Ext" type="{http://tempuri.org/}ArrayOfKvp" minOccurs="0" form="qualified"/>
 *         &lt;element name="RespBody" type="{http://tempuri.org/}TransRespItem" minOccurs="0" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transferResponse", propOrder = {
    "merchantId",
    "ext",
    "respBody"
})
public class TransferResponse2
    extends ResponseBase
{

    @XmlElement(name = "MerchantId")
    protected String merchantId;
    @XmlElement(name = "Ext")
    protected ArrayOfKvp ext;
    @XmlElement(name = "RespBody")
    protected TransRespItem respBody;

    /**
     * 获取merchantId属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMerchantId() {
        return merchantId;
    }

    /**
     * 设置merchantId属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMerchantId(String value) {
        this.merchantId = value;
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

    /**
     * 获取respBody属性的值。
     * 
     * @return
     *     possible object is
     *     {@link TransRespItem }
     *     
     */
    public TransRespItem getRespBody() {
        return respBody;
    }

    /**
     * 设置respBody属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link TransRespItem }
     *     
     */
    public void setRespBody(TransRespItem value) {
        this.respBody = value;
    }

}
