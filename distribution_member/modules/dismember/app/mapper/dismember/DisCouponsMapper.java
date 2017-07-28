package mapper.dismember;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import entity.dismember.DisActive;
import entity.dismember.DisCoupons;

public interface DisCouponsMapper extends BaseMapper<DisCoupons> {
    int deleteByPrimaryKey(Integer id);

    int insert(DisCoupons record);

    int insertSelective(DisCoupons record);

    DisCoupons selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DisCoupons record);

    int updateByPrimaryKey(DisCoupons record);
    
    int batchSaveCoupons(List<DisCoupons> list);

	List<DisCoupons> queryPageCoupons(@Param("param")Map<String, Object> param);

	int queryTotalCount(@Param("param")Map<String, Object> param);
	
	DisCoupons getCoupons(@Param("couponsNo")String couponsNo);

	int updateCoupons(DisCoupons coupons);
	
	int updateState(DisActive active);
	
}