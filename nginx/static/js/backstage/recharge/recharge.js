var layer = undefined;
var laypage = undefined;
var sign = undefined;

function postModify(data,callback){
	ajax_post("/member/audit", JSON.stringify(data), "application/json",
		function(data) {
			callback();
			$(".list_content").next().hide();
			$(".recharge_black").hide();
			$(".check_fail").hide();
			$(".check_suc").hide();
			$(".check_abnormal").hide();
			$('.check_all').show();
			if (data.success) {
				layer.msg("审核申请成功！", {icon: 6, time: 2000});
			} else {
				layer.msg("审核失败，请稍后重试！", {icon: 6, time: 2000});
			}
		},
		function(xhr, status) {
			layer.msg("审核失败，请稍后重试！", {icon: 6, time: 2000});
		}
	);
	$('.layui-layer-shade').click();
}

var tag = undefined;
//var htmlCode;

//充值审核部分弹出框
function operationShow(obj, check,auditState) {
	var t = $(obj).attr("tag");
	var state = $(obj).attr("state");
	var htmls = "<div class='recharge_check' style='display: block;'>"
		+"<ul><li class='check_all' style='display: block;margin-top: 40px;'>"
		+"<ul><li>审核之前请确认汇款是否已完成！</li>"
		+"<li>"
		+"<input type='button' value='审核不通过' onclick=\"operationShow(this,'fail')\">"
		+"<input type='button' value='审核异常' onclick=\"operationShow(this,'abnormal')\">"
		+"<input type='button' value='审核通过' onclick=\"operationShow(this,'suc')\">"
		+"</li></ul></li>"
		+"<li class='check_fail check_pages' style='display: none;'>"
		+"<ul><li><span>审核理由：</span><span><b class='red'>*</b>备注：</span></li>"
		+"<li>"
		+"<select name=''>"
		+"<option value=''>请选择</option>"
		+"<option value='无效申请'>无效申请</option>"
		+"</select>"
		+"<textarea name='' cols='30' rows='10'></textarea>"
		+"</li></ul>"
		+"<div class='audit_'><input type='hidden' value='1'><input type='button' value='提交' atr='modify' style='cursor: pointer;'></div></li>"
		+"<li class='check_abnormal check_pages' style='display: none;'><ul><li><span>审核理由：</span><span><b class='red'>*</b>备注：</span></li>"
		+"<li>"
		+"<select name=''>"
		+"<option value=''>请选择</option>"
		+"<option value='用户提交汇款金额与实收金额不符'>用户提交汇款金额与实收金额不符</option>"
		+"<option value='用户提交汇款信息与实际信息不符'>用户提交汇款信息与实际信息不符</option>"
		+"</select>"
		+"<textarea name='' id='' cols='30' rows='10'></textarea>"
		+"</li></ul>"
		+"<div class='audit_'><input type='hidden' value='3'><input type='button' value='提交' atr='modify' style='cursor: pointer;'></div></li>"
		+"<li class='check_suc check_pages' style='display: none;'><ul><li><span><b class='red'>*</b>到账时间：</span><span><b class='red'>*</b>实收金额：</span><span><b class='red'>*</b>审核备注：</span></li>"
		+"<li>"
		+"<input placeholder='开始日期' id='actualTime' name='actualTime' value = '"+geTime()+"' onclick=\"laydate({istime: true, format: 'YYYY-MM-DD hh:mm:ss'})\" tag='validate'>"
		+"<input type='text' name='actualAmount'><input type='text'  name='remark'></li></ul>"
		+"<div id='operate'>";
		if(auditState == 3){
			htmls += "<input type='hidden' value=''><input type='button'  atr='sure' value='确定'><input type='hidden' value='2'>";
		}else{
			htmls += "<input type='hidden' value='2'><input type='button'  atr='sure' value='确定'><input type='hidden' value=''>";
		}
		htmls += "</div></li></ul></div>";
	if(t){
		tag = t;
	}
	if (check == 'Ushow') {
		layer.open({
				type:1,
				title:'审核',
				btn:false,
				shadeClose: true,
				content:htmls,
				area:['308px','306px'],
				move: false,
				success:function(){
					$('.check_all ul li:eq(1) input:eq(1)').show();
					if(state == 3){
						$('.check_all ul li:eq(1) input:eq(1)').hide();
					}
					$("#operate input[atr='sure']").click(function(){
						var time = $(this).parent().prev().find("li input:eq(0)").val();
						if (time == undefined || time == '') {
							layer.msg('请输入到账时间', {icon : 2, time : 2000});
							return;
						}
						var amount = $(this).parent().prev().find("li input:eq(1)").val();
						var maxAmount = $("input[name = 'actualAmount']").data("max");
						if (amount == undefined || isNaN(amount) || amount == '') {
							layer.msg('请输入有效金额', {icon : 2, time : 2000});
							return;
						}else if(!checkMoney(amount)){
							layer.msg('输入金额格式不对',{icon:2,time:2000});
							return; 
						}
						var params = {
							id : tag,
							auditState : $(this).prev().val(),
							actualAmount : amount,
							actualTime : time,
							reviewState : $(this).next().val()
						};
						if(auditState == 3){
							params.reAuditRemark = $("input[name=remark]").val().trim();
							if(!params.reAuditRemark){
								layer.msg("复审备注不能为空!",{icon:2,time:2000});
								return;
							}
							postModify(params,function() {
								post_review_audit();
							});
						}else{
							params.auditRemark = $("input[name=remark]").val().trim();
							if(!params.auditRemark){
								layer.msg("初审备注不能为空!",{icon:2,time:2000});
								return;
							}
							postModify(params,function() {
								post_initial_audit();
							});
						}
					});
					//事件绑定
					$("#operate input[atr='close']").click(function(){
						$(".list_content").next().hide();
						$(".recharge_black").hide();
						$(".check_suc").hide();
					});
					//事件绑定
					$("div.audit_ input[atr='modify']").click(function(){
						var auditStates = $(this).prev().val();
						var auditReasons  = $(this).parent().prev().find("li select").val();
						var auditRemark =  $(this).parent().prev().find("li textarea").val().trim();
						if(auditStates == 1 || auditStates ==3){
							if(!auditReasons){
								layer.msg("请选择审核理由!",{icon:2,time:2000});
								return;
							}
							if(!auditRemark){
								layer.msg("描述信息不能为空!",{icon:2,time:2000});
								return;
							}
						}
						var params = {
							id : tag,
							auditReasons : auditReasons,
						};
						if (auditState == 3) {
							params.reAuditRemark = auditRemark;
							params.reviewState = auditStates;
							postModify(params,function() {
								post_review_audit();
							});
						} else {
							params.auditState = auditStates;
							params.auditRemark = auditRemark;
							postModify(params,function() {
								post_initial_audit();
							});
						}
					});
				}
		})
		var transferAmount ;
		var actualAmount;
		switch(sign){
			case 2:
				transferAmount = $(obj).parent().parent().children().eq(8).text();
				$("input[name = 'actualAmount']").val(transferAmount);
				break;
			case 3:
				transferAmount = $(obj).parent().parent().children().eq(9).text();
				actualAmount = $(obj).parent().parent().children().eq(10).text();
				$("input[name = 'actualAmount']").val(actualAmount);
				break;
		}
		$("input[name = 'actualAmount']").data("max",transferAmount);
	}else if(check == 'Uhide') {
		$(".recharge_check").hide();
		$('.check_pages').hide();
		$('.check_all').hide();
		$('.recharge_black').hide();
	}else if (check == 'fail') {
		$(obj).parent().parent().parent().hide();
		$(".check_fail").show();
	} else if (check == 'abnormal') {
		$(obj).parent().parent().parent().hide();
		$(".check_abnormal").show();
	} else if (check == 'suc') {
		$(obj).parent().parent().parent().hide();
		$(".check_suc").show();
	}
}


