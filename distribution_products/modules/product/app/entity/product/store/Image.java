package entity.product.store;

/**
 * SKU图片表实体
 */
public class Image extends StoreBase {

	private static final long serialVersionUID = 456730589434245858L;

	/**
     * 主键
     */
    private Integer id;

    /**
     * 实体id
     */
    private Integer entityId;

    /**
     * 图片链接
     */
    private String imgurl;

    /**
     * 是否缩略图
     */
    private Boolean bthumbnail;

    /**
     * 排序
     */
    private Integer position;

    /**
     * 是否是小图
     */
    private Boolean bsmallimage;

    /**
     * 是否是主图
     */
    private Boolean bmainimage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public Boolean getBthumbnail() {
        return bthumbnail;
    }

    public void setBthumbnail(Boolean bthumbnail) {
        this.bthumbnail = bthumbnail;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Boolean getBsmallimage() {
        return bsmallimage;
    }

    public void setBsmallimage(Boolean bsmallimage) {
        this.bsmallimage = bsmallimage;
    }

    public Boolean getBmainimage() {
        return bmainimage;
    }

    public void setBmainimage(Boolean bmainimage) {
        this.bmainimage = bmainimage;
    }
}