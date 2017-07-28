require.config({
    baseUrl: "../js/",
    paths: {
        "jquery": "lib/jquery-1.11.3.min",
        "laypage": "lib/laypage1.3/laypage",
        "laydate": "lib/laydate/laydate",
        "layer": "lib/layer2.0/layer",
        "webuploader": "lib/webuploader/webuploader.min",
        "common": "common",
        "calendar": "personal/calendar",
        "myinfo": "personal/myinfo",
        "alterinfo": "personal/alterinfo",
        "mystore": "personal/mystore",
        "myaccount": "personal/myaccount",
        "purchase": "personal/purchase_list",
        "sales": "personal/sales",
        "transaction": "personal/transaction",
        "inventory": "personal/inventory",
        "apply": "personal/apply",
        "goods": "personal/purchase_goods",
        "alterphone": "personal/alterphone",
        "session": "personal/jquerysession",
        "orderimport": "personal/order_import",
        "generateOrder": "personal/generate_order",
        "lazyload": "lib/jquery.lazyload",
        "quotation" : "personal/quotation",
        "wechat":"payment/wechat",
        "qrcode":"payment/qrcode",
        "autobrowse":"lib/jquery.esn.autobrowse",
        "jsrorage":"lib/jstorage",
        "jqform" : "lib/jquery.form",
        "scroll":"lib/mCustomScrollbar/jquery.mCustomScrollbar.min"
    },
    shim: {
        "laypage": {
            exports: "",
            deps: []
        },
        "sales":{
            exports: "",
            deps: ["webuploader","jqform"]
        },
        //"laydate": {
        //    exports: "",
        //    deps: ["css!../css/laydate/skins/molv/laydate.css"]
        //},
        "layer": {
            exports: "",
            deps: ["jquery"]
        },
        "webuploader": {
            exports: "",
            deps: ["jquery"]
        },
        "common": {
            exports: "",
            deps: ["jquery","scroll"]
        },
        "calendar": {
            exports: "",
            deps: ["jquery"]
        },
        "autobrowse":{
            exports: "",
            deps: ["jquery","jsrorage"]
        },
        "alterinfo": {
            exports: "",
            deps: ["jquery", "calendar", "webuploader", "layer"]
        },
        "purchase": {
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        },
        "transaction": {
            exports: "",
            deps: ["jquery"]
        },
        "inventory": {
            exports: "",
            deps: ["jquery"]
        },
        "apply": {
            exports: "",
            deps: ["jquery","jqform"]
        },
        "goods": {
            exports: "",
            deps: ["jquery", "laypage", "layer","autobrowse"]
        },
        "session": {
            exports: "",
            deps: ["jquery"]
        },
        "lazyload": {
            exports: "",
            deps: ["jquery"]
        },
        "quotation": {
            exports: "",
            deps: ["jquery"]
        },
        "wechat": {
            exports: "",
            deps: ["jquery","common","session","layer","qrcode"]
        }
    }
});
var layer;
require(["jquery", "layer", "laypage", "common", "session", "lazyload"], function ($, lay, laypage) {
    //未登录直接跳转到登录页面
    layer = lay;
    //isnulogin(function(){});
    if (GetQueryString("emnunum") == null) {
        var n = 1;
        if (null != $.session.get('n')) {
            n = parseInt($.session.get('n'));
        }
        toggle_menu_content(n);
        //loading
        reload = true;
        if(!unLoad()){
            creatLoading(false, $(findActive(n)).attr('id'));
        }
    } else {
        toggle_menu_content(parseInt(GetQueryString("emnunum")));
        //loading
        reload = true;
        creatLoading(false, $(findActive(parseInt(GetQueryString("emnunum")))).attr('id'));
    }

//右侧购物车悬浮定位
    var minWidth = 1300;
    var perLeft;
    var scrolls;
    var perWidth = $('.personal').width();
    var leftMenuTop = 210;
    $.fn.smartFloat = function () {
        //alert("调用了");
        var position = function (element) {
            var pos = element.css("position"), right = -48;
            $(window).scroll(function () {
                scrolls = $(this).scrollTop();
                perLeft = $('.personal').offset().left;
                if ($(window).width() > minWidth) {
                    if (scrolls > leftMenuTop) {
                        if (window.XMLHttpRequest) {
                            element.css({
                                position: "fixed",
                                top: 10,
                                left: perWidth + perLeft + 15
                            });
                        } else {
                            element.css({
                                top: scrolls
                            });
                        }
                    } else {
                        element.css({
                            position: "absolute",
                            left: perWidth + 15,
                            top: 10
                        });
                    }
                }
            });
        };
        return $(this).each(function () {
            position($(this));
        });
    };

    $(window).resize(function () {
        if ($('.personal').offset()) {
            perLeft = $('.personal').offset().left;
        }
        if ($(window).width() < minWidth) {
            $(".float-addcart").hide();
        } else if ($(window).width() > minWidth && scrolls > leftMenuTop) {
            $(".float-addcart").show().css({
                left: perWidth + perLeft + 15
            });
        } else if ($(window).width() > minWidth && scrolls < leftMenuTop) {
            $(".float-addcart").show()
        }
    });

    $(document).ready(function () {
        if ($('.personal').offset()) {
            perLeft = $('.personal').offset().left;
            //固定首页右边购物车、客服、返回顶部导航
            $(".float-addcart").smartFloat();
        }
    });

    var footerHeight = $('#div_footer').height();
    var boxLeftHeight = $(".box-left").height();
    //左侧菜单不随右侧内容滚动而滚动
    function navPosition() {
        var scrollLength = $(window).scrollTop();
        var bodyHeight = $('body').height();
        if (scrollLength >= leftMenuTop && bodyHeight - scrollLength - boxLeftHeight >= footerHeight) {
            $(".box-left").css({
                "position": "fixed",
                "top": "0",
                "z-index": "999"
            });
            $(".personal-content").children("div[class^='box-right-']").css({
                "margin-left": "160px"
            });
        } else if (scrollLength < leftMenuTop && bodyHeight - scrollLength - boxLeftHeight >= footerHeight) {
            $(".box-left").removeAttr("style");
            $(".personal-content").children("div[class^='box-right-']").css({
                "margin-left": "0"
            });
        } else if (scrollLength >= leftMenuTop && bodyHeight - scrollLength - boxLeftHeight < footerHeight) {
            $(".box-left").css({
                "position": "fixed",
                "top": $(window).height() - boxLeftHeight - footerHeight,
                "z-index": "999"
            });
            $(".personal-content").children("div[class^='box-right-']").css({
                "margin-left": "160px"
            });
        } else if (scrollLength == 0) {
            $('.box-left').removeAttr('style');
        }
    }

    $(window).scroll(function () {
        navPosition();//调用nav侧导航固定方法
    });

    //绑定click增加loading动画
    $('.box-left').on('click', 'li', function (event) {
        reload = true;
        var e = event.target || event.srcElement; //寻找事件源
        var tarId = $(e).attr('id');
        $('.change').hide();
        creatLoading(false, tarId);
    });
});

