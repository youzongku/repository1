/**
 * Created by Administrator on 2016/7/26.
 */
/**商品属性*/
var ATTR_GOODS = "1";
/**购物车属性*/
var ATTR_SHOPPING_CART = "2";
/**用户属性*/
var ATTR_USER = "3";
//定义全局变量
var layer = undefined;
var laypage = undefined;
var trashImg = "<img onclick='deleteCondt(this)' src='../img/trash.png'/>";

//初始化
function init_pro_category(layerParam, laypageParam) {
    //初始化全局变量
    layer = layerParam;
    laypage = laypageParam;
    loadPromotionCategories(1);
    // 属性查询条件
    getAttrs(function (data) {
        var options = "<option value=''>请选择</option>";
        for (var key in data) {
            options += "<option value='" + key + "'>" + data[key] + "</option>";
        }
        $("#search_attr_select").html(options);
    });
}

//初始化
function init_pro_category_new(layerParam, laypageParam,BbcGrid) {
    //初始化全局变量
    layer = layerParam;
    laypage = laypageParam;

    var grid = new BbcGrid();
    grid.initTable($('#promotion_category_table'), getSetting_promotion_category());

    // 属性查询条件
    getAttrs(function (data) {
        var options = "<option value=''>请选择</option>";
        for (var key in data) {
            options += "<option value='" + key + "'>" + data[key] + "</option>";
        }
        $("#search_attr_select").html(options);
    });
}

function getSetting_promotion_category() {
    var setting = {
        url:"../market/pro/load/protypes",
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        rownumbers : true, // 是否显示前面的行号
        datatype : "json", // 返回的数据类型
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        // styleUI: 'Bootstrap',
        colNames:["id","促销名称","促销类型描述","条件属性","状态","添加时间","添加人","操作"],
        colModel:[{name:"id",index:"id",width:"12%",align:"center",sortable:false,hidden:true},
            {name:"name",index:"name",width:"12%",align:"center",sortable:true},
            {name:"description",index:"description",width:"12%",align:"center",sortable:true,formatter:function(cellvalue,options,rowObject){
                var description = "";
                if (!isnull(cellvalue)) {
                    var arr = cellvalue.split("=");
                    description += '<p>条件：' + arr[0] + '</p>';
                    description += '<p>优惠：' + arr[1] + '</p>';
                }
                return description;
            }},
            {name:"attr",index:"attr",width:"14%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                return getAttrStr(cellvalue.toString());
            }},
            {name:"used",index:"used",width:"14%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                return getUsedStr(cellvalue);
            }},
            {name:"createTime",index:"create_time",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                return formatDateTime(cellvalue);
            }},
            {name:"createUser",index:"create_user",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                var createUser = "";
                if (!isnull(cellvalue)) {
                    createUser = cellvalue;
                }
                return createUser;
            }},
            {name:"used",index:"used",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                var proTypeHtml = "";
                if (!cellvalue) {
                    // 已被使用的，不能编辑
                    proTypeHtml += '<span class="category_edit" onclick="category_edit(this)" proTypeId="' + rowObject.id + '">编辑</span>';
                    // 已被使用的，不能删除
                    proTypeHtml += '<span class="category_del" onclick="category_del(this)" proTypeId="' + rowObject.id + '">删除</span>';
                }
                proTypeHtml += '<span class="category_copy" onclick="category_copy(this)" proTypeId="' + rowObject.id + '">复制到</span>';

                return '<div class=\"category_operate\">' + proTypeHtml + '</div>';
            }}
        ],
        viewrecords : true,
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#promotion_category_pagination",//分页
        caption:"促销类型管理",//表名称
        pagerpos : "center",
        pgbuttons : true,
        autowidth: true,
        rownumbers: true, // 显示行号
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        jsonReader:{
            root: "result",  //数据模型
            page: "currPage",//数据页码
            total: "totalPage",//数据总页码
            records: "rows",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        },
        serializeGridData : function() {
            return JSON.stringify(getSearchParam_promotion_category($(this).jqGrid('getGridParam', 'postData')))
        }
    };
    return setting;
}

