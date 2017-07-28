/**
 * 采购单-客服确认
 * Created by Administrator on 2016/10/26.
 */
var laypage = undefined;
var layer = undefined;
var globalCSPurchaseOrderMap = undefined;

function initAuditByCustomerService(lay, layd){
    laypage = layd;
    layer = lay;
}

function getSetting_purchaseorder_cs() {
    globalCSPurchaseOrderMap={}
    var setting = {
        url:"/purchase/viewpurchasenew",
        postData:{status:"4"},
        rownumbers : true, // 是否显示前面的行号
        datatype : "json", // 返回的数据类型
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        // colNames:["订单编号"],
        colModel:[
            {label:"订单编号",name:"purchaseOrderNo",index:"purchaseOrderNo",align:"center",sortable:false},
            {label:"下单时间",name:"sorderDate",index:"purchaseDate",align:"center",sortable:true},
            {label:"名称",name:"nickName",index:"nickName",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"分销商",name:"email",index:"email",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"业务员",name:"customerService",index:"customerService",align:"center",sortable:false},
            {label:"录入人",name:"inputUser",index:"inputUser",align:"center",sortable:false},
            {label:"支付方式",name:"paymentId",index:"paymentId",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                return getPayMethod(cellvalue)
            }},
            {label:"操作",sortable:false,align:"center", formatter:function(cellvalue, options, rowObject){
                globalCSPurchaseOrderMap[rowObject.id] = rowObject
                var opt = //'<span style = "cursor:pointer;" id="'+rowObject.id+'" onclick="showCSPurchaseOrderDetail(this)">详情</span>'+
                    '<span style = "cursor:pointer;" id="'+rowObject.id+'" onclick="shoppingGoods(this)">去确认</span>'+
                    '<span style = "cursor:pointer;" id="'+rowObject.id+'" onclick="showCSAuditLogs(this)">查看日志</span>'
                return opt
            }}
        ],
        viewrecords : true,
        onPaging:function(pageBtn){
            // 清空缓存的数据
            globalCSPurchaseOrderMap = {}
        },
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#orders_by_CS_pagination",//分页
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
            doShowCSPurchaseOrderDetail(rowid)
        }
    }

    return setting;
}

function getCSPurchaseOrderByConditions(){
    // 拿到原有的，清除掉
    var postData = $("#orders_by_CS_table").jqGrid("getGridParam", "postData");
    $.each(postData, function (k, v) {
        delete postData[k];
    });
    var params = {
        status: 4, seachFlag: $("#searchOrdersByCSInput").val().trim(),
        sorderDate: $("#cs_startTime").val(), eorderDate: $("#cs_endTime").val(),
    }
    console.log(params)

    $("#orders_by_CS_table").jqGrid('setGridParam', {page:1, postData: params}).trigger("reloadGrid");
}

