package dto.dismember.result;

import java.util.List;

import dto.dismember.ResultDto;
import entity.dismember.AdminOperateRecord;

/**
 * @author zbc
 * 2017年4月28日 下午5:10:10
 */
public class AdminRecordResult<T> extends ResultDto<List<AdminOperateRecord>> {

	private static final long serialVersionUID = 9217015509142676315L;

	/**
	 * @param flag
	 * @param msg
	 */
	public AdminRecordResult(boolean flag, String msg) {
		super(flag, msg);
	}
	
	@Override
	public List<AdminOperateRecord> getObj() {
		return super.getObj();
	}
	
}