function infoShow(obj){
	var htmlCode;
	$(".recharge_operation_li1").siblings().remove();
		ajax_get("/member/queryOp?d="+$(obj).attr("tag"),"","application/json",
			function(data){
				data && data.length > 0?htmlCode = showOpData(data):htmlCode = "<span style='text-align: center;line-height: 190px '>暂无操作记录！</span>";
				layer.open({
					type: 1,
					title: '操作记录',
					shadeClose: true,
					content: htmlCode,
					move: false,
					area: ['460px','250px']
				});
			},
			function(xhr, status){});
}

function showOpData(data){
	var htmlCode = "";
	$.each(data, function (i,item) {
		htmlCode += 	"<ul class=\"recharge_operation_li2\">" +
						"<li>"+item.opDateStr+"</li>" +
						"<li>"+item.opdesc+"</li>" +
						"</ul>" ;
	});
	return htmlCode
}
//线上充值tab
$('body').on('click','.recharge_ul li',function(){
	$(this).addClass('recharge_current').siblings().removeClass('recharge_current');
	var tabid = $(this).attr("tab");
	if (tabid == 2) {
		$("#withdraw").hide();
		$("#recharge_offline").hide();
        $("#recharge_online").show();
		var grid = new BbcGrid();
		grid.initTable($("#recharge_online_record"),onlineSetting());
	} else if (tabid == 1) {
		$("#withdraw").hide();
		$("#recharge_offline").show();
		$("#recharge_online").hide();
	} else if (tabid == 3) {
		$("#withdraw").show();
		$("#recharge_offline").hide();
		$("#recharge_online").hide();
		var grid = new BbcGrid();
		grid.initTable($("#withdraw_record"),withdrawSetting());
    }
});

/**
 * 审核提现申请
 * @Author LSL on 2016-09-21 10:12:45
 */
function audit_withdraw_apply(applyId,accountId) {
    layer.confirm('审核之前请确认转账是否已完成！', {
        btn: ['审核通过', '审核不通过'] ,//按钮
		btn2 : function () {
			layer.closeAll();
			var audit_not_pass_content =
				'<div class="presentAudit_pop_false">' +
				'<p>' +
				'<span><i class="red">*</i>&nbsp;审核理由：</span>' +
				'<select>' +
				'<option value="" selected="selected">请选择</option>' +
				'<option value="账户异常">账户异常</option>' +
				'<option value="无法转账">无法转账</option>' +
				'</select>' +
				'</p>' +
				'<p>' +
				'<span><i class="red">*</i>&nbsp;审核备注：</span>' +
				'<textarea type="text" placeholder="100字以内"></textarea>' +
				'</p>' +
				'</div>';
			layer.open({
				type: 1,
				skin: 'layui-layer-rim', //加上边框
				title: '审核不通过',
				btn: ['提交', '取消'],
				area: ['420px', '300px'], //宽高
				content: audit_not_pass_content,
				yes: function() {
					var $select = $(".presentAudit_pop_false select");
					var $textarea = $(".presentAudit_pop_false textarea");
					if ($select.eq(0).val().trim() == "") {
						layer.msg("请选择【审核理由】", {icon: 0, time: 3000});
						return false;
					}
					if ($textarea.eq(0).val().trim() == "") {
						layer.msg("请输入【审核备注】", {icon: 0, time: 3000});
						return false;
					} else if($textarea.eq(0).val().trim().length > 100) {
						layer.msg("【审核备注】输入不能超过100位长度", {icon: 0, time: 3000});
						return false;
					}
					save_withdraw_apply_audit_result({
						applyId: applyId,
						accountId:accountId,
						auditState: 1,
						auditReason: $select.eq(0).val().trim(),
						auditRemark: $textarea.eq(0).val().trim()
					});
				}
			});
		}
    }, function () {
        layer.closeAll();
        var audit_pass_content =
            '<div class="presentAudit_pop">' +
            '<p>' +
            '<span><i class="red">*</i>&nbsp;转账金额：</span>' +
            '<input type="text" placeholder="请输入转账金额">' +
            '</p>' +
            '<p>' +
            '<span><i class="red">*</i>&nbsp;转账流水号：</span>' +
            '<input type="text" placeholder="请输入转账流水号">' +
            '</p>' +
            '<p>' +
            '<span><i class="red">*</i>&nbsp;转账时间：</span>' +
            '<input type="text" placeholder="请输入转账时间" onclick="laydate({istime: true, format: \'YYYY-MM-DD hh:mm:ss\'})">' +
            '</p>' +
            '<p>' +
            '<span><i class="red">*</i>&nbsp;审核备注：</span>' +
            '<textarea type="text" placeholder="100字以内"></textarea>' +
            '</p>' +
            '</div>';
        layer.open({
            type: 1,
            skin: 'layui-layer-rim', //加上边框
            title: '审核通过',
            area: ['420px', '380px'], //宽高
            btn: ['提交', '取消'],
            content: audit_pass_content,
            yes: function() {
                var $input = $(".presentAudit_pop input");
                var $textarea = $(".presentAudit_pop textarea");
                var decimal_regexp = /^((\d{1})|([1-9]{1}\d+)|(\d{1}\.\d{1,2})|([1-9]{1}\d+\.\d{1,2}))$/;
                if (!decimal_regexp.test($input.eq(0).val().trim())) {
                    layer.msg("【转账金额】为大于0的整数且最多保留两位小数", {icon: 0, time: 3000});
                    return false;
                }
                if ($input.eq(1).val().trim() == "") {
                    layer.msg("请输入【转账流水号】", {icon: 0, time: 3000});
                    return false;
                }
                var date_regexp = /^([1-2]{1}\d{3})\-(([0]{1}[1-9]{1})|([1]{1}[0-2]{1}))\-(([0]{1}[1-9]{1})|([1-2]{1}\d{1})|([3]{1}[0-1]{1}))\s(([0-1]{1}\d{1})|([2]{1}[0-3]))\:([0-5]{1}\d{1})\:([0-5]{1}\d{1})$/;
                if (!date_regexp.test($input.eq(2).val().trim())) {
                    layer.msg("请输入正确的【转账时间】", {icon: 0, time: 3000});
                    return false;
                }
                //校验输入的转账时间不能大于当前时间 by huchuyin 2016-9-30
                var curTime = new Date().getTime();
                var transDate = $input.eq(2).val().trim();
                var year = transDate.substring(0,4);
                var month = transDate.substring(5,7) - 1;
                var day = transDate.substring(8,10);
                var hour = transDate.substring(11,13);
                var minute = transDate.substring(14,16);
                var second = transDate.substring(17,19);
                var transTime = new Date(year,month,day,hour,minute,second).getTime();
                if(transTime > curTime) {
                	layer.msg("转账时间不能大于当前时间！", {icon: 0, time: 3000});
                    return false;
                }
                //End by huchuyin 2016-9-30
                if ($textarea.eq(0).val().trim() == "") {
                    layer.msg("请输入【审核备注】", {icon: 0, time: 3000});
                    return false;
                } else if($textarea.eq(0).val().trim().length > 100) {
                	layer.msg("【审核备注】输入不能超过100位长度", {icon: 0, time: 3000});
                    return false;
                }
                save_withdraw_apply_audit_result({
                    applyId: applyId,
                    auditState: 2,
                    transferAmount: $input.eq(0).val().trim(),
                    transferNumber: $input.eq(1).val().trim(),
                    transferTime: $input.eq(2).val().trim(),
                    auditReason: "已确认",
                    auditRemark: $textarea.eq(0).val().trim()
                });
            }
        });
    });
}

