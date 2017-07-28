package entity.sales;

import java.util.Date;

/**
 * 自动任务时间记录表:记录自动任务执行到的时间点
 */
public class TimerRecord {

    private Integer id;

    private String executeType;

    private Date executeDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExecuteType() {
        return executeType;
    }

    public void setExecuteType(String executeType) {
        this.executeType = executeType;
    }

    public Date getExecuteDate() {
        return executeDate;
    }

    public void setExecuteDate(Date executeDate) {
        this.executeDate = executeDate;
    }
}