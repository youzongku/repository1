require(['../personal/personal'], function (common) {
    require(['layer', 'apply', 'jqform'], function (layer, Apply) {
        initLayer(layer);
        gain_receipt_mode();
        init_payers();
        //收款方式下拉选
        $("select[name='recipientId']").change(function (event) {
            if (this.value != "") {
                var opt = undefined, mode = this.value;
                $.each($(this).children(), function (i, item) {
                    if (mode == item.value) opt = item;
                });
                $("#audit_form input[name='receiptAccount']").val($(opt).data("mode").split("——")[0]);
                $("#receipt_payee input[name='receiptName']").val($(opt).data("mode").split("——")[1]);
            } else {
                $("#audit_form input[name='receiptAccount']").val("");
                $("#receipt_payee input[name='receiptName']").val("");
            }
        });

        //展示采购单信息
        var purno = GetQueryString("purno");
        $("#pno").val(purno);
        if (purno != null || purno != undefined) {
            var url = "/purchase/viewpurchase";
            var param = {
                pageSize: "10",
                pageCount: "0",
                seachFlag: purno
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
                        $(".purchaseNo").text(order.purchaseOrderNo);
                        $(".paid-order-list").data("orderId", order.id);

                        totalAmount = parseFloat(order.purchaseDiscountAmount + (bbcPostage ? bbcPostage : 0)).toFixed(2);
                        if (couponsAcount) {
                            if (totalAmount > couponsAcount) {
                                totalAmount -= couponsAcount;
                            } else {
                                totalAmount = 0;
                            }
                        }
                        totalAmount = parseFloat(totalAmount).toFixed(2);
                        $(".productAmount").text(order.purchaseDiscountAmount);
                        $(".totalAmount").text(totalAmount);
                        $(".totalAmount").data("type", (bbcPostage ? "6" : ""));

                        $(".couponsAcount").text(deal_with_illegal_value(couponsAcount));
                        $(".bbcPostage").text(deal_with_illegal_value(bbcPostage));

                        $("#audit_form input[name='transferAmount']").val(totalAmount);
                        $("#audit_form input[name='transferAmount']").data("fee", totalAmount);
                    }
                }
            });
        }

        $("#go_purchase_list").click(function(){
            window.location.href = "/personal/personal.html?emnunum=15";
        });

        //选中上传文件确定后展示文件名称
        $("#audit_form input[name='image']").change(function(e) {
            var file = $(this)[0].files[0];
            if(file){
                $(this).parent().next().text($(this).val());
                console.log(file.name);
                console.log(file.type);
            }else{
                $(this).parent().next().text("");
            }
        });

        $("#purchase_audit").click(function(){
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
                        layer.msg("请选择收款渠道", {icon: 0, time: 2000});
                        return false;
                    }
                    var text3 = $("#audit_form input[name='receiptAccount']").val().trim();
                    if (text3 == "") {
                        layer.msg("收款账户不能为空", {icon: 0, time: 2000});
                        return false;
                    }
                    var text4 = $("#receipt_payee input[name='receiptName']").val().trim();
                    if (text4 == "") {
                        layer.msg("收款人不能为空", {icon: 0, time: 2000});
                        return false;
                    }
                    var text2 = $("#audit_form select[name='transferType']").val();
                    if (text2 == "") {
                        layer.msg("请选择付款账户", {icon: 0, time: 2000});
                        return false;
                    }
                    var text5 = $("#audit_form input[name='distributorName']").val().trim();
                    if (text5 == "") {
                        layer.msg("账户开户名不能为空", {icon: 0, time: 2000});
                        return false;
                    }
                    var text6 = $("#audit_form input[name='transferAmount']").val().trim();
                    if (!/^((\d{1})|([1-9]{1}\d+)|(\d{1}\.\d{1,2})|([1-9]{1}\d+\.\d{1,2}))$/.test(text6)) {
                        layer.msg("请输入正确格式的付款金额", {icon: 0, time: 2000});
                        return false;
                    }
                    var text7 = $("#audit_form input[name='transferNumber']").val().trim();
                    if (text7 == ""){
                        layer.msg("付款流水号不能为空", {icon: 0, time: 2000});
                        return false;
                    }
                    var text9 = $("#audit_form input[name='transferAmount']").data("fee");
                    console.info("fee----->" + text9);
                    if (text9 != "" && parseFloat(text6) < parseFloat(text9)) {
                        layer.msg("付款金额不能小于订单金额", {icon: 0, time: 2000});
                        return false;
                    }
                    var $file = $("#audit_form input[name='image']");
                    if ($file != undefined && $file.val() != "") {
                        var name = $file.val();
                        if ($file[0].files[0].size > (2 * 1024 * 1024) ||
                            !(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
                            layer.msg("付款截图只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
                            return false;
                        }
                    }
                },
                //提交成功后的回调函数
                success: function(data,status,xhr,$form){
                    if (data.suc) {
                        $("#audit_form").resetForm();
                        $("#receipt_payee input[name='receiptName']").val("");
                        $("#audit_form input[name='image']").parent().next().text("");
                        layer.msg("提交成功", {icon: 1, time: 2000}, function() {
                            window.location.href = window.location.protocol + '//' + window.location.host + "/personal/personal.html";
                        });
                    } else {
                        layer.msg(data.msg, {icon: 2, time: 3000});
                    }
                },
                error: function(xhr, status, error, $form){},
                complete: function(xhr, status, $form){}
            };
            $("#audit_form").ajaxSubmit(options);

        });
    });
    function readImg(obj){
        preview(obj,$("#preview"));
    }
});







