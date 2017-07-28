//定义全局变量
var layer = undefined;
var laypage = undefined;
var applyType = undefined;
var isOnline=undefined

$("#sendApply input[tag=validate]").blur(function(){
    checkInput(this);
});

function readImg(obj){
    preview(obj,$("#preview"));
}

function checkInput(node){
    //定时器，由于日期选中瞬间input还没有值，所以总是报空，延迟0.1秒后执行，效果正常
    setTimeout(function(){
        if($(node).attr("type") != "file"&&$(node).attr("name") != "transferNumber"&&$(node).attr("name") != "applyRemark"){
            var value = $(node).val();
            if(!value){
                layer.msg($(node).parent().prev().find("span:eq(1)").text() + '不能为空', {icon : 2, time : 1000});
                return ;
            }
            if($(node).attr("name") == "transferAmount" && !/^((\d{1})|([1-9]{1}\d+)|(\d{1}\.\d{1,2})|([1-9]{1}\d+\.\d{1,2}))$/.test($(node).val())){
                layer.msg('请规范填写金额', {icon : 2, time : 1000});
                $(node).focus();
            }
        }
    },100);
}

function initLayer(l,lp) {
    layer = l;
    laypage = lp;
}

function sendApply(){
    var options = {
        //url: url,                 //默认是form的action
        //type: type,               //默认是form的method（get or post）
        dataType: 'json',           //html(默认), xml, script, json...接受服务端返回的类型
        clearForm: false,          //成功提交后，清除所有表单元素的值
        resetForm: false,          //成功提交后，重置所有表单元素的值
        //target: '#output',          //把服务器返回的内容放入id为output的元素中
        //timeout: 3000,               //限制请求的时间，当请求大于3秒后，跳出请求
        //提交前的回调函数
        beforeSubmit: function(formData, jqForm, options){
            //formData: 数组对象，提交表单时，Form插件会以Ajax方式自动提交这些数据，格式如：[{name:user,value:val },{name:pwd,value:pwd}]
            //jqForm:   jQuery对象，封装了表单的元素
            //options:  options对象
            //比如可以再表单提交前进行表单验证
            var text1 = $("#sendApply select[name='recipientId']").val();
            if (text1 == "") {
                layer.msg("请选择收款渠道", {icon: 0, time: 2000});
                return false;
            }
            var text2 = $("#sendApply select[name='transferType']").val();
            if (text2 == "") {
                layer.msg("请选择付款账户", {icon: 0, time: 2000});
                return false;
            }
            var text5 = $("#sendApply input[name='distributorName']").val().trim();
            if (text5 == "") {
                layer.msg("账户开户名不能为空", {icon: 0, time: 2000});
                return false;
            }//form表单中元素为disabled，不会存入options里的fromData中
            formData.push({"name":"distributorName",value:$("#sendApply input[name='distributorName']").val().trim()});
            var text6 = $("#sendApply input[name='transferAmount']").val().trim();
            if (!/^((\d{1})|([1-9]{1}\d+)|(\d{1}\.\d{1,2})|([1-9]{1}\d+\.\d{1,2}))$/.test(text6)) {
                layer.msg("请输入正确格式的付款金额", {icon: 0, time: 2000});
                return false;
            }
            var text7 = $("#sendApply input[name='transferNumber']").val().trim();
            if(!text7){
                layer.msg("付款流水号不能为空", {icon: 0, time: 2000});
                return false;
            }
            var $file = $("#sendApply input[name='image']");
            if ($file != undefined && $file.val() != "") {
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
                $("#Top-up-remittance").hide();
                $("#apply-list").show();
                $("#timeSelect").val("");
                $("#purposeSelect").val("");
                $("#preview").attr("src","");
                layer.msg("充值申请已提交，请耐心等待审核，您可以到充值提现查看到账情况。", {icon: 1, time: 2000}, function() {
                   ajax_get_applys(0);
                });
            } else {
                layer.msg(data.msg, {icon: 2, time: 3000});
            }
        },
        error: function(xhr, status, error, $form){},
        complete: function(xhr, status, $form){}
    };
    $("#sendApply").ajaxSubmit(options);
}

//初始化申请列表
function init_applys(l, lp){
    layer = l;
    laypage = lp;
    applyType = 1;
    isOnline = true;

    ajax_get_applys(0);

    $("#Top-up-remittance").on("click","input[name='online-pay-type']",function (){
        if(this.value == "易极付"){
            if(this.checked)
                $("#yjf_type_choice").show();
        }else{
            $("#yjf_type_choice").hide();
        }
    })
}

/**
 * 充值申请列表
 * @param curr 页码
 */
function ajax_get_applys(curr) {
    if(applyType == 1){// 线上/线下充值
        var param = {
            pageSize : 10,
            currPage : curr == undefined || curr == 0 ? 1 : curr,
            time : $("#timeSelect").val(),
            // disState : $("#purposeSelect").val(),
            type : 1,
            applyType : applyType
        };
        if(isOnline){
            param.isOnline = 1;
            // 初审状态
            param.auditState = $("#purposeSelect").val()
        }else{
            // 复审状态
            param.reviewState = $("#purposeSelect").val()
        }
        ajax_post("/member/queryApply", JSON.stringify(param), "application/json",
        function(data) {
            if (data && data.success) {
                var page = data.result;
                $(".order-handle-detail tr").remove();
                $('.nothing').remove();
                insert_applys(page.list);
                init_pagination_apply(page.totalPage,page.currPage);
            }
        },
        function(xhr, status) {});
    }else if(applyType == 2){ // 提现申请
        var param = {
            type : 1,
            pageSize : 10,
            currPage : curr == undefined || curr == 0 ? 1 : curr,
            createDate : $("#wb_create_date").val(),
            transferTime : $("#wb_transfer_time").val(),
            auditState : $("#wb_audit_state").val(),
            applyType : 2
        };
        ajax_post("/member/queryWithdraw", JSON.stringify(param), "application/json",
            function(data) {
                if (data && data.success) {
                    var page = data.result;
                    $(".order-handle-detail tr").remove();
                    $('.nothing').remove();
                    insert_applys(page.list);
                    init_pagination_apply(page.totalPage,page.currPage);
                }
            },
            function(xhr, status) {}
        );
    }
}

/**
 *
 * @param data 数据集合
 */
