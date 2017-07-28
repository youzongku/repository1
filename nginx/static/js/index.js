require.config({
    baseUrl: "../js/",
    paths: {
        "jquery": "lib/jquery-1.11.3.min",
        "common": "common",
        "index": "index",
        "layer": "lib/layer2.0/layer",
        "wechat": "payment/wechat",
        "qrcode": "payment/qrcode",
        "scroll":"lib/mCustomScrollbar/jquery.mCustomScrollbar.min",
        "slide":"lib/jquery.SuperSlide.2.1.1"
    },
    shim: {
        "common": {
            exports: "",
            deps: ["jquery","scroll"]
        },
        "index": {
            exports: "",
            deps: ["jquery"]
        },
        "layer": {
            exports: "",
            deps: ["jquery"]
        },
        "wechat": {
            exports: "",
            deps: ["jquery", "common", "layer", "qrcode"]
        },
        "scroll":{
            exports: "",
            deps: ["jquery"]
        },
        "slide":{
            exports: "",
            deps: ["jquery"]
        }
    }
});


require(["jquery","layer","common","scroll","slide"], function ($,layer) {
    //init_index_product();
    init_bbc_categor();
    //展示特价活动
    //show_sprice_activity();
    init_banner();

    $(window).scroll(function(event) {
        var val=$(document).scrollTop();
        if(val==0){
            $("#subNav").stop().fadeOut("fast");
        }
        if(val>0){
            $("#subNav").stop().fadeIn("fast");
        }
    });

    $(document).ready(function(){
        $('.search span:eq(0)').click(function(){
            var searchContent = $('.search input:eq(0)').val().trim();
            var s = encodeURI(searchContent);
            window.location = '/product/product_search.html?s='+ s;
        });

        if ($(".page-head-login span")[0] != undefined && $(".page-head-login span")[1] != undefined) {
            var url = "../member/isnulogin?" + (new Date()).getTime();
            $.ajax({
                url: url,
                type: "get",
                dataType: "json",
                async: false,
                success: function (data) {
                    if (data.suc) {
                        var user = data.user.email;
                        if (user) {
                            //getCartQty();
                            $('#LoginBeforeMenu').addClass("hide");
                            $('#LoginAfterMenu').removeClass("hide");
                            $('#LoginAfterMenu').find('.hotB').html(data.user.email);
                            $('#LoginAfterMenu').find('.user-type').html(formatterDistributionmode(data.user.distributionmode));
                            $('#LoginAfterMenu').addClass("page-head-login-box");

                            $(".out").click(function () {
                                $.ajax({
                                    url: "/member/logout?" + (new Date()).getTime(),
                                    type: "GET",
                                    async: false,
                                    success: function (data) {
                                        window.location.href = "/";
                                    }
                                });
                            });
                            getCartQty();
                        }
                    }
                }
            });
        }

        /*input,textarea的placeholder 兼容ie8的代码片段 start*/
        if( !('placeholder' in document.createElement('input')) ){
            $('input[placeholder],textarea[placeholder]').each(function(){
                var that = $(this),
                    text= that.attr('placeholder');
                if(that.val()===""){
                    that.val(text).addClass('placeholder');
                }
                that.focus(function(){
                    if(that.val()===text){
                        that.val("").removeClass('placeholder');
                    }
                })
                    .blur(function(){
                        if(that.val()===""){
                            that.val(text).addClass('placeholder');
                        }
                    })
                    .closest('form').submit(function(){
                        if(that.val() === text){
                            that.val('');
                        }
                    });
            });
        }
        /*input,textarea的placeholder 兼容ie8的代码片段 end*/

        /*滚动条 start*/
        (function($){
            $(window).load(function(){
                $(".content-nav-child").mCustomScrollbar({
                    themes:"minimal-dark",
                    advanced:{ updateOnBrowserResize:true },
                    scrollInertia:300
                });
            });
        })($);
        /*滚动条 end*/
        /*左侧导航 end*/

        /*banner轮播效果 start*/
        var $key=0;
        $(".content-top-right ol li").mousemove(function() {
            $(this).addClass('current-circle').siblings().removeClass('current-circle');
            $(".content-top-right ul li").eq($(this).index()).stop().fadeIn("fast").siblings().stop().fadeOut("fast");
            $('.content-top').css('background',$(".content-top-right ol li[class='current-circle']").data('color'));
            $key=$(this).index();
            //$('.content-top').css('background',$(".banner-r-img ul li").hasClass('current3').data('color'));
        });
        var timer=setInterval(autoplay, 1500);
        function autoplay(){
            $key++;
            if($key>5){
                $key=0;
            }
            $(".content-top-right ol li").eq($key).addClass('current-circle').siblings().removeClass('current-circle');
            $(".content-top-right ul li").eq($key).stop().fadeIn("fast").siblings().stop().fadeOut("fast");
            $('.content-top').css('background',$(".content-top-right ol li[class='current-circle']").data('color'));
        }
        $(".content-top-right").hover(function() {
            clearInterval(timer);
        }, function() {
            clearInterval(timer);
            timer=setInterval(autoplay, 1500);
        });
        /*banner轮播效果 end*/

        /*tab切换 start*/
        //jQuery(".content-category-cont").slide();
        /*tab切换 end*/

        /*浮动导航 start*/
        /*判断是否滚动屏幕*/
        $(window).scroll(function(event) {
            /*检测盒子距离页面顶端距离*/
            //判断类目是否存在 start
            if($('#contCate1').length>0){
                var a=$("#contCate1").offset().top;
            }else{
                $("#subNavL .a").hide();
            }

            if($('#contCate2').length>0){
                var b=$("#contCate2").offset().top;
            }else{
                $("#subNavL .b").hide();
            }

            if($('#contCate3').length>0){
                var c=$("#contCate3").offset().top;
            }else{
                $("#subNavL .c").hide();
            }

            if($('#contCate4').length>0){
                var d=$("#contCate4").offset().top;
            }else{
                $("#subNavL .d").hide();
            }

            if($('#contCate5').length>0){
                var e=$("#contCate5").offset().top;
            }else{
                $("#subNavL .e").hide();
            }
            //判断类目是否存在 end
            var val=$(document).scrollTop();
            if(val==0){
                $("#subNavL").stop().fadeOut("fast");
                $("#subNavR").stop().fadeOut("fast");
            }
            if(val>0){
                var arr2 = [];
                $('.content-nav li').each(function(index,item) {
                    if(index == 5) {
                        return true;
                    }
                    arr2.push($(this).find('a').html());
                });
                $("#subNavL .tt").each(function(index, el) {
                    $(el).hover(function() {
                        $(el).html(arr2[index]);
                    });
                });
                $("#subNavL").stop().fadeIn("fast");
                $("#subNavR").stop().fadeIn("fast");
            }
            if(val>=a){
                $("#subNavL .a").html(arr2[0]).addClass('current7').siblings().removeClass('current7');
            }
            if(val>=b){
                $("#subNavL .b").html(arr2[1]).addClass('current7').siblings().removeClass('current7');
            }
            if(val>=c){
                $("#subNavL .c").html(arr2[2]).addClass('current7').siblings().removeClass('current7');
            }
            if(val>=d){
                $("#subNavL .d").html(arr2[3]).addClass('current7').siblings().removeClass('current7');
            }
            if(val>=e){
                $("#subNavL .e").html(arr2[4]).addClass('current7').siblings().removeClass('current7');
            }
        });
        /*点击事件开始*/
        $(".a").click(function(event) {
            $("body,html").stop().animate({"scrollTop":$("#contCate1").offset().top}, 800);
        });

        $(".b").click(function(event) {
            $("body,html").stop().animate({"scrollTop":$("#contCate2").offset().top}, 800);
        });
        $(".c").click(function(event) {
            $("body,html").stop().animate({"scrollTop":$("#contCate3").offset().top}, 800);
        });
        $(".d").click(function(event) {
            $("body,html").stop().animate({"scrollTop":$("#contCate4").offset().top}, 800);
        });
        $(".e").click(function(event) {
            $("body,html").stop().animate({"scrollTop":$("#contCate5").offset().top}, 800);
        });

        $(".back-top").click(function(event) {
            $("body,html").stop().animate({"scrollTop":0}, 800);
        });
        /*浮动导航 end*/
    });
});

