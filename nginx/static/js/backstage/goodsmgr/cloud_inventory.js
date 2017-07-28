//定义全局变量
var layer = undefined;
var laypage = undefined;
var BbcGrid = undefined
function new_init_product(layerParam, laypageParam, BbcGridParam){
    BbcGrid = BbcGridParam;
    layer = layerParam;
    laypage = laypageParam;
    product_init_select();
    $("#checksku").click(function(){
            product_init_select_reload();
         });
}

// 新的查询列表
function product_init_select(){
    var grid = new BbcGrid();
    grid.initTable($("#product_init_table"),getSetting_product_initInfo());
}

function getSetting_product_initInfo() {
    var sku=$("#sku").val();

    var setting = {
        url:"/inventory/cloud/initRecord?sku="+sku,
        rownumbers : true, // 是否显示前面的行号
        datatype : "json", // 返回的数据类型
        mtype : "get", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        loadComplete: function (data) {
             if(data.result==1){
                layer.alert(data.msg);
             }
         },
        // styleUI: 'Bootstrap',
        colNames:["sku","仓库id","初始化时间","操作"],
        colModel:[{name:"sku",index:"sku",width:"12%",align:"center",sortable:true},
            {name:"warehouseId",index:"warehouseId",width:"14%",align:"center",sortable:true},
            {name:"lastSyncingTime",index:"lastSyncingTime",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                return formateDate(cellvalue)
            }},
            {name:"initProduct",index:"initProduct",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                if(rowObject.lastSyncingTime ==null){
                     return "<a href='javascript:;' warehouseId='"+rowObject.warehouseId + "' sku='" + rowObject.sku + "' onclick='initProduct(this)' >初始化</a>";
                 }else{
                    return "--";
                 }
               
            }}
        ],
        viewrecords : false,
        caption:"初始化信息",//表名称
        autowidth: true,
        rownumbers: true, // 显示行号
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        caption:"初始化信息",//表名称
        jsonReader:{
            root: "syncdata",  //数据模型
            epeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
             id: "id"//唯一标识
        }
    };
    return setting;
}

function product_init_select_reload(){
    $("#product_init_table").jqGrid('setGridParam',{
        url:"/inventory/cloud/initRecord?sku="+$("#sku").val(),
    }).trigger('reloadGrid');
}

function formateDate(time){
    if(time==null){
        return "未初始化"
    }
	var da=new Date(time);
    var year = da.getFullYear();
    var month = da.getMonth()+1;
    var date = da.getDate();
    return ([year,month,date].join('-'));
}

function initProduct(obj){
    var operator="";
    ajax_get("../member/admin/loginhistory?" + Math.random(), {}, undefined,
        function(response) {
            if (response.suc) {
                operator=response.info.email;
            } else {
                window.location.href = "login.html";
            }
        }
    );
    var param = {
        sku: $(obj).attr("sku"),
        operator:operator,
        warehouseId:$(obj).attr("warehouseId")
    };
    var url = "/inventory/cloud/initNewProduct";
    ajax_post(url, JSON.stringify(param), "application/json", function (data) {
        layer.alert(data.msg);
    });
}


/*同步erp库存变更数据*/
function new_sync_erpchange(layerParam, laypageParam, BbcGridParam){
    BbcGrid = BbcGridParam;
    layer = layerParam;
    laypage = laypageParam;
    stockchange_sync();
    $("#syncerpchange").click(function(){
            stockchange_sync_reload();
        });
}

//同步信息
function stockchange_sync(){
    var grid = new BbcGrid();
    grid.initTable($("#stockchange_sync_table"),new_stockchange_sync());
}

