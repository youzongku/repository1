package controllers.sales;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import dto.JsonResult;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.sales.ISalesContractService;
import util.sales.JsonCaseUtil;

/**
 * 发货单合同控制类
 * @author zbc
 * 2017年5月12日 上午10:19:17
 */
@Api(value="/发货单合同费用",description="contract fee")
public class SalesContractController extends Controller {
	
	@Inject
	private ISalesContractService contractService;

	/*
	 * 费用类型可以新增，必须选择费用类型 
	 * 费用类型（计算公式）： 
	 * 		1、固定费用值 ： 合同均摊费用 = 费用/业绩*报价 
	 * 		（ 合同内预估均摊费用 = 预估费用/预估业绩*报价 合同内实际均摊费用 = 实际费用/实际业绩*报价 ） 
	 * 		2、固定费用率 ： 合同费用 = 报价*费用率 
	 * 费用项类型:
	 * 		1、费用名称( 费用项名称 + 预估费用，费用项名称+实际费用)
	 * 		2、 费用名称 = 费用项名称
	 */
	/**
	 * 保存合同费用
	 * <ol>
	 *  <li>
	 *  	<ul>
	 *     		<li>参数：多个{合同号，pros}
	 *     	
	     { 	
	       "payDate":"2017-05-16 17:58:50",
	       "contracts":[
			  {
			    "contractNo": "HT001", 
			    "pros": [
			      {
			        "sku": "IF639", 
			        "warehouseId": 2024
			      }
			    ]
			  }, 
			  {
			    "contractNo": "HT002", 
			    "pros": [
			      {
			        "sku": "IF639", 
			        "warehouseId": 2024
			      }
			    ]
			  }
			]
		}	
	 *     		<li>返回值：多个费用项,当费用项为0个是，不计算费用
	 *      </ul>   
	 *  </li>
	 * 	<li>计算
	 * 		<ul>
	 * 			<li>匹配合同
	 * 			<li>获取所有费用项
	 * 			<li>动态字段：<br/>
	 * 				根据费用项类型计算<br/>
	 * 				类型1：费用项名称  + 预估费用  =  预估费用/预估业绩*报价<br/> 
	 * 					    费用项名称  + 实际费用 = 实际费用/实际业绩*报价 <br/> 
	 * 				类型2：费用项名称 = 报价*费用率
	 * 			<li>固定字段:<br/>
	 * 				合同内订单预估费用合计
	 *              整笔订单预估费用合计
	 *              合同内订单实际费用合计
	 *              整笔订单实际费用合计
	 * 		</ul>
	 * 	</li>
	 *  <li>保存
	 *  </li>
	 *  </ol>
	 * @author zbc
	 * @since 2017年5月11日 下午6:16:27
	 * @return
	 */
	public Result saveContractFee(Integer sid){
		return ok(Json.toJson(contractService.caculate(sid)));
	}
	
	/**
	 * 查询合同费用
	 * @author zbc
	 * @since 2017年5月11日 下午6:18:54
	 * @return
	 */
//	@ALogin
	@ApiOperation(value="订单费用明细查询",httpMethod="POST",
			notes="该接口返回值:response<br/>"
					+ "分页对象:response.data<br/>"
					+ "response.data.currPage:当前页<br/>"
					+ "response.data.totalPage:页长度<br/>"
					+ "response.data.totalCount:总记录数<br/>"
					+ "response.data.data:返回值对象集合(取值方式：调用/sales/contract/getFields/{cno}中的name为字段名称)<br/>",
			response=JsonResult.class)
	@ApiImplicitParams(
		{
			@ApiImplicitParam(name = "body", required = true, value = "分页查询:contractNo,page,rows 必传", dataType = "dto.sales.ContractFeeSearch", paramType = "body",
					defaultValue = "{\n" + "\"page\": 1,\n" + "\"rows\": 10, \n"
							+ "\"contractNo\": \"HT2017050915114300000256\"\n" + "}"
			)
		}
	)
	public Result readContractFee(){
		JsonNode node = request().body().asJson();
		if(!JsonCaseUtil.checkParam(node,"contractNo","page","rows")){
			return ok(Json.toJson(JsonResult.newIns().result(false).msg("参数错误")));
		}
		return ok(Json.toJson(contractService.pages(node.toString())));
	}
	
	/**
	 * 获取字段名称
	 * @author zbc
	 * @since 2017年5月15日 上午11:57:06
	 * @param cno
	 * @return
	 */
	@ApiOperation(value="获取订单合同费用列名",httpMethod="GET",notes="返回值为数组:<br/>columnName：列名称<br/>name：字段名称<br/>index：排序字段名称")
	@ApiImplicitParams(
		{
			@ApiImplicitParam(name="cno",required=true,dataType="string",paramType="path",defaultValue="HT2017050915114300000256")
		}
	)
	public Result getFieldNames(String cno){
		return ok(Json.toJson(contractService.fieldNames(cno)));
	}
	
	/**
	 * 
		{
		  "cno": "HT001", 
		  "startTime": "2017-05-01 00:00:01", 
		  "endTime": "2017-05-31 23:59:59"
		}
	 * @author zbc
	 * @since 2017年5月16日 下午6:02:01
	 * @return
	 */
	public Result refresh(){
		JsonNode node = request().body().asJson();
		Logger.info("更新费用项刷新费用参数:{}",node);
		return ok(Json.toJson(contractService.refresh(node.toString())));
	}
	
	/**
	 * 
	 * @author zbc
	 * @since 2017年5月19日 上午10:44:25
	 * @param cno
	 * @return
	 */
	@ApiOperation(value="获取合同费用合计",notes="contractActualTotal:合同实际费用合计<br/>contractEstimatedTotal:合同实际费用合计",httpMethod="GET")
	@ApiImplicitParams({
		@ApiImplicitParam(name="cno",value="合同号",defaultValue="HT2017050915114300000256",dataType="string",paramType="path",required=true)
	})
	public Result contractFee(String  cno){
		return ok(Json.toJson(contractService.contractFee(cno)));
	}
	
	
}