//定义loading使用的全局变量
var theDiv;
var activeDiv;
var reload = false; //判断是否需要右边Box重新展示用
//创建及删除loading盒子
function creatLoading(isClose, obj, isReload) {
    //if(isReload){reload = true}
    var pers = $('.personal');
    if (reload) {
        if (isClose) {
            setTimeout(function () {
                $('.proscenium-loading').remove();
                $('.change').hide();//重新关闭所有框，以免选择选项过快导致content部同时出现两个div
                $('.' + obj).fadeIn('fast');
                return $("img.bbclazy").lazyload({
                    effect: 'fadeIn',
                    threshold: '100'
                });
            }, 500);

            reload = false;
        } else {
            $('body').append("<div class='proscenium-loading'><img src='../../img/tloading.gif' style='width:150px'></div>");
            if ($(window).width() < 1235) {
                $('.proscenium-loading').css({
                    left: '605px',
                    top: (pers.offset().top == 0 ? 140 : pers.offset().top) + 150 + 'px',
                    'z-index': 100000
                })
            }
            else {
                if (pers.offset()) {
                    $('.proscenium-loading').css({
                        left: pers.offset().left + 605 + "px",
                        top: (pers.offset().top == 0 ? 140 : pers.offset().top) + 150 + 'px',
                        'z-index': 100000
                    })
                }
            }
        }
    }
    return theDiv = obj;
}

