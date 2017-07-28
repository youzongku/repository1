var layer, layerpage, grid;
function warehouse(lay, laypage, jqgrid) {
    layer = lay;
    layerpage = laypage;
    grid = jqgrid;
    ajax_get(
        "/inventory/queryWarehouse", undefined, "",
        function (data) {
            $("#selected_warehouse").empty();
            var opHtml = '<option value="">所有仓库</option>';
            $.each(data, function (i, item) {
                opHtml += '<option value="' + item.warehouseId + '">' + item.warehouseName + '</option>';
            });
            $("#selected_warehouse").append(opHtml);
        }
        , function (e) {
            console.log(e);
        }
    );
    $("#_quoted_").keydown(function (event) {
        var key = event.keyCode || event.which;
        if (key == 13) {
            reloadQuotedTable();
        }
    });
    $("#chooice_contract_input").keydown(function (event) {
        var key = event.keyCode || event.which;
        if (key == 13) {
            reloadContractChoicePop();
        }
    });
    $("#chooice_contract_input").click(function (event) {
        var key = event.keyCode || event.which;
        if (key == 13) {
            reloadContractChoicePop();
        }
    });

}
function reloadQuotedTable() {
    var param = {
        search: trimValue($("#_quoted_").val()),
        start: $("#_start_").val(),
        end: $("#_end_").val(),
        warehouseId: $("#selected_warehouse").val(),
        status: $("#selected_status").val()
    };
    $("#contract_bid_table").jqGrid('setGridParam', {page: 1, postData: param}).trigger('reloadGrid');
}

/*报价管理 start*/
function contractBid() {
    var setting = {
        url: "/product/quoted/get",
        datatype: "json", // 返回的数据类型
        ajaxGridOptions: {contentType: 'application/json; charset=utf-8'},
        mtype: "post", // 提交方式
        height: "auto", // 表格宽度
        autowidth: true, // 是否自动调整宽度
        title: "合同文件管理",
        colNames: ['ID', 'distributionMode','分销商账号','名称','业务员','SKU', '仓库', '报价（元）', '有效期限', '关联合同', '状态', '操作'],
        colModel: [
            {name: 'id', index: '', width: '10%', align: 'center', sortable: false, hidedlg: true, hidden: true},
            {name: 'distributionMode', index: '', width: '10%', align: 'center', sortable: false, hidedlg: true, hidden: true},
            {name: 'account', index: '', width: '10%', align: 'center', sortable: false,formatter: function (cellvalue, options, rowObject) {
                return deal_with_illegal_value(cellvalue);
            }},
            {name: 'distributionName', index: '', width: '10%', align: 'center', sortable: false,formatter: function (cellvalue, options, rowObject) {
                return deal_with_illegal_value(cellvalue);
            }},
            {name: 'bussinessErp', index: '', width: '10%', align: 'center', sortable: false,formatter: function (cellvalue, options, rowObject) {
                return deal_with_illegal_value(cellvalue);
            }},
            {name: 'sku', index: '', width: '10%', align: 'center', sortable: false},
            {name: 'warehouseName', index: '', width: '10%', align: 'center', sortable: false},
            {name: 'contractPrice', index: 'q.contract_price', width: '10%', align: 'center', sortable: true,formatter: function (cellvalue, options, rowObject) {
                if(rowObject.distributionMode == '3') {
                    return cellvalue.toFixed(4);
                } else {
                    return cellvalue.toFixed(2);
                }
            }},
            {
                name: 'contractStart', index: 'q.contract_end', width: '25%', align: "center", sortable: true,
                formatter: function (cellvalue, options, rowObject) {
                    return rowObject.contractStart + " 至 " + rowObject.contractEnd;
                }
            },
            {name: 'contractNo', index: 'q.contract_no', width: '25%', align: "center", sortable: true},
            {
                name: 'status', index: '', width: '10%', align: "center", sortable: false,
                formatter: function (cellvalue, options, rowObject) {
                    return statusStr(cellvalue);
                }
            },
            {
                name: 'operation', index: '', width: '25%', align: "center", sortable: false,
                formatter: function (cellvalue, options, rowObject) {
                    return fomatterOperate(cellvalue, options, rowObject);
                }
            }
        ],
        viewrecords: true,//是否在浏览导航栏显示记录总数
        rowNum: 15,//每页显示记录数
        loadtext: "加载中...",
        pager:"#contract_bid_pagination",//分页
        rownumbers: true, // 显示行号
        pagerpos : "center",
        pgbuttons : true,
        pgtext : "当前页 {0} 一共{1}页",
        rowList: [15, 20, 25],//用于改变显示行数的下拉列表框的元素数组。
        serializeGridData: function (postData) {
            return JSON.stringify(postData);
        },
        jsonReader: {
            root: "data.result",  //数据模型
            page: "data.currPage",//数据页码
            total: "data.totalPage",//数据总页码
            records: "data.rows",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        }
    };
    return setting;
}

