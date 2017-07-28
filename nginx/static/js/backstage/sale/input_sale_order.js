/**
 * 只发深圳仓的商品-微仓/云仓
 * Created by Administrator on 2017/1/11.
 */
var layer = undefined;
var laypage = undefined;
var provinceIdForFreight = 0;//完税产品计算运费用的省id
var tempProCollection = {};// 临时选中的
var finalProCollection = {};
var prov_map,city_map,region_map;
var needSelectExpirationText = '当前选择的商品存在多个到期日期，请确认是否需要选择商品到期日期<br>' +
    '如果不需要选择，系统将按照现有业务逻辑选择商品<br>' +
    '如果需要选择，请点击选择到期日期，然后逐步选择相应商品的到期日期数量，' +
    '且您已经选择的数量将被重置。';

//初始化中单订单录入
function init_input_sales(layerParam, laypageParam,categoryTitle){
    $('#input_sales_order_title').html(categoryTitle);
    layer = layerParam;
    laypage = laypageParam;
    tempProCollection = {}

    initAreaSel();
    inputso_getShippingMethod()
}

// function addsales(){
//     initAreaSel();
//     //加载物流方式
//     ajax_get("/inventory/getShippingMethod?wid=" +2024, "", "",
//         function (shipResStr) {
//             if(shipResStr.code){
//                 window.location.href = "/backstage/login.html";
//             }
//             var shipRes = $.parseJSON(shipResStr);
//             if (shipRes.length > 0) {
//                 var options = ''
//                 for (var i = 0; i < shipRes.length; i++) {
//                     if (shipRes[i].default) {
//                         options += "<option value='" + shipRes[i].id + "' code='" + shipRes[i].methodCode + "' selected='selected'>" + shipRes[i].methodName + "</option>"
//                     } else {
//                         options += "<option value='" + shipRes[i].id + "' code='" + shipRes[i].methodCode + "'>" + shipRes[i].methodName + "</option>"
//                     }
//                 }
//                 $(".record_sendList_line #shippings").append(options)
//                 $("#thirdPostfee").hide();
//                 // $(".record_sendList_line #shippings").append("<option value='0' code='BBC-TPL' >第三方物流</option>");
//                 $('.record_sendList_line').on('change', '#shippings', function(){
//                     getFreight();
//                 });
//             } else {
//                 layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
//             }
//         },
//         function (XMLHttpRequest, textStatus) {
//             layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
//         }
//     );
// }

// 加载物流方式
function inputso_getShippingMethod(){
    $(".record_sendList_line #shippings").empty()
    //加载物流方式
    ajax_get("/inventory/getShippingMethod?wid=" +2024, "", "",
        function (shipResStr) {
            if(shipResStr.code){
                window.location.href = "/backstage/login.html";
            }
            var shipRes = $.parseJSON(shipResStr);
            if (shipRes.length > 0) {
                var options = ''
                var selected = ""
                for (var i = 0; i < shipRes.length; i++) {
                    selected = !selected && shipRes[i].default ? "selected='selected'" : ""
                    options += "<option "+selected+" value='"+shipRes[i].id+"' code='"+shipRes[i].methodCode+"'>"+shipRes[i].methodName+"</option>"
                }
                $(".record_sendList_line #shippings").html(options)
                $("#thirdPostfee").hide();
                $('.record_sendList_line').on('change', '#shippings', function(){
                    getFreight();
                });
            } else {
                layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
            }
        },function (XMLHttpRequest, textStatus) {
            layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
        }
    );
}

$("body").on("blur","#thirdPostfee",function(){
    var node = $(this);
    var fee = node.val();
    var reg = /^[0-9]+(.[0-9]{1,2})?$/;
    var flag = fee?reg.test(fee)&&fee>0:false;
    if(!flag){
        layer.msg("请输入有效的[<b style='color:#E74C3C'>第三方物流费用</b>]（至多两位小数,且必须大于零）", {icon: 2, time: 2500});
        node.val("");
    }
    sumOrderTotal();
});

//选择分销商
$('body').on('click', '#importChoice_disturb', function () {
    $("#iso_distract_choice_pop_div .searchInput").val("");
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['580px', '580px'],
        content: $('#iso_distract_choice_pop_div'),
        btn:['确定','取消'],
        title: '关联分销商',
        success: function(){
            tempProCollection = {};
            finalProCollection = {};
        },
        yes: function(i, currdom) {
            $(".record_sendList_table tbody").empty();
            var node = $(".operation_table").find("input[type='radio']:checked");
            var email = node.attr("email")
            if (email && email != "") {
                comfortable_selected(email);
            }
            $("#inso_email").val(email)
            $("#inso_email").attr("disMode",node.attr("disMode"))
            $("#inso_email").attr("distributorType",node.attr("distributorType"));
            layer.close(i);
        }
    });
    get_distributes_list(1);
})

//智能条件选择
function comfortable_selected(email){
    $(".select-warehouse-product").empty("label");
    ajax_get("/inventory/manager/warehousing/queryMicroWarehouse?email="+email, undefined, "",
        function (data) {
            var wareHtml = ' 我的微仓:&nbsp;&nbsp;';
            $.each(data,function(i,item){
                if (item.warehouseId == 2024) {// TODO 只发深圳仓的
                    wareHtml += '<label><input email="'+email+'" type="radio" onclick = "selectOneMicroWarehouse(this)" data-val = 0 value = "'+item.warehouseId+'">全选'+item.warehouseName+'商品</label>';
                }
            });
            $(".select-warehouse-product").append(wareHtml);
            $(".select-warehouse-product").show();
        }
    );
}

