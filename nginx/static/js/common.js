function getModuleName() {
    var currentPath = window.location.pathname;
    var moduleName;
    if (currentPath != "/") {
        moduleName = currentPath.split("/")[1];
    } else {
        moduleName = "personal";
    }
    return moduleName;
}

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

function getPayMethod(method){
    return  method == "alipay" ? "支付宝" :
        method == "wechatpay" ? "微信支付" :
            method == "yijifu" ? "易极付" :
                method == "system" ? "余额支付" :
                    method == "balance" ? "余额支付" :
                        method == "yjf_wx" ? "易极付微信扫码支付" :
                            method =='cash' ? "现金支付":
                                method == "cash-noline"?"线下转账":"线上支付";
}
/**
 * 查询后台用户是否登录
 */
function isaulogin(back_email) {
    var url = "../member/isaulogin?" + (new Date()).getTime();
    $.ajax({url: url, type: "get", dataType: "json", async: false,
        success: function (data) {
            if (data.suc) {
                back_email(data.user.email);
            } else {
                window.location.href = "login.html";
            }
        }
    });
}

//解决placeholder兼容性
$(document).ready(function () {
    function initPlaceHolders() {
        if ('placeholder' in document.createElement('input')) { //如果浏览器原生支持placeholder
            return;
        }
        function target(e) {
            var e = e || window.event;
            return e.target || e.srcElement;
        };
        function _getEmptyHintEl(el) {
            var hintEl = el.hintEl;
            return hintEl && g(hintEl);
        };
        function blurFn(e) {
            var el = target(e);
            if (!el || el.tagName != 'INPUT' && el.tagName != 'TEXTAREA') return;//IE下，onfocusin会在div等元素触发
            var emptyHintEl = el.__emptyHintEl;
            if (emptyHintEl) {
                //clearTimeout(el.__placeholderTimer||0);
                //el.__placeholderTimer=setTimeout(function(){//在360浏览器下，autocomplete会先blur再change
                if (el.value) emptyHintEl.style.display = 'none';
                else emptyHintEl.style.display = '';
                //},600);
            }
        };
        function focusFn(e) {
            var el = target(e);
            if (!el || el.tagName != 'INPUT' && el.tagName != 'TEXTAREA') return;//IE下，onfocusin会在div等元素触发
            var emptyHintEl = el.__emptyHintEl;
            if (emptyHintEl) {
                //clearTimeout(el.__placeholderTimer||0);
                emptyHintEl.style.display = 'none';
            }
        };
        if (document.addEventListener) {//ie
            document.addEventListener('focus', focusFn, true);
            document.addEventListener('blur', blurFn, true);
        } else {
            document.attachEvent('onfocusin', focusFn);
            document.attachEvent('onfocusout', blurFn);
        }

        var elss = [document.getElementsByTagName('input'), document.getElementsByTagName('textarea')];
        for (var n = 0; n < 2; n++) {
            var els = elss[n];
            for (var i = 0; i < els.length; i++) {
                var el = els[i];
                var placeholder = el.getAttribute('placeholder'),
                    emptyHintEl = el.__emptyHintEl;
                if (placeholder && !emptyHintEl) {
                    emptyHintEl = document.createElement('span');
                    emptyHintEl.innerHTML = placeholder;
                    emptyHintEl.className = 'emptyhint';
                    emptyHintEl.onclick = function (el) {
                        return function () {
                            try {
                                el.focus();
                            } catch (ex) {
                            }
                        }
                    }(el);
                    if (el.value) emptyHintEl.style.display = 'none';
                    el.parentNode.insertBefore(emptyHintEl, el);
                    el.__emptyHintEl = emptyHintEl;
                }
            }
        }
    }

    initPlaceHolders();

     //初始化虚拟类目
    intit_index_category();

    /*左侧导航 start*/
    $(document).ready(function () {
        /*左侧导航 start*/
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
    });
});

$(document).ready(function () {
    //前台通用加载页面头尾
    $("#div_head").load("/" + getModuleName() + "/header.html", function () {

        var s = getQueryString("s");
        $('.searchInput').val(s);
        $('.searchInput2').val(s);

        if (window.location.pathname == "/") {
            $(".content-top-left").show();
        } else {
            $(".content-top-left").hide();
        }
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
                            $('#LoginAfterMenu').find('.user-type').html(formatterDistributionmode(data.msg.comsumerType));
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
    });

    if (window.location.pathname == "/") {
        $(".content-top-left").show();
    } else {
        $(".content-top-left").hide();
    }
    $("#div_footer").load("/footer.html");
});

