/**
 * Created by Administrator on 2016/12/26.
 */
var laypage = undefined;
var layer = undefined;
var globalASMarketingOrderMap = {}

function initMoAuditSecondly(laypageArg, layerArg){
    laypage = laypageArg
    layer = layerArg
    // get2ASMoList(1)

    // 绑定事件
    // $("#as_mo_search_btn").on("click",function(){
    //     get2ASMoList(1)
    // })

    globalASMarketingOrderMap = {}
}

function getSetting_as_mo() {
    globalASMarketingOrderMap = {}
    var setting = {
        url:"/sales/mo/list",
        postData:{status: 2},
        rownumbers : true, // 是否显示前面的行号
        datatype : "json", // 返回的数据类型
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        // colNames:["订单编号"],
        colModel:[
            {label:"订单编号",name:"marketingOrderNo",index:"marketing_order_no",align:"center",sortable:true},
            {label:"下单时间",name:"createDateStr",index:"create_date",align:"center",sortable:true},
            {label:"订单状态",name:"statusMsg",index:"status",align:"center",sortable:false},
            {label:"分销商",name:"email",index:"email",align:"center",sortable:false},
            {label:"分销商名称",name:"nickName",index:"nickName",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"分销商类型",name:"distributorTypeStr",index:"distributor_type",align:"center",sortable:false},
            {label:"录入人",name:"createUser",index:"createUser",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"业务员",name:"salesman",index:"salesman",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"业务备注",name:"businessRemark", index:"businessRemark",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                if(cellvalue && cellvalue.length>20){
                    return cellvalue.substring(0,20)+"..."
                }
                return deal_with_illegal_value(cellvalue)
            }},
            {label:"操作",sortable:false,align:"center", formatter:function(cellvalue, options, rowObject){
                globalASMarketingOrderMap[rowObject.id] = rowObject
                var opt = //"<a href='javascript:;' onclick='showASMarketingOrderDetail(this)' id='"+rowObject.id+"'>详情</a><br/>"+
                    "<a href='javascript:;' id='"+rowObject.id+"' onclick='auditSecondly(this)'>审核</a>" +
                    "<br/><a href='javascript:;' marketingOrderNo='"+rowObject.marketingOrderNo+"' " +
                    "onclick='as_showAuditLogs(this)'>操作日志</a>"
                return opt
            }}
        ],
        viewrecords : true,
        onPaging:function(pageBtn){
            // 清空缓存的数据
            globalASMarketingOrderMap = {}
        },
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#as_mo_pagination",//分页
        caption:"营销单复审列表（双击行，可查看营销单详情）",//表名称
        pagerpos : "center",
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        jsonReader:{
            root: "datas",  //数据模型
            page: "currPage",//数据页码
            total: "totalPage",//数据总页码
            records: "totalCount",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        },
        ondblClickRow: function(rowid) {
            doShowASMarketingOrderDetail(rowid)
        }
    }

    return setting;
}

function getASMarketingOrderByConditions(){
    // 拿到原有的，清除掉
    var postData = $("#as_mo_table").jqGrid("getGridParam", "postData");
    $.each(postData, function (k, v) {
        delete postData[k];
    });
    var params = {
        searchText: $.trim($("#as_mo_searchText").val()), status: 2,
        startDate: $("#as_mo_createdate_start").val(), endDate: $("#as_mo_createdate_end").val()
    }
    if($("#as_mo_distributor_type").val()){
        params.distributorType = $("#as_mo_distributor_type").val()
    }
    console.log(params)

    $("#as_mo_table").jqGrid('setGridParam', {page:1, postData: params}).trigger("reloadGrid");
}

