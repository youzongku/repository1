package mapper.warehousing;

import java.util.List;

import entity.warehousing.Warehouse;
import forms.warehousing.WarehouseForm;

public interface WarehouseMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Warehouse record);

    int insertSelective(Warehouse record);

    Warehouse selectByPrimaryKey(Warehouse record);

    int updateByPrimaryKeySelective(Warehouse record);

    int updateByPrimaryKey(Warehouse record);

    List<Warehouse> getWaresByBatch(String batchNo);
    
    List<Warehouse> query(WarehouseForm form);

	int getTotal(WarehouseForm warehouse);
}