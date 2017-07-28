package dto.marketing;

/**
 * Created by LSL on 2016/7/4.
 */
public class ActivityDTO {

    private Integer currPage;

    private Integer pageSize;

    private Integer startNum;

    private String actName;

    private Integer actState;

    private String startTime;

    private String endTime;

    private String creater;

    private String sidx;//jqgrid排序字段

    private String sord;//jqgird排序字段

    public Integer getCurrPage() {
        return currPage;
    }

    public void setCurrPage(Integer currPage) {
        this.currPage = currPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getStartNum() {
        return startNum != null ? startNum :
                currPage == null || pageSize == null ? 0 :
                (currPage - 1) * pageSize;
    }

    public void setStartNum(Integer startNum) {
        this.startNum = startNum;
    }

    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public Integer getActState() {
        return actState;
    }

    public void setActState(Integer actState) {
        this.actState = actState;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getSidx() {
        return sidx;
    }

    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    public String getSord() {
        return sord;
    }

    public void setSord(String sord) {
        this.sord = sord;
    }
}
