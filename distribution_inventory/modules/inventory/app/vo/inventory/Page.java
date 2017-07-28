package vo.inventory;

import java.io.Serializable;
import java.util.List;

/**
 * 用于存储分页数据的实体模型
 * Created by LSL on 2015/12/3.
 */
public class Page<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer currPage;//当前页，默认从1开始

    private Integer pageSize;//每页记录条数

    private Integer rows;//总记录条数

    private Integer totalPage;//总页数

    private List<T> data;//当前页数据

    /**
     * 此构造器会自动计算出totalPage属性值
     * @param currPage
     * @param pageSize
     * @param rows
     * @param data
     */
    public Page(Integer currPage, Integer pageSize, Integer rows, List<T> data) {
        this.currPage = currPage;
        this.pageSize = pageSize;
        this.rows = rows;
        this.totalPage = rows / pageSize + (rows % pageSize == 0 ? 0 : 1);
        this.data = data;
    }

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

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    /**
     * 若要获取totalPage属性值，请先设置pageSize属性值和rows属性值
     * 若使用了此方法所属类的带参构造器则无需顾虑上述优先条件
     * @return
     */
    public Integer getTotalPage() {
        totalPage = rows / pageSize + (rows % pageSize == 0 ? 0 : 1);
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
