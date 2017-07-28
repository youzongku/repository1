package services.product;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import dto.JsonResult;
import dto.product.ContractQuotationsDto;
import dto.product.PageResultDto;
import entity.contract.ContractQuotations;
import entity.contract.QuotedOprecord;

public interface IQuotedService {

	/**
	 * 添加报价信息
	 * 
	 * @param params
	 * @param opUser
	 * @return
	 */
	public Map<String, Object> addQuoted(Map<String, String[]> params, String opUser);

	/**
	 * 删除报价信息
	 * 
	 * @param qid
	 * @param string
	 * @return
	 */
	public Map<String, Object> deleteQuoted(Integer qid, String opUser);

	/**
	 * 查询报价信息
	 * 
	 * @param node
	 * @param string
	 * @return
	 */
	public PageResultDto<ContractQuotationsDto> getQuoted(JsonNode node, String opUser);

	/**
	 * 修改报价
	 * 
	 * @param node
	 * @param string
	 * @return
	 */
	/*public Map<String, Object> updateQuoted(JsonNode node, String opUser);*/
	
	/**
	 * 修改报价
	 * 
	 * @param node
	 * @param string
	 * @return
	 */
	public JsonResult<?> updateQuoted(String str, String opUser);

	/**
	 * 查询报价操作日志
	 * @param qid
	 * @return
	 */
	public List<QuotedOprecord> getOprecord(Integer qid);

	/**
	 * 自动开启报价，每天1次
	 */
	public void autoOpenNotStartQuoted();
	
	/**
	 * 查询对应分销商的报价
	 * @param skus
	 * @param email
	 * @return
	 */
	public List<ContractQuotations> queryQuoted(List<String> skus,String email);

	public JsonResult<?> batchAdd(String string, String adminAccount);

	public JsonResult<?> earlyTermination(Integer id, String adminAccount);

	public JsonResult<?> getQuoted(Integer id);

	/**
	 * 给合同报价的商品设置商品分类-刷数据使用
	 * @return
	 */
	public Map<String, Object> batchSetCategoryId();

}
