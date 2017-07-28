
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Payer complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="Payer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MemberId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="MemberIdType" type="{http://tempuri.org/}AccountTypes" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Payer", propOrder = {
    "memberId",
    "memberIdType"
})
public class Payer {

    @XmlElement(name = "MemberId")
    protected String memberId;
    @XmlElement(name = "MemberIdType", required = true)
    protected AccountTypes memberIdType;

    /**
     * 获取memberId属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * 设置memberId属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMemberId(String value) {
        this.memberId = value;
    }

    /**
     * 获取memberIdType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link AccountTypes }
     *     
     */
    public AccountTypes getMemberIdType() {
        return memberIdType;
    }

    /**
     * 设置memberIdType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link AccountTypes }
     *     
     */
    public void setMemberIdType(AccountTypes value) {
        this.memberIdType = value;
    }

}
