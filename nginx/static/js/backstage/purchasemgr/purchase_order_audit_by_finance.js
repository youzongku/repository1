/**
 * 采购单-客服确认
 * Created by Administrator on 2016/10/26.
 */
var laypage = undefined;
var layer = undefined;
var globalFinancePurchaseOrderMap = {};

function initAuditByFinance(lay, layd){
    laypage = layd;
    layer = lay;
    globalFinancePurchaseOrderMap = {}
}

function getSetting_purchaseorder_f() {
    globalCSPurchaseOrderMap={}
    var setting = {
        url:"/purchase/viewpurchasenew",
        postData:{status:"6"},
        rownumbers : true, // 是否显示前面的行号
        datatype : "json", // 返回的数据类型
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        // colNames:["订单编号"],
        colModel:[
            {label:"订单编号",name:"purchaseOrderNo",index:"purchaseOrderNo",align:"center",sortable:false},
            {label:"下单时间",name:"sorderDate",index:"purchaseDate",align:"center",sortable:true},
            {label:"名称",name:"nickName",index:"nickName",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"分销商",name:"email",index:"email",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"业务员",name:"customerService",index:"customerService",align:"center",sortable:false},
            {label:"录入人",name:"inputUser",index:"inputUser",align:"center",sortable:false},

            {label:"收款账户名",name:"purchaseOrderNo",index:"purchaseOrderNo",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                var val = rowObject.purchaseAudit ? rowObject.purchaseAudit.recipientName : "";
                return deal_with_illegal_value(val);
            }},
            {label:"收款账号",name:"purchaseOrderNo",index:"purchaseOrderNo",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                var val = rowObject.purchaseAudit ? rowObject.purchaseAudit.recipientAccount : "";
                return deal_with_illegal_value(val);
            }},
            {label:"付款人",name:"purchaseOrderNo",index:"purchaseOrderNo",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                var val = rowObject.purchaseAudit ? rowObject.purchaseAudit.transferName : "";
                return deal_with_illegal_value(val);
            }},
            {label:"付款金额（元）",name:"offlineMoney",index:"offlineMoney",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                return (cellvalue ? cellvalue : 0);
            }},
            {label:"付款流水号",name:"purchaseOrderNo",index:"purchaseOrderNo",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                var val = rowObject.purchaseAudit&&rowObject.purchaseAudit.transferNumber ? rowObject.purchaseAudit.transferNumber : "";
                return deal_with_illegal_value(val);
            }},
            {label:"付款截图",name:"purchaseOrderNo",index:"purchaseOrderNo",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                var val = undefined
                if(rowObject.purchaseAudit && rowObject.purchaseAudit.screenshotUrl){
                    val = "<img onclick='previewImg(this)' style='height: 3.5em!important;cursor: pointer;' src='/purchase/getApplyImage?id="+rowObject.purchaseAudit.id+"'/>"
                }
                return deal_with_illegal_value(val);
            }},
            {label:"支付方式",name:"paymentId",index:"paymentId",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                return getPayMethod(cellvalue)
            }},
            {label:"操作",sortable:false,align:"center", formatter:function(cellvalue, options, rowObject){
                globalFinancePurchaseOrderMap[rowObject.id] = rowObject
                var opt = //'<span style = "cursor:pointer;" id="'+rowObject.id+'" onclick="showFinancePurchaseOrderDetail(this)">详情</span>'+
                    '<span style = "cursor:pointer;" id="'+rowObject.id+'" onclick="auditByFinance(this)">去确认</span>'+
                    '<span style = "cursor:pointer;" id="'+rowObject.id+'" onclick="showFinanceAuditLogs(this)">查看日志</span>'
                return opt
            }}
        ],
        viewrecords : true,
        onPaging:function(pageBtn){
            // 清空缓存的数据
            globalFinancePurchaseOrderMap = {}
        },
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#orders_by_finance_pagination",//分页
        caption:"采购单（双击行，可查看采购单详情）",//表名称
        pagerpos : "center",
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        jsonReader:{
            root: "orders",  //数据模型
            page: "page",//数据页码
            total: "pages",//数据总页码
            records: "total",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        },
        ondblClickRow: function(rowid) {
            doShowFinancePurchaseOrderDetail(rowid)
        }
    }
    return setting;
}

function getFinancePurchaseOrderByConditions(){
    // 拿到原有的，清除掉
    var postData = $("#orders_by_finance_table").jqGrid("getGridParam", "postData");
    $.each(postData, function (k, v) {
        delete postData[k];
    });
    var params = {
        status: 6, seachFlag: $("#financeSearchInput").val().trim(),
        sorderDate: $("#financeStartTime").val(), eorderDate: $("#financeEndTime").val()
    }
    $("#orders_by_finance_table").jqGrid('setGridParam', {page:1, postData: params}).trigger("reloadGrid");
}

