package entity.warehousing;

import java.util.Date;

/**
 * erp推送商品库存记录实体
 * 
 * @author luwj
 */
public class ErpPushInvenRecode implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String contents;// 内容

	private Date created;// 时间

	private Integer uniqueId;// ERP维护的库存推送唯一标识，标识相同视为同一批次的库存变化

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Integer getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(Integer uniqueId) {
		this.uniqueId = uniqueId;
	}

}