/**
 * 后台云仓发货
 */
var layer, laypage, prov_map, city_map, region_map;
var needSelectExpirationText = '当前选择的商品存在多个到期日期，请确认是否需要选择商品到期日期<br>' +
	'如果不需要选择，系统将按照现有业务逻辑选择商品<br>' +
	'如果需要选择，请点击选择到期日期，然后逐步选择相应商品的到期日期数量，' +
	'且您已经选择的数量将被重置。';
var provinceIdForFreight = 0;//完税产品计算运费用的省id
var c_tempProCollection = {};
var c_finalProCollection = {}

//初始化中单订单录入
function init_cloud_sales(lay, layd, salesOrderNoParam, needExpirationDateParam){
	c_tempProCollection = {}
	c_finalProCollection = {}
	layer = lay;
	laypage = layd;

	// 编辑的
	if(salesOrderNoParam){
		loadSOByOrderNo(salesOrderNoParam, needExpirationDateParam)
	}else{
		init_c_AreaSel();
		cso_getShippingMethod()
	}

	init_func();
}

// 加载发货单，进行数据回显
function loadSOByOrderNo(salesOrderNo, needExpirationDate){
	if(!salesOrderNo){
		layer.msg("非法操作，未选择要复制的发货单！",{icon:2,time:2000});
		return
	}

	ajax_post("/sales/manager/getSaleOrderByNo", JSON.stringify({salesOrderNo:salesOrderNo}), "application/json", function (data) {
		console.log(data)
		var sm = data.saleMain
		var sb = data.saleBase
		// 第一步：分销商
		var $selectedEmailObj = $(".record_sendList_line #selected_email");
		$selectedEmailObj.val(sm.email);
		$selectedEmailObj.attr("disMode",sm.disMode);
		$selectedEmailObj.attr("distributorType",sm.distributorType);

		// 第二步：商品
		var details = data.details;
		var sku2Stock = data.sku2Stock;
		c_tempProCollection = {};
		$.each(details,function(i,e){
			var stock = 0, batchNumber = 0;
			for(var key in sku2Stock){
				if(key==e.sku){
					stock = sku2Stock[key].stock;
					batchNumber = sku2Stock[key].batchNumber;
					break;
				}
			}
			var isgift = e.isgift ? e.isgift : false;
			c_tempProCollection[e.sku+"|"+isgift] = {
				sku: e.sku,
				batchNumber: batchNumber,// 起批量
				title: e.productName,
				interBarCode: e.interBarCode,
				warehouseName: e.warehouseName,
				warehouseId: e.warehouseId,
				stock: stock,
				price: e.finalSellingPrice,
				qty: e.qty,
				isgift: isgift,
				marketPrice: e.marketPrice,
				imgUrl: e.productImg,
				interBarCode: e.interBarCode
			};
		})
		c_showChoiceProducts(needExpirationDate);

		// 第三步：收货信息
		$(".receiveName").val(sb.receiver);
		$(".sale-tel").val(sb.tel);
		$(".sale-postcode").val(sb.postCode);
		// 广东省 深圳市 龙岗区 坂田华城百货1号
		var address = sb.address
		var addressArray = address.split(" ");
		// 地址选择
		init_c_AreaSel(addressArray[0],addressArray[1],addressArray[2])
		$("#addr").val(addressArray.slice(3).join(" "))

		// 第四步：支付信息
		cso_getShippingMethod(sb.logisticsTypeCode)
		// 计算运费
		c_getFreight()
	})
}

function cso_getShippingMethod(logisticsTypeCode){
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
				if(logisticsTypeCode){// 复制的
					if(shipRes[i].methodCode==logisticsTypeCode){
						options += "<option value='" + shipRes[i].id + "' code='" + shipRes[i].methodCode + "' selected='selected'>" + shipRes[i].methodName + "</option>"
					}else{
						options += "<option value='" + shipRes[i].id + "' code='" + shipRes[i].methodCode + "'>" + shipRes[i].methodName + "</option>"
					}
				}else{
					if (shipRes[i].default) {
						options += "<option value='" + shipRes[i].id + "' code='" + shipRes[i].methodCode + "' selected='selected'>" + shipRes[i].methodName + "</option>"
					}else{
						options += "<option value='" + shipRes[i].id + "' code='" + shipRes[i].methodCode + "'>" + shipRes[i].methodName + "</option>"
					}
				}
			}
			$(".record_sendList_line .shippings").append(options)
			$("#thirdPostfee").hide();
		}
	)
}

function init_func(){
	//仓库选择
    $("#warehose_id").change(function () {
        getProDetail($(".record_sendList_line #selected_email").val().trim());//目前需求只展示完税仓中的商品（深圳仓）
    });

    //类目选择
    $(".add_sendProduct_pop .product_select").change(function(){
        getProDetail($(".record_sendList_line #selected_email").val().trim());
    })

    //搜索按钮
    $(".add_sendProduct_pop .searchButton").click(function(){
        getProDetail($(".record_sendList_line #selected_email").val().trim());
    });

    $("#forword").val(3);
}

$("body").on("blur","#thirdPostfee",function(){
  	var fee = $(this).val();
   	var flag = fee?isMoneyPattern()&&fee>0:false;
    if(!flag){
        layer.msg("请输入有效的[<b style='color:#E74C3C'>第三方物流费用</b>]（至多两位小数,且必须大于零）", {icon: 2, time: 2500});
		$(this).val("");
		$(this).focus();
    }
    sumOdTotal();
});

