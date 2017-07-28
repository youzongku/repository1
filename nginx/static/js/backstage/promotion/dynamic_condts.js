/**
 * Created by Administrator on 2016/7/29.
 */
var layer = undefined;
var laypage = undefined;
//初始化
function init_dynamic_condts(layerParam, laypageParam) {
    //初始化全局变量
    layer = layerParam;
    laypage = laypageParam;
}

/**
 * 创建动态的条件
 * @param containerId    存放的dom id
 * @param actInst       条件实例
 * @param condtInstDtoList     条件集合
 */
function createDynamicCondts(containerId, fullCondtInstDto) {
    $("#match_type_div").removeAttr("style").empty().hide();
    // if (condtInstDtoList.length > 1) {
    //     var matchType = actInst.matchType;
    //     // 多个条件显示
    //     var match_type_select_html = '<select class="promotion_select" canUse="true" id="match_type_select">';
    //     if (matchType == 1) {
    //         match_type_select_html += '<option selected="selected" value="1">全部</option><option value="2">任意</option>';
    //     } else if (matchType == 2) {
    //         match_type_select_html += '<option value="1">全部</option><option selected="selected" value="2">任意</option>';
    //     }
    //     match_type_select_html += '</select>';
    //     $("#match_type_div").html('如果购物车中的商品找到满足下列' + match_type_select_html + '条件');
    //     $("#match_type_div").css("margin-bottom", "10px").show();
    // }

    $("#stepped_checkbox").prop("checked",false) // 可阶梯复选框
    // TODO 暂时屏蔽可阶梯
    // $("#stepped_checkbox").parent().show()
    var condtInstExt = fullCondtInstDto.condtInstExt  // 额外的指定属性json值
    //var stepped = condtInstExt.stepped;  // 可阶梯不
    //$("#stepped_checkbox").prop("checked",stepped)
    var doubleUp = condtInstExt.doubleUp; // 可翻倍不

    // 多个条件实例
    var content = '';
    var condtInstDtoList = new Array()
    condtInstDtoList.push(fullCondtInstDto)
    for (var i in condtInstDtoList) {
        var condtInst = condtInstDtoList[i];// 条件实例
        var condtJgmntTypeList = condtInst.condtJgmntTypeList;// 判断类型：是、否、大于、小于等

        //商品分类=1，指定商品=2，指定仓库=3，商品类型=4，小计金额=5，总商品数量=6，总重量=7，运送地区=8
        //普通分销商=9，合营分销商=10，内部分销商=11，新注册用户=12，第一次购物=13，三个月内未购物的用户=14
        var cType = condtInst.cType;   // 条件的类型
        var condtInstId = condtInst.id;// 条件实例id
        var jType = condtInst.condtJgmntType;// 条件的判断类型
        var condtJgmntValue = eval("(" + condtInst.condtJgmntValue + ")");// 将json字符串转变为json对象
        var options = createJgmntTypeOptions(condtJgmntTypeList, jType);
        switch (cType) {
            case 1:// 商品分类 // 商品分类 {"productCategories":[{"categoryId":4700,"categoryName":"钟表首饰"}]}
                var cnameArray = new Array(), idAndNameArray = new Array();// 分类名称数组；分类id和名称拼接起来的数组
                var combined = false;
                if (!isnull(condtJgmntValue)) {
                    var productCategories = condtJgmntValue.productCategories;
                    combined = condtJgmntValue.combined
                    for (var i in productCategories) {
                        cnameArray.push(productCategories[i].categoryName);
                        idAndNameArray.push(productCategories[i].categoryId + ":" + productCategories[i].categoryName);
                    }
                }
                var combined_checkedValue = ''
                if(combined) combined_checkedValue="checked"// 是否选中可组合
                var condt_product_category =
                    '<p class="para_blcok dynamic_condt dynamic_condt_p" condtInstId="' + condtInstId + '" c_type="1">' +
                    '<span><b>添加条件：</b></span>' +
                    '<span>' +
                    '<b>商品分类：</b>' +
                    '<select name="jType_select">' + options + '</select>' +
                    '<input value="' + cnameArray.toString() + '" type="text" class="promotion_input" ' +
                    'title="' + cnameArray.toString() + '" id="product_category_cnames" readonly />' +
                    '<input value="' + idAndNameArray.join("||") + '" id="product_category_idAndNames" type="hidden" />' +
                    '<button onclick="product_category_popup(this)" class="promotion_button">搜索</button>' +
                    '</span>' +
                    '<span style="display:inline;float: right;margin-right: 32px;">' +
                    '<input '+ combined_checkedValue + ' name="combined_checkbox" type="checkbox">可组合' +
                    '</span>'+
                    '</p>';
                content += condt_product_category;
                break;
            case 2:// 指定商品 "specifyProductList":[{sku:"IM27","warehouseId":2024,num:1},{sku:"IM21","warehouseId":2024,num:1}]
                // sku数组、仓库id数组、仓库名称数组、商品名称数组、商品图片url数组、箱规个数数组
                var skuArray = new Array(), warehouseIdArray = new Array(), warehouseNameArray = new Array(),
                    cTitleArray = new Array(), imgUrlArray = new Array(), unitNumArray = new Array();
                var unitNum = 0;
                var unit = '件';
                var combined = false
                if (!isnull(condtJgmntValue)) {
                    var specifyProductList = condtJgmntValue.specifyProductList;
                    for (var i in specifyProductList) {
                        skuArray.push(specifyProductList[i].sku);
                        warehouseIdArray.push(specifyProductList[i].warehouseId);
                        warehouseNameArray.push(specifyProductList[i].warehouseName);
                        cTitleArray.push(specifyProductList[i].cTitle);
                        imgUrlArray.push(specifyProductList[i].imgUrl);
                        unitNumArray.push(specifyProductList[i].unitNum);
                    }
                    combined = condtJgmntValue.combined;
                    unitNum = condtJgmntValue.unitNum;
                    unit = condtJgmntValue.unit;
                }
                var combined_checkedValue = ''
                if(combined) combined_checkedValue="checked"// 是否选中可组合
                var specify_product_unit_select_display = "none";
                if(jType=='y'){
                    specify_product_unit_select_display = "inline";
                }
                // 箱规
                var unit_html = '<span id="specify_product_unit_span" style="display:' + specify_product_unit_select_display + ';">' +
                    '指定商品规格：' +
                    '<select id="specify_product_unit_select" onchange="specify_product_unit_select_onchange(this)" ' +
                    'style="width: 50px;height:25px;">';
                if (unit == '箱') {
                    unit_html += '<option value="件">个</option><option value="箱" selected="selected">箱</option>';
                } else {
                    unit_html += '<option value="件" selected="selected">个</option><option value="箱">箱</option>';
                }
                unit_html += '</select></span>';

                var condt_specify_product =
                    '<p class="para_blcok dynamic_condt dynamic_condt_p" condtInstId="' + condtInstId + '" c_type="2">' +
                    '<span><b>添加条件：</b></span>' +
                    '<span>' +
                    '<b>指定商品：</b>' +
                    '<select onchange="specify_product_select_onchange(this)" name="jType_select">' + options + '</select>' +
                    '<input id="specify_product_skus" title="' + skuArray.toString() + '" value="' + skuArray.toString() + '" type="text" readonly class="promotion_input">' +
                    '<input value="' + warehouseIdArray.toString() + '" id="specify_product_warehouseIds" type="hidden">' +
                    '<input value="' + warehouseNameArray.toString() + '" id="specify_product_warehouseNames" type="hidden">' +
                    '<input value="' + cTitleArray.join("||") + '" id="specify_product_cTitles" type="hidden">' +
                    '<input value="' + imgUrlArray.toString() + '" id="specify_product_imgUrls" type="hidden">' +
                    '<input value="' + unitNumArray.toString() + '" id="specify_product_unitNums" type="hidden">' +
                    '<button onclick="specify_product_popup(this)" class="promotion_button appoint_search">搜索</button>' +
                    '</span>'+
                    '<span style="display:inline;float: right;margin-right: 32px;">' +
                    '<input '+ combined_checkedValue + ' name="combined_checkbox" type="checkbox">可组合' +
                    '</span>'+
                    unit_html +
                    '</p>';
                content += condt_specify_product;
                break;
            case 3:// 指定仓库 "specifyWarehouses":[{warehouseId:12,warehouseName:"深圳仓"},{warehouseId:13,warehouseName:"广州仓"}]
                // todo 暂不支持
                var warehouseNameArray = new Array(), warehouseIdArray = new Array();
                if (!isnull(condtJgmntValue)) {
                    var specifyWarehouses = condtJgmntValue.specifyWarehouses;
                    for (var i in specifyWarehouses) {
                        warehouseNameArray.push(specifyWarehouses[i].warehouseName);
                        warehouseIdArray.push(specifyWarehouses[i].warehouseId);
                    }
                }
                var condt_specify_warehouse =
                    '<p class="para_blcok dynamic_condt dynamic_condt_p" condtInstId="' + condtInstId + '" c_type="3">' +
                    '<span><b>添加条件：</b></span>' +
                    '<span><b>指定仓库：</b>' +
                    '<select name="jType_select">' + options + '</select>' +
                    '<input id="specify_warehouse_names" value="' + warehouseNameArray.toString() + '" type="text" readonly class="promotion_input">' +
                    '<input value="' + warehouseIdArray.toString() + '" id="specify_warehouse_ids" type="hidden">' +
                    '<button onclick="specify_warehouse_popup(this)" class="promotion_button warehouse_search">搜索</button>' +
                    '</span>' +
                    '<span style="display:inline;"><input name="combined_checkbox" type="checkbox">可组合</span>'+
                    '</p>';
                content += condt_specify_warehouse;
                break;
            case 4:// 商品类型，完税还是跨境
                // todo 暂不支持
                var productType = 0;
                if (!isnull(condtJgmntValue)) {
                    productType = condtJgmntValue.productType;
                }
                var product_type_select = '<select>';
                if (productType == 1) {
                    product_type_select += '<option selected="selected" value="1">完税商品</option><option value="2">跨境商品</option>';
                } else if (productType == 2) {
                    product_type_select += '<option value="1">完税商品</option><option selected="selected" value="2">跨境商品</option>';
                } else if (productType == 0) {
                    product_type_select += '<option selected="selected" value="">请选择</option><option value="1">完税商品</option>' +
                        '<option value="2">跨境商品</option>';
                }
                product_type_select += '</select>';
                var condt_product_type =
                    '<p class="para_blcok dynamic_condt dynamic_condt_p" condtInstId="' + condtInstId + '" c_type="4">' +
                    '<span><b>添加条件：</b></span>' +
                    '<span><b>商品类型：</b>' +
                    '<select name="jType_select">' + options + '</select>' +
                    product_type_select +
                    '</span>' +
                    '<span style="display:inline;"><input name="combined_checkbox" type="checkbox">可组合</span>'+
                    '</p>';
                content += condt_product_type;
                break;
            case 5:// 小计金额 "minPrice":1,"maxPrice":2
                // 小计金额是没有可阶梯的
                $("#stepped_checkbox").parent().hide()

                var minPriceValue = "";
                var maxPriceValue = "";
                var doubleUp_checkedValue = "";
                if (!isnull(condtJgmntValue)) {
                    minPriceValue = "value='" + condtJgmntValue.minPrice + "'";
                    maxPriceValue = "value='" + condtJgmntValue.maxPrice + "'";
                    if (doubleUp) {
                        doubleUp_checkedValue = "checked";
                    }
                }
                var subtotal_span_html;
                if (jType == 'ltv') {
                    subtotal_span_html = '<b>购物车小计金额范围区间：</b>' +
                        '<input ' + minPriceValue + ' type="text" class="promotion_input">至' +
                        '<input ' + maxPriceValue + ' type="text" class="promotion_input">';
                } else {
                    subtotal_span_html = '<input type="text" ' + minPriceValue + ' class="promotion_input">';
                }
                var condt_subtotal =
                    '<p class="para_blcok dynamic_condt dynamic_condt_p" condtInstId="' + condtInstId + '" c_type="5">' +
                    '<span><b>添加条件：</b>小计金额</span>' +
                    '<select onchange="subtotal_span(this)" name="jType_select">' + options + '</select>'
                    + '<span id="subtotal_span">' + subtotal_span_html + '</span>';
                if (jType != 'ltv') { // 区间是没有可翻倍的
                    condt_subtotal += '<span style="display: inline;float:right;margin-right:33px">' +
                        '<input ' + doubleUp_checkedValue + ' id="subtotal_doubleUp" type="checkbox">可翻倍' +
                        '</span>';
                }
                condt_subtotal += '</p>';
                content += condt_subtotal;
                break;
            case 6:// 总商品数量 "minTotalCount":1,"maxTotalCount":2
                var minTotalCount = 0;
                var doubleUp_checkedValue = ''
                if (!isnull(condtJgmntValue)) {
                    minTotalCount = condtJgmntValue.minTotalCount;
                    if (doubleUp) {
                        doubleUp_checkedValue = "checked";
                    }
                }
                var condt_product_total_count =
                    '<p class="para_blcok dynamic_condt dynamic_condt_p" condtInstId="' + condtInstId + '" c_type="6">' +
                    '<span><b>添加条件：</b></span>' +
                    '<span>' +
                    '<b>总商品数量：</b>' +
                    '<select name="jType_select">' + options + '</select>' +
                    '<input type="text" value="' + minTotalCount + '" class="promotion_input">' +
                    '</span>'   +
                    '<span style="display: inline;float:right;margin-right:33px">' +
                    '<input ' + doubleUp_checkedValue + ' id="total_count_doubleUp" type="checkbox" >可翻倍' +
                    '</span>';
                    '</p>';
                content += condt_product_total_count;
                break;
            case 7:// 购物车总重量  "minWeight":1,"maxWeight":2
                // todo 暂不支持
                var minWeight = 0.00;
                var maxWeight = 0.00;
                if (!isnull(condtJgmntValue)) {
                    minWeight = condtJgmntValue.minWeight;
                    maxWeight = condtJgmntValue.maxWeight;
                }
                var shopping_cart_total_weight_span_html;
                if (jType == 'ltv') {
                    shopping_cart_total_weight_span_html = '<b>总重量范围区间：</b>' +
                        '<input type="text" value="' + minWeight + '" class="promotion_input" >至' +
                        '<input type="text" value="' + maxWeight + '" class="promotion_input">kg';
                } else {
                    shopping_cart_total_weight_span_html = '<input type="text" value="' + minWeight + '" class="promotion_input">kg';
                }
                var condt_shopping_cart_total_weight =
                    '<p class="para_blcok dynamic_condt dynamic_condt_p" condtInstId="' + condtInstId + '" c_type="7">' +
                    '<span><b>添加条件：</b>总重量</span>' +
                    '<select onchange="shopping_cart_total_weight_span(this)" name="jType_select">' + options + '</select>' +
                    '<span id="shopping_cart_total_weight_span">' + shopping_cart_total_weight_span_html + '</span>' +
                    '</p>';
                content += condt_shopping_cart_total_weight;
                break;
            case 8:// 运送地区 "shippingRegions":[{cityId:12,cityName:"北京"},{cityId:12,cityName:"广州"}]
                // todo 暂不支持
                var cityNameArray = new Array(), cityIdArray = new Array();
                if (!isnull(condtJgmntValue)) {
                    var shippingRegions = condtJgmntValue.shippingRegions;
                    for (var i in shippingRegions) {
                        cityNameArray.push(shippingRegions[i].cityName);
                        cityIdArray.push(shippingRegions[i].cityId);
                    }
                }
                var condt_shipping_region =
                    '<p class="para_blcok dynamic_condt dynamic_condt_p" condtInstId="' + condtInstId + '" c_type="8">' +
                    '<span><b>添加条件：</b>运送地区</span>' +
                    '<select name="jType_select">' + options + '</select>' +
                    '<input value="' + cityNameArray.toString() + '" id="shipping_region_names" type="text" readonly class="promotion_input">' +
                    '<input value="' + cityIdArray.toString() + '" id="shipping_region_ids" type="hidden">' +
                    '<button class="promotion_button convey_search" onclick="searchShippingRegion(this)">搜索</button>' +
                    '</span>' +
                    '</p>';
                content += condt_shipping_region;
                break;
            default:
                break;
        }
    }

    // 指定商品属性或指定购物车属性
    var hasExtCondt = fullCondtInstDto.hasExtCondt
    $("#condtInstExtId_hidden").val(condtInstExt.id)// 额外的指定属性的id
    var specifyAttrValue;
    if (hasExtCondt) {
        if(!isnull(condtInstExt.specifyAttrValue)){
            specifyAttrValue = eval("("+condtInstExt.specifyAttrValue+")");// 将json字符串转变为json对象
        }
        var specify_attr_name = ''
        if (fullCondtInstDto.attr == 1) {
            specify_attr_name = '指定商品属性'
        } else if (fullCondtInstDto.attr == 2) {
            specify_attr_name = '指定购物车属性'
        }

        var attrType ,jType,singleVal=''
        // 拿出具体的值
        if(!isnull(specifyAttrValue)){
            attrType = specifyAttrValue.attrType
            jType = specifyAttrValue.jType
            singleVal = specifyAttrValue.singleVal
        }
        // 数据回显
        // 数量/金额
        var moneyOrNumSelect_options = '<option value="">请选择</option><option value="1">数量</option><option value="2">金额</option>'
        if(attrType == 1){
            moneyOrNumSelect_options = '<option value="1" selected>数量</option><option value="2">金额</option>'
        } else if(attrType == 2){
            moneyOrNumSelect_options = '<option value="1">数量</option><option value="2" selected>金额</option>'
        }
        // 判断类型
        var moneyOrNumJtypeSelect_options = '<option value="">请选择</option><option value="gteq">大于等于</option>' +
        '<option value="gt">大于</option><option value="ltv">区间</option>';
        if(jType=='gteq'){
            moneyOrNumJtypeSelect_options = '<option value="gteq" selected>大于等于</option>' +
                '<option value="gt">大于</option><option value="ltv">区间</option>';
        }else if(jType=='gt'){
            moneyOrNumJtypeSelect_options = '<option value="gteq">大于等于</option>' +
                '<option value="gt" selected>大于</option><option value="ltv">区间</option>';
        }else if(jType=='ltv'){
            moneyOrNumJtypeSelect_options = '<option value="gteq">大于等于</option>' +
                '<option value="gt">大于</option><option value="ltv" selected>区间</option>';
        }

        // 判断类型的数值
        var dynamic_ext_condt_value_html = ""
        if(!isnull(jType) && jType != 'ltv'){
            dynamic_ext_condt_value_html = "<input type='text' oninput='checkSpecifyValue(this)' class='promotion_input' value='"+singleVal+"' />"
        } else if (!isnull(jType) && jType == 'ltv') {
            var minJType ,minValue ,maxJType ,maxValue
            if(!isnull(specifyAttrValue) && !isnull(specifyAttrValue.ltvVal)){
                minJType = specifyAttrValue.ltvVal.minJType
                minValue = specifyAttrValue.ltvVal.minValue
                maxJType = specifyAttrValue.ltvVal.maxJType
                maxValue = specifyAttrValue.ltvVal.maxValue
            }
            var minJTypeSelect = '<select><option value="gteq" selected>大于等于</option><option value="gt">大于</option></select>'
            if(minJType == 'gt'){
                minJTypeSelect = '<select><option value="gteq">大于等于</option><option value="gt" selected>大于</option></select>'
            }

            var maxJTypeSelect = '<select><option value="lteq" selected>小于等于</option><option value="lt">小于</option></select>'
            if(maxJType == 'lt'){
                maxJTypeSelect = '<select><option value="lteq">小于等于</option><option value="lt" selected>小于</option></select>'
            }
            // 区间的值
            dynamic_ext_condt_value_html =
                minJTypeSelect + "<input type='text' oninput='checkSpecifyValue(this)' value='"+minValue+"' class='promotion_input' />" +
                maxJTypeSelect + "<input type='text' onblur='checkSpecifyValue(this,\"maxValue\")' value='"+maxValue+"' class='promotion_input'/>之间"
        }

        var doubleUp_checkedValue = ''
        if(doubleUp) doubleUp_checkedValue="checked"// 选中可翻倍
        var specify_attr_value =
            '<p id="specify_attr_value_p" class="para_blcok dynamic_condt dynamic_condt_ext_p">' +
            '<span><b id="specify_attr_name">' + specify_attr_name + '：</b></span>' +
            '<span>' +
            '<select id="moneyOrNumSelect">' +
                moneyOrNumSelect_options +
            '</select>' +
            '<select id="moneyOrNumJtypeSelect" onchange="moneyOrNumJtypeSelect_onchange(this)">' +
            moneyOrNumJtypeSelect_options +
            '</select>' +
            '</span>' +
            '<span id="dynamic_ext_condt_value">' + dynamic_ext_condt_value_html + '</span>' +
            '<span style="display:inline;float: right;margin-right: 32px;">' +
            '<input '+doubleUp_checkedValue+' type="checkbox">可翻倍' +
            '</span>'+
            '</p>';
        content += specify_attr_value;
    }
    $("#" + containerId).html(content);
}
// 检查指定商品属性/指定购物车属性的数量/金额的输入数据
function checkSpecifyValue(obj,isMaxValue){
    var attrType = $("#moneyOrNumSelect").val();
    if(isnull(attrType)){// 没有选择数量或金额
        layer.msg(getMoneyOrNumSelect_alertMsg(), {icon: 2, time: 2000});
        $(obj).val("")
        return
    }

    var inputVal = $(obj).val();
    if(isnull(inputVal)){// 没有填数值
        layer.msg('请填写数值', {icon: 2, time: 2000});
        $(obj).focus()
        $(obj).val("")
        return
    }
    var jType = $("#moneyOrNumJtypeSelect").val()
    var pattern = '', errorMsg = ''
    if(attrType==1){// 数量
        pattern = /^([1-9]\d*)$/;// 要判断它是否是整数
        errorMsg = "数量必须是正整数"
    }else if(attrType==2){// 金额
        pattern = /^(0|([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/; // 要判断它是否是正确的金额数
        errorMsg = "请输入正确的金额数"
    }
    if (!pattern.test(inputVal)) {
        $(obj).val('');
        $(obj).focus()
        layer.msg(errorMsg, {icon: 5, time: 2000});
        return;
    }

    // 区间的额外验证
    if(jType == 'ltv' && !isnull(isMaxValue)){
        var parentObj = $(obj).parent();
        var minValue = $.trim(parentObj.find("input:eq(0)").val());
        if(isnull(minValue)){
            layer.msg("请填写左区间值", {icon: 2, time: 2000});
            parentObj.find("input:eq(0)").val("")
            parentObj.find("input:eq(1)").val("")
            parentObj.find("input:eq(0)").focus()
            return;
        }
        var maxValue = $.trim(parentObj.find("input:eq(1)").val());
        if(isnull(maxValue)){
            layer.msg("请填写右区间值", {icon: 2, time: 2000});
            parentObj.find("input:eq(1)").val("")
            parentObj.find("input:eq(1)").focus()
            return;
        }
        if(parseFloat(maxValue) < parseFloat(minValue)){
            layer.msg("右区间值不能小于左区间值", {icon: 2, time: 2000});
            parentObj.find("input:eq(1)").val("")
            parentObj.find("input:eq(1)").focus()
            return;
        }
        // TODO 是否要判断左右区间的判断类型
        var minJType = parentObj.find("select:eq(0)").val();// 大于等于/大于
        var maxJType = parentObj.find("select:eq(1)").val();// 小于等于
    }

    createDynamicDescription()
}

// 检查是否选择了指定商品属性/指定购物车属性的数量/金额
function checkMoneyOrNumSelect(){
     return isnull($("#moneyOrNumSelect").val());
}
// 获取未选择 指定商品属性/指定购物车属性的数量/金额 的提示信息
function getMoneyOrNumSelect_alertMsg(){
    var moneyOrNumSelectOptionValues = new Array()
    $("#moneyOrNumSelect").find("option").each(function (i, e) {
        if (!isnull($(e).val())) {
            moneyOrNumSelectOptionValues.push($(e).text())
        }
    })
    var name = $("#specify_attr_name").text()
    name = name.substring(0, name.length - 1)
    return "请选择" + name + moneyOrNumSelectOptionValues.join("/")
}
// 选择 指定商品属性/指定购物车属性的数量/金额 的判断类型
function moneyOrNumJtypeSelect_onchange(obj) {
    if (checkMoneyOrNumSelect()) {
        layer.msg(getMoneyOrNumSelect_alertMsg(), {icon: 2, time: 2000});
        $(obj).val("")
        $("#dynamic_ext_condt_value").html("")
        return;
    }
    if ($(obj).val() != 'ltv') {
        $("#dynamic_ext_condt_value").html("<input type='text' oninput='checkSpecifyValue(this)' class='promotion_input' />")
    } else {
        var html = '<select><option value="gteq">大于等于</option><option value="gt">大于</option></select>' +
            "<input type='text' oninput='checkSpecifyValue(this)' class='promotion_input' />" +
            '<select><option value="lteq">小于等于</option><option value="lt">小于</option></select>' +
            "<input type='text' onblur='checkSpecifyValue(this,\"maxValue\")' class='promotion_input' />之间"
        $("#dynamic_ext_condt_value").html(html)
    }

    createDynamicDescription()
}

/**商品分类（真实分类）*/
function product_category_popup(obj) {
    var jtypeSelectValue = $(obj).parent().find("select").find("option:selected").val();
    if (isnull(jtypeSelectValue)) {
        layer.msg("请选择商品分类判断类型", {icon: 2, time: 2000});
        return;
    }
    getProductCategories4Condt();
    layer.open({
        area: ['500', '350px'],
        type: 1,
        shade: false,
        skin: '.pop_style',
        content: $('#product_category_popup_div'),
        title: false, //不显示标题
        btn: ['确认', '取消'],
        yes: function (index) {
            var cnameArray = new Array(), idAndNameArray = new Array();
            $("#product_category_table tbody").find("input[type='checkbox']:checked").each(function (i, e) {
                cnameArray.push($(e).attr("cname"));
                idAndNameArray.push($(e).val() + ":" + $(e).attr("cname"));
            });
            if (idAndNameArray.length != 0) {
                $("#product_category_cnames").prop("title", cnameArray.toString()).val(cnameArray.toString());
                $("#product_category_idAndNames").val(idAndNameArray.join("||"));
            }
            layer.close(index);
        }
    });
}

/**获取指定仓库弹窗*/
function specify_warehouse_popup(obj) {
    var jtypeSelectValue = $(obj).parent().find("select").find("option:selected").val();
    if (isnull(jtypeSelectValue)) {
        layer.msg("请选择指定仓库判断类型", {icon: 2, time: 2000});
        return;
    }
    ajax_get("../inventory/queryWarehouse", null, null,
        function (response) {
            var tbody = $("#specify_warehouse_table tbody");
            tbody.empty();
            if (response.length > 0) {
                for (var i = 0; i < response.length; i++) {
                    var product = response[i];
                    var productHtml = '';
                    productHtml += '<tr>';
                    productHtml += '<td><input warehouseName="' + product.warehouseName + '" warehouseId="' + product.warehouseId + '" type="checkbox"></td>';
                    productHtml += '<td>' + product.warehouseId + '</td>';
                    productHtml += '<td>' + product.warehouseName + '</td>';
                    productHtml += '</tr>';
                    tbody.append(productHtml);
                }
            } else {
                layer.msg("获取仓库数据失败", {icon: 2, time: 2000});
            }
        },
        function (XMLHttpRequest, textStatus) {
            layer.msg("获取仓库数据失败", {icon: 2, time: 2000});
        }
    );
    layer.open({
        area: ['500', '400px'],
        type: 1,
        shade: false,
        skin: '.pop_style',
        content: $('#specify_warehouse_popup_div'),
        title: false, //不显示标题
        btn: ['确认', '取消'],
        yes: function (index) {
            // 选择了仓库，保存起来
            var warehouse_name_array = new Array(), warehouse_id_array = new Array();
            $("#specify_warehouse_table tbody").find("input:checkbox:checked").each(function (i, e) {
                if (!isnull($(e).attr("warehouseName"))) {
                    warehouse_id_array.push($(e).attr("warehouseId"));
                    warehouse_name_array.push($(e).attr("warehouseName"));
                }
            });
            var warehouse_names = warehouse_name_array.toString();
            $("#specify_warehouse_names").attr("title", warehouse_names).val(warehouse_names);
            $("#specify_warehouse_ids").val(warehouse_id_array.toString());
            layer.close(index);
        }
    });
}

/**获取条件指定商品弹窗*/
function specify_product_popup(obj) {
    var jtypeSelectValue = $(obj).parent().find("select").find("option:selected").val();
    if (isnull(jtypeSelectValue)) {
        layer.msg("请选择指定商品判断类型", {icon: 2, time: 2000});
        return;
    }
    cleanSearchProducts();
    layer.open({
        area: ['800', '500px'],
        type: 1,
        shade: false,
        skin: '.pop_style',
        content: $('#specify_product_popup_div'),
        title: false, //不显示标题
        btn: ['确认', '取消'],
        yes: function (index) {
            // 对最终的结果进行验证
            var skus = $("#specify_product_skus").val();
            if (isnull(skus)) {
                layer.msg("您当前选择的指定商品个数为0，请选择赠品", {icon: 2, time: 2000});
            }
            // 清除搜索条件
            $("#specify_product_search_skus").val("");
            createDynamicDescription()
            layer.close(index);
        }
    });

    var specify_product_json = {
        skus_exists: $("#specify_product_skus").val(),
        warehouseIds_exists: $("#specify_product_warehouseIds").val(),
        unitNums_exists: $("#specify_product_unitNums").val(),
        unit: $("#specify_product_unit_select").val()
    };
    // 给弹窗的搜索按钮添加点击事件
    $("body").off("click", "#specify_product_search_btn");
    $("body").on("click", "#specify_product_search_btn", function () {
        searchProducts(null, null, specify_product_json,
            specify_product_callbackAfterSelected
            , specify_product_callbackAfterSelectedAll, specify_product_unit_num_callback);
    });
    searchProducts(null, null, specify_product_json,
        specify_product_callbackAfterSelected
        , specify_product_callbackAfterSelectedAll, specify_product_unit_num_callback);
}
/**
 * 获取商品
 * @param obj
 * @param red
 * @param selected_product_json 原先选中的商品信息
 * @param callbackAfterSelected 选中商品后的回调
 * @param callbackAfterSelectedAll 全选中商品后的回调
 */
function searchProducts(obj, red, selected_product_json,
                        callbackAfterSelected, callbackAfterSelectedAll, unitNumCallback) {
    var currPage = "1";
    var pageSize = 5;
    if (red) {
        currPage = obj.curr;
    }
    var inner_param = {
        "pageSize": pageSize,
        "currPage": currPage,
        "istatus": 1
    };
    var skus_search = $.trim($("#specify_product_search_skus").val());
    if (!isnull(skus_search)) {
        if (skus_search.indexOf(",") == -1) {
            inner_param['title'] = skus_search;
        } else {
            var skusArray = new Array();
            var sku_arr = skus_search.split(",");
            for (var i = 0; i < sku_arr.length; i++) {
                skusArray.push($.trim(sku_arr[i]));
            }
            inner_param['skuList'] = skusArray;
        }
    }
    var param = {"data": inner_param};
    ajax_post("../product/api/getProducts", JSON.stringify(param), "application/json",
        function (response) {
            var data = response.data;
            var totalPage = data.totalPage;
            var currPage = data.currPage;
            var result = data.result;
            var unit_input = '-';
            // 原来就选择的商品，要商品列表里要回显
            // {skus_exists:yyy,warehouseIds_exists:zzz,unitNums_exists:xxx,unit:件/箱}
            var unit = selected_product_json.unit;
            var unitNums_exists = selected_product_json.unitNums_exists;
            var skus_exists = selected_product_json.skus_exists;
            var warehouseIds_exists = selected_product_json.warehouseIds_exists;

            // 根据逗号分割，因为原先可能没有，所以要判断
            var unitNum_exists_Array = new Array();
            if (!isnull(unitNums_exists)) {
                unitNum_exists_Array = unitNums_exists.split(",");
            }
            var sku_exists_Array = new Array();
            if (!isnull(skus_exists)) {
                sku_exists_Array = skus_exists.split(",");
            }
            var warehouseId_exists_Array = new Array();
            if (!isnull(warehouseIds_exists)) {
                warehouseId_exists_Array = warehouseIds_exists.split(",");
            }

            var tbody = $("#specity_product_table tbody");
            tbody.empty();
            if (result.length > 0) {
                var ctitle;
                var ctitle_sub;
                var stock;
                var disabled = "", checked = "";
                for (var i = 0; i < result.length; i++) {
                    var product = result[i];
                    var productHtml = '';
                    productHtml += '<tr>';
                    stock = (isnull(product.stock) || parseInt(product.stock) <= 0) ? 0 : parseInt(product.stock);
                    disabled = "", checked = "";
                    if (stock == 0) {
                        disabled = "disabled";// 库存为0，不能选择
                    }
                    if (unit == '箱') {
                        unit_input = '<input value="" type="text" style="width:30px;height:20px;">';
                    }
                    var resultIndex = containProduct(sku_exists_Array, warehouseId_exists_Array, product.csku, product.warehouseId);
                    if (resultIndex != -1) {
                        checked = "checked";
                        if (unit == '箱') {
                            unit_input = '<input value="' + unitNum_exists_Array[resultIndex] + '" type="text" style="width:30px;height:20px;">';
                        }
                    }
                    productHtml += '<td><input ' + checked + ' onchange="each_product_select(this)" ' +
                        'stock="' + stock + '" ' + disabled + ' ctitle="' + product.ctitle + '" ' +
                        'warehouseId="' + product.warehouseId + '" warehouseName="' + product.warehouseName + '" ' +
                        'sku="' + product.csku + '" categoryId="' + product.categoryId + '" ' +
                        'imgUrl="' + product.imageUrl + '" type="checkbox"></td>';
                    productHtml += '<td>' + product.csku + '</td>';
                    ctitle = product.ctitle;
                    ctitle_sub = product.ctitle;
                    if (ctitle_sub.length > 15) {
                        ctitle_sub = ctitle_sub.substring(0, 15) + "...";
                    }
                    productHtml += '<td title="' + ctitle + '">' + ctitle_sub + '</td>';
                    productHtml += '<td>' + product.warehouseName + '</td>';
                    stock = parseInt(product.stock);
                    if (stock == 0) {
                        productHtml += '<td style="color: red">' + stock + '</td>';
                    } else {
                        productHtml += '<td>' + stock + '</td>';
                    }
                    productHtml += '<td>' + product.cname + '</td>';
                    // 箱规
                    productHtml += '<td name="unitNum_td">' + unit_input + '</td>';
                    productHtml += '</tr>';
                    tbody.append(productHtml);
                }
                // 设置checkbox的onchange事件
                tbody.find("input[type='checkbox']").on("change", function () {
                    each_product_select(this, callbackAfterSelected);
                });
                // 给全选设置回调
                $("#checkbox_specify_product_selectAll").off("change");
                $("#checkbox_specify_product_selectAll").on("change", function () {
                    selectAll(this, callbackAfterSelectedAll);
                });
                // 给箱规设置oninput事件
                tbody.find("input[type='text']").on("input", function () {
                    unitNumCallback.apply(this);
                });
            } else {
                layer.msg("无商品数据", {icon: 5, time: 2000});
            }
            init_specify_product_pagination(totalPage, currPage, selected_product_json
                , callbackAfterSelected, callbackAfterSelectedAll, unitNumCallback);
        },
        function (XMLHttpRequest, textStatus) {
            layer.msg("获取商品数据失败", {icon: 2, time: 2000});
        }
    );
}
function specify_product_unit_num_callback() {
    if (!specify_product_unit_num(this)) {
        layer.msg("请输入正整数", {icon: 5, time: 2000});
        return;
    }

    // 原先选择的商品
    var skus = $("#specify_product_skus").val();
    var warehouseIds = $("#specify_product_warehouseIds").val();
    var warehouseNames = $("#specify_product_warehouseNames").val();
    var cTitles = $("#specify_product_cTitles").val();
    var imgUrls = $("#specify_product_imgUrls").val();
    var unitNums = $("#specify_product_unitNums").val();
    // 分割
    var warehouseId_array = new Array();
    if (!isnull(warehouseIds)) warehouseId_array = warehouseIds.split(",");
    var warehouseName_array = new Array();
    if (!isnull(warehouseNames)) warehouseName_array = warehouseNames.split(",");
    var sku_array = new Array();
    if (!isnull(skus)) sku_array = skus.split(",");
    var cTitle_array = new Array();
    if (!isnull(cTitles)) cTitle_array = cTitles.split("||");
    var imgUrl_array = new Array();
    if (!isnull(imgUrls)) imgUrl_array = imgUrls.split(",");
    var unitNum_array = new Array();
    if (!isnull(unitNums)) unitNum_array = unitNums.split(",");

    // 获取每箱的数量
    var unit = $("#specify_product_unit_select").val();
    if (unit == '箱') {
        var sku = $(this).parent().parent().find("input[type='checkbox']:checked").attr("sku");
        var warehouseId = $(this).parent().parent().find("input[type='checkbox']:checked").attr("warehouseId");
        var foundIndex = -1;
        // 检查此sku在原先选中的skus中的位置
        for (var i = 0, length = sku_array.length; i < length; i++) {
            if (sku_array[i] == sku && warehouseId_array[i] == warehouseId) {
                foundIndex = i;
                break;
            }
        }
        var unitNum = $(this).parent().parent().find('td[name="unitNum_td"]').find("input").val();
        if (foundIndex != -1) {
            unitNum_array[foundIndex] = unitNum;
            $("#specify_product_unitNums").val(unitNum_array.toString());
        }
    }

}
// 指定商品选中商品后的处理
function specify_product_callbackAfterSelected() {
    var checked = $(this).prop("checked");
    // 原先选择的商品
    var skus = $("#specify_product_skus").val();
    var warehouseIds = $("#specify_product_warehouseIds").val();
    var warehouseNames = $("#specify_product_warehouseNames").val();
    var cTitles = $("#specify_product_cTitles").val();
    var imgUrls = $("#specify_product_imgUrls").val();
    var unitNums = $("#specify_product_unitNums").val();
    // 分割
    var warehouseId_array = new Array();
    if (!isnull(warehouseIds)) warehouseId_array = warehouseIds.split(",");
    var warehouseName_array = new Array();
    if (!isnull(warehouseNames)) warehouseName_array = warehouseNames.split(",");
    var sku_array = new Array();
    if (!isnull(skus)) sku_array = skus.split(",");
    var cTitle_array = new Array();
    if (!isnull(cTitles)) cTitle_array = cTitles.split("||");
    var imgUrl_array = new Array();
    if (!isnull(imgUrls)) imgUrl_array = imgUrls.split(",");
    var unitNum_array = new Array();
    if (!isnull(unitNums)) unitNum_array = unitNums.split(",");

    // 获取每箱的数量
    var unit = $("#specify_product_unit_select").val();
    if (unit == '箱') {
        var unitNum = $(this).parent().parent().find('td[name="unitNum_td"]').find("input").val();
        if (isnull(unitNum) || unitNum == '0') {
            layer.msg("请填写箱规，必须是正整数", {icon: 5, time: 2000});
            $(this).prop("checked", false);
            return;
        }
    }

    if (checked) {// 选中的情况
        if (unit == '箱') {
            var unitNum = $(this).parent().parent().find('td[name="unitNum_td"]').find("input").val();
            unitNum_array.push(unitNum);
        } else {
            unitNum_array.push('0');
        }
        sku_array.push($(this).attr("sku"));
        warehouseId_array.push($(this).attr("warehouseId"));
        warehouseName_array.push($(this).attr("warehouseName"));
        cTitle_array.push($(this).attr("ctitle"));
        imgUrl_array.push($(this).attr("imgUrl"));
    } else {// 不选的情况
        var foundIndex = -1;
        var sku = $(this).attr("sku");
        var warehouseId = $(this).attr("warehouseId");
        // 检查此sku在原先选中的skus中的位置
        for (var i = 0, length = sku_array.length; i < length; i++) {
            if (sku_array[i] == sku && warehouseId_array[i] == warehouseId) {
                foundIndex = i;
                break;
            }
        }
        // 进行移除操作
        if (foundIndex != -1) {
            unitNum_array.splice(foundIndex, 1);
            sku_array.splice(foundIndex, 1);
            warehouseId_array.splice(foundIndex, 1);
            warehouseName_array.splice(foundIndex, 1);
            cTitle_array.splice(foundIndex, 1);
            imgUrl_array.splice(foundIndex, 1);
        }
    }

    // 保存最新的
    $("#specify_product_skus").attr("title", sku_array.toString()).val(sku_array.toString());
    $("#specify_product_warehouseIds").val(warehouseId_array.toString());
    $("#specify_product_warehouseNames").val(warehouseName_array.toString());
    $("#specify_product_cTitles").val(cTitle_array.join("||"));
    $("#specify_product_imgUrls").val(imgUrl_array.toString());
    $("#specify_product_unitNums").val(unitNum_array.toString());

}

// 指定商品全选中商品后的处理
function specify_product_callbackAfterSelectedAll() {
    var checked = $(this).prop("checked");
    // 原先选择的商品
    var skus = $("#specify_product_skus").val();
    var warehouseIds = $("#specify_product_warehouseIds").val();
    var warehouseNames = $("#specify_product_warehouseNames").val();
    var cTitles = $("#specify_product_cTitles").val();
    var imgUrls = $("#specify_product_imgUrls").val();
    var unitNums = $("#specify_product_unitNums").val();
    // 分割
    var warehouseId_array = new Array();
    if (!isnull(warehouseIds)) warehouseId_array = warehouseIds.split(",");
    var warehouseName_array = new Array();
    if (!isnull(warehouseNames)) warehouseName_array = warehouseNames.split(",");
    var sku_array = new Array();
    if (!isnull(skus)) sku_array = skus.split(",");
    var cTitle_array = new Array();
    if (!isnull(cTitles)) cTitle_array = cTitles.split("||");
    var imgUrl_array = new Array();
    if (!isnull(imgUrls)) imgUrl_array = imgUrls.split(",");
    var unitNum_array = new Array();
    if (!isnull(unitNums)) unitNum_array = unitNums.split(",");

    // 获取每箱的数量
    var unit = $("#specify_product_unit_select").val();
    if (unit == '箱') {
        var errorText = '';
        $(this).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']:checkbox").each(function (i, e) {
            var unitNum = $(e).parent().parent().find('td[name="unitNum_td"]').find("input").val();
            if (isnull(unitNum) || unitNum == '0') {
                errorText = "请填写箱规，必须是正整数";
                return false;
            }
        });
        if (!isnull(errorText)) {
            $(this).prop("checked", false);
            $(this).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']:checked").each(function (i, e) {
                $(e).prop("checked", false);
            });
            layer.msg(errorText, {icon: 5, time: 2000});
            return;
        }
    }

    if (checked) {// 选中的情况
        $(this).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']:checkbox").each(function (i, e) {
            var foundIndex = -1;
            var sku = $(e).attr("sku");
            var warehouseId = $(e).attr("warehouseId");
            // 检查此sku在原先选中的skus中的位置
            for (var i = 0, length = sku_array.length; i < length; i++) {
                if (sku_array[i] == sku && warehouseId_array[i] == warehouseId) {
                    foundIndex = i;
                    break;
                }
            }
            if (foundIndex == -1) {
                if (unit == '箱') {
                    var unitNum = $(e).parent().parent().find('td[name="unitNum_td"]').find("input").val();
                    unitNum_array.push(unitNum);
                } else {
                    unitNum_array.push('0');
                }
                sku_array.push($(e).attr("sku"));
                warehouseId_array.push($(e).attr("warehouseId"));
                warehouseName_array.push($(e).attr("warehouseName"));
                cTitle_array.push($(e).attr("ctitle"));
                imgUrl_array.push($(e).attr("imgUrl"));
            }
        });
    } else {// 不选的情况
        $(this).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']").each(function (i, e) {
            var foundIndex = -1;
            var sku = $(e).attr("sku");
            var warehouseId = $(e).attr("warehouseId");
            // 检查此sku在原先选中的skus中的位置
            for (var i = 0, length = sku_array.length; i < length; i++) {
                if (sku_array[i] == sku && warehouseId_array[i] == warehouseId) {
                    foundIndex = i;
                    break;
                }
            }
            // 进行移除操作
            if (foundIndex != -1) {
                unitNum_array.splice(foundIndex, 1);
                sku_array.splice(foundIndex, 1);
                warehouseId_array.splice(foundIndex, 1);
                warehouseName_array.splice(foundIndex, 1);
                cTitle_array.splice(foundIndex, 1);
                imgUrl_array.splice(foundIndex, 1);
            }
        });
    }

    // 保存最新的
    $("#specify_product_skus").attr("title", sku_array.toString()).val(sku_array.toString());
    $("#specify_product_warehouseIds").val(warehouseId_array.toString());
    $("#specify_product_warehouseNames").val(warehouseName_array.toString());
    $("#specify_product_cTitles").val(cTitle_array.join("||"));
    $("#specify_product_imgUrls").val(imgUrl_array.toString());
    $("#specify_product_unitNums").val(unitNum_array.toString());

}
// 全选
function selectAll(obj, callback) {
    var checked = $(obj).prop("checked");
    $("#specity_product_table").find("tbody").find("input[type='checkbox'][stock!='0']").each(function (i, e) {
        $(e).prop("checked", checked);
    });
    // 选择后的回调
    if (callback != undefined) {
        callback.apply(obj);
    }
}
// 单选商品
function each_product_select(obj, callback) {
    var checked = $(obj).prop("checked");
    if (!checked) {
        $("#specity_product_table").find("thead").find("input[type='checkbox']").prop("checked", false);
    }
    var size = $("#specity_product_table").find("tbody").find("input[type='checkbox'][stock!='0']").size();
    var checked_size = $("#specity_product_table").find("tbody").find("input[type='checkbox'][stock!='0']:checked").size();
    if (size == checked_size) {
        $("#specity_product_table").find("thead").find("input[type='checkbox']").prop("checked", true);
    }
    // 选择后的回调
    if (callback != undefined) {
        callback.apply(obj);
    }
}

