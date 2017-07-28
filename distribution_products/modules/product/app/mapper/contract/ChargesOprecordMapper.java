package mapper.contract;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.contract.ChargesOprecord;

public interface ChargesOprecordMapper {
    int insert(ChargesOprecord record);

    int insertSelective(ChargesOprecord record);
    
    List<ChargesOprecord> selectByCid(@Param("cid")Integer cid);
}