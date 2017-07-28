//结算，跳转订单确认页
function settlement() {
    if(flag) {
        var actCheck = $("input[name=active_type]:checked");
        if(actCheck&&actCheck.attr("tag") != 0&&activePlvg){
            ajax_post_sync("/cart/saveActiveInfo",JSON.stringify({"activePlvg":JSON.stringify(activePlvg)}),"application/json",
                function(data){
                    if (data.code) {
                        layer.msg(data.msg, {icon: 6, time: 3000});
                        return;
                    }
                    if(data.suc) {
                        window.location.href = "../product/sure-cart.html";
                    }
                },
                function (XMLHttpRequest, textStatus) {}
            );
        }else{
            delact();
            window.location.href = "../product/sure-cart.html";
        }
    }
}

//删除购物车中已选的活动信息
function delact(){
    ajax_get("/cart/delact","","application/json",
        function(data){},
        function (XMLHttpRequest, textStatus) {}
    );
}


$(document).ready(function () {
        ajax_get(
            "/member/infor",
            "",
            "application/json",
            function (response) {
                if (response.id != null && response.id != 'null' && response.id != '' && response.id != undefined) {
                    showCartData(response);
                } else {
                    window.location.href = "/personal/login.html";
                }
            },
            function (XMLHttpRequest, textStatus) {
                layer.msg("获取用户折扣出错！", {icon: 2, time: 2000});
            }
        );
});

