package dto.category;

import java.util.List;

import dto.SearchParamBaseDto;

/**
 * 类目相关查询dto
 * 
 * @author ye_ziran
 * @since 2015年12月8日 下午4:30:52
 */
public class CategorySearchParamDto extends SearchParamBaseDto{
	
	private List<Integer> catIds;//类目id
	private Integer parentId;//父类目id
	private Integer level;//类目级别
	private Integer istatus;//商品状态
	private String title;//sku或者商品title
	private String name; //虚拟类目名称
	
	private Integer pageSize;//分页参数
	private Integer currPage;
	
	private Integer model;

	public Integer getModel() {
		return model;
	}
	public void setModel(Integer model) {
		this.model = model;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getCurrPage() {
		return currPage;
	}
	public void setCurrPage(Integer currPage) {
		this.currPage = currPage;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getIstatus() {
		return istatus;
	}
	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public List<Integer> getCatIds() {
		return catIds;
	}
	public void setCatIds(List<Integer> catIds) {
		this.catIds = catIds;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
}
