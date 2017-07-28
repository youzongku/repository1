define("generateOrder", ["jquery", "layer"], function($, layer){
	'use strict';
	//保存订单的参数
	var saveOrderParam = {};
	//淘宝订单编号list
	var platformOrderNos = [];
	var shortFlag = false;
	//订单编号与skulist 键值对
	var orderNoSkuListMap = {};
	//{"1522310000000000":{"2012":["IM180"]},"1513990000000000":{"2012":["IM101","IM180"],"2024":["IW716PU"]}}
	var orderNoWarehouseSkuListMap = {};
	var handleCount = 0;//生成订单执行次数；用于跟要生成的订单数比较
	var customer_account = "";
	var distributorType = 1;//分销商类型，默认是1（普通）
	function customer_service_account() {
		var result= false;
		ajax_get("/member/custaccount?" + Math.random(), JSON.stringify(""), "application/json",
			function (data) {
				customer_account = data.account;
				if(!customer_account) {
					layer.open({
						type: 1,
						title: "客户须知",
						content: "<div style='padding:5px;text-align:center'>您未关联客服，请先关联客服，谢谢！</div>",
						area: ['350px', '145px','558.5px'],
						btn: ["联系客服", "取消"],
						closeBtn: 1,
						shadeClose: false,
						//i和currdom分别为当前层索引、当前层DOM对象
						yes: function (i, currdom) {
							/*BizQQWPA.addCustom({
								aty: '0',
								nameAccount: '2881033425',//营销QQ号
								selector: 'online'
							});*/
						},
						cancel: function (i) {
							//$("#addodr").hide();
							layer.close(i);
						},
						success: function(layero, index){
							$(".layui-layer-btn0").attr("id","online");
						}
					});
				}else{//有账号，true
					result = true;
				}
			},
			function (xhr, status) {
			}
		);
		return result;
	}

	//全选按钮
	$("body").on("click", "input[name=selectAll]",function(){
		$(".generateOrderBtn").attr("disabled", false).html("生成订单");
		if($(this).is(":checked")){//全选
			$(".import-list").find("input[type=checkbox]").each(function(){if(!this.disabled){this.checked=true}})
		}else{//没勾选，取消全选
			$(".import-list").find("input[type=checkbox]").each(function(){if(!this.disabled){this.checked=false}})
		}
	});

	//某条订单勾选按钮,该元素是ajax动态添加，需以下面的方式绑定
	$("body").on("click",".orderCheckbox",function(){
		$(".generateOrderBtn").attr("disabled", false).html("生成订单");
		if($(this).is(":checked")){//全选
			$(this).parents(".import-list").find("input[type=checkbox]").each(function(){if(!this.disabled){this.checked=true}})
		}else{
			$(this).parents(".import-list").find("input[type=checkbox]").each(function(){if(!this.disabled){this.checked=false}})
		}
		changeAllSelected();
	});

	//商品勾选按钮，勾选时，其订单勾选框也勾选
	$("body").on("click",".goodsCheckbox",function(){
		$(".generateOrderBtn").attr("disabled", false).html("生成订单");
		if(!this.checked){//判断下有没有其他兄弟被勾选，如果没有，则取消订单勾选框的勾选状态
			/*var checked = false;
			 $(this).parents("tr").siblings(".olsit-2").find(".goodsCheckbox").each(function(){
			 if(this.checked == true){
			 checked = true;
			 }
			 });
			 if(!checked){
			 $(this).parents(".import-list").find(".orderCheckbox")[0].checked=false;
			 }*/
			var checkedNum = 0;
			checkedNum =  $(this).parents("tr").siblings(".olsit-2").find("input:checked").length;
			if(checkedNum == 0){
				$(this).parents(".import-list").find(".orderCheckbox")[0].checked=false;
			}
		}else{//勾选状态，设置订单勾选框为勾选状态
			$(this).parents(".import-list").find(".orderCheckbox")[0].checked=true;
		}
		changeAllSelected();

	});

	function changeAllSelected(){
		if ($(".goodsCheckbox:checked").length != $(".goodsCheckbox").length ) {
			$("input[name=selectAll]").prop("checked", false);
		} else {
			$("input[name=selectAll]").prop("checked", true);
		}
	}

	//批量生成订单按钮
	$("body").on("click",".generateOrderBtn",function(){
		if(customer_service_account()){//客服信息

			layer.confirm('确定批量生成订单？', {
				icon: 3,
				title: '提示'
			}, function(index) {
				//do something
				orderNoSkuListMap = {}; //每次点击先清空
				handleCount = 0;
				orderNoWarehouseSkuListMap = {};
				var orderNoSkuQtymap = {}; //订单号与商品sku与数量的map {orderno：{sku1:qty1,sku2:qty2,...},....}
				// var warehouseIdSkuListMap = {};
				var checkedNum = 0;
				/*$(".import-list").find("input[type=checkbox]").each(function() {
				 if (this.checked) {
				 checkedNum++;
				 }
				 });*/
				checkedNum = $(".import-list").find(".goodsCheckbox:checked").length;
				var notGoods = $(".orderCheckbox:checked").parents(".import-list").find(".goodsCheckbox").not("input:checked");//所有被选中的订单中没被选中的商品
				if (notGoods.length > 0) {
					layer.msg('存在未匹配的商品，请核对', {icon: 5,time : 1000});
					return;
				}
				if (checkedNum > 0) {
					// 执行
					$(".orderCheckbox").each(function() {
						if (this.checked) {
							var warehouseIdSkuList = {};
							var orderNo = $(this).attr("id");
							var skuQtymap = {};
							platformOrderNos.push(orderNo);
							$(this).parents(".import-list").find(".goodsCheckbox:checked").each(function() {
								var skulist = [];
								var sku = $(this).attr("id");
								skulist.push(sku);
								var qty = parseInt($(this).parent().siblings().eq(4).find("#num input").val());
								var warehouseId = $(this).parent().siblings().find(".warehouseInfo").attr("warehouseid");
								// var warehouseName = $(this).parent().siblings().find(".warehouseInfo").html();
								if (mapSize(warehouseIdSkuList) == 0 || warehouseIdSkuList[warehouseId] == undefined) { //为空或新的仓库id发现时
									warehouseIdSkuList[warehouseId] = [sku];
								} else {
									warehouseIdSkuList[warehouseId].push(sku);
								}
								skuQtymap[sku] = qty;
								orderNoWarehouseSkuListMap[orderNo] = warehouseIdSkuList;

							});
							orderNoSkuQtymap[orderNo] = skuQtymap; //订单号与商品sku与数量的map{订单号：{sku:qty},....}
							//console.log("skuqtymap---" + JSON.stringify(skuQtymap));
						}
					});
					goGenerate(orderNoWarehouseSkuListMap, orderNoSkuQtymap);

				} else {
					layer.msg('请选择订单', {
						icon: 6,
						time: 1000
					});
					return;
				}
				layer.close(index);
			}); //end layer confirm
		}//end if customer_service
	});/*---------end $(".generateOrderBtn").on("click") */

	// “生成订单” 按钮点击事件
	$('.box-right-four').on('click', '.gene-order', function () {
		$(this).parents(".import-list").find("input[type=checkbox]").each(function(){if(!this.disabled){this.checked=true}});
		if(customer_service_account()){//客服信息
			// $(this).attr("onclick",null);
			handleCount = 0;//初始化数据
			orderNoSkuListMap = {};
			orderNoWarehouseSkuListMap = {};
			var orderNoSkuQtymap = {};
			var platformOrderNo = $(this).parents(".import-list").find("li>span").html();//平台订单号

			var checkedObj  = $(this).parents(".import-list").find(".goodsCheckbox:checked");
			var notCheckObj = $(this).parents(".import-list").find(".goodsCheckbox").not("input:checked");
			var skuList = [];
			var warehouseIds = [];
			var warehouseNames = [];
			var skuQtyMap = {};//sku与对应购买数量map
			var warehouseMap = {};//仓库id与仓库名{id:name}
			var warehouseIdSkuListMap = {};
			// var paramMap = {};
			if(notCheckObj.length > 0) {
				layer.msg('存在未匹配的商品，请核对', {icon: 5,time : 1000});
				return;
			}
			if(checkedObj.length > 0){
				$.session.remove("pur_sale");
				checkedObj.each(function(){
					var sku = $(this).attr("id");
					var qty = parseInt($(this).parent().siblings().eq(4).find("#num input").val());
					var warehouseId = $(this).parent().siblings().find(".warehouseInfo").attr("warehouseid");
					var warehouseName = $(this).parent().siblings().find(".warehouseInfo").html();
					skuList.push(sku);
					warehouseIds.push(warehouseId);
					warehouseNames.push(warehouseName);
					warehouseMap[warehouseId]=warehouseName;
					// warehouseIdSkuListMap[warehouseId] = skuList;
					if (mapSize(warehouseIdSkuListMap) == 0 || warehouseIdSkuListMap[warehouseId] == undefined) { //为空或新的仓库id发现时
						warehouseIdSkuListMap[warehouseId] = [sku];
					} else {
						warehouseIdSkuListMap[warehouseId].push(sku);
					}
					skuQtyMap[sku]=qty;
				});
				//orderNoSkuListMap[platformOrderNo] = skuList;//{"订单编号"，"skuList"}
				orderNoSkuQtymap[platformOrderNo] = skuQtyMap;
				orderNoWarehouseSkuListMap[platformOrderNo] = warehouseIdSkuListMap;

				//执行生成订单,自动拆单
				goGenerate(orderNoWarehouseSkuListMap,orderNoSkuQtymap);

				/*-----以下是单条生成，不支持拆单*/
			}else{
				layer.msg('请选择订单', {icon: 6,time : 1000});
				return ;
			}
		}
	});//------------“生成订单”点击事件结束--------

	//分条执行生成订单
	function goGenerate(orderNoWarehouseSkuListMap,orderNoSkuQtymap){
		isnulogin(function(email){
			$(".generateOrderBtn").attr("disabled", true).html("正在生成订单");//防止多次点击
			shortFlag = false;//订单缺货标识
			for(var i in orderNoWarehouseSkuListMap){
				var orderno = i;
				var warehouseIdSkuListMap = orderNoWarehouseSkuListMap[i];
				var tradeNoIndex = -1;//支付交易号递增数，同一个订单里有不同的仓库，会生成不同的销售单，销售单里交易号需要唯一性
				if (Object.keys(warehouseIdSkuListMap).length > 1) {
					tradeNoIndex = 0;
				}
				for(var j in warehouseIdSkuListMap){
					if (!shortFlag) {//只要存在某个仓库里的某个商品库存缺货，就不往下执行
						var paramMap ={};
						var warehouseid = j;
						var skulist = warehouseIdSkuListMap[j];
						//判断此导入的订单编号，对应的仓库在销售订单系统中是否一样
						$.ajax({
							url: "/sales/checkOrderNo",
							type: 'POST',
							data: JSON.stringify({"platformOrderNo":orderno,"warehouseId":warehouseid,"list":skulist}),
							async: false,//是否异步
							contentType: 'application/json',
							dataType: 'json',
							success: function (data) {
								if (data.suc) {
									var skuqtymap = orderNoSkuQtymap[orderno];
									paramMap.skuList = data.msg;
									paramMap.warehouseId = warehouseid;
									paramMap.skuQtyMap = skuqtymap;
									paramMap.email = email;
									orderNoWarehouseSkuListMap[i][j] = data.msg;
									//执行 TODO 改版锁库
									generateOrder(orderno, paramMap, tradeNoIndex);
								} else {
									$(".generateOrderBtn").attr("disabled", false).html("生成订单");
									layer.msg(data.msg, {icon : 2, time : 3000});
									delete orderNoWarehouseSkuListMap[i][j];
									return;
								}
							},
							error: function (XMLHttpRequest, textStatus) {
								console.error("error--->" + status);
							}
						});
						tradeNoIndex += 1;
					}
				}
			}
		});
	}

	/*
	 * 生成订单操作
	 * 1.生成销售订单order_base；2.判断库存信息：库存足则生成销售单，状
	 先查库存
	 */
	function generateOrder(platformOrderNo, paramMap, tradeNoIndex) {
		var skuList = paramMap.skuList;
		var warehouseId = paramMap.warehouseId;
		var email = paramMap.email;
		// var warehouseName = paramMap.warehouseName;
		var skuQtyMap = paramMap.skuQtyMap;
		var skuPriceMap = {};//sku与单价map；
		var productSearchParam = {
			"data": {
				"istatus": 1
			}
		}; //商品查询条件
		var platformOrderBaseInfo; //订单基本信息
		var productDetails = []; //商品信息
		//根据平台订单编号取基本信息 ajax_post(url, params, contentType, successCallback, errorCallback)
		var getFreight = false;
		var freightCount = undefined;
		$.ajax({
			url: "/sales/orderDetails",
			data: JSON.stringify({
				"orderNo": platformOrderNo,
				"email":email
			}),
			type: 'post',
			async: false, //同步
			contentType: 'application/json',
			success: function(data) {
				platformOrderBaseInfo = data.data;
				//console.log("根据平台订单编号取基本信息---" + JSON.stringify(platformOrderBaseInfo));

				//1，接口"/checkout/orders/freight",POST协议
				//add by xuse计算运费
				//var freightCount = undefined;
				var details = [];
				var detail = {};

				var freight = {};
				// var code = platformOrderBaseInfo.logisticsTypeCode;
				if(warehouseId == 2024) {
					// var proParam = {
					// 	"data":{
					// 		"skuList": skuList,
					// 		"warehouseId":warehouseId
					// 	}
					// };
					$.each(skuList,function(i,sku) {
						detail.sku = sku;
						detail.num = skuQtyMap[sku];
						details.push(detail);
					});
					freight = {
						"warehouseId" : warehouseId,
						"shippingCode" : platformOrderBaseInfo.logisticsTypeCode,
						"orderDetails" : details,
						"provinceId" : 17,
						"countryId" : 44
					};
					$.ajax({
						url: "/member/provinces?key=" + platformOrderBaseInfo.address.split(" ")[0],
						type: 'get',
						async: false, //同步
						contentType: 'application/json',
						success: function(data) {
							freight.provinceId = data.id;
						},
						error:function(){
							layer.msg("未匹配到该省份信息！", {icon: 2, time: 2000});
							return;
						}
					});
					getFreight = true;
				}

				if(getFreight) {
					$.ajax({
						url: "/inventory/getFreight",
						data: JSON.stringify(freight),
						type: 'post',
						async: false, //同步
						contentType: 'application/json',
						success: function(data) {
							var freightRes = $.parseJSON(data);
							if(freightRes.result){
								freightCount =freightRes.msg;
				                freightCount = parseFloat(freightCount).toFixed(2);
							}else{
								layer.msg("运费获取失败！"+freightRes.msg, {icon: 2, time: 2000});
								freightCount = undefined;
							}
						},
						error:function(){
							layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
							freightCount = undefined;
						}
					});
				}
				var shopId = undefined;
				var collectAccount = undefined;
				$.ajax({
					url: "/member/shop?name=" + platformOrderBaseInfo.shopName,
					type: 'get',
					async: false, //同步
					contentType: 'application/json',
					success: function(data) {
						if(data.suc) {
							shopId = data.shop.id;
							collectAccount = data.shop.shroffAccountNumber;
						}
					}
				});
				//构造保存订单的参数
				saveOrderParam = {
					"id":"",
					"isNotified": 1,
					//"tradeNo": (platformOrderBaseInfo.paymentNo && tradeNoIndex >= 0) ?  (platformOrderBaseInfo.paymentNo +"-"+tradeNoIndex) : platformOrderBaseInfo.paymentNo,//平台支付交易号
					"tradeNo": platformOrderBaseInfo.paymentNo == null ? null : (tradeNoIndex >= 0 ? (platformOrderBaseInfo.paymentNo +"-"+tradeNoIndex) : platformOrderBaseInfo.paymentNo),
					/* "email" : "xxxx" ,*/
					"base": { //平台订单基本信息 order_base表
						"platformOrderNo": platformOrderBaseInfo.orderNo,
						"address": platformOrderBaseInfo.address,
						"receiver": platformOrderBaseInfo.receiverName,
						"tel": platformOrderBaseInfo.receiverPhone,
						"idcard": platformOrderBaseInfo.receiverCardNumber,
						"postCode":platformOrderBaseInfo.postCode,
						"orderActualAmount": platformOrderBaseInfo.orderTotal,//发货单实际金额
						"orderingDate": platformOrderBaseInfo.paymentDate == null ? null : new Date(platformOrderBaseInfo.paymentDate).getTime(),
						"remark": platformOrderBaseInfo.financeRemark,
						"orderer": platformOrderBaseInfo.paymentName,
						"ordererIDCard":platformOrderBaseInfo.paymentCardNumber,
						"ordererTel":platformOrderBaseInfo.paymentPhone,
						"logisticsTypeCode":platformOrderBaseInfo.logisticsTypeCode,
						"customerservice": customer_account,
						"orderPostage":platformOrderBaseInfo.logisticsCost,
//						"bbcPostage" : freightCount,
						"logisticsMode" : platformOrderBaseInfo.logisticsTypeName,
						"collectAccount" : collectAccount,
						"buyerID" : platformOrderBaseInfo.buyerAccount,
						"shopId" : shopId
					}
					/*"details": [产品基本信息list],*/ //---order_detail表
					/*"disPriceSystem": [产品分销价格信息list],*/ ////---order_detail表
				};
				//防止生成订单时运费出错

			},
			error: function(e) {
				//console.log(e)
			}
		}); /*------结束取订单基本信息--------*/

		if(getFreight){
			if(freightCount == undefined){//获取运费失败 则返回
				return;
			}
			saveOrderParam.base.bbcPostage = freightCount;
		}

		//取商品信息

		ajax_get("/sales/taobao-goods", {"orderNo": platformOrderNo, "skuList": skuList}, '',
			function(data) {
				// 构造商品sku-价格map
				for(var i = 0; i < data.length; i++){
					var e = data[i];
					var sku = e.sku;
					var price = e.price;
					skuPriceMap[sku] = price;
				}
				//console.log("取商品信息--构造商品sku-价格map"+JSON.stringify(data));
			},
			function(e) {
				//console.log(e)
			}
		);
		//--------------查询账户信息----------
		ajax_get("/member/infor?" + Math.random(), "", "application/json",
			function(response) {//查询账户信息成功
				if (response.id != null && response.id != 'null' && response.id != '' && response.id != undefined) {
					//将邮箱添加进保存订单参数对象
					saveOrderParam.email = response.email;

					var distributionMode = response.distributionMode;
					//将折扣加入到商品搜索参数对象中
					//productSearchParam.data.disCount = discount;

					productSearchParam.data.skuList = skuList;
					productSearchParam.data.warehouseId = warehouseId;
					productSearchParam.data.model = distributionMode;
					distributorType = response.comsumerType;
					saveOrderParam.distributorType = distributorType;
					// 查询商品信息
					$.ajax({
						url: "/product/api/getProducts",
						type: "post",
						dataType: "json",
						contentType: "application/json",
						data: JSON.stringify(productSearchParam),
						async: false,
						success: function(data) {
							var products = data.data.result; //商品信息
							saveOrderParam.disPriceSystem = data.data.result;

							//console.log(JSON.stringify(products));
							//处理商品信息 ---> handleProducts()，得到保存订单参数对象[saveOrderParam]的details参数
							productDetails = saveOrderParam.details = handleProducts(products,skuQtyMap,skuPriceMap);
							
							//真实售价
							if(distributorType != 3){
					            var finalTotal = 0;
								$.each(saveOrderParam.details, function (i, item) {
						            finalTotal += item.finalSellingPrice * item.qty;
						        });
						        if (saveOrderParam.details[0].warehouseId == 2024) {
						        	finalTotal += Number(saveOrderParam.base.bbcPostage);
						        }
								saveOrderParam.base.orderActualAmount = parseFloat(finalTotal).toFixed(2);
					        }
							var w = saveOrderParam.details[0].warehouseId;
					        if(w != 2024 && w != 73 && w != 6){
					        	if(saveOrderParam.base.orderActualAmount&&Number(saveOrderParam.base.orderActualAmount)>2000){
					        		layer.msg("非完税产品单笔订单实付款不得超过2000元,请分开下单  ");
	        						return; 
					        	}
					        }

							// 保存订单
							//console.log("保存订单参数---------"+JSON.stringify(saveOrderParam))
							saveOrder(saveOrderParam,skuQtyMap);
						}
					});
				} else {
					layer.msg("获取用户折扣出错！", {
						icon: 2,
						time: 2000
					});
				}
			},
			function(XMLHttpRequest, textStatus) {
				layer.msg("获取用户信息出错！", {
					icon: 2,
					time: 2000
				});
			}
		);

	} //---------结束生成订单操作----generateOrder----

	/*处理商品信息，返回保存订单所需的商品信息--->‘details’*/
	function handleProducts(products,skuQtyMap,skuPriceMap) {
		//这里传过来的 products = saveOrderParam.disPriceSystem
		var details = [];
		for (var i = 0; i < products.length; i++) {
			var p = products[i];
			var pro = {};
			//分销成本价
			// var disTotalCost = p.disTotalCost;
			//分销利润率
			// var disProfitRate = p.disProfitRate;
		 	var disPrice = p.disPrice;
            if(p.isSpecial){
                disPrice = p.specialSale;
            }
			pro.sku = p.csku;
			pro.qty = skuQtyMap[p.csku]; //根据sku找到对应的数量
			pro.productName = p.ctitle;
			pro.productImg = p.imageUrl;
			pro.purchasePrice = disPrice; //分销商的采购价
			pro.marketPrice = p.localPrice;
			pro.warehouseId = p.warehouseId;
			pro.warehouseName = p.warehouseName+"";
			pro.finalSellingPrice = skuPriceMap[p.csku]; //淘宝真实售价
			pro.disShippingType = p.disShippingType;
			details.push(pro);
		}
		//将平台售价---> finalSellingPrice 设置到订单保存参数[saveOrderParam]的[disPriceSystem]参数中
		for (var i = 0; i < products.length; i++) {
			//取得对应SKU的最终零售价
			for (var j = 0; j < details.length; j++) {
				if (details[j].sku == saveOrderParam.disPriceSystem[i].csku) {
					saveOrderParam.disPriceSystem[i].finalSellingPrice = details[j].finalSellingPrice;
				}
			}
		}
		return details;
	} /*--------------end- handleProducts-------------*/

	/*
	 *保存订单操作，来自sales.js的代码
	 *参数 param:saveOrderParam;
	 */
	function saveOrder(param,skuQtyMap){
		var email = param.email;
		var details = param.details;
		var orderNo = param.base.platformOrderNo;
		var warehouseId = param.details[0].warehouseId;
		var warehouseName = param.details[0].warehouseName;
		var skus = param.disPriceSystem;
		//var maxFlag = true;
		if(distributorType != 3 && warehouseId == 2024){
			param.status = 103; //待支付运费 状态码
		}
		var shortGoods = [];
		for (var i = 0; i < skus.length; i++) {
			for (var j = 0; j < details.length; j++) {
				if (skus[i].csku == details[j].sku && (skus[i].stock + skus[i].microStock) < details[j].qty) {
					orderNoWarehouseSkuListMap[orderNo][warehouseId].splice(orderNoWarehouseSkuListMap[orderNo][warehouseId].indexOf(details[j].sku),1);
					details.splice(j,1);
					shortGoods.push(skus[i].csku);
					break;
				}
			}
		}
		if (shortGoods.length > 0) {
			shortFlag = true;
			var SshortGoods = "";
			for (var i in shortGoods){
				SshortGoods += shortGoods[i] + ",";
			}
			SshortGoods = SshortGoods.substr(0,SshortGoods.length-1);
			layer.open({
				type: 1,
				title: '提醒',
				content:
				'<div id="select_pay_method_dialog" style="padding: 30px 60px;">' +
				"订单号"+orderNo +" ["+warehouseName+"]里的"+SshortGoods+"库存不足"+
				'</div>',
				move: false
			});
			$(".generateOrderBtn").attr("disabled", false).html("生成订单");
			return;
		}

		//此处由于删除产品是由于传入的参数过多，导致后台接收不到参数，后台去查询订单详情进行构造销售单详情
		delete param.details;
		delete  param.disPriceSystem;

		param.warehouseId = warehouseId;
		param.warehouseName = warehouseName;
		ajax_post_sync("/sales/taobao-order", JSON.stringify(param), "application/json",
			function(taobaoResult) {
				if (!taobaoResult.suc) {
					layer.msg(taobaoResult.msg,{icon:6,time:2000});
					$(".generateOrderBtn").attr("disabled", false).html("生成订单");
					return;
				}
				var detailsDB = taobaoResult.data;
				// TODO -------------仓库接口，需要进行对接--------------
				ajax_post_sync("/inventory/ivyChk", JSON.stringify({
						"pros": details,
						"salesOrderNo": detailsDB.currentDetail[0].salesOrderNo
					}), "application/json",
					 function (ivyChkRes) {
	                	if(ivyChkRes.code){
	                        layer.msg(ivyChkRes.msg,{icon:6,time:2000});
							$(".generateOrderBtn").attr("disabled", false).html("生成订单");
	                        return;
                      	}
						//遍历检查结果
						var res = true;
						for (var i = 0; i < ivyChkRes.length; i++) {
							//某单品库存不足或微仓中不存在
							if (ivyChkRes[i].status == 'notEnough' || ivyChkRes[i].status == 'notExist') {
								res = false;
							}
						}

						//状态标识
						var saleStatus = 1;//待采购
						//内部分销商的缺货客户订单直接变为待通知发货
						if (distributorType == 3) {
							saleStatus = 3;//待客服审核
						}

						//缺货时，变更订单状态为“待采购”；内部分销商直接变为待通知发货
						if (!res) {
							ajax_post_sync("/sales/updStu", JSON.stringify({
									"id": detailsDB.mainId,
									"status": saleStatus
								}), "application/json",
								function(updStuRes) {
									//状态变更无误
									if (updStuRes) {
										//组织采购单数据
										var orderDetail = new Array();
										var totalPrice = 0;
										var enoughArr = [];
										var enoughArrToDe = [];
										for (var i = 0; i < ivyChkRes.length; i++) {
											//组织待采购的数据
											if (ivyChkRes[i].status == 'notExist') {
												//微仓中不存在，那么不存在微仓扣减行为，将微仓检查结果全部列入缺货采购单信息
												var od = {
													"itemId": '',
													"title": ivyChkRes[i].productName,
													"price": ivyChkRes[i].purchasePrice,
													"qty": ivyChkRes[i].stockOutQty,
													"warehouseId": ivyChkRes[i].warehouseId,
													"warehouseName":ivyChkRes[i].warehouseName,
													"sumPrice": ivyChkRes[i].purchasePrice * ivyChkRes[i].stockOutQty,
													"marketPrice": ivyChkRes[i].marketPrice,
													"publicImg": ivyChkRes[i].productImg,
													"sku": ivyChkRes[i].sku,
													"salesOrderNo": detailsDB.currentDetail[0].salesOrderNo
												};
												orderDetail.push(od);
												totalPrice = totalPrice + ivyChkRes[i].purchasePrice * ivyChkRes[i].stockOutQty;
											}// 微仓中存在，但是不足，那么需要扣除掉微仓中已存在的，再把不足的添加到缺货采购单信息
											else if (ivyChkRes[i].status == 'notEnough') {
												// 缺货信息添加到缺货采购
												var od = {
													"itemId": '',
													"title": ivyChkRes[i].productName,
													"price": ivyChkRes[i].purchasePrice,
													"warehouseId": ivyChkRes[i].warehouseId,
													"warehouseName":ivyChkRes[i].warehouseName,
													"sumPrice": ivyChkRes[i].purchasePrice * ivyChkRes[i].stockOutQty,
													"marketPrice": ivyChkRes[i].marketPrice,
													"publicImg": ivyChkRes[i].productImg,
													"sku": ivyChkRes[i].sku,
													"salesOrderNo": detailsDB.currentDetail[0].salesOrderNo,
													"qty": ivyChkRes[i].stockOutQty

												}
												orderDetail.push(od);
												totalPrice = totalPrice + ivyChkRes[i].purchasePrice * ivyChkRes[i].stockOutQty;

												// 内部分销商
												//if (distributorType == 3) {
												//扣除掉微仓中原有的库存
												var deductPro = {};
												deductPro.qty = ivyChkRes[i].sendoutTotalQty - ivyChkRes[i].stockOutQty;
												deductPro.warehouseId = ivyChkRes[i].warehouseId;
												deductPro.warehouseName = ivyChkRes[i].warehouseName;
												deductPro.sku = ivyChkRes[i].sku;
												deductPro.salesOrderNo = detailsDB.currentDetail[0].salesOrderNo;
												deductPro.productName = ivyChkRes[i].productName;
												deductPro.purchasePrice = ivyChkRes[i].purchasePrice;
												enoughArrToDe.push(deductPro);
												//}
											} else {
												//构造扣除充足的库存的数据
												var deductPro = {};
												deductPro.qty = ivyChkRes[i].stockOutQty;
												deductPro.warehouseId = ivyChkRes[i].warehouseId;
												deductPro.sku = ivyChkRes[i].sku;
												deductPro.salesOrderNo = detailsDB.currentDetail[0].salesOrderNo;
												deductPro.productName = ivyChkRes[i].productName;
												deductPro.purchasePrice = ivyChkRes[i].purchasePrice;

												for (var j = 0; j < detailsDB.currentDetail.length; j++) {
													if (detailsDB.currentDetail[j].sku == deductPro.sku) {
														deductPro.id = detailsDB.currentDetail[j].id;
													}
												}
												enoughArr.push(deductPro);
												//缺货订单中存在库存充足可以扣减的商品，则扣减对应的库存
											}
										}
										if(enoughArrToDe.length >0){
                                            // TODO -------------仓库接口，需要进行对接--------------
											ajax_post_sync("/inventory/ivyDe", JSON.stringify({
													"email": email,
													"pros": {"currentDetail": enoughArrToDe}
												}), "application/json",
												function (ivyInfo) {
												},
												function (xhr, status) {
												}
											);
										}
										if(enoughArr.length > 0){
                                            // TODO -------------仓库接口，需要进行对接--------------
											ajax_post_sync("/inventory/ivyDe", JSON.stringify({
													"email": email,
													"pros": {"currentDetail": enoughArr}
												}), "application/json",
												function (ivyInfo) {
												},
												function (xhr, status) {
												}
											);
										}
										//标记那些需采购的订单详细
										if (orderDetail.length > 0 && distributorType != 3) {
											// ajax_post("/sales/updStockOutDetail", JSON.stringify(orderDetail), "application/json",
											ajax_post_sync("/sales/updStockOutDetail", JSON.stringify(orderDetail), "application/json",
												function(bindingRes) {
													//if (bindingRes && bindingRes == orderDetail.length) {
													//	console.log("发货单中缺货产品详细已标记")
													//}
												},
												function(xhr, status) {}
											);
										}

										var orderData = {
											"sid": detailsDB.mainId,
											"distributorType": distributorType,
											"email": email,
											"orderDetail": orderDetail,
											"totalPrice": totalPrice,
											"bbcPostage" : param.base.bbcPostage,
											"logisticsMode" : param.base.logisticsMode
										};
										// 生成采购订单
										purchaseOrder(param, orderData, detailsDB,skuQtyMap);
										var orderNo = param.base.platformOrderNo;
										//var skulist = orderNoSkuListMap[orderNo];
										//拆单情况，需根据订单编号加仓库id确定一组sku
										var skulist = orderNoWarehouseSkuListMap[orderNo][warehouseId];
										var deleteTaobaoOrderParam = {
											"skuList": skulist,
											"orderNoList": [orderNo],
											"email":email,
											"orderNo": orderNo
										}
										deleteTaobaoOrder(deleteTaobaoOrderParam);
									}
								},
								function(xhr, status) {}
							);
						} else { //无缺货产品，扣减库存
                            // TODO -------------仓库接口，需要进行对接--------------
							ajax_post_sync("/inventory/ivyDe", JSON.stringify({
									"email": email,
									"pros": detailsDB
								}), "application/json",
								function(ivyInfo) {
									//处理次数加1
									handleCount++;
									// 删除淘宝订单信息
									//构造参数
									var orderNo = param.base.platformOrderNo;
									//var skulist = orderNoSkuListMap[orderNo];
									//拆单情况，需根据订单编号加仓库id确定一组sku
									var skulist = orderNoWarehouseSkuListMap[orderNo][warehouseId];
									var deleteTaobaoOrderParam = {
										"skuList": skulist,
										"orderNoList": [orderNo],
										"email":email,
										"orderNo": orderNo
									}
									deleteTaobaoOrder(deleteTaobaoOrderParam);

									/*if(distributorType != 3 && warehouseId != 2029 && warehouseId != 2050) {
										var s = 6;*/
										if(warehouseId == 2024 && param.base.bbcPostage > 0) {
											// 改为待支付运费
											ajax_post_sync("/sales/toPayFreight", JSON.stringify({"id": detailsDB.mainId}),
												"application/json", function (updStuRes) {}, function (xhr, status) {});
										}

								/*	}*/
									//skuQtyMap的大小就是选择的商品数量大小
									/*if (mapSize(skuQtyMap) != $("#" + orderNo).parents(".import-list").find(".goodsCheckbox").length) {
									 console.log("订单还有其他商品，不删除订单");
									 } else {
									 deleteTaobaoOrder(deleteTaobaoOrderParam)
									 }*/
									//成功后返回客户订单页面
									// 暂时未做批量生成功能，批量时，这个点击事件应该是在最后一个订单处理完后弹出，

									if (countOrder(orderNoWarehouseSkuListMap) == handleCount){//处理完最后一个订单
										layer.msg('订单已经全部生成！', {
											icon: 6
										}, function(index) {
											layer.close(index);
										});
										$(".generateOrderBtn").attr("disabled", false).html("生成订单");
									}
									$("#waitingOrder").click();//点击 “待生成订单” 按钮
									//$("#box-right-four").click();
								},
								function(xhr, status) {}
							);
						}
					},
					function(xhr, status) {}
				);
			},
			function(xhr, status) {}
		);
	}/*---------------end saveOrder()--------------------------*/

	//生成采购单
	function purchaseOrder(param, orderData, detailsDB, skuQtyMap){
		isnulogin(function (email) {
			//内部分销商无需询问是否采购，直接生成已支付的采购订单和待客服确认的客户订单
			if (distributorType == 3) {
				ajax_post("/purchase/order", JSON.stringify(orderData), "application/json",
					function (response) {
						if (response.errorCode == 1) {
							alert(response.errorInfo);
						} else if (response.errorCode == 2) {
							alert(response.errorInfo);
						} else {
							//绑定该销售发货单和补货采购单
							ajax_post("/sales/bindingPurchaseOrder", JSON.stringify({
									"sid": detailsDB.mainId,
									"pNo": response.errorInfo
								}), "application/json",
								function (bindingCount) {
									if (bindingCount) {
										//构造参数
										var warehouseId = param.warehouseId;
										var orderNo = param.base.platformOrderNo;
										// var skulist = orderNoSkuListMap[orderNo];
										//拆单情况，需根据订单编号加仓库id确定一组sku
										var skulist = orderNoWarehouseSkuListMap[orderNo][warehouseId];
										var deleteTaobaoOrderParam = {
											"skuList": skulist,
											"orderNoList": [orderNo],
											"orderNo": orderNo
										}
										// 删除淘宝订单信息
										deleteTaobaoOrder(deleteTaobaoOrderParam);

										//if (mapSize(skuQtyMap) != $("#" + orderNo).parents(".import-list").find(".goodsCheckbox").length) {
										//	console.log("订单还有其他商品，不删除订单,删除该商品");
										//} else {
										//	console.log("该订单全部商品都选择了---");
										//}
										var orderDetail = orderData.orderDetail;
										var updIvyParam = [];
										//微仓库存扣减数据构造
										for (var i = 0; i < orderDetail.length; i++) {
											var item = {};
											item.sku = orderDetail[i].sku;
											item.productName = orderDetail[i].title;
											item.qty = orderDetail[i].qty;
											item.purchasePrice = orderDetail[i].price;
											item.warehouseId = orderDetail[i].warehouseId;
											item.warehouseName = orderDetail[i].warehouseName;
											updIvyParam.push(item);
										}

										//下面的微仓增减操作是为了生成微仓进出记录，因为内部分销商的缺货采购单不经过付款

										//将待采购的商品直接加入微仓
                                        // TODO -------------仓库接口，需要进行对接--------------
										ajax_post("/inventory/updIvy", JSON.stringify({
												"email": email,
												"pros": updIvyParam,
												"purchaseNo": response.errorInfo
											}), "application/json",
											function (updIvyRes) {
												//构建微仓扣减参数
												updIvyParam[0].salesOrderNo = detailsDB.currentDetail[0].salesOrderNo;

												//直接扣减这些微仓
                                                // TODO -------------仓库接口，需要进行对接--------------
												ajax_post_sync("/inventory/ivyDe", JSON.stringify({
														"email": email,
														"pros": {"currentDetail": updIvyParam}
													}), "application/json",
													function (ivyDeRes) {
														//更新库存，不跳转到支付
														layer.msg('订单已经全部生成！', {
															icon: 6
														});
														var param = {purchaseNo : response.errorInfo, flag : "PAY_SUCCESS", payType:'system'};
														ajax_post("/purchase/cancel",JSON.stringify(param),"application/json",function(data) {});
														$(".generateOrderBtn").attr("disabled", false).html("生成订单");
														$("#waitingOrder").click();//点击 “待生成订单” 按钮
													},
													function (xhr, status) {
													}
												);
											},
											function (xhr, status) {
											}
										);
									}
								},
								function (xhr, status) {
									//console.error("status-->" + status);
								}
							);
						}
					},
					function (xhr, status) {
						//console.error("status-->" + status);
					}
				)
			}else{
				ajax_post("/purchase/order", JSON.stringify(orderData), "application/json",
					function(response) {
						if (response.errorCode == 1) {
							alert(response.errorInfo);
						} else if (response.errorCode == 2) {
							alert(response.errorInfo);
						} else {
							//绑定该销售发货单和补货采购单
							ajax_post("/sales/bindingPurchaseOrder", JSON.stringify({
									"sid": detailsDB.mainId,
									"pNo": response.errorInfo
								}), "application/json",
								function(bindingCount) {
									if (bindingCount) {
										// 删除淘宝订单信息
										//构造参数
										var warehouseId = param.warehouseId;
										var orderNo = param.base.platformOrderNo;
										// var skulist = orderNoSkuListMap[orderNo];
										//拆单情况，需根据订单编号加仓库id确定一组sku
										var skulist = orderNoWarehouseSkuListMap[orderNo][warehouseId];
										var deleteTaobaoOrderParam = {
											"skuList": skulist,
											"orderNoList": [orderNo],
											"orderNo": orderNo
										}

										deleteTaobaoOrder(deleteTaobaoOrderParam)

										//if (mapSize(skuQtyMap) != $("#" + orderNo).parents(".import-list").find(".goodsCheckbox").length) {
										//	console.log("订单还有其他商品，不删除订单,删除该商品");
										//} else {
										//	console.log("该订单全部商品都选择了---");
										//}
										//告知发货单状态
										//批量时，这个弹出框应该是在最后一个订单处理完后弹出，
										handleCount++; //处理次数加1；
										//console.log("第-------------------"+handleCount+"次----------订单数---------------");
										if (countOrder(orderNoWarehouseSkuListMap) == handleCount) {//最后一次弹出提示信息
											var total = orderData.totalPrice;
											var tip = "";
											if(orderData.bbcPostage) {
												tip = "（订单商品需支付金额"+parseFloat(total).toFixed(2)+"+运费"+parseFloat(orderData.bbcPostage).toFixed(2)+"）";
												total = (parseFloat(total)+parseFloat(orderData.bbcPostage)).toFixed(2);
											}
											var desc = "您还需要支付"+ total +"元"+ tip +"，订单才可通知发货，请您确认后前往客户订单付款。";
											layer.open({
												type: 1,
												title: "采购须知",
												content: "<div style='padding:5px;text-align:center'>"+desc+"</div>",
												area: ['600px', '145px'],
												btn: ["前往支付"],
												closeBtn: 1,
												shadeClose: false,
												yes: function(i, currdom) {
													//更新库存
													updIvyStock(response.errorInfo, "FROZEN", false);//更新库存信息
													layer.close(i);
													$("#box-right-four").click();//跳到客户订单页面
												},
												cancel:function(i, currdom) {
													//更新库存
													updIvyStock(response.errorInfo, "FROZEN", false);//更新库存信息
													layer.close(i);
													$("#box-right-four").click();//跳到客户订单页面
												}
											});
										}else{
											//更新库存
											updIvyStock(response.errorInfo, "FROZEN", false);
										}
									}
									// resetFormData();
								},
								function(xhr, status) {
									//console.error("status-->" + status);
								}
							);
						}
					},
					function(xhr, status) {
						//console.error("status-->" + status);
					}
				);
			}
		});
	}
	/*--------end purchaseOrder()*/

	//更新微仓，物理仓信息
	function updIvyStock(purchaseOrderNo, flag, goPay) {
		var getInfoUrl = "/purchase/getByNo";
		var g_data = {
			purchaseOrderNo: purchaseOrderNo,
			flag: flag
		};
		$.ajax({
			url: getInfoUrl,
			type: "post",
			dataType: "json",
			contentType: "application/json",
			data: JSON.stringify(g_data),
			success: function(data) {
				if (data.returnMess.errorCode == "0") {
					if (flag == "SUCCESS") {
						stockChange(data, flag, purchaseOrderNo, goPay);
					} else { //生成采购单，冻结库存;支付失败，解冻库存
						stockChange(data, flag, purchaseOrderNo, goPay);
					}
				}
			}
		});
	}

	//库存记录表操作
	function stockChange(params, flag, purchaseOrderNo, goPay) {
		var stockUrl = "/inventory/stkChgRrd";
		$.ajax({
			url: stockUrl,
			type: "post",
			dataType: "json",
			contentType: "application/json",
			data: JSON.stringify(params),
			success: function(data) {
				if (!data) {
					layer.msg("产品库存更新失败！", {
						icon: 2,
						time: 2000
					});
				}

				if (flag == "SUCCESS") {
					updIvy(params, purchaseOrderNo, goPay);
				}

				if (goPay) {
					window.location.href = "../product/balance-paid.html?purno=" + purchaseOrderNo;
				}
			}
		});
	}/*-------------end stockChange()---------*/

	//更新微仓，物理仓信息
    // TODO -------------仓库接口，需要进行对接--------------
	function updIvy(params, purchaseOrderNo, goPay) {
		var upurl = "/inventory/updIvy"
		$.ajax({
			url: upurl,
			type: "post",
			dataType: "json",
			contentType: "application/json",
			data: JSON.stringify(params),
			success: function(data) {
				if (JSON.stringify(data) <= 0) {
					layer.msg("产品入库失败！", {
						icon: 2,
						time: 2000
					});
				}
				if (goPay) {
					window.location.href = "../product/balance-paid.html?purno=" + purchaseOrderNo;
				}
			}
		});
	}/*---------end updIvy()-----------*/

	/*删除淘宝订单商品
	 *参数为 {"orderNo":订单编号,"skuList":skulist }
	 */
	function deleteTaobaoOrder(param){
		//目前做逻辑删除
		$.ajax({
			url : "/sales/del-taobao-order",
			type : "POST",
			dataType : "json",
			async: false,
			contentType : "application/json",
			data : JSON.stringify(param),
			success : function (data){
				if(data.code){
					window.location.href = "/personal/login.html";
				}
				//console.log("删除淘宝订单 成功！");
			},
			error : function (e){
				//console.log("删除淘宝订单信息失败"+e);
			}
		})
	}

	//批量删除订单按钮
	$(document).on("click",".deleteOrderBtn",function(){
		isnulogin(function(email){
			var checkedObj = $(".orderCheckbox:checked");
			if(checkedObj.length > 0){
				var orderList = [];
				checkedObj.each(function(){
					orderList.push($(this).attr("id"));
				});
				//console.log("----"+orderList);
				layer.confirm('确定删除所选订单？', {icon: 3, title:'提示'}, function(index){
					$.ajax({
						url: "/sales/deleteOrder",
						type: 'POST',
						data: {orderList: orderList,email:email},
						dataType: 'json',
						success: function(data){
							if(data.code){
								window.location.href = "/personal/login.html";
							}
							if(data.data>0){
								layer.msg('订单删除成功！', {icon: 6});
								$("#waitingOrder").click();//点击 “待生成订单” 按钮
							}else{
								layer.msg('操作失败！', {icon: 5});
							}
						},
						error: function(){
							layer.msg("删除订单出错，请稍后重试！", {icon : 2, time : 2000});
						}
					});
					layer.close(index);
				});
			}else{
				layer.msg("请勾选要删除的订单！", {icon : 2});
			}
		});
	})

	//计算map的大小
	function mapSize(map) {
		var n = 0;
		for (var i in map) {
			n++
		};
		return n
	}

	//计算要生成订单的总数量,参数为 【orderNoWarehouseSkuListMap】
	function countOrder(m) {
		var num = 0;
		for (var i in m) {
			var warehouseIdSkuListMap = m[i];
			num += mapSize(warehouseIdSkuListMap)
		};
		return num;
	}
//------------end------------------
})