var info = {};
// 显示购物车商品
function showCartData(res) {
    info = res;
    ajax_get("/cart/getcartdata?" + Math.random(), "", "application/json",
        function (response) {
            if (response.code) {
                layer.msg(response.msg, {icon: 6, time: 3000});
                return;
            }
            hidePromotion();
            if (response.result == false) {
                var cartHtml = "<tr>" +
                    "<td colspan='7' style='width:100%;' id='nothing-cart'>" +
                    "<span></span>" +
                    "<span>购物车是空的，快去<a href='/personal/personal.html?emnunum=3'>添加商品</a>吧！</span>" +
                    "</td>" +
                    "</tr>";
                $(".product_table").find(".product_content").append(cartHtml);
                $(".freight").remove();
                $(".promotion_condition").hide();
                count();
            } else {
                var sum = 0;
                for (var i = 0, len = response.cartData.length; i < len; i++) {
                    var val = response.cartData[i];
                    var itemId = val.itemId;
                    var sku = val.sku;
                    var price = val.disPrice;
                    var discountPrice = val.discountPrice;
                    var title = val.title;
                    var iqty = val.qty;
                    var publicImg = val.image;
                    var sumPrice = val.sumprice;
                    var selected = val.selected;
                    var warehouseId = val.warehouseId;//仓库暂时默认为1
                    var whName = val.storageName;
                    var batchnum = val.batchnum;
                    var categoryId = val.categoryId ? val.categoryId : -1;
                    var fweight = val.fweight;
					var istatus=val.istatus;
                    sum += parseFloat(sumPrice);
					var productContentHtml ="";
					if(istatus==1){
						productContentHtml = "<tr data-whid='" + warehouseId + "' data-ssku='" + sku + "' data-categoryId='" + categoryId + "' data-fweight='" + fweight + "'>" +
                            "<td class='cart-select'>" +
                            "<input type='checkbox' tag='" + itemId + "' name='orderCheckBox' value='" + itemId + "'" + (selected ? "checked='checked'" : "") + " onclick='cart_item_single(this);' class='checkbox-child'/>" +
                            "</td>" +
                            "<td id='product_descrption' class='cart-goods'>" +
                            "<a target='_blank' href='../../product/product-detail.html?sku=" + sku + "&warehouseId=" + warehouseId + "'>" +
                            "<img src='" + publicImg + "' />" +
                            "</a>" +
                            "<p class='word-break'>" +
                            "<a target='_blank' href='../../product/product-detail.html?sku=" + sku + "&warehouseId=" + warehouseId + "'>" +
                            "（"+sku+"）"+ title.substring(0, title.length - 5) +
                            "</a>" +
                            "</p>" +
                            "<td class='cart-warehouse'>" + whName + "</td>";
					}else{
						productContentHtml = "<tr data-whid='" + warehouseId + "' data-ssku='" + sku + "' data-categoryId='" + categoryId + "' data-fweight='" + fweight + "'>" +
                            "<td class='cart-select'><input type='checkbox' disabled='true' tag='" + itemId + "' name='orderCheckBox' value='" + itemId + "'" + (selected ? "" : "") + " onclick='' class=''/></td>" +
                            "<td id='product_descrption' class='cart-goods'>" +
                            "<a target='_blank' href='../../product/product-detail.html?sku=" + sku + "&warehouseId=" + warehouseId + "'>" +
                            "<img src='" + publicImg + "' />" +
                            "</a><p class='word-break'>" +
                            "<a target='_blank' href='../../product/product-detail.html?sku=" + sku + "&warehouseId=" + warehouseId + "'>" +
                            "（"+sku+"）"+title.substring(0, title.length - 5) +
                            "<font color='red'>（该商品暂不可购买！）</font>" +
                            "</a>" +
                            "</p>" +
                            "<td class='cart-warehouse'>" + whName + "</td>";
					}
                    productContentHtml += "<td class='cart-price'>￥<b>" + price.toFixed(2) + "</b></td>";
                    productContentHtml += "<td class='cart-num'>"
                        + "<ul class='cart_num'>"
                        + "<li id='cart_num_Reduction' type='button' name='submit.add-to-cart' batch='"+ batchnum +"' onclick='totalPrice(this,false," + itemId + ")'>-"
                        + "</li>"
                        + "<li id='cart_nums'>"
                        + "<input id='cart-button-num' type='text' value='" + iqty + "' data-qty='" + iqty + "' name='qty' onblur='iptChange(this," + itemId + ")'>"
                        + "</li>"
                        + "<li id='cart_num_add' type='button' name='submit.add-to-cart' batch='"+ batchnum +"' onclick='totalPrice(this,true," + itemId + ")'>+"
                        + "</li>"
                        + "</ul>"
                        + "</td>"
                        + "<td class='cart-rental'><span class='red'>￥<b>" + sumPrice.toFixed(2) + "</b></span></td>"
                        + "<td class='cart-operation'>"
                        + "<span class='delete' onclick='deleteProduct(this)'><font>×</font></span>"
                        + "<p class='deletePop' style='display: none;margin-left: -80px;'>"
                        + "<i></i>确认要删除该商品吗？<br>"
                        + "<input onclick='deleteProduct(this," + itemId + ")' type='button' value='是'>"
                        + "<input onclick='deleteProduct(this)' type='button' value='否'>"
                        + "</p>"
                        + "</td>"
                        + "</tr>";
                    $("#product_content").append(productContentHtml);
                }
                count();
                //xse add 获取活动
                refreshActive();
            }
        },
        function (XMLHttpRequest, textStatus) {
            layer.msg("显示购物车列表出错！", {icon: 2, time: 2000});
        }
    );
}

/**
 * 删除购物车中的产品
 * @param {Object} obj
 * @param {Object} itemId
 */
function deleteProduct(obj, itemId) {
    if (arguments.length == 1) {  //弹出是否删除询问框
        $(obj).get(0).tagName == 'INPUT' ? $(obj).parent().toggle() : $(".delete").next().hide() && $(obj).siblings(".deletePop").toggle();
    } else {
        ajax_post("/cart/removeitem", JSON.stringify({"itemId": itemId}), "application/json",
            function (response) {
                if (response.code) {
                    layer.msg(response.msg, {icon: 6, time: 3000});
                    return;
                }
                if (response.result == true) {
                    delact();
                    $("#cartItemCountNew").text($("#cartItemCount").text() - 1);
                    $(obj).parent().parent().parent().remove();
                    var trTag = $(obj).parent().parent().parent().find("tr");
                    if (trTag == undefined || trTag == null || trTag.length <= 0) {
                        $(".freight").remove();
                    }

                    count();
                    refreshActive();
                    $(".clear-suc").fadeIn();
                    setTimeout(function () {
                        $(".clear-suc").fadeOut()
                    }, 2000)

                    if($('#product_content').find('tr').length == 0){
                        var cartHtml = "<tr>" +
                            "<td colspan=\"7\" style=\"width:100%;\" id=\"nothing-cart\">" +
                            "<span></span>" +
                            "<span>购物车是空的，快去<a href='/personal/personal.html?emnunum=3'>添加商品</a>吧！</span></td>" +
                            "</tr>";
                        $(".product_table").find(".product_content").append(cartHtml);
                        hidePromotion();
                        $(".promotion_condition").hide();
                    }
                } else {
                    layer.msg(response.msg, {icon: 2, time: 2000});
                }
            },
            function (XMLHttpRequest, textStatus) {
                $(".clear-fail").fadeIn();
                setTimeout(function () {
                    $(".clear-fail").fadeOut()
                }, 2000)
            }
        );
    }
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
        updateqty(params, sourceQty, $(obj).parent());
        return;
    }
    var params = {
        "itemId": itemId,
        "qty": quantity
    };
    flag = true;
    updateqty(params, sourceQty, $(obj).parent());
}

