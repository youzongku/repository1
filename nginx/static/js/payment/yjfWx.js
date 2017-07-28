//获取请求地址后的参数
function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null)return unescape(r[2]);
    return null;
}

$(function() {
	var params = {
		id: GetQueryString("one"),
		outOrderNo: GetQueryString("two"),
		tradeAmount: GetQueryString("three"),
		orderDetail: JSON.parse(decodeURIComponent(GetQueryString("four")))
	};
	if(GetQueryString("service")){
		params.service = GetQueryString("service");
	}
	if(GetQueryString("postflag")){
		params.postflag = GetQueryString("postflag");
	}
	console.log(">>>>>>"+JSON.stringify(params));
	layer.load(1, {shade: 0.5});
	$(".layui-layer-loading1").css("margin", "auto");
	$.ajax({
		url: "../payment/yijipay/yjfWx",
		data: JSON.stringify(params),
		type: "post",
		async: false,
		contentType: "application/json",
		dataType: "json",
		success: function(data) {
			if(data.suc){
				console.log(">>>>>>"+JSON.stringify(data));
				var param = data.info;
				$("#yjfWx_form").attr("action", param.actionUrl);
				// $("#yjfWx_protocol").val(param.protocol);
				$("#yjfWx_service").val(param.service);
				$("#yjfWx_version").val(param.version);
				$("#yjfWx_partnerId").val(param.partnerId);
				$("#yjfWx_orderNo").val(param.orderNo);
				$("#yjfWx_signType").val(param.signType);
				$("#yjfWx_outOrderNo").val(param.outOrderNo);
				// $("#yjfWx_uiStyle").val(param.uiStyle);
				$("#yjfWx_sellerUserId").val(param.sellerUserId);
				$("#yjfWx_goodsClauses").val(param.goodsClauses);
				$("#yjfWx_tradeAmount").val(param.tradeAmount);
				$("#yjfWx_sign").val(param.sign);
				// $("#yjfWx_currency").val(param.currency);
				$("#yjfWx_returnUrl").val(param.returnUrl);
				$("#yjfWx_notifyUrl").val(param.notifyUrl);
				$("#yjfWx_form").submit();
			}else{
				layer.closeAll();
				layer.alert(data.info, {icon: 2});
			}
		},
		error: function(xhr, status) {
			layer.closeAll();
			console.log("error--->" + status);
		}
	});
});
