package utils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LSL on 2015/12/25.
 */
public class Page<T> implements Serializable {

    private static final long serialVersionUID = -5034218344364248529L;

    private Integer currPage;//当前页，默认从1开始

    private Integer pageSize;//每页记录条数

    private Integer rows;//总记录条数

    private Integer totalPage;//总页数
    
    private boolean hasnext;//是否有下一页

    private List<T> data_list;//当前页数据

    public boolean isHasnext() {
		return hasnext;
	}

	public void setHasnext(boolean hasnext) {
		this.hasnext = hasnext;
	}

	/**
     * 此构造器会自动计算出totalPage属性值
     * @param currPage
     * @param pageSize
     * @param rows
     * @param list
     */
    public Page(Integer currPage, Integer pageSize, Integer rows, List<T> data_list) {
        this.currPage = currPage;
        this.pageSize = pageSize;
        this.rows = rows;
        this.totalPage = (rows != null && pageSize != null) ? (rows / pageSize + (rows % pageSize == 0 ? 0 : 1)) : 0;
        this.data_list = data_list;
        this.hasnext = (rows != null && pageSize != null)?(currPage*pageSize < rows):false;
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
    
	public static long getSerialversionuid() {
		return serialVersionUID;
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

    public List<T> getData_list() {
        return data_list;
    }

    public void setData_list(List<T> data_list) {
        this.data_list = data_list;
    }
}