//选择我的微仓里的某一个仓库，例如深圳
function selectOneMicroWarehouse(obj){
    if ($(obj).prop("checked")) {
        var loading_index = layer.load(1, {shade: 0.5});
        ajax_get("/member/infor?email=" + $(obj).attr("email"), "", "application/json",
            function (response) {
                var distributionMode = response.distributionMode
                var param = {email: $(obj).attr("email"), warehouseId: $(obj).val(), model: distributionMode};
                ajax_post_sync("/product/mirc-inventory",JSON.stringify({data:param}),"application/json",function (data) {
                    tempProCollection = {};
                    $.each(data.data.result, function (i, item) {
                        item.disPrice = item.disPrice ? item.disPrice : item.localPrice;
                        if (distributionMode == 1) {
                            item.disPrice = (item.electricityPrices ? item.electricityPrices : item.disPrice);
                        } else if (distributionMode == 2) {
                            item.disPrice = (item.distributorPrice ? item.distributorPrice : item.disPrice);
                        } else if (distributionMode == 3) {
                            item.disPrice = (item.supermarketPrice ? item.supermarketPrice : item.disPrice);
                        } else {
                            item.disPrice = (item.ftzPrice ? item.ftzPrice : item.disPrice);
                        }
                        if (item.isSpecial) {
                            item.disPrice = item.specialSale;
                        }
                        item.disPrice = parseFloat(item.disPrice).toFixed(2);

                        tempProCollection[item.csku] = {
                            sku: item.csku,
                            batchNumber: item.batchNumber,
                            title: item.ctitle,
                            interBarCode: item.interBarCode,
                            warehouseName: item.warehouseName,
                            warehouseId: item.warehouseId,
                            stock: item.stock,
                            subStock: item.stock,
                            microStock: item.microStock,
                            subMicroStock: item.microStock,
                            price: item.disPrice,
                            qty: item.batchNumber,
                            marketPrice: item.localPrice,
                            imgUrl: item.imageUrl
                        }
                    });

                    // 判断是否要选择到期日期
                    layer.confirm(needSelectExpirationText, {
                        btn: ['不需要选择到期日期', '选择到期日期'] //按钮
                    }, function () {
                        // 不需要选择到期日期
                        inputso_showChoiceProducts(false);
                        layer.close(loading_index)
                        getFreight();
                    }, function () {
                        // 选择到期日期
                        inputso_showChoiceProducts(true);
                        layer.close(loading_index)
                        getFreight();
                    });
                })
            }
        );
    }
}

//得到分销商列表
function get_distributes_list(curr){
    var params = {
        currPage: (curr == undefined || curr == 0) ? 1 : curr, pageSize: 10,
        search: $("#iso_distract_choice_pop_div .searchInput").val().trim(), notType: 3//分销商不为内部分销商
    };

    ajax_post_sync("/member/relatedMember",JSON.stringify(params),"application/json",function(data) {
        if (data) {
            if (data.mark == 2 || data.mark == 3) {
                inputso_insert_users_list(data.data.list);
                inputso_init_users_pagination(data.data);
            } else if (!data.suc) {
                window.location.href = "login.html";
            } else if (data.mark == 1){
                layer.msg("获取分销商失败", {icon : 2, time : 1000});
            }
        }
    })
}

//插入分销商列表
function inputso_insert_users_list(list){
    $('#iso_distract_choice_pop_div').find("table").find("tbody").empty();
    var itemHTML = '';
    $.each(list, function(i, item) {
        itemHTML +=
            '<tr>'+
            '<td style="width: 10%">' +
            '<input style="cursor: pointer"  '+(item.isFrozen?"disabled":"")+' type="radio" name="email" email="'+item.email+'" disMode="'+item.distributionMode+'" distributorType="'+item.comsumerType+'">' +
            '</td>'+
            '<td style="width: 35%">' + deal_with_illegal_value(item.email) +(item.isFrozen?'【<b style="color: red;">冻结</b>】':"") + '</td>'+
            '<td style="width: 20%">' + deal_with_illegal_value(item.nick) + '</td>'+
            '<td style="width: 35%">' + deal_with_illegal_value(item.telphone) + '</td>'+
            '</tr>'
    });
    $('#iso_distract_choice_pop_div').find("table").find("tbody").html(itemHTML);
}

//分销商列表分页展示
function inputso_init_users_pagination(page){
    if ($("#sales_distribution_pagination")[0] != undefined) {
        $("#sales_distribution_pagination").empty();
        laypage({
            cont: 'sales_distribution_pagination',
            pages: page.totalPage,
            curr: page.currPage,
            groups: 5,
            skin: '#55ccc8',
            first: '首页',
            last: '尾页',
            prev: '上一页',
            next: '下一页',
            skip: true,
            jump: function(obj, first) {
                //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                $("#sales_distribution_pagination .laypage_total").find("input[type='number']").css("width","40px");
                if(!first){
                    get_distributes_list(obj.curr);
                }
            }
        });
    }
}

