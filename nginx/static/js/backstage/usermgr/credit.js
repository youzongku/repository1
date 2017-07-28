//定义全局变量
var layer = undefined;
var laypage = undefined;

//初始化后台用户列表
function init_credit(layerParam, laypageParam) {
	//初始化全局变量
	layer = layerParam;
	laypage = laypageParam;
	ajax_get_credit(1);
}

//发送请求获取数据(默认展示用户临时额度数据)
function ajax_get_credit(curr) {
	var params = {
		currPage: curr,
		redit:1,
		pageSize: $(".list_content p select").val()
	};
	params.search = $("#searchInput").val();
	params.sregdate = $("#seachTime0").val();
	params.eregdate = $("#seachTime1").val();
	params.limiteState = $("#limiteStatus").val();
	params.comsumerType = $("#comsumerTypeOfCredit").val();
	params.redit = $("#redit").val();
	$("#temporary_pagination").empty();
	ajax_post("../member/getAllCredit", params, undefined,
		function(response) {
			if (response.suc) {
				insert_ul_credit(response.page.list);
				$("#count").text(response.page.rows);
				$("#page_count").text(response.page.totalPage);
				init_credit_pagination(response.page);
			} else if (response.code == "2") {
				window.location.href = "login.html";
			} else {
				layer.msg(response.msg, {icon : 2, time : 1000});
			}
		},
		function(xhr, status) {
			layer.msg('获取后台用户额度数据失败', {icon : 2, time : 1000});
		}
	);

}

