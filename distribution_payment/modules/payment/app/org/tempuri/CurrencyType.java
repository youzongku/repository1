
package org.tempuri;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>CurrencyType的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <p>
 * <pre>
 * &lt;simpleType name="CurrencyType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Rmb"/>
 *     &lt;enumeration value="CNY"/>
 *     &lt;enumeration value="USD"/>
 *     &lt;enumeration value="GBP"/>
 *     &lt;enumeration value="HKD"/>
 *     &lt;enumeration value="SGD"/>
 *     &lt;enumeration value="JPY"/>
 *     &lt;enumeration value="CAD"/>
 *     &lt;enumeration value="AUD"/>
 *     &lt;enumeration value="EUR"/>
 *     &lt;enumeration value="CHF"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CurrencyType")
@XmlEnum
public enum CurrencyType {

    @XmlEnumValue("Rmb")
    RMB("Rmb"),
    CNY("CNY"),
    USD("USD"),
    GBP("GBP"),
    HKD("HKD"),
    SGD("SGD"),
    JPY("JPY"),
    CAD("CAD"),
    AUD("AUD"),
    EUR("EUR"),
    CHF("CHF");
    private final String value;

    CurrencyType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CurrencyType fromValue(String v) {
        for (CurrencyType c: CurrencyType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
