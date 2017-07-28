var needSelectExpirationText = '当前选择的商品存在多个到期日期，请确认是否需要选择商品到期日期<br>' +
    '如果不需要选择，系统将按照现有业务逻辑选择商品<br>' +
    '如果需要选择，请点击选择到期日期，然后逐步选择相应商品的到期日期数量，' +
    '且您已经选择的数量将被重置。';

/**
 * 手动录入采购单
 */
function typeIn_prepared2CreateOrder(){
    // 数据回显
    $("#qtyTotal_span em").text($("#type_in_qtyTotal").text());
    $("#priceTotal_span em").text($("#type_in_priceTotal").text());
    $("#money2Paid_div").find("input").val($("#type_in_priceTotal").text())
    $("#order_sure_btn").off("click")
    $("#order_sure_btn").on("click",typeIn_createOrder);
}

// 录入的数据生成订单生成订单
function typeIn_createOrder(){
    var inputId = $("#inputId_hidden").val();
    var money2Paid = $("#money2Paid_div").find("input[type='text']").val()
    var params = {inputId : inputId}
    var genetateType = $("#po_generateType").val()// 生成方式
    var payType = $("#po_payType").val()// 支付方式
    if(payType=='cash'){// 现金支付
        var cashAmount = $(".cashPayment").find("input[type='text']").val()
        if(!cashAmount){
            layer.msg("请输入金额！",{icon:2,time:2000});
            $(".cashPayment").find("input[type='text']").focus()
            return
        }
        if (!isMoneyPattern(cashAmount)) {
            layer.msg("请输入正确的金额！",{icon:2,time:2000});
            $(".cashPayment").find("input[type='text']").focus()
            return;
        }
        if(parseFloat(cashAmount) != money2Paid){
            layer.msg("输入的金额需与待付款金额一致！",{icon:2,time:2000});
            return;
        }
        params.money = cashAmount
    }
    params.genetateType = genetateType
    params.payType = payType

    // 出库的验证
    if(genetateType==2){// 完税仓商品发货
        var receiver = $(".microCapsule").find("input[name='receiver']").val()
        if(!receiver){
            layer.msg("请输入收货人姓名！",{icon:2,time:2000});
            $(".microCapsule").find("input[name='receiver']").focus()
            return
        }
        var telephone = $(".microCapsule").find("input[name='telephone']").val()
        if(!telephone){
            layer.msg("请输入手机号码！",{icon:2,time:2000});
            $(".microCapsule").find("input[name='telephone']").focus()
            return
        }
        if(!checkTel(telephone)){
            layer.msg("输入手机号码格式错误", {icon: 2, time: 2000});
            $(".microCapsule").find("input[name='telephone']").focus()
            return
        }
        var province = $(".microCapsule").find("select[name='sale-province']").val()
        if(!province){
            layer.msg("请选择省！",{icon:2,time:2000});
            return
        }
        var provinceTxt = $(".microCapsule").find("select[name='sale-province'] option:selected").text()

        var city = $(".microCapsule").find("select[name='sale-city']").val()
        if(!city){
            layer.msg("请选择市！",{icon:2,time:2000});
            return
        }
        var cityTxt = $(".microCapsule").find("select[name='sale-city'] option:selected").text()

        var region = $(".microCapsule").find("select[name='sale-region']").val()
        if(!region){
            layer.msg("请选择区！",{icon:2,time:2000});
            return
        }
        var regionTxt = $(".microCapsule").find("select[name='sale-region'] option:selected").text()

        var addressDetail = $(".microCapsule").find("input[name='address-detail']").val()
        if(!addressDetail){
            layer.msg("请输入详细地址！",{icon:2,time:2000});
            $(".microCapsule").find("input[name='address-detail']").focus()
            return
        }
        if(addressDetail.length<5 || addressDetail.length>120){
            layer.msg("无需重复填写省市区，必须大于5个字符，小于120字！",{icon:2,time:2000});
            $(".microCapsule").find("input[name='address-detail']").focus()
            return
        }

        params.bbcPostage = $("#bbcPostage_span em").text()
        params.receiver=receiver
        params.telephone=telephone
        params.provinceId=province
        params.address=provinceTxt+" "+cityTxt+" "+regionTxt+" "+addressDetail
        params.postCode=$(".microCapsule").find("input[name='post-code']").val()
        params.shippingCode=$(".microCapsule").find("select[name='shippingMethod'] option:selected").attr("code") // 运送方式
        params.shippingName=$(".microCapsule").find("select[name='shippingMethod'] option:selected").text() // 运送方式
    }

    params.oaAuditNo = $.trim($("#oa_auditNo").val())// oa审批号
    var remarks = $("#buseness_remarks").val()// 业务备注
    if($.trim(remarks) && $.trim(remarks).length>500){
        layer.msg("业务备注不能超过500个字！",{icon:2,time:2000});
        $("#buseness_remarks").focus()
        return
    }
    params.remarks = remarks

    // 下单
    $("#order_sure_btn").attr("disabled","disabled")
    // 根据生成方式，调用不同的接口：1微仓进货，2完税仓商品发货
    var url = genetateType==2 ? "/purchase/deliverDutyPaidGoods" : "/purchase/inputOrder"
    ajax_post(url, JSON.stringify(params), "application/json", function(response) {
        // 用户未登录
        if(response.code == 101){
            layer.msg(response.msg, {icon: 1, time: 2000}, function(){
                window.location.href = "/backstage/login.html"
            });
            return
        }

        if(!response.suc){
            layer.msg(response.msg,{icon:2,time:2000});
            $("#order_sure_btn").removeAttr("disabled")
            return;
        }

        cleanTypeInDatas();

        var purchaseOrderNo = response.purchaseOrderNo
        if(payType == 'balance'){// 先生成采购单再现金支付
            layer.msg("下单成功，即将进行自动进行余额扣款！",{icon:1,time:2000},function(){
                payWithBalance(purchaseOrderNo)
            })
        } else if(payType == 'cash-online'){ // 线下转账
            layer.msg("下单成功，即将跳转到线下付款页面",{icon:1,time:2000},function(){
                $("#input_purchase_order_div").hide()
                $(".place_order").hide();
                $('#transfer_offline_div').show()
                transferOffline(purchaseOrderNo)
            });
        }else{
            layer.msg("下单成功！",{icon:1,time:2000}, function(){
                $("p[position="+$("#forword").val()+"]").click();
            });
        }
    });
}