//选择分销商
$('body').on('click', '#sco_importChoice_disturb', function () {
	$("#sco_distract_choice_pop_div .searchInput").val("");
	layer.open({
		type: 1,
		skin: 'layui-layer-rim',
		area: ['580px', '580px'],
		content: $('#sco_distract_choice_pop_div'),
		btn: ['确定', '取消'],
		title: '选择分销商',
		success: function () {
			c_tempProCollection = {};
			c_finalProCollection = {};
		},
		yes: function (i, currdom) {
			$(".record_sendList_table tbody").empty();
			var node = $(".operation_table").find("input[type='radio']:checked");
			$(".record_sendList_line #selected_email").val(node.attr("email"));
			$(".record_sendList_line #selected_email").attr("disMode", node.attr("disMode"));
			$(".record_sendList_line #selected_email").attr("distributorType", node.attr("distributorType"));
			layer.close(i);
		}
	});
	get_dis_list(1);
})

//得到分销商列表
function get_dis_list(curr){
	var params = {
		currPage: (curr == undefined || curr == 0) ? 1 : curr, pageSize: 10,
		search: $("#sco_distract_choice_pop_div .searchInput").val().trim(),
		notType: 3//分销商不为内部分销商
	};
	ajax_post_sync("/member/relatedMember",JSON.stringify(params),"application/json",function(data) {
		if (data) {
			if (data.mark == 2 || data.mark == 3) {
				c_insert_users_list(data.data.list);
				c_init_dis_pagination(data.data);
			} else if (!data.suc) {
				window.location.href = "login.html";
			} else if (data.mark == 1) {
				layer.msg("获取分销商失败", {icon: 2, time: 1000});
			}
		}
	})
}

//插入分销商列表
function c_insert_users_list(list){
	var $tbody = $('#sco_distract_choice_pop_div .operation_table').find("tbody");
	$tbody.empty();
	var itemHTML = '';
	$.each(list, function(i, item) {
		itemHTML +=
			'<tr>'+
			'<td style="width: 10%">' +
			'<input style="cursor: pointer" '+(item.isFrozen?"disabled":"")+' type="radio" name="email" email="'+item.email+'" ' +
			'disMode="'+item.distributionMode+'" distributorType="'+item.comsumerType+'">' +
			'</td>'+
			'<td style="width: 35%">' + deal_with_illegal_value(item.email)+(item.isFrozen?'【<b style="color: red;">冻结</b>】':"") + '</td>'+
			'<td style="width: 20%">' + deal_with_illegal_value(item.nick) + '</td>'+
			'<td style="width: 35%">' + deal_with_illegal_value(item.telphone) + '</td>'+
			'</tr>'
	});
	$tbody.append(itemHTML);
}

//分销商列表分页展示
function c_init_dis_pagination(page){
	if ($("#sales_distribution_pagination")[0] != undefined) {
		$("#sales_distribution_pagination").empty();
		laypage({
			cont: 'sales_distribution_pagination',
			pages: page.totalPage,
			curr: page.currPage,
			groups: 5,
			skin: '#55ccc8',
			first: '首页',
			last: '尾页',
			prev: '上一页',
			next: '下一页',
			skip: true,
			jump: function(obj, first) {
				//first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
				$("#sales_distribution_pagination .laypage_total").find("input[type='number']").css("width","40px");
				if(!first){
					get_dis_list(obj.curr);
				}
			}
		});
	}
}

$('body').on('click', '#show_erp_sto', function () {
	showErpStock();
});

//选择商品
$('body').on('click', '#pro_choice,#gift_choice', function () {
	var email = $(".record_sendList_line #selected_email").val().trim();
	if (email == undefined ||email == "") {
		layer.msg("请先选择分销商",{icon:2,time:2000});
		return;
	}
	isgift =  $(this).attr("type")==1;// 0为正价商品 1为赠品
	layer.open({
		type: 1,
		skin: 'layui-layer-demo', //样式类名
		title: false, //不显示标题
		shift: 2,
		shadeClose: true, //开启遮罩关闭
		content: $('.add_sendProduct_pop'),
		area: ['850px', '500px'],
		btn: ["确认添加"],
		yes: function(index) {
			var listPop = $(".add_sendProduct_pop");
			var $trNode;
			listPop.find("input[name='product-check']:checked").each(function(i, e) {
				$trNode = $(e).parents("tr");
				c_tempProCollection[$trNode.find("td[name='sku']").text() + "|" + isgift] = {
					sku: $trNode.find("td[name='sku']").text(),
					batchNumber: $(e).attr("batchNumber"),
					title: $trNode.find("td[name='title']").text(),
					interBarCode: $trNode.find("td[name='interBarCode']").text(),
					warehouseName: $trNode.find("td[name='warehouseName']").text(),
					warehouseId: $trNode.find("td[name='warehouseName']").attr("warehouseId"),
					stock: $trNode.find("td[name='stock']").text(),
					price: isgift ? 0.00 : parseFloat($trNode.find("td[name='price']").text()),
					qty: isgift ? 1 : $(e).attr("batchNumber"),// 如果是赠品，数量从1开始
					isgift: isgift,
					marketPrice: $trNode.find("td[name='marketPrice']").text(),
					imgUrl: $trNode.find("td[name='title']").attr("imgUrl")
				};
			});
			// 判断是否要选择到期日期
			layer.confirm(needSelectExpirationText, {
				btn: ['不需要选择到期日期', '选择到期日期'] //按钮
			}, function () {
				// 不需要选择到期日期
				c_showChoiceProducts(false);
				c_getFreight();
			}, function () {
				// 选择到期日期
				c_showChoiceProducts(true);
				c_getFreight();
			});
		}
	})

	$(".add_sendProduct_pop .searchInput").val("");
	loadWare();//仓库
	loandCategory();//类目
	getProDetail(email);
	//全选按钮---商品弹出框
	$(".add_sendProduct_pop").on("click", "input[name='all-check']", function(){
		$("input[name='product-check']").prop("checked", this.checked);
	});

	$(".add_sendProduct_pop").on("click", "input[name='product-check']", function(){
		$("input[name='all-check']").prop("checked" , $("input[name='product-check']").length == $("input[name='product-check']").filter(":checked").length ? true :false);
	});
});

