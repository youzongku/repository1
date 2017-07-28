package entity.category;

/**
 * t_virtual_category实体
 * 
 * @author ye_ziran
 * @since 2015年12月8日 下午4:47:17
 */
public class VirtualCategory {
	
    private Integer id;
    private String name;
    private Integer parentid;
    private Integer level;
    private Integer position;
    private Boolean show;
    private Boolean navi;
    private String url;
    private Boolean floatshow;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
    public Integer getParentid() {
        return parentid;
    }
    public void setParentid(Integer parentid) {
        this.parentid = parentid;
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
    public Boolean getShow() {
        return show;
    }
    public void setShow(Boolean show) {
        this.show = show;
    }
    public Boolean getNavi() {
        return navi;
    }
    public void setNavi(Boolean navi) {
        this.navi = navi;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }
    public Boolean getFloatshow() {
        return floatshow;
    }
    public void setFloatshow(Boolean floatshow) {
        this.floatshow = floatshow;
    }
}