/*
 * @author : zbc
 * @params cname    cookie名称
 * @params cvalue   cookie值
 * @params exdays   时间 1天数
 */
function setCookie(cname,cvalue,exdays)
{
  var d = new Date();
  d.setTime(d.getTime()+(exdays*24*60*60*1000));
  var expires = "expires="+d.toGMTString();
  document.cookie = cname + "=" + cvalue + "; " + expires;
}
/*
 * @params cname    cookie名称
 */
function getCookie(cname)
{
  var name = cname + "=";
  var ca = document.cookie.split(';');
  for(var i=0; i<ca.length; i++) 
  {
    var c = ca[i].trim();
    if (c.indexOf(name)==0) return c.substring(name.length,c.length);
  }
  return "";
}

//虚拟类目 一级 bbc 二级 bbc 下的
function init_bbc_categor(){
    // /product/api/vcQuery
    //获取一级类目id
    $.ajax({
        url: "/product/api/vcQuery",
        type: "post",
        contentType: "application/json",
        data: JSON.stringify({"name":"bbc"}),
        async: true,
        success: function (data) {
            if(data.length > 0){
                var parentId =  data[0].vcId;
                //初始化2级类目
                intit_scecond_level_category(parentId);
            }
        }
    })
}

//初始化虚拟类目
function intit_scecond_level_category(parentId){
    getAllCategory(parentId,function(data){
        if(data.length > 0){

            var cateHtml = '';
            $.each(data,function(i,item) {

                var content = '<div class="content-category-cont" id="contCate'+ (i + 1) +'"><div class="content-category-nav hd" data-id="'+ item.vcId +'" id="content-category-nav_'+ item.vcId + '"> <span>';
                content += item.name.substring(0,6) + '</span>';

                var ulContent = '<ul>';
                var catej = item.form;
                if(!catej || catej.length <= 0) {
                    return true;
                }
                for(var j = 0;j <catej.length && j <=4;j++){
                    ulContent += '<li id="cate_product_'+ catej[j].vcId +'" name="cateli" tag="'+ catej[j].vcId +'" class="'+ (j==0?'on':'') +'"><a target="_blank" href="/product/sub_page.html?v='+catej[j].vcId+'">'+ catej[j].name +'</a></li>';

                    if (j == 0) {
                           product(catej[j].vcId);
                    }
                }

                content += ulContent + '</ul></div></div>';
                cateHtml += content;
            });

            $('.content-category .main').prepend(cateHtml);

            for(var index in columnImgArray) {
                if($('#content-category-nav_' + columnImgArray[index].categoryId).length > 0) {
                    $('#content-category-nav_' + columnImgArray[index].categoryId).parent().append('<a href="'+ columnImgArray[index].relatedInterfaceUrl +'" class="foot-banner"> <img src="../product/banner/getBannerImg?id='+ columnImgArray[index].id + '" alt=""/> </a>')
                }
            }

        }

        $("li[name=cateli]").hover(function(){
            $(this).addClass("on").siblings().removeClass("on");
            product($(this).attr("tag"));
        },function(){
        });
    });
}