// 查询列表
function get2ASMoList(currPage) {
    if (!currPage) {
        currPage = 1
    }
    var param = {
        pageSize: $("#as_mo_pageSize").val(), currPage: currPage,
        startDate: $("#as_mo_createdate_start").val(), endDate: $("#as_mo_createdate_end").val(),
        status: 2, searchText: $.trim($("#as_mo_searchText").val())
    }
    if($("#as_mo_distributor_type").val()){
        param.distributorType = $("#as_mo_distributor_type").val()
    }

    $("#as_mo_table tbody").empty()

    // 查询
    ajax_post("/sales/mo/list", JSON.stringify(param), "application/json", function (data) {
        $("#as_mo_totalCount").text(data.totalCount);
        $("#as_mo_pages").text(data.totalPage);
        if (data.totalCount > 0) {
            var orders = data.datas;
            for (var i in orders) {
                var totalQty = 0;
                var details = orders[i].detailList;
                for (var j in details) {
                    totalQty += parseInt(details[j].qty)
                }
                var partHtml = "<tr onmouseover='mo_onmouseover(this)'>" +
                    "<td class='add_list_down'>" +
                    "<span onclick='showMOSymbol(1,this)'>+</span>" +
                    "<span id='span_minus' onclick='showMOSymbol(2,this)' style='display:none;'>-</span>" +
                    "</td>" +
                    "<td>" + orders[i].marketingOrderNo + "</td>" +
                    "<td>" + orders[i].createDateStr + "</td>" +
                    // "<td>" + deal_with_illegal_value(orders[i].salesOrderNo) + "</td>" +
                    "<td>" + orders[i].statusMsg + "</td>" +
                    "<td>" + orders[i].email + "</td>" +
                    "<td>" + deal_with_illegal_value(orders[i].nickName) + "</td>" +
                    "<td>" + orders[i].distributorTypeStr + "</td>" +
                    "<td>" + deal_with_illegal_value(orders[i].createUser) + "</td>" +
                    "<td>" + deal_with_illegal_value(orders[i].salesman) + "</td>" +
                    "<td title='"+(orders[i].businessRemark ? orders[i].businessRemark : '')+"'>"  + (orders[i].businessRemark ?
                        (orders[i].businessRemark.length>5) ? orders[i].businessRemark.substring(0,5)+"..." : orders[i].businessRemark
                        : deal_with_illegal_value(orders[i].businessRemark)) +  "</td>" +
                    "<td><a href='javascript:;' totalQty='"+totalQty+"' bbcPostage='"+orders[i].bbcPostage+"' " +
                    "totalAmount='"+orders[i].totalAmount+"' marketingOrderNo='"+orders[i].marketingOrderNo+"' " +
                    "onclick='auditSecondly(this)'>审核</a><br/>" +
                    "<a href='javascript:;' marketingOrderNo='"+orders[i].marketingOrderNo+"' " +
                    "onclick='as_showAuditLogs(this)'>操作日志</a></td>" +
                    "</td>" +
                    "</tr>";

                // 详情
                partHtml += "<tr class='display'>" +
                    "<td colspan='12' style='padding:0'>" +
                    "<div class='list_detail detail  p_detail add_list_down_one'>" +
                    "<div style='padding: 10px;'>" +
                    "<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>收件人信息</span>" +
                    "<div style='text-align: left;'>"+
                    "<span class='spliter'>姓名："+orders[i].receiver+"</span>" +
                    "<span class='spliter'>手机号："+orders[i].receiverTel+"</span><br>" +
                    "<span class='spliter'>邮编："+orders[i].receiverPostcode+"</span>" +
                    "<span class='spliter'>收货地址："+(orders[i].provinceName+" "+orders[i].cityName+" "+orders[i].regionName+" "+orders[i].addressDetail)+"</span>" +
                    "</div>" +
                    "</div>" +
                    "<hr>";

                partHtml += "<p class='bbc_postage_p'>商品信息</p>" +
                    "<ul>" +
                    "<li style='width: 15%'>商品编号</li>" +
                    "<li style='width: 30%'>商品名称</li>" +
                    "<li style='width: 15%'>所属仓库</li>" +
                    "<li style='width: 15%'>单价（元）</li>" +
                    "<li style='width: 10%'>数量（个）</li>" +
                    "<li style='width: 15%'>小计（元）</li>" +
                    "</ul>";
                for (var j in details) {
                    partHtml += "<ul>" +
                        "<li style='width: 15%'>" + details[j].sku + "</li>" +
                        "<li style='width: 30%'>" + details[j].productName +"</li>" +
                        "<li style='width: 15%'>" + details[j].warehouseName + "</li>" +
                        "<li style='width: 15%'>" + parseFloat(details[j].disPrice).toFixed(2) + "</li>" +
                        "<li style='width: 10%'>" + details[j].qty + "</li>" +
                        "<li style='width: 15%'>" + (parseFloat(details[j].disPrice) * parseFloat(details[j].qty)).toFixed(2) + "</li>" +
                        "</ul>";
                }
                partHtml += '<div class="sum_price">商品总数<span>'+totalQty+'</span>个&nbsp;&nbsp;&nbsp;' +
                    '商品总计：<span>'+(parseFloat(orders[i].totalAmount).toFixed(2))+'</span>元';
                partHtml += '&nbsp;&nbsp;+&nbsp;&nbsp;运费<span>'+orders[i].bbcPostage+'</span>元</p>';
                partHtml += "</div>";
                partHtml += '</td></tr>';

                $("#as_mo_table tbody").append(partHtml);
            }
        }else{
            var partHtml = "<tr><td colspan='12'>暂无数据</td></tr>"
            $("#as_mo_table tbody").append(partHtml);
            return
        }
        init_mo_audits_pagination(data.totalPage, currPage);
    })
}

