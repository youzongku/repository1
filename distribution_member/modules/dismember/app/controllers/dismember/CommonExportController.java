package controllers.dismember;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.dismember.ICommonExportService;

@Api(value = "/通用导出功能", description = "account period")
public class CommonExportController extends Controller {
	
	@Inject
	ICommonExportService commonExportService;
	
	@ApiOperation(value = "通用导出", notes = "", nickname = "", httpMethod = "POST")
	@ApiImplicitParam(name = "body", value = "", required = false,
		        dataType = "application/json", paramType = "body")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", value = "", required = true, paramType = "body" 
			,defaultValue = "{\"functionId\": \"exportapbill\",\"billid\": 81}"
				) })
	public Result export(){
		JsonNode reqNode = request().body().asJson();
		Map<String,String> result=Maps.newHashMap();
		if(reqNode==null){
			result.put("result", "1");
			result.put("msg","导出参数不正确！");
			return ok(Json.toJson(result));
		}
		String reqStr=reqNode.toString();
		String resStr= commonExportService.commonExport(reqStr);
		return ok(Json.parse(resStr));
	}
	public Result download(){
		String tempFileName = request().getQueryString("tempFileName");
		String fileName = request().getQueryString("fileName");
		File file=new File("/tmp/"+tempFileName);
		if(!file.exists()){
			return ok("downLoadError");
		}
		try {
			response().setHeader("Content-disposition", "attachment;filename="+new String(fileName.getBytes("utf-8"),"ISO8859_1"));
		} catch (UnsupportedEncodingException e) {
			Logger.info("commondownloadError----------->{}",e);
			return ok("downLoadError");
		}
		return ok(file);
	}
}