/**
 * 是否包含此商品
 * @param sku_exists_Array  原来就有的sku数组
 * @param warehouseId_exists_Array  原来就有的仓库id数组
 * @param sku  选择的sku
 * @param warehouseId 选择的仓库id
 * @returns {number} 返回脚标，不存在返回-1
 */
function containProduct(sku_exists_Array, warehouseId_exists_Array, sku, warehouseId) {
    if (sku_exists_Array.length == 0) {
        return -1;
    }
    for (var i = 0; i < sku_exists_Array.length; i++) {
        var skuEquals = sku_exists_Array[i] == sku;
        var warehouseIdEquals = warehouseId_exists_Array[i] == warehouseId;
        if (skuEquals && warehouseIdEquals) {
            return i;
        }
    }
    return -1;
}
//初始化商品分页栏
function init_specify_product_pagination(pages, currPage, selected_product_json,
                                         callbackAfterSelected, callbackAfterSelectedAll, unitNumCallback) {
    if ($("#specify_product_pagination")[0] != undefined) {
        $("#specify_product_pagination").empty();
        laypage({
            cont: 'specify_product_pagination',
            pages: pages,
            curr: currPage,
            groups: 5,
            skin: '#55ccc8',
            first: '首页',
            last: '尾页',
            prev: '上一页',
            next: '下一页',
            skip: true,
            jump: function (obj, first) {
                //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                if (!first) {
                    searchProducts(obj, true, selected_product_json,
                        callbackAfterSelected, callbackAfterSelectedAll, unitNumCallback);
                }
            }
        });
    }
}

