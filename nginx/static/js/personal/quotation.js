var detail;
define("quotation", ["jquery", "layer"], function($, layer) {
    $("body").on("click",".create_purchasing_order",function(){
        var id = $(this).attr("quotation");
        isnulogin(function(email){
            var ods = new Array();
            $.each(detail[id],function(i,e){
                ods.push({
                    "title": e.ctitle,
                    "price": e.disPrice,
                    "marketPrice": e.localPrice,
                    "purchaseCostPrice": e.disTotalCost,
                    "disProfitMargin": e.disProfitRate,
                    "qty": e.qty,
                    "warehouseId": e.warehouseId,
                    "warehouseName":e.warehouseName,
                    "sumPrice": parseFloat(parseFloat(e.disPrice * e.qty).toFixed(2)),
                    "publicImg": e.imageUrl,
                    "sku": e.csku,
                    //添加属性
                    "disProfit": e.disProfit,//分销毛利润
                    "disVat": e.disVat,//分销增值税
                    "disStockFee": e.disStockFee,//分销操作费
                    "disShippingType": e.disShippingType,//分销物流方式
                    "disOtherCost": e.disOtherCost,//分销其他费用
                    "disTransferFee": e.disTransferFee,//分销转仓费
                    "dislistFee": e.disListFee,//分销登录费
                    "distradeFee": e.disTradeFee,//分销平台 交易费
                    "dispostalFee": e.disPostalFee,//分销行邮税
                    "disimportTar": e.disImportTar,//分销进口关税
                    "disgst": e.disGst,//分销消费税
                    "disinsurance": e.disInsurance,//分销保险费
                    "distotalvat": e.disTotalVat,//分销增值税
                    "disCifPrice": e.disCifPrice,//分销cif价格
                    "cost": e.cost,//裸采购价
                    "disFreight": e.disFreight,//分销物流价
                    "disStockId": e.warehouseId,//仓库id
                    "disPrice": e.disPrice  //分销价
                });
            });
            var orderData = {
                "email": email,
                "orderDetail": ods,
                "totalPrice": sumPrice
            };
            $(this).attr("disabled",true).addClass("disabled-btn");
            ajax_post("/purchase/order", JSON.stringify(orderData), "application/json",
                function (response) {
                    if(response.errorCode == 0){
                        layer.msg("生成订单成功。", {icon : 6, time : 2000});
                        //更新报价单状态
                         ajax_post("/purchase/buildOrder", JSON.stringify({id:id}), "application/json",
                            function (data) {
                                if(data.suc){
                                    layer.msg(data.msg, {icon : 6, time : 2000},function(index){
                                        layer.close(index);
                                        window.location.href = "../product/balance-paid.html?purno="+response.errorInfo;
                                    });
                                }else{
                                    layer.msg(data.msg, {icon : 2, time : 2000});
                                }
                            },
                            function (xhr, status) {
                                console.error("status-->" + status);
                            }
                        );

                    }else{
                        layer.msg("生成订单失败", {icon : 2, time : 1000});
                        $(this).attr("disabled",false).removeClass("disabled-btn");
                    }
                },
                function (xhr, status) {
                    console.error("status-->" + status);
                }
            );
        });
    });

    $("body").on("click",".mine_price_detail_back",function(){
        $(".mine_price_list").show();
        $(".mine_price_box").hide();
    })

    function init_quotation(curr,email) {
        var params = {
            currPage: curr == undefined || curr == 0 ? 1 : curr,
            pageSize: 5,
            email : email
        };
        ajax_post("../purchase/gainquotrecord", JSON.stringify(params), "application/json",
            function(data) {
                if (data.returnMess.errorCode == "0") {
                    $("#quotation_body").empty();
                    detail = {};
                    var htmlCode = "";
                    $.each(data.quos,function(i,item){
                        detail[item.id] = JSON.parse(item.excelInfo);
                        htmlCode += "<tr>" +
                            "<td>"+item.excelName+"</td>" +
                            "<td>"+item.createDateStr+"</td>" +
                            "<td>"+(item.isBuildOrder ? '已生成' : '未生成') +"</td>" +
                            "<td class='mine_price_link'data-name = '"+item.excelName+"'' onclick='show_detail("+item.id+","+item.isBuildOrder+",this)' >查看我的报价单</td>" +
                            "</tr>";
                    });
                    $("#quotation_body").prepend(htmlCode);
                    var page = {
                        currPage: params.currPage,
                        totalPage: (data.totalCount % params.pageSize == 0) ? (data.totalCount / params.pageSize) : (parseInt(data.totalCount / params.pageSize) + 1)
                    };
                    init_export_detail_list_pagination(page,email);
                } else {
                    layer.msg('获取报价单导出明细数据失败，请稍后重试！', {icon: 2, time: 2000});
                }
            },
            function(xhr, status) { console.log("error--->" + status); }
        );
    }

    //导出明细页面初始化导出明细列表分页栏
    function init_export_detail_list_pagination(page,email) {
        if ($("#quotation_page")[0] != undefined) {
            $("#quotation_page").empty();
            laypage({
                cont: 'quotation_page',
                pages: page.totalPage,
                curr: page.currPage,
                groups: 5,
                skin: '#55ccc8',
                first: '首页',
                last: '尾页',
                prev: '上一页',
                next: '下一页',
                skip: true,
                jump: function(obj, first) {
                    //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                    if(!first){
                        init_quotation(obj.curr,email);
                    }
                }
            });
        }
    }

    return {
        init_quotation: init_quotation
    };
});

