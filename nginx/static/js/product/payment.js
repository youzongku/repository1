var comsumerType = undefined;
var distributionMode = undefined;
var detailData = [];//此订单的商品详情
var totalNumber = 0;//商品总数

$(function(){
	isnulogin(function(email){
	    if("0"==email){
	        window.location.href = "../../personal/login.html";
    	}else{
			$.ajax({
				url: "/member/infor?" + Math.random(),
				type: "get", dataType: "json", async: false,
				success: function (u) {
					comsumerType =  u.comsumerType;
					distributionMode = u.distributionMode;
					var purno = GetQueryString("purno");
					if(purno != null || purno != undefined){
						var param = {email: email, seachFlag: purno, pageSize: "10", pageCount: "0"};
						ajax_post("/purchase/viewpurchase",JSON.stringify(param),"application/json",function(data){
							if(data.returnMess.errorCode == "0"){
								var order = data.orders[0];
								$.each(order.details,function(i,item){
									var skuParam = {
										"sku": item.sku,
										"commodityCategoryId": item.categoryId,//商品类目
										"warehouseId": item.warehouseId,
										"totalPrice":item.totalPrices,
										"number": item.qty
									};
									totalNumber += Number(skuParam.number);
									detailData.push(skuParam);
								});
								var totalAmount = 0;
								var bbcPostage = order.bbcPostage;
								var couponsAcount = order.couponsAmount;
								$(".purchaseNo").text(order.purchaseOrderNo);
								$(".paid-order-list").data("orderId",order.id);
								//内部分销商在客户订单下的采购单  目前内部分销商不需要支付
								if(comsumerType == 3 && order.purchaseType == 2) {
									$(".totalAmount").text(order.salesAmount);
									$(".b-price").text("￥" + order.salesAmount);
									valMoney = order.salesAmount;
								} else {
									totalAmount = parseFloat(order.purchaseDiscountAmount+(bbcPostage?bbcPostage:0)).toFixed(2);
									if(couponsAcount){
										if(totalAmount > couponsAcount){
											totalAmount -= couponsAcount;
										}else{
											totalAmount = 0;
										}
									}
									totalAmount = parseFloat(totalAmount).toFixed(2)
									$(".productAmount").text(order.purchaseDiscountAmount);
									$(".totalAmount").text(totalAmount);
									$(".b-price").text("￥" + totalAmount);
									$(".totalAmount").data("type",(bbcPostage?"6":""));
								}
								$(".couponsAcount").text(deal_with_illegal_value(couponsAcount));
								$(".bbcPostage").text(deal_with_illegal_value(bbcPostage));
								$("#pay_accounts").text(email);
								$.ajax({
									url: "/member/account?"+(new Date()).getTime(),
									type: "get", dataType: "json",
									success: function(data){
										if(data.errorCode == "0"){
											$("#mem_balance").text("￥"+fmoney(parseFloat(data.balance),2));
											if(parseFloat(totalAmount) > parseFloat(data.balance)){
												$(".money-less").css("display","block");
												$("#paid").attr("disabled","true");
											}
										}
									}
								});
							}
						})
					}
				}
			});
		}
	});

	// 去支付按钮事件
	$("#paid").click(function(){
		isnulogin(function(email){
		    if("0"==email){
		        window.location.href = "../../personal/login.html";
	    	}else{
	    		var purchaseNo = $(".purchaseNo").text();
	    		var totalAmount = $(".totalAmount").text();
	    		var type = $(".totalAmount").data("type");// 支付方式
				pay_purchaseOrder(purchaseNo,email,totalAmount,type);
			}
		});
	});
	//验证码处理 by huchuyin 2016-10-8
	initPayCaptcha();
});

//支付开始
/**
 *支付z
 * @param purchaseNo 采购单号
 * @param email
 * @param transferAmount  转账金额
 * @param type 支付方式
 * TODO 商品锁库改版 
 * change by zbc 
 */
