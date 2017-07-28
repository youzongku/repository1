package entity.timer;

import java.io.Serializable;
import java.util.Date;

import services.base.utils.DateFormatUtils;

/**
 * 账期操作日志
 * @author zbc
 * 2017年2月27日 上午11:21:45
 */
public class ApOptRecord  implements Serializable{

	private static final long serialVersionUID = -4001841785094189646L;
	//定义操作类型常量
	public static final int 
			CREATE = 0,//新增
			UPDATE = 1,//修改
			FORBIDDEN = 2,//禁用
			START_USING = 3,//启用
			GENERATED_BILLS = 4,//生成账单
			VERIFICATION = 5,//核销
			OPEN_THE_NEXT_ISSUE = 6;//开启下一期
	
	public ApOptRecord(){
	}
	
	/**
	 * @param operator      操作人
	 * @param operateType   操作类型
	 * @param operateDesc   操作描述
	 * @param slaveId       子账期id
	 * @param masterId      账期id
	 */
	public ApOptRecord(String operator,Integer operateType, String operateDesc,
			Integer slaveId, Integer masterId) {
		super();
		this.operator = operator;
		this.operateType = operateType;
		this.operateDesc = operateDesc;
		this.slaveId = slaveId;
		this.masterId = masterId;
	}

	/**
     * 主键
     */
    private Integer id;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 操作类型
     * （0：新增 1：修改 2：禁用 3：启用 4：生成账单  5：核销 6：开启下一期）
     */
    private Integer operateType;

    /**
     * 操作描述
     */
    private String operateDesc;

    /**
     * 账期明细id
     */
    private Integer slaveId;

    /**
     * 账期id
     */
    private Integer masterId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getOperateDesc() {
        return operateDesc;
    }

    public void setOperateDesc(String operateDesc) {
        this.operateDesc = operateDesc;
    }

    public Integer getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(Integer slaveId) {
        this.slaveId = slaveId;
    }

    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }
    
    public String getOperateTimeStr(){
    	return DateFormatUtils.getStrFromYYYYMMDDHHMMSS(operateTime);
    }
}