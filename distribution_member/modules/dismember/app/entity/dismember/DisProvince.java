package entity.dismember;

import java.io.Serializable;

public class DisProvince implements Serializable {

    private static final long serialVersionUID = -1624478210792220960L;

    private Integer id;

    private String provinceName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}