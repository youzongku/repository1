package valueobjects.product;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LSL on 2016/7/4.
 */
public class Pager<T> implements Serializable {

    private static final long serialVersionUID = 281330564350260664L;

    private Boolean suc;

    private String msg;

    private Integer currPage;

    private Integer pageSize;

    private Integer startNum;

    private Integer totalCount;

    private Integer totalPage;

    private List<T> list;

    public Pager() {
    }

    public Pager(Boolean suc, String msg) {
        this.suc = suc;
        this.msg = msg;
    }

    public Boolean getSuc() {
        return suc;
    }

    public void setSuc(Boolean suc) {
        this.suc = suc;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCurrPage() {
        return currPage == null ? 0 : currPage;
    }

    public void setCurrPage(Integer currPage) {
        this.currPage = currPage;
    }

    public Integer getPageSize() {
        return pageSize == null ? 0 : pageSize;
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

    public Integer getTotalCount() {
        return totalCount == null ? 0 : totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getTotalPage() {
        return totalPage != null ? totalPage :
                totalCount == null || pageSize == null ? 0 :
                (totalCount % pageSize == 0) ? totalCount / pageSize :
                (totalCount / pageSize + 1);
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getList() {
        return list != null ? list : Lists.newArrayList();
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