//插入后台用户额度数据
function insert_ul_credit(list) {
	$(".list_message").remove();
	var ulHTML = '';
	$.each(list, function(i, item) {
		var member = item.member;
		var credits = item.credits;
		//用户额度的详情列表
		var detailHmtl = "<tr class='aftersales-detail display'><td colspan='15' style='padding:0'>";
		//赋予分销商类型名称
		var comsumerTypeName = "";
		switch (member.comsumerType){
			case 2:
				comsumerTypeName = "合营分销商";
				break;
			case 3:
				comsumerTypeName = "内部分销商";
				break;
			default:
				comsumerTypeName = "---";
				break;
		}

		//写入用户额度
		if(credits[0].limitState == 3){
			ulHTML+="<tr class='list_message' id='list_message_1'>"+
				"<td class='add_list_down' style='width:3%'>"+
				"<span onclick='AfterDetail(1,this)'>+</span>"+
				"<span onclick='AfterDetail(2,this)' id='span_minus' style='display: none'>-</span>"+
				"</td>"+
				"<td style='width:8%'>"+comsumerTypeName+"</td>"+
				"<td style='width:9%'>"+member.email+"</td>"+
				"<td style='width:5%'>"+deal_with_illegal_value(member.realName)+"</td>"+
				"<td style='width:9%'>"+deal_with_illegal_value(member.telphone)+"</td>"+
				"<td style='width:6%'>"+parseFloat(credits[0].creditLimit).toFixed(2)+"</td>"+
				"<td style='width:8%'>"+parseFloat(credits[0].usedAmount).toFixed(2)+"</td>"+
				"<td style='width:6%'>"+(parseFloat(credits[0].creditLimit)-parseFloat(credits[0].usedAmount)).toFixed(2)+"</td>"+
				"<td style='width:8%'>"+deal_with_illegal_value(credits[0].startTimeStr)+"</td>"+
				"<td style='width:8%'>"+(credits[0].redit==1?deal_with_illegal_value(credits[0].endTimeStr):"---")+"</td>"+
				"<td style='width:6%'>"+(credits[0].limitState==1?'待使用':(credits[0].limitState==2?'使用中':'已失效'))+"</td>"+
				"<td style='width:6%'>"+(credits[0].isFinished?'是':'否')+"</td>"+
				"<td style='width:5%'>"+deal_with_illegal_value(credits[0].createuser)+"</td>"+
				"<td style='width:13%'>"+
				"<input type='hidden' name='redit' value='"+credits[0].redit+"'/>"+
				"<input type='hidden' name='comsumerType' value='"+member.comsumerType+"'/>"+
				"<input type='hidden' name='email' value='"+member.email+"'/>"+
				"<input type='hidden' name='id' value='"+credits[0].id+"'/>"+
				"<span class='aftersales-log'>操作日志</span>"+
				"</td></tr>";
		}else{
			ulHTML+="<tr class='list_message' id='list_message_1'>"+
				"<td class='add_list_down' style='width:3%'>"+
				"<span onclick='AfterDetail(1,this)'>+</span>"+
				"<span onclick='AfterDetail(2,this)' id='span_minus' style='display: none'>-</span>"+
				"</td>"+
				"<td style='width:8%'>"+comsumerTypeName+"</td>"+
				"<td style='width:9%'>"+member.email+"</td>"+
				"<td style='width:5%'>"+deal_with_illegal_value(member.realName)+"</td>"+
				"<td style='width:9%'>"+deal_with_illegal_value(member.telphone)+"</td>"+
				"<td style='width:6%'>"+parseFloat(credits[0].creditLimit).toFixed(2)+"</td>"+
				"<td style='width:8%'>"+parseFloat(credits[0].usedAmount).toFixed(2)+"</td>"+
				"<td style='width:6%'>"+(parseFloat(credits[0].creditLimit)-parseFloat(credits[0].usedAmount)).toFixed(2)+"</td>"+
				"<td style='width:8%'>"+deal_with_illegal_value(credits[0].startTimeStr)+"</td>"+
				"<td style='width:8%'>"+(credits[0].redit==1?deal_with_illegal_value(credits[0].endTimeStr):"---")+"</td>"+
				"<td style='width:6%'>"+(credits[0].limitState==1?'待使用':(credits[0].limitState==2?'使用中':'已失效'))+"</td>"+
				"<td style='width:6%'>"+(credits[0].isFinished?'是':'否')+"</td>"+
				"<td style='width:5%'>"+deal_with_illegal_value(credits[0].createuser)+"</td>"+
				"<td style='width:13%'>"+
				"<input type='hidden' name='redit' value='"+credits[0].redit+"'/>"+
				"<input type='hidden' name='comsumerType' value='"+member.comsumerType+"'/>"+
				"<input type='hidden' name='email' value='"+member.email+"'/>"+
				"<input type='hidden' name='id' value='"+credits[0].id+"'/>"+
				"<b class='credit-change'> " +
				"<span style='cursor: pointer' class='modifybtn'>修改</span> " +
				"<span style='display: none;'>保存</span> " +
				"</b> " +
					//"<b class='role-forbidden' tag='"+list[i].isactive+"'><span class='forbidden'>"+getState(list[i])+"</span></b> " +
				"<b style='cursor: pointer' class='credit-forbidden' tag='"+credits[0].isActivated+"'>" +
				"<span class='forbidden'>"+getState(credits[0])+"</span></b> " +
				"<span class='aftersales-log'>操作日志</span>"+
				"</td></tr>";
		}
		//循环写入用户额度详情
		if(credits.length>1){
			for(var j=1;j<credits.length;j++){
				detailHmtl+= "<ul class='list_message list-bgc public_ul' id='list_message_1'>"+
					"<li style='width:3%'></li>"+
					"<li style='width:8%'></li>"+
					"<li style='width:9%'></li>"+
					"<li style='width:5%'></li>"+
					"<li style='width:9%'></li>"+
					"<li style='width:6%'>"+parseFloat(credits[j].creditLimit).toFixed(2)+"</li>"+
					"<li style='width:8%'>"+parseFloat(credits[j].usedAmount).toFixed(2)+"</li>"+
					"<li style='width:6%'>"+(parseFloat(credits[j].creditLimit)-parseFloat(credits[j].usedAmount)).toFixed(2)+"</li>"+
					"<li style='width:8%'>"+deal_with_illegal_value(credits[j].startTimeStr)+"</li>"+
					"<li style='width:8%'>"+deal_with_illegal_value(credits[j].endTimeStr)+"</li>"+
					"<li style='width:6%'>"+(credits[j].limitState==1?'待使用':(credits[j].limitState==2?'使用中':'已失效'))+"</li>"+
					"<li style='width:6%'>"+(credits[j].isFinished?'是':'否')+"</li>"+
					"<li style='width:5%'>"+deal_with_illegal_value(credits[j].createuser)+"</li>"+
					"<li style='width:13%'></li></ul>";
			}
		}
		ulHTML+=detailHmtl+"</rd></tr>";
	});
	$("tbody.list_content").prepend(ulHTML);
}

