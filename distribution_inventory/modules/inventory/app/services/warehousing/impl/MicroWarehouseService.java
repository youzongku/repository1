package services.warehousing.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

import dto.warehousing.MicroRealWarehouseDto;
import dto.warehousing.MicroWarehouseDto;
import entity.warehousing.MicroWarehouse;
import forms.warehousing.MicroWarehouseForm;
import mapper.warehousing.MicroGoodsInventoryMapper;
import mapper.warehousing.MicroWarehouseMapper;
import services.warehousing.IMicroWarehouseService;
import util.warehousing.Page;

public class MicroWarehouseService implements IMicroWarehouseService {
	@Inject
	MicroWarehouseMapper microMapper;
	
	@Inject
	MicroGoodsInventoryMapper mricoGoodMapper;

	@Override
	public Map<String, Object> save(MicroWarehouse mWarehouse) {
		boolean result = false;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (null != mWarehouse.getId() && mWarehouse.getId() != 0) {// 更新
			mWarehouse.setLastUpdate(new Date());
			result = microMapper.update(mWarehouse) > 0;
		} else {// 新增
			mWarehouse.setCreateTime(new Date());
			result = microMapper.insert(mWarehouse) > 0;
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
	public Page<MicroWarehouseDto> query(MicroWarehouseForm mWarehouse) {
		List<MicroWarehouseDto> list = microMapper.query(mWarehouse);
		int total = microMapper.getTotal(mWarehouse);
		int pageNo = mWarehouse.getPageNo();
		int pageSize = mWarehouse.getPageSize();
		if(mWarehouse.getPageNo() == 0){
			pageNo = 1;
		}
		if(mWarehouse.getPageSize() == 0){
			pageSize = 1;
		}
		Page<MicroWarehouseDto> page = new Page<MicroWarehouseDto>(list,total,pageNo,pageSize);
		return page;
	}

	@Override
	public Map<String, Object> delete(MicroWarehouse mWarehouse) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (microMapper.delete(mWarehouse) > 0) {
			resultMap.put("result", true);
		} else {
			resultMap.put("result", false);
			resultMap.put("msg", "删除失败");
		}
		return resultMap;
	}

	@Override
	public List<MicroRealWarehouseDto> queryMicroWarehouse(String email) {
		return mricoGoodMapper.selectbyemail(email);
	}

}
