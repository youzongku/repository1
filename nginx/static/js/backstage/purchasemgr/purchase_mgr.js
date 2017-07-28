var laypage = undefined;
var layer = undefined;
var p_import = undefined;
var prov_map,city_map,region_map;
var globalPurchaseOrderMap = {}

function init_purchase_order_category_title(categoryTitle) {
    $('#purchase_order_title').html(categoryTitle);
}

function init_input_purchase_order_category_title(categoryTitle) {
    $('#input_purchase_order_category_title').html(categoryTitle);
}

function init_(lay, layd,imp,categoryTitle) {
    laypage = layd;
    layer = lay;
    p_import = imp;
    //订单状态
    $("#statusSelected").change(function () {
        $("#purchase_pagination").empty();
        getPurchaseOrderByConditions()
    });
    //赠品状态
    $("#priStatus").change(function () {
        getPurchaseOrderByConditions()
    });
}

function getSetting_purchaseorder() {
    globalPurchaseOrderMap={}
    var setting = {
        url:"/purchase/viewpurchasenew",
        rownumbers : true, // 是否显示前面的行号
        datatype : "json", // 返回的数据类型
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        colModel:[
            {label:"订单编号",name:"purchaseOrderNo",index:"purchaseOrderNo",align:"center",sortable:false},
            {label:"订单状态",name:"statusMes",index:"statusMes",align:"center",sortable:false},
            {label:"下单时间",name:"sorderDate",index:"purchaseDate",align:"center",sortable:true},
            {label:"支付/取消时间",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                return (rowObject.payDate ? rowObject.spayDate : (rowObject.cancelDate ? rowObject.cancelDateStr : "--"))
            }},
            {label:"订单金额(元)",name:"purchaseTotalAmount",index:"purchaseTotalAmount",align:"center",sortable:false},
            {label:"折后金额(元)",name:"purchaseDiscountAmount",index:"purchaseDiscountAmount",align:"center",sortable:false},
            {label:"是否已选赠品",name:"status",index:"status",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                return (rowObject.isPro == true ? (rowObject.isChoose == true ? "已选" : "待选") : "--")
            }},
            {label:"客户实付(元)",name:"offlineMoney",index:"offlineMoney",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                return cellvalue ? cellvalue : 0
            }},
            {label:"财务实收(元)",name:"financeMoney",index:"financeMoney",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                return cellvalue ? cellvalue : 0
            }},
            {label:"名称",name:"nickName",index:"nickName",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"下单分销商",name:"email",index:"email",align:"center",sortable:false},
            {label:"业务员",name:"customerService",index:"customerService",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"录入人",name:"inputUser",index:"inputUser",align:"center",sortable:false},
            {label:"录单备注",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(rowObject.remarks ? rowObject.remarks : rowObject.busenessRemarks)
            }},
            {label:"操作",sortable:false,align:"center", formatter:function(cellvalue, options, rowObject){
                globalPurchaseOrderMap[rowObject.id] = rowObject
                var opt = (rowObject.isPro == true && rowObject.isChoose == true ? "<a href='javascript:;' onclick='operationlog(this)' tag='" + rowObject.id + "'>操作日志</a>" : "--")
                return opt
            }}
        ],
        viewrecords : true,
        onPaging:function(pageBtn){
            // 清空缓存的数据
            globalPurchaseOrderMap = {}
        },
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#purchase_pagination",//分页
        pagerpos : "center",
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        caption:"采购单（双击行，可查看采购单详情）",//表名称,
        jsonReader:{
            root: "orders",  //数据模型
            page: "page",//数据页码
            total: "pages",//数据总页码
            records: "total",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        },
        ondblClickRow: function(rowid) {
            doShowPurchaseOrderDetail(rowid);
        }
    }

    return setting;
}

function getPurchaseOrderByConditions(){
    // 拿到原有的，清除掉
    var postData = $("#purchase_orders_table").jqGrid("getGridParam", "postData");
    $.each(postData, function (k, v) {
        delete postData[k];
    });

    var priStatus = $("#priStatus").val()
    var params = {
        status: $("#statusSelected").val(), priStatus: priStatus,
        seachFlag: $.trim($("#searchInput").val()),
        sorderDate: $("#seachTime0").val(), eorderDate: $("#seachTime1").val(),
        spaydate: $("#seachTime2").val(), epaydate: $("#seachTime3").val()
    };
    if (priStatus == 1) {//有赠品的订单
        params.isPro = true;
    } else if (priStatus == 2) {//待选
        params.isPro = true;
        params.isChoose = false;
    } else if (priStatus == 3) {//已选
        params.isPro = true;
        params.isChoose = true;
    }
    $("#purchase_orders_table").jqGrid('setGridParam', {page:1, postData: params}).trigger("reloadGrid");
}

