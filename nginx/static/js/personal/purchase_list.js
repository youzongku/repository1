var laypage = undefined;
// 初始化获取数据
/* postPurchase(null,false,"");*/
var detail_id = undefined;
var layer = undefined;
var distributorType = 1;//分销商类型，默认是1（普通）
var yjfWxUrl = "";
var purchaseType = undefined;//采购类型（1：常规采购，2：缺货采购）

/*$("#statusSelected").change(function(){
 var seachSpan = $("#seachInput").val().trim();
 postPurchase(null,false,seachSpan,laypage);
 });*/

function init_orders(lay, ler) {
    laypage = lay;
    layer = ler;
    postPurchase(null, false, "", laypage);
}

$("#timeSelected").change(function () {
    $("#pagination_order").empty();
    var seachSpan = $("#seachInput").val().trim();
    postPurchase(null, false, seachSpan, laypage);
});;

function timeSelected(e) {
    $(".purchase-current").removeClass();
    $(e).addClass("purchase-current");
    $("#pagination_order").empty();
    var seachSpan = $("#seachInput").val().trim();
    postPurchase(null, false, seachSpan, laypage);
}

//更新订单状态
function cancelOrder(purchaseNo, flag, sid) {
    var url = "/purchase/cancel";
    var param = {
        purchaseNo: purchaseNo,
        flag: flag
    };
    //询问框
    layer.confirm('确认取消订单？', {
        btn: ['确认','取消'] //按钮
    }, function(){
        isnulogin(function (email) {
            ajax_post(url,JSON.stringify(param),"application/json",function (data) {
                // 用户未登录
                if(data.code == 101){
                    layer.msg(data.msg, {icon: 1, time: 2000}, function(){
                        window.location.href = "/personal/login.html"
                    });
                    return
                }
                if (data.errorCode == "0") {
                    //如果这是缺货采购单
                    if (sid && sid != '') {
                        //取消发货单
                        ajax_post("/sales/updStu", JSON.stringify({"id": sid, "status": 5}), "application/json",
                            function (detailsDB) {
                                if (detailsDB && detailsDB.length > 0) {
                                    //查询采购单
                                    ajax_post("/purchase/getByNo",JSON.stringify({"purchaseOrderNo":purchaseNo,"flag":"SUCCESS"}), "application/json",
                                        function (data) {
                                            if (data.returnMess.errorCode == "0") {
                                                $.each(detailsDB,function(i,sale) {
                                                    $.each(data.pros,function(i,purchase) {
                                                        if(sale.sku == purchase.sku) {
                                                            sale.isDeducted = 1;
                                                            sale.qty = sale.qty - purchase.qty;
                                                        }
                                                    });
                                                });
                                                // TODO -------------仓库接口，需要进行对接--------------
                                                ajax_post("/inventory/resSto", JSON.stringify({"pros": {"historyDetail": detailsDB}}), "application/json",
                                                    function (resStoRes) {
                                                        var obj = new Object();
                                                        obj.curr = parseInt($("span.laypage_curr").text());
                                                        layer.msg("订单取消成功，客户缺货订单取消[成功]", {icon: 1, time: 3500});
                                                        postPurchase(obj, true, "", laypage);
                                                    },
                                                    function (xhr, status) {
                                                    }
                                                );
                                            }
                                        }
                                    )
                                }
                            },
                            function (xhr, status) {
                            }
                        )
                    } else {//一般采购单直接取消
                        var obj = new Object();
                        obj.curr = parseInt($("span.laypage_curr").text());
                        layer.msg("订单取消成功", {icon: 1, time: 2000});
                        postPurchase(obj, true, "", laypage);
                    }
                } else {
                    layer.msg("订单取消失败：" + data.errorInfo, {icon: 1, time: 2000});
                }
            })
        });
    }, function(){
    });
}


//搜索
function seachSpan() {
    var seachSpan = $("#seachInput").val().trim();
    postPurchase(null, false, seachSpan, laypage);
}

// function seachInput(){
//     $("#seachInput").val("");
// }

