
package org.tempuri;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>AccountTypes的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <p>
 * <pre>
 * &lt;simpleType name="AccountTypes">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PtId"/>
 *     &lt;enumeration value="MerchantNo"/>
 *     &lt;enumeration value="AccountId"/>
 *     &lt;enumeration value="memberId"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AccountTypes")
@XmlEnum
public enum AccountTypes {

    @XmlEnumValue("PtId")
    PT_ID("PtId"),
    @XmlEnumValue("MerchantNo")
    MERCHANT_NO("MerchantNo"),
    @XmlEnumValue("AccountId")
    ACCOUNT_ID("AccountId"),
    @XmlEnumValue("memberId")
    MEMBER_ID("memberId");
    private final String value;

    AccountTypes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AccountTypes fromValue(String v) {
        for (AccountTypes c: AccountTypes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