function doShowPurchaseOrderDetail(id){
    var rowObject = globalPurchaseOrderMap[id]
    var csAuditLogs = rowObject.csAuditLogs
    var financeAuditLogs = rowObject.financeAuditLogs
    var details = rowObject.details;
    // OA审批单号
    $("#purchaseorder_detail_div").find("span[name='oaAuditNoSpan']").html("OA审批单号："+deal_with_illegal_value(rowObject.oaAuditNo))
    // 录单备注信息
    var remarksHtml = '<div><span>备注人：'+rowObject.inputUser+'</span><em>备注时间：'+rowObject.sorderDate+'</em></div>' +
        '<div>备注：'+deal_with_illegal_value(rowObject.remarks ? rowObject.remarks : rowObject.busenessRemarks)+'</div>'
    $("#purchaseorder_detail_div").find("div[name='remarksInfoDiv']").html(remarksHtml)
    // 客服备注信息
    var cs_auditUser='', cs_auditDate='', cs_remark='';
    if(csAuditLogs && csAuditLogs.length>0) {
        cs_auditUser = deal_with_illegal_value(csAuditLogs[0].auditUser);
        cs_auditDate = deal_with_illegal_value(csAuditLogs[0].auditDateStr);
        var jsonValue = eval("(" + csAuditLogs[0].jsonValue + ")");
        cs_remark = deal_with_illegal_value(jsonValue.remark)
    }
    var csRemarksHtml = '<div><span>备注人：'+deal_with_illegal_value(cs_auditUser)+'</span>' +
    '<em>备注时间：'+deal_with_illegal_value(cs_auditDate)+'</em></div>' +
        '<div>备注：'+deal_with_illegal_value(cs_remark)+'</div>'
    $("#purchaseorder_detail_div").find("div[name='csRemarksInfoDiv']").html(csRemarksHtml)
    // 财务备注信息
    var finance_auditUser='', finance_auditDate='', finance_remark='';
    if(financeAuditLogs && financeAuditLogs.length>0){
       finance_auditUser=deal_with_illegal_value(financeAuditLogs[0].auditUser);
       finance_auditDate=deal_with_illegal_value(financeAuditLogs[0].auditDateStr);
       var jsonValue = eval("(" + financeAuditLogs[0].jsonValue + ")");
       if(jsonValue.paymentRemark) {
           finance_remark = jsonValue.paymentRemark
       }
       if(jsonValue.profitRemark) {
           if(finance_remark) {
               finance_remark += '<br/>'
           }
           finance_remark+=jsonValue.profitRemark
       }
    }
    var financeRemarksHtml = '<div><span>备注人：'+deal_with_illegal_value(finance_auditUser)+'</span>' +
        '<em>备注时间：'+deal_with_illegal_value(finance_auditDate)+'</em></div>' +
        '<div>备注：'+deal_with_illegal_value(finance_remark)+'</div>'
    $("#purchaseorder_detail_div").find("div[name='financeRemarksInfoDiv']").html(financeRemarksHtml)
    // 商品信息
    var productInfoHtml = ''
    var totalQty = 0;
    for (var j in details) {
       var capFee = (details[j].capFee != null?details[j].capFee:details[j].purchasePrice);
       totalQty += details[j].qty;
       productInfoHtml += '<tr>' +
           '<td>'+ details[j].sku +'</td>' +
           '<td>'+ details[j].productName + (details[j].isgift?"<em class='red'>【赠】</em>":"")+'</td>' +
           '<td>'+ deal_with_illegal_value(details[j].expirationDate) +'</td>' +
           '<td>'+ deal_with_illegal_value(details[j].interBarCode) +'</td>' +
           '<td>'+ details[j].warehouseName +'</td>' +
           '<td>'+ (details[j].isgift ? (details[j].realPrice != null ? details[j].realPrice.toFixed(4) : details[j].purchasePrice.toFixed(4)) : details[j].purchasePrice.toFixed(4)) +'</td>' +
           '<td>'+ capFee.toFixed(2) +'</td>' +
           '<td>'+ details[j].qty +'</td>' +
           '<td>'+ (capFee*details[j].qty).toFixed(2) +'</td>' +
           '</tr>'
    }
    $("#purchaseorder_detail_div").find("tbody[name='productInfoTbody']").html(productInfoHtml)

    var bbcPostage = rowObject.bbcPostage;
    var totalPrices  = rowObject.purchaseTotalAmount + (bbcPostage?bbcPostage:0);
    var couponsCode =  rowObject.couponsCode;
    var couponsAmount = rowObject.couponsAmount;
    var actually_paid = totalPrices;

    var sumInfoHtml = '<span>商品总数'+totalQty+'个</span>&nbsp;&nbsp;&nbsp;' +
       '<span>商品总计'+(rowObject.orderProTotal?parseFloat(rowObject.orderProTotal).toFixed(2):0.00)+'元</span>';
    if(rowObject.reducePrice){
        sumInfoHtml +=
            '-&nbsp;&nbsp;<span>' + (rowObject.purchaseType == 1?"活动优惠":"整单优惠") +
            parseFloat(rowObject.reducePrice).toFixed(2)+'元</span>';
    }
    if(couponsCode){
        sumInfoHtml += '-&nbsp;&nbsp;<span>优惠金额'+parseFloat(couponsAmount).toFixed(2)+'元</span>';
        actually_paid -= couponsAmount;
    }
    if(bbcPostage){
        sumInfoHtml += '+&nbsp;&nbsp;<span>运费'+parseFloat(bbcPostage).toFixed(2)+'元</span>';
    }
    if(actually_paid <= 0){
       actually_paid = 0.00;
    }
    sumInfoHtml +=
       '&nbsp;&nbsp;=&nbsp;&nbsp;订单金额<span>' + parseFloat(actually_paid).toFixed(2) + '元</span>';
    $("#purchaseorder_detail_div").find("p[name='sumInfoP']").html(sumInfoHtml)

    layer.open({
        type: 1,
        title: rowObject.purchaseOrderNo,
        content: $("#purchaseorder_detail_div"),
        area: ['1100px', '510px'],
        shadeClose: true, //点击遮罩关闭
        scrollbar: false,
        move: false,
        btn: ['关闭'],
        yes:function(index){
            layer.close(index)
        }
    });
}

