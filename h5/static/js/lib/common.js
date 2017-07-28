require.config({
    paths: {
        "zepto": '/js/lib/zepto',
        "zepto_cookie": '/js/lib/zepto.cookie.min',
        "iscroll": '/js/lib/iscroll',
        "dropload": '/js/lib/dropload.min',
        "layer": '/js/lib/layer',
        "picLazyload": '/js/lib/picLazyLoad.min',
        "urlPram": '/js/lib/getUrlParam',
        "vue": '/js/lib/vue',
        "frozen": '/js/lib/frozen',
        "session": '/js/lib/jquerysession',
        "laydate": '/js/lib/laydate',
        "jqform" : "/js/lib/jquery.form"
    },
    shim: {
        "jqform":{deps: ['zepto']},
        "zepto_cookie": {deps: ['zepto']},
        "dropload": {deps : ['zepto']},
        "picLazyload": ['zepto'],
        "frozen": {deps: ['zepto']},
        "session": {deps: ['zepto']}
    }
});


//全局设置
require(["zepto","frozen"], function(FastClick,Clipboard) {
    $("header .set-out").on("click", function(){
         $.get("/member/adminlogout?" + (new Date()).getTime(), function (response) {
            window.location.href = "/login.html";
        });
    });
    $("header .home-logo").on("click", function(){
        window.location.href = "/user/index/index.html"
    });
});
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
    var tmpImgUrl = "http://static.tomtop.com.cn/imaging/imaging/product/" + sku;
    if (fromPage == 'desc') {
        tmpImgUrl = "http://static.tomtop.com.cn/imaging/imaging/product/detail/" + sku;
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
    srcUrl = srcUrl || "";
    var imgName = srcUrl.substr(srcUrl.lastIndexOf("\/"));

    if (width == undefined || height == undefined) {
        resUrl = tmpImgUrl + imgName;

    } else {
        resUrl = tmpImgUrl + "/" + parseInt(width) + "-" + parseInt(height) + "-" + quality + imgName;
    }
    return resUrl;
}
/*
 *获取url参数扩展方法 $.getUrlParam('elem_id')
 *
 */
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" +
        name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}
/**
 *
 * 去除左右空白符号
 */
function trim(str) {
    return str.replace(/(^\s*)|(\s*$)/g, "");
}
/**
 * 判断是否为空
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
 * 判断是否不为空
 * @param data
 * @returns {boolean}
 */