//根据不同的分销商类型动态加载数据
function changeComsumerType(obj){
	$('.comsumerType').find('input[type=button]').removeClass('cumsumers_cho');
	$(obj).addClass('cumsumers_cho');

	$("#comsumerType").val($(obj).attr("eg"));
	init_admin(2, layer, laypage);
}

//后台分销商用户额度信息导出
function exportCredit() {
	var headLi = $(".list_title th");
	var headName = [];
	$.each(headLi, function (i, li) {
		var name = $(li).attr("name");
		if (name != undefined && name != "" && name != null) {
			headName.push("header=" + name);
		}
	});
	headName.push("search=" + $("#searchInput").val().trim());
	headName.push("sregdate=" + $("#seachTime0").val().trim());
	headName.push("eregdate=" + $("#seachTime1").val().trim());
	headName.push("limiteState="+ ($("#limiteStatus")!=undefined?$("#limiteStatus").val():""));
	headName.push("comsumerType=" + $("#comsumerTypeOfCredit").val());
	headName.push("redit=" + $("#redit").val());
	if (headName.length > 0) {
		window.location.href = "/member/exportCredit?" + headName.join("&");
	}
}

//初始化分页栏
function init_credit_pagination(page) {
	if ($("#credit_pagination")[0] != undefined) {
		$("#credit_pagination").empty();
		laypage({
			cont: 'credit_pagination',
			pages: page.totalPage,
			curr: page.currPage,
			groups: 5,
			skin: '#55ccc8',
			first: '首页',
			last: '尾页',
			prev: '上一页',
			next: '下一页',
			skip: true,
			jump: function (obj, first) {
				//first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
				if (!first) {
					ajax_get_credit(obj.curr);
				}
			}
		});
	}
}
//填写用户邮箱，获得用户其他信息
function getMemberInfo(obj){
	var email = $(obj).val();
	if(email != undefined && email != null && email.length>0){
		ajax_post("../member/gaininfo", JSON.stringify({email:email}), "application/json",
			function(response) {
				if (response.suc) {
					var info = response.info;
					var config = JSON.parse(info.creditConfig);
					if(config){
						if(!config.hasLongCredit){
							$("select[name='addRedit']").children().eq(1).css("display","none");
							$("select[name='addRedit']").children().eq(0).attr("selected","selected");
						}else{
							$("select[name='addRedit']").children().eq(1).css("display","");
						}
						if(!config.hasShortCredit){
							$("select[name='addRedit']").children().eq(0).css("display","none");
						}else{
							$("select[name='addRedit']").children().eq(0).css("display","");
						}
						if(!config.hasLongCredit&&!config.hasShortCredit){
							layer.msg("该分销商没有信用额度，请选择其他分销商", {icon : 0, time : 1000},function(){$(obj).val("");});
						}else{
							$("input[name='addUserName']").val(info.realName);
						}
					}else{
						layer.msg("该类型分销商没有信用额度配置，不能设置信用额度", {icon : 0, time : 1000},function(){$(obj).val("");}	);
					}
					/*var comsumerType = response.info.comsumerType;
					if(comsumerType == '1'){
						layer.msg("该分销商没有信用额度，请选择其他分销商", {icon : 0, time : 1000},function(){$(obj).val("");});
					}else if(comsumerType == '2'){
						$("select[name='addRedit']").children().eq(1).css("display","");
						$("input[name='addUserName']").val(info.realName);
					}else{
						$("select[name='addRedit']").children().eq(0).attr("selected","selected");
						$("select[name='addRedit']").children().eq(1).css("display","none");
						$("input[name='addUserName']").val(info.realName);
						$("input[name='addEndTime']").removeAttr('disabled');
					}*/
				} else {
					layer.msg(response.msg, {icon : 2, time : 1000});
				}
			},
			function(xhr, status) {
				layer.msg('获取用户信息失败', {icon : 2, time : 1000});
			}
		);
	}
}

