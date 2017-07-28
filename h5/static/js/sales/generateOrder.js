/**
 * 添加正价商品
 * Created by Administrator on 2016/9/12.
 */
require(['../lib/common'], function (common) {
    require(['vue','zepto','dropload','iscroll','layer','picLazyload','urlPram','frozen','session'], function (Vue,picLazyload,IScroll){
        isLogin(function (email) {});
        var distributor = $.session.get("email");
        var distributorType = $.session.get("distributorType");
        var disMode = $.session.get("model");
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
            el: '#body_ele',
            data: {
                receiver:"",
                tel:"",
                provinces:[],
                citys:[],
                regions:[],
                addr:"",
                postcode:"",
                products:[],
                provinceIdForFreight:0,
                isPay:false
            },
            methods:{
                getProvince:function(event){//点击得到省份
                    var target = $(event.target);
                    $(".curr").hide().removeClass("curr");
                    $("#provinceIds").addClass("curr").show();
                    $(".ret-addr").show();
                    $(".ret-addr").siblings().hide();
                    $(".opration").hide();
                },
                cancleGenerate:function(){
                    window.location.href = "../../user/index/index.html";
                },
                checkfee:function(){
                    var target = $(event.target);
                    var fee = target.val();
                    var reg = /^[0-9]+(.[0-9]{1,2})?$/;
                    var flag = fee?reg.test(fee)&&fee>0:false;
                    if(!flag){
                        var dia=$.dialog({
                            title:'温馨提示',
                            content:"请输入有效的[<b style='color:#E74C3C'>第三方物流费用</b>]（至多两位小数,且必须大于零）",
                            button:["确认"]
                        });
                        target.val("");
                    }
                },
                generateSaleOrder:function(){//生成订单
                    var flag = validation();
                    if (!flag) {
                        return;
                    }
                    var dia=$.dialog({
                        title:'温馨提示',
                        content:'确认要生成订单？',
                        button:["取消","确认"]
                    });
                    dia.on("dialog:action",function(e){
                        if(e.index==0){// 0为取消按钮，1为确认按钮
                            return;
                        }
                        isLogin(function(salesman){
                            var address = $(".chooseAddr").text().trim() + " " + vm.addr.trim();

                            var param = {
                                "email" : distributor,
                                "warehouseId" : undefined,
                                "warehouseName" : undefined,
                                "remark" : "",
                                "address" : address,
                                "receiver" : vm.receiver,
                                "telphone" : vm.tel,
                                "postCode": vm.postcode,
                                "LogisticsTypeCode": $("#shippings").val().split("/")[0],
                                "logisticsMode": $("#shippings").val().split("/")[1] ? $("#shippings").val().split("/")[1] : null,
                                "createUser": salesman,
                                "isBack": true,
                                "provinceId" : vm.provinceIdForFreight,
                                "isPay" : vm.isPay,
                                "skuList" : []
                            };
                            if(param.LogisticsTypeCode == 'BBC-TPL'){
                                param.thirdPostfee = $("#thirdPostfee").val();
                            }
                            var details = [];
                            $.each(vm.products,function(i,item){
                                var pro = {
                                    "sku" : item.sku,
                                    "num" : item.qty,
                                    "finalSellingPrice" : item.finalSellingPrice,
                                    "expirationDate" : ""
                                };
                                param.warehouseId = item.warehouseId;
                                param.warehouseName = item.warehouseName;
                                details.push(pro);
                            });
                            param.skuList = details;

                            //保存订单
                            ajax_post("/sales/manager/postOrder", JSON.stringify(param), "application/json",
                                function (response) {
                                    if(response.code == 108) {
                                        var dia_orderSuccessfully = $.dialog({
                                            title: '温馨提示',
                                            content: "订单已生成，单号为：" + response.msg,
                                            button: ["确认"]
                                        });
                                        dia_orderSuccessfully.on("dialog:action",function(e){
                                            window.location.href='../list.html';
                                        });
                                    } else {
                                        $.dialog({
                                            title: '温馨提示',
                                            content: response.msg,
                                            button: ["确认"]
                                        });
                                    }
                                },
                                function (xhr, status) {
                                }
                            );
                        })
                    });
                    dia.on("dialog:hide",function(e){
                        console.log("dialog hide")
                    });
                }
            }
        });


        function getCity(event){
            $(".curr").hide().removeClass("curr");
            $("#cityIds").addClass("curr").show();
            $("#cities").empty();
            var htmlCode = "";
            ajax_get("/member/getcities", "proId=" + $(event).attr("pid"), "",
                function (data) {
                    $.each(data.cities,function(i,item){
                        htmlCode += '<div code="'+item.zipCode+'" class="impornor addSel cities" pid="'+item.id+'">'+item.cityName+'</div>';
                    });
                    $("#cities").prepend(htmlCode);
                    $(".cities").click(function(){
                        $(this).addClass("cselected");
                        $(this).siblings().removeClass("cselected");
                        vm.postcode = $(this).attr("code");
                        $(".postCode").val(vm.postcode);
                        getArea(this);
                    });
                },
                function (xhr, status) {
                }
            );
        }
        function getArea(event) {
            $(".curr").hide().removeClass("curr");
            $("#regionIds").addClass("curr").show();
            $("#areas").empty();
            var htmlCode = "";
            $.ajax({
                url : '/member/getareas?cityId='+$(event).attr("pid"),
                type : 'GET',
                data : {},
                success : function (data){
                    $.each(data.areas,function(i,item){
                        htmlCode += '<div class="impornor addSel areas" pid="'+item.id+'">'+item.areaName+'</div>';
                    });
                    $("#areas").prepend(htmlCode);
                    $(".areas").click(function(){
                        $(".curr").hide().removeClass("curr");
                        $("#receiverList").addClass("curr").show();
                        $(".opration").show();
                        $(".chooseAddr").html($(".province.pselected").text() + " " + $(".cities.cselected").text() + " " + $(this).text());
                        vm.provinceIdForFreight = $(".province.pselected").attr("pid");
                        getFreight();
                    });
                }
            });
        }
        //得到运费
        function getFreight() {
            $(".autoFreight").prop("checked",false);
            if (vm.provinceIdForFreight == 0) {
                return;
            }
            if (vm.products.length > 0 && vm.products[0].warehouseId == 2024) {
                var target = $("#shippings");
                //物流方式已添加，则选择既存物流方式
                //添加完毕，获取默认选中的物流方式的运费
                var code = target.val().split("/")[0];
                if(code != 'BBC-TPL'){
                    var freightParam = {};
                    freightParam.warehouseId = vm.products[0].warehouseId;
                    freightParam.shippingCode =code;
                    var orderDetails = [];
                    $.each(vm.products,function(i,item){
                        var pro = {};
                        pro.sku = item.sku;
                        pro.num = item.qty;
                        pro.costPrice = item.finalSellingPrice;
                        orderDetails.push(pro);
                    });
                    freightParam.orderDetails = orderDetails;
                    freightParam.countryId = 44;//写死为中国的id
                    freightParam.provinceId = vm.provinceIdForFreight;
                    getFreightByProvince(freightParam);
                    $("#thirdPostfee").hide();
                }else{
                    $("#thirdPostfee").show();
                    $(".freight .sale_freight").hide().text(0.00);
                }
            } else {
                $(".freight .sale_freight").text(0.00);
            }
        }

        /**
         * 根据上分ID获取运费
         * @param freightParam
         */
        function getFreightByProvince(freightParam) {
            var url = "/inventory/getFreight";
            ajax_post(
                "/inventory/getFreight",
                JSON.stringify(freightParam),
                "application/json",
                function (freightResStr) {
                    var freightRes = freightResStr;
                    if (freightRes.result) {
                        //自提标识
                        var fee = freightRes.msg;
                        $(".freight .sale_freight").show().text(parseFloat(fee).toFixed(2));
                    } else {
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '运费获取失败！',
                            button: ["确认"]
                        });
                    }
                },
                function (XMLHttpRequest, textStatus) {
                    var dia = $.dialog({
                        title: '温馨提示',
                        content: '物流方式获取失败！',
                        button: ["确认"]
                    });
                }
            );
        }


        function real_time_freight(){
            var code = $("#shippings").val().split("/")[0];
            return get_post_fee(code);
        };

        //根据物流code等参数 获取运费
        function get_post_fee(code) {
            var fee = 0;
            var freightParam = {};
            freightParam.warehouseId = vm.products[0].warehouseId;
            freightParam.shippingCode = code;
            var orderDetails = [];
            $.each(vm.products,function(i,item){
                var pro = {};
                pro.sku = item.sku;
                pro.num = item.qty;
                pro.costPrice = item.finalSellingPrice;
                orderDetails.push(pro);
            });
            //自提标识
            //var ztflag = (code == "X2");
            freightParam.orderDetails = orderDetails;
            freightParam.countryId = 44;//写死为中国的id
            freightParam.provinceId = vm.provinceIdForFreight;
            var url = "/inventory/getFreight";
            $.ajax({
                url: url,
                type: "post",
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify(freightParam),
                async: false,//解决异步问题
                success: function (freightResStr) {
                    var freightRes = freightResStr;
                    if (freightRes.result) {
                        //运费
                        fee = parseFloat(freightRes.msg).toFixed(2);
                        //自提不需要加上操作费和其他费
                        //if(!ztflag){
                        //    if(freightRes.isOptFeeActived){//操作费加其他费
                        //        fee += freightRes.otherFee + freightRes.optFee;
                        //    }
                        //}
                        //fee = parseFloat(fee).toFixed(2);
                    } else {
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '运费获取失败！',
                            button: ["确认"]
                        });
                        fee = undefined;
                    }
                },
                error: function () {
                    var dia = $.dialog({
                        title: '温馨提示',
                        content: '物流方式获取失败！',
                        button: ["确认"]
                    });
                    fee = undefined;
                }
            });
            return fee;
        }


        //勾选是否支付运费
        $(".autoFreight").click(function(){
            if ($(".autoFreight").prop("checked")) {
                if (distributor && distributor != "") {
                    ajax_get("/member/getAccount", "email=" + distributor, "",
                        function (data) {
                            var banlance = parseFloat(data.balance);
                            var freight = parseFloat($(".freight .sale_freight").html());
                            if (banlance && banlance < freight) {
                                var dia = $.dialog({
                                    title: '温馨提示',
                                    content: '该分销商余额不充足无法扣除运费！',
                                    button: ["确认"]
                                });
                                $(".autoFreight").prop("checked",false);
                            }
                        },
                        function (xhr, status) {
                        }
                    );
                } else {
                    $(".autoFreight").prop("checked",false);
                    var dia = $.dialog({
                        title: '温馨提示',
                        content: '请先选择分销商！',
                        button: ["确认"]
                    });
                }
            }
        });

        //订单生成前的验证
        function validation() {
            //必须有发货商品
            if (vm.products.length == 0) {
                var dia = $.dialog({
                    title: '温馨提示',
                    content: '需要为客户订单添加发货商品！',
                    button: ["确认"]
                });
                return false;
            }
            //每个发货产品必须有真实售价
            if (!vm.receiver || vm.receiver.trim().length > 20) {
                var dia = $.dialog({
                    title: '温馨提示',
                    content: '请规范填写收货人姓名（最多20字）！',
                    button: ["确认"]
                });
                return false;
            }
            var code = $("#shippings").val().split("/")[0];
            if(!code){
                var fee = $("#thirdPostfee").val();
                var reg = /^[0-9]+(.[0-9]{1,2})?$/;
                var flag = fee?reg.test(fee)&&fee>0:false;
                if(!flag){
                    var dia = $.dialog({
                        title: '温馨提示',
                        content: "请输入有效的[<b style='color:#E74C3C'>第三方物流费用</b>]（至多两位小数,且必须大于零）",
                        button: ["确认"]
                    });
                    return false;
                }
            }
            //收货人手机验证
            if (!checkTel(vm.tel)) {
                var dia = $.dialog({
                    title: '温馨提示',
                    content: '请输入有效手机号码！',
                    button: ["确认"]
                });
                return false;
            }
            //收货地址验证
            //省市区验证
            if ($(".chooseAddr").text().trim() == "请选择"){
                var dia = $.dialog({
                    title: '温馨提示',
                    content: '请添加省市区！',
                    button: ["确认"]
                });
                return false;
            }
            if (!vm.addr.trim() || vm.addr.trim().length < 5 || vm.addr.trim().length > 120) {
                var dia = $.dialog({
                    title: '温馨提示',
                    content: '街道地址不规范，请重新填写！',
                    button: ["确认"]
                });
                return false;
            }
            if (vm.postcode && !checkPost(vm.postcode)) {
                var dia = $.dialog({
                    title: '温馨提示',
                    content: '请输入有效的邮政编码！',
                    button: ["确认"]
                });
                return false;
            }
            return true;
        }

        //返回键按钮的处理
        $(".ret-logo").click(function(){
            if ($(".curr").attr("id") == "receiverList") {
                window.location.href = "step_3.html";
            } else {
                $(".curr").removeClass("curr").prev().addClass("curr").show();
                $(".curr").siblings("section").hide();
            }

        });

        function initLode(){
            $(".curr").show();
            ajax_get('/sales/input/getInfo?email='+distributor,null,null,
                function(res) {
                    var result = JSON.parse(res);
                    if(result.suc){
                        bindData(result.data);
                    }
                }
            );
            $("#provinces").empty();
            var htmlCode = "";
            ajax_get("/member/getprovs", JSON.stringify(""), "application/json",
                function (data) {
                    $.each(data,function(i,item){
                        htmlCode += '<div class="impornor addSel province" pid="'+item.id+'">'+item.provinceName+'</div>';
                    });
                    $("#provinces").prepend(htmlCode);
                    $(".province").click(function(){
                        $(this).addClass("pselected");
                        $(this).siblings().removeClass("pselected");
                        getCity(this);
                    });
                },
                function (xhr, status) {
                }
            );
            ajax_get(
                "/inventory/getShippingMethod?wid=" +2024,
                "",
                "",
                function (shipResStr) {
                    var shipRes = $.parseJSON(shipResStr);
                    if (shipRes.length > 0) {
                        for (var i = 0; i < shipRes.length; i++) {
                            if (shipRes[i].default) {
                                $("#shippings").append("<option value='" + shipRes[i].methodCode+"/" + shipRes[i].methodName + "' code='" +  shipRes[i].id + "' selected='selected'>" + shipRes[i].methodName + "</option>")
                            } else {
                                $("#shippings").append("<option value='" + shipRes[i].methodCode+"/" + shipRes[i].methodName + "' code='" + shipRes[i].id + "'>" + shipRes[i].methodName + "</option>")
                            }
                        }
                        // $("#shippings").append("<option value='/第三方物流' code='BBC-TPL'>第三方物流</option>");
                        $("#thirdPostfee").hide();
                        $('.impornor').on('change', '#shippings', function(){
                            getFreight();
                        });
                    }
                    else {
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '物流方式获取失败！',
                            button: ["确认"]
                        });
                    }
                },
                function (XMLHttpRequest, textStatus) {
                    var dia = $.dialog({
                        title: '温馨提示',
                        content: '物流方式获取失败！',
                        button: ["确认"]
                    });
                }
            );
        }
        initLode();
        // 绑定数据
        function bindData(list){
            $.each(list,function(i,item){
                var murl = item.productImg||'';
                item.productImg = urlReplace(murl,item.sku,null,150,150,80);
            });
            vm.products = list;
            $('.p-goods-img').picLazyLoad({
                threshold : 200
            });
        }
    });
});