function product(id) {
    var param = {
        categoryId : id,
        currPage : 1,
        pageSize : 8
    };
    if($('#bd_' + id).length){
        $("#bd_"+id).addClass("show").siblings().filter(".bd").removeClass('show').addClass("hide");
        return;
    }
    $.ajax({
        url: "/product/api/getSkuList",
        type: "post",
        contentType: "application/json",
        data: JSON.stringify(param),
        async: true,
        success: function (data) {
            var pros = data.result;
            //<a href="" class="content-category-con-l"> <img src="img/001.jpg" alt=""/> </a>
            var html = '<div class="bd" id="bd_'+ id +'"> <div class="content-category-con"><a href="" class="content-category-con-l" id="content-category-con-l_'+ id +'"> <img src="" alt=""/> </a>  <ul class="content-category-con-r">';
            if(pros && pros.length > 0) {
                $.each(pros,function(i,item){
                    html += '<li class="bor-b"><div class="product-top"><div>';
                    html += '<a href="../product/product-detail.html?sku=' + item.csku + '&warehouseId=' + item.warehouseId + '" target="_blank">';
                    html += '<img src="'+urlReplace(item.imageUrl,item.csku,"",150,142,100)+'" alt="" />';
                    html += '</a>';
                    html += '</div> </i>';
                    html += '<p class="bar-code">国际条码：' + item.interBarCode + '</p>';
                    html += '<p class="product-title">' + item.ctitle + '</p>';
                    html += '<p class="product-price">¥' + item.disPrice + '</p></div></li>';
                    //html += '<div class="product-shop"><p class="product-title" title="' + item.ctitle + '">' + item.ctitle + '</p><div class="product-color">￥<em>549.00</em></span></div><a class="product-shop-btn">立即购买</a></div></li>';
                });
                html += "</ul></div>";
            }
            $("#cate_product_"+id).parents(".content-category-nav").after(html);
            $("#bd_"+id).addClass("show").siblings().filter(".bd").removeClass('show').addClass("hide");

            for(var index in floorImgArray) {
                if(floorImgArray[index].categoryId == id) {
                    $('#content-category-con-l_' + id).find('img').attr('src','../product/banner/getBannerImg?id=' + floorImgArray[index].id);
                    $('#content-category-con-l_' + id).attr('href',floorImgArray[index].url);
                    break;
                }
            }
            //console.log($("#bd_"+id).addClass("show").siblings().filter(".bd"));
        }
    });

}