// 选择商品按钮
function inputso_selectProducts() {
    var email = $.trim($("#inso_email").val());
    if (email == undefined ||email == "") {
        layer.msg("请先选择分销商",{icon:2,time:2000});
        return;
    }

    $(".add_sendProduct_pop .searchInput").val("");
    layer.open({
        type: 1,
        skin: 'layui-layer-demo', //样式类名
        title: false, //不显示标题
        shift: 2,
        shadeClose: true, //开启遮罩关闭
        content: $('.add_sendProduct_pop'),
        area: ['850px', '500px'],
        btn: ["确认添加"],
        yes: function(index) {
            //确定添加按钮
            var warehouseId = $("#warehouse_select").val();
            $(".add_sendProduct_pop").find("input[name='product-check']:checked").each(function(i, e) {
                var $trNode = $(e).parents("tr");
                // 采购数量最起码是要起批量开始
                tempProCollection[$trNode.find("td[name='sku']").text()] = {
                    sku: $trNode.find("td[name='sku']").text(),
                    batchNumber: $(e).attr("batchNumber"),
                    title: $trNode.find("td[name='ctitle']").text(),
                    interBarCode: $trNode.find("td[name='interBarCode']").text(),
                    warehouseName: $trNode.find("td[name='warehouseName']").text(),
                    warehouseId: $trNode.find("td[name='warehouseName']").attr("warehouseId"),
                    stock: $trNode.find("td[name='stock']").text(),// 云仓库存
                    subStock: $trNode.find("td[name='stock']").text(),// 到期日期对应的云仓库存
                    microStock: $trNode.find("td[name='microStock']").text(),// 微仓库存
                    subMicroStock: $trNode.find("td[name='microStock']").text(),// 到期日期对应的微仓库存
                    price: $trNode.find("td[name='disPrice']").text(),
                    qty: $(e).attr("batchNumber"),
                    marketPrice: $trNode.find("td[name='marketPrice']").text(),
                    imgUrl: $trNode.find("td[name='ctitle']").attr("imgUrl")
                }
            });
            // 判断是否要选择到期日期
            layer.confirm(needSelectExpirationText, {
                btn: ['不需要选择到期日期', '选择到期日期'] //按钮
            }, function () {
                // 不需要选择到期日期
                inputso_showChoiceProducts(false);
                getFreight();
            }, function () {
                // 选择到期日期
                inputso_showChoiceProducts(true);
                getFreight();
            });
        }
    })

    loadWarehouses();//仓库
    loandCategory();//类目
    inputso_searchProducts();
    //全选按钮---商品弹出框
    $(".add_sendProduct_pop").on("click", "input[name='all-check']", function(){
        $("input[name='product-check']").prop("checked", this.checked);
    });
    $(".add_sendProduct_pop").on("click", "input[name='product-check']", function(){
        $("input[name='all-check']").prop("checked" , $("input[name='product-check']").length == $("input[name='product-check']").filter(":checked").length ? true :false);
    });
}

//查询商品（可以选择深圳仓和微仓）
function inputso_searchProducts(curr){
    $(".my_micro_product tbody").empty();
    $("#myProducts_pagination").empty();
    $("input[name='all-check']").prop("checked",false);//全选框
    var url = $("#warehouse_select").val() ? "/product/api/getProducts" : "/product/mirc-inventory";
    var param = {
        email: $.trim($("#inso_email").val()),
        currPage: curr ? curr: 1, pageSize: 10, warehouseId: 2024,
        model: $("#inso_email").attr("disMode")
    }
    var categoryId = $("#product_select").val();
    if (categoryId) {
        param.categoryId = categoryId;
    }
    //判断是否是逗号隔开
    var title = $(".add_sendProduct_pop .searchInput").val().trim();
    if (title) {
        var regStr = /,/;
        if (regStr.test(title)) {
            param.skuList = title.split(",");
        } else {
            param.title = title;
        }
    }
    var loading_index = layer.load(1, {shade: 0.5});
    ajax_post(url,JSON.stringify({data:param}),"application/json",function (data) {
        var reData = data.data;
        var result = reData.result;
        inputso_insertProductDetail(result);
        inputso_searchProducts_pagination(reData);
        layer.close(loading_index)
    })
}

function sumOrderTotal(){
    var total = 0;
    var cloudQty = 0;
    $.each(finalProCollection,function(k,item) {
        // var stock = parseInt(item.subStock)
        var subMicroStock = parseInt(item.subMicroStock)
        if(subMicroStock >= 0){
            cloudQty = (item.qty - subMicroStock) <= 0 ? 0 : (item.qty - subMicroStock);
        } else {// 云仓为负数，那说明购买的都是云仓的
            cloudQty = item.qty - subMicroStock;
        }
        total += item.price * cloudQty;
    });
    var code = $("#shippings").find("option:selected").attr("code")
    if(code != 'BBC-TPL'){
        total += Number($("#sale_freight").find("b").text()) 
    }else{
        total += Number($("#thirdPostfee").val());
    }
    $("#auto_check").prop("checked",false);
    $("#total").text(total.toFixed(2));
}

