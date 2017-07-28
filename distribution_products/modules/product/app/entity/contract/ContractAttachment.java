package entity.contract;

import java.util.Date;

/**
 * 合同附件表
 * @author Administrator
 *
 */
public class ContractAttachment {
	
    private Integer id;

    private String contractNo;//合同号

    private String fileName;//文件名称

    private String fileType;//文件后缀

    private String filePath;//文件路径

    private Integer status;//状态

    private Date createTime;//创建时间

    private String mdValue;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMdValue() {
        return mdValue;
    }

    public void setMdValue(String mdValue) {
        this.mdValue = mdValue;
    }
}