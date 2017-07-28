require(['../lib/common'], function (common) {
    require(['vue','zepto','picLazyload',"frozen"], function (Vue,Zepto,picLazyload){

        isLogin(function (email) {});
        var vm = new Vue({
            el : "#showDetail",
            data:{
                sale:{
                    saleBase:{},
                    saleDetails:[],
                    saleMain:{},
                    platformName:"",
                    senderName:"",
                    senderTel:"",
                    senderAddress:"",
                    orderTotal:""
                }
            }
        });
        getDetail();
        function getDetail(){
            var orderid = getUrlParam("ii");
            var sendInfo = {};
            var info = {};
            ajax_post("/sales/showOrderDetail", JSON.stringify({"orderId":orderid}), "application/json",
                function (data) {
                    if (data.result) {
                        info = data.saleOrderInfo;
                        var orderTotal = 0;
                        for (var i = 0,len=info.saleDetails.length; i<len; i++) {
                            // 取真实单价
                            var warehouseName = info.saleDetails[i].warehouseName;
                            if(warehouseName==undefined || warehouseName==""||warehouseName=='null'){
                                info.saleDetails[i].warehouseName="广州仓";
                            }
                            orderTotal += info.saleDetails[i].finalSellingPrice * info.saleDetails[i].qty;
                        }
                        orderTotal +=info.saleBase.bbcPostage?info.saleBase.bbcPostage:0;
                        info.orderTotal = orderTotal.toFixed(2);
                        var bbcPostage = info.saleBase.bbcPostage;
                        if(bbcPostage == undefined || bbcPostage == null || bbcPostage == "" || bbcPostage == "null"){
                            info.saleBase.bbcPostage = 0;
                        }
                        var purchasePaymentType = getPamentType(deal_with_illegal_value(info.saleMain.purchasePaymentType));
                        info.saleMain.purchasePaymentType = purchasePaymentType;//更新支付方式信息---采购进货
                        var payment_type = getPamentType(deal_with_illegal_value(info.saleMain.paymentType));
                        info.saleMain.paymentType = payment_type;
                        ajax_post_sync("/member/getstoreById", JSON.stringify({"sid":info.saleBase.shopId}), "application/json",
                            function (data) {
                                if (data != undefined){
                                    var senderAddress = data.provinceName?data.provinceName:"";
                                    senderAddress += data.cityName?data.cityName:"";
                                    senderAddress += data.areaName?data.areaName:"";
                                    senderAddress += data.addr?data.addr:"";
                                    sendInfo.platformName = data.otherPlatform ? data.otherPlatform : data.platformName;
                                    sendInfo.senderName = data.keeperName ? data.keeperName : "刘先生";
                                    sendInfo.senderTel = data.telphone ? data.telphone : "18826551638";
                                    sendInfo.senderAddress = senderAddress;
                                }
                            },
                            function (xhr, status) {
                            }
                        );
                        bindData(info,sendInfo);
                    } else {
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: ""+data.msg,
                            button: ["确认"]
                        });
                    }
                },
                function (xhr, status) {
                }
            );
        }
        function  bindData(saleOrderInfo,sendInfo){
            vm.sale.saleBase = saleOrderInfo.saleBase;
            for(var i= 0,length=saleOrderInfo.saleDetails.length;i<length;i++){
                var murl = saleOrderInfo.saleDetails[i].productImg || "";
                var imgurl = urlReplace(murl,saleOrderInfo.saleDetails[i].sku,null,150,150,80);
                saleOrderInfo.saleDetails[i].productImg = imgurl;
                vm.sale.saleDetails.push(saleOrderInfo.saleDetails[i]);
            }
            vm.sale.saleMain = saleOrderInfo.saleMain;
            vm.sale.platformName = deal_with_illegal_value(sendInfo.platformName);
            vm.sale.senderName = deal_with_illegal_value(sendInfo.senderName);
            vm.sale.senderTel = deal_with_illegal_value(sendInfo.senderTel);
            vm.sale.senderAddress = deal_with_illegal_value(sendInfo.senderAddress);
            vm.sale.orderTotal = saleOrderInfo.orderTotal;
            setTimeout(function(){
                 $('.p-goods-img').picLazyLoad({
                    threshold: 100
                });
            },100);
        };

        //获取支付方式
        function getPamentType(payment_type){
            return 	payment_type == "alipay" ? "支付宝" :
                payment_type == "wechatpay" ? "微信支付" :
                    payment_type == "yijifu" ? "易极付" :
                        payment_type == "system" ? "余额支付" :
                            payment_type == "yjf_wx" ? "易极付微信扫码支付" :payment_type;
        }
    });
});

