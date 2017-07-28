var laypage = undefined;
var flag = undefined;

function init_goods_purchase_price(lay,f,BbcGrid) {
    laypage = lay;
    flag = f;
    viewWarehouse_ad();
    var grid = new BbcGrid();
    grid.initTable($("#goodsprice_system_table"),getSetting_purchase_price());
}

function viewWarehouse_ad() {
    //仓库信息显示
    var wareUrll = "/inventory/queryWarehouse";
    $.ajax({
        url: wareUrll,
        type: "get",
        dataType: "json",
        async: false,
        success: function (data) {
            var wareHtml = "<li value=\"0\" class=\"Storage-distribution-active\">全部</li>";
            if (data.length > 0) {
                for (var i in data) {
                    wareHtml += "<li value=\"" + data[i].id + "\" >" + data[i].warehouseName + "</li>";
                }
                $("#wareShow").empty();
                $("#wareShow").append(wareHtml);
            }
        }
    });
}

/**
 * 新版查询商品列表
  */
function getSetting_purchase_price() {
    var setting = {
        url:"/product/api/getProducts",
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        rownumbers : true, // 是否显示前面的行号
        datatype : "json", // 返回的数据类型
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        colModel:[
            {label:"商品名称",name:"ctitle",index:"product_name",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"SKU",name:"csku",index:"csku",align:"center",sortable:true},
            {label:"所属仓库",name:"warehouseName",index:"warehouse_name",align:"center",sortable:false},
            {label:"到岸价(元)",name:"cost",index:"cost",align:"center",sortable:false},
            {label:"分销增值税",name:"disTotalVat",index:"dis_total_vat",align:"center",sortable:true},
            {label:"分销进口关税",name:"disImportTar",index:"dis_import_tar",align:"center",sortable:true},
            {label:"分销消费税",name:"disGst",index:"dis_gst",align:"center",sortable:true},
            {label:"行邮税率",name:"postalFeeRate",index:"postal_fee_rate",align:"center",sortable:false}
        ],
        viewrecords : true,
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#goodsprice_system_pagination",//分页
        caption:"采购成本列表",//表名称
        pagerpos : "center",
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        caption:"采购成本列表",//表名称,
        jsonReader:{
            root: "data.result",  //数据模型
            page: "data.currPage",//数据页码
            total: "data.totalPage",//数据总页码
            records: "data.rows",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        },
        serializeGridData : function(postData) {
            return JSON.stringify(getSearchParam_purchase_price())
        }
    }

    return setting;
}

/**
 * 组织查询参数
 */
function getSearchParam_purchase_price() {
    var categoryId = $("#price_select").val();
    var title = $("#searchInput").val();
    var warehouseId = $(".Storage-distribution-active").val();

    var param = {
        pageSize: $('#goodsprice_system_table').getGridParam('rowNum'),
        currPage: $('#goodsprice_system_table').getGridParam('page')
    };

    if (categoryId != 0) {
        param.categoryId = categoryId;
    }

    if (title != null && title != '') {
        param.title = title;
    }
    if (warehouseId != 0) {
        param.warehouseId = warehouseId;
    }

    var sparam = {
        data: param
    };
    return sparam;
}

/**
 * 点击按钮查询
 */
function searchGoods_goodsprice_system(){
    $("#goodsprice_system_table").jqGrid('setGridParam',{page: 1}).trigger('reloadGrid');
}

//按下enter进行查询
function getGoodsListByCondition_goodsprice_system() {
    $("#goodsprice_system_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}

function deal_with_status_value(statusValue) {
    if(statusValue == 1) {
        return '在售';
    } else if(statusValue == 2){
        return '停售';
    } else {
        return '下架';
    }
}

function init_catgs(f) {
    isaulogin(function (email) {
        ajax_get("/product/api/realCateQuery?level=1", "", "application/json",
            function (data) {
                if (data && data.length > 0) {
                    if (f && f == 1) {
                        priceCategorys(data);
                    } else {
                        categorys(data);
                    }
                }
            },
            function (xhr, status) {

            }
        );
    });
}

function init_types() {
    ajax_get("/product/api/getAllTypes", "", "application/json",
        function (data) {
            if (data) {
                $(".search_category #select-type").empty();
                var html = "<option value='0' >请选择</option>";
                $.each(data,function(i,item){
                    html += "<option value='"+item.id+"' name='"+item.name+"' tid='"+item.id+"'>"+item.name+"</option>";
                });
                $(".search_category #select-type").append(html);
            }
        },
        function (xhr, status) {
        }
    );
}

//表格头部类型下拉值 商品库存
function categorys(data) {
    $("select[name='categorys'] option:eq(0)").siblings().remove();
    var htmlCode = "";
    $.each(data, function (i, item) {
        htmlCode += "<option value=\"" + item.iid + "\">" + item.cname + "</option>";
    });
    $("select[name='categorys'] option:eq(0)").after(htmlCode);
}
//价格体系
//商品类目
function priceCategorys(data) {
    $("select[name='price_categorys'] option:eq(0)").siblings().remove();
    var htmlCode = "";
    $.each(data, function (i, item) {
        htmlCode += "<option value=\"" + item.iid + "\">" + item.cname + "</option>";
    });
    $("select[name='price_categorys'] option:eq(0)").after(htmlCode);
}
