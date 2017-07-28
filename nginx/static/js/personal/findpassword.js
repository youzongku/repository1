//定义全局变量
var layer = undefined;

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

require(["jquery", "layer", "common"], function($, layerParam) {
	//初始化全局变量
	layer = layerParam;
	console.log("layer version-->" + layer.v);

	var url = location.search;
	//邮箱
	var email = getUrlParam(url,"email");
	//修改密码标识
	var isSuccess = getUrlParam(url,"isSuccess");
	//手机账号 找回密码标识
	var flag = getUrlParam(url,"flag");
	var eflag = getUrlParam(url,"eflag");

	//发送邮件之后验证密码页面初始化
	if($("#emailSpan") != undefined && $("#emailSpan")!= undefined && $("#sendAgain") != undefined){
		$("#emailSpan").text(email);
		//$("#changePasswordA").attr("href","../personal/find_password_reset.html?email="+email);
		$("#sendAgain").click(function(){
			sendAgain(email);
		});
	}

	if(flag == "true") {//如果是手机账户找回，显示手机找回
		$(".find-content").hide();
		$(".find-phone-content").show();
	}
	if(eflag == "true"){
		$(".phone-safe").show();
		$("title").text("手机验证 - TOMTOP Supply Chain");
	}else{
		//修改密码页面初始化
		if($("input[name='email']")!=undefined){
			$("input[name='email']").val(email);
		}
		$(".cart").show();
	}
	//成功修改密码结果页面初始化
	if(isSuccess !=undefined && isSuccess != ""){
		if(isSuccess==true || isSuccess == "true"){
			$("#resetSuccess").attr("style","display:");
		} else{
			$("#resetFail").attr("style","display:");
		}
	}
	$(".find-phone-content").find("#email").blur(function(){
		var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/;
		var telNo = $(this).val();
		if (!(CP_RE.test(telNo))) {
            layer.msg('手机号码为空或格式错误，请输入正确的手机号码', {icon : 2, time : 2000});
            fix_layer_css();
            return;
        }
		if(!checkEmail(telNo)){
			layer.msg('该账号不存在', {icon : 6, time : 3000});
            fix_layer_css();
            return;
		}
	})
	$(".phone-safe").find("#email").blur(function(){
		var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/;
		var telNo = $(this).val();
		if (!(CP_RE.test(telNo))) {
            layer.msg('手机号码为空或格式错误，请输入正确的手机号码', {icon : 2, time : 2000});
            fix_layer_css();
            return;
        }
	});
});

function sendToTel(obj){
	var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/;
	var telNo = $(".phone-safe").find("#email").val();
	if (!(CP_RE.test(telNo))) {
        layer.msg('手机号码为空或格式错误，请输入正确的手机号码', {icon : 2, time : 2000});
        fix_layer_css();
        return;
    }
    //60秒倒计时
    toggle_sendmsg_btntext(obj);
    getTelCaptcha(telNo);
}

function sendCheckCode(){
	var email = getUrlParam(location.search,"email");
	var telNo = $(".phone-safe").find("#email").val();
	var smsc = $(".phone-safe").find("input[name=smsc]").val();
	var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/;
	var telNo = $(".phone-safe").find("#email").val();
	if (!(CP_RE.test(telNo))) {
        layer.msg('手机号码为空或格式错误，请输入正确的手机号码', {icon : 2, time : 2000});
        fix_layer_css();
        return;
    }
    if(!smsc){
		layer.msg('手机验证码不能为空', {icon : 2, time : 2000});
        fix_layer_css();
        return;
	}
	var params = {
		email:email,
		cell:telNo,
		smsc:smsc
	};
	ajax_post("/member/sendCheckCode", JSON.stringify(params), "application/json",
		function(data) {
			if(data.suc){
				window.location.href = "/personal/find_password_reset.html?m="+data.email+"&s="+data.smsc+"&code="+data.code+"&cell="+data.cell;
			}else{
				layer.msg(data.msg, {icon : 2, time : 2000});
			}
		},
		function(xhr, textStatus) {
			layer.msg('验证码验证异常，系统错误！', {icon : 2, time : 2000});
		}
	);
}