//查询展示
function postPurchase(obj, red, sea, lay) {
    sea = $("#seachInput").val().trim();
    isnulogin(function (email) {
        if ("0" == email) {
            window.location.href = "login.html";
        } else {
            //获取分销商类型
            ajax_get("/member/infor?" + Math.random(),null,undefined,function (u) {
                //分销商类型
                distributorType = u.comsumerType;
            })
            laypage = lay;
            var pageCount = "0";
            var pageSize = "10";
            if (red) {
                pageCount = obj.curr;
            }
            var timeSelected = $("#timeSelected").val();
            var statusSelected = $(".purchase-current").attr("value");//使用val()会导致‘’变成0
            var param = {
                email: email, pageSize: pageSize, pageCount: pageCount,
                orderDate: timeSelected, statusStr: statusSelected, seachFlag: sea
            };
            ajax_post("/purchase/viewpurchase",JSON.stringify(param),"application/json",function (data) {
                $(".order-product-detail").empty();
                $("#pagination_order").empty();
                if (data.returnMess.errorCode == "0") {
                    if (data.total > 0) {
                        var orders = data.orders;
                        var partHtml = "";
                        for (var i in orders) {
                            var part1 = "<div class='three-2 purchase-list light-gray'>" +
                                "<ul>" +
                                "<li>采购订单号:</li>" +
                                "<li type='"+orders[i].purchaseType+"' id='purchaseNo" + orders[i].purchaseOrderNo + "'>" + orders[i].purchaseOrderNo + "</li>" +
                                "<li id='orderDate" + orders[i].purchaseOrderNo + "'>" + orders[i].sorderDate + "</li>" +
                                "</ul>" +
                                "<div class='purchase-table'>" +
                                "<table>" +
                                "<tr class='three-2-tr1 bg-gray word-black'>" +
                                "<td class='tr1-td1' style='width: 40%;'>商品名称</td>" +
                                "<td class='tr1-td2' style='width: 10%;'>商品编号</td>" +
                                "<td class='tr1-td3' style='width: 10%;'>单价(元)</td>" +
                                "<td class='tr1-td4' style='width: 10%;'>数量(个)</td>" +
                                "<td class='tr1-td5' style='width: 10%;' >所属仓库</td>" +
                                "<td class='tr1-td7' style='width: 10%;' >订单状态</td>" +
                                "<td class='tr1-td8' style='width: 10%;'>订单操作</td>" +
                                "</tr>";
                            var details = orders[i].details;
                            //合并数量集合
                            var mergeMap = {};
                            for (var j in details) {
                                details[j].productImg=details[j].productImg.replace('http://','https://');
                                var key = details[j].sku +"_"+ details[j].warehouseId + "_" + details[j].isgift;
                                var capFee = (details[j].capFee != null?details[j].capFee:details[j].purchasePrice);
                                var part2 = "<tr class='three-2-tr2'>" +
                                    "<td class='tr2-td1 td-border'>" +
                                        "<div class='list-img'>" +
                                        "<a target='_blank' href='../../product/product-detail.html?sku=" + details[j].sku + "&warehouseId="+details[j].warehouseId+"' >" +
                                            "<img class='bbclazy' src='../img/img-load.png' data-original='" + details[j].productImg + "'/>" +
                                        "</a>" +
                                        "</div>" +
                                        "<span class='list-order'>" +
                                        "<a target='_blank' href='../../product/product-detail.html?sku=" + details[j].sku + "&warehouseId="+details[j].warehouseId+"' >" + details[j].productName + (details[j].isgift?"<em class='red'>【赠】</em>":"") + "</a>" +
                                        "</span>" +
                                    "</td>" +
                                    "<td class='tr2-td2 td-border'>" + details[j].sku + "</td>" +
                                    "<td class='tr2-td3 td-border'>" +
                                        "<p><s>" + details[j].marketPrice.toFixed(4) + "</s></p><p>" + details[j].purchasePrice.toFixed(4) + "</p>" +
                                    "</td>" +
                                    "<td class='tr2-td4 td-border' id = 'qty'>#{qty}</td>" +
                                    "<td class='tr2-td5 td-border' name='wareName' wareid='" + details[j].warehouseId + "'>"+details[j].warehouseName+"</td>";
                                if(mergeMap[key]){
                                    mergeMap[key].qty  += details[j].qty;
                                    mergeMap[key].part = $(mergeMap[key].part).find("#qty").text(mergeMap[key].qty).parents("tr")[0].outerHTML;
                                }else{
                                    part2 = $(part2).find("#qty").text(details[j].qty).parents("tr")[0].outerHTML;
                                    var map = {
                                        qty: details[j].qty,
                                        part:part2
                                    }
                                    mergeMap[key] = map;
                                }
                            }
                            $.each(mergeMap,function(key,value){
                                part1 += value.part;
                            });
                            var statusMes = orders[i].statusMes
                            if(orders[i].status==4 || orders[i].status==6)
                                statusMes = "待确认"
                            var part3 =
                                "<td class='tr2-td7'>" +
                                "<p class='lineh-27' id='status_" + orders[i].purchaseOrderNo + "'>"
                                + statusMes +
                                "</p>" +
                                "<p><a href='javascript:;'  class='btn-green-small' onclick='showDetail(\"" + orders[i].id + "\")' >订单详情</a></p>" +
                                "</td>" +
                                "<td class='tr2-td8' id='td_" + orders[i].purchaseOrderNo + "'>";
                            var part4 =
                                "<p>" +
                                "<input type='hidden' id='pay_email' value='" + orders[i].email + "'/>" +
                                "<input type='hidden' id='pay_Amount_" + orders[i].purchaseOrderNo + "' value='" + orders[i].purchaseTotalAmount + "'/>"
                            if(distributorType == 3){
                                part4 += "<a href='javascript:;' class='btn-blue-small blk' onclick='gopay(\"" + orders[i].purchaseOrderNo + "\")'>付款</a>"
                            }else{
                                part4 += "<a href='javascript:;' class='btn-blue-small blk' onclick='initPaymentBox(\"" + orders[i].purchaseOrderNo + "\")'>付款</a>"
                            }
                            part4 += "</p>";

                            var part5 = "<a href='javascript:;' class='cancel-order btn-green-small' onclick='cancelOrder(\"" + orders[i].purchaseOrderNo + "\",\"CANCEL\"," + orders[i].sid + ")' >取消订单</a>";
                            var part7 = "<a href='javascript:;' class='pay-agian btn-hyacinthine-small' onclick='buyAgain(\"" + orders[i].purchaseOrderNo + "\")' value='0'>再次采购</a>";
                            var part6 =
                                "</td>" +
                                "</tr>" +
                                "</table>" +
                                "</div>" +
                                "<div class='searchUD' onclick='downSilder(this)'>" +
                                "<b>" +
                                "<span class='attMs' style='display: inline;'><span>查看更多</span></span>" +
                                "<span class='attLs' style='display: none;'>上拉</span><em></em>" +
                                "</b>" +
                                "</div>" +
                                "</div>"
                            if (orders[i].status == "0") {
                                partHtml += part1 + part3 + part4 + part5 + part6;
                            } else if (orders[i].status == "3" || orders[i].status == "2") {
                                partHtml += part1 + part3 + part7 + part6;
                            } else {
                                partHtml += part1 + part3 + part6;
                            }
                        }
                        $(".order-product-detail").html(partHtml).show();
                        init_pagination_order(data.pages, pageCount);
                        purchaseLength();
                        $("img.bbclazy").lazyload({
                            effect:'fadeIn',
                            threshold:'10'
                        });
                    } else {
                        //$(".order-product-detail").fadeIn(100);
                        addLoadGif({
                            togBox:".order-product-detail",
                            togPage:'#pagination_order',
                            hint: '<div class="nothing"><div>抱歉，暂无相关订单</div></div>'
                        },true);
                        //layer.msg('暂无相关订单', {icon: 5, time: 1000}, function (index) {
                        //    layer.close(index);
                        //});
                    }
                }
            })
        }
    });
    //loading
    return creatLoading(true, theDiv);
}

