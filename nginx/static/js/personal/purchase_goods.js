var laypage = undefined;
var layer = undefined;

function init_goods(lay, layd) {
    laypage = lay;
    layer = layd;
    viewWarehouse();
    viewGoods(null, false);
}

function viewWarehouse() {
    //加载仓库信息
    var wareUrll = "/inventory/queryWarehouse";
    $.ajax({
        url: wareUrll,
        type: "get",
        dataType: "json",
        async: false,
        success: function (data) {
            var wareHtml = "<span>所属仓库：</span><li value='0' class='cheWarCls' onclick='checkWare(this)'>全部</li>";
            if (data.length > 0) {
                for (var i in data) {
                    wareHtml += "<li value='" + data[i].id + "' onclick='checkWare(this)'>" + data[i].warehouseName + "</li>";//+data[i].warehouseName+"</li>";
                }
                wareHtml += "<li value='-1'  onclick='checkWare(this)' >我的微仓</li>";
                $(".purchase-save-nav").empty();
                $(".purchase-save-nav").append(wareHtml);
            }
        }
    });
}

function viewGoods(obj, red, url) {
    isnulogin(function (email) {
        var pageCount = "1";
        var pageSize = "10";
        if (red) {
            pageCount = obj.curr;
        }
        if(!url) {
            url = "/product/api/getProducts";
        }
        var categoryId = $(".category_class").val();
        var title = $("#seachInput").val().trim();
        var minPrice = $("#minPrice").attr("data-oral");
        var maxPrice = $("#maxPrice").attr("data-oral");
        var warehouseId = $(".cheWarCls").val();

        var param = {
            pageSize: pageSize,
            currPage: pageCount,
            //表示在售状态，下架不显示
            istatus: 1,
            email:email
        };
        if (categoryId != 0) {
            param.categoryId = categoryId;
        }
        //判断是否是逗号隔开
        var regStr = /,/;
        if (title != null && title != '' && title != undefined) {
            if (regStr.test(title)) {
                param.skuList = title.split(",");
            } else {
                param.title = title;
            }
        }
        if (minPrice != null && minPrice != '' && minPrice != undefined) {
            param.minPrice = minPrice;
        }
        if (maxPrice != null && maxPrice != '' && maxPrice != undefined) {
            param.maxPrice = maxPrice;
        }
        if(warehouseId < 0){
            param.microSort = 1;
        }else if(warehouseId > 0){
            param.warehouseId = warehouseId;
        }
        var sparam = {
            data: param
        };
        ajax_get("/member/infor?" + Math.random(), "", "application/json",
            function (response) {
                if (response.id != null && response.id != 'null' && response.id != '' && response.id != undefined) {
                    // var comsumerType = response.comsumerType;
                    //分销商模式 
                    var distributionMode = response.distributionMode;
                    //将折扣加入到参数中
                    sparam.data.model = distributionMode;
                    $.ajax({
                        url: url,
                        type: "post",
                        dataType: "json",
                        contentType: "application/json",
                        data: JSON.stringify(sparam),
                        async: false,
                        success: function (data) {
                            var reData = data.data;
                            $(".purchase-product").empty();
                            $("#pagination_goo").empty();
                            var paramHtml = "<ul class='purchase-product-title'>" +
                                "<li>商品名称</li>" +
                                "<li>商品编号</li>" +
                                "<li>采购价（元）</li>" +
                                "<li>市场零售价（元）</li>" +
                                "<li >云仓库存</li>" +
                                "<li >所属仓库</li>" +
                                "<li >订单操作</li>" +
                                "</ul>" +
                                "<table class='purchase-product-content'>";
                            if (reData.totalPage > 0) {
                                var result = reData.result;
                                for (var i in result) {
                                    paramHtml += "<tr><td>";
                                    var img_url_ = result[i].imageUrl;
                                    if (img_url_ == null)
                                        img_url_ = "../../img/IW71-4-1a3c.jpg";
                                    var disPrice = result[i].disPrice;

                                    if(distributionMode == '3') {
                                        disPrice = disPrice ? disPrice.toFixed(4) : '--';
                                    }else {
                                        disPrice = disPrice ? disPrice.toFixed(2) : '--';
                                    }

                                    if(result[i].isSpecial){
                                        disPrice = result[i].specialSale;
                                    }
                                    var marketPrice =  result[i].proposalRetailPrice?(result[i].proposalRetailPrice).toFixed(2):"--";
                                    paramHtml += "<a target='_blank' href='../../product/product-detail.html?sku=" + result[i].csku + "&warehouseId=" + result[i].warehouseId + "' class='title_" + result[i].iid + "' style='display: inline-block;'>" +
                                        "<img class='pro-img bbclazy' id='img_" + result[i].iid + "' src='../img/img-load.png' data-original=\"" + urlReplace(img_url_, result[i].csku,"",80,80,100) + "\"/>" +
                                        "</a>" +
                                        "<p style='text-align:left;'><a target='_blank' style='display:block' href='../../product/product-detail.html?sku=" + result[i].csku + "&warehouseId=" + result[i].warehouseId + "' class='title_" + result[i].iid + "'>" +
                                        (result[i].ctitle == null ? '--' : result[i].ctitle) + "</a></p></td>" +
                                        "<td class='csku_" + result[i].iid + "'>" + result[i].csku + "</td>" +
                                        "<td class='sprice_" + result[i].iid + "'>" + disPrice + "</td>" +
                                        "<td class='lprice_" + result[i].iid + "'>" + marketPrice + "</td>" +
                                        "<td class='qty_" + result[i].iid + "'>" + result[i].stock + "</td>" +
                                        "<td class='ware_" + result[i].iid + "' name='" + result[i].warehouseId + "'>" +
                                        result[i].warehouseName +
                                        "</td>";
                                    if (result[i].stock > 0) {
                                        paramHtml += "<td class='tr2-td7'>" +
                                            "<a name='" + result[i].iid + "' href='javascript:;' onclick=\"pushCart(this,"+distributionMode+")\" value='1' class='add-purchasegoods btn-blue-small '>加入购物车</a>" +
                                            "<a name='" + result[i].iid + "' href='javascript:;' onclick=\"pushCart(this,"+distributionMode+")\" value='0' class='buy-purchasegoods btn-green-small'>立即采购</a>" +
                                            "</td>";
                                    }
                                    paramHtml += "</tr>";
                                }
                                init_pagination_goods(reData.totalPage, pageCount, url);
                            } else {
                                //layer.msg('暂无相关商品', {icon: 5, time: 1000}, function (index) {
                                //    layer.close(index);
                                //});
                                addLoadGif({
                                    togBox: ".purchase-product",
                                    togPage: '#pagination_goo'
                                }, true);
                            }
                            paramHtml += "</table>";
                            $(".purchase-product").append(paramHtml).show();
                            $("img.bbclazy").lazyload({
                                effect: 'fadeIn',
                                threshold: '10'
                            });
                        }
                    });
                }
                else {
                    layer.msg("获取用户折扣出错！", {icon: 2, time: 2000});
                }
            },
            function (XMLHttpRequest, textStatus) {
                layer.msg("获取用户折扣出错！", {icon: 2, time: 2000});
            }
        );
    });
    //loading
    return creatLoading(true, theDiv);
}