/**
 * 保存提现申请审核结果
 * @Author LSL on 2016-09-21 10:18:50
 */
function save_withdraw_apply_audit_result(params) {
    ajax_post("/member/auditWithdraw", JSON.stringify(params), "application/json",
        function(data) {
            if (data.suc) {
                layer.closeAll();
                layer.msg("保存审核结果成功！", {icon: 1, time: 2000}, function() {
                    presentAuditQuery();
                });
            } else if (data.code == 2) {
                window.location.href = "login.html";
            } else {
                layer.alert(data.msg, {icon: 2});
            }
        },
        function(xhr, status) {
            layer.msg("保存提现申请审核结果出错，请稍后重试！", {icon: 2, time: 2000});
        }
    );
}

/**
 * 导出提现申请数据
 * @Author LSL on 2016-09-22 11:47:41
 */
function export_withdraw_apply_data() {
	var header = $("#withdraw_record").jqGrid("getGridParam", "colModel");
    var keyValue = [];
    $.each(header, function(n, node) {
        if (n > 0) {
            keyValue.push("header=" + node.name);
        }
    });
    keyValue.push("header=auditMark");
    keyValue.push("applyType=2");
    keyValue.push("search=" + $("#withdraw_search_input").val().trim());
    keyValue.push("auditState=" + $("#withdraw_auditstate_select").val());
    keyValue.push("createDate=" + $("#withdraw_applytime_select").val());
    keyValue.push("updateDate=" + $("#withdraw_audittime_select").val());
    if (keyValue.length > 0) {
        window.location.href = "/member/exportWithdraw?" + keyValue.join("&");
    }
}

function export_apply(tabid) {
	var header = $("#" + tabid).jqGrid("getGridParam", "colModel");
	var headName = [];
	$.each(header, function (i, td) {
		if (i > 0) {
			headName.push("header=" + td.name);
		}
	});
	headName.push("applyType=1");
	//在线充值标识
	if (tabid == 'recharge_online_record') {
		headName.push("isOnline=1");
		headName.push("time=" + $("#online_pay_date").val());
		headName.push("search=" + $("#onlineSearchInput").val());
		headName.push("auditState=" + $("#online_pay_status").val());
	} else {
		headName.push("receiptMode=" + $("#receipt_mode").val());
		headName.push("time=" + $("#transfer_time").val());
		headName.push("search=" + $("#searchInput").val());
		headName.push("auditState=" + $("#audit_state").val());
		headName.push("reviewState=" + $("#reaudit_state").val());
	}
	if (headName.length > 0) {
		window.location.href = "/member/exportApply?" + headName.join("&");
	}
}
/**
 * 设置提现限制
 * @Author LSL on 2016-09-22 16:07:43
 */