function searchProduct() {
    var searchContent = $('.searchInput').val().trim();
    var s = encodeURI(searchContent);
    window.location = '/product/product_search.html?s='+ s;
}
function searchProduct2() {
    var searchContent = $('.searchInput2').val().trim();
    var s = encodeURI(searchContent);
    window.location = '/product/product_search.html?s='+ s;
}

function getQueryString(key){
    var reg = new RegExp("(^|&)"+key+"=([^&]*)(&|$)");
    var result = window.location.search.substr(1).match(reg);
    return result?decodeURIComponent(result[2]):null;
}

function formatterDistributionmode(value) {
    if(isnull(value)) {
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

//购物车产品数量
function getCartQty() {
    ajax_get("/cart/getcartdata?" + Math.random(), "", "application/json",
        function (data) {
            if (data.code) {
                layer.msg(data.msg, {icon: 6, time: 3000});
                return;
            }
            $("#cartItemCountNew").text(data.cartQty);
            $("#addcartnum").text(data.cartQty);
        },
        function (XMLHttpRequest, textStatus) {
            layer.msg("显示购物车列表出错！", {icon: 2, time: 2000});
        }
    );
}

/**
 * 通用GET方式AJAX请求，同步的
 * 后端若使用JSON接收参数，参数params必须用JSON.stringify()处理，同时contentType必须为'application/json'
 */
function ajax_get(url, params, contentType, successCallback, errorCallback) {
    contentType = (contentType == undefined || contentType == '') ? 'application/x-www-form-urlencoded' : contentType;
    $.ajax({
        url: url,
        type: 'GET',
        data: params,
        contentType: contentType,
        async: false,//是否异步
        dataType: 'json',
        success: function (response) {
            successCallback(response);
        },
        error: function (XMLHttpRequest, textStatus) {
            if(errorCallback != undefined){
                errorCallback(XMLHttpRequest, textStatus);
            }
        }
    });
}

/**
 * 通用POST方式AJAX请求，异步的
 * 后端若使用JSON接收参数，参数params必须用JSON.stringify()处理，同时contentType必须为'application/json'
 */
function ajax_post(url, params, contentType, successCallback, errorCallback) {
    contentType = (contentType == undefined || contentType == '') ? 'application/x-www-form-urlencoded' : contentType;
    $.ajax({
        url: url,
        type: 'POST',
        data: params,
        contentType: contentType,
        async: true,//是否异步
        dataType: 'json',
        success: function (response) {
            successCallback(response);
        },
        error: function (XMLHttpRequest, textStatus) {
            if(errorCallback != undefined){
                errorCallback(XMLHttpRequest, textStatus);
            }
        }
    });
}
/**
 * 通用 同步 POST方式AJAX请求，同步的
 * 后端若使用JSON接收参数，参数params必须用JSON.stringify()处理，同时contentType必须为'application/json'
 */
function ajax_post_sync(url, params, contentType, successCallback, errorCallback) {
    contentType = (contentType == undefined || contentType == '') ? 'application/x-www-form-urlencoded' : contentType;
    $.ajax({
        url: url,
        type: 'POST',
        data: params,
        contentType: contentType,
        async: false,//是否异步
        dataType: 'json',
        success: function (response) {
            successCallback(response);
        },
        error: function (XMLHttpRequest, textStatus) {
            if(errorCallback != undefined){
                errorCallback(XMLHttpRequest, textStatus);
            }
        }
    });
}

//后台刷新页头右侧日期时间
function refresh_date_time() {
    //获取当前时间
    var now = new Date();
    //获取年份
    var year = now.getFullYear();
    //获取月份0-11
    var month = now.getMonth() + 1;
    //获取日数
    var date = now.getDate();
    //获取小时数
    var hours = now.getHours();
    //获取分钟数
    var minutes = now.getMinutes();
    //获取秒数
    var seconds = now.getSeconds();
    //获取星期几0-6
    var week = now.getDay();
    //判断月份、日数、小时数、分钟数、秒数是否为两位数，不足则补0
    month = month < 10 ? "0" + month : month;
    date = date < 10 ? "0" + date : date;
    hours = hours < 10 ? "0" + hours : hours;
    minutes = minutes < 10 ? "0" + minutes : minutes;
    seconds = seconds < 10 ? "0" + seconds : seconds;

    switch (week) {
        case 0:
            week = "星期日";
            break;
        case 1:
            week = "星期一";
            break;
        case 2:
            week = "星期二";
            break;
        case 3:
            week = "星期三";
            break;
        case 4:
            week = "星期四";
            break;
        case 5:
            week = "星期五";
            break;
        case 6:
            week = "星期六";
            break;
    }
    //将各个部分拼接成固定格式的日期时间
    $("span.time_day").text(year + "-" + month + "-" + date);
    $("span.time_clock").text(hours + ":" + minutes);
    $("span.time_week").text(week);
}

//获取指定格式yyyy-MM-dd HH:mm:ss的当前系统时间
function getCDT() {
    var now = new Date();
    var year = now.getFullYear();
    var month = now.getMonth() + 1;
    var day = now.getDate();
    var hour = now.getHours();
    var minute = now.getMinutes();
    var second = now.getSeconds();
    return (year + '-' + (month > 9 ? month : ('0' + month)) + '-' + (day > 9 ? day : ('0' + day)) + ' ' +
        (hour > 9 ? hour : ('0' + hour)) + ':' + (minute > 9 ? minute : ('0' + minute)) + ':' +
        (second > 9 ? second : ('0' + second)));
}

function getUrlParam(url, paramName) {
    if (url.indexOf("?") != -1) {
        var str = url.substr(1)　//去掉?号
        var strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            var name = strs[i].split("=")[0];
            var value = strs[i].split("=")[1];
            if (name == paramName) {
                return value;
            }
        }
    }
}

//获取请求地址后的参数
function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null)return unescape(r[2]);
    return null;
}