// 初始化分页栏
function init_pagination_order(pages, counts) {
    scrollPosTop();
    if ($("#pagination_order")[0] != undefined) {
        $("#pagination_order").empty();
        laypage({
            cont: 'pagination_order', pages: pages, curr: counts,
            groups: 5, skin: 'yahei',
            first: '首页', last: '尾页', prev: '上一页', next: '下一页',
            skip: true,
            jump: function (obj, first) {
                //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                if (!first) {
                    postPurchase(obj, true, "", laypage);
                }
            }
        });
    }
}

$("body").on("click","#goback_purchaseorderlist_btn",function(){
    $("#purchase_list").css("display", "block");
    $(".order_detail_alert").css("display", "none");
});

//查看详情
function showDetail(e) {
    detail_id = e;
    $("#purchase_list").css("display", "none");
    $(".order_detail_alert").css("display", "block");

    var params = {id: e};
    ajax_post("/purchase/getOrderById",JSON.stringify(params),"application/json",function(data){
        if (data.returnMess.errorCode == "0") {
            $(".product-list-detail").empty();
            var order = data.orders[0];
            $("#orderDate_").text(order.sOrderDate);
            if (order.status == 1){
                $("#payDate_").text(order.spayDate);
            }
            $("#detail_no").text(order.purchaseOrderNo);
            var statusMes = order.statusMes
            if(order.status==4 || order.status==6)
                statusMes = "待确认"
            $("#detail_stat").text(statusMes);
            if (order.deductionAmount) {
                $("#deductionAmount").text(parseFloat(order.deductionAmount).toFixed(2)).parent().show();
            } else {
                $("#deductionAmount").parent().hide();
            }
            if (order.status != 0) {
                $(".ma-pay").css("display", "none");
            }
            if(order.reason){
                $("#reason").attr("title",order.remark).text(order.reason).parent().show();
            } else {
                $("#reason").parent().hide();
            }
            //内外部分销商，区分，内部分销商只支持余额支付，外部分销商可以多种支付方式
            if(distributorType == 3){
                $(".ma-pay").attr("onclick", "gopay('" + order.purchaseOrderNo + "')");
            }else{
                //一键代发
                $(".ma-pay").attr("onclick","initPaymentBox('" + order.purchaseOrderNo + "')");
            }
            //用户备注信息
            $(".customer-remarks #remarkTime").text(order.sorderDate);
            $(".customer-remarks #remark").text(deal_with_illegal_value(order.remarks));
            var details = order.details;
            //合并数量集合
            var mergeMap = {};
            for (var i in details) {
                $.ajax({url: "/product/api/getUrl?sku=" + details[i].sku, type: "get", async: false,
                    success: function (data) {
                        var key = details[i].sku +"_"+ details[i].warehouseId + "_" + details[i].isgift;
                        var img_p_url = "../../img/IW71-4-1a3c.jpg";
                        if (data) {
                            img_p_url = data;
                        }
                        // 单价
                        var purchasePrice = details[i].purchasePrice
                        var partHtml = "<tr>" +
                            "<td style='width:10%'>" +
                                "<a target='_blank' href='../../product/product-detail.html?sku="+details[i].sku+"&warehouseId="+details[i].warehouseId+"' " +
                                    "class='title_"+details[i].iid+"' style='display: inline-block;'>" +
                                    "<img src='" + urlReplace(img_p_url, details[i].sku,"",80,80,100) + "'/ style=\"width:80px;height:80px;border:1px solid #ccc;  vertical-align: middle;\">" +
                                "</a>" +
                            "</td>" +
                            "<td style='width:20%'>" +
                                "<a target='_blank' href='../../product/product-detail.html?sku="+details[i].sku+"&warehouseId="+details[i].warehouseId+"' " +
                                "class='title_"+details[i].iid+"' style='display: inline-block;'>" + details[i].productName + (details[i].isgift?"<em class='red'>【赠】</em>":"") + "</a>" +
                            "</td>" +
                            "<td style='width:15%'>" + details[i].sku + "</td>" +
                            "<td style='width:15%'>" + deal_with_illegal_value(details[i].warehouseName) + "</td>" +
                            "<td style='width:13%'>" + purchasePrice.toFixed(2) + "</td>"

                        if(mergeMap[key]){
                            partHtml += "<td style='width:13%'>"+mergeMap[key].qty+"</td>" +
                                "<td style='width:14%'>"+(purchasePrice * mergeMap[key].qty).toFixed(2)+"</td>" +
                                "</tr>"
                            mergeMap[key].part = partHtml;
                        }else{
                            partHtml += "<td style='width:13%'>"+details[i].qty+"</td>" +
                                "<td style='width:14%'>"+(purchasePrice * details[i].qty).toFixed(2)+"</td>" +
                                "</tr>"
                            mergeMap[key] = {
                                qty: details[i].qty,
                                part: partHtml
                            };
                        }
                    }
                });
            }
            $.each(mergeMap,function(key,value){
                $(".product-list-detail").append(value.part);
            });
            var bbcPostage = order.bbcPostage;
            var totalPrices  = order.purchaseTotalAmount + (bbcPostage?bbcPostage:0);
            var couponsCode =  order.couponsCode;
            var couponsAmount = order.couponsAmount;
            var actually_paid = totalPrices;
            //计算总价公式
            var caHtml =
                '<div id="price_msg">' +
                '<p class="total-price">商品总计：' +
                '<span>'+(order.orderProTotal?parseFloat(order.orderProTotal).toFixed(2):0.00)+'</span>元';
            if(order.reducePrice){
                caHtml += '&nbsp;&nbsp;-&nbsp;&nbsp;' +(order.purchaseType == 1?"活动优惠":"整单优惠")+
                    '<span style="color: #EF5B5B;font-size: 22px;">'+parseFloat(order.reducePrice).toFixed(2)+'</span>元';
            }
            if(couponsCode){
                caHtml += '&nbsp;&nbsp;-&nbsp;&nbsp;优惠码' +
                    '<span style="color: #EF5B5B;font-size: 22px;">'+parseFloat(couponsAmount).toFixed(2)+'</span>元';
                actually_paid -= couponsAmount;
                $("#couponsCode").text(couponsCode);
            }
            if(bbcPostage != null){
                caHtml += '&nbsp;&nbsp;+&nbsp;&nbsp;运费' +
                    '<span style="color: #EF5B5B;font-size: 22px;">'+parseFloat(bbcPostage).toFixed(2)+'</span>元</p>';
            }
            if(actually_paid <= 0){
                actually_paid = 0.00;
            }
            caHtml += '</div>';
            $("#price_msg").remove();
            $(".product-list-detail").after(caHtml);
            $("#detail_total").text(parseFloat(actually_paid).toFixed(2));
        } else {
            layer.msg(data.returnMess.errorInfo, {icon: 1, time: 2000});
        }
    });
}

