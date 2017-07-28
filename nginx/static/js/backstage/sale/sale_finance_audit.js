var layer = undefined;
var laypage = undefined;
var orderTemplate = undefined;
var yjfWxUrl = "";

function init_sale_new(lay, layd,BbcGrid,oTemplate) {
	layer = lay;
	laypage = layd;
	orderTemplate = oTemplate;
	//初始化仓库下拉选
	initiate_warehouse_select();
	//初始化待审核订单列表
	var grid = new BbcGrid();
	grid.initTable($("#sale_finance_audit_table"),getSetting_sale_finance_audit());
	$("#delivery_warehouse_select").change(function(){
		$("#sale_finance_audit_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
		if($("#delivery_warehouse_select").val()!=""){
			$("#mergeAudit").removeAttr("disabled");
			$("#mergeAudit").css({"cursor": "pointer", "background-color": "#283442"});
		}else{
			$("#mergeAudit").attr("disabled",true);
			$("#mergeAudit").css({"cursor": "not-allowed", "background-color": "darkgray"});
		}
	});
	$("#distributorType").change(function(){
		$("#sale_finance_audit_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
	});
	$(".searchButton").click(function(){
		$("#sale_finance_audit_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
	});
}

function getSetting_sale_finance_audit() {
	var setting = {
		url:"/sales/manager/showAllSalesOrder",
		ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
		rownumbers : true, // 是否显示前面的行号
		datatype : "json", // 返回的数据类型
		mtype : "post", // 提交方式
		height : "auto", // 表格宽度
		autowidth : true, // 是否自动调整宽度
		// styleUI: 'Bootstrap',
		colNames:["id","订单编号","操作费","下单时间","业务员","订单收货人","名称", "分销商","录入人", "分销商类型", "发货仓库",  "相关操作"],
		colModel:[{name:"saleMain.id",index:"id",width:"12%",align:"center",sortable:false,hidden:true},
			{name:"saleMain.salesOrderNo",index:"sales_order_no",width:"12%",align:"center",sortable:false},
			{name:"saleMain.optFee",index:"opt_fee",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
				return parseFloat(cellvalue).toFixed(2);
			}},
			{name:"saleMain.orderingDateStr",index:"ordering_date",width:"14%",align:"center",sortable:true},
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
			{name:"saleMain.email",index:"email",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(rowObject.saleBase.createUser);
			}},
			{name:"saleMain.distributorTypeStr",index:"distributor_type",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleMain.warehouseName",index:"warehouse_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleMain",index:"",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
				var operateHtml =
					"<a style='display:block;' data-payno='"+rowObject.saleMain.paymentNo+"' data-proname='"+rowObject.saleMain.productName+"' "+
					" pno='"+(rowObject.saleMain.purchaseOrderNo ? rowObject.saleMain.purchaseOrderNo : "")+"' data-sid='"+rowObject.saleMain.id+"' sno='"+rowObject.saleMain.salesOrderNo+"' "+
					"  data-sumprice='"+deal_with_illegal_value(rowObject.saleBase.orderActualAmount)+"'   onclick='openToCheck(this,"+rowObject.saleMain.warehouseId+")'>去确认</a>" +
					"<a style='display:block;' onclick='operateRecord("+rowObject.saleMain.id+")'>查看日志</a>";
				return "<div class=\"sale_save_about\">" + operateHtml + "</div>";
			}}
		],
		viewrecords : true,
		rowNum : 10,
		rowList : [ 10, 20, 30 ],
		pager:"#sale_finance_audit_pagination",//分页
		caption:"财务确认(双击行，可查看发货单详情)",//表名称
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
			var rowData = $("#sale_finance_audit_table").jqGrid('getRowData',rowid);
			doShowSaleOrderDetail_sale_finance_audit(rowData,false)
		},
		serializeGridData : function() {
			return JSON.stringify(getSearchParam_sale_finance_audit($(this).jqGrid('getGridParam', 'postData')))
		}
	};
	return setting;
}

function getSearchParam_sale_finance_audit(postData) {
	return {
		pageSize: $('#sale_finance_audit_table').getGridParam('rowNum'),
		pageCount: $('#sale_finance_audit_table').getGridParam('page'),
		orderStartDate: $("input[name='startTime']").val(),
		orderEndDate: $("input[name='endTime']").val(),
		status: "11",
		noticeStartDate: $("input[name='noticeStartTime']").val(),
		noticeEndDate: $("input[name='noticeEndTime']").val(),
		warehouseId: $("#delivery_warehouse_select").val(),
		seachSpan: $("#searchInput").val().trim(),
		distributorType: $("#distributorType").val(),
		sort: postData.sord
	};
}

