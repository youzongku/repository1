/**
 * 添加正价商品
 * Created by Administrator on 2016/9/12.
 */
require(['../lib/common'], function (common) {
    require(['vue','zepto','dropload','iscroll','layer','picLazyload','urlPram','frozen','session'], function (Vue,IScroll){
        isLogin(function (email) {});
        var distributor = $.session.get("email")
        if (!distributor) {
            var dia = $.dialog({
                title: '温馨提示',
                content: '请先选择分销商！',
                button: ["确认"]
            });
            dia.on("dialog:action",function(e){
                window.location.href = "step_1.html";
            });
            return;
        }
        var vm = new Vue({
            el:'#body_ele',
            data:{
                email:distributor,
                selectedPros:[],//被选中的商品
                products:[],
                showSelectAll:false,
                selectAllChecked:false
            },
            watch:{
                products:function(val, oldVal){// 监视列表的变化
                    if(val.length>0){
                        this.showSelectAll=true;
                        return;
                    }
                    this.showSelectAll=false;
                }
            },
            methods:{
                inputQty:function(event){// 修改正价商品或赠品数量
                    var target = $(event.target);
                    var proId = target.attr('proId');
                    var stock = target.attr('stock');
                    var newQty = target.val(),sourceQty = target.data("qty");
                    var regNum = /^[1-9]\d*$/;
                    if (!regNum.test(newQty)) {
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '请输入数字(必须是大于0的整数)！',
                            button: ["确认"]
                        });
                        target.val(sourceQty);
                        return;
                    }
                    // 不能大于库存
                    if(parseInt(newQty)> parseInt(stock)){
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '数量不能大于库存！',
                            button: ["确认"]
                        });
                        target.val(sourceQty);
                        return;
                    }
                    target.data("qty",newQty);
                    var param = {
                        "id": proId,
                        "qty": newQty
                    };
                    ajax_post("/sales/input/updateInfo", JSON.stringify(param), "application/json",
                        function (res) {
                        },
                        function (xhr, status) {
                        }
                    );
                },
                batchDelete:function(){
                    if (vm.products.length == 0) {
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '请添加商品！',
                            button: ["确认"]
                        });
                        return;
                    }
                    if (vm.selectedPros.length == 0) {
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '请选择商品删除！',
                            button: ["确认"]
                        });
                        return;
                    }
                    var dia=$.dialog({
                        title:'温馨提示',
                        content:'确认要批量删除？',
                        button:["取消","确认"]
                    });
                    dia.on("dialog:action",function(e){
                        if(e.index==0){// 0为取消按钮，1为确认按钮
                            return;
                        }
                        var param = {
                            "ids":vm.selectedPros
                        };
                        ajax_post_sync("/sales/input/deleteProducts", JSON.stringify(param), "application/json",
                            function(res) {
                                var data = JSON.parse(res);
                                if(data.suc){
                                    initLoad()
                                }else{
                                    var dia = $.dialog({
                                        title: '温馨提示',
                                        content: '删除商品失败！',
                                        button: ["确认"]
                                    });
                                }
                            },
                            function(xhr, status) { console.log("error--->" + status); }
                        );
                    });
                    dia.on("dialog:hide",function(e){
                        console.log("dialog hide")
                    });
                },
                goStep2:function(){
                    window.location.href="step_2.html";
                },
                goStep4:function(){
                    if (vm.products.length == 0) {
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '无待生成商品，请添加！',
                            button: ["确认"]
                        });
                    } else {
                        window.location.href = "order.html";
                    }
                }
            }
        });

        // 监视全选按钮
        vm.$watch('selectAllChecked',function(val){
            var checked = val;
            $(vm.$el).find('input[type="checkbox"][name="list"]').prop('checked',checked);
            vm.selectedPros = [];
            if(checked){
                $(vm.$el).find('input[type="checkbox"][name="list"]:checked').each(function(i,e){
                    vm.selectedPros.push($(e).val());
                });
            }
        });

        // 监视单选按钮
        vm.$watch('selectedPros',function(val){
            if(vm.selectedPros.length == vm.products.length) {
                vm.selectAllChecked = true;
            }
        });

        // 初始化加载
        function initLoad(){
            vm.products=[];
            //获取此分销商原来勾选的商品
            ajax_get('/sales/input/getInfo?email='+vm.email,null,null,
                function(res) {
                    var result = JSON.parse(res);
                    if(result.suc){
                        bindData(result.data);
                    }
                }
            );
        }
        initLoad();

        // 绑定数据
        function bindData(list){
            $.each(list,function(i,item){
                var murl = item.productImg||'';
                item.productImg = urlReplace(murl,item.sku,null,150,150,80);
            });
            vm.products = list;
            setTimeout(function(){
                $('.p-goods-img').picLazyLoad({
                    threshold : 200
                });
            },100);
        }
    });
});