// 初始化分页栏
function init_pagination_goods(pages, counts, url) {
    scrollPosTop();
    if ($("#pagination_goo")[0] != undefined) {
        $("#pagination_goo").empty();
        laypage({
            cont: 'pagination_goo',
            pages: pages,
            curr: counts,
            groups: 5,
            skin: 'yahei',
            first: '首页',
            last: '尾页',
            prev: '上一页',
            next: '下一页',
            skip: true,
            jump: function (obj, first) {
                //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                if (!first) {
                    viewGoods(obj, true, url);
                }
            }
        });
    }
}

//下拉菜单
function init_catgs() {
    isnulogin(function (email) {
        ajax_get("/product/api/realCateQuery?level=1", "", "application/json",
            function (data) {
                if (data && data.length > 0) {
                    var paramHtml = "<li value='0' onclick='checkCategory(this)' class = 'category_class'>全部</li>";
                    for (var i in data) {
                        paramHtml += "<li value='" + data[i].iid + "' onclick='checkCategory(this)'>" + data[i].cname + "</li>";
                    }
                    $(".purchase-nav").append(paramHtml);
                }
            },
            function (xhr, status) {
            }
        );
    });
}

// 选择当前的状态
function checkCategory(e) {
    $(".category_class").removeClass();
    $(e).addClass("category_class");
    var url = "/product/api/getProducts";
    if ($(".cheWarCls").val() == -1) {
        url = "/product/mirc-inventory";
    }
    viewGoods(null, false,url);
}

