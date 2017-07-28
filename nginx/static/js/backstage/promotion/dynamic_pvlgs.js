/**
 * Created by Administrator on 2016/7/29.
 */
var layer = undefined;
var laypage = undefined;
//初始化
function init_dynamic_pvlgs(layerParam, laypageParam) {
    //初始化全局变量
    layer = layerParam;
    laypage = laypageParam;
}

/**
 * 动态创建优惠dom
 * @param containerId 存放的dom id
 * @param pvlgInstDtoList 优惠实例集合
 */
function createDynamicPvlgs(containerId, fullPvlgInstDto){
    var content = '';
    var pvlgInstDtoList = new Array()
    pvlgInstDtoList.push(fullPvlgInstDto)
    for(var i in pvlgInstDtoList){
        var pvlgInst = pvlgInstDtoList[i];// 优惠实例

        //商品属性的优惠：购买X即可免费获得Y（赠品）=1，满减金额=2，整个购物车的定额折扣=3
        //购物车属性的优惠：购买X即可免费获得Y（赠品）=4，满减金额=5，整个购物车的定额折扣=6
        var pType = pvlgInst.pType;  // 优惠的类型
        var pvlgInstId = pvlgInst.id;// 优惠实例id
        var pvlgValue;               // 优惠的json字符串
        if(!isnull(pvlgInst.pvlgValue)){
            pvlgValue=eval("("+pvlgInst.pvlgValue+")");// 将json字符串转变为json对象
        }
        // 1~3是商品属性条件的优惠；4~6是购物车属性条件的优惠
        switch (pType){
            case 1:// 购买X即可免费获得Y（赠品） {donations:[{cTitle:"可比克零食",sku:"IM27","wareHouseId":2024,warehouseName:xx,imgUrl:xx,"num":1}],"num":1,"combined":false}
                var skuArray=new Array(), warehouseIdArray=new Array(),warehouseNameArray=new Array(),
                    cTitleArray=new Array(), imgUrlArray=new Array(), unitNumArray=new Array();
                var numValue = "";
                var unit = "件";
                if(!isnull(pvlgValue)){
                    var donations = pvlgValue.donations;
                    for(var i in donations){
                        skuArray.push(donations[i].sku);
                        warehouseIdArray.push(donations[i].warehouseId);
                        warehouseNameArray.push(donations[i].warehouseName);
                        cTitleArray.push(donations[i].cTitle);
                        imgUrlArray.push(donations[i].imgUrl);
                        unitNumArray.push(donations[i].unitNum);
                    }
                    var num = pvlgValue.num;
                    numValue = "value='"+num+"'";
                    unit = pvlgValue.unit;
                }
                // 箱规
                var num_html = '赠品商品规格：<select id="pvlg_p_mz_unit_select" onchange="pvlg_p_mz_unit_select_onchange(this)" style="width: 50px;height:25px;">';
                if(unit=='箱'){
                    num_html += '<option value="件">个</option><option value="箱" selected="selected">箱</option>';
                }else{
                    num_html += '<option value="件" selected="selected">个</option><option value="箱">箱</option>';
                }
                num_html += '</select>';
                var pvlg_p_mz_html=
                    '<p class="para_blcok dynamic_pvlg_p" pType="'+pType+'" pvlgInstId="'+pvlgInstId+'">' +
                    '<span><b>获得优惠：</b>满赠</span>'+ num_html +
                    '<span><b>赠品商品编号：</b>' +
                    '<input value="'+skuArray.toString()+'" title="'+skuArray.toString()+'" type="text" id="pvlg_p_mz_skus" class="promotion_input" readonly>' +
                    '<input value="'+warehouseIdArray.toString()+'" id="pvlg_p_mz_warehouseIds" type="hidden">' +
                    '<input value="'+warehouseNameArray.toString()+'" id="pvlg_p_mz_warehouseNames" type="hidden">' +
                    '<input value="'+cTitleArray.join("||")+'" id="pvlg_p_mz_cTitles" type="hidden">' +
                    '<input value="'+imgUrlArray.toString()+'" id="pvlg_p_mz_imgUrls" type="hidden">' +
                    '<input value="'+unitNumArray.toString()+'" id="pvlg_p_mz_unitNums" type="hidden">' +
                    '<button onclick="pvlg_p_mz_popup()" class="promotion_button appoint_check">搜索</button></span>'+
                     '<span style="display: block"><b>赠品商品数量：</b>' +
                    '<input '+numValue+' style="width: 50px" oninput="check_mz_number(this)" type="text" class="promotion_input">'
                if(unit == '件'){
                    unit = '个'
                }
                pvlg_p_mz_html += '<b id="pvlg_p_mz_unit">'+unit+'</b></span></p>';
                content+=pvlg_p_mz_html;
                break;
            case 2:// 满减金额 {"moneyReduce":12.5}
                var moneyReduceValue = "";
                if(!isnull(pvlgValue)) {
                    moneyReduceValue = "value='"+pvlgValue.moneyReduce+"'";
                }
                var pvlg_p_mj_html=
                    '<p class="para_blcok dynamic_pvlg_p" pType="'+pType+'" pvlgInstId="'+pvlgInstId+'">' +
                    '<span><b>获得优惠：</b>满减</span><span>'+
                    '<span><b>减价：</b><input '+moneyReduceValue+' oninput="check_reduceMoney(this)" type="text"></span></p>';
                content +=pvlg_p_mj_html;
                break;
            case 3:// 折扣 {"num":11}
                $("#specify_attr_value_p").find("input[type='checkbox']").parent().remove()// 折扣是没有翻倍的
                var numValue = "";
                if(!isnull(pvlgValue)) {
                    numValue = "value='"+pvlgValue.num+"'";
                }
                var pvlg_p_zk_html =
                    '<p class="para_blcok dynamic_pvlg_p" pType="'+pType+'" pvlgInstId="'+pvlgInstId+'">' +
                    '<span><b>获得优惠：</b>折扣</span>'+
                    '<span><b>定额折扣：</b><input '+numValue+' class="promotion_input" type="text">%</span></p>';
                content+=pvlg_p_zk_html;
                break;
            case 4:// 购物车满赠
                var skuArray=new Array(), warehouseIdArray=new Array(),warehouseNameArray=new Array(),
                    cTitleArray=new Array(), imgUrlArray=new Array(), unitNumArray=new Array();
                var numValue = "";
                var unit = "件";
                if(!isnull(pvlgValue)){
                    var donations = pvlgValue.donations;
                    for(var i in donations){
                        skuArray.push(donations[i].sku);
                        warehouseIdArray.push(donations[i].warehouseId);
                        warehouseNameArray.push(donations[i].warehouseName);
                        cTitleArray.push(donations[i].cTitle);
                        imgUrlArray.push(donations[i].imgUrl);
                        unitNumArray.push(donations[i].unitNum);
                    }
                    var num = pvlgValue.num;
                    numValue = "value='"+num+"'";
                    unit = pvlgValue.unit;
                }
                // 箱规
                var num_html = '赠品商品规格：<select id="pvlg_sc_mz_unit_select" onchange="pvlg_sc_mz_unit_select_onchange" style="width: 50px;height:25px;">';
                if(unit=='箱'){
                    num_html += '<option value="件">个</option><option value="箱" selected="selected">箱</option>';
                }else{
                    num_html += '<option value="件" selected="selected">个</option><option value="箱">箱</option>';
                }
                num_html += '</select>';
                var pvlg_sc_mz_html=
                    '<p class="para_blcok dynamic_pvlg_p" pType="'+pType+'" pvlgInstId="'+pvlgInstId+'">' +
                    '<span><b>获得优惠：</b>购物车满赠</span>'+ num_html +
                    '<span><b>赠品商品编号：</b>' +
                    '<input value="'+skuArray.toString()+'" title="'+skuArray.toString()+'" id="pvlg_sc_mz_skus" class="promotion_input" readonly type="text">' +
                    '<input value="'+warehouseIdArray.toString()+'" id="pvlg_sc_mz_warehouseIds" type="hidden">' +
                    '<input value="'+warehouseNameArray.toString()+'" id="pvlg_sc_mz_warehouseNames" type="hidden">' +
                    '<input value="'+cTitleArray.join("||")+'" id="pvlg_sc_mz_cTitles" type="hidden">' +
                    '<input value="'+imgUrlArray.toString()+'" id="pvlg_sc_mz_imgUrls" type="hidden">' +
                    '<input value="'+unitNumArray.toString()+'" id="pvlg_sc_mz_unitNums" type="hidden">' +
                    '<button onclick="pvlg_sc_mz_popup()" class="promotion_button appoint_check">搜索</button></span>'+
                    '<span style="display: block"><b>赠品商品数量：</b>' +
                    '<input '+numValue+' oninput="check_mz_number(this)" style="width: 50px" class="promotion_input" type="text">'
                if(unit == '件'){
                    unit = '个'
                }
                pvlg_sc_mz_html += '<b id="pvlg_sc_mz_unit">'+unit+'</b></span></p>';
                content+=pvlg_sc_mz_html;
                break;
            case 5:// 购物车满减
                var moneyReduceValue = "";
                if(!isnull(pvlgValue)) {
                    moneyReduceValue = "value='"+pvlgValue.moneyReduce+"'";
                }
                var pvlg_sc_mj_html=
                    '<p class="para_blcok dynamic_pvlg_p" pType="'+pType+'" pvlgInstId="'+pvlgInstId+'">' +
                    '<span><b>获得优惠：</b>购物车满减</span><span>'+
                    '<span><b>减价：</b><input oninput="check_reduceMoney(this)" '+moneyReduceValue+' class="promotion_input" type="text"/></span></p>';
                content +=pvlg_sc_mj_html;
                break;
            case 6:// 整个购物车的定额折扣
                $("#subtotal_doubleUp").parent().remove()       // 小计金额-折扣没有翻倍
                if($("#total_count_doubleUp")[0] != undefined){
                    $("#total_count_doubleUp").parent().remove()    // 购物车商品总数量没有翻倍
                    $("#stepped_checkbox").parent().hide()  // 购物车商品总数量在折扣的情况下是没有可阶梯的
                }
                var numValue = "";
                if(!isnull(pvlgValue)) {
                    numValue = "value='"+pvlgValue.num+"'";
                }
                var pvlg_sc_zk_html =
                    '<p class="para_blcok dynamic_pvlg_p" pType="'+pType+'" pvlgInstId="'+pvlgInstId+'">' +
                    '<span><b>获得优惠：</b>整个购物车的定额折扣</span>'+
                    '<span><b>定额折扣：</b><input '+numValue+' class="promotion_input" type="text" />%</span></p>';
                content+=pvlg_sc_zk_html;
                break;
            default:
                break;
        }
    }
    $("#"+containerId).html(content);
}

