var laypage = undefined;
var flag = undefined;

function init_goods_category(lay,f,show,BbcGrid) {
    laypage = lay;
    flag = f;
    viewWarehouse_ad();
    var grid = new BbcGrid();
    grid.initTable($("#goodsmgr_stock_category_table"),getSetting_goods_category());
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
function getSetting_goods_category() {
    var setting = {
        url:"/product/api/getProducts",
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        rownumbers : true, // 是否显示前面的行号
        datatype : "json", // 返回的数据类型
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        colModel:[
            {label:"勾选",name:"warehouseId",index:"warehouseId",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                if($("#select_all").prop("checked")){
                    return "<span class='checkbox-one'><input warehouseId=\""+rowObject.warehouseId+"\" sku=\""+rowObject.csku+"\" type='checkbox' checked/></span>";
                } else {
                    return "<span class='checkbox-one'><input warehouseId=\""+rowObject.warehouseId+"\" sku=\""+rowObject.csku+"\" type='checkbox'/></span>";
                }
            }},
            {label:"商品名称",name:"ctitle",index:"product_name",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"SKU",name:"csku",index:"csku",align:"center",sortable:true},
            {label:"商品分类",name:"cname",index:"cname",align:"center",sortable:true},
            {label:"商品类别",name:"typeName",index:"typeName",align:"center",sortable:true, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"状态",name:"istatus",index:"istatus",align:"center",sortable:true, formatter:function(cellvalue, options, rowObject){
                return deal_with_status_value(cellvalue)
            }},
            {label:"所属仓库",name:"warehouseName",index:"warehouse_name",align:"center",sortable:false},
            {label:"操作",name:"detail",index:"businessRemark",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return "<span style='cursor: pointer;'  warehouseId=\""+rowObject.warehouseId+"\" sku=\""+rowObject.csku+"\" onclick='setType(this)'>设置类别</span>"
            }}
        ],
        viewrecords : true,
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#goodsmgr_stock_category_pagination",//分页
        caption:"商品分类列表",//表名称
        pagerpos : "center",
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        caption:"商品分类列表",//表名称,
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
            return JSON.stringify(getSearchParam())
        }
    }

    return setting;
}

