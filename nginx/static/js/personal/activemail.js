require.config({
    baseUrl : "../js/",
    paths : {
        "jquery" : "lib/jquery-1.11.3.min",
        "layer" : "lib/layer2.0/layer",
        "common" : "common"
    },
    shim : {
        "layer" : {
            exports : "",
            deps : ["jquery"]
        },
        "common" : {
            exports : "",
            deps : ["jquery"]
        }
    }
});

require(["jquery", "layer","common"], function($, layer) {
    $(function (){
        var msg = GetQueryString("msg");
        var mes = "<div class='bind-suc-hint'><i class='fail' id='img_mark'></i><span>";
        if(msg == "0" || msg == "8"){
            mes += "地址有误，请确认。";
        }else if(msg == "4"){
            mes += "已经绑定，请勿再次提交。";
        }else if(msg == "6"){
            mes += "验证码错误。";
        }else if(msg == "7"){
            mes += "验证地址已失效。";
        }else if(msg == "1"){
            mes += "恭喜您，验证通过，提现帐号绑定成功。";
        }else{
            mes += "服务器出错啦。";
        }
        mes += "</span></div>";
        $("#wb_activ_email").html(mes);
        if(msg == "1"){
            $("#img_mark").removeClass("fail").addClass("suc");
        }
    });
});