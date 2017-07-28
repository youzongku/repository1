package services.sales;

import com.fasterxml.jackson.databind.JsonNode;
import dto.sales.ShOrderDto;
import entity.sales.ShAttachment;
import entity.sales.ShLog;
import entity.sales.ShOrder;
import entity.sales.ShOrderDetail;
import pager.sales.Pager;
import play.mvc.Http;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author longhuashen
 * @since 2017/4/8
 */
public interface ISaleAfterService {
	
	/**
	 * 计算指定采购单单号退款详情的实际退款均摊
	 * @param purchaseOrderNo 采购单单号
	 * @return {
	 * 				suc:true, 
	 * 				shOrderInfoList:[
	 * 					{
	 * 						shOrder: {}, 
	 * 						shOrderDetailList: [
	 * 							{售后详情}
	 * 						]
	 * 					}
	 * 				]
	 * 			}
	 */
	public Map<String, Object> getReturnAmountCapfee4Sku(String purchaseOrderNo);

    /**
     * 获取某个用户的发货单售后列表
     *
     * @param main
     * @param email
     * @return
     */
    Pager<ShOrder> selectShSaleOrderList(JsonNode main, String email);

    /**
     * 获取售后单详情
     *
     * @param orderId
     * @return
     */
    ShOrderDto getAfterSaleOrderDtoById(int orderId);

    /**
     * 发起售后申请
     *
     * @param params
     * @param files
     * @return
     */
    Map<String, Object> saleOrderRefundsApply(Map<String, String[]> params, List<Http.MultipartFormData.FilePart> files);

    /**
     * 取消售后单申请
     *
     * @param json
     * @return
     */
    Map<String,Object> cancleSaleOrderRefundsApply(JsonNode json);


    /**
     * 后台查询退款单列表
     *
     * @param json
     * @param accounts
     * @return
     */
    Map<String, Object> selectSaleOrderRefundsListOfBackstage(JsonNode json, List<String> accounts);

    ShOrder getSalesOrderRefundsById(JsonNode json);

    List<ShAttachment> getShAttachmentListByShOrderId(JsonNode json);

    File getShAttachmentImg(Integer integer);

    List<ShOrderDetail> getShOrderDetailListByShOrderId(JsonNode json);

    Map<String,Object> shAudit(JsonNode json, String account);

    List<ShLog> getShLogListByShOrderId(JsonNode json);

    Map<String,Object> saleOrderRefundsApplyLogistics(JsonNode json);

    Map<String, Object> selectEffectiveShOrderCount(JsonNode json);

    Map<String, Object> selectEffectiveShOrderByDetailOrderId(JsonNode json);
}
