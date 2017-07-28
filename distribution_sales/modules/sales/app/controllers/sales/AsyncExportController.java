package controllers.sales;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.annotation.ALogin;
import controllers.annotation.DivisionMember;
import dto.sales.AsyncExportDto;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import services.sales.IAsyncExportService;
import services.sales.IUserService;
import util.sales.Constant;
import util.sales.StringUtils;

/**
 * 异步导出控制类
 * @author zbc
 * 2017年6月23日 上午9:48:36
 */
@Api(value="/异步导出控制类型",description="Async Export")
public class AsyncExportController extends Controller {
	
	@Inject private IAsyncExportService asyncExportService;
	@Inject private IUserService userService;
	
	/**
	 * 创建excel
	 * 1.发起请求
	 * 2.根据参数查询数据 
	 * 3.异步创建excel 文件
	 * 4.保存excel文件到服务器上
	 * @author zbc
	 * @since 2017年6月26日 上午9:45:14
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ApiOperation(value="异步生成发货单excel",httpMethod="GET",notes="<b>后台登录校验</b><br/>本接口替换原先: /manager/exportSaleOrder,改为异步生产excel文件 ",response=String.class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="orderStartDate",value="开始时间:yyyy-dd-mm",dataType="string",paramType="query"),
		@ApiImplicitParam(name="orderEndDate",value="结束时间:yyyy-dd-mm",dataType="string",paramType="query"),
		@ApiImplicitParam(name="status",value="订单状态",dataType="string",paramType="query"),
		@ApiImplicitParam(name="seachSpan",value="模糊搜索条件",dataType="string",paramType="query"),
		@ApiImplicitParam(name="warehouseId",value="仓库id",dataType="integer",paramType="query"),
		@ApiImplicitParam(name="distributorType",value="分销商类型",dataType="integer",paramType="query"),
		@ApiImplicitParam(name="source",value="来源",dataType="string",paramType="query"),
		@ApiImplicitParam(name="sort",value="规则:\"asc\",\"desc\"",dataType="string",paramType="query"),
		@ApiImplicitParam(name="sort_field",value="排序字段",dataType="string",paramType="query")
	})
	@DivisionMember
	@ALogin
	public Result create() throws UnsupportedEncodingException{
		Map<String, String[]> map = request().queryString();
		
		Map<String, String> exportSaleOrderMap = Constant.EXPORT_SALE_ORDER_MAP;
		Set<String> strings = exportSaleOrderMap.keySet();
		Iterator<String> iterator = strings.iterator();
		List<String> newHeader = Lists.newArrayList();
		while (iterator.hasNext()) {
			newHeader.add(iterator.next());
		}
		String[] headerString = new String[newHeader.size()];
		newHeader.toArray(headerString);
		String account = userService.getRelateAccounts();
		List<String> accounts = null;
		if (!StringUtils.isBlankOrNull(account)) {
			accounts = Arrays.asList(account.split(","));
		}
		Logger.info("{}:发货单导出导出====",userService.getAdminAccount());
		return ok(asyncExportService.createFile(map,new AsyncExportDto(
				userService.getAdminAccount(), request().remoteAddress(), 
				accounts, "销售发货单.xlsx","saleOrderExport")
				,headerString, Constant.EXPORT_SALE_ORDER_MAP,accounts));
	}

	/**
	 * 下载excel文件
	 * 1.下载文件
	 * 2.异步删除该文件
	 * @author zbc
	 * @since 2017年6月26日 上午9:47:12
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@ALogin
	@ApiOperation(value="下载发货单excel文件",notes="<b>后台登录校验</b>",httpMethod="GET",produces="application/vnd.ms-excel;charset=utf-8",response=File.class)
	@ApiImplicitParams({
		
	})
	public Result dowload() throws UnsupportedEncodingException{
		Logger.info("====下载文件====");
		AsyncExportDto dto = new AsyncExportDto(userService.getAdminAccount(), null, null, null,"saleOrderExport");
		File file = asyncExportService.dowloadFile(dto);
		if(file == null){
			return notFound("文件正在生成中，请稍后..");
		}
		response().setHeader("Content-disposition",
						"attachment;filename=" + new String("销售发货单".getBytes(), "ISO8859-1") + ".xlsx");
		response().setContentType("application/vnd.ms-excel;charset=utf-8");
		return ok(file);
	}
	
}