function setup_withdraw_limit() {
    ajax_get("/member/getCWLimit", {}, "application/json",
        function(data) {
            var limit = undefined;
            if (data.suc) {
                limit = data.msg;
            } else if (data.code == 2) {
                window.location.href = "login.html";
            } else {
                limit = {};
            }
            var limit_content =
                '<div class="presentAudit_pop">' +
                '<input type="hidden" value="' + deal_with_illegal_json(limit.id) + '"/>' +
                '<p>' +
                '<span style="width: 150px;"><i class="red">*</i>&nbsp;每月最多提现次数：</span>' +
                '<input type="text" value="' + deal_with_illegal_json(limit.permonthTimes) + '" placeholder="请输入每月最多提现次数">' +
                '</p>' +
                '<p>' +
                '<span style="width: 150px;"><i class="red">*</i>&nbsp;单次提现最低限额：</span>' +
                '<input type="text" value="' + deal_with_illegal_json(limit.pertimeLeast) + '" placeholder="请输入单次提现最低限额">' +
                '</p>' +
                '</div>';
            layer.open({
                type: 1,
                skin: 'layui-layer-rim', //加上边框
                title: '提现限制',
                area: ['420px', '240px'], //宽高
                btn: ['保存', '取消'],
                content: limit_content,
                yes: function() {
                    var $input = $(".presentAudit_pop input");
                    var int_regexp = /^[1-9]{1}[0-9]*$/;
                    var decimal_regexp = /^((\d{1})|([1-9]{1}\d+)|(\d{1}\.\d{1,2})|([1-9]{1}\d+\.\d{1,2}))$/;
                    if (!int_regexp.test($input.eq(1).val().trim())) {
                        layer.msg("请输入正确的【每月最多提现次数】", {icon: 0, time: 3000});
                        return false;
                    }
                    if (!decimal_regexp.test($input.eq(2).val().trim())) {
                        layer.msg("请输入正确的【单次提现最低限额】", {icon: 0, time: 3000});
                        return false;
                    }
                    if ($input.eq(2).val() <= 0) {
                        layer.msg("【单次提现最低限额】不能小于0!", {icon: 0, time: 3000});
                        return false;
                    }
                    var params = {
                        id: $input.eq(0).val(),
                        permonthTimes: $input.eq(1).val().trim(),
                        pertimeLeast: $input.eq(2).val().trim()
                    };
                    ajax_post("/member/saveWLimit", JSON.stringify(params), "application/json",
                        function(data) {
                            if (data.suc) {
                                layer.closeAll();
                                layer.msg("保存成功！", {icon: 1, time: 2000});
                            } else if (data.code == 2) {
                                window.location.href = "login.html";
                            } else {
                                layer.alert(data.msg, {icon: 2});
                            }
                        },
                        function(xhr, status) {
                            layer.msg("保存出错，请稍后重试！", {icon: 2, time: 2000});
                        }
                    );
                }
            });
        },
        function(xhr, status) {
            layer.msg("获取通用提现限制出错，请稍后重试！", {icon: 2, time: 2000});
        }
    );
}

///////////////////////////////////////////////////////////////////////  录入充值记录 //////////////////////////////////////////////////////////////
//获取收款方式数据
function gain_receipt_mode() {
	ajax_get("/member/getremos?"+(new Date()).getTime(), {}, undefined,
		function(data) {
			if (data.suc) {
				var optionHTML = '<option value="" selected="selected">请选择</option>';
				$.each(data.list, function(i, item) {
					optionHTML += '<option value="' + item.id + '" data-mode="' + item.account + '——' + item.payee + '">' + item.remark + '</option>';
				});
				$("#receipt_mode").empty().append(optionHTML);
				//收款方式下拉选
				$("#receipt_mode").change(function(event) {
					if (this.value != "") {
						var opt = undefined, mode = this.value;
						$.each($(this).children(), function(i, item) {
							if (mode == item.value) opt = item;
						});
						$("#receipt_account").text($(opt).data("mode").split("——")[0]);
						$("#receipt_payee").text($(opt).data("mode").split("——")[1]);
					} else {
						$("#receipt_account").text("");
						$("#receipt_payee").text("");
					}
				});
			} else {
				layer.msg(data.msg, {icon: 6, time: 2000});
			}
		},
		function(xhr, status) {
			layer.msg("获取收款方式数据出错，请稍后重试！", {icon: 6, time: 2000});
		}
	);
}

function showDistributes(obj){
	var popHtml=
		'<div id="distributor_list" class="distract_operation_pop structure_pop display">                                    '+
		'<div>                                                                                         '+
		'<input onkeyup="enter_keyUp(event,this, gain_distribution_list)" class="searchInput" placeholder="分销账号/名称/手机号"/>                               '+
		'<button class="searchButton">搜索</button>                                                    '+
		'</div>                                                                                        '+
		'<table class="operation_table">                                                               '+
		'<thead>                                                                                       '+
		'<tr>                                                                                          '+
		'<th style="width: 10%">勾选</th>                                                              '+
		'<th style="width: 35%">分销商账号</th>                                                        '+
		'<th style="width: 20%">名称</th>                                                              '+
		'<th style="width: 35%">手机号</th>                                                            '+
		'</tr>                                                                                         '+
		'</thead>                                                                                      '+
		'<tbody>                                                                                       '+
		'</tbody>                                                                                      '+
		'</table>                                                                                      '+
		'<div class="clear"></div>                                                                     '+
		'<div id="related_distribution_pagination" style="text-align: center; margin-top: 30px;"></div>'+
		'</div>	 ';
	layer.open({
		type: 1,
		skin: 'layui-layer-rim',
		area: ['580px', '580px'],
		content: popHtml,
		btn:['确定','取消'],
		title: '关联分销商',
		success: function(i,currdom) {
			var listPop = $('.distract_operation_pop');
			gain_distribution_list(1);
			listPop.show();
			//光联分销商弹出页面-------搜索分销商
			$(".distract_operation_pop").on("click", ".searchButton", function(){
				gain_distribution_list(1);
			});
		},
		yes: function(index) {
			var $checked = $("#distributor_list input[type='radio']:checked");
			var email = $checked.parent().siblings(".email").text();
			$(obj).parents(".sendApply").find("input[name='selectedEmail']").val(email);
			layer.close(index);
		}
	});
}

function gain_distribution_list(curr) {
	var params = {
		role: 2,
		currPage: curr == undefined || curr == 0 ? 1 : curr,
		pageSize: 10,
		search: $(".distract_operation_pop .searchInput").val().trim(),
	};
	$.ajax({
		url: "../member/relatedMember",
		type: 'POST',
		data: JSON.stringify(params),
		contentType: "application/json",
		async: true,//是否异步
		dataType: 'json',
		success: function(data) {
			if (data) {
				if (data.mark == 2 || data.mark == 3) {
					insert_distribution_list(data.data.list);
					init_distribution_pagination(data.data);
				} else if (!data.suc) {
					window.location.href = "login.html";
				} else if (data.mark == 1){
					layer.msg("获取分销商失败", {icon : 2, time : 1000});
				}
			}
		},
		error: function(xhr, status) { console.log("error--->" + status); }
	});
}

