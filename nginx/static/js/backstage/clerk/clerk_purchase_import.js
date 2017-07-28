/**
 * 手动导入采购单
 */
define("pruchase_import", ["jquery", "layer", "webuploader"], function($, layer, webuploader){
	var uploader = undefined;
	function init_import(){
		init_webuploader_register();
		isaulogin(function (email) {
			init_uploader(email);
		});
	}

	function view_list(){
		view_import_list();
	}

	// 注册
	function init_webuploader_register() {
		webuploader.Uploader.register(
			{
				"before-send-file": "beforeSendFile"
			},
			{
				"beforeSendFile": function(file) {
					var task = webuploader.Base.Deferred();
					(new webuploader.Uploader()).md5File(file, 0, file.size)
						//MD5值计算实时进度
						.progress(function(percentage) {
							console.log("beforeSendFile    [MD5 percentage]----->" + percentage);
						})
						//如果读取出错了，则通过reject告诉webuploader文件上传出错。
						.fail(function() {
							task.reject();
							console.log("beforeSendFile    [file MD5 value calculate error]");
						})
						//MD5值计算完成
						.then(function(md5) {
							console.log("beforeSendFile    " + file.id + "_md5----->" + md5);
							uploader.options.formData[file.id + "_md5"] = md5;
							task.resolve();
							console.log("beforeSendFile    [next step]");
						});
					return webuploader.Base.when(task);
				}
			}
		);
	}

	// 初始化
	function init_uploader(email){
		uploader = webuploader.create({
			// swf文件路径
			swf: '../js/webuploader/Uploader.swf',
			// 文件接收服务端。
			server: '/purchase/importPurchase',
			// 选择文件的按钮。可选。
			// 内部根据当前运行是创建，可能是input元素，也可能是flash.
			pick: '#picker',
			accept: {
				title: 'Excel',
				extensions: 'xls,xlsx,csv'
			},
			fileNumLimit: 2,
			duplicate: false,
			formData: {"entryUser": email}
		});

		// 文件选择限制
		uploader.on( 'error', function( type ) {
			if(type=="Q_EXCEED_NUM_LIMIT"){
				layer.msg('超过文件添加最大数！', {icon: 5});
			}
			if(type=="Q_TYPE_DENIED"){
				layer.msg('请选择正确的Excel文件！', {icon: 5});
			}
			if(type=="F_DUPLICATE"){
				layer.msg('不能选择重复文件！', {icon: 5});
			}
		});
		
		uploader.on( 'uploadAccept', function( file, response ) {
			if(response.flag==false){
				layer.msg(response.msg, {icon: 2,time:2000});
			}else{
				//导入详情提示框
//				openResultBox(response.resultInfos);
			}
		});

		//上传成功事件
		uploader.on( 'uploadSuccess', function( file, response ) {
			if(!response.flag){
				layer.msg(response.msg, {icon: 2,time:2000});
			}else{
				console.log(response.resultInfos);
				if (response.resultInfos.messages.length > 0) {
					layer.msg(response.resultInfos.messages[0], {icon: 2,time:5000});
					return false;
				} else {
					layer.msg("导入成功", {icon: 1,time:2000});
				}
				view_import_list();
				return false;
			}
		});

		// 当有文件被添加进队列的时候
		uploader.on( 'fileQueued', function( file ) {
			$("#thelist").append('<b id="'+file.id+'">'+file.name+'</b>');
		});

		//上传失败事件
		uploader.on( 'uploadError', function( file ) {
			layer.msg(file.name+'上传失败！', {icon: 5});
		});

		// 上传完毕
		uploader.on( 'uploadComplete', function( file ) {
			$("#thelist").empty();
			uploader.reset();
		});

		//上传文件到服务器
		$("#import-excel").on( 'click', function() {
			//获取配置的传入参数
			//var email = uploader.option("formData").email;
			uploader.option("formData").templateType=1;
			uploader.upload();
		});
	}
	return {
		init_import:init_import,
		view_list:view_list
	};
});

