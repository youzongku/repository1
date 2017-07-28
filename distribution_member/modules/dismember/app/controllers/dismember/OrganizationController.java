package controllers.dismember;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.IDisSalesmanService;
import services.dismember.ILoginService;
import services.dismember.IOrganizationService;


@Api(value = "/组织架构", description = "organization")
public class OrganizationController extends Controller{
	
	@Inject 
	private IOrganizationService organizationService;
	
	@Inject
	private IDisSalesmanService disSalesmanService;
	
	@Inject
	private ILoginService loginService;
	
	/**
	 * 查询分销商组织结构
	 * @return
	 */
	public Result initOrganization(){
		if(!loginService.isLogin(2)){
			return internalServerError("用户未登陆。");
		}
		Map<String, String[]> param = request().body().asFormUrlEncoded();
		if(null == param){
			return null;
		}
		
		return ok(Json.toJson(organizationService.getChildOrganizations(param)));
	}
	
	/**
	 * 检查组织结构的名称是否重复
	 */
	public Result checkOrganization(){
		if(!loginService.isLogin(2)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("code", 1);
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if(null == params || !params.containsKey("name")){
			Map<String, Object> result = Maps.newHashMap();
			result.put("code", 2);
			result.put("msg", "请求参数不存在");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(organizationService.getOrganization(params)));
	}

	/**
	 * 添加新区域节点
	 * @return  
	 */
	public Result addOrganization(){
		if(!loginService.isLogin(2)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		Logger.info("----" + Json.toJson(params).toString());
		if (params == null || !params.containsKey("parentid") || !params.containsKey("level")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
            result.put("msg", "请求参数不存在");
            return ok(Json.toJson(result));
        }
		
		return ok(Json.toJson(organizationService.addOrganization(params)));
	}
	
	/**
	 * 更新区域节点
	 */
	public Result updateOrganzition(){
		if(!loginService.isLogin(2)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("id") || !params.containsKey("headerId")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
            result.put("msg", "请求参数不存在");
            return ok(Json.toJson(result));
        }
		
		return ok(Json.toJson(organizationService.updateOrganization(params)));
	}
	
	/**
	 * 删除指定区域节点
	 */
	public Result removeOrganzition(){
		if(!loginService.isLogin(2)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("id") || !params.containsKey("headerId")
				|| !params.containsKey("parentId")) {
			Map<String, Object> result = Maps.newHashMap();
            result.put("suc", false);
            result.put("msg", "请求参数不存在");
            return ok(Json.toJson(result));
        }
		
		return ok(Json.toJson(organizationService.deleteOrganzitionById(params)));
	}
	
	/**
	 * 查询指定组织节点所对应的负责人信息
	 * @return
	 */
	public Result queryHeader(){
		if(!loginService.isLogin(2)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("id")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "请求参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(organizationService.queryHeaderByOrganizationId(params)));
	}
	
	/**
	 * 增加业务员
	 */
	public Result addSalesman(){
		if(!loginService.isLogin(2)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("headerId")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "请求参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(disSalesmanService.addSalesMan(params)));
	}
	
	/**
	 * 查看业务员
	 */
	public Result querySalesmans(){
		if(!loginService.isLogin(2)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "请求参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(disSalesmanService.querySalesmansByCondition(params)));
	}
	
	/**
	 * 编辑业务员
	 */
	
	public Result updateSalesman(){
		if(!loginService.isLogin(2)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("id")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "请求参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(disSalesmanService.updateSalesman(params)));
	}
	
	/**
	 * 删除业务员
	 */
	
	public Result removeSalesMan(){
		if(!loginService.isLogin(2)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("saleid")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "请求参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(disSalesmanService.deleteSalesman(params)));
	}
	
	/**
	 * 得到所有分销商
	 */
	public Result getAllUsers(){
		if (!loginService.isLogin(2)) {
			Map<String, Object> result = Maps.newHashMap();
			Logger.info("当前用户未登录或登录超时");
			result.put("suc", false);
			result.put("code", "2");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if(null == params || !params.containsKey("role")
				|| !params.containsKey("salesmanid")){
			Map<String, Object> result = Maps.newHashMap();
			Logger.info("请求参数不存在或格式错误");
			result.put("suc", false);
			result.put("msg", "请求参数不存在或格式错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(disSalesmanService.getAllUsers(params)));
	}
	
	
	/**
	 * 业务员关联分销商
	 */
	public Result relatedDistributors(){
		if(!loginService.isLogin(2)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("salesmanid") || !params.containsKey("memberids")
				|| params.get("memberids").length() == 0 || !params.containsKey("salesmanErp")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "请求参数错误");
			return ok(Json.toJson(result));
		}
		Logger.info("关联分销商【{}】操作人【{}】,参数【{}】",new Date(),loginService.getLoginContext(2).getEmail(),Json.toJson(params));
		return ok(Json.toJson(disSalesmanService.relatedDistributors(params)));
	}
	
	
	/**
	 * 管理员关联业务员
	 */
	public Result relatedSalesMan(){
		if(!loginService.isLogin(2)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("salesmanid") || !params.containsKey("memberids")
				|| params.get("memberids").length() == 0 ) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "请求参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(disSalesmanService.relatedSalesMan(params)));
	}
	
	/**
	 * 根据条件得到相关业务人员关联的分销商
	 */
	public Result getSalesmanMember(){
		if(!loginService.isLogin(2)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("code", 1);
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("salesmanid")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("code", 2);
			result.put("msg", "请求参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(disSalesmanService.getSalesmanMember(params)));
	}
	
	/**
	 * 根据多个分销商的id查询分销商信息
	 */
	public Result gainMemberByIds(){
		if(!loginService.isLogin(2)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("mids") || !params.containsKey("salesmanid")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "请求参数错误");
			return ok(Json.toJson(result));
		}
		
		return ok(Json.toJson(disSalesmanService.gainMemberByCondition(params)));
	}
	
	/**
	 * 取消关联某个分销商
	 */
	public Result cancleRelated(){
		if(!loginService.isLogin(2)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		Map<String, String> params = Form.form().bindFromRequest().data();
		if (params == null || !params.containsKey("salesmanid") || !params.containsKey("memberid")) {
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "请求参数错误");
			return ok(Json.toJson(result));
		}
		Logger.info("取消关联分销商【{}】操作人【{}】,参数【{}】",new Date(),loginService.getLoginContext(2).getEmail(),Json.toJson(params));
		return ok(Json.toJson(disSalesmanService.removeRelated(params)));
	}
	
	/**
	 * 根据当前登录的email查询关联的分销商
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public Result relatedMember(){
		if(!loginService.isLogin(2)){
			Map<String, Object> result = Maps.newHashMap();
			result.put("suc", false);
			result.put("msg", "用户未登录");
			return ok(Json.toJson(result));
		}
		
		JsonNode node = request().body().asJson();
		return ok(Json.toJson(disSalesmanService.relatedMember(loginService.getLoginContext(2).getEmail(),node)));
	}
	
	/**
	 * 删除关联关系
	 * @author zbc
	 * @since 2016年11月25日 下午8:30:06
	 */
	public Result cancelEmpRelate(Integer salesManId){
		return ok(Json.toJson(disSalesmanService.cancelEmpRelate(salesManId)));
	}
	
	/**
	 * 根据参数获取组织架构关联分销商信息
	 * @author zbc
	 * @since 2017年3月20日 上午10:52:22
	 */
	public Result getInfo(Integer id){
		return ok(Json.toJson(organizationService.getRelate(id)));
	}
	
	/**
	 * 五部获取组织架构数据
	 * {
	 * 	"success":true/false,
	 * 	"data":{
	 * 		"dis_organizational":[],
	 * 		"dis_header":[],
	 * 		"dis_salesman":[],
	 * 		"dis_node_header_mapper":[],
	 * 		"dis_header_salesman_mapper":[]
	 * 	}
	 * }
	 * @return
	 */
	@ApiOperation(
		    value = "获取组织架构信息",
		    notes = "参数必填",
		    nickname = "auth",
		    httpMethod = "GET")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "account", required = true,
		        dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "key", required = true,
        dataType = "string", paramType = "query")
	})
	public Result organizationalData(String account,String key) {
		Logger.info("ERP拉取BBC组织架构信息,[{}],[{}]",account,key);
		return ok(Json.toJson(organizationService.organizationalData(account,key)));
	}
}
