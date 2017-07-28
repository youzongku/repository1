package mapper.warehousing;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dto.warehousing.MicroRealWarehouseDto;
import entity.warehousing.MicroGoodsInventory;
import forms.warehousing.MicroGoodsInventoryForm;

/**
 * 
 * 微仓库存mapper
 * 
 * @author ye_ziran
 * @since 2016年3月2日 下午5:18:28
 */
public interface MicroGoodsInventoryMapper {
	/**
	 * 插入数据
	 * 
	 * @param mInventory
	 * @return
	 * @author ye_ziran
	 * @since 2016年3月2日 下午5:16:18
	 */
	public int insert(MicroGoodsInventory mInventory);
	
	/**
	 * 修改数据
	 * 
	 * @param mInventory
	 * @return
	 * @author ye_ziran
	 * @since 2016年3月2日 下午5:16:25
	 */
	public int update(MicroGoodsInventory mInventory);
	
	/**
	 * 删除数据
	 * 
	 * @param mInventory
	 * @return
	 * @author ye_ziran
	 * @since 2016年3月2日 下午5:16:32
	 */
	public int delete(MicroGoodsInventory mInventory);
	
	/**
	 * 查询数据
	 * 
	 * @param mInventory			实体(暂且不考虑连表查询，所以用实体做查询条件)
	 * @param pageNum				第几页
	 * @param length				每页长度
	 * 
	 * @return
	 * @author ye_ziran
	 * @since 2016年3月2日 下午5:16:40
	 */
	public List<MicroGoodsInventory> query(@Param("paramDto")MicroGoodsInventoryForm mInventory,@Param("pageNum") Integer pageNum,@Param("length") Integer length);
	
	/**
	 * 根据查询条件，获得总记录数
	 * 
	 * @param mInventory
	 * @return
	 * @author ye_ziran
	 * @since 2016年3月3日 上午11:46:52
	 */
	public Integer total(@Param("paramDto")MicroGoodsInventoryForm mInventory);

	public List<MicroRealWarehouseDto> selectbyemail(@Param("email")String email);

	public List<Map<String, String>> queryImage(@Param("list")List<String> skus);

	List<MicroGoodsInventory> queryByList();
}