// 发送手机验证码
function checkCode(){
	var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/;
	// var test_smsc = /[a-zA-Z0-9]{6}$/;
	var captcha = $(".find-phone-content").find("#captcha").val();
	var telNo = $(".find-phone-content").find("#email").val();
	if (!(CP_RE.test(telNo))) {
        layer.msg('手机号码为空或格式错误，请输入正确的手机号码', {icon : 2, time : 2000});
        fix_layer_css();
        return;
    }
	if(!checkEmail(telNo)){
		layer.msg('该账号不存在', {icon : 6, time : 3000});
        fix_layer_css();
        return;
	}
	if(!captcha){
		layer.msg('验证码不能为空',{icon : 2, time : 2000});
        return;
	}
	var params = {
		email:telNo,
		captcha:captcha
	}
	ajax_post("/member/checkTelFindPWD", JSON.stringify(params), "application/json",
		function(data) {
			if(data.suc){
			   window.location.href = "/personal/find_password_validate.html?eflag=true&email="+data.email;
			}else{
				layer.msg(data.msg,{icon: 2 , time:2000});
			}
		},
		function(xhr, textStatus) {
			layer.msg('验证码验证异常，系统错误！', {icon : 2, time : 2000});
		}
	);
}

//邮箱校验  判断账号是否存在
function checkEmail(email){
	var flag = false;
	if(email!=undefined && email != ""){
		var url = "/member/checkEmail?"+(new Date()).getTime();
		$.ajax({
	        url : url,
	        type : "GET",
	        data : {email:email},
	        async:false,
	        success : function(data) {
	        	if(data){
	        		flag = true;
	        	}
	        }
	    });
	}
	return flag;
}

//修复layer弹出框位置偏下问题
var fix_layer_css = function() {
	$($(".layui-layer")[0]).css({"top" : (parseInt($($(".layui-layer")[0]).offset().top)/2)});
};

//更新验证码
function change(imgObj){
	var url = "/member/getcaptcha"+"?date="+new Date().getTime();
	imgObj.src=url;
}

//修改密码
function savePassword(){
	if($("#confirm_password")!=undefined && $("#password")!=undefined){
		var url = location.search;
		var d = getUrlParam(url,"d");
		var m = getUrlParam(url,"m");
		var s = getUrlParam(url,"s");
		//UUID by huchuyin 2016-10-10
		var code = getUrlParam(url,"code");
		var cell = getUrlParam(url,"cell");
		//End by huchuyin 2016-10-10
		// var PW_RE = /(?!^[0-9]+$)(?!^[a-zA-Z]+$)(?!^[^0-9a-zA-Z]+$)^.{6,20}$/;
		var confirm_password = $("#confirm_password").val();
		//修改原有校验，增强密码复杂度 by huchuyin 2016-10-6
		//if (!(PW_RE.test(confirm_password))) {
		//	layer.msg('密码必须为6-20个字符，且至少包含数字、字母(区分大小写)等两种或以上字符', {icon : 2, time : 2000});
		//	fix_layer_css();
		//	return;
		//}
		if((confirm_password.search(/[0-9]/) == -1)
        	|| (confirm_password.search(/[A-Z]/) == -1)
        	|| (confirm_password.search(/[a-z]/) == -1)
        	|| (confirm_password.length < 6) || (confirm_password.length > 20)) {
        	layer.msg('密码必须为6-20个字符，且至少包含数字、大写、小写字母等三种或以上字符！', {icon: 2, time: 2000});
        	fix_layer_css();
        	return;
	    }
	    //End by huchuyin 2016-10-6
		var password = $("#password").val();
		if(confirm_password && password ){
			if(confirm_password != password){
				layer.msg('两次输入的密码不正确，请重新输入！', {icon : 2, time : 2000});
				fix_layer_css();
				return;
			}else{
				if(s && m){
					ajax_post("/member/reseloginPWDbyTEL", {email : m,smsc : s,password:password,code:code,cell:cell }, undefined,
						function(data) {
							if(data.success){
								window.location.href = "../personal/find_password_result.html?isSuccess="+data.success;
							}else{
								layer.msg(data.info,{icon : 5, time : 2000});
							}
						},function(xhr, textStatus) {
							layer.msg('修改密码失败，系统错误！', {icon : 2, time : 2000});
						}
					);
				}else if(d && m){
					ajax_post("/member/getRecord", {d : d,m : m}, undefined,
						function(data) {
							if(data){
								reset(data.email,password);
							}else{
								layer.msg('链接过时，请重新找回密码！', {icon : 2, time : 2000});
							}
						},
						function(xhr, textStatus) {
							layer.msg('修改密码失败，系统错误！', {icon : 2, time : 2000});
						}
					);
				}else{
					ajax_get("/member/isnulogin", {}, undefined,
						function(data) {
							if(data.suc){
								reset(data.user.email,password);
							}else{
								window.location.href = "login.html";
							}
						},
						function(xhr, textStatus) {
							layer.msg('修改密码失败，系统错误！', {icon : 2, time : 2000});
						}
					);
				}
			}
		}
	}
}

