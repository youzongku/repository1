/**
 * 获取属性
 * @param callback
 */
function getAttrs(callback){
    $.ajax({
        url: "../market/pro/load/condt/attrs",
        type: "GET",
        dataType: "json",
        success: function (data) {
            if(!isnull(callback)){
                callback.call(this,data);
            }
        }
    });
}

/**
 * 时间格式化
 * @param time
 * @returns {string}
 */
function formateDate(time) {
    if(isnull(time)){
        return;
    }
    var dates = new Date(time);
    var years = dates.getFullYear();
    var months = dates.getMonth() + 1;
    var days = dates.getDate();
    var hours = dates.getHours();
    var mins = dates.getMinutes();
    var secs = dates.getSeconds();
    if (months < 10) {
        months = "0" + months;
    }
    if (days < 10) {
        days = "0" + days;
    }
    if (hours < 10) {
        hours = "0" + hours;
    }
    if (mins < 10) {
        mins = "0" + mins;
    }
    if (secs < 10) {
        secs = "0" + secs;
    }
    return years + "-" + months + "-" + days + " " + hours + ":" + mins + ":" + secs;
}
/**
 * 获取属性对应的字符串
 * @param attr
 * @returns {*}
 */
function getAttrStr(attr){
    if(isnull(attr)){
        return "未知属性";
    }
    var attrStr;
    switch (attr){
        case ATTR_GOODS:
            attrStr = "商品属性";
            break;
        case ATTR_SHOPPING_CART:
            attrStr = "购物车属性";
            break;
        case ATTR_USER:
            attrStr = "用户属性";
            break;
        default:
            attrStr = "未知属性";
            break;
    }
    return attrStr;
}
/**
 * 判断data是否为null/空字符串/undefined
 * @param data
 * @returns {Boolean}
 */
function isnull(data) {
    if (data == null || data == undefined || data == "") {
        return true;
    } else {
        return false;
    }
}

/**
 * 进入促销活动列表，菜单样式也要改变
 */
function gotoProActList(){
    var pro_act_list_menu = $('p[class="back-current"]').parent().find("p:eq(0)");
    $("p[onclick^='load_menu_content']").removeClass("back-current");
    pro_act_list_menu.addClass("back-current");
    load_menu_content(20);// 进入列表页
}
/**
 * 返回到列表
 */
$("body").on("click",".promotion_back",function(){
    gotoProActList();
});
$("body").on("click",".cancel_promotion",function(){
    layer.confirm('确认取消创建促销？', {btn: ['确认取消','继续创建']}, function(){
        layer.msg('即将跳转到促销一览表', {icon: 1,time:2000},function(){
            gotoProActList();
        });
    }, function(){
    });
});

function cleanSearchProducts(){
    $("#specify_product_search_skus").val("");
    $("#specity_product_table tbody").empty();
    $("#checkbox_specify_product_selectAll").prop("checked",false);
}

// 是否包含电商模式
function containsEcommerceMode(){
    var modeIds = $("#mode_ids_hidden").val();
    return containsMode(modeIds.split(","),"1")
}

/**
 * 是否包含指定的模式
 * @param modeArray 选择模式列表
 * @param mode 被包含的mode
 * @returns {boolean} true包含，false不包含
 */
function containsMode(modeArray, mode){
    if(modeArray==undefined) return false;
    for(var i in modeArray){
        if(modeArray[i] == mode){
            return true;
        }
    }
    return false;
}

function red(text){
    return "<b style='color: red'>"+text+"</b>"
}

/**点击条件：商品分裂搜索按钮，查询商品分类信息*/
function getProductCategories4Condt(){
    ajax_get("../product/api/realCateQuery?level=1", null, null,
        function(response) {
            var tbody = $("#product_category_table tbody");
            tbody.empty();
            if(response.length>0){
                // 原先的数据处理
                var idAndNames = $("#product_category_idAndNames").val();
                var idAndNameJson = {};
                if(!isnull(idAndNames)){
                    var idAndNameArr = idAndNames.split("||");
                    for(var i=0;i<idAndNameArr.length;i++){
                        var idAndName = idAndNameArr[i].split(":");
                        idAndNameJson[idAndName[0]+""]=idAndName[1];
                    }
                }
                var length = response.length;
                var productCHtml = "";
                for(var i = 0;i < length;i++ ){
                    var product_c_json = response[i];
                    if(!isnull(idAndNameJson[product_c_json["iid"]])){
                        // 原先选中的要回显
                        productCHtml += '<td><input onchange="selectOneProductCategory(this)" value="'+product_c_json["iid"]+'" cname="'+product_c_json["cname"]+'" checked="checked" type="checkbox">'+product_c_json["cname"]+'</td>';
                    }else{
                        productCHtml += '<td><input onchange="selectOneProductCategory(this)" value="'+product_c_json["iid"]+'" cname="'+product_c_json["cname"]+'" type="checkbox">'+product_c_json["cname"]+'</td>';
                    }
                    // 3个分类作为一行
                    if( ((i+1)%3)==0 || i==(length-1) ){
                        tbody.append("<tr>"+productCHtml+"</tr>");
                        productCHtml = "";
                    }
                }
                checkSelectAllProductCategories();
            }else{
                layer.msg("获取商品分类数据失败",{icon:2,time:2000});
            }
        },
        function(XMLHttpRequest, textStatus) {
            layer.msg("获取商品分类数据失败",{icon:2,time:2000});
        }
    );
}

