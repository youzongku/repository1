package mapper.dismember;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.dismember.ApBillOrderMapping;

public interface ApBillOrderMappingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApBillOrderMapping record);

    int insertSelective(ApBillOrderMapping record);

    ApBillOrderMapping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApBillOrderMapping record);

    int updateByPrimaryKey(ApBillOrderMapping record);
    
    int deleteByBillId(@Param("billId")Integer billId);
    
    int batchInsert(@Param("list")List<ApBillOrderMapping> mappings);
}