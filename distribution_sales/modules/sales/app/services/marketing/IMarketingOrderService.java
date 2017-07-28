package services.marketing;

import dto.marketing.AuditParams;
import dto.marketing.MarketingOrderDto;
import dto.marketing.QueryMarketingOrderParams;
import dto.marketing.ViewMarketingOrderDto;
import entity.marketing.MarketingOrderAuditLog;
import events.sales.GenerateSaleOrderEvent;
import pager.sales.Pager;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 营销单service
 * 
 * @author huangjc
 * @since 2017年3月10日
 */
public interface IMarketingOrderService {
	
	/**
	 * 获取初审的最新一条审核记录
	 * @param marketingOrderNo
	 * @return
	 */
	public MarketingOrderAuditLog getAuditFirstlyLatestLog(String marketingOrderNo);
	
	/**
	 * 获取一个营销单所有的审核记录
	 * @param marketingOrderNo
	 * @return
	 */
	public List<MarketingOrderAuditLog> getAllAuditLogs4AMo(String marketingOrderNo);
	
	/**
	 * 创建营销单
	 * @param loginAccount 
	 * 
	 * @param mo
	 * @param detailList
	 * @return
	 */
	public Map<String, Object> createMarketingOrder(String mainStr, String loginAccount);

	/**
	 * 查询发货单，非分页
	 * @param params
	 * @return
	 */
	public List<MarketingOrderDto> getMarketingOrderList(QueryMarketingOrderParams params);
	
	/**
	 * 分页查询
	 * 
	 * @return
	 */
	public Pager<ViewMarketingOrderDto> getMarketingOrderPage(QueryMarketingOrderParams param);

	/**
	 * 初审和复审
	 * 
	 * @return
	 */
	public Map<String, Object> audit(AuditParams params);

	/**
	 * 生成销售单
	 * xuse
	 * 2016年12月29日
	 * @param event
	 * @return
	 */
	public void generateOrder(GenerateSaleOrderEvent event);

	/**
	 * 营销单锁库
	 * @author zbc
	 * @since 2017年1月4日 下午6:43:33
	 */
	public Map<String, Object> lock(String orderNo);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月17日 上午10:38:47
	 */
	public File smExport(String fileName,Map<String, String[]> map);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月20日 下午5:32:33
	 */
	public Pager<ViewMarketingOrderDto> query(Map<String, String[]> map);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月20日 下午5:35:48
	 */
	public File exportMoList(String filename, Map<String, String[]> map);

	/**
	 * 
	 * @author zbc
	 * @since 2017年3月20日 下午5:38:22
	 */
	public Pager<ViewMarketingOrderDto> smMoList(Map<String, String[]> map);

}