function getState(item){
	return item.isActivated == true ? "禁用" : "启用";
}

//额度的禁用和启用
$('.content-R-fifteenth').on('click', '.forbidden', function () {
	var creditId = $(this).parent().parent().find("input[name='id']").val();
	var flag = $(this).parent().attr("tag");
	var $event = $(this);
	//alert(""+creditId+" "+flag);
	//layer.msg('确定要禁止【用户名】的额度吗', {icon : 2, time : 1000});
	var params = {
		id : creditId,
		isActivated : flag
	};
	layer.confirm("您确定"+$(this).text()+"该额度吗？", {icon: 3},
		//i和currdom分别为当前层索引、当前层DOM对象
		function(i, currdom) {
			layer.close(i);
			ajax_post("../member/updIsActivated",JSON.stringify(params),"application/json",
				function(data){
					if(data.suc){
						layer.msg("该额度已被"+$event.text()+"。", {icon : 1, time : 2000});
						if(params.isActivated == "true"){
							$event.parent().attr("tag","false").find("span").text("启用");
						}else if(params.isActivated == "false"){
							$event.parent().attr("tag","true").find("span").text("禁用");
						}
					}else{
						window.location.href = "login.html";
					}
				});
		}
	);
})

//设置用户额度
$('.content-R-fifteenth').on('click', '.modifybtn', function () {
	var obj = $(this);
	var creditId = $(this).parent().parent().find("input[name='id']").val();
	var comsumerType = $(this).parent().parent().find("input[name='comsumerType']").val();
	var name = $($(this).parent().parent().siblings()[3]).html();
	var reditType = $(this).parent().parent().find("input[name='redit']").val();
	$("input[name='setName']").val(name);
	layer.open({
		type: 1,
		title: "额度设置",
		area: ['330px', '430px'],
		content: $('.aftersales-audit-pop'),
		btn: ['确定', '取消'],
		shadeClose: true,
		success:function(){
			if(reditType && reditType == 1){
				$("select[name='setRedit']").children().eq(1).css("display","none");
			}else{
				$("select[name='setRedit']").children().eq(0).css("display","none");
			}
			//获取额度信息
			ajax_post("../member/getCreditInfo", JSON.stringify({id:creditId}), "application/json",
				function(response) {
					if (response.suc) {
						var credit = response.credit;
						if(credit != undefined && credit != null && credit.credits.length>0){
							var redit = credit.credits[0].redit;
							$("input[name='setId']").val(credit.credits[0].id);
							$("input[name='setAudioCode']").val(credit.credits[0].auditCode);
							$("input[name='setEmail']").val(credit.credits[0].email);
							$("select[name='setRedit']").find("option[value='"+redit+"']").attr("selected","selected");
							$("input[name='setCreditLimit']").val(credit.credits[0].creditLimit);
							$("input[name='setStartTime']").val(credit.credits[0].startTimeStr);
							$(".setStartTime").val(credit.credits[0].startTimeStr);
							$(".setEndTime").val(credit.credits[0].endTimeStr == null ? "无期限" : credit.credits[0].endTimeStr);
							$("input[name='setAudioCode']").attr('disabled','disabled');
							$("input[name='setEmail']").attr('disabled','disabled');
							$("input[name='setName']").attr('disabled','disabled');
							$(".setStartTime").attr('disabled','disabled');
							$(".setEndTime").attr('disabled','disabled');
						}
					} else {
						layer.msg(response.msg, {icon : 2, time : 1000});
					}
				},
				function(xhr, status) {
					layer.msg('获取额度信息失败', {icon : 2, time : 1000});
				}
			);
		},
		yes:function(){
			var id = $("input[name='setId']").val();
			var creditLimit = $("input[name='setCreditLimit']").val();
			//校验审核单号
			//if(auditCode=='' || auditCode == undefined || auditCode.length<=0){
			//	layer.msg('审核单号不能为空', {icon : 2, time : 1000});
			//	return false;
			//}
			var reg = /^\+?[1-9]\d*$/;
			//校验用户额度
			if(creditLimit=='' || creditLimit == undefined || !reg.test(creditLimit)){
				layer.msg('用户额度必须为大于0的整数', {icon : 2, time : 1000});
				return false;
			}
			//校验开始时间
			//if(startTime=='' || startTime == undefined || startTime.length<=0){
			//	layer.msg('开始时间不能为空', {icon : 2, time : 1000});
			//	return false;
			//}
			//校验失效时间
			//if((endTime=='' || endTime == undefined || endTime.length<=0)&&redit!=2&&redit!='2'){
			//	layer.msg('失效时间不能为空', {icon : 2, time : 1000});
			//	return false;
			//}
			//校验失效时间不能小于开始时间
			//if(new Date(endTime)<new Date(startTime) && redit !=2 && redit !='2'){
			//	layer.msg('失效时间不能小于开始时间', {icon : 2, time : 1000});
			//	return false;
			//}
			var params ={
				id:id,
				creditLimit:creditLimit
			}
			//保存设置数据
			ajax_post("../member/updateCredit", JSON.stringify(params), "application/json",
				function(response) {
					if (response.suc) {
						layer.msg(response.msg, {icon : 1, time : 1000},function(){ajax_get_credit(1);});
					} else {
						layer.msg(response.msg, {icon : 2, time : 2000},function(){layer.closeAll();});
					}
				},
				function(xhr, status) {
					layer.msg('获取额度信息失败', {icon : 2, time : 1000});
				}
			);
			layer.closeAll();
		}
	});
});

