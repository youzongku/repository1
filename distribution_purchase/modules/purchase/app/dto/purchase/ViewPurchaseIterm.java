package dto.purchase;

import java.util.List;

/**
 * Created by luwj on 2015/12/8.
 */
public class ViewPurchaseIterm {

    private ReturnMess returnMess;

    private String token;//用户信息

    private int total;//总条目

    private int pages;//总页数
    
    //add by ye_ziran 2016-04-06
    private int page;//页码，当前页数
    private int recordPerPage;//页长，每页显示数量

    private List<ViewPurchaseOrder> orders;

    public ReturnMess getReturnMess() {
        return returnMess;
    }

    public void setReturnMess(ReturnMess returnMess) {
        this.returnMess = returnMess;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<ViewPurchaseOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<ViewPurchaseOrder> orders) {
        this.orders = orders;
    }

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRecordPerPage() {
		return recordPerPage;
	}

	public void setRecordPerPage(int recordPerPage) {
		this.recordPerPage = recordPerPage;
	}
    
    
}