//展示已选商品
function inputso_showChoiceProducts(needExpirationDate){
    $(".record_sendList_table tbody").empty();
    if(needExpirationDate){// 选择到期日期
        // 去查询微仓商品和云仓商品的到期日期
        var params = {email: $.trim($("#inso_email").val())}
        var selectedProducts = []
        for(var i in tempProCollection){
            tempProCollection[i].subStock = 0;
            tempProCollection[i].subMicroStock = 0;
            selectedProducts.push(tempProCollection[i])
        }
        params.selectedProducts = selectedProducts;
        ajax_post_sync("/sales/manager/inputso/expirationdates",JSON.stringify(params), "application/json",function(response){
            if(response.suc){
                var result = response.result;
                for(var i in result){
                    var finalKey = getKey(result[i].sku,result[i].warehouseId,result[i].expirationDate)
                    if(finalProCollection[finalKey]){
                        delete finalProCollection[finalKey]
                    }
                    finalProCollection[finalKey] = result[i]
                }
            }else{
                layer.msg("获取商品到期日期失败", {icon: 2,time: 2000});
            }
        })
    }else{// 系统默认时间
        for(var k in tempProCollection){
            var finalKey = getKey(tempProCollection[k].sku,tempProCollection[k].warehouseId,tempProCollection[k].expirationDate)
            if(finalProCollection[finalKey]){// 已经选过了的，就不要了
                continue
            }else{
                finalProCollection[finalKey] = tempProCollection[k]
            }
        }
    }

    tempProCollection = {}// 清除
    var html = "";
    var i = 0;
    for (var k in finalProCollection) {
        var pro = finalProCollection[k]
        var expirationDate = pro.expirationDate?pro.expirationDate:"";
        html += '<tr>'+
            '<td>'+(++i)+'</td>'+
            '<td name="sku">'+pro.sku+'</td>'+
            '<td imgUrl="'+ pro.imgUrl +'" title="'+pro.title+'">'+pro.title+'</td>'+
            '<td interBarCode="'+ pro.interBarCode +'">'+pro.interBarCode+'</td>'+
            '<td name="warehouseName" warehouseId="'+pro.warehouseId+'">'+pro.warehouseName+'</td>'+
            '<td name="stock">'+pro.subStock+'</td>'+
            '<td name="microStock">'+pro.subMicroStock+'</td>'+
            '<td name="expirationDate" expirationDate="'+expirationDate+'">'+ deal_with_illegal_value(expirationDate) +'</td>'+
            '<td name="price">'+pro.price+'</td>'+
            '<td><input onblur="inputso_inputFinalPrice(this)" originFinalPrice="'+parseFloat(pro.price).toFixed(2)+'" value="'+ parseFloat(pro.price).toFixed(2)+'" class="final-price" type="text"  style="text-align:center;width: 80px;"></td>'+
            '<td class="record_num">'+
            '<span onclick="inputso_reduceProductNum(this)">－</span>'+
            '<input onblur="inputso_inputProductNum(this)" class="input_num" style="text-align:center" type="text" value="'+pro.qty+'">'+
            '<span onclick="inputso_increaseProductNum(this)">＋</span>'+
            '</td>'+
            '<td name="subtotal">'+changeTwoDecimal_f(pro.qty * pro.price)+'</td>'+
            '<td style="cursor: pointer" marketPrice="'+pro.marketPrice+'" onclick="deleteProduct(this)">删除</td>'+
            '</tr>';
    }
    $(".record_sendList_table tbody").html(html)
    layer.closeAll()
}

function inputso_inputFinalPrice(obj){
    var $srcTr = $(obj).parents("tr")
    var sku = $srcTr.find("td[name='sku']").text()
    var warehouseId = $srcTr.find("td[name='warehouseName']").attr("warehouseId")
    var inputVal = $.trim($(obj).val())
    var originFinalPrice = parseFloat($(obj).attr("originFinalPrice"))
    if(!inputVal){
        layer.msg("真实售价不能为空", {icon: 2,time: 2000});
        $(obj).val(originFinalPrice)
        inputso_sameSkuSameFinalPrice(sku,warehouseId,originFinalPrice)
        return;
    }
    if(!isMoneyPattern(inputVal)){
        layer.msg("真实售价格式错误（保留两位小数）", {icon: 2,time: 2000});
        $(obj).val(originFinalPrice)
        inputso_sameSkuSameFinalPrice(sku,warehouseId,originFinalPrice)
        return;
    }

    inputso_sameSkuSameFinalPrice(sku,warehouseId,inputVal)
}

function inputso_sameSkuSameFinalPrice(sku, warehouseId, finalPriceVal){
    if(!sku || !warehouseId) {
        return;
    }
    // 把其他相同sku、仓库的商品的真实售价也改了（同一个商品真实售价一样）2017.3.16
    var skuTemp = '', warehouseIdTemp = ''
    $(".record_sendList_table tbody").find("tr").each(function (i,e) {
        skuTemp = $(e).find("td[name='sku']").text()
        warehouseIdTemp = $(e).find("td[name='warehouseName']").attr("warehouseId")
        if(skuTemp==sku && warehouseIdTemp==warehouseId){
            $(e).find("input[class='final-price']").val(finalPriceVal)
        }
    })
}

function inputso_inputProductNum(obj){
    var $trObj = $(obj).parent().parent();
    var finalKey = getKey($trObj.find("td[name='sku']").text(),
        $trObj.find("td[name='warehouseName']").attr("warehouseId"),
        $trObj.find("td[name='expirationDate']").attr("expirationDate"))
    var stock = finalProCollection[finalKey].subStock
    // 云仓没有，微仓有
    if(parseInt(stock)<1 && parseInt(finalProCollection[finalKey].subMicroStock)>0){
        stock = finalProCollection[finalKey].subMicroStock;
    }else if(parseInt(stock)<1 && parseInt(finalProCollection[finalKey].subMicroStock)<1){
        // 云仓和微仓都没有
        layer.msg("库存不足", {icon: 2,time: 2000});
        return;
    }
    var batchNumber = finalProCollection[finalKey].batchNumber;
    var inputVal = $(obj).val(); // 输入数字
    var regNum = /^[1-9]\d*$/;
    if (!regNum.test(inputVal)) {
        layer.msg("请输入数字(必须是大于0的整数)", {icon: 2,time: 2000});
        $(obj).val(batchNumber);
        return;
    }
    if (parseInt(inputVal) < parseInt(batchNumber)) {
        $(obj).val(batchNumber)
        layer.msg("发货数量不能小于起批量("+batchNumber+")", {icon: 2,time: 2000});
        return;
    }
    if (parseInt(inputVal) > parseInt(stock)) {
        $(obj).val(stock)
        layer.msg("发货数量需等于或小于库存("+stock+")", {icon: 2,time: 2000});
        return;
    }
    finalProCollection[finalKey].qty = parseInt(inputVal);
    // 计算此sku的小计
    $trObj.find("td[name='subtotal']").text(
        changeTwoDecimal_f(finalProCollection[finalKey].qty * finalProCollection[finalKey].price)
    )
    sumOrderTotal();
    getFreight();
}