// 去支付
function gopay(purpo) {
    window.location.href = "../../product/paid-sure.html?purno=" + purpo;
}

// 弹出框的付款
function initPaymentBox(purpo){
    isnulogin(function(email){
        var balanceUrl = "../../product/paid-sure.html?purno=" + purpo + "";
        var url = "/purchase/viewpurchase";
        var param = {
            email: email, pageSize: "10", pageCount: "0", seachFlag: purpo
        };
        ajax_post_sync(url,JSON.stringify(param),"application/json",function (data) {
            if(data.returnMess.errorCode == "0"){
                var list = [];
                var details = {};
                var sameSku = {};
                var order = data.orders[0];
                purchaseType = order.purchaseType;
                $.each(order.details,function(i,ktem){
                    var kindqty = {};//临时记录正品数量，和发货数量
                    var item = {};
                    item.sku = ktem.sku;
                    item.qty = ktem.qty;
                    item.warehouseId = ktem.warehouseId.toString();
                    item.proqty = ktem.qty;
                    if (sameSku[ktem.sku+"_"+ktem.warehouseId]){
                        delete details[ktem.sku+"_"+ktem.warehouseId];
                        item.qty += sameSku[ktem.sku+"_"+ktem.warehouseId].totalqty;
                        sameSku[ktem.sku+"_"+ktem.warehouseId].totalqty = item.qty;
                        if (!ktem.isgift) {
                            item.proqty += sameSku[ktem.sku+"_"+ktem.warehouseId].proqty;
                            sameSku[ktem.sku+"_"+ktem.warehouseId].proqty = item.proqty;
                        } else {
                            item.proqty = sameSku[ktem.sku+"_"+ktem.warehouseId].proqty;
                        }
                    } else {
                        kindqty.totalqty = item.qty;
                        if (!ktem.isgift) {
                            kindqty.proqty = item.qty;
                        } else {
                            kindqty.proqty = 0;
                        }
                        sameSku[ktem.sku+"_"+ktem.warehouseId] = kindqty;
                        item.proqty = sameSku[ktem.sku+"_"+ktem.warehouseId].proqty;
                    }
                    details[ktem.sku+"_"+ktem.warehouseId] = item;
                });
                var products = [];
                for (var key in details){
                    products.push(details[key]);
                }
                var res = true;//商品是否缺货标识
                var resMsg = "";
                // TODO -------------仓库接口，需要进行对接--------------
                // ajax_post_sync("/inventory/ivyChk",JSON.stringify({"pros": products, "totalCheck": 1}),
                //     "application/json",
                //     function(ivyChkRes){
                //         if(ivyChkRes.code){
                //             layer.msg(ivyChkRes.msg, {icon: 0});
                //             return;
                //         }
                //         for (var i = 0; i < ivyChkRes.length; i++) {
                //             //某单品总仓不足
                //             var sendqty = ivyChkRes[i].sendoutTotalQty;//发出的数量
                //             var cloudqty = ivyChkRes[i].totoalStock;//云仓数量
                //             if (cloudqty < sendqty) {
                //                 if (cloudqty >= details[ivyChkRes[i].sku+"_"+ivyChkRes[i].warehouseId].proqty) {
                //                     var param = {};
                //                     param.warehouseId = ivyChkRes[i].warehouseId;
                //                     param.sku = ivyChkRes[i].sku;
                //                     param.qty = cloudqty - details[ivyChkRes[i].sku+"_"+ivyChkRes[i].warehouseId].proqty//表示剩余的赠品数量
                //                     param.isgift = true;
                //                     list.push(param);
                //                 } else {
                //                     resMsg += ivyChkRes[i].sku + " ";
                //                     res = false;
                //                 }
                //             }
                //         }
                //     }
                // )
                // if (!res) {
                //     layer.msg(resMsg + "总仓库存不足，无法进行支付，请稍后再试", {icon: 0});
                //     return;
                // }
                //change by zbc 支付前先锁库
                orderLock(purpo,function(red){
                    res = red;
                });
                if (!res) {
                    return;
                }
                var id = order.id;
                var bbcPostage = order.bbcPostage;
                var couponsAmount = order.couponsAmount;
                var sumPrice = parseFloat(order.purchaseDiscountAmount+(bbcPostage?bbcPostage:0)).toFixed(2);
                sumPrice = parseFloat(sumPrice - (couponsAmount?couponsAmount:0)).toFixed(2);
                var productName = encodeURIComponent(order.details[0].productName);
                var p = [{"name":productName}];
                var shengpayUrl = "";
                var alipayUrl = "../../payment/alipay.html?purno="+id+"&tradeNo="+purpo+"&productName="+encodeURIComponent(productName)+"&sumPrice="+sumPrice;
                var weinParam = {
                    id:id,
                    orderNo: purpo,
                    orderDes: order.details[0].productName,
                    totalPrice: sumPrice
                }
                var   forword_url = "/product/pay-success.html?isok=true&transamount="+sumPrice+"&od="+purpo;
                weinParam.forword_url = forword_url;
                var wxpayUrl = "javascript:pur_loadweixinPay("+JSON.stringify(weinParam)+");";
                // var wxpayUrl = "../../payment/wechat.html?one="+id+"&two="+purpo+"&three="+encodeURIComponent(productName)+"&four="+sumPrice;
                var yjpayUrl = "../../payment/yijipay.html?one="+id+"&two="+purpo+"&three="+sumPrice+"&four="+encodeURIComponent(encodeURIComponent("采购单支付")) + "&five=ONLINEBANK&six=PC";
                //yjfWxUrl = "../../payment/yjfWx.html?one=" + id + "&two=" + purpo + "&three=" + sumPrice + "&four=" + encodeURIComponent(JSON.stringify(p)) + "&service=commonWchatTradeRedirect";
                yjfWxUrl = "../../payment/yijipay.html?one="+id+"&two="+purpo+"&three="+sumPrice+"&four="+encodeURIComponent(encodeURIComponent("采购单支付")) + "&five=THIRDSCANPAY&six=PC";
                p_paymentType(purpo,alipayUrl, wxpayUrl, yjpayUrl,balanceUrl,shengpayUrl,list,id)
            }
        })
    });
}

