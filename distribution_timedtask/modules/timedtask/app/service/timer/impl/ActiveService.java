package service.timer.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import mapper.timer.DisActiveMapper;
import mapper.timer.DisCouponsMapper;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import entity.timer.DisActive;
import play.Logger;
import service.timer.IActiveService;

public class ActiveService implements IActiveService {
	
	@Inject
	private DisActiveMapper activeMapper;
	@Inject
	private DisCouponsMapper couponsMapper;
	
	@Override
	public void execute() {
		Date now = new Date();
		Map<String, Object> param = Maps.newHashMap();
		param.put("nowDate", now);
		param.put("status",2);
		List<DisActive> actives = activeMapper.queryInnerTimeActive(param);
		DisActive active = null;
		int rows = 0;
		if(actives != null && actives.size() > 0) {
			for (DisActive disActive : actives) {
				Logger.info("更新正常活动状态：" + disActive.getCouponsName());
				active = new DisActive();
				active.setId(disActive.getId());
				active.setStatus(0);
				activeMapper.updateByPrimaryKeySelective(active);
				rows = couponsMapper.updateState(active);
				Logger.info("更新正常优惠码状态：" + rows);
			}
		}
		param.put("status",0);//
		List<DisActive> actives2 = activeMapper.queryOutTimeActive(param);
		if(actives2 != null && actives2.size() > 0) {
			for (DisActive disActive : actives2) {
				Logger.info("更新过期活动状态：" + disActive.getCouponsName());
				active = new DisActive();
				active.setId(disActive.getId());
				active.setStatus(1);
				activeMapper.updateByPrimaryKeySelective(active);
				active.setStatus(4);
				rows = couponsMapper.updateState(active);
				Logger.info("更新过期优惠码状态：" + rows);
			}
		}
	}
}
