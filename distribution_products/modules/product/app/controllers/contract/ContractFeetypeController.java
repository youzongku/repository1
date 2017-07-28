package controllers.contract;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import controllers.annotation.ALogin;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import play.mvc.Result;
import services.product.IContractFeetypeService;
import services.product.impl.UserService;
import java.util.Map;

/**
 * @author longhuashen
 * @since 2017/5/11
 */
@Api(value = "/合同费用项", description = "合同费用项")
public class ContractFeetypeController extends Controller {

    @Inject
    private IContractFeetypeService contractFeetypeService;

    @Inject
    private UserService userService;

    /**
     * 合同费用项
     *
     * @return
     */
    @ApiOperation(value="添加合同费用项",httpMethod="POST",notes="添加合同费用项，带*为必填" ,produces="form")
    @ApiImplicitParams({@ApiImplicitParam(name="body",
            required=false,value="可用参数："
            + "\nname:*费用项名称，\n"
            + "\ntype:*费用类型 1:固定费用值 2：固定费用率\n"
            + "\ndesc:描述",
            paramType="body",dataType="form",
            defaultValue = "{\n\"name\":\"返点\",\n\"type\":1, \n\"desc\":\"remark xxxx\"\n}")})
    @ALogin
    public Result addContractFeetype() {
        Map<String, String> params = Form.form().bindFromRequest().data();
        Logger.info("------------------------------------>addContractFeetype:{}", params);

        if (params == null || !params.containsKey("name") || !params.containsKey("type")) {
            Map<String, Object> result = Maps.newHashMap();
            result.put("suc", false);
            result.put("msg", "请求参数不存在");
            return ok(Json.toJson(result));
        }

        String createUser = userService.getAdminAccount();
        return ok(Json.toJson(contractFeetypeService.addContractFeetype(params, createUser)));
    }

    @ApiOperation(value="分页获取合同费用项",httpMethod="POST",notes="分页获取合同费用项，带*为必填" ,produces="application/json")
    @ApiImplicitParams({@ApiImplicitParam(name="body",
            required=true,value="可用参数："
            + "\nname:费用项名称，\n"
            + "\ncurrPage:*页码，\n"
            + "\npageSize:*pageSize",
            paramType="body",dataType="application/json",
            defaultValue = "{\n\"name\":\"返点\",\n\"currPage\":1,\n\"pageSize\":10\n}")})
    public Result getContractFeetypes() {
        Map<String, Object> result = Maps.newHashMap();
        Map<String, String> params = Form.form().bindFromRequest().data();
        if(null == params){
            Logger.info("请求参数不存在或格式错误");
            result.put("suc", false);
            result.put("msg", "请求参数不存在或格式错误");
            return ok(Json.toJson(result));
        }

        Logger.info("分页获取合同费用项参数为：{}",params);

        Integer currPage = params.containsKey("currPage") ? Integer.valueOf(params.get("currPage")) : 1;
        Integer pageSize = params.containsKey("pageSize") ? Integer.valueOf(params.get("pageSize")) : 10;
        String name = params.containsKey("name") ? params.get("name").trim() : "";

        if (currPage.intValue() <= 0 || pageSize.intValue() <= 0) {
            result.put("suc", false);
            result.put("msg", "请求参数错误,请检查!");
            return ok(Json.toJson(result));
        }

        Map<String, Object> map = Maps.newHashMap();
        map.put("currPage", currPage);
        map.put("pageSize", pageSize);
        map.put("name", name);

        result.put("suc", true);
        result.put("page", contractFeetypeService.getContractFeetypesByPage(map));
        return ok(Json.toJson(result));
    }
}
