package entity.category;

/**
 * t_category_base实体
 * 
 * @author ye_ziran
 * @since 2015年12月8日 下午4:48:12
 */
public class CategoryBase {
    private Integer iid;
    private Integer iparentid;
    private String cpath;
    private Integer ilevel;
    private Integer iposition;
    private Integer ichildrencount;
    private String cname;

    public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public Integer getIid() {
        return iid;
    }
    public void setIid(Integer iid) {
        this.iid = iid;
    }
    public Integer getIparentid() {
        return iparentid;
    }
    public void setIparentid(Integer iparentid) {
        this.iparentid = iparentid;
    }
    public String getCpath() {
        return cpath;
    }
    public void setCpath(String cpath) {
        this.cpath = cpath == null ? null : cpath.trim();
    }
    public Integer getIlevel() {
        return ilevel;
    }
    public void setIlevel(Integer ilevel) {
        this.ilevel = ilevel;
    }
    public Integer getIposition() {
        return iposition;
    }
    public void setIposition(Integer iposition) {
        this.iposition = iposition;
    }
    public Integer getIchildrencount() {
        return ichildrencount;
    }
    public void setIchildrencount(Integer ichildrencount) {
        this.ichildrencount = ichildrencount;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cname == null) ? 0 : cname.hashCode());
		result = prime * result + ((ilevel == null) ? 0 : ilevel.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoryBase other = (CategoryBase) obj;
		if (cname == null) {
			if (other.cname != null)
				return false;
		} else if (!cname.equals(other.cname))
			return false;
		if (ilevel == null) {
			if (other.ilevel != null)
				return false;
		} else if (!ilevel.equals(other.ilevel))
			return false;
		return true;
	}
    
    
}