// 商品属性-满赠的，选择箱规
function pvlg_p_mz_unit_select_onchange(obj){
    var unit = $(obj).val();
    if(unit=='件'){
        unit = '个'
    }
    $("#pvlg_p_mz_unit").text(unit);
    createDynamicDescription()
}

// 商品属性-满赠的，选择箱规
function pvlg_sc_mz_unit_select_onchange(obj){
    var unit = $(obj).val();
    if(unit=='件'){
        unit = '个'
    }
    $("#pvlg_sc_mz_unit").text(unit);
    createDynamicDescription()
}

function check_reduceMoney(obj){
    var money = $.trim($(obj).val());
    if(isnull(money)){
        $(obj).val("");
        $(obj).focus()
        layer.msg("满减金额不能为空！",{icon:5,time:2000});
        return;
    }
    var pattern = /^(0|([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/; // 要判断它是否是正确的金额数
    if(!pattern.test(money)){
        layer.msg("请输入正确的满减金额数！",{icon:5,time:2000});
        $(obj).val("");
        $(obj).focus()
        return;
    }
    createDynamicDescription()
}
// 赠品数量输入控制
function check_mz_number(obj){
    var num = $.trim($(obj).val());
    if(isnull(num)){
        $(obj).val("");
        $(obj).focus()
        layer.msg("赠品数量不能为空！",{icon:5,time:2000});
        return;
    }
    var pattern = /^[1-9]\d*$/;
    if(!pattern.test(num)){
        layer.msg("赠品数量只能为正整数！",{icon:5,time:2000});
        $(obj).val("");
        $(obj).focus()
        return;
    }
    createDynamicDescription()
}

// 商品属性-满赠弹窗
function pvlg_p_mz_popup(){
    cleanSearchProducts();
    layer.open({
        area: ['800', '500px'],
        type: 1,
        shade: false,
        skin: '.pop_style',
        content: $('#specify_product_popup_div'),
        title: false, //不显示标题
        btn: ['确认', '取消'],
        yes: function(index){
            // 对最终的结果进行验证
            var skus = $("#pvlg_p_mz_skus").val();
            if(isnull(skus)){
                layer.msg("您当前选择的赠品个数为0，请选择赠品！",{icon:2,time:2000});
            }
            // 清除搜索条件
            $("#specify_product_search_skus").val("");
            createDynamicDescription()
            layer.close(index);
        }
    });

    var specify_donation_product_json = {
        skus_exists:$("#pvlg_p_mz_skus").val(),
        warehouseIds_exists:$("#pvlg_p_mz_warehouseIds").val(),
        unitNums_exists:$("#pvlg_p_mz_unitNums").val(),
        unit:$("#pvlg_p_mz_unit_select").val()
    };
    // 给弹窗的搜索按钮添加点击事件
    $("body").off( "click", "#specify_product_search_btn");
    $("body").on("click","#specify_product_search_btn",function(){
        searchProducts(null,null,specify_donation_product_json,
            pvlg_p_mz_callbackAfterSelected,pvlg_p_mz_callbackAfterSelectedAll,pvlg_p_mz_unitNum_callback);
    });
    searchProducts(null,null, specify_donation_product_json,
        pvlg_p_mz_callbackAfterSelected,pvlg_p_mz_callbackAfterSelectedAll,pvlg_p_mz_unitNum_callback);
}

// 商品属性-满赠的
function pvlg_p_mz_unitNum_callback(){
    if(!specify_product_unit_num(this)){
        return;
    };
    // 获取原先就选中的商品
    var skus = $("#pvlg_p_mz_skus").val();
    var warehouseIds = $("#pvlg_p_mz_warehouseIds").val();
    var warehouseNames = $("#pvlg_p_mz_warehouseNames").val();
    var cTitles = $("#pvlg_p_mz_cTitles").val();// 使用||分割
    var imgUrls = $("#pvlg_p_mz_imgUrls").val();
    var unitNums = $("#pvlg_p_mz_unitNums").val();
    // 分割
    var warehouseId_array = new Array(); if(!isnull(warehouseIds)){warehouseId_array = warehouseIds.split(",");}
    var warehouseName_array = new Array(); if(!isnull(warehouseNames)){warehouseName_array = warehouseNames.split(",");}
    var sku_array = new Array(); if(!isnull(skus)){sku_array = skus.split(",");}
    var cTitle_array = new Array(); if(!isnull(cTitles)){cTitle_array = cTitles.split("||");}
    var imgUrl_array = new Array(); if(!isnull(imgUrls)){imgUrl_array = imgUrls.split(",");}
    var unitNum_array = new Array(); if(!isnull(unitNums)){unitNum_array = unitNums.split(",");}

    // 获取每箱的数量
    var unit = $("#pvlg_p_mz_unit_select").val();
    if(unit=='箱'){
        var sku = $(this).parent().parent().find("input[type='checkbox']:checked").attr("sku");
        var warehouseId = $(this).parent().parent().find("input[type='checkbox']:checked").attr("warehouseId");
        var foundIndex=-1;
        // 检查此sku在原先选中的skus中的位置
        for(var i= 0,length=sku_array.length;i<length;i++){
            if(sku_array[i]==sku && warehouseId_array[i]==warehouseId){
                foundIndex=i;
                break;
            }
        }
        var unitNum = $(this).parent().parent().find('td[name="unitNum_td"]').find("input").val();
        if(foundIndex!=-1){
            unitNum_array[foundIndex] = unitNum;
            $("#pvlg_p_mz_unitNums").val(unitNum_array.toString());
        }
    }
}

// // 商品属性-满赠的，赠品选中商品后的处理
function pvlg_p_mz_callbackAfterSelected(){
    var checked = $(this).prop("checked");
    // 获取原先就选中的商品
    var skus = $("#pvlg_p_mz_skus").val();
    var warehouseIds = $("#pvlg_p_mz_warehouseIds").val();
    var warehouseNames = $("#pvlg_p_mz_warehouseNames").val();
    var cTitles = $("#pvlg_p_mz_cTitles").val();// 使用||分割
    var imgUrls = $("#pvlg_p_mz_imgUrls").val();
    var unitNums = $("#pvlg_p_mz_unitNums").val();
    // 分割
    var warehouseId_array = new Array(); if(!isnull(warehouseIds)){warehouseId_array = warehouseIds.split(",");}
    var warehouseName_array = new Array(); if(!isnull(warehouseNames)){warehouseName_array = warehouseNames.split(",");}
    var sku_array = new Array(); if(!isnull(skus)){sku_array = skus.split(",");}
    var cTitle_array = new Array(); if(!isnull(cTitles)){cTitle_array = cTitles.split("||");}
    var imgUrl_array = new Array(); if(!isnull(imgUrls)){imgUrl_array = imgUrls.split(",");}
    var unitNum_array = new Array(); if(!isnull(unitNums)){unitNum_array = unitNums.split(",");}

    // 不组合，就只能选一个商品
    // var combined_checked = $("#condt_specify_product_combined").prop("checked");
    // if(combined_checked==undefined){
    //     // 为undefined，就说明没有可组合这个选项，就不用进行下边的检查
    //     combined_checked = true;
    // }
    // if(!combined_checked){
    //     // 已经为1了
    //     if((sku_array.length == 1) && checked){
    //         layer.msg("在指定商品不可组合的情况下，不能选择多个赠品",{icon:2,time:2000});
    //         $(this).prop("checked",false);
    //         return;
    //     }
    // }

    // 单位
    var unit = $("#pvlg_p_mz_unit_select").val();
    // 验证是否填了箱规
    if(unit=='箱'){
        var unitNum = $(this).parent().parent().find('td[name="unitNum_td"]').find("input").val();
        if(isnull(unitNum)){
            layer.msg("请填写箱规",{icon:5,time:2000});
            $(this).prop("checked",false);
            return;
        }
    }

    if(checked){// 选中的情况
        if(unit=='箱'){
            var unitNum = $(this).parent().parent().find('td[name="unitNum_td"]').find("input").val();
            unitNum_array.push(unitNum);
        }else{
            unitNum_array.push('0');
        }
        sku_array.push($(this).attr("sku"));
        warehouseId_array.push($(this).attr("warehouseId"));
        warehouseName_array.push($(this).attr("warehouseName"));
        cTitle_array.push($(this).attr("ctitle"));
        imgUrl_array.push($(this).attr("imgUrl"));
    }else{// 不选的情况
        var foundIndex=-1;
        var sku = $(this).attr("sku");
        var warehouseId = $(this).attr("warehouseId");
        // 检查此sku在原先选中的skus中的位置
        for(var i= 0,length=sku_array.length;i<length;i++){
            if(sku_array[i]==sku && warehouseId_array[i]==warehouseId){
                foundIndex=i;
                break;
            }
        }
        // 进行移除操作
        if(foundIndex!=-1){
            unitNum_array.splice(foundIndex,1);
            sku_array.splice(foundIndex,1);
            warehouseId_array.splice(foundIndex,1);
            warehouseName_array.splice(foundIndex,1);
            cTitle_array.splice(foundIndex,1);
            imgUrl_array.splice(foundIndex,1);
        }
    }

    // 保存最新的
    $("#pvlg_p_mz_skus").attr("title",sku_array.toString()).val(sku_array.toString());
    $("#pvlg_p_mz_warehouseIds").val(warehouseId_array.toString());
    $("#pvlg_p_mz_warehouseNames").val(warehouseName_array.toString());
    $("#pvlg_p_mz_cTitles").val(cTitle_array.join("||"));
    $("#pvlg_p_mz_imgUrls").val(imgUrl_array.toString());
    $("#pvlg_p_mz_unitNums").val(unitNum_array.toString());

    createDynamicDescription()
}
// // 商品属性-满赠的，赠品全选中商品后的处理
function pvlg_p_mz_callbackAfterSelectedAll(){
    var checked = $(this).prop("checked");
    // 获取原先就选中的商品
    var skus = $("#pvlg_p_mz_skus").val();
    var warehouseIds = $("#pvlg_p_mz_warehouseIds").val();
    var warehouseNames = $("#pvlg_p_mz_warehouseNames").val();
    var cTitles = $("#pvlg_p_mz_cTitles").val();// 使用||分割
    var imgUrls = $("#pvlg_p_mz_imgUrls").val();
    var unitNums = $("#pvlg_p_mz_unitNums").val();
    // 分割
    var warehouseId_array = new Array(); if(!isnull(warehouseIds)){warehouseId_array = warehouseIds.split(",");}
    var warehouseName_array = new Array(); if(!isnull(warehouseNames)){warehouseName_array = warehouseNames.split(",");}
    var sku_array = new Array(); if(!isnull(skus)){sku_array = skus.split(",");}
    var cTitle_array = new Array(); if(!isnull(cTitles)){cTitle_array = cTitles.split("||");}
    var imgUrl_array = new Array(); if(!isnull(imgUrls)){imgUrl_array = imgUrls.split(",");}
    var unitNum_array = new Array(); if(!isnull(unitNums)){unitNum_array = unitNums.split(",");}

    // 不组合，就只能选一个商品
    // var combined_checked = $("#condt_specify_product_combined").prop("checked");
    // if(combined_checked==undefined){
    //     // 为undefined，就说明没有可组合这个选项，就不用进行下边的检查
    //     combined_checked = true;
    // }
    //
    // if(!combined_checked){
    //     // 已经为1了
    //     if((sku_array.length == 1 || sku_array.length==0) && checked){
    //         layer.msg("在指定商品不可组合的情况下，不能选择多个赠品",{icon:2,time:2000});
    //         $(this).prop("checked",false);
    //         $(this).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']").each(function(i,e){
    //             if(sku_array[0] != $(e).attr("sku") || warehouseId_array[0] != $(e).attr("warehouseId")){
    //                 $(e).prop("checked",false);
    //             }
    //         });
    //         return;
    //     }
    // }

    // 单位
    var unit = $("#pvlg_p_mz_unit_select").val();
    // 验证是否填了箱规
    if(unit=='箱'){
        var errText='';
        $(this).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']:checked").each(function(i,e){
            var unitNum = $(e).parent().parent().find('td[name="unitNum_td"]').find("input").val();
            if(isnull(unitNum)){
                errText = '请填写箱规';
                return false;
            }
        });
        if(!isnull(errText)){
            $(this).prop("checked",false);
            $(this).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']:checked").each(function(i,e){
                $(e).prop("checked",false);
            });
            layer.msg(errText,{icon:5,time:2000});
        }
        return;
    }

    if(checked){// 选中的情况
        $(this).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']:checked").each(function(i,e){
            var foundIndex=-1;
            var sku = $(e).attr("sku");
            var warehouseId = $(e).attr("warehouseId");
            // 检查此sku在原先选中的skus中的位置
            for(var i= 0,length=sku_array.length;i<length;i++){
                if(sku_array[i]==sku && warehouseId_array[i]==warehouseId){
                    foundIndex=i;
                    break;
                }
            }
            if(foundIndex==-1){
                if(unit=='箱'){
                    var unitNum = $(e).parent().parent().find('td[name="unitNum_td"]').find("input").val();
                    unitNum_array.push(unitNum);
                }else{
                    unitNum_array.push('0');
                }
                sku_array.push($(e).attr("sku"));
                warehouseId_array.push($(e).attr("warehouseId"));
                warehouseName_array.push($(e).attr("warehouseName"));
                cTitle_array.push($(e).attr("ctitle"));
                imgUrl_array.push($(e).attr("imgUrl"));
            }
        });
    }else{// 不选的情况
        $(this).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']").each(function(i,e){
            var foundIndex=-1;
            var sku = $(e).attr("sku");
            var warehouseId = $(e).attr("warehouseId");
            // 检查此sku在原先选中的skus中的位置
            for(var i= 0,length=sku_array.length;i<length;i++){
                if(sku_array[i]==sku && warehouseId_array[i]==warehouseId){
                    foundIndex=i;
                    break;
                }
            }
            // 进行移除操作
            if(foundIndex!=-1){
                unitNum_array.splice(foundIndex,1);
                sku_array.splice(foundIndex,1);
                warehouseId_array.splice(foundIndex,1);
                warehouseName_array.splice(foundIndex,1);
                cTitle_array.splice(foundIndex,1);
                imgUrl_array.splice(foundIndex,1);
            }
        });
    }

    // 保存最新的
    $("#pvlg_p_mz_skus").attr("title",sku_array.toString()).val(sku_array.toString());
    $("#pvlg_p_mz_warehouseIds").val(warehouseId_array.toString());
    $("#pvlg_p_mz_warehouseNames").val(warehouseName_array.toString());
    $("#pvlg_p_mz_cTitles").val(cTitle_array.join("||"));
    $("#pvlg_p_mz_imgUrls").val(imgUrl_array.toString());
    $("#pvlg_p_mz_unitNums").val(unitNum_array.toString());

    createDynamicDescription()
}