function showFinancePurchaseOrderDetail(obj){
    clickThisTr($(obj).parent().parent())
    var id = $(obj).attr("id")
    doShowFinancePurchaseOrderDetail(id)
}
function doShowFinancePurchaseOrderDetail(id){
    //$("#finance_purchaseorder_detail_div").empty()
    var rowObject = globalFinancePurchaseOrderMap[id]
    var csAuditLogs = rowObject.csAuditLogs
    var financeAuditLogs = rowObject.financeAuditLogs
    var details = rowObject.details;
    // OA审批单号
    $("#cs_purchaseorder_detail_div").find("span[name='oaAuditNoSpan']").html("OA审批单号："+deal_with_illegal_value(rowObject.oaAuditNo))
    // 录单备注信息
    var remarksHtml = '<div><span>备注人：'+rowObject.inputUser+'</span><em>备注时间：'+rowObject.sorderDate+'</em></div>' +
        '<div>备注：'+deal_with_illegal_value(rowObject.remarks ? rowObject.remarks : rowObject.busenessRemarks)+'</div>'
    $("#finance_purchaseorder_detail_div").find("div[name='remarksInfoDiv']").html(remarksHtml)
    // 客服备注信息
    var cs_auditUser='', cs_auditDate='', cs_remark='';
    if(csAuditLogs && csAuditLogs.length>0) {
        cs_auditUser = deal_with_illegal_value(csAuditLogs[0].auditUser);
        cs_auditDate = deal_with_illegal_value(csAuditLogs[0].auditDateStr);
        var jsonValue = eval("(" + csAuditLogs[0].jsonValue + ")");
        cs_remark = deal_with_illegal_value(jsonValue.remark)
    }
    var csRemarksHtml = '<div><span>备注人：'+deal_with_illegal_value(cs_auditUser)+'</span>' +
        '<em>备注时间：'+deal_with_illegal_value(cs_auditDate)+'</em></div>' +
        '<div>备注：'+deal_with_illegal_value(cs_remark)+'</div>'
    $("#finance_purchaseorder_detail_div").find("div[name='csRemarksInfoDiv']").html(csRemarksHtml)
    // 财务备注信息
    var finance_auditUser='', finance_auditDate='', finance_remark='';
    if(financeAuditLogs && financeAuditLogs.length>0){
        finance_auditUser=deal_with_illegal_value(financeAuditLogs[0].auditUser);
        finance_auditDate=deal_with_illegal_value(financeAuditLogs[0].auditDateStr);
        var jsonValue = eval("(" + financeAuditLogs[0].jsonValue + ")");
        if(jsonValue.paymentRemark) {
            finance_remark = jsonValue.paymentRemark
        }
        if(jsonValue.profitRemark) {
            if(finance_remark) {
                finance_remark += '<br/>'
            }
            finance_remark+=jsonValue.profitRemark
        }
    }
    var financeRemarksHtml = '<div><span>备注人：'+deal_with_illegal_value(finance_auditUser)+'</span>' +
        '<em>备注时间：'+deal_with_illegal_value(finance_auditDate)+'</em></div>' +
        '<div>备注：'+deal_with_illegal_value(finance_remark)+'</div>'
    $("#finance_purchaseorder_detail_div").find("div[name='financeRemarksInfoDiv']").html(financeRemarksHtml)
    // 商品信息
    var productInfoHtml = ''
    var totalQty = 0;
    for (var j in details) {
        var capFee = (details[j].capFee != null?details[j].capFee:details[j].purchasePrice);
        totalQty += details[j].qty;
        productInfoHtml += '<tr>' +
            '<td>'+ details[j].sku +'</td>' +
            '<td>'+ details[j].productName + (details[j].isgift?"<em class='red'>【赠】</em>":"")+'</td>' +
            '<td>'+ deal_with_illegal_value(details[j].expirationDate) +'</td>' +
            '<td>'+ deal_with_illegal_value(details[j].interBarCode) +'</td>' +
            '<td>'+ details[j].warehouseName +'</td>' +
            '<td>'+ (details[j].isgift ? (details[j].realPrice != null ? details[j].realPrice.toFixed(4) : details[j].purchasePrice.toFixed(4)) : details[j].purchasePrice.toFixed(4)) +'</td>' +
            '<td>'+ capFee.toFixed(2) +'</td>' +
            '<td>'+ details[j].qty +'</td>' +
            '<td>'+ (capFee*details[j].qty).toFixed(2) +'</td>' +
            '</tr>'
    }
    $("#finance_purchaseorder_detail_div").find("tbody[name='productInfoTbody']").html(productInfoHtml)

    var bbcPostage = rowObject.bbcPostage;
    var totalPrices  = rowObject.purchaseTotalAmount + (bbcPostage?bbcPostage:0);
    var couponsCode =  rowObject.couponsCode;
    var couponsAmount = rowObject.couponsAmount;
    var actually_paid = totalPrices;

    var sumInfoHtml = '<span>商品总数'+totalQty+'个</span>&nbsp;&nbsp;&nbsp;' +
        '<span>商品总计'+(rowObject.orderProTotal?parseFloat(rowObject.orderProTotal).toFixed(2):0.00)+'元</span>';
    if(rowObject.reducePrice){
        sumInfoHtml += '-&nbsp;&nbsp;'+
            '<span>(rowObject.purchaseType == 1?"活动优惠":"整单优惠")'+
            parseFloat(rowObject.reducePrice).toFixed(2)+'元</span>';
    }
    if(couponsCode){
        sumInfoHtml += '-&nbsp;&nbsp;<span>优惠金额'+parseFloat(couponsAmount).toFixed(2)+'元</span>';
        actually_paid -= couponsAmount;
    }
    if(bbcPostage){
        sumInfoHtml += '+&nbsp;&nbsp;<span>运费'+parseFloat(bbcPostage).toFixed(2)+'元</span>';
    }
    if(actually_paid <= 0){
        actually_paid = 0.00;
    }
    sumInfoHtml +=
        '&nbsp;&nbsp;=&nbsp;&nbsp;订单金额<span>' + parseFloat(actually_paid).toFixed(2) + '元</span>'
    $("#finance_purchaseorder_detail_div").find("p[name='sumInfoP']").html(sumInfoHtml)

    layer.open({
        type: 1,
        title: rowObject.purchaseOrderNo,
        content: $("#finance_purchaseorder_detail_div"),
        area: ['1100px', '510px'],
        shadeClose: true, //点击遮罩关闭
        scrollbar: false,
        move: false,
        btn: ['关闭'],
        yes:function(index){
            layer.close(index)
        }
    });
}

