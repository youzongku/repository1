package service.timer;

import entity.timer.SaleMain;

public interface ISaleService {

	/**
	 * 订单自动客服确认，支付后1小时
	 */
	public void autoCsConfirm();
	
	/**
	 * 系统自动更新已货订单 状态
	 */
	public void autoConfirmReceipt();
	
	/**
	 * 异步记录日志，共php同步
	 * @param main
	 * @param type
	 */
	public void syncLogs(SaleMain main,String type);
}
