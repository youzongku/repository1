/**
 * 添加正价商品
 * Created by Administrator on 2016/9/12.
 */
require(['../../lib/common'], function (common) {
    require(['vue','zepto','dropload','iscroll','layer','picLazyload','urlPram','frozen'], function (Vue,picLazyload,IScroll){
        // 获取链接参数
        var inputId = getUrlParam("ii");
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
        // 检查是否有之前的输入数据
        ajax_post("/purchase/ti/Inputorder/id", JSON.stringify({"inputId":inputId}), "application/json",
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
            }
        );

        var vm = new Vue({
            el:'#body_ele',
            data:{
                inputId:inputId,
                disAccount:'',
                //prosGiftMappings:[],
                proList:[],  // 正价商品列表
                giftList:[], // 赠品列表
                selectedProIds:[],
                selectedGiftIds:[],
                selectAllChecked:false,
                showSelectAll:false,
                showBatchDeleteBtn:true,
                disMode:0
            },
            watch:{
                prosGiftMappings:function(val, oldVal){// 监视列表的变化
                    if(val.length>0){
                        this.showSelectAll=true;
                        //this.showBatchDeleteBtn = true;
                        return;
                    }
                    this.showSelectAll=false;
                    //this.showBatchDeleteBtn = false;
                }
            },
            methods:{
                deleteBatch:function(){// 批量删除
                    if(this.selectedProIds.length==0 && this.selectedGiftIds.length==0){
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '请选择要删除的正价商品或赠品！',
                            button: ["确认"]
                        });
                        return;
                    }

                    var dia=$.dialog({
                        content:'您确认要删除？',
                        button:["取消","删除"]
                    });

                    dia.on("dialog:action",function(e){
                        if(e.index){
                            // 删除正价商品
                            if(vm.selectedProIds.length>0){
                                var proIdsParam={
                                    proIds:vm.selectedProIds.toString()
                                };
                                ajax_post("/purchase/ti/pro/rm", JSON.stringify(proIdsParam), "application/json",
                                    function(data) {
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
                                        // 删除成功，置为空
                                        vm.selectedProIds = [];
                                        if(data.suc){
                                            initLoad()
                                        }else{
                                            var dia = $.dialog({
                                                title: '温馨提示',
                                                content: '删除正价商品失败！',
                                                button: ["确认"]
                                            });
                                        }
                                    },
                                    function(xhr, status) { console.log("error--->" + status); }
                                );
                            }
                            // 删除赠品
                            if(vm.selectedGiftIds.length>0){
                                var proIdsParam={
                                    giftIds:vm.selectedGiftIds.toString()
                                };
                                ajax_post("/purchase/ti/gift/rm", JSON.stringify(proIdsParam), "application/json",
                                    function(data) {
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
                                        // 删除成功，置为空
                                        vm.selectedGiftIds = [];
                                        if(data.suc){
                                            initLoad();
                                        }else{
                                            var dia = $.dialog({
                                                title: '温馨提示',
                                                content: '删除赠品失败！',
                                                button: ["确认"]
                                            });
                                        }
                                    },
                                    function(xhr, status) { console.log("error--->" + status); }
                                );
                            }
                        }
                        console.log(e.index)
                    });
                    dia.on("dialog:hide",function(e){
                        console.log("dialog hide")
                    });
                },
                checkNum:function(){
                    var target = $(event.target);
                    var words = target.val();
                    var keyCode = event.which;
                    if (keyCode == 46 || (keyCode >= 48 && keyCode <= 57)) {
                        return true;
                    }
                },
                inputProQty:function(event){// 修改正价商品或赠品数量
                    console.log(event);
                    var target = $(event.target);
                    var proId = target.attr('proId');
                    var stock = parseInt(target.attr('stock'));
                    var batchNumber = parseInt(target.attr('batchNumber'));
                    var oldQty = parseInt(target.attr("oldValue"));
                    var newQtyStr = $.trim(target.val());
                    if(isnull(newQtyStr)){
                        target.val(oldQty)
                        return;
                    }
                    var newQty = parseInt(newQtyStr);

                    var pattern = /^[1-9]\d*$/;
                    if(!pattern.test(newQty)){
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '数量只能为正整数！',
                            button: ["确认"]
                        });
                        target.val(oldQty)
                        return;
                    }
                    
                    if(newQty==oldQty)  return;

                    // 不能小于起批量
                    if(newQty<batchNumber){
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '数量不能小于起批量！',
                            button: ["确认"]
                        });
                        newQty=batchNumber
                    }else if(newQty>stock){  // 不能大于库存
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '数量不能大于库存！',
                            button: ["确认"]
                        });
                        newQty=stock
                    }
                    target.val(newQty)
                    if(newQty==oldQty)  return;
                    target.attr("oldValue",newQty)

                    console.log("更新正价商品的数量为"+newQty)
                    // 更新数量
                    var params={
                        proId:proId,
                        qty:newQty
                    };
                    ajax_post("/purchase/ti/pro/qty/update", JSON.stringify(params), "application/json",
                        function(data) {
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
                        },
                        function(xhr, status) { console.log("error--->" + status); }
                    );
                },
                inputGiftQty:function(event){// 修改赠品数量
                    console.log(event);
                    var target = $(event.target);
                    var giftId = target.attr('giftId');
                    var stock = parseInt(target.attr('stock'));
                    var newQtyStr = $.trim(target.val());
                    var oldQty = parseInt(target.attr("oldValue"));
                    if(isnull(newQtyStr)){
                        target.val(oldQty)
                        return;
                    }
                    var newQty = parseInt(newQtyStr);

                    if(isnull(newQty)){
                        target.val(oldQty)
                        return;
                    }

                    var pattern = /^[1-9]\d*$/;
                    if(!pattern.test(newQty)){
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '数量只能为正整数！',
                            button: ["确认"]
                        });
                        target.val(oldQty)
                        return;
                    }

                    if(newQty==oldQty)  return;

                    // 不能小于1
                    if(newQty<1){
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '数量不能小于1！',
                            button: ["确认"]
                        });
                        newQty = 1
                    }else if(newQty>stock){  // 不能大于库存
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '数量不能大于库存！',
                            button: ["确认"]
                        });
                        newQty = stock
                    }
                    target.val(newQty)
                    if(newQty==oldQty)  return;
                    target.attr("oldValue",newQty)

                    console.log("更新赠品的数量为"+newQty)
                    var params={
                        giftId:giftId,
                        qty:newQty
                    };
                    ajax_post("/purchase/ti/gift/qty/update", JSON.stringify(params), "application/json",
                        function(data) {
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
                        },
                        function(xhr, status) { console.log("error--->" + status); }
                    );
                },
                edit:function(){// 编辑弹窗
                    layer.open({
                        title: [
                            '请选择编辑项',
                            'background-color:#666; color:#fff;'
                        ],
                        anim: 'up',
                        content: '<div class="editPop">' +
                        '<p><a href="step_2.html?ii='+inputId+'"><span>添加正价商品</span></a></p>' +
                        '<p><a id="goAddGift_a"><span>添加赠品</span></a></p>' +
                        '</div>'
                    });
                    $("#goAddGift_a").on('click',this.goAddGift);
                },
                goAddGift:function(){
                    /*
                    if(this.selectedProIds.length==0){
                        // var dia = $.dialog({
                        //     title: '温馨提示',
                        //     content: '请先选择正价商品！',
                        //     button: ["确认"]
                        // });
                        alert("请先选择正价商品")
                        return;
                    } */
                    window.location.href='step_3.html?ii='+inputId;
                },
                createOrder:function(){// 下单
                    initLoad();

                    // if(this.prosGiftMappings.length==0){
                    if(this.proList.length==0){
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '没有要下单的正价商品！',
                            button: ["确认"]
                        });
                        return;
                    }
                    var totalPrice = 0;
                    var totalCount = 0;
                    for (var p=0;p<this.proList.length;p++){
                        totalCount += this.proList[p].qty;
                        totalPrice += (this.proList[p].qty * this.proList[p].price);
                    }
                    for (var g=0;g<this.giftList.length;g++){
                        totalCount += this.giftList[g].qty;
                    }
                    // for (var i=0,len=this.prosGiftMappings.length;i<len;i++){
                    //     for (var p=0;p<this.prosGiftMappings[i].pros.length;p++){
                    //         totalCount += this.prosGiftMappings[i].pros[p].qty;
                    //         totalPrice += (this.prosGiftMappings[i].pros[p].qty * this.prosGiftMappings[i].pros[p].price);
                    //     }
                    //     for (var g=0;g<this.prosGiftMappings[i].gifts.length;g++){
                    //         totalCount += this.prosGiftMappings[i].gifts[g].qty;
                    //     }
                    // }
                    totalPrice = parseFloat(totalPrice).toFixed(2);
                    // var dia=$.dialog({
                    //     title:'温馨提示',
                    //     content:'确认要生成订单？',
                    //     button:["取消","确认"]
                    // });
                    var html =
                        '<span>' +
                        '<em style="margin-right: 10px;">商品总数：<label>'+totalCount+'</label></em>' +
                        '<em>合计：￥<label>'+totalPrice+'</label></em>' +
                        '</span>'+
                        (this.disMode == 1?"":
                        '<span class="remind-cash" style="width:100%;">' +
                        '<em style="width:35%;display: inline-block;vertical-align: top;">是否收款：</em>' +
                        '<em style="width:60%;display: inline-block">' +
                        '<label>' +
                        '<input style="-webkit-appearance: radio" type="radio" value="" checked="checked"  name="po_payType">未收款' +
                        '</label>'+
                        '<br >'+
                        '<label>' +
                        '<input style="-webkit-appearance: radio" type="radio" value="cash"  name="po_payType">已收款' +
                        '</label>' +
                        '<input type="text" name="money" style="width:65px;border: 1px solid #eee;margin-left: 5px;">' +
                        '<label>' +
                        '<input style="-webkit-appearance: radio" type="radio" value="cash-online"  name="po_payType">线下转账' +
                        '</label>' +
                        '</em>' +
                        '</span>')
                    var dia=$.dialog({
                        title:'温馨提示',
                        content:html,
                        button:["取消","确认"]
                    });
                    dia.on("dialog:action",function(e){
                        // 支付方式
                        var payType = $(".remind-cash").find('input[type="radio"]:checked').val();
                        // var ispaied = $(".remind-cash").find('input[type="radio"]').eq(1).prop("checked");
                        // var fcard = $(".remind-cash").find('input[type="radio"]').eq(2).prop("checked");

                        var param = {
                            inputId:inputId
                        };
                        if (payType=="cash") {
                            var cashAmount = $(".remind-cash").find('input[name="money"]').val();
                            var reg = /^([1-9][\d]{0,7}|0)(\.[\d]{1,2})?$/;
                            if (!reg.test(cashAmount)){
                                if(e.index==0){
                                    window.location.href='../polist.html';
                                }else{
                                    $.dialog({
                                        title: '温馨提示',
                                        content: '请输入规范金额！',
                                        button: ["确认"]
                                    });
                                }
                                return;
                            }
                            // 现金交易金额
                            param.money = parseFloat(cashAmount).toFixed(2);
                        }
                        if(e.index==0){// 0为取消按钮，1为确认按钮
                            return;
                        }
                        if(isnotnull(inputId)){
                            // 支付方式
                            param.payType = payType;

                            // 有商品时才下单
                            ajax_post("/purchase/inputOrder", JSON.stringify(param), "application/json", function(response) {
                                // 用户未登录
                                if(response.code == 101){
                                    var dia_notLogined = $.dialog({
                                        title: '温馨提示',
                                        content: response.msg,
                                        button: ["确认"]
                                    });
                                    dia_notLogined.on("dialog:action",function(e){
                                        window.location.href = "/login.html";
                                    });
                                    return
                                }
                                if(!response.suc){
                                    $.dialog({
                                        title: '温馨提示',
                                        content: '下单失败！',
                                        button: ["确认"]
                                    });
                                    return;
                                }

                                var dia_orderSuccessfully = $.dialog({
                                    title: '温馨提示',
                                    content: '下单成功！',
                                    button: ["确认"]
                                });
                                dia_orderSuccessfully.on("dialog:action",function(e){
                                    // 线下转账
                                    if(payType == 'cash-online') {
                                        window.location.href='step_5.html?ii='+response.purchaseOrderNo;
                                    }else{
                                        window.location.href='../polist.html';
                                    }
                                });
                            });
                        }
                    });
                    dia.on("dialog:hide",function(e){
                        console.log("dialog hide")
                    });
                }
            }
        });

        // 监视全选按钮
        vm.$watch('selectAllChecked',function(val){
            var checked = val;
            $(vm.$el).find('input[type="checkbox"][name^="selected"]').prop('checked',checked);

            vm.selectedProIds = [];
            vm.selectedGiftIds = [];
            if(checked){
                // 正价商品
                $(vm.$el).find('input[type="checkbox"][name="selectedProId"]:checked').each(function(i,e){
                    vm.selectedProIds.push($(e).val());
                });
                // 赠品
                $(vm.$el).find('input[type="checkbox"][name="selectedGiftId"]:checked').each(function(i,e){
                    vm.selectedGiftIds.push($(e).val());
                });
            }
        });

        // 监视单选按钮
        vm.$watch('selectedProIds',function(val){
            if(vm.selectedProIds.length>0) {
                // 更新正价商品的checked状态
                var params = {
                    inputId: inputId,
                    proIds: vm.selectedProIds.toString()
                }
                ajax_post("/purchase/ti/pro/checked/update", JSON.stringify(params), "application/json",
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
                            console.log("更新checked成功");
                        }
                    },
                    function (xhr, status) {
                        console.log("error--->" + status);
                    }
                );
            }
        });

        // 初始化加载
        function initLoad(){
            vm.products=[];
            var params = {inputId:vm.inputId};
            ajax_post_sync('/purchase/ti/Inputorder/id',JSON.stringify(params),"application/json",
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
                    bindData(res.inputDto);
                },function(xhr, type) {
                }
            );
        }

        initLoad();

        // 绑定数据
        function bindData(inputDto){
            vm.disAccount = inputDto.disAccount
            // var prosGiftMappings = inputDto.prosGiftMappings;
            //图片替换
            // $.each(prosGiftMappings,function(i,item){
            //     $.each(item.pros,function(j,pro){
            //         var murl = pro.imageUrl||'';
            //         pro.imageUrl = urlReplace(murl,pro.sku,null,150,150,80);
            //     });
            // });
            // vm.prosGiftMappings = prosGiftMappings;

            var proList = inputDto.proList  // 正价商品
            var giftList = inputDto.giftList // 赠品
            //图片替换
            $.each(proList,function(j,pro){
                var murl = pro.imageUrl||'';
                pro.imageUrl = urlReplace(murl,pro.sku,null,150,150,80);
            });
            $.each(giftList,function(j,gift){
                var murl = gift.imageUrl||'';
                gift.imageUrl = urlReplace(murl,gift.sku,null,150,150,80);
            });

            vm.proList = proList
            vm.giftList = giftList
            vm.disAccount = inputDto.disAccount;
            vm.disMode = inputDto.disMode;
            setTimeout(function(){
                $('.p-goods-img').picLazyLoad({
                    threshold : 200
                });
            },100);
        }
    });
});

