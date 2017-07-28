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

var  emailFlag  = false;
define("alterphone", ["jquery", "layer","common"], function($, layer) {
    //绑定修改手机页面相关HTML元素事件
    function bind_phone_event() {
        var email = $($(".nav-top-left a")[0]).text();
        var EM_RE = /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
        emailFlag = EM_RE.test(email);
        if(emailFlag){
            $("#alter_phone_form").show();
            $("#send_email_div").hide();
            $("#alter_phone_form .alter-youremail").text(email);  
            $(".captchaImg1").click();
        }else{
            $("#alter_phone_form").hide();
            $("#alter_phone_box").show();
            $("#alter_phone_box .alter-youremail").text(email);
            $(".captchaImg2").click();
        }
        //第一步中的确定按钮
        $("#alter_phone_form .sure-change").on("click", function(event) {
            $("#alter_phone_form .sure-change").css({"cursor":"not-allowed", "color": "white","background-color":"rgb(204, 204, 204)"});
            $("#alter_phone_form .sure-change").attr("disabled", "disabled");
            var captcha = $("#alter_phone_form").find("input[name='captcha']").val();
            if (captcha == undefined || captcha == '') {
                layer.msg('验证码不能为空', {icon : 2, time : 2000});
                $("#alter_phone_form .sure-change").removeAttr("disabled");
                $("#alter_phone_form .sure-change").css({"cursor":"pointer", "color": "white","background-color":"rgb(17, 122, 212)"});
                return;
            }
            var params = {
                email: email,
                captcha: captcha
            };

            ajax_post("/member/applymodicell", params, undefined,
                function(data) {
                    if (data.suc) {
                        $("#alter_phone_form").hide();
                        $("#send_email_div").show();
                        $("#email_result_span").text(data.email);
                        $("#send_email_div a").eq(1).data("email", data.email);
                    } else {
                        layer.msg(data.msg, {icon : 2, time : 2000});
                        $("#alter_phone_form .sure-change").removeAttr("disabled");
                        $("#alter_phone_form .sure-change").css({"cursor":"pointer", "color": "white","background-color":"rgb(17, 122, 212)"});
                    }
                },
                function(xhr, status) { console.log("error--->" + status); }
            );
        });

        //第二步重新发送链接
        $("#send_email_div a").eq(1).on("click", function(event) {
        	var email = $(this).data("email");
        	ajax_post("/member/repeatsendmpe", JSON.stringify({email: email}), "application/json",
				function(data) {
				    if (data.suc) {
						layer.msg("已重发，请查收", {icon : 1, time : 2000});
				    } else {
				        layer.msg(data.msg, {icon : 2, time : 2000});
				    }
				},
                function(xhr, status) { console.log("error--->" + status); }
            );
        });
    }

   //第三步获取短信验证码
    $(".send-message-b").click(function() {
        var email = getUrlParam(window.location.search,"e");
        var cell = $("input[name='cell']").val();
        var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/;
        if (!CP_RE.test(cell)) {
            layer.msg("手机号为空或格式有误，请重新输入", {icon: 2, time: 2000});
            return;
        }
        //判断新手机号是否和旧手机号是否相同，相同则取消短信发送
        var flag = true;
        $.ajax({
            url: "/member/infor?" + email,
            type: 'GET',
            data: {},
            async: false,//是否异步
            dataType: 'json',
            success: function (data) {
                if (data) {
                    var tel = data.tel;//原来的手机号
                    if (tel == cell) {
                        layer.msg('请确认新手机号码和旧手机号码是否一致', {icon: 2, time: 2000});
                        flag = false;
                    }
                } else {
                    layer.msg('请先登录', {icon: 2, time: 2000});
                }
            },
            error: function (XMLHttpRequest, textStatus) {
                layer.msg('后台系统出现故障', {icon: 2, time: 2000});
                return;
            }
        });
        if (flag == true) {
           toggle_sendmsg_btntext(this)
           getTelCaptcha(cell);
        }
    })

    function getTelCaptcha(telphone){
    	//参数新增发送短信类型 by huchuyin 2016-10-11
            var param = {tel : telphone,types:5};
            ajax_post("/member/message", JSON.stringify(param), "application/json",
                function (data) {
                    if(!data) {
                        layer.alert('当前页面已失效，请刷新页面。', function(index){
                            layer.close(index);
                            location.reload();
                        });
                        return;
                    }
                    if (data.suc) {
                        layer.msg(data.msg,{icon : 6, time : 2000});
                    } else {
                    	layer.msg(data.msg,{icon : 2, time : 2000});
                    }
                },
                function (xhr, status) {
                    layer.msg('短信发送失败，系统错误！', {icon : 2, time : 2000});
                }
            );

        }

    //第四步：点击确定按钮修改手机号码
    $("#reset_phone_form .phone-change").click(function(){
        var cell = $("input[name='cell']").val();
        var smsc = $("input[name='smsc']").val();
        var captcha = $("input[name='captcha']").val();
        var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/;
        var test_smsc = /[a-zA-Z0-9]{6}$/;
        var email =  getUrlParam(window.location.search,"e");
        var code = getUrlParam(window.location.search,"c");
        if (!CP_RE.test(cell)) {
            layer.msg("手机号为空或格式有误，请重新输入", {icon : 2, time : 2000});
            return;
        }
        if(!test_smsc.test(smsc)){
            layer.msg('请输入6位验证码',{icon : 2, time : 2000});
            return;
        }
        if(!smsc){
            layer.msg("图片验证码不能为空",{icon : 2, time : 2000});
        }
        var params = {
            code: code,
            cell: cell,
            smsc: smsc,
            captcha: captcha,
            email: email
        };
        $.ajax({
            url: "/member/resetcellphone",
            type: 'POST',
            data: params,
            async: false,//是否异步
            dataType: 'json',
            success: function (data) {
                if (data.suc) {
                    layer.msg("手机号修改成功！", {icon : 1, time : 2000}, function() {
                        window.location.href = "/personal/personal.html?emnunum=2";
                    });
                } else {
                    layer.msg(data.msg, {icon : 2, time : 2000});
                }
            },
            error: function (XMLHttpRequest, textStatus) {
                console.error("error--->" + status);
            }
        });
    });

    //切换获取短信验证码按钮中的文字
    function toggle_sendmsg_btntext(node) {
        var duration = 60;
        $(node).text("已发送(" + duration + ")");
        $(node).css({"cursor":"not-allowed", "color": "white"});
        $(node).attr("disabled", "disabled");
        var intervalID = window.setInterval(function() {
            duration = duration - 1;
            $(node).text("已发送(" + duration + ")");
            if (duration == 0) {
                window.clearInterval(intervalID);
                $(node).text("获取短信验证码");
                $(node).css({"cursor":"pointer", "color": "white"});
                $(node).removeAttr("disabled");
            }
        }, 1000);
    }

    return {
        bind_phone_event: bind_phone_event
    };
});