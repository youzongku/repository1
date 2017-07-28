package dto.category;

import java.util.List;

import dto.product.ProductLite;
import entity.category.VirtualCategory;

/**
 * 虚拟类目dto
 * 
 * @author ye_ziran
 * @since 2017年3月28日 下午4:55:45
 */
public class VirCategoryDto extends VirtualCategory{
	
	private String virCategoryIdTree;
	private Integer bannerId;
	private String bannerLink;
	private String bannerBgcolor;
	private List<VirCategoryDto> subList;
	private List<ProductLite> prodList;
	
	public String getVirCategoryIdTree() {
		return virCategoryIdTree;
	}
	public void setVirCategoryIdTree(String virCategoryIdTree) {
		this.virCategoryIdTree = virCategoryIdTree;
	}
	public String getBannerLink() {
		return bannerLink;
	}
	public void setBannerLink(String bannerLink) {
		this.bannerLink = bannerLink;
	}
	public List<VirCategoryDto> getSubList() {
		return subList;
	}
	public void setSubList(List<VirCategoryDto> subList) {
		this.subList = subList;
	}
	public Integer getBannerId() {
		return bannerId;
	}
	public void setBannerId(Integer bannerId) {
		this.bannerId = bannerId;
	}
	public String getBannerBgcolor() {
		return bannerBgcolor;
	}
	public void setBannerBgcolor(String bannerBgcolor) {
		this.bannerBgcolor = bannerBgcolor;
	}
	public List<ProductLite> getProdList() {
		return prodList;
	}
	public void setProdList(List<ProductLite> prodList) {
		this.prodList = prodList;
	}
	
}