function checkWare(e) {
    $(".cheWarCls").removeClass();
    $(e).addClass("cheWarCls");
    var url = "/product/api/getProducts";
    if ($(e).val() == -1) {
       url = "/product/mirc-inventory";
    }
    viewGoods(null, false,url);
}

//分类
$(".purchase-nav").change(function () {
    viewGoods(null, false);
});

function pushCart(obj) {
    var e = obj;
    isnulogin(function (email) {
        var parent = $(obj).parent().parent();
        var iid = obj.name;
        var csku = parent.find("td:eq(1)").text();
        var warehouseid = parent.find(".ware_" + iid).attr("name");
        var publicImg = parent.find("td:eq(0)").find("img").attr("src");
        var paramData = {
            "skuList": [csku],
            "warehouseId": warehouseid,
            "publicImg" : publicImg
        };
        $.ajax({
            url: "/cart/pushCart",
            type: "post",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(paramData),
            success: function (response) {
                if (response.result) {
                    if ($(obj).attr("value") == '1') {
                        addToCart(e);
                        getCartQty();
                    } else {
                        window.location.href = "../cart/shop-cart.html";
                    }
                } else {
                    layer.msg(response.msg, {icon: 1, time: 2000});
                }
            }
        });
    });
}

//购物车产品数量
function getCartQty() {
    var iptCartQty = $("#addcartnum").html();
    ajax_get("/cart/getcartdata?" + Math.random(), "", "application/json",
        function (data) {
            if (data.code) {
                layer.msg(data.msg, {icon: 6, time: 3000});
                return;
            }
            $("#cartItemCountNew").text(data.cartQty);
            if (iptCartQty < data.cartQty) {
                $('.float-addcart').append('<span id="add-up-suc" class="add-up-suc">+1</span>');
                setTimeout(function () {
                    $('#add-up-suc').show().animate({
                        top: "90px",
                        opacity: "hide"
                    }, 500, function () {
                        $(this).remove();
                        $("#addcartnum").text(data.cartQty);
                    })
                }, 1100)
            }
        },
        function (XMLHttpRequest, textStatus) {
            layer.msg("显示购物车列表出错！", {icon: 2, time: 2000});
        }
    );
}

function seachgoods(node) {
    var url = "/product/api/getProducts";
    if ($(".cheWarCls").val() == -1) {
        url = "/product/mirc-inventory";
    }
    viewGoods(null, false, url);
}

/*************************************************下述脚本为销售发货订单中的采购进货功能所用***********************************************************/

function seachgoods_forSaleOrder(node) {
    var url = "/product/api/getProducts";
    if ($(".cheWarCls").val() == -1) {
        url = "/product/mirc-inventory";
    }
    viewGoods_forSaleOrder(null, false,url);
}