var skuMap = {};

// 把数据清空
function cleanTypeInDatas(){
    $(".record_purchaseList_table").find("input[type='checkbox']").prop("checked",false);
    $(".record_purchaseList_table").find("tbody").empty();
    $("#type_in_distributor_input").val("");
    $("#inputId_hidden").val("");
    $("#distributionMode_hidden").val("");
    $("#type_in_qtyTotal").text(0);
    $("#type_in_priceTotal").text(0.00);
}

// 查询录入的信息
function show_inputOrder(inputId){
    // 正价商品表格的全选框不勾选
    $(".record_purchaseList_table").find("input[type='checkbox']").prop("checked",false);

    var url = "../purchase/ti/Inputorder/id";
    var param = {"inputId":inputId};
    // 如果是为null，说明是第一次进来的
    if(!inputId){
        cleanTypeInDatas();
        url = "../purchase/ti/Inputorder/user";
    }
    emtpySkuMap();
    ajax_post(url, JSON.stringify(param), "application/json",
        function(response) {
            // 用户未登录
            if(response.code == 101){
                layer.msg(response.msg, {icon: 1, time: 2000}, function(){
                    window.location.href = "/backstage/login.html"
                });
            }
            if(response.suc){
                var inputDto = response.inputDto;
                if(inputDto){
                    $("#type_in_distributor_input").val(inputDto.disAccount);
                    $("#inputId_hidden").val(inputDto.inputId);
                    $("#distributionMode_hidden").val(inputDto.disMode);
                    var target_tbody = $(".record_purchaseList_table").find("tbody");
                    target_tbody.empty();
                    var proList = inputDto.proList  // 正价商品
                    var giftList = inputDto.giftList // 赠品
                    //插入查询erp库存skuMap
                    insertSkuMap(proList,giftList);
                    // 正价商品组循环
                    var allHtml = ''
                    for(var i=0;i<proList.length;i++){
                        allHtml += createAProductTrHtml(proList[i],false)
                    }
                    // 赠品组循环
                    for(var i=0;i<giftList.length;i++){
                        allHtml += createAProductTrHtml(giftList[i],true)
                    }
                    target_tbody.html(allHtml);

                    recalculateQtyAndPriceTotal();
                }
            }
        }
    );
}

// {expirationDate:2012-02-02, stock=10}
function getExpirationDateAndStock(product){
    var defaultVal = {
        expirationDate:deal_with_illegal_value(undefined),
        stock:product.stock
    };
    // 判断是否需要选择到期日期
    if(!product.needExpirationDate || !product.expirationDate){
        return defaultVal
    }

    var expirationDateDtoSet = product.expirationDateDtoSet;
    var expirationDate = product.expirationDate;
    var found = false;
    for(var i in expirationDateDtoSet){
        if(expirationDate && expirationDate==expirationDateDtoSet[i].expirationDate){
            found = true;
            return {
                expirationDate:expirationDateDtoSet[i].expirationDate,
                stock:expirationDateDtoSet[i].subStock
            };
        }
    }

    if(!found){ // 找不到
        return defaultVal
    }
}

/**
 * 创建正价商品和赠品的表格行html
 * @param product  商品信息
 * @param isGift    是否是赠品，true赠品，false正价商品
 * @returns {string}
 */
function createAProductTrHtml(product,isGift){
    var qty = (!product.qty || parseInt(product.qty)<=0) ? 0 : product.qty
    // 赠品标志
    var giftMark = isGift ? '【<b style="color: red;">赠</b>】' : ""
    var qtyChangeOptHtml = ""
    var expirationAndStock = getExpirationDateAndStock(product)
    if (isGift){
        qtyChangeOptHtml='<td class="record_num"><span onclick="reduceGiftQty(this)">－</span>' +
            '<input style="text-align: center" oldQty="'+qty+'" onblur="inputGiftQty(this)" value="'+qty+'" ' +
            'giftId="'+product.id+'" stock="'+expirationAndStock.stock+'" price="'+product.price+'" type="text">' +
            '<span onclick="increaseGiftQty(this)">＋</span>' +
            '</td>'+
            '<td><span giftId="'+product.id+'" style="cursor: pointer;" onclick="deleteGift(this)">删除</span></td>'
    }else{
        qtyChangeOptHtml='<td class="record_num">' +
            '<span onclick="reduceProQty(this)">－</span>' +
            '<input style="text-align: center" batchNumber="'+product.batchNumber+'" oldQty="'+qty+'" ' +
            'onblur="inputProQty(this)" stock="'+expirationAndStock.stock+'" price="'+product.price+'" ' +
            'value="'+qty+'" inputProId="'+product.id+'" name="proQtyInput" type="text">' +
            '<span onclick="increaseProQty(this)">＋</span>' +
            '</td>'+
            '<td><span inputProId="'+product.id+'" style="cursor: pointer;" onclick="deletePro(this)">删除</span></td>'
    }
    return "<tr>"+
        '<td>' +
        '<img class="bagproduct-img" alt="'+product.title+'" src=\"'+urlReplace(product.imageUrl,product.sku,"",100,50,100)+'\"/>'+
        '<p style="text-align: left">'+product.title+giftMark+'</p>' +
        '</td>'+
        "<td>"+product.interBarCode+"</td>"+
        "<td>"+product.sku+"</td>"+
        "<td warehouseId='"+product.warehouseId+"'>"+product.warehouseName+"</td>"+
        "<td>"+expirationAndStock.expirationDate+"</td>"+
        "<td>"+expirationAndStock.stock+"</td>"+
        "<td>"+product.price+"</td>"+
        qtyChangeOptHtml+
        "</tr>"
}

