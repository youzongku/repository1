package mapper.product.store;

import java.util.List;
import java.util.Map;

import entity.product.store.BbcAttribute;

public interface BbcAttributeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BbcAttribute record);

    int insertSelective(BbcAttribute record);

    BbcAttribute selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BbcAttribute record);

    int updateByPrimaryKey(BbcAttribute record);
    
    List<BbcAttribute> queryPage(Map<String, Object> map );
    
    int queryCount(Map<String, Object> map );
}