//选择支付方式
function p_paymentType(tradeNo,alipayUrl, wxpayUrl, yjpayUrl,balanceUrl,shengpayUrl,list,id) {
    //判断是采购或者发货
    var purpose = $("#purchaseNo"+tradeNo).attr("type") == 2?3:2;
    var payHtml = '<option value="">请选择支付方式</option>' ;
     ajax_get("/member/method?purpose="+purpose,"","",function(data){
        if(data.suc){
            $.each(data.list,function(i,item){
                payHtml += '<option value="'+item.key+'">'+item.name+'</option>';
            });      
        }else{
            payHtml += '<option value="balance">余额支付</option>';
        }
    });
    layer.open({
        type: 1,
        title: '支付方式',
        area:['450px','270px'],
        content:
        '<span style="margin-left: 47px;margin-top: 21px;" class="red reminder">'+
        '</span>'+
        '<div id="select_pay_method_dialog" class="all_pop select_pay_method_dialog">' +
        '<p>' +
        '<span>选择支付方式：</span>'+
        '<select>' +payHtml +'</select>' +
        '</p>' +
        '<p style="display: none;" class="display cash-money">' +
        '<span>支付金额：</span>' +
        '<input style="width: 253px;" type="text">&nbsp;&nbsp;元' +
        '</p>'+
        '</div>',
        move: false,
        btn: ['确定', '取消'],
        success: function(layero, index){
            $("#select_pay_method_dialog select").change(function(){
                if ($(this).val() == "cash") {
                    $(".cash-money").show();
                } else {
                    $(".cash-money").hide();
                }
            });
            if (list && list.length > 0) {
                var resMsg = "";
                $.each(list,function(i,item) {
                    resMsg += item.sku + " ";
                });
                $(".reminder").html("赠品"+resMsg + "缺货,要继续支付吗？");
            }
        },
        yes: function() {
            $(".layui-layer-btn0").hide();
            var mode = $("#select_pay_method_dialog select").val();
            var flag  = changeGiftqty(list,id);
            if (!flag) {
                $(".layui-layer-btn0").show();
                return;
            }
            if (mode == "zhifubao") {
                layer.closeAll();
                console.log("alipayUrl--->" + alipayUrl);
                window.open(alipayUrl, "_blank");
                query_payment_result_url_one = "/payment/qryAlipayResult";
                p_confirm_pay_result_dialog(tradeNo,alipayUrl, wxpayUrl, yjpayUrl,balanceUrl,shengpayUrl,list,id);
            } else if (mode == "weixin") {
                layer.closeAll();
                console.log("wxpayUrl--->" + wxpayUrl);
                window.location.href = wxpayUrl;
            } else if (mode == "easy") {
                layer.closeAll();
                console.log("yjpayUrl--->" + yjpayUrl);
                window.open(yjpayUrl, "_blank");
                query_payment_result_url_one = "/payment/yijipay/getpayresult";
                p_confirm_pay_result_dialog(tradeNo,alipayUrl, wxpayUrl, yjpayUrl,balanceUrl,shengpayUrl,list,id);
            } else if (mode == "balance"){
                layer.closeAll();
                window.open(balanceUrl, "_blank");
                console.log("balanceUrl--->" + balanceUrl);
                p_confirm_pay_result_dialog(tradeNo,alipayUrl, wxpayUrl, yjpayUrl,balanceUrl,shengpayUrl,list,id);
            } else if (mode == "cash"){//现金支付
                moneyPay(id,tradeNo);
            } else if (mode == "easy-wx"){
                layer.closeAll();
                window.open(yjfWxUrl, "_blank");
                console.log("yjfWxUrl--->" + yjfWxUrl);
                p_confirm_pay_result_dialog(tradeNo,alipayUrl, wxpayUrl, yjpayUrl,balanceUrl,shengpayUrl,list,id);
            } else if (mode == "cash-online") {
                layer.closeAll();
                window.location.href = "../../product/paid-online.html?purno=" + tradeNo;
            } else {
                query_payment_result_url_one = "";
                layer.msg("请选择支付方式", {icon: 0, time: 1000});
                $(".layui-layer-btn0").show();
            }
        }
    });
}

