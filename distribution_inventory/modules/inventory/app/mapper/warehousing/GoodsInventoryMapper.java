package mapper.warehousing;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.warehousing.GoodsInventoryListDto;
import dto.warehousing.GoodsInventorySearchDto;
import entity.warehousing.GoodsInventory;

public interface GoodsInventoryMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(GoodsInventory record);

    int insertSelective(GoodsInventory record);

    GoodsInventory selectByPrimaryKey(GoodsInventory goodsInventory);

   // List<GoodsInventory> getGoodsInvens(GoodsInventory goodsInventory);

    int updateByPrimaryKeySelective(GoodsInventory record);

    int updateByPrimaryKey(GoodsInventory record);

	int getTotal(@Param("paramDto") GoodsInventorySearchDto param);
	
	int getCloudTotal(@Param("paramDto") GoodsInventorySearchDto param);
	
	List<GoodsInventoryListDto> getGoodsInvens(@Param("paramDto") GoodsInventorySearchDto param);
	
	List<GoodsInventoryListDto> getCloudGoodsInvens(@Param("paramDto") GoodsInventorySearchDto param);
}