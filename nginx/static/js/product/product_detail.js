/**
 * Created by Administrator on 2016/1/11.
 */
//全局变量
var sku = undefined; //产品sku
var warehouseId = undefined;//仓库id
var stock = undefined;
var addProductParam = [
    {"warehouseId":"",
        "csku":"",
        "iqty":"",
        "price":"",//采购价
        "publicImg":"",
        "title":"",
        "purchaseCostPrice":"", //分销总成本
        "disProfitRate":"", //分销利润率
        //添加的新属性
        "disProfit":"",//分销毛利润
        "disStockFee":"",//分销操作费
        "disShippingType":"",//分销物流方式
        "disOtherCost":"",//分销其他费用
        "disTransferFee":"",//分销转仓费
        "dislistFee":"",//分销登录费
        "distradeFee":"",//分销平台 交易费
        "dispayFee":"",//分销支付费
        "dispostalFee":"",//分销行邮税
        "disimportTar":"",//分销进口关税
        "disgst":"",//分销消费税
        "disinsurance":"",//分销保险费
        "distotalvat":"",//分销增值税
        "disCifPrice":"",//分销cif价格
        "cost":"",//裸采购价
        "disFreight":"",//分销物流价
        "disStockId":"",//仓库id
        "disPrice":""  //分销价
    }];

$(function () {
    //初始化详情页面
    init(null);
});

function init(wd) {
    //解析url，获取sku
    var srcUrl = window.location.href;
    sku = srcUrl.substring(srcUrl.indexOf("sku=") + 4, srcUrl.indexOf("&"));
    warehouseId = wd!=null?wd:getUrlParam(srcUrl,"warehouseId");
    ajax_get("/member/infor?"+Math.random(), "", "application/json",
        function (response) {
            var model = undefined;
            if(response){
                model = response.distributionMode;
            }
            var url = "/product/api/productDetail?sku=" + sku+"&wd="+warehouseId.replace("&","");
            if(model){
                url += "&md="+model;
            }    
            ajax_get(
                url,JSON.stringify(""),"application/json",
                function (response) {
                    if (response.errCode == 0) {
                        var trans = response.data.trans == null?{}:response.data.trans;
                        var base = response.data.base == null?{}:response.data.base;
                        var batchNumber = base.batchNumber ? base.batchNumber : 1
                        var store = response.data.storage == null?{}:response.data.storage;
                        var image = response.data.images == null?{}:response.data.images;
                        //市场零售价
                        var marketPrice = base.localPrice;
                        //分销成本价
                        // var disTotalCost = base.disTotalCost;
                        //分销利润率
                        // var disProfitRate = base.disProfitRate;
                        //物流费用
                        // var freight = base.disFreight;
                        //分销操作费
                        // var stockFee = base.disStockFee;

                        // var otherCost = base.disOtherCost;
                        //添加产品属性,属性名称详情见变量addProductParam申明处
                        addProductParam[0].price = marketPrice
                        addProductParam[0].disPrice=base.disPrice;
                        addProductParam[0].isSpecial = base.isSpecial;
                        addProductParam[0].specialSale = base.specialSale;
                        //初始化数量为起批量
                        $("#cart-button-qty_new").val(batchNumber);
                        $("#product_weight_td").html('重量：' + base.fweight + 'g');
                        $("#batchNum").val(batchNumber);
                        $('#batchNumShow').html('起发量：'+ batchNumber + '个')

                        $('#productSKU').val(trans.csku);


                        //数据绑定
                        $(".productSKU span").html(trans.csku);//sku
                        $("#productIntroduce_title").html(trans.ctitle);//title
                        //$(".productSpecialPrice_NUM span").html(base.fprice.toFixed(2));//sale

                        marketPrice = base.proposalRetailPrice?base.proposalRetailPrice:"--";
                        //根据模式显示价格
                        var disPrice = base.disPrice;
                        if(!isNaN(disPrice)){
                            disPrice = parseFloat(disPrice).toFixed(2);
                        }
                        if(!isNaN(marketPrice)){
                            marketPrice = parseFloat(marketPrice).toFixed(2);
                        }
                        if(base.isSpecial){
                            disPrice = base.specialSale;
                        }
                        $("#productSpecialPrice_NUM").html(disPrice);
                        $("#localrefpriceNew").html(marketPrice);//local

                        $('#internalCode').html('国际条码：' + base.interBarCode);

                        //保存所属仓库Id
                        $("#warehouseId").val(store.iid);
                        $("#proSales span:eq(1)").html("");//saled
                        $("#stock").html(base.stock + '个');//stock 取产品基本表里的库存
                        $("#canSaleNum").html(base.stock);//stock 取产品基本表里的库存
                        // 如果库存为0，就不要显示加入购物车等按钮
                        if(parseInt(base.stock)<1){
                            $("#buynow").parent().remove()
                            $("#add-to-cart-button").remove()
                            $("#alertMsg").text("库存不足，暂不能采购！").css("color","red").show()
                        }
                        var cdescription = trans.cdescription;
                        try{
                            //desc
                            $(".productDescription_box").html(imgObjReplace($(cdescription),'desc'));
                        }catch(e){
                            console.log("获取商品描述图片异常!"+e);
                        }
                        $("#proSales").find("span:eq(1)").html(base.sales);//sales
                        $(".position").find("li:eq(0)").html(response.data.category);//nav
                        //title
                        $("title").html(trans.ctitle);
                        stock = base.stock;
                        subCount();
                        //img
                        var target = $("#thumblistNew");
                        target.empty();
                        var baseImg = image.images;
                        //默认
                        var def =  $("<a class='ProductLazy' href=''><img src='"+urlReplace(baseImg[0].cimageurl,sku)+"' alt='默认' rel='"+urlReplace(baseImg[0].cimageurl,sku)+"' class='jqzoom'/></a>");
                        $("#defaultImg").append(def);
                        //添加图片组
                        for(var i = 0;i < baseImg.length;i++){
                            var item = $("<li><img src='"
                                +
                                urlReplace(baseImg[i].cimageurl,sku)
                                + "' " +
                                "data-original='"+
                                urlReplace(baseImg[i].cimageurl,sku)
                                +"'" +
                                "mid='"+
                                urlReplace(baseImg[i].cimageurl,sku)
                                +"'big='"+
                                urlReplace(baseImg[i].cimageurl,sku)
                                +"'/></li>");
                            target.append(item);

                            $("a.ProductLazy>img").lazyload({
                                effect: 'fadeIn',
                                threshold: '100'
                            });
                        }

                        $(".goods-detail-l").slide({
                            titCell: "",
                            mainCell: ".goods-detail-ol",
                            autoPage: true,
                            effect: "topLoop",
                            autoPlay: false,
                            vis: 3
                        });

                        target.find("li>img:eq(1)").addClass("current4");
                        //获取仓库信息
                        viewWarehouse(store);
                    }else {
                        alert(response.errMsg);
                        window.location.href = "/personal/personal.html";
                    }
                },    
                function (XMLHttpRequest, textStatus) {
                    alert("获取产品详情出错");
                }
            );
        }
    );
}