//选择现金支付方式的处理
function moneyPay(purchaseId,tradeNo){
    var cash = $(".cash-money input").val();
    var reg = /^([1-9][\d]{0,7}|0)(\.[\d]{1,2})?$/;
    if (!reg.test(cash)) {
        layer.msg('请输入正确交易金额', {icon : 2, time : 2000});
        $(".layui-layer-btn0").show();
        return;
    }
    var param = {
        "purchaseOrder":{
            "id":purchaseId,
            "offlineMoney":parseFloat(cash).toFixed(2),
            "status":4
        }
    };
    ajax_post_sync("/purchase/cash",JSON.stringify(param),"application/json",
        function(data){
            // 用户未登录
            if(data.code == 101){
                layer.msg(data.msg, {icon: 1, time: 2000}, function(){
                    window.location.href = "/personal/login.html"
                });
                return
            }
            if (data && data.errorCode == 0) {
                layer.msg('更新订单信息成功！', {icon : 1, time : 2000},function(){
                    $("#td_"+tradeNo).empty();
                    $("#status_"+tradeNo).text("待审核");
                    layer.closeAll();
                });
                return;
            }
            layer.msg('更新订单相关信息失败', {icon : 2, time : 2000});
            $(".layui-layer-btn0").show();
        },
        function (XMLHttpRequest, textStatus) {}
    );
}