function deleteGift(obj){
    layer.confirm('确认删除此赠品？', {
        btn: ['确认','取消'] //按钮
    }, function(){
        ajax_post("../purchase/ti/gift/rm", JSON.stringify({giftIds:$(obj).attr("giftId")}), "application/json",
            function(data) {
                // 用户未登录
                if(data.code == 101){
                    layer.msg(data.msg, {icon: 1, time: 2000}, function(){
                        window.location.href = "/backstage/login.html"
                    });
                    return
                }
                if(data.suc){
                    layer.msg("删除赠品成功！",{icon:1,time:2000});
                    show_inputOrder($("#inputId_hidden").val());
                }else{
                    layer.msg("删除赠品失败！",{icon:2,time:2000});
                }
            }
        );
    });
}

function deletePro(obj){
    layer.confirm('确认删除此正价商品？', {
        btn: ['确认','取消'] //按钮
    }, function(){
        ajax_post("../purchase/ti/pro/rm", JSON.stringify({proIds:$(obj).attr("inputProId")}), "application/json",
            function(data) {
                // 用户未登录
                if(data.code == 101){
                    layer.msg(data.msg, {icon: 1, time: 2000}, function(){
                        window.location.href = "/backstage/login.html"
                    });
                    return
                }
                if(data.suc){
                    layer.msg("删除正价商品成功！",{icon:1,time:2000});
                    show_inputOrder($("#inputId_hidden").val());
                }else{
                    layer.msg("删除正价商品失败！",{icon:2,time:2000});
                }
            }
        );
    });
}

// 减少正价商品数量
function reduceProQty(obj){
    var qtyObj = $(obj).parent().find("input[type='text']");
    var batchNumber = qtyObj.attr("batchNumber");
    var qty = qtyObj.val();
    if(parseInt(qty) == 1){
        return;
    }
    var newQty = parseInt(qty) - 1;
    if(newQty<parseInt(batchNumber)){
        layer.msg("正价商品数量不能小于起批量！",{icon:2,time:2000});
        qtyObj.val(batchNumber);
        recalculateQtyAndPriceTotal();
        return;
    }
    qtyObj.val(newQty);
    updateProQty(qtyObj.attr("inputProId"),newQty);
    recalculateQtyAndPriceTotal();
}

// 增加正价商品数量
function increaseProQty(obj){
    var qtyObj = $(obj).parent().find("input[type='text']");
    var stock = qtyObj.attr("stock");// 云仓库存
    var qty = qtyObj.val();
    var newQty = parseInt(qty) + 1;
    if(newQty>parseInt(stock)){
        layer.msg("正价商品数量不能大于云仓库存！",{icon:2,time:2000});
        qtyObj.val(stock);
        recalculateQtyAndPriceTotal();
        return;
    }
    qtyObj.val(newQty);
    updateProQty(qtyObj.attr("inputProId"),newQty);
    recalculateQtyAndPriceTotal();
}

// 输入正价商品数量
function inputProQty(obj){
    var qtyObj = $(obj).parent().find("input[type='text']");
    var batchNumber = qtyObj.attr("batchNumber");
    var stock = qtyObj.attr("stock");
    var qty = qtyObj.val();
    var oldQty = qtyObj.attr("oldQty");
    if(!qty){
        layer.msg("正价商品数量不能为空！",{icon:2,time:2000});
        qtyObj.val(oldQty);
        return;
    }
    var pattern = /^[1-9]\d*$/;
    if(!pattern.test(qty)){
        layer.msg("正价商品只能为正整数！",{icon:2,time:2000});
        qtyObj.val(oldQty);
        return;
    }

    var newQty = qtyObj.val();
    if(newQty<parseInt(batchNumber)){
        layer.msg("正价商品数量不能小于起批量！",{icon:2,time:2000});
        qtyObj.val(batchNumber);
        newQty = batchNumber
        recalculateQtyAndPriceTotal();
    }
    if(newQty>parseInt(stock)){
        layer.msg("正价商品数量不能大于云仓库存！",{icon:2,time:2000});
        qtyObj.val(stock);
        newQty = stock
    }
    if(newQty != oldQty){
        updateProQty(qtyObj.attr("inputProId"),newQty);
        qtyObj.attr("oldQty",newQty);
        recalculateQtyAndPriceTotal();
    }
}

// 减少赠品数量
function reduceGiftQty(obj){
    var qtyObj = $(obj).parent().find("input[type='text']");
    var qty = qtyObj.val();
    if(parseInt(qty) == 1){
        return;
    }
    var newQty = parseInt(qty) - 1;
    qtyObj.val(newQty);
    updateGiftQty(qtyObj.attr("giftId"),newQty);
    recalculateQtyAndPriceTotal();
}