function insert_applys(data){
    var htmlCode = "";
    if(applyType == 1){//充值
        if(isOnline){ // 线上
            var $lis = $(".order-handle-header").find("th");
            $($lis[0]).text("充值单号");
            $($lis[1]).text("付款方式");
            $($lis[2]).text("付款金额（元）");
            $($lis[3]).text("付款流水号");
            $($lis[4]).css("display","none");
            $($lis[5]).text("实际付款日期");
            $($lis[6]).text("状态");
            $($lis[7]).css("display","none");
            if(data.length > 0) {
                $.each(data, function (i, item) {
                    htmlCode += "<tr>" +
                        "<td>" + item.onlineApplyNo + "</td>" +
                        "<td>" + item.transferType + "</td>" +
                        "<td>" + fmoney(item.transferAmount, 2) + "</td>" +
                        "<td>" + deal_with_illegal_value(item.transferNumber) + "</td>" +
                        "<td class='detail-time'>" + deal_with_illegal_value(item.transTime) + "</td>"+
                        "<td>" + item.auditState + "</td>";
                    htmlCode += "</tr>";
                });
            }else{
                addLoadGif({
                    togBox:".topup-date",
                    togPage:'#pagination_apply',
                    hint: '<div class="nothing"><div>暂无充值记录~</div></div>'
                },true);
            }
        }else{ // 线下
            var $lis = $(".order-handle-header").find("th");
            $($lis[0]).text("收款账户");
            $($lis[1]).text("付款账户");
            $($lis[2]).text("付款金额（元）");
            $($lis[3]).text("付款流水号");
            $($lis[4]).css("display","none");
            $($lis[5]).text("转账时间");
            $($lis[6]).text("状态");
            $($lis[7]).css("display","none");
            if(data.length > 0) {
                $.each(data, function (i, item) {
                    htmlCode += "<tr>" +
                    "<td>" + deal_with_illegal_value(item.receiptCard) + "</td>" +
                    "<td>" + deal_with_illegal_value(item.transferCard) + "</td>" +
                    "<td>" + fmoney(item.transferAmount, 2) + "</td>" +
                    "<td>" + deal_with_illegal_value(item.transferNumber) + "</td>" +
                    "<td class='detail-time'>" + item.transTime + "</td>";
                    if (item.state == "拒绝") {
                        htmlCode += "<td>" +
                        "<a class = 'refuse blue' style = 'font-size:12px' data-reasons = '" + item.auditReasons + "' data-remark = '" + (isnull(item.reAuditRemark) ? item.auditRemark : item.reAuditRemark) + "'>审核不通过</a>" +
                        "</td>";
                    } else {
                        htmlCode += "<td>" + item.state + "</td>";
                    }
                    htmlCode += "</tr>";
                });
            }else{
                addLoadGif({
                    togBox:".topup-date",
                    togPage:'#pagination_apply',
                    hint: '<div class="nothing"><div>暂无充值记录~</div></div>'
                },true);
            }
        }
    }else{//提现
        var $lis = $(".order-handle-header").find("th");
        $($lis[0]).text("提现单号");
        $($lis[1]).text("收款方式");
        $($lis[2]).text("提现账户");
        $($lis[3]).text("提现金额（元）");
        $($lis[4]).text("手续费（元）");
        //$($lis[4]).css("display","block");
        $($lis[4]).css("display","none");//手续费此版本暂时隐藏
        $($lis[5]).text("申请时间");
        $($lis[6]).text("处理时间");
        $($lis[7]).text("状态");
        $($lis[7]).css("display","block");
        if(data.length > 0) {
            $.each(data, function (i, item) {
                htmlCode += "<tr>" +
                "<td>" + deal_with_illegal_value(item.onlineApplyNo) + "</td>" +
                "<td>" + deal_with_illegal_value(item.accountUnit) + "</td>" +
                "<td>" + deal_with_illegal_value(item.withdrawAccountNo) + "</td>" +
                "<td>" + fmoney(item.withdrawAmount, 2) + "</td>" +
                "<td style='display: none;'>" + deal_with_illegal_value(item.counterFee) + "</td>" +
                "<td class='detail-time'>" + deal_with_illegal_value(item.createDateStr) + "</td>"+
                "<td class='detail-time'>" + deal_with_illegal_value(item.transferTimeStr) + "</td>" +
                "<td data-reasons = '" + item.auditReasons + "' class='detail-time apply_status'>" + deal_with_illegal_value(item.auditStateStr) + "</td>";
                htmlCode += "</tr>";
            });
        }else{
            addLoadGif({
                togBox:".topup-date",
                togPage:'#pagination_apply',
                hint: '<div class="nothing"><div>暂无提现记录~</div></div>'
            },true);
        }
    }
    $(".order-handle-detail").prepend(htmlCode);
	
	$(".order-handle-detail").on("mouseover",".apply_status",function(){
        var node = $(this);
        if(node.html() == "处理失败") {
	        var reason = node.data("reasons");
	        var remark = node.data("remark");
	        var desc = reason+(remark?":"+remark:""); 
	        layer.tips(desc, node);
        }
    });
	
    //loading
    creatLoading(true,theDiv);
    //审核不通过显示理由
    $(".order-handle-detail").on("click",".refuse",function(){
        var node = $(this);
        var reason = node.data("reasons");
        var remark = node.data("remark");
        var desc = reason+(remark?":"+remark:""); 
        layer.tips(desc, node);
    });
}

//充值申请tab
$('body').on("click",".topup_box_ul li",function(){
    $(this).addClass("topup_box_current").siblings().removeClass();
    
    if($(this).text() == "线上充值"){
        // 初审的
        var options = '<option value="" selected>所有状态</option>'+
            '<option value="100">未支付</option>'+
            '<option value="101">已支付</option>';
        $("#purposeSelect").empty();
        $("#purposeSelect").html(options);
        applyType = 1;
        isOnline=true
        ajax_get_applys(0);
        $(".withdraw-bala").css("display", "none");
        $(".cz_apply").css("display", "block");
    }else if($(this).text() == "线下充值"){
        // 复审状态
        var options = '<option value="" selected>所有状态</option>'+
            '<option value="4">待审核</option>'+
            '<option value="3">审核异常</option>'+
            '<option value="2">审核通过</option>'+
            '<option value="1">审核不通过</option>';
        $("#purposeSelect").empty();
        $("#purposeSelect").html(options);
        applyType = 1;//充值
        isOnline=false
        ajax_get_applys(0);
        $(".withdraw-bala").css("display", "none");
        $(".cz_apply").css("display", "block");
    }else{
        applyType = 2;//提现
        isOnline=false
        ajax_get_applys(0);
        $(".withdraw-bala").css("display", "block");
        $(".cz_apply").css("display", "none");
    }
})

$('body').on("click",".Topup-remittance-tab li",function(){
    $(this).addClass("topup_box_current").siblings().removeClass();
    $(".Topup-remittance-box > div").hide().eq($(".Topup-remittance-tab li").index(this)).show();
})

//初始化分页栏
function init_pagination_apply(total,currPage) {
    scrollPosTop();
    if ($("#pagination_apply")[0] != undefined) {
        $("#pagination_apply").empty();
        laypage({
            cont: 'pagination_apply',
            pages: total,
            curr: currPage,
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
                    ajax_get_applys(obj.curr);
                }
            }
        });
    }
}

var emailFlag = false;

//付款密码设置
function paypw_set(firstTime) {
    $(".phone-user").find("input").val("");
    var EM_RE = /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
    isnulogin(function(email){  
        emailFlag = EM_RE.test(email);
        $(".pay_email").text(email);
        $(".pay_phone").text(email);
        $.ajax({
            url : "/member/clientid?" + new Date(),
            type : "GET",
            data : {},
            async:false,
            success : function(data) {
            }
        });
    })
    if(firstTime){
        $("#tips").find("p").text("初次支付请先设置支付密码");
        $("#tips").show();
    }
    else{
        $("#tips").hide();
    }
    $("div.six-user").fadeOut(200);
    $("div.five-date").fadeOut(200);
    $("#apply-list").fadeOut(200);
    $("#send_email_pay").fadeOut(200);
    if(emailFlag){
        $("#Topup-paycode-set").fadeIn(200);
        $("#Topup-paycode-set").find("#captchaImg").click();
    }else{
        $(".phone-user").fadeIn(200);
        $("#Topup-phone-set").fadeIn(200);
        $("#Topup-phone-set").find("#captchaImg").click();
    }
}

//余额提现
$('body').on('click', '#withdraw_balance', function () {
	//一开始打开时，清除弹出框数据
	$(".balanceCommission_one input").val("");
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['470px', '360px'],
        title: '余额提现',
        content: $('.balanceCommission_one'),
        btn: ['下一步'],
        yes: function () {
            var wb_pay_pwd = $("#wb_pay_pwd").val();
            //验证码
            var payCaptcha = $("#pay_captcha").val();
            if(wb_pay_pwd == ''){
                layer.msg('请输入支付密码!', {time: 2000, icon: 0});
            }else{
                var param = {
                	captcha:wb_pay_pwd,
                	payCaptcha:payCaptcha==undefined?"":payCaptcha
                };
                ajax_post("/member/checkPayPwd", JSON.stringify(param), "application/json",
                    function(data) {
                        if (data && data.suc) {
                            layer.closeAll();
                            $("#wb_balance").text(data.msg);
                            $("#wb_amount").val("");
                            $("#counterFee").val("");
                            $("#counter_fee").text("手续费： 0 元");
                            //提现申请信息弹框
                            applyInfo();
                        }else{
                            layer.msg(data.msg, {time: 2000, icon: 0});
                            //验证码处理
                            initPayCaptcha();
                        }
                    },
                    function(xhr, status) {}
                );
            }
        }
    });
    //初始化提示信息:提交申请限制
    initApplyLimit();
    //验证码处理
    initPayCaptcha();
});