/**获取运送地区弹窗*/
function searchShippingRegion(obj) {
    var jtypeSelectValue = $(obj).parent().find("select").find("option:selected").val();
    if (isnull(jtypeSelectValue)) {
        layer.msg("请选择运送地区判断类型", {icon: 2, time: 2000});
        return;
    }
    layer.open({
        area: ['350px', '100px'],
        type: 1,
        shade: false,
        skin: '.pop_style',
        content: $('#region_popup_div'),
        title: false, //不显示标题
        btn: ['确认', '取消'],
        yes: function (index) {
            // 选择了城市，保存起来
            var shipping_region_id = $("#city_select").find("option:selected").val();
            var shipping_region_name = $("#city_select").find("option:selected").text();
            $("#shipping_region_ids").val(shipping_region_id);
            $("#shipping_region_names").attr("title", shipping_region_name).val(shipping_region_name);
            layer.close(index);
        }
    });
    $.ajax({
        url: "../member/getprovs",
        type: "GET",
        dataType: "json",
        success: function (data) {
            var options = "<option>请选择</option>";
            for (var i in data) {
                options += "<option value='" + data[i].id + "'>" + data[i].provinceName + "</option>";
            }
            $("#province_select").html(options);
        }
    });
}

/**选择省份后联动城市数据*/
function getCitiesByProvinceId(obj) {
    var provinceId = $(obj).val();
    ajax_get("../member/getcities?proId=" + provinceId, null, null,
        function (response) {
            var options = "<option>请选择</option>";
            var cities = response.cities;
            for (var i in cities) {
                options += "<option value='" + cities[i].id + "'>" + cities[i].cityName + "</option>";
            }
            $("#city_select").html(options);
        },
        function (XMLHttpRequest, textStatus) {
            layer.msg("获取城市数据失败", {icon: 2, time: 2000});
        }
    );
}

