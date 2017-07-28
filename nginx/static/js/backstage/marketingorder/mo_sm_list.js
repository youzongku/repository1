/**
 * Created by Administrator on 2016/12/26.
 */
var layer = undefined;
var moDetailMap = {};

// canExport是否可以导出
function init_mo_sm(layerArg, BbcGrid){
    layer = layerArg;
    var grid = new BbcGrid();
    grid.initTable($("#mo_table"),smo_setting());
    moDetailMap = {};
    loadOrg();
    init_mo_func();
}

function init_mo_func(){
    $("#mo_distributor_type").change(function(){ mo_search_params();});
    $("#mo_status").change(function(){ mo_search_params();});
    entry_func($("#mo_searchText"),function(){mo_search_params()});
    $("#mo_search_btn").click(function(){mo_search_params()});
    $("#exportMarketingOrderBtn").click(function(){exportMoOrder()});
}

function loadOrg(){
    var zTree;
    var setting = {
        view: {
            dblClickExpand: false
        },
        data: {
            simpleData: {
                enable: true
            }
        },
        async: {
            enable: true,
            url:"/member/getOrganization",
            autoParam:["id"]
        },
        callback: {
            onClick: onClick
        }
    };
    var zNodes = [
        {id:0, name:"组织架构",isParent:true}
    ];
    $.fn.zTree.init($("#treeDemo"), setting, zNodes);
    $("#treeDemo").mouseleave(function(){
       $(this).slideUp("fast");
    });
}

function onClick(e, treeId, treeNode) {
    var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
        nodes = zTree.getSelectedNodes(),
        v = "";
        var orgId = "";
    nodes.sort(function compare(a,b){return a.id-b.id;});
    for (var i=0, l=nodes.length; i<l; i++) {
        v += nodes[i].name + ",";
        orgId += nodes[i].id;
    }
    if (v.length > 0 ) v = v.substring(0, v.length-1);
    var orgObj = $("#org");
    orgObj.val(v);
    orgObj.data("orgId", orgId);
    $("#treeDemo").slideUp("fast");
    mo_search_params();
}

function showOrg() {
    $("#treeDemo").slideDown("fast");
}

function smo_setting() {
    moDetailMap = {}
    var setting = {
        url:"/sales/mo/list/sm",
        rownumbers : true, // 是否显示前面的行号
        datatype : "json", // 返回的数据类型
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        // colNames:["订单编号"],
        colModel:[
            {label:"订单编号",name:"marketingOrderNo",index:"marketing_order_no",align:"center",sortable:true},
            {label:"下单时间",name:"createDateStr",index:"create_date",align:"center",sortable:true},
            {label:"发货单号",name:"salesOrderNo",index:"salesOrderNo",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"订单状态",name:"statusMsg",index:"status",align:"center",sortable:false},
            {label:"分销商",name:"email",index:"email",align:"center",sortable:false},
            {label:"分销商名称",name:"nickName",index:"nickName",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"分销商类型",name:"distributorTypeStr",index:"distributor_type",align:"center",sortable:false},
            {label:"录入人",name:"createUser",index:"createUser",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"业务员",name:"salesman",index:"salesman",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"业务备注",name:"businessRemark",index:"businessRemark",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                if(cellvalue && cellvalue.length>20){
                    return cellvalue.substring(0,20)+"..."
                }
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"操作",sortable:false,align:"center", formatter:function(cellvalue, options, rowObject){
                moDetailMap[rowObject.id] = rowObject
                var opt = //"<a href='javascript:;' id='"+rowObject.id+"' onclick='showMoDeail(this)'>详情</a><br/>"+
                    "<a href='javascript:;' marketingOrderNo='"+rowObject.marketingOrderNo+"' onclick='showAuditLogs(this)'>操作日志</a>"
                return opt
            }}
        ],
        viewrecords : true,
        onPaging:function(pageBtn){
            // 清空缓存的数据
            moDetailMap = {}
        },
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#mo_pagination",//分页
        caption:"营销单列表（双击行，可查看营销单详情）",//表名称
        pagerpos : "center",
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        jsonReader:{
            root: "datas",  //数据模型
            page: "currPage",//数据页码
            total: "totalPage",//数据总页码
            records: "totalCount",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        },
        ondblClickRow: function(rowid) {
            doshowMoDeail(rowid)
        }
    }

    return setting;
}

function mo_search_params(){
    // 拿到原有的，清除掉
    var postData = $("#mo_table").jqGrid("getGridParam", "postData");
    $.each(postData, function (k, v) {
        delete postData[k];
    });
    var params = {
        searchText: $.trim($("#mo_searchText").val()),
        startDate: $("#mo_createdate_start").val(), endDate: $("#mo_createdate_end").val()
    }
    if($("#mo_distributor_type").val()){
        params.distributorType = $("#mo_distributor_type").val()
    }
    if($("#org").data("orgId")){
        params.orgId=$("#org").data("orgId");
    }
    if($("#mo_status").val()){
        params.status = $("#mo_status").val()
    }
    console.log(params)

    $("#mo_table").jqGrid('setGridParam', {page:1, postData: params}).trigger("reloadGrid");
}

