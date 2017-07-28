require.config({
    baseUrl: "../js/",
    paths: {
        "jquery": "lib/jquery-1.11.3.min",
        "layer": "lib/layer2.0/layer",
        "common": "common",
        "session": "personal/jquerysession",
        "wechat":"payment/wechat",
        "qrcode":"payment/qrcode",
        "sure":"product/order_sure",
        "scroll":"lib/mCustomScrollbar/jquery.mCustomScrollbar.min"
    },
    shim: {
        "layer": {
            exports: "",
            deps: ["jquery"]
        },
        "common": {
            exports: "",
            deps: ["jquery","scroll"]
        },
        "session": {
            exports: "",
            deps: ["jquery"]
        },
         "wechat": {
            exports: "",
            deps: ["jquery","common","session","layer","qrcode"]
        },
    }
});

var layer,payFlag = false, gift=[],activities,flag = true,payParam = {},cancelFlag = false,comsumerType = 1;//分销商类型，默认是1（普通）
require(["jquery", "layer", "common", "session"], function ($, laye) {
    layer = laye;

    $(function () {
        $(".promotion-code").show();
        isnulogin(function (email) {
            if ("0" == email) {
                window.location.href = "../../personal/login.html";
            } else {
                $.ajax({
                    url: "/member/infor?" + Math.random(),
                    type: "get",
                    dataType: "json",
                    async: false,
                    success: function (u) {
                        comsumerType = u.comsumerType;
                        var purno = GetQueryString("purno");
                        if (purno != null || purno != undefined) {
                            var url = "/purchase/viewpurchase";
                            var param = {
                                email: email,
                                pageSize: "10",
                                pageCount: "0",
                                seachFlag: purno
                            };
                            $.ajax({
                                url: url,
                                type: "post",
                                dataType: "json",
                                contentType: "application/json",
                                data: JSON.stringify(param),
                                success: function (data) {
                                    $(".product_table").empty();
                                    if (data.returnMess.errorCode == "0") {
                                        var order = data.orders[0];
                                        $("#purchaseNo").text(order.purchaseOrderNo);
                                        $("#purchaseNo").data("orderid",order.id);
                                        $(".customer-remarks #remarkTime").text(order.sorderDate);
                                        $(".customer-remarks #remark").text(deal_with_illegal_value(order.remark));
                                        // 缺货采购单 包含 完税商品需要支付运费
                                        var bbcPostage = order.bbcPostage;
                                        var couponsAcount = order.couponsAmount;
                                        var purchaseTotalAmount = order.purchaseTotalAmount;
                                        var totalAmount = order.purchaseTotalAmount + (bbcPostage ? bbcPostage : 0);
                                        //内部分销商在客户订单下的采购单
                                        if (comsumerType == 3 && order.purchaseType == 2) {
                                            $("#totalAmount").text(order.salesAmount);
                                        } else {
                                            if (couponsAcount) {
                                                if (totalAmount > couponsAcount) {
                                                    totalAmount -= couponsAcount;
                                                } else {
                                                    totalAmount = 0;
                                                }
                                            }
                                            $("#totalAmount").text(fmoney(totalAmount, 2));
                                        }
                                        if (bbcPostage) {
                                            $(".paid_logistics_mode").text(order.logisticsMode);
                                            $(".bbc_postage").text(bbcPostage);
                                            $(".paid-bbcpostfee").show();
                                        }
                                        var length = order.details.length;
                                        var h = length > 5 ? 450 : length*90;
                                        var paramHtml = "<div id='product_content' class='order-sure-product' style='height: "+h+"px;' >" +
                                            "<table class='product_content'>";
                                        for (var i in order.details) {
                                            var detail = order.details[i];
                                            paramHtml += "<tr>" +
                                                "<td id='product_descrption'>" +
                                                "<img src='" + detail.productImg + "' />" +
                                                "<p>" +"（"+detail.sku+"）" + detail.productName + "</p></td>" +
                                                "<td>" + detail.purchasePrice + "</td>" +
                                                "<td>" +
                                                "<ul class='cart_num'><li id='cart_nums'>" + detail.qty + "</li></ul>" +
                                                "</td>" +
                                                "<td>" + detail.totalPrices + "</td>" +
                                                "</tr>";
                                        }
                                        if (length > 2) {
                                            paramHtml += "</table>" +
                                                "</div>" +
                                                "<div class='searchUD' onclick='downSilder2(this)'>" +
                                                "<b>" +
                                                "<span class='attMs' style='display: inline;'>" +
                                                "<span>显示更多∨</span>" +
                                                "</span>" +
                                                "<span class='attLs' style='display: none;'>上拉∧</span>" +
                                                "<em></em></b></div>";
                                        } else {
                                            paramHtml += "</table></div>";
                                        }
                                        $(".product_table").append(paramHtml);
                                        if($('.product_content').find('tr').length <= 5){
                                            $('.searchUD').hide();
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        var payHtml = "";
        ajax_get("/member/method?purpose=2","","",function(data){
            if(data.suc){
                $.each(data.list,function(i,item){
                    payHtml += 
                        '<div class="'+item.key+'-pay" data-type="'+item.key+'">'+
                        '<label><i></i></label>'+
                        '<p><span></span><i>'+item.name+'</i></p>'+
                        '</div>';
                });      
            }else{
                payHtml += 
                    '<div class="balance-pay" data-type="balance">'+
                    '<label><i></i></label>'+
                    '<p><span></span><i>余额支付</i></p>'+
                    '</div>';
            }
        });
        payHtml +=  
            '<span class="settle_accounts">'+
            '<p class="paid-bbcpostfee">'+
            '运送方式：'+
            '<span class="paid_logistics_mode"></span>'+
            '运费：￥'+
            '<span class="bbc_postage" style="color: #4a7cc7;"></span>'+
            '<font style="color:#4a7cc7 ;font-size:14px"> (包含了此客户订单商品的所有运费)</font>'+
            '</p>'+
            '<button id="paid">去支付</button>'+
            '</span>';
         $(".pay-methods").append(payHtml);
            $(".pay-methods > div").click(function () {
            $(this).addClass("pay-methods-icon").siblings().removeClass("pay-methods-icon");
        });
        $("#paid").click(function () {
            //检查库存 然后支付
            var tradeNo = $("#purchaseNo").text();
            init_payment(tradeNo);
        });
    });

    // 支付start
    function init_payment(tradeNo) {
        isnulogin(function (email) {
            if ("0" == email) {
                window.location.href = "../../personal/login.html";
            } else {
                var pay_type = $(".pay-methods-icon");
                if (pay_type.length == 0) {
                    layer.msg("请选择支付方式", {icon: 0, time: 2000});
                    return;
                }
                var type = pay_type.data("type");
                //库存检查
                ajax_post_sync("/purchase/getDeByNo",JSON.stringify({"pno": tradeNo}),"application/json",function (data) {
                    var res = true;//商品是否缺货标识
                    //change by zbc 支付前先锁库
                    orderLock(tradeNo,function(red){
                        res = red;
                    });
                    if (!res) {
                        return;
                    }
                    var url = get_payment_url(email, tradeNo, type);
                    if (type == "balance") { //余额
                        window.location.href = "../product/paid-sure.html?purno=" + tradeNo;
                    } else if (type == "cash-online") { // 线下转账
                        window.location.href = "../product/paid-online.html?purno=" + tradeNo;
                    } else if (type == "cash"){ // 现金
                        moneyPay();
                    } else {
                        if (url) {
                            if(type == "weixin"){ // 微信
                                window.location.href = url;
                            }else{
                                window.open(url, "_blank");
                                confirm_pay_result_dialog();
                            }
                        } else {
                            layer.msg("获取支付方式失败", {icon: 0, time: 2000});
                        }
                    }

                })
            }
        });
    }
    // 支付end

    //根据单号与支付方式获取 支付URL
    function get_payment_url(email, purpo, type) {
        var url = "/purchase/viewpurchase";
        var param = {
            email: email,
            pageSize: "10",
            pageCount: "0",
            seachFlag: purpo
        };
        $.ajax({
            url: url,
            type: "post",
            dataType: "json",
            contentType: "application/json",
            async: false,
            data: JSON.stringify(param),
            success: function (data) {
                if (data.returnMess.errorCode == "0") {
                    var order = data.orders[0];
                    var id = order.id;
                    var bbcPostage = order.bbcPostage;
                    var couponsAmount = order.couponsAmount;
                    var sumPrice = parseFloat(order.purchaseDiscountAmount + (bbcPostage ? bbcPostage : 0)).toFixed(2);
                    sumPrice = parseFloat(sumPrice - (couponsAmount ? couponsAmount : 0)).toFixed(2);
                    var productName = encodeURIComponent(order.details[0].productName);
                    var p = [{"name": productName}];
                    var weinParam = {
                        id:id,
                        orderNo: purpo,
                        orderDes: order.details[0].productName,
                        totalPrice: sumPrice
                    }

                    var forword_url = "/product/pay-success.html?isok=true&transamount="+sumPrice+"&od="+purpo;
                    weinParam.forword_url = forword_url;
                    if (type == "zhifubao") { // 支付宝
                        url = "../../payment/alipay.html?purno=" + id + "&tradeNo=" + purpo + "&productName=" + encodeURIComponent(productName) + "&sumPrice=" + sumPrice;;
                    } else if (type == "weixin") { // 微信
                        url = "javascript:loadweixinPay("+JSON.stringify(weinParam)+");";;
                    } else if (type == "easy") { // 易极付
                        url = "../../payment/yijipay.html?one=" + id + "&two=" + purpo + "&three=" + sumPrice + "&four=" + encodeURIComponent(encodeURIComponent("审核支付")) + "&five=ONLINEBANK&six=PC";
                    } else if(type == "easy-wx"){ // 易极付-微信扫码
                        //url = "../../payment/yjfWx.html?one=" + id + "&two=" + purpo + "&three=" + sumPrice + "&four=" + encodeURIComponent(JSON.stringify(p)) + "&service=commonWchatTradeRedirect";;
                        url = "../../payment/yijipay.html?one=" + id + "&two=" + purpo + "&three=" + sumPrice + "&four=" + encodeURIComponent(encodeURIComponent("审核支付")) + "&five=THIRDSCANPAY&six=PC";
                    }else {
                        url = undefined;
                    }
                } else {
                    layer.msg(data.returnMess.errorInfo, {icon: 5, time: 2000});
                    url = undefined;
                }
            }
        });
        return url;
    }

    //选择现金支付方式的处理
    function moneyPay(){
        layer.open({
            type:1,
            title:'现金支付',
            area:['450px','230px'],
            content:$('.money_pop'),
            btn:['确定','取消'],
            yes: function(i, currdom) {
                $(".layui-layer-btn0").hide();
                var cash = $(".money_pop input").val();
                var reg = /^([1-9][\d]{0,7}|0)(\.[\d]{1,2})?$/;
                if (!reg.test(cash)) {
                    layer.msg('请输入正确交易金额', {icon : 2, time : 2000});
                    $(".layui-layer-btn0").show();
                    return;
                }
                var purchaseId =  $("#purchaseNo").data("orderid");
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
                            layer.msg('现金交易成功', {icon : 1, time : 2000},function(){
                                window.location.href = "/personal/personal.html?emnunum=15";
                            });
                            return;
                        }
                        layer.msg('更新订单相关信息失败', {icon : 2, time : 2000});
                        $(".layui-layer-btn0").show();
                    },
                    function (XMLHttpRequest, textStatus) {}
                );
            }
        })
    }

    function showCartData(email, mode) {
        ajax_get(
            "/cart/getcartdata?" + Math.random(),
            "",
            "application/json",
            function (response) {
                if (response.code) {
                    layer.msg(response.msg, {icon: 6, time: 3000});
                    return;
                }
                if (response.result == false) {
                    $(".freight").remove();
                }
                else {
                    var itemId = "";
                    var sum = 0;
                    $("#product_list tbody").empty();
                    for (var i = 0, len = response.cartData.length; i < len; i++) {
                        var val = response.cartData[i];
                        var itemId = val.itemId;
                        var sku = val.sku;
                        var price = val.disPrice;
                        var title = val.title;
                        var iqty = val.qty;
                        var publicImg = val.image;
                        var sumPrice = val.sumprice;
                        var selected = val.selected;
                        var warehouseId = val.warehouseId;//仓库暂时默认为1
                        var whName = val.storageName;
                        var batchnum = val.batchnum;
                        if (selected == false) {
                            continue;
                        }
                        sum += parseFloat(sumPrice);

                        var proHtml = '<tr data-whid="' + warehouseId + ' " data-ssku="' + sku + '">' +
                            '<td id="product_descrption" style = "width:40%">' +
                            '<a target="_blank" href="../../product/product-detail.html?sku=' + sku + '&warehouseId=' + warehouseId + '">' +
                            '<img  src="' + publicImg + '">' +
                            '</a>' +

                            '<p><a target="_blank" href="../../product/product-detail.html?sku=' + sku + '&warehouseId=' + warehouseId + '">' +
                            '' +'（'+sku+'）'+ title.substring(0, title.length - 5) + '</a></p>' +
                            '</td>' +
                            '<td class="cart-warehouse" style = "width:15%">' + whName + '</td>' +
                            '<td>￥<b>' + changeTwoDecimal_f(price) + '</b></td>' +
                            '<td>' +
                            '<ul class="cart_num">' +
                            '<li id="cart_nums">' +
                            '<span>' + iqty + '</span>'+
                            '</li>' +
                            '</ul>' +
                            '</td>' +
                            '<td style = "width:15%"><span class="red">￥<b>' + changeTwoDecimal_f(sumPrice) + '</b></span></td>' +
                            '</tr>';
                        $("#product_list tbody").append(proHtml);
                    }
                    $("#totolPrice").text(parseFloat(sum).toFixed(2));
                    $(".p-mainprice").text(parseFloat(sum).toFixed(2));

                    //采购列表加载完毕，添加确认按钮漂浮动作

                    //优惠码定位
                    $.fn.submitBtn = function () {
                        var closed = false;
                        var box = $('.promotion-code-box');
                        var pcd = $(".promotion-code");
                        $('.promotion-close').click(function () {
                            pcd.css({
                                'position': 'relative',
                                'width': box.width(),
                                'left': 0
                            });
                            closed = true;
                        });
                        if (box.offset().top > ($(window).height() + pcd.height())) {
                            pcd.css({
                                'position': 'fixed',
                                'width': box.width(),
                                'left': box.offset().left
                            })
                        }
                        var position = function () {
                            $(window).scroll(function () {
                                if (!closed) {
                                    var posTopBox = box.offset().top;
                                    if ($(this).scrollTop() >= posTopBox - $(window).height() + pcd.height()) {
                                        pcd.css({
                                            'position': 'relative',
                                            'width': box.width(),
                                            'left': 0
                                        });
                                    } else {
                                        if (pcd.css('position') !== 'fixed') {
                                            pcd.css({
                                                'position': 'fixed',
                                                'width': box.width(),
                                                'left': box.offset().left
                                            })
                                        }
                                    }
                                }
                            });
                        };
                        return $(this).each(function () {
                            position($(this));
                        });
                    };

                    $(".promotion-code").submitBtn();
                    activities = {};
                    if (mode == 1) {
                        promotion(response.activeInfo);
                    }
                }
            },
            function (XMLHttpRequest, textStatus) {
                layer.msg("显示购物车列表出错！", {icon: 2, time: 2000});
            }
        );
    }
    
    //add xuse
    function promotion(act){
        if(act && act.activePlvg) {
            //change by zbc
            show_promotion_data(act);
        } else {
            $(".promotion_console").hide();
        }
    }
    function show_promotion_data(data){
        $(".promotion_consoleR_content").empty();
        var pri = JSON.parse(data.activePlvg);
        $(".promotion_consoleL b").text(pri.proActName);
        var htmlCode = "";
        var plvgSum = pri.plvg.sum;
        var sum = parseFloat($("#totolPrice").text());
        $.each(pri.plvg.gift,function(i,item){
            htmlCode += 
                '<span>活动优惠：'+
                '<b class="red">[赠品]</b>'+
                '<span>'+item.cTitle+'</span>'+
                '<span>×'+
                '<em data-qty="'+ item.num +'" data.sku="'+item.sku+'" data-wid="'+item.warehouseId+'">'+item.num+'</em>'+
                '</span>'+
                '</span>'; 
        });
        if(Number(plvgSum) != Number(sum)) {
            htmlCode +=  '<p>'+
            '<span>购物车减价后优惠：'+
            '<b class="red">'+(-Number(sum) + Number(plvgSum)).toFixed(2)+'</b>元'+
            '</span>'+
            '</p>';
            sum = plvgSum;
        }
        $(".promotion_consoleR_content").prepend(htmlCode);
        $(".p-mainprice").text(parseFloat(sum)>0?parseFloat(sum).toFixed(2):0.00);
        $("#totolPrice").text(parseFloat(sum)>0?parseFloat(sum).toFixed(2):0.00);
    }

    //确认支付结果弹出框
    function confirm_pay_result_dialog() {
        layer.open({
            type: 1,
            title: '确认支付结果',
            content: '',
            move: false,
            btn: ['支付成功', '选择其他支付方式'],
            area: ["400", "140"],
            btn1: function () {
                layer.msg('订单校验中，请稍后查询。', {icon: 6, time: 3000});
                layer.closeAll();
                $.session.set('n', 15);
                window.location.href = "../../personal/personal.html";
            },
            btn2: function () {
                layer.closeAll();
            }
        });
    }
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
        isnulogin(function (email) {
            ajax_get(
                "/member/infor",
                "",
                "application/json",
                function (response) {
                    if (response.id != null && response.id != 'null' && response.id != '' && response.id != undefined) {
                        showCartData(email, response.distributionMode);
                        if (response.distributionMode != 1) {
                            $(".promotion_console").hide();
                        }
                    } else {
                        layer.msg("获取用户折扣出错！", {icon: 2, time: 2000});
                    }
                },
                function (XMLHttpRequest, textStatus) {
                    layer.msg("获取用户折扣出错！", {icon: 2, time: 2000});
                }
            );
        });
    });
});


 //订单支付页面产品列表下拉
function downSilder2(obj) {
    var liH = $(".product_content").height();
    var payH = $(obj).parents(".product_table").find(".product_content").height();
    if ($(obj).find("em").hasClass("upBAK") == false) {
        $(obj).parents(".product_table").find("#product_content").animate({height: payH});//支付页面的
    } else {
        $(obj).parents(".product_table").find("#product_content").animate({height: 450});
    }
    $(obj).find("em").toggleClass("upBAK");
    $(obj).find(".attMs").toggle();
    $(obj).find(".attLs").toggle();
    var paymentLileng = $(".product_table").find(".cartListUL");
    if (paymentLileng.length > 0) {
        $(".searchUD").show();
    }
}
//新 提交订单
function submitOrder(){
    var code = $(".promotion-input").val();
    if (code) {
        var flag = getCoupons();
        if (!flag) {
            return;
        }
    } else {
        $("#couponsFlag").val("false");
    }
    var orderData ={};
    var remarks = $("#remarks").val()
    if ($.trim(remarks)) {
        if(remarks.length>500){
            layer.msg("超出500个字符限制，请重新输入！", {icon: 2, time: 2000});
            return
        }
        orderData.remarks = remarks;
    }
    var couponsFlag = $("#couponsFlag").val();
    if (couponsFlag == "true") {
        orderData.couponsAmount = $("#couponsFlag").data("couponsCost");
        orderData.couponsCode = $("#couponsFlag").data("code");
    }
    $("#accounts").css('backgroundColor', '#ccc').attr('disabled', true);
    ajax_post("/cart/order",JSON.stringify(orderData),"application/json",
        function(res){
            if(res.suc){
                layer.msg("下单成功",{icon:6,time:2000},function(index){
                    layer.close(index);
                    if(res.code == 0 ){
                        window.location.href = "../product/balance-paid.html?purno=" + res.msg;
                    }else if(res.code == 3){
                        $.session.set('n', 15);
                        window.location.href = "../../personal/personal.html";
                    }
                });
            }else{
                layer.msg(res.msg,{icon:5,time:2000});
                $("#accounts").css('backgroundColor', '#117ad4').attr('disabled', false);
            }
        },function(e){
            layer.msg("下单异常",{icon:5,time:2000});
            console.log(e);
        }
    );
}


//计算总价格和总数量
function count() {
    var ipts = new Array(),
        sumNode = new Array(),
        tPrice = 0,
        quantity = 0;
    $.each($("#product_list tbody tr"), function (i, e) {
        var tds = $(e).find("td");
        ipts.push(tds.eq(3).find("input")[0]);
        sumNode.push(tds.eq(4)[0]);
    });
    if (ipts.length == 0) {
        $(".product_num").html(0);
        $(".all_price").html(changeTwoDecimal_f(0));
        $("#totolPrice").html(0);
        $(".p-mainprice").html(0);
    } else {
        for (var s = 0; s < ipts.length; s++) {
            quantity += parseFloat($(ipts[s]).val());

        }
        for (var i = 0; i < sumNode.length; i++) {
            tPrice += parseFloat($(sumNode[i]).children("span").find("b").text());

        }
        $("#totolPrice").html(parseFloat(tPrice).toFixed(2));
        var couponsCost = parseFloat($("#couponsCost").text()).toFixed(2);
        if (tPrice > couponsCost) {
            $(".p-mainprice").html(parseFloat(parseFloat(tPrice).toFixed(2) - couponsCost).toFixed(2));
        } else {
            $(".p-mainprice").html(0.00);
        }
    }
    if (quantity == "0") {
        $("#accounts").css('backgroundColor', '#ccc').attr('disabled', true);
    } else {
        $("#accounts").css('backgroundColor', '#117ad4').attr('disabled', false);
    }
}

 //验证优惠码有效性
function getCoupons() {
    var code = $(".promotion-input").val();
    var allPrice = $("#totolPrice").text();
    if (!code) {
        layer.msg("优惠码不能为空", {icon: 5, time: 2000});
        return;
    }
    var flag = false;
    ajax_get("/member/getCouponsInfo?couponsNo=" + code + "&orderAmount=" + allPrice, "", "",
        function (response) {
            if (response.suc) {
                var active = response.active;
                var couponsCost = active.couponsCost;
                var threshold_price = active.thresholdPrice;
                $("#threshold_price").text(threshold_price);
                $("#couponsCost").text(couponsCost);
                if (allPrice > couponsCost) {
                    $(".p-mainprice").text(parseFloat(allPrice - couponsCost).toFixed(2));
                } else {
                    $(".p-mainprice").text(0.00);
                }
                $("#couponsFlag").val(response.suc);
                $("#couponsFlag").data("couponsCost", couponsCost);
                $("#couponsFlag").data("code", code);
                $("#des_couponsCost").text(couponsCost);
                $(".des-promotion").show();
                flag = true;
            } else {
                $("#couponsFlag").val("false");
                $("#couponsCost").text(0.00);
                $(".p-mainprice").text(allPrice);
                layer.msg(response.info, {icon: 5, time: 2000});
                $(".des-promotion").hide()
                flag = false;
            }
        },
        function (xhr, status) {
            $("#couponsFlag").val("false");
            layer.msg(response.info, {icon: 2, time: 2000});
            flag = false;
        }
    );
    return flag;
}
//点击加减按钮增加商品数量
function totalPrice(obj, isAdd, itemId) {
    var quantity = 0, sourceQty = 0;
    var batch = $(obj).attr("batch");
    if (isAdd) {
        sourceQty = parseInt($(obj).prev().find("input").val());
        quantity = sourceQty + 1;
    } else {
        sourceQty = parseInt($(obj).next().find("input").val());
        quantity = sourceQty == 1 ? 1 : (sourceQty - 1);
    }
    if(quantity < batch) {
        $(obj).prev().find("input").val(batch);
        flag = true;
        layer.msg("购买数量必须大于或者等于商品起批量【" + batch + "】", {icon: 2, time: 2000});
        var params = {
            "itemId": itemId,
            "qty": batch
        };
        flag = true;
        check_isexceed_inventory(params, sourceQty, $(obj).parent());
        return;
    }
    var params = {
        "itemId": itemId,
        "qty": quantity
    };
    flag = true;
    check_isexceed_inventory(params, sourceQty, $(obj).parent());
}

var changeTwoDecimal_f

//微信支付
function loadweixinPay(param){
    var winxinHtml = $('#winxin_content');
    winxinHtml.load("../payment/wechat.html", function (response, status, xhr) {
        require(["layer","wechat","order_sure"], function (layer) {
            $('.modal').fadeIn(300);
            var url = param.forword_url;
            init_win_payment(param,url,layer);
        });
    });
}
 