//详情展示
function showSymbol(param, e) {
    if (param == 1) {
        $(e).css("display", "none");
        $(e).next().css("display", "inline-block");
        $(e).parent().parent().next().css("display", "table-row");
    } else {
        $(e).css("display", "none");
        $(e).prev().css("display", "inline-block");
        $(e).parent().parent().next().css("display", "none");
    }
}

//操作日志
function changeradio(obj, change) {
    $(".change-discount input[type=text]").val("");
    if (change == 'Uchange') {
        $(".change-money").show().css('display', 'inline-block');
        $(".dis-money").hide();
    } else if (change == 'Uclick') {
        $(".change-money").hide();
        $(".dis-money").show().css('display', 'inline-block');
    }
}

function clear() {
    $(".order-discount-m").text("");
    $(".order-discount-m").next().find("span").text("");
    $("span[tag=discountPrice]").text("");
    $("span[tag=costPrice]").text("");
    $(".warehouse-toppop-box input[name=orderId]").val("");
    $(".warehouse-toppop-box input[name=emial]").val("");
}

//操作日志
function operationlog(obj) {
    clickThisTr($(obj).parent().parent())
    var goal = $(".operation-log-pop-box");
    $(".operation-log-list p").remove();
    $(".operation-log-list span").remove();
    layer.open({
        type: 1,
        title: '操作日志',
        btn: false,
        shadeClose: true,
        content: goal,
        area: ['450px', '300px'],
        move: false,
        success: function () {
            goal.show();
        },
        end: function () {
        }
    });
    initRecord(obj);
}

function initRecord(obj) {
    var orderId = $(obj).attr("tag");
    if (orderId) {
        $.ajax({
            url: "../purchase/showGiftOperation?purchaseId=" + orderId,
            type: "get",
            dataType: "json",
            async: false,
            success: function (res) {
                var data = JSON.parse(res);
                if (data.suc) {
                    var htmlCode = "";
                    if (data.data.length > 0) {
                        $.each(data.data, function (i, item) {
                            htmlCode += "<p>" +
                                "<span>" + item.operateTimeStr + "</span>" +
                                "<span>" + item.operatorEmail + "</span>" +
                                "<span>添加赠品" + item.sku + "</span>" +
                                "<span style='width: 72px;'>数量" + item.qty + "</span>" +
                                "</p>";
                        });
                    } else {
                        htmlCode = "<span style='text-align: center;line-height: 190px '>暂无操作记录！</span>";
                    }
                    $(".operation-log-list").prepend(htmlCode);
                }
            }
        });
    }
}

var changeTwoDecimal_f
$(document).ready(function () {
    //小数点取整
    changeTwoDecimal_f = function (x) {
        var f_x = parseFloat(x);
        if (isNaN(f_x)) {
            return false;
        }
        f_x = Math.round(f_x * 100) / 100;
        var s_x = f_x.toString();
        var pos_decimal = s_x.indexOf('.');
        if (pos_decimal < 0) {
            pos_decimal = s_x.length;
            s_x += '.';
        }
        while (s_x.length <= pos_decimal + 2) {
            s_x += '0';
        }
        return s_x;
    }
});

