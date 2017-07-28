/**
 * Created by Administrator on 2017/2/14.
 */

var laypage = undefined;
var flag = undefined;
var globalReturnCoefficientMap = {}
function initReturnOrderCoefficients(layerParam, laypageParam){
    layer = layerParam;
    laypage = laypageParam;
    console.log("initReturnOrderCoefficients")
    globalReturnCoefficientMap = {}

    rc_getWarehouses()
    rc_getCategories()
    rc_getAllTypes()

}

function reloadRC(){
    $("#return_coefficients_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}

function getSetting_rc() {
    globalReturnCoefficientMap = {}
    var setting = {
        url:"/purchase/returnod/coefficient/list",
        rownumbers : true, // 是否显示前面的行号
        datatype : "json", // 返回的数据类型
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        // colNames:["订单编号"],
        colModel:[
            {label:"商品编号",name:"sku",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return rowObject.productInfo.csku
            }},
            {label:"商品名称",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return rowObject.productInfo.ctitle
            }},
            {label:"商品分类",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(rowObject.productInfo.cname)
            }},
            {label:"商品类别",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(rowObject.productInfo.typeName)
            }},
            {label:"仓库id",name:"warehouseId",align:"center",hidden:true,sortable:false, formatter:function(cellvalue, options, rowObject){
                return rowObject.productInfo.warehouseId
            }},
            {label:"所属仓库",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return rowObject.productInfo.warehouseName
            }},
            {label:"退款系数id",name:"racId",align:"center",hidden:true,sortable:false, formatter:function(cellvalue, options, rowObject){
                if(rowObject.rac) {
                    return rowObject.rac.id
                }
                return ""
            }},
            {label:"30天",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                if(rowObject.rac) {
                    var coefficientJsonValue = rowObject.rac.coefficientJsonValue
                    if (coefficientJsonValue) {
                        return coefficientJsonValue['30']
                    }
                }
                return deal_with_illegal_value(undefined)
            }},
            {label:"60天",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                if(rowObject.rac){
                    var coefficientJsonValue = rowObject.rac.coefficientJsonValue
                    if(coefficientJsonValue){
                        return coefficientJsonValue['60']
                    }
                }
                return deal_with_illegal_value(undefined)
            }},
            {label:"90天",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                if(rowObject.rac) {
                    var coefficientJsonValue = rowObject.rac.coefficientJsonValue
                    if (coefficientJsonValue) {
                        return coefficientJsonValue['90']
                    }
                }
                return deal_with_illegal_value(undefined)
            }},
            {label:"180天",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                if(rowObject.rac) {
                    var coefficientJsonValue = rowObject.rac.coefficientJsonValue
                    if (coefficientJsonValue) {
                        return coefficientJsonValue['180']
                    }
                }
                return deal_with_illegal_value(undefined)
            }},
            {label:"360天",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                if(rowObject.rac) {
                    var coefficientJsonValue = rowObject.rac.coefficientJsonValue
                    if (coefficientJsonValue) {
                        return coefficientJsonValue['360']
                    }
                }
                return deal_with_illegal_value(undefined)
            }},
            {label:"540天",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                if(rowObject.rac) {
                    var coefficientJsonValue = rowObject.rac.coefficientJsonValue
                    if (coefficientJsonValue) {
                        return coefficientJsonValue['540']
                    }
                }
                return deal_with_illegal_value(undefined)
            }},
            {label:"720天",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                if(rowObject.rac) {
                    var coefficientJsonValue = rowObject.rac.coefficientJsonValue
                    if (coefficientJsonValue) {
                        return coefficientJsonValue['720']
                    }
                }
                return deal_with_illegal_value(undefined)
            }},
            {label:"1080天",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                if(rowObject.rac) {
                    var coefficientJsonValue = rowObject.rac.coefficientJsonValue
                    if (coefficientJsonValue) {
                        return coefficientJsonValue['1080']
                    }
                }
                return deal_with_illegal_value(undefined)
            }},
            {label:"1080天以上",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                if(rowObject.rac) {
                    var coefficientJsonValue = rowObject.rac.coefficientJsonValue
                    if (coefficientJsonValue) {
                        return coefficientJsonValue['1081']
                    }
                }
                return deal_with_illegal_value(undefined)
            }},
            {label:"操作",sortable:false,align:"center", formatter:function(cellvalue, options, rowObject){
                globalReturnCoefficientMap[rowObject.productInfo.iid] = rowObject
                var opt = "<a href='javascript:;' id='"+rowObject.productInfo.iid+"' onclick='setReturnAmountCoefficientValue(this)'>设置系数</a><br/>"+
                    "<a href='javascript:;' id='"+rowObject.productInfo.iid+"' onclick='showSetCoefficientLogs(this)'>查看日志</a>"
                return opt
            }}
        ],
        viewrecords : true,
        onPaging:function(pageBtn){
            // 清空缓存的数据
            globalReturnCoefficientMap = {}
        },
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#return_coefficients_pagination",//分页
        caption:"商品退款比例列表",//表名称
        pagerpos : "center",
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        jsonReader:{
            root: "list",  //数据模型
            page: "currPage",//数据页码
            total: "totalPage",//数据总页码
            records: "rows",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        },
        ondblClickRow: function(rowid) {
        },
        serializeGridData : function(postData) {
            return JSON.stringify(getParams_rc());
        },
        multiselect: true,
        onSelectRow: function (rowId, status, e) {
        }
    }

    return setting;
}

