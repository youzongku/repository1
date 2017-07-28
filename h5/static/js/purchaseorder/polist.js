/**
 * 采购单
 * Created by Administrator on 2016/9/12.
 */
require(['../lib/common'], function (common) {
    require(['vue','zepto','dropload','picLazyload'], function (Vue){
        var status = getUrlParam("ss")||9;
        var searchCondtion = getUrlParam("sc");
        if(isnull(searchCondtion)) searchCondtion=''
        var vm = new Vue({
            el:'#body_ele',
            data:{
                status:9, // 状态默认为9的
                haveOrders:true,
                orders:[],
                currPage:1,
                pageSize:10,
                totalPage:0,
                nextPage:1,
                seachFlag:'',
                searchCondtion:''
            },
            filters:{
                selectFirstProImg:function(orderDetails){
                    if(orderDetails.length>0){
                        return orderDetails[0].productImg;
                    }
                    return ""
                },
                selectFirstProName:function(orderDetails){
                    if(orderDetails.length>0){
                        return orderDetails[0].productName;
                    }
                    return ""
                },
                calculateTotalCount:function(orderDetails){
                    var totalCount=0;
                    if(orderDetails.length>0){
                        for(var i=0;i<orderDetails.length;i++){
                            totalCount += orderDetails[i].qty
                        }
                    }
                    return totalCount
                }
            },
            methods:{
                chooseStatus : function(e){
                    var targetStatus = e.target;
                    var status = $(targetStatus).attr("status");
                    this.status = parseInt(status);
                    console.log('status='+status)
                    $(".current2").removeClass("current2");
                    $(targetStatus).addClass("current2");
                    this.currPage = 1;
                    this.nextPage = 1;
                    list();
                }
            }
        });
        $(".current2").removeClass("current2");
        $(vm.$el).find("li[status='"+status+"']").addClass("current2");
        vm.status = status;
        //字符串解码
        vm.seachFlag = decodeURIComponent(searchCondtion);
        vm.searchCondtion = encodeURIComponent(encodeURIComponent(vm.seachFlag));
        function list(){
            vm.orders=[]
            $("#orders").find('div[class="dropload-down"]').remove();
            var params = getSearchParams();
            ajax_post("/purchase/viewpurchase",JSON.stringify(params), "application/json",
                function (data) {
                    // 用户未登录
                    if(data.code == 101){
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: data.msg,
                            button: ["确认"]
                        });
                        dia.on("dialog:action",function(e){
                            window.location.href = "/login.html";
                        });
                        return
                    }
                    // 绑定数据
                    bindData(data);
                    loadMore();
                },
                function (xhr, status) {
                }
            );
        }
        list();

        function loadMore(){
            var isload = false;
            if (vm.currPage < vm.totalPage) {
                isload = true;
            }
            if (isload) {
                console.log("----------load  more  data -------------");
                $('#orders').dropload({
                    scrollArea: window,
                    loadDownFn: function(me) {
                        var params = getSearchParams();
                        ajax_post('/purchase/viewpurchase',JSON.stringify(params),"application/json",
                            function(res) {
                                // 用户未登录
                                if(res.code == 101){
                                    var dia = $.dialog({
                                        title: '温馨提示',
                                        content: res.msg,
                                        button: ["确认"]
                                    });
                                    dia.on("dialog:action",function(e){
                                        window.location.href = "/login.html";
                                    });
                                    return
                                }
                                bindData(res);
                                if (vm.currPage == vm.totalPage) {
                                    // 锁定
                                    me.isLockDown = true;
                                    // 无数据
                                    me.noData();
                                }
                                // 每次数据加载完，必须重置
                                me.resetload();
                            },function(xhr, type) {
                                // 即使加载出错，也得重置
                                me.resetload();
                            }
                        );
                    }
                });
            }
        }

        function getSearchParams() {
            return {
                "status": vm.status,
                "pageSize": vm.pageSize,
                "pageCount": vm.nextPage,
                "seachFlag": vm.seachFlag
            };
        }

        function bindData(res){
            vm.currPage = vm.nextPage
            vm.nextPage = vm.currPage+1;
            vm.totalPage = res.pages;
            var list = res.orders;
            // 追加数据
            for(var i= 0,length=list.length;i<length;i++){
                if(list[i].details.length>0){
                    var murl = list[i].details[0].productImg||'';
                    list[i].details[0].productImg = urlReplace(murl,list[i].details[0].sku,null,150,150,80);
                }
                vm.orders.push(list[i]);

                // 关联的订单
                // if(list[i].hasAssociatedOrders) {
                //     var associatedOrders = list[i].associatedOrders;
                //     for (var j in associatedOrders) {
                //         if(associatedOrders[j].details.length>0){
                //             var murl1 = associatedOrders[j].details[0].productImg||'';
                //             associatedOrders[j].details[0].productImg = urlReplace(murl1,associatedOrders[j].details[0].sku,null,150,150,80);
                //         }
                //         vm.orders.push(associatedOrders[j]);
                //     }
                // }
            }

            if(vm.orders.length>0){
                vm.haveOrders=true
            }  else{
                vm.haveOrders=false
            }
            setTimeout(function(){
                $('.p-goods-img').picLazyLoad({
                    threshold: 100
                });
            },100);
        }
    });
});