//显示操作日志
$('.content-R-fifteenth').on('click', '.aftersales-log', function () {
	var email = $(this).parent().find("input[name='email']").val();
	layer.open({
		type: 1,
		title: "操作日志",
		area: ['404px', '400px'],
		content: $('.aftersales-log-lit'),
		shadeClose: true,
		success:function(){
			$('.aftersales-log-lit').empty();
			//展示操作日志数据
			ajax_post("../member/getCreditOperRecord", JSON.stringify({"email":email,"operateType":$("#redit").val()}), "application/json",
				function(response) {
					if (response.suc) {
						var records = response.result;
						if(records != undefined && records.length>0){
							var ulHtml = "";
							for(var i=0;i<records.length;i++){
								ulHtml+="<ul class='aftersales_lit'>"+
									"<li>"+records[i].operatorTimeStr+"</li>"+
									"<li>"+records[i].comments+"</li>"+
									"</ul>";
							}
							$('.aftersales-log-lit').prepend(ulHtml);
						}
					} else {
						layer.msg(response.msg, {icon : 2, time : 1000});
					}
				},
				function(xhr, status) {
					layer.msg('获取操作日志失败', {icon : 2, time : 1000});
				}
			);
		}

	});
});

//添加用户额度
$('.content-R-fifteenth').on('click', '.aftersales-addQuota', function () {
	var creditHtml = '<div class="aftersales-quota" >												'+		
		'	<div class="recharge-button">                                                                                       '+
		'			<span>                                                                                                      '+
		'				<p><b>*</b>分销商账户：</p><input type="text" name="addUserEmail" onblur="getMemberInfo(this)">         '+
		'			</span>                                                                                                     '+
		'			<span>                                                                                                      '+
		'				<p>分销商姓名：</p><p><input type="text" name="addUserName" disabled="disabled"/></p>                   '+
		'			</span>                                                                                                     '+
		'		<p></p>                                                                                                         '+
		'			<span>                                                                                                      '+
		'				<p><b>*</b>审核单据号：</p><input type="text" name="addAudioCode">                                      '+
		'			</span>                                                                                                     '+
		'			<span>                                                                                                      '+
		'				<p><b>*</b>额度类型：</p><select name="addRedit" onchange="addReditChange(this)">                       '+
		'				<option value="1" selected="selected">临时额度</option>                                                 '+
		'				<option value="2">永久额度</option>                                                                     '+
		'			</select>                                                                                                   '+
		'			</span>                                                                                                     '+
		'			<span><p><b>*</b>设置额度：</p><input type="text" name="addCreditLimit">                                    '+
		'			</span>                                                                                                     '+
		'			<span>                                                                                                      '+
		'				<ul>                                                                                                    '+
		'					<li>                                                                                                '+
		'						<p><b>*</b>有效时间：</p>                                                                       '+
		'						<div class="valid-time">                                                                        '+
		'							<input name="addStartTime" placeholder="年/月/日 时分秒"                                    '+
		'									onclick=\'laydate({istime: true,min:laydate.now(), format: "YYYY-MM-DD hh:mm:ss"})\' > '+
		'							<div>至</div>                                                                               '+
		'								<input name="addEndTime" placeholder="年/月/日 时分秒"                                  '+
		'								onclick=\'laydate({istime: true, min:laydate.now(),format: "YYYY-MM-DD hh:mm:ss"})\'>     '+
		'						</div>                                                                                          '+
		'					</li>                                                                                               '+
		'				</ul>                                                                                                   '+
		'			</span>                                                                                                     '+
		'	</div>                                                                                                              '+
		'	<div class="y-recharge" style="display: none">                                                                      '+
		'		<p>是否需要寄回商品：                                                                                           '+
		'			<span><input type="radio" name="return">是</span>                                                           '+
		'			<span><input type="radio" name="return">否</span>                                                           '+
		'		</p>                                                                                                            '+
		'	</div>                                                                                                              '+
		'	<div class="n-recharge" style="display: none">                                                                      '+
		'		<p>请填写不通过审核的原因：</p>                                                                                 '+
		'		<textarea placeholder="不超过500字"></textarea>                                                                 '+
		'	</div>                                                                                                              '+
		'</div>                                                                                                                 ';
					
	layer.open({
		type: 1,
		title: "添加额度",
		area: ['330px', '430px'],
		content: creditHtml,
		btn: ['确定', '取消'],
		shadeClose: true,
		end:function(){
			addClear();
		},
		yes:function(){
			var email= $("input[name='addUserEmail']").val();
			var auditCode= $("input[name='addAudioCode']").val();
			var redit= $("select[name='addRedit']").val();
			var creditLimit= $("input[name='addCreditLimit']").val();
			var startTime= $("input[name='addStartTime']").val();
			var endTime= $("input[name='addEndTime']").val();
			//数据校验
			//校验分销商账号
			if(email=='' || email == undefined || email.length<=0){
				layer.msg('用户账号不能为空', {icon : 2, time : 1000});
				return false;
			}
			//校验审核单号
			if(auditCode=='' || auditCode == undefined || auditCode.length<=0){
				layer.msg('审核单号不能为空', {icon : 2, time : 1000});
				return false;
			}
			//校验额度类型
			if(redit=='' || redit == undefined || redit == null){
				layer.msg('额度类型不能为空', {icon : 2, time : 1000});
				return false;
			}
			var reg = /^\+?[1-9]\d*$/;
			//校验用户额度
			if(creditLimit=='' || creditLimit == undefined || !reg.test(creditLimit)){
				layer.msg('用户额度必须为大于0的整数', {icon : 2, time : 1000});
				return false;
			}
			//校验开始时间
			if(startTime=='' || startTime == undefined || startTime.length<=0){
				layer.msg('开始时间不能为空', {icon : 2, time : 1000});
				return false;
			}
			//校验失效时间
			if((endTime=='' || endTime == undefined || endTime.length<=0) && redit !=2 && redit !='2'){
				layer.msg('失效时间不能为空', {icon : 2, time : 1000});
				return false;
			}

			//校验失效时间不能小于开始时间
			if(new Date(endTime)<new Date(startTime) && redit !=2 && redit !='2'){
				layer.msg('失效时间不能小于开始时间', {icon : 2, time : 1000});
				return false;
			}

			var params = {
				email: email,
				auditCode: auditCode,
				redit: redit,
				creditLimit: creditLimit,
				startTimeStr: startTime,
				endTimeStr: endTime
			};
			//保存添加数据
			ajax_post("../member/addCredit", JSON.stringify(params), "application/json",
				function(response) {
					if (response.suc) {
						layer.msg(response.msg, {icon : 1, time : 1000},
							function(){
								layer.closeAll();
								ajax_get_credit(1);
							});
					} else {
						layer.msg(response.msg, {icon : 2, time : 3000},function(){layer.closeAll();});
					}
					addClear();
				},
				function(xhr, status) {
					layer.msg('添加用户额度失败', {icon : 2, time : 1000});
					layer.closeAll();
				}
			);

		}
	});
});

