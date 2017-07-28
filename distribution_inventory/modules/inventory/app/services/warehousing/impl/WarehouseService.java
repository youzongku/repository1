package services.warehousing.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

import entity.warehousing.Warehouse;
import forms.warehousing.WarehouseForm;
import mapper.warehousing.WarehouseMapper;
import services.warehousing.IWarehouseService;
import util.warehousing.Page;

public class WarehouseService implements IWarehouseService {
	@Inject
	WarehouseMapper mapper;

	@Override
	public Map<String, Object> save(Warehouse warehouse) {
		boolean result = false;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (null != warehouse.getId() && warehouse.getId() != 0) {// 更新
			warehouse.setLastUpdated(new Date());
			result = mapper.updateByPrimaryKeySelective(warehouse) > 0;
		} else {// 新增
			result = mapper.insert(warehouse) > 0;
		}
		if (result) {
			resultMap.put("result", true);
		} else {
			resultMap.put("result", false);
			resultMap.put("msg", "保存失败");
		}
		return resultMap;
	}

	@Override
	public Page<Warehouse> query(WarehouseForm warehouse) {
		List<Warehouse> list = mapper.query(warehouse);
		int total = mapper.getTotal(warehouse);
		int pageNo = warehouse.getPageNo();
		int pageSize = warehouse.getPageSize();
		if(warehouse.getPageNo() == 0){
			pageNo = 1;
		}
		if(warehouse.getPageSize() == 0){
			pageSize = 1;
		}
		Page<Warehouse> page = new Page<Warehouse>(list,total,pageNo,pageSize);
		return page;
	}

	@Override
	public Map<String, Object> delete(Warehouse warehouse) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (mapper.deleteByPrimaryKey(warehouse.getId()) > 0) {
			resultMap.put("result", true);
		} else {
			resultMap.put("result", false);
			resultMap.put("msg", "删除失败");
		}
		return resultMap;
	}

}
