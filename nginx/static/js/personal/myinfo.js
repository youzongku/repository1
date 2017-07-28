define("myinfo", ["jquery", "layer"], function($, layer) {
	//初始化个人资料
	function init_information() {
		ajax_get("/member/infor?" + Math.random(), {}, undefined,
			function(response) {
				if (response.id != undefined) {
					var email = response.email;
					var telFlag= checkTel(email);
					if(telFlag){
						$("#change-login-pwd1").attr("href","/personal/find_password.html?flag=true");
						$("#change-login-pwd2").attr("href","/personal/find_password.html?flag=true");
					}
					//$("#myinfo_table").find("tr").eq(0).find("img").eq(0).attr("src", response.headImg);
					$("#myinfo_table").find("tr").eq(0).children().eq(1).text(deal_with_illegal2NotSet(response.nickName));
					$("#myinfo_table").find("tr").eq(1).children().eq(1).text(deal_with_illegal2NotSet(response.realName));
					$("#myinfo_table").find("tr").eq(2).children().eq(1).text(
						response.gender == "0" ? "保密" : (response.gender == "1" ? "男" : (response.gender == "2" ? "女" : "未设置"))
					);
					$("#myinfo_table").find("tr").eq(3).children().eq(1).text(deal_with_illegal2NotSet(response.birthday));
					$("#myinfo_table").find("tr").eq(4).children().eq(1).text(deal_with_illegal2NotSet(response.email));
					$("#myinfo_table").find("tr").eq(5).children().eq(1).text(deal_with_illegal2NotSet(response.tel));
					$("#myinfo_table").find("tr").eq(6).children().eq(1).text(deal_with_illegal2NotSet(response.registerInviteCode));
					$("#myinfo_table").find("tr").eq(7).children().eq(1).text(deal_with_illegal2NotSet(response.selfInviteCode));
					$("#myinfo_table").find("tr").eq(8).children().eq(1).text(deal_with_illegal2NotSet(response.profile));
				} else if (response.errorCode == "2") {
					window.location.href = "login.html";
				}
			},
			function(xhr, status) {
				setTimeout(function(){layer.msg('获取个人资料出错，请稍候重试', {icon : 2})},1000);
			}
		);
		//loading
		return creatLoading(true,theDiv);
	}

	var checkPerMinute = null;
	//查询此分销商的移动客户端信息
	function getMobileInfo(){
		isnulogin(function(email){
			$.ajax({
				url: "/member/getMobileApplyInfo",
				type: 'POST',
				data:{"disemail":email} ,
				async: false,//是否异步
				dataType: 'json',
				success: function (response) {
					if (response) {
						if (response.code == 3) {//说明没有申请
							$("#mobile_table").hide();
							$("#mine_phones .open-phone").show();

						} else if (response.code == 4){//说明已经申请了
							var mobileClient = response.data;
							$("#mobile_table").find("tr").eq(4).find("td").eq(1).html(mobileClient.applydateStr);

							$("#mobile_table").find("tr").eq(0).find(".type a").html(format$Url(mobileClient.siteurl));
							$("#mobile_table").find("tr").eq(2).find(".type").html(mobileClient.mastersite);
							if (mobileClient.status == 1){//待审核(审核中)
								$("#mine_phones .open-phone").hide();
								$("#mobile_table").show();
								$("#mobile_table").find("tr").eq(3).find("td").eq(2).hide();
								$("#mobile_table").find("tr").eq(3).find("td").eq(1).hide();
								$("#mobile_table").find("tr").eq(3).find("td").eq(3).show();
							} else if (mobileClient.status == 2){//审核通过
								$("#mine_phones .open-phone").hide();
								$(".app-create").show();
								$.ajax({
									url: "/member/getApplyAccount",
									type: 'GET',
									data: 'account=' + email,
									success: function (data) {
										if(data.id){//申请过APP
											if(data.isSuccess){//打包完毕，提示可以下载
												$(".generate-project").hide();
												$('.generate-install').hide();
												$('.generate-finish').show();
												$('.app-name').text('您的应用[' + data.identifier + ']已经生成完毕');
												$("#code").empty();
												$("#code").qrcode({
													render: "canvas", //table方式
													width: 90, //宽度
													height: 90, //高度
													text: window.location.origin + "/member/downloadApk?account="+email //任意内容
												});
												return;
												//TODO 直接显示可以下载的APK文件
											}else{//打包为完毕，提示排队人数
												$(".generate-project").hide();
												$('.generate-install').show();
												$('.generate-finish').hide();
											}

											function temp(){checkApkIsReady(email);}
											checkPerMinute =setInterval(temp,15000);
										}else{
											//未申请过APP
											$(".generate-project").show();
										}
									}
								});

								$("#mobile_table").show();
								$("#mobile_table").find("tr").eq(3).find("td").eq(2).show();
								$("#mobile_table").find("tr").eq(3).find("td").eq(1).hide();
								$("#mobile_table").find("tr").eq(3).find("td").eq(3).hide();
								$("#mobile_table").find("tr").eq(0).find(".type a").attr("href","http://" + mobileClient.siteurl);
								//$("#mobile_table").find("tr").eq(3).find("td").eq(2).find("a").attr("href","http://" + mobileClient.siteurl);
								//审核时间
							} else {//审核不通过
								var reason = "";
								$("#mine_phones .open-phone").show();
								$("#mobile_table").show();
								$("#mobile_table").find("tr").eq(3).find("td").eq(2).hide();
								$("#mobile_table").find("tr").eq(3).find("td").eq(1).show();
								$("#mobile_table").find("tr").eq(3).find("td").eq(3).hide();
								if (mobileClient.reviewreason == 1) {
									reason = " 申请的站点域名格式不正确";
								} else if (mobileClient.reviewreason == 2) {
									reason = "申请原因不明确";
								} else {
									reason = "审核不通过原因未知";
								}
								$("#view-reason").find("span").eq(1).html("");
								$("#view-reason").find("span").eq(1).html(reason);
							}
						}
					}
				},
				error: function (XMLHttpRequest, textStatus) {
				}
			});
		});
	}

	// 查看原因-弹出框-----我的移动端
	$('.view-reason').on('click', function(){
		//layer.open({
		//    type: 1,
		//    title: false,
		//    area: ['400px', 'auto'],
		//    shadeClose: true, //点击遮罩关闭
		//    content:$('#view-reason'),
		//    scrollbar: false
		//});
		layer.msg($("#view-reason").find("span").eq(1).html(),{icon: 6, time: 2000});
	});

	//每分钟循环检查打包状态的定时任务
	function checkApkIsReady(email){
		console.log('access');
		$.ajax({
			url: "/member/getApplyNeedToRebuiltBeforeYou",
			type: 'GET',
			data: 'account=' + email,
			success: function (data) {
				if(data == 0){
					$.ajax({
						url: "/member/getApplyAccount",
						type: 'GET',
						data: 'account=' + email,
						success: function (data) {
							if(data.id){//申请过APP
								if(data.isSuccess){
									layer.closeAll();
									$('.generate-project').hide();
									$('.generate-install').hide();
									$('.generate-finish').show();
									$('.app-name').text('您的应用[' + data.identifier + ']已经生成完毕');
									$("#code").empty();
									$("#code").qrcode({
										render: "canvas", //table方式
										width: 90, //宽度
										height: 90, //高度
										text: window.location.origin + "/member/downloadApk?account="+email //任意内容
									});
									clearInterval(checkPerMinute);
									//TODO 直接显示可以下载的APK文件
								}else{
									$('.waiting-tip').text('你的申请正在处理中...');
								}
							}else{
								//未申请过APP
								$(".generate-project").show();
								$('.generate-install').hide();
								$('.generate-finish').hide();
							}
						}
					});
				}else{
					//等候打包的人数不为0，则给出提示
					$('.howmanybeforeyou').text(data.msg);
				}
			}
		});
	}

	//下载
	$("body").on("click","#downloadApk",function(){
		isnulogin(function(email) {
			$("#downloadApk").attr("href","/member/downloadApk?account=" + email);
		});
	});

	var isApplyClicked = false;
	//申请安卓APP
	$("body").on("click",".app-btn",function(){
		isnulogin(function(email) {
			layer.open({
				type: 1,
				area: ['470px', 'auto'],
				title: '申请安卓版APP',
				content: $('.app-pop'),
				btn: ['提交', '取消'],
				yes: function () {

					//点击之后不可再重复点击
					if(isApplyClicked){

						$(".layui-layer-btn0").css("background-color","#999");
						$(".layui-layer-btn0").css("border-color","#999");

						return;
					}
					isApplyClicked = true;

					//基本校验
					if($("#identifier").val() == ''){
						layer.msg("你需要填写[APP名称]",{icon: 2, time: 2000});
						$("#identifier").focus();
						isApplyClicked = false;

						$(".layui-layer-btn0").css("background-color","#2e8ded");
						$(".layui-layer-btn0").css("border-color","#2e8ded");

						return;
					}

					if($("#appicon").val() == ''){
						layer.msg("你需要上传[应用icon]",{icon: 2, time: 2000});
						$("#appicon").focus();
						isApplyClicked = false;

						$(".layui-layer-btn0").css("background-color","#2e8ded");
						$(".layui-layer-btn0").css("border-color","#2e8ded");

						return;
					}

					$("#apkapply").ajaxSubmit({success:function(data){
						if (data.success) {
							//打包排队人数为0，需要直接提示用户
							if (data.msg == 0) {
								$('.waiting-tip').text('你的申请正在处理中...');
							} else {
								//等候打包的人数不为0，则给出提示
								$('.howmanybeforeyou').text(data.msg);
							}
							function temp(){checkApkIsReady(email);}
							checkPerMinute = setInterval(temp, 15000);

							layer.closeAll();
							layer.open({
								type: 1,
								area: ['470px', 'auto'],
								title: '生成安装包',
								content: $('.package-pop'),
								btn: ['确定'],
								yes: function () {
									layer.closeAll();
									$('.generate-project').hide();
									$('.generate-install').show();

									isApplyClicked = false;
								},
								cancel:function () {
									layer.closeAll();
									$('.generate-project').hide();
									$('.generate-install').show();

									isApplyClicked = false;
								}
							});
						} else {
							//申请不成功
							layer.msg("申请出了点问题，稍后再试下",{icon: 2, time: 2000});
							isApplyClicked = false;
						}
					}});
				}
			});
		});
	});

	//icon预览
	$("body").on("change",".appicon",function(){
		preview(this,$("#preview"));
	})

	// 申请开通移动端-弹出框
	$("body").on("click",".open-phone",function(){
		var disemail = "";
		// var time = Date.prototype.format;
		isnulogin(function(email){
			disemail = email;
			//清空数据
			$("#add-phoneshop-box .apply-eamil").find("div").html("");
			$("#add-phoneshop-box .siteName").val("");
			$("#add-phoneshop-box .sitePrefix").val("");
			$("#add-phoneshop-box .apply-eamil").find("div").html(disemail);
		});
		layer.open({
			type: 1,
			title: "申请开通移动端",
			area: ['600px', '300px'],
			content: $('#add-phoneshop-box'),
			btn: ['提交', '取消'],
			success:function(){
				$("#add-phoneshop-box").on("keyup",".sitePrefix",function(){
					$(this).val($(this).val().toLowerCase());
				})
			},
			yes: function () {
				var inputParam =  verification(disemail);
				if (inputParam != undefined) {
					var mastersite = inputParam.mastersite;
					var siteurl = inputParam.siteurl + $("#add-phoneshop-box .sitePrefix").next().html();
					var params = {"disemail":disemail,
						"mastersite":mastersite,
						"siteurl":siteurl
					};
					$.ajax({
						url: "/member/applyOpenMobile",
						type: 'POST',
						data: params,
						async: false,//是否异步
						dataType: 'json',
						success: function (data) {
							if (data.suc){
								$(".layui-layer-btn0").css({"cursor":"not-allowed", "color": "white","background-color":"rgb(204, 204, 204)"});
								$(".layui-layer-btn0").attr("disabled", "disabled");
								layer.msg('您于申请站点' +siteurl+ '成功，正在审核中，请耐心等待。', {icon: 1, time: 2000},function(){
									layer.closeAll();
									getMobileInfo();
								});
							} else {
								layer.msg(data.msg, {icon: 2, time: 2000});
							}
						},
						error: function (XMLHttpRequest, textStatus) {
						}
					});
				}
			},
			btn2:function(){
				layer.closeAll();
			},
			skin: 'layui-layer-demo',
			shadeClose: true
		});
	});

	function format$Url(url) {
		var regex = /(https?:\/\/)?(\w+\.?)+(\/[a-zA-Z0-9\?%=_\-\+\/]+)?/gi;
		url = url.replace(regex, function (match, capture) {
			if (capture) {
				return match;
			}
			else {
				return 'http://' + match;
			}
		});
		return url;
	}

	//输入验证
	function verification(email){
		var flag = false;
		var mastersite = $("#add-phoneshop-box .siteName").val().trim();//站点名称
		var siteurl = $("#add-phoneshop-box .sitePrefix").val().trim();
		var check =  /^[a-zA-Z0-9]{6,10}$/;
		if (mastersite == "" || mastersite == undefined) {
			layer.msg("请输入站点名称", {icon: 2, time: 2000});
			return undefined;
		}

		if (siteurl == "" || siteurl == undefined) {
			layer.msg("请输入站点域名前缀", {icon: 2, time: 2000});
			return undefined;
		}
		if (!check.test(siteurl)) {
			layer.msg("请输入6到10位的数字或字母组成的域名", {icon: 2, time: 3000});
			return undefined;
		}
		$.ajax({
			url: "/member/getMobileApplyInfo",
			type: 'POST',
			data: {"siteurl":siteurl,"status":2},
			async: false,//是否异步
			dataType: 'json',
			success: function (data) {
				if (data){
					if (data.code == 3){//说明没有查到关于这个url的信息
						flag = true;
						return;
					} else if (data.code == 4) {//说明有相同的url信息
						layer.msg("此站点域名已经存在",{icon: 2, time: 2000});
					} else {
						layer.msg(data.msg,{icon: 2, time: 2000});
					}
				}
			},
			error: function (XMLHttpRequest, textStatus) {
			}
		});
		if (!flag) {
			return undefined;
		}
		return {
			mastersite:mastersite,
			siteurl:siteurl
		};
	}

	return {
		init_information: init_information,
		getMobileInfo: getMobileInfo
	};
});