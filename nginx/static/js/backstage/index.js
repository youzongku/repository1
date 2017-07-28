require.config({
    baseUrl: "../js/",
    paths: {
        "jquery": "lib/jquery-1.11.3.min",
        "laypage": "lib/laypage1.3/laypage",
        "layer": "lib/layer2.0/layer",
        "common": "common",
        "webuploader": "lib/webuploader/webuploader",
        "welcome": "backstage/welcome",
        "role": "backstage/usermgr/role",
        "admin": "backstage/usermgr/admin",
        "userlevel": "backstage/usermgr/level",
        "credit": "backstage/usermgr/credit",
        "purchase": "backstage/purchasemgr/purchase_mgr",
        "purchaseReturnList": "backstage/purchasemgr/returnod/purchase_return_list",
        "purchaseAuditReturnList": "backstage/purchasemgr/returnod/purchase_audit_return_list",
        "purchaseReturnCoefficients": "backstage/purchasemgr/returnod/purchase_return_coefficients",
        "purchase_order_audit_by_cs": "backstage/purchasemgr/purchase_order_audit_by_cs",
        "sale": "backstage/sale/saleManager",
        "inputso": "backstage/sale/input_sale_order",
        "editso": "backstage/sale/edit_sale_order",
        "afterSale": "backstage/sale/afterSaleManager",
        "saleOrderTemplate": "backstage/sale/SaleOrderTemplate",
        "goods": "backstage/goodsmgr/goodsmgr_stock",
		"microstock": "backstage/goodsmgr/micro_stock",
        "recharge": "backstage/recharge/recharge",
        "addrecharge": "backstage/recharge/new_recharge_apply",
        "treecore": "lib/jquery.ztree.core-3.5",
        "excheck": "lib/jquery.ztree.excheck-3.5",
        "disprice": "backstage/goodsmgr/goodsmgr_disprice",
        "active": "backstage/usermgr/active",
        "sparea": "backstage/mktool/sprice_area",
        "wechat": "payment/wechat",
        "qrcode": "payment/qrcode",
        "pro_common": "backstage/promotion/promotion_common",
        "pro_category": "backstage/promotion/promotion_category",
        "dynamic_condts": "backstage/promotion/dynamic_condts",
        "dynamic_pvlgs": "backstage/promotion/dynamic_pvlgs",
        "dynamic_description": "backstage/promotion/dynamic_description",
        "add_pro": "backstage/promotion/add_promotion",
        "edit_pro": "backstage/promotion/edit_promotion",
        "organization": "backstage/usermgr/organization",
        "price": "backstage/goodsmgr/product_price",
        "banner": "backstage/System_Settings/banner_manage",
        "clerk_type_in": "backstage/clerk/clerk_warehouse_type_in",
        "pruchase_import":"backstage/clerk/clerk_purchase_import",
        "auditModel": "backstage/usermgr/audit_model",
        "purchase_order_audit_by_finance":"backstage/purchasemgr/purchase_order_audit_by_finance",
        "jqform" : "lib/jquery.form",
        "useraccount": "backstage/usermgr/account",
        "attr_mana":"backstage/goodsattr/attr_manager",
        "sale_cs":"backstage/sale/sale_cs_audit",
        "sale_finance_audit":"backstage/sale/sale_finance_audit",
        "mo_list":"backstage/marketingorder/mo_list",
        "mo_sm_list":"backstage/marketingorder/mo_sm_list",
        "mo_input":"backstage/marketingorder/mo_input",
        "mo_audit_firstly":"backstage/marketingorder/mo_audit_firstly",
        "mo_audit_secondly":"backstage/marketingorder/mo_audit_secondly",
        "mo_common":"backstage/marketingorder/mo_common",
        "BbcGrid" : "model/BbcGrid",
        "sale_cloud_order":"backstage/sale/sale_cloud_order",
        "sale_cloud_order_import":"backstage/sale/sale_cloud_order_import",
        "purchase_price":"backstage/goodsmgr/purchase_price",
        "goods_category":"backstage/goodsmgr/goods_category",
        "acperiod":"backstage/acperiod/acperiod",
        "acperiod_check":"backstage/acperiod/acperiod_check",
        "floating_advertising_manage":"backstage/System_Settings/floating_advertising_manage",
        "column_advertising_manage":"backstage/System_Settings/column_advertising_manage",
        "floor_advertising_manage":"backstage/System_Settings/floor_advertising_manage",
        "sales_order_refunds":"backstage/sale/sales_order_refunds",
        "sales_order_refunds_cs_audit":"backstage/sale/sales_order_refunds_cs_audit",
        "sales_order_refunds_finance_audit":"backstage/sale/sales_order_refunds_finance_audit",
        "sales_order_refunds_finance_audit":"backstage/sale/sales_order_refunds_finance_audit",
        "sales_order_refunds_confirm_receipt":"backstage/sale/sales_order_refunds_confirm_receipt",
        "floor_advertising_manage":"backstage/System_Settings/floor_advertising_manage",
        "contract_file":"backstage/contract/contract_file",
        "contract_bid":"backstage/contract/contract_bid",
        "trading_record":"backstage/usermgr/trading_record",
        "cloud_inventory":"backstage/goodsmgr/cloud_inventory",
        "md5":"md5"
    },
    shim: {
        "laypage": {
            exports: "",
            deps: []
        },
        "layer": {
            exports: "",
            deps: ["jquery"]
        },
        "common": {
            exports: "",
            deps: ["jquery"]
        },
        "webuploader": {
            exports: "",
            deps: ["jquery"]
        },
        "welcome": {
            exports: "",
            deps: ["jquery"]
        },
        "role": {
            exports: "",
            deps: ["jquery"]
        },
        "purchase": {
            exports: "",
            deps: ["jquery", "laypage"]
        },
        "purchaseReturnList": {
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        },
        "purchaseReturnCoefficients": {
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        },
        "sale": {
            exports: "",
            deps: ["jquery", "laypage"]
        },
        "inputso": {
            exports: "",
            deps: ["jquery", "layer", "laypage"]
        },
        "editso": {
            exports: "",
            deps: ["jquery", "layer", "laypage"]
        },
        "sale_cloud_order":{
            exports: "",
            deps: ["jquery", "laypage"]
        },
        "sale_cloud_order_import":{
            exports: "",
            deps: ["jquery", "laypage"]
        },
        "sale_finance_audit": {
            exports: "",
            deps: ["jquery", "laypage"]
        },
        "sale_cs": {
            exports: "",
            deps: ["jquery", "laypage"]
        },
        "afterSale": {
            exports: "",
            deps: ["jquery", "laypage"]
        },
        "mo_sm_list": {
            exports: "",
            deps: ["jquery", "laypage"]
        },
        "goods": {
            exports: "",
            deps: ["jquery", "laypage"]
        },
		"microstock": {
            exports: "",
            deps: ["jquery", "laypage"]
        },
        "recharge": {
            exports: "",
            deps: ["jquery", "laypage"]
        },
        "addrecharge": {
            exports: "",
            deps: ["jquery", "laypage", "jqform"]
        },
        "userlevel": {
            exports: "",
            deps: ["jquery", "laypage"]
        },
        "treecore": {
            exports: "",
            deps: ["jquery"]
        },
        "excheck": {
            exports: "",
            deps: ["jquery", "treecore"]
        },
        "disprice": {
            exports: "",
            deps: ["jquery", "laypage"]
        },
        "active": {
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        },
        "wechat": {
            exports: "",
            deps: ["jquery", "common", "layer", "qrcode"]
        },
        "pro_category": {
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        },
        "add_pro": {
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        },
        "edit_pro": {
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        },
        "organization": {
            exports: "",
            deps: ["jquery"]
        },
        "price": {
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        },
        "banner": {
            exports: "",
            deps: ["jquery"]
        },
        "auditModel": {
            exports: "",
            deps: ["jquery"]
        },
        "jqform": {
            exports: "",
            deps: ["jquery"]
        },
        "useraccount": {
            exports: "",
            deps: ["jquery", "laypage"]
        },
        "attr_mana":{
            exports: "",
            deps: ["jquery", "laypage"]
        },
        "mo_list":{
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        },
        "mo_input":{
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        },
        "mo_audit_firstly":{
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        },
        "mo_audit_secondly":{
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        },
        "contract_file":{
            exports: "",
            deps: ["jquery", "laypage", "layer","webuploader","md5"]
        },
        "contract_bid":{
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        },
        "acperiod":{
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        },
        "acperiod_check":{
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        },
        "trading_record":{
            exports: "",
            deps: ["jquery", "laypage", "layer"]
        }
    }
});

