var interval_id, qrurl, errorCode, orderNo, totalFee,sid,postflag,forword_url,layer;
var audit_func,wxpayUrl,alipayUrl,yjpayUrl;
var saleOrderId, isFront = false;

$(function() {
    $("body").on("click","button.close",function(){
    	window.clearInterval(interval_id);
        $('.modal').hide();
        if(audit_func){
        	//执行函数
        	audit_func(alipayUrl, wxpayUrl, yjpayUrl);
        }
    });
    $(".modal-body-wechat").mouseover(function(){
        $(".wechat-course").stop();
        $(".wechat-course").show().animate({right:"-249px"},"slow");
    }).mouseout(function(){
        $(".wechat-course").stop();
        $(".wechat-course").hide().animate({right:"0px"},"slow");
    });
});

function init_win_payment(params,url,laye,call){
	if(call){
		//根据函数名称，获取该函数
		audit_func = eval(call.func);
		wxpayUrl = call.wxpayUrl;
		alipayUrl = call.alipayUrl;
		yjpayUrl = call.yjpayUrl;
		saleOrderId = call.saleOrderId  // added by huangjc 2016.12.22  为了实现支付销售单后，跳到详情页
		isFront = call.isFront  // added by huangjc 2016.12.22  为了实现支付销售单后，跳到详情页
	}
	layer = laye;
	var index = layer.load(1, {shade: 0.5});
	
	forword_url = url;
	sid = params.id;
	postflag = params.postflag;
	$.ajax({
	url: "../payment/wechat/getpayparam",
	data: JSON.stringify(params),
	type: "post",
	async: false,
	contentType: "application/json",
	dataType: "json",
	success: function(data) {
		if (data.suc) {
			qrurl = data.info.codeUrl;
			errorCode = data.info.errorCode;
			orderNo = data.info.fProposalCode;
			totalFee = data.info.totalFee;
		} else {
			errorCode = "1";
		}
		if(!qrurl){
			layer.msg(data.info.errorInfo,{icon:2,time:2000});
		}
	},
	error: function(xhr, status) {
		errorCode = "1";
		console.log("error--->" + status);
	}
	});
	init_code();
	layer.close(index);
}

//初始化二维码
function init_code(){
	if(errorCode == '1'){
		//支付错误，显示错误页面
		layer.msg("显示错误页面",{icon:2,time:2000});
	}else{
		$(".modal-title-wechat").text("支付金额"+totalFee+"元");
		$("#code").qrcode({
			render: "canvas", //table方式
			width: 200, //宽度
			height: 200, //高度
			text: qrurl //任意内容	
		});
		//启动一个定时器，检查支付结果
		interval_id = window.setInterval(check_pay_result, 3000);
	}
}

//检查是否支付成功
function check_pay_result() {
	var params = {
		orderNo: orderNo,
		postflag:postflag,
		sid:sid,
		total : totalFee
	};
	$.ajax({
		url: "../payment/wechat/getpayresult",
		data: params,
		type: "get",
		async: true,
		dataType: "json",
		success: function(data) {
			if (data.suc == "true") {
				//关闭定时器
				window.clearInterval(interval_id);
				layer.msg("支付成功，页面即将关闭！", {shade: 0.5, type: 1, time: 2000},
					function() {
						$('button.close').click();
						if(audit_func){
							//执行函数
							new audit_func(alipayUrl, wxpayUrl, yjpayUrl);
						}else{
							window.location.href = forword_url;
						}
					}
				);
			} else if (data.suc == "success"||data.suc == "cz_success") {//cz_success在线充值成功
				window.clearInterval(interval_id);
				layer.msg("支付成功，页面即将关闭！", {shade: 0.5, type: 1, time: 2000},
					function() {
						$('button.close').click();
						if(audit_func){
							if(isFront && saleOrderId){
								console.log("跳到发货单详情页，saleOrderId="+saleOrderId)
								// added by huangjc 2016.12.22  为了实现支付销售单后，跳到详情页
								new audit_func(saleOrderId, true);// true说明需要刷新列表
							}else{
								//执行函数
								new audit_func(alipayUrl, wxpayUrl, yjpayUrl);
							}
						}else{
							window.location.href = forword_url;
						}
					}
				);
			} else {
				console.log("fail--->" + data.msg);
			}
		},
		error: function(xhr, status) { console.log("error--->" + status); }
	});
}