//封装获取虚拟类目方法
function getAllCategory(parentId,callback){
    $.ajax({
        url: "/product/api/vcQueryAll",
        type: "post",
        contentType: "application/json",
        data: JSON.stringify({"parentId":parentId}),
        async:true,
        success: function (data) {
            callback(data);
        }
    })
}

function bind_banner() {
    $(function () {
        // 大banner
        var $key=0;
        $(".banner-r-img ol li").mousemove(function(event) {
            $(this).addClass('current3').siblings().removeClass('current3');
            $(".banner-r-img ul li").eq($(this).index()).stop().fadeIn("fast").siblings().stop().fadeOut("fast");

            $('.content-top').css('background',$(".banner-r-img ul li[class='current3']").eq($(this).index()).data('color'));
            $key=$(this).index();

        });
        /*定时器开始*/
        var timer=setInterval(autoplay, 6000);
        function autoplay(){
            $key++;
            if($key>2){
                $key=0;
            }
            $(".banner-r-img ol li").eq($key).addClass('current3').siblings().removeClass('current3');
            /*console.log($key);*/
            $(".banner-r-img ul li").eq($key).stop().fadeIn("fast").siblings().stop().fadeOut("fast");
            $('.content-top').css('background',$(".banner-r-img ul li[class='current3']").eq($(this).index()).data('color'));
        }
        $(".banner-r-img").hover(function() {
            clearInterval(timer);
        }, function() {
            clearInterval(timer);
            timer=setInterval(autoplay, 6000);
        });
    });
}