// 判断单选是否导致全选了
function checkSelectAllProductCategories(){
    var tbody = $("#product_category_table tbody");
    var checkbox_size = tbody.find("input[type='checkbox']").size();
    var checked_size = tbody.find("input[type='checkbox']:checked").size();
    if(checkbox_size!=checked_size){
        $("#checkbox_selectAllProductCategory").prop("checked",false);
        return;
    }
    $("#checkbox_selectAllProductCategory").prop("checked",true);
}
/** 全选商品分类*/
function selectAllProductCategory(obj){
    var checked = $(obj).prop("checked");
    $("#product_category_table tbody").find("input[type='checkbox']").each(function(i,e){
        $(e).prop("checked",checked);
    });
}
/** 选中一个商品分类*/
function selectOneProductCategory(obj){
    var checked = $(obj).prop("checked");
    if(!checked){
        $("#checkbox_selectAllProductCategory").prop("checked",false);
        return;
    }
    checkSelectAllProductCategories();
}

//优先级的疑问解释
$('body').on("mouseover", ".question_first", function () {
    layer.open({
        area: ['350px', '200px'],
        type: 1,
        shade: false,
        skin: '.pop_style',
        content: $('.question_box'),
        time: 8000,
        title: false //不显示标题
    });
});
// 全选
function selectAllProActis(obj){
    var checked = $(obj).prop("checked")
    $(obj).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']").each(function(i,e){
        $(e).prop("checked",checked)
    })
}
/** 选中一个促销模板*/
function selectOneProActi(obj){
    var checked = $(obj).prop("checked");
    if(!checked){
        $("#checkbox_selectAllProActis").prop("checked",false);
        return;
    }
    checkSelectAllProActis();
}

// 判断单选是否导致全选了
function checkSelectAllProActis(){
    var tbody = $("#pro_act_pro_type_table tbody");
    var checkbox_size = tbody.find("input[type='checkbox']").size();
    var checked_size = tbody.find("input[type='checkbox']:checked").size();
    if(checkbox_size!=checked_size){
        $("#checkbox_selectAllProActis").prop("checked",false);
        return;
    }
    $("#checkbox_selectAllProActis").prop("checked",true);
}

// 获取促销活动的已添加促销类型（活动实例）
function loadProActi(proActId){
    if(isnull(proActId)){
        proActId = $("#pro_act_id").val();
    }
    var param = {
        proActId:proActId // 促销活动id
    };
    ajax_post("../market/pro/act/alldetail", JSON.stringify(param), "application/json",
        function(response) {
            var tbody = $("#pro_act_pro_type_table tbody");
            tbody.empty();
            $("#pro_type_added_count").html("0");
            var pro_type_added_count = 0;
            var seq = 0;
            var fullActInstDtoList = response.fullActInstDtoList  // 活动实例

            for(var i = 0;i < fullActInstDtoList.length;i++ ){
                seq = seq+1;
                pro_type_added_count = pro_type_added_count+1
                var fullActInstDto = fullActInstDtoList[i];
                var fullCondtInstDtoList = fullActInstDto.fullCondtInstDtoList

                var actInstAndProTypeHtml = '<tr>';

                for(var j = 0;j < fullCondtInstDtoList.length;j++){

                    if(j==0){
                        var rowCount = fullCondtInstDtoList.length;
                        actInstAndProTypeHtml += '<td rowspan="'+rowCount+'"><input onchange="selectOneProActi(this)" actInstId="'+fullActInstDto.id+'" type="checkbox"></td>';
                        actInstAndProTypeHtml += '<td rowspan="'+rowCount+'">'+seq+'</td>';
                        actInstAndProTypeHtml += '<td rowspan="'+rowCount+'">' + fullActInstDto.name + '</td>';
                        var description = '';
                        // if(!isnull(fullActInstDto.description)){
                        //     var arr = fullActInstDto.description.split("=");
                        //     description += '<p>条件：'+arr[0]+'</p>';
                        //     description += '<p>优惠：'+arr[1]+'</p>';
                        // }
                        actInstAndProTypeHtml += '<td class="pro_intro" rowspan="'+rowCount+'">'+description+'</td>';
                        actInstAndProTypeHtml += '<td rowspan="'+rowCount+'">'+getAttrStr(fullActInstDto.attr+'')+'</td>';
                    }

                    var fullCondtInstDto = fullCondtInstDtoList[j]
                    actInstAndProTypeHtml += '<td class="pro_intro">' + fullCondtInstDto.name + '</td>';
                    var isSetV_flag = (fullCondtInstDto.isSetV == 1); // 是否设置了参数
                    if(isSetV_flag){
                        actInstAndProTypeHtml += '<td class="pro_intro">已设置</td>';
                    }else{
                        actInstAndProTypeHtml += '<td class="pro_intro" style="color:red">未设置</td>';
                    }
                    // 优先级设置
                    actInstAndProTypeHtml += '<td class="pro_intro" style="text-align: center">' +
                        '<input style="width: 60px" ' +
                        'oldPriority="'+fullCondtInstDto.priority+'" newPriority="'+fullCondtInstDto.priority+'" ' +
                        'onchange="savePriority(this)" oninput="changePriority(this)" ' +
                        'value="'+fullCondtInstDto.priority+'" condtInstId="'+fullCondtInstDto.id+'">' +
                        '</td>';
                    actInstAndProTypeHtml += '<td class="select_operate">';
                    actInstAndProTypeHtml += '<span condtInstId="'+fullCondtInstDto.id+'" class="set_para set_dynamic_parameter">设置参数</span>';
                    // if(fullCondtInstDto.cType==1 || fullCondtInstDto.cType==2 || fullCondtInstDto.cType==6){
                    //     actInstAndProTypeHtml += '<span condtInstId="'+fullCondtInstDto.id+'" class="set_para set_dynamic_parameter">添加阶梯条件</span>';
                    // }
                    actInstAndProTypeHtml += '</td>';

                    actInstAndProTypeHtml += '</tr>';
                }

                tbody.append(actInstAndProTypeHtml);
            }

            $("#pro_type_added_count").html(pro_type_added_count);// 已添加的促销类型
        },
        function(XMLHttpRequest, textStatus) {
            layer.msg("获取促销类型数据失败",{icon:5,time:2000});
        }
    );
}

