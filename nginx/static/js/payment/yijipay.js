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
		goodsName: decodeURIComponent(GetQueryString("four")),
		paymentType: GetQueryString("five"),
		userTerminalType: GetQueryString("six")
	};
	if(GetQueryString("postflag")){
		params.postflag = GetQueryString("postflag");
	}
	layer.load(1, {shade: 0.5});
	$(".layui-layer-loading1").css("margin", "auto");
	$.ajax({
		url: "../payment/yijipay/getpayparam",
		data: JSON.stringify(params),
		type: "post",
		async: false,
		contentType: "application/json",
		dataType: "json",
		success: function(data) {
			if (data.suc) {
				var param = data.info;
				$("#yijipay_form").attr("action", param.actionUrl);
				$("#orderNo").val(param.orderNo);
				$("#merchOrderNo").val(param.merchOrderNo);
				$("#service").val(param.service);
				$("#version").val(param.version);
				$("#partnerId").val(param.partnerId);
				$("#signType").val(param.signType);
				$("#sign").val(param.sign);
				$("#notifyUrl").val(param.notifyUrl);
				$("#returnUrl").val(param.returnUrl);
				$("#userTerminalType").val(param.userTerminalType);
				$("#goodsName").val(param.goodsName);
				$("#sellerUserId").val(param.sellerUserId);
				$("#tradeAmount").val(param.tradeAmount);
				$("#paymentType").val(param.paymentType);
				$("#yijipay_form").submit();
			} else {
				layer.closeAll();
				layer.alert(data.msg, {icon: 2});
			}
		},
		error: function(xhr, status) {
			layer.closeAll();
			console.log("error--->" + status);
		}
	});
});