// 线下转账
function transferOffline(purchaseOrderNo){
    $("#pno").val(purchaseOrderNo)
    // 采购进货单号
    var transferOffline_params = {pageSize: "10", pageCount: "0", seachFlag: purchaseOrderNo}
    ajax_post("../purchase/viewpurchase", JSON.stringify(transferOffline_params), "application/json", function(response) {
        if (response.returnMess.errorCode == "0") {
            var order = response.orders[0];
            var bbcPostage = order.bbcPostage;
            var couponsAcount = order.couponsAmount;

            // 加运费减金额
            var totalAmount = parseFloat(order.purchaseDiscountAmount + (bbcPostage ? bbcPostage : 0)).toFixed(2);
            if (couponsAcount) {
                if (totalAmount > couponsAcount) {
                    totalAmount -= couponsAcount;
                } else {
                    totalAmount = 0;
                }
            }
            totalAmount = parseFloat(totalAmount).toFixed(2);

            $("#audit_form input[name='transferAmount']").data("fee", totalAmount);

            $("#email_hidden").val(order.email)
            var html = '<td>'+order.purchaseOrderNo+'</td>'+
                '<td>'+order.email+'</td>'+
                '<td>'+order.purchaseDiscountAmount+'</td>'+
                '<td>'+deal_with_illegal_value(bbcPostage)+'</td>'+
                '<td>'+totalAmount+'</td>'
            $("#transfer_offline_div tbody").html(html)
        }
    })

    // 初始数据
    gain_receipt_mode();

    //收款方式下拉选
    $("select[name='recipientId']").change(function (event) {
        if (this.value != "") {
            var opt = undefined, mode = this.value;
            $.each($(this).children(), function (i, item) {
                if (mode == item.value) opt = item;
            });
            $("#receiptAccount").text($(opt).data("mode").split("——")[0]);
            $("#receipt_payee").text($(opt).data("mode").split("——")[1]);
            $("input[name=receiptAccount]").val($(opt).data("mode").split("——")[0]);
            $("input[name=receiptName]").val($(opt).data("mode").split("——")[1]);
        } else {
            $("#receiptAccount").text("");
            $("#receipt_payee").text("");
            $("input[name=receiptAccount]").val("");
        }
    });
}

// 余额支付
function payWithBalance(purchaseOrderNo){
    // 采购单余额支付
    ajax_post("/purchase/balancePaymentBackStage",JSON.stringify({purchaseOrderNo : purchaseOrderNo}),"application/json",function (data) {
        if(!data.suc){
            layer.msg(data.msg, {icon : 5, time : 3000});
            return;
        }
        layer.msg("余额支付成功，即将跳转到列表页！", {icon : 1, time : 3000},function(){
            $("p[position="+$("#forword").val()+"]").click();
        });
    })
}

// tab切换
$('body').on("click",".record_purchaseList_tab li",function () {
    $(this).addClass("recharge_current").siblings().removeClass();
    var index = $(".record_purchaseList_tab li").index(this);
    $(".record_purchaseList_content > div").hide().eq(index).show();
    if(index){
        p_import.view_list();
    }else{
        disabled($("#import_generator"),false);
    }
});

function getProvinces(){
    $(".microCapsule").find("select[name='sale-province']").empty();
    $(".microCapsule").find("select[name='sale-city']").empty();
    $(".microCapsule").find("select[name='sale-region']").empty();
    ajax_get("/member/getprovs", JSON.stringify(""), "application/json",
        function (data) {
            prov_map = {};
            var html = "<option value='' >请选择省</option>";
            for (var i = 0; i < data.length; i++) {
                html += "<option value='" + data[i].id + "' >" + data[i].provinceName + "</option>";
                prov_map[data[i].provinceName.substr(0,data[i].provinceName.length-1)] = data[i].id;
            }
            $(".microCapsule").find("select[name='sale-province']").html(html);
        }, function (xhr, status) {
        }
    );
}

function getCities(){
    var proId = $(".microCapsule").find("select[name='sale-province']").val();
    if(!proId){
        return;
    }
    // 计算运费
    getFreight4AProvince()

    $(".microCapsule").find("select[name='sale-city']").empty();
    ajax_get("/member/getcities", "proId=" + proId, "",
        function (data) {
            city_map = {};
            var html = "<option value=''>请选择市</option>"
            for (var i = 0; i < data.cities.length; i++) {
                html += "<option value='" + data.cities[i].id + "' code='"+data.cities[i].zipCode+"' >" + data.cities[i].cityName + "</option>"
                city_map[data.cities[i].cityName.substr(0,data.cities[i].cityName.length-1)] = data.cities[i].id;
            }
            $(".microCapsule").find("select[name='sale-city']").html(html)
        }, function (xhr, status) {
        }
    );
}

function getAreas(){
    var cityId = $(".microCapsule").find("select[name='sale-city']").val()
    if(!cityId)
        return;

    $(".microCapsule").find("select[name='sale-region']").empty()
    ajax_get("/member/getareas", "cityId=" + cityId, "",
        function (data) {
            region_map = {};
            var html = "<option value=''>请选择区</option>"
            for (var i = 0; i < data.areas.length; i++) {
                html += "<option value='" + data.areas[i].id + "' >" + data.areas[i].areaName + "</option>"
                region_map[data.areas[i].areaName.substr(0,data.areas[i].areaName.length-1)] = data.areas[i].id;
            }
            $(".microCapsule").find("select[name='sale-region']").html(html)
        }, function (xhr, status) {
        }
    );
}