function inputso_reduceProductNum(obj){
    var $trObj = $(obj).parent().parent();
    var finalKey = getKey($trObj.find("td[name='sku']").text(),
        $trObj.find("td[name='warehouseName']").attr("warehouseId"),
        $trObj.find("td[name='expirationDate']").attr("expirationDate"))
    var batchNumber = finalProCollection[finalKey].batchNumber;
    var inputVal = parseInt($(obj).parent().find("input").val());
    inputVal = inputVal == batchNumber ? batchNumber : (inputVal - 1);// 数量减1
    $(obj).parent().find("input").val(inputVal);
    finalProCollection[finalKey].qty = inputVal;

    // 计算此sku的小计
    $trObj.find("td[name='subtotal']").text(
        changeTwoDecimal_f(finalProCollection[finalKey].qty * finalProCollection[finalKey].price)
    )
    sumOrderTotal();
    getFreight();
}

function inputso_increaseProductNum(obj){
    var $trObj = $(obj).parent().parent();
    var finalKey = getKey($trObj.find("td[name='sku']").text(),
        $trObj.find("td[name='warehouseName']").attr("warehouseId"),
        $trObj.find("td[name='expirationDate']").attr("expirationDate"))
    var stock = finalProCollection[finalKey].subStock
    // 云仓没有，微仓有
    if(parseInt(stock)<1 && parseInt(finalProCollection[finalKey].subMicroStock)>0){
        stock = finalProCollection[finalKey].subMicroStock;
    }else if(parseInt(stock)<1 && parseInt(finalProCollection[finalKey].subMicroStock)<1){
        // 云仓和微仓都没有
        layer.msg("库存不足", {icon: 2,time: 2000});
        return;
    }

    var inputVal = parseInt($(obj).parent().find("input").val());
    inputVal = inputVal == stock ? stock : (inputVal + 1);// 数量加1
    $(obj).parent().find("input").val(inputVal);
    finalProCollection[finalKey].qty = inputVal;

    // 计算此sku的小计
    $(obj).parent().parent().find("td[name='subtotal']").text(
        changeTwoDecimal_f(finalProCollection[finalKey].qty * finalProCollection[finalKey].price)
    )
    sumOrderTotal();
    getFreight();
}

// 商品分页展示
function inputso_searchProducts_pagination(page){
    if ($("#myProducts_pagination")[0] != undefined) {
        $("#myProducts_pagination").empty();
        laypage({
            cont: 'myProducts_pagination',
            pages: page.totalPage,
            curr: page.currPage,
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
                    inputso_searchProducts(obj.curr);
                }
            }
        });
    }
}

//插入产品详细信息
function inputso_insertProductDetail(list){
    $(".my_micro_product tbody").empty();
    if(!list || list.length==0){
        $(".my_micro_product tbody").append("<tr style='text-align: center;'><td colspan='9'>暂无商品</td></tr>");
        return
    }

    var html = "";
    $.each(list, function (i, item) {
        if(item.isSpecial){
            item.disPrice = item.specialSale;
        }
        if($("#inso_email").attr("disMode") == '3') {
            item.disPrice = item.disPrice == null ? null : parseFloat(item.disPrice).toFixed(4);
        } else {
            item.disPrice = item.disPrice == null ? null : parseFloat(item.disPrice).toFixed(2);
        }

        var pro = finalProCollection[getKey(item.csku,item.warehouseId)];
        // 云仓库存&微仓库存都为0，不可以选择
        var notEnoughStock = (parseInt(item.microStock)<1 && parseInt(item.stock)<1);
        var disabled = notEnoughStock ? 'disabled="disabled"' : "";
        // 已选中过
        var selectedBefore = !disabled && pro
        disabled = selectedBefore ? 'disabled="disabled"' : "";
        // var microStock = item.microStock == 0 ? 1 : item.microStock;
        html += '<tr class="list_my_product">'+
            '<td name="sku">'+item.csku+'</td>'+
            '<td name="ctitle" imgurl="'+ item.imageUrl +'">'+item.ctitle+'</td>'+
            '<td name="interBarCode">'+item.interBarCode+'</td>'+
            '<td name="warehouseName" warehouseId="'+item.warehouseId+'">'+item.warehouseName+'</td>'+
            '<td name="stock">'+item.stock+'</td>'+
            '<td name="microStock">'+item.microStock+'</td>'+
            '<td name="disPrice">'+deal_with_illegal_value(item.disPrice)+'</td>'+
            '<td name="marketPrice">'+item.localPrice+'</td>'+
            '<td>' +
            (item.disPrice == null ? "--" : '<input ' + disabled + ' style="cursor:pointer" batchNumber="'+ item.batchNumber +'"  ' +
            'type="checkbox" name="product-check" title="'+(notEnoughStock ? "库存不足" : (selectedBefore?"已选择过":""))+'">') +
            '</td>'+
            '</tr>';
    });
    $(".my_micro_product tbody").append(html);
    $("input[name='all-check']").prop("checked" , $("input[name='product-check']").length == $("input[name='product-check']").filter(":checked").length ? true :false);
}

