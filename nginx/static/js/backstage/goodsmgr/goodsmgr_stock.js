var laypage = undefined;
var flag = undefined;

function init_goods_goodsmgr_stock(lay,BbcGrid) {
    laypage = lay;
    viewWarehouse_ad();
    var grid = new BbcGrid();
    grid.initTable($('#goodsmgr_stock_table'),getSetting_goodsmgr_stock());
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
function getSetting_goodsmgr_stock() {
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
            {label:"SKU",name:"csku",index:"csku",align:"center",sortable:false},
            {label:"商品分类",name:"cname",index:"cname",align:"center",sortable:false},
            {label:"商品类别",name:"typeName",index:"typeName",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"状态",name:"istatus",index:"istatus",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_status_value(cellvalue)
            }},
            {label:"实际库存",name:"stock",index:"stock",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"实际库存分布",name:"stock",index:"stock",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"可售库存",name:"stock",index:"stock",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"可售库存分布",name:"stock",index:"stock",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"所属仓库",name:"warehouseName",index:"warehouse_name",align:"center",sortable:false},
            {label:"查看云仓明细",name:"detail",index:"businessRemark",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return "<a href='javascript:;' sku='"+rowObject.csku+"' onclick='openDetail(this)' warehouseId='"+rowObject.warehouseId+"'>查看</a>";
            }}
        ],
        viewrecords : true,
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#goodsmgr_stock_pagination",//分页
        caption:"商品库存列表",//表名称
        pagerpos : "center",
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        caption:"商品库存列表",//表名称,
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
            return JSON.stringify(getSearchParam_goodsmgr_stock())
        }
    }

    return setting;
}

/**
 * 组织查询参数
 */
function getSearchParam_goodsmgr_stock() {
    var categoryId = $("#small_select").val();
    var status = $("#goods_status").val();
    var title = $("#searchInput").val();
    var warehouseId = $(".Storage-distribution-active").val();
    var type = $("#select-type").val();

    var param = {
        pageSize: $('#goodsmgr_stock_table').getGridParam('rowNum'),
        currPage: $('#goodsmgr_stock_table').getGridParam('page')
    };

    if (categoryId != 0) {
        param.categoryId = categoryId;
    }
    if (status && status != 0) {
        param.istatus = status;
    }
    if (title != null && title != '') {
        param.title = title;
    }
    if (warehouseId != 0) {
        param.warehouseId = warehouseId;
    }
    if (type != 0) {
        param.typeId = type;
    }

    var sparam = {
        data: param
    };
    return sparam;
}

//按下enter键进行查询
function getGoodsListByCondition_goodsmgr_stock() {
    $("#goodsmgr_stock_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}

/**
 * 点击按钮查询
 */
function searchGoods_goodsmgr_stock(){
    $("#goodsmgr_stock_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
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

//查看云仓明细
function openDetail(obj) {
    var sku = $(obj).attr("sku");
    var warehouseId = $(obj).attr("warehouseId");
    var param = {
        sku: sku,
        warehouseId:warehouseId
    };

    ajax_post("/inventory/cloud/getCloudInventoryDetail", JSON.stringify(param), "application/json",
        function(data) {
            console.log(data);
            var cloudInventoryDetailList=data.cloudInventoryDetailList;
            if (cloudInventoryDetailList && cloudInventoryDetailList.length > 0) {
                var domContent = '';
                var microInventoryStockpile=data.microInventoryStockpile;
                var orderLockNum=data.orderLockNum;
                var orderFlowNum=data.orderFlowNum;
                var kaLockNum=data.kaDistributorLockNum;
                for(var i = 0; i< cloudInventoryDetailList.length;i++) {
                    domContent += '<tr>'
                        + '<td>' + cloudInventoryDetailList[i].expirationDate + '</td>'
                        + '<td>' + cloudInventoryDetailList[i].stock + '</td>'
                        + '<td>' + cloudInventoryDetailList[i].warehouseName + '</td>'
                        + '</tr>';
                }
                var content="<div><span style=\"padding: 2px 15px\"> 微仓囤货数量:<font color='red'>"+microInventoryStockpile+"</font></span>"+
                    " <span style=\"padding: 2px 15px\">流转中订单库存占有数量:<font color='red'>"+orderFlowNum+"</font></font></span>"+
                    "  <span style=\"padding: 2px 15px\">订单锁定数量:<font color='red'>"+orderLockNum+"</font></span>"+
                    "  <span style=\"padding: 2px 15px\">KA经销商锁定数量:<font color='red'>"+kaLockNum+"</font></span></div>"+
                    "<div><table class=\"list_title all_table\" style=\"padding: 10px;box-sizing: border-box;\"\">\n" +
                    "  <thead>\n" +
                    "    <tr>\n" +
                    "      <th style=\"width: 30%;\">到期日期</th>\n" +
                    "      <th style=\"width: 30%;\">数量</th>\n" +
                    "      <th style=\"width: 30%;\">所属仓库</th>\n" +
                    "    </tr> \n" +
                    "  </thead>\n" +
                    "  <tbody>\n" +
                    domContent +
                    "  </tbody>\n" +
                    "</table></div>";

                layer.open({
                    type: 1
                    ,area: ['600px', '400px']
                    ,title: '库存明细(当前可用量=各到期日期数量和-订单锁定数量-KA经销商锁定数量)'
                    ,shade: 0.6 //遮罩透明度
                    ,anim: 1 //0-6的动画形式，-1不开启
                    ,content:content
                });
            } else {
                layer.msg('没有对应的云仓明细',{icon:6,time:2000},function(index){
                    layer.close(index);
                });
            }
        },
        function(xhr, status) {

        }
    );
}
function exportProduct_inventorydata(laypage){
    $("#expirationdateselect").show();
        layer.open({
        type: 1,
        title:"选择到期日期",
        area: ['450px', '100px'],
        content: $('#expirationdateselect')
    });
}
function beginExport(){
    var url="/product/api/productInventoryExprot?expiration_begin="+$("#expirtion_begin").val()+"&expiration_end="+$("#expirtion_end").val()+"&type=2";
    window.location.href = url;
}