function pay_purchaseOrder(purchaseNo,email,transferAmount,type){
	//防止重复提交
	$("#paid").attr("disabled",true).css("background","#F0F0F0");
	var paycode = $(".pay-code").val();
	if(paycode != null && paycode != '' && paycode != undefined){
        /*********************支付前重新锁库- *************************/
        orderLock(purchaseNo,function(lockFlag){
        	if(lockFlag){
        		var isValiUrl = "/purchase/isValiPayDate";
		        var isValiParam = {
		            purchaseOrderNo: purchaseNo
		        };
		        if(!type){
		            type = "3";
		        }
		        ajax_post(isValiUrl,JSON.stringify(isValiParam),"application/json",function(data){
		            if(data.errorCode == "0"){
		                //验证码
		                var payCaptcha = $("#pay_captcha").val();
		                var param = {
		                    email: email,
		                    transferAmount : transferAmount,
		                    transferNumber : purchaseNo,
		                    paycode: paycode,
		                    applyType : type,
		                    //验证码 by huchuyin 2016-10-8
		                    payCaptcha:payCaptcha==undefined?"":payCaptcha
		                };
		                // 余额支付
		                ajax_post("/member/balancePayment",JSON.stringify(param),"application/json",function (data) {
		                    if(data.code == "1"){
		                        window.location.href = "login.html";
		                    }else if(data.code == "4"){// 支付成功
		                        cancelOrder(purchaseNo,transferAmount,"PAY_SUCCESS","system",function(flag){
		                            if(flag){
										window.location.href = "../../product/pay-success.html?isok=true&od="+purchaseNo+"&transamount="+transferAmount;
		                            }
		                        });
		                    }else if(data.code == "5"){
		                        layer.msg('当前帐户余额：'+data.balance+" "+data.info, {icon : 5, time : 3000}, function(index){ layer.close(index); });
		                    }else{
		                        layer.msg(data.info, {icon : 5, time : 3000}, function(index){ layer.close(index); });
		                        $("#paid").attr("disabled",false).css("background","#4a7cc7");
		                        //验证码处理 by huchuyin 2016-10-8
		                        initPayCaptcha();
		                    }
		                })
		            }else if(data.errorCode == "3"){
		                cancelOrder(purchaseNo,null, "INVALID",null,function(coflag){
		                    if(coflag){
		                        layer.msg('订单有效支付时间是2天以内，已超时。请重新下单，并尽快完成支付，谢谢！', {icon : 5, time : 4000}, function(index){ window.location.href = "/personal/personal.html"; });
		                    }
		                });
		            }
		        })
        	}else{
        		$("#paid").attr("disabled",false).css("background","#4a7cc7");
        	}
        });
	}else{
		layer.msg('请先正确填写支付密码', {icon : 5, time : 3000}, function(index){ layer.close(index); });
		$(".pay-code").focus();
		$("#paid").attr("disabled",false).css("background","#4a7cc7");
	}  
}


//更新订单状态
function cancelOrder(purchaseNo,actualAmount,flag,payType,callback){
    var param = {purchaseNo : purchaseNo, flag : flag, actualAmount: actualAmount,payType:payType};
    ajax_post("/purchase/cancel",JSON.stringify(param),"application/json",function(data) {
		// 用户未登录
		if(data.code == 101){
			layer.msg(data.msg, {icon: 1, time: 2000}, function(){
				window.location.href = "/personal/login.html"
			});
			return
		}
		if(data.errorCode == "0"){
			//封装到后台处理
			callback(true);
		}else{
			layer.msg("订单取消失败："+data.errorInfo, {icon : 5, time : 3000}, function(index){ layer.close(index); });
			callback(false);
		}
	})
}

var changeTwoDecimal_f
$(document).ready(function () {
	//小数点取整
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
});

/**
 * 处理验证码
 * Created by huchuyin 2016-10-7 
 */
function initPayCaptcha() {
	ajax_get("/member/getDisAccountInfo?"+(new Date()).getTime(), {}, undefined,
		function(data) {
			if (data.suc) {
				if (data.msg && data.msg.inputErrorNumTimes >= 3) {
					$("#wb_pay_img").click();
					$("#wb_pay_img").parents("ul").show();
				} else {
					$("#wb_pay_img").parents("ul").hide();
				}
            } else if (data.code == 2) {
                window.location.href = "login.html";
            } 
		},
		function(xhr, status) {
			layer.msg("系统异常", {icon : 2, time : 3000});
		}
	);
}