// 删除活动实例
$("body").on("click", ".cate_del_all", function () {
    var actInstIdArray = new Array();
    $("#pro_act_pro_type_table").find("input:checkbox:checked").each(function (i, e) {
        if (!isnull($(e).attr("actInstId"))) {
            actInstIdArray.push($(e).attr("actInstId"));
        }
    });
    if (actInstIdArray.length == 0) {
        layer.msg("请选择要删除的促销类型", {icon: 2, time: 2000});
        return;
    }

    var param = {actInstIds: actInstIdArray.toString()};
    ajax_post("../market/pro/actinst/rms", JSON.stringify(param), "application/json",
        function (response) {
            layer.msg("删除成功", {icon: 1, time: 2000}, function () {
                $("#pro_type_added_count").html(parseInt($("#pro_type_added_count").html()) - actInstIdArray.length);
                loadProActi($("#pro_act_id").val());
            });
        },
        function (XMLHttpRequest, textStatus) {
            layer.msg("获取促销类型数据失败", {icon: 5, time: 2000});
        }
    );
});

// 保存优先级
function savePriority(obj){
    var inpVal = $(obj).val();
    if(!isNaN(inpVal) && parseInt(inpVal)>0){
        var oldPriority = $(obj).attr("oldPriority");
        var newPriority = $(obj).attr("newPriority");
        if(oldPriority != newPriority){
            // 两者不一样，才是修改了
            var param = {
                "condtInstId":$(obj).attr("condtInstId"),
                "priority":newPriority
            };
            ajax_post("../market/pro/actinst/condtinst/priority/update", JSON.stringify(param), "application/json",
                function(response) {
                    if(!response.success){
                        layer.msg("修改优先级失败",{icon:5,time:2000});
                        return;
                    }
                    layer.msg("修改优先级成功",{icon:1,time:2000});
                    $(obj).attr("oldPriority",newPriority);
                },
                function(XMLHttpRequest, textStatus) {
                    layer.msg("修改优先级失败",{icon:5,time:2000});
                }
            );
        }
    }
}

// 验证修改的优先级
function changePriority(obj){
    var inpVal = $(obj).val();
    if(!isNaN(inpVal) && parseInt(inpVal)>0){
        $(obj).attr("newPriority",inpVal);
        //layer.msg(inpVal,{icon:5,time:2000});
    }else{
        $(obj).val("");
        $(obj).attr("newPriority",$(obj).attr("oldPriority"));
        layer.msg("请输入正整数",{icon:5,time:2000});
    }
}

/**
 * 动态设置参数，供新增和编辑使用
 */
