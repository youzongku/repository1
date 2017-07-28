package services.sales;

import java.util.List;

import dto.sales.TaoBaoGoodsSearchDto;

import org.apache.ibatis.annotations.Param;

import entity.platform.order.template.TaoBaoOrderGoods;

public interface ITaoBaoOrderGoodsService {

	int insert(TaoBaoOrderGoods record);

	int insertSelective(TaoBaoOrderGoods record);

	List<TaoBaoOrderGoods> getGoodsByOrderNoAndEmail(String orderNo,String email);

	List<TaoBaoOrderGoods> getGoodsByParam(TaoBaoGoodsSearchDto dto);

	TaoBaoOrderGoods queryGoodsByOrderNoAndSKU(String orderNo, String sku);

	TaoBaoOrderGoods selectBygroup(TaoBaoOrderGoods appointGoods);
	
	int saveGoodsInfo(@Param("param") TaoBaoOrderGoods good);
	
	int batchDeleteOrderGoods(@Param("param") TaoBaoGoodsSearchDto form);//批量物理删除

	boolean updateImportItemQty(String goodId, Integer qty);

	int deletOrderGoodsById(Integer goodId);

}