var sumPrice = 0;
function show_detail(id,flag,obj){
    $(".mine_price_list").hide();
    $(".mine_price_box").show();
    $("title").text("我的报价单 - TOMTOP Supply Chain");
    $("#excelName").text($(obj).data("name"));
    var details = detail[id];
    $("#quotation_detail").empty();
    var htmlCode = "";
    var num = 0;
    sumPrice = 0;
    $.each(details,function(i,item){
        sumPrice += item.disPrice * item.qty;
        num += item.qty;
        htmlCode += '<tr  data-unittotal="'+(item.disPrice*item.qty)+'" data-whid="'+item.warehouseId+'" data-ssku="'+item.csku+'" >' +
            '<td style = "width:46%">'+item.ctitle+'</td>' +
            '<td>'+item.csku+'</td>' +
            '<td>'+parseFloat(item.disPrice).toFixed(2)+'</td>' +
            '<td>'+parseFloat(item.localPrice).toFixed(2)+'</td>' +
            '<td>' +
            (
                flag?
                item.qty:
                '<ul class="cart_num">' +
                '<li id="cart_num_Reduction"  onclick = "totalPrice(this,false,'+item.warehouseId+','+item.qty+')" >—</li>' +
                '<li id="cart_nums"><input id="cart-button-num" type="text" onkeyup="iptChange(this,'+item.warehouseId+','+item.qty+')"  value="'+item.qty+'"  data-qty = "'+item.qty+'" name="qty"></li>' +
                '<li id="cart_num_add" onclick = "totalPrice(this,true,'+item.warehouseId+','+item.qty+')"  name="">+</li>' +
                '</ul>'
            )+
            '</td>' +
            '<td>'+item.warehouseName+'</td>' +
            '</tr>';
    });
    $("#quotation_detail").prepend(htmlCode);
    $(".selected_product").text(details.length);
    $(".all_product").text(num);
    $(".create_purchasing_order").attr("quotation",id);
    if(flag || !num){
        $(".create_purchasing_order").css("display","none").attr("disabled",true).addClass("disabled-btn");
    }else{
        $(".create_purchasing_order").css("display","block").attr("disabled",false).removeClass("disabled-btn");
    }
    $(".num_order_product").text(parseFloat(sumPrice).toFixed(2));
}

