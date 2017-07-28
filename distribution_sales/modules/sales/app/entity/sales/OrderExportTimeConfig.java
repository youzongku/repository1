package entity.sales;

import java.util.Date;

/**
 * 订单导出控制
 * @author zbc
 * 2017年6月26日 下午3:04:02
 */
public class OrderExportTimeConfig {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 是否同步中
     */
    private Boolean isSync;

    /**
     * 文件目录
     */
    private String path;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 导出类型
     */
    private String exportType;
    
    public OrderExportTimeConfig(){
    	
    }
    public OrderExportTimeConfig(String operator, String exportType) {
		super();
		this.operator = operator;
		this.exportType = exportType;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Boolean getIsSync() {
        return isSync;
    }

    public void setIsSync(Boolean isSync) {
        this.isSync = isSync;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }
}