// 获取运送方式
function getShippingMethod(){
    var result = canCreateSaleOrder()
    if(result.canCreateSaleOrder){
        ajax_get("/inventory/getShippingMethod?wid=" + result.warehouseId, "", "",
            function (shipResStr) {
                var shipRes = $.parseJSON(shipResStr);
                if (shipRes.length > 0) {
                    var html = ''
                    for (var i = 0; i < shipRes.length; i++) {
                        if (shipRes[i].default) {
                            html += "<option value='" + shipRes[i].id + "' code='" + shipRes[i].methodCode + "' selected='selected'>" + shipRes[i].methodName + "</option>"
                        } else {
                            html += "<option value='" + shipRes[i].id + "' code='" + shipRes[i].methodCode + "'>" + shipRes[i].methodName + "</option>"
                        }
                    }
                    $(".microCapsule").find("select[name='shippingMethod']").html(html)

                    // 计算运费
                    getFreight4AProvince()
                } else {
                    layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
                }
            }, function (XMLHttpRequest, textStatus) {
                layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
            }
        );
    }
}

//自动匹配收货人信息
//广东省 深圳市 龙岗区 坂田街道桥联东路顺兴楼1003 518116 黄安纳 13603064940        千牛格式
//李心瑞，13999889672，新疆维吾尔自治区 乌鲁木齐市 沙依巴克区 红庙子街道西城街荣和城二期28号楼2单元1701 ，830000      网页格式
function sureMatchInfo(obj){
    var template = $("#address-template").find("option:selected").val();//选择的地址格式
    if (template == 0) {
        layer.msg("请选择地址格式！", {icon: 5, time: 2000});
        return;
    }

    var flag = false;
    if (template == 1) {//淘宝千牛格式
        var waitInfo = $("input[name=waitInfo]").val().trim().split("  ");
        if (waitInfo.length >= 7) {
            $(".microCapsule").find("select[name='sale-province']").val(getAddressId(waitInfo[0],prov_map)).change();
            if( $(".microCapsule").find("select[name='sale-province']").val()){
                $(".microCapsule").find("select[name='sale-city']").val(getAddressId(waitInfo[1],city_map)).change();
                if($(".microCapsule").find("select[name='sale-province']").val()){
                    flag = true;
                    $(".microCapsule").find("select[name='sale-region']").val(getAddressId(waitInfo[2],region_map));
                }
            }
            $(".microCapsule input[name='receiver']").val(waitInfo[length-2]);
            $(".microCapsule input[name='telephone']").val(waitInfo[length-1]);
            $(".microCapsule input[name='post-code']").val(waitInfo[length-3]);
            $(".microCapsule input[name='address-detail']").val(waitInfo[4]);
        }
    } else {//淘宝网页格式
        var waitInfo = $("input[name=waitInfo]").val().split("，");
        if (waitInfo.length >= 4) {
            var addrs = waitInfo[2].trim().split(" ");// 省市区详细地址切分
            if (addrs.length >= 4) {
                $(".microCapsule").find("select[name='sale-province']").val(getAddressId(addrs[0],prov_map)).change();
                if($(".microCapsule").find("select[name='sale-province']").val()){
                    $(".microCapsule").find("select[name='sale-city']").val(getAddressId(addrs[1],city_map)).change();
                    if($(".microCapsule").find("select[name='sale-city']").val()){
                        flag = true;
                        $(".microCapsule").find("select[name='sale-region']").val(getAddressId(addrs[2],region_map));
                    }
                }
                var address = "";
                $.each(addrs,function(i,item){
                    if(i>=3) {
                        address += item;
                    }
                });
                $(".microCapsule input[name='address-detail']").val(address);
            }
            $(".microCapsule input[name='receiver']").val(waitInfo[0]);
            $(".microCapsule input[name='telephone']").val(waitInfo[1]);
            $(".microCapsule input[name='post-code']").val(waitInfo[3]);
        }
    }
    if (!flag) {
        layer.msg("输入格式不正确或省市区未匹配！", {icon: 5, time: 2000});
    }
}

function getAddressId(key,map){
    if(map[key]){
        return map[key];
    }

    key = key.substr(0,key.length-1);
    if(key){
        return map[key];
    }
}