/**
 * 图片替换
 *
 * @param srcUrl      需要替换的地址
 * @param sku         产品的商品编码
 * @param fromPage    源页面;由于存放在不同的图片服务器，暂时只判断是否详情页的描述
 * @param setWidth    宽；可选；不传值时，用img对象的width属性作为值
 * @param setHeight   高；可选；不传值时，用img对象的height属性作为值
 * @param setQuality  图片质量，1~100之间
 *
 * @author ye_ziran
 *
 * @since 2016-01-21 20:40
 */
function urlReplace(srcUrl, sku, fromPage, setWidth, setHeight, setQuality) {
    var resUrl;
    var tmpImgUrl = "https://static.tomtop.com.cn/imaging/imaging/product/" + sku;
    if (fromPage == 'desc') {
        tmpImgUrl = "https://static.tomtop.com.cn/images/uploads/" + sku;
    }
    var width, height, quality = 100;
    if (setWidth != undefined && setWidth != null) {
        width = setWidth;
    }
    if (setHeight != undefined && setHeight != null) {
        height = setHeight;
    }
    if (setQuality != undefined && setQuality != null) {
        quality = setQuality;
    }
    srcUrl = srcUrl || '';
    var imgName = srcUrl.substr(srcUrl.lastIndexOf("\/"));

    if (width == undefined || height == undefined) {
        resUrl = tmpImgUrl + imgName;

    } else {
        resUrl = tmpImgUrl + "/" + parseInt(width) + "-" + parseInt(height) + "-" + quality + imgName;
    }
    return resUrl;
}

/**
 * 图片替换
 *
 * @param $destObj    需要替换的img的jq对象
 * @param fromPage    源页面;由于存放在不同的图片服务器，暂时只判断是否详情页的描述
 * @param setWidth    宽；可选；不传值时，用img对象的width属性作为值
 * @param setHeight   高；可选；不传值时，用img对象的height属性作为值
 * @param setQuality  图片质量，1~100之间
 *
 * @author ye_ziran
 *
 * @since 2016-01-21 20:40
 */
function imgObjReplace(obj, fromPage, setWidth, setHeight, quality) {
    obj.siblings("img").each(function () {
        imgUrlReplace($(this), fromPage, setWidth, setHeight, quality);
    });

    obj.find("img").each(function () {
        imgUrlReplace($(this), fromPage, setWidth, setHeight, quality);
    });

    return obj;
}