function reset(email,pass){
	ajax_post("/member/resetPasswordRemote", {email:email,password:pass}, undefined,
		function(data) {
			if(data.success){
				window.location.href = "../personal/find_password_result.html?isSuccess="+data.success;
			}else{
				layer.msg(data.info,{icon : 5, time : 2000});
			}
		},
		function(xhr, textStatus) {
			layer.msg('修改密码失败，系统错误！', {icon : 2, time : 2000});
		}
	);
}

//重新发送
function sendAgain(email){
	if(email!=undefined && email != ""){
		$.ajax({
			url : "/member/sendEmailAgain",
			type : "POST",
			data : JSON.stringify({"email" : email}),
			dataType : "json",
			contentType : "application/json",
			success : function(data) {
				if(!data.recordOverflag){
					if(data.sendEmailFlag){
						layer.msg('邮件发送成功！', {icon : 1, time : 2000});
					} else{
						layer.msg('邮件发送失败！', {icon : 2, time : 2000});
					}
				} else{
					layer.msg('当天找回密码次数超过三次，不能进行找回密码！', {icon : 2, time : 2000});
				}
			},
			error : function(XMLHttpRequest) {
				layer.msg('邮件发送失败，系统错误！', {icon : 2, time : 2000});
			}
		});
	}
	else{
		layer.msg('邮件发送失败，邮件地址为空！', {icon : 2, time : 2000});
	}
}

//发送邮件找回密码
function sendEmail(){
	var email = $("#email").val();
	var captcha = $("#captcha").val();
	var EM_RE = /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
	if (!(EM_RE.test(email))) {
		layer.msg('邮箱格式错误，请输入正确的邮箱', {icon : 2, time : 2000});
		fix_layer_css();
		return;
	}
	if(!checkEmail(email)){
		layer.msg('邮箱不存在，请输入正确的邮箱', {icon : 2, time : 2000});
		fix_layer_css();
		return;
	}
	if(!checkCaptcha(captcha)){
		layer.msg('验证码错误或为空，请输入正确的验证码', {icon : 2, time : 2000});
		fix_layer_css();
		$("#captchaImg").attr("src","/member/getcaptcha"+"?date="+new Date().getTime());
//        change();
		return;
	}
	$.ajax({
		url : "/member/sendEmailAgain",
		type : "POST",
		data : JSON.stringify({"email" : email}),
		dataType : "json",
		contentType : "application/json",
		success : function(data) {
			if(!data.recordOverflag){
				window.location.href="../personal/find_password_validate.html?email="+email;
				/*if(data.sendEmailFlag){
					window.location.href="../personal/find_password_validate.html?email="+email;
				}*/
				/*else{
					window.location.href="../personal/find_password_result.html?sendEmailFlag="+sendEmailFlag;
				}*/
			} else{
				layer.msg('当天找回密码次数超过三次，不能进行找回密码！', {icon : 2, time : 2000});
			}
		},
		error : function(XMLHttpRequest) {
			layer.msg('邮件发送失败，系统错误！', {icon : 2, time : 2000});
		}
	});
}

//验证码校验
function checkCaptcha(captcha){
	var flag = false;
	if(captcha!=undefined && captcha != ""){
		$.ajax({
			url : "/member/checkcaptcha",
			type : "POST",
			data : {captcha : captcha},
			async:false,
			success : function(data) {
				if(data){
					flag = true;
				}
			}
		});
	}
	return flag;
}

//邮箱校验
function checkEmail(email){
	var flag = false;
	if(email!=undefined && email != ""){
		var url = "/member/checkEmail";
		$.ajax({
			url : url,
			type : "GET",
			data : {email:email},
			async:false,
			success : function(data) {
				if(data){
					flag = true;
				}
			}
		});
	}
	return flag;
}

//发送短信
function getTelCaptcha(telphone){
	//参数新增发送短信类型 by huchuyin 2016-10-11
    var param = {tel : telphone,types:4};
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