function changeGiftqty(list,id){
    var flag = true;
    if (list.length > 0) {//说明此订单存在正价商品库存充足，赠品不足（5），但是云仓有多余的（3）
        ajax_post_sync("/purchase/batchUpdateOrders", JSON.stringify({"orderid" : id, "skus" : list}),
            "application/json",
            function(data){
                if(data && data.suc) {
                } else {
                    layer.msg(data.msg, {icon: 0, time: 2000});
                    flag = false;
                }
            },function (xhr, status) {
                flag = false;
            }
        );
    }
    return flag;
}

//load 微信支付弹出窗
function pur_loadweixinPay(param){
    var winxinHtml = $('#winxin_content');
    winxinHtml.load("../payment/wechat.html", function (response, status, xhr) {
        require(["wechat"], function (wechat) {
            $('.modal').fadeIn(300);
            var url = param.forword_url;
            init_win_payment(param,url,layer);
        });
    });
}

//确认支付结果弹出框
function p_confirm_pay_result_dialog(tradeNo,alipayUrl, wxpayUrl, yjpayUrl,balanceUrl,shengpayUrl,list,id) {
    layer.open({
        type: 1,
        title: '确认支付结果',
        content: '',
        move: false,
        btn: ['支付成功', '选择其他支付方式'],
        area: ["400", "140"],
        btn1: function() {
            layer.msg('订单校验中，请稍后查询。', {icon: 6, time: 3000});
            layer.closeAll();
            //支付成功
            window.location.href = "../../personal/personal.html";
        },
        btn2: function() {
            layer.closeAll();
            p_paymentType(tradeNo,alipayUrl, wxpayUrl, yjpayUrl,balanceUrl,shengpayUrl,list,id);
        }
    });
}

