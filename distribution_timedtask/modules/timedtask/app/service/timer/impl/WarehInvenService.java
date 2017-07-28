package service.timer.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import mapper.timer.WarehouseMapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import entity.timer.Warehouse;
import entity.timer.WarehouseForm;
import service.timer.IWarehInvenService;

public class WarehInvenService implements IWarehInvenService{

	
	@Inject
	private WarehouseMapper mapper;
	

	@Override
	public List<Warehouse> queryWarehouse(Integer wid) {
		WarehouseForm form = new WarehouseForm();
		if(null == wid) {
			form.setStatus(1);
		} else {
			form.setStatus(1);
			form.setWarehouseId(wid.toString());
		}
		List<Warehouse> list = mapper.query(form);
		for (Warehouse house : list) {
			house.setId(Integer.parseInt(house.getWarehouseId()));
			house.setRemarks("");
		}
		return list;
	}
	
	
}