// 购物车属性-满赠弹窗
function pvlg_sc_mz_popup(){
    cleanSearchProducts();
    layer.open({
        area: ['800', '500px'],
        type: 1,
        shade: false,
        skin: '.pop_style',
        content: $('#specify_product_popup_div'),
        title: false, //不显示标题
        btn: ['确认', '取消'],
        yes: function(index){
            // 对最终的结果进行验证
            var skus = $("#pvlg_sc_mz_skus").val();
            if(isnull(skus)){
                layer.msg("您当前选择的赠品个数为0，请选择赠品",{icon:2,time:2000});
            }
            // 清除搜索条件
            $("#specify_product_search_skus").val("");
            layer.close(index);

            createDynamicDescription()
        }
    });

    var specify_donation_product_json = {
        skus_exists:$("#pvlg_sc_mz_skus").val(),
        warehouseIds_exists:$("#pvlg_sc_mz_warehouseIds").val(),
        unitNums_exists:$("#pvlg_sc_mz_unitNums").val(),
        unit:$("#pvlg_sc_mz_unit_select").val()
    };
    // 给弹窗的搜索按钮添加点击事件
    $("body").off( "click", "#specify_product_search_btn");
    $("body").on("click","#specify_product_search_btn",function(){
        searchProducts(null,null,specify_donation_product_json,
            pvlg_sc_mz_callbackAfterSelected,pvlg_sc_mz_callbackAfterSelectedAll,pvlg_sc_mz_unitNum_callback);
    });
    searchProducts(null,null, specify_donation_product_json,
        pvlg_sc_mz_callbackAfterSelected,pvlg_sc_mz_callbackAfterSelectedAll,pvlg_sc_mz_unitNum_callback);
}