// 查看日志
function showFinanceAuditLogs(obj){
    clickThisTr($(obj).parent().parent())

    var id = $(obj).attr("id")
    var rowObject = globalFinancePurchaseOrderMap[id]
    ajax_post("/purchase/allAuditLogs", JSON.stringify({purchaseOrderNo:rowObject.purchaseOrderNo}),
        "application/json",
        function (data) {
            var allAuditLogs = data.allAuditLogs;
            var tbody = $("#finance_auditLogs").find("table tbody");
            tbody.empty();

            var html = '';
            // 时间 xxx（管理员）客服确认通过/客服确认关闭/财务确认已支付
            // /财务确认未支付/财务确认通过/财务确认不通过
            if(allAuditLogs.length > 0){
                for(var i in allAuditLogs){
                    var log = allAuditLogs[i]
                    var jsonValue = eval("(" + log.jsonValue + ")")
                    var text = "<span style='display: inline-block; margin-left: 20px'>"+formatDateTime(log.auditDate)+"</span>"+
                        "<span style='display: inline-block; margin-left: 40px'>"+log.auditUser+"(管理员)</span>"
                    if(log.auditType==1){// 客服审核的
                        if(log.status==6)
                            text+="<span  style='display: inline-block; margin-left: 40px'>客服确认通过</span>"
                        else if(log.status==2)
                            text+="<span  style='display: inline-block; margin-left: 40px'>客服确认关闭</span>"
                    }else{// 财务审核的
                         // 0和1
                        if(jsonValue.paid==0){// 未支付
                            text+="<span  style='display: inline-block; margin-left: 40px'>财务确认未支付</span>"
                        }else{// 已支付
                            // true/false
                            if(jsonValue.profitPassed){// 财务通过
                                text+="<span  style='display: inline-block; margin-left: 40px'>财务确认通过</span>"
                            }else{// 财务未通过
                                text+="<span  style='display: inline-block; margin-left: 40px'>财务确认不通过</span>"
                            }
                        }
                    }
                    html += '<tr><td>'+text+'</td></tr>'
                }
            }else{
                html = '<tr><td>暂无操作日志</td></tr>'
            }
            tbody.html(html)

            layer.open({
                type: 1,
                title: "操作日志",
                area: ['450px', "500"],
                shadeClose: true, //点击遮罩关闭
                content: $("#finance_auditLogs"),
                scrollbar: false,
                btn: ['取消'],
                yes:function(index){
                    layer.close(index)
                }
            });
        }
    )
}