//加载商品类目
function loandCategory(){
    $("#product_select").empty();
    ajax_get("/product/api/realCateQuery?level=1", "", "application/json",
        function (data) {
            var cateHtml = "<option value='' selected>所有商品</option>";
            if (data && data.length > 0) {
                $.each(data, function (i, item) {
                    cateHtml += "<option value='" + item.iid + "'>" + item.cname + "</option>";
                });
            }
            $("#product_select").html(cateHtml);
        }
    );
}

//加载仓库-只能发深圳仓
function loadWarehouses(){
    $("#warehouse_select").empty();
    var wareHtml = "<option value='' selected>微仓</option>";
    $.ajax({url: "/inventory/queryWarehouse", type: "get", dataType: "json", async: false,
        success: function (data) {
            if (data.length > 0) {
                for (var i in data) {
                    if (data[i].id == 2024) {
                        wareHtml += "<option value='" + data[i].id + "'>" + data[i].warehouseName + "</option>";
                    }
                }
                $("#warehouse_select").html(wareHtml);
            }
        }
    });
}

//删除某个已选产品
function deleteProduct(obj){
    var finalKey = getKey($(obj).parent().find("td[name='sku']").text(),
        $(obj).parent().find("td[name='warehouseName']").attr("warehouseId"),
        $(obj).parent().find("td[name='expirationDate']").attr("expirationDate"))
    delete finalProCollection[finalKey];
    inputso_showChoiceProducts();
    getFreight();
}

$("body").on("change", "#sale_province", function () {
    citySel();
    $("#sale_postcode").val($("#sale_city").find("option[value="+$("#sale_city").val()+"]").attr("code"));
    provinceIdForFreight = $("#sale_province").val();
    getFreight();
});

function initAreaSel(pid, cid, rid) {
    $("#sale_province").empty();
    $("#sale_city").empty();
    $("#sale_region").empty();
    ajax_get("/member/getprovs", JSON.stringify(""), "application/json",
        function (data) {
            prov_map = {};
            for (var i = 0; i < data.length; i++) {
                $("#sale_province").append("<option value='" + data[i].id + "' >" + data[i].provinceName + "</option>");
                prov_map[data[i].provinceName.substr(0,data[i].provinceName.length-1)] = data[i].id;
            }
            //省份存在就选中
            if (pid) {
                $("#sale_province").val(pid);
            } else {
                //不存在已选择省份，默认选第一个
                var sel = $("#sale_province").find("option:first").val();
                $("#sale_province").val(sel);
            }
            citySel(cid, rid);
            $("#sale_postcode").val($("#sale_city").find("option[value="+$("#sale_city").val()+"]").attr("code"));
            provinceIdForFreight = $("#sale_province").val();
            getFreight();
        }
    );
}

function citySel(cid, rid) {
    $("#sale_city").empty();
    if($("#sale_province").val()){
        ajax_get("/member/getcities", "proId=" + $("#sale_province").val(), "",
            function (data) {
                city_map = {};
                for (var i = 0; i < data.cities.length; i++) {
                    $("#sale_city").append("<option value='" + data.cities[i].id + "' code='"+data.cities[i].zipCode+"' >" + data.cities[i].cityName + "</option>");
                    city_map[data.cities[i].cityName.substr(0,data.cities[i].cityName.length-1)] = data.cities[i].id;
                }
                //城市存在就选中
                if (cid) {
                    $("#sale_city").val(cid);
                } else {
                    var sel = $("#sale_city").find("option:first").val();
                    $("#sale_city").val(sel);
                }
                regionSel(rid);
            }
        );
    }
}

//区级下拉框联动
$("body").on("change", "#sale_city", function () {
    regionSel();
});

function regionSel(rid) {
    $("#sale_region").empty();
    if($("#sale_city").val()){
        ajax_get("/member/getareas", "cityId=" + $("#sale_city").val(), "",
            function (data) {
                region_map = {};
                for (var i = 0; i < data.areas.length; i++) {
                    $("#sale_region").append("<option value='" + data.areas[i].id + "' >" + data.areas[i].areaName + "</option>");
                    region_map[data.areas[i].areaName.substr(0,data.areas[i].areaName.length-1)] = data.areas[i].id;
                }
                //地区存在就选中
                if (rid) {
                    $("#sale_region").val(rid);
                } else {
                    var sel = $("#sale_region").find("option:first").val();
                    $("#sale_region").val(sel);
                }
            }
        );
    }
}