function isnotnull(data){
    return !isnull(data);
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

/**
 * 通用GET方式AJAX请求
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
            errorCallback(XMLHttpRequest, textStatus);
        }
    });
}

function deal_with_illegal_value(value) {
    return ("number" == typeof value) ? value : (value == undefined || value == null || value == "") ? "---" : value;
}

/**
 * 通用POST方式AJAX请求
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
            errorCallback(XMLHttpRequest, textStatus);
        }
    });
}

/**
 * 通用 同步 POST方式AJAX请求
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
            errorCallback(XMLHttpRequest, textStatus);
        }
    });
}

/**
 *
 * 根据cookie名获取cookie工具方法
 */
function getCookie(cName) {
    var cookies = document.cookie;
    var cookieArr = cookies.split(";");
    var res;
    for (var i = 0, len = cookieArr.length; i < len; i++) {
        var cookie = cookieArr[i].split("=");
        if (trim(cookie[0]) == cName) {
            res = trim(cookie[1]);
        }
    }
    return res;
}
/**
 *
 * 加cookie;
 * cookie是一长串字符
 **/
function addCookie(cookie) {
    var cookies = document.cookie;
    cookies += "; " + cookie;
    document.cookie = cookies;
}

function isLogin(back_email) {
    var url = "/member/isaulogin?" + (new Date()).getTime();
    $.ajax({
        url: url,
        type: "get",
        dataType: "json",
        async: false,
        success: function (data) {
            if (data.suc) {
                back_email(data.user.email);
            } else {
                window.location.href = "/login.html";
            }
        }
    });
}

/*取当前域名，不包含 www   by oyx */
function getRealhost() {
    return handleHost(location.host);
}

function handleHost(h) {
    if (h.indexOf("www") == -1) {
        return h;
    } else {
        return h.substring(h.indexOf("w.") + 2);
    }
}

//手机号码格式校验
function checkTel(telNo) {
    var regTel = /^(13[0-9]|15[012356789]|17[10678]|18[0-9]|14[57])[0-9]{8}$|(0[1-9][0-9])-(\d{7,8})$|(0[1-9]\d{2}-(\d{7,8}))$/;
    return regTel.test(telNo);
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

function log(data) {
    typeof(data) === 'string' ? console.log(data) : console.log(JSON.stringify(data));
}
//根据身份证号，获取性别，生日
function discriCard(idcard) {
    //获取输入身份证号码 
    var UUserCard = idcard;
    var map = {};
    //获取出生日期 
    var birthday = UUserCard.substring(6, 10) + "-" + UUserCard.substring(10, 12) + "-" + UUserCard.substring(12, 14);
    map['birthday'] = birthday;
    //获取性别 
    if (parseInt(UUserCard.substr(16, 1)) % 2 == 1) {
        // alert("男");
        //是男则执行代码 ... 
        map['sex'] = '男';
    } else {
        // alert("女");
        //是女则执行代码 ... 
        map['sex'] = '男';
    }

    //获取年龄 
    var myDate = new Date();
    var month = myDate.getMonth() + 1;
    var day = myDate.getDate();
    var age = myDate.getFullYear() - UUserCard.substring(6, 10) - 1;
    if (UUserCard.substring(10, 12) < month || UUserCard.substring(10, 12) == month && UUserCard.substring(12, 14) <= day) {
        age++;
    }
    // alert(age);
    //年龄 age 
    return map;
}
/*校验身份证号码   by oyx */
function identityCardValid(code) {
    var city = {
        11: "北京",
        12: "天津",
        13: "河北",
        14: "山西",
        15: "内蒙古",
        21: "辽宁",
        22: "吉林",
        23: "黑龙江 ",
        31: "上海",
        32: "江苏",
        33: "浙江",
        34: "安徽",
        35: "福建",
        36: "江西",
        37: "山东",
        41: "河南",
        42: "湖北 ",
        43: "湖南",
        44: "广东",
        45: "广西",
        46: "海南",
        50: "重庆",
        51: "四川",
        52: "贵州",
        53: "云南",
        54: "西藏 ",
        61: "陕西",
        62: "甘肃",
        63: "青海",
        64: "宁夏",
        65: "新疆",
        71: "台湾",
        81: "香港",
        82: "澳门",
        91: "国外 "
    };
    var tip = "";
    var pass = true;

    if (!code || !/^\d{6}(18|19|20)?\d{2}(0[1-9]|1[12])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i.test(code)) {
        tip = "身份证号格式错误";
        pass = false;
    } else if (!city[code.substr(0, 2)]) {
        tip = "地址编码错误";
        pass = false;
    } else {
        //18位身份证需要验证最后一位校验位
        if (code.length == 18) {
            code = code.split('');
            //∑(ai×Wi)(mod 11)
            //加权因子
            var factor = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
            //校验位
            var parity = [1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2];
            var sum = 0;
            var ai = 0;
            var wi = 0;
            for (var i = 0; i < 17; i++) {
                ai = code[i];
                wi = factor[i];
                sum += ai * wi;
            }
            var last = parity[sum % 11];
            if (parity[sum % 11] != code[17]) {
                pass = false;
            }
        }
    }
    return pass;
}

// 对Date的扩展，将 Date 转化为指定格式的String   
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，   
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)   
// 例子：   
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423   
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18   
Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

function SetCookie(name, value, time) { //time单位为 分钟
    var Days = 30;
    var exp = new Date();
    exp.setTime(exp.getTime() + time * 1000 * 60); //过期时间 1*1000*60ms= 6000ms = 1分钟
    document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
}
//设置跨域cookie
function SetGloabalCookie(name, value, time) { //time单位为 分钟
    var Days = 30;
    var exp = new Date();
    exp.setTime(exp.getTime() + time * 1000 * 60); //过期时间 1*1000*60ms= 6000ms = 1分钟
    document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString() + ";domain=tomtop.hk";
}
/*自动登录  ck:cookie信息*/
function autoLogin(ck) {
    SetCookie(ck.name, ck.value, ck.expire);
}

function checkEmail(email) {
    var zip = /^([a-zA-Z0-9]+[\_|\-|\.]?)+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,20}){1,20})$/;
    return zip.test(email)
}

