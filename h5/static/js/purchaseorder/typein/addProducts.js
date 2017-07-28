/**
 * 添加正价商品
 * Created by Administrator on 2016/9/12.
 */
require(['../../lib/common'], function (common) {
    require(['vue','zepto','dropload','iscroll','layer','picLazyload','urlPram',"frozen"], function (Vue,picLazyload,IScroll){

        // 获取链接参数
        var inputId = getUrlParam("ii");

        var searchCondtionOrg = getUrlParam("sc");
        var searchCondtion = searchCondtionOrg
        if(isnull(searchCondtion)) {
            searchCondtion='';
            searchCondtionOrg='';
        }else {
            searchCondtion = decodeURIComponent(searchCondtion);
            searchCondtionOrg=encodeURIComponent(searchCondtionOrg);
        }

        if(isnull(inputId)){
            var dia = $.dialog({
                title: '温馨提示',
                content: '未选择分销商，不能进行选正价商品操作！',
                button: ["确认"]
            });
            dia.on("dialog:action",function(e){
                window.location.href='step_1.html';
            });

        }
        var disMode;
        // 检查是否有之前的输入数据
        ajax_post_sync("/purchase/ti/Inputorder/id", JSON.stringify({"inputId":inputId}), "application/json",
            function(response) {
                // 用户未登录
                if(response.code == 101){
                    var dia = $.dialog({
                        title: '温馨提示',
                        content: response.msg,
                        button: ["确认"]
                    });
                    dia.on("dialog:action",function(e){
                        window.location.href = "/login.html";
                    });
                    return
                }
                var inputDto = response.inputDto;
                if(isnull(inputDto)){
                    var dia = $.dialog({
                        title: '温馨提示',
                        content: '未选择分销商，不能进行选正价商品操作！',
                        button: ["确认"]
                    });
                    dia.on("dialog:action",function(e){
                        window.location.href='step_1.html';
                    });
                }
                disMode = inputDto.disMode
            }
        );

        var vm = new Vue({
            el:'#body_ele',
            data:{
                inputId:inputId,
                disMode:disMode,
                categoryId:'',
                categories:[],
                warehouseId:'',
                warehouses:[],
                currPage:1,
                nextPage:1,
                totalPage:0,
                showProducts:true,
                products:[],
                selectedProducts:[],// 选中的商品,
                // existedSelectedProducts:[],// 原来选中的商品,数据库中存在的,
                selectAllChecked:false,
                canSelectAll:false,
                searchCondtion:'',
                searchCondtionOrg:''
            },
            filters:{
                /*
                filterChecked:function(sku,warehouseId){
                    if(this.existedSelectedProducts.length>0){
                        for(var i= 0,length=this.existedSelectedProducts.length;i<length;i++){
                            var selectedProduct = this.existedSelectedProducts[i];
                            var skuWarehouseIdQty = selectedProduct.split(':');
                            if(skuWarehouseIdQty[0] == sku && skuWarehouseIdQty[1] == warehouseId){
                                return true;
                            }
                        }
                    }else{
                        return false;
                    }
                }
                 */
            },
            methods:{
                selectCategory:function(categoryId){
                    var targetSpan = $("#categorySpan_"+categoryId);
                    targetSpan.parent().parent().parent().find('span').removeClass('current');
                    targetSpan.addClass("current");
                    this.categoryId = targetSpan.attr('categoryId');
                },
                goStep3:function(){
                    if(!this.selectAllChecked && this.selectedProducts.length==0){
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '请选择要保存的正价商品！',
                            button: ["确认"]
                        });
                        return;
                    }

                    if(this.selectAllChecked){
                        // 符合条件的所有商品作为正价商品
                        var dataParams = {
                            "istatus":1,
                            "model":this.disMode
                        };
                        // 商品分类
                        if(isnotnull(this.categoryId)){
                            dataParams['categoryId']=this.categoryId;
                        }
                        // 仓库
                        if(isnotnull(this.warehouseId)){
                            dataParams['warehouseId']=this.warehouseId;
                        }
                        if(isnotnull(searchCondtion)){
                            var skuPattern = /,/;
                            var isSku = skuPattern.test(searchCondtion);
                            if(!isSku){
                                dataParams['title'] = searchCondtion;
                            }else{
                                var skuList = new Array()
                                var skuArray = searchCondtion.split(",");
                                for(var i=0;i<skuArray.length;i++){
                                    skuList.push($.trim(skuArray[i]));
                                }
                                dataParams['skuList'] = skuList;
                            }
                        }
                        var params = {
                            'inputId':inputId,
                            'queryProParams':{
                                'data':dataParams
                            }
                        };
                        // 保存正价商品信息
                        ajax_post("/purchase/ti/pros/addAllMatched", JSON.stringify(params), "application/json",
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
                                if (data.suc) {
                                    // 进入选赠品页面
                                    window.location.href = 'step_3.html?ii=' + inputId;
                                }
                            },
                            function (xhr, status) {
                                console.log("error--->" + status);
                            }
                        );
                    }else {
                        var params = {
                            inputId: this.inputId,
                        };
                        var skuWarehouseIdBatchNumberArray = vm.selectedProducts;

                        var productsArray = new Array();
                        for (var i = 0, length = skuWarehouseIdBatchNumberArray.length; i < length; i++) {
                            var skuWarehouseIdBatchNumber = skuWarehouseIdBatchNumberArray[i];
                            var array = skuWarehouseIdBatchNumber.split(":");
                            var productJson = {
                                sku: array[0],
                                warehouseId: array[1],
                                unitType: 1, // 单位类型（1 为单个商品，2 为整箱商品）
                                qty: array[2], // 数量,
                                checked: true // 表示选中的
                            };
                            productsArray.push(productJson);
                        }
                        if (productsArray.length > 0) {
                            params['products'] = productsArray;
                            // 保存正价商品信息
                            ajax_post("/purchase/ti/pros/add", JSON.stringify(params), "application/json",
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
                                    if (data.suc) {
                                        // 进入选赠品页面
                                        window.location.href = 'step_3.html?ii=' + inputId;
                                    }
                                },
                                function (xhr, status) {
                                    console.log("error--->" + status);
                                }
                            );
                        }
                    }
                }
            }
        });

        vm.searchCondtion=searchCondtion;
        vm.searchCondtionOrg=searchCondtionOrg

        vm.$watch('products',function(){
            if(vm.products.length==0)
                vm.showProducts = false;
            else
                vm.showProducts = true;
        })

        // 获取原来选中的正价商品
        /*
        ajax_post("/purchase/ti/pros/checked", JSON.stringify({inputId:inputId}), "application/json",
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
                if(res.suc){
                    var checkedInputPros = res.checkedInputPros;
                    if(isnotnull(checkedInputPros) && checkedInputPros.length>0){
                        for(var i= 0,length=checkedInputPros.length;i<length;i++){
                            var pro = checkedInputPros[i];
                            var skuWarehouseIdQty = pro.sku+':'+pro.warehouseId+':'+pro.qty;
                            vm.existedSelectedProducts.push(skuWarehouseIdQty);
                        }
                    }
                }
            }
        );
        */

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
            canSelectAll();
            initLoad();
        });

        // 仓库
        ajax_get('/inventory/queryWarehouse',null,null,
            function(res) {
                vm.warehouses = res;
            }
        );
        vm.$watch('warehouseId', function (val) {
            vm.currPage = 1;
            vm.nextPage = 1;
            vm.totalPage = 0;
            $("#products_div").find('div[class="dropload-down"]').remove();
            canSelectAll();
            initLoad();
        });

        // 是否可以选择所有
        function canSelectAll(){
            vm.canSelectAll = false;
            if(isnotnull(vm.categoryId) || isnotnull(vm.warehouseId) || isnotnull(searchCondtion)){
                vm.canSelectAll = true;
            }
        }
        canSelectAll();

        // 初始化加载
        function initLoad(){
            vm.products=[];
            var params = getSearchParams();
            ajax_post('/product/api/getProducts',JSON.stringify(params),"application/json",
                function(res) {
                    bindData(res.data);
                    loadMore();
                },function(xhr, type) {
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
                                // 即使加载出错，也得重置
                                me.resetload();
                            }
                        );
                    }
                });
            }
        }

        // 获取查询参数
        function getSearchParams(){
            var data = {
                "istatus":1,
                "pageSize":10,
                "currPage": vm.nextPage,
                "model":vm.disMode
            };
            if(isnotnull(vm.categoryId)){
                data["categoryId"] = vm.categoryId;
            }
            if(isnotnull(vm.warehouseId)){
                data["warehouseId"] = vm.warehouseId;
            }
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
                //图片替换
                var item = list[i];
                var murl = item.imageUrl||'';
                list[i].imageUrl = urlReplace(murl,item.csku,null,150,150,80);
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

