define("myaccount", ["jquery", "layer"], function($, layer) {
	//检查账户是否被冻结
	function checkFrozen() {
		$.ajax({
			url : "/member/checkFrozen?"+(new Date()).getTime(),
			type : "GET",
			dataType : "json",
			async : true,
			contentType : "application/json",
			success : function(data) {
				if(data.code == 100){
					var result = data.obj.result;
					// var credits = data.credits;
					var nick = data.nick;
					$("#member").text(nick ? (nick + "("+result.email+")") : result.email);
					//首次访问个人中心
					$("#balance").text(fmoney(result.balance,2));
					var acPeriond = data.acPeriond;
					if(result.frozen){
						//账户被冻结
						// $(".personal").replaceWith("<div class='frozen'><p>您的账户已被冻结，请联系管理员！</p></div>");
						$("button[tag=frozen]").show();
					}else{
						$("button[tag=frozen]").hide();
					}
					/*if(credits && credits[0]){
						creditInformation(credits);
					}else{
						$($(" .box-right-one-2")[0]).find("li").eq(1).hide();
						$($(" .box-right-one-2")[1]).hide();
						$(".limit-validity-box").hide();
						$(".warm-tip").css({"margin-top" : "0px", "border-left" : "2px solid #FFFFFF"});
					}*/
					if(acPeriond){
                        $("#credit_limit").text(acPeriond.totalLimit);
                        $("#available_credit_limit").text(acPeriond.leftLimit);
                        $("#used_amount").text(acPeriond.usedLimit);
                        $("#startTime").text(acPeriond.startTimeStr);
                        $("#forRefundTime").text(acPeriond.contractPeriodDateStr);
                        $("#lastForRefundTime").text(acPeriond.redLineDateStr);
						if(acPeriond.state == 4){
							$("#disable-the").show();
						}
                    }
					$(".time-up:eq(1)").text(result.historys.lastLoginTime);
					$("td.user_name").text(data.nick == "" ? result.email : data.nick);
					get_purchase_amount();
					get_sales_amount();
				} else {
					window.location.href = "/personal/login.html";
				}
			}
		});
		//loading
		return creatLoading(true,theDiv);
	}

	//获取采购订单数
	function get_purchase_amount() {
		isnulogin(function(email) {
			ajax_post("/purchase/getAmount", JSON.stringify({ email: email }), "application/json",
				function(data) {
					if (data.returnMess.errorCode == "0") {
						$("#purchase_amount").text(data.amount);
					} else {
						layer.msg(data.returnMess.errorInfo, {icon : 2}, function(index){ layer.close(index); });
					}
				},
				function(xhr, status) {
					//layer.alert("获取采购订单数出错，请稍后重试！", {icon : 2}, function(index){ layer.close(index); });
				}
			);
		});
	}

	//获取销售订单数
	function get_sales_amount() {
		isnulogin(function(email) {
			ajax_post("/sales/ctslodr", JSON.stringify({ email: email, months: 1 }), "application/json",
				function(data) {
					$("#sales_amount").text(data);
				},
				function(xhr, status) {
					//layer.alert("获取销售订单数出错，请稍后重试！", {icon : 2}, function(index){ layer.close(index); });
				}
			);
		});
	}

	return {
		checkFrozen: checkFrozen
	};
});