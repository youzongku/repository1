package service.timer.impl;

import mapper.timer.ContractQuotationsMapper;

import com.google.inject.Inject;

import play.Logger;
import service.timer.IQuotedService;

public class QuotedServiceImpl implements IQuotedService {

	@Inject
	private ContractQuotationsMapper quotationsMapper;

	@Override
	public void autoOpenNotStartQuoted() {
		Integer line = quotationsMapper.updateNotStartQuoted();
		Integer line2 = quotationsMapper.updateEndedQuoted();
		Logger.info("自动开启报价：" + line + " 结束报价:" + line2);
	}
	
	

}