//自动匹配收货人信息
//广东省 深圳市 龙岗区 坂田街道桥联东路顺兴楼1003 518116 黄安纳 13603064940        千牛格式
//李心瑞，13999889672，新疆维吾尔自治区 乌鲁木齐市 沙依巴克区 红庙子街道西城街荣和城二期28号楼2单元1701 ，830000      网页格式
function sureMatch(){
    var template = $("#address_template").val();//选择的地址格式
    if (!template) {
        layer.msg("请选择地址格式！", {icon: 5, time: 2000});
        return;
    }

    var flag = false;
    if (template == 1) {//淘宝千牛格式
        var waitInfo = $("input[name='address2Parsed']").val().trim().split("  ");
        var length = waitInfo.length;
        if (length >= 7) {
            $("#sale_province").val(getAddressId(waitInfo[0],prov_map)).change();
            if($("#sale_province").val()){
                $("#sale_city").val(getAddressId(waitInfo[1],city_map)).change();
                if($("#sale_city").val()){
                    flag = true;
                    $("#sale_region").val(getAddressId(waitInfo[2],region_map));
                }
            }
            $("#receiveName").val(waitInfo[length-2]);
            $("#sale_tel").val(waitInfo[length-1]);
            $("#sale_postcode").val(waitInfo[length-3]);
            $("#address_detail").val(waitInfo[4]);
        }
    } else {//淘宝网页格式
        var waitInfo = $("input[name='address2Parsed']").val().split("，");
        if (waitInfo.length >= 4) {
            var addrs = waitInfo[2].trim().split(" ");
            if (addrs.length >= 4) {
                $("#sale_province").val(getAddressId(addrs[0],prov_map)).change();
                if($("#sale_province").val()){
                    $("#sale_city").val(getAddressId(addrs[1],city_map)).change();
                    if($("#sale_city").val()){
                        flag = true;
                        $("#sale_region").val(getAddressId(addrs[2],region_map));
                    }
                }
                var address = "";
                $.each(addrs,function(i,item){
                    if(i>=3) {
                        address += item;
                    }
                });
                $("#address_detail").val(address);
            }
            $("#receiveName").val(waitInfo[0]);
            $("#sale_tel").val(waitInfo[1]);
            $("#sale_postcode").val(waitInfo[3]);
        }
    }
    if (!flag) {
        layer.msg("输入格式不正确或省市区未匹配！", {icon: 5, time: 2000});
    }
}

function getAddressId(key,map){
    if(map[key]){
        return map[key];
    }

    key = key.substr(0,key.length-1);
    if(key){
        return map[key];
    }
}

function getFreight() {
    $(".autoFreight").prop("checked",false);
    if (provinceIdForFreight == 0) {
        return;
    }
    var target = $(".record_sendList_line").find("#shippings");
    var code = target.find("option:selected").attr("code");
    var keys = Object.keys(finalProCollection);
    var warehouseId = finalProCollection[keys[0]] ? finalProCollection[keys[0]].warehouseId : "";
    if (code && keys.length > 0 && warehouseId == 2024) {
        //物流方式已添加，则选择既存物流方式
        if(code != 'BBC-TPL'){
            //添加完毕，获取默认选中的物流方式的运费
            var freightParam = {
                warehouseId:warehouseId, shippingCode:code,
                countryId:44, provinceId:provinceIdForFreight
            };
            var orderDetails = [];
            for (var k in finalProCollection) {
                var pro = finalProCollection[k];
                orderDetails.push({sku:pro.sku, num:pro.qty});
            }
            freightParam.orderDetails = orderDetails;
            getFreightByProvince(freightParam);
            $("#thirdPostfee").hide()
        }else{
            $("#thirdPostfee").show();
            $("#sale_freight").hide().find("b").text(0.00);
            sumOrderTotal();
        }
    } else {
        $("#sale_freight b").text(0.00);
        sumOrderTotal();
    }
}

/**
 * 根据上分ID获取运费
 * @param freightParam
 */
function getFreightByProvince(freightParam) {
    ajax_post("/inventory/getFreight", JSON.stringify(freightParam), "application/json",
        function (freightResStr) {
            var freightRes = freightResStr;
            if (freightRes.result) {
                //自提标识
                var fee = freightRes.msg;
                $("#sale_freight b").text(parseFloat(fee).toFixed(2));
                sumOrderTotal();
                $("#sale_freight").show();
            } else {
                layer.msg("运费获取失败！" + freightRes.msg, {icon: 2, time: 2000});
            }
        },
        function (XMLHttpRequest, textStatus) {
            layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
        }
    );
}

function checkdBanlance(obj){
    var email = $.trim($("#inso_email").val());
    if ($(obj).prop("checked")) {
        if (email && email != "") {
            ajax_get("/member/getAccount", "email=" + email, "",
                function (data) {
                    var banlance = parseFloat(data.balance);
                    if (banlance && banlance < total) {
                        layer.msg("该分销商余额不充足无法扣除运费！", {icon: 5, time: 2000});
                        $(obj).prop("checked",false);
                    }
                }
            );
        } else {
            $(obj).prop("checked",false);
            layer.msg("请先选择分销商！", {icon: 2, time: 2000});
        }
    }
}

//生成订单
function generateSaleOrder() {
    $("#submit_sale").attr("disabled", true).css("cursor", "not-allowed").html("正在生成");
    isaulogin(function (salesman) {
        var flag = inputso_validation();
        if (!flag) {
            $("#submit_sale").attr("disabled", false).css("cursor", "pointer").html("生成订单");
            return;
        }

        var email = $.trim($("#inso_email").val());
        var code = $("#shippings").find("option:selected").attr("code");
        //收货地址要有空格
        var address = $("#sale_province option:selected").text() + " "
            + $("#sale_city option:selected").text() + " "
            + $("#sale_region option:selected").text() + " "
            + $("#address_detail").val().trim();

        var param = {
            "email" : email,
            "remark" : "",
            "address" : address,
            "receiver" : $("#receiveName").val(),
            "telphone" : $("#sale_tel").val(),
            "postCode": $("#sale_postcode").val(),
            "LogisticsTypeCode": code,
            "logisticsMode": $("#shippings").find("option:selected").text() ? $("#shippings").find("option:selected").text() : null,
            "createUser": salesman,
            "isBack": true,
            "provinceId" : provinceIdForFreight,
            "isPay" : $("#auto_check").prop("checked"),
        };
        if(code == 'BBC-TPL'){
            param.thirdPostfee = $("#thirdPostfee").val();
        }
        var details = [];
        $(".record_sendList_table tbody").find("tr").each(function (i,e) {
            var warehouseId = $(e).find("td[name='warehouseName']").attr("warehouseId")
            details.push({
                "sku": $(e).find("td[name='sku']").text(),
                "warehouseId": warehouseId,
                "expirationDate": $(e).find("td[name='expirationDate']").attr("expirationDate"),
                "num": $(e).find(".input_num").val(),
                "finalSellingPrice": $(e).find(".final-price").val()
            });
            param['warehouseName'] = $(e).find("td[name='warehouseName']").text();
            param['warehouseId'] = warehouseId;
        });
        param['skuList'] = details;

        ajax_post_sync("/sales/manager/postOrder",JSON.stringify(param),"application/json",function (response) {
            if(response.code == 108) {
                layer.msg("订单已生成，单号为：" + response.msg, {icon: 6, time: 2000});
                $("p[position=" + $("#forword").val() + "]").click();
            } else {
                layer.msg(response.msg, {icon: 5, time: 2000});
                $("#submit_sale").attr("disabled", false).css("cursor", "pointer").html("生成订单");
            }
        })
    });
}