function fomatterOperate(cellvalue, options, rowObject){
    var htmlCode = "<div class='operation-a'>";
    var status = rowObject.status;
    if(status == 1) {
        htmlCode += "<a onclick='setUp(" + options.rowId + "," + rowObject.warehouseId + "," + rowObject.status + ");'>设置</a>";
        htmlCode += "<a onclick='remove(" + rowObject.id + ");'>删除</a>";
    } else if(status == 2) {
        htmlCode += "<a onclick='setUp(" + options.rowId + "," + rowObject.warehouseId + "," + rowObject.status + ");'>设置</a>";
    }
    htmlCode += "<a onclick='operationQuotedLog(" + rowObject.id + ");'>操作日志</a></div>";
    return htmlCode;
}
function statusStr(status) {
    switch (status) {
        case 1 :
            return "未开始";
        case 2 :
            return "已开始";
        case 3 :
            return "已结束";
    }
}

function addBid() {
    grid.initTable($("#add_bid_table"), addBidCon());
    $("#add_bid_table").clearGridData();
    $("#disPrice").html("0.00");
    $("#contractBid").hide();
    $("#addBid").show();
}
function returnButton() {
    $("#addBid").hide();
    $("#contractBid").show();
}

function saveButton() {
    var options = {
        dataType: 'json',           //html(默认), xml, script, json...接受服务端返回的类型
        clearForm: true,          //成功提交后，清除所有表单元素的值
        resetForm: false,          //成功提交后，重置所有表单元素的值
        timeout: 3000,               //限制请求的时间，当请求大于3秒后，跳出请求
        //提交前的回调函数
        beforeSubmit: function (formData, jqForm, options) {
            var vali = validateQuoted(formData);
            if (!vali.result) {
                layer.msg(vali.msg, {icon: 5, time: 3000});
                return false;
            }
        },
        //提交成功后的回调函数
        success: function (data, status, xhr, $form) {
            if (data.suc) {
                $("#quoted_create_form").resetForm();
                layer.msg('添加报价成功！', {icon: 6, time: 2000});
                reloadQuotedTable()
                $("#addBid").hide();
                $("#contractBid").show();
            } else {
                layer.msg(data.msg, {icon: 5, time: 3000});
                $("#add_bid_table").clearGridData();
            }
        },
    };
    $("#quoted_create_form").ajaxSubmit(options);
}
function validateQuoted(form) {
    var obj = {};
    var flag = true;
    $.each(form, function (i, item) {
        if(!item.value) {
            obj.result = false;
            flag = false;
            obj.msg = "请检查必填参数是否为空。";
            return false;
        }


        if(item.name == 'contractPrice') {
            var model = $("input[name='model']").val();
            var price = item.value;
            if(model == '3') {
                if(!checkPriceWithKa(price) || !parseFloat(price)){
                    obj.result = false;
                    flag = false;
                    obj.msg = "商品报价格式错误(至多四位小数)";
                    return false;
                }
            } else {
                if(!checkPrice(price) || !parseFloat(price)){
                    obj.result = false;
                    flag = false;
                    obj.msg = "商品报价格式错误(至多两位小数)";
                    return false;
                }
            }
        }
    });
    if(!flag) {
        return obj;
    }
    return {result: true};
}

//添加报价
function inputContractPrice(obj) {
    var price = $.trim($(obj).val());
    console.log($("input[name='model']").val());
    if($("input[name='model']").val() && $("input[name='model']").val() != ''){
        if($("input[name='model']").val() == '3') {
            if(!checkPriceWithKa(price) || !parseFloat(price)){
                layer.msg('商品报价格式错误(至多四位小数)', {icon: 5, time: 3000});
                return false;
            }
        } else {
            if(!checkPrice(price) || !parseFloat(price)){
                layer.msg('商品报价格式错误(至多两位小数)', {icon: 5, time: 3000});
                return false;
            }
        }
    }
}

