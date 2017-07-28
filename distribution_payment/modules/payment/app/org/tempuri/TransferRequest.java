
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>TransferRequest complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="TransferRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://tempuri.org/}RequestBase">
 *       &lt;sequence>
 *         &lt;element name="Version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="InterfaceType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="AppId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="ReqBody" type="{http://tempuri.org/}TransReqItem" minOccurs="0" form="qualified"/>
 *         &lt;element name="Ext" type="{http://tempuri.org/}ArrayOfKvp" minOccurs="0" form="qualified"/>
 *         &lt;element name="Summary" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *         &lt;element name="MachineName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransferRequest", propOrder = {
    "version",
    "interfaceType",
    "appId",
    "reqBody",
    "ext",
    "summary",
    "machineName"
})
public class TransferRequest
    extends RequestBase
{

    @XmlElement(name = "Version")
    protected String version;
    @XmlElement(name = "InterfaceType")
    protected String interfaceType;
    @XmlElement(name = "AppId")
    protected String appId;
    @XmlElement(name = "ReqBody")
    protected TransReqItem reqBody;
    @XmlElement(name = "Ext")
    protected ArrayOfKvp ext;
    @XmlElement(name = "Summary")
    protected String summary;
    @XmlElement(name = "MachineName")
    protected String machineName;

    /**
     * 获取version属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置version属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * 获取interfaceType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterfaceType() {
        return interfaceType;
    }

    /**
     * 设置interfaceType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterfaceType(String value) {
        this.interfaceType = value;
    }

    /**
     * 获取appId属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppId() {
        return appId;
    }

    /**
     * 设置appId属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppId(String value) {
        this.appId = value;
    }

    /**
     * 获取reqBody属性的值。
     * 
     * @return
     *     possible object is
     *     {@link TransReqItem }
     *     
     */
    public TransReqItem getReqBody() {
        return reqBody;
    }

    /**
     * 设置reqBody属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link TransReqItem }
     *     
     */
    public void setReqBody(TransReqItem value) {
        this.reqBody = value;
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
     * 获取summary属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSummary() {
        return summary;
    }

    /**
     * 设置summary属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSummary(String value) {
        this.summary = value;
    }

    /**
     * 获取machineName属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMachineName() {
        return machineName;
    }

    /**
     * 设置machineName属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMachineName(String value) {
        this.machineName = value;
    }

}