//清空添加框中的数据
function addClear(){
	//清空账户
	$("input[name='addUserEmail']").val("");
	$("input[name='addUserName']").val("");
	//恢复下拉选项
	$("select[name='addRedit']").children().eq(1).css("display","");
	$("input[name='addAudioCode']").val("");
	$("select[name='addRedit']").val("");
	$("input[name='addCreditLimit']").val("");
	$("input[name='addStartTime']").val("");
	$("input[name='addEndTime']").val("");
	$("input[name='addEndTime']").removeAttr("disabled");
}

//修改额度，额度类型修改，切换失效时间是否可用
function setReditChange(obj){
	var redit = $(obj).val();
	//将失效时间置灰
	if(redit == '2'||redit == 2){
		$("input[name='setEndTime']").attr('disabled','disabled');
		$("input[name='setEndTime']").val('');
	}else{
		$("input[name='setEndTime']").removeAttr('disabled');
	}
}

//添加额度，额度类型的修改,切换失效时间是否可用
function addReditChange(obj){
	var redit = $(obj).val();
	//将失效时间置灰
	if(redit == '2'||redit == 2){
		$("input[name='addEndTime']").attr('disabled','disabled');
		$("input[name='addEndTime']").val('');
	}else{
		$("input[name='addEndTime']").removeAttr('disabled');
	}
}

