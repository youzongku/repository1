var layer,laypage,orderTemplate,prov_map,city_map,region_map;

//change to jqGrid
function init_cs_sale_new(lay, layd,BbcGrid,oTemplate) {
	layer = lay;
	laypage = layd;
	orderTemplate = oTemplate;
	//初始化仓库下拉选
	initiate_warehouse_select();
	//初始化待审核订单列表
	var grid = new BbcGrid();
	grid.initTable($("#sale_cs_table"),getSetting_sale_cs_audit());
	$("#delivery_warehouse_select").change(function(){
		$("#sale_cs_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
		if($("#delivery_warehouse_select").val()!=""){
			$("#mergeAudit").removeAttr("disabled");
			$("#mergeAudit").css({
				"cursor": "pointer",
				"background-color": "#283442"
			});
		}else{
			$("#mergeAudit").attr("disabled",true);
			$("#mergeAudit").css({
				"cursor": "not-allowed",
				"background-color": "darkgray"
			});
		}
	});
	$("#distributorType").change(function(){
		getSaleCSListByCondition()
	});
	$(".searchButton").click(function(){
		getSaleCSListByCondition()
	});
}

function getSaleCSListByCondition(){
	$("#sale_cs_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}

function getSetting_sale_cs_audit() {
	var setting = {
		url:"/sales/manager/showAllSalesOrder",
		ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
		rownumbers : true, // 是否显示前面的行号
		datatype : "json", // 返回的数据类型
		mtype : "post", // 提交方式
		height : "auto", // 表格宽度
		autowidth : true, // 是否自动调整宽度
		// styleUI: 'Bootstrap',
		colNames:["id","订单编号","操作费","下单时间","业务员","订单收货人","名称", "分销商","录入人", "分销商类型", "发货仓库" , "相关操作"],
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
			{name:"saleBase.createUser",index:"create_user",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleMain.distributorTypeStr",index:"distributor_type",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleMain.warehouseName",index:"warehouse_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"saleMain.status",index:"detail",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
				var operateHtml =
				"<a style='display:block;' pno='"+(rowObject.saleMain.purchaseOrderNo ? rowObject.saleMain.purchaseOrderNo : "")+"' sno='"+rowObject.saleMain.salesOrderNo+"'  onclick='confirm(this)'>去确认</a>" +
				"<a style='display:block;' onclick='operateRecord("+rowObject.saleMain.id+")'>查看日志</a>";
				return "<div class='sale_save_about'>" + operateHtml + "</div>";
			}}
		],
		viewrecords : true,
		rowNum : 10,
		rowList : [ 10, 20, 30 ],
		pager:"#sale_cs_pagination",//分页
		caption:"客服确认(双击行，可查看发货单详情)",//表名称
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
			doShowSaleOrderDetail_sale_cs_audit(rowid,false)
		},
		serializeGridData : function(postData) {
			return JSON.stringify(getSearchParam_sale_cs_audit($(this).jqGrid('getGridParam', 'postData')))
		}
	};
	return setting;
}