// 增加赠品数量
function increaseGiftQty(obj){
    var qtyObj = $(obj).parent().find("input[type='text']");
    var stock = qtyObj.attr("stock");
    var qty = qtyObj.val();
    var newQty = parseInt(qty) + 1;
    if(newQty>parseInt(stock)){
        layer.msg("赠品数量不能大于云仓库存！",{icon:2,time:2000});
        qtyObj.val(stock);
        return;
    }
    qtyObj.val(newQty);
    updateGiftQty(qtyObj.attr("giftId"),newQty);
    recalculateQtyAndPriceTotal();
}

// 输入赠品数量
function inputGiftQty(obj){
    var qtyObj = $(obj).parent().find("input[type='text']");
    var stock = qtyObj.attr("stock");
    var qty = qtyObj.val();
    var oldQty = qtyObj.attr("oldQty");
    if(!qty){
        layer.msg("赠品数量不能为空！",{icon:2,time:2000});
        qtyObj.val(oldQty);
        return;
    }
    var pattern = /^[1-9]\d*$/;
    if(!pattern.test(qty)){
        layer.msg("赠品数量只能为正整数！",{icon:2,time:2000});
        qtyObj.val(oldQty);
        return;
    }
    var newQty = qtyObj.val();
    if(newQty>parseInt(stock)){
        layer.msg("赠品数量不能大于云仓库存！",{icon:2,time:2000});
        qtyObj.val(stock);
        newQty = stock
    }
    if(newQty != oldQty){
        updateGiftQty(qtyObj.attr("giftId"),newQty);
        qtyObj.attr("oldQty",newQty);
        recalculateQtyAndPriceTotal();
    }
}

// 更新正价商品数量
function updateProQty(proId,qty){
    ajax_post("../purchase/ti/pro/qty/update", JSON.stringify({proId:proId, qty:qty}), "application/json",
        function(data) {
            // 用户未登录
            if(data.code == 101){
                layer.msg(data.msg, {icon: 1, time: 2000}, function(){
                    window.location.href = "/backstage/login.html"
                });
                return
            }
        }
    );
}

// 更新赠品数量
function updateGiftQty(giftId,qty){
    ajax_post("../purchase/ti/gift/qty/update", JSON.stringify({giftId:giftId, qty:qty}), "application/json",
        function(data) {
            // 用户未登录
            if(data.code == 101){
                layer.msg(data.msg, {icon: 1, time: 2000}, function(){
                    window.location.href = "/backstage/login.html"
                });
                return
            }
        }
    );
}

// 重新计算价格
function recalculateQtyAndPriceTotal(){
    var priceTotal = 0.00;// 价格总计
    var qtyTotal = 0;// 商品数量总计

    // 计算总数量
    $(".record_purchaseList_table tbody").find("tr").find("input[type='text']").each(function(i,e){
        qtyTotal += parseInt($(e).val());
    });

    // 总计
    $(".record_purchaseList_table tbody").find("tr").find("input[type='text'][name='proQtyInput']").each(function(i,e){
        var qty = parseInt($(e).val());
        var price = parseFloat($(e).attr("price"));
        priceTotal += price * qty;
    });

    $("#type_in_qtyTotal").html(qtyTotal);
    $("#type_in_priceTotal").html(priceTotal.toFixed(2));
}

// 条件按钮搜索商品
$('body').on('click', '#search_disturb_btn', function () {
    gain_distribution_list_data(1);
});

// 分销商弹窗
$('body').on('click', '#type_in_choice_disturb', function () {
    layer.open({
        type: 1,
        skin: 'layui-layer-demo', //样式类名
        closeBtn: 0, //不显示关闭按钮
        shift: 2,
        shadeClose: true, //开启遮罩关闭
        content: $('.disturb_choice_pop'),
        area: ['600px', '550px'],
        btn: ['确定', '取消'],
        title:'选择分销商',
        yes:function(index){
            var distributor_radio_checked = $("#disturb_choice_pop_div").find("table tbody").find("input[type='radio']:checked");
            var distributor = distributor_radio_checked.val();
            var distributionMode = distributor_radio_checked.attr("distributionMode");
            var comsumerType = distributor_radio_checked.attr("comsumerType");
            if(distributor){
                // 保存
                var params = {disAccount : distributor, disMode: distributionMode, comsumerType:comsumerType};
                if ($("#inputId_hidden").val()) {
                    params['inputId'] = $("#inputId_hidden").val();
                }
                ajax_post("../purchase/ti/main/addUpdt", JSON.stringify(params), "application/json",
                    function (data) {
                        // 用户未登录
                        if (data.code == 101) {
                            layer.msg(data.msg, {icon: 1, time: 2000}, function () {
                                window.location.href = "/backstage/login.html"
                            });
                            return
                        }
                        $("#inputId_hidden").val(data.inputId);
                        //分销商 模式(1,电商 2，经销商 3 ,商超 4，进口专营)
                        $("#distributionMode_hidden").val(distributionMode);
                        $("#type_in_distributor_input").attr("title", distributor).val(distributor);
                        layer.close(index);
                    }
                );
            }
        }
    });
    gain_distribution_list_data(1);
});

// 获取分销商列表
function gain_distribution_list_data(currPage) {
    var email = "";
    isaulogin(function (email) {
        email = email;
    });
    var params = {
        role: 2,
        currPage: currPage == undefined || currPage == 0 ? 1 : currPage,
        pageSize: 10,
        search: $("#search_disturb_input").val(),
        email:email,
        notType: 3//分销商为内部分销商
    };
    ajax_post("../member/relatedMember", JSON.stringify(params), "application/json",
        function(response) {
            if (response.data) {
                insert_distribution_list_data(response.data.list);
                init_distribution_list_pagination(response.data.currPage,response.data.totalPage);
            } else{
                layer.msg("获取分销商数据失败！", {icon : 2, time : 1000});
            }
        }
    );
}

