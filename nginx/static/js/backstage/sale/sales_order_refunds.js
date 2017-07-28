/**
 * Created by Administrator on 2016/8/16.
 */
//发货单售后-退款单

function init_sales_order_refunds(BbcGrid) {
    var grid = new BbcGrid();
    grid.initTable($('#sales_order_refunds_table'), getSetting_sales_order_refunds());
    grid.initTable($("#detail_table"),detailSetting());
    grid.initTable($("#purchasing_info_table"),purchasingInfoSetting());

    $("#orderStatus").change(function(){
        $("#sales_order_refunds_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
    });
    $(".searchButton").click(function(){
        $("#sales_order_refunds_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
    });
}

function getSetting_sales_order_refunds() {
    var setting = {
        url: "/sales/manager/showSalesOrderRefunds",
        ajaxGridOptions: {contentType: 'application/json; charset=utf-8'},
        rownumbers: true, // 是否显示前面的行号
        datatype: "json", // 返回的数据类型
        mtype: "post", // 提交方式
        height: "auto", // 表格宽度
        autowidth: true, // 是否自动调整宽度
        // styleUI: 'Bootstrap',
        colNames: ["id", "编号", "申请时间", "发货单号", "分销商账号", "分销商名称", "业务员", "状态", "操作"],
        colModel: [{name: "id", index: "id", width: "12%", align: "center", sortable: false, hidden: true},
            {name: "shOrderNo", index: "describe", width: "12%", align: "center", sortable: false},
            {name: "createTime", index: "describe", width: "12%", align: "center", sortable: false},
            {name: "xsOrderNo", index: "describe", width: "12%", align: "center", sortable: false},
            {name: "email", index: "related_interface_url", width: "14%", align: "center", sortable: false},
            {name: "disName", index: "describe", width: "12%", align: "center", sortable: false,formatter:function(cellvalue, options, rowObject) {
                return deal_with_illegal_value(cellvalue);
            }},
            {name: "businessErp", index: "describe", width: "12%", align: "center", sortable: false,formatter:function(cellvalue, options, rowObject) {
                return deal_with_illegal_value(cellvalue);
             }},
            {name: "status", index: "describe", width: "12%", align: "center", sortable: false, formatter:function(cellvalue, options, rowObject){
                return formatStatus(cellvalue);
            }},
            {
                name: "id",
                index: "status",
                width: "12%",
                align: "center",
                sortable: false,
                formatter: function (cellvalue, options, rowObject) {
                    return "<a style='display:block;' onclick='checkDetails_sales_refunds(" + rowObject.id + ")'>查看详情</a>";
                }
            }
        ],
        viewrecords: true,
        rowNum: 10,
        rowList: [10, 20, 30],
        pager: "#sales_order_refunds_pagination",//分页
        caption: "发货单退款-退款单",//表名称
        pagerpos: "center",
        pgbuttons: true,
        autowidth: true,
        rownumbers: true, // 显示行号
        loadtext: "加载中...",
        pgtext: "当前页 {0} 一共{1}页",
        jsonReader: {
            root: "saleOrderRefundInfos",  //数据模型
            page: "pageCount",//数据页码
            total: "pages",//数据总页码
            records: "total",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        },
        serializeGridData: function () {
            return JSON.stringify(getSearchParam_sales_order_refunds($(this).jqGrid('getGridParam', 'postData')));
        }
    };
    return setting;
}

function getSearchParam_sales_order_refunds(postData) {
    var params = {
        pageSize: $('#sales_order_refunds_table').getGridParam('rowNum'),
        pageCount: $('#sales_order_refunds_table').getGridParam('page'),
        status: $('#orderStatus').val(),
        searchSpan: $("#searchInput").val(),
        orderStartDate: $("#seachTime0").val(),
        orderEndDate: $("#seachTime1").val(),
        isProductReturn: 0,
        sord: postData.sord,
        sidx: postData.sidx
    };
    return params;
}

function getList_sale_order_refunds() {
    $("#sales_order_refunds_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
}

function formatStatus(cellValue) {
    if(cellValue == 1) {
        return '待客服确认';
    }

    if(cellValue == 2) {
        return '待财务确认';
    }

    if(cellValue == 3) {
        return '待寄回商品';
    }

    if(cellValue == 4) {
        return '待平台收货';
    }

    if(cellValue == 5) {
        return '售后已完成';
    }

    if(cellValue == 6) {
        return '售后关闭';
    }
}

/*查看详情-详细内容 start*/
function detailSetting() {
    var setting = {
        url:"/member/getUsers",
        datatype : "json", // 返回的数据类型
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        width : 1067,
        colNames:['商品编号','商品名称', '所属仓库','数量（个）'],
        colModel:[
            {name:'sku',index:'', width:'15%',align:'center', sortable:false},
            {name:'productName',index:'', width:'40%',align:'center', sortable:false},
            {name:'warehouseName',index:'', width:'15%',align:'center', sortable:false},
            {name:'qty',index:'', width:'15%', align:"center", sortable:false}
        ],
        viewrecords: true,//是否在浏览导航栏显示记录总数
        serializeGridData : function(postData) {
            return JSON.stringify({});
        }
    };
    return setting;
}
function pageInit(){
    var list = [
        {"id":"1","productID":"IM199","name":"美国Aveeno Baby艾维诺宝宝天然燕麦洗发沐浴液236ml","dateDue":"2017-02-14 10:10:01","warehouse":"深圳仓","number":"3"}
    ];
    $.each(list,function(i,item){
        jQuery("#detail_table").jqGrid("addRowData",i+1,item);
    })
}
/*查看详情-详细内容 end*/

/*查看详情-采购信息 start*/
function purchasingInfoSetting() {
    var setting = {
        url:"/member/getUsers",
        datatype : "json", // 返回的数据类型
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        width : 1067,
        colNames:['商品编号','采购单号', '采购价', '采购均摊价'],
        colModel:[
            {name:'sku',index:'', width:'25%',align:'center', sortable:false},
            {name:'purchaseNo',index:'', width:'25%',align:'center', sortable:false},
            {name:'purchasePrice',index:'', width:'25%',align:'center', sortable:false},
            {name:'capfee',index:'', width:'25%',align:'center', sortable:false}
        ],
        viewrecords: true,//是否在浏览导航栏显示记录总数
        serializeGridData : function(postData) {
            return JSON.stringify({});
        }
    };
    return setting;
}
function pageInit2(){
    var list = [
        {"id":"1","productID":"IM199","purchaseOrderNumber":"CG-201602241213456321","purchasingPeriod":"2017-02-14 10:10:01","purchaseBetweenPrice":"3"}
    ];
    $.each(list,function(i,item){
        jQuery("#purchasing_info_table").jqGrid("addRowData",i+1,item);
    })
}
/*查看详情-采购信息 end*/

function checkDetails_sales_refunds(id){
    $("#sales_order_box").hide();
    $("#checkDetails").show();

    $('#OPERATE_SALES_ORDER_REFUNDS_DIV').empty();
    //基本信息
    var param = {"id": id};
    ajax_post("/sales/getSalesOrderRefundsById", JSON.stringify(param),"application/json",function (data) {
        if(data){
            $('#XS_ORDER_NO').html(data.shOrderNo);
            $('#CREATE_TIME').html(data.createTime);
            $('#SALE_ORDER_NO').html(data.xsOrderNo);
            $('#EMAIL').html(deal_with_illegal_value(data.email));
            $('#DIS_NAME').html(deal_with_illegal_value(data.disName));
            $('#BUSINESS_ERP').html(deal_with_illegal_value(data.businessErp));
            $('#DESC').html(deal_with_illegal_value(data.qaDesc));
            $('#DEMAND_AMOUNT').html(deal_with_illegal_value(data.demandAmount));
            $('#ACTUAL_AMOUNT').html(deal_with_illegal_value(data.actualAmount));

            var shOrderStatusText = formatStatus(data.status);
            $('#SH_ORDER_STATUS').html(shOrderStatusText);

            //商品信息
            $("#detail_table").clearGridData();
            var list = [
                {"sku":data.sku,"productName":data.productName,"warehouseName":data.warehouseName,"qty":data.demandQty}
            ];
            $.each(list,function(i,item){
                $("#detail_table").jqGrid("addRowData",i+1,item);
            });

            //寄回商品
            if(data.company) {
                $('#returnProduct').show();
                $('#returnProduct').find('em:eq(0)').html(data.company);
                $('#returnProduct').find('em:eq(1)').html(data.expressCode);
                $('#returnProduct').find('em:eq(2)').html(data.sendProductTime);
            }
        }
    });

    //图片信息
    $('#IMG_SPAN_LIST').empty();
    ajax_post("/sales/getShAttachmentListByShOrderId", JSON.stringify(param),"application/json",function (data) {
        if(data.result){
            if(data.data.length > 0) {
                var ds = data.data;
                for(var i =0; i< ds.length;i++) {
                    var spanContent = '<span>' +
                        '<img src="/sales/getShAttachmentImgById?id='+ ds[i].id + '" class="preview-img" alt="暂无图片" title="点击预览大图" onclick="previewImg(this)">' +
                        '</span>';
                    $('#IMG_SPAN_LIST').append(spanContent);
                }
            }
        }
    });

    //采购信息
    ajax_post("/sales/getShOrderDetails", JSON.stringify(param),"application/json",function (data) {
        if(data.result){
            if(data.data.length > 0) {
                var ds = data.data;
                var list = [];
                $("#purchasing_info_table").clearGridData();
                for(var i =0; i< ds.length;i++) {
                    //var purchaseTime = "";
                    //ajax_post_sync("/inventory/inventoryorder/getPurchaseByPurchaseOrderNo", JSON.stringify({"orderNo": ds[i].purchaseOrderNo}),"application/json",function (data) {
                    //    if(data) {
                    //        console.log(data);
                    //        purchaseTime = data.purchaseTime;
                    //    }
                    //});
                    //console.log(purchaseTime);
                    var info = {"sku":ds[i].sku,"purchaseNo":ds[i].purchaseOrderNo,"purchasePrice":deal_with_illegal_value(ds[i].purchasePrice),"capfee":ds[i].capfee};
                    list.push(info);
                }
                $.each(list,function(i,item){
                    jQuery("#purchasing_info_table").jqGrid("addRowData",i+1,item);
                })
            }
        }
    });

    //操作记录
    ajax_post("/sales/getShLogListByShOrderId", JSON.stringify(param),"application/json",function (data) {
        if(data.result){
            console.log(data);
            if(data.data.length > 0) {
                var ds = data.data;
                for(var i =0; i< ds.length;i++) {
                    if(ds[i].type == 1) {
                        var content = '<div>';
                        content += '<h4>客服确认</h4>';
                        content += '<div>'+ (ds[i].result == '1' ? '确认通过': '确认不通过') +'</div>';
                        content += '<div>' + (ds[i].isProductReturn == '1' ? '需要寄回商品': '不需要寄回商品') + '</div>';
                        content += '<div>备注：<em>'+ deal_with_illegal_value(ds[i].remark) +'</em></div>';
                        content += '<div>操作人：<em>' + ds[i].operator + '</em></div>';
                        content += '<div>时间：<em>'+ ds[i].createTime +'</em></div>';
                        content += '</div>';
                        $('#OPERATE_SALES_ORDER_REFUNDS_DIV').append(content);
                    }

                    if(ds[i].type == 2) {
                        var content = '<div>';
                        content += '<h4>财务确认</h4>';
                        content += '<div>'+ (ds[i].result == '1' ? '确认通过': '确认不通过') +'</div>';
                        content += '<div>' + (ds[i].isProductReturn == '1' ? '需要寄回商品': '不需要寄回商品') + '</div>';
                        content += '<div>备注：<em>'+ deal_with_illegal_value(ds[i].remark) +'</em></div>';
                        content += '<div>操作人：<em>' + ds[i].operator + '</em></div>';
                        content += '<div>时间：<em>'+ ds[i].createTime +'</em></div>';
                        content += '</div>';
                        $('#OPERATE_SALES_ORDER_REFUNDS_DIV').append(content);
                    }


                    if(ds[i].type == 3) {
                        $('#saleOrderFinish').show();
                        $('#saleOrderFinish').find('em:eq(0)').html('确认收货，' + ds[i].isProductReturn == 1 ? '退货': '退款');
                        $('#saleOrderFinish').find('em:eq(1)').html( deal_with_illegal_value(ds[i].remark));
                        $('#saleOrderFinish').find('em:eq(2)').html(ds[i].operator);
                        $('#saleOrderFinish').find('em:eq(3)').html(ds[i].createTime);
                    }

                    if(ds[i].type == 4) {
                        var content = '<div>';
                        content += '<h4>客户取消</h4>';
                        content += '<div>操作人：<em>' + ds[i].operator + '</em></div>';
                        content += '<div>时间：<em>'+ ds[i].createTime +'</em></div>';
                        content += '</div>';
                        $('#OPERATE_SALES_ORDER_REFUNDS_DIV').append(content);
                    }
                }
            }
        }
    });
}
function detailReturn(){
    $("#sales_order_box").show();
    $("#checkDetails").hide();
}
