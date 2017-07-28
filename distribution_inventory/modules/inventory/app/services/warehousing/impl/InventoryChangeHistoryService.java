package services.warehousing.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import dto.warehousing.InventoryChangeHistoryDto;
import dto.warehousing.ReturnMess;
import dto.warehousing.WarehouseChangeRecordDto;
import entity.warehousing.InventoryChangeHistory;
import entity.warehousing.MicroGoodsInventory;
import enums.warehousing.OrderType;
import forms.warehousing.InventoryChangeHistoryForm;
import forms.warehousing.MicroGoodsInventoryForm;
import mapper.warehousing.InventoryChangeHistoryMapper;
import mapper.warehousing.MicroGoodsInventoryMapper;
import services.warehousing.IInventoryChangeHistoryService;
import util.warehousing.Page;

public class InventoryChangeHistoryService implements IInventoryChangeHistoryService {
	
	@Inject
	InventoryChangeHistoryMapper inventoryHistoryMapper;
	@Inject
	MicroGoodsInventoryMapper microGoodsInventoryMapper;

	@Override
	public int save(InventoryChangeHistory history) {
		int res = 0;
		if(history.getId() == null){
			res = inventoryHistoryMapper.insertSelective(history);
		}else{
			res = inventoryHistoryMapper.update(history);
		}
		return res;
	}

	@Override
	public ReturnMess delete(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<InventoryChangeHistory> get(InventoryChangeHistoryDto dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<InventoryChangeHistoryForm> queryByParam(WarehouseChangeRecordDto dto) {
		List<InventoryChangeHistoryForm> list = inventoryHistoryMapper.queryByParam(dto);
		int total = inventoryHistoryMapper.getTotal(dto);
		int pageNo = dto.getPageNo();
		int pageSize = dto.getPageSize();
		Page<InventoryChangeHistoryForm> page = new Page<InventoryChangeHistoryForm>(list,total,pageNo, pageSize);
		return page;
	}

	@Override
	public Map<String, Object> getPurchasePriceByChangeHistory(JsonNode node) {
		
		JsonNode details = node.get("details");
		
		Map<String, Object> disPriceCovered = new LinkedHashMap<String, Object>();
		
		for (JsonNode detail : details) {
			//时间降序获取微仓入库记录
			InventoryChangeHistoryDto dto = new InventoryChangeHistoryDto();
			dto.setSku(detail.get("sku").asText());
			dto.setWarehouseId(node.get("warehouseId").asInt());
			dto.setMwarehouseName(node.get("email").asText());
			dto.setType(1);//入库记录
			dto.setChangeTimeDesc(1);//降序排序
			dto.setOrderType(OrderType.IN_RESET.getValue());//当orderType参数存在，则意味着会排除掉该状态的记录
			List<InventoryChangeHistory> hs = inventoryHistoryMapper.selectByParamDto(dto);
			
			//此次发货数量
			List<InventoryChangeHistory> coveredHistoryList = new ArrayList<InventoryChangeHistory>();
			//微仓库存覆盖到的入仓记录
			List<InventoryChangeHistory> StockcoveredHistoryList = new ArrayList<InventoryChangeHistory>();
			
			//获取微仓剩余库存
			MicroGoodsInventoryForm form = new MicroGoodsInventoryForm();
			form.setWarehouseId(node.get("warehouseId").asInt());
			form.setMwarehouseName(node.get("email").asText());
			form.setSku(detail.get("sku").asText());
			int stock = 0;
			List<MicroGoodsInventory> mgis = microGoodsInventoryMapper.query(form, 1, 10);
			if(mgis != null && mgis.size() > 0){
				stock = mgis.stream().mapToInt(e->e.getAvaliableStock()).sum();
			}
			//获取剩余库存覆盖到的入仓记录
			for (InventoryChangeHistory i : hs) {
				//临界校验，如果某一条入库记录已经大于剩余所需要校验的数量，那么添加这个记录，终止循环
				if(stock <= i.getNum()){
					i.setNum(stock);
					StockcoveredHistoryList.add(i);
					break;
				}
				stock = stock - i.getNum();
				if(stock >= 0){
					StockcoveredHistoryList.add(i);
				}else{
					break;
				}
			}
			
			//反转入仓记录顺序，升序进行覆盖，查找价格
			Collections.reverse(StockcoveredHistoryList);
			//获取需要检查的库存
			int qtyForCheck = detail.get("qty").asInt();
			//收集已覆盖的采购价格
			//检查覆盖情况，获取覆盖到的
			for (InventoryChangeHistory i : StockcoveredHistoryList) {
				
				//临界校验，如果某一条入库记录已经大于剩余所需要校验的数量，那么添加这个记录，终止循环
				if(qtyForCheck <= i.getNum()){
					i.setNum(qtyForCheck);
					coveredHistoryList.add(i);
					break;
				}
				
				qtyForCheck = qtyForCheck - i.getNum();
				if(qtyForCheck >= 0){
					coveredHistoryList.add(i);
				}else{
					break;
				}
			}
			
			disPriceCovered.put(detail.get("sku").asText(), coveredHistoryList);
		}
		
		return disPriceCovered;
	}

}