function inputso_validation() {
    var email = $.trim($("#inso_email").val());
    if (!email) {
        layer.msg('请选择分销商', {icon: 2, time: 2000});
        return false;
    }
    //必须有发货商品
    var skus = Object.keys(finalProCollection);
    if (skus.length == 0) {
        layer.msg('需要为客户订单添加发货商品', {icon: 2, time: 2000});
        return false;
    }
    var code = $("#shippings").find("option:selected").attr("code");
    if(!code){
        var fee = $("#thirdPostfee").val();
        var reg = /^[0-9]+(.[0-9]{1,2})?$/;
        var flag = fee?reg.test(fee)&&fee>0:false;
        if(!flag){
            layer.msg("请输入有效的[<b style='color:#E74C3C'>第三方物流费用</b>]（至多两位小数,且必须大于零）", {icon: 2, time: 2500});
            return false;
        }
    }
    //每个发货产品必须有真实售价
    var fspFlag = true;
    $(".record_sendList_table tbody").find("tr").each(function (i,e) {
        var curfsp = $.trim($(e).find(".final-price").val());

        if (!curfsp) {
            fspFlag = false;
            $(e).find(".final-price").focus();
            layer.msg("请输入有效的[<b style='color:#E74C3C'>真实单价</b>]（至多两位小数）", {icon: 2, time: 2500});
            return false;
        }
        if (!isMoneyPattern(curfsp)) {
            fspFlag = false;
            $(e).find(".final-price").focus();
            layer.msg("请输入有效的[<b style='color:#E74C3C'>真实单价</b>]（至多两位小数）", {icon: 2, time: 2500});
            return false;
        }
    });
    if (!fspFlag) {
        return false;
    }
    if (!$("#receiveName").val()) {
        layer.msg('收货人不能为空', {icon: 2, time: 2000});
        return false;
    }
    //收货人手机验证
    if (!checkTel($("#sale_tel").val())) {
        layer.msg('收货人手机号码有格式错误，请输入有效手机号码', {icon: 2, time: 2000});
        return false;
    }
    //收货地址验证
    if (!$("#sale_province").val()) {
        layer.msg("请选择省！",{icon:2,time:2000});
        return
    }
    if (!$("#sale_city").val()) {
        layer.msg("请选择市！",{icon:2,time:2000});
        return
    }
    if (!$("#sale_region").val()) {
        layer.msg("请选择区！",{icon:2,time:2000});
        return
    }
    var address = $("#address_detail").val().trim();
    if (!address || address.length < 5) {
        layer.msg('收货地址不规范，请重新填写', {icon: 2, time: 2000});
        return false;
    }
    var postCode = $("#sale_postcode").val();
    if (postCode && !checkPost(postCode)) {
        layer.msg('请输入有效的收货地址邮政编码', {icon: 2, time: 2000});
        return false;
    }
    return true;
}

var changeTwoDecimal_f = function (x) {
    var f_x = parseFloat(x);
    if (isNaN(f_x)) {
        return false;
    }
    f_x = Math.round(f_x * 100) / 100;
    var s_x = f_x.toString();
    var pos_decimal = s_x.indexOf('.');
    if (pos_decimal < 0) {
        pos_decimal = s_x.length;
        s_x += '.';
    }
    while (s_x.length <= pos_decimal + 2) {
        s_x += '0';
    }
    return s_x;
}

function showErpSto(){
    var keys = Object.keys(finalProCollection);
    if(!keys.length){
        layer.msg("请选择商品",{icon:5,time:2000});
        return;
    }

    var skuMap = {};
    $.each(finalProCollection,function(k,v){
        var sku = v.sku
        //数量合并
        if(skuMap[sku]){
            skuMap[sku].qty = Number(v.qty) + Number(skuMap[sku].qty);
        }else{
            skuMap[sku] = {
                sku: v.sku,
                title: v.title,
                warehouseName: v.warehouseName,
                qty: v.qty,
                erpStock: 0,
                short: true, // 默认erp库存不够
                interBarCode: v.interBarCode
            };
        }
    });
    var skus = Object.keys(skuMap);
    erpStoHtml(erpSto({warehouseId:finalProCollection[keys[0]].warehouseId, skus:skus},skuMap));
}

function getKey(sku, warehouseId, expirationDate){
    var key = sku;
    if(warehouseId){
        key = key + "_" + warehouseId
    }
    if(expirationDate){
        key = key + "_" + expirationDate
    }
    return key;
}
