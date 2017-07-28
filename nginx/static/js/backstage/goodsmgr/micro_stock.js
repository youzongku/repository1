var laypage = undefined;
var flag = undefined;

function init_microstock(lay,f,show) {
    laypage = lay;
    flag = f;
}

/**
 * 新的获取微仓明细
 *
 * @param layerParam
 * @param laypageParam
 * @param BbcGridParam
 * @param typeParam
 */
function init_microstock_new(layerParam, laypageParam, BbcGridParam,typeParam){
    BbcGrid = BbcGridParam;
    type = typeParam;
    layer = layerParam;
    laypage = laypageParam;
    initMicroStocksByBbcGrid();
}

function initMicroStocksByBbcGrid() {
    var grid = new BbcGrid();
    grid.initTable($("#micro_stock_table"),getSetting_micro_stock());
}

function getSetting_micro_stock() {

    var param = {
        key: $("#key").val(),
        account: $("#account").val(),
        expirationDate:  $("#mo_createdate_start").val()
    }

    var setting = {
        url:"/inventory/micro/getmicrodata",
        postData:param,
        rownumbers : true, // 是否显示前面的行号
        datatype : "json", // 返回的数据类型
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        // styleUI: 'Bootstrap',
        colNames:["分销商","名称","商品编号","商品名称","到期日期","均摊价格(元)","数量(个)","所属仓库","采购记录"],
        colModel:[{name:"account",index:"account",width:"12%",align:"center",sortable:true},
            {name:"accountName",index:"account_name",width:"14%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                    return deal_with_illegal_value(cellvalue)
            }},
            {name:"sku",index:"sku",width:"14%",align:"center",sortable:true},
            {name:"productTitle",index:"product_title",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                if(rowObject.isGift == '1') {
                    return '【<b style="color: red;">赠</b>】' + cellvalue;
                } else {
                    return cellvalue;
                }
            }},
            {name:"expirationDate",index:"expiration_date",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                return formateDate(cellvalue)
            }},
            {name:"capfee",index:"capfee",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                return cellvalue ? cellvalue.toFixed(2) : 0
            }},
            {name:"stock",index:"stock",width:"12%",align:"center",sortable:true},
            {name:"warehouseName",index:"warehouse_name",width:"12%",align:"center",sortable:true},
            {name:"detail",index:"detail",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return "<a href='javascript:;' id='"+rowObject.id + "' sku='" + rowObject.sku + "' productTitle='" + rowObject.productTitle +"' stock='"+ rowObject.stock + "' onclick='purchaseDetail(this)' >查看</a>";
            }}
        ],
        viewrecords : true,
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#micro_stock_pagination",//分页
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

/**
 * 搜索按钮点击事件
 */
function getMicroStockOrderByConditions(){
    // 拿到原有的，清除掉
    var postData = $("#micro_stock_table").jqGrid("getGridParam", "postData");
    $.each(postData, function (k, v) {
        delete postData[k];
    });

    var params = {
        key: $.trim($("#key").val()),
        account: $.trim($("#account").val()), expirationDate: $("#mo_createdate_start").val()
    };
    console.log(params)

    $("#micro_stock_table").jqGrid('setGridParam', {page:1, postData: params}).trigger("reloadGrid");
}


function formateDate(time){
	var da=new Date(time);
    var year = da.getFullYear();
    var month = da.getMonth()+1;
    var date = da.getDate();
    return ([year,month,date].join('-'));
}

function purchaseDetail(obj){
         var id = $(obj).attr("id");
        var sku =  $(obj).attr("sku");;
		var productTitle =  $(obj).attr("productTitle");
		var stock = $(obj).attr("stock");

        var url = "/inventory/micro/getmicropurchasedetail?id="+id;
        $.ajax({
            url: url,
            type: "get",
            dataType: "json",
            contentType: "application/json",
            async: true,
            success: function (data) {
                    $("#detailTitle").empty();
					var detailTitleHtml="<span>"+sku+"</span><span>"+productTitle+"</span><span>"+stock+"</span>";
					$("#detailTitle").append(detailTitleHtml);
                    $("#purchaseDetail").empty();
                        var result = data;
                        for (var i in result) {
                            var purchaseDetailHtml = "<tr class='list_message' style='background: #fafafa;'>"+
								"<td>"+formateDate(result[i].expirationDate)+"</td>"+
								"<td>"+result[i].orderNo+"</td>"+
								"<td>"+formateDate(result[i].purchaseTime)+"</td>"+
								"<td>"+result[i].residueNum+"</td>"+
								//"<td><a href='####' class='commodityInvY' onclick='realsestock("+result[i].id+")'>"+"释放"+"</a></td>"+
								"</tr>";
                            $("#purchaseDetail").append(purchaseDetailHtml);
                        }
				layer.open({
					type: 1,
					title: '采购明细',
					area: ['600px','400px'],
					content: $('.commodityInvYPop')
				})
	        }
	    });
}

function realsestock(id){
	var url="/inventory/micro/realsemicrostock";
	var param={
		id:id
	}
	$.ajax({
            url: url,
            type: "post",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(param),
            async: true,
            success: function (data) {
                    layer.open({
						title:'释放结果',
						content: data.msg,
						scrollbar: false
					});
	        }
	    });
}

function getWarehouseInventorytByCondition() {
    // 拿到原有的，清除掉
    var postData = $("#micro_stock_table").jqGrid("getGridParam", "postData");
    $.each(postData, function (k, v) {
        delete postData[k];
    });
    var params = {
        key: $.trim($("#key").val()),
        account: $("#account").val(), expirationDate: $("#mo_createdate_start").val()
    };
    $("#micro_stock_table").jqGrid('setGridParam', {page:1, postData: params}).trigger("reloadGrid");
}