function insert_distribution_list(list,type){
	$('.distract_operation_pop .operation_table').find("tbody").empty("tr");
	var itemHTML = '';
	$.each(list, function(i, item) {
		itemHTML +=
			'<tr>'+
			'<td style="width: 10%"><input style="cursor: pointer;" '+(item.isFrozen?"disabled":"")+' type="radio"  value="' + item.id + '"></td>'+
			'<td style="width: 35%" class="email">' + deal_with_illegal_value(item.email) +(item.isFrozen?'【<b style="color: red;">冻结</b>】':"")+ '</td>'+
			'<td style="width: 20%" class="nickName">' + deal_with_illegal_value(item.nick) + '</td>'+
			'<td style="width: 35%" class="distributorTel">' + deal_with_illegal_value(item.telphone) + '</td>'+
			'</tr>'
	});
	$('.distract_operation_pop .operation_table').find("tbody").append(itemHTML);
}

function init_distribution_pagination(page,type){
	if ($("#related_distribution_pagination")[0] != undefined) {
		$("#related_distribution_pagination").empty();
		laypage({
			cont: 'related_distribution_pagination',
			pages: page.totalPage,
			curr: page.currPage,
			groups: 5,
			skin: '#55ccc8',
			first: '首页',
			last: '尾页',
			prev: '上一页',
			next: '下一页',
			skip: true,
			jump: function(obj, first) {
				//first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
				$("#related_distribution_pagination .laypage_total").find("input[type='number']").css("width","40px");
				if(!first){
					gain_distribution_list(obj.curr);
				}
			}
		});
	}
}