/**
 * 图片替换
 *
 * @param $destObj    需要替换的img的jq对象
 * @param fromPage    源页面;由于存放在不同的图片服务器，暂时只判断是否详情页的描述
 * @param setWidth    宽；可选；不传值时，用img对象的width属性作为值
 * @param setHeight   高；可选；不传值时，用img对象的height属性作为值
 * @param setQuality  图片质量，1~100之间
 *
 * @author ye_ziran
 *
 * @since 2016-01-21 20:40
 */
function imgUrlReplace($destObj, fromPage, setWidth, setHeight, setQuality) {
    var tmpImgUrl = "https://static.tomtop.com.cn/imaging/imaging/product";
    if (fromPage == 'desc') {
        tmpImgUrl = "https://static.tomtop.com.cn/images/uploads/";
    }
    var width = $destObj.attr("width");
    var height = $destObj.attr("height");
    var imgUrl = $destObj.attr("src");
    if(!imgUrl){
        return;
    }
    var imgName = imgUrl.substr(imgUrl.lastIndexOf("\/"));

    var quality = 100;
    if (setWidth != undefined && setWidth != null) {
        width = setWidth;
    }
    if (setHeight != undefined && setHeight != null) {
        height = setHeight;
    }
    if (setQuality != undefined && setQuality != null) {
        quality = setQuality;
    }
    if (width == undefined || height == undefined) {
        $destObj.attr("src", tmpImgUrl + imgName);
    } else {
        $destObj.attr("src", tmpImgUrl + "/" + parseInt(width) + "-" + parseInt(height) + "-" + quality + imgName);
    }
}

//处理非法值(null、undefined、"")统一为"---"
function deal_with_illegal_value(value) {
    return ("number" == typeof value) ? value : (value == undefined || value == null || value == "" || value == "null") ? "--" : value;
}

//处理json，null值替换为""
function deal_with_illegal_json(value) {
    return ("number" == typeof value) ? value : (value == undefined || value == null || value == "null") ? "" : value;
}

//处理非法值(null、undefined、"")，替换为"未设置"
function deal_with_illegal2NotSet(value) {
    return ("number" == typeof value) ? value : (value == undefined || value == null || value == "" || value == "null") ? "未设置" : value;
}

/**
 * 判断非法值(undefined、null、""、"null"、"{}"、"[]")统一为false
 */
function deal_with_illegal_boolean(value) {
    return (value != undefined && value != null && value != "" && value != "null" && value != "{}" && value != "[]") ? true : false;
}

//处理非法值(null、undefined、"")，如果是数字，可以做省略小数点处理
function comToFixed(value,num){
    return ("number" == typeof value) ? parseFloat(value).toFixed(num) : (value == undefined || value == null || value == "null") ? "" :parseFloat(value).toFixed(num) ;
}

//判断仓库完税、保税
function warehouseType(value) {
    var flag = true;
    var warehouse = ["2024"];//完税仓库的id数组
    if ($.inArray(value, warehouse) < 0) {//value不在完税仓库数组中
        flag = false;
    }
    return flag;
}

//采购价输入校验(只能输入整数)
$('.personal-content').on('keyup', '.only-number input[type="text"]', function (event) {
    var words = $(this).val();
    var keyCode = event.which;
    if (keyCode == 46 || (keyCode >= 48 && keyCode <= 57)) {
        return true;
    } else {
        words = words.replace(/\s+/g, "");
        $(this).val(words.replace(/\D/g, ""));
    }
})

/**
 * 为空
 * @param data
 * @returns {boolean}
 */
function isnull(data) {
    if (data == null || data == undefined || data == "") {
        return true;
    } else {
        return false;
    }
}
/**
 * 不为空
 * @param data
 * @returns {boolean}
 */
function isnotnull(data){
    return !isnull(data);
}

