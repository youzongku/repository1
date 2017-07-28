/**
 * 采购单
 * Created by Administrator on 2016/9/12.
 */
require(['../lib/common'], function (common) {
    require(['vue','zepto','dropload','picLazyload'], function (Vue){

        var orderid = getUrlParam("id");
        if(isnull(orderid)){
            window.location.href='polist.html';
        }

        var vm = new Vue({
            el:'#body_ele',
            data:{
                status:'',
                statusMsg:'',
                purchaseOrderNo:'',
                purchaseTotalAmount:'' ,
                email:'',
                inputUser:'',
                details:[],
                sorderDate:'',
                reason:''
            },
            methods:{
            }
        });

        var param ={
            "id":orderid
        }
        // 获取订单
        ajax_post("/purchase/getOrderById", JSON.stringify(param), "application/json",
            function(response) {
                var order = response.orders[0];

                vm.status = order.status
                vm.statusMsg=order.statusMes
                vm.purchaseOrderNo = order.purchaseOrderNo
                vm.purchaseTotalAmount=order.purchaseTotalAmount
                vm.email=order.email
                vm.inputUser=order.inputUser
                vm.sorderDate=order.sorderDate;
                vm.reason=order.reason

                var mergeMap = {};
                $.each(order.details,function(i,item){
                    var key = item.sku +"_"+ item.warehouseId + "_" + item.isgift;
                    var murl = item.productImg||'';
                    item.productImg = urlReplace(murl,item.sku,null,150,150,80);
                   if(mergeMap[key]){
                        mergeMap[key].qty  += item.qty;
                    }else{
                        mergeMap[key] = item;
                    }
                });
                $.each(mergeMap,function(key,value){
                    vm.details.push(value);
                });
                setTimeout(function(){
                    $('.p-goods-img').picLazyLoad({
                        threshold: 100
                    });
                },100);
            }
        );
        
    });
});

