package mapper.warehousing;

import java.util.List;

import dto.warehousing.MicroWarehouseDto;
import entity.warehousing.MicroWarehouse;
import forms.warehousing.MicroWarehouseForm;

/**
 * 微仓信息mapper
 * 
 * @author ye_ziran
 * @since 2016年3月2日 下午5:17:57
 */
public interface MicroWarehouseMapper {
	
	/**
	 * 插入数据
	 * 
	 * @param mWarehouse
	 * @return
	 * @author oyx
	 * @since 2016年3月2日 下午5:16:18
	 */
	public int insert(MicroWarehouse mWarehouse);
	
	/**
	 * 修改数据
	 * 
	 * @param mWarehouse
	 * @return
	 * @author oyx
	 * @since 2016年3月2日 下午5:16:25
	 */
	public int update(MicroWarehouse mWarehouse);
	
	/**
	 * 删除数据
	 * 
	 * @param mWarehouse
	 * @return
	 * @author oyx
	 * @since 2016年3月2日 下午5:16:32
	 */
	public int delete(MicroWarehouse mWarehouse);
	
	/**
	 * 查询数据
	 * 
	 * @param mWarehouse
	 * @return
	 * @author oyx
	 * @since 2016年3月2日 下午5:16:40
	 */
	public List<MicroWarehouseDto> query(MicroWarehouseForm mWarehouse);

	public int getTotal(MicroWarehouseForm mWarehouse);

	
}
