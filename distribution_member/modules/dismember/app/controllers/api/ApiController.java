package controllers.api;


import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ResourceListing;

import controllers.SwaggerBaseApiController;
import play.api.mvc.Request;
import play.api.mvc.RequestHeader;
import play.mvc.Http.Context;
import play.mvc.Result;
import scala.Option;

/**
 * @author zbc
 * 2017年4月13日 上午8:56:10
 */
public class ApiController extends SwaggerBaseApiController {
	
	private static final String LOCALHOST = "tomtopx.com.cn";
	
	public Result  getResources(){
		RequestHeader  request = Context.current()._requestHeader();
		ResourceListing resourceListing = getResourceListing(request); 
		String responseStr = returnXml((Request<?>) request)?
				toXmlString(resourceListing):toJsonString(resourceListing);
		setCharset();
		return new Result() {
			@Override
			public play.api.mvc.Result toScala() {
				return returnValue((Request<?>)request, responseStr);
			}
		};
	}
	public Result  getResource(String path){
		RequestHeader  request = Context.current()._requestHeader();
		Option<ApiListing> apiListing=  getApiListing("/"+path,(Request<?>) request);
		String responseStr = returnXml((Request<?>) request)?
				toXmlString(apiListing):toJsonString(apiListing);
		setCharset();
		return new Result() {
			@Override
			public play.api.mvc.Result toScala() {
				return returnValue((Request<?>)request, responseStr);
			}
		};
	}
	
	/**
	 * 根据环境设置编码
	 * @author zbc
	 * @since 2017年4月21日 上午10:00:30
	 */
	private void setCharset() {
		if(Context.current().request().host().contains(LOCALHOST)){
			Context.current().response().setContentType("application/json;charset=gb2312;");
		}else{
			Context.current().response().setContentType("application/json;charset=UTF-8;");
		}
	}
}
