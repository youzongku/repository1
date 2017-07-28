package services.purchase;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import dto.JsonResult;
import dto.purchase.CancelPurchaseOrderParam;
import dto.purchase.InStorageIterm;
import dto.purchase.PurchaseOrderDto;
import dto.purchase.ReturnMess;
import dto.purchase.StatisIterm;
import dto.purchase.ViewPurchaseIterm;
import dto.purchase.ViewPurchaseOrder;
import entity.purchase.OrderOperateRecord;
import entity.purchase.PurchaseActive;
import entity.purchase.PurchaseAudit;
import entity.purchase.PurchaseOrder;
import entity.purchase.PurchaseOrderDetail;
import entity.purchase.PurchaseStockout;
import forms.purchase.DeliverDutyPaidGoodsParam;
import forms.purchase.InputOrderParam;
import play.mvc.Http.MultipartFormData.FilePart;

/**
 * Created by luwj on 2015/12/8.
 */
public interface IPurchaseOrderService {
	
	/**
	 * 整批出库
	 * @param stockout
	 */
	public void stockout(PurchaseStockout stockout);
	
	/**
	 * 后台现金支付采购单：会进行库存校验之类的检查
	 * @param purchaseOrderNo
	 * @return
	 */
	public Map<String,Object> balancePaymentBackStage(String purchaseOrderNo);
	
	/**
	 * 出库入库
	 * @param purchaseOrder
	 * @throws Exception
	 */
	public void stockInThenOut(PurchaseOrder purchaseOrder) throws Exception;
	
	/**
	 * 获取采购单的总到仓价
	 * @param purchaseOrderNo
	 */
	public Double getTotalArriveWarehousePrice(String purchaseOrderNo);
	
    /**
     * 采购单展示
     * @return
     */
    public ViewPurchaseIterm viewPurchase(Map<String,Object> map);
    
    public ViewPurchaseOrder viewPurchaseOrderDetailById(int orderId);

    /**
     * 更新订单状态
     * @param purchaseNo
     * @return
     */
    public ReturnMess cancelPurchaseOrder(CancelPurchaseOrderParam param);

    /**
     * 采购下单
     * @param node
     * @return
     */
    public ReturnMess orderPurchase(JsonNode node,String email);
    
    /**
     * 描述：更新采购单
     * @param purchaseOrder
     * @return
     */
    public boolean updatePurchaseOrder(PurchaseOrder purchaseOrder);

    /**
     * 通过采购单号查询微仓、物理仓参数
     * @return
     */
    public InStorageIterm getInfoByNo(String purchaseOrderNo,String flag);

    /**
     * 判断订单是否在有效的支付时间内
     * @param purchaseOrderNo
     * @return
     */
    public ReturnMess isValiPayDate(String purchaseOrderNo);

    /**
     *  采购单统计（某段时间内）
     * @param node
     * @return
     */
    public StatisIterm statisPurchaseOrder(JsonNode node);

    /**
     * 通过id查询采购订单详情
     * @param node
     * @return
     */
    public ViewPurchaseIterm getOrderById(JsonNode node);

	public List<ViewPurchaseOrder> getExportList(Map<String, Object> params);

	public List<PurchaseActive> getActive(String orderno);

	public InStorageIterm addGift(JsonNode node);

	public Map<String, Object> updateOrders(JsonNode node);
	/**
	 * 批量处理失效订单
	 * @author zbc
	 * @since 2016年8月23日 下午3:51:45
	 */
	public void batchInvalid();
	
	/**
	 * 处理订失效
	 * @author zbc
	 * @since 2016年12月14日 下午2:44:21
	 */
	public void invalid(PurchaseOrder order)  throws JsonProcessingException, IOException;
	
	/**
	 * 查询采购单列表
	 * @param node
	 * @param accounts
	 * @return
	 */
	public ViewPurchaseIterm queryPurchases(JsonNode node, List<String> accounts);
	
	public Map<String, Object> importOrder(File file, String fileName, Map<String, String[]> params, String account);

	/**
	 * 获取导入订单信息
	 * @author zbc
	 * @since 2016年8月31日 下午2:47:22
	 */
	public Map<String, Object> getImportOrder(JsonNode node, int inputTypeImport); 

