package mapper.dismember;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.dismember.EmpSalesManMapper;

public interface EmpSalesManMapperMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EmpSalesManMapper record);

    int insertSelective(EmpSalesManMapper record);

    EmpSalesManMapper selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EmpSalesManMapper record);

    int updateByPrimaryKey(EmpSalesManMapper record);
    
    List<EmpSalesManMapper> selectBySaleManId(@Param("empId")Integer salesmanId);

	int deleteBySalemanId(@Param("salesManId")Integer salesManId);
	
	List<EmpSalesManMapper> selectBySaleManIds(@Param("list")List<Integer> list);

}


