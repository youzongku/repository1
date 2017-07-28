
require.config({
	baseUrl : "../js/",
	paths : {
		"jquery" : "lib/jquery-1.11.3.min",
        "layer" : "lib/layer2.0/layer",
        "common" : "common"
	},
	shim : {
		"layer" : {
            exports : "",
            deps : ["jquery"]
		},
		"common" : {
			exports : "",
            deps : ["jquery"]
		}
	}
});

require(["jquery", "layer", "common"], function($, layer) {
	//登录状态下直接跳转到后台首页
	ajax_get("/member/isaulogin?"+(new Date()).getTime(), {}, undefined,
		function(response) {
			if (response.suc) {
				window.location.href = "/backstage/index.html";
			}
		},
		function(xhr, status) {
			console.log("status-->" + status);
		}
	);
	$("input[name='user']").focus();
	refresh_date_time();
	window.setInterval("refresh_date_time()", 59000);
	//登录按钮
	$("span.login_button").click(function(event) {
		var user = $("input[name='user']").val();
		var cipher = $("input[name='cipher']").val();
		if (user == undefined || user == "" || cipher == undefined || cipher == "") {
			layer.msg('请输入用户名和登录密码', {icon : 5,time:2000}, function(index){ layer.close(index); });
			return;
		}
        ajax_post("/member/adminlogin", {user : user, cipher : cipher}, undefined,
        	function(response) {
	            if (response.success == "true") {
	                window.location.href = "/backstage/index.html";
	            } else {
	                layer.msg(response.message, {icon : 5,time:2000}, function(index){ layer.close(index); });
	                fix_layer_css();
	            }
			},
			function(xhr, status) {
				layer.msg('登录提交出错，请稍候重试', {icon : 5,time:2000}, function(index){ layer.close(index); });
				fix_layer_css();
        	}
		);
	});

	//回车(Enter)触发登录或注册表单提交
    $("body").keyup(function(event) {
        var key = event.keyCode || event.which;
        if (key == 13) {
            if ($(".layui-layer")[0] != undefined) {
                layer.closeAll();
            } else {
                $("span.login_button").click();
            }
        }
    });
});

//修复layer弹出框位置偏下问题
var fix_layer_css = function() {
    $($(".layui-layer")[0]).css({"top" : (parseInt($($(".layui-layer")[0]).offset().top)/2)});
};