function new_stockchange_sync(){
    var updated_begin=$("#begin_time").val();
    var updated_end=$("#end_time").val();

    var setting = {
        url:"/inventory/cloud/stockInManually?updated_begin="+updated_begin+"&updated_end="+updated_end+"&time="+Math.random(),
        rownumbers : true, // 是否显示前面的行号
        datatype : "json", // 返回的数据类型
        mtype : "get", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        loadError:function(xhr,status,error){  
             layer.alert("同步时间过长,请等候一段时间查看sku出入库记录。");    
             },
        loadComplete: function (data) {
             if(data.syncinfo.length==0 && data.result==0){
                layer.alert("erp无出有效入库记录!");
             }else{
                layer.alert(data.msg);
             }
         },
        // styleUI: 'Bootstrap',
        colNames:["标识符","sku","仓库id","过期时间","数量","更新时间"],
        colModel:[
            {name:"identifier",index:"identifier",width:"12%",align:"center",sortable:true},
            {name:"sku",index:"sku",width:"12%",align:"center",sortable:true},
            {name:"warehouseId",index:"warehouseId",width:"14%",align:"center",sortable:true},
            {name:"expirationDate",index:"expirationDate",width:"12%",align:"center",sortable:true},
            {name:"containerStockChange",index:"containerStockChange",width:"12%",align:"center",sortable:true},
            {name:"updateDate",index:"updateDate",width:"12%",align:"center",sortable:false}
        ],
        viewrecords : false,
        caption:"初始化信息",//表名称
        autowidth: true,
        rownumbers: true, // 显示行号
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        caption:"erp库存变更信息",//表名称
        jsonReader:{
            root: "syncinfo",  //数据模型
            epeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
             id: "identifier"//唯一标识
        }
    };
    return setting;
}

function stockchange_sync_reload(){
    $("#stockchange_sync_table").jqGrid('setGridParam',{
        url:"/inventory/cloud/stockInManually?updated_begin="+$("#begin_time").val()+"&updated_end="+$("#end_time").val()+"&time="+Math.random()
    }).trigger('reloadGrid');
}

function new_erpstock_changerecord(layerParam, laypageParam, BbcGridParam){
    BbcGrid = BbcGridParam;
    layer = layerParam;
    laypage = laypageParam;
    erpstock_changerecord();
    $("#stockchange").click(function(){
            erpstock_changerecord_reload();
         });
}

// 新的查询列表
function erpstock_changerecord(){
    var grid = new BbcGrid();
    grid.initTable($("#erpstock_changerecord_table"),getSetting_erpstock_changerecord());
}

function getSetting_erpstock_changerecord() {
    var param = {
        sku: $("#sku").val(),
        updated_begin: $("#begin_time").val(),
        updated_end: $("#end_time").val()
    }

    var setting = {
        url:"/inventory/cloud/getsyncrecord",
        postData:param,
        rownumbers : true, // 是否显示前面的行号
        datatype : "json", // 返回的数据类型
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        // styleUI: 'Bootstrap',
        colNames:["标识符","sku","仓库","过期时间","变更数量","时间"],
        colModel:[
            {name:"identifier",index:"identifier",width:"12%",align:"center",sortable:true},
            {name:"sku",index:"sku",width:"12%",align:"center",sortable:true},
            {name:"warehouseName",index:"warehouseName",width:"14%",align:"center",sortable:true},
            {name:"expirationDate",index:"expirationDate",width:"12%",align:"center",sortable:true},
            {name:"containerStockChange",index:"containerStockChange",width:"12%",align:"center",sortable:true},
            {name:"updateDate",index:"updateDate",width:"14%",align:"center",sortable:true}
        ],
        viewrecords : true,
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#erpstock_changerecord_pagination",//分页
        caption:"微仓明细",//表名称
        pagerpos : "center",
        pgbuttons : true,
        autowidth: true,
        rownumbers: true, // 显示行号
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        caption:"微仓明细",//表名称,
        jsonReader:{
            root: "data",  //数据模型
            page: "currPage",//数据页码
            total: "totalPage",//数据总页码
            records: "rows",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        }
    };
    return setting;
}

function erpstock_changerecord_reload(){
    $("#erpstock_changerecord_table").jqGrid('setGridParam',{
        page:1, 
        postData: 
            {
                sku: $("#sku").val(),
                updated_begin: $("#begin_time").val(),
                updated_end: $("#end_time").val()
            }
    }).trigger('reloadGrid');
}

