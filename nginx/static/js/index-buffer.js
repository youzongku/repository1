require.config({
    baseUrl: "../js/",
    paths: {
        "jquery": "lib/jquery-1.11.3.min",
        "layer": "lib/layer2.0/layer",
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
require(["jquery","layer","scroll","slide"], function ($,layer) {

    var counter = 0, counterMax;
    var footerHeight;
    var headerHeight = 38 + 187 + 450;//banner及以上的高度

    counterMax = $(".content-nav").children().length;

    //判断是否登录
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

    /*banner轮播效果 start*/
    var $key=0;
    $(".content-top-right ol li").mousemove(function() {
        $(this).addClass('current-circle').siblings().removeClass('current-circle');
        $(".content-top-right ul li").eq($(this).index()).stop().fadeIn("fast").siblings().stop().fadeOut("fast");
        $('.content-top').css('background',$(".content-top-right ol li[class='current-circle']").data('color'));
        $key=$(this).index();
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
    
    $("#div_footer").load("/footer.html", null, function(){
        footerHeight = document.getElementById("div_footer").offsetHeight;
    });

    loadCate();
    
    //分销商类型显示
    function formatterDistributionmode(value) {
        if(undefined == value || null == value) {
            return;
        }
        var distributionmode = "";
        if(value == '1') {
            distributionmode = '普通分销商';
        }
        if(value == '2') {
            distributionmode = '合营分销商';
        }
        if(value == '3') {
            distributionmode = '内部分销商';
        }
        return distributionmode;
    }

    //=====事件绑定   开始=====
    $(window).scroll(function(event) {
        var clientHeight = document.body.clientHeight;
        var scrollTop = document.body.scrollTop;
        var innerHeight = window.innerHeight;
        var navOffset = $("#subNavL").offset().top;
        //滚动时，左右导航栏
        if(scrollTop==0){
            $("#subNavL").stop().fadeOut("fast");
            $("#subNavR").stop().fadeOut("fast");
        }else{
            $("#subNavL").stop().fadeIn("fast");
            $("#subNavR").stop().fadeIn("fast");
            //滚动时，飘在顶端的搜索栏
            if(scrollTop > 187){
                $( '#headerFloat' ).addClass( 'fixer').removeClass('hide');

            }else{
                $( '#headerFloat' ).removeClass( 'fixer').addClass('hide');
            }

            //判断是否需要加载下一个类目
            if(clientHeight - scrollTop - innerHeight <= footerHeight + 50){//提前50个像素点
                loadCate();
            }

            //左导航栏的current标记
            var cateDivPixel = navOffset - headerHeight;
            if(cateDivPixel > 0){//current标记
                var currentNavl = $("#subNavL .tt")[parseInt(cateDivPixel/781)];
                $(currentNavl).addClass('current7').siblings().removeClass('current7');
            }
        }
    });

    $(".searchInput").on("keyup",function(){
        var key = event.keyCode || event.which;
        if (key == 13) {
            searchProd();
        }
    });
    $(".searchSpan").on("click", searchProd);


    //===========导航start========
    var cateNames = [];
    $('.content-nav li').each(function(index,item) {
        if(index == 5) {
            return true;
        }
        cateNames.push($(this).find('a').html());
    });
    $("#subNavL .tt").each(function(index, el) {
        $(el).hover(function() {
            $(el).html(cateNames[index]);
        });
    });

    /*左侧导航*/
    $("body").on("mouseover mouseout",".content-nav li",function(event){
        $(".content-nav-child").mCustomScrollbar({
            themes:"minimal-dark",
            advanced:{ updateOnBrowserResize:true },
            scrollInertia:300
        });

        if(event.type == "mouseover"){
            $(this).children(".content-nav-child").show();
        }else if(event.type == "mouseout"){
            $(this).children(".content-nav-child").hide();
        }
    });

    //左侧导航 点击事件
    $("#subNavL .tt").click(function(){
        var ele = this, ind = 1;
        while(ele = ele.previousElementSibling){
            ind += 1;
        }
        if(ind > counter){//大于已加载的页面时，先加载页面
            //加载到够多的页面，才能滚动下去
            loadCate(ind - counter, function(){
                var scrollPixel = $('.content-category .main .content-category-cont')[ind-1].offsetTop;
                $("body,html").stop().animate({"scrollTop":scrollPixel}, 800);
            });
        }
        
    });

    //返回顶部 点击事件
    $(".back-top").click(function(event) {
        $("body,html").stop().animate({"scrollTop":0}, 800);
    });

    //购物车产品数量
    function getCartQty() {
        $.ajax({
            url: "/cart/getcartdata?" + Math.random(), 
            type: "GET",
            contentType : "application/json",
            success: function (data) {
                if (data.code) {
                    layer.msg(data.msg, {icon: 6, time: 3000});
                    return;
                }
                $("#cartItemCountNew").text(data.cartQty);
            },
            error : function(XMLHttpRequest, textStatus){
                layer.msg("显示购物车列表出错！", {icon: 2, time: 2000});
            }
        });
    }

    //加载html文件
    function loadCate(step, callBack){
        if(!step){
            step = 1;
        }
        if(counter <= counterMax){
            for (var i=0; i<step; i++) {
                counter ++;
                var fileName = counter +".html"
                $.ajax({
                    url: fileName, 
                    type: "GET",
                    async : false,
                    success : function(data){
                        $('.content-category .main').append(data);

                        $("li[name=cateli]").hover(function(){
                            var catId = $(this).attr("tag");
                            $(this).addClass("on").siblings().removeClass("on");
                            $("#bd_"+catId).addClass("show").siblings().filter(".bd").removeClass('show').addClass("hide");
                        });
                        if(callBack){
                            if(i+1 == step){
                                callBack();
                            }
                        }
                    }
                });
            }
        }
    }

    //搜索事件
    function searchProd(){
        var searchContent = $('.searchInput').val().trim();
        var s = encodeURI(searchContent);
        window.location = '/product/product_search.html?s='+ s;
    }
});


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

/**
 * 查询前台用户是否登录
 */
function isnulogin(back_email) {
    var url = "../member/isnulogin?" + (new Date()).getTime();
    $.ajax({url: url, type: "get", dataType: "json", async: false,
        success: function (data) {
            if (data.suc) {
                back_email(data.user.email);
            } else {
                window.location.href = "/personal/login.html";
            }
        }
    });
}