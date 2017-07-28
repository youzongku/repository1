/**
 * 后台云仓发货-导入
 */
var cimport_prov_map={}, cimport_city_map={}, cimport_region_map={}
var layer, lay, cimport_webuploader, cimport_uploader = undefined;
var cimport_finalProCollection = {}

// 初始化中单订单导入
function init_import_cloud_sales(layerParam, laypageParam, webuploaderParam){
	cimport_finalProCollection = {}
	layer = layerParam;
	laypage = laypageParam;
	cimport_webuploader = webuploaderParam

	cimport_init_webuploader_register()
	cimport_init_uploader()
	cimport_getShippingMethod()//物流方式
}

// 注册
function cimport_init_webuploader_register() {
	cimport_webuploader.Uploader.register(
		{
			"before-send-file": "beforeSendFile"
		},
		{
			"beforeSendFile": function(file) {
				var task = cimport_webuploader.Base.Deferred();
				(new cimport_webuploader.Uploader()).md5File(file, 0, file.size)
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
						cimport_uploader.options.formData[file.id + "_md5"] = md5;
						task.resolve();
						console.log("beforeSendFile    [next step]");
					});
				return cimport_webuploader.Base.when(task);
			}
		}
	);
}

// 初始化
function cimport_init_uploader(){
	cimport_uploader = cimport_webuploader.create({
		// swf文件路径
		swf: '../js/webuploader/Uploader.swf',
		// 文件接收服务端。
		server: '/sales/manager/cloudsale/import',
		// 选择文件的按钮。可选。
		// 内部根据当前运行是创建，可能是input元素，也可能是flash.
		pick: '#cimport_picker',
		accept: {
			title: 'Excel',
			extensions: 'xls,xlsx'
		},
		fileNumLimit: 1,
		duplicate: false,
		formData: {}
	});

	// 文件选择限制
	cimport_uploader.on( 'error', function( type ) {
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

	cimport_uploader.on( 'uploadAccept', function( file, response ) {
		if(response.flag==false){
			layer.msg(response.msg, {icon: 2,time:2000});
		}
	});

	//上传成功事件
	cimport_uploader.on( 'uploadSuccess', function( file, response ) {
		console.log(response);
		if(!response.suc){
			layer.msg(response.msg, {icon: 2,time:5000});
		}else{
			layer.msg(response.msg, {icon: 1,time:5000});
			var email = response.email
			var comsumerType = response.comsumerType
			var distributionMode = response.distributionMode
			var proSetExpirDateList = response.proSetExpirDateList
			var proNotSetExpirDateList = response.proNotSetExpirDateList
			var giftSetExpirDateList = response.giftSetExpirDateList
			var giftNotSetExpirDateList = response.giftNotSetExpirDateList
			cimport_showImportProducts(email,comsumerType,distributionMode,proSetExpirDateList,proNotSetExpirDateList,giftSetExpirDateList,giftNotSetExpirDateList);
		}
	});

	// 当有文件被添加进队列的时候
	cimport_uploader.on( 'fileQueued', function( file ) {
		$("#thelist").append('<b id="'+file.id+'">'+file.name+'</b>');
	});

	//上传失败事件
	cimport_uploader.on( 'uploadError', function( file ) {
		layer.msg(file.name+'上传失败！', {icon: 5});
	});

	// 上传完毕
	cimport_uploader.on( 'uploadComplete', function( file ) {
		$("#thelist").empty();
		cimport_uploader.reset();
	});

	//上传文件到服务器
	$("#cimport-excel").on( 'click', function() {
		// 清空数据
		$("#cimport_selected_email").empty()
		$("#cimport_selected_email").removeAttr("disMode")
		$("#cimport_selected_email").removeAttr("distributorType")
		$("#cimport_productTable").find("tbody").empty()

		//获取配置的传入参数
		cimport_uploader.upload();
	});
}

/**
 * 显示导入的内容
 * @param proSetExpirDateList 设置了到期日期的正价商品
 * @param proNotSetExpirDateList 没有设置到期日期的正价商品
 * @param giftSetExpirDateList 设置了到期日期的赠品
 * @param giftNotSetExpirDateList 没有设置到期日期的赠品
 */
function cimport_showImportProducts(email,comsumerType,distributionMode,proSetExpirDateList,proNotSetExpirDateList,giftSetExpirDateList,giftNotSetExpirDateList) {
	$("#cimport_selected_email").text(email)
	$("#cimport_selected_email").attr("disMode",distributionMode)
	$("#cimport_selected_email").attr("distributorType",comsumerType)
	mergeIntoCollection(proSetExpirDateList)
	mergeIntoCollection(proNotSetExpirDateList)
	mergeIntoCollection(giftSetExpirDateList)
	mergeIntoCollection(giftNotSetExpirDateList)
	doInsertProductsHtml()

	// 获取省市区
	cimport_getProvinces()
}
function mergeIntoCollection(list) {
	if(list && list.length>0){
		for(var i in list){
			var pro = list[i]
			cimport_finalProCollection[cimport_getKey(pro.sku,pro.warehouseId,pro.isgift,pro.expirationDate)]=pro
		}
	}
}
function doInsertProductsHtml(){
	var $tbody = $("#cimport_productTable").find("tbody");
	$tbody.empty()
	var keys = Object.keys(cimport_finalProCollection)
	if(keys.length==0){
		return;
	}
	var html = ''
	for(var k in cimport_finalProCollection){
		var pro = cimport_finalProCollection[k];
		var expirationDate = pro.expirationDate ? pro.expirationDate : ""
		var stock = expirationDate ? pro.subStock : pro.stock // 有到期日期就选择到期日期的云仓库存
		html += '<tr>'+
			'<td name="title" imgUrl="'+ pro.imgUrl +'" title="'+pro.title+'">'+pro.title+(pro.isgift?"【<b style='color: red;'>赠</b>】":"")+'</td>'+
			'<td name="sku" isgift="'+pro.isgift+'" key="'+k+'">'+pro.sku+'</td>'+
			'<td name="interBarCode">'+pro.interBarCode+'</td>'+
			'<td name="warehouseName" warehouseId="'+pro.warehouseId+'">'+pro.warehouseName+'</td>'+
			'<td name="expirationDate" expirationDate="'+expirationDate+'">'+deal_with_illegal_value(expirationDate)+'</td>'+
			'<td name="stock">'+stock+'</td>'+
			'<td name="price">'+parseFloat(pro.price).toFixed(2)+'</td>'+
			'<td class="record_num">'+
			'<span onclick="cimport_reduceProductNum(this)">－</span>'+
			'<input onblur="cimport_inputProductNum(this)" class="input_num" style="text-align:center" type="text" data-qty="'+pro.qty+'" value="'+pro.qty+'">'+
			'<span onclick="cimport_increaseProductNum(this)">＋</span>'+
			'</td>'+
			'<td name="subtotal">'+changeTwoDecimal_f(pro.qty * pro.price)+'</td>'+
			'<td style="cursor: pointer" marketPrice="'+pro.marketPrice+'" onclick="cimport_delPro(this)">删除</td>'+
			'</tr>';
	}
	$tbody.html(html)
}

function cimport_inputProductNum(obj){
	var $trObj = $(obj).parent().parent();
	var key = $trObj.find("td[name='sku']").attr("key")
	var isgift = $trObj.find("td[name='sku']").attr("isgift")
	var stock = parseInt(cimport_finalProCollection[key].stock)
	var batchNumber = cimport_finalProCollection[key].batchNumber;
	var inputVal = $(obj).val(); // 输入数字
	var regNum = /^[1-9]\d*$/;
	if (!regNum.test(inputVal)) {
		layer.msg("请输入数字(必须是大于0的整数)", {icon: 2,time: 2000});
		if(isgift=='true'){
			inputVal = 1 // 赠品最小数量为1
		}else{
			inputVal = batchNumber
		}
	}
	if(isgift=='true'){
		if (parseInt(inputVal) < 1) {
			inputVal = 1 // 赠品最小数量为1
			layer.msg("赠品发货数量最小为1", {icon: 2,time: 2000});
		}
	}else{
		if (parseInt(inputVal) < parseInt(batchNumber)) {
			inputVal = batchNumber
			layer.msg("发货数量不小于起批量("+batchNumber+")", {icon: 2,time: 2000});
		}
	}
	if (parseInt(inputVal) > parseInt(stock)) {
		inputVal = stock
		layer.msg("发货数量需等于或小于云仓库存("+stock+")", {icon: 2,time: 2000});
	}
	cimport_finalProCollection[key].qty = parseInt(inputVal);
	cimport_recalculate($trObj,key)
}

function cimport_reduceProductNum(obj){
	var $trObj = $(obj).parent().parent();
	var key = $trObj.find("td[name='sku']").attr("key")
	var isgift = $trObj.find("td[name='sku']").attr("isgift")
	var batchNumber = cimport_finalProCollection[key].batchNumber;
	var inputVal = parseInt($(obj).parent().find("input").val());
	if(isgift=='true'){
		if (inputVal == 1) {
			inputVal = 1 // 赠品最小数量为1
			layer.msg("赠品发货数量最小为1", {icon: 2,time: 2000});
		}else{
			inputVal = inputVal-1
		}
	}else{
		if (inputVal == parseInt(batchNumber)) {
			inputVal = batchNumber
			layer.msg("发货数量不小于起批量("+batchNumber+")", {icon: 2,time: 2000});
		}else{
			inputVal = inputVal-1
		}
	}

	$(obj).parent().find("input").val(inputVal);
	cimport_finalProCollection[key].qty = inputVal;
	cimport_recalculate($trObj,key)
}

function cimport_increaseProductNum(obj){
	var $trObj = $(obj).parent().parent();
	var key = $trObj.find("td[name='sku']").attr("key")
	var stock = parseInt($trObj.find("td[name='stock']").text())
	var inputVal = parseInt($(obj).parent().find("input").val());
	// 原先数量就等于云仓库存
	inputVal = inputVal == stock ? stock : (inputVal + 1);// 数量加1
	$(obj).parent().find("input").val(inputVal);
	cimport_finalProCollection[key].qty = inputVal;
	cimport_recalculate($trObj,key)
}

function cimport_recalculate($trObj,key){
	// 计算此sku的小计
	$trObj.find("td[name='subtotal']").text(
		changeTwoDecimal_f(cimport_finalProCollection[key].qty * cimport_finalProCollection[key].price)
	)
	cimport_getFreight()
}

// 物流方式获取
function cimport_getShippingMethod(){
	//加载物流方式
	ajax_get("/inventory/getShippingMethod?wid=" +2024, "", "",
		function (shipResStr) {
			if(shipResStr.code){
				window.location.href = "/backstage/login.html";
			}
			var shipRes = $.parseJSON(shipResStr);
			if (shipRes.length < 1) {
				layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
				return
			}
			var options = ''
			for (var i = 0; i < shipRes.length; i++) {
				if (shipRes[i].default) {
					options += "<option value='" + shipRes[i].id + "' code='" + shipRes[i].methodCode + "' selected='selected'>" + shipRes[i].methodName + "</option>"
				}else{
					options += "<option value='" + shipRes[i].id + "' code='" + shipRes[i].methodCode + "'>" + shipRes[i].methodName + "</option>"
				}
			}
			$("#cimport_shippingMethod").html(options)
		}
	)
}

function cimport_calculateToBePaid() {
	var bbcPostage = parseFloat($("#cimport_sale_freight b").text());// 运费
	// 商品金额
	var productTotalAmount = 0.00
	$.each(cimport_finalProCollection, function (k, pro) {
		productTotalAmount += parseFloat(pro.price) * parseInt(pro.qty)
	})
	// 待支付金额，包含运费
	$("#cimport_toBePaid b").text(parseFloat(productTotalAmount + bbcPostage).toFixed(2))
}

// 运费计算
function cimport_getFreight() {
	var provinceId = $("#cimport_sale_province").val()
	if(!provinceId){
		return;
	}
	var keys = Object.keys(cimport_finalProCollection)
	if(keys.length==0){
		return;
	}
	var shippingCode = $("#cimport_shippingMethod").find("option:selected").attr("code")
	var warehouseId = cimport_finalProCollection[keys[0]]?cimport_finalProCollection[keys[0]].warehouseId:""
	if (shippingCode && keys.length > 0 && warehouseId == 2024) {
		var orderDetails = [];
		for (var k in cimport_finalProCollection) {
			var pro = cimport_finalProCollection[k];
			orderDetails.push({sku: pro.sku, num: pro.qty, costPrice: pro.disTotalCost});
		}
		var freightParam = {
			warehouseId: warehouseId, shippingCode: shippingCode,
			orderDetails: orderDetails, countryId: 44, //写死为中国的id
			provinceId: provinceId
		};
		ajax_post("/inventory/getFreight", JSON.stringify(freightParam), "application/json",
			function (freightResStr) {
				var freightRes = freightResStr;
				if (freightRes.result) {
					$("button[name='submit-sale']").each(function (i, e) {
						$(e).removeAttr("disabled").removeAttr("style").val("生成订单");
					})
					var fee = freightRes.msg;
					$("#cimport_sale_freight b").text(parseFloat(fee).toFixed(2))
					cimport_calculateToBePaid()
				} else {
					layer.msg("运费获取失败！" + freightRes.msg, {icon: 2, time: 2000});
					cimport_calculateToBePaid()// 获取运费失败，也要计算金额
					$("button[name='submit-sale']").each(function (i, e) {
						$(e).attr("disabled", true).css("cursor", "not-allowed").html("生成订单");
					})
				}
			}, function (XMLHttpRequest, textStatus) {
				layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
			}
		);
	} else {
		$("#cimport_sale_freight b").text(0.00);
		cimport_calculateToBePaid()
	}
}

// 省市区获取
function cimport_getProvinces() {
	$("#cimport_sale_province").empty()
	ajax_get("/member/getprovs", JSON.stringify(""), "application/json",
		function (data) {
			var options = ''
			var proId = ''
			cimport_prov_map = {}
			for (var i = 0; i < data.length; i++) {
				if(i==0){
					proId = data[i].id
					options += "<option selected value='" + data[i].id + "' >" + data[i].provinceName + "</option>"
				}else{
					options += "<option value='" + data[i].id + "' >" + data[i].provinceName + "</option>"
				}
				cimport_prov_map[data[i].provinceName.substr(0,data[i].provinceName.length-1)] = data[i].id;
			}
			$("#cimport_sale_province").html(options);
			cimport_getCities(proId);
		}
	);

	// 设置点击事件
	$("#cimport_sale_province").off("change")
	$("#cimport_sale_province").on("change",function(){
		cimport_getCities($(this).val())
	})
	$("#cimport_sale_city").off("change")
	$("#cimport_sale_city").on("change",function(){
		cimport_getAreas($(this).val())
	})
}

function cimport_getCities(proId) {
	if (!proId) {
		return;
	}
	cimport_getFreight();
	$("#cimport_sale_city").empty()
	ajax_get("/member/getcities", "proId=" + proId, "",
		function (data) {
			var cities = data.cities
			var options = ''
			var cityId = '',code=''
			cimport_city_map = {}
			for (var i = 0; i < cities.length; i++) {
				if(i==0){
					cityId = cities[i].id
					code = cities[i].zipCode
					options += "<option selected value='" + cityId + "' code='"+code+"' >" + cities[i].cityName + "</option>"
				}else{
					options += "<option value='" + cities[i].id + "' code='"+cities[i].zipCode+"' >" + cities[i].cityName + "</option>"
				}
				cimport_city_map[cities[i].cityName.substr(0,cities[i].cityName.length-1)] = cities[i].id;
			}
			$("#cimport_sale_postcode").val(code)
			$("#cimport_sale_city").html(options);
			cimport_getAreas(cityId)
		}
	);
}

function cimport_getAreas(cityId) {
	if (!cityId) {
		return;
	}
	$("#cimport_sale_postcode").val($("#cimport_sale_city").find("option:selected").attr("code"))
	$("#cimport_sale_region").empty()
	ajax_get("/member/getareas", "cityId=" + cityId, "",
		function (data) {
			$("#sale-region").empty();
			var areas = data.areas
			var options = ''
			cimport_region_map = {}
			for (var i = 0; i < data.areas.length; i++) {
				if(i==0){
					options += "<option selected value='" + areas[i].id + "' >" + areas[i].areaName + "</option>"
				}else{
					options += "<option value='" + areas[i].id + "' >" + areas[i].areaName + "</option>"
				}
				cimport_region_map[areas[i].areaName.substr(0,areas[i].areaName.length-1)] = areas[i].id;
			}
			$("#cimport_sale_region").html(options);
		}
	);
}

function cimport_validation() {
	var email = $.trim($("#cimport_selected_email").text());
	if (!email) {
		layer.msg('请选择分销商', {icon: 2, time: 2000});
		return false;
	}
	//必须有发货商品
	var skus = Object.keys(cimport_finalProCollection);
	if (skus.length == 0) {
		layer.msg('没有要发货的商品', {icon: 2, time: 2000});
		return false;
	}
	var code = $("#cimport_shippingMethod").find("option:selected").attr("code");
	var receiver = $("#cimport_receiverName").val();
	if (!receiver) {
		layer.msg('收货人不能为空', {icon: 2, time: 2000});
		return false;
	}
	//收货人手机验证
	var tel = $("#cimport_sale_tel").val();
	if (!checkTel(tel)) {
		layer.msg('收货人手机号码有格式错误，请输入有效手机号码', {icon: 2, time: 2000});
		return false;
	}
	//收货地址验证
	var province = $("#cimport_sale_province").val();
	if (!province) {
		layer.msg("请选择省！",{icon:2,time:2000});
		return
	}
	var city = $("#cimport_sale_city").val();
	if (!city) {
		layer.msg("请选择市！",{icon:2,time:2000});
		return
	}
	var region = $("#cimport_sale_region").val();
	if (!region) {
		layer.msg("请选择区！",{icon:2,time:2000});
		return
	}
	var addr = $("#cimport_sale_addr").val().trim();
	if (!addr || addr.length < 5) {
		layer.msg('收货地址不规范，请重新填写', {icon: 2, time: 2000});
		return false;
	}
	var postCode = $("#cimport_sale_postcode").val();
	if (postCode && !checkPost(postCode)) {
		layer.msg('请输入有效的收货地址邮政编码', {icon: 2, time: 2000});
		return false;
	}
	return true;
}

function cimport_getKey(sku, warehouseId, isgift, expirationDate){
	var key = sku;
	if(warehouseId){
		key = key + "_" + warehouseId
	}
	key = key + "_" + isgift
	if(expirationDate){
		key = key + "_" + expirationDate
	}
	return key;
}

// 模板下载
function exportCloudSaleTemplate(){
	window.location.href = "/sales/manager/cloudsale/template";
}

function cimport_checkdBanlance(obj){
	var email = $.trim($("#cimport_selected_email").text())
	if ($(obj).prop("checked")) {
		if (email) {
			ajax_get("/member/getAccount", "email=" + email, "",
				function (data) {
					var banlance = parseFloat(data.balance);
					if (banlance && banlance < parseFloat($("#cimport_toBePaid b").text())) {
						layer.msg("该分销商余额不充足无法扣除运费！", {icon: 5, time: 2000});
						$(obj).prop("checked",false);
					}
				}
			);
		} else {
			$(obj).prop("checked",false);
			layer.msg("请先选择分销商！", {icon: 2, time: 2000});
		}
	}
}

//自动匹配收货人信息
//广东省  深圳市  龙岗区  坂田街道桥联东路顺兴楼1003  518116  黄安纳  13603064940        千牛格式
//李心瑞，13999889672，新疆维吾尔自治区 乌鲁木齐市 沙依巴克区 红庙子街道西城街荣和城二期28号楼2单元1701 ，830000      网页格式
function cimport_sureMatch(obj){
	var template = $("#cimport_address_template").val();//选择的地址格式
	if (template == 0) {
		layer.msg("请选择地址格式！", {icon: 5, time: 2000});
		return;
	}
	var info = $(obj).parent().find("input[name=waitInfo]").val().trim();
	if(!info){
		layer.msg("请输入收货信息！", {icon: 5, time: 2000});
		$(obj).parent().find("input[name=waitInfo]").focus();
		return;
	}

	var flag = false;
	if (template == 1) {//淘宝千牛格式
		var waitInfo = info.split("  ");
		var length = waitInfo.length
		if (length >= 7) {
			$("#cimport_sale_province").val(cimport_getAddressId(waitInfo[0],cimport_prov_map)).change();
			if($("#cimport_sale_province").val()){
				$("#cimport_sale_city").val(cimport_getAddressId(waitInfo[1],cimport_city_map)).change();
				if($("#cimport_sale_city").val()){
					flag = true;
					$("#cimport_sale_region").val(cimport_getAddressId(waitInfo[2],cimport_region_map));
				}
			}
			$("#cimport_receiverName").val(waitInfo[length-2]);
			$("#cimport_sale_tel").val(waitInfo[length-1]);
			$("#cimport_sale_postcode").val(waitInfo[length-3]);
			$("#cimport_sale_addr").val(waitInfo[3]);
		}
	} else {//淘宝网页格式
		var waitInfo = info.split("  ");
		if (waitInfo.length >= 4) {
			var addrs = waitInfo[2].trim().split(" ");
			if (addrs.length >= 4) {
				$("#cimport_sale_province").val(cimport_getAddressId(addrs[0],cimport_prov_map)).change();
				if($("#cimport_sale_province").val()){
					$("#cimport_sale_city").val(cimport_getAddressId(addrs[1],cimport_city_map)).change();
					if($("#cimport_sale_city").val()){
						flag = true;
						$("#cimport_sale_region").val(cimport_getAddressId(addrs[2],cimport_region_map));
					}
				}
				var address = "";
				$.each(addrs,function(i,item){
					if(i>=3) {
						address += item;
					}
				});
				$("#cimport_sale_addr").val(address);
			}
			$("#cimport_receiverName").val(waitInfo[0]);
			$("#cimport_sale_tel").val(waitInfo[1]);
			$("#cimport_sale_postcode").val(waitInfo[3]);
		}
	}
	if (!flag) {
		layer.msg("输入格式不正确或省市区未匹配！", {icon: 5, time: 2000});
	}
}

function cimport_getAddressId(key,map){
	if(map[key]){
		return map[key];
	}

	key = key.substr(0,key.length-1);
	if(key){
		return map[key];
	}
}

//删除某个已选产品
function cimport_delPro(obj){
	var $tr = $(obj).parent();
	var finalKey = cimport_getKey($tr.find("td[name='sku']").text(),
		$tr.find("td[name='warehouseName']").attr("warehouseId"),$tr.find("td[name='sku']").attr("isgift"),
		$tr.find("td[name='expirationDate']").attr("expirationDate"))
	delete cimport_finalProCollection[finalKey];
	doInsertProductsHtml()
	cimport_getFreight()
}