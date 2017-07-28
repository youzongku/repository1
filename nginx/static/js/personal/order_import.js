define("orderimport", ["jquery", "layer", "webuploader","laypage","laydate"], function($, layer, webuploader,laypage){
	var taobao_uploader = undefined;
	var offline_uploader = undefined;
	var other_uploader = undefined;

	//检查账户是否被冻结
	function checkFrozen() {
		$.ajax({url: "/member/checkFrozen?" + (new Date()).getTime(), type: "GET", dataType: "json",
			async: false, contentType: "application/json", success : function(data) {
				if(data.code == 100){
					var result = data.obj.result;
					if(result.frozen){
						$(".personal").replaceWith("<div class='frozen'><p>您的账户已被冻结，请联系管理员！</p></div>");
					}else{
						$("#balance").text(result.balance);//首次访问个人中心
					}
				}
			}
		});
	}

	var started = {
		elem: '#import_start_time',
		format: 'YYYY-MM-DD hh:mm:ss',
		min: laydate.now(-30), //设定最小日期为当前日期
		max: laydate.now(-1),
		start: laydate.now(-7),
		istime: true,
		istoday: false,
		choose: function(datas){
			var stringTime = datas;
			var timestamp2 = Date.parse(new Date(stringTime));
			timestamp2 += 30*24*60*60*1000;
			var date = new Date(timestamp2);
			//结束时间最多多一个月
			end.max =date.format('yyyy-MM-dd hh:mm:ss');
			timestamp2 = Date.parse(new Date(stringTime));
			timestamp2 += 1*24*60*60*1000;
			date = new Date(timestamp2);
			//结束时间最少一天
			end.min = date.format('yyyy-MM-dd hh:mm:ss');
			timestamp2 = Date.parse(new Date(stringTime));
			timestamp2 += 7*24*60*60*1000;
			date = new Date(timestamp2);
			//默认 7天
			end.start = date.format('yyyy-MM-dd hh:mm:ss');
		}
	};

	var end = {
		elem: '#import_end_time',
		format: 'YYYY-MM-DD hh:mm:ss',
		max: laydate.now(),
		start: laydate.now(),
		istime: true,
		istoday: true,
		choose: function(datas){
			var stringTime = datas;
			//相差最多一个月
			var timestamp2 = Date.parse(new Date(stringTime)) -30*24*60*60*1000;
			var date = new Date(timestamp2);
			started.min =date.format('yyyy-MM-dd hh:mm:ss');
			//默认7天
			timestamp2 = Date.parse(new Date(stringTime)) - 7*24*60*60*1000;
			date =  new Date(timestamp2);
			started.start = date.format('yyyy-MM-dd hh:mm:ss');
			//相差最少一天
			timestamp2 = Date.parse(new Date(stringTime)) - 1*24*60*60*1000;
			date =  new Date(timestamp2);
			started.max = date.format('yyyy-MM-dd hh:mm:ss');
		}
	};

	/**
	 * 获取店铺数据数据sales
	 * @param seldId 已选店铺id
	 */
	function getShop(seldId) {
		ajax_post("/member/getstore", {currPage: 1, pageSize: 9999}, undefined,
			function (response) {
				$("#import_shop").empty();
				if (response.suc) {
					var shopList = response.page.list;
					for (var i = 0; i < shopList.length; i++) {
						var shop = shopList[i];
						//只展示京东和有赞
						if(shop.pfid == 4 ||shop.pfid == 13 || shop.pfid == 14){
							var item = $("<option platform="+shop.pfid+" value=" + shop.id + " >" + shop.name + "(" + shop.type + ")</option>");
						}
						$("#import_shop").append(item);
					}
					if($("#import_shop option:selected").attr("platform") == 14) {
						$("#sync_date").hide();
					} else {
						$("#sync_date").show();
					}
				} else if (response.code == "2") {
					window.location.href = "login.html";
				} else {
					layer.msg(response.msg, {icon: 2, time: 2000});
				}
			}
		);
	}

	// 选择店铺
	$("#import_shop").change(function(){
		if($("#import_shop option:selected").attr("platform") == 14) {
			$("#sync_date").hide();
		} else {
			$("#sync_date").show();
		}
	});

	// 自动拉取 京东订单//有赞订单 生产待补全订单
	function autoPullOrder(){
		var platform = $("#import_shop option:selected").attr("platform");
		var shopId = $("#import_shop").val();
		var import_start_time = $("#import_start_time").val();
		var import_end_time = $("#import_end_time").val();
		if(!shopId ){
			layer.msg("没有可选中的店铺",{icon:2,time:2000});
			return;
		}
		if(!import_start_time || !import_end_time){
			layer.msg("请选择开始时间和结束时间",{icon:2,time:2000});
			return;
		}
		if (platform == 4) {//京东店铺
			pullJDOrder(shopId);
		} else if (platform == 13) {//拉取有赞订单
			pullYZOrder(shopId);
		} else if (platform == 14) {//拉取拼多多订单
			pullPddOrder(shopId);
		}
	}

	//拉取平多多订单
	function pullPddOrder(data){
		ajax_post("/sales/pdd/pull", JSON.stringify({shopId: data}), "application/json",
			function (response) {
				if(response.code){
					window.location.href = "/personal/login.html";
				}
				if (response.suc) {
					layer.msg(response.msg, {icon: 6, time: 2000});
					$(".orderinfo").click();
				} else {
					layer.msg(response.msg, {icon: 5, time: 2000});
				}
			}, function (xhr, status) {
				layer.msg("拉取有赞订单异常",{icon:5,time:2000});
			}
		);
	}

	//拉取有赞订单
	function pullYZOrder(data){
		var pullYZparams = {start_created:$("#import_start_time").val(), end_created:$("#import_end_time").val(), shopId:data}
		ajax_post("/sales/youzan/pull", JSON.stringify(pullYZparams), "application/json",
			function (response) {
				if (response.code) {
					window.location.href = "/personal/login.html";
				}
				if (response.suc) {
					layer.msg(response.msg, {icon: 6, time: 2000});
					$(".orderinfo").click();
				} else {
					layer.msg(response.msg, {icon: 5, time: 2000});
				}
			}, function (xhr, status) {
				layer.msg("拉取有赞订单异常", {icon: 5, time: 2000});
			}
		);
	}

	//拉取京东订单
	function pullJDOrder(data){
		var pullParams = {start_date:$("#import_start_time").val(), end_date:$("#import_end_time").val(), shopId:data.id}
		ajax_post("/sales/jd/pull", JSON.stringify(pullParams), "application/json",
			function (response) {
				if(response.code){
					window.location.href = "/personal/login.html";
				}
				if (response.suc) {
					layer.msg(response.msg, {icon: 6, time: 2000});
					$(".orderinfo").click();
				} else {
					layer.msg(response.msg, {icon: 5, time: 2000});
				}
			}, function (xhr, status) {
				layer.msg("拉取京东订单异常",{icon:5,time:2000});
			}
		);
	}

	function init_orderimport(){
		init_webuploader_register();
		isnulogin(function(email){
			myOrder();
			//淘宝订单上传初始化
			init_taobaoOrder_uploader(email);
			//线下订单上传初始化
			init_offlineOrder_uploader(email);
			//初始化其他上传订单
			init_otherOrder_uploader(email);

			getShop();
			//默认七天
			$(".order-import-content").find("#import_start_time").val(laydate.now(-7,'YYYY-MM-DD hh:mm:ss'));
			$(".order-import-content").find("#import_end_time").val(laydate.now(0,'YYYY-MM-DD hh:mm:ss'));
			$(".order-import-content").on("click","#import_start_time",function(){// 时间
				laydate(started);
			});
			$(".order-import-content").on("click","#import_end_time",function(){// 时间
				laydate(end);
			});
		});

		checkFrozen();
		creatLoading(true,theDiv);

		// 获取订单按钮
		$(".order-import-content").on("click","#autoPullOrder",function(){
			autoPullOrder();
		})

		//淘宝订单与线下订单导入tab切换
		$(".importOder-list li").click(function () {
			$(this).addClass("importOder-show").siblings().removeClass();
			var index = $('.importOder-list li').index(this);
			if(index<2){
				$("#tabs > div").hide().eq(index).show();
			}else{
				//展示其他订单导入界面（阿里巴巴，京东，有赞）
				$("#tabs > div").hide().eq(2).show();
				$("#otherList").empty();
				$("#otherExport").text("");
				if(index==2){//京东订单
					$("#otherExport").data("name","京东订单模板");
					$("#otherExport").text("京东订单模板下载");
					$("#other-import-excel").attr("eg","5");//设置上传的模板类型
				}else if(index==3){//有赞订单
					$("#otherExport").data("name","有赞订单模板");
					$("#otherExport").text("有赞订单模板下载");
					$("#other-import-excel").attr("eg","7");//设置上传的模板类型
				}else if(index==4){//阿里巴巴订单
					$("#otherExport").data("name","阿里巴巴订单模板");
					$("#otherExport").text("阿里巴巴订单模板下载");
					$("#other-import-excel").attr("eg","6");//设置上传的模板类型
				}else if(index==5){
					$("#otherExport").data("name","人人店订单模板");
					$("#otherExport").text("人人店订单模板下载");
					$("#other-import-excel").attr("eg","8");//设置上传的模板类型
				}
			}
		});

		//同步订单和手动导入tab切换
		$(".synchronousOder-list li").click(function () {
			$(this).addClass("synchronousOder-show").siblings().removeClass();
		});

		// 同步订单
		$(".synchronousOder").click(function () {
			$('.synchronousOderCont').show();
			$('.manualImportCont').hide();
		});
		// 手动导入
		$(".manualImport").click(function () {
			$('.synchronousOderCont').hide();
			$('.manualImportCont').show();
		});
	}

	function init_webuploader_register() {
		webuploader.Uploader.register(
			{
				"before-send-file": "beforeSendFile"
			},
			{
				"beforeSendFile": function(file) {
					var task = webuploader.Base.Deferred();
					(new webuploader.Uploader()).md5File(file, 0, file.size)
						.progress(function(percentage) {//MD5值计算实时进度
							console.log("beforeSendFile    [MD5 percentage]----->" + percentage);
						}).fail(function() {//如果读取出错了，则通过reject告诉webuploader文件上传出错。
							task.reject();
							console.log("beforeSendFile    [file MD5 value calculate error]");
						}).then(function(md5) {//MD5值计算完成
							console.log("beforeSendFile    " + file.id + "_md5----->" + md5);
							taobao_uploader.options.formData[file.id + "_md5"] = md5;
							offline_uploader.options.formData[file.id + "_md5"] = md5;
							other_uploader.options.formData[file.id + "_md5"] = md5;
							task.resolve();
							console.log("beforeSendFile    [next step]");
						});
					return webuploader.Base.when(task);
				}
			}
		);
	}

	//淘宝订单上传初始化
	function init_taobaoOrder_uploader(email){
		taobao_uploader = webuploader.create({
			swf: '../js/webuploader/Uploader.swf',// swf文件路径
			server: '/sales/importOrder',// 文件接收服务端
			// 选择文件的按钮。可选
			// 内部根据当前运行是创建，可能是input元素，也可能是flash.
			pick: '#picker',
			accept: {
				title: 'Excel',
				extensions: 'xls,xlsx,csv'
			},
			fileNumLimit: 2,
			duplicate: false,
			formData: {"email": email}
		});

		// 文件选择限制
		taobao_uploader.on( 'error', function( type ) {
			if(type=="Q_EXCEED_NUM_LIMIT"){
				layer.msg('超过文件添加最大数！', {icon: 5});
			}
			if(type=="Q_TYPE_DENIED"){
				layer.msg('请选择正确的Excel文件！', {icon: 5});
			}
			if(type=="F_DUPLICATE"){
				layer.msg('不能选择重复文件！', {icon: 5});
			}
		});

		// 当有文件被添加进队列之前的时候
		/*uploader.on( 'beforeFileQueued', function( file ) {
		 if(file.name.split('.')[0]=="淘宝订单信息模板"||file.name.split('.')[0]=="淘宝商品信息模板"){
		 return true;
		 }else{
		 layer.msg('请按规定的文件名上传！', {icon: 5});
		 return false;
		 }
		 });*/

		// 当有文件被添加进队列的时候
		taobao_uploader.on( 'fileQueued', function( file ) {
			$("#thelist").append('<b id="'+file.id+'">'+file.name+'</b>');
		});

		taobao_uploader.on( 'uploadAccept', function( file, response ) {
			if(response.flag==false){
				layer.msg(response.msg, {icon: 2,time:2000});
			}else{
				//导入详情提示框
				// openResultBox(response.resultInfos);
			}
		});

		//上传成功事件
		taobao_uploader.on( 'uploadSuccess', function( file, response ) {
			if(!response.flag){
				layer.msg(response.msg, {icon: 2,time:2000});
			}else{
				console.log(response.resultInfos);
				if (response.resultInfos[0].messages.length > 0) {
					layer.msg(response.resultInfos[0].messages[0], {icon: 2,time:2000});
					return false;
				} else {
					layer.msg("导入成功", {icon: 1,time:2000});
				}
				//导入详情提示框
				// openResultBox(response.resultInfos);
				myOrder();
				return false;
			}
		});

		//上传失败事件
		taobao_uploader.on( 'uploadError', function( file ) {
			layer.msg(file.name+'上传失败！', {icon: 5});
		});

		// 上传完成
		taobao_uploader.on( 'uploadComplete', function( file ) {
			$("#thelist").empty();
			taobao_uploader.reset();
		});

		//上传服务器
		$("#import-excel").on( 'click', function() {
			//获取配置的传入参数
			taobao_uploader.options.formData.templateType=1;
			taobao_uploader.upload();
		});
	}

	//线下订单上传初始化
	function init_offlineOrder_uploader(email){
		offline_uploader = webuploader.create({
			swf: '../js/webuploader/Uploader.swf',// swf文件路径
			server: '/sales/importOrder',// 文件接收服务端
			// 选择文件的按钮。可选。
			// 内部根据当前运行是创建，可能是input元素，也可能是flash.
			pick: '#offlinePicker',
			accept: {
				title: 'Excel',
				extensions: 'xls,xlsx,csv'
			},
			fileNumLimit: 2,
			duplicate: false,
			formData: {"email": email}
		});

		// 文件选择限制
		offline_uploader.on( 'error', function( type ) {
			if(type=="Q_EXCEED_NUM_LIMIT"){
				layer.msg('超过文件添加最大数！', {icon: 5});
			}
			if(type=="Q_TYPE_DENIED"){
				layer.msg('请选择正确的Excel文件！', {icon: 5});
			}
			if(type=="F_DUPLICATE"){
				layer.msg('不能选择重复文件！', {icon: 5});
			}
		});

		// 当有文件被添加进队列之前的时候
		/*uploader.on( 'beforeFileQueued', function( file ) {
		 if(file.name.split('.')[0]=="淘宝订单信息模板"||file.name.split('.')[0]=="淘宝商品信息模板"){
		 return true;
		 }else{
		 layer.msg('请按规定的文件名上传！', {icon: 5});
		 return false;
		 }
		 });*/

		// 当有文件被添加进队列的时候
		offline_uploader.on( 'fileQueued', function( file ) {
			$("#offlineList").append('<b id="'+file.id+'">'+file.name+'</b>');
		});

		offline_uploader.on( 'uploadAccept', function( file, response ) {
			if(response.flag==false){
				layer.msg(response.msg, {icon: 2,time:2000});
			}else{
				//导入详情提示框
				// openResultBox(response.resultInfos);
			}
		});

		//上传成功事件
		offline_uploader.on( 'uploadSuccess', function( file, response ) {
			if(!response.flag){
				layer.msg(response.msg, {icon: 2,time:2000});
			}else{
				console.log(response.resultInfos);
				if (response.resultInfos[0].messages.length > 0) {
					layer.msg(response.resultInfos[0].messages[0], {icon: 2,time:2000});
					return false;
				} else {
					layer.msg("导入成功", {icon: 1,time:2000});
				}
				//导入详情提示框
				// openResultBox(response.resultInfos);
				myOrder();
				return false;
			}
		});

		//上传失败事件
		offline_uploader.on( 'uploadError', function( file ) {
			layer.msg(file.name+'上传失败！', {icon: 5});
		});

		// 上传完成
		offline_uploader.on( 'uploadComplete', function( file ) {
			$("#offlineList").empty();
			offline_uploader.reset();
		});

		//上传服务器
		$("#offline_import-excel").on( 'click', function() {
			offline_uploader.option("formData").templateType=3;
			offline_uploader.upload();
		});
	}

	//其他订单上传初始化（有赞，阿里巴巴，京东）
	function init_otherOrder_uploader(email){
		other_uploader = webuploader.create({
			swf: '../js/webuploader/Uploader.swf',// swf文件路径
			server: '/sales/importOtherOrder',// 文件接收服务端
			// 选择文件的按钮。可选。
			// 内部根据当前运行是创建，可能是input元素，也可能是flash.
			pick: '#otherPicker',
			accept: {
				title: 'Excel',
				extensions: 'xls,xlsx,csv'
			},
			fileNumLimit: 2,
			duplicate: false,
			formData: {"email": email}
		});

		// 文件选择限制
		other_uploader.on( 'error', function( type ) {
			if(type=="Q_EXCEED_NUM_LIMIT"){
				layer.msg('超过文件添加最大数！', {icon: 5});
			}
			if(type=="Q_TYPE_DENIED"){
				layer.msg('请选择正确的Excel文件！', {icon: 5});
			}
			if(type=="F_DUPLICATE"){
				layer.msg('不能选择重复文件！', {icon: 5});
			}
		});

		// 当有文件被添加进队列之前的时候
		/*uploader.on( 'beforeFileQueued', function( file ) {
		 if(file.name.split('.')[0]=="淘宝订单信息模板"||file.name.split('.')[0]=="淘宝商品信息模板"){
		 return true;
		 }else{
		 layer.msg('请按规定的文件名上传！', {icon: 5});
		 return false;
		 }
		 });*/

		// 当有文件被添加进队列的时候
		other_uploader.on( 'fileQueued', function( file ) {
			$("#otherList").append('<b id="'+file.id+'">'+file.name+'</b>');
		});

		other_uploader.on( 'uploadAccept', function( file, response ) {
			if(response.flag==false){
				layer.msg(response.msg, {icon: 2,time:2000});
			}else{
				//导入详情提示框
				// openResultBox(response.resultInfos);
			}
		});

		//上传成功事件
		other_uploader.on( 'uploadSuccess', function( file, response ) {
			if(!response.flag){
				layer.msg(response.msg, {icon: 2,time:2000});
			}else{
				console.log(response.resultInfos);
				var length = response.resultInfos[0].messages.length;
				if (length > 0) {
					var messages = response.resultInfos[0].messages
					var msg = ''
					for(var i in messages){
						msg += messages[i]+"<br>"
					}
					layer.msg(msg, {icon: 2,time:2000});
					return false;
				} else {
					layer.msg("导入成功", {icon: 1,time:2000});
				}
				//导入详情提示框
				// openResultBox(response.resultInfos);
				myOrder();
				return false;
			}
		});

		//上传失败事件
		other_uploader.on( 'uploadError', function( file ) {
			layer.msg(file.name+'上传失败！', {icon: 5});
		});

		// 上传完成
		other_uploader.on( 'uploadComplete', function( file ) {
			$("#otherList").empty();
			other_uploader.reset();
		});

		// 将excel上传服务器
		$("#other-import-excel").on( 'click', function() {
			//配置传入参数
			other_uploader.option("formData").templateType=$(this).attr("eg");
			other_uploader.upload();
		});
	}

	//弹出导入订单提示框
	function openResultBox(resultInfos){
		var goal = $("#import_order_result_box");
		//清空原有内容
		goal.children().empty();
		//添加新提示
		var itemHTML = '';
		var resultInfo = resultInfos[0];
		itemHTML +='<p>导入'+resultInfo.fileName+'提示信息：</p>';
		for (var i = 0; i < resultInfo.messages.length; i++) {
			itemHTML +='<p><span>'+resultInfo.messages[i]+'</span></p>';
		}
		itemHTML +='<p>成功：'+resultInfo.successCount+'条,失败：'+resultInfo.failCount+'条</p>';
		goal.children().append(itemHTML);
		layer.open({
			type: 1,
			title: '导入提示',
			btn: false,
			shadeClose: true,
			content: goal,
			area: ['450px', '300px'],
			move: false
		});
	}

	function synchronization(obj, change) {
		if (change == 'Ushow') {
			$(".Manual-import-orders").show();
			$(".Synchronization-order").hide();
			$(".get-orders-list").hide();
		} else if (change == 'Uhide') {
			$(".Manual-import-orders").hide();
			$(".Synchronization-order").show();
			$(".get-orders-list").hide();
		}
	}

	//获取当前用户上传的订单
	function getUploadOrders(email,status,option,curr){
		var params = {"email": email, "oprateStatus": option, "pageNo": curr, "pageSize": 5};
		ajax_post("/sales/showOrders", params, undefined,
			function(response) {
				insert_order(response.data,option,status);
				init_pagination_order(response.page,email,status,option);
				changeDisable();
				$("input[name='all-check']").prop("checked",false);
				$("input[name='selectAll']").prop("checked",false);
			}, function(xhr, status) {
				layer.msg('获取订单列表数据出错，请稍候重试', {icon : 2, time : 2000});
			}
		);
	}

	//我的订单
	function myOrder(){
		isnulogin(function(email){
			getUploadOrders(email,"Ushow",1,1);
		});
		$(".orderinfo:eq(0)").css("background-color","#4A7CC7");
		$(".orderinfo:eq(1)").css("background-color","#ADD");
	}

	//待生成，待补全订单信息
	function orderInfoTab(tab,option){
		isnulogin(function(email){
			getUploadOrders(email,tab,option,1);
		});
	}

	function insert_order(list,option,status){
		$(".import-list-box table").remove();
		$(".completion-order-list-box table").remove();
		var total = 0;//商品总数量
		if(option==0){//待补全
			total = 0;
			$.each(list,function(i,item){//遍历待补全订单信息
				var goodStr = "";
				total = 0;
				if(item.goods.length>0){
					var index = 0;
					for(var j = 0; j< item.goods.length; j++){
						index += 1;
						total += item.goods[j].amount;
						if(j==0){
							if(item.goods[j].warehouseNameId && item.goods[j].warehouseNameId.length > 0){
								var imgUrl = "../../img/IW71-4-1a3c.jpg";
								if (item.goods[j].imageUrl) {
									imgUrl = item.goods[j].imageUrl;
								}
								var warehouseStr = "<select name='warehouse' data-good='"+item.goods[j].id+"'>" +
									"<option value='0'>请选择</option>";
								var warehouseNameAndId = item.goods[j].warehouseNameId;
								for (var i = 0;i < warehouseNameAndId.length;i++) {
									if (i == 0) {
										warehouseStr +="<option selected='selected' value='"+warehouseNameAndId[i].warehouseId+"'>"+warehouseNameAndId[i].warehouseName+"</option>";
									} else {
										warehouseStr +="<option value='"+warehouseNameAndId[i].warehouseId+"'>"+warehouseNameAndId[i].warehouseName+"</option>";
									}
								}
								warehouseStr += "</select>";
								goodStr +="<tr class='olsit-2'>" +
									"<td width='4%;'>"+index+"</td>" +
									"<td class='olsit-left' style='width: 5%;'>" +
									"<input style='display: none;' id='"+item.goods[j].sku+"' class='goodsCheckbox'  type='checkbox'>" +
									"<span style='cursor:pointer' goodId='"+item.goods[j].id+"' status='Ushow' class='delete_good' >x</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>" +
									"<td class='olsit-left product-detail' style='width:35%;'>" +
									"<img src='"+urlReplace(imgUrl, item.goods[j].sku)+"'/>" +
									"<a style='width:180px;' target='_blank' href='../../product/product-detail.html?sku="+item.goods[j].sku+"&warehouseId="+warehouseNameAndId[0].warehouseId+"'>"+
									deal_with_illegal_json(item.goods[j].ctitle)+"</a>" +
									"</td>" +
									"<td class='olsit-left select-ware' style='width: 12%;margin-left: -88px;'>"+warehouseStr+"</td>" +
									"<td class='olsit-left' style='width: 10%;'>"+item.goods[j].sku+"</td>" +
									"<td class='olsit-left olist-border' style='width: 10%;'>" +
									"<ul class='nums' style='margin-top: -4px;margin-left: 21px'>" +
									"<li class='change_num' data-change='"+0+"' data-good='"+item.goods[j].id+"'" +
									"style='line-height: 19px;margin-top: 5px;width: 25%;cursor: pointer;height: 19px;background-color: #4a7cc7;color: #fff;float: left'>-" +
									"</li>" +
									"<li id='num'>" +
									"<input class='button-num' type='text' value='" + item.goods[j].amount + " ' data-qty='" + item.goods[j].amount + " ' name='qty' " +
									"style='text-align: center;margin-top: 5px;width: 44%; height: 17px; float: left;border-left: 0;border-right: 0;border-top: 1px solid #4a7cc7;border-bottom: 1px solid #4a7cc7;'>" +
									"</li>" +
									"<li class='change_num' data-change='"+1+"' data-good='"+item.goods[j].id+"'" +
									"style='line-height: 19px;margin-top: 5px;width: 25%;cursor: pointer;height: 19px;background-color: #4a7cc7;color: #fff;float: left'>+" +
									"</li>" +
									"</ul>" +
									"</td>" +
									"<td class='olsit-left min-sale' style='width: 10%;'>"+deal_with_illegal_value(item.goods[j].batchNumber)+"</td>" +
									"<td class='olsit-right-1' style='width: 12%;'>" +
									"<span style='display: block;'>待补全</span>" +
									"<span class='detail-order-down' data-orderno='"+item.ORDERNO+"'>查看详情</span>" +
									"<span class='detail-order-up' style='display: none;' onclick=\"checkorders(this,'Uhide')\">收起详情</span>" +
									"</td>" +
									"<td class='olsit-right-2' style='width: 11%;'>" +
									"<span class='amend-order update-order' data-flag='0' data-orderno='"+item.ORDERNO+"'>修改订单</span><br>" +
									"<span class='del-order' data-status='Uhide' data-orderno='"+item.ORDERNO+"'>删除订单</span>" +
									"</td>" +
									"</tr>"
							}else{
								goodStr +="<tr class='olsit-2'>" +
									"<td width='4%;'>"+index+"</td><td class='olsit-left' style='width: 5%;'>" +
									"<input style='display: none;' class='goodsCheckbox' type='checkbox' disabled>" +
									"<span style='cursor:pointer'  goodId='"+item.goods[j].id+"' status='Ushow' class='delete_good' >x</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
									"</td>" +
									"<td class='olsit-left product-detail' style='width: 35%;'>" +
									"<img src='#'/><a style='width:180px;' href='javascript:;'>"+item.goods[j].goodsTitle+"</a>" +
									"</td>" +
									"<td class='olsit-left' style='width: 12%;margin-left: -88px;'>"+deal_with_illegal_value(null)+"</td>" +
									"<td class='olsit-left' style='width: 10%;'>未匹配商品</td>" +
									"<td class='olsit-left olist-border' style='width: 10%;'>"+item.goods[j].amount+"</td>" +
									"<td class='olsit-left min-sale' style='width: 10%;'>"+deal_with_illegal_value(null)+"</td>" +
									"<td class='olsit-right-1' style='width: 12%;'><span style='display: block;'>待补全</span>" +
									"<span class='detail-order-down' data-orderno='"+item.ORDERNO+"'>查看详情</span>" +
									"<span class='detail-order-up' style='display: none;' onclick=\"checkorders(this,'Uhide')\">收起详情</span>" +
									"</td>" +
									"<td class='olsit-right-2' style='width: 11%;'>" +
									"<span class='amend-order update-order' data-flag='0' data-orderno='"+item.ORDERNO+"'>修改订单</span><br>" +
									"<span class='del-order' data-status='Uhide' data-orderno='"+item.ORDERNO+"'>删除订单</span>" +
									"</td>" +
									"</tr>"
							}
						}else{
							if(item.goods[j].warehouseNameId && item.goods[j].warehouseNameId.length > 0){
								var imgUrl = "../../img/IW71-4-1a3c.jpg";
								if (item.goods[j].imageUrl) {
									imgUrl = item.goods[j].imageUrl;
								}
								var warehouseStr = "<select name='warehouse' data-good='"+item.goods[j].id+"'>" +
									"<option value='0'>请选择</option>";
								var warehouseNameAndId = item.goods[j].warehouseNameId;
								for (var i = 0;i < warehouseNameAndId.length;i++) {
									if (i == 0) {
										warehouseStr +="<option selected='selected' value='"+warehouseNameAndId[i].warehouseId+"'>"+warehouseNameAndId[i].warehouseName+"</option>";
									} else {
										warehouseStr +="<option value='"+warehouseNameAndId[i].warehouseId+"'>"+warehouseNameAndId[i].warehouseName+"</option>";
									}
								}
								warehouseStr += "</select>";
								goodStr +="<tr class='olsit-2'>" +
									"<td width='4%;'>"+index+"</td>" +
									"<td class='olsit-left' style='width: 5%;'>" +
									"<input style='display: none;' id='"+item.goods[j].sku+"' class='goodsCheckbox'  type='checkbox'>" +
									"<span style='cursor:pointer' goodId='"+item.goods[j].id+"' status='Ushow' class='delete_good' >x</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
									"</td>" +
									"</td><td class='olsit-left product-detail' style='width: 35%;'>" +
									"<img src='"+urlReplace(imgUrl, item.goods[j].sku)+"'/>" +
									"<a style='width:180px;' target='_blank' href='../../product/product-detail.html?sku="+item.goods[j].sku+"&warehouseId="+warehouseNameAndId[0].warehouseId+"'>"+
									deal_with_illegal_json(item.goods[j].ctitle)+
									"</a>" +
									"</td>" +
									"<td class='olsit-left select-ware' style='width: 12%;margin-left: -88px;'>"+warehouseStr+"</td>" +
									"<td class='olsit-left' style='width: 10%;'>"+item.goods[j].sku+"</td>" +
									"<td class='olsit-left olist-border' style='width: 10%;'>" +
									"<ul class='nums' style='margin-top: -4px;margin-left: 21px'>" +
									"<li class='change_num' data-change='"+0+"' data-good='"+item.goods[j].id+"'" +
									"style='line-height: 19px;margin-top: 5px;width: 25%;cursor: pointer;height: 19px;background-color: #4a7cc7;color: #fff;float: left'>-" +
									"</li>" +
									"<li id='num'>" +
									"<input class='button-num' type='text' value='" + item.goods[j].amount + " ' data-qty='" + item.goods[j].amount + " ' name='qty' " +
									"style='text-align: center;margin-top: 5px;width: 44%; height: 17px; float: left;border-left: 0;border-right: 0;border-top: 1px solid #4a7cc7;border-bottom: 1px solid #4a7cc7;'>" +
									"</li>" +
									"<li class='change_num' data-change='"+1+"' data-good='"+item.goods[j].id+"'" +
									"style='line-height: 19px;margin-top: 5px;width: 25%;cursor: pointer;height: 19px;background-color: #4a7cc7;color: #fff;float: left'>+" +
									"</li>" +
									"</ul>" +
									"<td class='olsit-left min-sale' style='width: 10%;'>"+deal_with_illegal_value(item.goods[j].batchNumber)+"</td>" +
									"</td>" +
									"</tr>"
							}else{
								goodStr +="<tr class='olsit-2'>" +
									"<td width='4%;'>"+index+"</td>" +
									"<td class='olsit-left' style='width: 5%;'>" +
									"<input style='display: none;' class='goodsCheckbox' type='checkbox' disabled>" +
									"<span style='cursor:pointer'  goodId='"+item.goods[j].id+"' status='Ushow' class='delete_good' >x</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
									"</td>" +
									"</td>" +
									"<td class='olsit-left product-detail' style='width: 35%;'>" +
									"<img src='#'/>" +
									"<a style='width:180px;' href='javascript:;'>"+item.goods[j].goodsTitle+"</a>" +
									"</td>" +
									"<td class='olsit-left' style='width: 12%;margin-left: -88px;'>"+deal_with_illegal_value(null)+"</td>" +
									"<td class='olsit-left' style='width: 10%;'>未匹配商品</td>" +
									"<td class='olsit-left olist-border' style='width: 10%;'>"+item.goods[j].amount+"</td>" +
									"<td class='olsit-left min-sale' style='width: 10%;'>"+deal_with_illegal_value(null)+"</td>" +
									"</tr>"
							}
						}
					}
				}else{
					goodStr +="<tr class='olsit-2'>" +
						"<td colspan='5'>" +
						"<p style='height:95px;line-height:95px;'>该张单没有导入相应的商品信息</p>" +
						"</td>" +
						"<td class='olsit-right-1' style='width: 12%;'>" +
						"<span style='display: block;'>待补全</span>" +
						"<span class='detail-order-down' data-orderno='"+item.ORDERNO+"'>查看详情</span>" +
						"<span class='detail-order-up' style='display: none;' onclick=\"checkorders(this,'Uhide')\">收起详情</span>" +
						"</td>" +
						"<td class='olsit-right-2' style='width: 11%;'>" +
						"<span class='amend-order' style='background-color: #ccc;' data-flag='0' data-orderno='"+item.ORDERNO+"'>修改订单</span><br>" +
						"<span class='del-order' data-status='Uhide' data-orderno='"+item.ORDERNO+"'>删除订单</span>" +
						"</td>" +
						"</tr>"
				}

				var orderStr="<table class='completion-order-list'>" +
					"<tbody>" +
					"<tr class='olsit-1'>" +
					"<td class='olsit-left' style='width: 5%;text-align: center;'>" +
					"<input type='checkbox' data-no='"+item.ORDERNO+"' name='order-check'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
					"</td>" +
					"<td colspan='6' style='width: 95%;'>" +
					"<ul class='detail'>" +
					"<li>平台订单号：<span>"+item.ORDERNO+"</span></li>" +
					"<li>收货人姓名：<span>"+item.RECEIVERNAME+"</span></li>" +
					"<li class='totalAmount'>商品总数：<span>"+total+"</span></li>" +
					"</ul>" +
					"</td>" +
					"</tr>" +
					"<tr class='olsit-title'>" +
					"<td style='width: 3%;'>序号</td>" +
					"<td style='width: 5%;'>删除</td>" +
					"<td style='width: 28%;'>商品名称</td>" +
					"<td style='width: 12%;'>仓库</td>" +
					"<td style='width: 10%;'>商品编号</td>" +
					"<td style='width: 10%;'>数量</td>" +
					"<td style='width: 10%;'>起发量</td>" +
					"<td style='width: 12%;'>订单状态</td>" +
					"<td style='width: 10%;'>操作</td>" +
					"</tr>"
					+ goodStr +
					"</tbody>" +
					"</table>";
				$(".completion-order-list-box").append(orderStr);
			});
		}

		if(option==1){//待生成
			total = 0;
			$.each(list,function(i,item){//遍历待生成订单
				var goodStr = "";
				total = 0;
				var index = 0;
				if(item.goods.length>0){
					for(var j = 0; j< item.goods.length; j++){
						total += item.goods[j].amount;
						index += 1;
						if(j==0){
							if(item.goods[j].warehouseNameId && item.goods[j].warehouseNameId.length > 0){
								var imgUrl = "../../img/IW71-4-1a3c.jpg";
								if (item.goods[j].imageUrl) {
									imgUrl = item.goods[j].imageUrl;
								}
								goodStr +="<tr class='olsit-2'>" +
									"<td width='4%;'>"+index+"</td>" +
									"<td class='olsit-left' style='width: 5%;'>" +
									"<input style='display: none;' id='"+item.goods[j].sku+"' class='goodsCheckbox'  type='checkbox'>" +
									"<span style='cursor:pointer' goodId='"+item.goods[j].id+"' status='Ushow' class='delete_good' >x</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
									"</td>" +
									"<td class='olsit-left product-detail' style='width: 35%;'>" +
									"<img src='"+urlReplace(imgUrl, item.goods[j].sku)+"'/>" +
									"<a style='width:180px;' target='_blank' href='../../product/product-detail.html?sku="+item.goods[j].sku+"&warehouseId="+item.goods[j].warehouseId+"'>"+deal_with_illegal_json(item.goods[j].ctitle)+"</a>" +
									"</td>" +
									"<td class='olsit-left' style='width: 12%;margin-left: -88px;'>" +
									"<span class='warehouseInfo' warehouseId='"+item.goods[j].warehouseId+"'>"+item.goods[j].warehouseName+"</span>" +
									"</td>" +
									"<td class='olsit-left' style='width: 10%;'>"+item.goods[j].sku+"</td>" +
									"<td data-warehouseid='"+item.goods[j].warehouseId+"' class='olsit-left olist-border' style='width: 10%;'>" +
									"<ul class='nums' style='margin-top: -4px;margin-left: 21px'>" +
									"<li class='change_num' data-change='"+0+"' data-good='"+item.goods[j].id+"'" +
									"style='line-height: 19px;margin-top: 5px;width: 25%;cursor: pointer;height: 19px;background-color: #4a7cc7;color: #fff;float: left'>-" +
									"</li>" +
									"<li id='num'>" +
									"<input class='button-num' type='text' value='" + item.goods[j].amount + " ' data-qty='" + item.goods[j].amount + " ' name='qty' " +
									"style='text-align: center;margin-top: 5px;width: 44%; height: 17px; float: left;border-left: 0;border-right: 0;border-top: 1px solid #4a7cc7;border-bottom: 1px solid #4a7cc7;'>" +
									"</li>" +
									"<li class='change_num' data-change='"+1+"' data-good='"+item.goods[j].id+"'" +
									"style='line-height: 19px;margin-top: 5px;width: 25%;cursor: pointer;height: 19px;background-color: #4a7cc7;color: #fff;float: left'>+" +
									"</li>" +
									"</ul>" +
									"</td>" +
									"<td class='olsit-left min-sale' style='width: 10%;'>"+deal_with_illegal_value(item.goods[j].batchNumber)+"</td>" +
									"<td class='olsit-right-1' style='width: 12%;top:42%;'>" +
									"<span style='display: block;'>待生成</span>" +
									"<span class='detail-order-down' data-orderno='"+item.ORDERNO+"'>查看详情</span>" +
									"</td><td class='olsit-right-2' style='width: 11%;top:42%;'>" +
									"<span class='place-order gene-order'>生成订单</span><br>" +
									"<span class='amend-order update-order' data-flag='1' data-orderno='"+item.ORDERNO+"'>修改订单</span><br>" +
									"<span class='del-order' data-status='Ushow' data-orderno='"+item.ORDERNO+"'>删除订单</span>" +
									"</td>" +
									"</tr>"
							}else{
								goodStr +="<tr class='olsit-2'>" +
									"<td width='4%;'>"+index+"</td>" +
									"<td class='olsit-left' style='width: 5%;'>" +
									"<input style='display: none;' class='goodsCheckbox' type='checkbox' disabled>" +
									"<span style='cursor:pointer'  goodId='"+item.goods[j].id+"' status='Ushow' class='delete_good' >x</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>" +
									"<td class='olsit-left product-detail' style='width: 35%;' class=''>" +
									"<img src='#'/><a style='width:180px;' href='javascript:;'>"+item.goods[j].goodsTitle+"</a>" +
									"</td>" +
									"<td class='olsit-left' style='width: 12%;margin-left: -88px;'>"+deal_with_illegal_value(null)+"</td>" +
									"<td class='olsit-left' style='width: 10%;'>未匹配商品</td>" +
									"<td class='olsit-left olist-border' style='width: 10%;'>"+item.goods[j].amount+"</td>" +
									"<td class='olsit-left min-sale' style='width: 10%;'>"+deal_with_illegal_value(null)+"</td>" +
									"<td class='olsit-right-1' style='width: 12%;top:42%;'>" +
									"<span style='display: block;'>待生成</span>" +
									"<span class='detail-order-down' data-orderno='"+item.ORDERNO+"'>查看详情</span>" +
									"</td>" +
									"<td class='olsit-right-2' style='width: 11%;top:42%;'>" +
									"<span class='place-order gene-order'>生成订单</span><br>" +
									"<span class='amend-order update-order' data-flag='1' data-orderno='"+item.ORDERNO+"'>修改订单</span><br>" +
									"<span class='del-order' data-status='Ushow' data-orderno='"+item.ORDERNO+"'>删除订单</span>" +
									"</td>" +
									"</tr>"
							}
						}else{
							if(item.goods[j].warehouseNameId && item.goods[j].warehouseNameId.length > 0){
								var imgUrl = "../../img/IW71-4-1a3c.jpg";
								if (item.goods[j].warehouseNameId.length > 0) {
									imgUrl = item.goods[j].imageUrl;
								}
								goodStr +="<tr class='olsit-2'>" +
									"<td width='4%;'>"+index+"</td>" +
									"<td class='olsit-left' style='width: 5%;'>" +
									"<input style='display: none;'  id='"+item.goods[j].sku+"' class='goodsCheckbox'  type='checkbox'>" +
									"<span style='cursor:pointer'  goodId='"+item.goods[j].id+"' status='Ushow' class='delete_good' >x</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
									"</td>" +
									"<td class='olsit-left product-detail' style='width: 35%;' class=''>" +
									"<img src='"+urlReplace(imgUrl, item.goods[j].sku)+"'/>" +
									"<a style='width:180px;' target='_blank' href='../../product/product-detail.html?sku="+item.goods[j].sku+"&warehouseId="+item.goods[j].warehouseId+"'>"+
									deal_with_illegal_json(item.goods[j].ctitle)+
									"</a>" +
									"</td>" +
									"<td class='olsit-left' style='width: 12%;margin-left: -88px;'>" +
									"<span class='warehouseInfo' warehouseId='"+item.goods[j].warehouseId+"'>"+item.goods[j].warehouseName+"</span>" +
									"</td>" +
									"<td class='olsit-left' style='width: 10%;'>"+item.goods[j].sku+"</td>" +
									"<td data-warehouseid='"+item.goods[j].warehouseId+"' class='olsit-left olist-border' style='width: 10%;'>" +
									"<ul class='nums' style='margin-top: -4px;margin-left: 21px'>" +
									"<li class='change_num' data-change='"+0+"' data-good='"+item.goods[j].id+"'" +
									"style='line-height: 19px;margin-top: 5px;width: 25%;cursor: pointer;height: 19px;background-color: #4a7cc7;color: #fff;float: left'>-" +
									"</li>" +
									"<li id='num'>" +
									"<input class='button-num' type='text' value='" + item.goods[j].amount + " ' data-qty='" + item.goods[j].amount + " ' name='qty' " +
									"style='text-align: center;margin-top: 5px;width: 44%; height: 17px; float: left;border-left: 0;border-right: 0;border-top: 1px solid #4a7cc7;border-bottom: 1px solid #4a7cc7;'>" +
									"</li>" +
									"<li class='change_num' data-change='"+1+"' data-good='"+item.goods[j].id+"'" +
									"style='line-height: 19px;margin-top: 5px;width: 25%;cursor: pointer;height: 19px;background-color: #4a7cc7;color: #fff;float: left'>+" +
									"</li>" +
									"</ul>" +
									"</td>" +
									"<td class='olsit-left min-sale' style='width: 10%;'>"+deal_with_illegal_value(item.goods[j].batchNumber)+"</td>" +
									"</tr>"
							}else{
								goodStr +="<tr class='olsit-2'>" +
									"<td width='4%;'>"+index+"</td>" +
									"<td class='olsit-left' style='width: 5%;'>" +
									"<input style='display: none;' class='goodsCheckbox' type='checkbox' disabled>" +
									"<span style='cursor:pointer'  goodId='"+item.goods[j].id+"' status='Ushow' class='delete_good' >x</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
									"</td>" +
									"<td class='olsit-left product-detail' style='width: 35%;' class=''>" +
									"<img src='#'/><a style='width:180px;' href='javascript:;'>"+item.goods[j].goodsTitle+"</a></td>" +
									"<td class='olsit-left' style='width: 12%;margin-left: -88px;'>"+deal_with_illegal_value(null)+"</td>" +
									"<td class='olsit-left' style='width: 10%;'>未匹配商品</td>" +
									"<td class='olsit-left olist-border' style='width: 10%;'>"+item.goods[j].amount+"</td>" +
									"<td class='olsit-left min-sale' style='width: 10%;'>"+deal_with_illegal_value(null)+"</td>" +
									"</tr>"
							}
						}
					};
				}else{
					goodStr +="<tr class='olsit-2'>" +
						"<td colspan='5'><p style='height:95px;line-height:95px;'>该张单没有导入相应的商品信息</p></td>" +
						"<td class='olsit-right-1' style='width: 10%;top:42%;'>" +
						"<span style='display: block;'>待生成</span>" +
						"<span class='detail-order-down' data-orderno='"+item.ORDERNO+"'>查看详情</span>" +
						"</td>" +
						"<td class='olsit-right-2' style='width: 10%;top:42%;'>" +
						"<span class='place-order'>生成订单</span><br>" +
						"<span class='amend-order' data-flag='1' data-orderno='"+item.ORDERNO+"'>修改订单</span><br>" +
						"<span class='del-order' data-status='Ushow' data-orderno='"+item.ORDERNO+"'>删除订单</span>" +
						"</td>" +
						"</tr>"
				}

				var orderStr="<table class='import-list'>" +
					"<tr class='olsit-1'><td colspan='7' style='width:100%'>" +
					"<ul class='detail'>" +
					"<li><input id='"+item.ORDERNO+"' class='orderCheckbox' type='checkbox'></li>" +
					"<li>平台订单号：<span>"+item.ORDERNO+"</span></li>" +
					"<li>收货人姓名：<span>"+item.RECEIVERNAME+"</span></li>" +
					"<li>收货人身份证号：<span>"+deal_with_illegal_value(item.RECEIVERCARDNUMBER)+"</span></li>" +
					"<li class='totalAmount'>商品总数：<span>"+total+"</span></li>" +
					"</ul>" +
					"</td>" +
					"</tr>" +
					"<tr class='olsit-title'>" +
					"<td style='width: 3%;'>序号</td>" +
					"<td style='width: 5%;'>删除</td>" +
					"<td style='width: 28%;'>商品名称</td>" +
					"<td style='width: 12%;'>仓库</td>" +
					"<td style='width: 10%;'>商品编号</td>" +
					"<td style='width: 10%;'>数量</td>" +
					"<td style='width: 10%;'>起发量</td>" +
					"<td style='width: 12%;'>订单状态</td>" +
					"<td style='width: 10%;'>操作</td>" +
					"</tr>"
					+ goodStr +
					"</tbody>" +
					"</table>";
				$(".import-list-box").append(orderStr);
			});
		}
		getorders(null,status);
	}

	//初始化分页栏
	function init_pagination_order(page,email,status,option) {
		if ($("#pagination_order")[0] != undefined) {
			$("#pagination_order").empty();
			laypage({
				cont: 'pagination_order',
				pages: page.totalPage,
				curr: page.currPage,
				groups: 5,
				skin: 'yahei',
				first: '首页',
				last: '尾页',
				prev: '上一页',
				next: '下一页',
				skip: true,
				jump: function(obj, first) {
					//first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
					if(!first){
						getUploadOrders(email,status,option,obj.curr);
					}
				}
			});
		}
	}


	function getorders(obj, change) {
		var option = 1;
		$(".get-orders-list").show();
		if (change == 'Ushow') {
			$(".import-list-box").show();
			$(".completion-order-list-box").hide();
		} else if (change == 'Uhide') {
			$(".import-list-box").hide();
			$(".completion-order-list-box").show();
			option = 0;
		}
		$(".change_num").on("click",function(){
			var flag = $(this).data("change");
			var goodId = $(this).data("good");
			if(flag == 0){
				flag = false;
				changeNum(this,flag,goodId,layer);
			} else{
				flag = true;
				changeNum(this,flag,goodId,layer);
			}
		});

		$(".button-num").on("keyup",function(){
			var goodId = $($(this)[0]).parent().prev().data("good");
			iptChange(this,goodId,layer);
		});

		//删除某个商品
		$(".delete_good").click(function(){
			var $obj = $(this);
			var goodId = $obj.attr("goodId");
			ajax_get("/sales/deleteTaobaoGood?goodId="+goodId, JSON.stringify(""), "application/json",
				function (data) {
					if (data && data.suc) {
						layer.msg('删除成功', {icon : 1, time : 2000},function(){
							isnulogin(function(email){
								getUploadOrders(email,change,option,$("#pagination_order .laypage_curr").text());
							})
						});
					} else {
						layer.msg('删除失败', {icon : 2, time : 2000});
					}
				}
			);
		});
	}

	//查看详情
	function checkorders(obj, change) {
		if (change == 'Ushow') {
			$(".check-detail").show();
			$(".box-orderlist").hide();
		} else if (change == 'Uhide') {
			$(".check-detail").hide();
			$(".box-orderlist").show();
		}
	}

	//检查起发量
	function checkminSale(obj){
		var flag = true;
		var minSale = 0;
		var sale = 0;
		$.each(obj, function(i,item){
			minSale = parseInt($(item).find(".min-sale").html());
			sale = parseInt($(item).find("#num input").val());
			if (isNaN(minSale)) {
				return true;
			}
			if (sale < minSale){
				flag = false;
				return flag;
			}
		});
		if (!flag) {
			layer.msg('发货数量需等于或大于起发量，请更改发货数量再生成订单', {icon : 2, time : 2000});
		}
		return flag;
	}

	//选择仓库
	function selectWarehouse(data,tab){
		var info = {};
		var warehouses = [];
		var isHasSZ = false;
		var isHasNotSZ = false;
		var  warehouseIds = [];
		var j = 0;
		$.each(data, function(i,item){
			if(tab == 0){
				var parm = {};
				var goodid = $(item).data("good");
				var houseid = $(item).find("option:selected").val();
				var housename = $(item).find("option:selected").html();
				if(houseid=="请选择"||housename=="请选择"){
					return false;
				}
				if (warehouseIds.length > 0 && $.inArray(houseid,warehouseIds) == -1) {
					return false;
				}
				j++;
				if(warehouseType(houseid)){
					isHasSZ = true;
				}else{
					isHasNotSZ = true;
				}
				parm.id = goodid;
				parm.warehouseId = houseid;
				parm.warehouseName = housename;
				warehouses.push(parm);
				warehouseIds.push(houseid);
			} else{
				var iid = $(item).attr("warehouseid");//typeof number 要转 string
				if(warehouseType(iid+"")){
					isHasSZ = true;
				} else{
					isHasNotSZ = true;
				}
				warehouses.push({warehouseId : iid});
				j++;
			}
		});
		if(data.length>j){
			return undefined;
		}
		info.warehouses=warehouses;
		info.isHasSZ=isHasSZ;
		info.isHasNotSZ = isHasNotSZ;
		return info;
	}

	function getId(key,map){
		if(map[key]){
			return map[key];
		}
		key = key.substr(0,key.length-1);
		if(key){
			return map[key];
		}
	}

	function matchAddress(addr){
		var adl = addr.split(" ");
		var flag = false;
		if(adl.length >= 4){
			var prov_id = getId(adl[0],prov_map);
			$("#province").val(getId(adl[0],prov_map)).change();
			if($("#province").val()){
				$("#city").val(getId(adl[1],city_map)).change();
				if($("#city").val()){
					flag = true;
					$("#region").val(getId(adl[2],region_map)).change();
				}
			}
			var newaddr=adl.slice(3).join(" ");
			$("#addr").val(newaddr);
		}
		if(!flag){
			layer.msg("请修改地址，并点击应用",{icon:5,time:2000});
		}
		return flag;
	}

	//获取表单验证后的参数
	function getParamters(flag) {
		var receiverName = $("#form input[name='receiverName']").val().trim();
		var province = $("#province option:selected").val().trim();
		var city = $("#city option:selected").val().trim();
		var county = $("#region option:selected").val().trim();
		var street = $("#form #addr").val().trim();
		var receiverPhone = $("#form input[name='receiverPhone']").val().trim();
		var paymentNo = $("#form input[name='paymentNo']").val().trim();
		var logistics = $("#form select[id='logisticsSelect']");
		var shopName = $("#form select[id='shopName']");
		if (flag) {
			var receiverCardNumber = $("#form input[name='receiverCardNumber']").val().trim();
			var paymentName = $("#form input[name='paymentName']").val().trim();
			var paymentCardNumber = $("#form input[name='paymentCardNumber']").val().trim();
			var paymentPhone = $("#form input[name='paymentPhone']").val().trim();
		}
		var postCode = $("#form input[name='postCode']").val().trim();
		if (receiverName == "") {
			layer.msg('请输入收货人姓名', {icon : 2, time : 2000});
			return undefined;
		}
		if (province == "") {
		 	layer.msg('请选择省', {icon : 2, time : 2000});
		 	return undefined;
		 }
		 if (city == "") {
		 	layer.msg('请选择市', {icon : 2, time : 2000});
		 	return undefined;
		 }
		 if (county == "") {
		 	layer.msg('请选择县', {icon : 2, time : 2000});
			 return undefined;
		 }
		 if (!street||street.length<5) {
		 	layer.msg('请输入详细地址,并且长度必修大于5', {icon : 2, time : 2000});
		 	return undefined;
		 }
		if (receiverCardNumber !=undefined && !checkIDCard(receiverCardNumber)) {
			layer.msg('请输入有效的收货人身份证号', {icon : 2, time : 2000});
			return undefined;
		}
		if(!checkTel(receiverPhone)){
			layer.msg("输入收货人手机号码或者固定电话格式错误",{icon : 2 , time : 2000});
			return undefined;
		}
		if (paymentNo == "") {
			layer.msg('请输入支付交易号', {icon : 2, time : 2000});
			return undefined;
		}
		if (paymentName != undefined && paymentName == "") {
			layer.msg('请输入买家姓名', {icon : 2, time : 2000});
			return undefined;
		}
		if (paymentCardNumber != undefined && !checkIDCard(paymentCardNumber)) {
			layer.msg('请输入有效的买家身份证号', {icon : 2, time : 2000});
			return undefined;
		}
		if(paymentPhone !=undefined && !checkTel(paymentPhone)){
			layer.msg("输入买家手机号码或者固定电话格式错误",{icon : 2 , time : 2000});
			return undefined;
		}
		if (postCode && !checkPost(postCode)) {
			layer.msg('请输入有效的邮政编码', {icon: 2, time: 2000});
			return undefined;
		}
		if(logistics!=undefined && $(logistics).val() =="请选择"){
			layer.msg('请选择物流方式', {icon: 2, time: 2000});
			return undefined;
		}
		if(shopName!=undefined && $(shopName).val() =="请选择"){
			layer.msg('请选择店铺，若没有可选店铺则在我的店铺中添加', {icon: 2, time: 2000});
			return undefined;
		}
		return true;
	}

	//修改订单
	function getOrderDetails(paramData,warehouse,status){
		if(warehouse==undefined){
			layer.msg('请选择或调整仓库（一个订单只支持一个仓库）', {icon : 5, time : 2000});
			return;
		}
		isnulogin(function(email){
			//添加email传入参数，查询当前用户的订单详情
			paramData.email = email;
			console.log(warehouse);
			//完税仓的物流方式列表
			var deliveryTypeList = [];
			if(warehouse!=undefined){
				$.ajax({url:  "/inventory/getShippingMethod?wid=" + warehouse.warehouses[0].warehouseId,//当前一个订单只能选择一个仓库
					type: 'get', data: "", async: false,//是否异步
					success: function(data){
						if(data.code){
							window.location.href = "/personal/login.html";
						}
						deliveryTypeList =  $.parseJSON(data);
					}
				});
			}
			//当前用户店铺列表
			var shopList = [];
			$.ajax({url: "/member/getAllUserShop", type: 'get', data: "", async: false,//是否异步
				success: function(data){
					shopList = data;
				}
			});

			var orderStr = "";
			ajax_post_sync("/sales/orderDetails", JSON.stringify(paramData), "application/json", function(data){
				console.log(data);
				if(data.data!=null){
					//店铺选择html
					var shopListHtml = "<tr>" +
						"<th><font style='color:#e4393c'>*</font>店铺：</th>" +
						"<td>" +
						"<select id='shopName' name='shopName'>" +
						"<option>请选择</option>";
					if(shopList!=null && shopList.length >0){
						for(var i=0;i<shopList.length;i++){
							if(data.data.shopName==shopList[i].name){
								shopListHtml += "<option selected='selected' value='" + shopList[i].name + "'>" + shopList[i].name + "</option>";
							}else{
								shopListHtml += "<option value='" + shopList[i].name + "'>" + shopList[i].name + "</option>";
							}
						}
					}
					shopListHtml += "</select></td></tr>";
					//当仓库为完税仓时，添加物流方式
					var deliveryHtml = "";
					if (deliveryTypeList != null && deliveryTypeList.length > 0) {
						var logisticsTypeCode = data.data.logisticsTypeCode;
						var logisticsTypeName = "";
						deliveryHtml += "<tr>" +
							"<th><font style='color:#e4393c'>*</font>物流方式：</th>" +
							"<td><select id='logisticsSelect' name='logisticsTypeCode'>" +
							"<option>请选择</option>";
						if (logisticsTypeCode) {
							for (var i = 0; i < deliveryTypeList.length; i++) {
								if(deliveryTypeList[i].methodCode == logisticsTypeCode){
									logisticsTypeName = deliveryTypeList[i].name;
									deliveryHtml+= "<option selected='selected' value='"+deliveryTypeList[i].methodCode+"'>"+deliveryTypeList[i].methodName+"</option>";
								}else{
									deliveryHtml+= "<option value='"+deliveryTypeList[i].methodCode+"'>"+deliveryTypeList[i].methodName+"</option>";
								}
							}
						} else {
							for(var i=0;i<deliveryTypeList.length;i++){
								if(deliveryTypeList[i].default){
									logisticsTypeName = deliveryTypeList[i].name;
									deliveryHtml+= "<option selected='selected' value='"+deliveryTypeList[i].methodCode+"'>"+deliveryTypeList[i].methodName+"</option>";
								}else{
									deliveryHtml+= "<option value='"+deliveryTypeList[i].methodCode+"'>"+deliveryTypeList[i].methodName+"</option>";
								}
							}
						}
						deliveryHtml+="</select>" +
							"<input type='hidden' name='logisticsTypeName' value='"+logisticsTypeName+"'/>" +
							"</td>" +
							"</tr>";
					} else {
						layer.msg("物流方式获取失败，订单暂时不能修改！", {icon: 5, time: 2000});
						return;
					}

					var sinfo = "";
					if(warehouse!=undefined && warehouse.isHasNotSZ){
						sinfo +="<tr>" +
							"<th><font style='color:#e4393c'>*</font>收货人身份证号：</th>" +
							"<td><input type='text' value='"+deal_with_illegal_json(data.data.receiverCardNumber)+"' name='receiverCardNumber'></td>" +
							"</tr>" +
							"<tr>" +
							"<th><font style='color:#e4393c'>*</font>买家姓名：</th>" +
							"<td><input type='text' value='"+deal_with_illegal_json(data.data.paymentName)+"' name='paymentName'></td>" +
							"</tr>" +
							"<tr>" +
							"<th><font style='color:#e4393c'>*</font>买家身份证号：</th>" +
							"<td><input type='text' value='"+deal_with_illegal_json(data.data.paymentCardNumber)+"' name='paymentCardNumber'></td>" +
							"</tr>" +
							"<tr>" +
							"<th><font style='color:#e4393c'>*</font>买家手机号：</th>" +
							"<td><input type='text' value='"+deal_with_illegal_json(data.data.paymentPhone)+"' name='paymentPhone'></td>" +
							"</tr>"
					}
					orderStr +="<form id='form'>" +
						"<table class='alter_amend'>" +
						"<input type='hidden' name='email' value='"+email+"' />" +
						"<input type='hidden' name='id' value='"+deal_with_illegal_json(data.data.id)+"'/>"+
						"<tr>" +
						"<th>自动匹配格式：</th>" +
						"<td>" +
						"<select id='address-template'><option value='0'>请选择地址格式</option><option value='1'>淘宝千牛格式</option><option value='2'>淘宝网页格式</option></select>" +
						"</td>" +
						"</tr>"+
						"<tr>" +
						"<th></th>" +
						"<td><input id='waitInfo' name='waitInfo' type='text'><button type='button' class='place-order' style='margin-top: 10px;height: 30px;' id='sure-match' >确定添加</button></td>" +
						"</tr>"+
						"<tr>" +
						"<th><font style='color:#e4393c'>*</font>收货人姓名：</th>" +
						"<td><input id='receiver' type='text'  value='"+deal_with_illegal_json(data.data.receiverName)+"' name='receiverName'></td>" +
						"</tr>"+
						"<tr>" +
						"<th><font style='color:#e4393c'></font>收货地址：</th>" +
						"<td><input id='address' name='addr' type='text' disabled style='border: 0;background: #ccc;' value='"+deal_with_illegal_json(data.data.address).trim()+"'></td>" +
						"</tr>"+
						"<tr>" +
						"<th><font style='color:#e4393c'>*</font>省市区：</th>"+
						"<td>"+
						"<select id='province' style='width:30%;'></select>"+
						"<select id='city' style='width:30%;'></select>"+
						"<select id='region' style='width:30%;'></select>"+
						"</td>"+
						"</tr>" +
						"<tr>" +
						"<th><font style='color:#e4393c'>*</font>详细地址：</th>"+
						"<td>"+
						"<input id = 'addr' maxlength = '120'  placeholder=\"不需重复填写省市区，必须大于5个字符，小于120个字符\" >"+
						"</td>"+
						"</tr>" +
						"<tr>" +
						"<th><font style='color:#e4393c'>*</font>收货人手机号：</th>" +
						"<td><input id='tel' type='text' value='"+deal_with_illegal_json(data.data.receiverPhone)+"' name='receiverPhone'></td>" +
						"</tr>"+
						"<tr>" +
						"<th>收货人电话：</th>" +
						"<td><input type='text' value='"+deal_with_illegal_json(data.data.receiverTelephone)+"' name='receiverTelephone'></td>" +
						"</tr>"+
						"<tr>" +
						"<th><font style='color:#e4393c'>*</font>支付交易号：</th>" +
						"<td><input type='text' value='"+deal_with_illegal_json(data.data.paymentNo == null ? null : data.data.paymentNo.trim())+"' name='paymentNo'></td>" +
						"</tr>"+
						shopListHtml + sinfo +
						"<tr>" +
						"<th>邮编：</th>" +
						"<td><input id='postCode' type='text' value='"+deal_with_illegal_json(data.data.postCode)+"' name='postCode'></td>" +
						"</tr>"+
						"<tr>" +
						"<th>财务备注：</th>" +
						"<td><input type='text' value='"+deal_with_illegal_json(data.data.financeRemark)+"' name='financeRemark'></td>" +
						"</tr>"+
						deliveryHtml +
						"</table>" +
						"</form>";
				}else{
					orderStr +="数据获取失败!";
				}
				layer.open({
					type: 1,
					title: "修改订单",
					area: ['568px', '585px'],
					content: orderStr,
					btn: '保存',
					shadeClose: true,
					skin: 'demo-class',
					yes: function(index, layero){
						var params = getParamters(warehouse.isHasNotSZ);
						if($("#logisticsSelect")!=undefined){
							$("input[name='logisticsTypeName']").val($("#logisticsSelect").find("option:selected").text());
						}
						console.log(params);
						if (params != undefined) {
							var data = layero.find("#form").serializeArray();
							var o = {};
							$.each(data, function() {
								if(this.name == "waitInfo"){
									return true;
								}
								if (o[this.name]) {
									if (!o[this.name].push) {
										o[this.name] = [o[this.name]];
									}
									o[this.name].push(this.value || '');
								} else {
									o[this.name] = this.value || '';
								}
							});
							o.address =  $("#province option:selected").text() + " " + $("#city option:selected").text() + " " + $("#region option:selected").text() + " " + $("#addr").val().trim();
							var paramData = {"data": o, "warehouse": warehouse.warehouses};
							ajax_post_sync("/sales/saveOrder", JSON.stringify(paramData), "application/json", function(data){
								if(data.data>0){
									layer.msg('更新成功！', {icon: 6});
									layer.close(index);
									var flag = "Uhide";
									console.log(typeof flag);
									if(status == "1"){
										flag = "Ushow";
									}
									getUploadOrders(email,flag,status,1);
								}else{
									layer.msg('更新失败！', {icon: 5});
								}
							})
						}
					},
					success: function(layero, index){
						//初始化地址
						initAreaSel();
						$("#form #province").change(function () {//市级下拉框联动
							citySel();
						});
						$("#form #city").change(function () {//区级下拉框联动
							regionSel();
						});
						//校验地址
						var address = data.data.address;
						matchAddress(address);
						$("#sure-match").click(function(){
							sureMatch(this);
						});
					}
				});
			})
		});
	}

	//删除订单
	function deleteOrder(paramData,status){
		isnulogin(function(email){
			layer.confirm('确定删除订单？', {icon: 3, title:'提示'}, function(index){
				$.ajax({
					url: "/sales/deleteOrder",
					type: 'POST',
					data: paramData,
					dataType: 'json',
					success: function(data){
						if(data.code){
							window.location.href = "/personal/login.html";
						}
						if(data.data>0){
							layer.msg('订单删除成功！', {icon: 6});
							var flag = 0;
							if(status == "Ushow"){
								flag = 1;
							}
							getUploadOrders(email,status,flag,1);
						}else{
							layer.msg('操作失败！', {icon: 5});
						}
					},
					error: function(){
						layer.msg("删除订单出错，请稍后重试！", {icon : 2, time : 2000});
					}
				});
				layer.close(index);
			});
		});
	}

	//批量删除订单
	function batchDeleteOrder(data,status,title){
		isnulogin(function(email){
			if (data.length < 1) {
				layer.msg("请勾选要删除的订单！", {icon : 2});
				return;
			}

			layer.confirm(title, {icon: 3, title: '提示'}, function (index) {
				$.ajax({
					url: "/sales/deleteOrder",
					type: 'POST',
					data: {orderList: data,email:email},
					dataType: 'json',
					success: function(data){
						if(data.code){
							window.location.href = "/personal/login.html";
						}
						if(data.data>0){
							layer.msg('订单删除成功！', {icon: 6});
							var flag = 0;
							if(status == "Ushow"){
								flag = 1;
							}
							getUploadOrders(email,status,flag,1);
						}else{
							layer.msg('操作失败！', {icon: 5});
						}
					},
					error: function(){
						layer.msg("删除订单出错，请稍后重试！", {icon : 2, time : 2000});
					}
				});
				layer.close(index);
			});
		});
	}

	//置灰修改订单按钮
	function changeDisable(){
		$(".completion-order-list").each(function(i){
			var selectnum = $(this).find("tr.olsit-2 select").length;
			if(selectnum == 0){
				$(this).find("tr.olsit-2 td.olsit-right-2 span.amend-order").removeClass("update-order").css("background-color","#ccc");
			}
		});

		$(".import-list").each(function(i){
			var selectnum = $(this).find("tr.olsit-2 td span.warehouseInfo").length;
			if(selectnum == 0){
				$(this).find("tr.olsit-2 td.olsit-right-2 span.amend-order").removeClass("update-order").css("background-color","#ccc");
				$(this).find("tr.olsit-2 td.olsit-right-2 span.place-order").removeClass("gene-order").css("background-color","#ccc");
			}
		});
	}

	// 查看详情
	function viewDetails(paramData,status,obj){
		$(".box-right-four .check-detail").remove();
		isnulogin(function(email){
			paramData.email = email; //加上email
			ajax_post("/sales/orderDetails", JSON.stringify(paramData), "application/json", function(data){
				if(data.code){
					window.location.href = "/personal/login.html";
				}
				if(data.data == undefined || data.data == null || data.data == 'null'){
					layer.msg("系统数据有误！", {icon : 2, time : 2000});
					return ;
				}
				console.log(data);
				var oprateStatus = '';
				if(data.data.oprateStatus == 0){
					oprateStatus = '待补全';
				}else{
					oprateStatus = '待生成';
				}
				var str = '<div class="check-detail">'+
					'<div class="title" style="margin-left: 0;">订单导入-订单详情</div>'+
					'<span class="detail-order-up" data-tab="Uhide">返回订单列表</span>'+
					'<div>'+
					'<h2 class="check-title">订单信息</h2>'+
					'<ul>'+
					'<li>订单生成时间：'+deal_with_illegal_value(data.data.updateDate)+'</li>'+
					'<li>订单状态：<span>'+deal_with_illegal_value(oprateStatus)+'</span></li>'+
					'<li>订单编号：<span>'+deal_with_illegal_value(data.data.orderNo)+'</span></li>'+
					'<li>商品总数：<span>'+deal_with_illegal_value($(obj).parents("table").find(".totalAmount span").html())+'</span></li>'+
					'</ul>'+
					'</div>'+
					'<div>'+
					'<h2 class="check-title">买家信息</h2>'+
					'<ul>'+
					'<li>买家账户：<span>'+deal_with_illegal_value(data.data.buyerAccount)+'</span></li>'+
					'<li>收货人信息：<span>'+deal_with_illegal_value(data.data.receiverName)+' , '+deal_with_illegal_value(data.data.receiverPhone)+' ,'+deal_with_illegal_value(data.data.receiverTelephone)+' , '+deal_with_illegal_value(data.data.address)+' , '+deal_with_illegal_value(data.data.receiverCardNumber)+' , '+deal_with_illegal_value(data.data.postCode)+'</span></li>'+
					'<li>买家信息：<span>'+deal_with_illegal_value(data.data.paymentName)+' , '+deal_with_illegal_value(data.data.paymentPhone)+' , '+deal_with_illegal_value(data.data.paymentCardNumber)+'</span></li>'+
					'</ul>'+
					'</div>'+
					'<div>'+
					'<h2 class="check-title">卖家信息</h2>'+
					'<ul>'+
					'<li>店铺名称：<span>'+deal_with_illegal_value(data.data.shopName)+'</span></li>'+
					'<li>平台订单号：<span>'+deal_with_illegal_value(data.data.orderNo)+'</span></li>'+
					'<li>物流方式：<span>'+deal_with_illegal_value(data.data.logisticsTypeName)+'</span></li>'+
					'<li>物流费用：<span>'+deal_with_illegal_value(data.data.logisticsCost)+'</span></li>'+
					'<li>订单总额：<span>'+deal_with_illegal_value(data.data.orderTotal)+'</span></li>'+
					'<li>卖家备注：<span>'+deal_with_illegal_value(data.data.sellerRemark)+'</span></li>'+
					'<li>买家留言：<span>'+deal_with_illegal_value(data.data.buyerMessage)+'</span></li>'+
					'<li>发票信息：<span>'+deal_with_illegal_value(data.data.invoiceInfo)+'</span></li>'+
					'<li>财务备注：<span>'+deal_with_illegal_value(data.data.financeRemark)+'</span></li>'+
					'</ul>'+
					'</div>'+
					'<div class="good-info">'+
					'<h2 class="check-title">商品列表</h2>'+
					'<ul class="detail-olsit-title">'+
					'<li style="width: 40%;">基本信息</li>'+
					'<li style="width: 15%;">商品编号</li>'+
					'<li style="width: 15%;">数量</li>'+
					'<li style="width: 15%;">单价(元)</li>'+
					'<li style="width: 15%;">小计(元)</li>'+
					'</ul>'+
					'</div>'+
					'</div>';
				$(".box-right-four").append(str);
				checkorders(null,status);
				ajax_post("/sales/getGoods",JSON.stringify({orderNo: data.data.orderNo, email:email}),"application/json",function(data){
					$.each(data.data,function(i,good){
						ajax_post("/product/api/getProducts",JSON.stringify({"data": {"skuList": [good.sku]}}),"application/json",function(data){//data.data.result[0].
							console.log(data.data);
							if(data.data.result.length>0){
								var imgUrl = "../../img/IW71-4-1a3c.jpg";
								if (data.data.result[0].imageUrl) {
									imgUrl = data.data.result[0].imageUrl;
								}
								var total = good.amount*good.price;
								var goodStr = '<ul class="detail-olsit-content">'+
									'<li class="product-detail" style="width: 39%;padding-left:5px;">'+
									'<img src="'+urlReplace(imgUrl, data.data.result[0].csku)+'"/><a style="width: 225px;line-height:24px;" target="_blank" href="../../product/product-detail.html?sku='+good.sku+'&warehouseId='+good.warehouseId+'">'+data.data.result[0].ctitle+'</a>'+
									'</li>'+
									'<li style="width: 15%;">'+data.data.result[0].csku+'</li>'+
									'<li style="width: 15%;">'+good.amount+'</li>'+
									'<li style="width: 15%;">'+(good.price).toFixed(2)+'</li>'+
									'<li style="width: 15%;">'+total.toFixed(2)+'</li>'+
									'</ul>';
								$(".good-info").append(goodStr);
							}
						})
					});
				})
			})
		});
	}

	$(".orderTab").on("click", function(){
		synchronization(this, $(this).data("tab"));
	});

	//淘宝订单页面内我的订单展示
	$(".box-right-four").on("click", "#my-order", function(){
		myOrder();
	});

	//线下店铺页面我的订单展示
	$(".box-right-four").on("click", "#sub-my-order", function(){
		myOrder();
	});

	//其他订单（阿里巴巴，有赞，京东）导入页面我的订单展示
	$(".box-right-four").on("click", "#other-my-order", function(){
		myOrder();
	});

	// 待生成信息订单 & 待补全信息订单
	$(".box-right-four").on("click", ".orderinfo", function(){
		$("input[name='all-check']").prop("checked",false);
		$("input[name='selectAll']").prop("checked",false);
		orderInfoTab($(this).data("tab"),$(this).data("option"));
		$(this).css("background-color","#4A7CC7");
		$(this).parents("li").siblings().children("button").css("background-color", "#ADD");
	});

	// 修改订单按钮
	$(".box-right-four").on("click", ".update-order", function(){
		var productTr = $(this).parents("table").find(".olsit-2");
		var paramData = {
			orderNo: $(this).data("orderno"),
			warehouseId:$(this).parent().parent().find("td").eq(2).find("span").eq(0).attr("warehouseid")
		};
		var flag = undefined;
		if ($(this).data("flag") == "0") {
			var minsaleFlag = checkminSale(productTr);
			if (!minsaleFlag) {
				return;
			}
			var data = $(this).parents("table.completion-order-list").find("select");
			flag = selectWarehouse(data,0);
		} else{
			var minsaleFlag = checkminSale(productTr);
			if (!minsaleFlag) {
				return;
			}
			var data = $(this).parents("table.import-list").find("span.warehouseInfo");
			flag = selectWarehouse(data,1);
		}
		getOrderDetails(paramData,flag,$(this).data("flag"));
	});

	// 删除订单
	$(".box-right-four").on("click", ".del-order", function(){
		var orderList =  [$(this).data("orderno")];
		batchDeleteOrder(orderList,$(this).data("status"),"确定删除订单？");
	});

	// 删除所有
	$(".box-right-four").on("click", ".all-delete", function(){
		var obj = $("input[name='order-check']").filter(":checked");
		var orderList = [];
		$.each(obj, function(i,item){
			orderList.push($(item).data("no"));
		});
		batchDeleteOrder(orderList,$(this).data("status"),"确定删除所选订单？");
	});

	// 查看详情
	$(".box-right-four").on("click", ".detail-order-down", function(){
		viewDetails({orderNo: $(this).data("orderno")},"Ushow",this);
	});

	// 返回订单列表
	$(".box-right-four").on("click", ".detail-order-up", function(){
		checkorders(this, $(this).data("tab"));
	});

	// 下单订单excel模板
	$(".box-right-four").on("click", ".export-mould", function(){
		exportMould($(this).data("name"));
	});

	// 全选
	$(".box-right-four").on("click", "input[name='all-check']", function(){
		$("input[name='order-check']").prop("checked", this.checked);
	});

	// 单选
	$(".box-right-four").on("click", "input[name='order-check']", function(){
		$("input[name='all-check']").prop("checked" , $("input[name='order-check']").length == $("input[name='order-check']").filter(":checked").length ? true :false);
	});

	// 模板下载
	function exportMould(name){
		window.location.href = "/sales/exportMould?name=" + name;
	}

	return {
		init_orderimport: init_orderimport
	};
});

