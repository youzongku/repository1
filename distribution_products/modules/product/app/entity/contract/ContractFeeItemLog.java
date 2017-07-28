package entity.contract;

import java.util.Date;

public class ContractFeeItemLog {
	private Integer id;

	private Integer feeItemId;

	private String feeTypeName;

	private Integer feeType;

	private String contentOriginal;

	private String contentNew;
	
	private Integer optType;

	private String optUser;

	private Date optTime;
	
	/** 1添加预估费用 */
	public static final int OPT_TYPE_ADD_ESIMATED_FEE = 1;
	/** 2添加实际费用 */
	public static final int OPT_TYPE_ADD_REAL_FEE = 2;
	/** 3修改预估费用 */
	public static final int OPT_TYPE_UPDATE_ESIMATED_FEE = 3;
	/** 4修改实际费用 */
	public static final int OPT_TYPE_UPDATE_REAL_FEE = 4;
	/** 5添加费用率 */
	public static final int OPT_TYPE_ADD_FEE_RATE = 5;
	/** 6修改费用率 */
	public static final int OPT_TYPE_UPDATE_FEE_RATE = 6;
	/** 7提前结束 */
	public static final int OPT_TYPE_FINISH_AHEAD_OF_TIME = 7;

	public ContractFeeItemLog() {
	}

	public ContractFeeItemLog(Integer feeItemId, String feeTypeName, Integer feeType, String optUser) {
		super();
		this.feeItemId = feeItemId;
		this.feeTypeName = feeTypeName;
		this.feeType = feeType;
		this.optUser = optUser;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getFeeItemId() {
		return feeItemId;
	}

	public void setFeeItemId(Integer feeItemId) {
		this.feeItemId = feeItemId;
	}

	public String getFeeTypeName() {
		return feeTypeName;
	}

	public void setFeeTypeName(String feeTypeName) {
		this.feeTypeName = feeTypeName;
	}

	public Integer getFeeType() {
		return feeType;
	}

	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}

	public String getContentOriginal() {
		return contentOriginal;
	}

	public void setContentOriginal(String contentOriginal) {
		this.contentOriginal = contentOriginal;
	}

	public String getContentNew() {
		return contentNew;
	}

	public void setContentNew(String contentNew) {
		this.contentNew = contentNew;
	}

	public String getOptUser() {
		return optUser;
	}

	public void setOptUser(String optUser) {
		this.optUser = optUser;
	}

	public Date getOptTime() {
		return optTime;
	}

	public void setOptTime(Date optTime) {
		this.optTime = optTime;
	}

	public Integer getOptType() {
		return optType;
	}

	public void setOptType(Integer optType) {
		this.optType = optType;
	}

}