var modifyPwsSave;

require(["jquery", "layer", "laypage", "common"], function ($, layer, laypage) {
    //未登录则直接跳转到后台登录页面
    ajax_get("/member/isaulogin?" + (new Date()).getTime(), {}, undefined,
        function (response) {
            if (!response.suc) {
                window.location.href = "/backstage/login.html";
            }
        },
        function (xhr, status) {
            console.log("status-->" + status);
        }
    );
    //加载左侧菜单
    loadMenu();
    //初始加载欢迎页面
    load_menu_content(0);
    refresh_date_time();
    window.setInterval("refresh_date_time()", 59000);

    //解决不支持placeholder属性的浏览器支持该属性
    $(document).ready(function () {
        $(function () {
            if (!placeholderSupport()) {
                $('[placeholder]').focus(function () {
                    var input = $(this);
                    if (input.val() == input.attr('placeholder')) {
                        input.val('');
                        input.removeClass('placeholder');
                    }
                }).blur(function () {
                    var input = $(this);
                    if (input.val() == '' || input.val() == input.attr('placeholder')) {
                        input.addClass('placeholder');
                        input.val(input.attr('placeholder'));
                    }
                }).blur();
            }
        })
        function placeholderSupport() {
            return 'placeholder' in document.createElement('input');
        }

    });

    //伸缩导航栏
    $(".small-menu").on("click", function () {
        $('#page-wrapper').toggleClass('nav-small');
        if ($(".content-B").hasClass("nav-small")) {
            //function HoverSmall() {
            $('#nav-left li').addClass('Smallhov');
            $('.Smallhov').mouseover(function () {
                $(this).children('.menu_body').stop(false, false).slideDown(200);
            })
            $('.Smallhov').mouseout(function () {
                $(this).children('.menu_body').stop(false, false).slideUp(100);
            });
        }
        else {
            $('.Smallhov').off('mouseover mouseout')
            $('#nav-left li').removeClass('Smallhov');
        }
    });

    //退出登录状态
    $("#close").parent().on("click", function (event) {
        $.get("/member/adminlogout?" + (new Date()).getTime(), function (response) {
            window.location.href = "login.html";
        });
    });

    modifyPwsSave = function () {
        //修改原有校验，增强密码复杂度 by huchuyin 2016-10-6
        //var PW_RE = /(?!^[0-9]+$)(?!^[a-zA-Z]+$)(?!^[^0-9a-zA-Z]+$)^.{6,20}$/;
        //var newcode = $("#modifyPws input[name=newcode]").val();
        //if (!(PW_RE.test(newcode))) {
        //    layer.msg('密码必须为6-20个字符，且至少包含数字、字母(区分大小写)等两种或以上字符', {icon: 2, time: 2000});
        //    return;
        //}
        var newcode = $("#modifyPws input[name=newcode]").val();
        if((newcode.search(/[0-9]/) == -1)
            || (newcode.search(/[A-Z]/) == -1)
            || (newcode.search(/[a-z]/) == -1)
            || (newcode.length < 6) || (newcode.length > 20)) {
            layer.msg('密码必须为6-20个字符，且至少包含数字、大写、小写字母等三种或以上字符！', {icon: 2, time: 2000});
            return;
        }
        //End by huchuyin 2016-10-6
        var confirmcode = $("#modifyPws input[name=surecode]").val();
        if (newcode && confirmcode) {
            if (confirmcode != newcode) {
                layer.msg('两次输入的密码不正确，请重新输入！', {icon: 2, time: 2000});
                return;
            } else {
                var oldcode = $("#modifyPws input[name=oldcode]").val();
                var param = {
                    oldcode:oldcode,
                    newcode:newcode,
                    surecode:confirmcode
                };
                ajax_post("/member/resetpwd", JSON.stringify(param), "application/json",
                    function(data) {
                        var code = data.code;
                        if (data.success) {
                            if (code == 1) {
                                layer.msg('请填写完整参数。', {icon: 2, time: 2000});
                            } else if (code == 5) {
                                layer.msg('密码必须为6-20个字符，且至少包含数字、大写、小写字母等三种或以上字符！', {icon: 2, time: 2000});
                            } else if (code == 2) {
                                layer.msg('两次输入的密码不正确，请重新输入！', {icon: 2, time: 2000});
                            } else if (code == 3) {
                                layer.msg('旧密码错误，请确认后重新输入。', {icon: 2, time: 2000});
                            } else if (code == 4) {
                                layer.msg('密码修改成功。', {icon: 1, time: 2000});
                                layer.close(changCodeBox);
                            }
                        } else {
                            window.location.href = "login.html";
                        }
                    }
                );
            }
        }
    };
});

//突出显示当前菜单
function backSelected(obj) {
    $(".back-current").removeClass();
    $(obj).addClass("back-current");
}