function getSearchParam_promotion_category(postData) {
    var param = {
        curr: $('#promotion_category_table').getGridParam('page'),
        pageSize: $('#promotion_category_table').getGridParam('rowNum'),
        proTypeName: $("#promotion_name").val(),
        createDate: $("#seachTime0").val(),
        attr: $("#search_attr_select").val(),
        used: $("#search_used_select").val(),
        sord:postData.sord,
        sidx:postData.sidx
    };
    return param;
}

function getList_promotion_category() {
    $("#promotion_category_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
}

$('#search_attr_select').change(function(){
    $("#promotion_category_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
});

$('#search_used_select').change(function(){
    $("#promotion_category_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
});

/**回到列表*/
function goBackToList() {
    $('.promotion_category').show();
    $('.add_promotion_box').hide();
    //loadPromotionCategories();
    $("#promotion_category_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}

/**加载促销类型列表*/
function loadPromotionCategories(obj, red) {
    var currPage = "1";
    var pageSize = $("#proType_pageSize").val();
    if (pageSize == undefined || pageSize == "" || pageSize == 0) {
        pageSize = 10;
    }
    if (red) {
        currPage = obj.curr;
    }
    var param = {
        curr: currPage,
        pageSize: pageSize,
        proTypeName: $("#promotion_name").val(),
        createDate: $("#seachTime0").val(),
        attr: $("#search_attr_select").val(),
        used: $("#search_used_select").val()
    };
    ajax_post("../market/pro/load/protypes", param, undefined,
        function (response) {
            var result = response.result;
            var totalPage = response.totalPage;
            var currPage = response.currPage;
            var totalCount = response.rows;
            $("#proTypeTotal").text(totalCount);
            $("#proTypePages").text(totalPage);
            var tbody = $("#category_table tbody");
            tbody.empty();
            if (totalCount > 0) {
                var seq = (currPage - 1) * pageSize;
                for (var i = 0; i < result.length; i++) {
                    seq++;
                    var proType = result[i];
                    var proTypeHtml = '';
                    proTypeHtml += '<tr>';
                    proTypeHtml += '<td>' + seq + '</td>';
                    proTypeHtml += '<td>' + proType.name + '</td>';
                    var description = '';
                    if (!isnull(proType.description)) {
                        var arr = proType.description.split("=");
                        description += '<p>条件：' + arr[0] + '</p>';
                        description += '<p>优惠：' + arr[1] + '</p>';
                    }
                    proTypeHtml += '<td class="pro_intro">' + description + '</td>';
                    proTypeHtml += '<td>' + getAttrStr(proType.attr + '') + '</td>';
                    proTypeHtml += '<td>' + getUsedStr(proType.used) + '</td>';
                    proTypeHtml += '<td class="schedule_time">' + formateDate(proType.createTime) + '</td>';
                    var createUser = "";
                    if (!isnull(proType.createUser)) {
                        createUser = proType.createUser;
                    }
                    proTypeHtml += '<td>' + createUser + '</td>';
                    proTypeHtml += '<td class="category_operate">';
                    if (!proType.used) {
                        // 已被使用的，不能编辑
                        proTypeHtml += '<span class="category_edit" onclick="category_edit(this)" proTypeId="' + proType.id + '">编辑</span>';
                        // 已被使用的，不能删除
                        proTypeHtml += '<span class="category_del" onclick="category_del(this)" proTypeId="' + proType.id + '">删除</span>';
                    }
                    proTypeHtml += '<span class="category_copy" onclick="category_copy(this)" proTypeId="' + proType.id + '">复制到</span>';
                    proTypeHtml += '</td>';
                    proTypeHtml += '</tr>';
                    tbody.append(proTypeHtml);
                }
            } else {
                layer.msg("没有符合条件的促销类型信息", {icon: 5, time: 2000});
            }
            init_promotion_category_pagination(totalPage, currPage);
        },
        function (XMLHttpRequest, textStatus) {
            layer.msg("获取促销类型数据失败！", {icon: 5, time: 2000});
        }
    );
}

//初始化分页栏
function init_promotion_category_pagination(pages, currPage) {
    if ($("#proType_pagination")[0] != undefined) {
        $("#proType_pagination").empty();
        laypage({
            cont: 'proType_pagination',
            pages: pages,
            curr: currPage,
            groups: 5,
            skin: '#55ccc8',
            first: '首页',
            last: '尾页',
            prev: '上一页',
            next: '下一页',
            skip: true,
            jump: function (obj, first) {
                //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                if (!first) {
                    loadPromotionCategories(obj, true);
                }
            }
        });
    }
}

function deleteCondt(obj) {
    $(obj).parent().parent().remove();
}

// 删除
function category_del(obj) {
    var proTypeId = $(obj).attr("proTypeId");
    var param = {id: proTypeId};
    ajax_post("../market/pro/protype/rm", JSON.stringify(param), "application/json",
        function (response) {
            if (!response.success) {
                layer.msg(response.errorMessage, {icon: 5, time: 2000});
                return;
            }
            layer.msg(response.resultObject, {icon: 1, time: 2000}, function () {
                //loadPromotionCategories();
                $("#promotion_category_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
            });
        },
        function (XMLHttpRequest, textStatus) {
            layer.msg("删除失败！", {icon: 5, time: 2000});
        }
    );
}

// 复制
function category_copy(obj) {
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['390px', '180px'],
        content: $('.category_copy_box'),
        btn: ['确定', '取消'],
        title: '复制促销类型',
        yes: function (oIndex) {
            var newProTypeName = $.trim($("#newProTypeName").val());
            if (isnull(newProTypeName)) {
                layer.msg("输入框不能为空！", {icon: 2, time: 2000});
                return;
            }
            var proTypeId = $(obj).attr("proTypeId");
            var param = {
                id: proTypeId,
                newProTypeName: newProTypeName
            };
            ajax_post("../market/pro/protype/cp", JSON.stringify(param), "application/json",
                function (response) {
                    if (!response.success) {
                        layer.msg(response.errorMessage, {icon: 5, time: 2000});
                        return;
                    }
                    layer.msg(response.resultObject, {icon: 1, time: 2000}, function () {
                        layer.close(oIndex);
                        $("#newProTypeName").val("");
                        //loadPromotionCategories();
                        $("#promotion_category_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
                    });
                },
                function (XMLHttpRequest, textStatus) {
                    layer.msg("复制失败！", {icon: 5, time: 2000});
                }
            );
        },
        cancel: function (index) {
            layer.close(index);
            $("#newProTypeName").val("");
        }
    });
}

// 编辑
function category_edit(obj) {
    cleanSaveAndEditDiv();
    // 进入编辑页
    $('.promotion_category').hide();
    $('.add_promotion_box').show();


    var proTypeId = $(obj).attr("proTypeId");
    setEditValues(proTypeId);
}

/**
 * 编辑之前要设置数据
 * @param proTypeId
 */
function setEditValues(proTypeId) {
    initDefault();
    var param = {id: proTypeId};
    ajax_post("../market/pro/load/protype", JSON.stringify(param), "application/json",
        function (response) {
            console.log(response);
            $("#pro_type_name").val(response.name);
            $("#pro_type_id").val(response.id);// 促销类型id
            var attr = response.attr;
            var attrStr = attr + "";
            var conditionList = response.fullProCondtDtoList;
            var privilegeList = response.fullProPvlgDtoList;
            var description = response.description;

            // 属性选中
            var attrLi = $(".commodity_attribute_tab li[attr='" + attr + "']");
            attrLi.addClass("attribute_tab").siblings().removeClass();
            // 条件&优惠div显示，attrLi的index和条件&优惠div的index是一样的
            $(".commodity_attribute > div").hide().eq(attrLi.index()).show();

            switch (attrStr) {
                case ATTR_GOODS:
                    $("#add_goods_condition_div").empty();
                    break;
                case ATTR_SHOPPING_CART:
                    $("#add_shopping_cart_condition_div").empty();
                    break;
                case ATTR_USER:
                    break;
                default:
                    break;
            }

            // 促销类型原有条件
            getCondtsByAttr(attr, function (data) {
                for (var i in conditionList) {
                    var condt_options = '';
                    for (var j in data) {
                        if (data[j].id == conditionList[i].id) {
                            condt_options += "<option selected='selected' value='" + data[j].id + "'>" + data[j].name + "</option>";
                        } else {
                            condt_options += "<option value='" + data[j].id + "'>" + data[j].name + "</option>";
                        }
                    }
                    // 一个condt_options是一个条件
                    switch (attrStr) {
                        case ATTR_GOODS:
                            $("#add_goods_condition_div").append("<p class='add_condition_line'><em>添加条件:</em><span> <select onchange='createGoodsConditionDescriptions(this)' class='promotion_select'>" + condt_options + "</select>" + trashImg + "</span></p>");
                            break;
                        case ATTR_SHOPPING_CART:
                            $("#add_shopping_cart_condition_div").append("<p class='add_condition_line'><em>添加条件:</em><span> <select onchange='createShoppingCartConditionDescriptions(this)' class='promotion_select'>" + condt_options + "</select>" + trashImg + "</span></p>");
                            break;
                        case ATTR_USER:
                            break;
                        default:
                            break;
                    }
                }
            });
            // 促销类型原有优惠
            getDiscounts(attr,function (data) {
                var pvlg_options = '';
                for (var j in data) {
                    // 优惠只有一个
                    if (data[j].id == privilegeList[0].id) {
                        pvlg_options += "<option selected='selected' value='" + data[j].id + "'>" + data[j].name + "</option>";
                    } else {
                        pvlg_options += "<option value='" + data[j].id + "'>" + data[j].name + "</option>";
                    }
                }

                switch (attrStr) {
                    case ATTR_GOODS:
                        $("#default_goods_discount_select").html(pvlg_options);
                        break;
                    case ATTR_SHOPPING_CART:
                        $("#default_shopping_cart_discount_select").html(pvlg_options);
                        break;
                    case ATTR_USER:
                        break;
                    default:
                        break;
                }
            });
            // 描述
            var descriptionArr = description.split("=");
            switch (attrStr) {
                case ATTR_GOODS:
                    $("#goods_condition_description").html(descriptionArr[0]);
                    $("#goods_privilege_description").html(descriptionArr[1]);
                    break;
                case ATTR_SHOPPING_CART:
                    $("#shopping_cart_condition_description").html(descriptionArr[0]);
                    $("#shopping_cart_privilege_description").html(descriptionArr[1]);
                    break;
                case ATTR_USER:
                    break;
                default:
                    break;
            }
        },
        function (XMLHttpRequest, textStatus) {
            layer.msg("获取促销类型数据失败！", {icon: 2, time: 2000});
        }
    );
}

// 清空原有的数据
function cleanSaveAndEditDiv() {
    $("#pro_type_id").val("");
    $("#pro_type_name").val("");
    var attrLi = $(".commodity_attribute_tab li[attr='" + ATTR_GOODS + "']");
    attrLi.addClass("attribute_tab").siblings().removeClass();
    // 条件&优惠div显示，attrLi的index和条件&优惠div的index是一样的
    $(".commodity_attribute > div").hide().eq(attrLi.index()).show();

    // 商品
    $("#add_goods_condition_div").empty();
    $("#default_goods_discount_select").empty();
    // 清空描述
    $("#goods_condition_description").html("");
    $("#goods_privilege_description").html("");

    // 购物车
    $("#add_shopping_cart_condition_div").empty();
    $("#default_shopping_cart_discount_select").empty();
    // 清空描述
    $("#shopping_cart_condition_description").html("");
    $("#shopping_cart_privilege_description").html("");
}

// 查询
$('body').on("click", '#searchProTypeBtn', function () {
    //loadPromotionCategories();
    $("#promotion_category_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
});

// 点击添加促销类型按钮
$('body').on("click", ".add_promotion_category", function () {
    $('.promotion_category').hide();
    $('.add_promotion_box').show();
    cleanSaveAndEditDiv();
    initDefault();
});
/**
 * 初始化默认的条件&优惠
 */
function initDefault() {
    // 默认的商品条件
    getCondtsByAttr(ATTR_GOODS, function (data) {
        var condt_options = "<option value=''>请选择</option>";
        for (var i in data) {
            condt_options += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
        }
        $("#add_goods_condition_div").append("<p class='add_condition_line'><em>添加条件:</em><span><select onchange='createGoodsConditionDescriptions(this)' class='promotion_select' id='default_goods_condition_select'>" + condt_options + "</select></span></p>");
    });

    // 默认的购物车条件
    getCondtsByAttr(ATTR_SHOPPING_CART, function (data) {
        var condt_options = "<option value=''>请选择</option>";
        for (var i in data) {
            condt_options += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
        }
        $("#add_shopping_cart_condition_div").append("<p class='add_condition_line'><em>添加条件:</em><span> <select onchange='createShoppingCartConditionDescriptions(this)' class='promotion_select' id='default_shopping_cart_condition_select'>" + condt_options + "</select></span></p>");
    });

    // 默认的商品优惠
    getDiscounts(1,function (data) {
        var pvlg_options = "<option value=''>请选择</option>";
        for (var i in data) {
            pvlg_options += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
        }
        $("#default_goods_discount_select").html(pvlg_options);
    });
    // 默认的购物车优惠
    getDiscounts(2,function (data) {
        var pvlg_options = "<option value=''>请选择</option>";
        for (var i in data) {
            pvlg_options += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
        }
        $("#default_shopping_cart_discount_select").html(pvlg_options);
    });
}

// 选择页记录数
$('body').on("change", "#proType_pageSize", function () {
    loadPromotionCategories();
})

// 返回列表
$('body').on("click", ".back_promotion_category", function () {
    $('.promotion_category').show();
    $('.add_promotion_box').hide();
})

// 促销属性tab切换
$("body").on("click", ".commodity_attribute_tab li", function () {
    $(this).addClass("attribute_tab").siblings().removeClass();
    $(".commodity_attribute > div").hide().eq($(".commodity_attribute_tab li").index(this)).show();
});

//*********************************************************商品属性的 start*********************************************************
// 点击添加商品条件
$('body').on("click", ".add_goods_condition_button", function () {
    $("#add_goods_condition_div").append("<p class='add_condition_line'><em>添加条件:</em><span> <select onchange='createGoodsConditionDescriptions(this)' class='promotion_select'></select>" + trashImg + "</span></p>");
    // 获取条件属性
    var attr = getSelectedAttr();
    getCondtsByAttr(attr, function (data) {
        var options = "<option value=''>请选择</option>";
        for (var i in data) {
            options += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
        }
        $("#add_goods_condition_div").find("p:last-child").find("select").html(options);
    });
});

// 点击添加商品优惠
$('body').on("click", ".add_goods_discount_button", function () {
    getDiscounts(1,function (data) {
        var pvlg_options = "<option value=''>请选择</option>";
        for (var i in data) {
            pvlg_options += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
        }
        $("#add_goods_discount_div").append("<p><em>获得优惠:</em><span><select onchange='createGoodsPrivilegeDescriptions()' class='promotion_select'>"+pvlg_options+"</select></span> </p>");
    });
});
// 描述
function createGoodsConditionDescriptions(obj) {
    // 需要进行去重
    // 原有的条件
    var cIdArray = new Array();
    $("#add_goods_condition_div").find('select').find("option:selected").each(function (i, e) {
        if (!isnull($(e).val())) {
            cIdArray.push($(e).val());
        }
    });
    // 当前选中的条件
    var currCondtId = $(obj).val();
    var condtRepeatedCount = 0;
    if (!isnull(currCondtId)) {
        for (var i in cIdArray) {
            if (cIdArray[i] == currCondtId) {
                condtRepeatedCount++;
            }
        }
    }
    if (condtRepeatedCount > 1) {
        layer.msg("条件不能重复", {icon: 2, time: 1000});
        $(obj).html($(obj).html());
        return;
    }

    var cdArray = new Array();
    $("#add_goods_condition_div").find('select').find("option:selected").each(function (i, e) {
        if (!isnull($(e).val())) {
            cdArray.push($(e).text());
        }
    });
    $("#goods_condition_description").html(cdArray.toString());
}
function createGoodsPrivilegeDescriptions() {
    var pdArray = new Array();
    $("#add_goods_discount_div").find('select').find("option:selected").each(function (i, e) {
        if (!isnull($(e).val())) {
            pdArray.push($(e).text());
        }
    });
    $("#goods_privilege_description").html(pdArray.toString());
}
//*********************************************************商品属性的 end*********************************************************


//*********************************************************购物车属性的 start*********************************************************
// 点击添加购物车条件
$('body').on("click", ".add_shopping_cart_condition_button", function () {
    $("#add_shopping_cart_condition_div").append("<p class='add_condition_line'><em>添加条件:</em><span> <select onchange='createShoppingCartConditionDescriptions(this)' class='promotion_select'></select>" + trashImg + "</span></p>");
    // 获取条件属性
    var attr = getSelectedAttr();
    getCondtsByAttr(attr, function (data) {
        var options = "<option value=''>请选择</option>";
        for (var i in data) {
            options += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
        }
        $("#add_shopping_cart_condition_div").find("p:last-child").find("select").html(options);
    });
});

// 点击添加购物车优惠
$('body').on("click", ".add_shopping_cart_discount_button", function () {
    getDiscounts(2,function (data) {
        var pvlg_options = "<option value=''>请选择</option>";
        for (var i in data) {
            pvlg_options += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
        }
        $("#add_shopping_cart_discount_div").append("<p><em>获得优惠:</em><span><select onchange='createShoppingCartPrivilegeDescriptions()' class='promotion_select'>"+pvlg_options+"</select></span> </p>");
    });
});
// 描述
function createShoppingCartConditionDescriptions(obj) {
    // 需要进行去重
    // 原有的条件
    var cIdArray = new Array();
    $("#add_shopping_cart_condition_div").find('select').find("option:selected").each(function (i, e) {
        if (!isnull($(e).val())) {
            cIdArray.push($(e).val());
        }
    });
    // 当前选中的条件
    var currCondtId = $(obj).val();
    var condtRepeatedCount = 0;
    if (!isnull(currCondtId)) {
        for (var i in cIdArray) {
            if (cIdArray[i] == currCondtId) {
                condtRepeatedCount++;
            }
        }
    }
    if (condtRepeatedCount > 1) {
        layer.msg("条件不能重复", {icon: 2, time: 1000});
        $(obj).html($(obj).html());
        return;
    }

    var cdArray = new Array();
    $("#add_shopping_cart_condition_div").find('select').find("option:selected").each(function (i, e) {
        if (!isnull($(e).val())) {
            cdArray.push($(e).text());
        }
    });
    $("#shopping_cart_condition_description").html(cdArray.toString());
}

function createShoppingCartPrivilegeDescriptions() {
    var pdArray = new Array();
    $("#add_shopping_cart_discount_div").find('select').find("option:selected").each(function (i, e) {
        if (!isnull($(e).val())) {
            pdArray.push($(e).text());
        }
    });
    $("#shopping_cart_privilege_description").html(pdArray.toString());
}
//*********************************************************购物车属性的 end*********************************************************

// 保存新建/更新的促销类型
$('body').on("click", ".save_promotion_category", function () {
    var id = $("#pro_type_id").val();
    var p_name = $.trim($("#pro_type_name").val());
    var attr = getSelectedAttr();
    // 条件id，优惠id，条件描述，优惠描述
    var cIdArray = new Array(), pIdArray = new Array(), cdArray = new Array(), pdArray = new Array();
    var unselectedCondtCount = 0;
    switch (attr) {
        case ATTR_GOODS:
            $("#add_goods_condition_div").find('select').find("option:selected").each(function (i, e) {
                if (!isnull($(e).val())) {
                    cIdArray.push($(e).val());
                    cdArray.push($(e).text());
                } else {
                    unselectedCondtCount = unselectedCondtCount + 1;
                }
            });
            $("#add_goods_discount_div").find('select').find("option:selected").each(function (i, e) {
                if (!isnull($(e).val())) {
                    pdArray.push($(e).text());
                    pIdArray.push($(e).val());
                }
            });
            break;
        case ATTR_SHOPPING_CART:
            $("#add_shopping_cart_condition_div").find('select').find("option:selected").each(function (i, e) {
                if (!isnull($(e).val())) {
                    cIdArray.push($(e).val());
                    cdArray.push($(e).text());
                } else {
                    unselectedCondtCount = unselectedCondtCount + 1;
                }
            });
            $("#add_shopping_cart_discount_div").find('select').find("option:selected").each(function (i, e) {
                if (!isnull($(e).val())) {
                    pdArray.push($(e).text());
                    pIdArray.push($(e).val());
                }
            });
            break;
        case ATTR_USER:
            break;
        default:
            break;
    }
    var description = cdArray.toString() + "=" + pdArray.toString();
    if (isnull(p_name)) {
        layer.msg("促销类型名称不能为空", {icon: 2, time: 1000});
        $("#pro_type_name").focus();
        return;
    }
    if (unselectedCondtCount > 0) {
        layer.msg("请选择条件", {icon: 2, time: 1000});
        return;
    }
    if (pIdArray.length == 0) {
        layer.msg("请选择优惠", {icon: 2, time: 1000});
        return;
    }

    // 新增
    var url = "../market/pro/protype/create";
    var param = {
        name: p_name,
        attr: attr,
        description: description,
        condtIds: cIdArray.toString(),
        pvlgIds: pIdArray.toString()
    };
    // var create_user = "";
    // isaulogin(function (email) {
    //     create_user = email;
    // });

    // 更新
    if (!isnull(id)) {
        param["id"] = id;
        // param["lastUpdateUser"] = create_user;// 更新人
        url = "../market/pro/protype/update";
    }else{
        // param["createUser"] = create_user;// 创建人
    }

    $.ajax({
        url: url,
        data: param,
        type: "POST",
        dataType: "json",
        success: function (data) {
            // 用户未登录
            if(data.code == 101){
                layer.msg(data.msg, {icon: 1, time: 2000}, function(){
                    window.location.href = "/backstage/login.html"
                });
                return
            }
            if (!data.success) {
                layer.msg(data.errorMessage, {icon: 5, time: 1000});
                return;
            }
            layer.msg(data.resultObject, {icon: 1, time: 1000}, function () {
                goBackToList();
            });
        }
    });
});

// 重置
$("body").on("click", ".reset_promotion_category", function () {
    // 重置分为两种：
    var pro_type_id = $("#pro_type_id").val();
    //layer.msg("重置了，pro_type_id="+pro_type_id,{icon:1,time:2000});
    cleanSaveAndEditDiv();
    if (isnull(pro_type_id)) {
        initDefault();
    } else {
        // 编辑：恢复原有的数据
        setEditValues(pro_type_id);
    }
});

/**
 * 获取选中的促销属性
 * @returns {*|jQuery}
 */
function getSelectedAttr() {
    var attr = $(".commodity_attribute_tab").find(".attribute_tab").attr("attr");
    return attr;
}

/**
 * 根据属性获取条件
 * @param attr
 * @param callback
 */
function getCondtsByAttr(attr, callback) {
    $.ajax({
        url: "../market/pro/load/condts",
        data: {attr: attr},
        type: "GET",
        dataType: "json",
        success: function (data) {
            if (!isnull(callback)) {
                callback.call(this, data);
            }
        }
    });
}
/**
 * 获取优惠
 * @param callback
 */
function getDiscounts(attr,callback) {
    $.ajax({
        url: "../market/pro/load/pvlgs?attr="+attr,
        type: "GET",
        dataType: "json",
        success: function (data) {
            if (!isnull(callback)) {
                callback.call(this, data);
            }
        }
    });
}

function getUsedStr(used) {
    if (used) {
        return "已应用";
    }
    return "未应用";
}