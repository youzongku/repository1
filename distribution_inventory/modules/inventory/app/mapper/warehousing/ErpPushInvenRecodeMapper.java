package mapper.warehousing;

import entity.warehousing.ErpPushInvenRecode;

public interface ErpPushInvenRecodeMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(ErpPushInvenRecode record);

    int insertSelective(ErpPushInvenRecode record);

    ErpPushInvenRecode selectByPrimaryKey(Integer id);
    
    ErpPushInvenRecode selectByUniqueId(Integer uniqueId);

    int updateByPrimaryKeySelective(ErpPushInvenRecode record);

    int updateByPrimaryKey(ErpPushInvenRecode record);
}