function init_index_product() {
    var disMode = "";
    getDiscount(function (dis) {
        disMode = dis;
    });
    $.ajax({
        url: "/product/api/getAllSkuListByParam",
        type: "post",
        contentType: "application/json",
        data: JSON.stringify({"pageSize": 5, "currPage": 1, "name": "bbc","model":disMode}),
        async: true,
        success: function (data) {
            $(".floor-box1").empty();
            var content = "";
            $.each(data, function (i, item) {
                content += show_index_product(item);
            });
            $(".floor-con").prepend(content);

            for (var i = 0; i < $('.color-mark').length; i++) {
                $($('.color-mark')[i]).addClass('color-mark' + i);
            }
        }
    });

}

function getDiscount(call) {
    $.ajax({
        url: "/member/infor?" + Math.random(),
        type: "get",
        dataType: "json",
        async: false,
        success: function (response) {
            if (response.id != null && response.id != 'null' && response.id != '' && response.id != undefined) {
                call(response.distributionMode);
            }
            else {
                call("");
            }
        }
    });
}

function show_index_product(item) {
    if (item.list.result.length <= 0) {
        return "";
    }
    var title = '<div class="floor-box1">' +
        '<div class="floor-title">' +
        '<i class="color-mark"></i>' +
        '<div class="floor-name">' + item.cate.name + '</div>' +
        '<a href="/product/sub_page.html?v=' + item.cate.vcId + '" target="_blank" class="more-products">更多&nbsp;>></a>' +
        '</div>' +
        '<div class="floor-body1"><ul class="special-line">';

    $.each(item.list.result, function (i, item) {
        var disPrice = 0;
        disPrice = item.disPrice;
      /*  if (mode) {
            //分根据模式 计算分销价
            disPrice = item.proposalRetailPrice?item.proposalRetailPrice:"--";
            if (mode == 1) {
                disPrice = (item.electricityPrices ? item.electricityPrices : disPrice);
            } else if (mode == 2) {
                disPrice = (item.distributorPrice ? item.distributorPrice : disPrice);
            } else if (mode == 3){
                disPrice = (item.supermarketPrice ? item.supermarketPrice : disPrice);
            }else{
                disPrice = (item.ftzPrice? item.supermarketPrice : disPrice);
            }
            if(!isNaN(disPrice)){
                disPrice = parseFloat(disPrice).toFixed(2);
            }
        } else {
            disPrice = (item.proposalRetailPrice?item.proposalRetailPrice : "--");
        }*/
        title += '<li class="special-item">' +
            '<a class="card-item" style="text-decoration:none;height: 300px;" target="_blank" href="../../product/product-detail.html?sku=' + item.csku + '&warehouseId=' + item.warehouseId + '">' +
            '<span class="item-pic"><img src="' + urlReplace(item.imageUrl, item.csku, null, 230, 210, 100) + '"></span>' +
            '<span class="item-info">' +
            '<span class="item-desc">' +
            '<em class="item-name">' + item.ctitle + '</em>' +
            '</span>' +
            '<span class="m-memberLabel-1">' +
            '</span>' +
            /* '<span class="inventory">' +
             '<label class="">库存:</label>' +
             '<label>'+item.stock+'</label>' +
             '</span>' +*/
            '<p class="productPrice">价格<strong><span>¥</span>' + disPrice + '</strong></p>' +
            //'<p class="productRef">国内参考价¥ <span>'+item.localPrice+'</span></p>' +
            '</span>' +
            '</a>' +
            '</li>';
    });
    title += '</ul></div></div>';
    return title;
}