//再次购买
function buyAgain(purno) {
    isnulogin(function (email) {
        var param = {
            email: email, pageSize: 10, pageCount: 0, seachFlag: purno
        };
        ajax_post("/purchase/viewpurchase",JSON.stringify(param),"application/json",function (data) {
            if (data.returnMess.errorCode == "0") {
                var details = data.orders[0].details;
                var pParams = [];
                var dParams = {};
                $.each(details,function(j,node){
                    if(node.isgift){
                        return false;
                    }
                    var paramData = {
                        "skuList": [node.sku],
                        "warehouseId": node.warehouseId,
                        "publicImg" : node.productImg,
                        "iqty" : node.qty
                    };
                    $.ajax({
                        url: "/cart/pushCart",
                        type: "post",
                        dataType: "json",
                        contentType: "application/json",
                        data: JSON.stringify(paramData),
                        async : false,
                        success: function (response) {
                        }
                    });
                });
                window.location.href = "../cart/shop-cart.html";
            }
        })
    });
}

//采购订单的下拉
function purchaseLength(obj){
    var LGH = $('.purchase-table table');
    for(var i = 0 ;i <= LGH.length;i ++){
        if($(LGH[i]).find('.three-2-tr2').length < 6){
            $(LGH[i]).parents('.purchase-list').find('.searchUD').hide();
        }else{
            $(LGH[i]).parent().height(540);
        }
    }
};

function downSilder(obj) {
    var payH = $(obj).parents(".purchase-list").find(".purchase-table table").height();
    if ($(obj).find("em").hasClass("upBAK") == false) {
        $(obj).parents(".purchase-list").find(".purchase-table").animate({height: payH})//支付页面的
    } else {
        $(obj).parents(".purchase-list").find(".purchase-table").animate({height: 540})
    }
    $(obj).find("em").toggleClass("upBAK");
    $(obj).find(".attMs").toggle();
    $(obj).find(".attLs").toggle();
}