//订单导入中改变数量
function changeNum(obj, isAdd, goodId, lay) {
	var minSale = parseInt($(obj).parent().parent().next().html());
	var quantity = 0, sourceQty = 0;
	if (isAdd) {
		sourceQty = parseInt($(obj).prev().find("input").val());
		quantity = sourceQty + 1;
	} else {
		sourceQty = parseInt($(obj).next().find("input").val());
		quantity = sourceQty == 1 ? 1 : (sourceQty - 1);
		if (quantity < minSale) {
			lay.msg("发货数量需等于或大于起发量", {icon: 2,time: 2000});
			return;
		}
	}
	check_import_inventory({"goodId": goodId, "qty": quantity}, sourceQty, $(obj).parent().parent(),lay);
}

// 直接更改输入框中的商品数量
function iptChange(obj, goodId, lay) {
	var quantity = $(obj).val(), sourceQty = parseInt($(obj).data("qty"));
	if (quantity == undefined || quantity == null || isNaN(quantity) || parseInt(quantity) <= 0) {
		lay.msg("请输入数字(必须是大于0的整数)", {icon: 2,time: 2000});
		$(obj).val(sourceQty);
		return;
	}
	if (parseInt($(obj).val()) < parseInt($(obj).parent().parent().parent().next().html())) {
		$(obj).val(sourceQty)
		lay.msg("发货数量需等于或大于起发量", {icon: 2,time: 2000});
		return;
	}
	check_import_inventory({"goodId": goodId, "qty": parseInt(quantity)}, sourceQty, $(obj).parent().parent().parent(),lay);
}

