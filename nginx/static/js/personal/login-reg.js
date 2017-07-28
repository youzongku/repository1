require.config({
	baseUrl : "../js/",
	paths : {
		"jquery" : "lib/jquery-1.11.3.min",
		"layer" : "lib/layer2.0/layer",
		"cookie" : "lib/jquery.cookie",
		"common" : "common",
		"jqform" : "lib/jquery.form"
	},
	shim : {
		"layer" : {
			exports : "",
			deps : ["jquery"]
		},
		"cookie" : {
			exports : "",
			deps : ["jquery"]
		},
		"common" : {
			exports : "",
			deps : ["jquery"]
		},
		"jqform" : {
			exports : "",
			deps : ["jquery"]
		}
	}
});

var lay = undefined;
require(["jquery", "layer", "cookie","common","jqform"], function($, layer) {
	lay = layer;

	//登录状态下直接跳转到个人中心
	ajax_get("/member/isnulogin?"+(new Date()).getTime(), {}, undefined,
		function(response) {
			if (response.suc) {
				window.location.href = "personal.html";
			} else {
				//登录状态下直接跳转到个人中心
				ajax_get("/member/times?"+(new Date()).getTime(), {}, undefined,
					function(times) {
						if (times && times >= 3) {
							$("#login_img").click();
							$("#login_img").parent().show();
						}
					},
					function(xhr, status) {
						console.log("status-->" + status);
					}
				);
			}
		},
		function(xhr, status) {
			console.log("status-->" + status);
		}
	);

	$("body").on("click","#findBackPwd",function(){
		findBackPwd();
	});

	function findBackPwd(){
		$.ajax({
			url : "/member/clientid?" + new Date(),
			type : "GET",
			data : {},
			async:false,
			success : function(data) {
			}
		});
		layer.confirm('请选择的账号类型?',
			{
				icon: 6,
				title:'找回密码',
				btn:['手机用户','邮箱用户'],
				yes:function(index, layero){
					layer.close(index);
					window.location.href = "/personal/find_password.html?flag=true";
				},
				btn2:function(index,layero){
					layer.close(index);
					window.location.href = "/personal/find_password.html";
				}
			}
		);
	}

	//手机注册tab
	$(".nav-login li").click(function () {
		$(this).addClass("show").siblings().removeClass();
		$(".nav-login-content > div").hide().eq($(".nav-login li").index(this)).show();
		$("#phone_captcha_img").click();
	});

	//判断显示登录或注册
	var url = window.location.search;
	if (url.indexOf("sign=register") > 0) {
		toRegister();
		$("#reg-form").find("input[name='email']").focus();
	} else {
		$("#login-form").find("input[name='email']").focus();
	}

	//回车(Enter)触发登录或注册表单提交
	$("body").keydown(function(event) {
		var key = event.keyCode || event.which;
		if (key == 13) {
			var layer_btn = $(".layui-layer-btn")[0];
			if (layer_btn == undefined) {
				var visible = $("#reggit-content").css("display");
				if (visible == 'none') {
					$("#login-btn").click();
				} else {
					var flag = $("#first_step").css("display");
					if (flag == 'block') {
						$("#next_btn").click();
					} else {
						$("#reg-btn").click();
					}
				}
			} else {
				$(".layui-layer-btn0").click();
			}
		}
	});

	$("#reg-form").find("input[name='email']").blur(function(){
		var EM_RE = /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
		var email = $("#reg-form").find("input[name='email']").val();
		if (!(EM_RE.test(email))) {
			layer.msg('邮箱为空或格式错误，请输入正确的邮箱', {icon : 5, time : 2000});
			fix_layer_css();
			return;
		}
		if(checkEmail(email)){
			layer.msg('邮箱已存在，请重新输入邮箱', {icon : 5, time : 2000});
			fix_layer_css();
			return;
		}else{
			layer.msg('邮箱格式正确，请务必用有效邮箱注册，以便账号激活，感谢合作', {icon : 6, time : 3000});
			fix_layer_css();
		}
	});

	$("#reg-form").find("input[name='telNo']").blur(function(){
		var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/;
		var telNo = $("#reg-form").find("input[name='telNo']").val();
		if (!(CP_RE.test(telNo))) {
			layer.msg('手机号码为空或格式错误，请输入正确的手机号码', {icon : 5, time : 2000});
			fix_layer_css();
			return;
		}
		if(checkEmail(telNo)){
			layer.msg('该手机号码已存在，请重新输入', {icon : 5, time : 2000});
			fix_layer_css();
			return;
		}else{
			layer.msg('手机号码格式正确，请务必用有效手机号码，以便账号激活，感谢合作', {icon : 6, time : 3000});
			fix_layer_css();
		}
	});

	$(".user_protalcol input[type=checkbox]").click(function(){
		if($(this).prop("checked")){
			$("#tel_next_btn").attr('disabled',false).removeClass('disabled-btn');
			$("#tel_next_btn").css('background',"#ff6d34");
		}else{
			$("#tel_next_btn").attr('disabled',true).addClass('disabled-btn');
			$("#tel_next_btn").css('background',"#eee");
		}
	});

	//下一步
	$("#tel_next_btn").click(function(){
		//TODO 验证 短信验证码 与验证码的正确性，如果正确 显示密码输入框 再进行注册
		if(!$("#user_protalcol").is(':checked')) {
			return;
		}
		$(this).attr('disabled',true).addClass('disabled-btn');
		var email = $("#reg-form").find("input[name='telNo']").val();
		var captcha = $("#reg-form").find("input[name='telCAPTCHA']").val();
		var smsc = $("#reg-form").find("input[name='smsc']").val();
		var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/;
		if(!CP_RE.test(email)){
			layer.msg('手机号码为空或格式错误，请输入正确的手机号码', {icon : 5, time : 2000});
			fix_layer_css();
			recoverBtn2();
			return;
		}
		if(!smsc){
			layer.msg('手机验证码不能为空', {icon : 5, time : 2000});
			fix_layer_css();
			recoverBtn2();
			return;
		}

		if(!captcha){
			layer.msg('请输入验证码', {icon : 5, time : 2000});
			fix_layer_css();
			recoverBtn2();
			return;
		}
		var param = {
			email:email,
			captcha:captcha,
			smsc:smsc
		};
		go_to_next(param);
	});

	function go_to_next(param){
		ajax_post("/member/checktelRegister", JSON.stringify(param), "application/json",
			function(response) {
				if(response.suc){
					layer.msg(response.msg,{icon:6,time:2000},function(){toNormalDistribution()});
				}else{
					recoverBtn2();
					layer.msg(response.msg,{icon:5,time:2000});
				}
			},
			function(XMLHttpRequest, textStatus) {
				recoverBtn2();
				layer.msg("验证手机号码异常！",{icon:5,time:2000});
			}
		);
		//toNormalDistribution();
	}

	//手机注册账号
	$("#phone_next_btn").click(function(){
		//短信验证码 by huchuyin 2016-10-11
		var smsc = $("#reg-form").find("input[name='smsc']").val();
		var email = $("#reg-form").find("input[name='telNo']").val();
		var registerInviteCode = $.trim($("#normal-form").find("input[name='registerCode']").val());//注册邀请码
		var passWord = $("#normal-form").find("input[name='passWord2']").val();
		var affirmPW = $("#normal-form").find("input[name='affirmPW2']").val();
		var lengthCheckResult = registerInviteCode.length == 6 || registerInviteCode.length == 8;
		// var PW_RE = /(?!^[0-9]+$)(?!^[a-zA-Z]+$)(?!^[^0-9a-zA-Z]+$)^.{6,20}$/;
		if(!registerInviteCode || !lengthCheckResult){
			layer.msg("请输入含有字母数字的6位或8位字符串作为邀请码", {icon: 5, time: 1000});
			fix_layer_css();
			recoverBtn3();
			return;
		}
		//修改原有校验，增强密码复杂度 by huchuyin 2016-10-6
		//if (!(PW_RE.test(passWord))) {
		//    layer.msg('密码必须为6-20个字符，且至少包含数字、字母(区分大小写)等两种或以上字符', {icon : 5, time : 2000});
		//    fix_layer_css();
		//	recoverBtn3();
		//    return;
		//}
		if((passWord.search(/[0-9]/) == -1)
			|| (passWord.search(/[A-Z]/) == -1)
			|| (passWord.search(/[a-z]/) == -1)
			|| (passWord.length < 6) || (passWord.length > 20)) {
			layer.msg('密码必须为6-20个字符，且至少包含数字、大写、小写字母等三种或以上字符！', {icon: 5, time: 2000});
			fix_layer_css();
			recoverBtn3();
			return;
		}
		//End by huchuyin 2016-10-6
		if (affirmPW != passWord) {
			layer.msg('确认密码与登录密码不匹配，请重新输入', {icon : 5, time : 2000});
			fix_layer_css();
			recoverBtn3();
			return;
		}
		var param = {
			email:email,
			passWord:passWord,
			smsc:smsc,
			registerInviteCode:registerInviteCode
		};
		phone_regiter(param);
	});

	//注册账号
	function phone_regiter(param){
		ajax_post("/member/telRegister", JSON.stringify(param), "application/json", function(response) {
				if (response.errorCode == 0) {
					layer.msg("注册账号成功，自动登录中.....", {icon : 6,time:2000},function(index){
						layer.close(index);
						window.location.href = "/personal/personal.html";
					});
				} else {
					layer.msg(response.errorInfo, {icon : 5}, function(index){ layer.close(index); recoverBtn3();});
					fix_layer_css();
				}
			},
			function(XMLHttpRequest, textStatus) {
				recoverBtn2();
				layer.msg("验证手机号码异常！",{icon:5,time:2000});
			}
		);

	}

	//注册下一步切换验证
	$("#next_btn").click(function() {
		$(this).attr('disabled',true).addClass('disabled-btn');

		var email = $("#reg-form").find("input[name='email']").val();
		var tel = $("#reg-form").find("input[name='tel']").val();
		var passWord = $("#reg-form").find("input[name='passWord']").val();
		var affirmPW = $("#reg-form").find("input[name='affirmPW']").val();
		var CAPTCHA = $("#reg-form").find("input[name='CAPTCHA']").val();
		var registerInviteCode = $("#reg-form").find("input[name='registerInviteCode']").val();
		var EM_RE = /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
		var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/;
		// var PW_RE = /(?!^[0-9]+$)(?!^[a-zA-Z]+$)(?!^[^0-9a-zA-Z]+$)^.{6,20}$/;
		if (!(EM_RE.test(email))) {
			layer.msg('邮箱为空或格式错误，请输入正确的邮箱', {icon : 5, time : 2000});
			fix_layer_css();
			recoverBtn();
			return;
		}
		if(checkEmail(email)){
			layer.msg('邮箱已存在，请重新输入邮箱', {icon : 5, time : 2000});
			fix_layer_css();
			recoverBtn();
			return;
		}else{
			layer.msg('邮箱格式正确，请务必用有效邮箱注册，以便账号激活，感谢合作', {icon : 6, time : 2000});
			fix_layer_css();
			recoverBtn();
		}
		if (!(CP_RE.test(tel))) {
			layer.msg('手机号为空或格式有误，请重新输入', {icon : 5, time : 2000});
			fix_layer_css();
			recoverBtn();
			return;
		}
		//修改原有校验，增强密码复杂度 by huchuyin 2016-10-6
		//if (!(PW_RE.test(passWord))) {
		//    layer.msg('密码必须为6-20个字符，且至少包含数字、字母(区分大小写)等两种或以上字符', {icon : 5, time : 2000});
		//   fix_layer_css();
		//	recoverBtn();
		//    return;
		//}
		if((passWord.search(/[0-9]/) == -1)
			|| (passWord.search(/[A-Z]/) == -1)
			|| (passWord.search(/[a-z]/) == -1)
			|| (passWord.length < 6) || (passWord.length > 20)) {
			layer.msg('密码必须为6-20个字符，且至少包含数字、大写、小写字母等三种或以上字符！', {icon: 5, time: 2000});
			return;
		}
		//End by huchuyin 2016-10-6
		if (affirmPW != passWord) {
			layer.msg('确认密码与登录密码不匹配，请重新输入', {icon : 5, time : 2000});
			fix_layer_css();
			recoverBtn();
			return;
		}
		if (CAPTCHA == undefined || CAPTCHA == '') {
			layer.msg('请输入验证码', {icon : 5, time : 2000});
			fix_layer_css();
			recoverBtn();
			return;
		}

		var lengthCheckResult = registerInviteCode.length == 6
		//校验邀请码 只能是6位含有字母数字的字符串
		if(!registerInviteCode || !lengthCheckResult){
			layer.msg("请输入含有字母数字的6位字符串作为邀请码", {icon: 5, time: 1000});
			return;
		}
		var url = "/member/checkcaptcha";
		$.ajax(url, {captcha: CAPTCHA}, function(data) {
			if (data == "true") {
				//注册流程变动，都云涛修改为以下内容
				$.ajax({
					url : "/member/register",
					type : "POST",
					data : {
						email : email,
						passWord : passWord,
						registerInviteCode:registerInviteCode,
						tel:tel
					},
					dataType : "json",
					success : function(data) {
						if (data.errorCode == 0) {
							window.location.href = "/personal/email_actived.html?e=" + data.email +"&code="+data.code;
						} else {
							layer.msg(data.errorInfo, {icon : 5,time:2000}, function(index){ layer.close(index); recoverBtn();});
							fix_layer_css();
						}
					},
					error : function(XMLHttpRequest) {
						layer.msg('注册提交出错，请稍候重试', {icon : 5,time:2000}, function(index){ layer.close(index); recoverBtn();});
						fix_layer_css();
					}
				});
			} else {
				layer.msg("验证码输入错误，请重新输入", {icon : 5,time:2000}, function(index){ layer.close(index); recoverBtn();});
				fix_layer_css();
			}
		}, 'text');
	});

	function recoverBtn(){
		setTimeout(function(){$('#next_btn').attr('disabled',false).removeClass('disabled-btn');},2000)
	}

	function recoverBtn2(){
		setTimeout(function(){$('#tel_next_btn').attr('disabled',false).removeClass('disabled-btn');},2000)
	}

	function recoverBtn3(){
		setTimeout(function(){$('#phone_next_btn').attr('disabled',false).removeClass('disabled-btn');},2000)
	}

	//如果cookie中有email，passWord那就写入对应输入框
	$(document).ready(function () {
		$("#tel_next_btn").css('background',"#eee");
		var email = $.cookie("email");
		var passWord = $.cookie("passWord");
		if (email && passWord) {
			$("#login-form").find("input[name='email']").val(email);
			$("#login-form").find("input[name='passWord']").val(passWord);
		}
	});

	//登录校验提交
	$("#login-btn").click(function(event) {
		var flag = $.cookie("email") ? false : true;//判断email，passWord是否已经存入cookie，如果存在就没必要再存一次
		var email = $("#login-form").find("input[name='email']").val();
		var passWord = $("#login-form").find("input[name='passWord']").val();
		var captcha = $("input[name=login_captcha]").val();
		//var EM_RE = /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
		//var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/;
		if (!email) {
			layer.msg('请输入正确的账号', {icon : 5, time : 2000});
			fix_layer_css();
			return;
		}
		if (!(/^.+$/.test(passWord))) {
			layer.msg('请输入密码', {icon : 5, time : 2000});
			fix_layer_css();
			return;
		}
		$.ajax({
			url : "/member/login",
			type : "POST",
			data : JSON.stringify({email : email, passWord : passWord,captcha:captcha}),
			dataType : "json",
			contentType: "application/json",
			success : function(data) {
				if (data.errorCode == 0) {
					window.location.href = "/personal/personal.html";
					//如果选了记住密码，就把email和passWord存入cookie
					if (flag) {//说明cookie没有存入email,passWord,那就存
						if($(".remenber-password").prop("checked")) {
							var date = new Date();
							date.setTime(date.getTime() + 2*30*24*60*60*1000);
							$.cookie("email", email, { expires: date });
							$.cookie("passWord", passWord);
							console.log("a");
						}
					}
				} else if(data.errorCode == 4) {//
					toReggistAudit();
					showAuditDetail(JSON.parse(data.data));
				} else {
					layer.msg(data.errorInfo, {icon : 5,time:2000}, function(index){ layer.close(index); });
					fix_layer_css();
					if(data.times) {
						$("#login_img").click();
						$("#login_img").parent().show();
					}
				}
			},
			error : function(XMLHttpRequest) {
				layer.msg('登录提交出错，请稍候重试', {icon : 5,time:2000}, function(index){ layer.close(index); });
				fix_layer_css();
			}
		});
	});

	//回车(Enter)触发登录表单提交
	$("body").keyup(function(event) {
		var key = event.keyCode || event.which;
		if (key == 13) {
			$("#login-btn").click();
		}
	});

	//展示注册申请详情
	function showAuditDetail(data){
		var currAudit = data[0];//最近一次的申请
		$("#reggist-audit .statusDesc").text(currAudit.statusDesc);
		if (currAudit.status == 1) {
			$("#reggist-audit .audit-reject").show();
			$("#reggist-audit .wait-audit").hide();
			$("#reggist-audit .remind-message").text("您的注册申请（成为经销商）状态为审核不通过");
			$()
		} else {
			$("#reggist-audit .audit-reject").hide();
			$("#reggist-audit .wait-audit").show().attr("sid",currAudit.id);
			$("#reggist-audit .remind-message").text("您的注册申请（成为经销商），已经成功提交，请耐心等待审核");
		}
		$("#reggist-audit .apply-date").text(currAudit.createDateDesc);//申请时间
		var html = "";
		$("#reggist-audit .apply-files").empty("a");
		$.each(currAudit.files,function(i,item){//当前申请详情
			if (item.type == "goods-licence") {
				html += '<a href=../member/watchAuidtFile?id='+item.id+' class="btn-blue-linear">'+item.typeDesc+'</a>';
			} else {
				html += '<a class="btn-blue-linear" id="'+item.id+'"  onclick="previewFile(this)">'+item.typeDesc+'</a>'
			}
		});
		$("#reggist-audit .apply-files").append(html);
		var history = "";
		var historyFile = "";
		$("#reggist-audit .apply-history").empty("a");
		$.each(data,function(i,item){//申请历史记录
			if (i != 0) {
				historyFile = "";
				$.each(item.files,function(j,ktem){
					if (ktem.type == "goods-licence") {
						historyFile += '<a href=../member/watchAuidtFile?id='+ktem.id+' class="btn-blue-linear">'+ktem.typeDesc+'</a>';
					} else {
						historyFile += '<a class="btn-blue-linear" id="'+ktem.id+'"  onclick="previewFile(this)">'+ktem.typeDesc+'</a>'
					}
				});
				history += '<p class="audit-accessory">'+ '附件(点击查看)：<br />'+
							historyFile+
							'</p>'+
							'<p class="audit-accessory">申请时间：<span>'+item.createDateDesc+'</span>'+
							'</p>'+
							'<p class="audit-accessory">审核结果：<span class="red">'+item.statusDesc+'</span>'+
							'</p>'+
							'<p class="audit-accessory">审核理由：<span>'+item.auditReason+'</span>'+
							'</p>'+
							'<p class="audit-accessory">'+
							'    备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注：<span>'+deal_with_illegal_value(item.auditRemark)+'</span>'+
							'</p>';
			}
		});
		$("#reggist-audit .apply-history").append(history);
	}

	//点击‘成为普通分销商按钮'
	$("#reggist-audit").on("click",".becomeNormol", function(){
		var $obj = $(this);
		layer.open({
			type: 1,
			title: "提示",
			content: "<div style='padding:5px;text-align:center'>您确定要变更注册类型吗？</div>",
			area: ['350px', '145px', '558.5px'],
			btn: ["确定", "取消"],
			closeBtn: 1,
			shadeClose: false,
			//i和currdom分别为当前层索引、当前层DOM对象
			yes: function (i, currdom) {
				var id = $obj.attr("sid");
				ajax_get("../member/becomeOrdinaryUser?id="+id, undefined, undefined,
					function (response) {
						response = JSON.parse(response);
						if (response.suc) {
							layer.msg("注册账号成功，正跳转至登录页.....", {icon : 6,time:2000},function(index){
								layer.close(index);
								window.location.href = "/personal/personal.html";
							});
						} else {
							layer.msg(response.msg, {icon : 5}, function(index){ layer.close(index); recoverBtn3();});
						}
					},
					function (xhr, status) {
					}
				);
			}
		});
	});

	//注册校验提交
	$("#reg-btn").click(function(event) {
		$(this).attr('disabled');
		var regForm = $("#reg-form");
		var email = regForm.find("input[name='email']").val();
		var passWord = regForm.find("input[name='passWord']").val();
		var shopType = regForm.find("select[name='shopType']").val();
		var shopName = regForm.find("input[name='shopName']").val();
		var tel = regForm.find("input[name='tel']").val();
		//手机验证码验证
		// var telCaptcha = "";
		//获取验证码    $("#reg-form").find("input[name='message']").val();
		var SH_RE = /^[A-Za-z0-9\u4E00-\u9FFF\s_-]+$/;
		var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/;
		// var CAP_RE = /^[0-9]{4}$/;
		if (shopType == undefined || shopType == '') {
			layer.msg('请选择您的店铺类型', {icon : 5, time : 2000});
			fix_layer_css();
			return;
		}
		if (!(SH_RE.test(shopName))) {
			layer.msg('请输入您的店铺名称', {icon : 5, time : 2000});
			fix_layer_css();
			return;
		}
		if (!(CP_RE.test(tel))) {
			layer.msg('手机号为空或格式有误，请重新输入', {icon : 5, time : 2000});
			fix_layer_css();
			return;
		}
		//手机验证码验证
		/*if(!CAP_RE.test(telCaptcha) || checkTelCaptcha(telCaptcha)){
		 layer.msg('验证码错误或为空，请重新输入', {icon : 5, time : 2000});
		 fix_layer_css();
		 return;
		 }*/
		$.ajax({
			url : "/member/register",
			type : "POST",
			data : {
				email : email,
				passWord : passWord,
				shopType : shopType,
				shopName : shopName,
				tel : tel
			},
			dataType : "json",
			success : function(data) {
				if (data.errorCode == 0) {
					window.location.href = "/personal/email_actived.html?e=" + data.email+"&code="+data.code;
				} else {
					layer.msg(data.errorInfo, {icon : 5}, function(index){ layer.close(index); });
					fix_layer_css();
				}
			},
			error : function(XMLHttpRequest) {
				layer.msg('注册提交出错，请稍候重试', {icon : 5,time:2000}, function(index){ layer.close(index); });
				fix_layer_css();
			}
		});
	});

	$(".select-lience").click(function(){//三证合一单选框
		if ($(this).prop("checked")) {
			$(".common-file").hide();
		} else {
			$(".common-file").show();
		}
	});

    $(".certificates input[type='file']").change(function(e){
    	var file = $(this)[0].files[0];
    	var view = $(this).parent().siblings("#preview");
    	var fileName = "";
    	if(file){
    		fileName = file.name.length>6?file.name.substr(0,6)+"...":file.name;
    	}
		view.text(fileName);
	});

	//注册成为经销商
	$("#register-apply").click(function(){
		//短信验证码
		var smsc = $("#reg-form").find("input[name='smsc']").val();
		var registerInviteCode = $.trim($("#distributor2-form").find("input[name='invitationCode']").val());//注册邀请码
		var passWord = $("#distributor2-form").find("input[name='password']").val();
		var affirmPW = $("#distributor2-form").find("input[name='againPassword']").val();
		var vdtUserCode = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6}$/
		if(!registerInviteCode || !vdtUserCode.test(registerInviteCode)){
			layer.msg("请输入含有字母数字的6位字符串作为邀请码", {icon: 5, time: 1000});
			fix_layer_css();
			recoverBtn3();
			return;
		}
		if((passWord.search(/[0-9]/) == -1)
			|| (passWord.search(/[A-Z]/) == -1)
			|| (passWord.search(/[a-z]/) == -1)
			|| (passWord.length < 6) || (passWord.length > 20)) {
			layer.msg('密码必须为6-20个字符，且至少包含数字、大写、小写字母等三种或以上字符！', {icon: 5, time: 2000});
			fix_layer_css();
			recoverBtn3();
			return;
		}
		if (affirmPW != passWord) {
			layer.msg('确认密码与登录密码不匹配，请重新输入', {icon : 5, time : 2000});
			fix_layer_css();
			recoverBtn3();
			return;
		}
		var $businessLicence = $("#distributor2-form").find("input[name='business-licence']");
		if ($businessLicence != undefined && $businessLicence.val() != "") {
			var name = $businessLicence.val();
			if ($businessLicence[0].files[0].size > (2 * 1024 * 1024) ||
				!(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
				layer.msg("营业执照只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
				return false;
			}
		} else {
			layer.msg('请上传营业执照！', {icon : 5, time : 2000});
			return;
		}
		if (!$("#distributor2-form .select-lience").prop("checked")){//如果没有勾选了三证合一
			var $organizationCode = $("#distributor2-form").find("input[name='organization-code']");
			if ($organizationCode != undefined && $organizationCode.val() != "") {
				var name = $organizationCode.val();
				if ($organizationCode[0].files[0].size > (2 * 1024 * 1024) ||
					!(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
					layer.msg("组织机构代码只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
					return;
				}
			} else {
				layer.msg("请上传组织机构代码", {icon: 0, time: 2000});
				return;
			}
			var $taxLicence = $("#distributor2-form").find("input[name='tax-licence']");
			if ($taxLicence != undefined && $taxLicence.val() != "") {
				var name = $taxLicence.val();
				if ($taxLicence[0].files[0].size > (2 * 1024 * 1024) ||
					!(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
					layer.msg("税务登记证只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
					return;
				}
			} else {
				layer.msg("请上传税务登记证", {icon: 0, time: 2000});
				return;
			}
		}
		var $taxpayerLicence = $("#distributor2-form").find("input[name='taxpayer-licence']");
		if ($taxpayerLicence != undefined && $taxpayerLicence.val() != "") {
			var name = $taxpayerLicence.val();
			if ($taxpayerLicence[0].files[0].size > (2 * 1024 * 1024) ||
				!(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
				layer.msg("一般纳税人资格证只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
				return;
			}
		} else {
			layer.msg("请上传一般纳税人资格证", {icon: 0, time: 2000});
			return;
		}
		var $foodLicence = $("#distributor2-form").find("input[name='food-licence']");
		if ($foodLicence != undefined && $foodLicence.val() != "") {
			var name = $foodLicence.val();
			if ($foodLicence[0].files[0].size > (2 * 1024 * 1024) ||
				!(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
				layer.msg("食品流通许可证只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
				return;
			}
		}
		var $goodsLicence = $("#distributor2-form").find("input[name='goods-licence']");
		if ($goodsLicence != undefined && $goodsLicence.val() != "") {
			var name = $goodsLicence.val();
			if ($goodsLicence[0].files[0].size > (2 * 1024 * 1024) ||
				!(name.indexOf(".doc") != -1 || name.indexOf(".docx") != -1
				|| name.indexOf(".dot") != -1 || name.indexOf(".dotx") != -1 || name.indexOf(".docm") != -1)) {
				layer.msg("收货授权书只支持doc、docx、dot、dotx、docm五种格式，且大小不能大于2MB", {icon: 0, time: 2000});
				return;
			}
		} else {
			layer.msg("请上传收货授权书", {icon: 0, time: 2000});
			return;
		}
		var remark = $("#distributor2-form .res-taxtarea").val().trim();
		if (remark && remark.length > 500) {
			layer.msg('备注不能超过500字！', {icon : 5, time : 2000});
			return;
		}
		var options = {
			//url: url,                 //默认是form的action
			//type: type,               //默认是form的method（get or post）
			//dataType: json,           //html(默认), xml, script, json...接受服务端返回的类型
			//clearForm: true,          //成功提交后，清除所有表单元素的值
			//resetForm: true,          //成功提交后，重置所有表单元素的值
			//target: '#output',          //把服务器返回的内容放入id为output的元素中
			//timeout: 3000,               //限制请求的时间，当请求大于3秒后，跳出请求
			//提交前的回调函数
			beforeSubmit: function(formData, jqForm, options){
				//formData: 数组对象，提交表单时，Form插件会以Ajax方式自动提交这些数据，格式如：[{name:user,value:val },{name:pwd,value:pwd}]
				//jqForm:   jQuery对象，封装了表单的元素
				//options:  options对象
				//比如可以再表单提交前进行表单验证

				var fileValue = undefined;
				if ($(".select-lience").prop("checked")) {//选中了三合一，组织机构代码、税务登记证和营业执照一样
					for (var i = 0;i < formData.length;i++) {
						if (formData[i].name == "business-licence") {
							fileValue = formData[i].value;
						}
						if (formData[i].name == "organization-code" || formData[i].name == "tax-licence") {
							formData[i].value = fileValue;
						}
					}
					formData.push({name:"isCombine",value:true});
				} else {
					formData.push({name:"isCombine",value:false});
				}
				var regiserTel = $("#reg-form").find("input[name='telNo']").val().trim();//注册填写的手机号
				var loginTel = $("#login-form").find("input[name='email']").val().trim();//登录时填写的手机号
				if (regiserTel) {
					formData[0].value = regiserTel;
				} else {
					formData[0].value = loginTel;
				}
				formData.push({name:"smsc",value:smsc});
			},
			//提交成功后的回调函数
			success: function(data,status,xhr,$form){
				data = JSON.parse(data);
				if (data.suc) {
					layer.msg("您的申请已成功提交，可登录查看详情,正跳转至登录页...", {icon : 6, time : 4000},function(){
						window.location.href = "login.html";
					});
				} else {
					this.clearForm = false;
					layer.msg(data.msg, {icon : 6, time : 2000});
				}
			},
			error: function(xhr, status, error, $form){},
			complete: function(xhr, status, $form){}
		};
		$("#distributor2-form").ajaxSubmit(options);
	});

	$(".privew-picture").click(function(){
		var file = $(this).prev().find("input[type='file']")[0].files[0];
		var reader = new FileReader();
		reader.readAsDataURL(file);
		var result = reader.result;
		lay.open({
			type: 1,
			title: false,
			area: ['660px', 'auto'],
			content: '<div class="banner_add_pop" id="addBanner" style="display: block;height: 440px;text-align: center;"><img style="height: 100%" src="'+result+'"></div>'
		});
		$(".layui-layer-content").css("padding","10px")
	});

	$("#distribution-cont").on("click","#preview",function(){
		var file = $(this).parent().find("input[type=file]")[0].files[0];
		var reader = new FileReader();
		reader.onerror = function (e) {
	    }
	    reader.onload = function (e) {
			layer.open({
				type: 1,
				title: false,
				area: ['660px', 'auto'],
				content: '<div class="banner_add_pop" id="addBanner" style="display: block;height: 440px;text-align: center;"><img style="height: 100%" src="'+reader.result+'"></div>'
			});
			$(".layui-layer-content").css("padding","10px")
	    };
	    if(file){
	    	reader.readAsDataURL(file);
	    }
	});
});

//修复layer弹出框位置偏下问题
var fix_layer_css = function() {
	$($(".layui-layer")[0]).css({"top" : (parseInt($($(".layui-layer")[0]).offset().top)/2)});
};

//邮箱校验
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

/*登录与注册切换--zuoting-start*/
function toLogin(){
	$('#login-content').show();
	$('#loginIndex').show();
	$('#register-cont').hide();
	$("#normal-distribution-cont").hide();
	$('#distribution-cont').hide();
}

function toRegister(){
	$("#phone_captcha_img").click();
	$('#login-content').hide();
	$('#register-cont').show();
	$.ajax({
		url : "/member/clientid?" + new Date(),
		type : "GET",
		data : {},
		async:false,
		success : function(data) {
		}
	});
}

//下一步
function toNormalDistribution(){
	$("#register-cont").hide();
	$('#normal-distribution-cont').show();
}

//分销商切换
function toDistribution(){
	$("#loginIndex").hide();
	$('#reggist-audit').hide();
	$('#loginIndex').hide();
	$('#distribution-cont').show();
	$('.distributor2').css('left','0');
	$('.distributor1').css('left','150px');
}

function toNormalDistribution2(){
	$("#loginIndex").show();
	$('#register-cont').hide();
	$('#distribution-cont').hide();
	$('.distributor2').css('left','150px');
	$('.distributor1').css('left','0');
}

function toReggistAudit(){
	$("#loginIndex").hide();
	$('#distribution-cont').hide();
	$('#reggist-audit').show();
}

/*登录与注册切换--zuoting-end*/

//发送短信
function getTelCaptcha(telphone,event){
	//参数新增发送短信类型 by huchuyin 2016-10-11
	var param = {tel : telphone,types:3};
	ajax_post("/member/message", JSON.stringify(param), "application/json",
		function (data) {
			if(!data) {
				lay.alert('当前页面已失效，请刷新页面。', function(index){
					lay.close(index);
					location.reload();
				});
				return;
			}
			if (data.suc) {
				//60秒倒计时
				toggle_sendmsg_btntext(event);
				lay.msg(data.msg,{icon : 6, time : 2000});
			}else{
				//发送短信超过次数或错误 by huchuyin 2016-10-11
				lay.msg(data.msg,{icon : 5, time : 2000});
			}
		},
		function (xhr, status) {
			lay.msg('短信发送失败！', {icon : 5, time : 2000});
		}
	);
}

function sendMsg(event) {
	var telNo = $("#reg-form").find("input[name='telNo']").val();
	var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/;
	if (!CP_RE.test(telNo)) {
		lay.msg("手机号为空或格式有误，请重新输入", {icon: 5, time: 2000});
		return;
	}
	if(checkEmail(telNo)) {
		lay.msg('该手机号码已存在，请重新输入', {icon : 5, time : 2000});
		fix_layer_css();
		return;
	}
	$(event).css({"cursor": "not-allowed", "color": "white"});
	getTelCaptcha(telNo,event);
}

//预览文件
function previewFile(obj){
	var fileId = $(obj).attr("id");
	var random = "&abc"+new Date().getTime()+"="+new Date().getTime();
	lay.open({
		type: 1,
		title: false,
		area: ['660px', 'auto'],
		content: '<div class="banner_add_pop" id="addBanner" style="display: block;height: 440px;text-align: center;"><img style="height: 100%" src="../member/watchAuidtFile?id='+fileId+random+'"></div>'
	});
	$(".layui-layer-content").css("padding","10px")
}