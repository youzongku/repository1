require.config({
    baseUrl : "../js/",
    paths : {
        "jquery" : "lib/jquery-1.11.3.min",
        "common" : "common",
        "layer" : "lib/layer2.0/layer",
        "search" : "product/search",
        "Categorys" : "product/Categorys",
        "laypage": "lib/laypage1.3/laypage",
        "scroll":"lib/mCustomScrollbar/jquery.mCustomScrollbar.min"
    },
    shim : {
        "common" : {
            exports : "",
            deps: ["jquery","scroll"]
        },
        "laypage": {
            exports: "",
            deps: []
        },
        "layer" : {
            exports : "",
            deps : ["jquery"]
        },
        "scroll" :{
            exports : "",
            deps : ["jquery"]
        },
        "search" : {
            exports : "",
            deps : ["jquery","common","layer"]
        }
    }
});

require(["jquery","common","layer","laypage"], function($,Common,layer,laypage) {
    $(function(){
        var s = getQueryString("s");
        var param = {
            currPage:1,
            pageSize:10,
            title: s
        };
        show_search(param);
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

    function show_search(param) {
        ajax_post("/product/elastic-search/products", JSON.stringify({data:param}), "application/json",
            function (response) {
                console.log(response);
                $("#product-list").empty();
                $("#pagination_product").empty();
                $("#total").text(1);
                if(response) {
                    var data = response.data;
                    if(data.result && data.result.length > 0) {
                        var htmlCode = "";
                        var disPrice = 0;
                        $.each(data.result,function(i,item){
                            disPrice = item.disPrice;
                            if(item.isSpecial){
                                disPrice = item.specialSale;
                            }

                            if(item.disPrice != null) {
                                if(i % 5 == 4) {
                                    htmlCode += '<li style="margin-right: 0;">';
                                } else {
                                    htmlCode += '<li>';
                                }
                                htmlCode += '<div class="product-top"> <div>';
                                htmlCode += '<a href="../product/product-detail.html?sku=' + item.csku + '&warehouseId=' + item.warehouseId + '" target="_blank">';
                                htmlCode += '<img src="'+urlReplace(item.imageUrl, item.csku,"",180,180,100)+'" alt="" />';
                                htmlCode += '</a>';
                                htmlCode += '</div> <p class="bar-code">国际条码：' + item.interBarCode + '</p>';
                                htmlCode += '<input type="hidden" id="sku" value="'+ item.csku +'">';
                                htmlCode += '<input type="hidden" id="warehouseId" value="'+ item.warehouseId +'">';
                                htmlCode += '<input type="hidden" id="publicImg" value="'+ item.imageUrl +'">';
                                htmlCode += '<a href="../product/product-detail.html?sku=' + item.csku + '&warehouseId=' + item.warehouseId + '" target="_blank">';
                                htmlCode += '<p class="product-title">' + item.ctitle + '</p>';
                                htmlCode += '</a>';
                                htmlCode += '<p class="product-price">¥' + disPrice + '</p>';
                                htmlCode += '</div> <div class="product-bottom"> <span class="bor-r-org" id="buynow_new">';
                                htmlCode += '立即采购</span> <span id="add-to-cart-button_new">加入购物车</span> </div></li>';
                            }
                        });
                        $("#product-list").append(htmlCode);
                        init_pagination_search(data.totalPage,data.currPage,param);
                    }else {
                        $('#product-list').html('<h3>查询结果为空</h3>');
                        $('#product-list').addClass('goods-category-none');
                    }
                } else {
                    $('#product-list').html('<h3>查询结果为空</h3>');
                    $('#product-list').addClass('goods-category-none');
                }
            },
            function (XMLHttpRequest, textStatus) {
                $('#product-list').html('<h3>查询结果为空</h3>');
                $('#product-list').addClass('goods-category-none');
            }
        );
    }

    function init_pagination_search(total,currPage,param) {
        $("#pagination_product").empty();
        laypage({
            cont: 'pagination_product',
            pages: total,
            curr: currPage,
            groups: 5,
            skin: 'yahei',
            first: '首页',
            last: '尾页',
            prev: '上一页',
            next: '下一页',
            skip: true,
            jump: function(obj, first) {
                //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                if(!first){
                    param.currPage = obj.curr;
                    show_search(param);
                }
            }
        });
    }
});
