package vo.dismember;

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

    private List<T> list;//当前页数据
    
    public Page() {
		super();
	}

	/**
     * 此构造器会自动计算出totalPage属性值
     * @param currPage
     * @param pageSize
     * @param rows
     * @param list
     */
    public Page(Integer currPage, Integer pageSize, Integer rows, List<T> list) {
        this.currPage = currPage;
        this.pageSize = pageSize;
        this.rows = rows;
        if(rows!=null && pageSize!=null){
        	this.totalPage = rows%pageSize==0 ? rows/pageSize : rows/pageSize + 1;	
        }
//        this.totalPage = (rows != null && pageSize != null) ? (rows / pageSize + (rows % pageSize == 0 ? 0 : 1)) : 0;
        this.list = list;
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
    	if(rows != null && null != pageSize)
        totalPage = rows / pageSize + (rows % pageSize == 0 ? 0 : 1);
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

	@Override
	public String toString() {
		return "Page [currPage=" + currPage + ", pageSize=" + pageSize
				+ ", rows=" + rows + ", totalPage=" + totalPage + ", list="
				+ list + "]";
	}
    
}