//设置报价
function inputContractPrice2(obj) {
    var price = $.trim($(obj).val());
    var tag = $(obj).attr('tag');
    if(tag == '3'){
        if(!checkPriceWithKa(price) || !parseFloat(price)){
            layer.msg('商品报价格式错误(至多四位小数)', {icon: 5, time: 3000});
            return false;
        }
    } else{
        if(!checkPrice(price) || !parseFloat(price)){
            layer.msg('商品报价格式错误(至多两位小数)', {icon: 5, time: 3000});
            return false;
        }
    }
}

function checkPrice (price) {
    var regMoney = /^(0|[1-9][0-9]*)(.[0-9]{1,2})?$/;
    return regMoney.test(price);
}

function checkPriceWithKa (price) {
    var regMoney = /^(0|[1-9][0-9]*)(.[0-9]{1,4})?$/;
    return regMoney.test(price);
}
/*报价管理 end*/

/*添加报价 start*/
function addBidCon() {
    var setting = {
        datatype: "local",
        height: "auto", // 表格宽度
        width: 1027,
        colNames: ['分销渠道', '分销商账号', '名称', '分销商手机', '业务员工号'],
        colModel: [
            {name: 'distributionMode', index: '', width: '20%', align: 'center', sortable: false},
            {name: 'account', index: '', width: '20%', align: 'center', sortable: false},
            {name: 'distributionName', index: '', width: '20%', align: "center", sortable: false},
            {name: 'phone', index: '', width: '20%', align: "center", sortable: false},
            {name: 'bussinessErp', index: '', width: '20%', align: 'center', sortable: false}
        ],
        viewrecords: true,//是否在浏览导航栏显示记录总数
        rowNum: 15,//每页显示记录数
        rowList: [15, 20, 25]//用于改变显示行数的下拉列表框的元素数组。
    };
    return setting;
}
/*添加报价 end*/
/*弹框 start*/
function setUp(rowId, warehouseId, status) {
    var rowObject = $("#contract_bid_table").jqGrid('getRowData', rowId);
    $("#price_div").hide();
    layer.open({
        type: 1,
        title: "设置",
        content: $("#setUp"),
        area: ['540px', '460px'],
        btn: ["确定", "取消"],
        shadeClose: false,
        success: function () {
            $("#update_price").val(rowObject.contractPrice);
            $("#update_price").attr('tag', rowObject.distributionMode);
            if (status == 1) {
                $("#price_div").show();
            }
            var d = rowObject.contractStart.split(" 至 ");
            $("#update_start").val(d[0]);
            $("#update_end").val(d[1]);
            showHistory(rowObject, warehouseId);
        },
        yes: function (index) {
            var p = $("#update_price").val();
            if(rowObject.distributionMode == 3) {
                if(!checkPriceWithKa(p) || !parseFloat(p)){
                    layer.msg("商品报价格式错误(至多四位小数)", {icon: 2, time: 2000});
                    return;
                }
            } else {
                if(!checkPrice(p) || !parseFloat(p)){
                    layer.msg("商品报价格式错误(至多两位小数)", {icon: 2, time: 2000});
                    return;
                }
            }


            if(status == 1 && !p) {
                layer.msg("商品订单不能为空", {icon: 1, time: 2000});
                return;
            }
            var start = $("#update_start").val();
            if(!start) {
                layer.msg("合同期限开始时间不能为空", {icon: 1, time: 2000});
                return;
            }
            var end = $("#update_end").val();
            if(!end) {
                layer.msg("合同期限结束时间不能为空", {icon: 1, time: 2000});
                return;
            }
            var param = {
                qid: rowObject.id,
                contractPrice: p,
                start: start,
                end: end
            };

            submitUpdate(param, index);
        }
    });
}
function submitUpdate(param, index) {
    $.ajax({
        url: "/product/quoted/update",
        type: 'POST',
        data: JSON.stringify(param),
        contentType: "application/json",
        async: true,//是否异步
        dataType: 'json',
        success: function (response) {
            layer.msg(response.msg, {icon: 1, time: 2000});
            if (response.suc) {
                layer.close(index);
                reloadQuotedTable();
            }
        }
    });
}
function showHistory(rowObject, warehouseId) {
    $.ajax({
        url: "/product/quoted/get",
        type: 'POST',
        data: JSON.stringify({
            sku: rowObject.sku,
            contractNo: rowObject.contractNo,
            warehouseId: warehouseId,
            sidx: "q.contract_end",
            sord: "asc"
        }),
        contentType: "application/json",
        async: true,//是否异步
        dataType: 'json',
        success: function (response) {
            $("#setUp .set-pop-con").remove();
            var list = response.data.result;
            if (list) {
                var htmlCode = "";
                $.each(list, function (i, item) {
                    var price = item.contractPrice;
                    if(rowObject.distributionMode == '3') {
                        price = price.toFixed(4);
                    } else {
                        price = price.toFixed(2);
                    }

                    htmlCode += '<div class="set-pop-con">' +
                        '<div class="set-pop-span">' +
                        '<span>SKU：<em>' + item.sku + '</em></span>' +
                        '<span>供货价：<em>' + item.purchasePrice + '</em>元</span>' +
                        '<span>报价：<em>' + price + '</em>元</span>' +
                        '</div>' +
                        '<div>' +
                        '<span>合同编号：<em>' + item.contractNo + '</em></span>' +
                        '<span>有效期限：<em>' + item.contractStart + '</em>至<em>' + item.contractEnd + '</em></span>' +
                        '</div>' +
                        '</div>';
                });
                $("#setUp").prepend(htmlCode);
            }
        }
    });
}
function operationQuotedLog(qid) {
    layer.open({
        type: 1,
        title: "操作日志",
        content: $("#operationQuotedLog"),
        area: ['540px', '460px'],
        btn: ["关闭"],
        shadeClose: false,
        success: function () {
            showRecord(qid);
        }
    })
}
function showRecord(qid) {
    $("#operationQuotedLog").empty();
    ajax_get("/product/quoted/oprecord?qid=" + qid, "", "application/json",
        function (datad) {
            var htmlCode = "";
            if (datad && datad.length > 0) {
                $.each(datad, function (i, item) {
                    htmlCode += '<div class="log-pop-con">' +
                        '<span>' + item.opdateStr + '</span>' +
                        '<span>' + item.opuser + ':' + item.comment + '</span>' +
                        '</div>';
                });
            } else {
                htmlCode += '<span>暂无历史操作记录</span>';
            }
            $("#operationQuotedLog").prepend(htmlCode);
        },
        function (xhr, status) {
        }
    );
}
function remove(qid) {
    layer.confirm("您确定要删除吗？", {icon: 3},
        function (i, currdom) {
            layer.close(i);
            ajax_get("/product/quoted/del?qid=" + qid, "", "application/json",
                function (datad) {
                    layer.msg(datad.msg, {icon: 1, time: 2000});
                    if (datad.suc) {
                        reloadQuotedTable();
                    }
                },
                function (xhr, status) {
                }
            );
        }
    );
}
function choose_contract() {
    layer.open({
        type: 1,
        title: "选择合同",
        area: ['660px', '540px'],
        content: $("#disturb_choice_pop_div"),
        btn: ["确定", "取消"],
        success: function () {
            grid.initTable($("#contract_choice_pop"), getContractChoicePopSetting());
        }
    });
}
function reloadContractChoicePop() {
    var param = {
        search: trimValue($("#chooice_contract_input").val())
    };
    $("#contract_choice_pop").jqGrid('setGridParam', {page: 1, postData: param}).trigger('reloadGrid');
}