function check_import_inventory(params, sourceQty, $node,lay){
	var totalAmount = parseInt($node.parents("table").find(".totalAmount span").html());
	totalAmount = totalAmount - sourceQty;
	var sku = $node.prev().html();
	var warehouseId = $node.prev().prev().find("option:selected").val();
	if (!$node.prev().prev().find("option:selected").val()) {
		warehouseId = $node.prev().prev().find(".warehouseInfo").attr("warehouseid");
	}

	var checkWarehouse = warehouseId != undefined && warehouseId != null && warehouseId != 0
	if(!checkWarehouse){
		lay.msg("请选择仓库", {icon: 2, time: 2000});
		return;
	}

	isnulogin(function(email) {
		ajax_post("/product/api/getProducts", JSON.stringify({"data":{"skuList":[sku],"warehouseId": warehouseId}}), "application/json",
			function (data) {
				var productList = data.data.result;
				if(productList && productList.length>0) {
					var enough = productList[0].stock >= params.qty;
					if(!enough) {
						lay.msg("修改后的数量(" + params.qty + ")将超出库存("+data.list[0].availableStock+")", {icon: 2, time: 2000});
						$node.find("input").val(sourceQty);
						return;
					}
				} else {
					lay.msg("经云仓查询，当前商品"+sku+"在该仓库下无记录，无法判断库存是否充足！", {icon: 2, time: 2000});
					$node.find("input").val(sourceQty);
					return;
				}

				ajax_post_sync("/sales/updateqty", JSON.stringify(params), "application/json",
					function (data) {
						if (data.result) {
							$node.find("input").val(params.qty);
							$node.find("input").data("qty", params.qty);
							var unitPrice = $node.parent().prev().find("b").text();
							$node.parent().next().find("span").find("b").text(unitPrice * params.qty);
						} else {
							lay.msg("数量变更失败，请稍后重试", {icon: 2, time: 2000});
						}
					}
				);
			}
		);
	});
	totalAmount += parseInt($node.find("input").val());
	$node.parents("table").find(".totalAmount span").html(totalAmount);
}