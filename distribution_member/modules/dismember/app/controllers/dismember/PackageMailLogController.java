package controllers.dismember;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Map;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import services.dismember.IPackageMailLogService;

/**
 * @author longhuashen
 * @since 2017/5/25
 */
@Api(value="/用户模块",description="user module")
public class PackageMailLogController extends Controller {

    @Inject
    private IPackageMailLogService packageMailLogService;

    @ApiOperation(
            value = "获取运费设置操作日志",
            notes = "id:分销商id",
            nickname = "getPackageMailLogsByMemberId",
            httpMethod = "GET",produces="text/plain"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value="获取运费设置操作日志",required=true,paramType="path",dataType = "Integer")
    })
    public Result getPackageMailLogsByMemberId(Integer id) {
        Logger.info("--------------------------------------------->getPackageMailLogsByMemberId:【{}】", id);
        Map<String, Object> result = Maps.newHashMap();
        if (id == null) {
            result.put("suc", false);
            result.put("msg", "获取运费设置操作日志失败！分销商id不能为空");
        }
        result.put("suc", true);
        result.put("result", packageMailLogService.getPackageMailLogsByMemberId(id));
        return ok(Json.toJson(result));
    }
}
