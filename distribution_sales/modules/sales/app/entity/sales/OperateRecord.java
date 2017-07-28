package entity.sales;

import java.io.Serializable;
import java.util.Date;

import com.wordnik.swagger.annotations.ApiModel;

import services.base.utils.DateFormatUtils;

/**
 * 描述：销售订单管理操作记录实体类
 * 
 * @author hanfs
 *
 */
@SuppressWarnings("serial")
@ApiModel
public class OperateRecord implements Serializable {
	private Integer id;// 主键id
	private Integer orderId;// 销售发货订单id
	// 操作类型
	// 1：调拨，2：财务确认，3：通知发货，4：取消，5：发货，6:确认收货，
	// 7：修改价格，8：支付运费，9：取消通知发货,10:客服确认, 11erp关闭
	private Integer operateType;
	private String operateStr;
	private Date operateTime;// 操作时间
	private Integer result;// 操作结果（0：失败，1：成功）
	private String resultStr;
	private String comment;// 备注
	private String email;//操作用户邮箱

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public String getOperateTimeStr() {
		return DateFormatUtils.getStrFromYYYYMMDDHHMMSS(operateTime);
	}

	public String getOperateStr() {
		switch (operateType) {
		case 1:
			operateStr = "待调拨";
			break;
		case 2:
			operateStr = "财务确认";
			break;
		case 3:
			operateStr = "待发货";
			break;
		case 4:
			operateStr = "取消";
			break;
		case 5:
			operateStr = "发货";
			break;
		case 6:
			operateStr = "确认收货";
			break;
		case 7:
			operateStr = "修改价格";
			break;
		case 10:
			operateStr = "客服确认";
			break;
		case 11:
			operateStr = "erp关闭";
			break;
		default:
			break;
		}
		return operateStr;
	}

	public void setOperateStr(String operateStr) {
		this.operateStr = operateStr;
	}

	public String getResultStr() {
		switch (operateType) {
		case 1:
			if (result != null && result.equals(0)) {
				resultStr = "调拨成功";
			} else {
				resultStr = "调拨失败";
			}
			break;
		case 2:
			if (result != null && result.equals(1)) {
				resultStr = "通过";
			} else {
				resultStr = "不通过";
			}
			break;
		case 3:
			if (result != null && result.equals(0)) {
				resultStr = "通知发货失败";
			} else {
				resultStr = "通知发货成功";
			}
			break;
		case 4:
			if (result != null && result.equals(0)) {
				resultStr = "取消失败";
			} else {
				resultStr = "取消成功";
			}
			break;
		case 10:
			if (result != null && result.equals(1)) {
				resultStr = "通过订单";
			} else {
				resultStr = "关闭订单";
			}
		case 11:
			if (result != null && result.equals(1)) {
				resultStr = "通过";
			} else {
				resultStr = "不通过";
			}
		default:
			break;
		}
		return resultStr;
	}

	public void setResultStr(String resultStr) {
		this.resultStr = resultStr;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