// 获取运费
function getFreight4AProvince(){
    var result = canCreateSaleOrder()
    var provinceId = $(".microCapsule").find("select[name='sale-province']").val()
    var shippingCode = $(".microCapsule").find("select[name='shippingMethod'] option:selected").attr("code")
    // 可以出货，且选择了省和运送方式
    if(result.canCreateSaleOrder && provinceId && shippingCode){
        var freightParam = {warehouseId:result.warehouseId, shippingCode:shippingCode, countryId:44, provinceId:provinceId};
        var orderDetails = [];
        var type = getOptType();
        if(type){// 导入
            // 检查是否有完税仓的商品
            $(".import_purchaseList_table tbody tr").each(function(i,tr){
                if($(tr).find("td:eq(2)").find("select").val() == 2024){
                    orderDetails.push({sku: $(tr).find("td:eq(1)").text(), num: parseInt($(tr).find("td:eq(8)").text())});
                }
            });
        }else{// 手动录入
            // 检查是否有完税仓的商品
            $(".record_purchaseList_table").find("tbody tr").each(function(i,tr){
                if($(tr).find("td:eq(2)").attr("warehouseId") == 2024){
                    orderDetails.push({sku: $(tr).find("td:eq(1)").text(), num: parseInt($(tr).find("td:eq(5)").find("input").val())});
                }
            })
        }
        freightParam.orderDetails = orderDetails;
        ajax_post("/inventory/getFreight", JSON.stringify(freightParam), "application/json",
            function (freightResStr) {
                var freightRes = freightResStr;
                if (freightRes.result) {
                    //自提标识
                    var bbcPostage = freightRes.msg ? freightRes.msg : 0.00;
                    // 订单金额加上运费
                    var money2Paid = 0.00
                    if(type) {// 导入
                        money2Paid = (parseFloat($("#import_sumPrice").text()) + parseFloat(bbcPostage)).toFixed(2)
                    }else {// 手动录入
                        money2Paid = (parseFloat($("#type_in_priceTotal").text()) + parseFloat(bbcPostage)).toFixed(2)
                    }
                    $("#money2Paid_div").find("input").val(money2Paid)// 待付款金额
                    $("#bbcPostage_span em").text(parseFloat(bbcPostage).toFixed(2))// 运费

                    showBalancePaymentOrNot()
                } else {
                    layer.msg("运费获取失败！" + freightRes.msg, {icon: 2, time: 2000});
                }
            }, function (XMLHttpRequest, textStatus) {
                layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
            }
        );
    }
}

// 判断是否可以进行出库：{canCreateSaleOrder:true/false,warehouseId:xx}为true时，才有warehouseId
function canCreateSaleOrder(){
    var result = {canCreateSaleOrder:false};
    // 要区分是手动录入还是导入的
    var type =getOptType();
    if(type){// 导入
        // 检查是否有完税仓的商品
        $(".import_purchaseList_table tbody tr").each(function(i,tr){
            if($(tr).find("td:eq(2)").find("select").val() == 2024){
                result.canCreateSaleOrder = true;
                result.warehouseId = 2024
                return false;// return false相当于break；return true相当于continue
            }
        });
    }else{// 手动录入
        // 检查是否有完税仓的商品
        $(".record_purchaseList_table").find("tbody tr").each(function(i,tr){
            if($(tr).find("td:eq(2)").attr("warehouseId") == 2024){
                result.canCreateSaleOrder = true;
                result.warehouseId = 2024
                return false;// return false相当于break；return true相当于continue
            }
        })
    }
    return result
}

// 动态创建生成方式
function createDynamicGenerateTypeOptions(){
    var generateTypeOptions = '<option value="1">采购进微仓</option>'
    // 有完税仓深圳仓的商品
    var result = canCreateSaleOrder()
    // TODO 暂时屏蔽云仓商品发货
    // if(result.canCreateSaleOrder){
    //     generateTypeOptions += '<option value="2">云仓商品发货</option>'
    // }
    $("#po_generateType").html(generateTypeOptions)
}

// 获取支付方式
function getPayTypes(purpose){
    $("#po_payType").empty()
    // 获取支付方式
    ajax_get("/member/method/backstage?purpose="+purpose+"&email="+$("#type_in_distributor_input").val(),"","",function(data){
        if(data.suc){
            var payType_optionsHtml = '<option value="">未付款</option>'
            $.each(data.list,function(i,item){
                payType_optionsHtml += '<option value="'+item.key+'">'+item.name+'</option>'
            });
            $("#po_payType").html(payType_optionsHtml)

            showBalancePaymentOrNot()
        }
    });
}

// 下单
function placeOrder(){
    // 要区分是手动录入还是导入的
    var type =getOptType();
    if(type){
        if($(".import_purchaseList_table tbody").find("tr").size() < 1){
            layer.msg("请导入采购单！",{icon:2,time:2000});
            return
        }
        import_prepared2CreateOrder()
    }else{
        // 检查是否选有商品
        if($(".record_purchaseList_table tbody").find('tr').size() < 1){
            layer.msg("没有可下单的正价商品！",{icon:2,time:2000});
            return
        }
        typeIn_prepared2CreateOrder()
    }
    getPayTypes(2)

    // 显示下单的页面
    createDynamicGenerateTypeOptions();
    judgeGenerateType($("#po_generateType").val())

    $(".place_order").show();
    $(".record_purchaseList_box").hide();
}

