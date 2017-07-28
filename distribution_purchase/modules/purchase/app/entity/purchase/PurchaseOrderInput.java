package entity.purchase;

/**
 * 录入单实体
 * @author zbc
 * 2016年8月30日 下午4:21:57
 */
public class PurchaseOrderInput {
	public static final int INPUT_TYPE_TYPE_IN = 1;
	
	public static final int INPUT_TYPE_IMPORT = 2;
	
    private Integer id;

    private String inputUser;//录入人

    private String disAccount;//分销商账号
    
    private Integer inputType;//录入类型 1为手动录入 2 为导入
    
    private Integer disMode;//分销模式(1.电商 2.经销商3.KA直营4.进口专营)
    
    private Integer disType;//分销商类型（1：普通 2：合营 3：内部）
    
    public Integer getDisType() {
		return disType;
	}

	public void setDisType(Integer disType) {
		this.disType = disType;
	}

	public Integer getDisMode() {
		return disMode;
	}

	public void setDisMode(Integer disMode) {
		this.disMode = disMode;
	}

	public Integer getInputType() {
		return inputType;
	}

	public void setInputType(Integer inputType) {
		this.inputType = inputType;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInputUser() {
        return inputUser;
    }

    public void setInputUser(String inputUser) {
        this.inputUser = inputUser;
    }

    public String getDisAccount() {
        return disAccount;
    }

    public void setDisAccount(String disAccount) {
        this.disAccount = disAccount;
    }
}