function trimValue(obj){
    return obj ? obj.trim() : "";
}

function getContractChoicePopSetting() {
    var setting = {
        url: "/product/contract/get",
        datatype: "json", // 返回的数据类型
        ajaxGridOptions: {contentType: 'application/json; charset=utf-8'},
        mtype: "post", // 提交方式
        height: "auto", // 表格宽度
        autowidth: true, // 是否自动调整宽度
        caption: "合同列表（双击选择）",
        colNames: ['ID', 'MODEL', '合同编码', '分销渠道', '分销商账号', '名称', '分销商手机', '合同期限', '业务员工号'],
        colModel: [
            {name: 'id', index: '', width: '10%', align: 'center', sortable: false, hidedlg: true, hidden: true},
            {name: 'model', index: '', width: '10%', align: 'center', sortable: false, hidedlg: true, hidden: true},
            {name: 'contractNo', index: 'contract_no', width: '10%', align: 'center', sortable: true},
            {name: 'distributionMode', index: '', width: '12%', align: 'center', sortable: false},
            {name: 'account', index: '', width: '12%', align: 'center', sortable: false},
            {name: 'distributionName', index: '', width: '12%', align: "center", sortable: false},
            {name: 'phone', index: '', width: '12%', align: "center", sortable: false},
            {
                name: 'contractStart', index: 'contract_end', width: '14%', align: 'center', sortable: true,
                formatter: function (cellvalue, options, rowObject) {
                    return rowObject.contractStart + " 至 " + rowObject.contractEnd;
                }
            },
            {name: 'bussinessErp', index: '', width: '12%', align: 'center', sortable: false}
        ],
        viewrecords: true,//是否在浏览导航栏显示记录总数
        rowNum: 15,//每页显示记录数
        loadtext: "加载中...",
        pager: "#contract_choice_page",
        pgbuttons : true,
        pgtext : "当前页 {0} 一共{1}页",
        pagerpos : "center",
        rowList: [15, 20, 25],//用于改变显示行数的下拉列表框的元素数组。
        serializeGridData: function (postData) {
            return JSON.stringify(postData);
        },
        ondblClickRow: function (rowid) {
            contractSelected(rowid);
        },
        jsonReader: {
            root: "data.result",  //数据模型
            page: "data.currPage",//数据页码
            total: "data.totalPage",//数据总页码
            records: "data.rows",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        }
    };
    return setting;
}
function contractSelected(rowId) {
    var rowObject = $("#contract_choice_pop").jqGrid('getRowData', rowId);
    $("#add_bid_table").clearGridData();
    $("#add_bid_table").jqGrid("addRowData", 1, rowObject);
    var d = rowObject.contractStart.split(" 至 ");
    $("#quoted_create_form").find("input[name=start]").val(d[0]);
    $("#quoted_start_date").val(d[0]);
    $("#quoted_end_date").val(d[1]);
    $("#quoted_create_form").find("input[name=end]").val(d[1]);
    $("#quoted_create_form").find("input[name=contract_no]").val(rowObject.contractNo);
    $("#quoted_create_form").find("input[name=cid]").val(rowObject.id);
    $("#quoted_create_form").find("input[name=model]").val(rowObject.model);
    var sku = $("#quoted_create_form").find("input[name=sku]").val();
    if (sku) {
        check_product();
    }
    layer.closeAll();
}