// 箱规，数量输入控制
function specify_product_unit_num(obj) {
    var num = $.trim($(obj).val());
    if (isnull(num)) {
        $(obj).val("");
        return false;
    }
    var pattern = /^([1-9]\d*)$/;// 正整数
    if (!pattern.test(num)) {
        $(obj).val(num.substring(0, num.length - 1));
        return false;
    }
    return true;
}

// 箱规选择
function specify_product_unit_select_onchange(obj) {
    $("#input_num_each_box_extension").text($(obj).val());
    createDynamicDescription()
}

/**指定商品select事件*/
function specify_product_select_onchange(obj) {
    var jType = $(obj).find("option:selected").attr("jType");
    if (jType == 'n') {
        // 选择非的话，商品数量不要填写
        $("#specify_product_unit_span").hide();
    } else {
        $("#specify_product_unit_span").show();
    }
    createDynamicDescription()
}

/**选择区间，变换input*/
function shopping_cart_total_weight_span(obj) {
    var jType = $(obj).find("option:selected").attr("jType");
    if (jType == 'ltv') {
        $("#shopping_cart_total_weight_span").html('<b>总重量范围区间：</b>' +
            '<input class="promotion_input" type="text" >至' +
            '<input class="promotion_input" type="text" >kg');
    } else {
        $("#shopping_cart_total_weight_span").html('<input type="text" ' +
            'class="promotion_input" >kg');
    }
    createDynamicDescription()
}

