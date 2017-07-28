package entity.timer;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据同步PHP日志实体
 * @author zbc
 * 2017年2月10日 下午6:01:13
 */
public class DataSyncPhp implements Serializable{

	private static final long serialVersionUID = 907588375296524660L;
	//操作类型常量
	public final static String CRE = "create";
	public final static String DEL = "delelte";
	public final static String UPD = "update";
	/**
	 * 主键 uuid 串
	 */
	private String uid;

    /**
     * key 标识操作涉及的内容
     */
    private String key;

    /**
     * 操作类型 create,update,delete
     */
    private String optType;

    /**
     * 操作时间
     */
    private Date optTime;

    /**
     * 判断唯一性的json串
     * {"sku":"IF639","warehouseId":2024}
     */
    private String content;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOptType() {
        return optType;
    }

    public void setOptType(String optType) {
        this.optType = optType;
    }

    public Date getOptTime() {
        return optTime;
    }

    public void setOptTime(Date optTime) {
        this.optTime = optTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}