package mapper.marketing;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import entity.marketing.MarketingOrderAuditLog;

public interface MarketingOrderAuditLogMapper {
	/**
	 * 获取一条最新的指定审核类型的审核记录
	 * @param marketingOrderNo
	 * @return
	 */
	MarketingOrderAuditLog selectAOrderLatestLog(@Param("marketingOrderNo") String marketingOrderNo,@Param("auditType") Integer auditType);

	int insert(MarketingOrderAuditLog record);

	MarketingOrderAuditLog selectByPrimaryKey(Integer id);

	List<MarketingOrderAuditLog> selectAllLogs(
			@Param("marketingOrderNo") String marketingOrderNo,
			@Param("auditType") Integer auditType);
}