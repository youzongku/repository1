package mapper.dismember;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.dismember.DisShopDpLog;

public interface DisShopDpLogMapper {
	List<DisShopDpLog> select(@Param("shopId")int shopId, @Param("email")String email);
	
	List<DisShopDpLog> selectByShopIdList(List<Integer> shopIdList);
	
	int batchInsert(List<DisShopDpLog> list);
	
	int insertSelective(DisShopDpLog log);
	int insert(DisShopDpLog log);
}
