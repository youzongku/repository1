require.config({
    baseUrl : "../js/",
    paths : {
        "jquery" : "lib/jquery-1.11.3.min",
        "common" : "common",
        "layer" : "lib/layer2.0/layer",
        "subpage" : "product/subpage",
        "Categorys" : "product/Categorys",
        "laypage": "lib/laypage1.3/laypage",
        "scroll":"lib/mCustomScrollbar/jquery.mCustomScrollbar.min"
    },
    shim : {
        "common" : {
            exports : "",
            deps : ["jquery","scroll"]
        },
        "laypage": {
            exports: "",
            deps: []
        },
        "layer" : {
            exports : "",
            deps : ["jquery"]
        },
        "subpage" : {
            exports : "",
            deps : ["jquery","common","layer"]
        }
    }
});

require(["jquery","common","layer","laypage","Categorys"], function($,Common,layer,laypage,Categorys) {
    var cate = new Categorys();
    var vcId = GetQueryString("v");
    var param = {"pageSize":10,"currPage":1,"categoryId":vcId};
    cate.show(param);
    cate.category(vcId);
    // cate.cateDetail(vcId);
    cate.getWarehouse();
    cate.parentCategory(vcId);
    $(".m-select").change(function(){
        param.pageSize = $(".m-select").val();
        param.currPage = 1;
        cate.show(param);
    });

    $("#price_search").click(function(){
        var min = $("#priceStart").val();
        var max = $("#priceEnd").val();
        param.currPage = 1;
        param.pageSize = 10;
        if(!isNaN(min)) {
            param.minPrice = min;
        } else {
            $("#priceStart").val("");
        }
        if(!isNaN(max)) {
            param.maxPrice = max;
        } else {
            $("#priceEnd").val("");
        }
        cate.show(param);
    });

    // 点击分类
    $('body').on('click','.brands a',function(){
        if ($(this).data("level")) {
            $(".selectedCategory").removeClass("selectedCategory");
            $(this).addClass("selectedCategory");
        }
        var param = {};
        var warehouseId = $('#selectWarehouse').attr('tag');
        if(warehouseId) {
            param.warehouseId = warehouseId;
        }

        param.categoryId = $(this).attr("tag");
        var min = $("#priceStart").val();
        var max = $("#priceEnd").val();
        param.currPage = 1;
        param.pageSize = 10;
        if(!isNaN(min)) {
            param.minPrice = min;
        } else {
            $("#priceStart").val("");
        }
        if(!isNaN(max)) {
            param.maxPrice = max;
        } else {
            $("#priceEnd").val("");
        }
        if($(this).data("level") == 3) {
            cate.category($(this).attr("tag"));
            cate.parentCategory($(this).attr("tag"));
        }
        if(param.categoryId) {
            cate.show(param);
        }
    });

    //添加到购物车
    $('body').on('click','#add-to-cart-button_new',function(){
        addProduct(this,false);
    });

    //立即购买
    $('body').on('click','#buynow_new',function(){
        addProduct(this,true);
    });

    //添加到购物车
    function addProduct(obj,flag){
        isnulogin(function(email){
            var csku = $(obj).parent().parent().find('#sku').val();
            var warehouseid = $(obj).parent().parent().find('#warehouseId').val();
            var publicImg = $(obj).parent().parent().find('#publicImg').val();
            var paramData = {
                "skuList": [csku],
                "warehouseId": warehouseid,
                "publicImg" : publicImg,
                "iqty" : 1
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

    //按仓库查询
    $('body').on('click','#searchWarehouse li',function(){
        $('#selectWarehouse').html($(this).attr('warehouseName'));
        $('#selectWarehouse').attr('tag',$(this).attr('tag'));

        var param = {};

        $('.brands a').each(function(i, item){
            if($(this).hasClass('selectedCategory')) {
                param.categoryId = $(this).attr("tag");
            }
        });

        $('.crumbs-l a').each(function(i, item){
            if($(this).hasClass('selectedCategory')) {
                param.categoryId = $(this).data("cid");
            }
        });
        var min = $("#priceStart").val();
        var max = $("#priceEnd").val();
        param.currPage = 1;
        param.pageSize = 10;
        if(!isNaN(min)) {
            param.minPrice = min;
        } else {
            $("#priceStart").val("");
        }
        if(!isNaN(max)) {
            param.maxPrice = max;
        } else {
            $("#priceEnd").val("");
        }

        param.warehouseId = $(this).attr('tag');
        cate.show(param);
    });

    //按价格排序
    $('body').on('click','.priceSort li',function(){
        var param = {};

        $('.brands a').each(function(i, item){
            if($(this).hasClass('selectedCategory')) {
                param.categoryId = $(this).attr("tag");
            }
        });

        $('.crumbs-l a').each(function(i, item){
            if($(this).hasClass('selectedCategory')) {
                param.categoryId = $(this).data("cid");
            }
        });
        var min = $("#priceStart").val();
        var max = $("#priceEnd").val();
        param.currPage = 1;
        param.pageSize = 10;
        if(!isNaN(min)) {
            param.minPrice = min;
        } else {
            $("#priceStart").val("");
        }
        if(!isNaN(max)) {
            param.maxPrice = max;
        } else {
            $("#priceEnd").val("");
        }

        var warehouseId = $('#selectWarehouse').attr('tag');
        if(warehouseId) {
            param.warehouseId = warehouseId;
        }

        param.disPriceSort = $(this).data('sort');
        cate.show(param);
    });


    // 点击导航：当前分类
    $('body').on('click','.currCate',function(){
        var warehouseId = $('#selectWarehouse').attr('tag');
        var p = {"categoryId":$(this).data("cid")};
        if(warehouseId) {
            p.warehouseId = warehouseId;
        }
        p.currPage = 1;
        p.pageSize = 10;
        $(".selectedCategory").removeClass("selectedCategory");
        $(this).addClass("selectedCategory");
        cate.show(p);
    });


    // 右箭头点击事件
    $(".simplePage .go").click(function(){
        var flag = $(this).attr("tag");
        var curr = parseInt($("#curr").text());
        var total = parseInt($("#total").text());
        if(flag == "true") {
            param.currPage = curr + 1;
        } else {
            param.currPage = curr - 1;
        }

        if(param.currPage > 0 && param.currPage <= total) {
            // TODO 暂时解决方案：修改类别（地址栏的v参数没有修改）
            param.categoryId = $(".selectedCategory").attr("tag");
            $("#curr").text(param.currPage);
            cate.show(param);
        }
    });

    //gain_activity_goods();

    var titleTxtArray = new Array()
    $("span[class='currCate']").each(function(i,e){
        titleTxtArray.push($(e).text())
    });
    if(titleTxtArray.length>0){
        $("title").text(titleTxtArray.toString())
    }
});

function gain_activity_goods() {
    var id = GetQueryString("id");
    if (id != undefined && id != '' && id != null && id != 'null') {
        ajax_get("../product/mktool/getactinfo?id=" + id + "&t=" + Math.random(), "", "",
            function(data) {
                if (data.suc) {
                    var act = data.info.act;
                    var imgs = data.info.imgs;
                    var pros = data.info.pros;
                    var itemHTML = '';
                    /*$.each(imgs, function(i, item) {
                     itemHTML +=
                     '<li><a href="javascript:;"><img src="../product/spact/poster?id=' + item.id + '"></a></li>';
                     });
                     $(".special_content_one .special_wrap_one").html(itemHTML);*/
                    itemHTML = '';
                    $.each(pros, function(i, item) {
                        itemHTML +=
                            '<li class="goods">' +
                            '<div class="goodswrap">' +
                            '<div class="goodsimg">' +
                            '<a href="../product/product-detail.html?sku=' + item.csku + '&warehouseId=' + item.warehouseId + '" title="' + item.ctitle + '">' +
                            '<img src="' + item.imageUrl + '"></a>' +
                            '</div>' +
                            '<div class="desc clearfix">' +
                            '<div class="titlewrap">' +
                            '<a class="title" title="' + item.ctitle + '" href="../product/product-detail.html?sku=' + item.csku + '&warehouseId=' + item.warehouseId + '">' +
                            '<h2>' + item.ctitle + '</h2>' +
                            '</a>' +
                            '</div>' +
                            '<p class="price">' +
                            '<span class="cur">' +
                            '<i>¥</i>' + item.specialPrice +
                            '</span>' +
                            '<span class="marketprice">立省<span>' + ((parseFloat(item.localPrice) - parseFloat(item.specialPrice)).toFixed(2)) + '</span>元</span>' +
                            '</p>' +
                            '</div>' +
                            '</div>' +
                            '</li>';
                    });
                    $(".special_content_two .special_wrap_two").html(itemHTML);
                } else {
                    layer.alert(data.msg, {icon: 2});
                }
            },
            function(xhr, status) { console.log("error--->" + status); }
        );
    } else {
        console.log("id----->" + id);
    }
}