// 初始化分页栏
function init_mo_audits_pagination(pages, currPage) {
    if ($("#as_mo_pagination")[0] != undefined) {
        $("#as_mo_pagination").empty();
        laypage({
            cont: 'as_mo_pagination',
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
                    get2ASMoList(obj.curr);
                }
            }
        });
    }
}

function showASMarketingOrderDetail(obj){
    clickThisTr($(obj).parent().parent())
    var id = $(obj).attr("id")
    doShowASMarketingOrderDetail(id);
}
function doShowASMarketingOrderDetail(id){
    //$("#as_mo_detail_div").empty()
    var rowObject = globalASMarketingOrderMap[id]
    //
    //var partHtml = "<div class='list_detail detail  p_detail add_list_down_one'>" +
    //    "<div style='padding: 10px;'>" +
    //    "<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>收件人信息</span>" +
    //    "<div style='text-align: left;'>"+
    //    "<span class='spliter'>姓名："+rowObject.receiver+"</span>" +
    //    "<span class='spliter'>手机号："+rowObject.receiverTel+"</span><br>" +
    //    "<span class='spliter'>邮编："+rowObject.receiverPostcode+"</span>" +
    //    "<span class='spliter'>收货地址："+(rowObject.provinceName+" "+rowObject.cityName+" "+rowObject.regionName+" "+rowObject.addressDetail)+"</span>" +
    //    "</div>" +
    //    "</div>" +
    //    "<hr>";
    //
    //partHtml += "<p class='bbc_postage_p'>商品信息</p>" +
    //    "<ul>" +
    //    "<li style='width: 10%'>商品编号</li>" +
    //    "<li style='width: 30%'>商品名称</li>" +
    //    "<li style='width: 10%'>国际条码</li>" +
    //    "<li style='width: 10%'>所属仓库</li>" +
    //    "<li style='width: 10%'>单价（元）</li>" +
    //    "<li style='width: 10%'>数量（个）</li>" +
    //    "<li style='width: 20%'>小计（元）</li>" +
    //    "</ul>";
    //var totalQty = 0;
    //var details = rowObject.detailList;
    //for (var j in details) {
    //    totalQty += parseFloat(details[j].qty)
    //    partHtml += "<ul>" +
    //        "<li style='width: 10%'>" + details[j].sku + "</li>" +
    //        "<li style='width: 30%'>" + details[j].productName +"</li>" +
    //        "<li style='width: 10%'>" + deal_with_illegal_value(details[j].interBarCode) + "</li>" +
    //        "<li style='width: 10%'>" + details[j].warehouseName + "</li>" +
    //        "<li style='width: 10%'>" + parseFloat(details[j].disPrice).toFixed(2) + "</li>" +
    //        "<li style='width: 10%'>" + details[j].qty + "</li>" +
    //        "<li style='width: 20%'>" + (parseFloat(details[j].disPrice) * parseFloat(details[j].qty)).toFixed(2) + "</li>" +
    //        "</ul>";
    //}
    //partHtml += '<div class="sum_price">商品总数<span>'+totalQty+'</span>个&nbsp;&nbsp;&nbsp;' +
    //    '商品总计：<span>'+rowObject.totalAmount+'</span>元';
    //partHtml += '+&nbsp;&nbsp;运费<span>'+rowObject.bbcPostage+'</span>元</p>';
    //partHtml += "</div>";
    //$("#as_mo_detail_div").html(partHtml)

    var rowObject = globalASMarketingOrderMap[id]
    var receiverInfoHtml = '<div>'+
        '<span>姓名：'+rowObject.receiver+'</span>'+
        '<span>手机：'+rowObject.receiverTel+'</span>'+
        '<span>邮编：'+rowObject.receiverPostcode+'</span>'+
        '<address>地址：'+(rowObject.provinceName+" "+rowObject.cityName+" "+rowObject.regionName+" "+rowObject.addressDetail)+'</address>'+
        '</div>'
    $("#as_mo_detail_div").find("div[name='receiverInfoDiv']").html(receiverInfoHtml)

    var totalQty = 0;
    var details = rowObject.detailList;
    var productInfoHtml = ''
    for (var j in details) {
        totalQty += parseFloat(details[j].qty)
        productInfoHtml += '<tr>'+
            '<td>'+details[j].sku+'</td>'+
            '<td>'+details[j].productName+'</td>'+
            '<td>'+deal_with_illegal_value(details[j].interBarCode)+'</td>'+
            '<td>'+details[j].warehouseName+'</td>'+
            '<td>'+deal_with_illegal_value(details[j].expirationDate)+'</td>'+
            '<td>'+parseFloat(details[j].disPrice).toFixed(2)+'</td>'+
            '<td>'+details[j].qty+'</td>'+
            '<td>'+(parseFloat(details[j].disPrice) * parseFloat(details[j].qty)).toFixed(2)+'</td>'+
            '</tr>'
    }
    $("#as_mo_detail_div").find("table[name='productInfoTable']").find("tbody").html(productInfoHtml)
    var sumInfoHtml = '<span>商品总数量：'+totalQty+'个</span>' +
        '<span> 商品总计：'+rowObject.totalAmount+'元+运费'+rowObject.bbcPostage+'元</span>'
    $("#as_mo_detail_div").find("p[name='sumInfoP']").html(sumInfoHtml)

    layer.open({
        type: 1,
        title: rowObject.marketingOrderNo,
        content: $("#as_mo_detail_div"),
        area: ['1100px', '510px'],
        btn: ["关闭"],
        closeBtn: 1,
        shadeClose: true,
        move: false,
        btn1: function (index) {
            layer.close(index)
        }
    });
}