// 验证输入的银行卡号是否全是数字
function check_wb_no(obj){
    var pattern = /^([0-9]\d*)$/;// 数字
    var wb_no = $.trim($(obj).val())
    if (!pattern.test(wb_no)) {
        $(obj).val(wb_no.substring(0, wb_no.length - 1));
        layer.msg('只能输入数字', {time: 2000, icon: 2});
    }
}

 //绑定银行卡
$('body').on('click', '.AddCard', function () {
	//点击时，清除原有数据
	$(".balanceCommission_bind input").val("");
	$(".balanceCommission_bind select").val("");
    //加载省
    ajax_get("/member/getprovs", JSON.stringify(""), "application/json",
        function (data) {
            layer.closeAll();

            $("#wb_province").empty();
            $("#wb_city").empty();
            var item = "<option value=''>请选择省</option>";
            for (var i = 0; i < data.length; i++) {
                item += "<option value='" + data[i].id + "' >" + data[i].provinceName + "</option>";
            }
            $("#wb_province").append(item);
            $("#wb_city").append("<option value=''>请选择市</option>");

            layer.open({
                type: 1,
                skin: 'layui-layer-rim',
                area: ['450px', '350px'],
                title: '银行卡绑定',
                content: $('.balanceCommission_bind'),
                btn: ['立即绑定', '返回'],
                yes: function () {
                    var wb_name = $("#wb_name").val();
                    var wb_describe = $("#wb_describe").val();
                    var wb_no = $("#wb_no").val();
                    var wb_province = $("#wb_province").val();
                    var wb_city = $("#wb_city").val();
                    var name_rex = /^[\u2E80-\u9FFF]+$/;
                    // var no_rex = /^[0-9]+$/;

                    if(wb_province == ''){
                        layer.msg('请选择开户所在省', {time: 2000, icon: 0});
                        return
                    }
                    if(wb_city == ''){
                        layer.msg('请选择开户所在市', {time: 2000, icon: 0});
                        return
                    }
                    if($.trim(wb_name) == ''){
                        layer.msg('请输入您的开户姓名', {time: 2000, icon: 0});
                        return
                    }
                    if(!name_rex.test($.trim(wb_name)) || $.trim(wb_name).length < 2){
                        layer.msg('开户姓名为中文且长度不能小于2', {time: 2000, icon: 0});
                        return
                    }
                    if($.trim(wb_name).length > 20){
                        layer.msg('开户姓名输入长度太长', {time: 2000, icon: 0});
                        return
                    }
                    if(wb_describe == ''){
                        layer.msg('请选择您的开户银行名称', {time: 2000, icon: 0});
                        return
                    }
                    if($.trim(wb_no) == ''){
                        layer.msg('请输入您的银行卡号', {time: 2000, icon: 0});
                        return
                    }
                    // 判断银行卡号位数：16~19
                    if($.trim(wb_no).length < 16 || $.trim(wb_no).length>19){
                        layer.msg('银行卡号位数不正确，位数需在16~19之间', {time: 2000, icon: 0});
                        return
                    }

                    var loadIndex = layer.load(1);
                    var existP = {
                        accountUser : wb_name,
                        withdrawAccount : wb_no,
                        accountUnit : wb_describe,
                        province : $("#wb_province").find("option:selected").text(),
                        city : $("#wb_city").find("option:selected").text()
                    };
                    //校验帐号是否已经绑定
                    ajax_post("/member/existWithdrawNo", JSON.stringify(existP), "application/json",
                        function(data) {
                            if (data && data.suc) {
                                layer.closeAll();
                                if("9" == data.msg){//邮箱
                                    $("#wb_tel").css("display","none");
                                    $("#wb_eml").css("display","block");
                                    $("#wb_email").text(data.account);
                                    time(document.getElementById("re_send_mail"));
                                }else{//手机
                                    $("#wb_tel").css("display","block");
                                    $("#wb_eml").css("display","none");
                                    $("#wb_tel_no").text(data.account);
                                    time(document.getElementById("secondsremained"));
                                }

                                layer.open({
                                    type: 1,
                                    skin: 'layui-layer-rim',
                                    area: ['550px', '450px'],
                                    title: '身份验证',
                                    content: $('.balanceCommission_id'),
                                    btn: ['确定'],
                                    yes: function () {
                                        if("9" == data.msg){//邮箱
                                            layer.closeAll();
                                            applyInfo();
                                        }else{
                                            var wb_tel_code = $("#wb_tel_code").val();
                                            if(wb_tel_code == '' || $.trim(wb_tel_code).length != 6){
                                                layer.msg('请输入6位验证码', {time: 2000, icon: 0});
                                            }else{
                                                existP.code=wb_tel_code;
                                                ajax_post("/member/addWithdrawNo", JSON.stringify(existP), "application/json",
                                                    function(data) {
                                                        if (data && data.suc) {
                                                            layer.closeAll();
                                                            layer.open({
                                                                type: 1,
                                                                skin: 'layui-layer-rim',
                                                                area: ['550px', '450px'],
                                                                title: '绑定成功',
                                                                content: $('.wb_bind_suc'),
                                                                btn: ['确定'],
                                                                yes: function () {
                                                                    layer.closeAll();
                                                                    applyInfo();
                                                                }
                                                            });
                                                        }else{
                                                            layer.msg(data.msg, {time: 2000, icon: 0});
                                                        }
                                                    },
                                                    function(xhr, status) {}
                                                );
                                            }
                                        }
                                    }
                                });
                            }else{
                                layer.close(loadIndex);
                                layer.msg(data.msg, {time: 2000, icon: 0});
                            }
                        },
                        function(xhr, status) {
                            layer.close(loadIndex);
                            layer.msg("服务器异常！", {time: 2000, icon: 0});
                        }
                    );
                    
                },
                btn2: function (){
                    applyInfo();
                }
            });
        },
        function(xhr, status) {
        	layer.msg("服务器异常！", {time: 2000, icon: 0});
        }
    );
});

function forgetPwd(){
    layer.closeAll();
    paypw_set(false);
}

//    管理银行卡
function chargeCard() {
    layer.closeAll();
    var index = layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['550px', '450px'],
        title: '管理银行卡',
        content: $('.balanceCommission_charge'),
        btn:['返回'],
        btn1:function(){
            applyInfo();
        }
    });
    bindBankCardMgr(index);
}

//    解绑
$('.del_commission').click(function () {
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['550px', '450px'],
        title: '解绑',
        content: $('.balanceCommission_del'),
        btn: ['确定', '取消'],
        btn1: function () {
            layer.msg('您已成功取消绑定银行卡', {time: 5000, icon: 6});
        }
    });
});

$("#secondsremained").click(function () {
    var param = {
        withdrawAccount:$("#wb_no").val(),
        types:1
    };
    $.ajax({
        url: "/member/reSendTelFor",
        type: "POST",
        dataType: "json",
        data: JSON.stringify(param),
        contentType: "application/json",
        success: function (data) {
            if (data && data.suc) {
                layer.msg("发送成功，请注意查收", {time: 2000, icon: 6});
            }else if(data.msg == '2'){
                window.location.href = "login.html";
            }else{
                layer.msg(data.msg, {time: 2000, icon: 0});
            }
        }
    });
});

//申请弹框
function applyInfo(){
     layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['530px', '430px'],
        content: $('.balanceCommission_two'),
        btn: ['确定'],
        yes: function () {
            var wb_amount = $("#wb_amount").val();
            var decimal_regexp = /^((\d{1})|([1-9]{1}\d+)|(\d{1}\.\d{1,2})|([1-9]{1}\d+\.\d{1,2}))$/;
            if(wb_amount == ''){
                layer.msg('请输入您的提现金额', {time: 2000, icon: 0});
            } else if(!decimal_regexp.test(wb_amount)) {
            	layer.msg('提现金额为大于0的整数且最多保留两位小数', {time: 2000, icon: 0});
            } else{
                var wb_select_id = $("#wb_select").val();
                var counterFee = $("#counterFee").val();
                var param = {
                    wAmount : wb_amount,
                    wAmountId : wb_select_id,
                    counterFee : counterFee
                };
                ajax_post("/member/applyWithdraw", JSON.stringify(param), "application/json",
                    function(data) {
                        if (data && data.suc) {
                            layer.closeAll();
                            layer.msg(data.msg, {time: 2000, icon: 6});
                            checkFrozen();
                            //查询列表数据 by huchuyin 2016-9-29
                            $(".topup_box_ul li:contains('提现申请')").click();
                        }else{
                            layer.msg(data.msg, {time: 2000, icon: 0});
                        }
                    },
                    function(xhr, status) {}
                );
            } 
            
        }
    });
    //初始化银行卡下拉列表 by huchuyin 2016-9-22
    initBindBankCard();
    //End by huchuyin 2016-9-22
}

