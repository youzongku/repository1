var layer = undefined;
var laypage = undefined;
var orderTemplate = undefined;

function getList_all_sale() {
	$("#sale_order_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
}

function getList_wait_pay_sale() {
	$("#waitpay_sales_order_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
}

//销售发货——发货单 初始化
function init_allSale_sales_order(lay,layd,BbcGrid,oTemplate,categoryTitle){
	$('#sales_order_category_title').html(categoryTitle);
	layer = lay;
	laypage = layd;
	orderTemplate = oTemplate;
	//初始化仓库下拉选
	initiate_warehouse_select();
	var grid = new BbcGrid();
	grid.initTable($("#sale_order_table"),getSetting_sales_order());
	$("#orderStatus,#delivery_warehouse_select,#distributorType,#source,#sale_pageSize").change(function(){
		//初始化发货单列表
		$("#sale_order_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
	});
	$(".searchButton").click(function(){
		$("#sale_order_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
	});
}
//初始化待付款订单页面
function init_waitpay_sale_new(lay,layd,BbcGrid,oTemplate){
	layer = lay;
	laypage = layd;
	orderTemplate = oTemplate;
	//初始化仓库下拉选
	initiate_warehouse_select();
	//初始化模式列表
	init_getMode();
	//初始化发货单列表
	var grid = new BbcGrid();
	grid.initTable($("#waitpay_sales_order_table"),getSetting_waitpay_sale());

	$("#delivery_warehouse_select,#disMode,#source,#sale_pageSize").change(function(){
		//初始化发货单列表
		$("#waitpay_sales_order_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
	});
	$(".searchButton").click(function(){
		$("#waitpay_sales_order_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
	});
}

function init_getMode() {
	// 获取mode
	ajax_get("/member/getMode", null, null, function (data) {
		if (data) {
			$("#disMode").empty();
			var mode_html = "<option value='0'>所有渠道</option>";
			for (var i in data) {
				mode_html +="<option value='"+data[i].id+"'>"+data[i].disMode+"</option>";
			}
			$("#disMode").html(mode_html);
		}
	});
}

/*修改价格方法*/
function change_price(obj){
	var sNo = $(obj).data("sno");
	var status = $(obj).data("status");
	var pNo = $(obj).data("pno");
	var cHtml = '<div class="sales_change_price">';
	var height = "",total;
	//获取单号
	switch(status){
		case 103:
			height = "240px";
			ajax_post_sync("/sales/manager/showAllSalesOrder", JSON.stringify({"seachSpan": sNo}), "application/json",
				function(data){
					if(data.result){
						var order = data.saleOrderInfos[0];
						var base = order.saleBase;
						var couponsAmount = base.couponsAmount;
						var bbcPostage = base.bbcPostage;
						total =  bbcPostage - (couponsAmount?couponsAmount:0);
						total = total>0 ? total : 0;
						cHtml +=
							'<p>当前待付款金额:&nbsp;<span style="margin-left: 10px;margin-right: 10px;width: auto;" class="red" id="needPay">'+parseFloat(total).toFixed(2)+'</span>元</p>'+
							'<p>付款金额=待付款商品总计+运费-优惠码优惠-整单优惠</p>'+
							'<p><span>&nbsp;运费调整为：</span><input name="bbcPostage" value="'+bbcPostage+'" id="postFee"/></p>';
					}
				}
			);
			break;
		case 1:
			height = "280px";
			ajax_post_sync("/purchase/getOrderById", JSON.stringify({"pNo": pNo}), "application/json",
				function(data){
					if(data.returnMess.errorCode == 0){
						var order = data.orders[0];
						var bbcPostage = order.bbcPostage;
						var purchaseTotalAmount = order.purchaseTotalAmount;
						var couponsAmount = order.couponsAmount;
						total =  purchaseTotalAmount - (couponsAmount?couponsAmount:0)+(bbcPostage?bbcPostage:0);
						total = total>0?total:0;
						cHtml +=
							'<p>当前待付款金额:&nbsp;<span style="margin-left: 10px;margin-right: 10px;width: auto;" class="red" id="needPay">'+parseFloat(total).toFixed(2)+'</span>元</p>'+
							'<p>付款金额=待付款商品总计+运费-优惠码优惠-整单优惠</p>'+
							'<p><span>&nbsp;运费调整为：</span><input type="text" name="bbcPostage" value="'+(bbcPostage?bbcPostage:"")+'" id="postFee"/></p>'+
							'<p><span>&nbsp;整单优惠：</span><input type="text" name="reducePrice" id="reducePrice" /></p>';
					}else{
						layer.msg(data.errorInfo,{icon:5,time:2000});
					}
				}
			);
			break;
	}
	cHtml += '</div>';

	layer.open({
		type: 1,
		skin: 'layui-layer-rim', //加上边框
		title: '修改金额',
		area: ['420px', height], //宽高
		btn: ['确定', '取消'],
		content: cHtml,
		success:function(i,currdom){
			$(".sales_change_price input").blur(function(){
				var price = $(this).val();
				if(price != "" && !checkPrice(price)){
					layer.msg("请输入有效金额",{icon:5,time:2000});
				}
			});
		},
		yes:function(index){
			var param = {};
			var valid = true;
			$.each($(".sales_change_price input"),function(i,node){
				var price = $(node).val();
				var key = $(node).attr("name");
				if(price == ""){
					return true;
				}
				if(checkPrice(price)){
					param[key] = price;
				}else{
					valid = false;
				}
			});
			if(!valid){
				layer.msg("请输入有效金额",{icon:5,time:2000});
				return;
			}
			if(Object.keys(param).length>0){
				param['sno']= sNo;
				ajax_post("/sales/manager/changeOrderPrice", JSON.stringify(param), "application/json",
					function(data){
						if(data.suc){
							layer.msg(data.msg,{icon:6,time:2000},function(){
								layer.close(index);
								$("#waitpay_sales_order_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
							});
						}else{
							layer.msg(data.msg,{icon:5,time:2000});
						}
					}
				);
			}
		}
	});
}

function checkPrice (price) {
	var regMoney = /^(0|[1-9][0-9]*)(.[0-9]{1,2})?$/;
	return regMoney.test(price);
}

function getSetting_sales_order() {
	var setting = {
		url:"/sales/manager/showAllSalesOrder",
		ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
		rownumbers : true, // 是否显示前面的行号
		datatype : "json", // 返回的数据类型
		mtype : "post", // 提交方式
		height : "auto", // 表格宽度
		autowidth : true, // 是否自动调整宽度
		// styleUI: 'Bootstrap',
		colNames:["id","订单编号","操作费","下单时间","用户确认时间","发货时间","确认收货时间/取消时间","订单状态","erp状态","业务员","订单收货人","名称", "分销商", "分销商类型", "发货仓库", "录入人", "相关操作"],
		colModel:[{name:"saleMain.id",index:"id",width:"12%",align:"center",sortable:true,hidden:true},
			{name:"saleMain.salesOrderNo",index:"sales_order_no",width:"12%",align:"center",sortable:false},
			{name:"saleMain.optFee",index:"opt_fee",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
				return parseFloat(cellvalue).toFixed(2);
			}},
			{name:"saleMain.orderingDate",index:"ordering_date",width:"14%",align:"center",sortable:true},
			{name:"saleMain.orderTransDate",index:"orderTransDate",width:"14%",align:"center",sortable:false,hidden:true},
			{name:"saleMain.orderSendDate",index:"orderSendDate",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleMain.confirmReceiptDate",index:"confirmReceiptDate",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleMain.statusDesc",index:"status",width:"12%",align:"center",sortable:false},
			{name:"saleMain.erpStatus",index:"erp_status",width:"12%",align:"center",sortable:false,formatter:function(cellValue, options, rowObject){
				return formatErpStatus(cellValue);
			}},
			{name:"saleBase.customerservice",index:"stock",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleBase.receiver",index:"warehouse_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleMain.nickName",index:"nick_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleMain.email",index:"email",width:"12%",align:"center",sortable:false},
			{name:"saleMain.distributorTypeStr",index:"distributor_type",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleMain.warehouseName",index:"warehouse_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleMain.email",index:"email",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return rowObject.saleBase.createUser == null ? rowObject.saleMain.email : rowObject.saleBase.createUser;
			}},
			{name:"saleMain.status",index:"detail",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
				var operateHtml = "<li>" +
					"<span onclick=\"operateRecord('"+rowObject.saleMain.id+"')\">查看日志</span></li><li></li>";

				if(cellvalue==4 || cellvalue==8){//审核不通过，发货失败
					operateHtml = "<li><span onclick=\"operateRecord('"+rowObject.saleMain.id+"')\">查看日志</span></li>" +
						"<li><span onclick=\"reason('1','"+rowObject.saleMain.id+"')\">查看原因</span></li>";
				}else if(cellvalue==6 || cellvalue==7){//审核通过，待发货
					operateHtml = "<li><span onclick=\"operateRecord('"+rowObject.saleMain.id+"')\">查看日志</span></li><li></li>";
				}else if(cellvalue==9 || cellvalue==10){//待收货，已收货
					operateHtml = "<li><span onclick=\"operateRecord('"+rowObject.saleMain.id+"')\">查看日志</span></li>" +
						"<li><span onclick=\"reason('3','"+rowObject.saleMain.salesOrderNo+"')\">物流查询</span></li>";
				}

				// 满足条件：非代付款状态 & BBC录入 & 深圳仓的
				if('BBC' == rowObject.saleMain.source && rowObject.saleMain.warehouseId==2024){
					operateHtml += "<li><span salesOrderNo='"+rowObject.saleMain.salesOrderNo+"' onclick='copySaleOrder(this)'>复制</span></li>";
				}

				return "<div class='about_d'" + operateHtml + "</div>";
			}}
		],
		viewrecords : true,
		rowNum : 10,
		rowList : [ 10, 20, 30 ],
		pager:"#sale_order_pagination",//分页
		caption:"发货单(双击行，可查看发货单详情)",//表名称
		pagerpos : "center",
		pgbuttons : true,
		autowidth: true,
		rownumbers: true, // 显示行号
		loadtext: "加载中...",
		pgtext : "当前页 {0} 一共{1}页",
		jsonReader:{
			root: "saleOrderInfos",  //数据模型
			page: "pageCount",//数据页码
			total: "pages",//数据总页码
			records: "total",//数据总记录数
			repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
			//cell: "cell",//root 中row 行
			id: "id"//唯一标识
		},
		ondblClickRow: function(rowid) {
			var rowData = $("#sale_order_table").jqGrid('getRowData',rowid);
			doShowSaleOrderDetail(rowData,true)
		},
		serializeGridData : function() {
			return JSON.stringify(getSearchParam($(this).jqGrid('getGridParam', 'postData')))
		}
	};
	return setting;
}

function getSetting_waitpay_sale() {
	var setting = {
		url:"/sales/manager/showAllSalesOrder",
		ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
		rownumbers : true, // 是否显示前面的行号
		datatype : "json", // 返回的数据类型
		mtype : "post", // 提交方式
		height : "auto", // 表格宽度
		autowidth : true, // 是否自动调整宽度
		// styleUI: 'Bootstrap',
		colNames:["id","订单编号","下单时间","发货仓库","订单状态","分销渠道","分销商类型","业务员","订单收货人","名称", "分销商",  "录入人", "相关操作"],
		colModel:[{name:"saleMain.id",index:"saleMain.id",width:"12%",align:"center",sortable:false,hidden:true},
			{name:"saleMain.salesOrderNo",index:"sales_order_no",width:"12%",align:"center",sortable:false},
			{name:"saleMain.orderingDateStr",index:"ordering_date",width:"14%",align:"center",sortable:true,sorttype:'desc'},
			{name:"saleMain.warehouseName",index:"warehouse_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleMain.statusDesc",index:"status",width:"12%",align:"center",sortable:false},
			{name:"saleMain.disModeDesc",index:"dis_mode",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleMain.distributorTypeStr",index:"distributor_type",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleBase.customerservice",index:"customerservice",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleBase.receiver",index:"warehouse_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleMain.nickName",index:"nick_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleMain.email",index:"email",width:"12%",align:"center",sortable:false},
			{name:"saleMain.email",index:"email",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return rowObject.saleBase.createUser == null ? rowObject.saleMain.email : rowObject.saleBase.createUser;
			}},
			{name:"saleMain.status",index:"detail",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
				var operateHtml = "<li><span data-pno='"+rowObject.saleMain.purchaseOrderNo+"' data-sno='"+rowObject.saleMain.salesOrderNo+"' " +
					"data-status='"+rowObject.saleMain.status+"'  onclick='change_price(this)'>修改金额</span></li>"+
					"<li><span onclick=\"operateRecord('"+rowObject.saleMain.id+"')\">查看日志</span></li>";
				return "<div class=\"about_d\"" + operateHtml + "</div>";
			}}
		],
		viewrecords : true,
		rowNum : 10,
		rowList : [ 10, 20, 30 ],
		pager:"#waitpay_sales_order_pagination",//分页
		caption:"待付款订单(双击行，可查看发货单详情)",//表名称
		pagerpos : "center",
		pgbuttons : true,
		autowidth: true,
		rownumbers: true, // 显示行号
		loadtext: "加载中...",
		pgtext : "当前页 {0} 一共{1}页",
		jsonReader:{
			root: "saleOrderInfos",  //数据模型
			page: "pageCount",//数据页码
			total: "pages",//数据总页码
			records: "total",//数据总记录数
			repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
			//cell: "cell",//root 中row 行
			id: "id"//唯一标识
		},
		ondblClickRow: function(rowid) {
			var rowData = $("#waitpay_sales_order_table").jqGrid('getRowData',rowid);
			doShowSaleOrderDetail(rowData,true)
		},
		serializeGridData : function(postData) {
			return JSON.stringify(getSearchParam($(this).jqGrid('getGridParam', 'postData')))
		}
	};
	return setting;
}

/**
 * 获取销售发货-发货单查询参数
 */
function getSearchParam(postData) {
	return {
		status: $("#orderStatus").val(),
		orderStartDate: $("#seachTime0").val(),
		orderEndDate: $("#seachTime1").val(),
		warehouseId: $("#delivery_warehouse_select").val(),
		seachSpan: $("#searchInput").val().trim(),
		distributorType: $("#distributorType").val(),
		source: $("#source").val(),
		disMode: $("#disMode").val(),
		pageCount: $('#sale_order_table').getGridParam('page'),
		pageSize: $('#sale_order_table').getGridParam('rowNum'),
		sidx:postData.sidx,
		sort:postData.sord == 'desc' ? 'asc' : 'desc'
	};
}

//格式化时间戳
function formateDate(time){
	var da=new Date(time);
	var year = da.getFullYear();
	var month = da.getMonth()+1;
	var date = da.getDate();
	var h = da.getHours();
	var mm = da.getMinutes();
	var s = da.getSeconds();
	return year+'-'+month+'-'+date+' '+h+':'+mm+':'+s;
}

// 复制
function copySaleOrder(obj) {
	var salesOrderNo = $(obj).attr("salesOrderNo")
	var position = $('p[class="back-current"]').attr("position")
	var new_pro_act_menu = $("p[position='62']")
	$("p[onclick^='load_menu_content']").removeClass("back-current");
	new_pro_act_menu.addClass("back-current");

	// 判断是否要选择到期日期
	layer.confirm("注：如果该营销单包含商品到期日期信息，复制营销单后系统将不会复制相关商品到期日期信息，需要重新选择商品并选择到期日期！", {
		btn: ['不需要选择到期日期', '选择到期日期'] //按钮
	}, function () {
		// 不需要选择到期日期
		load_menu_content(parseInt(new_pro_act_menu.attr("position")),{'edit_so_type':'edit_so','needExpirationDate':false,'salesOrderNo':salesOrderNo});
	}, function () {
		// 选择到期日期
		load_menu_content(parseInt(new_pro_act_menu.attr("position")),{'edit_so_type':'edit_so','needExpirationDate':true,'salesOrderNo':salesOrderNo});
	});
}

//公共方法
function showAllSalesOrder(obj,red,type,callback){
	isaulogin(function(email){
		var pageCount = "1";
		if (red) {
			pageCount = obj.curr;
		}
		var param = {
			pageSize: $("#sale_pageSize").val(),
			pageCount: pageCount,
			status: $("#orderStatus").val(),
			orderStartDate: $("#seachTime0").val(),
			orderEndDate: $("#seachTime1").val(),
			warehouseId: $("#delivery_warehouse_select").val(),
			seachSpan: $("#searchInput").val().trim(),
			distributorType : $("#distributorType").val(),
			source:$("#source").val(),
			disMode:$("#disMode").val()
		};
		ajax_post("/sales/manager/showAllSalesOrder", JSON.stringify(param), "application/json",
			function(data){
				callback(data);
			}
		);
	});
}

//后台销售发货订单信息导出
function exportSaleOrder(){
	var headName = [];
	var heads = ['email','nickName','saleOrderNO', 'orderDateStr', 'status', 'disAccount', 'distributorType', 'warehouseName',
		'shopName', 'platformOrderNo', 'tradeNo', 'receiver', 'tel', 'receiverIDcard', 'createUser', 'address', 'sku',
		'productName', 'finalSellingPrice', 'qty', 'orderActualAmount', 'orderActualPayAmount', 'bbcPostage', 'remark']
	$.each(heads,function(i,item){
		headName.push("header=" + item);
	});
	headName.push("orderStartDate=" + $("#seachTime0").val());
	headName.push("orderEndDate=" + $("#seachTime1").val());
	headName.push("status=" + $("#orderStatus").val());
	headName.push("seachSpan="+$("#searchInput").val());
	headName.push("warehouseId=" + $("#delivery_warehouse_select").val());
	headName.push("distributorType="+$("#distributorType").val());
	headName.push("source="+$("#source").val());
	if(headName.length > 0 ){
		window.location.href = "/sales/manager/exportSaleOrder?" + headName.join("&");
	}
}
//获取支付方式
function getPamentType(payment_type){
	return 	payment_type == "alipay" ? "支付宝" :
		payment_type == "wechatpay" ? "微信支付" :
			payment_type == "yijifu" ? "易极付" :
				payment_type == "system" ? "余额支付" :
					payment_type == "balance" ? "余额支付" :
						payment_type == "yjf_wx" ? "易极付微信扫码支付" :payment_type;
}

$(document).ready(function(){
	// 订单全选
	$('.checkbox-top').click(function(){
		if($(this).is(':checked')){
			$(".checkbox-child").each(function(){
				$(this).attr("checked", "checked");
			});
		} else {
			$(".checkbox-child").each(function(){
				$(this).removeAttr("checked");
			});
		}
	});
});

//订单全选
function sale_select_all(node) {
	$(".checkbox-child").each(function() {
		this.checked = node.checked;
	});
}

//初始化仓库下拉选
function initiate_warehouse_select() {
	var selectId = $("#delivery_warehouse_select").val();
	ajax_get("/inventory/queryWarehouse?" + Math.random(), "", "",
		function(data) {
			if (data.length > 0) {
				var itemHTML = '<option value="">所有仓库</option>';
				$.each(data, function(i, item) {
					if(selectId != "" && selectId == item.id){
						itemHTML += '<option selected="selected" value="' + item.id + '">' + item.warehouseName + '</option>';
					} else{
						itemHTML += '<option value="' + item.id + '">' + item.warehouseName + '</option>';
					}
				});
				$("#delivery_warehouse_select").empty().append(itemHTML);
			} else {
				layer.msg("获取仓库信息失败", {icon: 2, time: 2000});
			}
		}
	);
}
function clearDetail(node1,node2,node3) {
	$("#mOrderActualAmount").parent().hide();
	$("#disPrimeCost").parent().hide();
	node1.html("");
	node2.empty();
	node3.empty();
}

//展示发货单详情
function doShowSaleOrderDetail(rowData,isAllOrder){
	layer.open({
		type: 1,
		title: rowData['saleMain.salesOrderNo'],
		content: $("#sale_order_detail_div"),
		area: ['1100px', '510px'],
		btn: ["关闭"],
		closeBtn: 1,
		shadeClose: true,
		move: false,
		btn1: function (index) {
			layer.close(index)
		},
		success : function(layero, index) {
			clearDetail($("#sale_order_detail_div em"),$("#purchase_detail_info"),$("#sales_detail_info"));
			isaulogin(function(email){
				if("0"==email){
					window.location.href = "login.html";
				}else{
					var orderId = rowData['saleMain.id'];
					ajax_post("/sales/showOrderDetail", JSON.stringify({orderId: orderId}), "application/json",
						function(data){
							if(data.result == true){
								var base = data.saleOrderInfo.saleBase;
								var main = data.saleOrderInfo.saleMain;
								var details = data.saleOrderInfo.saleDetails;
								var orderTotal = 0;
								//平台店铺
								ajax_post("/member/getstoreById", JSON.stringify({"sid":base.shopId}), "application/json",
									function(getstoreRes) {
										//拼接html
										var totalQty = 0;//商品总数
										var sales_order_detail = "";//订单详情
										$.each(details, function (i,item) {
											totalQty += item.qty;
											// 取真实单价
											var subTotal = (item.finalSellingPrice * item.qty).toFixed(2);
											orderTotal += item.finalSellingPrice * item.qty;
											sales_order_detail += '<tr>'
												+'<td>'+item.sku+'</td>'
												+'<td>'+(item.productName+(item.isgift?"【<b style='color: red;'>赠</b>】":""))+'</td>'
												+'<td>'+deal_with_illegal_value(item.interBarCode)+'</td>'
												+'<td>'+item.warehouseName+'</td>'
												+'<td>'+deal_with_illegal_value(item.expirationDateStr)+'</td>'
												+'<td>'+item.finalSellingPrice.toFixed(2)+'</td>'
												+'<td>'+item.qty+'</td>'
												+'<td>'+subTotal+'</td>'
												+'</tr>';
										});
										orderTotal += base.bbcPostage ? base.bbcPostage : 0;
										//为div设定一个id
										var payment_type = getPamentType(deal_with_illegal_value(main.paymentType));
										var purchasePaymentType = getPamentType(deal_with_illegal_value(main.purchasePaymentType));
										var bbcPostage = base.bbcPostage;
										if(bbcPostage == undefined || bbcPostage == null || bbcPostage == "" || bbcPostage == "null"){
											bbcPostage = 0;
										}
										var isMSiteOrder = (main.source == 'MSITE' || main.source == 'MSITE-BRAND');
										//基本信息
										setBaseField(main,base,getstoreRes);
										$("#totalQty").html(totalQty);
										//商品信息
										$("#sales_detail_info").prepend(sales_order_detail);
										if(isMSiteOrder) {
											var disPrimeCost = main.disPrimeCost ? main.disPrimeCost : 0.00;
											$("#mOrderActualAmount").parent().show();
											$("#mOrderActualAmount").html(deal_with_illegal_value(base.orderActualAmount));
											$("#bbcPostage2").parent().hide();
											$("#disPrimeCost").parent().show();
											$("#disPrimeCost").html(disPrimeCost);
										}
										// 要非取消状态才有采购信息
										if(main.status!=5 || main.status!= 20  ){
											// 查询采购信息
											ajax_post("/sales/purchaseInfo", JSON.stringify({"orderId": orderId}), "application/json",
												function (response) {
													//采购信息
													var sales_purchase_detail = "";//采购信息详情
													var historySaleDetailList = response.historySaleDetailList;
													$.each(historySaleDetailList,function(i,item){
														var purchaseOrderNo = item.purchaseOrderNo;
														var totalPurchasePrice = parseFloat(item.capFee != null ? item.capFee:item.purchasePrice) * parseFloat(item.qty);
														sales_purchase_detail += '<tr>'
															+'<td>'+deal_with_illegal_value(purchaseOrderNo)+'</td>'
															+'<td>'+item.sku+'</td>'
															+'<td>'+item.expirationDateStr+'</td>'
															+'<td>'+deal_with_illegal_value(item.capFee.toFixed(2))+'</td>'
															+'<td>'+item.qty+'</td>'
															+'<td>'+totalPurchasePrice.toFixed(2)+'</td>'
															+'</tr>';
													});
													$("#purchase_detail_info").prepend(sales_purchase_detail);
													if(parseFloat(response.purchaseAmountTotal) > 0){
														$("#total_purchases_amount").show();
													} else {
														$("#total_purchases_amount").hide();
													}
													//显示 待付款金额=待付款商品总价+运费-优惠码优惠-整单优惠
													var market_purchas_infor_detail = "";
													if(main.purchaseOrderNo){
														ajax_post_sync("/purchase/getOrderById",
															JSON.stringify({"pNo": main.purchaseOrderNo}),
															"application/json",
															function(data){
																var order = data.orders[0];
																var bbcPostage = order.bbcPostage;
																var purchaseTotalAmount = order.purchaseTotalAmount;
																var couponsAmount = order.couponsAmount;
																//整单优惠
																var reducePrice = order.reducePrice;
																//商品总价
																var orderProTotal = order.orderProTotal;
																total = purchaseTotalAmount;
																market_purchas_infor_detail += '<span>商品总计'+(orderProTotal?parseFloat(orderProTotal).toFixed(2):0.00)+'元</span>';
																if(bbcPostage){
																	total += bbcPostage;
																	market_purchas_infor_detail +='+&nbsp;&nbsp;<span>运费'+parseFloat(bbcPostage).toFixed(2)+'元</span>';
																}
																if(couponsAmount){
																	total -= couponsAmount;
																	market_purchas_infor_detail +='-&nbsp;&nbsp;<span>优惠码优惠'+parseFloat(couponsAmount).toFixed(2)+'元</span>';
																}
																if(reducePrice){
																	market_purchas_infor_detail +='-&nbsp;&nbsp;<span>整单优惠'+parseFloat(reducePrice).toFixed(2)+'元</span>';
																}
																total = total>0?parseFloat(total).toFixed(2):0.00;
															},function(e){
																console.log(e);
															}
														);
													}else{
														var bbcPostage = base.bbcPostage?base.bbcPostage:0.00;
														var couponsAmount = base.couponsAmount;
														var total = bbcPostage - (couponsAmount?couponsAmount:0.00);
														total = total>0?parseFloat(total).toFixed(2):0.00;
														market_purchas_infor_detail +='&nbsp;&nbsp;<span>运费'+parseFloat(bbcPostage).toFixed(2)+'元</span>';
														if(couponsAmount){
															market_purchas_infor_detail +='-&nbsp;&nbsp;<span>优惠码优惠'+parseFloat(couponsAmount).toFixed(2)+'元</span>';
														}
													}
													market_purchas_infor_detail +='=&nbsp;&nbsp;<span>订单金额'+total+'元</span>';
													// 采购信息
													$("#total_purchases_amount").html(market_purchas_infor_detail);
												},
												function (xhr, status) {
												}
											);
										}
									}
								);
							}
						}
					);
				}}
			);
		}
	});
}

function setBaseField(main,base,store) {
	//订单信息
	$("#platformName").html(deal_with_illegal_value(store.otherPlatform?store.otherPlatform:store.platformName));
	$("#platformNo").html(deal_with_illegal_value(base.platformOrderNo));
	$("#tradeNumber").html(deal_with_illegal_value(base.tradeNo));
	$("#actualAmount").html(deal_with_illegal_value(base.orderActualAmount));
	$("#postFee").html(deal_with_illegal_value(base.orderPostage));
	$("#collectAccount").html(deal_with_illegal_value(base.collectAccount));
	$("#remark").html(deal_with_illegal_value(base.remark));
	//物流信息
	$("#logisticsMode").html(deal_with_illegal_value(base.logisticsMode));
	$("#originalFreight").html(deal_with_illegal_value(base.originalFreight));
	$("#bbcPostage").html(deal_with_illegal_value(base.bbcPostage));
	$("#bbcPostage2").html(deal_with_illegal_value(base.bbcPostage));
	//收件人信息
	$("#receiverName").html(deal_with_illegal_value(base.receiver));
	$("#receiverTel").html(deal_with_illegal_value(base.tel));
	$("#receiverAddr").html(base.address);
	//寄件人信息
	var  senderAddress = store.provinceName?store.provinceName:"";
	senderAddress += store.cityName?store.cityName:"";
	senderAddress += store.areaName?store.areaName:"";
	senderAddress += store.addr?store.addr:"";
	$("#senderName").html(store.keeperName?store.keeperName:"唐义和");
	$("#senderTel").html(store.telphone?store.telphone:"13689528832");
	$("#senderAddress").html(senderAddress ? senderAddress : '平湖镇平湖街道平安大道乾隆物流园2期3楼');
	//采购进货支付信息
	var purchasePaymentType = getPamentType(deal_with_illegal_value(main.purchasePaymentType));
	$("#purchasePaymentNo").html(deal_with_illegal_value(main.purchasePaymentNo));
	$("#purchasePayDate").html(deal_with_illegal_value(main.purchasePayDateStr));
	$("#purchasePaymentType").html(purchasePaymentType);
	//后台审核支付信息
	var payment_type = getPamentType(deal_with_illegal_value(main.paymentType));
	$("#payer").html(deal_with_illegal_value(main.payer));
	$("#payerIDCard").html(deal_with_illegal_value(main.paryerIdcard));
	$("#payNumber").html(deal_with_illegal_value(main.paymentNo));
	$("#payTime").html(deal_with_illegal_value(main.payDateStr));
	$("#payType").html(payment_type);
	//订购人信息
	$("#orderer").html(deal_with_illegal_value(base.orderer));
	$("#ordererIDCard").html(deal_with_illegal_value(base.ordererIDCard));
	$("#ordererTel").html(deal_with_illegal_value(base.ordererTel));
	$("#ordererPostcode").html(deal_with_illegal_value(base.ordererPostcode));
	$("#buyerID").html(deal_with_illegal_value(base.buyerID));
}

function formatErpStatus(cellValue) {
	var str = '--';
	if(cellValue == -1) {
		str = '已关闭';
	}

	if(cellValue == 0) {
		str = '已关闭';
	}

	if(cellValue == 5) {
		str = '待应用发货优先规则';
	}

	if(cellValue == 10) {
		str = '待客服分配';
	}

	if(cellValue == 20) {
		str = '待客服确认';
	}

	if(cellValue == 30) {
		str = '客服暂不确认';
	}

	if(cellValue == 40) {
		str = '待财务确认';
	}

	if(cellValue == 50) {
		str = '财务暂不确认';
	}
	if(cellValue == 60) {
		str = '待采购';
	}
	if(cellValue == 70) {
		str = '待质检';
	}
	if(cellValue == 80) {
		str = '待API上传';
	}
	if(cellValue == 90) {
		str = '待生成波次';
	}
	if(cellValue == 95) {
		str = '订单异常状态';
	}
	if(cellValue == 98) {
		str = '上架异常-漏上架';
	}
	if(cellValue == 100) {
		str = '待打配货单';
	}
	if(cellValue == 110) {
		str = '待捡货';
	}
	if(cellValue == 112) {
		str = '待见货出单';
	}
	if(cellValue == 115) {
		str = '待捆绑确认';
	}
	if(cellValue == 120) {
		str = '待包装';
	}
	if(cellValue == 130) {
		str = '待发货确认';
	}
	if(cellValue == 135) {
		str = '待分拣';
	}
	if(cellValue == 140) {
		str = '待顾客反馈';
	}
	if(cellValue == 160) {
		str = '顾客已收到';
	}
	if(cellValue == 170) {
		str = '未收到-申请部分退款';
	}
	if(cellValue == 180) {
		str = '未收到-同意部分退款';
	}
	if(cellValue == 190) {
		str = '未收到-已部分退款';
	}
	if(cellValue == 200) {
		str = '未收到-申请全额退款';
	}
	if(cellValue == 210) {
		str = '未收到-同意全额退款';
	}
	if(cellValue == 220) {
		str = '未收到-已全额退款';
	}
	if(cellValue == 230) {
		str = '未收到-申请重发';
	}
	if(cellValue == 240) {
		str = '未收到-同意重发';
	}
	if(cellValue == 250) {
		str = '未收到-已重发';
	}
	if(cellValue == 260) {
		str = '已收到-申请部分退款';
	}
	if(cellValue == 270) {
		str = '已收到-同意部分退款';
	}
	if(cellValue == 280) {
		str = '已收到-已部分退款';
	}
	if(cellValue == 290) {
		str = '已收到-申请全额退款';
	}
	if(cellValue == 300) {
		str = '已收到-同意全额退款';
	}
	if(cellValue == 310) {
		str = '已收到-已全额退款';
	}
	if(cellValue == 320) {
		str = '已收到-申请重发';
	}
	if(cellValue == 330) {
		str = '已收到-同意重发';
	}
	if(cellValue == 340) {
		str = '已收到-已重发';
	}
	if(cellValue == 350) {
		str = '未发货-申请退款';
	}
	if(cellValue == 360) {
		str = '未发货-同意退款';
	}
	if(cellValue == 370) {
		str = '未发货-已退款';
	}
	if(cellValue == 380) {
		str = '发货异常-待海关查验';
	}
	if(cellValue == 390) {
		str = '发货异常-已退回';
	}
	if(cellValue == 400) {
		str = '发货异常-已销毁';
	}
	if(cellValue == 410) {
		str = '发货异常-已丢包';
	}
	if(cellValue == 420) {
		str = '发货异常-丢包已索赔';
	}
	if(cellValue == 430) {
		str = '签收异常';
	}
	if(cellValue == 440) {
		str = '待移库审核';
	}
	if(cellValue == 450) {
		str = '待业务二审';
	}
	return str;
}