function auditSecondly(obj){
    clickThisTr($(obj).parent().parent())

    var id = $(obj).attr("id")
    var rowObject = globalASMarketingOrderMap[id]

    var marketingOrderNo = rowObject.marketingOrderNo
    var businessRemark = rowObject.businessRemark
    businessRemark = businessRemark ? businessRemark : "&nbsp;&nbsp;&nbsp;&nbsp;"
    var $divObj = $("#as_mo_popup_div")
    $divObj.find("span:eq(1)").html(businessRemark) // 备注
    var totalQty = 0
    $.each(rowObject.detailList,function(i,e){
        totalQty += parseInt(e.qty)
    })
    $divObj.find("span:eq(7)").find("em").text(totalQty)// 商品总数量
    $divObj.find("span:eq(9)").find("em").text(parseFloat(rowObject.totalAmount).toFixed(2))// 订单金额
    // 订单总金额
    $divObj.find("span:eq(11)").find("em").text((parseFloat(rowObject.totalAmount)+parseFloat(rowObject.bbcPostage)).toFixed(2))

    // 获取初审最新审核记录
    ajax_post("/sales/mo/auditlogs/latest/af", JSON.stringify({marketingOrderNo: marketingOrderNo}), "application/json", function (data) {
        if(data.suc){
            if(data.log){
                $divObj.find("span:eq(3)").text(data.log.auditUser)
                $divObj.find("span:eq(5)").text(data.log.remarks)
            }
        }else{
            layer.msg(data.msg, {icon : 5, time : 2000});
        }
    })

    layer.open({
        type: 1,
        title: '营销单审核',
        shadeClose: true,
        move: false,
        area: ['600px','auto'],
        btn: ['提交','取消'],
        content:$('#as_mo_popup_div'),
        yes: function(index){
            var passed = $divObj.find("input[type='radio']:checked").val()
            var remarks = $divObj.find("textarea").val()
            if(remarks && remarks.length>200){
                layer.msg("备注不能超过200字符", {icon : 2, time : 2000})
                $divObj.find("textarea").focus()
                return
            }
            var params = {marketingOrderNo: marketingOrderNo, passed: passed, remarks: remarks}
            var loading_index = layer.load(1, {shade: 0.5});
            ajax_post("/sales/mo/auditS", JSON.stringify(params), "application/json", function (data) {
                if(data.suc){
                    layer.msg(data.msg, {icon : 1, time : 2000}, function(){
                        layer.close(index);
                        // get2ASMoList(1);
                        getASMarketingOrderByConditions()
                        layer.close(loading_index);

                        cleanDatasAfterAuditS()
                    });
                }else{
                    layer.msg(data.msg, {icon : 5, time : 2000},function(){
                        layer.close(loading_index)
                    });
                }
            })
        },
        btn2: function(){
            cleanDatasAfterAuditS()
        }
    })
}

