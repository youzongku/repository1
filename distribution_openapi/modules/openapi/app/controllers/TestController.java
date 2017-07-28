package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import utils.response.ResponseResultUtil;

public class TestController extends Controller {
	
	public Result suc(){
		return ResponseResultUtil.newSuccessJson("成功");
	}
	
	
	public Result err(){
		return ResponseResultUtil.newErrorJson(303, "参数不正确");
	}
}