//小计
function subCount() {
    var price =  $("#productSpecialPrice_NUM").html();
    if(!isNaN(price)){
       $("#subcount_new").html((parseFloat(price) * parseInt($("#cart-button-qty_new").val())).toFixed(2));
    }
}

// 直接输入数量
$("#cart-button-qty_new").keyup(function(){
    var num = $("#batchNum").val();

    var qty = $.trim($("#cart-button-qty_new").val());
    if(qty==''){
        layer.msg("购买商品数量不能为空！",{icon:2,time:2000});
        $("#cart-button-qty").val(num);
        // return;
    }

    var pattern = /^[1-9]\d*$/;
    if(!pattern.test(qty)){
        layer.msg("购买商品数量只能为正整数！",{icon:2,time:2000});
        $("#cart-button-qty_new").val(qty.substring(0,qty.length-1)==''?num:qty.substring(0,qty.length-1));
        //return;
    }

    if(parseInt(qty) < parseInt(num)) {
        layer.msg("购买商品数量不得低于起发量【" + num + "】",{icon:2,time:2000});
        $("#cart-button-qty_new").val(num);
        //return;
    }

    if(parseInt(qty)>parseInt(stock)){
        layer.msg("添加产品数量将超过商品库存："+stock+",无法添加",{icon:2,time:2000});
        $("#cart-button-qty_new").val(stock);
        //return;
    }

    subCount()
});

//数量加减
$("#cart-button-qty-sub_new").click(function(){
    var num = $("#batchNum").val();
    if(parseInt($("#cart-button-qty_new").val()) <= parseInt(num)) {
        layer.msg("购买商品不得低于起发量【" + num + "】",{icon:2,time:2000});
        return;
    }
    if($("#cart-button-qty_new").val() == 1){
        return;
    }
    $("#cart-button-qty_new").val(parseInt($("#cart-button-qty_new").val()) - 1);
    subCount()
});

$("#cart-button-qty-add_new").click(function(){
    var sum = parseInt($("#cart-button-qty_new").val()) + 1;
    if(parseInt(sum)>parseInt(stock)){
        layer.msg("添加产品数量将超过商品库存："+stock+",无法添加",{icon:2,time:2000});
        return;
    }
    $("#cart-button-qty_new").val(sum);
    subCount()
});

//添加到购物车
$("#add-to-cart-button_new").click(function(){
    addProduct();
});

//立即购买
$("#buynow_new").click(function(){
    addProduct(true);
});

//添加到购物车
function addProduct(flag){
    isnulogin(function(email){

        var iqty = $("#cart-button-qty_new").val();
        var csku = $("#productSKU").val();
        var publicImg = $("#defaultImg a").find("img").attr("src");
        var paramData = {
            "skuList": [csku],
            "warehouseId": warehouseId,
            "publicImg" : publicImg,
            "iqty" : iqty
        };
        $.ajax({
            url: "/cart/pushCart",
            type: "post",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(paramData),
            success: function (response) {
                if(response.result){
                    if(flag){
                        window.location.href = "../cart/shop-cart.html";
                    }else{
                        layer.msg(response.msg,{icon:1,time:2000});
                    }
                } else {
                    layer.msg(response.msg,{icon:2,time:2000});
                }
            }
        });
    });
}


function viewWarehouse(data){
    //加载产品仓库信息
    if(data.length > 0){
        var wareHtml = "";
        for(var i in data){
            //wareHtml += "<li><label><input type=\"radio\" value=\""+data[i].warehouseId+"\" name=\"area\" onchange=\"showProductByWare(this)\"/>"+data[i].warehouseName+"</label></li>";
            //产品来源则根据所属仓库id显示仓库名
            if(data[i].warehouseId == warehouseId){
                $("#productWarehouseName").html('所属仓库：' + data[i].warehouseName);
            }
        }
        //$(".productIntraduce_storage").find("li:eq(0)").siblings().remove();
        //$(".productIntraduce_storage").find("li:eq(0)").after(wareHtml);
        ////自动选中所选产品所在的仓库
        //$("input[value='"+warehouseId+"']").prop("checked","checked");
    }
}

//产品仓库切换动态加载数据
function showProductByWare(radioObj){
    var wareId = $(radioObj).val();
    init(wareId);
}