$('body').on('click', '#wb_province', function () {
    var pro = $("#wb_province").val();
    if(pro != ''){
        ajax_get("/member/getcities", "proId=" + $("#wb_province").val(), "",
            function (data) {
                $("#wb_city").empty();
                var item = ""
                for (var i = 0; i < data.cities.length; i++) {
                    item += "<option value='" + data.cities[i].id + "' >" + data.cities[i].cityName + "</option>";
                }
                $("#wb_city").append(item);
            },
            function(xhr, status) {}
        );
    }else{
        $("#wb_city").empty();
        $("#wb_city").append("<option value=''>请选择市</option>");
    }
});

//返回
function go_back_one(){
    $("#Topup-phone-set").fadeIn(200);
    $("#Topup-paycode-phone").fadeOut(200);
}

function go_back_two(){
    $("#Topup-paycode-phone").fadeIn(200);
    $("#Topup-paycode-sure").fadeOut(200);
}

//取消付款密码设置
function paypw_cancel() {
    $("#apply-list").fadeIn(200);
    //$("#pay_new_code").val("");
    //$("#pay_sure_code").val("");
    $("#captchaImg").prev().val("");
    $("div.six-user").fadeIn(200);
    $("div.five-date").fadeIn(200);
    $("#Topup-paycode-set").fadeOut(200);
    $("#send_email_pay").fadeOut(200);
    $(".phone-user").fadeOut(200);
    //$("#Topup-paycode-set").parent().css("height", "auto");
}

//为修改支付密码跳转手机验证
function sendPhoneforpwd(){
    var email = $(".pay_phone").text();
    var captcha = $("#Topup-phone-set").find("input[name='captcha']").val();
    if (captcha == undefined || captcha == '') {
        layer.msg('验证码不能为空', {icon : 2, time : 2000});
        $("#Topup-phone-set .paycode-sure").removeAttr("disabled");
        $("#Topup-phone-set .paycode-sure").css({"cursor":"pointer", "color": "white","background-color":"rgb(17, 122, 212)"});
        return;
    }
    var params = {
        email: email,
        captcha: captcha
    };
    ajax_post("/member/applymodicell2", params, undefined,
        function(data) {
            if (data.suc) {
                $("#Topup-phone-set").fadeOut(200);
                $("#Topup-paycode-phone").fadeIn(200);
                $("#Topup-paycode-phone").data("email",data.email);
            } else {
                layer.msg(data.msg, {icon : 2, time : 2000});
                $("#Topup-phone-set .paycode-sure").removeAttr("disabled");
                $("#Topup-phone-set .paycode-sure").css({"cursor":"pointer", "color": "white","background-color":"rgb(17, 122, 212)"});
            }
        },
        function(xhr, status) { console.log("error--->" + status); }
    );

}

function sendMessgePhone(obj){
    var email = $(".pay_phone").text();
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
                if (tel != cell) {
                    layer.msg('请输入正确的手机号码', {icon: 6, time: 2000});
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
       toggle_sendmsg_btntext(obj)
       getTelCaptcha(cell);
    }
}

//发送短信
function getTelCaptcha(telphone){
	//参数新增发送短信类型 by huchuyin 2016-10-10
    var param = {tel : telphone,types:2};
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
            	//发送短信超过次数或错误 by huchuyin 2016-10-10
            	layer.msg(data.msg,{icon : 2, time : 2000});
            }
        },
        function (xhr, status) {
            layer.msg('短信发送失败，系统错误！', {icon : 2, time : 2000});
        }
    );
}

//验证手机验证码的正确性
//member/checkModifyCode
function sendCheckCode(){
	//添加手机号码参数 by huchuyin 2016-10-10
    var cell = $("input[name='cell']").val();
    var smsc = $("input[name='smsc']").val();
    // var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/;
    //短信验证码校验修改为6位  by huchuyin 2016-10-10
    var test_smsc = /^[a-zA-Z0-9]{6}$/;
    if(!test_smsc.test(smsc)){
        layer.msg('请输入6位验证码',{icon : 2, time : 2000});
        return;
    }
    var param = {
        smsc:smsc,
        cell:cell
    };
     ajax_post("/member/checkModifyCode", JSON.stringify(param), "application/json",
        function (data) {
            if(data.suc){
                $("#Topup-paycode-phone").fadeOut(200);
                $("#Topup-paycode-sure").fadeIn(200);
                layer.msg(data.msg, {icon : 6, time : 2000});
            }else{
                layer.msg(data.msg, {icon : 2, time : 2000});
            }
        },
        function (xhr, status) {
            layer.msg('验证码验证错误，系统错误！', {icon : 2, time : 2000});
        }
    );
}

//手机用户修改支付密码
function resetPwd(){
    var email = $(".pay_phone").text();
    var newpass =  $("input[name=newpass]").val();
    var affirmPW = $("input[name=affirmPW]").val();
    //添加手机号码与验证码参数 by huchuyin 2016-10-10
    var cell = $("input[name='cell']").val();
    var smsc = $("input[name='smsc']").val();
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
        "cell":cell,
        "smsc":smsc
    };
    ajax_post("/member/resetpayPassword", JSON.stringify(params), "application/json",
        function(data) {
            if (data && data.suc == 2){
                layer.msg(data.msg, {icon : 6, time : 2000}, function() {
                    window.location.href = "/personal/personal.html?emnunum=16";
                });
            } else {
                layer.msg(data.msg, {icon : 5, time : 2000});
                return;
            }
        },
        function(xhr, status) { console.log("error--->" + status); }
    );
}

//为修改支付密码而发送邮件
function sendEmailforpwd(){
    //alert("a");
    $("#apply-list").fadeOut(200);
    $("div.six-user").fadeOut(200);
    $("div.five-date").fadeOut(200);
    //发送邮件
    $("#Topup-paycode-set .paycode-sure").css({"cursor":"not-allowed", "color": "white","background-color":"rgb(204, 204, 204)"});
    $("#Topup-paycode-set .paycode-sure").attr("disabled", "disabled");
    var captcha =  $("#Topup-paycode-set").find("#captchaImg").prev().val();
    if (captcha == undefined || captcha == '') {
        layer.msg('验证码不能为空', {icon : 2, time : 2000});
        $("#Topup-paycode-set .paycode-sure").removeAttr("disabled");
        $("#Topup-paycode-set .paycode-sure").css({"cursor":"pointer", "color": "white","background-color":"rgb(17, 122, 212)"});
        $("#captchaImg").attr("src","/member/getcaptcha?' + Math.random()");
        return;
    }
    var email = $("#Topup-paycode-set .pay_email").text();
    var params = {
        email: email,
        captcha: captcha
    };
    ajax_post("/member/changePayPwdByEmail", params, undefined,
        function(data) {
            if (data.suc) {
                //$("#alter_phone_form").hide();
                //$("#send_email_div").show();
                //$("#email_result_span").text(data.email);
                //$("#send_email_div a").eq(1).data("email", data.email);
                $("#Topup-paycode-set").hide();
                $("#send_email_pay").show();
                $("#email_result_span").text(data.email);
                $("#send_email_pay a").eq(1).data("email", data.email);
            } else {
                layer.msg(data.msg, {icon : 2, time : 2000});
                $("#Topup-paycode-set .paycode-sure").removeAttr("disabled");
                $("#Topup-paycode-set .paycode-sure").css({"cursor":"pointer", "color": "white","background-color":"rgb(17, 122, 212)"});
                $("#captchaImg").attr("src","/member/getcaptcha?' + Math.random()");
            }
        },
        function(xhr, status) { console.log("error--->" + status); }
    );
}

