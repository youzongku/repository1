package entity.dismember;

import java.io.Serializable;

/**
 * 省市区实体类
 * @author Lzl
 *
 */
public class DisChinaArea implements Serializable {

	private static final long serialVersionUID = -8924162423026695726L;
	
	private Integer provinceId; //省id
	
	private Integer cityId; //市id
	
	private Integer areaId; //区id
	
	private String provinceName; //省名称
	
	private String cityName; //市名称
	
	private String areaName; //区名称
	
	private String zipCode; //邮政编码

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

}
