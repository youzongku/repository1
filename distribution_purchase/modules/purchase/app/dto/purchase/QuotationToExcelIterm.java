package dto.purchase;

import java.util.List;

/**
 * Created by luwj on 2016/3/7.
 */
public class QuotationToExcelIterm {

    private Integer pageSize;

    private Integer totalPage;

    private Integer currPage;

    private Integer rows;

    private List<QuotationToExcelDto> result;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getCurrPage() {
        return currPage;
    }

    public void setCurrPage(Integer currPage) {
        this.currPage = currPage;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public List<QuotationToExcelDto> getResult() {
        return result;
    }

    public void setResult(List<QuotationToExcelDto> result) {
        this.result = result;
    }
}