//详情展示
function AfterDetail(param, e) {
	$('.list_content').removeClass('list_content_cho');
//  $(e).parents('.list_content').next().addClass('list_content_cho');
	$(e).parent().parent().next().addClass('list_content_cho');
	if (param == 1) {
		$(e).css("display", "none");
		$(e).next().css("display", "inline-block");
		$(e).parent().parent().next().css("display", "table-row");
	} else {
		$(e).css("display", "none");
		$(e).prev().css("display", "inline-block");
		$(e).parent().parent().next().css("display", "none");
	}

}

function AfterSalesPost(change){
	if(change == 'Ushow'){
		$('.after-postage-num,.after-edit').hide();
		$('.after-postage-input,.after-save').show();
	}
	else if(change == 'Uhide'){
		$('.after-postage-num,.after-edit').show();
		$('.after-postage-input,.after-save').hide();
	}
	else if(change == 'Uchange'){
		$('.address-return-address,.after-addressedit').hide();
		$('.address-return-input,.after-addresssave').show();
	}
	else if(change == 'Uback'){
		$('.address-return-address,.after-addressedit').show();
		$('.address-return-input,.after-addresssave').hide();
	}
}

function rechargebox(convert){
	if(convert == 'Ushow'){
		$('.y-recharge').show();
		$('.n-recharge').hide();
	}
	else if(convert == 'Uhide'){
		$('.y-recharge').hide();
		$('.n-recharge').show();
	}
}

//不同额度类型的切换展示
function toggleBox(obj,goal){
	$(goal).parent().children().removeClass('btn-cho');
	$(goal).addClass('btn-cho');
	if(obj == '1'){
		//展示临时额度
		$("#redit").val(1);
		ajax_get_credit(1);
	}else if(obj == '2'){
		//暂时永久额度
		$("#redit").val(2);
		ajax_get_credit(1);
	}
}