function loadDate(element) {
    laydate({
        elem: "#"+$(element).attr("id"), //需显示日期的元素选择器
        format: 'YYYY-MM-DD', //日期格式
        istime: true, //是否开启时间选择
        isclear: true, //是否显示清空
        istoday: true, //是否显示今天
        issure: true, //是否显示确认
        festival: true, //是否显示节日
        min: $("#quoted_start_date").val(), //最小日期
        max: $("#quoted_end_date").val(), //最大日期
        //start: '2014-6-15 23:00:00',    //开始日期
        fixed: false
    });
}
function check_product() {
    var sku = $("#quoted_create_form").find("input[name=sku]").val();
    if (!sku) {
        layer.msg("请输入SKU编号", {icon: 1, time: 2000});
        return;
    }
    var param = {
        "data": {
            "skuList": [sku],
            "model": $("#quoted_create_form").find("input[name=model]").val()
        }
    };
    $.ajax({
        url: "/product/api/getProducts",
        type: 'POST',
        data: JSON.stringify(param),
        contentType: "application/json",
        async: true,//是否异步
        dataType: 'json',
        success: function (data) {
            if (data.data.result.length > 0) {
                $("#disPrice").html(data.data.result[0].disPrice);
                $("#quoted_create_form").find("input[name=warehouseId]").val(data.data.result[0].warehouseId);
            } else {
                layer.msg("该商品不存在。", {icon: 5, time: 2000});
                return;
            }
        }
    });
}
function add_normalProduct_div() {
    layer.open({
        type: 1,
        title: "选择商品",
        area: ['1000px', '540px'],
        content: $("#add_normalProduct_div"),
        btn: ["确定", "取消"]
    });
}
/*弹框 end*/