function getSearchParam_sale_cs_audit(postData) {
	return {
		pageSize: $('#sale_cs_table').getGridParam('rowNum'),
		pageCount: $('#sale_cs_table').getGridParam('page'),
		orderStartDate: $("input[name='startTime']").val(),
		orderEndDate: $("input[name='endTime']").val(),
		status: "3",
		noticeStartDate: $("input[name='noticeStartTime']").val(),
		noticeEndDate: $("input[name='noticeEndTime']").val(),
		warehouseId: $("#delivery_warehouse_select").val(),
		seachSpan: $("#searchInput").val().trim(),
		distributorType: $("#distributorType").val(),
		sidx: postData.sidx,
		sort: postData.sord == 'desc' ? 'asc' : 'desc'
	};
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

//查询所有发货单
function showSalesOrder(param,callback){
	ajax_post("/sales/manager/showAllSalesOrder",JSON.stringify(param), "application/json", function(response) {
			if(response.result == true){
				callback(response);
			}else{
				layer.msg(response.msg,{icon:5,time:2000});
				callback(false);
			}
		}, function(XMLHttpRequest, textStatus){
			layer.msg("获取待审核销售订单失败！",{icon:5,time:2000});
			callback(false);
		}
	);	
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

$("body").on("change", "#sale-province", function () {
	cs_citySel();
});

function init_cs_AreaSel(pid, cid, rid) {
	ajax_get("/member/getprovs", JSON.stringify(""), "application/json",
		function (data) {
			$("#sale-province").empty();
			$("#sale-city").empty();
			$("#sale-region").empty();
			prov_map = {};
			for (var i = 0; i < data.length; i++) {
				$("#sale-province").append("<option value='" + data[i].id + "' >" + data[i].provinceName + "</option>");
				//add by zbc 省市地区匹配 省份集合
				prov_map[data[i].provinceName.substr(0,data[i].provinceName.length-1)] = data[i].id;
			}
			//省份存在就选中
			if (pid) {
				$("#sale-province").val(pid);
			} else {
				//不存在已选择省份，默认选第一个
				var sel = $("#sale-province").find("option:first").val();
				$("#sale-province").val(sel);
			}
			cs_citySel(cid, rid);
		}
	);
}

function cs_citySel(cid, rid) {
	if($("#sale-province").val()){
		ajax_get("/member/getcities", "proId=" + $("#sale-province").val(), "",
			function (data) {
				$("#sale-city").empty();
				var cities = data.cities;
				city_map = {};
				for (var i = 0; i < cities.length; i++) {
					$("#sale-city").append("<option value='" + cities[i].id + "' code='"+cities[i].zipCode+"' >" + cities[i].cityName + "</option>");
					//add by zbc 省市地区匹配 省份集合
					city_map[cities[i].cityName.substr(0,cities[i].cityName.length-1)] = cities[i].id;
				}
				//城市存在就选中
				if (cid) {
					$("#sale-city").val(cid);
				} else {
					var sel = $("#sale-city").find("option:first").val();
					$("#sale-city").val(sel);
				}
				cs_regionSel(rid);
			}
		);
	}
}

//区级下拉框联动
$("body").on("change", "#sale-city", function () {
	cs_regionSel();
});

function cs_regionSel(rid) {
	if($("#sale-city").val()){
		ajax_get("/member/getareas", "cityId=" + $("#sale-city").val(), "",
			function (data) {
				$("#sale-region").empty();
				region_map = {};
				var areas = data.areas;
				for (var i = 0; i < areas.length; i++) {
					$("#sale-region").append("<option value='" + areas[i].id + "' >" + areas[i].areaName + "</option>");
					//add by zbc 省市地区匹配 省份集合
					region_map[areas[i].areaName.substr(0,areas[i].areaName.length-1)] = areas[i].id;
				}
				//地区存在就选中
				if (rid) {
					$("#sale-region").val(rid);
				} else {
					var sel = $("#sale-region").find("option:first").val();
					$("#sale-region").val(sel);
				}
			}
		);
	}
}

function confirm(obj){
	var pno = $(obj).attr("pno")
	var loading_index = layer.load(1, {shade: 0.5});
	if(pno){
		var poStatus = -1;
		ajax_post_sync("/purchase/simpleInfo",JSON.stringify({purchaseOrderNo:pno}),"application/json",
			function(data){
				if(data){
					poStatus = data.status
				}
			}
		)
		// 缺货采购单没有完成，不能进行审核（采购单完成的状态为1）
		if(poStatus!=1){
			layer.alert("此发货单存在缺货采购单未完成，暂不能审核！采购单号为："+pno);
			layer.close(loading_index);
			return
		}
	}
	var sno = $(obj).attr("sno");
	var param = {salesOrderNo:sno};
	ajax_post("/sales/manager/getSaleOrderByNo",JSON.stringify(param),"application/json",
		function(response){
			layer.close(loading_index);
			if(response.suc){
				var base = response.saleBase;
				var main = response.saleMain;
				// TODO 带出订单信息
				var openHtml = 
					'<div class="redactPop">'+ 
						'<h4> 收件人信息 </h4>'+ 
						'<div>'+
						'<div class="redactPopL"> '+
								'<span class="to-confirm-l"> 姓名： </span> <span class="to-confirm-r"> <input name="receiver" value="'+(base.receiver?base.receiver:"")+'" type="text"/> </span> '+
							'</div>' +
							'<div class="redactPopR"> '+
								'<span class="to-confirm-l"> 电话： </span> <span class="to-confirm-r"> <input name="tel" value="'+(base.tel?base.tel:"")+'" type="text"/> </span> '+
							'</div> '+
						'</div> '+
						'<div> '+
							'<div class="redactPopL"> '+
								'<span class="to-confirm-l"> 邮编： </span> <span class="to-confirm-r"> <input name="postCode" value="'+(base.postCode?base.postCode:"")+'" type="text"/> </span> '+
							'</div> '+
							'<div class="redactPopR"> '+
								'<span class="to-confirm-l"> 身份证号码： </span> <span class="to-confirm-r"> <input name="idcard" value="'+(base.idcard?base.idcard:"")+'" type="text"/> </span>'+
							'</div> '+
						'</div> '+					
						'<div> '+
							'<span class="to-confirm-l"> 收货地址： </span> <span class="to-confirm-r"> '+
								'<input type="text" disabled="true" name="address" value="'+(base.address?base.address:"")+'" style="width: 80%;"/> '+
							'</span>'+
						'</div> '+
						'<div class="redactAddress"> '+
							'<span class="to-confirm-l"> 确认地址： </span> <span class="to-confirm-r"> '+
								'<p> '+
									'<select id="sale-province" disabled="true"></select> '+
									'<select id="sale-city" disabled="true"></select> '+
									'<select id="sale-region" disabled="true"></select> '+
								'</p>'+
								'<input type="text" name="addr" style="width: 50%;"/> '+
								//'<input type="button" id="addr_use" value="应用">'+
							'</span>'+
						'</div> '+
						'<div> '+
							'<span class="to-confirm-l"> <em class="red">*</em> 请选择： </span> '+
							'<span class="to-confirm-r"> '+
								'<label> <input type="radio" name="csAudit" value=true />确认通过 </label>'+
								'<label> <input type="radio" name="csAudit" value=false />确认关闭 </label> '+
							'</span>'+
						'</div>'+ 
						'<div>'+
					        '<span class="to-confirm-l">备注：</span>'+
					        '<span class="to-confirm-r">'+
					           ' <textarea id="csRemark"></textarea>'+
					        '</span>'+
					    '</div>'+
					'</div>';
				layer.open({
			        type: 1,
			        title: '编辑',
			        area: ['auto', 'auto'],
			        content: openHtml,
			        btn: ['提交','取消'],
			        success:function(i,currdom){
			        	init_cs_AreaSel();
			        	//默认选中第一个
			        	//$("input[name=csAudit]:eq(0)").prop("checked",true);
			        	matchAddress(base.address);
			        	$(".redactPop").on("click","#addr_use",function(){
			        		var addr = $("input[name=addr]").val();
			        		if(addr&&addr.length>=5){
			        			var address = $("#sale-province option:selected").text()+" "+
				        		$("#sale-city option:selected").text()+" "+
				        		$("#sale-region option:selected").text()+" "+
				        		$("input[name=addr]").val();
				        		$("input[name=address]").val(address);
			        		}else{
			        			layer.msg("详细地址格式不规范",{icon:5,time:2000});
			        		}
			        	});
			        },
			        yes:function(index){
			        	if(!matchAddress($("input[name=address]").val())||!cs_validation()){
			        		return; 
			        	}
			        	//收货地址格式严格控制
			        	var param = {
							address: $("input[name=address]").val(),
							receiver: $("input[name=receiver]").val(),
							tel: $("input[name=tel]").val(),
							postCode: $("input[name=postCode]").val(),
							idcard: $("input[name=idcard]").val(),
							csRemark: $("#csRemark").val(),
							csAudit: $("input[name=csAudit]:checked").val(),
							sno: sno
			        	}
			        	ajax_post("/sales/manager/cusAudit",JSON.stringify(param),"application/json",
			        		function(res){
			        			if(res.suc){
			        				layer.msg(res.msg,{icon:6,time:2000},function(){
				        				layer.close(index);
				        				//post_cs_sale(null,false);
										$("#sale_cs_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
				        			})
			        			}else{
			        				layer.msg(res.msg,{icon:5,time:2000});
			        			}
			        		},function(){
			        			layer.msg("客服审核异常",{icon:5,time:2000});
			        		}
			        	);
			        }
			    });
			}else{
				layer.msg(response.msg,{icon:5,time:2000});
			}
		},function(e){
			layer.close(loading_index);
			console.log(e);
		}
	)
}

function cs_validation() {
	var receiver = $("input[name=receiver]").val();
	if (!receiver) {
		layer.msg('收件人姓名', {icon: 2, time: 2000});
		return false;
	}
	//收货人手机验证
	var tel = $("input[name=tel]").val();
	if (!checkTel(tel)) {
		layer.msg('收件人手机号码有格式错误，请输入有效手机号码', {icon: 2, time: 2000});
		return false;
	}
	//收货地址验证
	var addr = $("input[name=addr]").val();
	if (!addr || addr.length < 5) {
		layer.msg('收货地址不规范，请重新填写', {icon: 2, time: 2000});
		return false;
	}
	var postCode = $("input[name=postcode]").val();
	if (postCode && !checkPost(postCode)) {
		layer.msg('请输入有效的收货地址邮政编码', {icon: 2, time: 2000});
		return false;
	}
	return true;
}

function matchAddress(addr){
	var adl = addr.split(" ");
	var flag = false;
	if(adl.length >= 4){
		var prov_id = getId(adl[0],prov_map);
		$("#sale-province").val(getId(adl[0],prov_map)).change();
		if($("#sale-province").val()){
			$("#sale-city").val(getId(adl[1],city_map)).change();
			if($("#sale-city").val()){
				flag = true;
				$("#sale-region").val(getId(adl[2],region_map)).change();
			}
		}
		var newaddr=adl.slice(3).join(" ");
		$("input[name=addr]").val(newaddr);
	}
	if(!flag){
		layer.msg("请修改地址，并点击应用",{icon:5,time:2000});
	}
	return flag;
}

function getId(key,map){
	if(map[key]){
		return map[key];
	}else{
		key = key.substr(0,key.length-1);
		if(key){
			return map[key];
		}
	}  
}

function clearDetail(node1,node2,node3) {
	$("#mOrderActualAmount").parent().hide();
	$("#disPrimeCost").parent().hide();
	node1.html("");
	node2.empty();
	node3.empty();
}

//展示发货单详情
function doShowSaleOrderDetail_sale_cs_audit(rowId,isAllOrder){
	var rowData = $("#sale_cs_table").jqGrid('getRowData',rowId);
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
				}
			});
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