var skuWare = {};
function view_import_list(){
	isaulogin(function (email) {
		var param = {"entryUser":email};
		emtpySkuMap();
		ajax_post("/purchase/getImportOrder",JSON.stringify(param), "application/json",
			function(data) {
				if(data.suc){
					var input = data.input;
					var pros = data.pros;
					var gifts = data.gifts;
					skuWare = data.skuWare;
					if(input){
						$("#im_dis_acc").val(input.disAccount);
						$("#im_dis_acc").data("mode",input.disMode);
						$("#im_dis_acc").data("type",input.disType);
						$("#im_dis_acc").data("id",input.id);
					}
					var tbody = $(".import_purchaseList_table tbody");
					tbody.empty();
					pros.sort(function(a,b){
						return a.id - b.id;
					});
					insertSkuMap(pros,gifts);
					var flag = true;
					var msg = "商品:";
					var ware_select = ""
					$.each(pros,function(i,item){// 正价商品
						var warehouseArray = item.warehouseNameId;
						var isMatch = warehouseArray.length>0;
						// 商品属于哪个仓库的
						if(isMatch){
							ware_select = createWarehouseSelectHtml(warehouseArray, false, item.id)
						}else{
							flag = false;
							msg += "["+item.sku+"]";
							ware_select = "未匹配到商品";
						}
						tbody.append(createAProductImportedTrHtml(item, false, ware_select));
					})
					$.each(gifts,function(i,item){// 赠品
						var warehouseArray = item.warehouseNameId;
						// 商品属于哪个仓库的
						if(warehouseArray.length > 0){
							ware_select = createWarehouseSelectHtml(warehouseArray, true, item.id)
						}else{
							flag = false;
							msg += "["+item.sku+"]";
							ware_select = "未匹配到商品";
						}
						tbody.append(createAProductImportedTrHtml(item, true, ware_select))
					});

					//判断是否能生成
					if(!flag){
						disabled($("#import_generator"),true);
						layer.msg(msg+"未匹配到商品,无法生成订单",{icon:2,time:2000});
					}else{
						disabled($("#import_generator"),false);
					}
					countPrice();
				}else{
					disabled($("#import_generator"),true);
					layer.msg(data.msg,{icon:2,time:2000});
				}
			}
		);
	});
}

// 生成正价商品/赠品tr html
function createAProductImportedTrHtml(item, isGift, ware_select){
	//单位类型 1 单个商品 单位类型 2 整箱商品
	var qty = item.unitType == 1 ? item.qty : item.qty*item.carton;
	var trName = isGift ? "" : "proTr"
	// 赠品标志
	var giftMark = isGift ? '【<b style="color: red;">赠</b>】' : ""
	return '<tr id="'+item.id+'" name="'+trName+'">'+
	'<td title="'+item.title+'">'+
	'<img class="bagproduct-img" alt="'+item.title+'" src="'+urlReplace(item.imageUrl,item.sku,"",100,50,100)+'"/>' +
	'<p style="text-align: left">'+deal_with_illegal_value(item.title)+giftMark+'</p>'+
	'</td>'+
	'<td name="sku">'+item.sku+'</td>'+
	'<td>'+ware_select+'</td>'+
	'<td name="expirationDate">'+deal_with_illegal_value(item.expirationDate)+'</td>'+
	'<td>'+deal_with_illegal_value(item.stock)+'</td>'+
	'<td name="price">'+(item.price?parseFloat(item.price).toFixed(2):"--")+'</td>'+
	'<td>'+(item.realPrice?parseFloat(item.realPrice).toFixed(2):"--")+'</td>'+
	'<td>'+deal_with_illegal_value(item.carton)+'</td>'+
	'<td>'+(item.unitType == 2?item.qty:"--")+'</td>'+
	'<td name="qty">'+qty+'</td>'+
	'</tr>';
}

// 创建正价商品/赠品的仓库选择select
function createWarehouseSelectHtml(warehouseArray, isGift, itemId){
	var selectName = isGift ? "gware_select" : "gware_select"
	var ware_select = "<select name='"+selectName+"' data-id ='"+itemId+"'>";
	for (var i = 0;i < warehouseArray.length;i++) {
		ware_select +="<option value='"+warehouseArray [i].warehouseId+"'>"+warehouseArray [i].warehouseName+"</option>";
	}
	ware_select += "</select>";
	return ware_select;
}

function disabled(obj,red){
	if(red){
		obj.attr("disabled", true).css("background", "#888");
	}else{
		obj.attr("disabled", false).removeAttr("style");
	}
}