// 分销商列表
function insert_distribution_list_data(list) {
    var $tbody = $("#disturb_choice_pop_div").find("table tbody");
    var trHTML = '';
    $.each(list, function(i, item) {
        trHTML += '<tr>'+
            '<td><input type="radio" name="distributors" distributionMode="' + item.distributionMode + '" comsumerType="'+item.comsumerType+'" value="' + item.email + '"/></td>'+ // 单选的
            '<td>' + deal_with_illegal_value(item.email) + '</td>'+
            '<td>' + deal_with_illegal_value(item.nick) + '</td>'+
            '<td>' + deal_with_illegal_value(item.telphone) + '</td>'+
            '</tr>';
    });
    $tbody.html(trHTML);
}

// 分销商列表分页条
function init_distribution_list_pagination(currPage,totalPage) {
    if ($("#distributions_pagination")[0] != undefined) {
        $("#distributions_pagination").empty();
        laypage({
            cont: 'distributions_pagination',
            pages: totalPage,
            curr: currPage,
            groups: 5,
            skin: '#55ccc8',
            first: '首页',
            last: '尾页',
            prev: '上一页',
            next: '下一页',
            skip: true,
            jump: function(obj, first) {
                //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                if(!first){
                    gain_distribution_list_data(obj.curr);
                }
            }
        });
    }
}

// 清除数据
function cleanTempSelectedProducts(){
    $("#temp_selected_skus").val("");
    $("#temp_selected_warehouseIds").val("");
    $("#temp_selected_qties").val("");
}

/**
 * 添加符合条件的商品-条件
 * @param needExpirationDate  是否需要到期日期
 */
function addAllMatchedParams(needExpirationDate){
    var params = {inputId: $("#inputId_hidden").val(), needExpirationDate: needExpirationDate};
    // 商品分类
    var categoryId = $("#add_normalProduct_div").find('select[name="product_category_id"]').val();
    if (categoryId) {
        params['categoryId'] = categoryId;
    }
    // 仓库
    var warehouseId = $("#add_normalProduct_div").find('select[name="warehouse_id"]').val();
    if (warehouseId) {
        params['warehouseId'] = warehouseId;
    }
    var search_product_input_value = $.trim($("#search_product_input").val());
    if (search_product_input_value) {
        var skuPattern = /^[a-zA-Z]*[0-9-]*(\s*,?\s*[a-zA-Z]*[0-9-]*)?$/;
        var isSku = skuPattern.test(search_product_input_value);
        if (!isSku) {
            params['title'] = search_product_input_value;
        } else {
            var skuList = new Array()
            var skuArray = search_product_input_value.split(",");
            for (var i = 0; i < skuArray.length; i++) {
                skuList.push($.trim(skuArray[i]));
            }
            params['skuList'] = skuList;
        }
    }
    return params;
}

// 正价商品弹窗，和赠品弹窗是同一个div
$('body').on('click', '#addNormalProducts_btn', function () {
    if(!$("#type_in_distributor_input").val()){
        layer.msg("请先选择分销商！", {icon: 2, time: 2000});
        return;
    }
    cleanTempSelectedProducts();
    cleanSearchConditions();
    layer.open({
        type: 1,
        skin: 'layui-layer-demo', //样式类名
        title: "选择正价商品", //不显示标题
        shift: 2,
        shadeClose: false, //开启遮罩关闭
        closeBtn: 0,
        content: $('.add_normalProduct_pop'),
        area: ['870px', '550px'],
        btn: ['确认添加', '取消'],
        yes: function (index) {
            // 判断是否要选择到期日期
            layer.confirm(needSelectExpirationText, {
                btn: ['不需要选择到期日期', '选择到期日期'] //按钮
            }, function () {
                // 不需要选择到期日期
                doAddPros(false);
            }, function () {
                // 选择到期日期
                doAddPros(true);
            });
        },
        cancel: function () {
            cleanTempSelectedProducts();
        }
    });
    searchWarehouses();
    searchProductCategories();
    searchProducts(1);
});

// 添加正价商品
function doAddPros(needExpirationDate){
    var products_match_condition_checked = $("#add_normalProduct_div").find('input[name="products_match_condition"][type="checkbox"]').prop("checked");
    if (products_match_condition_checked) {
        // 符合条件的所有商品作为正价商品
        var params = addAllMatchedParams(needExpirationDate)
        // 保存正价商品信息
        var loading_index = layer.load(1, {shade: 0.5});
        ajax_post("../purchase/ti/pros/addAllMatched", JSON.stringify(params), "application/json",
            function (data) {
                // 用户未登录
                if (data.code == 101) {
                    layer.msg(data.msg, {icon: 1, time: 2000}, function () {
                        window.location.href = "/backstage/login.html"
                    });
                    return
                }
                if (data.suc) {
                    show_inputOrder(inputId);// 重新加载整个的录入信息
                }
                layer.close(loading_index)
            }
        );
    } else {
        // unitType：单位类型（1 为单个商品，2 为整箱商品）
        // {inputId:123, needExpirationDate:true/false,products:[{sku:xxx, warehouseId:xxx, unitType:1, qty:1}]}
        var inputId = $("#inputId_hidden").val();
        var params = {inputId: inputId, needExpirationDate: needExpirationDate};

        // 原有数据，用来实现翻页选择的
        var skusExists = $("#temp_selected_skus").val();
        var warehouseIdsExists = $("#temp_selected_warehouseIds").val();
        var qtiesExists = $("#temp_selected_qties").val();
        var skuArray = new Array(), warehouseIdArray = new Array(), qtyArray = new Array();
        if (skusExists) skuArray = skusExists.split(",");
        if (warehouseIdsExists) warehouseIdArray = warehouseIdsExists.split(",");
        if (qtiesExists) qtyArray = qtiesExists.split(",");

        var products = new Array();
        for (var i = 0; i < skuArray.length; i++) {
            products.push({
                sku: skuArray[i],
                warehouseId: warehouseIdArray[i],
                unitType: 1, // 单位类型（1 为单个商品，2 为整箱商品）
                qty: qtyArray[i] // 数量不能小于起批量
            });
        }
        if (products.length == 0) {
            layer.msg("请选择正价商品", {icon: 1, time: 2000})
            return
        }

        params['products'] = products;
        // 保存正价商品信息
        var loading_index = layer.load(1, {shade: 0.5});
        ajax_post("../purchase/ti/pros/add", JSON.stringify(params), "application/json",
            function (data) {
                // 用户未登录
                if (data.code == 101) {
                    layer.msg(data.msg, {icon: 1, time: 2000}, function () {
                        window.location.href = "/backstage/login.html"
                    });
                    return
                }
                if (data.suc) {
                    show_inputOrder(inputId);// 重新加载整个的录入信息
                }
                layer.close(loading_index)
            }
        );
    }
    cleanTempSelectedProducts();
    layer.closeAll();
}

