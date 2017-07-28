
var orderNo = document.getElementById("out_trade_no").value;

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
    var out_trade_no = GetQueryString("tradeNo");
    var order_id = GetQueryString("purno");
    var rmb_fee = GetQueryString("sumPrice");
    var subject = GetQueryString("productName");
    var postflag = GetQueryString("postflag");
	var url = "/payment/alipayGateway";
	var dataParam = {
		out_trade_no:out_trade_no,
		subject:decodeURIComponent(subject),
		order_id:order_id,
		total_fee:rmb_fee,
		body:"测试"
	};
	if(postflag){
		dataParam.postflag = postflag;
	}
	$.ajax({
		url : url,
		type : "POST",
		dataType : "json",
		contentType: "application/json",
		async: false,
		data: JSON.stringify(dataParam),
		success : function(data) {
			var returnMess = data.returnMess;
			if(returnMess.errorCode == "0"){
				var param = data.payParam;
				$("#alipaysubmit").attr("action", param.action_url);
				$("#out_trade_no").val(param.out_trade_no);
				$("#partner").val(param.partner);
				$("#service").val(param.service);
				$("#_input_charset").val(param._input_charset);
				$("#subject").val(param.subject);
				$("#total_fee").val(param.total_fee);
				$("#sign").val(param.sign);
				$("#return_url").val(param.return_url);
				$("#currency").val(param.currency);
				$("#notify_url").val(param.notify_url);
				$("#body").val(param.subject);
				$("#sign_type").val(param.sign_type);
				$("#supplier").val(param.supplier);
				$("#payment_type").val(param.payment_type);
				$("#seller_id").val(param.seller_id);
			}else{
				alert("获取支付信息出错:"+returnMess.errorInfo);
			}
		}
	}); 
    sub_status = true;
    $("#alipaysubmit").submit();	
    sub_status = false;
});