// 是否符合金额的正则
function isMoneyPattern(money){
    if(isnull(money)) return false
    var pattern = /^(0|([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
    return pattern.test(money);
}

//返回格式化后金额
function fmoney(s, n) {
    n = n > 0 && n <= 20 ? n : 2;
    s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";
    var l = s.split(".")[0].split("").reverse(),
        r = s.split(".")[1];
    var t = "";
    for (var i = 0; i < l.length; i++) {
        t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");
    }
    if (r == '00') {
        return t.split("").reverse().join("");
    }
    return t.split("").reverse().join("") + "." + r;
};

//金额格式化----$("body")动态加载出来的元素也能加载事件
$("body").on("focus", ".formatMoney", function () {
    $(this).attr("data-fmt", $(this).val()); //将当前值存入自定义属性
});

$("body").on("blur", ".formatMoney", function () {
    var oldVal = $(this).attr("data-fmt"); //获取原值
    var newVal = $(this).val(); //获取当前值
    if(newVal && newVal.length > 12) {
        $(this).removeAttr("data-oral");
        layer.msg("充值金额不规范", {icon: 5, time: 2000});
        return;
    }
    var oral = parseFloat(newVal.replace(/[^\d\.-]/g, ""));
    if (!isNaN(oral)) {
        $(this).attr("data-oral", oral);
    } else {
        $(this).removeAttr("data-oral");
        $(this).val("");
        layer.msg("金额格式错误！", {icon: 5, time: 2000});
    }
    if (oldVal != newVal) {
        newVal = newVal.replace(/[^\d\.-]/g, "");
        if (newVal == "" || isNaN(newVal)) {
            return this.value;
        }
        var s = this.value;
        var temp;

        if (/.+(\..*\.|\-).*/.test(s)) {
            return;
        }
        s = parseFloat((s + "").replace(/[^\d\.\-]/g, "")).toFixed(2) + "";
        var l = s.split(".")[0].split("").reverse(),
            r = s.split(".")[1];
        var t = "";
        for (var i = 0; i < l.length; i++) {
            t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length && (l[i + 1] != '-') ? "," : "");
        }
        if (r == '00') {
            temp = t.split("").reverse().join("");
        } else {
            temp = t.split("").reverse().join("") + "." + r;
        }
        this.value = temp;
        return this.value;
    }
});
//手机号码格式校验
function checkTel(telNo) {
    if(!telNo){
        return false;
    }
    var regTel = /^(13[0-9]|15[012356789]|17[103678]|18[0-9]|14[57])[0-9]{8}$|(0[1-9][0-9])-(\d{7,8})$|(0[1-9]\d{2}-(\d{7,8}))$/;
    return regTel.test(telNo.trim());
}
//身份证格式校验
function checkIDCard(IDCardNo) {
    var regIdCard = /^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/;
    return regIdCard.test(IDCardNo);
}
//邮编格式校验
function checkPost(code) {
    var regZip = /^[0-9]{6}$/;
    return regZip.test(code);
}

//绑定，样式参考售后订单
$('.box-right-four').on('click', '.pub-showmore', function () {
    var initH = 150;   //设定最小高度
    var showM = new ShowMore();
    showM.tapp(this, initH);
});
//公共显示更多方法
var ShowMore = function () {
    ShowMore.prototype.tapp = function (obj, eleH) {
        var mBox = $(obj).siblings('.pub-more');
        var tabHeight = mBox.find('table').height() - 3;
        if (tabHeight > mBox.height()) {
            mBox.stop().animate({'height': tabHeight}, 400);
            $(obj).html('收起');
        } else {
            mBox.stop().animate({'height': eleH}, 400);
            $(obj).html('显示更多');
        }
    };
};


//切换获取短信验证码按钮中的文字
function toggle_sendmsg_btntext(node) {
    var duration = 60;
    var func = $(node).attr("onclick")
    $(node).text("已发送(" + duration + ")");
    $(node).css({"cursor": "not-allowed", "color": "white"});
    $(node).removeAttr("onclick");
    var intervalID = window.setInterval(function () {
        duration = duration - 1;
        $(node).text("已发送(" + duration + ")");
        if (duration == 0) {
            window.clearInterval(intervalID);
            $(node).text("发送验证码");
            $(node).css({"cursor": "pointer", "color": "white"});
            $(node).attr("onclick", func);
        }
    }, 1000);
};

//添加局部异步请求加载动画
function addLoadGif(items, isSuc) {
    var info = {
        togBox: '',
        togPage: '',
        scrollTop: '170',
        popBox: '',
        hint: '<div class="nothing"><div>抱歉，暂无相关商品，去其他分类逛逛吧~</div></div>'
    };
    $.extend(info, items);
    function gifFail() {
        $(document).off('ajaxComplete ajaxError');
        $('.nothing').remove();
        $(info.togBox).show().append(info.hint);
    }
    $('.nothing').hide().remove();
    setTimeout(gifFail,600)
}

//单页面锚点跳转至指定位置
function scrollPosTop(obj, psTop) { //不传参则默认body滚动到170的位置
    var s = psTop || 0;
    if (obj == undefined || obj == '') {
        $('body').animate({scrollTop: 170}, 0);
    } else {
        $(obj).animate({scrollTop: s}, 0);
    }
}

function showCate(){
    $(".content-top-left").show();
}

function hideCate(){
    if (window.location.pathname != "/") {
        $(".content-top-left").hide();
    }
}

//虚拟类目 一级 bbc 二级 bbc 下的
function intit_index_category(){
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
                intit_scecond_level_cate(parentId);
            }
        }
    })
}
//初始化虚拟类目
function intit_scecond_level_cate(parentId){
    getCategory(parentId,function(data){
        if(data.length > 0){
            $(".content-nav").empty();
            $.each(data,function(i,item) {
                if (i == 9) {
                    return false;
                }

                var cateHtml = '<li> <a href="/product/sub_page.html?v='+ item.vcId +'"> <i class="icon' + (i + 1)+ '"></i>';
                cateHtml += '<em>' + item.name.substr(0,6) + '</em>';
                cateHtml += '<i class="arrows-r-w"></i></a>';
                cateHtml += '<div class="content-nav-child" data-mcs-theme="minimal-dark"> <div class="content-nav-child-box">';

                var catej = item.form;
                var dlContent = "";
                for(var j = 0;j <catej.length;j++){
                    dlContent += '<dl> <dt> <a href="">';
                    dlContent += (catej[j].name).substr(0,6);
                    dlContent += '<i class="arrows-r-g"></i> </a></dt><dd>';

                    var catek = catej[j].form;
                    var ddContent = "";
                    for(var k = 0;k <catek.length;k++){
                        ddContent += '<a href="/product/sub_page.html?v='+catek[k].vcId+'" class="hot-word">'+(catek[k].name).substr(0,10)+'</a>';
                    }
                    ddContent += '</dd>';
                    dlContent += ddContent + "</dl>";
                }
                cateHtml += dlContent + '</div> </div> </li>';
                $(".content-nav").append(cateHtml);
                if (window.location.pathname == "/") {
                    $(".content-nav").show();
                }
            });
        }
    })
}