//提交申请
function submit_recharge(obj){
	var $sendApply = $(obj).parents(".sendApply");
	var type = $sendApply.attr("type");//1：线下充值  2；现金充值
	var options = {
		//url: url,                 //默认是form的action
		//type: type,               //默认是form的method（get or post）
		//dataType: 'json',           //html(默认), xml, script, json...接受服务端返回的类型
		clearForm: true,          //成功提交后，清除所有表单元素的值
		//resetForm: true,          //成功提交后，重置所有表单元素的值
		//target: '#output',          //把服务器返回的内容放入id为output的元素中
		//timeout: 3000,               //限制请求的时间，当请求大于3秒后，跳出请求
		//提交前的回调函数
		beforeSubmit: function(formData, jqForm, options){
			//formData: 数组对象，提交表单时，Form插件会以Ajax方式自动提交这些数据，格式如：[{name:user,value:val },{name:pwd,value:pwd}]
			//jqForm:   jQuery对象，封装了表单的元素
			//options:  options对象
			//比如可以再表单提交前进行表单验证
			var selectedEmail = $sendApply.find("input[name='selectedEmail']").val().trim();
			if (selectedEmail == "") {
				layer.msg("请选择分销商", {icon: 0, time: 2000});
				return false;
			}
			formData.push({"name":"selectedEmail",value:selectedEmail});
			if (type && type == 1) {
				var distributorName =  $sendApply.find("input[name='distributorName']").val().trim();
				if (distributorName == "") {
					layer.msg("账户开户名不能为空", {icon: 0, time: 2000});
					return false;
				}
			}
			var transferAmount = $sendApply.find("input[name='transferAmount']").val().trim();
			if (!/^((\d{1})|([1-9]{1}\d+)|(\d{1}\.\d{1,2})|([1-9]{1}\d+\.\d{1,2}))$/.test(transferAmount)) {
				layer.msg("请输入正确格式的付款金额", {icon: 0, time: 2000});
				return false;
			}
			if($sendApply.find("input[name='transferNumber']").length){
				var transferNumber = $sendApply.find("input[name='transferNumber']").val().trim();
				if(!transferNumber){
					layer.msg("付款流水号不能为空", {icon: 0, time: 2000});
					return false;
				}
			}
			var $file = $sendApply.find("input[name='image']");
			if ($file.length > 0 && $file.val() != "") {
				var name = $file.val();
				if ($file[0].files[0].size > (2 * 1024 * 1024) ||
					!(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
					layer.msg("付款截图只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
					return false;
				}
			}
		},
		//提交成功后的回调函数
		success: function(data,status,xhr,$form){
			if (data.success) {
				layer.msg("充值成功", {icon: 1, time: 3000});
				$sendApply.find("#preview").attr("src","");
			} else {
				layer.msg("充值失败", {icon: 5, time: 3000});
			}
		},
		error: function(xhr, status, error, $form){},
		complete: function(xhr, status, $form){}
	};
	$sendApply.ajaxSubmit(options);
}

function readImg(obj){
	preview(obj,$("#preview"));
}

function initial_audit_event() {
	$("#receipt_mode,#trial_transfer_time").change(function(){
		post_initial_audit();
	});
	$("#trial_searchButton").click(function () {
		post_initial_audit();
	});
	$("#trial_searchInput").keydown(function (e) {
		if (e.keyCode == 13) {
			post_initial_audit();
		}
	});
}
function review_audit_event() {
	$("#audit_state,#receipt_mode,#transfer_time").change(function(){
		post_review_audit();
	});
	$("#review_searchButton").click(function () {
		post_review_audit();
	});
	$("#review_searchInput").keydown(function (e) {
		if (e.keyCode == 13) {
			post_review_audit();
		}
	});
}

function post_review_audit () {
	// 拿到原有的，清除掉
	var postData = $("#review_audit_tab").jqGrid("getGridParam", "postData");
	$.each(postData, function (k, v) {
		delete postData[k];
	});
	var search = $("#review_searchInput").val();
	var params = {
		applyType: 1,
		auditState : $("#audit_state").val(),
		receiptMode: $("#receipt_mode").val(),
		time: $("#transfer_time").val(),
		reviewState : 4,
		auditOrreview : 1,
		search : search ? search.trim() : ""
	};
	$("#review_audit_tab").jqGrid('setGridParam', {page: 1, postData: params}).trigger('reloadGrid');
}
function post_initial_audit() {
	// 拿到原有的，清除掉
	var postData = $("#initial_auit_tab").jqGrid("getGridParam", "postData");
	$.each(postData, function (k, v) {
		delete postData[k];
	});
	var search = $("#trial_searchInput").val();
	var params = {
		auditState : 0,
		receiptMode : $("#receipt_mode").val(),
		time : $("#trial_transfer_time").val(),
		search : search ? search.trim() : ""
	};
	$("#initial_auit_tab").jqGrid('setGridParam', {page: 1, postData: params}).trigger('reloadGrid');
}

function initialAuditSetting() {
	var url = "/member/queryApply";
	var colNames = ["用户名", "名称","收款账户名", "收款账户",
		"付款账户", "付款人", "付款流水号", "实际付款日期",
		"付款金额(元)","付款截图",
		"录入人", "充值备注","操作"];
	var colModel = [
		{name: "email", index: "distributor_name", width: "5%", align: "center", sortable: false},
		{name: "nickName", index: "nickName", width: "5%", align: "center", sortable: false},
		{name: "receiptName", index: "receiptName", width: "6%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "receiptCard", index: "receiptCard", width: "6%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "transferCard", index: "transferCard", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "name", index: "name", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "transferNumber", index: "transferNumber", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "transTime", index: "transfer_time", width: "5%", align: "center", sortable: true,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "transferAmount", index: "transfer_amount", width: "5%", align: "center", sortable: true},
		{
			name: "screenshotUrl", index: "screenshotUrl", width: "5%", align: "center", sortable: false,
			formatter: function (cellvalue, options, rowObject) {
				return (cellvalue ? "<img onclick='previewImg(this)' style='width:3.5em;height:3.5em;display: table-cell;margin: 0 auto;cursor: pointer;' src='/member/screenUrl?ap=" + rowObject.id + "'/>" : "--");
			}
		},
		{name: "applyMan", index: "applyMan", width: "4%", align: "center", sortable: false},
		{name: "applyRemark", index: "applyRemark", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{
			name: "op", index: "op", width: "6%", align: "center", sortable: false,
			formatter: function (cellvalue, options, rowObject) {
				var f = "\"Ushow\"";
				return "<a href='javascript:;' onclick='operationShow(this,"+f+")' tag='"+rowObject.id+"'>审核</a>";
			}
		}
	];
	var pager = "#initial_auit_pager";//分页
	var caption = "充值初审";//表名称,
	var jsonReader = {
		root: "result.list",  //数据模型
		page: "result.currPage",//数据页码
		total: "result.totalPage",//数据总页码
		records: "result.rows",//数据总记录数
		repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
		//cell: "cell",//root 中row 行
		id: "id"//唯一标识
	};
	var s = setting(url, colNames, colModel, pager, jsonReader, caption);
	s.postData = {auditState : 0};
	return s;
}
/*-------------------------------------jqGrid  recharge_all  start---------------------------------------*/
var BbcGrid = undefined;

function init_recharge(Grid, l) {
	layer = l;
	BbcGrid = Grid;
	var bbcGrid = new Grid();
	bbcGrid.initTable($("#recharge_offline_record"), rechargeAllSetting());
	gain_receipt_mode();
}
function initial_audit(Grid, l) {
	layer = l;
	BbcGrid = Grid;
	var bbcGrid = new Grid();
	bbcGrid.initTable($("#initial_auit_tab"), initialAuditSetting());
	gain_receipt_mode();
}
function review_audit(Grid, l) {
	layer = l;
	BbcGrid = Grid;
	var bbcGrid = new Grid();
	bbcGrid.initTable($("#review_audit_tab"), reviewAuditSetting());
	gain_receipt_mode();
}

function reviewAuditSetting() {
	var url = "/member/queryApply";
	var colNames = ["用户名", "名称","收款账户名", "收款账户",
		"付款账户", "付款人", "付款流水号", "实际付款日期",
		"实际到账日期", "付款金额(元)", "实际到账金额(元)", "付款截图",
		"录入人", "充值备注", "初审备注", "初审状态", "操作"];
	var colModel = [
		{name: "email", index: "distributor_name", width: "5%", align: "center", sortable: false},
		{name: "nickName", index: "nickName", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "receiptName", index: "receiptName", width: "6%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "receiptCard", index: "receiptCard", width: "6%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "transferCard", index: "transferCard", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "name", index: "name", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "transferNumber", index: "transferNumber", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "transTime", index: "transfer_time", width: "5%", align: "center", sortable: true,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "actualTime", index: "actual_time", width: "5%", align: "center", sortable: true,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "transferAmount", index: "transfer_amount", width: "5%", align: "center", sortable: true,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "actualAmount", index: "actual_amount", width: "5%", align: "center", sortable: true},
		{
			name: "screenshotUrl", index: "screenshotUrl", width: "5%", align: "center", sortable: false,
			formatter: function (cellvalue, options, rowObject) {
				return (cellvalue ? "<img onclick='previewImg(this)' style='width:3.5em;height:3.5em;display: table-cell;margin: 0 auto;cursor: pointer;' src='/member/screenUrl?ap=" + rowObject.id + "'/>" : "--");
			}
		},
		{name: "applyMan", index: "applyMan", width: "4%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "applyRemark", index: "applyRemark", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "auditRemark", index: "auditRemark", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "auditState", index: "auditState", width: "5%", align: "center", sortable: false},
		{name: "op", index: "op", width: "6%", align: "center", sortable: false,
			formatter: function (cellvalue, options, rowObject) {
				var f = "\"Ushow\"";//"+rowObject.audit+"
				return "<a href='javascript:;' onclick='operationShow(this,"+f+",3)' state='3' tag='"+rowObject.id+"'>审核</a>";
			}
		}
	];
	var pager = "#review_audit_pager";//分页
	var caption = "充值复审";//表名称,
	var jsonReader = {
		root: "result.list",  //数据模型
		page: "result.currPage",//数据页码
		total: "result.totalPage",//数据总页码
		records: "result.rows",//数据总记录数
		repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
		//cell: "cell",//root 中row 行
		id: "id"//唯一标识
	};
	var s = setting(url, colNames, colModel, pager, jsonReader, caption);
	s.postData = {reviewState : 4,auditOrreview : 1,applyType: 1}
	return s;
}

function rechargeAllSetting() {
	var url = "/member/queryApply";
	var colNames = ["用户名", "名称","收款账户名", "收款账户",
		"付款账户", "付款人", "付款流水号", "实际付款日期",
		"实际到账日期", "付款金额(元)", "实际到账金额(元)", "付款截图",
		"录入人", "充值备注", "初审备注", "复审备注", "初审状态", "复审状态", "操作"];
	var colModel = [
		{name: "email", index: "distributor_name", width: "5%", align: "center", sortable: false},
		{name: "nickName", index: "nickName", width: "5%", align: "center", sortable: false},
		{name: "receiptName", index: "receiptName", width: "6%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "receiptCard", index: "receiptCard", width: "6%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "transferCard", index: "transferCard", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "name", index: "name", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "transferNumber", index: "transferNumber", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "transTime", index: "transfer_time", width: "5%", align: "center", sortable: true,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "actualTime", index: "actual_time", width: "5%", align: "center", sortable: true,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "transferAmount", index: "transfer_amount", width: "5%", align: "center", sortable: true,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "actualAmount", index: "actual_amount", width: "5%", align: "center", sortable: true,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{
			name: "screenshotUrl", index: "screenshotUrl", width: "5%", align: "center", sortable: false,
			formatter: function (cellvalue, options, rowObject) {
				return (cellvalue ? "<img onclick='previewImg(this)' style='width:3.5em;height:3.5em;display: table-cell;margin: 0 auto;cursor: pointer;' src='/member/screenUrl?ap=" + rowObject.id + "'/>" : "--");
			}
		},
		{name: "applyMan", index: "applyMan", width: "4%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "applyRemark", index: "applyRemark", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "auditRemark", index: "auditRemark", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "reAuditRemark", index: "reAuditRemark", width: "5%", align: "center", sortable: false,formatter: function (cellvalue, options, rowObject) {
			return deal_with_illegal_value(cellvalue);
		}},
		{name: "auditState", index: "auditState", width: "5%", align: "center", sortable: false},
		{name: "reviewState", index: "reviewState", width: "5%", align: "center", sortable: false},
		{
			name: "op", index: "op", width: "6%", align: "center", sortable: false,
			formatter: function (cellvalue, options, rowObject) {
				return '<a href="javascript:;" onclick="infoShow(this,true)" tag="' + rowObject.id + '">操作记录</a>';
			}
		}
	];
	var pager = "#recharge_offline_pager";//分页
	var caption = "线下充值记录";//表名称,
	var jsonReader = {
		root: "result.list",  //数据模型
		page: "result.currPage",//数据页码
		total: "result.totalPage",//数据总页码
		records: "result.rows",//数据总记录数
		repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
		//cell: "cell",//root 中row 行
		id: "id"//唯一标识
	};
	return setting(url, colNames, colModel, pager, jsonReader, caption);
}

function initRechargeAllEvent() {
	$("#audit_state,#reaudit_state,#receipt_mode,#transfer_time").change(function () {
		offlineQuery();
	});
	$("#searchButton").click(function () {
		offlineQuery();
	});
	$("#searchInput").keydown(function (e) {
		if (e.keyCode == 13) {
			offlineQuery();
		}
	});
	$("#online_pay_status,#online_pay_date").change(function () {
		onlineQuery();
	});
	$("#onlineSearchButton").click(function () {
		onlineQuery();
	});
	$("#onlineSearchInput").keydown(function (e) {
		if (e.keyCode == 13) {
			onlineQuery();
		}
	});
	$("#withdraw_auditstate_select,#withdraw_applytime_select,#withdraw_audittime_select").change(function () {
		withdrawQuery();
	});
	$("#withdraw_search_btn").click(function () {
		withdrawQuery();
	});
	$("#withdraw_search_input").keydown(function (e) {
		if (e.keyCode == 13) {
			withdrawQuery();
		}
	});
	$("#withdraw_export_btn").click(function(e) {
	    export_withdraw_apply_data();
	});
	$("#export_apply,#export_apply_1").click(function (){
		export_apply($(this).attr("tabid"));
	});
}
// 条件查询
function offlineQuery() {
	// 拿到原有的，清除掉
	var postData = $("#recharge_offline_record").jqGrid("getGridParam", "postData");
	$.each(postData, function (k, v) {
		delete postData[k];
	});
	var params = {applyType: 1};
	params.auditState = $("select[name='audit_state']").val();
	params.reviewState = $("select[name='reaudit_state']").val();
	params.receiptMode = $("select[name='receipt_mode']").val();
	params.time = $("select[name='transfer_time']").val();
	var search = $("#searchInput").val();
	params.search = search ? search.trim() : "";
	$("#recharge_offline_record").jqGrid('setGridParam', {page: 1, postData: params}).trigger('reloadGrid');
}

function setting(url, colNames, colModel, pager, jsonReader, caption) {
	var setting = {
		url: url,
		ajaxGridOptions: {contentType: 'application/json; charset=utf-8'},
		rownumbers: true, // 是否显示前面的行号
		datatype: "json", // 返回的数据类型
		mtype: "post", // 提交方式
		height: "auto", // 表格宽度
		autowidth: true, // 是否自动调整宽度
		colNames: colNames,
		colModel: colModel,
		viewrecords: true,
		rowNum: 10,
		rowList: [10, 20, 30],
		pager: pager,//分页
		pagerpos: "center",
		pgbuttons: true,
		loadtext: "加载中...",
		pgtext: "当前页 {0} 一共{1}页",
		caption: caption,//表名称,
		jsonReader: jsonReader,
		serializeGridData : function(postData) {
			postData.currPage = postData.page;
			postData.pageSize = postData.rows;
			return JSON.stringify(postData);
		}
	};
	return setting;
}

function onlineSetting() {
	var url = "/member/queryApply";
	var colNames = ["用户名","名称", "付款方式", "付款流水号",
		"实际付款日期", "付款金额(元)", "付款状态"];
	var colModel = [
		{name: "email", index: "distributor_name", width: "5%", align: "center", sortable: false},
		{name: "nickName", index: "distributor_name", width: "5%", align: "center", sortable: false},
		{name: "transferType", index: "transferType", width: "6%", align: "center", sortable: false},
		{name: "transferNumber", index: "transferNumber", width: "5%", align: "center", sortable: false},
		{name: "transTime", index: "transfer_time", width: "5%", align: "center", sortable: true},
		{name: "actualAmount", index: "actual_amount", width: "5%", align: "center", sortable: true},
		{name: "auditState", index: "auditState", width: "5%", align: "center", sortable: false}
	];
	var pager = "#recharge_online_pager";//分页
	var caption = "线上充值记录";//表名称,
	var jsonReader = {
		root: "result.list",  //数据模型
		page: "result.currPage",//数据页码
		total: "result.totalPage",//数据总页码
		records: "result.rows",//数据总记录数
		repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
		//cell: "cell",//root 中row 行
		id: "id"//唯一标识
	};
	var s = setting(url, colNames, colModel, pager, jsonReader, caption);
	s.postData = {isOnline:1};
	return s;
}

function onlineQuery() {
	// 拿到原有的，清除掉
	var postData = $("#recharge_online_record").jqGrid("getGridParam", "postData");
	$.each(postData, function (k, v) {
		delete postData[k];
	});
	var params = {applyType: 1};
	params.isOnline = 1;
	params.auditState = $("#online_pay_status").val();
	params.time= $("#online_pay_date").val();
	params.search= $("#onlineSearchInput").val().trim();
	$("#recharge_online_record").jqGrid('setGridParam', {page: 1, postData: params}).trigger('reloadGrid');
}

function withdrawQuery() {
	// 拿到原有的，清除掉
	var postData = $("#withdraw_record").jqGrid("getGridParam", "postData");
	$.each(postData, function (k, v) {
		delete postData[k];
	});
	var params = {applyType: 2};
	params.search = $("#withdraw_search_input").val().trim();
	params.auditState = $("#withdraw_auditstate_select").val();
	params.createDate = $("#withdraw_applytime_select").val();
	params.updateDate = $("#withdraw_audittime_select").val();
	$("#withdraw_record").jqGrid('setGridParam', {page: 1, postData: params}).trigger('reloadGrid');
}

function withdrawSetting(){
	var url = "/member/queryWithdraw";
	var colNames = ["提现单号", "用户名","名称" ,"开户所在省/市",
		"收款方式", "开户名", "提现账户", "申请时间", "处理时间", "转账流水号", "提现金额(元)", "转账金额(元)",
		"手续费(元)", "审核理由", "状态"];
	var colModel = [
		{name: "onlineApplyNo", index: "online_apply_no", width: "5%", align: "center", sortable: false},
		{name: "distributorEmail", index: "distributor_email", width: "6%", align: "center", sortable: false},
		{name: "nickName", index: "nick_name", width: "5%", align: "center", sortable: false},
		{name: "accountPC", index: "account_pc", width: "5%", align: "center", sortable: false},
		{name: "accountUnit", index: "account_unit", width: "5%", align: "center", sortable: false},
		{name: "accountUser", index: "account_user", width: "5%", align: "center", sortable: false},
		{name: "withdrawAccountNo", index: "withdraw_account", width: "5%", align: "center", sortable: false},
		{name: "createDateStr", index: "createdate", width: "5%", align: "center", sortable: true},
		{name: "updateDateStr", index: "updatedate", width: "5%", align: "center", sortable: true},
		{name: "transferNumber", index: "transfer_number", width: "5%", align: "center", sortable: false},
		{name: "withdrawAmount", index: "withdraw_amount", width: "5%", align: "center", sortable: true},
		{name: "transferAmount", index: "transfer_amount", width: "5%", align: "center", sortable: true},
		{name: "counterFee", index: "counter_fee", width: "5%", align: "center", sortable: false},
		{name: "auditReasons", index: "audit_reasons", width: "5%", align: "center", sortable: false},
		{name: "auditStateStr", index: "audit_state", width: "5%", align: "center", sortable: false}
	];
	var pager = "#withdraw_pager";//分页
	var caption = "提现记录";//表名称,
	var jsonReader = {
		root: "result.list",  //数据模型
		page: "result.currPage",//数据页码
		total: "result.totalPage",//数据总页码
		records: "result.rows",//数据总记录数
		repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
		//cell: "cell",//root 中row 行
		id: "id"//唯一标识
	};
	var s = setting(url, colNames, colModel, pager, jsonReader, caption);
	s.postData = {applyType:2};
	return s;
}

/*----------------------------余额提现start----------------------------*/
function present_audit(Grid,l) {
	layer = l;
	BbcGrid = Grid;
	var bbcGrid = new Grid();
	bbcGrid.initTable($("#present_audit_tab"), presentAuditSetting());
}
function presentAuditSetting() {
	var url = "/member/queryWithdraw";
	var colNames = ["用户名","提现单号", "开户所在省/市",
		"收款方式", "开户名", "提现账户", "提现金额(元)","申请时间","操作"];
	var colModel = [
		{name: "distributorEmail", index: "distributor_email", width: "6%", align: "center", sortable: false},
		{name: "onlineApplyNo", index: "online_apply_no", width: "5%", align: "center", sortable: false},
		{name: "accountPC", index: "account_pc", width: "5%", align: "center", sortable: false},
		{name: "accountUnit", index: "account_unit", width: "5%", align: "center", sortable: false},
		{name: "accountUser", index: "account_user", width: "5%", align: "center", sortable: false},
		{name: "withdrawAccountNo", index: "withdraw_account", width: "5%", align: "center", sortable: false},
		{name: "withdrawAmount", index: "withdraw_amount", width: "5%", align: "center", sortable: true},
		{name: "createDateStr", index: "createdate", width: "5%", align: "center", sortable: true},
		{name: "op", index: "op", width: "5%", align: "center", sortable: false,
			formatter: function (cellvalue, options, rowObject) {
				return '<a href="javascript:" onclick="audit_withdraw_apply('+rowObject.id+','+rowObject.withdrawAccountId+');" class="presentAudit_check" data-accountid="'+rowObject.withdrawAccountId+'" data-id="' + rowObject.id + '">审核</a>';
			}
		}
	];
	var pager = "#present_audit_pager";//分页
	var caption = "余额提现";//表名称,
	var jsonReader = {
		root: "result.list",  //数据模型
		page: "result.currPage",//数据页码
		total: "result.totalPage",//数据总页码
		records: "result.rows",//数据总记录数
		repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
		//cell: "cell",//root 中row 行
		id: "id"//唯一标识
	};
	var s = setting(url, colNames, colModel, pager, jsonReader, caption);
	s.postData = {applyType : 2,auditState : 0,ismswa : '0'};
	return s;
}
function presentAuditEvent() {
	$("#withdraw_setup_btn").click(function(){
		setup_withdraw_limit();
	});
	$("#withdraw_search_btn").click(function () {
		presentAuditQuery();
	});
	$("#withdraw_search_input").keydown(function (e) {
		if (e.keyCode == 13) {
			presentAuditQuery();
		}
	});
}
function presentAuditQuery() {
	// 拿到原有的，清除掉
	var postData = $("#present_audit_tab").jqGrid("getGridParam", "postData");
	$.each(postData, function (k, v) {
		delete postData[k];
	});
	var params = {applyType : 2,auditState : 0,ismswa : '0'};
	params.search = $("#withdraw_search_input").val().trim();
	$("#present_audit_tab").jqGrid('setGridParam', {page: 1, postData: params}).trigger('reloadGrid');
}
/*----------------------------余额提现end----------------------------*/