function show_sprice_activity() {
    ajax_get("product/spact/opened?" + Math.random(), "", "",
        function (data) {
            var prefix = window.location.protocol + '//' + window.location.host +
                '/product/special_price.html?id=';
            var acts = data.acts, act, imgs, itemHTML = '<ul class="wrap_4">', img_len, img_num;
            if (acts.length > 0) {
                $.each(acts, function (i, item) {
                    imgs = item.imgs;
                    img_num += imgs.length;
                });
                //TODO
                if (img_num == 1) {

                }
                $.each(acts, function (i, item) {
                    act = item.act;
                    imgs = item.imgs;
                    img_len = imgs.length;
                    $.each(imgs, function (n, img) {
                        itemHTML += '<li><a href="' + prefix + act.id + '" target="_blank"><img src="../product/spact/poster?id=' + img.id + '" width="292" height="242"></a></li>';
                    });
                });
                itemHTML += '</ul>';
                $(".discount_price_box").html(itemHTML);
            } else {
                $(".discount_price").hide();
            }
            $(function () {
                var oPic = $('#slider_pic').find('ul');
                var oImg = oPic.find('li');
                var oLen = oImg.length;
                var prev = $("#prev");
                var next = $("#next");
                oPic.width(oLen * 1210);//计算总长度
                var iNow = 0;
                prev.click(function () {
                    console.log("xxx")
                    if (iNow > 0) {
                        iNow--;
                    }
                    ClickScroll();
                })
                if ($("#slider_pic>ul").hasClass("wrap_4")) {
                    if (oLen % 4 > 0) {
                        next.click(function () {
                            if (iNow < parseInt(oLen / 4)) {
                                iNow++
                            }
                            ClickScroll();
                        })
                    }
                    else {
                        next.click(function () {
                            if (iNow < oLen / 4 - 1) {
                                iNow++
                            }
                            ClickScroll();
                        })
                    }

                }
                else if ($("#slider_pic>ul").hasClass("wrap_3")) {
                    if (oLen % 3 > 0) {
                        next.click(function () {
                            console.log(oLen % 3 + 1);
                            if (iNow < parseInt(oLen / 3)) {
                                iNow++
                            }
                            ClickScroll();
                        })
                    }
                    else {
                        next.click(function () {
                            console.log("4");
                            if (iNow < oLen / 3 - 1) {
                                iNow++
                            }
                            ClickScroll();
                        })
                    }

                }
                else if ($("#slider_pic>ul").hasClass("wrap_2")) {
                    if (oLen % 2 > 0) {
                        next.click(function () {
                            if (iNow < parseInt(oLen / 2)) {
                                iNow++
                            }
                            ClickScroll();
                        })
                    }
                    else {
                        next.click(function () {
                            if (iNow < oLen / 2 - 1) {
                                iNow++
                            }
                            ClickScroll();
                        })
                    }

                }
                else if ($("#slider_pic>ul").hasClass("wrap_1")) {
                    next.click(function () {
                        if (iNow < oLen - 1) {
                            iNow++
                        }
                        ClickScroll();
                    })
                }
                function ClickScroll() {
                    iNow == 0 ? prev.addClass('no_click') : prev.removeClass('no_click');
                    iNow == oLen ? next.addClass("no_click") : next.removeClass("no_click");
                    oPic.animate({left: -iNow * 1210})
                }

            })
        },
        function (xhr, status) {
            console.log("error--->" + status);
        }
    );
}

