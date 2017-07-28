//定义全局变量
var layer = undefined;

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

function init(){
    var code = getUrlParam(location.search,"d");
    $("#restart_login").on("click", function() {
        window.location.href = "login.html";
    });
    ajax_post("/member/verify", {d : code}, undefined,
        function(data) {
            if(data){
                $("span[name='success']").text(data.cemail);
                postCart(data.cemail);
            }else{
                layer.msg('未查询到注册记录！', {icon : 2, time : 2000});
            }
        },
        function(xhr, textStatus) {
            layer.msg('系统错误！', {icon : 2, time : 2000});
        }
    );
}

require(["jquery", "layer", "common"], function($, layerParam) {
    layer = layerParam;
    init();
});

function postCart(email){
    ajax_post("/cart/addDisCart", {email:email}, undefined,
        function(data) {
        },
        function(xhr, textStatus) {
            layer.msg('系统错误！', {icon : 2, time : 2000});
        }
    );
}