$("body").on("change","select[name=pware_select]",function(){
	var node = $(this);
	var wareId = node.val();
	var id = node.data("id");
	ajax_post("/purchase/imporProUpd",JSON.stringify({id:id, warehouseId:wareId}), "application/json",
		function(data) {
			// 用户未登录
			if(data.code == 101){
				layer.msg(data.msg, {icon: 1, time: 2000}, function(){
					window.location.href = "/backstage/login.html"
				});
				return
			}
			if(data.suc){
				view_import_list();
			}else{
				layer.msg(data.msg,{icon:2,time:2000});
			}
		}
	);	
});

$("body").on("change","select[name=gware_select]",function(){
	var node = $(this);
	var wareId = node.val();
	var id = node.data("id");
	ajax_post("/purchase/imporProUpd",JSON.stringify({id:id, warehouseId:wareId}), "application/json",
		function(data) {
			// 用户未登录
			if(data.code == 101){
				layer.msg(data.msg, {icon: 1, time: 2000}, function(){
					window.location.href = "/backstage/login.html"
				});
				return
			}
			if(data.suc){
				view_import_list();
			}else{
				layer.msg(data.msg,{icon:2,time:2000});
			}
		}
	);
});

/*实际价格=正价商品单价-（正价商品单价*赠品总计/正价商品总计）*/
function countPrice(){
	var priceTotal = 0.00;// 价格总计
	var qtyTotal = 0;// 商品数量总计

	// 计算总数量
	$(".import_purchaseList_table tbody tr").each(function(i,tr){
		qtyTotal += parseInt($(tr).find("td[name='price']").text());
	});

	// 总计
	$(".import_purchaseList_table tbody").find("tr[name='proTr']").each(function(i,tr){
		var qty = parseInt($(tr).find("td[name='qty']").text());
		var price = parseFloat($(tr).find("td[name='price']").text());
		priceTotal += price * qty;
	});

	$("#import_total").text(qtyTotal);
	$("#import_sumPrice").text(parseFloat(priceTotal).toFixed(2));
}

function import_prepared2CreateOrder(){
	// 数据回显
	$("#qtyTotal_span em").text($("#import_total").text());
	$("#priceTotal_span em").text($("#import_sumPrice").text());
	$("#money2Paid_div").find("input").val($("#import_sumPrice").text())

	$("#order_sure_btn").off("click")
	$("#order_sure_btn").on("click",import_generator);
}

