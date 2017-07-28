package entity.dismember;

import java.io.Serializable;

/**
 * 头像图片实体
 * luwj
 */
public class HeadImg implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer iid;

    private String cpath;

    private String ccontenttype;

    private String cmd5;

    private byte[] bcontent;

    public Integer getIid() {
        return iid;
    }

    public void setIid(Integer iid) {
        this.iid = iid;
    }

    public String getCpath() {
        return cpath;
    }

    public void setCpath(String cpath) {
        this.cpath = cpath;
    }

    public String getCcontenttype() {
        return ccontenttype;
    }

    public void setCcontenttype(String ccontenttype) {
        this.ccontenttype = ccontenttype;
    }

    public String getCmd5() {
        return cmd5;
    }

    public void setCmd5(String cmd5) {
        this.cmd5 = cmd5;
    }

    public byte[] getBcontent() {
        return bcontent;
    }

    public void setBcontent(byte[] bcontent) {
        this.bcontent = bcontent;
    }
}