var floorImgArray = [];
var columnImgArray = [];
function init_banner() {
    $.ajax({
        url: "/product/banner/getAllBanner",
        type: "get",
        dataType: "json",
        async: false,
        success: function (response) {
            if (response.length == 0) {
                return;
            }
            var li = "";
            var page = "";

            var bannerArray = [];
            var floatImgArray = [];
            for (var index in response) {
                if(response[index].type == 0) {
                    bannerArray.push(response[index]);
                }

                if(response[index].type == 1) {
                    floatImgArray.push(response[index]);
                }

                if(response[index].type == 2) {
                    var obj = {
                        categoryId: response[index].categoryId,
                        id: response[index].id,
                        url:response[index].relatedInterfaceUrl
                    }
                    floorImgArray.push(obj);
                }


                if(response[index].type == 3) {
                    var obj = {
                        categoryId: response[index].categoryId,
                        id: response[index].id,
                        relatedInterfaceUrl: response[index].relatedInterfaceUrl
                    }
                    columnImgArray.push(obj);
                }
            }

            //渲染Banner
            $(".banner-r-img ul").hide();
            for(var index in bannerArray) {
                if (index == 0) {
                    li += " <li class='current3' data-color='"+ bannerArray[index].bgColor +"'><a href='"+bannerArray[index].relatedInterfaceUrl+"' target='_blank'><img src='../product/banner/getBannerImg?id=" + bannerArray[index].id + "' alt=''/></a></li>";
                    page += "<li class='current-circle' data-color='"+ bannerArray[index].bgColor +"'></li>"
                } else {
                    li += " <li data-color='"+ bannerArray[index].bgColor +"'><a href='"+bannerArray[index].relatedInterfaceUrl+"' target='_blank'><img src='../product/banner/getBannerImg?id=" + bannerArray[index].id + "' alt=''/></a></li>";
                    page += "<li data-color='"+ bannerArray[index].bgColor +"'></li>"
                }
            }

            $(".banner-r-img ul").append(li);
            $(".banner-r-img ol").append(page);
            $(".banner-r-img ul").hide();
            $(".banner-r-img ul").show();


            //渲染浮窗图
            $('#content-top-right-span').hide();
            for(var index in floatImgArray) {
                if (index == 0 && floatImgArray.length > 0) {
                    $('#content-top-right-span a:eq(0)').attr('href', floatImgArray[index].relatedInterfaceUrl);
                    $('#content-top-right-span a>img:eq(0)').attr('src', '../product/banner/getBannerImg?id='+floatImgArray[index].id);
                } else {
                    if(response[index]) {
                        $('#content-top-right-span a:eq(1)').attr('href', floatImgArray[index].relatedInterfaceUrl);
                        $('#content-top-right-span a>img:eq(1)').attr('src', '../product/banner/getBannerImg?id='+floatImgArray[index].id);
                    }
                }
            }
            console.log('size:'+floatImgArray.length);
            if(floatImgArray.length == 0) {
                $('#content-top-right-span').hide();
                return;
            }

            $('#content-top-right-span').show();
        }
    });
}


/*
* 当页面滚动到一定的位置，相对应的nav子元素高亮；例子详见Bbc首页左侧锚点导航
* 传入参数： items -- 滚动部分的锚点对应div统一的class名称，
*           lightObj -- nav的子元素的统一名称，
*           claName -- 给nav子元素添加高亮的class名称。
* author : ZhangTao
* time  : 2016-11-8
 */
var LeftNavScroll = function(items,lightObj,claName){
    this.heightArr = [];
    this.ofsTop = [];
    this.items = $(items);
    this.lightObj = lightObj;
    this.claName = claName;
};

LeftNavScroll.prototype = {
    divIndex : function(){
        var $tops = this.items;
        for(var i = 0; i < $tops.length; i++){
            this.heightArr.push($($tops[i]).height());
            this.ofsTop.push($($tops[i]).offset().top-150); //-150是为了提前触发light方法
        }
        this.winScrollMethod(this.lightObj,this.claName);
    },

    checkNavLight : function (gol,i,clName){
        $(gol).removeClass(clName);
        $($(gol)[i]).addClass(clName);
    },

    winScrollMethod : function(gol,clName){
        var hArr = this.heightArr;
        var ofsTop = this.ofsTop;
        var par = this;
        $(window).scroll(function(){
            var winSl = document.documentElement.scrollTop || window.pageYOffset || document.body.scrollTop;
            for(var i = 0;i < hArr.length; i++){
                //console.log(winSl,ofsTop[i],ofsTop[i]+hArr[i]);
                if(winSl > ofsTop[i] && winSl < ofsTop[i]+hArr[i]){
                    par.checkNavLight(gol,i,clName);
                }
            }
        })
    },

    run : function(){
        this.divIndex();
    }
};