// 创建采购单
function import_generator() {
	var inputId = $("#im_dis_acc").data("id")
	var money2Paid = $("#money2Paid_div").find("input[type='text']").val()

	var params = {inputId:inputId}
	var genetateType = $("#po_generateType").val()// 生成方式
	var payType = $("#po_payType").val()// 支付方式
	if(payType=='cash'){// 现金支付
		var cashAmount = $(".cashPayment").find("input[type='text']").val()
		if(!cashAmount){
			layer.msg("请输入金额！",{icon:2,time:2000});
			$(".cashPayment").find("input[type='text']").focus()
			return
		}
		// 金额正则
		if (!isMoneyPattern(cashAmount)) {
			layer.msg("请输入正确的金额！",{icon:2,time:2000});
			$(".cashPayment").find("input[type='text']").focus()
			return;
		}

		if(parseFloat(cashAmount) != money2Paid){
			layer.msg("输入的金额需与总计的金额一致！",{icon:2,time:2000});
			return;
		}
		params.money = cashAmount
	}
	params.genetateType=genetateType
	params.payType=payType

	// 出库的验证
	if(genetateType==2){// 完税仓商品发货
		var receiver = $(".microCapsule").find("input[name='receiver']").val()
		if(!receiver){
			layer.msg("请输入收货人姓名！",{icon:2,time:2000});
			$(".microCapsule").find("input[name='receiver']").focus()
			return
		}

		var telephone = $(".microCapsule").find("input[name='telephone']").val()
		if(!telephone){
			layer.msg("请输入手机号码！",{icon:2,time:2000});
			return
		}
		if(!checkTel(telephone)){
			layer.msg("输入手机号码格式错误", {icon: 2, time: 2000});
			$(".microCapsule").find("input[name='telephone']").focus()
			return
		}

		var province = $(".microCapsule").find("select[name='sale-province']").val()
		if(!province){
			layer.msg("请选择省！",{icon:2,time:2000});
			return
		}
		var provinceTxt = $(".microCapsule").find("select[name='sale-province'] option:selected").text()

		var city = $(".microCapsule").find("select[name='sale-city']").val()
		if(!city){
			layer.msg("请选择市！",{icon:2,time:2000});
			return
		}
		var cityTxt = $(".microCapsule").find("select[name='sale-city'] option:selected").text()

		var region = $(".microCapsule").find("select[name='sale-region']").val()
		if(!region){
			layer.msg("请选择区！",{icon:2,time:2000});
			return
		}
		var regionTxt = $(".microCapsule").find("select[name='sale-region'] option:selected").text()

		var addressDetail = $(".microCapsule").find("input[name='address-detail']").val()
		if(!addressDetail){
			layer.msg("请输入详细地址！",{icon:2,time:2000});
			$(".microCapsule").find("input[name='address-detail']").focus()
			return
		}
		if(addressDetail.length<5 || addressDetail.length>120){
			layer.msg("无需重复填写省市区，必须大于5个字符，小于120字！",{icon:2,time:2000});
			$(".microCapsule").find("input[name='address-detail']").focus()
			return
		}

		params.bbcPostage = $("#bbcPostage_span em").text()
		params.receiver=receiver
		params.telephone=telephone
		params.provinceId=province
		params.address=provinceTxt+" "+cityTxt+" "+regionTxt+" "+addressDetail
		params.postCode=$(".microCapsule").find("input[name='post-code']").val()
		params.shippingCode=$(".microCapsule").find("select[name='shippingMethod'] option:selected").attr("code") // 运送方式
		params.shippingName=$(".microCapsule").find("select[name='shippingMethod'] option:selected").text() // 运送方式
	}

	if ($.trim($("#oa_auditNo").val())) {
		params.oaAuditNo = $.trim($("#oa_auditNo").val());// os审批号
	}
	var remarks = $("#buseness_remarks").val();// 业务备注
	if($.trim(remarks) && $.trim(remarks).length>500){
		layer.msg("业务备注不能超过500个字！",{icon:2,time:2000});
		$("#buseness_remarks").focus();
		return
	}
	if ($.trim(remarks)) {
		params.remarks = remarks;
	}

	// 根据生成方式，调用不同的接口：1微仓进货，2完税仓商品发货
	var url = genetateType==2 ? "/purchase/deliverDutyPaidGoods" : "/purchase/inputOrder"

	// 下单
	// 按钮变灰
	$("#order_sure_btn").attr("disabled","disabled")
	ajax_post(url, JSON.stringify(params), "application/json", function(response) {
		// 用户未登录
		if(response.code == 101){
			layer.msg(response.msg, {icon: 1, time: 2000}, function(){
				window.location.href = "/backstage/login.html"
			});
			return
		}

		if(!response.suc){
			layer.msg(response.msg,{icon:2,time:2000});
			$("#order_sure_btn").removeAttr("disabled")
			return;
		}

		cleanImportDatas();

		var purchaseOrderNo = response.purchaseOrderNo
		if(payType == 'balance'){// 先生成采购单再现金支付
			layer.msg("下单成功，即将进行自动进行余额扣款！",{icon:1,time:2000},function(){
				payWithBalance(purchaseOrderNo)
			})
		}else if(payType == 'cash-online'){// 线下转账
			layer.msg("下单成功，即将跳转到线下付款页面",{icon:1,time:2000},function(){
				$("#input_purchase_order_div").hide()
				$(".place_order").hide();
				$('#transfer_offline_div').show()

				transferOffline(purchaseOrderNo)
			});
		}else{
			layer.msg("下单成功！",{icon:1,time:2000}, function(){
				$("p[position="+$("#forword").val()+"]").click();
			});
		}
	});
}

var skuMap = {};

// 清空导入的数据
function cleanImportDatas(){
	$("#input_order_pop_div").find('label[name="qtyTotal"]').text(0);
	$("#input_order_pop_div").find('label[name="moneyTotal"]').text("￥0.0");
	$("#input_order_pop_div").find("input[name='money']").val("")
	$("#input_order_pop_div").find("input[type='radio']:eq(0)").prop("checked",true);
	$("#input_order_pop_div").find("input[type='radio']:eq(1)").prop("checked",false);

	// 导入的数据清空
	$("#im_dis_acc").val("")
	$(".import_purchaseList_table tbody").empty()
	$("#import_total").val("0.00")
	$("#import_sumPrice").val("0.00")
}