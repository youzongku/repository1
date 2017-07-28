package entity.product.store;


/**
 * SKU详情表实体
 */
public class Translate extends StoreBase{
	

	private static final long serialVersionUID = -6534969658489653488L;

	/**
     * 主键
     */
    private Integer id;

    /**
     * 实体id
     */
    private Integer entityId;

    /**
     * title
     */
    private String ctitle;

    /**
     * 描述
     */
    private String cdescription;

    /**
     * 短描述
     */
    private String cshortdescription;

    /**
     * 关键字
     */
    private String ckeyword;

    /**
     * meta title
     */
    private String cmetatitle;

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

    public String getCtitle() {
        return ctitle;
    }

    public void setCtitle(String ctitle) {
        this.ctitle = ctitle;
    }

    public String getCdescription() {
        return cdescription;
    }

    public void setCdescription(String cdescription) {
        this.cdescription = cdescription;
    }

    public String getCshortdescription() {
        return cshortdescription;
    }

    public void setCshortdescription(String cshortdescription) {
        this.cshortdescription = cshortdescription;
    }

    public String getCkeyword() {
        return ckeyword;
    }

    public void setCkeyword(String ckeyword) {
        this.ckeyword = ckeyword;
    }

    public String getCmetatitle() {
        return cmetatitle;
    }

    public void setCmetatitle(String cmetatitle) {
        this.cmetatitle = cmetatitle;
    }
}