//封装获取虚拟类目方法
function getCategory(parentId,callback){
    $.ajax({
        url: "/product/api/vcQueryAll",
        type: "post",
        contentType: "application/json",
        data: JSON.stringify({"parentId":parentId}),
        success: function (data) {
             callback(data);
        }
    })
}


//返回顶部定位方法

function goBackTop(){
    var bTop = 380;
    if($('.nav-middle').offset())
    var bLeft = $('.nav-middle').offset().left;
    var winScroll = $(window).scrollTop();
    var item = $('#gotop');

    if(winScroll >= bTop && item.css('display')=='none'){
        item.show().css({
            right:bLeft-54
        })
    }else if(winScroll < bTop){
        item.hide();
    }

};

$(window).scroll(function(){
    if ($('#div_footer').length !== 0) {
        goBackTop();
    }
});

$(document).on('click','#gotop',function(){
    $("html,body").stop().animate({scrollTop:0},300);
});
//    点击内容滚动至指定锚点
function click_scroll(obj) {
    var navs = [];
    (function () {
        var as = $('.left-title').find('span,b');
        for (var i = 0; i < as.length; i++) {
            navs.push($(as[i]).attr('data-num'))
        }
    })();
    var objC = $(obj).attr('data-num');
    $("html,body").stop().animate({scrollTop: $('.liner-' + objC).offset().top - $('.top').height() - 10}, 500);
    var navsIndex = navs.indexOf($(obj).attr('data-num'))
    $('.left-title').find('span,b').removeClass();
    $(obj).addClass('num' + navsIndex);
    $(obj).addClass('float-current').siblings().removeClass('float-current');
}

$(window).scroll(function(){
    var navsTop = $(window).scrollTop();
    if ( navsTop > 175) {
        $('.about-left').addClass("navtop");
    }
    else{
        $('.about-left').removeClass("navtop");
    }
});

/*
 * 时间格式化
 */