function cleanDatasAfterAuditS(){
    var $divObj = $("#as_mo_popup_div")
    // 清除数据
    $divObj.find("span:eq(3)").text("") // 初审人
    $divObj.find("span:eq(5)").text("")// 初审备注
    $divObj.find("span:eq(1)").html("") // 备注
    $divObj.find("span:eq(7)").find("em").text(0)// 商品总数量
    $divObj.find("span:eq(9)").find("em").text(0.00)// 订单金额
    $divObj.find("span:eq(11)").find("em").text(0.00)// 订单总金额

    $divObj.find("textarea").val("")// 输入的备注
    $divObj.find("input[type='radio']:eq(1)").prop("checked",true)// 选中
}

function as_showAuditLogs(obj) {
    clickThisTr($(obj).parent().parent())

    layer.open({
        type: 1,
        title: "操作日志",
        content: $("#as_mo_audit_logs_div"),
        area: ['550px', '330px'],
        btn: ["关闭"],
        closeBtn: 1,
        shadeClose: true,
        move: false,
        btn1: function (index) {
            layer.close(index)
        }
    });

    // 查询日志
    ajax_post("/sales/mo/auditlogs", JSON.stringify({marketingOrderNo:$(obj).attr("marketingOrderNo")}), "application/json", function (data) {
        var $tbody = $("#as_mo_audit_logs_div").find("table tbody");
        $tbody.empty()
        if(!data.suc){
            layer.msg(data.msg, {icon : 2, time : 2000});
            return
        }
        var logs = data.logs;
        if(logs.length<1){
            $tbody.append("<tr><td>暂无操作日志</td></tr>")
        }else{
            var logs_html = ''
            for(var i in logs){
                var log_html = logs[i].auditDateStr + "　" + logs[i].auditUser + "　操作　"
                log_html += (logs[i].auditType==1 ? "初审状态为：" : "复审状态为：")
                log_html += ((logs[i].status==2 || logs[i].status==3) ? "通过" : (logs[i].status==4 ? "不通过" : "未知状态"))
                var log_remark_html = "<b class='mo_audit_bl'>"+(logs[i].auditType==1 ? "初审备注：" : "复审备注：") +"</b><b class='mo_audit_br'>"+ (logs[i].remarks ? logs[i].remarks : "") + "</b>"
                logs_html += "<tr><td>"+log_html+"</td></tr>"
                logs_html += "<tr><td>"+log_remark_html+"</td></tr>"
                if(i>0){
                    logs_html += "<tr><td></td></tr>"
                }
                // 2016-11-02 17:20:58 admin 操作 初审状态为：通过。
                // 初审备注：建议通过，营销产品

                //2016-11-02 17:20:58 admin 操作 初审状态为：通过。
                // 复审备注：同意
            }
            $tbody.append(logs_html)
        }
    })
}