// 赠品弹窗，和正价商品弹窗是同一个div
$('body').on('click', '#addDonations_btn', function () {
    if(!$("#type_in_distributor_input").val()){
        layer.msg("请先选择分销商！", {icon: 2, time: 2000});
        return;
    }
    cleanTempSelectedProducts();
    cleanSearchConditions();
    layer.open({
        type: 1,
        skin: 'layui-layer-demo', //样式类名
        title: "选择赠品", //不显示标题
        shift: 2,
        shadeClose: false, //开启遮罩关闭
        closeBtn: 0,
        content: $('.add_normalProduct_pop'),
        area: ['870px', '550px'],
        btn: ['确认添加', '取消'],
        yes:function(index){
            // 判断是否要选择到期日期
            layer.confirm(needSelectExpirationText, {
                btn: ['不需要选择到期日期', '选择到期日期'] //按钮
            }, function () {
                // 不需要选择到期日期
                doAddGifts(false);
            }, function () {
                // 选择到期日期
                doAddGifts(true);
            });
        },
        cancel:function(){
            cleanTempSelectedProducts();
        }
    });
    searchWarehouses();
    searchProductCategories();
    searchProducts(1);
});

function doAddGifts(needExpirationDate){
    var products_match_condition_checked = $("#add_normalProduct_div").find('input[name="products_match_condition"][type="checkbox"]').prop("checked");
    if(products_match_condition_checked){
        // 符合条件的所有商品作为赠品
        var params = addAllMatchedParams(needExpirationDate);
        // 保存赠品信息
        var loading_index = layer.load(1, {shade: 0.5});
        ajax_post("../purchase/ti/gifts/addAllMatched", JSON.stringify(params), "application/json",
            function (data) {
                // 用户未登录
                if(data.code == 101){
                    layer.msg(data.msg, {icon: 1, time: 2000}, function(){
                        window.location.href = "/backstage/login.html"
                    });
                    return
                }
                if (data.suc) {
                    // 重新加载整个的录入信息
                    show_inputOrder(inputId);
                }
                layer.close(loading_index)
            });
    }else {
        // unitType:1, 单位类型（1 为单个商品，2 为整箱商品）
        // {inputId:123, gifts:[{sku:IF001, warehouseId:2024, unitType:1}]}
        var inputId = $("#inputId_hidden").val()
        var params = {inputId: inputId, needExpirationDate: needExpirationDate};

        // 原有数据，用来实现翻页选择的
        var skusExists = $("#temp_selected_skus").val();
        var warehouseIdsExists = $("#temp_selected_warehouseIds").val();
        var skuArray = new Array(),warehouseIdArray = new Array()
        if(skusExists) skuArray = skusExists.split(",");
        if(warehouseIdsExists) warehouseIdArray = warehouseIdsExists.split(",");

        var gifts = new Array();
        for(var i=0;i<skuArray.length;i++){
            gifts.push({
                sku: skuArray[i],
                warehouseId: warehouseIdArray[i],
                unitType: 1, // 单位类型（1 为单个商品，2 为整箱商品）
                qty: 1 // 数量不能小于起批量
            });
        }
        if (gifts.length > 0) {
            params['gifts'] = gifts;
            // 保存正价商品信息
            var loading_index = layer.load(1, {shade: 0.5});
            ajax_post("../purchase/ti/gifts/add", JSON.stringify(params), "application/json",
                function (data) {
                    // 用户未登录
                    if(data.code == 101){
                        layer.msg(data.msg, {icon: 1, time: 2000}, function(){
                            window.location.href = "/backstage/login.html"
                        });
                        return
                    }
                    if (data.suc) {
                        // 重新加载整个的录入信息
                        show_inputOrder(inputId);
                    }
                    layer.close(loading_index)
                }
            );
        }
    }
    cleanTempSelectedProducts();
    layer.closeAll();
}

function searchWarehouses(){
    var selectObj = $("#add_normalProduct_div").find('select[name="warehouse_id"]');
    selectObj.empty();
    selectObj.append('<option value="" selected>所有仓库</option>');
    // 查询仓库
    ajax_get("../inventory/queryWarehouse", null, null,
        function(data) {
            var wList = data;
            for(var i=0;i<wList.length;i++){
                selectObj.append('<option value="'+wList[i].warehouseId+'">'+wList[i].warehouseName+'</option>');
            }
        }
    );
}

