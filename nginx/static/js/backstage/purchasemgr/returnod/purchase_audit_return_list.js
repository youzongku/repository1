/**
 * Created by Administrator on 2017/2/14.
 */

var laypage = undefined;
var flag = undefined;
var auditro_globalReturnOrderMap = {}
var logsHtmlJson = {
    1:"提交退货申请",
    2:"审核退货申请通过",
    3:"审核退货申请不通过",
    4:"取消退货申请"
}
function initAuditReturnOrderList(layerParam, laypageParam){
    layer = layerParam;
    laypage = laypageParam;
    console.log("initAuditReturnOrderList")
    auditro_globalReturnOrderMap = {}
}

function auditro_reloadROs(){
    $("#auditro_return_orders_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}

function auditro_getSetting_ro() {
    auditro_globalReturnOrderMap = {}
    var setting = {
        url:"/purchase/returnod/manager/list/tobeaudit",
        rownumbers : true, // 是否显示前面的行号
        datatype : "json", // 返回的数据类型
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        // colNames:["订单编号"],
        colModel:[
            {label:"退货单号",name:"returnOrderNo",index:"returnOrderNo",align:"center",sortable:false},
            {label:"申请时间",name:"applicationTime",index:"application_time",align:"center",sortable:true, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(rowObject.applicationTimeStr)
            }},
            {label:"退款金额（元）",name:"actualTotalReturnAmount",index:"actual_total_return_amount",align:"center",sortable:true},
            {label:"分销商账号",name:"email",index:"email",align:"center",sortable:false},
            {label:"分销商昵称",name:"nickName",index:"nickName",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"业务员",name:"salesman",index:"salesman",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"状态",name:"statusStr",index:"status",align:"center",sortable:false},
            {label:"操作",sortable:false,align:"center", formatter:function(cellvalue, options, rowObject){
                auditro_globalReturnOrderMap[rowObject.id] = rowObject
                var opt = ''
                if(canAudit(rowObject.status)){
                    opt += "<a href='javascript:;' id='"+rowObject.id+"' onclick='auditReturnOrder(this)'>审核</a><br/>"
                }
                opt += "<a href='javascript:;' id='"+rowObject.id+"' onclick='showReturnOrderLogs(this)'>查看日志</a>"
                return opt
            }}
        ],
        viewrecords : true,
        onPaging:function(pageBtn){
            // 清空缓存的数据
            globalMarketingOrderMap = {}
        },
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#auditro_return_orders_pagination",//分页
        caption:"退货单列表（双击行，可查看退货单详情）",//表名称
        pagerpos : "center",
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        jsonReader:{
            root: "list",  //数据模型
            page: "currPage",//数据页码
            total: "totalPage",//数据总页码
            records: "rows",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        },
        ondblClickRow: function(rowid) {
            doShowAuditReturnOrderDetail(rowid)
        },
        serializeGridData : function(postData) {
            return JSON.stringify(auditro_getParams());
        }
    }

    return setting;
}

function doShowAuditReturnOrderDetail(id){
    var productInfoTableBody = $("#auditro_ro_detail_div").find("table[name='productInfoTable'] tbody")
    var totalReturnAmountSpan = $("#auditro_ro_detail_div").find("span[name='totalReturnAmount']")
    var userExpectTotalReturnAmountSpan = $("#auditro_ro_detail_div").find("span[name='userExpectTotalReturnAmount']")
    var actualTotalReturnAmountSpan = $("#auditro_ro_detail_div").find("span[name='actualTotalReturnAmount']")
    var purchaseInfoTableBody = $("#auditro_ro_detail_div").find("table[name='purchaseInfoTable'] tbody")
    productInfoTableBody.empty()
    actualTotalReturnAmountSpan.empty()
    purchaseInfoTableBody.empty()
    layer.open({
        type: 1,
        title: "退货单详情",
        content: $("#auditro_ro_detail_div"),
        area: ['1100px', '510px'],
        btn: ["关闭"],
        closeBtn: 1,
        shadeClose: true,
        move: false,
        success: function(){
            var returnOrder = auditro_globalReturnOrderMap[id]
            var details = returnOrder.details
            var productInfoHtml = ''
            var purchaseInfoHtml = ''
            for(var i in details){
                var returnUnitPrice = parseFloat(details[i].capfee)
                if(details[i].coefficient){
                    returnUnitPrice = (returnUnitPrice*parseFloat(details[i].coefficient)).toFixed(2)
                }
                productInfoHtml += '<tr>'+
                    '<td>'+details[i].sku+'</td>'+
                    '<td>'+details[i].productTitle+'</td>'+
                    '<td>'+details[i].expirationDate+'</td>'+
                    '<td>'+details[i].warehouseName+'</td>'+
                    '<td>'+returnUnitPrice+'</td>'+
                    '<td>'+details[i].returnQty+'</td>'+
                    '<td>'+deal_with_illegal_value(details[i].subTotalReturnAmount)+'</td>'+
                    '</tr>'

                purchaseInfoHtml += '<tr>'+
                    '<td>'+details[i].sku+'</td>'+
                    '<td>'+details[i].purchaseOrderNo+'</td>'+
                    '<td>'+deal_with_illegal_value(details[i].purchaseTime)+'</td>'+
                    '<td>'+details[i].capfee+'</td>'+
                    '</tr>'
            }

            productInfoTableBody.html(productInfoHtml);
            totalReturnAmountSpan.html("（系统计算）预计退款金额："+deal_with_illegal_value(returnOrder.totalReturnAmount)+"元")
            userExpectTotalReturnAmountSpan.html("用户要求退款金额："+deal_with_illegal_value(returnOrder.userExpectTotalReturnAmount)+"元")
            if(returnOrder.status==2){// 要审核通过才有实际退款
                actualTotalReturnAmountSpan.html("实际退款金额："+deal_with_illegal_value(returnOrder.actualTotalReturnAmount)+"元");
            }else{
                actualTotalReturnAmountSpan.html("实际退款金额："+deal_with_illegal_value(undefined)+"元");
            }
            purchaseInfoTableBody.html(purchaseInfoHtml);
        },
        btn1: function (index) {
            layer.close(index)
        }
    });
}

// 是否可以进行审核操作
function canAudit(status){
    if(status && status==1){
        return true;
    }
    return false;
}

function auditro_getParams(){
    var param = {
        currPage:$('#auditro_return_orders_table').getGridParam('page'),
        pageSize:$('#auditro_return_orders_table').getGridParam('rowNum'),
        startApplicationDate:$("#auditro_ro_startApplicationDate").val(),
        endApplicationDate:$("#auditro_ro_endApplicationDate").val(),
        sort:$('#auditro_return_orders_table').getGridParam("sortname"),
        filter:$('#auditro_return_orders_table').getGridParam("sortorder"),
        searchText:$.trim($("#auditro_ro_searchText").val())
    };
    if($("#auditro_ro_status").val()){
        param["status"] = $("#auditro_ro_status").val()
    }
    return  param;
}

function showReturnOrderLogs(obj){
    clickThisTr($(obj).parent().parent())

    var id = $(obj).attr("id");
    var rowObject = auditro_globalReturnOrderMap[id]
    $("#auditro_ro_logsDiv").empty()
    layer.open({
        type: 1,
        title: "退货单日志",
        content: $("#auditro_ro_logsDiv"),
        area: ['500px', '400px'],
        btn: ["关闭"],
        closeBtn: 1,
        shadeClose: true,
        move: false,
        success: function(){
            var logs = rowObject.logs;
            if(logs && logs.length>0) {
                var logHtml = ''
                for(var i=0;i<logs.length;i++){
                    logHtml+='<p>'+logs[i].createTimeStr+'　　　'+deal_with_illegal_value(logs[i].createUser)+"　"+logsHtmlJson[logs[i].status]+'</p>'
                }
                $("#auditro_ro_logsDiv").html(logHtml)
            }
        },
        btn1: function (index) {
            layer.close(index)
        }
    });
}

// 审核
function auditReturnOrder(obj){
    clickThisTr($(obj).parent().parent())

    var id = $(obj).attr("id")
    var returnOrder = auditro_globalReturnOrderMap[id]
    $("#auditro_ro_auditDiv").find("span[name='userExpectTotalReturnAmountSpan']").html(deal_with_illegal_value(returnOrder.userExpectTotalReturnAmount)+"元");
    $("#auditro_ro_auditDiv").find("span[name='remarks']").html(deal_with_illegal_value(returnOrder.remarks));
    $("#auditro_ro_auditDiv").find("span[name='totalReturnAmountSpan']").html(deal_with_illegal_value(returnOrder.totalReturnAmount)+"元");
    $("#auditro_ro_auditDiv").find("input[name='auditActualTotalReturnAmount']").val(returnOrder.actualTotalReturnAmount);

    layer.open({
        type: 1,
        title: "审核退货单",
        content: $("#auditro_ro_auditDiv"),
        area: ['500px', 'auto'],
        btn: ["审核通过","审核不通过"],
        closeBtn: 1,
        shadeClose: false,
        move: false,
        btn1: function (index) {
            if(validateAuditParams()){
                commonAudit(id, 1, index, obj)
            }
        },
        btn2: function (index) {
            if(validateAuditParams()){
                commonAudit(id, 0, index, obj)
            }
        }
    });
}

/**
 *
 * @param id
 * @param passed   是否审核通过 0不通过 1通过
 * @param layerIndex  layer弹窗index
 * @param auditBtnObj   审核按钮对象
 */
function commonAudit(id, passed, layerIndex, auditBtnObj){
    var loading_index = layer.load(1, {shade: 0.5});
    var rowObject = auditro_globalReturnOrderMap[id]
    var returnOrderNo = rowObject.returnOrderNo
    var actualTotalReturnAmount = $.trim($("#auditro_ro_auditDiv").find("input[name='auditActualTotalReturnAmount']").val())
    var auditRemarks = $.trim($("#auditro_ro_auditDiv").find("textarea[name='auditRemarks']").val())
    var params = {
        returnOrderNo: returnOrderNo,
        passed: passed,
        auditRemarks: auditRemarks,
        actualTotalReturnAmount: actualTotalReturnAmount
    }

    $(auditBtnObj).attr("disabled","disabled")
    ajax_post("/purchase/returnod/audit",JSON.stringify(params),"application/json",function (data) {
        if(data.suc) {
            layer.msg(data.msg,{icon:1,time:2000},function () {
                $(auditBtnObj).remove()
                layer.close(layerIndex)
                layer.close(loading_index)
                auditro_reloadROs()
            });
        }else{
            layer.msg(data.msg,{icon:2,time:2000},function () {
                layer.close(loading_index)
            })
        }
    })
}

// 验证审核数据
function validateAuditParams(){
    var actualTotalReturnAmount = $.trim($("#auditro_ro_auditDiv").find("input[name='auditActualTotalReturnAmount']").val())
    if(!actualTotalReturnAmount){
        layer.msg("退款金额不能为空",{icon:1,time:2000})
        $("#auditro_ro_auditDiv").find("input[name='auditActualTotalReturnAmount']").focus()
        return false;
    }
    if(!isMoneyPattern(actualTotalReturnAmount)){
        layer.msg("金额格式不正确",{icon:1,time:2000})
        $("#auditro_ro_auditDiv").find("input[name='auditActualTotalReturnAmount']").val("")
        $("#auditro_ro_auditDiv").find("input[name='auditActualTotalReturnAmount']").focus()
        return false;
    }
    if(!isMoneyPattern(actualTotalReturnAmount)){
        layer.msg("请输入正确的金额格式",{icon:2,time:2000})
        $("#auditro_ro_auditDiv").find("input[name='auditActualTotalReturnAmount']").empty()
        $("#auditro_ro_auditDiv").find("input[name='auditActualTotalReturnAmount']").focus()
        return false;
    }
    return true;
}
