package mapper.purchase;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import entity.purchase.PurchaseAudit;

public interface PurchaseAuditMapper extends BaseMapper<PurchaseAudit> {
	
	PurchaseAudit selectByPurchaseOrderNo(@Param("purchaseOrderNo")String purchaseOrderNo);
	
	List<PurchaseAudit> selectByPurchaseOrderNoList(@Param("purchaseOrderNoList")List<String> purchaseOrderNoList);

	List<PurchaseAudit> select(@Param("map")Map<String, Object> map);
	
	Integer selectCount(@Param("map")Map<String, Object> map);

    /**
     * 查询待审核的线下转账申请数
     * @Author LSL on 2016-10-22 16:50:02
     */
    int getNoAuditApplyCount(@Param("purchaseNo") String purchaseNo);
}