//加载所有商品
function getProDetail(email,curr){
	$(".my_micro_product tbody").empty();
	$("#myProducts_pagination").empty();
	$("input[name='all-check']").prop("checked",false);//全选框
	if(!email){
		email = $(".record_sendList_line #selected_email").val().trim()
	}
	var param = {
		email: email, warehouseId: 2024,//目前完税仓
		currPage: curr == undefined ? 1 : curr, pageSize: 10,
		model: $(".record_sendList_line #selected_email").attr("disMode")
	}
	var title = $(".add_sendProduct_pop .searchInput").val().trim();
	var categoryId = $(".add_sendProduct_pop .product_select").val();
	if (categoryId && categoryId != 0) {
		param.categoryId = parseInt(categoryId);
	}
	//判断是否是逗号隔开
	var regStr = /,/;
	if (title != null && title != '' && title != undefined) {
		if (regStr.test(title)) {
			param.skuList = title.split(",");
		} else {
			param.title = title;
		}
	}
	var loading_index = layer.load(1, {shade: 0.5});
	ajax_post("/product/api/getProducts",JSON.stringify({data:param}),"application/json",function (data) {
		var reData = data.data;
		var result = reData.result;
		insProDetail(result);
		init_myPro_pagin(email,reData);
		layer.close(loading_index)
	})
}

var total = 0;
function sumOdTotal(){
	total = 0;
	// 计算所选商品的金额，不计算赠品的
	$.each(c_finalProCollection, function (i, item) {
		total += item.price * item.qty;
	});
	var code = $(".shippings").find("option:selected").attr("code")
	if (code != 'BBC-TPL') {
		total += Number($(".sale_freight").find("b").text())
	} else {
		total += Number($("#thirdPostfee").val());
	}
	$("#auto_check").prop("checked", false);
	$("#total").text(total.toFixed(2));
}

//展示已选商品
function c_showChoiceProducts(needExpirationDate){
	$(".record_sendList_table tbody").empty();
	layer.closeAll()
	if(needExpirationDate){// 选择到期日期
		// 去查询微仓商品和云仓商品的到期日期
		var selectedProducts = []
		for(var i in c_tempProCollection){
			selectedProducts.push(c_tempProCollection[i])
		}
		ajax_post_sync("/sales/manager/inputso/expirationdates/cloud",JSON.stringify({selectedProducts: selectedProducts}), "application/json",function(response){
			var result = response.result;
			if(response.suc && result.length>0){
				for(var i in result){
					var finalKey = getKey(result[i].sku,result[i].warehouseId,result[i].isgift, result[i].expirationDate)
					if(!c_finalProCollection[finalKey]){
						c_finalProCollection[finalKey] = result[i]
					}
				}
			}else{
				layer.msg("获取商品到期日期失败", {icon: 2,time: 2000});
				c_tempProCollection = {}// 清除
				return
			}
		})
	}else{// 系统默认时间
		for(var k in c_tempProCollection){
			var finalKey = getKey(c_tempProCollection[k].sku,c_tempProCollection[k].warehouseId,c_tempProCollection[k].isgift,c_tempProCollection[k].expirationDate)
			if(!c_finalProCollection[finalKey]){
				c_finalProCollection[finalKey] = c_tempProCollection[k]
			}
		}
	}

	c_tempProCollection = {}// 清除
	var html = "";
	var i = 1;
	for (var k in c_finalProCollection) {
		var pro = c_finalProCollection[k];
		var expirationDate = pro.expirationDate ? pro.expirationDate : ""
		var stock = expirationDate ? pro.subStock : pro.stock // 有到期日期就选择到期日期的云仓库存
		html += '<tr>'+
			'<td>'+(i++)+'</td>'+
			'<td name="sku" isgift="'+pro.isgift+'" key="'+k+'">'+pro.sku+'</td>'+
			'<td name="title" imgUrl="'+ pro.imgUrl +'" title="'+pro.title+'">'+pro.title+(pro.isgift?"【<b style='color: red;'>赠</b>】":"")+'</td>'+
			'<td name="interBarCode">'+pro.interBarCode+'</td>'+
			'<td name="warehouseName" warehouseId="'+pro.warehouseId+'">'+pro.warehouseName+'</td>'+
			'<td name="stock">'+stock+'</td>'+
			'<td name="expirationDate" expirationDate="'+expirationDate+'">'+deal_with_illegal_value(expirationDate)+'</td>'+
			'<td name="price">'+pro.price+'</td>'+
			'<td><input onblur="cso_inputFinalPrice(this)" originFinalPrice="'+pro.price+'" value="'+pro.price+'" class="final-price" type="text" style="text-align:center;width: 80px;"></td>'+
			'<td class="record_num">'+
			'<span onclick="cso_reduceProductNum(this)">－</span>'+
			'<input onblur="cso_inputProductNum(this)" class="input_num" style="text-align:center" type="text" data-qty="'+pro.qty+'" value="'+pro.qty+'">'+
			'<span onclick="cso_increaseProductNum(this)">＋</span>'+
			'</td>'+
			'<td name="subtotal">'+changeTwoDecimal_f(pro.qty * pro.price)+'</td>'+
			'<td style="cursor: pointer" marketPrice="'+pro.marketPrice+'" onclick="delPro(this)">删除</td>'+
			'</tr>';
	}
	$(".record_sendList_table tbody").append(html);
}

