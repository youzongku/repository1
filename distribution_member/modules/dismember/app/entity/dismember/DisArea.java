package entity.dismember;

import java.io.Serializable;

/**
 * 地区(区/县/县级市)表实体
 */
public class DisArea implements Serializable {

    private static final long serialVersionUID = 6894946380823139540L;

    private Integer id;

    private String areaName;

    private Integer cityId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }
}