// 是否支付选择
function payOrNotPay(obj){
    if($(obj).val()==0){// 未支付
        $("#receivedAmount").parent().hide()
        $("#receivedTime").parent().hide()
        $("#auditReason").parent().show()
        $("#star_simbol_payment").show()// 备注必填
    }else{// 已支付
        $("#receivedAmount").parent().show()
        $("#receivedTime").parent().show()
        $("#auditReason").parent().hide()
        $("#star_simbol_payment").hide()// 备注非必填
    }
}

// 是否支付选择
function profitPassed(obj){
    if($(obj).val()=='true'){// 利润通过
        $("#star_simbol_profit").hide()// 备注非必填
    }else{// 利润未通过
        $("#star_simbol_profit").show()// 备注必填
    }
}

// 审核
function auditByFinance (obj) {
    clickThisTr($(obj).parent().parent())

    var id = $(obj).attr("id")
    var rowObject = globalFinancePurchaseOrderMap[id]

    var purchaseOrderNo = rowObject.purchaseOrderNo

    var bbcPostage = rowObject.bbcPostage?rowObject.bbcPostage:0;
    var totalPrices  = rowObject.purchaseTotalAmount + bbcPostage;
    var couponsCode =  rowObject.couponsCode;
    var couponsAmount = rowObject.couponsAmount;
    var actuallyPaid  = totalPrices;
    if(couponsCode){
        actuallyPaid  -= couponsAmount;
    }
    if(actuallyPaid  <= 0){
        actuallyPaid  = 0.00;
    }

    var payType = rowObject.paymentId
    var height = '330px'
    var html = '<div><span>订单金额：'+ parseFloat(actuallyPaid).toFixed(2)  +
        '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
        '订单商品到仓价总计：<label id="totalAWPrice_label"></label>'+'</span></div>'
    // 现金或线下转账
    if(payType && (payType=='cash' || payType=='cash-noline')){
        height = '530px'
        html += '<div id="payment_div" class="payment_div">'+
            '<span>'+
            '<span><em style="color:red">*</em>确认信息：</span>'+
            '<input type="radio" value="1" name="paid" onchange="payOrNotPay(this)" checked>确认已支付'+
            '<input type="radio" value="0" name="paid" onchange="payOrNotPay(this)">确认未支付' +
            '</span>'+
            '<span><span><em style="color:red">*</em>实收金额：</span><input id="receivedAmount" type="text"></span>'+
            '<span><span><em style="color:red">*</em>到账时间：</span><input id="receivedTime" type="text" onclick="laydate({istime: true, format: \'YYYY-MM-DD hh:mm:ss\',max:laydate.now()})"></span>'+
            '<span style="display: none"><span><em style="color:red">*</em>审核原因：</span><select id="auditReason" style="margin: 0 5px">' +
            '<option value="">请选择审核原因</option>' +
            '<option value="1">未收到货款</option>' +
            '<option value="2">其他</option>' +
            '</select>' +
            '</span>'+
            '<span style="height: 130px;">' +
            '<span style="vertical-align: top;">' +
            '<b style="color:red;display: none" id="star_simbol_payment">*</b>备注：' +
            '</span>' +
            '<textarea cols="50" rows="8" id="paymentRemark"></textarea>'+
            '</span>'+
            '</div>'
    }

    html += '<div id="profit_div" class="profit_div">'+
        '<span style="height:30px;line-height: 30px;">'+
        '确认订单利润：'+
        '<input onchange="profitPassed(this)" type="radio" value="true" name="profitPassed" checked>确认通过'+
        '<input onchange="profitPassed(this)" type="radio" value="false" name="profitPassed">确认不通过'+
        '</span>'+
        '<span style="height: 130px;">'+
        '<span style="vertical-align: top;">' +
        '<b style="color:red;display: none" id="star_simbol_profit">*</b>备注：' +
        '</span>'+
        '<textarea cols="50" rows="8" id="profitRemark"></textarea>'+
        '</span>'+
        '</div>'
    var content = '<div id="financeComfirmOrderDiv" class="financeComfirmOrderDiv">'+html+'</div>'

    layer.open({
        type: 1,
        title: "财务确认",
        area: ['450px', height],
        shadeClose: true, //点击遮罩关闭
        content: content,
        scrollbar: false,
        btn: ['提交','取消'],
        yes:function(index){
            var param = {}
            var paid = $("input[name='paid']:checked").val()
            if(payType && (payType=='cash' || payType=='cash-noline')){
                // 审核通过才有金额、时间输入框
                if(paid != 0){
                    var receivedAmount = $("#receivedAmount").val()
                    var receivedTime = $("#receivedTime").val()
                    if(!receivedAmount){
                        layer.msg("实收金额不能为空！", {icon: 2, time: 2000})
                        $("#receivedAmount").focus()
                        return;
                    }
                    if(!isMoneyPattern(receivedAmount)){
                        layer.msg("实收金额格式不正确（最多两位小数）！", {icon: 2, time: 2000})
                        $("#receivedAmount").val("")
                        $("#receivedAmount").focus()
                        return;
                    }
                    if(parseFloat(receivedAmount) < parseFloat(actuallyPaid)){
                        layer.msg("实收金额必须大于等于订单金额", {icon: 0, time: 2000});
                        $("#receivedAmount").focus()
                        return;
                    }
                    if(!receivedTime){
                        layer.msg("到账时间不能为空！", {icon: 2, time: 2000})
                        $("#receivedTime").focus()
                        return;
                    }
                    param["receivedAmount"] = receivedAmount
                    param["receivedTime"] = receivedTime
                }else{// 支付不通过
                    var auditReason = $("#auditReason").val()
                    if(!auditReason){
                        layer.msg("请选择审核原因！", {icon: 2, time: 2000})
                        return
                    }
                    param["auditReason"] = auditReason
                }
            }
            var paymentRemark = $("#paymentRemark").val()
            // 支付不通过，备注必填
            if(paid == 0){
                if(!paymentRemark){
                    layer.msg("确认支付信息备注不能为空！", {icon: 2, time: 2000})
                    $("#paymentRemark").focus()
                    return
                }
            }

            var profitPassed = $("input[name='profitPassed']:checked").val()
            var profitRemark = $("#profitRemark").val()
            // 利润不通过，备注必填
            if(profitPassed=="false"){
                if(!profitRemark){
                    layer.msg("确认订单利润备注不能为空！", {icon: 2, time: 2000})
                    $("#profitRemark").focus()
                    return
                }
            }

            param["purchaseOrderNo"] =  purchaseOrderNo,
            param["paid"] =  paid,
            param["paymentRemark"] =  paymentRemark,
            param["profitPassed"] =  profitPassed,
            param["profitRemark"] =  profitRemark
            var loading_index = layer.load(1, {shade: 0.5});
            // 客服审核
            ajax_post("/purchase/auditByFinance", JSON.stringify(param),"application/json",function (data) {
                if(data.suc){
                    layer.msg(data.msg, {icon: 1, time: 2000}, function () {
                        layer.close(index)
                        layer.close(loading_index)
                        getFinancePurchaseOrderByConditions()
                    })
                }else {
                    layer.msg(data.msg, {icon: 2, time: 2000},function(){
                        layer.close(loading_index)
                    })
                }
            })
        }
    });

    // TODO 没有再查询到仓价，旧数据问题
    if(!$("#totalAWPrice_"+purchaseOrderNo).text()){
        // 计算总到仓价
        ajax_post("/purchase/totalAWPrice",JSON.stringify({purchaseOrderNo:purchaseOrderNo}), "application/json",
            function (data) {
                $("#totalAWPrice_label").text(parseFloat(data.totalArriveWarehousePrice).toFixed(2)+"元");
            }
        )
    }else{
        // 从详情那查询
        $("#totalAWPrice_label").text($("#totalAWPrice_"+purchaseOrderNo).text()+"元");
    }
}

//详情展示
function showCashOrderSymbol(type, obj, purchaseOrderNo) {
    if (type == 1) {
        // TODO 没有再查询到仓价，旧数据问题
        if(!$("#totalAWPrice_"+purchaseOrderNo).text()){
            // 计算总到仓价
            ajax_post("/purchase/totalAWPrice",JSON.stringify({purchaseOrderNo:purchaseOrderNo}), "application/json",
                function (data) {
                    $("#totalAWPrice_"+purchaseOrderNo).text(parseFloat(data.totalArriveWarehousePrice).toFixed(2))
                }
            )
        }
        $(obj).css("display", "none");
        $(obj).next().css("display", "inline-block");
        $(obj).parent().parent().next().css("display", "table-row");
    } else {
        $(obj).css("display", "none");
        $(obj).prev().css("display", "inline-block");
        $(obj).parent().parent().next().css("display", "none");
    }
}