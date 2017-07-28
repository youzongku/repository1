function expenseManagement() {
    var setting = {
        url:"/member/getUsers",
        datatype : "json", // 返回的数据类型
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        width : 1086, // 是否自动调整宽度
        colNames:['费用类型','费用率','费用计算方式','时间期限','关联合同','分销商账号','名称','分销商手机','操作'],
        colModel:[
            {name:'costTypes',index:'', width:'11%',align:'center', sortable:false,
                formatter:function(cellvalue, options, rowObject){
                    return "其他"?"满返":"合同扣点";
                }
            },
            {name:'costRatio',index:'', width:'11%',align:'center', sortable:false},
            {name:'costCalculation',index:'', width:'11%',align:'center', sortable:false},
            {name:'timeBar',index:'', width:'12%', align:"center", sortable:false},
            {name:'relationshipAgreement',index:'', width:'11%', align:"center", sortable:false},
            {name:'resellerAccount',index:'', width:'11%', align:"center", sortable:false},
            {name:'name',index:'', width:'11%', align:"center", sortable:false},
            {name:'distributorMobilePhone',index:'', width:'11%', align:"center", sortable:false},
            {name:'operation',index:'', width:'11%', align:"center", sortable:false                ,
                formatter:function(cellvalue, options, rowObject){
                return "<div class='operation-a'>" +
                    "<a onclick='compile();'>编辑</a>" +
                    "<a onclick='operationLog();'>操作日志</a>" +
                    "</div>";
                }
            }
        ],
        viewrecords: true,//是否在浏览导航栏显示记录总数
        rowNum:15,//每页显示记录数
        rowList:[15,20,25],//用于改变显示行数的下拉列表框的元素数组。
        serializeGridData : function(postData) {
            return JSON.stringify({});
        }
        ,loadComplete:function(data){
            pageInit();
        }
    };
    return setting;
}
function pageInit(){
    var list = [
        {"id": "1","costTypes": null,"costRatio": "0.12","costCalculation": "订单金额×0.12","timeBar": "2017.03.08 00:00:00至2018.03.08 23:59:59","relationshipAgreement": "H005","resellerAccount": "13512125984","name": "比布莱恩特","distributorMobilePhone": "13512125984","operation": null}];
    $.each(list,function(i,item){
        jQuery("#expense_management_table").jqGrid("addRowData",i+1,item);
    })
}

/*弹框 start*/
function compile(){
    layer.open({
        type: '1',
        title: '编辑',
        area: ['600px','340px'],
        content: $("#compile"),
        btn: ['保存','返回']
    });
}

$("body").on("mouseover", ".question-icon", function () {
        layer.tips('请将费用率换算成小数，如 0.12', this, {
            tips: [2, '#55CCC8'],
            time: 0
        });
    })
    .on("mouseout", ".question-icon", function () {
        layer.closeAll('tips')
    });

function operationLog(){
    layer.open({
        type: '1',
        title: '操作日志',
        area: ['550px','400px'],
        content: $("#operationLog"),
        btn: ['关闭']
    });
}
/*弹框 end*/

function entryFee() {
    $("#expenseManagement").hide();
    $("#entryFee").show();
}
function entryFeeReturn() {
    $("#entryFee").hide();
    $("#expenseManagement").show();
}
/*录入费用 start*/
function entryFeeBox() {
    var setting = {
        url:"/member/getUsers",
        datatype : "json", // 返回的数据类型
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        width : 1027, // 是否自动调整宽度
        colNames:['合同编码','分销渠道','分销商账号','名称','分销商手机','合同期限','业务员工号'],
        colModel:[
            {name:'contractCode',index:'', width:'14%',align:'center', sortable:false},
            {name:'channelDistribution',index:'', width:'14%',align:'center', sortable:false},
            {name:'resellerAccount',index:'', width:'14%',align:'center', sortable:false},
            {name:'name',index:'', width:'14%', align:"center", sortable:false},
            {name:'distributorMobilePhone',index:'', width:'14%', align:"center", sortable:false},
            {name:'contractPeriod',index:'', width:'16%', align:"center", sortable:false},
            {name:'salesmanWorkNumber',index:'', width:'14%', align:"center", sortable:false}
        ],
        viewrecords: true,//是否在浏览导航栏显示记录总数
        rowNum:15,//每页显示记录数
        rowList:[15,20,25],//用于改变显示行数的下拉列表框的元素数组。
        serializeGridData : function(postData) {
            return JSON.stringify({});
        }
        ,loadComplete:function(data){
            pageInit2();
        }
    };
    return setting;
}
function pageInit2(){
    var list = [
        {"id": "1","contractCode": "--","channelDistribution": "--","resellerAccount": "--","name": "--","distributorMobilePhone": "--","contractPeriod":"--","salesmanWorkNumber": "--"}];
    $.each(list,function(i,item){
        jQuery("#entry_fee_table").jqGrid("addRowData",i+1,item);
    })
}
/*录入费用 end*/