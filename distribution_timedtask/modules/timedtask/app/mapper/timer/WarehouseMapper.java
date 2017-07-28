package mapper.timer;

import java.util.List;

import entity.timer.Warehouse;
import entity.timer.WarehouseForm;

public interface WarehouseMapper {
    
    List<Warehouse> query(WarehouseForm form);
	
}