// 商品属性-满赠的
function pvlg_sc_mz_unitNum_callback(){
    if(!specify_product_unit_num(this)){
        return;
    };
    // 获取原先就选中的商品
    var skus = $("#pvlg_sc_mz_skus").val();
    var warehouseIds = $("#pvlg_sc_mz_warehouseIds").val();
    var warehouseNames = $("#pvlg_sc_mz_warehouseNames").val();
    var cTitles = $("#pvlg_sc_mz_cTitles").val();// 使用||分割
    var imgUrls = $("#pvlg_sc_mz_imgUrls").val();
    var unitNums = $("#pvlg_sc_mz_unitNums").val();
    // 分割
    var warehouseId_array = new Array(); if(!isnull(warehouseIds)){warehouseId_array = warehouseIds.split(",");}
    var warehouseName_array = new Array(); if(!isnull(warehouseNames)){warehouseName_array = warehouseNames.split(",");}
    var sku_array = new Array(); if(!isnull(skus)){sku_array = skus.split(",");}
    var cTitle_array = new Array(); if(!isnull(cTitles)){cTitle_array = cTitles.split("||");}
    var imgUrl_array = new Array(); if(!isnull(imgUrls)){imgUrl_array = imgUrls.split(",");}
    var unitNum_array = new Array(); if(!isnull(unitNums)){unitNum_array = unitNums.split(",");}

    // 获取每箱的数量
    var unit = $("#pvlg_sc_mz_unit_select").val();
    if(unit=='箱'){
        var sku = $(this).parent().parent().find("input[type='checkbox']:checked").attr("sku");
        var warehouseId = $(this).parent().parent().find("input[type='checkbox']:checked").attr("warehouseId");
        var foundIndex=-1;
        // 检查此sku在原先选中的skus中的位置
        for(var i= 0,length=sku_array.length;i<length;i++){
            if(sku_array[i]==sku && warehouseId_array[i]==warehouseId){
                foundIndex=i;
                break;
            }
        }
        var unitNum = $(this).parent().parent().find('td[name="unitNum_td"]').find("input").val();
        if(foundIndex!=-1){
            unitNum_array[foundIndex] = unitNum;
            $("#pvlg_sc_mz_unitNums").val(unitNum_array.toString());
        }
    }
}

