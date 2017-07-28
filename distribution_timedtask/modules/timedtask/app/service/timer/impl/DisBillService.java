package service.timer.impl;

import java.util.Map;

import mapper.timer.DisBillMapper;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import entity.timer.DataSyncPhp;
import entity.timer.DisBill;
import events.timer.PhpEvents;
import service.timer.IDisBillService;

public class DisBillService implements IDisBillService {

    @Inject
    private DisBillMapper billMapper;
    
    @Inject
    private EventBus ebus;

	@Override
	public int save(DisBill bill) {
		int line = billMapper.insertSelective(bill);
		if(line >0 ){
		   Map<String,Object> map = Maps.newHashMap();
		   map.put("id",bill.getId());
		   ebus.post(new PhpEvents("bill",map,DataSyncPhp.CRE));
		}
		return line;
	}

}