function showBalancePaymentOrNot(){
    ajax_get("/member/getAccount?email="+$("#type_in_distributor_input").val(),"","",function(data){
        var money2Paid = parseFloat($("#money2Paid_div").find("input").val());
        var balance = parseFloat(data.balance)
        // 如果订单金额大于余额，则不显示
        if(money2Paid > balance){
            $("#po_payType").find("option[value='balance']").remove()
        }
    });
}

// 选择生成方式
function po_generateType_onchange(obj){
    var genetateType = $(obj).val();
    judgeGenerateType(genetateType);
}

// 判断生成方式
function judgeGenerateType(genetateType){
    if(genetateType==1){// 微仓进货
        $(".microCapsule").addClass("display");
        getPayTypes(2)// 支付方式

        var money2Paid = 0.00
        if(getOptType()) {// 导入
            money2Paid = parseFloat($("#import_sumPrice").text()).toFixed(2)
        }else {// 手动录入
            money2Paid = parseFloat($("#type_in_priceTotal").text()).toFixed(2)
        }
        $("#money2Paid_div").find("input").val(money2Paid)// 待付款金额
        $("#bbcPostage_span em").text(0.00)// 运费
    }else if(genetateType==2){// 完税仓商品发货
        $(".microCapsule").removeClass("display");

        getPayTypes(3) // 支付方式
        getProvinces() // 获取省
        getShippingMethod() // 获取运送方式
    }
}

// 选择支付方式
function po_payType_onchange(obj){
    var payType = $(obj).val();
    // 现金支付，显示现金输入框
    if(payType=='cash'){
        $(".cashPayment").show();
    }else{
        $(".cashPayment").hide();
    }
}

// 取消下单
function callOff(){
    $(".place_order").hide();
    $(".record_purchaseList_box").show();

    // 要区分是手动录入还是导入的
    var type =getOptType();
    if(type){// 导入
        view_list()
    }else{
        show_inputOrder()
    }
}
// 获取类型，录入还是导入，0录入，1导入
function getOptType(){
    return $(".record_purchaseList_tab li").index($(".recharge_current"))
}

//获取收款方式数据
function gain_receipt_mode() {
    ajax_get("/member/getremos?"+(new Date()).getTime(), {}, undefined,
        function(data) {
            if (data.suc) {
                var optionHTML = '<option value="" selected="selected">请选择</option>';
                $.each(data.list, function(i, item) {
                    optionHTML += '<option value="' + item.id + '" data-mode="' + item.account + '——' + item.payee + '">' + item.remark + '</option>';
                });
                $("select[name='recipientId']").empty().append(optionHTML);
            } else {
                layer.msg(data.msg, {icon: 6, time: 2000});
            }
        }, function(xhr, status) {
            layer.msg("获取收款方式数据出错，请稍后重试！", {icon: 6, time: 2000});
        }
    );
}

