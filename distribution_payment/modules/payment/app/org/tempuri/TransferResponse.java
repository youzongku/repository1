
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>TransferResponse complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="TransferResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransferResult" type="{http://tempuri.org/}transferResponse" minOccurs="0" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransferResponse", propOrder = {
    "transferResult"
})
public class TransferResponse {

    @XmlElement(name = "TransferResult")
    protected TransferResponse2 transferResult;

    /**
     * 获取transferResult属性的值。
     * 
     * @return
     *     possible object is
     *     {@link TransferResponse2 }
     *     
     */
    public TransferResponse2 getTransferResult() {
        return transferResult;
    }

    /**
     * 设置transferResult属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link TransferResponse2 }
     *     
     */
    public void setTransferResult(TransferResponse2 value) {
        this.transferResult = value;
    }

}
