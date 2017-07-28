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
						},
						cancel: function (i) {
							layer.close(i);
						},
						success: function(layero, index){
							$(".layui-layer-btn0").attr("id","online");
						}
					});
				}else{//有账号，true
					result = true;
				}
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
			var checkedNum =  $(this).parents("tr").siblings(".olsit-2").find("input:checked").length;
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
			var checkedNum = $(".import-list").find(".goodsCheckbox:checked").length;
			if (checkedNum < 1) {
				layer.msg('请选择订单', {icon: 5, time: 1000});
				return;
			}

			layer.confirm('确定批量生成订单？', {
				icon: 3,
				title: '提示'
			}, function(index) {
				orderNoSkuListMap = {}; //每次点击先清空
				handleCount = 0;
				orderNoWarehouseSkuListMap = {};
				var orderNoSkuQtymap = {}; //订单号与商品sku与数量的map {orderno：{sku1:qty1,sku2:qty2,...},....}
				var notGoods = $(".orderCheckbox:checked").parents(".import-list").find(".goodsCheckbox").not("input:checked");//所有被选中的订单中没被选中的商品
				if (notGoods.length > 0) {
					layer.msg('存在未匹配的商品，请核对', {icon: 5,time : 1000});
					return;
				}

				$(".orderCheckbox").each(function() {
					if (this.checked) {
						var warehouseIdSkuList = {};
						var orderNo = $(this).attr("id");
						var skuQtymap = {};
						platformOrderNos.push(orderNo);
						$(this).parents(".import-list").find(".goodsCheckbox:checked").each(function() {
							var sku = $(this).attr("id");
							var qty = parseInt($(this).parent().siblings().eq(4).find("#num input").val());
							var warehouseId = $(this).parent().siblings().find(".warehouseInfo").attr("warehouseid");
							if (mapSize(warehouseIdSkuList) == 0 || warehouseIdSkuList[warehouseId] == undefined) { //为空或新的仓库id发现时
								warehouseIdSkuList[warehouseId] = [sku];
							} else {
								warehouseIdSkuList[warehouseId].push(sku);
							}
							skuQtymap[sku] = qty;
							orderNoWarehouseSkuListMap[orderNo] = warehouseIdSkuList;
						});
						orderNoSkuQtymap[orderNo] = skuQtymap; //订单号与商品sku与数量的map{订单号：{sku:qty},....}
					}
				});
				goGenerate(orderNoWarehouseSkuListMap, orderNoSkuQtymap);
				layer.close(index);
			}); //end layer confirm
		}//end if customer_service
	});

	// “生成订单” 按钮点击事件
	$('.box-right-four').on('click', '.gene-order', function () {
		$(this).parents(".import-list").find("input[type=checkbox]").each(function(){if(!this.disabled){this.checked=true}});
		if(customer_service_account()){//客服信息
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
					if (mapSize(warehouseIdSkuListMap) == 0 || warehouseIdSkuListMap[warehouseId] == undefined) { //为空或新的仓库id发现时
						warehouseIdSkuListMap[warehouseId] = [sku];
					} else {
						warehouseIdSkuListMap[warehouseId].push(sku);
					}
					skuQtyMap[sku]=qty;
				});
				orderNoSkuQtymap[platformOrderNo] = skuQtyMap;
				orderNoWarehouseSkuListMap[platformOrderNo] = warehouseIdSkuListMap;

				//执行生成订单,自动拆单
				goGenerate(orderNoWarehouseSkuListMap,orderNoSkuQtymap);
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
					tradeNoIndex = 65;
				}
				for(var j in warehouseIdSkuListMap){
					if (!shortFlag) {//只要存在某个仓库里的某个商品库存缺货，就不往下执行
						var paramMap ={};
						var warehouseid = j;
						var skulist = warehouseIdSkuListMap[j];
						//判断此导入的订单编号，对应的仓库在销售订单系统中是否一样
						ajax_post_sync("/sales/checkOrderNo",JSON.stringify({"platformOrderNo":orderno,"warehouseId":warehouseid,"list":skulist}),'application/json',function (data) {
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
						})
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
		var skuQtyMap = paramMap.skuQtyMap;
		var skuPriceMap = {};//sku与单价map；
		var productSearchParam = {"data": {"istatus": 1}}; //商品查询条件
		var platformOrderBaseInfo; //订单基本信息
		var productDetails = []; //商品信息
		//根据平台订单编号取基本信息 ajax_post(url, params, contentType, successCallback, errorCallback)
		var freightCount = undefined;
		ajax_post_sync("/sales/orderDetails",JSON.stringify({"orderNo": platformOrderNo, "email":email}),'application/json',function(data) {
			platformOrderBaseInfo = data.data;
			var details = [];
			$.each(skuList,function(i,sku) {
				details.push({sku: sku, num: skuQtyMap[sku]});
			});
			var freight = {
				"warehouseId" : warehouseId, "shippingCode" : platformOrderBaseInfo.logisticsTypeCode,
				"orderDetails" : details, "provinceId" : 17, "countryId" : 44
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

			ajax_post_sync("/inventory/getFreight",JSON.stringify(freight),'application/json',function(data) {
				var freightRes = data;
				if(freightRes.result){
					freightCount =freightRes.msg;
					freightCount = parseFloat(freightCount).toFixed(2);
				}else{
					layer.msg("运费获取失败！"+freightRes.msg, {icon: 2, time: 2000});
					freightCount = undefined;
				}
			},function(){
				layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
				freightCount = undefined;
			})

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
				"tradeNo": platformOrderBaseInfo.paymentNo == null ? null : (tradeNoIndex > 0 ? (platformOrderBaseInfo.paymentNo + "-" + String.fromCharCode(tradeNoIndex)) : platformOrderBaseInfo.paymentNo),
				"base": { //平台订单基本信息 order_base表
					"platformType" : platformOrderBaseInfo.plateform,
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
					"logisticsMode" : platformOrderBaseInfo.logisticsTypeName,
					"collectAccount" : collectAccount,
					"buyerID" : platformOrderBaseInfo.buyerAccount,
					"shopId" : shopId
				}
			};
			//防止生成订单时运费出错
		})
		/*------结束取订单基本信息--------*/

		if(freightCount == undefined){//获取运费失败 则返回
			return;
		}
		saveOrderParam.base.bbcPostage = freightCount;

		//取商品信息
		ajax_get("/sales/taobao-goods", {"orderNo": platformOrderNo, "skuList": skuList}, '',
			function(data) {
				// 构造商品sku-价格map
				for(var i = 0; i < data.length; i++){
					var e = data[i];
					skuPriceMap[e.sku] = e.price;
				}
			}
		);

		//--------------查询账户信息----------
		ajax_get("/member/infor?" + Math.random(), "", "application/json",
			function(response) {//查询账户信息成功
				if (response.id != null && response.id != 'null' && response.id != '' && response.id != undefined) {
					//将邮箱添加进保存订单参数对象
					saveOrderParam.email = response.email;
					var distributionMode = response.distributionMode;
					productSearchParam.data.skuList = skuList;
					productSearchParam.data.warehouseId = warehouseId;
					productSearchParam.data.model = distributionMode;
					distributorType = response.comsumerType;
					saveOrderParam.distributorType = distributorType;
					// 查询商品信息
					ajax_post_sync("/product/api/getProducts",JSON.stringify(productSearchParam),"application/json",function(data) {
						var products = data.data.result; //商品信息
						saveOrderParam.disPriceSystem = data.data.result;
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
						saveOrder(saveOrderParam,skuQtyMap);
					})
				} else {
					layer.msg("获取用户折扣出错！", {icon: 2, time: 2000});
				}
			},
			function(XMLHttpRequest, textStatus) {
				layer.msg("获取用户信息出错！", {icon: 2, time: 2000});
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
		var warehouseId = param.details[0].warehouseId;
		var warehouseName = param.details[0].warehouseName;
		if(distributorType != 3 && warehouseId == 2024){
			param.status = 103; //待支付运费 状态码
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
				handleCount++; 
				if (countOrder(orderNoWarehouseSkuListMap) == handleCount){//处理完最后一个订单
					layer.msg('订单已经全部生成！', {
						icon: 6
					}, function(index) {
						layer.close(index);
					});
					$(".generateOrderBtn").attr("disabled", false).html("生成订单");
				}
				$("#waitingOrder").click();
			}
		);
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