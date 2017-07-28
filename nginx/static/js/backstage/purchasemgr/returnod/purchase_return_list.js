/**
 * Created by Administrator on 2017/2/14.
 */

var laypage = undefined;
var flag = undefined;
var globalReturnOrderMap = {}
var logsHtmlJson = {
    1:"提交退货申请",
    2:"审核退货申请通过",
    3:"审核退货申请不通过",
    4:"取消退货申请"
}
function initReturnOrderList(layerParam, laypageParam){
    layer = layerParam;
    laypage = laypageParam;
    console.log("initReturnOrderList")
    globalReturnOrderMap = {}
}

function reloadROs(){
    $("#return_orders_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}

function getSetting_ro() {
    globalReturnOrderMap = {}
    var setting = {
        url:"/purchase/returnod/manager/list",
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
                globalReturnOrderMap[rowObject.id] = rowObject
                // if(canAudit(rowObject.status)){
                //     opt += "<a href='javascript:;' id='"+rowObject.id+"' onclick='auditReturnOrder(this)'>审核</a><br/>"
                // }
                var opt = "<a href='javascript:;' id='"+rowObject.id+"' onclick='showReturnOrderLogs(this)'>查看日志</a>"
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
        pager:"#return_orders_pagination",//分页
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
            doShowReturnOrderDetail(rowid)
        },
        serializeGridData : function(postData) {
            return JSON.stringify(getParams());
        }
    }

    return setting;
}

function doShowReturnOrderDetail(id){
    // $("#ro_detail_div").empty()
    var productInfoTableBody = $("#ro_detail_div").find("table[name='productInfoTable'] tbody")
    var totalReturnAmountSpan = $("#ro_detail_div").find("span[name='totalReturnAmount']")
    var userExpectTotalReturnAmountSpan = $("#ro_detail_div").find("span[name='userExpectTotalReturnAmount']")
    var actualTotalReturnAmountSpan = $("#ro_detail_div").find("span[name='actualTotalReturnAmount']")
    var purchaseInfoTableBody = $("#ro_detail_div").find("table[name='purchaseInfoTable'] tbody")
    productInfoTableBody.empty()
    actualTotalReturnAmountSpan.empty()
    purchaseInfoTableBody.empty()
    layer.open({
        type: 1,
        title: "退货单详情",
        content: $("#ro_detail_div"),
        area: ['1100px', '510px'],
        btn: ["关闭"],
        closeBtn: 1,
        shadeClose: true,
        move: false,
        success: function(){
            var returnOrder = globalReturnOrderMap[id]
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

function getParams(){
    var param = {
        currPage:$('#return_orders_table').getGridParam('page'),
        pageSize:$('#return_orders_table').getGridParam('rowNum'),
        startApplicationDate:$("#ro_startApplicationDate").val(),
        endApplicationDate:$("#ro_endApplicationDate").val(),
        sort:$('#return_orders_table').getGridParam("sortname"),
        filter:$('#return_orders_table').getGridParam("sortorder"),
        searchText:$.trim($("#ro_searchText").val())
    };
    if($("#ro_status").val()){
        param["status"] = $("#ro_status").val()
    }
    return  param;
}

function showReturnOrderLogs(obj){
    clickThisTr($(obj).parent().parent())

    var id = $(obj).attr("id");
    var rowObject = globalReturnOrderMap[id]
    $("#ro_logsDiv").empty()
    layer.open({
        type: 1,
        title: "退货单日志",
        content: $("#ro_logsDiv"),
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
                $("#ro_logsDiv").html(logHtml)
            }
        },
        btn1: function (index) {
            layer.close(index)
        }
    });
}