// 导出
function exportMoOrder(){
    var params = "?searchText="+$.trim($("#mo_searchText").val())+"&startDate="+$("#mo_createdate_start").val()+
        "&endDate="+$("#mo_createdate_end").val()
    if($("#mo_distributor_type").val()){
        params += "&distributorType="+$("#mo_distributor_type").val()
    }
    if($("#mo_status").val()){
        params += "&status="+$("#mo_status").val()
    }
    if($("#org").data("orgId")){
        params += "&orgId="+$("#org").data("orgId");
    }
    console.log(params)
    window.location.href = "/sales/mo/list/sm/export"+params
}

// 查询列表

function showMoDeail(obj){
    clickThisTr($(obj).parent().parent())
    var id = $(obj).attr("id")
    doshowMoDeail(id)
}

function doshowMoDeail(id){
    var rowObject = moDetailMap[id]
    var receiverInfoHtml = '<div>'+
        '<span>姓名：'+rowObject.receiver+'</span>'+
        '<span>手机：'+rowObject.receiverTel+'</span>'+
        '<span>邮编：'+rowObject.receiverPostcode+'</span>'+
        '<address>地址：'+(rowObject.provinceName+" "+rowObject.cityName+" "+rowObject.regionName+" "+rowObject.addressDetail)+'</address>'+
        '</div>'
    $("#mo_detail_div").find("div[name='receiverInfoDiv']").html(receiverInfoHtml)

    var totalQty = 0;
    var details = rowObject.detailList;
    var productInfoHtml = ''
    for (var j in details) {
        totalQty += parseFloat(details[j].qty)
        productInfoHtml += '<tr>'+
            '<td>'+details[j].sku+'</td>'+
            '<td>'+details[j].productName+'</td>'+
            '<td>'+deal_with_illegal_value(details[j].interBarCode)+'</td>'+
            '<td>'+details[j].warehouseName+'</td>'+
            '<td>'+parseFloat(details[j].disPrice).toFixed(2)+'</td>'+
            '<td>'+details[j].qty+'</td>'+
            '<td>'+(parseFloat(details[j].disPrice) * parseFloat(details[j].qty)).toFixed(2)+'</td>'+
            '</tr>'
    }
    $("#mo_detail_div").find("table[name='productInfoTable']").find("tbody").html(productInfoHtml)
    var sumInfoHtml = '<span>商品总数量：'+totalQty+'个</span>' +
        '<span> 商品总计：'+rowObject.totalAmount+'元+运费'+rowObject.bbcPostage+'元</span>'
    $("#mo_detail_div").find("p[name='sumInfoP']").html(sumInfoHtml)

    layer.open({
        type: 1,
        title: rowObject.marketingOrderNo,
        content: $("#mo_detail_div"),
        area: ['1100px', '510px'],
        btn: ["关闭"],
        closeBtn: 1,
        shadeClose: true,
        move: false,
        btn1: function (index) {
            layer.close(index)
        }
    });
}

function showAuditLogs(obj) {
    clickThisTr($(obj).parent().parent())

    layer.open({
        type: 1,
        title: "操作日志",
        content: $("#mo_audit_logs_div"),
        area: ['550px', '330px'],
        btn: ["关闭"],
        closeBtn: 1,
        shadeClose: true,
        move: false,
        btn1: function (index) {
            layer.close(index)
        }
    });

    // 查询日志
    ajax_post("/sales/mo/auditlogs", JSON.stringify({marketingOrderNo:$(obj).attr("marketingOrderNo")}), "application/json", function (data) {
        var $tbody = $("#mo_audit_logs_div").find("table tbody");
        $tbody.empty()
        if(!data.suc){
            layer.msg(data.msg, {icon : 2, time : 2000});
            return
        }
        var logs = data.logs;
        if(logs.length<1){
            $tbody.append("<tr><td>暂无操作日志</td></tr>")
        }else{
            var logs_html = ''
            for(var i in logs){
                var log_html = logs[i].auditDateStr + "　" + logs[i].auditUser + "　操作　"
                log_html += (logs[i].auditType==1 ? "初审状态为：" : "复审状态为：")
                log_html += ((logs[i].status==2 || logs[i].status==3) ? "通过" : (logs[i].status==4 ? "不通过" : "未知状态"))
                var log_remark_html = "<b class='mo_audit_bl'>"+(logs[i].auditType==1 ? "初审备注：" : "复审备注：") +"</b><b class='mo_audit_br'>"+ (logs[i].remarks ? logs[i].remarks : "")+"</b>"
                logs_html += "<tr><td>"+log_html+"</td></tr>"
                logs_html += "<tr><td>"+log_remark_html+"</td></tr>"
                if(i>0){
                    logs_html += "<tr><td></td></tr>"
                }
            }
            $tbody.append(logs_html)
        }
    })
}

/*鼠标经过tbody tr变色效果*/
$('.tbodyTrHover tr').on('mouseover',function(){
    $(this).css('background','#eee').siblings().css('background','#fff');
});