// // 商品属性-满赠的，赠品选中商品后的处理
function pvlg_sc_mz_callbackAfterSelected(){
    var checked = $(this).prop("checked");
    // 获取原先就选中的商品
    var skus = $("#pvlg_sc_mz_skus").val();
    var warehouseIds = $("#pvlg_sc_mz_warehouseIds").val();
    var warehouseNames = $("#pvlg_sc_mz_warehouseNames").val();
    var cTitles = $("#pvlg_sc_mz_cTitles").val();// 使用||分割
    var imgUrls = $("#pvlg_sc_mz_imgUrls").val();
    var unitNums = $("#pvlg_sc_mz_unitNums").val();
    // 分割
    var warehouseId_array = new Array(); if(!isnull(warehouseIds)){warehouseId_array = warehouseIds.split(",");}
    var warehouseName_array = new Array(); if(!isnull(warehouseNames)){warehouseName_array = warehouseNames.split(",");}
    var sku_array = new Array(); if(!isnull(skus)){sku_array = skus.split(",");}
    var cTitle_array = new Array(); if(!isnull(cTitles)){cTitle_array = cTitles.split("||");}
    var imgUrl_array = new Array(); if(!isnull(imgUrls)){imgUrl_array = imgUrls.split(",");}
    var unitNum_array = new Array(); if(!isnull(unitNums)){unitNum_array = unitNums.split(",");}

    // 不组合，就只能选一个商品
    // var combined_checked = $("#condt_specify_product_combined").prop("checked");
    // if(combined_checked==undefined){
    //     // 为undefined，就说明没有可组合这个选项，就不用进行下边的检查
    //     combined_checked = true;
    // }
    // if(!combined_checked){
    //     // 已经为1了
    //     if((sku_array.length == 1) && checked){
    //         layer.msg("在指定商品不可组合的情况下，不能选择多个赠品",{icon:2,time:2000});
    //         $(this).prop("checked",false);
    //         return;
    //     }
    // }

    // 单位
    var unit = $("#pvlg_sc_mz_unit_select").val();
    // 验证是否填了箱规
    if(unit=='箱'){
        var unitNum = $(this).parent().parent().find('td[name="unitNum_td"]').find("input").val();
        if(isnull(unitNum)){
            layer.msg("请填写箱规",{icon:5,time:2000});
            $(this).prop("checked",false);
            return;
        }
    }

    if(checked){// 选中的情况
        if(unit=='箱'){
            var unitNum = $(this).parent().parent().find('td[name="unitNum_td"]').find("input").val();
            unitNum_array.push(unitNum);
        }else{
            unitNum_array.push('0');
        }
        sku_array.push($(this).attr("sku"));
        warehouseId_array.push($(this).attr("warehouseId"));
        warehouseName_array.push($(this).attr("warehouseName"));
        cTitle_array.push($(this).attr("ctitle"));
        imgUrl_array.push($(this).attr("imgUrl"));
    }else{// 不选的情况
        var foundIndex=-1;
        var sku = $(this).attr("sku");
        var warehouseId = $(this).attr("warehouseId");
        // 检查此sku在原先选中的skus中的位置
        for(var i= 0,length=sku_array.length;i<length;i++){
            if(sku_array[i]==sku && warehouseId_array[i]==warehouseId){
                foundIndex=i;
                break;
            }
        }
        // 进行移除操作
        if(foundIndex!=-1){
            unitNum_array.splice(foundIndex,1);
            sku_array.splice(foundIndex,1);
            warehouseId_array.splice(foundIndex,1);
            warehouseName_array.splice(foundIndex,1);
            cTitle_array.splice(foundIndex,1);
            imgUrl_array.splice(foundIndex,1);
        }
    }

    // 保存最新的
    $("#pvlg_sc_mz_skus").attr("title",sku_array.toString()).val(sku_array.toString());
    $("#pvlg_sc_mz_warehouseIds").val(warehouseId_array.toString());
    $("#pvlg_sc_mz_warehouseNames").val(warehouseName_array.toString());
    $("#pvlg_sc_mz_cTitles").val(cTitle_array.join("||"));
    $("#pvlg_sc_mz_imgUrls").val(imgUrl_array.toString());
    $("#pvlg_sc_mz_unitNums").val(unitNum_array.toString());

}
// // 商品属性-满赠的，赠品全选中商品后的处理
function pvlg_sc_mz_callbackAfterSelectedAll(){
    var checked = $(this).prop("checked");
    // 获取原先就选中的商品
    var skus = $("#pvlg_sc_mz_skus").val();
    var warehouseIds = $("#pvlg_sc_mz_warehouseIds").val();
    var warehouseNames = $("#pvlg_sc_mz_warehouseNames").val();
    var cTitles = $("#pvlg_sc_mz_cTitles").val();// 使用||分割
    var imgUrls = $("#pvlg_sc_mz_imgUrls").val();
    var unitNums = $("#pvlg_sc_mz_unitNums").val();
    // 分割
    var warehouseId_array = new Array(); if(!isnull(warehouseIds)){warehouseId_array = warehouseIds.split(",");}
    var warehouseName_array = new Array(); if(!isnull(warehouseNames)){warehouseName_array = warehouseNames.split(",");}
    var sku_array = new Array(); if(!isnull(skus)){sku_array = skus.split(",");}
    var cTitle_array = new Array(); if(!isnull(cTitles)){cTitle_array = cTitles.split("||");}
    var imgUrl_array = new Array(); if(!isnull(imgUrls)){imgUrl_array = imgUrls.split(",");}
    var unitNum_array = new Array(); if(!isnull(unitNums)){unitNum_array = unitNums.split(",");}

    // 不组合，就只能选一个商品
    // var combined_checked = $("#condt_specify_product_combined").prop("checked");
    // if(combined_checked==undefined){
    //     // 为undefined，就说明没有可组合这个选项，就不用进行下边的检查
    //     combined_checked = true;
    // }
    //
    // if(!combined_checked){
    //     // 已经为1了
    //     if((sku_array.length == 1 || sku_array.length==0) && checked){
    //         layer.msg("在指定商品不可组合的情况下，不能选择多个赠品",{icon:2,time:2000});
    //         $(this).prop("checked",false);
    //         $(this).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']").each(function(i,e){
    //             if(sku_array[0] != $(e).attr("sku") || warehouseId_array[0] != $(e).attr("warehouseId")){
    //                 $(e).prop("checked",false);
    //             }
    //         });
    //         return;
    //     }
    // }

    // 单位
    var unit = $("#pvlg_p_mz_unit_select").val();
    // 验证是否填了箱规
    if(unit=='箱'){
        var errText='';
        $(this).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']:checked").each(function(i,e){
            var unitNum = $(e).parent().parent().find('td[name="unitNum_td"]').find("input").val();
            if(isnull(unitNum)){
                errText = '请填写箱规';
                return false;
            }
        });
        if(!isnull(errText)){
            $(this).prop("checked",false);
            $(this).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']:checked").each(function(i,e){
                $(e).prop("checked",false);
            });
            layer.msg(errText,{icon:5,time:2000});
        }
        return;
    }

    if(checked){// 选中的情况
        $(this).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']:checked").each(function(i,e){
            var foundIndex=-1;
            var sku = $(e).attr("sku");
            var warehouseId = $(e).attr("warehouseId");
            // 检查此sku在原先选中的skus中的位置
            for(var i= 0,length=sku_array.length;i<length;i++){
                if(sku_array[i]==sku && warehouseId_array[i]==warehouseId){
                    foundIndex=i;
                    break;
                }
            }
            if(foundIndex==-1){
                if(unit=='箱'){
                    var unitNum = $(e).parent().parent().find('td[name="unitNum_td"]').find("input").val();
                    unitNum_array.push(unitNum);
                }else{
                    unitNum_array.push('0');
                }
                sku_array.push($(e).attr("sku"));
                warehouseId_array.push($(e).attr("warehouseId"));
                warehouseName_array.push($(e).attr("warehouseName"));
                cTitle_array.push($(e).attr("ctitle"));
                imgUrl_array.push($(e).attr("imgUrl"));
            }
        });
    }else{// 不选的情况
        $(this).parent().parent().parent().parent().find("tbody").find("input[type='checkbox']").each(function(i,e){
            var foundIndex=-1;
            var sku = $(e).attr("sku");
            var warehouseId = $(e).attr("warehouseId");
            // 检查此sku在原先选中的skus中的位置
            for(var i= 0,length=sku_array.length;i<length;i++){
                if(sku_array[i]==sku && warehouseId_array[i]==warehouseId){
                    foundIndex=i;
                    break;
                }
            }
            // 进行移除操作
            if(foundIndex!=-1){
                unitNum_array.splice(foundIndex,1);
                sku_array.splice(foundIndex,1);
                warehouseId_array.splice(foundIndex,1);
                warehouseName_array.splice(foundIndex,1);
                cTitle_array.splice(foundIndex,1);
                imgUrl_array.splice(foundIndex,1);
            }
        });
    }

    // 保存最新的
    $("#pvlg_p_mz_skus").attr("title",sku_array.toString()).val(sku_array.toString());
    $("#pvlg_p_mz_warehouseIds").val(warehouseId_array.toString());
    $("#pvlg_p_mz_warehouseNames").val(warehouseName_array.toString());
    $("#pvlg_p_mz_cTitles").val(cTitle_array.join("||"));
    $("#pvlg_p_mz_imgUrls").val(imgUrl_array.toString());
    $("#pvlg_p_mz_unitNums").val(unitNum_array.toString());

}