package services.sales.impl;

import java.util.List;

import com.google.common.eventbus.EventBus;
import events.sales.ImportOrderSyncEvent;
import mapper.sales.TaoBaoOrderGoodsMapper;
import mapper.sales.TaoBaoOrderMapper;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.guice.transactional.Transactional;

import play.Logger;
import services.sales.ITaoBaoOrderGoodsService;

import com.google.inject.Inject;

import dto.sales.TaoBaoGoodsSearchDto;
import dto.sales.TaoBaoOrderForm;
import entity.platform.order.template.TaoBaoOrderGoods;

public class TaoBaoOrderGoodsService implements ITaoBaoOrderGoodsService {

	@Inject private	TaoBaoOrderGoodsMapper taoBaoOrderGoodsMapper;
	@Inject private	TaoBaoOrderMapper taoBaoOrderMapper;

	@Inject
	private EventBus eventBus;
	
	@Override
	public int insert(TaoBaoOrderGoods record) {
		return taoBaoOrderGoodsMapper.insert(record);
	}

	@Override
	public int insertSelective(TaoBaoOrderGoods record) {
		return taoBaoOrderGoodsMapper.insertSelective(record);
	}

	@Override
	public List<TaoBaoOrderGoods> getGoodsByOrderNoAndEmail(String orderNo,String email) {
		return taoBaoOrderGoodsMapper.getGoodsByOrderNoAndEmail(orderNo,email);
	}
	
	@Override
	public List<TaoBaoOrderGoods> getGoodsByParam(TaoBaoGoodsSearchDto dto){
		return taoBaoOrderGoodsMapper.getGoodsByParam(dto);
	}

	@Override
	public TaoBaoOrderGoods queryGoodsByOrderNoAndSKU(String orderNo, String sku) {
		List<TaoBaoOrderGoods> goodsList = this.taoBaoOrderGoodsMapper.queryGoodsByCondition(orderNo, sku, null);
		if (goodsList!=null && goodsList.size()>0) {
			return goodsList.get(0);
		}
		return new TaoBaoOrderGoods();
	}

	@Override
	public TaoBaoOrderGoods selectBygroup(TaoBaoOrderGoods appointGoods) {
		return taoBaoOrderGoodsMapper.selectBygroup(appointGoods);
	}
	
	@Override
	public int saveGoodsInfo(TaoBaoOrderGoods good) {
		return taoBaoOrderGoodsMapper.saveGoodsInfo(good);
	}

	@Transactional
	@Override
	public int batchDeleteOrderGoods(TaoBaoGoodsSearchDto form) {
		Logger.info("email:"+form.getEmail()+"  orderNo:"+form.getOrderNo());
		int res = taoBaoOrderGoodsMapper.batchDeleteOrderGoods(form);//订单号加skulist删除goods表
		int goodsNum = taoBaoOrderGoodsMapper.getGoodsNumByOrderNoAndEmail(form.getOrderNo(),form.getEmail());
		if(goodsNum == 0){//该订单所有商品都被删除了，则删除该订单
			taoBaoOrderMapper.deleteLogicOrder(form.getOrderNo(),form.getEmail());
		} else {//说明部分生成
			TaoBaoOrderForm order = new TaoBaoOrderForm();
			order.setOrderNo(form.getOrderNo());
			order.setIsPart(true);
			taoBaoOrderMapper.saveOrder(order);
		}
		return res;
	}

	@Override
	public boolean updateImportItemQty(String goodId, Integer qty) {
		int line = 0;
		if(StringUtils.isNotEmpty(goodId)){
			TaoBaoOrderGoods ordergood = new TaoBaoOrderGoods();
			ordergood.setId(Integer.parseInt(goodId));
			ordergood.setAmount(Integer.valueOf(qty));
			line = taoBaoOrderGoodsMapper.saveGoodsInfo(ordergood);
			Logger.debug("updateDisCartItemByQty line-->" + line);
		}
		return line == 1 ? true : false;
	}

	/**
	 * 根据id删除某个商品
	 */
	@Override
	public int deletOrderGoodsById(Integer goodId) {
		if(goodId == null) {
			return 0;
		}
		TaoBaoOrderGoods taoBaoOrderGoods = taoBaoOrderGoodsMapper.selectByPrimaryKey(goodId);
		int line = 0;
		if (goodId != null) {
			line = taoBaoOrderGoodsMapper.deleteOrderGoodById(goodId);
		}

		if (taoBaoOrderGoods != null) {
			eventBus.post(new ImportOrderSyncEvent(taoBaoOrderGoods.getEmail()));
		}
		return line;
	}
}