function init_goods_forSaleOrder(lay, layd) {
    laypage = lay;
    layer = layd;
    var protable = "<table class='purchase-product-content' style = 'width:863px'>" +
        "<thead>" +
        "<th>商品名称</th><th >商品编号</th><th>采购价（元）</th>" +
        "<th style='width:150px;'>市场零售价（元）</th>" +
        "<th>云仓库存</th><th >微仓库存</th>" +
        "<th >所属仓库</th><th id='select_all'><label><input type='checkbox'>全选</label></th><th >发货数量</th>" +
        "</thead>" +
        "<tbody></tbody>" +
        "</table>";
    $(".purchase-product").append(protable);
    viewWarehouse_forSaleOrder();
    viewGoods_forSaleOrder(null, false);
    //替换搜索事件，改为发货单定制的采购进货内容
    $("#seachSpan").attr("onclick", "seachgoods_forSaleOrder(this)");
    $("#seachInput").attr("placeholder", "SKU(, 隔开可多个查询)/商品名称").css("width", "230px");

    $("#seachInput").keydown(function (e) {
        if (e.keyCode == 13) {
            seachgoods_forSaleOrder(this);
        }
    });
    $("#select_all label").click(function () {
        var boxes = $("tbody .seldPro");
        $(this).parents("table").find(".seldPro").prop("checked", $(this).find("input").is(':checked'));
        $.each(boxes,function(i,item){
            getProInfo($(item));
        });
    });
}

