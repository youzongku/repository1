package entity.sales;

import java.util.Date;

public class PlatformConfig {
    private Integer id;

    private String platformCode;

    private String platformKey;

    private String platformValue;

    private Date createDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode == null ? null : platformCode.trim();
    }

    public String getPlatformKey() {
        return platformKey;
    }

    public void setPlatformKey(String platformKey) {
        this.platformKey = platformKey == null ? null : platformKey.trim();
    }

    public String getPlatformValue() {
        return platformValue;
    }

    public void setPlatformValue(String platformValue) {
        this.platformValue = platformValue == null ? null : platformValue.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}