/**选择区间，变换input*/
function subtotal_span(obj) {
    var jType = $(obj).find("option:selected").attr("jType");
    if (jType == 'ltv') {
        $("#subtotal_span").html('<b>购物车小计金额范围区间：</b>' +
            '<input type="text" class="promotion_input" >至' +
            '<input type="text" class="promotion_input">');
        $("#subtotal_doubleUp").parent().hide();// 区间没有翻倍
    } else {
        $("#subtotal_span").html('<input type="text" class="promotion_input">');
        $("#subtotal_doubleUp").parent().show();
    }
    createDynamicDescription()
}

/**
 * 创建条件判断类型下拉列表
 * @param condtJgmntTypeList 判断类型列表
 * @param condtJgmntType 原有的判断类型
 * @returns {string}
 */
function createJgmntTypeOptions(condtJgmntTypeList, condtJgmntType) {
    var options = '<option value="" jType="">请选择</option>';
    if (!isnull(condtJgmntTypeList)) {
        var jType;
        for (var j in condtJgmntTypeList) {
            jType = condtJgmntTypeList[j].jType;
            //大于=gt, 小于=lt, 大于等于=gteq, 小于等于=lteq, 是=y, 非=n, 区间=ltv;
            if (!isnull(condtJgmntType) && (jType == condtJgmntType)) {
                options += '<option selected="selected" value="' + condtJgmntTypeList[j].id + '" jType="' + jType + '">' + condtJgmntTypeList[j].name + '</option>';
            } else {
                options += '<option value="' + condtJgmntTypeList[j].id + '" jType="' + jType + '">' + condtJgmntTypeList[j].name + '</option>';
            }
        }
    }
    return options;
}
