package controllers.contract;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.annotation.ALogin;
import dto.JsonResult;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.product.IContractChargesService;
import services.product.IUserService;
import util.product.JsonCaseUtil;

/**
 * 合同费用控制类
 * @author zbc
 * 2017年3月25日 上午11:44:47
 */
@Api(value="/合同费用管理",description="Contract Charges")
public class ChargesManagerController extends Controller {
	
	@Inject
	private IUserService userService;
	@Inject
	private IContractChargesService chargesService;
	
	@ALogin
	public Result typeGet(){
		return ok(Json.toJson(chargesService.getTypes()));
	}

	/**
	 * 费用新增
	 * 参数：
	 * {
	    "cno":"HT2017032410345200000023",
	    "typeId":1,
	    "scaleOfCharges":0.12,
	    "startTime":"2017-03-25 12:00:00",
	    "endTime":"2017-03-26 12:00:00",
	    "remark":"remark"
	  	}
	 * @author zbc
	 * @since 2017年3月25日 下午2:23:24
	 */
	@ApiOperation(value="新增合同费用",httpMethod="POST",
			notes="<b>该接口有后台登录校验</b><br/>参数去掉startTime,endTime 其他保持不变<br/>成功返回值:{\"suc\":true,\"msg\":\"xxxx\"}<br/>失败返回值:{\"suc\":false,\"msg\":\"xxxx\"}")
	@ApiImplicitParams(
		{
			@ApiImplicitParam(name="body",required=true,
					value="cno:合同号，必传<br/>typeId:类型id,必传<br/>scaleOfCharges:费用率,必传<br/>remark:备注,非必传"
					,defaultValue=
						"{\n"+
					    "\"cno\":\"HT2017032410345200000023\",\n"+
					    "\"typeId\":1,\n"+
					    "\"scaleOfCharges\":0.12,\n"+
					    "\"remark\":\"remark\"\n"+
					  	"}"
					,dataType="string",paramType="body"    
					)
		}
	)
	@ALogin
	public Result create(){
		JsonNode json =request().body().asJson();
		String admin = userService.getAdminAccount();
		Logger.info("[{}]合同费用创建参数:[{}]",admin,json);
		if(!JsonCaseUtil.checkParam(json, 
				new String[]{"cno","typeId","scaleOfCharges"})){
			Map<String,Object> res = Maps.newHashMap();
			res.put("suc", false);
			res.put("msg","参数错误");
			return ok(Json.toJson(res));
		}
		return ok(Json.toJson(chargesService.create(json.toString(),admin)));
	}
	
	/**
	 * 更新费用信息
		{
	    "id":2,
	    "scaleOfCharges":0.12,
	    "startTime":"2017-03-25 12:00:00",
	    "endTime":"2017-03-26 12:00:00",
	    "remark":"remark"
	  	}
	 * @author zbc
	 * @since 2017年3月27日 上午10:17:27
	 */
	@ApiOperation(value="更新合同费用",httpMethod="POST",
			notes="<b>该接口有后台登录校验</b><br/>参数去掉startTime,endTime 其他保持不变<br/>成功返回值:{\"suc\":true,\"msg\":\"xxxx\"}<br/>失败返回值:{\"suc\":false,\"msg\":\"xxxx\"}")
	@ApiImplicitParams(
		{
			@ApiImplicitParam(name="body",required=true,
					value="id:合同id，必传<br/>scaleOfCharges:费用率,必传<br/>remark:备注,非必传"
					,defaultValue=
						"{\n"+
					    "\"id\":2,\n"+
					    "\"scaleOfCharges\":0.12,\n"+
					    "\"remark\":\"remark\"\n"+
					  	"}"
					,dataType="string",paramType="body"    
					)
		}
	)
	@ALogin
	public Result update(){
		JsonNode json =request().body().asJson();
		String admin = userService.getAdminAccount();
		Logger.info("[{}]合同费用更新参数:[{}]",admin,json);
		if(!JsonCaseUtil.checkParam(json, 
				new String[]{"id","scaleOfCharges"})){
			Map<String,Object> res = Maps.newHashMap();
			res.put("suc", false);
			res.put("msg","参数错误");
			return ok(Json.toJson(res));
		}
		return ok(Json.toJson(chargesService.update(json.toString(),admin)));
	}
	