function getSearchParam() {
    var categoryId = $("#small_select").val();
    var status = $("#goods_status").val();
    var title = $("#searchInput").val();
    var warehouseId = $(".Storage-distribution-active").val();
    var type = $("#select-type").val();

    var param = {
        pageSize: $('#goodsmgr_stock_category_table').getGridParam('rowNum'),
        currPage: $('#goodsmgr_stock_category_table').getGridParam('page')
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

/**
 * 点击按钮查询
 */
function searchGoods_goodsmgr_stock_category(){
    $("#goodsmgr_stock_category_table").jqGrid('setGridParam',{page: 1}).trigger('reloadGrid');
}

//按下enter进行查询
function getGoodsListByCondition_goodsmgr_stock_category() {
    $("#goodsmgr_stock_category_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
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

//设置产品类型（单个设置）
function setType(obj){
    var $obj = $(obj);
    layer.open({
        title: '设置商品类别',
        content: '<div class="market_pop">' +
        '<span>商品类别：</span>' +
        '<span>' +
        '<select class="type-set">' +
        '</select>'+
        '</span>'+
        '</div>',
        btn: ['确定','取消'],
        success:function(layero, index){
            ajax_get("/product/api/getAllTypes", "", "application/json",
                function (data) {
                    if (data) {
                        $(".market_pop .type-set").empty();
                        var html = "<option>请选择</option>";
                        $.each(data,function(i,item){
                            html += "<option name='"+item.name+"' tid='"+item.id+"'>"+item.name+"</option>";
                        });
                        $(".market_pop .type-set").append(html);
                    }
                },
                function (xhr, status) {
                }
            );
        },
        //i和currdom分别为当前层索引、当前层DOM对象
        yes: function(i, currdom) {
            var $checkedType = $(".market_pop .type-set").find("option:selected");
            var name = $checkedType.attr("name");
            var tid = $checkedType.attr("tid");
            if (!tid) {
                layer.msg("请选择类型", {icon : 2, time : 2000});
                return;
            }
            var products = [{sku : $obj.attr("sku"), warehouseId : $obj.attr("warehouseid")}];
            var param = {
                typeName : name,
                typeId : tid,
                products : products
            };
            ajax_post("/product/api/chooseProductType", JSON.stringify(param), "application/json",
                function (data) {
                    if (data) {
                        data = JSON.parse(data);
                        if (data.suc) {
                            layer.msg("设置商品类型成功",{icon : 1, time : 2000},function(){
                                layer.closeAll();
                                //刷新页面
                                $("#goodsmgr_stock_category_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
                            });
                        } else {
                            layer.msg(data.msg, {icon : 2 , time : 2000});
                        }
                    }
                },
                function (xhr, status) {

                }
            );
        }
    })
}

//全选当前页，全选搜索结果所有
$("#select_all,#select_curr_all").click(function(){
    var node = $(this);
    var goodsTotal  = $('#goodsmgr_stock_category_table').getGridParam('rowNum');
    if(node.prop("checked")){
        $(".checkbox-one input").prop("checked",true);
    }else{
        $(".checkbox-one input").prop("checked",false);
    }
    if(!goodsTotal){
        node.prop("checked",false);
        layer.msg("搜索记录数为0时不能全选",{icon:5,time:2000});
    }
});

//一键设置商品类别(批量设置)
function set_name_a() {
    var goodsTotal  = parseInt($("#goodsTotal").text());
    var select = $(".checkbox-one input:checked");
    var select_all_flag  = $("#select_all").prop("checked");
    if(!select.length && !(select_all_flag && goodsTotal)){
        layer.msg("请选择商品",{icon:5,time:2000});
        return;
    }
    layer.open({
        title: '设置商品类别',
        content: '<div class="market_pop">' +
        '<span>商品类别：</span>' +
        '<span>' +
        '<select class="type-set">' +
        '</select>'+
        '</span>'+
        '</div>',
        btn: ['确定','取消'],
        success:function(layero, index){
            ajax_get("/product/api/getAllTypes", "", "application/json",
                function (data) {
                    if (data) {
                        $(".market_pop .type-set").empty();
                        var html = "<option>请选择</option>";
                        $.each(data,function(i,item){
                            html += "<option name='"+item.name+"' tid='"+item.id+"'>"+item.name+"</option>";
                        });
                        $(".market_pop .type-set").append(html);
                    }
                },
                function (xhr, status) {

                }
            );
        },
        //i和currdom分别为当前层索引、当前层DOM对象
        yes: function(i, currdom) {
            var $checkedType = $(".market_pop .type-set").find("option:selected");
            var name = $checkedType.attr("name");
            var tid = $checkedType.attr("tid");
            if (!tid) {
                layer.msg("请选择类型", {icon : 2, time : 2000});
                return;
            }
            var products = [];
            var product;
            $.each(select,function(i,item){
                product = {};
                product.sku = $(item).attr("sku");
                product.warehouseId = $(item).attr("warehouseid");
                products.push(product);
            });
            var param = {
                typeName : name,
                typeId : tid,
                products : products
            };
            ajax_post("/product/api/chooseProductType", JSON.stringify(param), "application/json",
                function (data) {
                    if (data) {
                        data = JSON.parse(data);
                        if (data.suc) {
                            layer.msg("批量设置商品类型成功",{icon : 1, time : 2000},function(){
                                layer.closeAll();
                                //刷新页面
                                if($("#select_curr_all").prop("checked")){
                                    $("#select_curr_all").prop("checked",false);
                                }
                                if($("#select_all").prop("checked")){
                                    $("#select_all").prop("checked",false);
                                }
                                $("#goodsmgr_stock_category_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
                            });
                        } else {
                            layer.msg(data.msg, {icon : 2 , time : 2000});
                        }
                    }
                },
                function (xhr, status) {

                }
            );
        }
    })
}
