/**
 * 添加正价商品
 * Created by Administrator on 2016/9/12.
 */
require(['../lib/common'], function (common) {
    require(['vue','zepto','dropload','iscroll','layer','picLazyload','urlPram','session',"frozen"], function (Vue,picLazyload,IScroll){
        //获取已选择的分销商
        isLogin(function(email){});
        var email = $.session.get("email");
        var model = $.session.get("model");
        if (isnull(email)) {
            var dia = $.dialog({
                title: '温馨提示',
                content: '未选择分销商，不能进行选正价商品操作！',
                button: ["确认"]
            });
            dia.on("dialog:action", function (e) {
                window.location.href = 'step_1.html';
            });
        }
        var searchCondtion = getUrlParam("sc");
        if(isnull(searchCondtion)) searchCondtion=''
        var vm = new Vue({
            el:'#body_ele',
            data:{
                categoryId:'',
                categories:[],
                warehouseId:'2024',
                warehouses:[],
                email:'',
                currPage:1,
                nextPage:1,
                totalPage:0,
                products:[],
                selectedProducts:[],// 选中的商品,
                searchCondtion:'',
                showProducts: true,
                existedSelectedProducts:[],// 原来选中的商品,数据库中存在的,
                search:'',
                selectAllChecked:false
            },
            filters:{
                filterChecked:function(sku,warehouseId){
                    if(this.existedSelectedProducts.length>0){
                        for(var i= 0,length=this.existedSelectedProducts.length;i<length;i++){
                            var selectedProduct = this.existedSelectedProducts[i];
                            var skuWarehouseIdQty = selectedProduct.split(',');
                            if(skuWarehouseIdQty[0] == sku && skuWarehouseIdQty[1] == warehouseId){
                                return true;
                            }
                        }
                    }else{
                        return false;
                    }
                }
            },
            methods:{
                selectCategory:function(categoryId){
                    var targetSpan = $("#categorySpan_"+categoryId);
                    targetSpan.parent().parent().parent().find('span').removeClass('current');
                    targetSpan.addClass("current");
                    this.categoryId = targetSpan.attr('categoryId');
                },
                goStep3:function(){
                    if(this.selectedProducts.length==0){
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '请选择要保存的商品！',
                            button: ["确认"]
                        });
                        return;
                    }
                    var skuWarehouseIdBatchNumberArray = vm.selectedProducts;
                    var productsArray = new Array();
                    for (var i = 0, length = skuWarehouseIdBatchNumberArray.length; i < length; i++) {
                        var skuWarehouseIdBatchNumber = skuWarehouseIdBatchNumberArray[i];
                        var array = skuWarehouseIdBatchNumber.split(",");
                        var productJson = {
                            email:email,
                            sku: array[0],
                            warehouseId: array[1],
                            qty: array[2],
                            warehouseName: array[3],
                            productImg: array[4],
                            finalSellingPrice: array[5] == "null" ? 0 : array[5],
                            title: array[6]
                        };
                        productsArray.push(productJson);
                    }
                    if (productsArray.length > 0) {
                        // 保存正价商品信息
                        ajax_post("/sales/input/addProducts", JSON.stringify(productsArray), "application/json",
                            function (data) {
                                var res = JSON.parse(data);
                                if (res.suc) {
                                    // 进入选赠品页面
                                    window.location.href = 'step_3.html';
                                }
                            },
                            function (xhr, status) {
                                console.log("error--->" + status);
                            }
                        );
                    }
                }
            }
        });

        // 监视全选按钮
        vm.$watch('selectAllChecked',function(val){
            var checked = val;
            $(vm.$el).find('input[type="checkbox"][name="list"]').prop('checked',checked);
            vm.selectedProducts = [];
            if(checked){
                $(vm.$el).find('input[type="checkbox"][name="list"]:checked').each(function(i,e){
                    var disPrice = $(e).val().split(",")[5];
                    if (disPrice != "null") {
                        vm.selectedProducts.push($(e).val());
                    }
                });
            }
        });

        // 监视单选按钮
        vm.$watch('selectedProducts',function(val){
            if(vm.selectedProducts.length == vm.products.length) {
                vm.selectAllChecked = true;
            }
        });

        //监视加载的商品列表
        vm.$watch('products',function(val){
            if (vm.selectAllChecked) {
                $(vm.$el).find('input[type="checkbox"][name="list"]').prop('checked',true);
                vm.selectedProducts = [];
                    $(vm.$el).find('input[type="checkbox"][name="list"]:checked').each(function(i,e){
                        var disPrice = $(e).val().split(",")[5];
                        if (disPrice != "null") {
                            vm.selectedProducts.push($(e).val());
                        }
                    });
            }
        });


        vm.searchCondtion=decodeURIComponent(searchCondtion);
        vm.search = encodeURIComponent(encodeURIComponent(vm.searchCondtion));
        vm.$watch('products',function(){
            if(vm.products.length==0)
                vm.showProducts = false;
            else
                vm.showProducts = true;
        });

        //获取此分销商原来勾选的商品
        ajax_get('/sales/input/getInfo?email='+email,null,null,
            function(res) {
                var result = JSON.parse(res);
                if(result.suc){
                    var checkedInputPros = result.data;
                    if(isnotnull(checkedInputPros) && checkedInputPros.length>0){
                        for(var i= 0,length=checkedInputPros.length;i<length;i++){
                            var pro = checkedInputPros[i];
                            var skuWarehouseIdQty = pro.sku+','+pro.warehouseId+','+pro.qty;
                            vm.existedSelectedProducts.push(skuWarehouseIdQty);
                        }
                    }
                }
            }
        );

        // 获取商品分类
        ajax_get('/product/api/realCateQuery?level=1',null,null,
            function(res) {
                vm.categories = res;
            }
        );
        vm.$watch('categoryId', function (val) {
            vm.currPage = 1;
            vm.nextPage = 1;
            vm.totalPage = 0;
            $("#products_div").find('div[class="dropload-down"]').remove();
            initLoad();
        });

        // 仓库
        ajax_get('/inventory/backstage/queryWarehouse',null,null,
            function(res) {
                var list = [];
                $.each(res,function(i,item){
                    if (item.warehouseId == 2024 || item.warehouseId == 2085){
                        list.push(item);
                    }
                });
                if (res) {
                    vm.warehouses = list;
                }
            }
        );
        vm.$watch('warehouseId', function (val) {
            if(isnotnull(vm.warehouseId)){
                vm.currPage = 1;
                vm.nextPage = 1;
                vm.totalPage = 0;
                $("#products_div").find('div[class="dropload-down"]').remove();
                initLoad();
            }
        });

        // 初始化加载
        function initLoad(){
            vm.products=[];
            var params = getSearchParams();
            ajax_post('/product/api/getProducts',JSON.stringify(params),"application/json",
                function(res) {
                    bindData(res.data);
                    loadMore();
                },function(xhr, type) {
                    layer.open({
                        content: "Ajax error!"
                        ,skin: 'msg'
                        ,time: 2 //2秒后自动关闭
                    });
                    // 即使加载出错，也得重置
                    me.resetload();
                }
            );
        }

        initLoad();

        // 下拉加载
        function loadMore(){
            var isload = false;
            if (vm.currPage < vm.totalPage) {
                isload = true;
            }
            if (isload) {
                console.log("----------load  more  data -------------");
                $('#products_div').dropload({
                    scrollArea: window,
                    loadDownFn: function(me) {
                        var params = getSearchParams();
                        ajax_post('/product/api/getProducts',JSON.stringify(params),"application/json",
                            function(res) {
                                bindData(res.data);
                                if (vm.currPage == vm.totalPage) {
                                    // 锁定
                                    me.isLockDown = true;
                                    // 无数据
                                    me.noData();
                                }
                                // 每次数据加载完，必须重置
                                me.resetload();
                            },function(xhr, type) {
                                layer.open({
                                    content: "Ajax error!"
                                    ,skin: 'msg'
                                    ,time: 2 //2秒后自动关闭
                                });
                                // 即使加载出错，也得重置
                                me.resetload();
                            }
                        );
                    }
                });
            }
        };

        // 获取查询参数
        function getSearchParams(){
            var data = {
                "model":model,
                "email":email,
                "pageSize":10,
                "currPage": vm.nextPage,
                "warehouseId": 2024,
                "istatus":1
            };
            if(isnotnull(vm.categoryId)){
                data["categoryId"] = vm.categoryId;
            }
            if(isnotnull(vm.warehouseId)){
                data["warehouseId"] = vm.warehouseId;
            }
            var searchCondtion = vm.searchCondtion;

            if(isnotnull(searchCondtion)){
                var skuPattern = /,/;
                var isSku = skuPattern.test(searchCondtion);
                if(!isSku){
                    data['title'] = searchCondtion;
                }else{
                    var skuList = new Array()
                    var skuArray = searchCondtion.split(",");
                    for(var i=0;i<skuArray.length;i++){
                        skuList.push($.trim(skuArray[i]));
                    }
                    data['skuList'] = skuList;
                }
            }
            return params = {
                "data":data
            };
        }

        // 绑定数据
        function bindData(data){
            vm.currPage = data.currPage;
            vm.nextPage = data.currPage+1;
            vm.totalPage = data.totalPage;
            var list = data.result;
            // 追加数据
            for(var i= 0,length=list.length;i<length;i++){
                if (list[i].isSpecial) {
                    list[i].disPrice = list[i].specialSale;
                }
                list[i].disPrice = list[i].disPrice == null ? null : parseFloat(list[i].disPrice).toFixed(2);
                var murl = list[i].imageUrl || "";
                var imgurl = urlReplace(murl,list[i].csku,null,150,150,80);
                list[i].imageUrl = imgurl;
                vm.products.push(list[i]);
            }
            setTimeout(function(){
                $('.p-goods-img').picLazyLoad({
                    threshold: 100
                });
            },100);
        }

    });
});

