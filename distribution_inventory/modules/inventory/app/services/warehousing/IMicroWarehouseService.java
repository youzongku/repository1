package services.warehousing;

import java.util.List;
import java.util.Map;

import dto.warehousing.MicroRealWarehouseDto;
import dto.warehousing.MicroWarehouseDto;
import entity.warehousing.MicroWarehouse;
import forms.warehousing.MicroWarehouseForm;
import util.warehousing.Page;

public interface IMicroWarehouseService {
	/**
	 * 保存（新增或更新）
	 * @author ouyangyaxiong
	 * @data 上午11:03:25
	 * @return
	 */	
	public Map<String, Object> save(MicroWarehouse mWarehouse);
	
	/**	
	 * 查询
	 * @author ouyangyaxiong
	 * @data 上午11:03:14
	 * @param mWareouse
	 * @return
	 */
	public Page<MicroWarehouseDto> query(MicroWarehouseForm mWarehouse);
	
	public Map<String, Object> delete(MicroWarehouse mWarehouse);

	public List<MicroRealWarehouseDto> queryMicroWarehouse(String email);

}
