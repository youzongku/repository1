package entity.dismember;

import java.io.Serializable;

/**
 * TODO 暂时没用到
 * 账期金额变化与账单 关系表
 * @author zbc
 * 2017年2月17日 下午2:08:25
 */
@Deprecated
public class ApChangeMapping implements Serializable{

	private static final long serialVersionUID = -8898494362548892675L;

	/**
     * 主键
     */
    private Integer id;

    /**
     * 账单id
     */
    private Integer billId;

    /**
     * 金额变化id
     */
    private Integer changeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    public Integer getChangeId() {
        return changeId;
    }

    public void setChangeId(Integer changeId) {
        this.changeId = changeId;
    }
}