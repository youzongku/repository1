package mapper.warehousing;

import entity.warehousing.ErpPushInvenDetail;

public interface ErpPushInvenDetailMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(ErpPushInvenDetail record);

    int insertSelective(ErpPushInvenDetail record);

    ErpPushInvenDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ErpPushInvenDetail record);

    int updateByPrimaryKey(ErpPushInvenDetail record);
}