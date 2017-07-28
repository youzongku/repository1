package mapper.dismember;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import dto.dismember.Search;
import entity.dismember.OrderByAp;

public interface OrderByApMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderByAp record);

    int insertSelective(OrderByAp record);

    OrderByAp selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderByAp record);

    int updateByPrimaryKey(OrderByAp record);
    
    List<OrderByAp> pageSearch(Search dto);
    
    List<OrderByAp> pageSearchAuto(Search dto);

    Integer pageCount(Search dto);
    
    int batchUpdate(@Param("list")List<OrderByAp> list);
    
    List<OrderByAp> selectByBillId(Integer billId);
    
    int deleteByBillId(Integer id);

	List<OrderByAp> selectAll();
}