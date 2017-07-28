package services.warehousing.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import dto.warehousing.GoodsInventoryListDto;
import dto.warehousing.GoodsInventorySearchDto;
import entity.warehousing.GoodsInventory;
import forms.warehousing.InventoryChangeDetailForm;
import forms.warehousing.InventoryChangeForm;
import mapper.warehousing.GoodsInventoryMapper;
import play.Logger;
import services.warehousing.IGoodsInventoryService;
import util.warehousing.Page;

public class GoodsInventoryService implements IGoodsInventoryService {
	
	@Inject
	InventoryChangeHistoryService iHistoryService;

	@Inject
	GoodsInventoryMapper inventoryMapper;

	@Override
	public Map<String, Object> save(GoodsInventorySearchDto param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<GoodsInventoryListDto> query(GoodsInventorySearchDto param) {
		List<GoodsInventoryListDto> list = inventoryMapper.getGoodsInvens(param);
		int total = inventoryMapper.getTotal(param);
		int pageNo = param.getPageNo();
		int pageSize = param.getPageSize();
		if(param.getPageNo() == 0){
			pageNo = 1;
			param.setPageNo(1);
		}
		if(param.getPageSize() == 0){
			pageSize = 10;
			param.setPageSize(10);
		}
		Page<GoodsInventoryListDto> page = new Page<>(list,total, pageNo, pageSize);
		return page;
	}
	
	@Override
	public Page<GoodsInventoryListDto> cloudInventoryQuery(GoodsInventorySearchDto param) {
		List<GoodsInventoryListDto> list = inventoryMapper.getCloudGoodsInvens(param);
		int total = inventoryMapper.getTotal(param);
		int pageNo = param.getPageNo();
		int pageSize = param.getPageSize();
		if(param.getPageNo() == 0){
			pageNo = 1;
			param.setPageNo(1);
		}
		if(param.getPageSize() == 0){
			pageSize = 10;
			param.setPageSize(10);
		}
		Page<GoodsInventoryListDto> page = new Page<>(list,total, pageNo, pageSize);
		return page;
	}
	

	@Override
	public Map<String, Object> delete(GoodsInventorySearchDto param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (inventoryMapper.deleteByPrimaryKey(param.getId()) > 0) {
			resultMap.put("result", true);
		} else {
			resultMap.put("result", false);
			resultMap.put("msg", "删除失败");
		}
		return resultMap;
	}
	

	@Override
	public List<Map<String, Object>> updateStock(InventoryChangeForm inventoryForm) {
		List<Map<String, Object>> resultList = new ArrayList<>();
		List<InventoryChangeDetailForm> detailList = inventoryForm.getDetailList();
		int type = inventoryForm.getOrderType();
		for (InventoryChangeDetailForm detailForm : detailList) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			int num = detailForm.getNum();
			
			// sku+仓库id 确定一条唯一的库存记录
			GoodsInventory goodsInventory = new GoodsInventory();
			goodsInventory.setSku(detailForm.getSku());
			goodsInventory.setWarehouseId(detailForm.getWarehouseId());
			goodsInventory = inventoryMapper.selectByPrimaryKey(goodsInventory);
			
			if(goodsInventory != null){
				int tmpNum = num;
				if( type >= 20 ){//type大于20，都是出库
					tmpNum = -tmpNum;
				}
				//判断库存是否足够
//				if(goodsInventory.getAvailableStock() + tmpNum >= 0){
					goodsInventory.setTotalStock(goodsInventory.getTotalStock() + tmpNum);
					goodsInventory.setAvailableStock(goodsInventory.getAvailableStock() + tmpNum);
					goodsInventory.setLastUpdated(new Date());
					inventoryMapper.updateByPrimaryKeySelective(goodsInventory);
					
//					//记录库存变更
//					InventoryChangeHistory history = new InventoryChangeHistory();
//					history.setChangeTime(new Date());
//					history.setDisMemberEmail(inventoryForm.getDistributorEmail());
//					history.setEffective(true);
//					history.setNum(num);
//					history.setOperator(inventoryForm.getDistributorEmail());
//					history.setOrderNo(inventoryForm.getOrderNo());
//					history.setProductName(detailForm.getProductTitle());
//					history.setSku(detailForm.getSku());
//					history.setType(inventoryForm.getOrderType());
//					history.setWarehouseId(detailForm.getWarehouseId());
//					history.setWarehouseName(detailForm.getWarehouseName());
//					iHistoryService.save(history);
//				}else{//提示库存不足
//					resultMap.put("result", false);
//					resultMap.put("msg", "出库失败，商品[sku="+detailForm.getSku()+"]库存不足");
//				}
				
				resultMap.put("result", true);
			}else{//库存为空，提示报错信息,仓库[2032]不存在[sku=IW148]的库存
				resultMap.put("result", false);
				resultMap.put("msg", "出入库失败，仓库["+detailForm.getWarehouseId()+"]不存在[sku="+detailForm.getSku()+"]的库存");
			}
			resultList.add(resultMap);
		}
		return resultList;
	}

	@Override
	public Map<String,String> restoreStockOfeOrderClosed(Integer warehouseId, String csku, Integer iqty) {
		Logger.info("订单关闭，库存恢复信息---->warehouseId:{};csku:{};iqty:{}", warehouseId,csku,iqty);
		Map<String,String> result=Maps.newHashMap();
		GoodsInventory goodsInventory=new GoodsInventory();
		goodsInventory.setSku(csku);
		goodsInventory.setWarehouseId(warehouseId);
		try {
			GoodsInventory resultGoodsInventory=inventoryMapper.selectByPrimaryKey(goodsInventory);
			Logger.info("订单关闭库存恢复查询结果----->{}",resultGoodsInventory);
			if(resultGoodsInventory==null){
				result.put("suc", "false");
				result.put("msg", "查询不到该商品");
				return result;
			}
			resultGoodsInventory.setTotalStock(resultGoodsInventory.getTotalStock()+iqty);
			resultGoodsInventory.setAvailableStock(resultGoodsInventory.getAvailableStock()+iqty);
			
			
			int i = inventoryMapper.updateByPrimaryKey(resultGoodsInventory);
			Logger.info("订单关闭库存恢复返回结果----->i：{}",i);
			result.put("suc", "success");
			result.put("msg", "库存恢复成功");
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logger.error("订单关闭库存恢复数据库连接异常----->{}",e);
			result.put("suc", "false");
			result.put("msg", "数据库连接异常");
			return result;
		}
	}
	
}