function getParams_rc(){
    var param = {
        "currPage":$('#return_coefficients_table').getGridParam('page'),
        "pageSize":$('#return_coefficients_table').getGridParam('rowNum'),
        "searchText":$.trim($('#rc_searchText').val()),
        "sort":$('#return_coefficients_table').getGridParam("sortname"),
        "filter":$('#return_coefficients_table').getGridParam("sortorder")
    };
    if($('#rc_categoriesSelect').val()){
        param["categoryId"] = $('#rc_categoriesSelect').val()
    }
    if($('#rc_typesSelect').val()){
        param["typeId"] = $('#rc_typesSelect').val()
    }
    if($('#rc_warehousesSelect').val()){
        param["warehouseId"] = $('#rc_warehousesSelect').val()
    }
    return  param;
}

// 清除设置退款系数弹窗里的数据
function cleanAuditDatas(){
    $("#setReturnAmountCoefficientValueDiv").find("input").each(function (i,e) {
        $(e).val("")
    })
    $("#setReturnAmountCoefficientValueDiv").find("input[name='sku']").val("")
    $("#setReturnAmountCoefficientValueDiv").find("input[name='warehouseId']").val("")
}

function batchSetReturnAmountCoefficientValue(){
    var rowIds = jQuery("#return_coefficients_table").jqGrid('getGridParam', 'selarrrow');
    if(rowIds.length==0){
        layer.msg('请选择要设置系数的商品',{icon:2,time:2000})
        return
    }

    cleanAuditDatas()

    // 选中的商品
    var skuAndWarehouseIdList = new Array()
    $(rowIds).each(function (index, id){
        //由id获得对应数据行
        var row = $("#return_coefficients_table").jqGrid('getRowData', id);
        console.log("sku="+row.sku+"，warehouseId="+row.warehouseId+"，racId="+row.racId);
        skuAndWarehouseIdList.push()
        var racIdAndSkuAndWarehouseIdJson = {sku:row.sku, warehouseId:row.warehouseId}
        var racId = row.racId
        if(racId){
            racIdAndSkuAndWarehouseIdJson['racId']=racId
        }
        skuAndWarehouseIdList.push(racIdAndSkuAndWarehouseIdJson)
    })

    layer.open({
        type: 1,
        title: "批量设置商品退款系数",
        content: $("#setReturnAmountCoefficientValueDiv"),
        area: ['500px', '400px'],
        btn: ["保存","取消"],
        closeBtn: 1,
        shadeClose: true,
        move: false,
        yes: function (index) {
            /*
             {
                 skuAndWarehouseIdList:[
                 {"racId":1,"sku": "IF942-1","warehouseId": 2024},{"racId":1,"sku": "IF942-1","warehouseId": 2024},...
                 ]
                 "coefficientValue": "{\"30\":0.1,\"60\":0.2,\"90\":0.3,\"180\":0.4,\"360\":0.5,\"540\":0.6,\"720\":0.7,\"1080\":0.8}"
             }
             */
            // 具体的系数值
            var coefficientValue = {}
            var validated = true;
            $("#setReturnAmountCoefficientValueDiv").find("input[name='coefficientKey']").each(function (i,e) {
                var val = $.trim($(e).val())
                if(!val){
                    $(e).focus()
                    coefficientValue = {}
                    layer.msg('系数不能为空',{icon:2,time:2000})
                    validated = false;
                    return false;
                }
                // 系数范围校验 [0,1]
                var iValue = parseInt(val)
                if(!(iValue>=0 && iValue<=1)){
                    $(e).focus()
                    coefficientValue = {}
                    layer.msg('系数范围为大于等于0且小于等于1',{icon:2,time:2000})
                    validated = false;
                    return false;
                }
                coefficientValue[$(e).attr("coefficientDays")] = val
            })
            // 系数设置没有通过校验
            if(!validated) return

            var params = {
                skuAndWarehouseIdList: skuAndWarehouseIdList,
                coefficientValue: coefficientValue
            }
            console.log(params)
            var loading_index = layer.load(1, {shade: 0.5});
            ajax_post("/purchase/returnod/coefficient/set", JSON.stringify(params), "application/json",
                function(data) {
                    layer.close(loading_index)
                    if(data.suc){
                        layer.msg(data.msg,{icon:1,time:2000},function () {
                            layer.close(index)
                            reloadRC()
                        })
                    }else{
                        layer.msg("设置商品退款系数失败",{icon:2,time:2000})
                    }
                }
            );
        },
        btn2: function (index) {
            layer.close(index)
        }
    });
}