//微仓商品分页展示
function init_myPro_pagin(email,page){
	if ($("#myProducts_pagination")[0] != undefined) {
		$("#myProducts_pagination").empty();
		laypage({
			cont: 'myProducts_pagination',
			pages: page.totalPage,
			curr: page.currPage,
			groups: 5,
			skin: '#55ccc8',
			first: '首页',
			last: '尾页',
			prev: '上一页',
			next: '下一页',
			skip: true,
			jump: function(obj, first) {
				//first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
				if(!first){
					getProDetail(email,obj.curr);
				}
			}
		});
	}
}

//插入产品详细信息
function insProDetail(list){
	var $tbody = $(".my_micro_product tbody");
	$tbody.empty();
	if(!list || list.length==0){
		$(".my_micro_product tbody").append("<tr style='text-align: center;'><td colspan='9'>暂无商品</td></tr>");
		return
	}

	var html = "";
	$.each(list, function (i, item) {
		if(item.isSpecial){
			item.disPrice = item.specialSale;
		}
		item.disPrice = item.disPrice == null ? null : parseFloat(item.disPrice).toFixed(2);

		var pro = c_finalProCollection[getKey(item.csku,item.warehouseId,isgift)];
		var microStock = item.microStock == 0 ? 1 : item.microStock;
		var notEnoughStock = (item.disPrice ==null || item.stock == 0)
		var disabledHtml = notEnoughStock ? "disabled='disabled'" : "";
		// 已选中过
		var selectedBefore = !disabledHtml && pro;
		disabledHtml = selectedBefore ? "disabled='disabled'" : "";
		html += '<tr class="list_my_product">'+
			'<td name="sku">'+item.csku+'</td>'+
			'<td name="title" imgurl="'+ item.imageUrl +'">'+item.ctitle+'</td>'+
			'<td name="interBarCode">'+item.interBarCode+'</td>'+
			'<td name="warehouseName" warehouseId="'+item.warehouseId+'">'+item.warehouseName+'</td>'+
			'<td name="stock">'+item.stock+'</td>'+
			'<td name="microStock">'+item.microStock+'</td>'+
			'<td name="price">'+deal_with_illegal_value(item.disPrice)+'</td>'+
			'<td name="marketPrice">'+item.localPrice+'</td>'+
			'<td><input '+disabledHtml+' batchNumber="'+item.batchNumber+'" title="'+(notEnoughStock ? "库存不足" : (selectedBefore?"已选择过":""))+'" style="cursor:pointer" type="checkbox" name="product-check"></td>'+
			'</tr>';
	});
	$tbody.html(html);
}

//加载商品类目
function loandCategory(){
	$(".add_sendProduct_pop .product_select").empty();
	ajax_get("/product/api/realCateQuery?level=1", "", "application/json",
		function (data) {
			var cateHtml = "<option value='0' selected>所有商品</option>";
			if (data && data.length > 0) {
				$.each(data, function (i, item) {
					cateHtml += "<option value='" + item.iid + "'>" + item.cname + "</option>";
				});
				$(".add_sendProduct_pop .product_select").append(cateHtml);
			}
		}
	);
}

//加载仓库
function loadWare(){
	$(".add_sendProduct_pop .warehuose_select").empty();
	$.ajax({
		url: "/inventory/queryWarehouse",
		type: "get",
		dataType: "json",
		async: false,
		success: function (data) {
			if (data.length > 0) {
				var wareHtml = "";
				for (var i in data) {
					if (data[i].id == 2024) {
						wareHtml += "<option value='" + data[i].id + "'>" + data[i].warehouseName + "</option>";
					}
				}
				$(".add_sendProduct_pop .warehuose_select").append(wareHtml);
			}
		}
	});
}

function cso_inputFinalPrice(obj){
	var $srcTr = $(obj).parents("tr")
	var sku = $srcTr.find("td[name='sku']").text()
	var warehouseId = $srcTr.find("td[name='warehouseName']").attr("warehouseId")
	var inputVal = $.trim($(obj).val())
	var originFinalPrice = parseFloat($(obj).attr("originFinalPrice"))
	if(!inputVal){
		layer.msg("真实售价不能为空", {icon: 2,time: 2000});
		$(obj).val(originFinalPrice)
		cso_sameSkuSameFinalPrice(sku,warehouseId,originFinalPrice)
		return;
	}
	if(!isMoneyPattern(inputVal)){
		layer.msg("真实售价格式错误（保留两位小数）", {icon: 2,time: 2000});
		$(obj).val(originFinalPrice)
		cso_sameSkuSameFinalPrice(sku,warehouseId,originFinalPrice)
		return;
	}

	cso_sameSkuSameFinalPrice(sku,warehouseId,inputVal)
}