var flag = true;
//直接更改输入框中的商品数量
function iptChange(obj, itemId) {
    var quantity = $(obj).val(), sourceQty = parseInt($(obj).data("qty"));
    if (!/\d/.test(quantity) || parseInt(quantity) <= 0) {
        layer.msg("请输入数字(必须是大于0的整数)", {icon: 2, time: 2000});
        $(obj).val(sourceQty);
        return;
    }
    flag = true;
    var batch = $(obj).parent().prev().attr("batch");
    if(parseInt(quantity) < parseInt(batch)) {
        flag = false;
        layer.msg("购买数量必须大于或者等于商品起批量【" + batch + "】", {icon: 2, time: 2000});
        return;
    }
    var params = {
        "itemId": itemId,
        "qty": parseInt(quantity)
    };
    //TODO 云仓库存校验
    updateqty(params, sourceQty, $(obj).parent().parent());
}


function updateqty(params,sourceQty,$node){
     ajax_post("/cart/updateqty", JSON.stringify(params), "application/json",
        function (response) {
            if (response.code) {
                layer.msg(response.msg, {icon: 6, time: 3000});
                return;
            }
             if (response.result) {
                delact();
                $node.find("input").val(params.qty);
                $node.find("input").data("qty", params.qty);
                var unitPrice = $node.parent().prev().find("b").text();
                $node.parent().next().find("span").find("b").text((unitPrice * params.qty).toFixed(2));
                count();
                refreshActive();
            } else {
                layer.msg("修改后的数量(" + params.qty + ")将超出库存", {icon: 2, time: 2000});
                $node.find("input").val(sourceQty);
            }
        },function(xhr, status){
            console.log("error--->" + status);
        }
    );    
}

//计算总价格和总数量
function count() {
    all_select_state();
    var ipts = new Array(),
        sumNode = new Array(),
        tPrice = 0,
        quantity = 0;
    $.each($(".checkbox-child"), function (i, e) {
        if (e.checked) {
            var tds = $(e).parent().parent().children();
            ipts.push(tds.eq(4).find("input")[0]);
            sumNode.push(tds.eq(5)[0]);
        }
    });
    if (ipts.length == 0) {
        $(".product_num").html(0);
        $(".all_price").html(0);
        $("#totolPrice").html(0);
        $(".p-mainprice").html(0);
    } else {
        for (var s = 0; s < ipts.length; s++) {
            if ($($(ipts[s]).closest('tr').children().get(0)).children().prop('checked')) {
                quantity += parseFloat($(ipts[s]).val());
            }
        }
        for (var i = 0; i < sumNode.length; i++) {
            if ($($(ipts[i]).closest('tr').children().get(0)).children().prop('checked')) {
                tPrice += parseFloat($(sumNode[i]).children("span").find("b").text());
            }
        }
        $(".product_num").html(quantity);
        var p = tPrice.toFixed(2);
        $(".all_price").attr("sum",p).html(p);
        $("#totolPrice").html(parseFloat(tPrice).toFixed(2));

        var couponsCost = parseFloat($("#couponsCost").text()).toFixed(2);
        if (tPrice > couponsCost) {
            $(".p-mainprice").html(parseFloat(parseFloat(tPrice).toFixed(2) - couponsCost).toFixed(2));
        } else {
            $(".p-mainprice").html(0.00);
        }
    }
    if (quantity == "0") {
        forbidden($("#accounts"));
    } else {
        startUsing($("#accounts"));
    }
}