$("#send_email_pay a").eq(1).on("click", function(event) {
    var email = $(this).data("email");
    ajax_post("/member/repeatPayPwdsendByEmail", JSON.stringify({email: email}), "application/json",
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

function checkKeyWord(){
    ajax_get("/member/account?"+(new Date()).getTime(),"","application/json",
        function (data) {
            if(data.errorCode == 0){
                $("#apply-list").hide();
                if(data.hasKeyWord == 'true'){
                    $("#Top-up-remittance").show("normal", function() {
                        $("#sendApply input").val("");
                        $("#receipt_account").text("");
                        $("#receipt_payee").text("");
                        gain_receipt_mode();
                        init_payers();
                        $("#instructions-submit").attr("disabled",false).removeAttr('style').val("提交");
                    });
                }else{
                    //初次充值，传true，弹出提示
                    paypw_set(true);
                }
            }
        },
        function (xhr, status) {
        }
    );
}

//获取收款方式数据
function gain_receipt_mode() {
    ajax_get("/member/getremos?"+(new Date()).getTime(), {}, undefined,
        function(data) {
            if (data.suc) {
                var optionHTML = '<option value="" selected="selected">请选择</option>';
                $.each(data.list, function(i, item) {
                    optionHTML += '<option value="' + item.id + '" data-mode="' + item.account + '——' + item.payee + '">' + item.remark + '</option>';
                });
                $("select[name='recipientId']").empty().append(optionHTML);
            } else {
                layer.msg(data.msg, {icon: 6, time: 2000});
            }
        },
        function(xhr, status) {
            layer.msg("获取收款方式数据出错，请稍后重试！", {icon: 6, time: 2000});
        }
    );
}

//检查账户是否被冻结
function checkFrozen() {
    $.ajax({
        url : "/member/checkFrozen?"+(new Date()).getTime(),
        type : "GET",
        dataType : "json",
        async : true,
        contentType : "application/json",
        success : function(data) {
            if(data.code == 100){
                var result = data.obj.result;
                // var credits = data.credits;
                var acPeriond = data.acPeriond;
                 if(result.frozen){
                    //账户被冻结
                    // $(".personal").replaceWith("<div class='frozen'><p>您的账户已被冻结，请联系管理员！</p></div>");
                    $("button[tag=frozen]").show();
                }else{
                     $("button[tag=frozen]").hide();
                }
                //首次访问个人中心
                $("#balance").text(fmoney(result.balance,2));
                var nick = data.nick;
                $("#member").text(nick ? (nick + "("+result.email+")") : result.email);
                if(acPeriond){
                    $("#credit_limit").text(acPeriond.totalLimit);
                    $("#available_credit_limit").text(acPeriond.leftLimit);
                    $("#used_amount").text(acPeriond.usedLimit);
                    $("#startTime").text(acPeriond.startTimeStr);
                    $("#forRefundTime").text(acPeriond.contractPeriodDateStr);
                    $("#lastForRefundTime").text(acPeriond.redLineDateStr);
                    if(acPeriond.state == 4){
                        $("#disable-the").show();
                    }
                }
                $(".time-up:eq(1)").text(result.historys.lastLoginTime);
                $("td.user_name").text(data.nick == "" ? result.email : data.nick);
              /*  if(result.frozenAmount != undefined && result.frozenAmount != null
                    && result.frozenAmount != '')
                    $("#frozen_balance").text(fmoney(result.frozenAmount,2));
                if(credits[0] != null){
                    creditInformation(credits);
                }else{
                    $(".six-user .user-quota").find("li").eq(2).hide();
                    $($(".six-user .six-user-name")[1]).hide();
                    $(".six-user .limit-validity").hide();
                }*/
            }
        }
    });
}

//获取付款方式
function init_payers(){
    ajax_get("/member/getTransferAccount?"+(new Date()).getTime(), {}, undefined,
        function(data) {
            if (data.suc) {
                var selectbank ;
                var optionHTML = '<option value = "" >请选择</option>';
                /* $("select[name='transferCard']").empty().removeAttr("disabled");*/
                $.each(data.accounts, function(i, item) {
                    if(!i){
                        optionHTML += '<option selected="selected" >' + item.bankName+ '</option>';
                        selectbank = item.bankName;
                        $("input[name = 'distributorName']").val(item.payerName);
                        $("input[name = 'transferCard'][tag = 'validate']").val(item.transferCard);
                    }
                    /*if(item.bankName == selectbank){
                     $("<option></option>").text(item.transferCard).appendTo($("select[name='transferCard']"));
                     }*/
                });
                $.each(data.bankNames, function(i, item) {
                    if(item != selectbank){
                        optionHTML += '<option >' + item + '</option>';
                    }
                });
                $("select[name='transferType']").change(function(){selectPayType(data.accounts)});
                /*$("select[name='transferCard']").change(function(){selectPayCard(data.accounts)}); */
                $("select[name='transferType']").empty().append(optionHTML).removeAttr("disabled");
            }
            else {
                var optionHTML = '<option selected="selected" value = "" >请点击添加付款账户</option>';
                $("select[name='transferType']").empty().append(optionHTML).attr("disabled", "disabled");
                /*var cardoption = '<option selected="selected" ></option>';
                 $("select[name='transferCard']").empty().append(cardoption).attr("disabled", "disabled");*/
                $("input[name = 'transferCard'][tag = 'validate']").val("");
                $("input[name = 'distributorName']").val("");
            }
        },
        function(xhr, status) {
            layer.msg("获取付款方式数据出错，请稍后重试！", {icon: 6, time: 2000});
        }
    );
}

//选择付款方式
function selectPayType(accounts){
    /*var $select = $("select[name='transferCard']");
     $select.empty();*/
    var iinput = $("input[name = 'transferCard'][tag = 'validate']");
    var type = $("select[name='transferType']").val();
    if(!type){
        /* var cardoption = '<option selected="selected" value = "" ></option>';
         $("select[name='transferCard']").empty().append(cardoption);*/
        iinput.val("");
        $("input[name = 'distributorName']").val("");
    }else{
        var list =new Array();
        $.each(accounts,function(i,item){
            if(item.bankName == type){
                list.push({"name":item.payerName,"card":item.transferCard});
            }
        });
        if(list.length == 1){
            $("input[name = 'distributorName']").val(list[0].name);
            /*$("<option></option>").text(list[0].card).appendTo($select);
             $select.val(list[0].card);*/
            iinput.val(list[0].card);
        }else{
            $("input[name = 'distributorName']").val("");
            $.each(list,function(i,item){
                if(!i){
                    $/*("<option></option>").text(item.card).appendTo($select);
                     $select.val(item.card);*/
                    $("input[name = 'distributorName']").val(item.name);
                    iinput.val(item.card);
                }else{
                    /*$("<option></option>").text(item.card).appendTo($select);*/
                    iinput.val("");
                }
            })
        }
    }
}
//选择付款账户 
//停用 不支持选择  目前为一种付款方式一个账号
/*function selectPayCard(accounts){
 var card = $("select[name='transferCard']").val();
 var type = $("select[name='transferType']").val();
 if(!type){
 layer.msg("请选择付款方式！", {icon: 6, time: 2000});
 $("input[name = 'distributorName'").val("");
 return;
 }else{
 $.each(accounts,function(i,item){
 if(item.bankName == type&&item.transferCard ==card){
 $("input[name = 'distributorName'").val(item.payerName);
 }
 });
 }
 }*/

//获取弹出框HTML内容
function modify_payer_content(account, call) {
    ajax_get("/member/getbanks", {}, undefined,
        function(response) {
            var banks = response.list;
            var optionHTML = '';
            for (var i in banks) {
                optionHTML += '<option value="' + banks[i].bankName + '" ' + (account.bankName == banks[i].bankName ? 'selected="selected"' : '') + '>' + banks[i].bankName + '</option>\n';
            }
            call(
                '<div style="width: 450px; height: 162px; border: 1px solid #ccc; margin-left: auto; margin-right: auto; padding: 10px;">'+
                '<table style="table-layout: fixed; width: 100%; border:"  id = "alter_payer">'+
                '<tr style ="width: 450px; display: block; margin-top: 20px;">'+
                '<th style = "width: 24%; display: inline-block;  text-align: right;"><font style="color:#e4393c">*</font>付款方式:</th>'+
                '<td style = "width: 73%; display: inline-block;  text-align: left;">'+
                '<select name = "bankName" style = "width: 42%; margin-right: 10px; padding: 4px;margin-left: 5px; ">'+
                '<option value = "" >请选择</option>'+
                optionHTML +
                '<option value = "10" ' + (account.customStatus == undefined || account.customStatus  != "1" ?  '' : 'selected="selected"') + ' >其它</option>'+
                '</select>'+
                '<input type="text" value="' + (account.customStatus == undefined || account.customStatus  != "1" ? '' : account.bankName) + '" placeholder = "请输入其它付款方式   "  name = "otherBankName"  maxlength = "22" style="width: 24%;display: ' + (account.customStatus == undefined || account.customStatus != "1" ? 'none' : 'inline-block') + ';padding: 5px;position: absolute;left: 288px;height: 13px;x: textfield;top: 32px;-webkit-appearance: textfield;-moz-appearance: textfield;-ms-appearance: textfield;">'+
                '</td>'+
                '</tr>'+
                '<tr style ="width: 450px; display: block; margin-top: 20px;">'+
                '<th style = "width: 24%; display: inline-block;  text-align: right;"><font style="color:#e4393c">*</font>付款账户:</th>'+
                ' <td style = "width: 73%; display: inline-block;  text-align: left;">'+
                '<input type="text" name = "transferCard"  value = "' + (account.transferCard == undefined ? '' : account.transferCard ) + '" tag = "pay" maxlength = "22" style = "width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;"/>'+
                '</td> '+
                '</tr>'+
                '<tr style ="width: 450px; display: block; margin-top: 20px;">'+
                '<th style = "width: 24%; display: inline-block;  text-align: right;"><font style="color:#e4393c">*</font>账户开户名:</th>'+
                '<td style = "width: 73%; display: inline-block;  text-align: left;">'+
                '<input type="text" name = "payerName" value = "'+(account.payerName == undefined ? '' : account.payerName)+'" maxlength = "22" tag = "pay" style = "width: 80%; display: block; padding: 5px; appearance: textfield;margin: 5px; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;" / >'+
                ' <input type = "hidden" name = "id" value = "'+(account.id == undefined ? '' : account.id)+'">'+
                ' </td>'+
                '</tr>'+
                '</table>'+
                '</div>'
            );
        },
        function(xhr, status) {
            layer.msg('获取付款方式出错，请稍候重试', {icon : 2, time : 2000});
        }
    );
}

//获取表单验证后的参数
function getPayerParamters() {
    var bankName = $("#alter_payer select[name='bankName']").val();
    var payerName = $("#alter_payer input[name = 'payerName']").val().trim();
    var transferCard = $("#alter_payer input[name = 'transferCard']").val().trim();
    var otherBankName = $("input[name = 'otherBankName']").val().trim();
    var customStatus = undefined;
    if (bankName == "") {
        layer.msg('请选择付款方式', {icon : 2, time : 2000});
        return undefined;
    }
    if(bankName =="10"){
        if(otherBankName == ""){
            layer.msg('请填写其它付款方式', {icon : 2, time : 2000});
            return undefined;
        }else{
            customStatus = 1;
            bankName = otherBankName;
        }
    }else{
        customStatus = 0;
    }
    if (payerName == "") {
        layer.msg('请输入账户开户名', {icon : 2, time : 2000});
        return undefined;
    }
    if (transferCard == "") {
        layer.msg('请输入付款账户', {icon : 2, time : 2000});
        return undefined;
    }
    return {
        bankName: bankName,
        payerName: payerName,
        transferCard: transferCard,
        id:$("input[name = 'id']").val(),
        customStatus:customStatus
    };
}

//新增付款账户
function add_payer() {
    modify_payer_content(new Object(), function(content) {
        layer.open({
            type: 1,
            title: "添加付款账户",
            content: content,
            area: ['500px', '280px'],
            btn: ["保存", "取消"],
            closeBtn: 1,
            shadeClose: false,
            success: function(layero, index){
                //初始化界面后成功回调
                $("select[name = 'bankName']").change(function(){
                    var type = $("select[name = 'bankName']").val();
                    if(type == "10"){
                        $("input[name = 'otherBankName']").show().removeAttr("disabled");
                    }else{
                        $("input[name = 'otherBankName']").hide().attr("disabled","disabled");
                    }
                });
            },
            //i和currdom分别为当前层索引、当前层DOM对象
            yes: function(i, currdom) {
                console.log(i + "<--i   currdom-->" + currdom[0]);
                var params = getPayerParamters();
                //保存新增付款账户信息
                if (params != undefined) {
                    ajax_post("/member/editTransferAccount", params, undefined,
                        function(response) {
                            if (response.success) {
                                layer.msg(response.msg, {icon : 1, time : 2000}, function() {
                                    init_payers();
                                });
                                layer.close(i);
                            } else if (response.code == "2") {
                                window.location.href = "login.html";
                            } else {
                                layer.msg(response.errorInfo, {icon : 5, time : 2000});
                            }
                        },
                        function(xhr, status) {
                            layer.msg('添加付款账户失败，请重试', {icon : 2, time : 2000});
                        }
                    );
                }
            }
        });
    });
}

//付款账户列表弹出框
function list_payer_content(call) {
    ajax_get("/member/getTransferAccount?"+(new Date()).getTime(), {}, undefined,
        function(response) {
            if (response.code == "2") {
                window.location.href = "login.html";
                return;
            }
            var list = response.accounts;
            var trHTML = '';
            for (var i in list) {
                trHTML +=
                    '<tr data-id="' + list[i].id + '" style=" display: block;margin-bottom:0;line-height: 30px;">\n' +
                    '<td style="width:25%; display: inline-block; text-align: center;">' + list[i].bankName + '</td>\n' +
                    '<td style="width:30%; display: inline-block; text-align: center;line-height: normal;word-break: break-all;">' + list[i].transferCard + '</td>\n' +
                    '<td style="width:20%; display: inline-block; text-align: center;line-height: normal;word-break: break-all;">' + list[i].payerName + '</td>\n' +
                    '<td style="width:18%; display: inline-block; text-align: center;">\n' +
                    '<input type="button" value="修改" class="modify_payer_btn" style="background-color: #117ad4; border: 1px solid #117ad4; color: white; cursor: pointer;"/>\n' +
                    '<input type="button" value="删除" class="delete_payer_btn" style="background-color: #117ad4; border: 1px solid #117ad4; color: white; cursor: pointer;"/>\n' +
                    '<input type = "hidden" id = "customStatus" value = "'+list[i].customStatus+'"/>'
                '</td>'
                '</tr>\n';
            }
            call(
                '<div style="width: 530px; height: auto; border: 1px solid #ccc; margin-left: auto; margin-right: auto; padding: 10px;">'+
                '<table style="table-layout: fixed; width: 100%; border:"  id = "payer_list">'+
                '<tr   style=" width:530px; display: block;background: #e8e8e8;height: 40px;line-height: 40px;">\n' +
                '<th style="width:25%; display: inline-block; text-align: center;">付款方式</th>\n' +
                '<th style="width:30%; display: inline-block; text-align: center;">付款账户</th>\n' +
                '<th style="width:20%;  display: inline-block; text-align: center;">账户开户名</th>\n' +
                '<th style="width:18%; display: inline-block; text-align: center;">操作</th>'+
                '</tr>\n'+
                trHTML+
                '</table>'+
                '</div>'
            )
        },function(xhr, status) {
            layer.msg('获取付款账户失败请重试，请稍候重试', {icon : 2, time : 2000});
        });
}

//付款账户列表
function list_payer() {
    list_payer_content( function(content) {
        layer.open({
            type: 1,
            title: "管理付款账户",
            content: content,
            area: ['570px', '280px'],
            closeBtn: 1,
            shadeClose: false,
            success: function(layero, index){
                //修改付款账户按钮事件绑定
                $("input.modify_payer_btn").click(function(event) {
                    alter_payer(this);
                });
                //删除付款账户按钮事件绑定
                $("input.delete_payer_btn").click(function(event) {
                    delete_payer(this);
                });
            }
        });
    });
}

//删除付款账户
function delete_payer(node) {
    var id = $(node).parent().parent().data("id");
    layer.confirm("确认删除该账户？", {icon: 3},
        //i和currdom分别为当前层索引、当前层DOM对象
        function(i, currdom) {
            console.log(i + "<--i   currdom-->" + currdom[0]);
            layer.close(i);

            ajax_get("/member/delTransferAccount?id="+id, {}, undefined,
                function(data) {
                    if(data.code == 2){
                        window.location.href = "login.html";
                    }else if (data.success) {
                        $(node).parent().parent().remove();
                        layer.msg(data.msg, {icon: 1, time: 2000});
                        init_payers();
                    } else {
                        layer.msg("删除失败请重试", {icon: 5, time: 2000});
                    }
                },
                function(xhr, status) {
                    layer.msg("删除收款方式数据出错，请稍后重试！", {icon: 6, time: 2000});
                });

        }
    );
}

//修改付款方式
function alter_payer(node){
    var $tds = $(node).parent().parent().children();
    var id = $(node).parent().parent().data("id");
    var account = new Object();
    account.bankName = $tds.eq(0).text();
    account.transferCard = $tds.eq(1).text();
    account.payerName = $tds.eq(2).text();
    account.customStatus = $tds.eq(3).find("#customStatus").val();
    account.id = id;

    modify_payer_content(account, function(content) {
        layer.open({
            type: 1,
            title: "添加付款账户",
            content: content,
            area: ['500px', '280px'],
            btn: ["保存", "取消"],
            closeBtn: 1,
            shadeClose: false,
            success: function(layero, index){
                //初始化界面后成功回调
                $("select[name = 'bankName']").change(function(){
                    var type = $("select[name = 'bankName']").val();
                    if(type == "10"){
                        $("input[name = 'otherBankName']").show().removeAttr("disabled");
                    }else{
                        $("input[name = 'otherBankName']").hide().attr("disabled","disabled");
                    }
                });
            },
            //i和currdom分别为当前层索引、当前层DOM对象
            yes: function(i, currdom) {
                console.log(i + "<--i   currdom-->" + currdom[0]);
                var params = getPayerParamters();
                //保存新增店铺信息
                if (params != undefined) {
                    ajax_post("/member/editTransferAccount", params, undefined,
                        function(response) {
                            if (response.success) {
                                layer.msg(response.msg, {icon : 1, time : 2000}, function() {
                                    init_payers();
                                    $tds.eq(0).text(params.bankName);
                                    $tds.eq(1).text(params.transferCard);
                                    $tds.eq(2).text(params.payerName);
                                    $tds.eq(3).find("#customStatus").val(params.customStatus);
                                });
                                layer.close(i);
                            } else if (response.code == "2") {
                                window.location.href = "login.html";
                            } else {
                                layer.msg(response.errorInfo, {icon : 5, time : 2000});
                            }
                        },
                        function(xhr, status) {
                            layer.msg('修改付款账户失败，请重试', {icon : 2, time : 2000});
                        }
                    );
                }
            }
        });
    });
}

/***************************************以下是在线支付的业务逻辑****************************************/

/**
 * 输入校验
 */
$("input.onlinePayTransferAmount").blur(function(){
    onlineTopUpAmountCheck()
});

function onlineTopUpAmountCheck(){
    var target = $("input.onlinePayTransferAmount").val();
    return !isNaN(target)&&target!="";
}

/**
 * 在线充值提交
 */
$(".box-right-twelve").on("click","#online_top_up",function(){
    //金额校验通过
    if(onlineTopUpAmountCheck()){
        var params = {
            'transferAmount':$("input.onlinePayTransferAmount").val(),
            'transferType':$("input[name='online-pay-type']:checked").val()
        };

        //生成状态为“未支付”的在线交易申请
        ajax_post("/member/sendOnlineApply", params, undefined,
            function(result) {
                if (result.success) {
                    //根据选中的支付方式，直接跳转
                    var sumPrice = result.bean.transferAmount;
                    var p = [{"name":encodeURIComponent("在线充值：" + sumPrice + "元")}];
                    var id = result.bean.id;
                    var tradeNo = result.bean.onlineApplyNo;
                    var alipayUrl = "../../payment/alipay.html?purno="+id+"&tradeNo="+tradeNo+"&productName="+encodeURIComponent(encodeURIComponent("在线充值：" + sumPrice + "元"))+"&sumPrice="+sumPrice;
                    var weinParam = {
                        id:id,
                        orderNo: tradeNo,
                        orderDes:"在线充值：" + sumPrice + "元" ,
                        totalPrice: sumPrice
                    }
                    var wxpayUrl = "javascript:apply_loadweixinPay("+JSON.stringify(weinParam)+");";
                    // var wxpayUrl = "../../payment/wechat.html?one="+result.bean.id+"&two="+result.bean.onlineApplyNo+"&three="+encodeURIComponent(encodeURIComponent("在线充值：" + result.bean.transferAmount + "元"))+"&four="+result.bean.transferAmount;
                    var yjpayUrl = "../../payment/yijipay.html?one="+id+"&two="+tradeNo+"&three="+sumPrice+"&four="+encodeURIComponent(encodeURIComponent("充值支付")) + "&five=ONLINEBANK&six=PC";

                    //var yjfWxUrl = "../../payment/yjfWx.html?one=" + id + "&two=" + tradeNo + "&three=" + sumPrice + "&four=" + encodeURIComponent(JSON.stringify(p)) +"&service=commonWchatTradeRedirect";
                    var yjfWxUrl = "../../payment/yijipay.html?one="+id+"&two="+tradeNo+"&three="+sumPrice+"&four="+encodeURIComponent(encodeURIComponent("充值支付")) + "&five=THIRDSCANPAY&six=PC";

                    if(params.transferType == "支付宝"){
                        window.open(alipayUrl, "_blank");
                    }
                    if(params.transferType == "微信"){
                        window.location.href = wxpayUrl;
                        return; 
                    }
                    if(params.transferType == "易极付"){
                        var yjfType = $("input[name='yjf-type']:checked").val();
                        if(yjfType == "yjf_card")
                            window.open(yjpayUrl, "_blank");
                        else if(yjfType == "yjf_wx")
                            window.open(yjfWxUrl, "_blank");
                    }

                    p_confirm_pay_result_dialog();
                } else{
                    layer.msg('在线充值申请失败，请稍后重试', {icon : 2, time : 2000});
                }
            },
            function(xhr, status) {
                layer.msg('在线充值申请失败，请稍后重试', {icon : 2, time : 2000});
            }
        );
    }
    else{
        layer.msg('请正确添加充值金额', {icon : 2, time : 2000});
    }
})

//load 微信支付弹出窗
function apply_loadweixinPay(param){
    var winxinHtml = $('#winxin_content');
    winxinHtml.load("../payment/wechat.html", function (response, status, xhr) {
        require(["wechat"], function (wechat) {
            var url = '../personal/personal.html?emnunum=16';
            $('.modal').fadeIn(300);
            init_win_payment(param,url,layer);
        }); 
    });
}

//确认支付结果弹出框
function p_confirm_pay_result_dialog() {
    layer.open({
        type: 1,
        title: '确认支付结果',
        content: '',
        move: false,
        btn: ['支付成功'],
        area: ["400", "140"],
        btn1: function() {
            layer.msg('订单校验中，请稍后查询。', {icon: 6, time: 3000});
            layer.closeAll();
            //支付成功
            window.location.href = "../../personal/personal.html";
        }
    });
}

/**
 * 初始化绑定的银行卡
 * Created by huchuyin 2016-9-21
 */
function initBindBankCard() {
	$("#wb_select").empty();
	var param = {};
    ajax_post("/member/getBindBankCard?"+Math.random(), JSON.stringify(param), "application/json",
        function(data) {
            if (data.suc) {
            	var optionHTML = "";
                if (deal_with_illegal_boolean(data.list)) {
                    $.each(data.list, function (i, item) {
                        //if (item.accountUnit != 'M站可结算余额') {//紧急处理
                            var bindBankCard = item.accountUnit + (item.accountType == 2 ? "" : ("(尾号" + item.withdrawAccount.substr(-4) + ")"));
                            optionHTML += '<option data-type="' + deal_with_illegal_json(item.accountType) + '" data-province="' + deal_with_illegal_json(item.accountProvince) + '" data-city="' + deal_with_illegal_json(item.accountCity) + '" value = "' + item.id + '">' + bindBankCard + '</option>';
                        //}
                    });
                }
	            $("#wb_select").html(optionHTML);
            } else if(data.code == "2") {
                window.location.href = "login.html";
            } else {
                layer.msg(data.msg, {icon : 2, time : 3000});
            }
        },
        function(xhr, status) {
			layer.msg(data.msg, {icon : 2, time : 3000});
        }
    );
}

/**
 * 管理银行卡列表
 * Created by huchuyin 2016-9-21
 */
function bindBankCardMgr(index) {
	var param = {};
	$.ajax({
        url: "/member/getBindBankCard?"+Math.random(),
        type: 'POST',
        data: JSON.stringify(param),
        contentType: "application/json",
        async: false,
        dataType: 'json',
        success: function (data) {
            if (data.suc) {
            	var bankCount = data.count;
            	var itemHTML = "";
	            itemHTML += '<p>共绑定<i id="bankCount" class="red">'+data.count+'</i>张银行卡</p>';
	            if(deal_with_illegal_boolean(data.list)) {
	            	$.each(data.list,function(i,item){
                        if (item.accountType != 2) {
                            var bindBankCard = item.accountUnit+"(尾号"+item.withdrawAccount.substr(-4)+")";
                            itemHTML += '<p>'
                                + '<b>'+bindBankCard+'</b>'
                                + '<button class="btn del_commission" data-account="'+item.withdrawAccount.substr(-4)+'" data-id="'+item.id+'">解绑</button>'
                                + '</p>';
                        } else {
                        	bankCount--;
                        }
		            });
	            }
	            $("#bindBankCardMgr").html(itemHTML);
	            $("#bankCount").html(bankCount);
	            bindEleEvent(index);
            } else if(data.code == "2") {
                window.location.href = "login.html";
            } else {
                layer.msg(data.msg, {icon : 2, time : 3000});
            }
        },
        error: function (XMLHttpRequest, textStatus) {
            layer.msg(data.msg, {icon : 2, time : 3000});
        }
    });
}

/**
 * 绑定元素事件
 * Created by huchuyin 2016-9-21 
 */
function bindEleEvent(index) {
	//解绑点击事件
	$(".del_commission").click(function() {
		var id = $(this).data("id");
		var account = $(this).data("account");
		var cIndex = layer.confirm("您确定要解绑尾号为"+account+"的银行账户吗？",{
			btn:['确定','取消']
		},function() {
			removeBindBankCard(id,index);
		});
	});
}

/**
 * 解除绑定银行卡 
 * @param {Object} id
 */
function removeBindBankCard(id,index) {
	var param = {
		id:id==undefined?"":id		
	};
	ajax_post("/member/removeBindBankCard?"+Math.random(), JSON.stringify(param), "application/json",
        function(data) {
            if (data.suc) {
            	layer.close(index);
                applyInfo();
            	//initBindBankCard();
            	layer.msg('您已成功取消绑定银行卡！', {icon: 1, time: 1000}, function() {});
            } else if(data.code == "2") {
                window.location.href = "login.html";
            } else {
                layer.msg(data.msg, {icon : 2, time : 3000});
            }
        },
        function(xhr, status) {
			layer.msg(data.msg, {icon : 2, time : 3000});
        }
    );
}

/**
 * 重新发送邮件
 * Created by huchuyin 2016-9-24 
 */
$('body').on('click', '#re_send_mail', function () {
	var loadIndex = layer.load(1);
	ajax_post("/member/reSendMail?"+Math.random(), null, null,
        function(data) {
            if (data.suc) {
            	layer.close(loadIndex);
            	layer.msg('邮件重新发送成功！', {icon: 1, time: 1000}, function() {
            		time(document.getElementById("re_send_mail"));
            	});
            } else if(data.code == "2") {
                window.location.href = "login.html";
            } else {
            	layer.close(loadIndex);
                layer.msg(data.msg, {icon : 2, time : 3000});
            }
        },
        function(xhr, status) {
        	layer.close(loadIndex);
			layer.msg("系统异常", {icon : 2, time : 3000});
        }
    );
});

/**
 * 初始化提示信息：申请限制
 * Created by huchuyin 2016-9-26 
 */
function initApplyLimit() {
	ajax_get("/member/getCWLimit", {}, "application/json",
        function(data) {
            if (data.suc) {
                var limit = data.msg;
                var itemHTML = '注：每月最多可提现'+deal_with_illegal_json(limit.permonthTimes)+'次，单次提现最低限额'+deal_with_illegal_json(limit.pertimeLeast) +'元';
            	$("#apply_limit").html(itemHTML);
            } else if (data.code == 2) {
                window.location.href = "login.html";
            } 
        },
        function(xhr, status) {
            var itemHTML = '注：每月最多可提现10次，单次提现最低限额500元';
            $("#apply_limit").html(itemHTML);
        }
    );
}

/**
 * 提现金额键盘输入事件
 * Created by huchuyin 2016-9-28 
 */
$('body').on('keyup', '#wb_amount', function () {
	setCounterFee();
});

/**
 * 切换提现账户事件
 * Created by huchuyin 2016-9-28
 */
$('body').on('change', '#wb_select', function () {
	setCounterFee();
});

/**
 * 设置提现金额手续费 
 */
function setCounterFee() {
	//可提现金额
	var wbBalance = $("#wb_balance").text();
	//输入非法
	if(isNaN(parseFloat($("#wb_amount").val()))) {
		$("#wb_amount").val("");
	}
	//若输入的提现金额大于可提现金额，则输入值固定为可提现金额
	if(parseFloat($("#wb_amount").val()) > parseFloat(wbBalance)) {
		$("#wb_amount").val(parseFloat(wbBalance));
	}
	//提现金额
	var value = $("#wb_amount").val();
	//手续费显示区域
	var counterFee = $("#counter_fee");
	//手续费隐藏域
	var counterFeeHidden = $("#counterFee");
	//获取银行账户省市
	var selectBankNo = $("#wb_select option:selected");
	var province = selectBankNo.data("province");
	var city = selectBankNo.data("city");
	var type = selectBankNo.data("type");
	//计算提现金额手续费
	if(("广东省" == province && "深圳市" == city) || type == 2) {
		//本地免手续费
		counterFee.text("手续费： 0  元");
		counterFeeHidden.val(0);
	} else {
		//异地手续费计算
		var counterFeeVal = 0;
		if(value == "" || value <=0) {
			//若值为空或小于等于0，则手续费为0
			counterFeeVal = 0;
		} else if(value <= 10000) {
			//若输入值大于0且小于等于10000，则手续费为5
			counterFeeVal = 5;
		} else {
			//若输入值大于10000，则按每加10万，手续费加5计算
			counterFeeVal = ((value%100000==0?0:1)+(parseInt(value / 100000,10) +1)) * 5;
		}
		if(isNaN(counterFeeVal)) {
			//值非法则值空
			$("#wb_amount").val("");
			counterFeeVal = 0;
		}
		counterFee.text("手续费： "+counterFeeVal+" 元");
		counterFeeHidden.val(counterFeeVal);
	}
}

/**
 * 处理验证码
 * Created by huchuyin 2016-10-7 
 */
function initPayCaptcha() {
	ajax_get("/member/getDisAccountInfo?"+(new Date()).getTime(), {}, undefined,
		function(data) {
			if (data.suc) {
				if (data.msg && data.msg.inputErrorNumTimes >= 3) {
					$("#wb_pay_img").click();
					$("#wb_pay_img").parent().show();
				} else {
					$("#wb_pay_img").parent().hide();
				}
            } else if (data.code == 2) {
                window.location.href = "login.html";
            } 
		},
		function(xhr, status) {
			layer.msg("系统异常", {icon : 2, time : 3000});
		}
	);
}