function setReturnAmountCoefficientValue(obj){
    clickThisTr($(obj).parent().parent())
    var id = $(obj).attr("id")
    var rowObject = globalReturnCoefficientMap[id]
    cleanAuditDatas()
    // 数据回显
    if(rowObject.rac) { // 有系数的情况
        $("#setReturnAmountCoefficientValueDiv").find("input[name='racId']").val(rowObject.rac.id)
        $("#setReturnAmountCoefficientValueDiv").find("input[name='sku']").val(rowObject.rac.sku)
        $("#setReturnAmountCoefficientValueDiv").find("input[name='warehouseId']").val(rowObject.rac.warehouseId)
        var coefficientJsonValue = rowObject.rac.coefficientJsonValue
        if (coefficientJsonValue) {
            $("#setReturnAmountCoefficientValueDiv").find("input[ type='text']").each(function (i,e) {
                $(e).val(coefficientJsonValue[$(e).attr("coefficientDays")])
            })
        }
    }else{// 没有系数的情况
        var productInfo = rowObject.productInfo
        $("#setReturnAmountCoefficientValueDiv").find("input[name='sku']").val(productInfo.csku)
        $("#setReturnAmountCoefficientValueDiv").find("input[name='warehouseId']").val(productInfo.warehouseId)
    }

    layer.open({
        type: 1,
        title: "设置商品退款系数",
        content: $("#setReturnAmountCoefficientValueDiv"),
        area: ['500px', '400px'],
        btn: ["保存","取消"],
        closeBtn: 1,
        shadeClose: true,
        move: false,
        yes: function (index) {
            /*
             {
                 skuAndWarehouseIdList:[
                 {"racId":1,"sku": "IF942-1","warehouseId": 2024}
                 ]
                 "coefficientValue": "{\"30\":0.1,\"60\":0.2,\"90\":0.3,\"180\":0.4,\"360\":0.5,\"540\":0.6,\"720\":0.7,\"1080\":0.8}"
             }
             */
            var skuAndWarehouseIdList = new Array()
            var racIdAndSkuAndWarehouseIdJson = {
                sku:$("#setReturnAmountCoefficientValueDiv").find("input[name='sku']").val(),
                warehouseId:$("#setReturnAmountCoefficientValueDiv").find("input[name='warehouseId']").val()
            }
            var racId = $("#setReturnAmountCoefficientValueDiv").find("input[name='racId']").val()
            if(racId){
                racIdAndSkuAndWarehouseIdJson['racId']=racId
            }
            skuAndWarehouseIdList.push(racIdAndSkuAndWarehouseIdJson)

            // 具体的系数值
            var coefficientValue = {}
            var validated = true;
            $("#setReturnAmountCoefficientValueDiv").find("input[name='coefficientKey']").each(function (i,e) {
                var val = $.trim($(e).val())
                if(!val){
                    $(e).focus()
                    coefficientValue = {}
                    layer.msg('系数不能为空',{icon:2,time:2000})
                    validated = false;
                    return false;
                }
                // 系数范围校验 [0,1]
                var iValue = parseInt(val)
                if(!(iValue>=0 && iValue<=1)){
                    $(e).focus()
                    coefficientValue = {}
                    layer.msg('系数范围为大于等于0且小于等于1',{icon:2,time:2000})
                    validated = false;
                    return false;
                }
                coefficientValue[$(e).attr("coefficientDays")] = val
            })
            // 系数设置没有通过校验
            if(!validated) return

            var params = {
                skuAndWarehouseIdList: skuAndWarehouseIdList,
                coefficientValue: coefficientValue
            }
            console.log(params)
            var loading_index = layer.load(1, {shade: 0.5});
            ajax_post("/purchase/returnod/coefficient/set", JSON.stringify(params), "application/json",
                function(data) {
                    layer.close(loading_index)
                    if(data.suc){
                        layer.msg(data.msg,{icon:1,time:2000},function () {
                            layer.close(index)
                            reloadRC()
                        })
                    }else{
                        layer.msg("设置商品退款系数失败",{icon:2,time:2000})
                    }
                }
            );
        },
        btn2: function (index) {
            layer.close(index)
        }
    });
}