	/**
	 * 获取费用信息
	 * @param id  
	 * @author zbc
	 * @since 2017年3月27日 上午10:37:27
	 */
	@ALogin
	public Result get(Integer id){
		return ok(Json.toJson(chargesService.get(id)));
	}
	
	/**
	 * 操作日志查询
	 * @author zbc
	 * @since 2017年3月27日 上午11:40:57
	 */
	@ALogin
	public Result oprecord(Integer id){
		return ok(Json.toJson(chargesService.oprecord(id)));
	}
	
	/**
	 * 分页查询
	 * @author zbc
	 * @since 2017年3月27日 上午11:44:44
	 */
	public Result pages(){
		JsonNode json =request().body().asJson();
		Logger.info("合同费用分页查询参数:[{}]",json);
		if(json == null){
			Map<String,Object> res = Maps.newHashMap();
			res.put("suc", false);
			res.put("msg","参数错误");
			return ok(Json.toJson(res));
		}
		
		return ok(Json.toJson(chargesService.pageSearch(json.toString())));
	}
	
	/**
	 * 根据合同号 支付时间取费用率
	  {
	   "chargeMapList":[
		   {
			   "contractNo":"HT2017032410345200000023",
			   "sum":69.6
		   }
	   ],
	   "salesOrderNo":"XS001", 
	   "payDate":"2017-03-25 15:00:00"
	  }
	 *  
	 * @author zbc
	 * @since 2017年3月27日 下午5:13:46
	 */
	public Result matchCharge(){
		JsonNode json = request().body().asJson();
		boolean checkResult = JsonCaseUtil.checkParam(json, new String[]{"chargeMapList","salesOrderNo","payDate"});
		if(!checkResult){
			Map<String,Object> res = Maps.newHashMap();
			res.put("suc", false);
			res.put("msg", "参数错误");
			return ok(Json.toJson(res));
		}
		
		return ok(Json.toJson(chargesService.match(json.toString())));
	}
	
	/**
	 * 删除合同费用
	 * @author zbc
	 * @since 2017年5月4日 上午10:13:06
	 * @param id
	 * @return
	 */
	@ApiOperation(value="删除合同费用",httpMethod="GET",notes="<b>后台登录校验</b><br>可删除未开始状态下的合同费用",response=JsonResult.class)
	@ApiImplicitParams(
		{
			@ApiImplicitParam(name="cid",required=true,value="合同费用id",dataType="integer",paramType="path",defaultValue="2")
		}
	)
	@ALogin
	public Result delete(Integer id){
		String admin = userService.getAdminAccount();
		Logger.info("[{}]删除合同费用[{}]",admin,id);
		return ok(Json.toJson(chargesService.delete(id,admin)));
	}
	
	/**
	 * 提前结束
	 * @author zbc
	 * @since 2017年5月4日 下午12:56:30
	 * @param id
	 * @return
	 */
	@ApiOperation(value="提前结束合同费用",httpMethod="GET",notes="<b>后台登录校验</b><br>已开始合同费用可以提前结束",response=JsonResult.class)
	@ApiImplicitParams(
		{
			@ApiImplicitParam(name="cid",required=true,value="合同费用id",dataType="integer",paramType="path",defaultValue="2")
		}
	)
	public Result earlyTermination(Integer id){
		String admin = userService.getAdminAccount();
		Logger.info("[{}]提前结束合同费用[{}]",admin,id);
		return ok(Json.toJson(chargesService.earlyTermination(id,admin)));
	}
	
}