function searchProductCategories(){
    var selectObj = $("#add_normalProduct_div").find('select[name="product_category_id"]');
    selectObj.empty();
    selectObj.append('<option value="" selected>所有商品</option>');
    // 查询商品分类
    ajax_get("../product/api/realCateQuery?level=1", null, null,
        function(data) {
            var cList = data;
            for(var i=0;i<cList.length;i++){
                selectObj.append('<option value="'+cList[i].iid+'">'+cList[i].cname+'</option>');
            }
        }
    );
}

/**
 * 查询商品信息
 * @param successCallback 对查询结果进行处理
 */
function searchProducts(curr){
    if(!curr){
        curr = 1;
    }
    var dataParams = {'model':$("#distributionMode_hidden").val(), "istatus":1, "pageSize":10, "currPage":curr};
    // 商品分类
    var categoryId = $("#add_normalProduct_div").find('select[name="product_category_id"]').val();
    if(categoryId){
        dataParams['categoryId']=categoryId;
    }
    // 仓库
    var warehouseId = $("#add_normalProduct_div").find('select[name="warehouse_id"]').val();
    if(warehouseId){
        dataParams['warehouseId']=warehouseId;
    }

    var search_product_input_value = $.trim($("#search_product_input").val());
    if(search_product_input_value){
        // sku正则表达式
        var skuPattern = /^[a-zA-Z]*[0-9-]*((\s*)?,?[a-zA-Z]*[0-9-]*)*$/
        var isSku = skuPattern.test(search_product_input_value);
        if(!isSku){
            dataParams['title'] = search_product_input_value;
        }else{
            var skuList = new Array()
            var skuArray = search_product_input_value.split(",");
            for(var i=0;i<skuArray.length;i++){
                skuList.push($.trim(skuArray[i]));
            }
            dataParams['skuList'] = skuList;
        }
    }

    ajax_post("../product/api/getProducts", JSON.stringify({"data":dataParams}), "application/json",
        function(response) {
            var $tbody = $("#add_normalProduct_div").find("table").find("tbody");
            $tbody.empty();
            var productList = response.data.result;
            if(productList.length>0){
                // 原有数据，用来实现翻页选择的
                var skusExists = $("#temp_selected_skus").val();
                var warehouseIdsExists = $("#temp_selected_warehouseIds").val();
                var skuArray = new Array(),warehouseIdArray = new Array();
                if(skusExists) skuArray = skusExists.split(",");
                if(warehouseIdsExists) warehouseIdArray = warehouseIdsExists.split(",");

                for(var i= 0,length=productList.length;i<length;i++){
                    var csku = productList[i].csku;
                    var warehouseId = productList[i].warehouseId;
                    var checked = containsSku(csku,warehouseId,skuArray,warehouseIdArray)!=-1 ? 'checked' : '';
                    var outOfStock = !checked && parseInt(productList[i].stock)<1 ? "disabled" : "" // 没有库存
                    var checkboxTitle = outOfStock ? "库存不足" : ""
                    var title = productList[i].ctitle;
                    if(title && title.length>20){
                        title=title.substring(0,20)+"...";
                    }
                    // 是否有特价
                    var disPrice= productList[i].isSpecial ? productList[i].specialSale : productList[i].disPrice;
                    var html = '<tr>'+
                        '<td title="'+checkboxTitle+'">' +
                        '<input '+checked+' '+outOfStock+' type="checkbox" onchange="checkbox_selectOne(this)" ' +
                        'iid="'+productList[i].iid+'" csku="'+csku+'" title="'+checkboxTitle+'" ' +
                        'ctitle="'+productList[i].ctitle+'" warehouseName="'+productList[i].warehouseName+'" ' +
                        'warehouseId="'+warehouseId+'" interBarCode="'+productList[i].interBarCode+'"  '  +
                        'stock="'+productList[i].stock+'" batchNumber="'+productList[i].batchNumber+'"  ></td>' +
                        '<td>'+productList[i].csku+'</td>'+
                        '<td title="'+productList[i].ctitle+'">'+title+'</td>' +
                        '<td>'+productList[i].interBarCode+'</td>' +
                        '<td>'+productList[i].warehouseName+'</td>' +
                        '<td>'+productList[i].stock+'</td>'+ //云仓库存
                        '<td>' + disPrice + '</td>'+ // 分销价
                        '</tr>';
                    $tbody.append(html);
                }
                init_normalProduct_pagination(response.data.currPage,response.data.totalPage);
            }else{
                $("#normalProduct_pagination").empty();
                layer.msg("暂无符合条件的商品数据", {icon: 2, time: 2000});
            }
        }
    );
}

// 分销商列表分页条
function init_normalProduct_pagination(currPage,totalPage) {
    if ($("#normalProduct_pagination")[0] != undefined) {
        $("#normalProduct_pagination").empty();
        laypage({
            cont: 'normalProduct_pagination',
            pages: totalPage,
            curr: currPage,
            groups: 5,
            skin: '#55ccc8',
            first: '首页',
            last: '尾页',
            prev: '上一页',
            next: '下一页',
            skip: true,
            jump: function(obj, first) {
                //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                if(!first){
                    searchProducts(obj.curr);
                }
            }
        });
    }
}

// 清空查询条件
function cleanSearchConditions(){
    $("add_normalProduct_div").find("select[name='product_category_id']").find("option[value='']").attr("selected","selected");
    $("add_normalProduct_div").find("select[name='warehouse_id']").find("option[value='']").attr("selected","selected");
    $("#search_product_input").val("");
    $("#add_normalProduct_div").find("input[type='checkbox']").prop("checked",false);
}