var rows;
function viewGoods_forSaleOrder(obj, red, url) {
    isnulogin(function (email) {
        var pageCount = "1";
        var pageSize = "10";
        var categoryId = $(".category_class").val();
        var title = $("#seachInput").val().trim();
        var minPrice = $("#minPrice").attr("data-oral");
        var maxPrice = $("#maxPrice").attr("data-oral");
        var warehouseId = $(".cheWarCls").val();
        var param = {
            categoryId:undefined,
            title:undefined,
            minPrice:undefined,
            maxPrice:undefined,
            warehouseId:undefined,
            skuList:undefined,
            pageSize: pageSize,
            currPage: pageCount,
            microSort:undefined,
            //表示在售状态，下架不显示
            istatus: 1,
            email:email
        };
        if (!url) {
            url = "/product/api/getProducts";
        }
        //判断是否是逗号隔开
        var regStr = /,/;
        if (title != null && title != '' && title != undefined) {
            if (regStr.test(title)) {
                title = title.replace(/\s+/g,'');
                param.skuList = title.split(",");
            } else {
                param.title = title;
            }
        }
        if (categoryId != 0) {
            param.categoryId = categoryId;
        }
        if (minPrice != null && minPrice != '' && minPrice != undefined) {
            param.minPrice = minPrice;
        }
        if (maxPrice != null && maxPrice != '' && maxPrice != undefined) {
            param.maxPrice = maxPrice;
        }
        if(warehouseId < 0){
            param.microSort = 1;
        }else if(warehouseId > 0){
            param.warehouseId = warehouseId;
        }
        var sparam = {
            data: param
        };
        jQuery.jStorage.flush();
        jQuery.jStorage.set("postData",sparam);
        jQuery.jStorage.set("url",url);
        $(".purchase-product tbody").empty();
        ajax_get("/member/infor?" + Math.random(), "", "application/json",
            function (response) {
                if (response.id != null && response.id != 'null' && response.id != '' && response.id != undefined) {
                    // var comsumerType = response.comsumerType;
                    var distributionMode = response.distributionMode;
                    //将折扣加入到参数中
                    sparam.data.model = distributionMode;
                    $(".purchase-product tbody").autobrowse({
                        url:url,
                        postData : sparam,
                        offset:0,
                        content:$(".pro-to-select"),
                        objName:".purchase-product tbody",
                        template: function (response)
                        {
                            var reData = response.data;
                            var paramHtml = "";
                            $.each(reData.result,function(i,item){
                               var img_url_ = item.imageUrl;
                                //分销利润率
                                var disPrice = item.disPrice;
                                if(item.isSpecial){
                                    disPrice = item.specialSale;
                                }

                                if(distributionMode == '3') {
                                    disPrice = disPrice ? disPrice.toFixed(4) : '--';
                                }else {
                                    disPrice = disPrice ? disPrice.toFixed(2) : '--';
                                }

                                var batchNumber = item.batchNumber;

                                var marketPrice = item.proposalRetailPrice?(item.proposalRetailPrice).toFixed(2):"--";

                                //云仓+微仓库存是否充足
                                var stockenough = item.microStock+(item.stock < 0 ? 0 : item.stock);
                                paramHtml += "<tr><td>";
                                if (img_url_ == null)
                                    img_url_ = "../../img/IW71-4-1a3c.jpg";
                                paramHtml += "<a target='_blank' href='../../product/product-detail.html?sku=" + item.csku + "&warehouseId=" + item.warehouseId + "' class='title_" + item.iid + "' style='display: inline-block;'>"
                                if ($('.pro-to-select').css('display') == 'none') {
                                    paramHtml += "<img class='pro-img bbclazy' id='img_" + item.iid + "' src='../img/img-load.png' data-original=\"" + urlReplace(img_url_,item.csku,"",80,80,100) + "\"/>"
                                }else{
                                    paramHtml += "<img class='pro-img bbclazy' id='img_" + item.iid + "' src=\"" + urlReplace(img_url_, item.csku,"",80,80,100) + "\"/>"
                                }
                                paramHtml += "</a>" +
                                    "<a target='_blank' class='inline-blo vertical-top' href='../../product/product-detail.html?sku=" + item.csku + "&warehouseId=" + item.warehouseId + "' class='title_" + item.iid + "'>" +
                                    "<p>" + (item.ctitle == null ? '--' : item.ctitle) + "</p>" +
                                    "</a>" +
                                    "</td>" +
                                    "<td class='csku_" + item.iid + "'>" + item.csku + "</td>" +
                                    "<td class='sprice_" + item.iid + "'>" + disPrice + "</td>" +
                                    "<td class='lprice_" + item.iid + "'>" + marketPrice + "</td>" +
                                    "<td class='qty_" + item.iid + "'>" + item.stock + "</td>" +
                                    "<td class='microStock'>" + item.microStock + "</td>" +
                                    "<td class='ware_" + item.iid + "' name='" + item.warehouseId + "'>" +
                                    item.warehouseName +
                                    "</td>" +
                                    "<td>" +
                                    (stockenough?
                                        "<input type='checkbox'  class='seldPro' " + (tempProCollection[item.csku] && tempProCollection[item.csku].wareId == item.warehouseId ? "checked = 'true'" : "") + " >"
                                        :"") +
                                    "</td>" +
                                    "<td class='control-num'>" +
                                    (stockenough?
                                    "<a><span class='editNum minus'>-</span></a>"+ "<input type='text' class='numToSend' batchNum = '"+(batchNumber?batchNumber:1)+"' value='" + (tempProCollection[item.csku] && tempProCollection[item.csku].wareId == item.warehouseId ? tempProCollection[item.csku].qty : (batchNumber&&parseInt(batchNumber)>0?batchNumber:1)) + "' >" +
                                    "<a><span class='editNum plus'>+</span></a>"
                                    :"") +
                                    "</td>" +
                                    "</tr>";
                            });
                            $("#pagination_goo").css("display","none");
                            if(reData.totalPage > 0){
                                 $("img.bbclazy").lazyload({
                                    effect: 'fadeIn',
                                    threshold: '10'
                                }); 
                             }else{
                                addLoadGif({
                                    togBox: ".purchase-product",
                                    togPage: '#pagination_goo'
                                }, true);
                             }
                            return paramHtml;
                        },
                        itemsReturned: function (response) {
                            return response.data.result.length;
                        },
                        max: rows,
                        initPage : function (response) {
                            rows = response.data.rows;
                        }
                    });
                } else {
                    layer.msg("获取用户折扣出错！", {icon: 2, time: 2000});
                }
            },
            function (XMLHttpRequest, textStatus) {
                layer.msg("获取用户折扣出错！", {icon: 2, time: 2000});
            }
        );
    });
}