function getList_sales_finance_audit() {
	$("#sale_finance_audit_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
}

//展示发货单详情
function doShowSaleOrderDetail_sale_finance_audit(rowData,isAllOrder){
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
															+'<td>'+(purchaseOrderNo ? purchaseOrderNo : "---")+'</td>'
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

function clearDetail(node1,node2,node3) {
	$("#mOrderActualAmount").parent().hide();
	$("#disPrimeCost").parent().hide();
	node1.html("");
	node2.empty();
	node3.empty();
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

function dual_pay(node){
	var $spans = $(node);
	var $spans = $(node);
	$("#orderNo").text($spans.attr("sno"));
	$("#toCheckId").text($spans.data("sid"));
	$("#tradeNo").text($spans.data("payno"));
	$("#productName").text($spans.data("proname"));
	$("#sumPrice").text($spans.data("sumprice"));
	//实际支付
	var id = $("#toCheckId").text();
	var tradeNo = $("#orderNo").text();
	var productName = encodeURIComponent($("#productName").text());
	var sumPrice = $("#sumPrice").text();

	var alipayUrl = "../../payment/alipay.html?purno="+id+"&tradeNo="+tradeNo+"&productName="+encodeURIComponent(productName)+"&sumPrice="+sumPrice;
	// confirm_pay_result_dialog(alipayUrl, wxpayUrl, yjpayUrl);
	// var wxpayUrl = "../../payment/wechat.html?one="+id+"&two="+tradeNo+"&three="+encodeURIComponent(productName)+"&four="+sumPrice;
	var yjpayUrl = "../../payment/yijipay.html?one="+id+"&two="+tradeNo+"&three="+sumPrice+"&four=";
	yjfWxUrl = "../../payment/yijifu.html?one="+id+"&two="+tradeNo+"&three="+sumPrice+"&four=";

	var weinParam = {
		id:id,
		orderNo: tradeNo,
		orderDes:$("#productName").text(),
		totalPrice: sumPrice
	}
	var callParma = {
		func:'confirm_pay_result_dialog',
		alipayUrl:alipayUrl,
		wxpayUrl:wxpayUrl,
		yjpayUrl:yjpayUrl
	}
	var wxpayUrl = "javascript:audit_loadweixinPay("+JSON.stringify(weinParam)+","+JSON.stringify(callParma)+");";
	callParma.wxpayUrl = wxpayUrl;
	wxpayUrl = "javascript:audit_loadweixinPay("+JSON.stringify(weinParam)+","+JSON.stringify(callParma)+");";
	var param = {
		orderId: $("#toCheckId").text()
	};
	ajax_post("/sales/showOrderDetail", JSON.stringify(param), "application/json",
		function(data) {
			if(data.code){
				window.location.href = "/backstage/login.html";
			}
			if(data.result){
				var names = new Array();
				var details = data.saleOrderInfo.saleDetails;
				$.each(details, function(i, item) {
					names.push({"name": item.productName});
				});
				yjpayUrl += encodeURIComponent(encodeURIComponent("财务审核")) + "&five=ONLINEBANK&six=PC";
				console.log("yjpayUrl--->" + yjpayUrl);
				//扫码支付
				yjfWxUrl += encodeURIComponent(encodeURIComponent("财务审核")) + "&five=BALANCE&six=PC";
				console.log("yjfWxUrl--->" + yjfWxUrl);

				multi_pay_mode_dialog(alipayUrl, wxpayUrl, yjpayUrl);
			} else {
				console.error("data.result--->" + data.result);
				multi_pay_mode_dialog(alipayUrl, wxpayUrl, yjpayUrl);
			}
		}
	);
}

//打开待审核页面审核框
function openToCheck(node,wid){
	// 判断是否存在缺货采购单，不能审核
	var pno = $(node).attr("pno")
	if(pno){
		var poStatus = -1;
		ajax_post_sync("/purchase/simpleInfo",JSON.stringify({purchaseOrderNo:pno}),"application/json",
			function(data){
				if(data){
					poStatus = data.status
				}
			}
		)
		// 缺货采购单没有完成，不能进行审核
		if(poStatus!=1){
			layer.alert("此发货单存在缺货采购单未完成，暂不能审核！采购单号为：" + pno)
			return
		}
	}
	toConfirm(node,wid);
}

//支付方式弹出框
var query_payment_result_url_one = "";
function multi_pay_mode_dialog(alipayUrl, wxpayUrl, yjpayUrl) {
	layer.open({
		type: 1,
		title: '支付方式',
		content:
		'<div id="select_pay_method_dialog" style="padding: 30px 60px;">' +
		'<select style="font-size: 16px; padding: 6px;">' +
		'<option value="">请选择支付方式</option>' +
		'<option value="alipay">支付宝</option>' +
		'<option value="wechat">微信支付</option>' +
		// '<option value="yijipay">易极付</option>' +
		'<option value="yjf_wx">易极付-余额</option>' +
		'</select>' +
		'</div>',
		move: false,
		btn: ['确定', '取消'],
		yes: function() {
			var mode = $("#select_pay_method_dialog select").val();
			if (mode == "alipay") {
				layer.closeAll();
				console.log("alipayUrl--->" + alipayUrl);
				window.open(alipayUrl, "_blank");
				query_payment_result_url_one = "/payment/qryAlipayResult";
				confirm_pay_result_dialog(alipayUrl, wxpayUrl, yjpayUrl);
			} else if (mode == "wechat") {
				layer.closeAll();
				console.log("wxpayUrl--->" + wxpayUrl);
				// window.open(wxpayUrl, "_blank");
				window.location.href = wxpayUrl;
				query_payment_result_url_one = "/payment/wechat/getpayresult";

			} else if (mode == "yijipay") {
				layer.closeAll();
				console.log("yjpayUrl--->" + yjpayUrl);
				window.open(yjpayUrl, "_blank");
				query_payment_result_url_one = "/payment/yijipay/getpayresult";
				confirm_pay_result_dialog(alipayUrl, wxpayUrl, yjpayUrl);
			} else if (mode == "yjf_wx") {
				layer.closeAll();
				console.log("yjfWxUrl--->" + yjfWxUrl);
				window.open(yjfWxUrl, "_blank");
				query_payment_result_url_one = "/payment/yijipay/getpayresult";
				confirm_pay_result_dialog(alipayUrl, wxpayUrl, yjpayUrl);
			}else {
				query_payment_result_url_one = "";
				layer.msg("请选择支付方式", {icon: 0, time: 1000});
			}
		}
	});
}

//load 微信支付弹出窗
function audit_loadweixinPay(param,callback){
	var winxinHtml = $('#winxin_content');
	winxinHtml.load("../payment/wechat.html", function (response, status, xhr) {
		require(["wechat","sale_finance_audit"], function (wechat) {
			var url = '../backstage/index.html';
			$('.modal').fadeIn(300);
			init_win_payment(param,url,layer,callback);
		});
	});
}

//确认支付结果弹出框
function confirm_pay_result_dialog(alipayUrl, wxpayUrl, yjpayUrl) {
	layer.open({
		type: 1,
		title: '确认支付结果',
		content: '',
		move: false,
		btn: ['支付成功', '支付失败', '选择其他支付方式'],
		area: ["400", "140"],
		btn1: function() {
			layer.closeAll();
			layer.confirm('确认支付成功？',{
					btn: ['确认']
				},
				function(){
					//检验订单是否支付成功
					ajax_get(query_payment_result_url_one, {orderNo: $("#orderNo").text()}, "",
						function(data) {
							if (data.suc) {
								// submitToCheck();
								layer.msg("已支付成功,状态信息已更新",{icon:6,time:2000},function(index){
									layer.closeAll();
									$("#sale_finance_audit_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
								});
							} else {
								layer.msg("经查询结果显示当前订单待支付或支付失败", {icon: 2, time: 4000});
							}
						}
					);
				}
			);
		},
		btn2: function() {
			layer.closeAll();
			$("#sale_finance_audit_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
		},
		btn3: function() {
			layer.closeAll();
			multi_pay_mode_dialog(alipayUrl, wxpayUrl, yjpayUrl);
		}
	});
}

function checkSaleMain(param){
	ajax_post_sync("/sales/manager/checkSaleMain", JSON.stringify(param), "application/json", function(response) {
			if(response.code){
				window.location.href = "/backstage/login.html";
			}
			if (response.result == true) {
				layer.msg(response.msg,{icon:6,time:2000});
				layer.closeAll();
				$("#sale_finance_audit_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
			} else {
				layer.msg(response.msg,{icon:5,time:2000});
			}
		}, function(XMLHttpRequest, textStatus) {
			layer.msg("审核订单失败",{icon:5,time:2000});
		}
	);
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

function toConfirm(obj,whid){
	var id = $(obj).data("sid");
	var pay_mark = false;
 	if (whid == "2029" || whid == "2050" ) {
 		pay_mark = true;
	}
	ajax_get("/sales/manager/getAmount?sid="+id,"","",function(data){
		var conhtml = 
		'<div class="to-confirm">'+
			'<div>'+
				'<span class="to-confirm-l confirmL">毛收入(元)：</span>'+
				'<span class="to-confirm-r confirmR"><em >'+(data.platformamount != null?comToFixed(data.platformamount,2):"--")+'</em></span>'+
			'</div>'+
			'<div>'+
				'<span class="to-confirm-l confirmL">到仓价总计(元)：</span>'+
				'<span class="to-confirm-r confirmR"><em >'+(data.arrvicetotal != null?comToFixed(data.arrvicetotal,2):"--")+'</em></span>'+
			'</div>'+
			'<div>'+
				'<span class="to-confirm-l confirmL">操作费(元)：</span>'+
				'<span class="to-confirm-r confirmR"><em >'+(data.optfee != null?comToFixed(data.optfee,2):"--")+'</em></span>'+
			'</div>'+
			'<div>'+
				'<span class="to-confirm-l confirmL">平台运费(元)：</span>'+
				'<span class="to-confirm-r confirmR"><em>'+(data.bbcpostage != null?comToFixed(data.bbcpostage,2):"--")+'</em></span>'+
			'</div>'+
			'<div>'+
				'<span class="to-confirm-l confirmL">订单总成本(元)：</span>'+
				'<span class="to-confirm-r confirmR"><em>'+(data.totalcost != null?comToFixed(data.totalcost,2):"--")+'</em></span>'+
			'</div>'+
			'<div>'+
				'<span class="to-confirm-l confirmL">利润(元)：</span>'+
				'<span class="to-confirm-r confirmR"><em '+(data.profit<=0?"class='red'":"")+' >'+(data.profit != null?comToFixed(data.profit,2):"--")+'</em></span>'+
			'</div>'+
			'<div>'+
				'<span class="to-confirm-l confirmL">利润率：</span>'+
				'<span class="to-confirm-r confirmR"><em '+(data.profitmargin<=0?"class='red'":"")+' >'+(data.profitmargin != null?comToFixed(data.profitmargin,2):"--")+'</em></span>'+
			'</div>'+
			'<div>'+
		        '<span class="to-confirm-l confirmL">'+
		            '<em class="red">*</em>'+
		            '请选择：'+
		        '</span>'+
		        '<span class="to-confirm-r confirmR">'+
		            '<label>'+
		                '<input type="radio" name="finance_audit" value="true"/>确认通过'+
		            '</label>'+
		            '<label>'+
		                '<input type="radio" name="finance_audit" value="false"/>确认不通过'+
		            '</label>'+
		        '</span>'+
		    '</div>'+
			'<div>'+
		        '<span class="to-confirm-l confirmL">'+
		            '备注：'+
		        '</span>'+
		        '<span class="to-confirm-r confirmR">'+
		            '<textarea id="finance_remark" ></textarea>'+
		        '</span>'+
		    '</div>'+
		'</div>';
		layer.open({
	        type: 1,
	        title: '去确认',
	        area: ['550px', '480px'],
	        content: conhtml,
	        btn: ['提交','取消'],
	        success:function(i,currdom){
	        	$("input[name=finance_audit]:eq(0)").prop("checked",true);
	        },
	        yes:function(index){
	        	var confirm = $("input[name=finance_audit]:checked").val() =="true";
	        	var status = confirm?6:3;
	        	var param = {"orderId": id, "status":status, "comment":$("#finance_remark").val()};
	        	if(pay_mark&&confirm){
	        		//将审核信息存在redis session
	        		ajax_post("/sales/manager/finAudit",JSON.stringify(param),"application/json",function(response){
	        			if(response.suc){
	        				layer.closeAll();
	        				dual_pay(obj);
	        			}else{
	        				layer.msg(response.msg,{icon:5,time:2000});
	        			}
	        		});
				} else {
	        		checkSaleMain(param);
	        	}
	        }
	    });
	});
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
					}
					else{
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