function showCSPurchaseOrderDetail(obj){
    clickThisTr($(obj).parent().parent())
    var id = $(obj).attr("id")
    doShowCSPurchaseOrderDetail(id)
}
function doShowCSPurchaseOrderDetail(id){
    //$("#cs_purchaseorder_detail_div").empty()
    var rowObject = globalCSPurchaseOrderMap[id]
    var csAuditLogs = rowObject.csAuditLogs
    var financeAuditLogs = rowObject.financeAuditLogs
    var details = rowObject.details

    // OA审批单号
    $("#cs_purchaseorder_detail_div").find("span[name='oaAuditNoSpan']").html("OA审批单号："+deal_with_illegal_value(rowObject.oaAuditNo))
    // 录单备注信息
    var remarksHtml = '<div><span>备注人：'+rowObject.inputUser+'</span><em>备注时间：'+rowObject.sorderDate+'</em></div>' +
        '<div>备注：'+deal_with_illegal_value(rowObject.remarks ? rowObject.remarks : rowObject.busenessRemarks)+'</div>'
    $("#cs_purchaseorder_detail_div").find("div[name='remarksInfoDiv']").html(remarksHtml)
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
    $("#cs_purchaseorder_detail_div").find("div[name='csRemarksInfoDiv']").html(csRemarksHtml)
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
    $("#cs_purchaseorder_detail_div").find("div[name='financeRemarksInfoDiv']").html(financeRemarksHtml)
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
    $("#cs_purchaseorder_detail_div").find("tbody[name='productInfoTbody']").html(productInfoHtml)

    var bbcPostage = rowObject.bbcPostage;
    var totalPrices  = rowObject.purchaseTotalAmount + (bbcPostage?bbcPostage:0);
    var couponsCode =  rowObject.couponsCode;
    var couponsAmount = rowObject.couponsAmount;
    var actually_paid = totalPrices;

    var sumInfoHtml = '<span>商品总数'+totalQty+'个</span>&nbsp;&nbsp;&nbsp;' +
        '<span>商品总计'+(rowObject.orderProTotal?parseFloat(rowObject.orderProTotal).toFixed(2):0.00)+'元</span>';
    if(rowObject.reducePrice){
        sumInfoHtml += '-&nbsp;&nbsp;'+
            '<span>(rowObject.purchaseType == 1?"活动优惠":"整单优惠")'+
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
        '&nbsp;&nbsp;=&nbsp;&nbsp;订单金额<span>' + parseFloat(actually_paid).toFixed(2) + '元</span>'
    $("#cs_purchaseorder_detail_div").find("p[name='sumInfoP']").html(sumInfoHtml)

    layer.open({
        type: 1,
        title: rowObject.purchaseOrderNo,
        content: $("#cs_purchaseorder_detail_div"),
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

// 查看日志
function showCSAuditLogs(obj){
    clickThisTr($(obj).parent().parent())

    var id = $(obj).attr("id")
    var rowObject = globalCSPurchaseOrderMap[id]
    var orderNo = rowObject.purchaseOrderNo;
    ajax_post("/purchase/allAuditLogs", JSON.stringify({purchaseOrderNo:orderNo}),
        "application/json",
        function (data) {
            var allAuditLogs = data.allAuditLogs;
            var tbody = $("#cs_auditLogs").find("table tbody");
            tbody.empty();

            var html = '';
            // 时间 xxx（管理员）客服确认通过/客服确认关闭/财务确认已支付
            // /财务确认未支付/财务确认通过/财务确认不通过
            if(allAuditLogs.length > 0){
                for(var i in allAuditLogs){
                    var log = allAuditLogs[i]
                    var jsonValue = eval("(" + log.jsonValue + ")")
                    var text = "<span style='display: inline-block; margin-left: 20px'>"+formatDateTime(log.auditDate)+"</span>"+
                        "<span style='display: inline-block; margin-left: 40px'>"+log.auditUser+"(管理员)</span>"
                    if(log.auditType==1){// 客服审核的
                        if(log.status==6)
                            text+="<span  style='display: inline-block; margin-left: 40px'>客服确认通过</span>"
                        else if(log.status==2)
                            text+="<span  style='display: inline-block; margin-left: 40px'>客服确认关闭</span>"
                    }else{// 财务审核的
                        if(jsonValue.paid==0){// 未支付
                            text+="<span  style='display: inline-block; margin-left: 40px'>财务确认未支付</span>"
                        }else{// 已支付
                            // text+="<span  style='display: inline-block; margin-left: 40px'>财务确认已支付</span>"
                            // true/false
                            if(jsonValue.profitPassed){// 财务通过
                                text+="<span  style='display: inline-block; margin-left: 40px'>财务确认通过</span>"
                            }else{// 财务未通过
                                text+="<span  style='display: inline-block; margin-left: 40px'>财务确认不通过</span>"
                            }
                        }
                    }
                    html += '<tr>' +
                    '<td>'+text+'</td>'
                    '</tr>'
                }
            }else{
                html = '<tr>' +
                '<td>暂无操作日志</td>'
                '</tr>'
            }
            tbody.html(html)

            layer.open({
                type: 1,
                title: "操作日志",
                area: ['450px', "500"],
                shadeClose: true, //点击遮罩关闭
                content: $("#cs_auditLogs"),
                scrollbar: false,
                btn: ['取消'],
                yes:function(index){
                    layer.close(index)
                }
            });
        }
    )
}

// 选赠品 --zuoting 12/14
function shoppingGoods(obj){
    clickThisTr($(obj).parent().parent())

    var id = $(obj).attr("id")
    var rowObject = globalCSPurchaseOrderMap[id]
    var orderNo = rowObject.purchaseOrderNo;
    var shopHtml = 
        '<div class="shopping-goods">'+
        '<div class="shopping-goods-l">'+
        '<p>请选择活动：</p>'+
        '<select  id="gift_sel">'+
        '<option value=0>不参加活动</option>'+
        '</select>'+
        '<p>请选择赠品：</p>'+
        '<table>'+
        '<thead>'+
        '<tr>'+
        '<th>赠品sku</th><th>赠品数量（个）</th></tr>'+
        '</thead>'+
        '<tbody id="gift_body">'+
        '</tbody>' +
        '</table>'+
        '<p>商品总数：<em class="red" id="total">0</em>&nbsp;个</p>'+
        '</div>'+
        '<div class="shopping-goods-r">'+
        '<p>备注：</p>' +
        '<textarea id="cs_remark"  placeholder="不超过500字" maxlength="500" ></textarea>' +
        '</div>'+
        '</div>';

    var giftList = {},priviledgeid,total = 0,sel_map = {},actId;
    layer.open({
        type: 1,
        title: "选赠品",
        area: ['500px', "auto"],
        shadeClose: true, //点击遮罩关闭
        content: shopHtml,
        btn: ['确定提交','关闭订单','取消'],
        success:function(){
            var actMap = {};
            ajax_get("/purchase/getMaketAct?orderNo="+orderNo,"","",function(res){
                if(res.suc){
                    if(res.actList.length>0){
                        $.each(res.actList,function(i,item){
                            $(".shopping-goods #gift_sel").append("<option value='"+item.id+"'>"+item.name+"</option>");
                        });
                    }else{
                       $(".shopping-goods #gift_sel").append('<option value=0>无可选活动</option>').attr("disabled",true);  
                    }
                    actMap = res.actMap;
                }else{
                    layer.msg(res.msg,{icon:5,time:2000});
                }
            });
            $(".shopping-goods").on("change","#gift_sel",function(){
                actId =$(this).val();
                $(".shopping-goods  #gift_body").empty();
                if(actMap[actId]){
                    giftList = actMap[actId].giftList;
                    total = actMap[actId].total;
                    priviledgeid = actMap[actId].priviledgeid;
                }else{
                    giftList = {};
                    total = 0;
                    priviledgeid = undefined;
                }
                $(".shopping-goods #total").text(total);
                sel_map = {};
                var trs = "",key;
                $.each(giftList,function(i,item){
                    key = item.sku+"_"+item.warehouseId;
                    sel_map[key] = item;
                    trs +=  '<tr>'+
                        '<td>'+item.sku+'</td> '+
                        '<td><input id="num" data-key="'+key+'" type="text"/></td> '+
                        '</tr>';
                });
                $(".shopping-goods  #gift_body").append(trs);
            });
            //校验数字格式
            $(".shopping-goods").on("keyup","#num",function(){
                if (this.value.length == 1) {
                    this.value = this.value.replace(/[^1-9]/g, '')
                } else {
                    this.value = this.value.replace(/\D/g, '')
                }
            });
            //数量赋值
            $(".shopping-goods").on("blur","#num",function(){
                var qty = parseInt($(this).val());
                var key = $(this).data("key");
                sel_map[key].qty = (qty !=""?qty:0);
            });
        },
        yes:function(index){
            var filter = [];
            var realTotal = 0;
            $.each(sel_map,function(key,value){
                if(value.qty > 0){
                    filter.push(value);
                    realTotal += value.qty;
                }
            });
            if(realTotal > total){
                layer.msg('请确认赠品数量！', {icon: 5, time: 2000});
                return;
            }
            var param = {
                remark: $("#cs_remark").val(),
                purchaseOrderNo: orderNo,
                passed: 1
            };
            if(filter.length>0){
                param.giftMap = {
                    giftList:filter,
                    actId:actId,
                    priviledgeid:priviledgeid
                }
            }
            var loading_index = layer.load(1, {shade: 0.5});
            ajax_post("/purchase/auditByCS", JSON.stringify(param),"application/json",function (data) {
              if(data.suc){
                    layer.msg(data.msg, {icon: 6, time: 2000}, function(){
                        layer.close(index);
                        layer.close(loading_index);
                        getCSPurchaseOrderByConditions();
                    })
                }else {
                    layer.msg(data.msg, {icon: 5, time: 2000}, function(){
                        layer.close(loading_index);
                    })
                }
            })
        },
        btn2:function(index){
            var remark = $("#cs_remark").val();
            layer.confirm('确定要关闭此采购单？', {btn: ['确认','取消']}, function(){
                var loading_index = layer.load(1, {shade: 0.5});
                // 确认关闭
                var param = {
                    remark: remark,
                    purchaseOrderNo: orderNo,
                    passed: 0
                };
                ajax_post("/purchase/auditByCS", JSON.stringify(param),"application/json",function (data) {
                    if(data.suc){
                        layer.msg(data.msg, {icon: 6, time: 2000}, function(){
                            layer.close(index)
                            layer.close(loading_index)
                            getCSPurchaseOrderByConditions()
                        })
                    }else {
                        layer.msg(data.msg, {icon: 5, time: 2000},function(){
                            layer.close(loading_index)
                        })
                    }
                })
            }, function(){
                layer.close(index)
            });
        },
        btn3:function(index){
            layer.close(index);
        }
    });
}
//详情展示
function showCashOrderSymbol(param, e) {
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