function cso_sameSkuSameFinalPrice(sku, warehouseId, finalPriceVal){
	if(!sku || !warehouseId) {
		return;
	}
	// 把其他相同sku、仓库的商品的真实售价也改了（同一个商品真实售价一样）2017.3.16
	var skuTemp = '', warehouseIdTemp = ''
	$(".record_sendList_table tbody").find("tr").each(function (i,e) {
		skuTemp = $(e).find("td[name='sku']").text()
		warehouseIdTemp = $(e).find("td[name='warehouseName']").attr("warehouseId")
		if(skuTemp==sku && warehouseIdTemp==warehouseId){
			$(e).find("input[class='final-price']").val(finalPriceVal)
		}
	})
}

function cso_inputProductNum(obj){
	var $trObj = $(obj).parent().parent();
	var key = $trObj.find("td[name='sku']").attr("key")
	var isgift = $trObj.find("td[name='sku']").attr("isgift")
	var stock = parseInt(c_finalProCollection[key].stock)
	var batchNumber = c_finalProCollection[key].batchNumber;
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
	c_finalProCollection[key].qty = parseInt(inputVal);
	c_recalculate($trObj,key)
}

function cso_reduceProductNum(obj){
	var $trObj = $(obj).parent().parent();
	var key = $trObj.find("td[name='sku']").attr("key")
	var isgift = $trObj.find("td[name='sku']").attr("isgift")
	var batchNumber = c_finalProCollection[key].batchNumber;
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
	c_finalProCollection[key].qty = inputVal;
	c_recalculate($trObj,key)
}

function cso_increaseProductNum(obj){
	var $trObj = $(obj).parent().parent();
	var key = $trObj.find("td[name='sku']").attr("key")
	var stock = parseInt($trObj.find("td[name='stock']").text())
	var inputVal = parseInt($(obj).parent().find("input").val());
	// 原先数量就等于云仓库存
	inputVal = inputVal == stock ? stock : (inputVal + 1);// 数量加1
	$(obj).parent().find("input").val(inputVal);
	c_finalProCollection[key].qty = inputVal;
	c_recalculate($trObj,key)
}

function c_recalculate($trObj,key){
	// 计算此sku的小计
	$trObj.find("td[name='subtotal']").text(
		changeTwoDecimal_f(c_finalProCollection[key].qty * c_finalProCollection[key].price)
	)
	sumOdTotal();
	c_getFreight()
}

//删除某个已选产品
function delPro(obj){
	var $tr = $(obj).parent();
	var finalKey = getKey($tr.find("td[name='sku']").text(),
		$tr.find("td[name='warehouseName']").attr("warehouseId"),$tr.find("td[name='sku']").attr("isgift"),
		$tr.find("td[name='expirationDate']").attr("expirationDate"))
	delete c_finalProCollection[finalKey];
	c_showChoiceProducts();
	c_getFreight();
}

$("body").on("change", "#sale-province", function () {
	cl_citySel();
	$(".sale-postcode").val($("#sale-city").find("option[value="+$("#sale-city").val()+"]").attr("code"));
	provinceIdForFreight = $("#sale-province").val();
	c_getFreight();
});

function init_c_AreaSel(pName, cName, rName) {
	ajax_get("/member/getprovs", JSON.stringify(""), "application/json",
		function (data) {
			$("#sale-province").empty();
			$("#sale-city").empty();
			$("#sale-region").empty();
			prov_map = {};
			var options = ''
			for (var i = 0; i < data.length; i++) {
				if(pName && pName==data[i].provinceName){
					options += "<option value='" + data[i].id + "' selected='selected' >" + data[i].provinceName + "</option>"
				}else{
					options += "<option value='" + data[i].id + "' >" + data[i].provinceName + "</option>"
				}
				prov_map[data[i].provinceName.substr(0,data[i].provinceName.length-1)] = data[i].id;
			}
			$("#sale-province").append(options);
			cl_citySel(cName, rName);
			$(".sale-postcode").val($("#sale-city").find("option[value="+$("#sale-city").val()+"]").attr("code"));
			provinceIdForFreight = $("#sale-province").val();
			c_getFreight();
		}
	);
}

function cl_citySel(cName, rName) {
	if($("#sale-province").val()){
		ajax_get("/member/getcities", "proId=" + $("#sale-province").val(), "",
			function (data) {
				$("#sale-city").empty();
				var cities = data.cities
				city_map = {};
				var options = ''
				for (var i = 0; i < cities.length; i++) {
					if(cName && cName==cities[i].cityName){
						options += "<option selected value='" + cities[i].id + "' code='"+cities[i].zipCode+"' >" + cities[i].cityName + "</option>"
					}else{
						options += "<option value='" + cities[i].id + "' code='"+cities[i].zipCode+"' >" + cities[i].cityName + "</option>"
					}
					city_map[cities[i].cityName.substr(0,cities[i].cityName.length-1)] = cities[i].id;
				}
				$("#sale-city").append(options);
				cl_regionSel(rName);
			}
		);
	}
}

//区级下拉框联动
$("body").on("change", "#sale-city", function () {
	cl_regionSel();
});

