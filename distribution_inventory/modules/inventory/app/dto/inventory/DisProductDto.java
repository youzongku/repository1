package dto.inventory;

public class DisProductDto {

	private String pname;// 产品名
	private String wid;// 所属微仓
	private String wname;// 微仓名字
	private String disinventoryid;// 微仓信息表dis_inventory的id,
	private String stock;// 对应库存

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public String getDisinventoryid() {
		return disinventoryid;
	}

	public void setDisinventoryid(String disinventoryid) {
		this.disinventoryid = disinventoryid;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getWid() {
		return wid;
	}

	public void setWid(String wid) {
		this.wid = wid;
	}

	public String getWname() {
		return wname;
	}

	public void setWname(String wname) {
		this.wname = wname;
	}

}