function forbidden(obj){
    obj.css('backgroundColor', '#ccc').attr('disabled', true);
}
function startUsing(obj){
    obj.css('backgroundColor', '#117ad4').attr('disabled', false);
}

//全选复选框是否选中，只有购物车中所有商品选中，全选复选框才是选中的。
function all_select_state() {
    var number = 0;
    $(".checkbox-child").each(function () {
        number += this.checked ? 1 : 0;
    });
    $(".checkbox-top")[0].checked = number == $(".checkbox-child").length ? true : false;
    $("#accounts").css("cursor", number > 0 ? "pointer" : "not-allowed");
}

//商品全选
function cart_select_all(node) {
    $(".checkbox-child").each(function () {
        this.checked = node.checked;
    });
    isnulogin(function (email) {
        delact();
        var params = JSON.stringify({cart: email, cartstate: node.checked});
        ajax_post("/cart/updateSelectState", params, "application/json",
            function (data) {
                if (data.code) {
                    layer.msg(data.msg, {icon: 6, time: 3000});
                    return;
                }
                if (data.suc) {
                    count();
                    refreshActive();
                } else {
                    layer.msg(data.msg, {icon: 2, time: 2000});
                }
            },
            function (xhr, status) {
                console.error("status-->" + status);
            }
        );
    });
}

//商品单选
function cart_item_single(node) {
    all_select_state();
    isnulogin(function (email) {
        delact();
        var params = JSON.stringify({item: node.value, itemstate: node.checked});
        ajax_post_sync("/cart/updateSelectState", params, "application/json",
            function (data) {
                if (data.code) {
                    layer.msg(data.msg, {icon: 6, time: 3000});
                    return;
                }
                if (data.suc) {
                    count();
                    refreshActive();
                } else {
                    layer.msg(data.msg, {icon: 2, time: 2000});
                }
            },
            function (xhr, status) {
                console.error("status-->" + status);
            }
        );
    });
}

function refreshActive(actId){
    if(actId == 0) {
        $(".all_price").html($(".all_price").attr("sum"));
        $(".gift_promotion").hide();
        $(".promotion_discount").hide();
        delact();
        return;
    }
    var totalNumber = 0;
    var skus = [];

    ajax_get("/cart/getcartdata?" + Math.random(), "", "application/json",
        function (response) {
            if (response.code) {
                layer.msg(response.msg, {icon: 6, time: 3000});
                return;
            }
            if(response.result == false){
                hidePromotion();
                $(".promotion_condition").hide();
                return;
            }
            var money = 0.00;
            $.each(response.cartData,function(i,item){
                if(item.selected){
                    var skuParam = {
                        "sku": item.sku,
                        "commodityCategoryId": item.categoryId?item.categoryId:-1,//商品类目
                        "warehouseId": item.warehouseId,
                        "number":item.qty,
                        "totalPrice":parseFloat(item.sumprice).toFixed(2)
                    }
                    money += Number(skuParam.totalPrice);
                    totalNumber += parseInt(skuParam.number);
                    skus.push(skuParam);
                }
            });
            if(skus.length >0){
               var conpons = {
                    "userAttr":info.comsumerType,
                    "account" : info.email,
                    "userMode":info.distributionMode,
                    "money":parseFloat(money).toFixed(2),
                    "commodity":skus,
                    "totalNumber" : totalNumber
                    };
                if(actId){
                    var param = {
                        "actId":actId,
                        "pros":conpons
                    }
                    show_init_promotion(param)
                }else{
                    getPromotion(conpons);    
                } 
            }else{
                hidePromotion();
                $(".promotion_condition").hide();
            }
        },function(e){
            console.log(e);
        }
    );
}

function hidePromotion(){
    $(".cart_promotion_line").hide();
    $(".promotion_discount").hide();
    $(".gift_promotion").hide();
    activePlvg = undefined;
    $("#active_0").siblings().remove();
    delact();
}

