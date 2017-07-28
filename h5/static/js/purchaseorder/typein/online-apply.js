require(['../../lib/common'], function (common) {
    require(['vue','laydate','jqform'], function (Vue){
        var orderNo = getUrlParam("ii");
        var vm = new Vue({
            el:'#pay_card',
            data:{
                recList:[],
                recAccount:'',
                recPayee:''
            }, 
            methods:{
                cancel:function(e){
                    window.location.href = "/purchaseorder/polist.html";
                },
                sumbit:function(e){
                    var options = {
                        //url: url,                 //默认是form的action
                        //type: type,               //默认是form的method（get or post）
                        dataType: 'json',           //html(默认), xml, script, json...接受服务端返回的类型
                        clearForm: false,          //成功提交后，清除所有表单元素的值
                        resetForm: false,          //成功提交后，重置所有表单元素的值
                        //target: '#output',          //把服务器返回的内容放入id为output的元素中
                        //timeout: 3000,               //限制请求的时间，当请求大于3秒后，跳出请求
                        //提交前的回调函数
                        beforeSubmit: function(formData, jqForm, options){
                            //formData: 数组对象，提交表单时，Form插件会以Ajax方式自动提交这些数据，格式如：[{name:user,value:val },{name:pwd,value:pwd}]
                            //jqForm:   jQuery对象，封装了表单的元素
                            //options:  options对象
                            //比如可以再表单提交前进行表单验证
                            var text1 = $("#audit_form select[name='recipientId']").val();
                            if (text1 == "") {
                                var dia = $.dialog({
                                    title: '温馨提示',
                                    content: '请选择收款渠道！',
                                    button: ["确认"]
                                });
                                return false;
                            }
                            var text3 = $("#audit_form input[name='receiptAccount']").val().trim();
                            if (text3 == "") {
                                var dia = $.dialog({
                                    title: '温馨提示',
                                    content: '收款账户不能为空',
                                    button: ["确认"]
                                });
                                return false;
                            }
                            var text4 = $("#receipt_payee").text().trim();
                            if (text4 == "") {
                                var dia = $.dialog({
                                    title: '温馨提示',
                                    content: '收款人不能为空',
                                    button: ["确认"]
                                });
                                return false;
                            }
                            var payCard = $("#audit_form input[name='transferCard']").val().trim();
                            if (payCard == "") {
                                var dia = $.dialog({
                                    title: '温馨提示',
                                    content: '付款账户不能为空',
                                    button: ["确认"]
                                });
                                return false;
                            }
                            var text5 = $("#audit_form input[name='distributorName']").val().trim();
                            if (text5 == "") {
                                var dia = $.dialog({
                                    title: '温馨提示',
                                    content: '账户开户名不能为空',
                                    button: ["确认"]
                                });
                                return false;
                            }
                            var text6 = $("#audit_form input[name='transferAmount']").val().trim();
                            if (!/^((\d{1})|([1-9]{1}\d+)|(\d{1}\.\d{1,2})|([1-9]{1}\d+\.\d{1,2}))$/.test(text6)) {
                                var dia = $.dialog({
                                    title: '温馨提示',
                                    content: '请输入正确格式的付款金额',
                                    button: ["确认"]
                                });
                                return false;
                            }
                            var text7 = $("#audit_form input[name='transferNumber']").val().trim();
                            if(!text7){
                                 var dia = $.dialog({
                                    title: '温馨提示',
                                    content: '付款流水号不能为空',
                                    button: ["确认"]
                                });
                                return false;
                            }
                            var text9 = $("#audit_form input[name='transferAmount']").data("fee");
                            console.info("fee----->" + text9);
                            if (text9 != "" && parseFloat(text6) < parseFloat(text9)) {
                                var dia = $.dialog({
                                    title: '温馨提示',
                                    content: '付款金额不能小于订单金额',
                                    button: ["确认"]
                                });
                                return false;
                            }
                            var text8 = $("#audit_form input[name='transferTime']").val().trim();
                            if (text8 &&!/^([1-2]{1}\d{3})\-(([0]{1}[1-9]{1})|([1]{1}[0-2]{1}))\-(([0]{1}[1-9]{1})|([1-2]{1}\d{1})|([3]{1}[0-1]{1}))\s(([0-1]{1}\d{1})|([2]{1}[0-3]))\:([0-5]{1}\d{1})\:([0-5]{1}\d{1})$/.test(text8)) {
                                var dia = $.dialog({
                                    title: '温馨提示',
                                    content: '请输入正确格式的实际付款日期',
                                    button: ["确认"]
                                });
                                return false;
                            }
                            var $file = $("#audit_form input[name='image']");
                            if ($file != undefined && $file.val() != "") {
                                var name = $file.val();
                                if ($file[0].files[0].size > (2 * 1024 * 1024) ||
                                    !(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
                                    var dia = $.dialog({
                                        title: '温馨提示',
                                        content: '付款截图只支持jpg、bmp、png三种格式，且大小不能大于2MB',
                                        button: ["确认"]
                                    });
                                    return false;
                                }
                            }
                        },
                        //提交成功后的回调函数
                        success: function(data,status,xhr,$form){
                            if (data.suc) {
                                $("#audit_form").resetForm();
                                $("#receipt_payee").text("");
                                $("#audit_form input[name='image']").parent().next().text("");
                                var dia = $.dialog({
                                    title: '温馨提示',
                                    content: '提交成功',
                                    button: ["确认"]
                                });
                                dia.on("dialog:action",function(e){
                                     window.location.href = "/purchaseorder/polist.html";
                                 });
                            } else {
                                var dia = $.dialog({
                                    title: '温馨提示',
                                    content: data.msg,
                                    button: ["确认"]
                                });
                            }
                        },
                        error: function(xhr, status, error, $form){},
                        complete: function(xhr, status, $form){}
                    };
                    $("#audit_form").ajaxSubmit(options);
                }
            }
        });
        ajax_get(
            "/member/getremos?" + Math.random(),
            "",
            "application/json",
            function (response) {
                if(response.suc){
                    vm.recList = response.list;
                }
            },function(e){
                console.log(e);
            }
        );
        $(vm.$el).on("change","#rec_sel",function(val){
            var target = $(event.target);
            if(target.val()){
                var msg = target.find("option:checked").attr("tag");
                var array = msg.split(",");
                vm.recPayee =  array[1];
                vm.recAccount = array[0];
            }else{
                vm.recPayee =  '';
                vm.recAccount = '';
            }
        });
        $("#audit_form input[name='purno']").val(orderNo);
        if (orderNo) {
            var url = "/purchase/viewpurchase";
            var param = {
                pageSize: "10",
                pageCount: "0",
                seachFlag: orderNo
            };
            $.ajax({
                url: url,
                type: "post",
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify(param),
                success: function (data) {
                    if (data.returnMess.errorCode == "0") {
                        var order = data.orders[0];
                        var totalAmount = 0;
                        var bbcPostage = order.bbcPostage;
                        var couponsAcount = order.couponsAmount;
                        totalAmount = parseFloat(order.purchaseDiscountAmount + (bbcPostage ? bbcPostage : 0)).toFixed(2);
                        if (couponsAcount) {
                            if (totalAmount > couponsAcount) {
                                totalAmount -= couponsAcount;
                            } else {
                                totalAmount = 0;
                            }
                        }
                        totalAmount = parseFloat(totalAmount).toFixed(2);
                        $("#audit_form input[name='email']").val(order.email);
                        $("#audit_form input[name='transferAmount']").data("fee", totalAmount);
                    }
                }
            });
        }
    });
});

