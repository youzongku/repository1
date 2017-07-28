require.config({
    baseUrl: "../js/",
    paths: {
        "jquery": "lib/jquery-1.11.3.min",
        "layer": "lib/layer2.0/layer",
        "common": "common",
        "session": "personal/jquerysession",
        "scroll":"lib/mCustomScrollbar/jquery.mCustomScrollbar.min"
    },
    shim: {
        "layer": {
            exports: "",
            deps: ["jquery"]
        },
        "common": {
            exports: "",
            deps: ["jquery","scroll"]
        },
        "session": {
            exports: "",
            deps: ["jquery"]
        }    
    }
});

require(["jquery", "layer", "common", "session"], function ($, laye) {
    var layer = laye;
    //支付完成统一跳转的此页面
    // var orderNo = GetQueryString("od");
    // var purPay = GetQueryString("ispu");
    var isok =  GetQueryString("isok");
    var transamount = GetQueryString("transamount");
    if(transamount == null || transamount == undefined || transamount == ''){
        transamount = "--";
    }
    $(".money-span").text(transamount);
    //如果是采购单，根据订单号查询订单类型，如果订单为普通采购在可以发货
   //立即发货按钮显示或隐藏
   //  ajax_post("/purchase/getByNo", JSON.stringify({"purchaseOrderNo":orderNo,"flag":"SUCCESS"}), "application/json",
   //      function(getByNoRes) {
   //          if(getByNoRes&&getByNoRes.purchaseType == 1){
   //              $("#now_sale").show().click(function(){
   //                  check(getByNoRes.pros,orderNo);
   //              });
   //              $("#now_sale_msg").show();
   //              now_sale_msg
   //          }else{
   //              $("#now_sale").hide();
   //              $("#now_sale_msg").hide();
   //          }
   //      },function(e){
   //          console.log(e);
   //      }
   //  );

    // function check(pros,orderNo){
    //     var wareMap = {};
    //     $.each(pros,function(i,item){
    //         wareMap[item.warehouseId] = item.sku;
    //     });
    //     if(Object.keys(wareMap).length > 1){
    //         layer.msg('不同仓库商品请分开下单，系统自动拆单正在开发中!', {icon: 6}, function (index) {
    //             layer.close(index);
    //         });
    //     }else{
    //         //将订单号放在session中 下完发货单后，删除seesion
    //         $.session.set("pur_sale",orderNo);
    //         window.location.href='../personal/personal.html?emnunum=4';
    //     }
    // }
});