//点击加减按钮增加商品数量
function totalPrice(obj, isAdd, itemId,minqty) {
    var quantity = 0, sourceQty = 0;
    if (isAdd) {
        sourceQty = parseInt($(obj).prev().find("input").val());
        quantity = sourceQty + 1;
    } else {
        sourceQty = parseInt($(obj).next().find("input").val());
        quantity = sourceQty == 1 ? 1 : (sourceQty - 1);
    }
    if(quantity < minqty){
        layer.msg("不能低于报价时的数量(" +minqty + ")", {icon: 2, time: 2000});
        return;
    }
    var params = {
        "itemId": itemId,
        "qty": quantity
    };
    check_isexceed_inventory(params, sourceQty, $(obj).parent());
}

//检查将要变更的购物车商品数量是否超出库存，未超出则更新购物车商品数量
function check_isexceed_inventory(params, sourceQty, $node) {
    var ssku = $node.parent().parent().data("ssku");
    var whid = $node.parent().parent().data("whid");
    isnulogin(function (email) {
        var ivyparams = {
            "sku": ssku,
            "warehouseId": whid
        };
        ajax_get("/inventory/warehousing/cloud-inventory", ivyparams, "",
            function (data) {
                if (data.list != null && data.list != undefined && data.list.length == 1) {
                    if (data.list[0].availableStock >= params.qty) {
                            $node.find("input").val(params.qty);
                            $node.find("input").data("qty", params.qty);
                            count();
                        //更新数量
                        var id = $(".create_purchasing_order").attr("quotation");
                        $.each(detail[id],function(i,e){
                            if(e.csku == ssku && e.warehouseId == whid){
                                e.qty = params.qty;
                            }
                        });
                    } else {
                        layer.msg("修改后的数量(" + params.qty + ")将超出库存", {icon: 2, time: 2000});
                        $node.find("input").val(sourceQty);
                    }
                } else {
                    layer.msg("经云仓查询，当前商品在该仓库下无记录，无法判断库存是否充足！", {icon: 2, time: 4000});
                    $node.find("input").val(sourceQty);
                }
            },
            function (xhr, status) {
                console.log("error--->" + status);
            }
        );
    });
}

//计算总价格和总数量
function count() {
    var ipts = new Array(),
        unitTotals = new Array(),
        tPrice = 0,
        quantity = 0;
    $.each($("#quotation_detail tr"), function (i, e) {
        var tds = $(e).find("td");
        var qty = parseInt(tds.eq(4).find("input").val());
        var unitTotal = parseFloat(tds.eq(2).text()*qty);
        ipts.push(qty);
        unitTotals.push(unitTotal);
    });
    if (ipts.length == 0) {
        $(".product_num").html(0);
        $(".all_price").html(changeTwoDecimal_f(0));
        $("#totolPrice").html(0);
        $(".p-mainprice").html(0);
    } else {
        for (var s = 0; s < ipts.length; s++) {
            quantity += ipts[s];
        }
        for (var i = 0; i < unitTotals.length; i++) {
            tPrice += unitTotals[i]
        }
        $(".all_product").text(quantity);
        sumPrice = tPrice;
        $(".num_order_product").text(parseFloat(tPrice).toFixed(2));
    }
    if (quantity == "0") {
        $(".create_purchasing_order").attr("disabled",true).addClass("disabled-btn");
    } else {
        $(".create_purchasing_order").attr("disabled",false).removeClass("disabled-btn");
    }
}

//直接更改输入框中的商品数量
function iptChange(obj, itemId,minqty) {
    var quantity = $(obj).val(), sourceQty = parseInt($(obj).data("qty"));
    if (!/^[1-9]\d*$/.test(quantity)) {
        layer.msg("请输入数字(必须是大于0的整数)", {icon: 2, time: 2000});
        $(obj).val(sourceQty);
        count();
        return;
    }
    if(quantity < minqty){
        layer.msg("不能低于报价时的数量(" +minqty + ")", {icon: 2, time: 2000});
        $(obj).val(minqty);
        count();
        return;
    }
    var params = {
        "itemId": itemId,
        "qty": parseInt(quantity)
    };
    check_isexceed_inventory(params, sourceQty, $(obj).parent().parent());
}