function getPromotion(param){
    if(!param) {
        hidePromotion();
        return;
    }
    forbidden($("#accounts"));
    ajax_post_sync("/market/pro/act/check",JSON.stringify(param),"application/json",
        function(data){
            startUsing($("#accounts"));
            if(data && data.length > 0) {
                promotion(data);
                //选中第一个
                $("input[name=active_type]:eq("+index+")").click();
                $(".promotion_condition").show()
            } else {
                hidePromotion();
                $(".promotion_condition").hide();
            }
        },function (xhr, status) {
            hidePromotion();
            startUsing($("#accounts"));
            $(".promotion_condition").hide();
        }
    );
}

var activePlvg;
var index = 0;//被选中的活动
function show_init_promotion(param){
    if(info.distributionMode != 1){
        hidePromotion();
        return;
    }
    forbidden($("#accounts"));
    ajax_post("/market/pro/act/excute",JSON.stringify(param),"application/json",
        function(data){
            startUsing($("#accounts"));
            $(".gift_promotion").empty();
            if(data.suc){
                activePlvg = {
                    plvg:data,
                    proActName:$("input[name=active_type][tag="+param.actId+"]").parent().text()
                };
                var gift = data.gift;
                var giftHtml = "";
                if(gift.length >0){
                    //判断重复，如果重复，数量叠加
                    $.each(gift,function(i,item){
                        giftHtml += '<div >'+
                            '<b class="red">[<span>赠品</span>]</b>'+
                            '<div>'+
                            '<span class="gift_name" data-qty="'+item.num+'" data.sku="'+item.sku+'" data-wid="'+item.warehouseId+'">'+item.cTitle+'</span>'+
                            '<span class="gift_num">x<em>'+ item.num+'</em></span>'+
                            '</div>'+
                            '</div>';  
                    });
                    $(".gift_promotion").prepend(giftHtml);
                    $(".gift_promotion").show();
                }else{
                    $(".gift_promotion").hide();
                }
                $(".all_price").html(data.sum>0?data.sum:0.00);
                //如果两个值不等，说明有打折
                if(data.sum != param.pros.money){
                    $(".promotion_discount").show();
                    $(".promotion_price").text((parseFloat($(".all_price").attr("sum")) - data.sum).toFixed(2));
                }else{
                    $(".promotion_discount").hide();
                }
            }else{
                layer.msg(data.msg,{icon:2,time:2000});
            }
        },function(e){
            startUsing($("#accounts"));
            console.log(e);
        }
     );
}

function promotion(data){
    $(".promotion_line_detail").empty();
    $("#active_0").siblings().remove();
    $(".gift_promotion div").remove();
    var htmlCode = "";
    var radioCode = "";
    // var gift = "";
    // var giftValue = {};
    if (info.distributionMode == 1) {
        $.each(data,function(i,item){//活动
            htmlCode += '<span>'+
                '<em>'+item.name+'：</em>'+
                '<b class="promotion_tame_start"> '+item.startTime+' </b> 至'+
                '<b class="promotion_tame_end"> '+item.endTime+' </b> 期间，'+
                '<d class="">'+item.description+'</d>'+
                '</span>' +
                '<div class="clear"></div>';;
            radioCode += '<p><label><input index="'+i+'" tag="'+item.id+'" type="radio" name="active_type">'+item.name+'</label></p>';
        });
    } else {
        $.each(data,function(i,item){//活动
            htmlCode += '<span>'+
                '<em>'+item.name+'：</em>'+
                '<b class="promotion_tame_start"> '+item.startTime+' </b> 至'+
                '<b class="promotion_tame_end"> '+item.endTime+' </b> 期间有效'+
                '</span>' +
                '<div class="clear"></div>';;
        });
        $(".promotion_condition").remove();
    }
    $("#active_0").before(radioCode);
    $(".promotion_line_detail").prepend(htmlCode);
    $(".cart_promotion_line").show();
    $("input[name=active_type]").click(function(){
        index = $(this).attr("index");
        refreshActive($(this).attr("tag"));
    });
}
