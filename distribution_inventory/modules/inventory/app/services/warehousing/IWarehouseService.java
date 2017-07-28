package services.warehousing;

import java.util.Map;

import dto.warehousing.MicroWarehouseDto;
import entity.warehousing.Warehouse;
import forms.warehousing.WarehouseForm;
import util.warehousing.Page;

/**
 * 真实仓service
 * @author ouyangyaxiong
 * @date 2016年3月11日
 */
public interface IWarehouseService {
	/**
	 * 保存（新增或更新）
	 * @author ouyangyaxiong
	 * @data 2016/3/11 
	 * @return
	 */	
	public Map<String, Object> save(Warehouse warehouse);
	
	/**	
	 * 查询
	 * @author ouyangyaxiong
	 * @data 2016/3/11 
	 * @param wareouse
	 * @return
	 */
	public Page<Warehouse> query(WarehouseForm warehouse);
	
	public Map<String, Object> delete(Warehouse warehouse);

}