	/**
	 * 采购 导入 与 录入订单 生成采购单
	 * @author zbc
	 * @since 2016年9月1日 下午2:32:43
	 */
	public Map<String,Object> generInputOder(InputOrderParam param);
	
	/**
	 * 完税仓商品出库
	 * @param param 下采购单的参数
	 * @param deliverParam 封装了下返货单所需的基础数据
	 * @return
	 */
	public Map<String,Object> deliverDutyPaidGoods(InputOrderParam param, DeliverDutyPaidGoodsParam deliverParam);

	/**
	 * 用于更新正价商品仓库信息
	 * @author zbc
	 * @since 2016年9月1日 下午3:07:39
	 */
	public Map<String,Object> proUpdate(JsonNode node);

	/**
	 * 用于更新赠品商品仓库信息
	 * @author zbc
	 * @since 2016年9月1日 下午3:07:55
	 */
	public Map<String,Object> giftUpdate(JsonNode node);

	/**
	 * 线下转账的
	 * @param params
	 * @param file
	 * @param email
	 * @return
	 */
	public String submitAudit(Map<String, String[]> params, FilePart file, String email);

	/**
	 * 查询线下转账的记录
	 * @param param
	 * @return
	 */
//	public Page<PurchaseAuditDto> queryAudits(String param);

	/**
	 * 获取转账申请
	 * @Author LSL on 2016-10-24 15:52:53
	 */
	public PurchaseAudit getTransferApply(Integer id);

	/**
	 * 修改采购单价格
	 * @author zbc
	 * @since 2016年12月2日 下午6:09:49
	 */
	Map<String, Object> changeOrderPrice(String string);
	
	
	
	/**
	 * @author zbc
	 * @since 2016年12月13日 下午6:41:26
	 */
	public void markPro(PurchaseOrder order);

	/**
	 * 获取 有赠品活动
	 * @author zbc
	 * @since 2016年12月14日 下午5:19:36
	 */
	public Map<String,Object> getMaketAct(String orderNo);

	/**
	 * 获取 活动的赠品
	 * @author zbc
	 * @since 2016年12月14日 下午5:20:09
	 */
	public JsonNode getGiftList(Integer actId, Map<String, Object> param,String email);

	public boolean checkNeedAuditOrNot(PurchaseOrder po);

	/**
	 * 现金支付
	 * @author zbc
	 * @since 2016年12月16日 下午4:06:12
	 */
	public ReturnMess payedByCash(JsonNode json);

	/**
	 * 根据email修改订单昵称
	 * @author lzl
	 * @since 2016年12月22日下午3:42:08
	 */
	public String changeNickNameByEmail(String param);

	/**
	 * 锁定库存
	 * @author zbc
	 * @since 2016年12月26日 下午5:29:57
	 */
	public Map<String,Object> lock(String orderNo);

	/**
	 * 同步计算均摊价
	 * @author zbc
	 * @since 2016年12月26日 下午6:00:37
	 */
	public List<PurchaseOrderDetail> caculateCapFee(PurchaseOrderDto event);

	/**
	 * 计算均摊价方法
	 * @author zbc
	 * @since 2016年12月28日 下午4:11:57
	 */
	public void caculateDetails(Double amount, List<PurchaseOrderDetail> details);
	
	public void changeInventoryCafee(PurchaseOrderDto event, List<PurchaseOrderDetail> details);

	public String savePurchaseStockout(String string);
	/**
	 * 合并发货时是更新取货采购单运费
	 * @author zbc
	 * @since 2017年5月24日 上午11:27:41
	 * @param string
	 * @return
	 */
	public JsonResult<?> changeFreight(String string);
	/**
	 * 获取采购单操作日志
	 * @param purchaseNo
	 * @return
	 */
	public List<OrderOperateRecord> orderOperateRecord(String purchaseNo);

	/**
	 * 获取采购单详情
	 * @param paramNode
	 * @param accounts
	 * @return
	 */
	public ViewPurchaseOrder purchaseSimpleInfo(JsonNode paramNode, List<String> accounts);
	
}
