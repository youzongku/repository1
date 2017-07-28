/**
 * Created by Administrator on 2016/10/27.
 */
// 生成活动实例描述
function createDynamicDescription(containerId,condtInstDto) {
    if(condtInstDto != undefined && condtInstDto.isSetV == 0){
        return
    }
    // 获取所有的条件所在的<p>标签
    var text = ''
    $(".dynamic_condt_p").each(function (i, e) {
        var cType = parseInt($(e).attr("c_type"));// 标明是哪个条件
        //大于=gt, 小于=lt, 大于等于=gteq, 小于等于=lteq, 是=y, 非=n, 区间=ltv;
        var jType = $(e).find("select[name='jType_select']").find("option:selected").attr("jType");// 判断类型：lt,gt,...
        switch (cType) {
            case 1:// 商品分类 cType,jType,categoryIds,categoryNames
                // productCategories:[{"id":1,"name":零食},{"id":2,"name":母婴}]
                var categoryNames = $(e).find("input:eq(0)").val();
                var combined = $(e).find("input[name='combined_checkbox'][type='checkbox']").prop("checked");
                var hidden = $(e).find("input[name='combined_checkbox'][type='checkbox']").is(":hidden")
                text = '购买指定商品分类【'+categoryNames+'】的商品，'
                if(!hidden){
                    if(combined){
                        text += '可组合，'
                    }else{
                        text += '不可组合，'
                    }
                }
                break;
            case 2:// 指定商品 cType,jType,skus,num
                var skus = $(e).find("input:eq(0)").val();
                // 箱规
                var combined = $(e).find("input[name='combined_checkbox'][type='checkbox']").prop("checked");
                var hidden = $(e).find("input[name='combined_checkbox'][type='checkbox']").is(":hidden")
                var unit = $("#specify_product_unit_select").val();
                if(unit=='件'){
                    unit = "个"
                }
                text = '购买指定商品【'+skus+'】，箱规为'+unit
                if(!hidden){
                    if(combined){
                        text += '可组合，'
                    }else{
                        text += '不可组合，'
                    }
                }

                // 是否组合
                // 电商模式没有可组合
                // if(containsEcommerceMode()){
                //     eachCondtValueData["combined"] = false;
                // }else{
                //     eachCondtValueData['combined'] = $(e).find("input[name='combined_checkbox'][type='checkbox']").prop("checked");
                // }
                break;
            case 3:// 指定仓库 cType,jType,warehouseIds,warehouseNames
                // todo 暂不支持
                break;
            case 4:// 商品类型
                // todo 暂不支持
                break;
            case 5:// 小计金额 cType,jType,minPrice,maxPrice
                text = '购买金额'
                var minPrice = $.trim($(e).find("input:eq(0)").val());
                if (jType == 'ltv') {// 区间，有两个input的，其他的都是一个input
                    var maxPrice = $.trim($(e).find("input:eq(1)").val());
                    text += '在'+minPrice+"~"+maxPrice+getJtypeStr(jType)+'内，'
                }else{
                    text += getJtypeStr(jType)+minPrice+"元，"
                }
                if($("#subtotal_doubleUp")[0] != undefined){
                    var hidden = $("#subtotal_doubleUp").parent().is(":hidden")
                    var doubleUp = $("#subtotal_doubleUp").prop("checked")
                    if(!hidden){
                        if(doubleUp){
                            text += '可翻倍，'
                        }else{
                            text += '不可翻倍，'
                        }
                    }
                }

                break;
            case 6:// 总商品数量
                var productTotalCount = $.trim($(e).find("input:eq(0)").val());
                text = '商品总数量'
                text += getJtypeStr(jType)+productTotalCount+"个，"
                if($("#total_count_doubleUp")[0] != undefined){
                    var hidden = $("#total_count_doubleUp").parent().is(":hidden")
                    var doubleUp = $("#subtotal_doubleUp").prop("checked")
                    if(!hidden){
                        if(doubleUp){
                            text += '可翻倍，'
                        }else{
                            text += '不可翻倍，'
                        }
                    }
                }
                break;
            case 7:// 购物车总重量 cType,jType,min,max
                // todo 暂不支持
                break;
            case 8:// 运送地区 cType,jType,cityIds,cityNames
                break;
            default:
                break;
        }
    });

    if($("#specify_attr_value_p")[0] != undefined) {
        var attrType = $("#specify_attr_value_p").find("select:eq(0)").val();
        if(attrType == 1) text += "指定商品属性："
        else if(attrType == 2) text += "指定购物车属性："

        if ($("#specify_attr_value_p") != undefined) {
            text += (attrType == 1 ? '数量' : '金额')
            var attrTypeJType = $("#specify_attr_value_p").find("select:eq(1)").val();
            if (attrTypeJType != 'ltv') {
                var singleVal = $("#dynamic_ext_condt_value").find("input:eq(0)").val()
                text += getJtypeStr(attrTypeJType)
                if (singleVal != undefined) text += singleVal
                text += "，"
            } else {
                var minJType = $("#dynamic_ext_condt_value").find("select:eq(0)").val()
                var minValue = $("#dynamic_ext_condt_value").find("input:eq(0)").val()
                var maxJType = $("#dynamic_ext_condt_value").find("select:eq(1)").val()
                var maxValue = $("#dynamic_ext_condt_value").find("input:eq(1)").val()
                text += getJtypeStr(minJType) + minValue + getJtypeStr(maxJType) + maxValue+"，"
            }
        }
        // 指定商品/购物车属性
        //var stepped = $("#stepped_checkbox").prop("checked"); // 可阶梯
        if ($("#specify_attr_value_p").find("input[type='checkbox']").prop("checked") != undefined) {// 商品属性的可翻倍
            var doubleUp = $("#specify_attr_value_p").find("input[type='checkbox']").prop("checked")// 可翻倍
            if(doubleUp){
                text += '可翻倍，'
            }else{
                text += '不可翻倍，'
            }
        }
    }

    // 优惠实例值数组
    $(".dynamic_pvlg_p").each(function (i, e) {
        var pType = parseInt($(e).attr("pType"));
        switch (pType) {
            case 1:// 满赠
                var skus = $(e).find("input:eq(0)").val();
                // {donations:[{sku:xxx,warehouseId:yyyy,warehouseName:yyyy,cTitle:zzzz,imgUrl:xxx}],num:1}
                var num = $.trim($(e).find("input:eq(6)").val());
                var unit = $("#pvlg_p_mz_unit_select").val();
                if(unit=='件'){
                    unit = '个'
                }
                text += '赠送商品【'+skus+'】'+num+unit
                break;
            case 2:// 满减
                var reduceMoney = $.trim($(e).find("input:eq(0)").val());
                text += '满减'+reduceMoney+'元'
                break;
            case 3:// 折扣
                var discountNum = $.trim($(e).find("input:eq(0)").val());
                var f1 = parseFloat(discountNum);
                var discount = f1/10;
                text += '打'+discount+'折'
                break;
            case 4:// 购物车满赠
                var skus = $(e).find("input:eq(0)").val();
                // {donations:[{sku:xxx,warehouseId:yyyy,warehouseName:yyyy,cTitle:zzzz,imgUrl:xxx}],num:1}
                var num = $.trim($(e).find("input:eq(6)").val());
                var unit = $("#pvlg_sc_mz_unit_select").val();
                if(unit=='件'){
                    unit = '个'
                }
                text += '赠送商品【'+skus+'】'+num+unit
                break;
            case 5:// 购物车满减
                var reduceMoney = $.trim($(e).find("input:eq(0)").val());
                text += '满减'+reduceMoney
                break;
            case 6:// 整个购物车的定额折扣
                var discountNum = $.trim($(e).find("input:eq(0)").val());
                var f1 = parseFloat(discountNum);
                var discount = f1/10;
                text += '打'+discount+'折'
                break;
            default:
                break;
        }
    });

    $("#"+containerId).html(text);
}

function getJtypeStr(jtype){
    //大于=gt, 小于=lt, 大于等于=gteq, 小于等于=lteq, 是=y, 非=n, 区间=ltv;
    if(isnull(jtype))return ''
    switch (jtype){
        case 'gt': return '大于'
        case 'lt': return '小于'
        case 'gteq': return '大于等于'
        case 'lteq': return '小于等于'
        case 'y': return '是'
        case 'n': return '非'
        case 'ltv': return '区间'
        default: return ''
    }
}