function cl_regionSel(rName) {
	if($("#sale-city").val()){
		ajax_get("/member/getareas", "cityId=" + $("#sale-city").val(), "",
			function (data) {
				$("#sale-region").empty();
				var areas = data.areas
				region_map = {};
				var options = ''
				for (var i = 0; i < data.areas.length; i++) {
					if(rName && rName==areas[i].areaName){
						options += "<option value='" + areas[i].id + "' selected='selected' >" + areas[i].areaName + "</option>"
					}else {
						options += "<option value='" + areas[i].id + "' >" + areas[i].areaName + "</option>"
					}
					region_map[areas[i].areaName.substr(0,areas[i].areaName.length-1)] = areas[i].id;
				}
				$("#sale-region").append(options);
			}
		);
	}
}

//自动匹配收货人信息
//广东省  深圳市  龙岗区  坂田街道桥联东路顺兴楼1003  518116  黄安纳  13603064940        千牛格式
//李心瑞，13999889672，新疆维吾尔自治区 乌鲁木齐市 沙依巴克区 红庙子街道西城街荣和城二期28号楼2单元1701 ，830000      网页格式
function _sureMatch(obj){
	var template = $("#address-template").find("option:selected").val();//选择的地址格式
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
			$("#sale-province").val(getAddressId(waitInfo[0],prov_map)).change();
			if($("#sale-province").val()){
				$("#sale-city").val(getAddressId(waitInfo[1],city_map)).change();
				if($("#sale-city").val()){
					flag = true;
					$("#sale-region").val(getAddressId(waitInfo[2],region_map));
				}
			}
			$(".receiveName").val(waitInfo[length-2]);
			$(".sale-tel").val(waitInfo[length-1]);
			$(".sale-postcode").val(waitInfo[length-3]);
			$("#addr").val(waitInfo[3]);
		}
	} else {//淘宝网页格式
		var waitInfo = info.split("，");
		if (waitInfo.length >= 4) {
			var addrs = waitInfo[2].trim().split(" ");
			if (addrs.length >= 4) {
				$("#sale-province").val(getAddressId(addrs[0],prov_map)).change();
				if($("#sale-province").val()){
					$("#sale-city").val(getAddressId(addrs[1],city_map)).change();
					if($("#sale-city").val()){
						flag = true;
						$("#sale-region").val(getAddressId(addrs[2],region_map));
					}
				}
				var address = "";
				$.each(addrs,function(i,item){
					if(i>=3) {
						address += item;
					}
				});
				$("#addr").val(address);
			}
			$(".receiveName").val(waitInfo[0]);
			$(".sale-tel").val(waitInfo[1]);
			$(".sale-postcode").val(waitInfo[3]);
		}
	}
	if (!flag) {
		layer.msg("输入格式不正确或省市区未匹配！", {icon: 5, time: 2000});
	}
}

function getAddressId(key,map){
	if(map[key]){
		return map[key];
	}

	key = key.substr(0,key.length-1);
	if(key){
		return map[key];
	}
}

// 运费
function c_getFreight() {
	$(".autoFreight").prop("checked",false);
	if (provinceIdForFreight == 0) {
		return;
	}

	var target = $(".record_sendList_line").find(".shippings");
	var code = target.find("option:selected").attr("code");
	var keys = Object.keys(c_finalProCollection);
	var warehouseId = c_finalProCollection[keys[0]]?c_finalProCollection[keys[0]].warehouseId:""
	if (code && keys.length > 0 && warehouseId == 2024) {
		//物流方式已添加，则选择既存物流方式
		if(code != 'BBC-TPL'){
			//添加完毕，获取默认选中的物流方式的运费
			var orderDetails = [];
			for (var k in c_finalProCollection) {
				var pro = c_finalProCollection[k];
				orderDetails.push({sku: pro.sku, num: pro.qty, costPrice: pro.disTotalCost});
			}
			var freightParam = {
				warehouseId:warehouseId,shippingCode:code,
				orderDetails:orderDetails,countryId:44, //写死为中国的id
				provinceId:provinceIdForFreight
			};
			getFreightByProvince(freightParam);
			$("#thirdPostfee").hide()
		}else{
			$("#thirdPostfee").show();
			sumOdTotal();
			$(".sale_freight").hide().find("b").text(0.00);
		}
	} else {
		$(".sale_freight b").text(0.00);
		sumOdTotal();
	}
}

/**
 * 根据省份ID获取运费
 * @param freightParam
 */
function getFreightByProvince(freightParam) {
	ajax_post("/inventory/getFreight", JSON.stringify(freightParam), "application/json",
		function (freightResStr) {
			var freightRes = freightResStr;
			if (freightRes.result) {
				//自提标识
				var fee = freightRes.msg;
				$(".sale_freight b").text(parseFloat(fee).toFixed(2));
				sumOdTotal();
				$(".sale_freight").show();
				$("button[name='submit-sale']").each(function (i, e) {
					$(e).attr("disabled", false).removeAttr("style").val("生成订单");
				})
			} else {
				layer.msg("运费获取失败！" + freightRes.msg, {icon: 2, time: 2000});
				$("button[name='submit-sale']").each(function (i, e) {
					$(e).attr("disabled", true).css("cursor", "not-allowed").html("生成订单");
				})
			}
		},
		function (XMLHttpRequest, textStatus) {
			layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
		}
	);
}

