package services.inventory.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

import entity.inventory.B2CGoodsInventory;
import entity.inventory.B2CWarehouse;
import entity.inventory.PostB2CData;
import entity.inventory.Warehouse;
import entity.inventory.WarehouseInventory;
import forms.warehousing.WarehouseForm;
import mapper.inventory.WarehouseInventoryMapper;
import mapper.inventory.WarehouseMapper;
import play.Logger;
import services.base.utils.JsonFormatUtils;
import services.inventory.IWarehInvenService;
import services.warehousing.IWarehouseService;

/**
 */
public class WarehInvenService implements IWarehInvenService{

	@Inject
	WarehouseMapper warehouseMapper;
	
	@Inject
	mapper.warehousing.WarehouseMapper mapper;
	
	@Inject
	WarehouseInventoryMapper warehouseInventoryMapper;
	
	@Inject
	IWarehouseService iWarehouseService;

    /**
     * 存储仓库信息（全量接口）
     * @param node
     * @return
     */
    @Override
    public Map<String, Object> saveWarehouse(Map<String, String[]> node) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			Warehouse b2bHouse = null;
			String batchNo = null;
			String[] value = node.get("data");
			PostB2CData data = JsonFormatUtils.jsonToBean(value[0], PostB2CData.class);
			List<B2CWarehouse> b2cWhs = data == null ? null : data.getHouses();
			if (null != b2cWhs && b2cWhs.size() > 0) {
				for (B2CWarehouse b2cHouse : b2cWhs) {
					if (null == batchNo) {
						batchNo = b2cHouse.getBatchNo();
					}
					b2bHouse = warehouseMapper.selectByPrimaryKey(Integer.parseInt(b2cHouse.getWarehouseId()));
					if (null == b2bHouse) {
						b2bHouse = new Warehouse();
						b2bHouse.setId(Integer.parseInt(b2cHouse.getWarehouseId()));
						b2bHouse.setWarehouseName(b2cHouse.getWarehouseName());
						b2bHouse.setStatus(1);
						b2bHouse.setBatchNo(b2cHouse.getBatchNo());
						warehouseMapper.insertSelective(b2bHouse);
					} else {
						b2bHouse.setBatchNo(b2cHouse.getBatchNo());
						b2bHouse.setWarehouseName(b2cHouse.getWarehouseName());
						b2bHouse.setStatus(1);
						warehouseMapper.updateByPrimaryKeySelective(b2bHouse);
					}
				}
				if (null != batchNo) {
					// 根据batchNo更新本次没有推送的仓库状态为禁用，status字段暂时b2b没用到，该功能只是暂时和b2c对应
					List<Warehouse> whs = warehouseMapper.queryWarehouse(null,batchNo);
					if (null != whs && whs.size() > 0) {
						for (Warehouse warehouse : whs) {
							warehouse.setStatus(0);
							warehouseMapper.updateByPrimaryKeySelective(warehouse);
						}
					}
				}
				Logger.info("仓库信息推送成功，批次为：" + batchNo + ",数据量为：" + b2cWhs.size() + "条。");
				result.put("success", true);
				result.put("code", "仓库信息推送成功，批次为：" + batchNo + ",数据量为：" + b2cWhs.size() + "条。");
			}else{
				Logger.info("未获取到仓库信息。");
				result.put("success", false);
				result.put("code", "未获取到仓库信息。");
			}
		} catch (Exception e) {
			result.put("success", false);
			result.put("code", "仓库信息推送失败。" + e);
		}
		return result;
	}

    /**
     * 存储商品库存信息（增量接口）
     * @param node
     * @return
     */
    @Override
    public Map<String, Object> saveInvenInfo(Map<String, String[]> node) {
    	Map<String, Object> result = Maps.newHashMap();
    	try {
    		String[] value = node.get("data");
    		
			PostB2CData data = JsonFormatUtils.jsonToBean(value[0], PostB2CData.class);
			List<B2CGoodsInventory> b2cGoods = data == null ? null : data.getGoods();
			if (null != b2cGoods && b2cGoods.size() > 0) {
				for (B2CGoodsInventory b2cInventory : b2cGoods) {
					WarehouseInventory search = new WarehouseInventory();
	    			search.setSku(b2cInventory.getSku());//SKU
	    			search.setWarehouseId(b2cInventory.getWarehouseId());//仓库ID
	    			//根据sku和仓库Id查询是否存在
	    			List<WarehouseInventory> list = warehouseInventoryMapper.checkInventory(search);
	    			WarehouseInventory b2bInventory = null;
	    			if(null != list && list.size() > 0){
	    				b2bInventory = list.get(0);
	    			}
	    			if(null == b2bInventory){
	    				b2bInventory = new WarehouseInventory();
	    				b2bInventory.setSku(b2cInventory.getSku());//SKU
	    				b2bInventory.setWarehouseId(b2cInventory.getWarehouseId());//仓库ID
	    				b2bInventory.setStock(b2cInventory.getAvailableStock());//可用库存
	    				b2bInventory.setCostprice(b2cInventory.getCostprice());//成本价
	    				b2bInventory.setWarehouseName(b2cInventory.getWarehouseName());//仓库名
	    				warehouseInventoryMapper.insertSelective(b2bInventory);
	    			}else{
	    				b2bInventory.setCostprice(b2cInventory.getCostprice());
	    				b2bInventory.setStock(b2cInventory.getAvailableStock());
	    				warehouseInventoryMapper.updateByPrimaryKeySelective(b2bInventory);
	    			}
				}
				Logger.info("库存信息推送成功,数据量为：" + b2cGoods.size() + "条。");
				result.put("success", true);
				result.put("code", "库存信息推送成功,数据量为：" + b2cGoods.size() + "条。");
			}else{
				Logger.info("未获取到商品库存信息。");
				result.put("success", false);
				result.put("code", "未获取到商品库存信息。");
			}
    		
		} catch (Exception e) {
			Logger.info("仓库信息推送失败, errInfo :" + e);
			result.put("success", false);
			result.put("code", "库存信息推送失败。");
		}
    	return result;
    }

	@Override
	public List<entity.warehousing.Warehouse> queryWarehouse(Integer wid,Boolean isBack, Boolean onlyVirtualHouse) {
		WarehouseForm form = new WarehouseForm();
		if(null == wid) {
			form.setStatus(1);
		} else {
			form.setStatus(1);
			form.setWarehouseId(wid.toString());
		}
		if(isBack!=null && !isBack) {
			form.setType("-10");// 前台不能查询到虚拟仓，表示不等于-10
		}
		List<entity.warehousing.Warehouse> list = mapper.query(form);
		
		for (entity.warehousing.Warehouse house : list) {
			house.setId(Integer.parseInt(house.getWarehouseId()));
			house.setRemarks("");
		}
		
		// 只要虚拟仓的
		if (onlyVirtualHouse!=null && onlyVirtualHouse) {
			return list.stream().filter(e->Objects.equals("-10", e.getType())).collect(Collectors.toList());
		}
		
		return list;
	}
	
	@Override
	public Map<String,Object> queryInventory(Map<String, String[]> node) {
		Map<String,Object> map = Maps.newHashMap();
		PostB2CData searchDto = JsonFormatUtils.jsonToBean(node.get("data")[0],PostB2CData.class);
		Logger.info("查询参数为：" + searchDto);
		map.put("inventorys", warehouseInventoryMapper.queryInventory(searchDto));
		map.put("count", warehouseInventoryMapper.queryInventoryCount(searchDto));
		return map;
	}

	@Override
	public WarehouseInventory inventory(JsonNode node) {
		PostB2CData searchDto = JsonFormatUtils.jsonToBean(node.toString(),PostB2CData.class);
		List<WarehouseInventory> list = warehouseInventoryMapper.queryInventory(searchDto);
		return list == null || list.size() == 0 ? null : list.get(0);
	}


}
