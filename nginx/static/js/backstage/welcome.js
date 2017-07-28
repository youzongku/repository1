//定义全局变量
var layer = undefined;

//初始化登录历史信息
function init_welcome(layerParam) {
	//初始化全局变量
	layer = layerParam;
	
	ajax_get_login_history();
	ajax_get_sales_orders();
	ajax_get_frecharge_audit();
	ajax_get_srecharge_audit();
	ajax_get_withdraw_audit();
}

//获取登录历史信息
function ajax_get_login_history() {
	ajax_get("../member/admin/loginhistory?" + Math.random(), {}, undefined,
		function(response) {
			if (response.suc) {
				$("#last_login_time").text(deal_with_illegal_value(response.info.recent));
				$("#recent_login_time").text(deal_with_illegal_value(response.info.number));
				//清除之前的欢迎内容，防止叠加显示
				if($(".wel_icon").parent().children("span").length>1){
					$(".wel_icon").parent().children("span").last().remove();
				}
				$(".wel_icon").after("<span style='display:inline-block;'>欢迎登录；"+response.info.email+"【"+response.info.roleName+"】！<span>");
				$(".search_category h4").text("尊敬的"+response.info.email+"：");
			} else {
				window.location.href = "login.html";
			}
		},
		function(xhr, status) {
			layer.msg('获取登录历史信息出错', {icon : 2, time : 1000});
		}
	);
}

//获取待审核的销售发货订单数
function ajax_get_sales_orders() {
	ajax_post("../sales/manager/ctslodr", JSON.stringify({ status: 3 }), "application/json",
		function(response) {
			$("#sales_order_number_span").text(response);
		},
		function(xhr, status) {
			layer.msg('获取待审核的销售发货订单数出错', {icon : 2, time : 1000});
		}
	);
}


//获取充值初审待审核的条目数
function ajax_get_frecharge_audit() {
	var params = {
		auditState: 0,
		currPage: 1,
		pageSize: "10",
		applyType: 1,
		receiptMode: "",
		search: "",
		time: ""
	};
	ajax_post("/member/queryApply", JSON.stringify(params), "application/json",
		function(data) {
			if (data.success) {
				$("#first_recharge_audit").text(data.result.rows);
			} else {
				window.location.href = "login.html";
			}
		},
		function(xhr, status) {
			layer.msg("获取充值申请数据出错，请稍后重试！", {icon: 6, time: 2000});
		}
	);
}

//获取充值复审待审核的条目数
function ajax_get_srecharge_audit() {
	var params = {
		auditOrreview: 1,
		auditState: "",
		currPage: 1,
		pageSize: "10",
		applyType: 1,
		receiptMode: "",
		reviewState: 4,
		search: "",
		time: ""
	};
	ajax_post("/member/queryApply", JSON.stringify(params), "application/json",
		function(data) {
			if (data.success) {
				$("#second_recharge_audit").text(data.result.rows);
			} else {
				window.location.href = "login.html";
			}
		},
		function(xhr, status) {
			layer.msg("获取充值申请数据出错，请稍后重试！", {icon: 6, time: 2000});
		}
	);
}

/**
 * 获取待审核的提现申请数
 * @Author LSL on 2016-09-21 15:10:34
 */
function ajax_get_withdraw_audit() {
    var params = {
        currPage: 1,
        pageSize: 10,
        applyType: '2',
        auditState: 0
    }
    ajax_post("/member/queryWithdraw", JSON.stringify(params), "application/json",
        function(data) {
            if (data.success) {
                $("#withdraw_audit_number").text(data.result.rows);
            } else if (data.code == 2) {
                window.location.href = "login.html";
            } else {
                layer.alert(data.msg, {icon: 2});
            }
        },
        function(xhr, status) {
            layer.msg("获取待审核提现申请数据出错，请稍后重试！", {icon: 2, time: 2000});
        }
    );
}

//折叠菜单栏
$(".numbercontent").on("click",function(event){
	var menu_type;
	var menu_type_child;
	var position = $(this).data("position");
	if (position == 5){
		//$("#sales_order_number_span").parents("body").find("#nav-left .menu_head")[2]
		menu_type = $(this).parents("body").find("#nav-left .menu_head")[2];
		menu_type_child = $(menu_type).next().find("p")[1];
		foldmenu(menu_type, menu_type_child);
	} else if (position == 10){
		menu_type = $(this).parents("body").find("#nav-left .menu_head")[3];
		menu_type_child = $(menu_type).next().find("p")[1];
		foldmenu(menu_type, menu_type_child);
	} else {
		menu_type = $(this).parents("body").find("#nav-left .menu_head")[3];
		menu_type_child = $(menu_type).next().find("p")[2];
		foldmenu(menu_type, menu_type_child);
	}
})

function foldmenu(menu_type, menu_type_child){
		var bodys = $('.menu_body')
	//$(#sales_order_number_span).parents("body").find("#nav-left .menu_head")[2];
	//$($("#sales_order_number_span").parents("body").find("#nav-left .menu_head")[2]).next().find("p")[1];
	$(".back-current").removeClass();
	//$($(menu_type).next().find("p")[1]).addClass("back-current");
	$(menu_type_child).addClass("back-current");
		if (menu_type == isOpen) {
			if(isRotate == true){
				$(menu_type).children("span").css({
					transform: "rotate(0deg)"
				})
				isRotate = false;
			}else{
				$(menu_type).children("span").css({
					transform: "rotate(45deg)"
				})
				isRotate = true;
			}
			$(menu_type).siblings(".menu_body").stop(false,false).slideToggle();
			return
		} else {
			$('.menu_body').slideUp("500");
			for (var i = 0; i < bodys.length; i++) {
				if ($(bodys[i]).css('display') == 'block') {
					$(bodys[i]).siblings().children("span").css({
						transform: "rotate(0deg)"
					})
				}
			}
			$(menu_type).children("span").css({
				transform: "rotate(45deg)"
			})
			isRotate = true;
			$(menu_type).siblings(".menu_body").stop(false,true).slideToggle();
		}
		isOpen = menu_type;
}