function checkdBanlance(obj){
	var email = $(".record_sendList_box #selected_email").val().trim();
	if ($(obj).prop("checked")) {
		if (email) {
			ajax_get("/member/getAccount", "email=" + email, "",
				function (data) {
					var banlance = parseFloat(data.balance);
					if (banlance && banlance < total) {
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

//生成订单
function c_generateSaleOrder() {
	$("button[name='submit-sale']").each(function (i, e) {
		$(e).attr("disabled", true).css("cursor", "not-allowed").html("正在生成");
	})
	isaulogin(function (salesman) {
		var index = $(".record_purchaseList_tab li").index($(".record_purchaseList_tab li[name='cimport']"));
		if(index==0){
			console.log("录入的生成订单")
			var flag = c_validation();
			if (!flag) {
				$("button[name='submit-sale']").each(function (i, e) {
					$(e).attr("disabled", false).css("cursor", "pointer").html("生成订单");
				})
				return;
			}

			var email = $(".record_sendList_box #selected_email").val().trim();
			var code = $(".shippings").find("option:selected").attr("code");
			//收货地址要有空格
			var address = $("#sale-province option:selected").text() + " "
				+ $("#sale-city option:selected").text() + " "
				+ $("#sale-region option:selected").text() + " "
				+ $("#addr").val().trim();

			var param = {
				"email" : email,
				"warehouseId" : undefined,
				"warehouseName" : undefined,
				"remark" : "",
				"address" : address,
				"receiver" : $(".receiveName").val(),
				"telphone" : $(".sale-tel").val(),
				"postCode": $(".sale-postcode").val(),
				"LogisticsTypeCode": code,
				"logisticsMode": $(".shippings").find("option:selected").text() ? $(".shippings").find("option:selected").text() : null,
				"createUser": salesman,
				"isBack": true,
				"provinceId" : provinceIdForFreight,
				"isPay" : $("#auto_check").prop("checked")
			};
			if(code == 'BBC-TPL'){
				param.thirdPostfee = $("#thirdPostfee").val();
			}
			var details = [];
			$(".record_sendList_table tbody").find("tr").each(function (i,e) {
				var warehouseId = $(e).find("td[name='warehouseName']").attr("warehouseId");
				details.push({
					"interBarCode": $(e).find("td[name='interBarCode']").text(),
					"sku": $(e).find("td[name='sku']").text(),
					"warehouseId": warehouseId,
					"isgift": $(e).find("td[name='sku']").attr('isgift'),
					"expirationDate": $(e).find("td[name='expirationDate']").attr("expirationDate"),
					"key": $(e).find("td[name='sku']").attr("key"),
					"num": $(e).find(".input_num").val(),
					"finalSellingPrice": $(e).find(".final-price").val()
				});
				param.warehouseId = warehouseId;
				param.warehouseName = $(e).find("td[name='warehouseName']").text();
			});
			param.skuList = details;// 下单的商品
			cloudsale_doGenerateSalesOrder(param)
		}else{
			console.log("导入的生成订单")
			var flag = cimport_validation();
			if (!flag) {
				$("button[name='submit-sale']").each(function (i, e) {
					$(e).attr("disabled", false).css("cursor", "pointer").html("生成订单");
				})
				return;
			}

			//收货地址要有空格
			var address = $("#cimport_sale_province option:selected").text() + " "
				+ $("#cimport_sale_city option:selected").text() + " "
				+ $("#cimport_sale_region option:selected").text() + " "
				+ $("#cimport_sale_addr").val().trim();

			var param = {
				"email" : $.trim($("#cimport_selected_email").text()),
				"warehouseId" : undefined,
				"warehouseName" : undefined,
				"remark" : "",
				"address" : address,
				"receiver" : $("#cimport_receiverName").val(),
				"telphone" : $("#cimport_sale_tel").val(),
				"postCode": $("#cimport_sale_postcode").val(),
				"LogisticsTypeCode": $("#cimport_shippingMethod").find("option:selected").attr("code"),
				"logisticsMode": $("#cimport_shippingMethod").find("option:selected").text() ? $("#cimport_shippingMethod").find("option:selected").text() : null,
				"createUser": salesman,
				"isBack": true,
				"provinceId" : $("#cimport_sale_province").val(),
				"isPay" : $("#cimport_auto_check").prop("checked")
			};
			var details = [];
			$("#cimport_productTable tbody").find("tr").each(function (i,e) {
				var warehouseId = $(e).find("td[name='warehouseName']").attr("warehouseId");
				details.push({
					"interBarCode": $(e).find("td[name='interBarCode']").text(),
					"sku": $(e).find("td[name='sku']").text(),
					"warehouseId": warehouseId,
					"isgift": $(e).find("td[name='sku']").attr('isgift'),
					"expirationDate": $(e).find("td[name='expirationDate']").attr("expirationDate"),
					"key": $(e).find("td[name='sku']").attr("key"),
					"num": $(e).find(".input_num").val()
				});
				param.warehouseId = warehouseId;
				param.warehouseName = $(e).find("td[name='warehouseName']").text();
			});
			param.skuList = details;// 下单的商品
			console.log(param)
			cloudsale_doGenerateSalesOrder(param)
		}
	});
}

function cloudsale_doGenerateSalesOrder(param){
	var loading_index = layer.load(1, {shade: 0.5});
	ajax_post("/sales/manager/cloudSale",JSON.stringify(param),"application/json",function (response) {
		if(response.code == 108) {
			layer.close(loading_index)
			layer.msg("订单已生成，单号为：" + response.msg, {icon: 6, time: 2000});
			$("p[position=" + $("#forword").val() + "]").click();
		} else {
			layer.close(loading_index)
			layer.msg(response.msg, {icon: 5, time: 2000});
			$("button[name='submit-sale']").each(function (i, e) {
				$(e).attr("disabled", false).css("cursor", "pointer").html("生成订单");
			})
		}
	})
}

function c_validation() {
	var email = $(".record_sendList_box #selected_email").val().trim();
	if (!email) {
		layer.msg('请选择分销商', {icon: 2, time: 2000});
		return false;
	}
	//必须有发货商品
	var skus = Object.keys(c_finalProCollection);
	if (skus.length == 0) {
		layer.msg('需要为客户订单添加发货商品', {icon: 2, time: 2000});
		return false;
	}
	var code = $(".shippings").find("option:selected").attr("code");
	if(!code){
		var fee = $("#thirdPostfee").val();
	    var reg = /^[0-9]+(.[0-9]{1,2})?$/;
	    var flag = fee?reg.test(fee)&&fee>0:false;
	    if(!flag){
	        layer.msg("请输入有效的[<b style='color:#E74C3C'>第三方物流费用</b>]（至多两位小数,且必须大于零）", {icon: 2, time: 2500});
	       return false;
	    }
	}
	//每个发货产品必须有真实售价
	var fspFlag = true;
	$(".record_sendList_table tbody").find("tr").each(function () {
		var tthis = $(this);
		var curfsp = tthis.find(".final-price").val();
		if (!curfsp) {
			fspFlag = false;
			tthis.find(".final-price").focus();
			layer.msg("请输入有效的[<b style='color:#E74C3C'>真实单价</b>]（至多两位小数）", {icon: 2, time: 2500});
			return false;
		}
		if (!isMoneyPattern(curfsp)) {
			fspFlag = false;
			tthis.find(".final-price").focus();
			layer.msg("请输入有效的[<b style='color:#E74C3C'>真实单价</b>]（至多两位小数）", {icon: 2, time: 2500});
			return false;
		}
	});
	if (!fspFlag) {
		return false;
	}
	var receiver = $(".receiveName").val();
	if (!receiver) {
		layer.msg('收货人不能为空', {icon: 2, time: 2000});
		return false;
	}
	//收货人手机验证
	var tel = $(".sale-tel").val();
	if (!checkTel(tel)) {
		layer.msg('收货人手机号码有格式错误，请输入有效手机号码', {icon: 2, time: 2000});
		return false;
	}
	//收货地址验证
	var province = $("#sale-province").val();
	if (!province) {
		layer.msg("请选择省！",{icon:2,time:2000});
		return
	}
	var city = $("#sale-city").val();
	if (!city) {
		layer.msg("请选择市！",{icon:2,time:2000});
		return
	}
	var region = $("#sale-region").val();
	if (!region) {
		layer.msg("请选择区！",{icon:2,time:2000});
		return
	}
	var addr = $("#addr").val().trim();
	if (!addr || addr.length < 5) {
		layer.msg('收货地址不规范，请重新填写', {icon: 2, time: 2000});
		return false;
	}
	var postCode = $(".sale-postcode").val();
	if (postCode && !checkPost(postCode)) {
		layer.msg('请输入有效的收货地址邮政编码', {icon: 2, time: 2000});
		return false;
	}
	return true;
}

var changeTwoDecimal_f;
changeTwoDecimal_f = function (x) {
	var f_x = parseFloat(x);
	if (isNaN(f_x)) {
		return false;
	}
	f_x = Math.round(f_x * 100) / 100;
	var s_x = f_x.toString();
	var pos_decimal = s_x.indexOf('.');
	if (pos_decimal < 0) {
		pos_decimal = s_x.length;
		s_x += '.';
	}
	while (s_x.length <= pos_decimal + 2) {
		s_x += '0';
	}
	return s_x;
}

//add by zbc 
function showErpStock(){
 	var keys = Object.keys(c_finalProCollection);
 	if(!keys.length){
 		layer.msg("请选择商品",{icon:5,time:2000});
 		return;
 	}

	var skuMap = {};
 	$.each(c_finalProCollection,function(k, v){
		var sku = v.sku;
		//数量合并
		if (skuMap[sku]) {
			skuMap[sku].qty = Number(v.qty) + Number(skuMap[sku].qty);
		} else {
			skuMap[sku] = {
				sku: v.sku,
				title: v.title,
				warehouseName: v.warehouseName,
				qty: v.qty,
				erpStock: 0,
				short: true, // 默认erp库存不够
				interBarCode: v.interBarCode
			};
		}                           n
 	});
	var skus = Object.keys(skuMap);
	erpStoHtml(erpSto({warehouseId:c_finalProCollection[keys[0]].warehouseId, skus:skus},skuMap));
}

function getKey(sku, warehouseId, isgift, expirationDate){
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

// 手动录入和导入tab切换
$('body').on("click",".record_purchaseList_tab li",function () {
	$(this).addClass("recharge_current").siblings().removeClass();
	var index = $(".record_purchaseList_tab li").index(this);
	$(".record_purchaseList_content > div").hide().eq(index).show();
	if(index==1){
		$("button[name='submit-sale']").each(function (i, e) {
			$(e).removeAttr("disabled")
		})
	}
});

function remove() {
	layer.confirm("您确定要删除吗？", {icon: 3});
}