function showSetCoefficientLogs(obj){
    clickThisTr($(obj).parent().parent())

    var id = $(obj).attr("id")
    var rowObject = globalReturnCoefficientMap[id]
    var productInfo = rowObject.productInfo
    var params = {
        sku:productInfo.csku,
        warehouseId:productInfo.warehouseId
    }

    layer.open({
        type: 1,
        title: "查看日志",
        content: $("#setCoefficientLogsDiv"),
        area: ['500px', '400px'],
        btn: ["保存","取消"],
        closeBtn: 1,
        shadeClose: true,
        move: false,
        success: function(){
            ajax_post("/purchase/returnod/coefficient/setlogs", JSON.stringify(params), "application/json",
                function(data) {
                    var setCoefficientLogs = data.setCoefficientLogs
                    var logHtml = ''
                    if(setCoefficientLogs.length>0){
                        for(var i=0; i<setCoefficientLogs.length; i++){
                            var logValue = eval("(" + setCoefficientLogs[i].logValue + ")");
                            for(var k in logValue){
                                var day = k+"天"
                                if(k==1081){
                                    day = "1080天以上"
                                }
                                logHtml += "<span>"+setCoefficientLogs[i].createTimeStr+"　　"
                                    +deal_with_illegal_value(setCoefficientLogs[i].createUser)+"将"+day+"系数设置为"+logValue[k]+"。</span>"
                            }
                        }
                    }else{
                        logHtml="<span>暂无日志</span>"
                    }
                    $("#setCoefficientLogsDiv").html(logHtml)
                }
            );
        },
        yes: function (index) {
            layer.close(index)
        }
    });
}

// 加载仓库
function rc_getWarehouses(){
    ajax_get("/inventory/queryWarehouse",undefined,undefined,function(data){
        var warehousesHtml = "<option value=''>请选择</option>";
        if (data.length > 0) {
            for (var i in data) {
                warehousesHtml += "<option value='" + data[i].id + "'>" + data[i].warehouseName + "</option>";
            }
            $("#rc_warehousesSelect").empty();
            $("#rc_warehousesSelect").append(warehousesHtml);
        }
    })
}

//加载商品类目
function rc_getCategories(){
    ajax_get("/product/api/realCateQuery?level=1", "", "application/json",
        function (data) {
            var cateHtml = "<option value='' selected>所有商品</option>";
            if (data && data.length > 0) {
                $.each(data, function (i, item) {
                    cateHtml += "<option value='" + item.iid + "'>" + item.cname + "</option>";
                });
                $("#rc_categoriesSelect").empty();
                $("#rc_categoriesSelect").append(cateHtml);
            }
        },
        function (xhr, status) {
        }
    );
}
// 获取所有商品类别
function rc_getAllTypes(){
    ajax_get("/product/api/getAllTypes",undefined,"",
        function(data) {
            $("#rc_typesSelect").empty();
            var opHtml = '<option value="" >所有商品类别</option>';
            $.each(data,function(i,item){
                opHtml += '<option value="'+item.id+'">'+item.name+'</option>';
            });
            $("#rc_typesSelect").html(opHtml);
        },function(e){
            console.log(e);
        }
    );
}