function load_menu_content(n, param_json) {
    n = parseInt(n);
    $(".content-R").each(function (i, node) {
        var visible = $(node).css("display");
        if (visible == "block") {
            $(node).empty();
        }
        $(node).hide();
    });
    //删除laydate创建的div
    if ($("#laydate_box")[0] != undefined) {
        $("#laydate_box").remove();
    }
    if (n == 0) {
        $(".content-R-zero").show();
        $(".content-R-zero").load("welcome.html", function (response, status, xhr) {
            require(["layer", "welcome"], function (layer) {
                init_welcome(layer);
            });
        });
        return;
    }
    ajax_post(
        "/member/checkMenuAuthority",
        JSON.stringify({"position": n}),
        "application/json",
        function (response) {
            if (response.success == true) {
                if (response.isFlag == true) {
                    switch (n) {
                        //欢迎页面
                        case 0:
                            $(".content-R-zero").show();
                            $(".content-R-zero").load("welcome.html", function (response, status, xhr) {
                                require(["layer", "welcome"], function (layer) {
                                    init_welcome(layer);
                                });
                            });
                            break;
                        //商品管理——商品库存
                        case 1:
                            $(".content-R-one").show();
                            $(".content-R-one").load("goodsmgr_stock.html", function (response, status, xhr) {
                                $("title").text("商品管理——商品库存 - B2B后台");
                                require(["laypage","BbcGrid"], function (laypage,BbcGrid) {
                                    require(["goods"], function () {
                                        init_goods_goodsmgr_stock(laypage,BbcGrid);
                                        init_catgs();
                                        init_types();
                                        $("#goods_status").change(function () {
                                            $("#goodsmgr_stock_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
                                        });
                                        $("#small_select").change(function () {
                                            $("#goodsmgr_stock_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
                                        });
                                        $("#select-type").change(function () {
                                            $("#goodsmgr_stock_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
                                        });

                                        $("#wareShow li").click(function(){
                                            $(".Storage-distribution-active").removeClass("Storage-distribution-active");
                                            $(this).addClass("Storage-distribution-active");
                                            //viewGoods_ad(null, false, flag);
                                            $("#goodsmgr_stock_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
                                        });

                                    });
                                });
                            });
                            break;
                        //微仓进货——采购单
                        case 2:
                            $(".content-R-two").show();
                            $(".content-R-two").load("purchase_order.html", function (response, status, xhr) {
                                $("title").text("采购单——采购单 - B2B后台");
                                require(["layer", "laypage","BbcGrid"], function (lay, laypage,BbcGrid) {
                                    require(["purchase"], function () {
                                        init_purchase_order_category_title('微仓进货');
                                        init_(lay, laypage, '微仓进货');
                                        var grid = new BbcGrid();
                                        grid.initTable($("#purchase_orders_table"),getSetting_purchaseorder());
                                    });
                                })
                            });
                            break;
                        //销售发货——发货单
                        case 3:
                            $(".content-R-three").show();
                            $(".content-R-three").load("sale/sales_order.html", function (response, status, xhr) {
                                $("title").text("销售发货——发货单 - B2B后台");
                                require(["layer", "laypage", "BbcGrid"], function (layer, laypage, BbcGrid) {
                                    require(["sale", "saleOrderTemplate"], function (sale, SaleOrderTemplate) {
                                        init_allSale_sales_order(layer, laypage,BbcGrid, new SaleOrderTemplate(), '销售发货');
                                        //导出销售订单
                                        $("#exportSaleOrder").click(function () {
                                            exportSaleOrder();
                                        });
                                        $('body').on('click', '#record_sendList', function () {//录入发货单按钮点击事件
                                            // addsales();
                                        });
                                        //录入销售单，判断余额
                                        //是否从余额里扣除运费
                                        $("body").on("click", ".autoFreight", function () {
                                            checkdBanlance(this);
                                        });
                                });
                            });
                            });
                            break;
                        //销售发货——库存调拨
                        case 4:
                            $(".content-R-four").show();
                            $(".content-R-four").load("stock_transfer.html", function (response, status, xhr) {
                                $("title").text("销售发货——库存调拨 - B2B后台");
                            });
                            break;
                         //销售发货——客服确认
                        case 5:
                            $(".content-R-five").show();
                            $(".content-R-five").load("sale/sales_cs_audit.html", function (response, status, xhr) {
                                $("title").text("销售发货——客服确认 - B2B后台");
                                require(["layer", "laypage", "BbcGrid","sale_cs", "saleOrderTemplate"], function (layer, laypage,BbcGrid) {
                                    require(["sale_cs", "saleOrderTemplate"], function (sale, SaleOrderTemplate) {
                                        //初始化销售订单数据
                                        init_cs_sale_new(layer, laypage, BbcGrid,new SaleOrderTemplate());
                                    });
                                });
                            });
                            break;
                        //用户管理——角色设置
                        case 6:
                            $(".content-R-six").show();
                            $(".content-R-six").load("usermgr_role.html", function (response, status, xhr) {
                                $("title").text("用户管理——角色设置 - B2B后台");
                                require(["layer", "laypage","BbcGrid",  "treecore", "excheck"], function (layer, laypage,BbcGrid) {
                                    require(["role"], function () {
                                        //初始化角色列表
                                        init_role(layer, laypage,BbcGrid);
                                    });
                                });
                            });
                            break;
                        //用户管理——后台用户
                        case 7:
                            $(".content-R-seven").show();
                            $(".content-R-seven").load("usermgr_admin.html", function (response, status, xhr) {
                                $("title").text("用户管理——后台用户 - B2B后台");
                                require(["layer", "laypage","BbcGrid","treecore","excheck"], function (layer, laypage,BbcGrid) {
                                    require(["admin"], function () {
                                        //初始化后台用户列表
                                        init_admin(0, layer, laypage,BbcGrid);
                                    });
                                });
                            });
                            break;
                        //用户管理——普通用户
                        case 8:
                            $(".content-R-eight").show();
                            $(".content-R-eight").load("usermgr_consumer.html", function (response, status, xhr) {
                                $("title").text("用户管理——普通用户 - B2B后台");
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["admin"], function () {
                                        //初始化前台用户列表
                                        init_admin(2, layer, laypage,BbcGrid);
                                        $("#export").click(function () {
                                            exportUser();
                                        });
                                    });
                                });
                            });
                            break;
                        //充值提现——充值提现记录
                        case 9:
                            $(".content-R-nine").show();
                            $(".content-R-nine").load("recharge_all.html", function (response, status, xhr) {
                                $("title").text("充值提现——充值提现记录 - B2B后台");
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["recharge"], function () {
                                        init_recharge(BbcGrid,layer);
                                        initRechargeAllEvent();
                                    });
                                });
                            });
                            break;
                        //充值提现——充值初审
                        case 10:
                            $(".content-R-ten").show();
                            $(".content-R-ten").load("recharge_first_trial.html", function (response, status, xhr) {
                                $("title").text("充值提现——充值初审 - B2B后台");
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["recharge"], function () {
                                        initial_audit(BbcGrid,layer);
                                        initial_audit_event();
                                    });
                                });
                            });
                            break;
                        //充值提现——充值复审
                        case 11:
                            $(".content-R-eleven").show();
                            $(".content-R-eleven").load("recharge_recheck.html", function (response, status, xhr) {
                                $("title").text("充值提现——充值复审 - B2B后台");
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["recharge"], function () {
                                        review_audit(BbcGrid,layer);
                                        review_audit_event();
                                    });
                                });
                            });
                            break;
                        //用户管理——等级设置
                        case 12:
                            $(".content-R-twelve").show();
                            $(".content-R-twelve").load("usermgr_level.html", function (response, status, xhr) {
                                $("title").text("用户管理——等级设置 - B2B后台");
                                require(["BbcGrid"], function () {
                                    require(["userlevel"], function (userlevel) {
                                        userlevel.initialize();  //等级设置用户合并到普通用户展示
                                    });
                                });
                            });
                            break;
                        //商品管理——报价单导出
                        case 13:
                            $(".content-R-thirteen").show();
                            $(".content-R-thirteen").load("distribution_price.html", function (response, status, xhr) {
                                $("title").text("商品管理——报价单导出 - B2B后台");
                                require(["laypage", "disprice"], function (laypage) {
                                    initialize_disprice(laypage);
                                    bind_disprice_element_event();
                                });
                            });
                            break;

                        //用户管理——额度管理
                        case 15:
                            $(".content-R-fifteenth").show();
                            $(".content-R-fifteenth").load("quota_management.html", function (response, status, xhr) {
                                $("title").text("用户管理——额度管理 - B2B后台");
                                require(["layer", "laypage", "credit"], function (layer, laypage) {
                                    //初始化用户额度表
                                    init_credit(layer, laypage);

                                    $("#comsumerTypeOfCredit").change(function () {
                                        ajax_get_credit(1);
                                    });
                                    $("#limiteStatus").change(function () {
                                        ajax_get_credit(1);
                                    });
                                    $("#searchButton").click(function () {
                                        ajax_get_credit(1);
                                    });
                                    $("#exportCredit").click(function () {
                                        exportCredit();
                                    });
                                    $(".list_content p select").change(function () {
                                        ajax_get_credit(1);
                                    });
                                });
                            });
                            break;
                        //营销工具——优惠码
                        case 16:
                            $(".content-R-sixteen").show();
                            $(".content-R-sixteen").load("promotion_code.html", function (response, status, xhr) {
                                $("title").text("营销工具——优惠码 - B2B后台");
                                require(["layer","laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["active"], function () {
                                        //初始化用户额度表
                                        //init_active(layer, laypage);
                                        init_active_new(layer, laypage, BbcGrid);
                                        init_coupons(BbcGrid);
                                    });
                                });
                            });
                            break;
                        //充值提现——返佣记录
                        case 17:
                            $(".content-R-seventeen").show();
                            $(".content-R-seventeen").load("your_earnings.html", function (response, status, xhr) {
                                $("title").text("充值提现——返佣记录 - B2B后台");
                            });
                            break;
                        //商品管理——采购成本
                        case 18:
                            $(".content-R-eighteen").show();
                            $(".content-R-eighteen").load("goodsprice_system.html", function (response, status, xhr) {
                                $("title").text("商品管理——采购成本 - B2B后台");
                                require(["laypage","BbcGrid"], function (laypage, BbcGrid) {
                                    require(["purchase_price"], function () {
                                        init_goods_purchase_price(laypage, 1, BbcGrid);
                                        init_catgs(1);
                                        $("#price_select").change(function () {
                                            $("#goodsprice_system_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
                                        });

                                        $(".searchButton").click(function () {
                                            $("#goodsprice_system_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
                                        });

                                        $("#wareShow li").click(function(){
                                            $(".Storage-distribution-active").removeClass("Storage-distribution-active");
                                            $(this).addClass("Storage-distribution-active");
                                            //viewGoods_ad(null, false, flag);
                                            $("#goodsprice_system_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
                                        });
                                    });
                                });
                            });
                            break;
                        //客户售后单
                        //case 14:
                        //	$(".content-R-fourteen").show();
                        //	$(".content-R-fourteen").load("after_sales_back.html", function(response, status, xhr) {
                        //		$("title").text("售后管理——客户售后单 - B2B后台");
                        //		require(["layer","laypage","afterSale"], function(layer,laypage) {
                        //			init_after_sale(layer,laypage);
                        //
                        //		});
                        //	});
                        //	break;
                        //特价
                        case 19:
                            $(".content-R-twenty").show();
                            $(".content-R-twenty").load("discount_price.html", function (response, status, xhr) {
                                $("title").text("特价专区——营销工具 - B2B后台");
                                require(["BbcGrid"], function (BbcGrid) {
                                    require(["sparea"], function (sparea) {
                                        sparea.init();
                                    });
                                });
                            });
                            break;
                        //促销一览表
                        case 20:
                            $(".content-R-twentyone").show();
                            $(".content-R-twentyone").load("promotion_schedule.html", function (response, status, xhr) {
                                $("title").text("促销管理——促销一览表 - B2B后台");
                                require(['BbcGrid'], function (BbcGrid) {
                                    require(["layer", "laypage", "pro_category", "dynamic_condts", "dynamic_pvlgs",'dynamic_description'], function (layer, laypage) {
                                        //init_schedule(layer, laypage)
                                        init_schedule_new(layer, laypage,BbcGrid);
                                    });
                                });
                            });
                            break;
                        //添加促销
                        case 21:
                            $(".content-R-twentytwo").show();
                            var html_page = "add_promotion.html";
                            if (param_json != undefined) {
                                if (param_json['edit_pro_type'] == 'edit_pro') {
                                    html_page = "edit_promotion.html";
                                }
                            }
                            $(".content-R-twentytwo").empty();
                            $(".content-R-twentytwo").load(html_page, function (response, status, xhr) {
                                $("title").text("促销管理——添加促销 - B2B后台");
                                if (param_json != undefined) {
                                    if (param_json['edit_pro_type'] == 'edit_pro') {
                                        require(["layer", "laypage", "pro_common", "dynamic_condts", "dynamic_pvlgs", 'dynamic_description',"edit_pro"], function (layer, laypage) {
                                            init_edit_pro(layer, laypage, param_json['pro_act_id']);
                                            init_dynamic_condts(layer, laypage);
                                            init_dynamic_pvlgs(layer, laypage);
                                        });
                                    }
                                } else {
                                    require(["layer", "laypage", "pro_common", "dynamic_condts", "dynamic_pvlgs", 'dynamic_description',"add_pro"], function (layer, laypage) {
                                        init_add_pro(layer, laypage);
                                        init_dynamic_condts(layer, laypage);
                                        init_dynamic_pvlgs(layer, laypage);
                                    });
                                }
                            });
                            break;
                        //促销类型管理
                        case 22:
                            $(".content-R-twentythree").show();
                            $(".content-R-twentythree").load("promotion_category.html", function (response, status, xhr) {
                                $("title").text("促销管理——促销类型管理 - B2B后台");
                                require(["BbcGrid"], function (BbcGrid) {
                                    require(["layer", "laypage", "pro_common", "pro_category"], function (layer, laypage) {
                                        //init_pro_category(layer, laypage);
                                        init_pro_category_new(layer, laypage,BbcGrid);
                                    });
                                });
                            });
                            break;
                        //组织架构管理
                        case 23:
                            $(".content-R-twentyfour").show();
                            $(".content-R-twentyfour").load("organization_manager.html", function (response, status, xhr) {
                                $("title").text("用户管理——组织架构管理 - B2B后台");
                                require(["layer", "laypage", "organization", "treecore", "excheck", "jqform"], function (layer, laypage) {
                                    init_organization(layer, laypage);
                                    //搜素业务员
                                    $(".related_operation").on("click",".searchButton", function(){
                                        getSalesman4EnterKey()
                                    });
                                    //业务人员对应分销商页面-----搜索分销商
                                    $('.related_distract').on("click", ".searchButton", function () {
                                        getMemberList4RelatedDistract()
                                    });
                                    //关联的分销商页面---pageSize的改变
                                    $(".list_content_member p select").change(function(){
                                        getMemberList4RelatedDistract()
                                    });
                                    //每页大小的改变---业务员列表
                                    $(".list_content_salesman p select").change(function(){
                                        getSalesman($(".business_message").data("headerId"), 1);
                                    });
                                });
                            });
                            break;
                        //价格设置
                         case 24:
                             $(".content-R-twentyfive").show();
                             $(".content-R-twentyfive").load("price/product_price.html", function (response, status, xhr) {
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["price"], function () {
                                        type = 'TOTAL';
                                        init_pro_price(layer, laypage,BbcGrid);
                                    });
                                });
                             });
                             break;
                        //banner管理
                        case 26:
                            $(".content-R-twentysix").show();
                            $(".content-R-twentysix").load("banner_manage.html", function (response, status, xhr) {
                                $("title").text("系统配置——广告位管理 - B2B后台");
                                require(["BbcGrid"], function () {
                                    require(["banner"], function (banner) {
                                        //banner.init();
                                        banner.init_new();
                                    });
                                });
                            });
                            break;
                        //业务员录单——采购单
                        case 28:
                            $(".content-R-twentyseven").show();
                            $(".content-R-twentyseven").load("purchase_order.html", function (response, status, xhr) {
                                $("title").text("业务员录单——采购单 - B2B后台");
                                require(["layer", "laypage","BbcGrid"], function (lay, laypage,BbcGrid) {
                                    require(["purchase"], function () {
                                        init_purchase_order_category_title('业务员录单');
                                        init_(lay, laypage);
                                        var grid = new BbcGrid();
                                        grid.initTable($("#purchase_orders_table"),getSetting_purchaseorder());
                                        // $("#export_purchase").click(function () {
                                        //     exportPurchase(this);
                                        // });
                                    });
                                })
                            });
                            break;
                        //业务员录单——发货单
                        case 29:
                            $(".content-R-twentyeight").show();
                            $(".content-R-twentyeight").load("sale/sales_order.html", function (response, status, xhr) {
                                $("title").text("业务员录单——发货单 - B2B后台");

                                require(["layer", "laypage", "BbcGrid"], function (layer, laypage, BbcGrid) {
                                    require(["sale", "saleOrderTemplate"], function (sale, SaleOrderTemplate) {
                                        //init_allSale(layer, laypage, new SaleOrderTemplate());
                                        init_allSale_sales_order(layer, laypage,BbcGrid, new SaleOrderTemplate(),'业务员录单');

                                        //导出销售订单
                                        $("#exportSaleOrder").click(function () {
                                            exportSaleOrder();
                                        });
                                        $('body').on('click', '#record_sendList', function () {//录入发货单按钮点击事件
                                            // addsales();
                                        });
                                        //录入销售单，判断余额
                                        //是否从余额里扣除运费
                                        $("body").on("click", ".autoFreight", function () {
                                            checkdBanlance(this);
                                        });
                                        ////生成订单
                                        //$("body").on("click", "#submit-sale", function () {
                                        //    generSlae();
                                        //});
                                    });
                                });
                            });
                            break;
                        //采购进货——采购单录入
                        case 30: $(".content-R-thirty").show();
                            $(".content-R-thirty").load("input_purchase_order.html", function (response, status, xhr) {
                                $("title").text("采购单录入——采购单 - B2B后台");
                                require(["layer", "laypage","pruchase_import","purchase","clerk_type_in",'jqform'], function (lay, laypage,p_import) {
                                    init_input_purchase_order_category_title('微仓进货');
                                    init_(lay, laypage,p_import);
                                    p_import.init_import();
                                    show_inputOrder();
                                    $("#forword").val(2);
                                });
                            });
                            break;
                        //业务员录单—采购单录入
                        case 31: $(".content-R-thirty-one").show();
                            $(".content-R-thirty-one").load("input_purchase_order.html", function (response, status, xhr) {
                                $("title").text("采购单录入——业务员录单 - B2B后台");
                                require(["layer", "laypage","pruchase_import","purchase","clerk_type_in",'jqform'], function (lay, laypage,p_import) {
                                    init_input_purchase_order_category_title('业务员录单');
                                    init_(lay, laypage,p_import);
                                    p_import.init_import();
                                    show_inputOrder();
                                    $("#forword").val(28);
                                });
                            });
                            break;
                        //销售发货-发货单录入
                        case 32: $(".content-R-thirty-two").show();
                            $(".content-R-thirty-two").empty()
                            $(".content-R-thirty-two").load("sale/input_sales_order.html", function (response, status, xhr) {
                                $("title").text("发货单录入——销售发货 - B2B后台");
                                require(["layer", "laypage", "inputso"], function (layer, laypage) {
                                    init_input_sales(layer, laypage,'销售发货');
                                    // addsales();
                                    $("#forword").val(3);

                                    //是否从余额里扣除运费
                                    $("body").on("click", ".autoFreight", function () {
                                        checkdBanlance(this);
                                    });
                                    //商品弹窗仓库选择
                                    $("#warehose_id").change(function () {
                                        inputso_searchProducts();//目前需求只展示完税仓中的商品（深圳仓）
                                    });

                                    //商品弹窗类目选择
                                    $(".add_sendProduct_pop #product_select").change(function(){
                                        var email = $(".record_sendList_line .selected_email").val().trim();
                                        inputso_searchProducts(email);
                                    })
                                    //商品弹窗仓库选择
                                    $(".add_sendProduct_pop #warehouse_select").change(function(){
                                        inputso_searchProducts();
                                    })

                                    //商品弹窗搜索按钮
                                    $(".add_sendProduct_pop .searchButton").click(function(){
                                        inputso_searchProducts();
                                    });
                                });
                            });
                            break;
                        //业务员录单-发货单录入
                        case 33:
                            $(".content-R-thirty-three").show();
                            $(".content-R-thirty-three").load("sale/input_sales_order.html", function (response, status, xhr) {
                                $("title").text("发货单录入——业务员录单 - B2B后台");
                                require(["layer", "laypage", "inputso"], function (layer, laypage) {
                                    init_input_sales(layer, laypage,'业务员录单');
                                    // addsales();
                                    $("#forword").val(29);
                                    //录入销售单，判断余额
                                    //是否从余额里扣除运费
                                    $("body").on("click", ".autoFreight", function () {
                                        checkdBanlance(this);
                                    });
                                    //仓库选择
                                    $("#warehose_id").change(function () {
                                        inputso_searchProducts();//目前需求只展示完税仓中的商品（深圳仓）
                                    });

                                    //类目选择
                                    $(".add_sendProduct_pop #product_select").change(function(){
                                        inputso_searchProducts();
                                    })

                                    //商品弹窗仓库选择
                                    $(".add_sendProduct_pop #warehouse_select").change(function(){
                                        inputso_searchProducts();
                                    })

                                    //搜索按钮
                                    $(".add_sendProduct_pop .searchButton").click(function(){
                                        inputso_searchProducts();
                                    });
                                });
                            });
                            break;
                        //充值提现---提现审核
                        case 34:
                            $(".content-R-thirty-four").show();
                            $(".content-R-thirty-four").load("present_audit.html", function (response, status, xhr) {
                                $("title").text("充值提现——提现审核 - B2B后台");
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["recharge"], function () {
                                        present_audit(BbcGrid,layer);
                                        presentAuditEvent();
                                    });
                                });
                            });
                            break;
                        //微仓进货---客服确认
                        case 35:
                            $(".content-R-thirty-five").show();
                            $(".content-R-thirty-five").load("purchase_order_audit_by_cs.html", function (response, status, xhr) {
                                $("title").text("微仓进货——待客服确认 - B2B后台");
                                require(["layer", "laypage","BbcGrid","purchase_order_audit_by_cs"], function (layer, laypage, BbcGrid) {
                                    initAuditByCustomerService(layer, laypage);
                                    var grid = new BbcGrid();
                                    grid.initTable($("#orders_by_CS_table"),getSetting_purchaseorder_cs());
                                });
                            });
                            break;
                        //微仓进货---待财务确认
                        case 36:
                            $(".content-R-thirty-five").show();
                            $(".content-R-thirty-five").load("purchase_order_audit_by_finance.html", function (response, status, xhr) {
                                $("title").text("微仓进货——线下转账待审核 - B2B后台");
                                require(["layer", "laypage","BbcGrid","purchase_order_audit_by_finance"], function (layer, laypage, BbcGrid) {
                                    initAuditByFinance(layer, laypage);

                                    var grid = new BbcGrid();
                                    grid.initTable($("#orders_by_finance_table"),getSetting_purchaseorder_f());
                                });
                            });
                            break;
                        //用户管理---模式审核
                        case 37:
                            $(".content-R-thirty-seven").show();
                            $(".content-R-thirty-seven").load("model_audit.html", function (response, status, xhr) {
                                $("title").text("用户管理——模式审核 - B2B后台");
                                require(["layer", "laypage","BbcGrid", "jqform"], function (layer, laypage,BbcGrid) {
                                    require(["auditModel"], function () {
                                        init_auditModel(layer, laypage,BbcGrid);
                                    });
                                });
                            });
                            break;
                        //价格设置
                        case 38:
                            $(".content-R-thirty-eight").show();
                            $(".content-R-thirty-eight").load("price/disprice2.html", function (response, status, xhr) {
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["price"], function () {
                                        type = 'TOTAL';
                                        init_pro_price(layer, laypage,BbcGrid);
                                    });
                                });
                            });
                            break;
                        //基础价格设置
                        case 39:
                            $(".content-R-thirty-nine").show();
                            $(".content-R-thirty-nine").load("price/basePrice.html", function (response, status, xhr) {
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["price"], function () {
                                        type = 'BASE';
                                        init_pro_price(layer, laypage,BbcGrid);
                                    });
                                });
                            });
                            break;
                        //经销商供货价设置
                        case 40:
                            $(".content-R-forty").show();
                            $(".content-R-forty").load("price/distributoPrice.html", function (response, status, xhr) {
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["price"], function () {
                                        type = 'DIS';
                                        init_pro_price(layer, laypage,BbcGrid);
                                    });
                                });
                            });
                            break;
                        //进口专营供货价设置
                        case 41:
                            $(".content-R-forty-one").show();
                            $(".content-R-forty-one").load("price/ftzPrice.html", function (response, status, xhr) {
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["price"], function () {
                                         type = 'FTZ';
                                        init_pro_price(layer, laypage,BbcGrid);
                                    });
                                });
                            });
                            break;
                        //Bbc价格设置
                        case 42:
                            $(".content-R-forty-two").show();
                            $(".content-R-forty-two").load("price/elePrice.html", function (response, status, xhr) {
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["price"], function () {
                                        type = 'ELE';
                                        init_pro_price(layer, laypage,BbcGrid);

                                    });
                                });
                            });
                            break;
                        // KA经销价格设置
                        case 43:
                            $(".content-R-forty-three").show();
                            $(".content-R-forty-three").load("price/supermarketPrice.html", function (response, status, xhr) {
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["price"], function () {
                                         type = 'SUP';
                                        init_pro_price(layer, laypage,BbcGrid);
                                    });
                                });
                            });
                            break;
                        // 组织架构
                        case 44:
                            $(".content-R-twentyfour").show();
                            $(".content-R-twentyfour").load("organization.html", function (response, status, xhr) {
                                $("title").text("用户管理——组织架构 - B2B后台");
                                require(["layer", "laypage", "organization", "treecore", "excheck", "jqform"], function (layer, laypage) {
                                    init_organization(layer, laypage);
                                    //搜素业务员
                                    $(".related_operation").on("click",".searchButton", function(){
                                        getSalesman($(".business_message").data("headerId"), 1);
                                    });
                                    //业务人员对应分销商页面-----搜索分销商
                                    $('.related_operation').on("click", ".searchButton", function () {
                                        getMemberList4RelatedOperation()
                                    });
                                    //关联的分销商页面---pageSize的改变
                                    $(".list_content_member p select").change(function(){
                                        getMemberList4RelatedOperation()
                                    });
                                    //每页大小的改变---业务员列表
                                    $(".list_content_salesman p select").change(function(){
                                        getSalesman($(".business_message").data("headerId"), 1);
                                    });
                                });
                            });
                            break;
                        // 商品管理——商品分类
                        case 45:
                            $(".content-R-one").show();
                            $(".content-R-one").load("goodsmgr_stock_category.html", function (response, status, xhr) {
                                $("title").text("商品管理——商品分类 - B2B后台");
                                require(["laypage","BbcGrid"], function (laypage,BbcGrid) {
                                    require(["goods_category"], function () {
                                        init_goods_category(laypage,null,true,BbcGrid);
                                        init_catgs();
                                        init_types();

                                        $("#goods_status").change(function () {
                                            $("#goodsmgr_stock_category_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
                                        });
                                        $("#small_select").change(function () {
                                            $("#goodsmgr_stock_category_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
                                        });
                                        $("#select-type").change(function () {
                                            $("#goodsmgr_stock_category_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
                                        });

                                        $("#wareShow li").click(function(){
                                            $(".Storage-distribution-active").removeClass("Storage-distribution-active");
                                            $(this).addClass("Storage-distribution-active");
                                            //viewGoods_ad(null, false, flag);
                                            $("#goodsmgr_stock_category_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
                                        });
                                    });
                                });
                            });
                        break;
                        // 待付款订单
                        case 46:
                            $(".content-R-forty-four").show();
                            $(".content-R-forty-four").load("sale/waitpay_sales_order.html", function (response, status, xhr) {
                                require(["layer", "laypage", "BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["sale", "saleOrderTemplate"], function (sale, SaleOrderTemplate) {
                                        init_waitpay_sale_new(layer, laypage,BbcGrid, new SaleOrderTemplate());
                                    });
                                });
                            });
                            break;
                        //  商品管理-属性管理
                        case 47:
                            $(".content-R-forty-seven").show();
                            $(".content-R-forty-seven").load("attribute_management.html", function (response, status, xhr) {
                                require(["layer", "laypage","attr_mana"], function (layer, laypage) {
                                    init_attr_man(layer, laypage);
                                });
                            });
                            break;
                        // 商品管理-属性集管理
                        case 48:
                            $(".content-R-forty-eight").show();
                            $(".content-R-forty-eight").load("property_set_management.html", function (response, status, xhr) {
                                require(["layer", "laypage"], function (layer, laypage) {
                                    init_waitpay_sale(layer, laypage);
                                    //每页显示条目
                                    $("#sale_pageSize").change(function () {
                                        postAllSale(null, false);
                                    });
                                });
                            });
                            break;
                        // 商品管理-分类属性集管理
                        case 49:
                            $(".content-R-forty-nine").show();
                            $(".content-R-forty-nine").load("classification_management.html", function (response, status, xhr) {
                                require(["layer", "laypage"], function (layer, laypage) {
                                    init_waitpay_sale(layer, laypage);
                                    //每页显示条目
                                    $("#sale_pageSize").change(function () {
                                        postAllSale(null, false);
                                    });
                                });
                            });
                            break;
                        // 商品管理-分类属性集管理
                        case 50:
                            $(".content-R-fifty").show();
                            $(".content-R-fifty").load("preview_of_goods.html", function (response, status, xhr) {
                                require(["layer", "laypage"], function (layer, laypage) {
                                    init_waitpay_sale(layer, laypage);
                                    //每页显示条目
                                    $("#sale_pageSize").change(function () {
                                        postAllSale(null, false);
                                    });
                                });
                            });
                            break;
                        // 充值提现-录入充值记录
                        case 51:
                            $(".content-R-fifty-one").show();
                            $(".content-R-fifty-one").load("recharge_backstage.html", function (response, status, xhr) {
                                require(["layer", "laypage", "addrecharge"], function (layer, laypage) {
                                    initParam(layer, laypage);
                                    $(".record-tab-con").eq(0).show();
                                    $(".record_purchaseList_tab li").click(function(){
                                        var num =$(".record_purchaseList_tab li").index(this);
                                        if($(this).hasClass("recharge_current")){
                                            $(this).removeClass("recharge_current").siblings().addClass("recharge_current");
                                        }else{
                                            $(this).addClass("recharge_current").siblings().removeClass("recharge_current");
                                        }
                                        $(".record-tab-con").hide();
                                        $(".record-tab-con").eq(num).show().siblings().hide();
                                    });
                                    $(".sendApply .instructions-submit").click(function(){
                                            submit_recharge(this);
                                        }
                                    )
                                });
                            });
                            break;
                        // 用户管理-账户余额
                        case 52:
                            $(".content-R-fifty-two").show();
                            $(".content-R-fifty-two").load("usermgr_account.html", function (response, status, xhr) {
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["useraccount"], function () {
                                        init_account_new(layer,laypage,BbcGrid,1)
                                    });
                                });
                            });
                            break;
                            //VIP价格设置
                        case 53:
                            $(".content-R-fifty-three").show();
                            $(".content-R-fifty-three").load("price/vipPrice.html", function (response, status, xhr) {
                                 require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["price"], function () {
                                         type = 'VIP';
                                        init_pro_price(layer, laypage,BbcGrid);
                                    });
                                });
                            });
                            break;
                        // 用户管理-账户余额管理
                        case 54:
                            $(".content-R-fifty-four").show();
                            $(".content-R-fifty-four").load("usermgr_account_manager.html", function (response, status, xhr) {
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["useraccount"], function () {
                                        init_account_new(layer,laypage,BbcGrid,2)
                                    });
                                });
                            });
                            break;
                        //销售发货——财务确认
                       case 55:
                            $(".content-R-fifty-five").show();
                            $(".content-R-fifty-five").load("sale/sales_finance_audit.html", function (response, status, xhr) {
                                $("title").text("销售发货——财务确认 - B2B后台");
                                require(["layer", "laypage", "BbcGrid"], function (layer, laypage, BbcGrid) {
                                    require(["sale_finance_audit", "saleOrderTemplate"], function (sale, SaleOrderTemplate) {
                                    //初始化销售订单数据
                                        init_sale_new(layer, laypage,BbcGrid, new SaleOrderTemplate());
                                    });
                                });
                            });
                            break;
                        //商品管理——商品微仓库存管理
                        case 57:
                            $(".content-R-fifty-seven").show();
                            $(".content-R-fifty-seven").load("warehouse_inventory.html", function (response, status, xhr) {
                                $("title").text("商品管理——商品微仓库存管理 - B2B后台");
                                require(["laypage","BbcGrid"], function (laypage, BbcGrid) {
                                    require(["microstock"], function () {
                                        init_microstock_new(layer,laypage,BbcGrid,1);
                                    });
                                });
                            });
                            break;
                        //营销单
                        case 58:
                            $(".content-R-fifty-eight").show();
                            $(".content-R-fifty-eight").load("marketOrder/mo_list.html", function (response, status, xhr) {
                                $("title").text("营销单管理——营销单");
                                require(["laypage", "layer", "BbcGrid", "mo_list", "mo_common"], function (laypage,layer,BbcGrid) {
                                    initMoList(laypage,layer)
                                    var grid = new BbcGrid();
                                    grid.initTable($("#mo_table"),getSetting_mo());
                                });
                            })
                            break;
                        //营销单录入
                        case 59:
                            $(".content-R-fifty-nine").show();
                            $(".content-R-fifty-nine").load("marketOrder/mo_input.html", function (response, status, xhr) {
                                $("title").text("营销单管理——营销单录入");
                                require(["laypage", "layer", "mo_input"], function (laypage,layer) {
                                    // 判断是否通过复制进入的
                                    var moInfo = (param_json && param_json['edit_mo_type'] == 'edit_mo') ? param_json['moInfo'] : undefined
                                    var needExpirationDate = moInfo?param_json['needExpirationDate']:undefined
                                    initMoInput(laypage,layer,moInfo,needExpirationDate);

                                    // 点击查询分销商
                                    $("#marketing_order_search_event").click(function(){
                                        getInnerDistributors(1);
                                    });
                                    // 点击查询分销商
                                    $("#marketing_order_search_value").keydown(function(event) {
                                        var key = event.keyCode || event.which;
                                        if (key == 13) {
                                            getInnerDistributors(1);
                                        }
                                    });
                                });
                            })
                            break;
                        //营销单初审
                        case 60:
                            $(".content-R-sixty").show();
                            $(".content-R-sixty").load("marketOrder/mo_audit_firstly.html", function (response, status, xhr) {
                                $("title").text("营销单管理——营销单初审");
                                require(["laypage", "layer", "BbcGrid", "mo_audit_firstly", "mo_common"], function (laypage,layer,BbcGrid) {
                                    initMoAuditFirstly(laypage,layer)

                                    var grid = new BbcGrid();
                                    grid.initTable($("#af_mo_table"),getSetting_af_mo());
                                });
                            })
                            break;
                        //营销单复审
                        case 61:
                            $(".content-R-sixty-one").show();
                            $(".content-R-sixty-one").load("marketOrder/mo_audit_secondly.html", function (response, status, xhr) {
                                $("title").text("营销单管理——营销单复审");
                                require(["laypage", "layer", "BbcGrid", "mo_audit_secondly", "mo_common"], function (laypage,layer,BbcGrid) {
                                    initMoAuditSecondly(laypage,layer)

                                    var grid = new BbcGrid();
                                    grid.initTable($("#as_mo_table"),getSetting_as_mo());
                                });
                            })
                            break;
                         //销售发货——云仓发货
                        case 62:$(".content-R-sixty-two").show();
                            $(".content-R-sixty-two").load("sale/sale_cloud_order.html", function (response, status, xhr) {
                                $("title").text("销售发货——云仓发货 - B2B后台");
                                var salesOrderNo = (param_json && param_json['edit_so_type'] == 'edit_so') ? param_json['salesOrderNo'] : undefined
                                require(["layer", "laypage", "webuploader", "sale_cloud_order", "sale_cloud_order_import"], function (layer, laypage, webuploader) {
                                    init_cloud_sales(layer, laypage, salesOrderNo);
                                    init_import_cloud_sales(layer,laypage,webuploader)
                                });
                            });
                            break;
                        case 63:$(".content-R-sixty-three").show();
                            $(".content-R-sixty-three").load("purchase_return_list.html", function (response, status, xhr) {
                                $("title").text("微仓退货——退货单 - B2B后台");
                                require(["laypage", "layer", "BbcGrid"], function (laypage,layer,BbcGrid) {
                                    require(["purchaseReturnList"], function () {
                                        initReturnOrderList(layer,laypage)

                                        var grid = new BbcGrid();
                                        grid.initTable($("#return_orders_table"),getSetting_ro());
                                    });
                                });
                            });
                            break;
                        // 待审核退货单
                        case 84:$(".content-R-sixty-three").show();
                            $(".content-R-sixty-three").load("purchase_audit_return_list.html", function (response, status, xhr) {
                                $("title").text("微仓退货——待审核退货单 - B2B后台");
                                require(["laypage", "layer", "BbcGrid"], function (laypage,layer,BbcGrid) {
                                    require(["purchaseAuditReturnList"], function () {
                                        initAuditReturnOrderList(layer,laypage)

                                        var grid = new BbcGrid();
                                        grid.initTable($("#auditro_return_orders_table"),auditro_getSetting_ro());
                                    });
                                });
                            });
                            break;
                         case 64:$(".content-R-sixty-four").show();
                            $(".content-R-sixty-four").load("purchase_return_coefficients.html", function (response, status, xhr) {
                                $("title").text("微仓退货——商品退货比例 - B2B后台");
                                require(["laypage", "layer", "BbcGrid"], function (laypage,layer,BbcGrid) {
                                    require(["purchaseReturnCoefficients"], function () {
                                        initReturnOrderCoefficients(layer,laypage)

                                        var grid = new BbcGrid();
                                        grid.initTable($("#return_coefficients_table"),getSetting_rc());
                                         //$("#return_coefficients_table").jqGrid("setGroupHeaders", {
                                         //    useColSpanStyle: true,
                                         //    groupHeaders:[
                                         //        {startColumnName:'30', numberOfColumns:7, titleText: '商品退款系数（按距到期日期天数设置）'},
                                         //    ]
                                         //})
                                  });
                                });
                            });
                            break;
                            //浮窗管理
                        case 65:$(".content-R-sixty-five").show();
                            $(".content-R-sixty-five").load("floating_advertising_manage.html", function (response, status, xhr) {
                                $("title").text("系统配置——浮窗管理 - B2B后台");
                                require(["BbcGrid"], function () {
                                    require(["floating_advertising_manage"], function (floatingAdvertising) {
                                        floatingAdvertising.init();
                                    });
                                });

                                $('#floating_advertising_searchButton').click(function () {
                                    $("#floating_advertising_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
                                });

                                $("#floating_advertising_manage_describe").keydown(function(e){
                                    if(e.keyCode==13){
                                        $("#floating_advertising_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
                                    }
                                });
                            });
                            break;
                        case 66:$(".content-R-sixty-six").show();
                            $(".content-R-sixty-six").load("floor_advertising_manage.html", function (response, status, xhr) {
                                $("title").text("系统配置——楼层广告位管理 - B2B后台");
                                require(["BbcGrid"], function () {
                                    require(["floor_advertising_manage"], function (floorAdvertising) {
                                        floorAdvertising.init();
                                    });
                                });

                                $('#floor_advertising_searchButton').click(function () {
                                    $("#floor_advertising_manage_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
                                });

                                $("#floor_advertising_manage_describe").keydown(function(e){
                                    if(e.keyCode==13){
                                        $("#floor_advertising_manage_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
                                    }
                                });
                            });
                            break;
                        case 67:$(".content-R-sixty-seven").show();
                            $(".content-R-sixty-seven").load("column_advertising_manage.html", function (response, status, xhr) {
                                $("title").text("系统配置——通栏广告位管理 - B2B后台");
                                require(["BbcGrid"], function () {
                                    require(["column_advertising_manage"], function (columnAdvertising) {
                                        columnAdvertising.init();
                                    });
                                });

                                $('#column_advertising_searchButton').click(function () {
                                    $("#column_advertising_manage_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
                                });

                                $("#common_advertising_manage_describe").keydown(function(e){
                                    if(e.keyCode==13){
                                        $("#column_advertising_manage_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
                                    }
                                });
                            });
                            break;
                        //营销单
                        case 68:
                            $(".content-R-fifty-eight").show();
                            $(".content-R-fifty-eight").load("marketOrder/mo_list.html", function (response, status, xhr) {
                                $("title").text("营销单管理——营销单");
                                require(["laypage", "layer", "BbcGrid", "mo_list", "mo_common"], function (laypage,layer,BbcGrid) {
                                    initMoList(laypage,layer,true);
                                    var grid = new BbcGrid();
                                    grid.initTable($("#mo_table"),getSetting_mo());
                                });
                            })
                            break;
                         //用户管理——账期管理
                        case 69:
                            $(".content-R-sixty-nine").show();
                            $(".content-R-sixty-nine").load("acperiod/acperiod.html", function (response, status, xhr) {
                                $("title").text("用户管理——账期管理");
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["acperiod"], function () {
                                        //初始化前台用户列表
                                        init_payment_management(0, layer, laypage,BbcGrid);
                                    });
                                });
                            });
                            break;
                        //用户管理——查看账期
                        case 70:
                            $(".content-R-seventy").show();
                            $(".content-R-seventy").load("acperiod/acperiod_check.html", function (response, status, xhr) {
                                $("title").text("用户管理——查看账期");
                                require(["layer", "laypage","BbcGrid"], function (layer, laypage,BbcGrid) {
                                    require(["acperiod_check"], function () {
                                        //初始化前台用户列表
                                        init_payment_check(0, layer, laypage,BbcGrid);
                                    });
                                });
                            });
                            break;
                        //用户管理——查看账期
                        case 71:
                            $(".content-R-seventy-one").show();
                            $(".content-R-seventy-one").load("trading_record.html", function (response, status, xhr) {
                                $("title").text("用户管理——查看账期");
                                require(["layer","BbcGrid"], function (layer,BbcGrid) {
                                    require(["trading_record"], function () {
                                        init_bills(layer, BbcGrid)
                                    });
                                })
                            });
                            break;
                        //合同管理——合同管理
                        case 76:
                            $(".content-R-seventy-six").show();
                            $(".content-R-seventy-six").load("contract/contract_file.html", function (response, status, xhr) {
                                $("title").text("合同管理——合同管理");
                                require(["layer", "laypage","BbcGrid","jqform"], function (layer,laypage,BbcGrid) {
                                    require(["contract_file"], function () {
                                        var grid = new BbcGrid();
                                        models(laypage,layer,grid);
                                        grid.initTable($("#contract_file_table"),contractFile());
                                    });
                                })
                            });
                            break;
                        //合同管理——报价管理
                        case 77:
                            $(".content-R-seventy-seven").show();
                            $(".content-R-seventy-seven").load("contract/contract_bid.html", function (response, status, xhr) {
                                $("title").text("合同管理——报价管理");
                                require(["layer", "laypage","BbcGrid","jqform"], function (layer,laypage,BbcGrid) {
                                    require(["contract_bid"], function () {
                                        var grid = new BbcGrid();
                                        warehouse(layer,laypage,grid);
                                        grid.initTable($("#contract_bid_table"),contractBid());
                                    });
                                })
                            });
                            break;
                        case 82:
                            $(".content-R-eighty-two").show();
                            $(".content-R-eighty-two").load("marketOrder/mo_sm_list.html", function (response, status, xhr) {
                                $("title").text("营销单管理——营销单导出-业务");
                                require(["layer","BbcGrid","treecore"], function (layer,BbcGrid) {
                                    require(["mo_sm_list"], function () {
                                        init_mo_sm(layer,BbcGrid);
                                    });
                                })
                            });
                            break;

                        case 72:
                            $(".content-R-seventy-two").show();
                            $(".content-R-seventy-two").load("product_initinventory.html", function (response, status, xhr) {
                                $("title").text("商品管理——sku库存初始化");
                                    require(["laypage","layer","cloud_inventory","BbcGrid"], function (laypage,layer,cloud_inventory,BbcGrid) {
                                        $("#checksku").click(function(){
                                        new_init_product(layer, laypage, BbcGrid);
                                    });
                                        
                                });
                            })
                            break;

                        case 73:
                            $(".content-R-seventy-three").show();
                            $(".content-R-seventy-three").load("erpstock_changerecord.html", function (response, status, xhr) {
                                $("title").text("商品管理——erp库存变更记录");
                                    require(["laypage","layer","cloud_inventory","BbcGrid"], function (laypage,layer,cloud_inventory,BbcGrid) {
                                        $("#stockchange").click(function(){
                                        new_erpstock_changerecord(layer, laypage, BbcGrid);
                                    });
                                        
                                });
                            })
                            break; 

                        case 74:
                            $(".content-R-seventy-four").show();
                            $(".content-R-seventy-four").load("sync_erpstockchange.html", function (response, status, xhr) {
                                $("title").text("商品管理——同步erp库存变更数据");
                                    require(["laypage","layer","cloud_inventory","BbcGrid"], function (laypage,layer,cloud_inventory,BbcGrid) {
                                        $("#syncerpchange").click(function(){
                                        new_sync_erpchange(layer, laypage, BbcGrid);
                                    });
                                        
                                });
                            })
                            break;

                        case 78:
                            $(".content-R-seven-eight").show();
                            $(".content-R-seven-eight").load("sales_order_refunds.html", function (response, status, xhr) {
                                $("title").text("发货单退款——退款单");
                                require(["BbcGrid"], function (BbcGrid) {
                                    require(["sales_order_refunds"], function () {
                                        init_sales_order_refunds(BbcGrid);
                                    });
                                });
                            });
                            break;
                        case 79:
                            $(".content-R-eight-nine").show();
                            $(".content-R-eight-nine").load("sales_order_refunds_cs_audit.html", function (response, status, xhr) {
                                $("title").text("发货单退款——客服确认");
                                require(["BbcGrid"], function (BbcGrid) {
                                    require(["sales_order_refunds_cs_audit"], function () {
                                        init_sales_order_refunds_cs_audit(BbcGrid);
                                    });
                                });
                            });
                            break;
                        case 80:
                            $(".content-R-seven-eight").show();
                            $(".content-R-seven-eight").load("sales_order_refunds_finance_audit.html", function (response, status, xhr) {
                                $("title").text("发货单退款——财务确认");
                                require(["BbcGrid"], function (BbcGrid) {
                                    require(["sales_order_refunds_finance_audit"], function () {
                                        init_sales_order_refunds_finance_audit(BbcGrid);
                                    });
                                });
                            });
                            break;
                        case 81:
                            $(".content-R-seven-eight").show();
                            $(".content-R-seven-eight").load("sales_order_refunds_confirm_receipt.html", function (response, status, xhr) {
                                $("title").text("发货单退款——确认收货");
                                require(["BbcGrid"], function (BbcGrid) {
                                    require(["sales_order_refunds_confirm_receipt"], function () {
                                        init_sales_order_refunds_confirm_receipt(BbcGrid);
                                    });
                                });
                            });
                            break;

                    }
                }
                else {
                    layer.msg("您没有该操作权限，请联系管理员",
                        {icon: 2, time: 1000},
                        function () {
                            window.location.href = '/backstage/index.html';
                        }
                    );
                }
            }
            else {
                window.location.href = '/backstage/index.html';
            }
        },
        function (XMLHttpRequest, textStatus) {
            layer.msg("校验权限错误，请联系管理员", {icon: 2, time: 2000});
        }
    );
}
//验证金额格式
function checkMoney(money) {
    var reg = /^(0|[1-9]\d*)(\.\d+)?$/;
    return reg.test(money);
}
//折叠导航栏
var isOpen, isRotate = false;
function loadMenu() {
    //通过用户对应的角色权限加载菜单
    ajax_get("/member/getRoleMenuOfUser?" + (new Date()).getTime(), "", "application/json",
        function (response) {
            if (response.success == true) {
                var menus = response.roleMenus;
                console.log(response);
                var menuHtml = "";
                for (var i = 0, len = menus.length; i < len; i++) {
                    var menu = menus[i];
                    var class1 = 'm-icon' + i;
                    var childMenus = menu.childMenus;
                    var itemHeadHtml = "<li>" +
                        "<div class='menu_head'>" +
                        "<div class='" + class1 + "'></div>" +
                        '<b>' + menu.menuName + '</b>' +
                        "<span>+</span>" +
                        "</div>" +
                        "<div class='menu_body'>";
                    var childHtml = "";
                    for (var j = 0, l = childMenus.length; j < l; j++) {
                        var child = childMenus[j];
                        childHtml += "<p position = '"+ child.position + "' onclick=\"load_menu_content('" + child.position + "'),backSelected(this);\">" + child.menuName + "</p>";
                    }
                    var itemFooterHtml = childHtml + "</div></li>";
                    menuHtml += itemHeadHtml + itemFooterHtml;
                }
                $("#nav-left").empty();
                $("#nav-left").prepend(menuHtml);
                $("#nav-left .menu_head").click(function (event) {
                    var bodys = $('.menu_body')
                    if (this == isOpen) {
                        if (isRotate == true) {
                            $(this).children("span").css({
                                transform: "rotate(0deg)"
                            })
                            isRotate = false;
                        } else {
                            $(this).children("span").css({
                                transform: "rotate(45deg)"
                            })
                            isRotate = true;
                        }
                        $(this).siblings(".menu_body").stop(false, false).slideToggle();
                        return
                    } else {
                        $('.menu_body').slideUp("500");
                        for (var i = 0; i < bodys.length; i++) {
                            if ($(bodys[i]).css('display') == 'block') {
                                $(bodys[i]).siblings().children().css({
                                    transform: "rotate(0deg)"
                                })
                            }
                        }
                        $(this).children("span").css({
                            transform: "rotate(45deg)"
                        })
                        isRotate = true;
                        $(this).siblings(".menu_body").stop(false, true).slideToggle();
                    }
                    isOpen = this;
                });
            } else if (response.code == 2) {
                window.location.href = "/backstage/login.html";
            } else {
                layer.msg(response.msg, {icon: 2, time: 2000});
            }
        },
        function (xhr, status) {
            layer.msg("加载菜单异常", {icon: 0, time: 2000});
        }
    );
}


//弹出框
var changCodeBox;
function changecode() {
    var goal = $('.user-changecode');
    goal.find('input[type=password]').each(function () {
        $(this).val('');
    });
    changCodeBox = layer.open({
        type: '1',
        title: '修改密码',
        content: goal,
        btn: ['确定', '取消'],
        area: ['380px', '250px'],
        yes: function () {
            modifyPwsSave();
        }
    })
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
    if (secs < 10) {
        secs = "0" + secs;
    }
    return years + "-" + months + "-" + days + " " + hours + ":" + mins + ":" + secs;
};

function pophandleExplain(){
    layer.open({
        type: 2,
        title:'操作说明',
        skin: 'layui-layer-rim', //加上边框 
        area: ['737px', '640px'], //宽高
        shadeClose:true,
        content: '../../img/handle-exp.jpg'
    });
}