Date.prototype.format = function(format) {
   var date = {
          "M+": this.getMonth() + 1,
          "d+": this.getDate(),
          "h+": this.getHours(),
          "m+": this.getMinutes(),
          "s+": this.getSeconds(),
          "q+": Math.floor((this.getMonth() + 3) / 3),
          "S+": this.getMilliseconds()
   };
   if (/(y+)/i.test(format)) {
          format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
   }
   for (var k in date) {
          if (new RegExp("(" + k + ")").test(format)) {
                 format = format.replace(RegExp.$1, RegExp.$1.length == 1
                        ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
          }
   }
   return format;
}

/**
 * 将表单序列化的数据序列化为json格式
 * @param {Object} param
 * Created by huchuyin 2016-9-14
 */
function serializeJSON(param) {
    var serializeObj = {};
    $(param).each(function(i,obj) {
        if(typeof(serializeObj[obj.name]) == 'undefined') {
            //若json不存在该key值，则直接赋值
            if(obj.value!=null && obj.value!="" && obj.value!='undefined') {
                serializeObj[obj.name] = obj.value;
            }
        } else {
            //若已存在，则value值用逗号拼接
            if(obj.value!=null && obj.value!="" && obj.value!='undefined') {
                serializeObj[obj.name] += "," + obj.value;
            }
        }
    });
    //返回json对象
    return serializeObj;
}

function HashMap(){
    //定义长度
    var length = 0;
    //创建一个对象
    var obj = new Object();

    /**
     * 判断Map是否为空
     */
    this.isEmpty = function(){
        return length == 0;
    };

    /**
     * 判断对象中是否包含给定Key
     */
    this.containsKey=function(key){
        return (key in obj);
    };

    /**
     * 判断对象中是否包含给定的Value
     */
    this.containsValue=function(value){
        for(var key in obj){
            if(obj[key] == value){
                return true;
            }
        }
        return false;
    };

    /**
     *向map中添加数据
     */
    this.put=function(key,value){
        if(!this.containsKey(key)){
            length++;
        }
        obj[key] = value;
    };

    /**
     * 根据给定的Key获得Value
     */
    this.get=function(key){
        return this.containsKey(key)?obj[key]:null;
    };

    /**
     * 根据给定的Key删除一个值
     */
    this.remove=function(key){
        if(this.containsKey(key)&&(delete obj[key])){
            length--;
        }
    };

    /**
     * 获得Map中的所有Value
     */
    this.values=function(){
        var _values= new Array();
        for(var key in obj){
            _values.push(obj[key]);
        }
        return _values;
    };

    /**
     * 获得Map中的所有Key
     */
    this.keySet=function(){
        var _keys = new Array();
        for(var key in obj){
            _keys.push(key);
        }
        return _keys;
    };

    /**
     * 获得Map的长度
     */
    this.size = function(){
        return length;
    };

    /**
     * 清空Map
     */
    this.clear = function(){
        length = 0;
        obj = new Object();
    };
}

function unLoad(){
   return $("#filter_load").length;
}
/*
 * @parma obj 文件选择input
 * @parma img 预览图片元素
 * 图片预览公共方法
 */
function preview(obj,img){
    var file = $(obj)[0].files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        img.attr("src",reader.result);
    }
    if(file){
        reader.readAsDataURL(file);
    }else{
        img.attr("src","");
    }
}

function previewImg(obj) {
    var src = $(obj).attr("src");
    layer.open({
        type: 1,
        title: false,
        area: ['800px', '500px'],
        content: '<div class="banner_add_pop big_img_pop" id="addBanner"><img src="'+src+'"></div>'
    });
    $(".layui-layer-content").css("padding","10px")
}

function formateDate(time){
    var da=new Date(time);
    var year = da.getFullYear();
    var month = da.getMonth()+1;
    var date = da.getDate();
    return ([year,month,date].join('-'));
}


/**
 * 时间格式化
 * @param time
 * @returns {string}
 */
function formatDateTime(time) {
    if(isnull(time)){
        return;
    }
    var dates = new Date(time);
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
}

//事件开关
function btn_switch(node,red){
    if(red){
        $(node).css({"cursor": "not-allowed", "color": "white"});
        $(node).attr("disabled",true);
    }else{
        $(node).css({"cursor": "pointer", "color": "white"});
        $(node).removeAttr("disabled",true);
    }
}

//采购单锁库公用接口
function orderLock(orderNo,callback){
    ajax_get("/purchase/orderLock?od="+orderNo,"","",function(respose){
        if(respose.suc){
            callback(true);
        }else{
            layer.msg(respose.msg,{icon:5,time:2000});
            callback(false);
        }
    });
}