// 提交线下转账的
function submitFormTransferOffline(){
    var options = {
        //url: url,                 //默认是form的action
        //type: type,               //默认是form的method（get or post）
        dataType: 'json',           //html(默认), xml, script, json...接受服务端返回的类型
        clearForm: false,          //成功提交后，清除所有表单元素的值
        resetForm: false,          //成功提交后，重置所有表单元素的值
        //target: '#output',          //把服务器返回的内容放入id为output的元素中
        //timeout: 3000,               //限制请求的时间，当请求大于3秒后，跳出请求
        //提交前的回调函数
        beforeSubmit: function(formData, jqForm, options){
            //formData: 数组对象，提交表单时，Form插件会以Ajax方式自动提交这些数据，格式如：[{name:user,value:val },{name:pwd,value:pwd}]
            //jqForm:   jQuery对象，封装了表单的元素
            //options:  options对象
            //比如可以再表单提交前进行表单验证
            var recipientId = $("#audit_form select[name='recipientId']").val();
            if (recipientId == "") {
                $("#audit_form select[name='recipientId']").focus()
                layer.msg("请选择收款渠道", {icon: 0, time: 2000});
                return false;
            }
            var receiptAccount = $("#receiptAccount").text().trim();
            if (receiptAccount == "") {
                layer.msg("收款账户不能为空", {icon: 0, time: 2000});
                return false;
            }
            var receipt_payee = $("#receipt_payee").text().trim();
            if (receipt_payee == "") {
                layer.msg("收款人不能为空", {icon: 0, time: 2000});
                return false;
            }
            var transferCard = $("#audit_form input[name='transferCard']").val().trim();
            if (transferCard == "") {
                $("#audit_form input[name='transferCard']").focus()
                layer.msg("付款账户不能为空", {icon: 0, time: 2000});
                return false;
            }
            var distributorName = $("#audit_form input[name='distributorName']").val().trim();
            if (distributorName == "") {
                $("#audit_form input[name='distributorName']").focus()
                layer.msg("账户开户名不能为空", {icon: 0, time: 2000});
                return false;
            }
            var transferAmount = $("#audit_form input[name='transferAmount']").val().trim();
            if (!/^((\d{1})|([1-9]{1}\d+)|(\d{1}\.\d{1,2})|([1-9]{1}\d+\.\d{1,2}))$/.test(transferAmount)) {
                $("#audit_form input[name='transferAmount']").focus()
                layer.msg("请输入正确格式的付款金额", {icon: 0, time: 2000});
                return false;
            }
            if(transferAmount.length>8){
                $("#audit_form input[name='transferAmount']").focus()
                layer.msg("付款金额", {icon: 0, time: 2000});
                return false;
            }
            var transferAmount_fee = $("#audit_form input[name='transferAmount']").data("fee");
            if (transferAmount_fee != "" && parseFloat(transferAmount) < parseFloat(transferAmount_fee)) {
                layer.msg("付款金额不能小于订单金额", {icon: 0, time: 2000});
                return false;
            }
            var transferNumber = $("#audit_form input[name='transferNumber']").val().trim();
            if(!transferNumber){
                layer.msg("付款流水号不能为空", {icon: 0, time: 2000});
                return false;
            }

            var $file = $("#audit_form input[name='image']");
            if ($file != undefined && $file.val() != "") {
                var name = $file.val();
                if ($file[0].files[0].size > (2 * 1024 * 1024) ||
                    !(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
                    layer.msg("付款截图只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
                    return false;
                }
            }
            $("#submit_po_transferOffline").prop("disabled",true)
        },
        //提交成功后的回调函数
        success: function(data,status,xhr,$form){
            if (data.suc) {
                $("#audit_form").resetForm();
                $("#receipt_payee").text("");
                $("#audit_form input[name='image']").parent().next().text("");

                layer.msg("提交成功", {icon: 1, time: 2000}, function() {
                    $("#submit_po_transferOffline").prop("disabled",false)
                    $("p[position="+$("#forword").val()+"]").click();
                });
            } else {
                $("#submit_po_transferOffline").prop("disabled",false)
                layer.msg(data.msg, {icon: 2, time: 3000});
            }
        },
        error: function(xhr, status, error, $form){},
        complete: function(xhr, status, $form){}
    };
    $("#audit_form").ajaxSubmit(options);
}

// 线下转账 返回
function transferofflineReturn(){
    cleanTransferofflineInfo()
    $("#input_purchase_order_div").show()
    $('#transfer_offline_div').hide()
}

// 线下转账 取消
function transferofflineCancel(){
    cleanTransferofflineInfo()
    $("p[position="+$("#forword").val()+"]").click();
}

function cleanTransferofflineInfo(){
    $("#receiptAccount").text("");
    $("#receipt_payee").text("");
    $("#receiptAccount").text("")
    $("#receipt_payee").text("")
    $("#audit_form input[name='transferCard']").val("")
    $("#audit_form input[name='distributorName']").val("")
    $("#audit_form input[name='transferAmount']").val("")
    $("#audit_form input[name='transferAmount']").attr("data-fee","");
    $("#audit_form input[name='transferNumber']").val("")
    $("#audit_form input[name='transferTime']").val("")
    $("#audit_form input[name='receiptAccount']").val("")
    $("#audit_form input[name='image']").val("");
}

function readImg(obj){
    preview(obj,$("#preview"));
}

function emtpySkuMap(){
    skuMap = {};
}

//插入skuMap
function insertSkuMap(pList,gList){
    skuMap = {};
    var listAry = [pList,gList];
    $.each(listAry,function(k,_item){
        insItem(_item);
    });
}

function insItem(list){
    $.each(list,function(i,item){
        var wareArry = item.warehouseNameId;
        if(!wareArry||wareArry&&wareArry.length>0){
            var qty = item.unitType == 1 ? item.qty : item.qty*item.carton;
            var key = item.sku;
            if (skuMap[key]) {
              skuMap[key].qty = qty + skuMap[key].qty;
            } else {
              skuMap[key] = {
                  sku: item.sku,
                  title: item.title,
                  warehouseName: item.warehouseName,
                  qty: item.qty,
                  erpStock: 0,
                  short: true,
                  warehouseId: item.warehouseId,
                  interBarCode: item.interBarCode
              };
            }
        }
    });
}

// 查看erp库存
function swErpSto(){
    //分仓查询
    var skus = Object.keys(skuMap);
    if(!skus.length){
        layer.msg("请选择商品",{icon:5,time:2000});
        return;
    }
    var params = {};
    $.each(skuMap,function(k,v){
        var warehouseId = v.warehouseId;
        if(params[warehouseId]){
            params[warehouseId].skus.push(v.sku);
        }else{
            params[warehouseId] = {warehouseId:warehouseId, skus:[v.sku]};
        }
    });
    $.each(params,function(k,ktem){
        skuMap = erpSto(ktem,skuMap);
    });
    erpStoHtml(skuMap);
}

// 查看erp库存按钮事件
$("body").on("click","#sw_erpsto",function(){
    swErpSto();
})