//下拉菜单
function init_catgs_forSaleOrder() {
    isnulogin(function (email) {
        ajax_get("/product/api/realCateQuery?level=1", "", "application/json",
            function (data) {
                if (data && data.length > 0) {
                    var paramHtml = "<li value='0' onclick='checkCategory_forSaleOrder(this)'>全部</li>";
                    for (var i in data) {
                        paramHtml += "<li value='" + data[i].iid + "' onclick='checkCategory_forSaleOrder(this)'>" + data[i].cname + "</li>";
                    }
                    $(".purchase-nav").append(paramHtml);
                }
            },
            function (xhr, status) {
            }
        );
    });
}

// 选择当前的状态
function checkCategory_forSaleOrder(e) {
    $(".category_class").removeClass();
    $(e).addClass("category_class");
    // var node = $(e).parent().parent().find(".purchase-save-nav").find(".cheWarCls");
    viewGoods_forSaleOrder(null, false);
}

function checkWare_forSaleOrder(e) {
    $(".cheWarCls").removeClass();
    $(e).addClass("cheWarCls");
    var url = "/product/api/getProducts";
    if ($(".cheWarCls").val() == -1) {
        url = "/product/mirc-inventory";
    }
    viewGoods_forSaleOrder(null, false, url);
}

//分类
$(".purchase-nav").change(function () {
    viewGoods_forSaleOrder(null, false);
});

function viewWarehouse_forSaleOrder() {
    //加载仓库信息
    var wareUrll = "/inventory/queryWarehouse";
    $.ajax({
        url: wareUrll,
        type: "get",
        dataType: "json",
        async: false,
        success: function (data) {
            var wareHtml = "<span>所属仓库：</span>" +
                "<li value='0' class='cheWarCls' onclick='checkWare_forSaleOrder(this)'>全部</li>";
            if (data.length > 0) {
                for (var i in data) {
                    wareHtml += "<li value='" + data[i].id + "' onclick='checkWare_forSaleOrder(this)'>" + data[i].warehouseName + "</li>";//+data[i].warehouseName+"</li>";
                }
                wareHtml += "<li value='-1'  onclick='checkWare_forSaleOrder(this)'>我的微仓</li>";
                $(".purchase-save-nav").empty();
                $(".purchase-save-nav").append(wareHtml);
            }
        }
    });
}

//加入购物车动画
function addToCart(event) {
    var theImg = $($(event).parent().parent().find('.pro-img')),
        offset = $('.float-addcart').offset(),
        flyer = $('<img class="u-flyer" src=' + theImg.attr('src') + '/>'),
        scrollTOP = $(document).scrollTop(),
        startLeft = theImg.offset().left,
        startTop = theImg.offset().top;

    flyer.fly({
        start: {
            left: startLeft,
            top: startTop - scrollTOP
        },
        end: {
            left: offset.left,
            top: offset.top - scrollTOP + 110,
            width: 20,
            height: 20
        }
    });
}

//查询微仓中的skus
// function lookforMywarehouse(){
//     var skus = [];
//     isnulogin(function(email){
//         ajax_post_sync("/inventory/getIvysAndStorage", JSON.stringify({email:email}), "application/json",
//             function(data) {
//                 var datas = JSON.parse(data);
//                 if (datas && datas.data.list.length > 0) {
//                     var list_p = datas.data.list;
//                     //根据微仓库存倒序显示
//                     list_p.sort(function(a,b){
//                         return b.avaliableStock - a.avaliableStock;
//                     });
//                     $.each(list_p,function(i,item){
//                         skus.push(item.sku);
//                     })
//                 }
//             },
//             function(xhr, status) {
//             }
//         );
//     })
//     return skus;
// }