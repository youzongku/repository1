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

require(["jquery", "layer","common"], function($, layer) {
    var email =  getUrlParam(window.location.search,"e");
    var code = getUrlParam(window.location.search,"c");

    ajax_post("/member/checkPayPwdEmailTime", JSON.stringify({email : email,code : code}), "application/json",
        function(data) {
            if (data && data.suc == 0){//表示未登录
                $(".new-password").hide();
                $(".new-password").next().hide();
            } else if (data.suc == 3){//表示链接超时
                $(".new-password").hide();
                $(".new-password").next().next().hide();
                $(".link-send").on("click", function(event){
                    ajax_post("/member/repeatPayPwdsendByEmail", JSON.stringify({email: email}), "application/json",
                        function(data) {
                            if (data.suc) {
                                layer.msg("已重发，请查收", {icon : 1, time : 2000});
                            } else {
                                layer.msg(data.msg, {icon : 2, time : 2000});
                                return;
                            }
                        },
                        function(xhr, status) { console.log("error--->" + status); }
                    );
                });
            } else if (data.suc == 7){//表示成功
                $(".new-password").next().hide();
                $(".new-password").next().next().hide();
            } else if (data.suc == 4){//表示邮件里的url是第二次点击
                $(".new-password").hide();
                $(".new-password").next().next().hide();
                $(".link-send").on("click", function(event){
                    ajax_post("/member/repeatPayPwdsendByEmail", JSON.stringify({email: email}), "application/json",
                        function(data) {
                            if (data.suc) {
                                layer.msg("已重发，请查收", {icon : 1, time : 2000});
                            } else {
                                layer.msg(data.msg, {icon : 2, time : 2000});
                                return;
                            }
                        },
                        function(xhr, status) { console.log("error--->" + status); }
                    );
                });
            } else if (data.suc == 5){//表示你当前点击的邮件不是最新的邮件
                $(".new-password").hide();
                $(".new-password").next().next().hide();
                $(".link-send").on("click",function(event){
                    ajax_post("/member/repeatPayPwdsendByEmail", JSON.stringify({email: email}), "application/json",
                        function(data) {
                            if (data.suc) {
                                layer.msg("已重发，请查收", {icon : 1, time : 2000});
                            } else {
                                layer.msg(data.msg, {icon : 2, time : 2000});
                                return;
                            }
                        },
                        function(xhr, status) { console.log("error--->" + status); }
                    );
                });
            }
        },
        function(xhr, status) { console.log("error--->" + status); }
    );
    //重新发送
    //function repeatsend(layer, email){
    //    ajax_post("/member/repeatPayPwdsendByEmail", JSON.stringify({email: email}), "application/json",
    //        function(data) {
    //            if (data.suc) {
    //                layer.msg("已重发，请查收", {icon : 1, time : 2000});
    //            } else {
    //                layer.msg(data.msg, {icon : 2, time : 2000});
    //                return;
    //            }
    //        },
    //        function(xhr, status) { console.log("error--->" + status); }
    //    );
    //}

    //确定按钮
    $(".new-password .sure-btn").on("click", function(event){
        var newpass =  $(".new-password .password").val();
        var affirmPW = $(".new-password .again-password").val();
        //修改原有校验，增强密码复杂度 by huchuyin 2016-10-6
        //var PW_RE = /(?!^[0-9]+$)(?!^[a-zA-Z]+$)(?!^[^0-9a-zA-Z]+$)^.{6,20}$/;
        //if (!(PW_RE.test(newpass))) {
        //    layer.msg('密码必须为6-20个字符，且至少包含数字、字母(区分大小写)等两种或以上字符', {icon : 2, time : 2000});
        //    return;
        //}
        if((newpass.search(/[0-9]/) == -1)
	    	|| (newpass.search(/[A-Z]/) == -1)
	    	|| (newpass.search(/[a-z]/) == -1)
	    	|| (newpass.length < 6) || (newpass.length > 20)) {
	    	layer.msg('密码必须为6-20个字符，且至少包含数字、大写、小写字母等三种或以上字符！', {icon: 2, time: 2000});
	    	return;
	    }
	    //End by huchuyin 2016-10-6
        if (affirmPW != newpass) {
            layer.msg('确认密码与新密码不匹配，请重新输入', {icon : 2, time : 2000});
            return;
        }
        var params = {
            "email" : email,
            "password" : newpass,
            "code" : code
        };
        ajax_post("/member/resetpayPassword", JSON.stringify(params), "application/json",
            function(data) {
                if (data && data.suc == 2){
                    layer.msg(data.msg, {icon : 1, time : 2000}, function() {
                        window.location.href = "/personal/personal.html?emnunum=16";
                    });
                } else {
                    layer.msg(data.msg, {icon : 1, time : 2000});
                    return;
                }
            },
            function(xhr, status) { console.log("error--->" + status); }
        );
    });
    //取消按钮
    $(".cancel-btn").on("click", function(event){
        $("#apply-list").fadeIn(200);
        //$("#pay_new_code").val("");
        //$("#pay_sure_code").val("");
        $("#captchaImg").prev().val("");
        $("div.six-user").fadeIn(200);
        $("div.five-date").fadeIn(200);
        $("#Topup-paycode-set").fadeOut(200);
        $("#send_email_pay").fadeOut(200);
        window.location.href = "/personal/personal.html?emnunum=16";
    });
});

