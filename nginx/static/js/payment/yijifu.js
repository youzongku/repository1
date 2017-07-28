
// var orderNo = document.getElementById("out_trade_no").value;

var sub_status = false;
function beforeSubmit(){
	return sub_status;
}

//获取请求地址后的参数
function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null)return unescape(r[2]);
    return null;
}

$(function alipay(){
    var builds = decodeURIComponent(GetQueryString("builds"));
	var url = "/payment/yijipay/unionPay";
	var param = {
		id: GetQueryString("one"),
		outOrderNo: GetQueryString("two"),
		tradeAmount: GetQueryString("three"),
		goodsName: decodeURIComponent(GetQueryString("four")),
		paymentType: GetQueryString("five"),
		userTerminalType: GetQueryString("six")
	};
	$.ajax({
		url : url,
		type : "POST",
		dataType : "json",
		contentType: "application/json",
		async: false,
		data: JSON.stringify(param),
		success : function(data) {
			var returnMess = data.returnMess;
			if(returnMess.errorCode == "0"){
				$("#yijifusubmit").attr("action", data.actionUrl);
				$("#orderNo").val(data.orderNo);
				$("#service").val(data.service);
				$("#version").val(data.version);
				$("#partnerId").val(data.partnerId);
				$("#signType").val(data.signType);
				$("#merchOrderNo").val(data.merchOrderNo);
				$("#notifyUrl").val(data.notifyUrl);
				$("#returnUrl").val(data.returnUrl);
				$("#buyerUserId").val(data.buyerUserId);
				$("#tradeInfo").val(JSON.stringify(data.tradeInfo));
				$("#paymentType").val(data.paymentType);
				$("#userTerminalType").val(data.userTerminalType);
				$("#sign").val(data.sign);
			}else{
				alert("获取支付信息出错:"+returnMess.errorInfo);
			}
		}
	}); 
    sub_status = true;
    $("#yijifusubmit").submit();	
    sub_status = false;
});