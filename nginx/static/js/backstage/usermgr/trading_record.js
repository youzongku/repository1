
var layer;
function init_bills(layerParam, BbcGrid) {
    //初始化全局变量
    layer = layerParam;
    var grid =new BbcGrid();
    grid.initTable($("#bill_table"),tradingRecordSetting());
    init_bill_func();
}
function init_bill_func(){
    $("#bill_search").click(function(){searchBills()});
    $("#purpose").change(function(){searchBills()});
    $("#time").change(function(){searchBills()});
    entry_func($("#bill_input"),function(){searchBills()});
    $("#bill_export").click(function(){exportBillData()});
}
function viewbills(){
    return {
        time:$("#time").val(),
        purpose:$("#purpose").val(),
        key:$("#bill_input").val(),
        currPage:$('#bill_table').getGridParam('page'),
        pageSize:$('#bill_table').getGridParam('rowNum'),
        //sort:$('#bill_table').getGridParam("sortname"),
        //filter:$('#bill_table').getGridParam("sortorder")
        sources:3
    }
}
function searchBills(){
    $("#bill_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}
function tradingRecordSetting() {
    var setting = {
        url:"/member/back/getBills",
        datatype : "json", // 返回的数据类型
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        caption:"交易记录（双击行，可查看交易记录详情）",//表名称,
        colNames:['分销商','名称','applyId','purpose', '业务员', '操作凭证','操作类型','交易途径','交易金额（元）','账户余额（元）','信用余额（元）','交易时间'],
        colModel:[
            {name:'email',index:'', width:'10%',align:'center', sortable:false},
            {name:'nickName',index:'', width:'10%',align:'center', sortable:false},
            {name:'applyId',index:'', width:'10%',align:'center', sortable:false,hidden:true},
            {name:'purpose',index:'', width:'10%',align:'center', sortable:false,hidden:true},
            {name:'salesmanErp',index:'', width:'10%',align:'center', sortable:false},
            {name:'serialNumber',index:'', width:'10%',align:'center', sortable:false},
            {name:'paymentType',index:'', width:'10%',align:'center', sortable:false},
            {name:'purposeStr',index:'', width:'10%',align:'center', sortable:false},
            {name:'amount',index:'amount', width:'10%',align:'center', sortable:true},
            {name:'balance',index:'balance', width:'10%',align:'center', sortable:true},
            {name:'creditLimitBalance',index:'credit_limit_balance', width:'10%',align:'center', sortable:true},
            {name:'create',index:'create_date', width:'10%',align:'center', sortable:true}
        ],
        viewrecords: true,//是否在浏览导航栏显示记录总数
        rowNum:10,//每页显示记录数
        rowList:[10,20,25],//用于改变显示行数的下拉列表框的元素数组。
        pagerpos : "center",
        pager:"#bill_pagination",//分页
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        jsonReader:{
            root: "bills.list",  //数据模型
            page: "bills.currPage",//数据页码
            total: "bills.totalPage",//数据总页码
            records: "bills.rows",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            id: "id"//唯一标识
        },
        serializeGridData : function(postData) {
            return JSON.stringify($.extend(viewbills(),postData));
        }
        ,ondblClickRow: function(rowid) {
            tradingRecordDetail(rowid);
        }
    };
    return setting;
}

function tradingRecordDetail(rowid){
    var data = jQuery("#bill_table").getRowData(rowid);
    var param = {
        applyId:data.applyId,
        serialNumber:data.serialNumber,
        son:true,
        purpose:data.purpose
    }
    var billHtml =
        '<div id="tradingRecordDetail">   '+
        '	<table class="list_title all_table"           '+
        '	style="padding: 10px;box-sizing: border-box;">'+
        '	<thead>                                       '+
        '	<tr>                                          '+
        '		<th style="width: 10%">分销商</th>        '+
        '		<th style="width: 10%">名称</th>          '+
        '		<th style="width: 10%">业务员</th>        '+
        '		<th style="width: 10%">操作凭证</th>      '+
        '		<th style="width: 10%">操作类型</th>      '+
        '		<th style="width: 10%">交易途径</th>      '+
        '		<th style="width: 10%">交易金额（元）</th>'+
        '		<th style="width: 10%">账户余额（元）</th>'+
        '		<th style="width: 10%">信用余额（元）</th>'+
        '		<th style="width: 10%">交易时间</th>      '+
        '	</tr>                                         '+
        '	</thead>                                      '+
        '	<tbody>';
    ajax_post_sync("/member/back/getBills",JSON.stringify(param),"application/json",
        function(data){
            $.each(data.bills.list,function(i,item){
                billHtml +=
                    '	<tr>                                          '+
                    '		<td>'+item.email+'</td>                      '+
                    '		<td>'+(item.nickName?item.nickName:"")+'</td>                             '+
                    '		<td>'+(item.salesmanErp?item.salesmanErp:"")+'</td>                             '+
                    '		<td>'+(item.serialNumber?item.serialNumber:"")+'</td>             '+
                    '		<td>'+item.paymentType+'</td>               '+
                    '		<td>'+item.purposeStr+'</td>                             '+
                    '		<td>'+item.amount+'</td>                            '+
                    '		<td>'+item.balance+'</td>                            '+
                    '		<td>'+(item.creditLimitBalance?item.creditLimitBalance:"")+'</td>                        '+
                    '		<td style="word-break: normal;">'+item.create+'</td>     '+
                    '	</tr>';
            });
            billHtml +=   '</tbody></table></div>';
        },function(e){
            console.log(e);
        }
    )
    layer.open({
        type: '1',
        title: '交易记录详情',
        area: ['1000px','460px'],
        content: billHtml,
        btn: ['关闭'],
        shadeClose: true
    });
}

function exportBillData(){
    var colModel = jQuery("#bill_table").getGridParam("colModel");
    var headName = [];
    $.each(colModel,function(i,item){
        if(!item.hidden){
            headName.push("header=" + (item.name == "purposeStr"?"purpose":item.name));
        }
    });
    headName.push("purpose=" + $("#purpose").val());
    headName.push("time=" + $("#time").val());
    headName.push("key=" + $("#bill_input").val());
    if(headName.length > 0 ){
        window.location.href = "/member/back/exportBills?" + headName.join("&");
    }
}