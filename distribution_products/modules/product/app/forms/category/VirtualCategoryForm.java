package forms.category;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class VirtualCategoryForm {
	private Integer vcId;//类目id
	private String name;//类目名称
	private Integer parentId;//父类目id
	private Integer level;//级别
	private Integer position;//排序序号
	private Boolean isShow;//是否显示
	private Boolean isNavi;//是否显示到导航
	private String linkUrl;//链接地址
	private Boolean isFloat;//是否悬浮
	private VirtualCategoryForm parentCate;//上级类目实体
	private List<VirtualCategoryForm> form;//下级类目列表
	private List<Integer> childIds = Lists.newArrayList();//下级所有类目
	
	public List<Integer> getChildIds() {
		if(this.form != null){
			List<List<Integer>> vcids =  Lists.transform(form, e->e.getChildIds());
			if(vcids.size() > 0){
				for(List<Integer> vcid:vcids){
					childIds.addAll(vcid);
				}
				childIds = Lists.newArrayList(Sets.newHashSet(childIds));
			}
		}
		return childIds;
	}
	public void setChildIds(List<Integer> childIds) {
		this.childIds = childIds;
	}
	public VirtualCategoryForm getParentCate() {
		return parentCate;
	}
	public void setParentCate(VirtualCategoryForm parentCate) {
		this.parentCate = parentCate;
	}
	
	public List<VirtualCategoryForm> getForm() {
		return form;
	}
	public void setForm(List<VirtualCategoryForm> form) {
		if(form != null){
			List<Integer> vcids =  Lists.transform(form, e->e.getVcId());
			childIds.addAll(Lists.newArrayList(vcids));
		}
		this.form = form;
	}
	public Integer getVcId() {
		return vcId;
	}
	public void setVcId(Integer vcId) {
		this.vcId = vcId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public Boolean getIsShow() {
		return isShow;
	}
	public void setIsShow(Boolean isShow) {
		this.isShow = isShow;
	}
	public Boolean getIsNavi() {
		return isNavi;
	}
	public void setIsNavi(Boolean isNavi) {
		this.isNavi = isNavi;
	}
	public String getLinkUrl() {
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	public Boolean getIsFloat() {
		return isFloat;
	}
	public void setIsFloat(Boolean isFloat) {
		this.isFloat = isFloat;
	}
	
}
