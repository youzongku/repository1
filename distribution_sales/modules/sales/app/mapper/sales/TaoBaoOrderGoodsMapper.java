package mapper.sales;

import java.util.List;

import dto.sales.TaoBaoGoodsSearchDto;

import org.apache.ibatis.annotations.Param;

import entity.platform.order.template.TaoBaoOrderGoods;

public interface TaoBaoOrderGoodsMapper {
	int insert(TaoBaoOrderGoods record);

	int insertSelective(TaoBaoOrderGoods record);

	List<TaoBaoOrderGoods> getGoodsByOrderNoAndEmail(@Param("orderNo")String orderNo,@Param("email")String email);

	List<TaoBaoOrderGoods> getGoodsByParam(TaoBaoGoodsSearchDto dto);

	List<TaoBaoOrderGoods> queryGoodsByCondition(@Param("orderNo") String orderNo, @Param("sku") String sku, @Param("email") String email);

	int saveGoodsInfo(@Param("param") TaoBaoOrderGoods good);

	int batchDeleteOrderGoods(@Param("param") TaoBaoGoodsSearchDto form);//批量逻辑删除
	
	int getGoodsNumByOrderNoAndEmail(@Param("orderNo") String orderNo,@Param("email") String email);

	TaoBaoOrderGoods selectBygroup(@Param("param") TaoBaoOrderGoods appointGoods);
	
	int batchInsert(List<TaoBaoOrderGoods> goods);
	
	int deleteOrderGoodById(Integer goodId);

	/**
	 * 根据订单号和email查询商品
	 * @param orderNos
	 * @param email
	 * @return
	 */
	List<TaoBaoOrderGoods> goodsLists(@Param("orderNos")List<String> orderNos, @Param("email")String email);

	TaoBaoOrderGoods selectByPrimaryKey(int id);
}