// 查找不到，返回-1
function containsSku(sku,warehouseId,skuArray,warehouseIdArray){
    var foundIndex=-1;
    if(skuArray.length>0){
        // 检查此sku在原先选中的skus中的位置
        for(var i= 0,length=skuArray.length;i<length;i++){
            if(skuArray[i]==sku && warehouseIdArray[i]==warehouseId){
                foundIndex=i;
                break;
            }
        }
    }
    return foundIndex;
}

function checkbox_selectAll(obj){
    // 原有数据
    var skusExists = $("#temp_selected_skus").val();
    var warehouseIdsExists = $("#temp_selected_warehouseIds").val();
    var qtiesExists = $("#temp_selected_qties").val();
    var skuArray = new Array(),warehouseIdArray = new Array(),qtyArray = new Array();
    if(skusExists) skuArray = skusExists.split(",");
    if(warehouseIdsExists) warehouseIdArray = warehouseIdsExists.split(",");
    if(qtiesExists) qtyArray = qtiesExists.split(",");

    var checked = $(obj).prop("checked");
    $(obj).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']").prop("checked",checked);
    if(checked){// 选中
        $("#normalProduct_table").find("tbody").find("input[type='checkbox']:checked").each(function(i,e){
            var sku = $(e).attr("csku");
            var warehouseId = $(e).attr("warehouseId");
            var foundIndex = containsSku(sku,warehouseId,skuArray,warehouseIdArray);
            if(foundIndex==-1) {
                skuArray.push($(e).attr("csku"));
                warehouseIdArray.push($(e).attr("warehouseId"));
                qtyArray.push($(e).attr("batchNumber"));
            }
        });
    }else{// 不选中
        $("#normalProduct_table").find("tbody").find("input[type='checkbox']").each(function(i,e){
            var sku = $(e).attr("csku");
            var warehouseId = $(e).attr("warehouseId");
            // 检查此sku在原先选中的skus中的位置
            var foundIndex = containsSku(sku,warehouseId,skuArray,warehouseIdArray);
            // 进行移除操作
            if(foundIndex!=-1){
                skuArray.splice(foundIndex,1);
                warehouseIdArray.splice(foundIndex,1);
                qtyArray.splice(foundIndex,1);
            }
        });
    }
    // 回写
    $("#temp_selected_skus").val(skuArray.toString());
    $("#temp_selected_warehouseIds").val(warehouseIdArray.toString());
    $("#temp_selected_qties").val(qtyArray.toString());
}

function checkbox_selectOne(obj){
    // 原有数据
    var skusExists = $("#temp_selected_skus").val();
    var warehouseIdsExists = $("#temp_selected_warehouseIds").val();
    var qtiesExists = $("#temp_selected_qties").val();
    var skuArray = new Array(),warehouseIdArray = new Array(),qtyArray = new Array();
    if(skusExists) skuArray = skusExists.split(",");
    if(warehouseIdsExists) warehouseIdArray = warehouseIdsExists.split(",");
    if(qtiesExists) qtyArray = qtiesExists.split(",");

    var checked = $(obj).prop("checked");
    if(!checked){// 不选中
        var sku = $(obj).attr("csku");
        var warehouseId = $(obj).attr("warehouseId");
        // 检查此sku在原先选中的skus中的位置
        var foundIndex = containsSku(sku,warehouseId,skuArray,warehouseIdArray);
        // 进行移除操作
        if(foundIndex!=-1){
            skuArray.splice(foundIndex,1);
            warehouseIdArray.splice(foundIndex,1);
            qtyArray.splice(foundIndex,1);
        }
        $(obj).parent().parent().parent().parent().find("thead").find("input[type='checkbox']").prop("checked",false);
    }else{// 选中
        skuArray.push($(obj).attr("csku"));
        warehouseIdArray.push($(obj).attr("warehouseId"));
        qtyArray.push($(obj).attr("batchNumber"));

        if($(obj).parent().parent().parent().find("input[type='checkbox']:checked").size() == $(obj).parent().parent().parent().find("input[type='checkbox']").size()){
            $(obj).parent().parent().parent().parent().find("thead").find("input[type='checkbox']").prop("checked",true);
        }else{
            $(obj).parent().parent().parent().parent().find("thead").find("input[type='checkbox']").prop("checked",false);
        }
    }
    // 回写
    $("#temp_selected_skus").val(skuArray.toString());
    $("#temp_selected_warehouseIds").val(warehouseIdArray.toString());
    $("#temp_selected_qties").val(qtyArray.toString());
}

function removeAllProductsTypedIn(){
    var inputId = $("#inputId_hidden").val();
    if(!inputId){
        layer.msg("暂无符合采购单录入信息", {icon: 2, time: 2000}, function () {
            show_inputOrder()
        });
        return;
    }

    var noProducts = $(".record_purchaseList_table").find("tbody").find("tr").size()<1;
    if(noProducts){
        layer.msg("暂无符合正价商品和赠品", {icon: 2, time: 2000});
        return;
    }

    layer.confirm("确认删除所有正价商品和赠品？", {
        btn: ['取消', '确认删除'] //按钮
    }, function (index) {
        layer.close(index)
    }, function () {
        var loading_index = layer.load(1, {shade: 0.5});
        // 清除
        ajax_post('/purchase/ti/rmall', JSON.stringify({inputId:inputId}), "application/json", function(data) {
            if(data.suc){
                show_inputOrder(inputId)
            }
            layer.close(loading_index)
        });
    });
}