function lyLoad() {
    $("img.bbclazy").lazyload({
        effect: 'fadeIn',
        threshold: '100'
    });
}

function transferStation(isCreat, goal) {
    reload = true;
    creatLoading(isCreat, goal);
}

//找到刷新网页后需要跳转的页面的序号
function findActive(n) {
    var word;
    var lisAttr = [];
    var lis = $('.box-left').find('li');
    for (var i = 0; i < lis.length; i++) {
        word = $(lis[i]).attr('onclick');
        lisAttr.push(word.substring(word.indexOf('(') + 1, word.indexOf(')')).trim());
    }
    return lis[lisAttr.indexOf(n.toString())];
}

//获取请求地址后的参数
function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null)return unescape(r[2]);
    return null;
}

//动态加载相应菜单内容
var bufferFlag = false;//缓存标识
function toggle_menu_content(n,mark) {
    if (bufferFlag) {
        bufferMemory();
        bufferFlag = false;
    }
    //初始化左侧导航栏悬浮状态
    $('.box-left').removeAttr('style');

    n = n == 0 ? 1 : n;
    //此处请过滤掉非菜单内容加载
    if (n < 11 || n == 15 || n == 16 || n == 19 || n == 18) {
        var menu_id = $(".active").attr("id");
        $("." + menu_id).empty();
        $("." + menu_id).hide();
        $(".active").removeClass();
        $.session.set('n',n);
    }
    //删除laydate创建的div
    if ($("#laydate_box")[0] != undefined) {
        $("#laydate_box").remove();
    }

    //校验，拦截冻结账户不给访问
    if([3,4,5,15].indexOf(n) != -1){
        if(!isNotFrozen()){
            return;
        }
    }

    switch (n) {
        case 1:
            $(".box-right-one").load("myaccount.html", function(response, status, xhr) {
                $("title").text("欢迎页 - TOMTOP Supply Chain");
                require(["myaccount"], function(myaccount) {
                    myaccount.checkFrozen();
                });
                $("#box-right-one").addClass("active");
            });
            break;
        case 2:
            $(".box-right-two").load("myinfo.html", function(response, status, xhr) {
                $("title").text("我的资料 - TOMTOP Supply Chain");
                require(["myinfo"], function(myinfo) {
                    myinfo.init_information();
                    $("#box-right-two").addClass("active");
                });
            });
            break;
        case 3:
            $(".box-right-three").load("purchase.html", function(response, status, xhr) {
                $("title").text("创建微仓进货 - TOMTOP Supply Chain");
                $("#purchase_list").css("display","block");
                $(".order_detail_alert").css("display","none");
                require(["laypage", "layer", "goods"], function(laypage,layer) {
                    init_goods(laypage,layer);
                    init_catgs();
                    $("#seachInput").keydown(function(e){
                        if(e.keyCode==13){
                            viewGoods(null,false);
                        }
                    });
                    $("#box-right-three").addClass("active");

                });
            });
            break;
        case 4:
            $(".box-right-four").load("market.html", function(response, status, xhr) {
                $("title").text("创建终端订单 - TOMTOP Supply Chain");
                require(["layer","laypage","sales"], function(layer,laypage) {
                    initParam(layer,laypage);
                    saleOrderList();
                    initAreaSel();
                    //时间控件
                    $(".box-right-four").on("click","#orderingDate",function(){
                        laydate({istime: true, format: 'YYYY-MM-DD hh:mm:ss'});
                    });
                    $("#desc").keydown(function (e) {
                        if(e.keyCode == 13){
                            var status = $(".sales-current").data('status');
                            if(status == 'ats') {
                                xsshSaleOrderList(null,false);
                                return;
                            }
                            saleOrderList();
                        }
                    });
                    //收款方式下拉选
                    $("#shop").change(function(event) {
                        init_shop_info(this);
                    });
                    $(".box-right-four").show();
                    $("#box-right-four").addClass("active");
                    //验证优惠码
                    $(".promotion-code").on("click", ".promotion-user", function () {
                        var code = $(".promotion-input").val();
                        validationCode(code);
                    });
                    if($.session.get("pur_sale")){
                        mark = true;
                        pur_sale()
                    }
                    //新增发货单
                    $("#addodr").on("click",  function () {
                        $("#updsalor").attr("disabled", false).removeAttr("style").val("提交订单");
                        init_microware();
                        handlebuffer();
                        $("#edit_detail").fadeIn(200);
                        $("#sale_send").fadeOut(200);
                        $(".promotion-code").show();
                        $(".promotion-code").submitBtn();
                        bufferFlag = true;
                        //$("#postCode").val($("#city").find("option[value="+$("#city").val()+"]").attr("code"));
                    });
                    //物流导出
                    $(".logisticsExport").on("click",function(){
                        logisticsExport();
                    });
                    if(mark) {
                        $("#addodr").click();
                    }
                });
            });

            break;
        case 5:
            $(".box-right-five").load("inventory.html", function(response, status, xhr) {
                $("title").text("我的微仓库存 - TOMTOP Supply Chain");
                require(["layer","laypage","inventory"],function(layer,laypage){
                    //头部微仓下拉值
                    init_header();
                    initParam(layer,laypage);
                    //表格顶部搜索下拉值
                    init_categorys();
                    //表格内容
                    init_inventory(null,false,laypage);
                    $("select[tag='changEvent']").change(function(){
                        init_inventory(null,false,laypage);
                    });

                    $("span[name='search']").click(function(){
                        init_inventory(null,false,laypage);
                    });
                    $("#title").keydown(function(e){
                        if(e.keyCode==13){
                            init_inventory(null,false,laypage);
                        }
                    });
                    $(".send_product_btn").click(function(){
                        toggle_menu_content(4,"add");
                    });
                });
            });
            $("#box-right-five").addClass("active");
            break;
        //交易记录
        case 6:
            $(".box-right-six").load("transaction.html", function(response, status, xhr) {
                $("title").text("交易记录 - TOMTOP Supply Chain");
                require(["layer", "laypage", "transaction"], function(layer,laypage) {
                    bill_checkFrozen();
                    init_bill_applys(layer, laypage);
                    show_init_bills(null,false,laypage);
                    $(".shuchu").click(function(){
                        exportData(this);
                    });
                    $("select").change(function(){
                        show_init_bills(null,false,laypage);
                    });
                });
            });
            $("#box-right-six").addClass("active");
            break;
        //我的收藏
        case 7:
            $(".box-right-seven").load("mycollect.html", function(response, status, xhr) {
                $("title").text("我的收藏 - TOMTOP Supply Chain");
            });
            $("#box-right-seven").addClass("active");
            activeDiv = this;
            break;
        //我的消息
        case 8:
            $(".box-right-eight").load("mynews.html", function(response, status, xhr) {
                $("title").text("我的消息 - TOMTOP Supply Chain");
            });
            $("#box-right-eight").addClass("active");
            break;
        //我的留言
        case 9:
            $(".box-right-nine").load("leavemsg.html", function(response, status, xhr) {
                $("title").text("我的留言 - TOMTOP Supply Chain");
            });
            $("#box-right-nine").addClass("active");
            break;
        //缺货登记
        case 10:
            $(".box-right-ten").load("stockout.html", function(response, status, xhr) {
                $("title").text("缺货登记 - TOMTOP Supply Chain");
            });
            $("#box-right-ten").addClass("active");
            break;
        //采购订单
        case 15:
            $(".box-right-eleven").load("purchase_order.html", function(response, status, xhr) {
                $("title").text("我的进货记录 - TOMTOP Supply Chain");
                require(["laypage", "layer", "purchase"], function(laypage,layer) {
                    init_orders(laypage,layer);
                    $("#seachInput").keydown(function(e){
                        if(e.keyCode==13){
                            var seachSpan = $("#seachInput").val().trim();
                            postPurchase(null,false,seachSpan,laypage);
                        }
                    });
                });
            });
            $("#box-right-eleven").addClass("active");
            break;
        //充值申请
        case 16:
            $(".box-right-twelve").load("top_up.html", function(response, status, xhr) {
                $("title").text("充值提现 - TOMTOP Supply Chain");
                require(["layer", "laypage", "apply"],function(layer, laypage){
                    console.log("laydate version-->" + laydate.v);
                    checkFrozen();
                    init_applys(layer, laypage);
                    //收款方式下拉选
                    $("select[name='recipientId']").change(function(event) {
                        if (this.value != "") {
                            var opt = undefined, mode = this.value;
                            $.each($(this).children(), function(i, item) {
                                if (mode == item.value) opt = item;
                            });
                            $("#receipt_account").text($(opt).data("mode").split("——")[0]);
                            $("#receipt_payee").text($(opt).data("mode").split("——")[1]);
                        } else {
                            $("#receipt_account").text("");
                            $("#receipt_payee").text("");
                        }
                    });
                    $("#instructions-submit").click(function(){
                        sendApply();
                    });
                    //立即充值按钮
                    $("#immediately-mony").click(function(){
                        checkKeyWord();
                    });
                    //申请时间和申请状态下拉选
                    $("select[name^='purpose']").change(function(){
                        init_applys(layer, laypage);
                    });
                });
            });
            $("#box-right-twelve").addClass("active");
            break;
        //订单导入
        case 18:
            //$(".box-right-fourteen").load("importOrder.html", function(response, status, xhr) {
            //	$("title").text("订单导入 - TOMTOP Supply Chain");
            //	require(["orderimport","generateOrder"],function(orderimport,generateOrder){
            //		orderimport.init_orderimport();
            //       $("#box-right-fourteen").addClass("active");
            //	});
            //});
            //break;
            $(".box-right-four").load("importOrder.html", function(response, status, xhr) {
                $("title").text("销售发货 - TOMTOP Supply Chain");
                require(["orderimport","generateOrder","laydate"],function(orderimport,generateOrder){
                    orderimport.init_orderimport();
                    $(".box-right-four").show();
                    $("#box-right-four").addClass("active");
                });
            });
            $.session.set('n',4);
            break;
        //以上为菜单内容加载
        //我的资料
        case 11:
            $("#mine-means").show();
            $("#mine_shops").hide();
            $("#mine_phones").hide();
            $("#check_despart").hide();
            $("title").text("我的资料 - TOMTOP Supply Chain");
            break;
        //我的店铺
        case 12:
            $("#mine-means").hide();
            $("#mine_shops").show();
            $("#mine_phones").hide();
            $("#check_despart").hide();
            $("title").text("我的店铺 - TOMTOP Supply Chain");
            require(["mystore"], function(mystore) {
                mystore.ajax_get_store();
            });
            $.session.set('n',2);
            break;
        //修改资料
        case 13:
            $(".box-right-two").empty();
            $(".box-right-two").load("alterinfo.html", function(response, status, xhr) {
                $("title").text("修改资料 - TOMTOP Supply Chain");
                require(["alterinfo"], function(alterinfo) {
                    alterinfo.init_user_info();
                });
            });
            $.session.set('n',2);
            break;
        //保存修改
        case 14:
            $(".box-right-two").empty();
            $(".box-right-two").load("myinfo.html", function(response, status, xhr) {
                $("title").text("我的资料 - TOMTOP Supply Chain");
                require(["myinfo"], function(myinfo) {
                    myinfo.init_information();
                });
            });
            $.session.set('n',2);
            break;
        //更改手机
        case 17:
            $(".box-right-two").empty();
            $(".box-right-two").load("change_phone.html", function(response, status, xhr) {
                $("title").text("修改手机 - TOMTOP Supply Chain");
                require(["alterphone"], function(alterphone) {
                    $.ajax({
                        url : "/member/clientid?" + new Date(),
                        type : "GET",
                        data : {},
                        async:false,
                        success : function(data) {
                        }
                    });
                    alterphone.bind_phone_event();
                    $("#alter_phone_form #captchaImg").click();
                });
            });
            $(".box-right-two").show();
            $.session.set('n',1);
            break;
        case 19:
            $(".box-right-two").empty();
            $(".box-right-two").load("",function(response, status, xhr){
                $("title").text("修改手机 - TOMTOP Supply Chain");
                require(["alterphone"], function(alterphone) {
                    alterphone.bind_phone_event();
                    $("#alter_phone_form #captchaImg").click();
                });
            });
            break;
        //微仓库存
        case 20:
            $(".box-right-twenty").load("repertory.html", function(response, status, xhr) {
                $("title").text("微仓库存 - TOMTOP Supply Chain");
                creatLoading(true,theDiv)
            });
            $("#box-right-twenty").addClass("active");
        //我的移动端
        case 21:
            $("#mine-means").hide();
            $("#mine_shops").hide();
            $("#mine_phones").show();
            $("#check_despart").hide();
            $("title").text("我的移动端 - TOMTOP Supply Chain");
            require(["myinfo","qrcode","common","jqform"], function(myinfo) {
                myinfo.getMobileInfo();
            });
            break;
        //客户订单与订单导入 合并
        case 23:
            $(".box-right-four").load("importOrder.html", function(response, status, xhr) {
                $("title").text("销售发货 - TOMTOP Supply Chain");
                require(["orderimport","generateOrder"],function(orderimport,generateOrder){
                    orderimport.init_orderimport();
                });
            });
            $.session.set('n',4);
            break;
        //查看我的报价单
        case 24:
            $("#purchase_list").hide();
            $(".mine_price_list").show();
            $("title").text("我的报价单列表 - TOMTOP Supply Chain");
            require(["quotation",""],function(quotation){
                isnulogin(function(email){
                    quotation.init_quotation(1,email);
                });
            });
            break;
        //我的报价单
        case 25:
            $(".mine_price_list").hide();
            $(".mine_price_box").show();
            $("title").text("我的报价单 - TOMTOP Supply Chain");
            break;
    }
    var _hmt = _hmt || [];
    (function() {
        var hm = document.createElement("script");
        hm.src = "https://hm.baidu.com/hm.js?26311fa008c983b30e4094b29fa4875f";
        var s = document.getElementsByTagName("script")[0];
        s.parentNode.insertBefore(hm, s);
    })();
}

//当前系统时间
function geTime() {
    var dates = new Date();
    var years = dates.getFullYear();
    var months = dates.getMonth() + 1;
    var days = dates.getDate();
    var hours = dates.getHours();
    var mins = dates.getMinutes();
    var secs = dates.getSeconds();
    if (months < 10) {
        months = "0" + months;
    }
    if (days < 10) {
        days = "0" + days;
    }
    if (hours < 10) {
        hours = "0" + hours;
    }
    if (mins < 10) {
        mins = "0" + mins;
    }
    secs = "00";
    return years + "-" + months + "-" + days + " " + hours + ":" + mins + ":" + secs;
}