function doSetDynamicCondtsPvlgValue(condtInstId){
    // 获取促销活动实例数据
    var param = {condtInstId: condtInstId};
    ajax_post("../market/pro/actinst/condtinst/detail", JSON.stringify(param), "application/json",
        function (response) {
            var fullCondtInstDto = response; // 条件实例
            var fullPvlgInstDto = fullCondtInstDto.fullPvlgInstDto; // 优惠实例
            // $("#act_inst_name").html(actInst.name);// 活动实例名称
            // $("#act_inst_id").val(actInst.id);// 活动实例id
            $("#dynamic_pro_inst_pvlg_desc_div").html("");
            // 动态创建条件
            createDynamicCondts("dynamic_pro_inst_condition_div", fullCondtInstDto);

            // 后台添加促销活动，选择四种模式同时进行。后台应该不可以勾选可组合
            // 电商模式没有可组合
            var mode_ids = $("#mode_ids_hidden").val()
            if((mode_ids.split(",").length == 4) || containsEcommerceMode()) {
                $("#dynamic_pro_inst_condition_div").find("input[ name='combined_checkbox']").prop("checked",false);
                $("#dynamic_pro_inst_condition_div").find("input[ name='combined_checkbox']").parent().hide()
            }

            // 动态创建优惠
            createDynamicPvlgs("dynamic_pro_inst_pvlg_div", fullPvlgInstDto);

            // 如果是购物车多条件的情况下是没有可阶梯&可翻倍的
            // if(actInst.attr == 2 && condtInstDtoList.length>1){
            //     $("#stepped_checkbox").parent().hide()
            //     $("#subtotal_doubleUp").parent().remove()
            //     $("#total_count_doubleUp").parent().remove()
            // }

            createDynamicDescription('dynamic_pro_inst_pvlg_desc_div',fullCondtInstDto);
        },
        function (XMLHttpRequest, textStatus) {
            layer.msg("获取数据失败", {icon: 5, time: 2000});
        }
    );

    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['900px', '450px'],   // 900 550
        content: $('.dynamic_pro_inst_condition_privilege_parameter_box'),
        btn: ['保存参数设置', '取消'],
        scrollbar: false,
        title: false,
        yes: function (index) {
            // 条件实例值数组
            var condtValueDatas = {};
            // 获取所有的条件所在的<p>标签
            var errorText = "";
            $(".dynamic_condt_p").each(function (i, e) {
                var condtInstId = $(e).attr("condtInstId");// 条件实例id
                var cType = parseInt($(e).attr("c_type"));// 标明是哪个条件
                var jdmntTypeId = $(e).find("select[name='jType_select']").val();// 判断类型id
                var jType = $(e).find("select[name='jType_select']").find("option:selected").attr("jType");// 判断类型：lt,gt,...

                var eachCondtValueData = {};// 每个条件实例的具体值
                eachCondtValueData["condtInstId"] = condtInstId;
                eachCondtValueData["jType"] = jType;
                eachCondtValueData["jdmntTypeId"] = jdmntTypeId;
                switch (cType) {
                    case 1:// 商品分类 cType,jType,categoryIds,categoryNames
                        if (isnull(jdmntTypeId)) {
                            errorText = "请选择商品分类判断类型";
                            return false;
                        }
                        var categoryNames = $(e).find("input:eq(0)").val();
                        if (isnull(categoryNames)) {
                            errorText = "请选择商品分类";
                            return false;
                        }
                        // productCategories:[{"id":1,"name":零食},{"id":2,"name":母婴}]
                        var pc_array = new Array();
                        var idAndNameArr = $(e).find("input:eq(1)").val().split("||");// 这个是隐藏域
                        for (var i = 0; i < idAndNameArr.length; i++) {
                            var json = {};
                            var idAndName = idAndNameArr[i].split(":");
                            json['id'] = idAndName[0];
                            json['name'] = idAndName[1];
                            pc_array.push(json);
                        }
                        eachCondtValueData['combined'] = $(e).find("input[name='combined_checkbox'][type='checkbox']").prop("checked");
                        eachCondtValueData["productCategories"] = pc_array;
                        break;
                    case 2:// 指定商品 cType,jType,skus,num
                        if (isnull(jdmntTypeId)) {
                            errorText = "请选择指定商品判断类型";
                            return false;
                        }
                        var skus = $(e).find("input:eq(0)").val();
                        if (isnull(skus)) {
                            errorText = "请选择商品";
                            return false;
                        }
                        // 箱规
                        var unit = $("#specify_product_unit_select").val();
                        // specifyProductList:[{sku:xxx,warehouseId:yyyy,warehouseName:yyyy,cTitle:zzzz,imgUrl:xxx}]
                        var skuArray = skus.split(",");
                        var warehouseIdArray = $(e).find("input:eq(1)").val().split(",");
                        var warehouseNameArray = $(e).find("input:eq(2)").val().split(",");
                        var cTitleArray = $(e).find("input:eq(3)").val().split("||");
                        var imgUrlArray = $(e).find("input:eq(4)").val().split(",");
                        var unitNumArray = $(e).find("input:eq(5)").val().split(",");

                        if (jType == 'y') {// 指定商品选择“是”的时候，有数量
                            // 检查，为箱的情况下，箱规不能为0
                            if (unit == '箱') {
                                for (var i = 0; i < unitNumArray.length; i++) {
                                    if (unitNumArray[i] == '0') {
                                        errorText = "选择的指定商品有箱规为0，请重新填写";
                                        return false;
                                    }
                                }
                            }

                            eachCondtValueData["unit"] = unit;
                            // 是否组合
                            // 电商模式没有可组合
                            // if(containsEcommerceMode()){
                            //     eachCondtValueData["combined"] = false;
                            // }else{
                            //     eachCondtValueData['combined'] = $(e).find("input[name='combined_checkbox'][type='checkbox']").prop("checked");
                            // }
                        }
                        eachCondtValueData['combined'] = $(e).find("input[name='combined_checkbox'][type='checkbox']").prop("checked");

                        var specifyProductArray = new Array();
                        for (var i = 0; i < skuArray.length; i++) {
                            var sku_warehouseId_cTitle_json = {};
                            sku_warehouseId_cTitle_json["sku"] = skuArray[i];
                            sku_warehouseId_cTitle_json["warehouseId"] = warehouseIdArray[i];
                            sku_warehouseId_cTitle_json["warehouseName"] = warehouseNameArray[i];
                            sku_warehouseId_cTitle_json["cTitle"] = cTitleArray[i];
                            sku_warehouseId_cTitle_json["imgUrl"] = imgUrlArray[i];
                            sku_warehouseId_cTitle_json["unit"] = unit;
                            if (unit == '箱') {
                                sku_warehouseId_cTitle_json["unitNum"] = unitNumArray[i];// 每箱多少，如果是件，则为0
                            } else {
                                sku_warehouseId_cTitle_json["unitNum"] = 0;// 每箱多少，如果是件，则为0
                            }
                            specifyProductArray.push(sku_warehouseId_cTitle_json);
                        }
                        eachCondtValueData["specifyProductList"] = specifyProductArray;
                        break;
                    case 3:// 指定仓库 cType,jType,warehouseIds,warehouseNames
                        if (isnull(jdmntTypeId)) {
                            errorText = "请选择指定仓库判断类型";
                            return false;
                        }
                        var warehouseNames = $(e).find("input:eq(0)").val();
                        if (isnull(warehouseNames)) {
                            errorText = "请选择仓库";
                            return false;
                        }
                        eachCondtValueData["warehouseIds"] = $(e).find("input:eq(1)").val();// 这个是隐藏域
                        eachCondtValueData["warehouseNames"] = warehouseNames;
                        break;
                    case 4:// 商品类型
                        if (isnull(jdmntTypeId)) {
                            errorText = "请选择商品类型判断类型";
                            return false;
                        }
                        var productType = $(e).find("select:eq(1)").val();
                        if (isnull(productType)) {
                            errorText = "请选择商品类型";
                            return false;
                        }
                        eachCondtValueData["productType"] = productType;
                        break;
                    case 5:// 小计金额 cType,jType,minPrice,maxPrice
                        if (isnull(jdmntTypeId)) {
                            errorText = "请选择小计金额判断类型";
                            return false;
                        }
                        var minPrice = $.trim($(e).find("input:eq(0)").val());
                        if (isnull(minPrice)) {
                            errorText = "请填写金额";
                            return false;
                        }
                        // 金额正则
                        var pattern = /^(0|([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
                        if (!pattern.test(minPrice)) {
                            errorText = "请输入正确的金额";
                            $(e).find("input:eq(0)").val(minPrice.substring(0, minPrice.length - 1));
                            return false;
                        }
                        eachCondtValueData["minPrice"] = minPrice;
                        if (jType == 'ltv') {// 区间，有两个input的，其他的都是一个input
                            var maxPrice = $.trim($(e).find("input:eq(1)").val());
                            if (isnull(maxPrice)) {
                                errorText = "请填写金额";
                                return false;
                            }
                            if (!pattern.test(maxPrice)) {
                                errorText = "请输入正确的金额";
                                $(e).find("input:eq(1)").val(maxPrice.substring(0, maxPrice.length - 1));
                                return false;
                            }
                            if (parseFloat(maxPrice) <= parseFloat(minPrice)) {
                                errorText = "小计金额：大金额不能小于或等于小金额";
                                $(e).find("input:eq(1)").val("");
                                return false;
                            }
                            eachCondtValueData["maxPrice"] = maxPrice;
                        }
                        break;
                    case 6:// 总商品数量
                        if (isnull(jdmntTypeId)) {
                            errorText = "请选择总商品数量判断类型";
                            return false;
                        }
                        var productTotalCount = $.trim($(e).find("input:eq(0)").val());
                        if (isnull(productTotalCount)) {
                            errorText = "请填写总商品数量";
                            return false;
                        }
                        eachCondtValueData["productTotalCount"] = productTotalCount;
                        break;
                    case 7:// 购物车总重量 cType,jType,min,max
                        if (isnull(jdmntTypeId)) {
                            errorText = "请选择总重量判断类型";
                            return false;
                        }
                        var minWeight = $.trim($(e).find("input:eq(0)").val());
                        if (isnull(minWeight)) {
                            errorText = "请填写重量";
                            return false;
                        }
                        eachCondtValueData["minWeight"] = minWeight;
                        if (jType == 'ltv') {// 区间，有两个input的，其他的都是一个input
                            var maxWeight = $.trim($(e).find("input:eq(1)").val());
                            if (isnull(maxWeight)) {
                                errorText = '请填写重量';
                                return false;
                            }
                            eachCondtValueData["maxWeight"] = maxWeight;
                        }
                        break;
                    case 8:// 运送地区 cType,jType,cityIds,cityNames
                        if (isnull(jdmntTypeId)) {
                            errorText = "请选择运送地区判断类型";
                            return false;
                        }
                        var cityNames = $(e).find("input:eq(0)").val();
                        if (isnull(cityNames)) {
                            errorText = "请选择运送地区";
                            return false;
                        }
                        eachCondtValueData["cityIds"] = $(e).find("input:eq(1)").val();// 这个是隐藏域
                        eachCondtValueData["cityNames"] = cityNames;
                        break;
                    default:
                        break;
                }
                condtValueDatas[cType] = eachCondtValueData;
            });
            if (!isnull(errorText)) {
                layer.msg(errorText, {icon: 2, time: 2000});
                return;
            }

            // 指定商品/购物车属性
            var specifyAttrValueDatas = {}
            specifyAttrValueDatas["condtInstExtId"] = $("#condtInstExtId_hidden").val() // 额外条件的id
            var stepped = $("#stepped_checkbox").prop("checked"); // 可阶梯
            specifyAttrValueDatas["stepped"] = stepped
            if ($("#specify_attr_value_p").find("input[type='checkbox']").prop("checked") != undefined) {// 商品属性的可翻倍
                specifyAttrValueDatas["doubleUp"] = $("#specify_attr_value_p").find("input[type='checkbox']").prop("checked")// 可翻倍
            } else if ($("#subtotal_doubleUp").prop("checked") != undefined) {// 购物车小计金额的可翻倍
                specifyAttrValueDatas["doubleUp"] = $("#subtotal_doubleUp").prop("checked")
            } else if ($("#total_count_doubleUp").prop("checked") != undefined) {// 购物车商品总数量的可翻倍
                specifyAttrValueDatas["doubleUp"] = $("#total_count_doubleUp").prop("checked")
            } else {
                specifyAttrValueDatas["doubleUp"] = false
            }

            if($("#specify_attr_value_p")[0] != undefined) {
                var attrType = $("#specify_attr_value_p").find("select:eq(0)").val();
                if (isnull(attrType)) {
                    layer.msg('请选择指定属性', {icon: 2, time: 2000});
                    return;
                }

                if ($("#specify_attr_value_p") != undefined) {
                    var attrTypeJType = $("#specify_attr_value_p").find("select:eq(1)").val();
                    if (isnull(attrTypeJType)) {
                        layer.msg('请选择指定属性的判断类型', {icon: 2, time: 2000});
                        return;
                    }
                    specifyAttrValueDatas['attrType'] = attrType
                    specifyAttrValueDatas['jType'] = attrTypeJType
                    if (attrTypeJType != 'ltv') {
                        var singleVal = $("#dynamic_ext_condt_value").find("input:eq(0)").val()
                        if (isnull(singleVal)) {
                            layer.msg('请填写指定' + (attrType == 1 ? '数量' : '金额') + '的数值', {icon: 2, time: 2000});
                            $("#dynamic_ext_condt_value").find("input:eq(0)").focus()
                            return;
                        }
                        specifyAttrValueDatas['singleVal'] = singleVal
                    } else {
                        var minJType = $("#dynamic_ext_condt_value").find("select:eq(0)").val()
                        var minValue = $("#dynamic_ext_condt_value").find("input:eq(0)").val()
                        if (isnull(minValue)) {
                            layer.msg('请填写指定' + (attrType == 1 ? '数量' : '金额') + '区间的数值', {icon: 2, time: 2000});
                            $("#dynamic_ext_condt_value").find("input:eq(0)").focus()
                            return;
                        }
                        var maxJType = $("#dynamic_ext_condt_value").find("select:eq(1)").val()
                        var maxValue = $("#dynamic_ext_condt_value").find("input:eq(1)").val()
                        if (isnull(maxValue)) {
                            layer.msg('请填写指定' + (attrType == 1 ? '数量' : '金额') + '区间的数值', {icon: 2, time: 2000});
                            $("#dynamic_ext_condt_value").find("input:eq(1)").focus()
                            return;
                        }
                        specifyAttrValueDatas['ltvVal'] = {
                            minJType: minJType, minValue: minValue, maxJType: maxJType, maxValue: maxValue
                        }
                    }
                }
            }

            // 优惠实例值数组
            var pvlgValueDatas = {};
            $(".dynamic_pvlg_p").each(function (i, e) {
                var pType = parseInt($(e).attr("pType"));
                var pvlgInstId = $(e).attr("pvlgInstId");
                var eachPvlgValueData = {};// 每个优惠实例的具体值
                eachPvlgValueData['pvlgInstId'] = pvlgInstId;
                switch (pType) {
                    case 1:// 满赠
                        var skus = $(e).find("input:eq(0)").val();
                        if (isnull(skus)) {
                            errorText = "请选择要赠送的商品";
                            return false;
                        }
                        // {donations:[{sku:xxx,warehouseId:yyyy,warehouseName:yyyy,cTitle:zzzz,imgUrl:xxx}],num:1}
                        var skuArray = skus.split(",");
                        var num = $.trim($(e).find("input:eq(6)").val());
                        if (isnull(num)) {
                            errorText = "请填写优惠赠品数量";
                            return false;
                        }
                        var warehouseIdArray = $(e).find("input:eq(1)").val().split(",");
                        var warehouseNameArray = $(e).find("input:eq(2)").val().split(",");
                        var cTitleArray = $(e).find("input:eq(3)").val().split("||");
                        var imgUrlArray = $(e).find("input:eq(4)").val().split(",");
                        var unitNumArray = $(e).find("input:eq(5)").val().split(",");
                        var unit = $("#pvlg_p_mz_unit_select").val();
                        // 检查，为箱的情况下，箱规不能为0
                        if (unit == '箱') {
                            for (var i = 0; i < unitNumArray.length; i++) {
                                if (unitNumArray[i] == '0') {
                                    errorText = "选择的赠品商品有箱规为0，请重新填写";
                                    return false;
                                }
                            }
                        }
                        var donationArray = new Array();
                        for (var i = 0; i < skuArray.length; i++) {
                            var sku_warehouseId_cTitle_json = {};
                            sku_warehouseId_cTitle_json["sku"] = skuArray[i];
                            sku_warehouseId_cTitle_json["warehouseId"] = warehouseIdArray[i];
                            sku_warehouseId_cTitle_json["warehouseName"] = warehouseNameArray[i];
                            sku_warehouseId_cTitle_json["cTitle"] = cTitleArray[i];
                            sku_warehouseId_cTitle_json["imgUrl"] = imgUrlArray[i];
                            sku_warehouseId_cTitle_json["unit"] = unit;
                            if (unit == '箱') {
                                sku_warehouseId_cTitle_json["unitNum"] = unitNumArray[i];// 每箱多少，如果是件，则为0
                            } else {
                                sku_warehouseId_cTitle_json["unitNum"] = 0;// 每箱多少，如果是件，则为0
                            }
                            donationArray.push(sku_warehouseId_cTitle_json);
                        }
                        eachPvlgValueData["unit"] = unit;
                        eachPvlgValueData["num"] = num;
                        eachPvlgValueData["donations"] = donationArray;
                        break;
                    case 2:// 满减
                        var reduceMoney = $.trim($(e).find("input:eq(0)").val());
                        if (isnull(reduceMoney)) {
                            errorText = "请输入正确的满减金额数";
                            return false;
                        }
                        // 满减金额正则
                        var pattern = /^(0|([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
                        if (!pattern.test(reduceMoney)) {
                            errorText = "请输入正确的满减金额";
                            $(e).find("input:eq(0)").val(reduceMoney.substring(0, num.length - 1));
                            return false;
                        }
                        eachPvlgValueData['reduceMoney'] = reduceMoney;
                        break;
                    case 3:// 折扣
                        var discountNum = $.trim($(e).find("input:eq(0)").val());
                        if (isnull(discountNum)) {
                            errorText = "请填写折扣";
                            return false;
                        }
                        // 定额折扣正则
                        var pattern = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
                        if (!pattern.test(discountNum)) {
                            errorText = "请输入正确的折扣";
                            $(e).find("input:eq(0)").val(discountNum.substring(0, discountNum.length - 1));
                            return false;
                        }
                        eachPvlgValueData['discountNum'] = discountNum;
                        break;
                    case 4:// 购物车满赠
                        var skus = $(e).find("input:eq(0)").val();
                        if (isnull(skus)) {
                            errorText = "请选择要赠送的商品";
                            return false;
                        }
                        // {donations:[{sku:xxx,warehouseId:yyyy,warehouseName:yyyy,cTitle:zzzz,imgUrl:xxx}],num:1}
                        var skuArray = skus.split(",");
                        var num = $.trim($(e).find("input:eq(6)").val());
                        if (isnull(num)) {
                            errorText = "请填写优惠赠品数量";
                            return false;
                        }
                        var warehouseIdArray = $(e).find("input:eq(1)").val().split(",");
                        var warehouseNameArray = $(e).find("input:eq(2)").val().split(",");
                        var cTitleArray = $(e).find("input:eq(3)").val().split("||");
                        var imgUrlArray = $(e).find("input:eq(4)").val().split(",");
                        var unitNumArray = $(e).find("input:eq(5)").val().split(",");
                        var unit = $("#pvlg_sc_mz_unit_select").val();
                        // 检查，为箱的情况下，箱规不能为0
                        if (unit == '箱') {
                            for (var i = 0; i < unitNumArray.length; i++) {
                                if (unitNumArray[i] == '0') {
                                    errorText = "选择的赠品商品有箱规为0，请重新填写";
                                    return false;
                                }
                            }
                        }
                        var donationArray = new Array();
                        for (var i = 0; i < skuArray.length; i++) {
                            var sku_warehouseId_cTitle_json = {};
                            sku_warehouseId_cTitle_json["sku"] = skuArray[i];
                            sku_warehouseId_cTitle_json["warehouseId"] = warehouseIdArray[i];
                            sku_warehouseId_cTitle_json["warehouseName"] = warehouseNameArray[i];
                            sku_warehouseId_cTitle_json["cTitle"] = cTitleArray[i];
                            sku_warehouseId_cTitle_json["imgUrl"] = imgUrlArray[i];
                            sku_warehouseId_cTitle_json["unit"] = unit;
                            if (unit == '箱') {
                                sku_warehouseId_cTitle_json["unitNum"] = unitNumArray[i];// 每箱多少，如果是件，则为0
                            } else {
                                sku_warehouseId_cTitle_json["unitNum"] = 0;// 每箱多少，如果是件，则为0
                            }
                            donationArray.push(sku_warehouseId_cTitle_json);
                        }
                        eachPvlgValueData["unit"] = unit;
                        eachPvlgValueData["num"] = num;
                        eachPvlgValueData["donations"] = donationArray;
                        break;
                    case 5:// 购物车满减
                        var reduceMoney = $.trim($(e).find("input:eq(0)").val());
                        if (isnull(reduceMoney)) {
                            errorText = "请填写优惠减价金额";
                            return false;
                        }
                        // 满减金额正则
                        var pattern = /^(0|([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
                        if (!pattern.test(reduceMoney)) {
                            errorText = "请输入正确的优惠减价金额";
                            $(e).find("input:eq(0)").val(reduceMoney.substring(0, num.length - 1));
                            return false;
                        }
                        eachPvlgValueData['reduceMoney'] = reduceMoney;
                        break;
                    case 6:// 整个购物车的定额折扣
                        var discountNum = $.trim($(e).find("input:eq(0)").val());
                        if (isnull(discountNum)) {
                            errorText = "请填写优惠定额折扣";
                            return false;
                        }
                        // 定额折扣正则
                        var pattern = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
                        if (!pattern.test(discountNum)) {
                            errorText = "请输入正确的优惠定额折扣";
                            $(e).find("input:eq(0)").val(discountNum.substring(0, discountNum.length - 1));
                            return false;
                        }
                        eachPvlgValueData['discountNum'] = discountNum;
                        break;
                    default:
                        break;
                }
                pvlgValueDatas[pType] = eachPvlgValueData;
            });
            if (!isnull(errorText)) {
                layer.msg(errorText, {icon: 2, time: 2000});
                return;
            }

            // 最终要提交的条件和优惠的具体值
            var condt_pvlg_value_json = {};
            condt_pvlg_value_json['stepped'] == $("#stepped_checkbox").prop("checked")// 可阶梯
            condt_pvlg_value_json['condtValueDatas'] = condtValueDatas;
            condt_pvlg_value_json['specifyAttrValueDatas'] = specifyAttrValueDatas;
            condt_pvlg_value_json['pvlgValueDatas'] = pvlgValueDatas;
            if ($("#match_type_select").attr("canUse") == 'true') {
                // 多个条件
                condt_pvlg_value_json['matchType'] = $("#match_type_select").val();
            }
            //condt_pvlg_value_json['actInstId'] = $("#act_inst_id").val();

            ajax_post("../market/pro/actinst/condtpvlg/update", JSON.stringify(condt_pvlg_value_json), "application/json",
                function (response) {
                    if (response.success) {
                        layer.msg("设置参数成功", {icon: 1, time: 2000}, function () {
                            layer.close(index);
                            loadProActi($("#pro_act_id").val());
                        });
                    }
                },
                function (XMLHttpRequest, textStatus) {
                    layer.msg("添加促销类型失败", {icon: 5, time: 2000});
                }
            );

        }
    });
}