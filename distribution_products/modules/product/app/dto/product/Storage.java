package dto.product;

import java.io.Serializable;

public class Storage implements Serializable {
	/**
	 * 仓库dto
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Integer iid;

	String cstoragename;

	Integer ioverseas;

	String ccreateuser;

	public Integer getIid() {
		return iid;
	}

	public void setIid(Integer iid) {
		this.iid = iid;
	}

	public String getCstoragename() {
		return cstoragename;
	}

	public void setCstoragename(String cstoragename) {
		this.cstoragename = cstoragename;
	}

	public Integer getIoverseas() {
		return ioverseas;
	}

	public void setIoverseas(Integer ioverseas) {
		this.ioverseas = ioverseas;
	}

	public String getCcreateuser() {
		return ccreateuser;
	}

	public void setCcreateuser(String ccreateuser) {
		this.ccreateuser = ccreateuser;
	}

}