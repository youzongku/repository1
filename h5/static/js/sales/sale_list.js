require(['../lib/common'], function (common) {
    require(['vue','zepto','dropload','iscroll','picLazyload',"frozen"], function (Vue,Zepto,picLazyload,IScroll){

        isLogin(function (email) {});
        var status = getUrlParam("ss")||0;
        var searchCondtion = getUrlParam("sc");
        if(isnull(searchCondtion)) searchCondtion=''
        var vm = new Vue({
            el : "#sale_list",
            data:{
                email:'',
                sales:[],
                status:0,
                currPage:1,
                pageSize:10,
                totalPage:0,
                nextPage:this.currPage,
                searchCondtion:'',
                haveOrders:true
            },
            filters:{
                filterFinalSellingPrice:function(finalSellingPrice){
                    if(!finalSellingPrice || isNaN(finalSellingPrice)){
                        return 0.00;
                    }
                    return finalSellingPrice
                }
            },
            methods : {
                showDetail : function(e){
                    var targetTime = e.target;
                    var orderid = $(targetTime).parents(".imporCom").attr("orderid");
                    window.location.href='detail.html?ii='+orderid;
                },
                chooseStatus : function(e){
                    var targetStatus = e.target;
                    var status = $(targetStatus).attr("status");
                    $(".current2").removeClass("current2");
                    $(targetStatus).addClass("current2");
                    this.status = status; //可能是字符串：1,2,3，也可能是单个值：1
                    this.currPage = 1;
                    this.nextPage = 1;
                    $("#orders").find("div[class='dropload-down']").remove()
                    sale_list();
                }
            }
        });
        // 登陆
        isLogin(function (email) {
            vm.email = email;
        });
        $(".current2").removeClass("current2");
        $(vm.$el).find("li[status='"+status+"']").addClass("current2");
        vm.status = status;
        vm.searchCondtion = searchCondtion;
        sale_list();
        function sale_list() {
            vm.sales=[]
            var params = getSearchParams();
            ajax_post("/sales/manager/getSalesForTel", JSON.stringify(params), "application/json",
                function (data) {
                    // 绑定数据
                    bindData(data);
                    loadMore();
                },
                function (xhr, status) {
                }
            );

        }
        function getSearchParams() {
            return {
                "status": vm.status,  //可能是字符串：1,2,3，也可能是单个值：1
                "pageSize": vm.pageSize,
                "currPage": vm.nextPage,
                "seachSpan":vm.searchCondtion
            };
        }
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
                        ajax_post('/sales/manager/getSalesForTel',JSON.stringify(params),"application/json",
                            function(res) {
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
        function bindData(res){
            var data = JSON.parse(res);
            vm.currPage = data.currPage;
            vm.nextPage = data.currPage+1;
            vm.totalPage = data.pages;
            var list = data.saleOrderInfos;
            // 追加数据
            for(var i= 0,length=list.length;i<length;i++){
                var murl = list[i].details.productImg || "";
                var imgurl = urlReplace(murl,list[i].details.sku,null,150,150,80);
                list[i].details.productImg = imgurl;
                list[i].details.finalSellingPrice = parseFloat(list[i].details.finalSellingPrice).toFixed(2);
                vm.sales.push(list[i]);
            }
            if (vm.sales.length > 0) {
                vm.haveOrders = true;
            } else {
                vm.haveOrders = false;
            }
            setTimeout(function(){
                $('.p-goods-img').picLazyLoad({
                    threshold: 100
                });
            },100);
        }
    });
});