/**
 * 选中当前行效果（jqgrid的）
 * @param obj
 */
function clickThisTr(obj){
    $(obj).click()
}

// 获取用户信息
function getInfo(email,callback){
    ajax_get("/member/infor?email=" + email, "", "application/json",
        function (response) {
            if(callback)
                callback(response);
        },function(e){
            console.log(e);
        }
    );
}

function erpSto(param,skuMap){
    ajax_post_sync("/inventory/erp/stock",JSON.stringify(param),"application/json",function(response){
        if(response.suc){
            $.each(response.data,function(i,item){
                skuMap[item.sku].erpStock = item.stock;
                skuMap[item.sku].short = skuMap[item.sku].qty > item.stock;
            })
        }else{
            layer.msg(response.msg,{icon:5,time:2000});
        }
    })
    return skuMap;
}

// erp库存结果显示
function erpStoHtml(skuMap){
    var trsHtml = ""
    $.each(skuMap,function(k,v){
        var tdBg = (v.short?"style='background:#ED6E6E;'":"");
        trsHtml += '<tr >'+
            '<td '+tdBg+'>'+ v.sku+'</td>'+
            '<td '+tdBg+'>'+ v.title+'</td>'+
            '<td '+tdBg+'>'+ v.interBarCode+'</td>'+
            '<td '+tdBg+'>'+ v.warehouseName+'</td>'+
            '<td '+tdBg+'>'+ v.qty+'</td>'+
            '<td '+tdBg+'>'+ v.erpStock+'</td>'+
            '</tr>';
    });
    var stoHtml =
        '<table class="record_sendList_table">' +
        '<thead>' +
        '<tr><th>商品编号</th><th>商品名称</th><th>国际条码</th><th>所属仓库</th><th>发货数量(个)</th><th>ERP库存</th></tr>' +
        '</thead>' +
        '<tbody>' + trsHtml + '</tbody>' +
        '</table>';
    layer.open({
        title: '查看ERP库存',
        area: ['700px','400px'],
        content: stoHtml,
        fixed:true,
        scrollbar:false
    });
}

/**
 * 点击回车执行方法
 * @param event   keyup方法事件参数
 * @param obj  控件对象（一般是input，输入this）
 * @param callback   要执行的方法
 */
function enter_keyUp(event, obj, callback) {
    var key = event.keyCode || event.which;
    // && $.trim($(obj).val())
    if (key == 13 && callback) {
        callback.apply()
    }
}
/**
 * 回测事件
 * @param obj jquey 对象
 * @param callback 要执行的方法
 *
 */
function entry_func(obj,callback){
    obj.keydown(function(event){
        var key = event.keyCode || event.which;
        if (key == 13) {
            callback();
        }
    })
}

function checkPrice (price) {
    var regMoney = /^(0|[1-9][0-9]*)(.[0-9]{1,2})?$/;
    return regMoney.test(price);
}

/*导航条滚动，固定导航 start*/
$(window).scroll( function (){
    var  scrollTop=$(window).scrollTop();
    if (scrollTop > 187){
        $( '#headerFloat' ).addClass( 'fixer').removeClass('hide');
    } else {
        $( '#headerFloat' ).removeClass( 'fixer').addClass('hide');
    }
});
/*导航条滚动，固定导航 end*/

//立即充值跳转
function to_charge(){
    transferStation(false,'box-right-twelve');
    toggle_menu_content(16);
    setTimeout(function(){
        checkKeyWord();
    },500)
};

//判断是否冻结
function isNotFrozen(){
    var flag = false;
    ajax_get("/member/checkFrozen?"+(new Date()).getTime(),"","",
        function(data){
            if(data.code == 100){
                var result = data.obj.result;
                if(result.frozen){
                    layer.open({
                        type: 1,
                        title: false,
                        area: ["380px","200px"],
                        content: "<div style='padding: 20px;'><div style='padding: 20px 0;'>由于您未及时还款，账期已逾期，您的账户已被冻结！您将无法进行部分操作！</div><div>如有疑问，请联系客服！</div></div>",
                        btn: false
                    })
                }else{
                   flag =  true; 
                }
            }else{
               window.location.href = "/personal/login.html"; 
            }
        },function(e){
            console.log(e);
        }
    )
     return flag;
}
