var layer,laypage,type = 'TOTAL';

//初始化商品价格列表
function init_pro_price(lay,layp,BbcGrid){
    layer = lay;
    laypage = layp;
    init_realCate();
    init_warehouse();
    init_func();
    init_brand();
    init_pro_type();
    var postData = $("#price_table").jqGrid("getGridParam", "postData");
    if(postData&&postData.length>0){
        $.each(postData, function (k, v) {
            delete postData[k];
        });
    }
    var grid = new BbcGrid();
    grid.initTable($("#price_table"),getSetting());
    $("#price_table").jqGrid('setFrozenColumns');

}
//初始化所有事件绑定
function init_func () {
     $("#price_search").click(function(){
        searchPrice();
    });
    $("#price_search_key").keydown(function (e) {
        if (e.keyCode == 13) {
            searchPrice();
        }
    });
    $("#price_pageSize,#cate_price_select,#ware_price_select,#price_brand_select,#price_type").change(function(){
        searchPrice();
    })
    $("input[tag=min]").blur(function(){
        var node = $(this);
        var price = node.val().trim();
        if(price == ''){
            return; 
        }
        if(!checkPrice(price) || !parseFloat(price)){
            layer.msg("请输入正确金额",{icon:5,time:2000});
            node.val("");
            return; 
        }
        var maxPrice = node.next().val();
        if(maxPrice){
            if(parseFloat(price) >= parseFloat(maxPrice)){
                layer.msg("最低价必须小于最高价",{icon:5,time:2000});
                node.val("");
                return;
            }
        }
        node.val(price);
    });
    $("input[tag=max]").blur(function(){
        var node = $(this);
        var price = node.val().trim();
        if(price == ''){
            return; 
        }
        if(!checkPrice(price) || !parseFloat(price) ){
            layer.msg("请输入正确金额",{icon:5,time:2000});
            node.val("");
            return; 
        }
        var minPrice = node.prev().val();
        if(minPrice){
            if(parseFloat(price) <= parseFloat(minPrice)){
                layer.msg("最高价必须高于最低价",{icon:5,time:2000});
                node.val("");
                return;
            }
        }
        node.val(price);
    });
    $("#select_all,#select_curr_all").click(function(){
        var node = $(this);
        var total = $("#price_table").jqGrid("getGridParam", "records");
        var priceTotal  = parseInt(total);
        if(node.prop("checked")){
            $("#price_table #checkbox_one").prop("checked",true);
        }else{
            $("#price_table #checkbox_one").prop("checked",false);
        }
        if(!priceTotal){
           node.prop("checked",false); 
           layer.msg("搜索记录数为0时不能全选",{icon:5,time:2000});
        }
    });

    //更新获取分类数据
    $('.refresh_Brand_data').click(function(){
        refresh_Brand_data();
    });

    //操作日志
    $('.priceCheck_log').click(function () {
        view_price_record(null,false);
        $(".productPrice_box").hide();
        $(".defaultLog_box").show();
    })
    $('#record_cate_select,#price_record_pageSize').change(function(){
        view_price_record(null,false);
    });

    $("#price_record_key").keydown(function (e) {
        if (e.keyCode == 13) {
            view_price_record(null, false);
        }
    });
    $("#price_record_search").click(function(){
        view_price_record(null, false);
    });
    $("#rule_log_key").keydown(function (e) {
        if (e.keyCode == 13) {
            view_rule_log(null, false);
        }
    });
    $("#rule_log_search").click(function(){
        view_rule_log(null, false);
    });
    $("#class_select,#rule_log_pageSize,#rule_status").change(function(){
        view_rule_log(null, false);
    });


    //默认价格设置
    $('.defaultPrice').click(function () {
        $(".productPrice_box").hide();
        $(".defaultPrice_box").show();
        view_price_factor(null,false);
    })
   $("#rule_cate_select,#brand_status_select,#brand_pageSize").change(function(){
        view_price_factor(null, false);
    });
   $("#brand_search").click(function(){
        view_price_factor(null, false);
    });
   $("#brand_key").keydown(function (e) {
        if (e.keyCode == 13) {
            view_price_factor(null, false);
        }
    });
    $(".check_ProductPrice").click(function(){
        if(hasNull){
            hasNull = false;
            $(this).css("background","#283442");
        }else{
            hasNull = true;
            $(this).css("background","rgb(79, 122, 171)");
        }
        searchPrice();
    });

}

/**
 * Created by Administrator on 2016/7/29.
 */
$(document).ready(function () {
    $(function () {
        if (!placeholderSupport()) {
            $('[placeholder]').focus(function () {
                var input = $(this);
                if (input.val() == input.attr('placeholder')) {
                    input.val('');
                    input.removeClass('placeholder');
                }
            }).blur(function () {
                var input = $(this);
                if (input.val() == '' || input.val() == input.attr('placeholder')) {
                    input.addClass('placeholder');
                    input.val(input.attr('placeholder'));
                }
            }).blur();
        }
    })
    function placeholderSupport() {
        return 'placeholder' in document.createElement('input');
    }
})

//刷新分类数据
function refresh_Brand_data(){
    ajax_get(
        "/product/fixprice/initData",undefined,"",
        function(data) {
            var msgHtml =   '<div class="refresh_pop ">'+
                            '<p>'+data.msg+'</p>'+
                            '</div>';
             layer.open({
                type: 1,
                skin: 'layui-layer-rim',
                area: ['420px', '140px'],
                content: msgHtml,
                time: 4000 ,
                title:'获取分类数据'
            })
            view_price_factor(null,false);
        },
        function(e){
            console.log(e);
        }
    );
}

//价格系数操作日志
function brand_log(id){
    var param = {
        categoryBrandId:id
    }
    ajax_post("/product/fixprice/rulelog",
        JSON.stringify(param),
        "application/json",
        function(response){
            if(response.suc){
                var pages = response.pages;
                var list = pages.list;
                var total = pages.totalCount;
                var logHtml = '<div class="add_Brand_data_logPop">';
                if(total >0 ){
                    $.each(list, function (i,item) {
                        logHtml +=  '<p><span>'+item.operateTimeStr+'</span>'+
                                    '管理员<em>'+item.operate+'</em>将<em>'+deal_with_illegal_value(item.categoryName)+'</em>'+
                                    '分类'+item.priceClassification+'系数设置为<b class="red">'+(item.factor?item.factor:"空")+'</b></p>';
                    });
                }
                logHtml += '</div>';
                layer.open({
                    type: 1,
                    skin: 'layui-layer-rim',
                    title:'设置系数',
                    content: logHtml,
                    area: ['560px', '520px'],
                    btn:['确定']

                });
            }else{
                layer.msg(response.msg,{icon:2,time:2000});
            }
        },
        function(e){
            console.log(e);
        }
    );     
}

$('body').on("click", ".defaultPrice_back,.defaultLog_back", function () {
    searchPrice();
    $(".productPrice_box").show();
    $(".defaultLog_box").hide();
    $(".defaultPrice_box").hide();
})

function getvalue(val){
    return val?val:undefined;
}

//初始化真实类目，目前先做以及类目
function init_realCate(){
    ajax_get(
        "/product/api/realCateQuery?level=1",undefined,"",
        function(data) {
            var _select = $("#cate_price_select,#record_cate_select,#rule_cate_select");
            _select.empty();
            var opHtml = '<option value="">所有商品分类</option>';
            $.each(data,function(i,item){
                opHtml += '<option value="'+item.iid+'">'+item.cname+'</option>'; 
            });
            _select.append(opHtml);
        },function(e){
            console.log(e);
        }
    );    
}
function init_brand(){
    ajax_get(
        "/product/api/getBrand",undefined,"",
        function(data) {
            var _select = $("#brand_selct,#price_brand_select");
            _select.empty();
            var opHtml = '<option value="">所有品牌</option>';
            $.each(data,function(i,item){
                opHtml += '<option value="'+item+'">'+item+'</option>'; 
            });
            _select.append(opHtml);
        },function(e){
            console.log(e);
        }
    ); 
}

//zdd by zbc 商品类别
function init_pro_type(){
    ajax_get(
        "/product/api/getAllTypes",undefined,"",
        function(data) {
            var _select = $("#price_type");
            _select.empty();
            var opHtml = '<option value="" >所有商品类别</option>';
            $.each(data,function(i,item){
                opHtml += '<option value="'+item.id+'">'+item.name+'</option>'; 
            });
            _select.append(opHtml);
        },function(e){
            console.log(e);
        }
    ); 
}


function init_warehouse(){
    ajax_get(
        "/inventory/queryWarehouse",undefined,"",
        function(data) {
            $("#ware_price_select").empty();
            var opHtml = '<option value="">所有仓库</option>';
            $.each(data,function(i,item){
                opHtml += '<option value="'+item.warehouseId+'">'+item.warehouseName+'</option>'; 
            });
            $("#ware_price_select").append(opHtml);
        }
        ,function(e){
            console.log(e);
        }
    );
}

//获取弹出框HTML内容
function modify_brand_data(brand, call) {
    var cateOpt = '',brandOpt = '<option value = "">无品牌</option>' ;
    $.ajax({
        url: "/product/api/realCateQuery?level=1",
        type: "get",
        dataType: "json",
        async: false,
        success: function (data) {
            $.each(data,function(i,item){
                cateOpt += '<option value="'+item.iid+'" '+(brand.categoryId?(brand.categoryId == item.iid?"selected":""):"")+'>'+item.cname+'</option>'; 
            });
        }
    });
    $.ajax({
        url: "/product/api/getBrand",
        type: "get",
        dataType: "json",
        async: false,
        success: function (data) {
            $.each(data,function(i,item){
                brandOpt += '<option value="'+item+'" '+(brand.brand?(brand.brand == item?"selected":""):"")+'>'+item+'</option>'; 
            });
        }
    });
    call(  
        '<div class="add_Brand_data_pop">'
        + '<div><em>分类:</em>' 
        + '<div><select id="add_cate_select">'+cateOpt+'</select></div></div>'
        + '<div><em>品牌:</em>'
        + '<div><select id="add_brand_select">'+brandOpt+'</select></div></div>'
        + '<div><em>最低价系数:</em>'
        + '<div><input type="text" id = "f_factor" tag = "ckf" kind = "floorPrice" value = "'+(brand.f_factor?brand.f_factor:'')+'" /></div></div>'
        + '<div><em>零售价系数:</em>'
        + '<div><input type="text" id = "p_factor" tag = "ckf" kind = "proposalRetailPrice" value = "'+(brand.p_factor?brand.p_factor:'')+'" /></div></div>'
        + '<div><em>经销商价格系数:</em>'
        + '<div><input type="text" id = "d_factor" tag = "ckf" kind = "distributorPrice" value = "'+(brand.d_factor?brand.d_factor:'')+'" /></div></div>'
        + '<div><em>Bbc价格系数:</em>'
        + '<div><input type="text" id = "e_factor" tag = "ckf" kind = "electricityPrices"  value = "'+(brand.e_factor?brand.e_factor:'')+'" /></div></div>'
        + '<div><em>KA经销价格系数:</em>'
        + '<div><input type="text" id = "s_factor" tag = "ckf" kind = "supermarketPrice" value = "'+(brand.s_factor?brand.s_factor:'')+'" /></div></div>'
        + '<div><em>状态:</em>'
        + '<div>'
        + '<label style="margin-right:15px;"><input type="radio" value = true '+(brand.status?"checked = 'checked'":"")+'  name="status">应用中</label>'
        + '<label><input type="radio" value = false name="status" '+(brand.status?"":"checked = 'checked'")+' >未应用</label>'
        + '<input type = "hidden"  value = '+(brand.id?brand.id:"")+' name = "id" >'
        + '</div></div>'
        + '</div>'
    )    
}
function getRule(type){
    return type == 'floorPrice'?'到仓价*市场最低价系数':
        type == 'proposalRetailPrice'?'到仓价*零售价系数':
            type == 'distributorPrice'?'市场最低价/（1-经销商价格系数）':
                type == 'electricityPrices'?'市场最低价/（1-Bbc价格系数）':
                    type == 'supermarketPrice'?'零售价*KA经销价格系数':'';
}
//编辑默认系数
function setFactor(obj,id){
    isaulogin(function(email){
        var f_factor = $("#"+id+"_floorPrice").text();
        var p_factor = $("#"+id+"_proposalRetailPrice").text();
        var d_factor = $("#"+id+"_distributorPrice").text();
        var e_factor = $("#"+id+"_electricityPrices").text();
        var s_factor = $("#"+id+"_supermarketPrice").text();
        var t_factor = $("#"+id+"_disCompanyCost").text();
        var m_factor = $("#"+id+"_marketInterventionPrice").text();
        var ftz_factor = $("#"+id+"_ftzPrice").text();   
        var status = $("#"+id+"_status").data("status");
        var factorHtml = '<div class="coefficient_pop">'+
            '<table>'+
                '<thead>'+
                    '<tr>'+
                        '<th style="width: 20%;">价格种类</th><th style="width: 60%;">计算公式</th><th style="width: 20%;">系数设置</th>'+
                    '</tr>'+
                '</thead>'+
                '<tbody>'+
                '<tr>'+
                '<td style="width: 20%;">营销成本价(元)</td>'+
                '<td style="width: 60%;">到仓价*营销成本价系数 </td>'+
                '<td style="width: 20%;"><input  id = "t_factor" tag="ckf" type="text" kind = "disCompanyCost" value = "'+(t_factor?t_factor:1.05)+'" /></td>'+
                '</tr>'+
                '<tr>'+
                    '<td style="width: 20%;" >最低价(元)</td>'+
                    '<td style="width: 60%;">到仓价*最低价系数</td>'+
                    '<td style="width: 20%;"><input  id = "f_factor" tag="ckf" type="text" kind = "floorPrice" value = "'+(f_factor?f_factor:1.1)+'"/></td>'+
                '</tr>'+
                '<tr>'+
                '<td style="width: 20%;">市场干预供货价(元)</td>'+
                '<td style="width: 60%;">到仓价*市场干预供货价系数 </td>'+
                '<td style="width: 20%;"><input  type="text" id = "m_factor" tag = ckf kind = "marketInterventionPrice" value = "'+(m_factor?m_factor:1.15)+'" /></td>'+
                '</tr>'+
                '<tr>'+
                    '<td style="width: 20%;">零售价(元)</td>'+
                    '<td style="width: 60%;">到仓价*零售价系数</td>'+
                    '<td style="width: 20%;"><input  id = "p_factor" tag="ckf" type="text" kind = "proposalRetailPrice" value = "'+(p_factor?p_factor:4)+'" /></td>'+
                '</tr>'+
                '<tr>'+
                    '<td style="width: 20%;">经销商价格(元)</td>'+
                    '<td style="width: 60%;">零售价*经销商价格系数</td>'+
                    '<td style="width: 20%;"><input  id = "d_factor" tag="ckf" type="text" kind = "distributorPrice" value = "'+(d_factor?d_factor:0.5)+'" /></td>'+
                '</tr>'+
                '<tr>'+
                '<td style="width: 20%;">自贸区经销价格(元)</td>'+
                '<td style="width: 60%;">零售价*自贸区经销价格系数</td>'+
                '<td style="width: 20%;"><input  type="text" id = "ftz_factor" tag = "ckf" kind = "ftzPrice" value = "'+(ftz_factor?ftz_factor:0.6)+'" /></td>'+
                '</tr>'+
                '<tr>'+
                    '<td style="width: 20%;">Bbc价格(元)</td>'+
                    '<td style="width: 60%;">零售价*Bbc价格系数</td>'+
                    '<td style="width: 20%;"><input id = "e_factor"  tag="ckf" type="text" kind = "electricityPrices" value = "'+(e_factor?e_factor:0.65)+'" /></td>'+
                '</tr>'+
                '<tr>'+
                    '<td style="width: 20%;">KA经销价格(元)</td>'+
                    '<td style="width: 60%;">零售价*KA经销价格系数</td>'+
                    '<td style="width: 20%;"><input  id = "s_factor" tag="ckf" type="text" kind = "supermarketPrice" value = "'+(s_factor?s_factor:0.8)+'" /></td>'+
                '</tr>'+
                '</tbody>'+
            '</table>'+
            '<div>'+
                '<em>状态:</em>'+
                '<div>'+
                    '<label style="margin-right:15px;">'+
                    '<input type="radio" name="status"  value = true '+(status?"checked = 'checked'":"")+' >应用中</label>'+
                    '<label><input type="radio" name="status" value = false '+(status?"":"checked = 'checked'")+'>未应用</label>'+
                '</div>'+
            '</div>'+
        '</div>';
        layer.open({
            type: 1,
            skin: 'layui-layer-rim',
            title:'设置系数',
            content: factorHtml,
            area: ['650px', '600px'],
            btn:['保存','取消'],
            success:function(){
                $('.coefficient_pop').on('keyup', 'input[tag=ckf]', function (event) {
                    var words = $(this).val();
                    var keyCode = event.which;
                    if (keyCode == 46 || (keyCode >= 48 && keyCode <= 57)) {
                        return true;
                    } else {
                        words = words.replace(/\s+/g, "");
                        $(this).val(words.replace(/[^(\d(\.\d{3})?]/g, ""));
                    }
                })
                $('.coefficient_pop').on('blur', 'input[tag=ckf]', function (event) {
                    var node = $(this);
                    var factor = node.val();
                    if(factor == "" || factor == 1){
                        return;
                    }
                    var type = node.attr("kind");
                    var ckParam = getReg(type);
                    var desc = node.parent().parent().find("td").eq(0).text();
                    if(!ckParam.reg.test(factor)){
                        layer.msg(desc+"格式错误,"+ckParam.msg,{icon:2,time:2000});
                        return;
                    }
                })  
            },
            yes:function(index){
                var flag = false;
                var factorMap = {};
                $.each($(".coefficient_pop input[tag=ckf]"),function(i,item){
                    var node = $(this);
                    var factor = node.val();
                    var type = node.attr("kind");
                    var ckParam = getReg(type);
                    var desc = node.parent().parent().find("td").eq(0).text();
                    if(factor != "" && factor != 1 && !ckParam.reg.test(factor)){
                        layer.msg(desc+"格式错误,"+ckParam.msg,{icon:2,time:2000});
                        flag = true;
                        return true;
                    }
                    factorMap[type] = factor;
                });
                if(flag){
                    return; 
                }
                var param = {
                    lastOperator:email,
                    id:id,
                    factorMap:factorMap,
                    status:$(".coefficient_pop input[name=status]:checked").val()
                }
                var shade = layer.load(1, {shade: 0.5});
                ajax_post("/product/fixprice/addfactor",
                    JSON.stringify(param),
                    "application/json",
                    function(response){
                        layer.close(shade);
                        if(response.suc){
                            layer.msg(response.msg,{icon:6,time:2000},function(){
                                $.each(factorMap,function(key,value){
                                    $("#"+id+"_"+key).text(value);
                                });
                                $("#"+id+"_status").text((param.status == "true"?"应用中":"未应用"));
                                $("#"+id+"_status").data("status",(param.status == "true"?true:false));
                                layer.close(index);
                            });
                        }else{
                            layer.msg(response.msg,{icon:5,time:2000});
                        }
                    },function(e){
                        layer.close(shade);
                        console.log(e);
                    }
                );
            },
            cancel:function(index){
                layer.close(index);
            }
        });
    });
}
//获取正则
function getReg(type){
   var reg,msg;
   if( type == 'proposalRetailPrice' || type == 'floorPrice' 
    || type == 'marketInterventionPrice' || type == 'disCompanyCost'){
        reg = /^([1-9][0-9]*)(.[0-9]{1,3})?$/;//大于1的数字
        msg = "必须是大于1的数字";
    }else{
        reg = /^0(.[0-9]{1,3})$/;//小于1的小数
        msg = "必须是大于0小于1的小数(最多三位小数)";
    }
    return {reg:reg,msg:msg};
}
//校验金额有效性
function checkAmount(am){
    //最多两位小数(可正可负)
    var reg = /^([-|+][0-9])?([0-9]*)(.[0-9]{1,2})?$/;
    return reg.test(am);
}

/*
 * @param red  为true：设置常规价格，为false设置销售价格
 */
function one_key_price(red){
    isaulogin(function (email) {
        var priceTotal  = parseInt($("#priceTotal").text());
        var select = $("#price_table #checkbox_one:checked");
        var select_all_flag  = $("#select_all").prop("checked");
        if(!select.length && !(select_all_flag && priceTotal)){
            layer.msg("请选择商品",{icon:5,time:2000});
            return;
        }
        var _setHtml =  '<div class="A_set_pop structure_pop marketPop">'+
            '<p>'+
            '<span class="marketPopCurrent" tag = 1 >系数设置</span>'+
            '<span tag = 2 >数值设置</span>'+
            '</p>';
        var height;
        var url = "/product/fixprice/bcsetprice";
        //change by zbc 栏目 区分类型，设置价格
        switch(type){
            case 'TOTAL':{
                height = (red?'220px':'315px');
                _setHtml += 
                    '<div>'+
                    '<div class="marketPopCon">'+
                    (red?'<div><em>市场最低价系数：</em><div><input placeholder = "到仓价*市场最低价系数" kind = "floorPrice" id = "dis_factor" tag = "ckf" type="text"></div></div>'+
                    '<div><em>零售价系数：</em><div><input placeholder = "到仓价*零售价系数"  kind = "proposalRetailPrice"  tag = "ckf" type="text"></div></div>':
                    '<div><em>经销商价格系数：</em><div><input placeholder = "零售价*经销商价格系数" kind = "distributorPrice" tag = "ckf" type="text"></div></div>'+
                    '<div><em>自贸区经销价格系数：</em><div><input placeholder = "零售价*自贸区经销价格系数"  kind = "ftzPrice"  tag = "ckf" type="text"></div></div>'+
                    '<div><em>Bbc价格系数：</em><div><input placeholder = "零售价*Bbc价格系数" kind = "electricityPrices" tag = "ckf"  type="text"></div></div>'+
                    '<div><em>KA经销价格系数：</em><div><input placeholder = "零售价*KA经销价格系数" kind = "supermarketPrice" tag = "ckf" type="text"></div></div>'+
                    '<div><em>VIP价格系数：</em><div><input placeholder = "零售价*VIP价格系数" kind = "vipPrice" tag = "ckf" type="text"></div></div>')+
                    '</div>'+
                    '<div class="marketPopCon" style="display: none;">'+
                    (red?'<div><em>市场最低价数值：</em><div><input placeholder = "到仓价+市场最低价数值" kind = "floorPrice" id = "dis_factor" tag = "ckf" type="text"></div></div>'+
                    '<div><em>零售价数值：</em><div><input placeholder = "到仓价+零售价数值"  kind = "proposalRetailPrice"  tag = "ckf" type="text"></div></div>':
                    '<div><em>经销商价格数值：</em><div><input placeholder = "零售价-经销商价格数值" kind = "distributorPrice" tag = "ckf" type="text"></div></div>'+
                    '<div><em>自贸区经销价格数值：</em><div><input placeholder = "零售价-自贸区经销价格数值"  kind = "ftzPrice"  tag = "ckf" type="text"></div></div>'+
                    '<div><em>Bbc价格数值：</em><div><input placeholder = "零售价-Bbc价格数值" kind = "electricityPrices" tag = "ckf"  type="text"></div></div>'+
                    '<div><em>KA经销价格数值：</em><div><input placeholder = "零售价-KA经销价格数值" kind = "supermarketPrice" tag = "ckf" type="text"></div></div>'+
                    '<div><em>VIP价格数值：</em><div><input placeholder = "零售价-VIP价格数值" kind = "vipPrice" tag = "ckf" type="text"></div></div>')+
                    '</div>'+
                    '</div>';
            }
            break;
            case 'BASE':{
                url += "/base";
                height = '220px';
                _setHtml +=
                    '<div>'+
                    '<div class="marketPopCon">'+
                    '<div><em>市场最低价系数：</em><div><input placeholder = "到仓价*市场最低价系数" kind = "floorPrice" id = "dis_factor" tag = "ckf" type="text"></div></div>'+
                    '<div><em>零售价系数：</em><div><input placeholder = "到仓价*零售价系数"  kind = "proposalRetailPrice"  tag = "ckf" type="text"></div></div>'+
                    '</div>'+
                    '<div class="marketPopCon" style="display: none;">'+
                    '<div><em>市场最低价数值：</em><div><input placeholder = "到仓价+市场最低价数值" kind = "floorPrice" id = "dis_factor" tag = "ckf" type="text"></div></div>'+
                    '<div><em>零售价数值：</em><div><input placeholder = "到仓价+零售价数值"  kind = "proposalRetailPrice"  tag = "ckf" type="text"></div></div>'+
                    '</div>'+
                    '</div>';
            }
            break; 
            case 'DIS':{
                url += "/dis";
                height = '180px';
                _setHtml +=
                    '<div>'+
                    '<div class="marketPopCon"><em>经销商价格系数：</em><span><input placeholder = "零售价*经销商价格系数" kind = "distributorPrice" tag = "ckf" type="text"></span></div>'+
                    '<div class="marketPopCon" style="display: none;"><em>经销商价格数值：</em><span><input placeholder = "零售价-经销商价格数值" kind = "distributorPrice" tag = "ckf" type="text"></span></div>'+
                    '</div>'
                    ;
            }
            break;
            case 'FTZ':{
                url += "/ftz";
                height = '180px';
                _setHtml +=
                    '<div>'+
                    '<div class="marketPopCon"><em>自贸区经销价格系数：</em><span><input placeholder = "零售价*自贸区经销价格系数"  kind = "ftzPrice"  tag = "ckf" type="text"></span></div>'+
                    '<div class="marketPopCon" style="display: none;"><em>自贸区经销价格数值：</em><span><input placeholder = "零售价-自贸区经销价格数值"  kind = "ftzPrice"  tag = "ckf" type="text"></span></div>'+
                    '</div>';
            }
            break;
            case 'ELE':{
                url += "/ele";
                height = '180px';
                _setHtml +=
                    '<div>'+
                    '<div class="marketPopCon"><em>Bbc价格系数：</em><span><input placeholder = "零售价*Bbc价格系数" kind = "electricityPrices" tag = "ckf"  type="text"></span></div>'+
                    '<div class="marketPopCon" style="display: none;"><em>Bbc价格数值：</em><span><input placeholder = "零售价-Bbc价格数值" kind = "electricityPrices" tag = "ckf"  type="text"></span></div>'+
                    '</div>';
            }
            break;
            case 'SUP':{
                url += "/sup";
                height = '180px';
                _setHtml +=
                    '<div>'+
                    '<div class="marketPopCon"><em>KA经销价格系数：</em><span><input placeholder = "零售价*KA经销价格系数" kind = "supermarketPrice" tag = "ckf" type="text"></span></div>'+
                    '<div class="marketPopCon" style="display: none;"><em>KA经销价格数值：</em><span><input placeholder = "零售价-KA经销价格数值" kind = "supermarketPrice" tag = "ckf" type="text"></span></div>'+
                    '</div>'
                ;
            }
            break;
            case 'VIP':{
                url += "/vip";
                height = '180px';
                _setHtml +=
                    '<div>'+
                    '<div class="marketPopCon"><em>VIP价格系数：</em><span><input placeholder = "零售价*VIP价格系数" kind = "vipPrice" tag = "ckf" type="text"></span></div>'+
                    '<div class="marketPopCon" style="display: none;"><em>VIP价格数值：</em><span><input placeholder = "零售价-VIP价格数值" kind = "vipPrice" tag = "ckf" type="text"></span></div>'+
                    '</div>'
                ;
            }
        }
        _setHtml += '</div>';
        layer.open({
            type: 1,
            skin: 'layui-layer-rim',
            area: ['540px', height],
            content: _setHtml,
            btn:['保存','取消'],
            title:false,
            move:false,
            success:function(){
                var  reg,kind;//0到1之间的小数
                var title = "";
                //切换事件
                marketPopTab();
                $(".structure_pop").on("blur","input[tag=ckf]",function(){
                    var tag = $(".marketPopCurrent").attr("tag");
                    var node = $(this);
                    var factor = node.val().trim();
                    title = node.parent().siblings("em").text();
                    if(tag == 1){
                        if(factor == '' || factor == 1){
                            return;
                        }
                        kind = node.attr("kind");
                        reg = getReg(kind).reg;
                        if(!reg.test(factor)){
                            layer.msg(title+"格式错误"+getReg(kind).msg,{icon:5,time:2000});
                            node.val("");
                        }else{
                            node.val(factor);
                            check_factor();
                        }
                    }else{
                        if(factor == ''){
                            return;
                        }
                        if(!checkAmount(factor)){
                            layer.msg(title+"格式错误,必须为数字并且最多两位小数",{icon:5,time:2000});
                            node.val("");
                        }
                    }
                });
            },
            yes: function (index) {
                var idList = new Array();
                $.each($("#price_table #checkbox_one:checked"), function (i,item) {
                    idList.push($(item).data("id"));
                });
                var ckParam = check_factor();
                if(!ckParam.red){
                    layer.msg(ckParam.msg+"格式错误,"+ckParam.tip,{icon:5,time:2000});
                    return;
                }
                var tag = $(".marketPopCurrent").attr("tag");
                var $content = (tag == 1?$(".marketPopCon:eq(0) input[tag=ckf]"):
                $(".marketPopCon:eq(1) input[tag=ckf]"));
                var map = {};
                $.each($content,function(i,item){
                    var node = $(this);
                    var factor = node.val().trim();
                    var kind = node.attr("kind");
                    if(factor){
                        map[kind] = factor
                    }
                });
                var param;
                //标识，全部搜索
                if(select_all_flag){
                    var param = {
                        idAll:true,
                        changeFactorMap:map,
                        operator:email,
                        minFloorPrice:$("#min_f_price").val(),
                        maxFloorPrice:$("#max_f_price").val(),
                        minProposalRetailPrice:$("#min_p_price").val(),
                        maxProposalRetailPrice:$("#max_p_price").val(),
                        minDistributorPrice:$("#min_d_price").val(),
                        maxDistributorPrice:$("#max_d_price").val(),
                        minElectricityPrices:$("#min_e_price").val(),
                        maxElectricityPrices:$("#max_e_price").val(),
                        minSupermarketPrice:$("#min_s_price").val(),
                        maxSupermarketPrice:$("#max_s_price").val(),
                        minMarketInterventionPrice:$("#min_m_price").val(),
                        maxMarketInterventionPrice:$("#max_m_price").val(),
                        minFtzPrice:$("#min_ftz_price").val(),
                        maxFtzPrice:$("#max_ftz_price").val(),
                        key:$("#price_search_key").val().trim(),
                        warehouseId:$("#ware_price_select").val(),
                        categoryId:$("#cate_price_select").val(),
                        brand:$("#price_brand_select").val(),
                        hasNull:hasNull,
                        typeId:$("#price_type").val(),
                        //设置类型，系数/利润值 
                        setType:(tag==1?'FR':'PF')
                    };
                }else{
                    param = {
                        priceIidList:idList,
                        operator:email,
                        changeFactorMap:map,
                        //设置类型，系数/利润值 
                        setType:(tag==1?'FR':'PF')
                    }
                }
                var shade = layer.load(1, {shade: 0.5});
                ajax_post(url,
                    JSON.stringify(param),
                    "application/json",
                    function(response){
                        if(response.suc){
                            layer.msg(response.msg,{icon:6,time:2000});
                            layer.close(index);
                            searchPrice();
                        }else{
                            layer.msg(response.msg,{icon:2,time:2000});
                        }
                        layer.close(shade);
                    },function(e) {
                        console.log(e);
                        layer.close(shade);
                    }
                );
            },
            cancel:function (index) {

            }
        });
    });
}
/*tab切换*/
function marketPopTab() {
    var $marketPopTab = $('.marketPop p span');
    var $marketPopCon = $('.marketPopCon');

    $marketPopTab.mouseover(function () {
        var $this = $(this);
        var $t = $this.index();
        $marketPopTab.removeClass();
        $this.addClass('marketPopCurrent');
        $marketPopCon.css('display', 'none');
        $marketPopCon.eq($t).css('display', 'block');
    })
}
//一键设置校验参数
function check_factor(){
    var flag = true,factor,kind,reg;
    var title = "";
    var tag = $(".marketPopCurrent").attr("tag");
    //change by zbc 两种校验规则
    var $content = (tag == 1?$(".marketPopCon:eq(0) input[tag=ckf]"):
        $(".marketPopCon:eq(1) input[tag=ckf]"));
    $.each($content, function (i,item) {
        factor = $(item).val().trim();
        kind = $(item).attr("kind");
        title = $(item).parent().siblings("em").text();
        if(tag == 1){
            reg = getReg(kind).reg;
            if(factor != "" && factor != 1 && !reg.test(factor)){
                  flag = false;  
                  return false;
            };
        }else{
            if(factor != "" && !checkAmount(factor)){
                flag = false;
                return false;
            }
        }
    });
    return {red:flag,msg:title,tip:(tag == 1?getReg(kind).msg:'必须为数字并且最多两位小数')};
}

//单个设置价格
function setPrice(obj){
    var id = $(obj).attr("id");
    isaulogin(function (email) {
         //取商品信息
        ajax_get(
            "/product/fixprice/getprice",{id : id},"",
            function(data) {
                if(!data.suc){
                    layer.msg(data.msg,{icon:5,time:2000});
                    return;
                }
                var pri = data.pri;
                var priceMap = data.priceMap;
                var factorMap = data.factorMap;
                var fitPriceMap =  data.fitPriceMap;
                var fitMap = data.fitMap;
                var height;
                var url = "/product/fixprice/setprice"
                var content =  
                    '<div class="defaultPrice_pop marketPop">'+
                    /*'<b class="red">'+
                    '到仓价：<em>'+pri.arriveWarePrice+'</em>'+
                    '</b>'+*/
                    '<table>'+
                    '<thead>'+
                    '<tr><th>价格种类</th><th>计算公式</th><th>系数/数值</th><th>系数/数值对应价格</th><th>当前价格(元)</th><th>价格设置</th><th>价格调整</th></tr>'+
                    '</thead>'+
                    '<tbody>';
                //change by zbc 根据栏目，区分类型，设置价格
                switch(type){
                    case 'TOTAL':{
                        height = '600px';
                        content +=
                            '<tr><td rowspan="2">市场最低价</td><td>到仓价*市场最低价系数</td><td>'+deal_with_illegal_json(factorMap.floorPrice)+'</td><td>'+deal_with_illegal_json(priceMap.floorPrice)+'</td><td rowspan="2">'+comToFixed(pri.floorPrice,2)+'</td>'+
                                '<td rowspan="2"><input name = "floorPrice" tag = "ck"  type="text" ></td><td></td></tr>'+
                            '<tr><td>到仓价+市场最低价数值</td><td>'+deal_with_illegal_json(fitMap.floorPrice)+'</td><td>'+deal_with_illegal_json(fitPriceMap.floorPrice)+'</td><td></td></tr>'+
                            '<tr><td rowspan="2">零售价</td><td>到仓价*零售价系数</td><td>'+deal_with_illegal_json(factorMap.proposalRetailPrice)+'</td><td>'+deal_with_illegal_json(priceMap.proposalRetailPrice)+'</td><td rowspan="2" >'+comToFixed(pri.proposalRetailPrice,2)+'</td>'+
                                '<td rowspan="2" ><input name = "proposalRetailPrice" tag = "ck" type="text"></td><td></td></tr>'+
                            '<tr><td>到仓价+零售价数值</td><td>'+deal_with_illegal_json(fitMap.proposalRetailPrice)+'</td><td>'+deal_with_illegal_json(fitPriceMap.proposalRetailPrice)+'</td><td></td></tr>'+
                            '<tr><td rowspan="2">经销商价格</td><td>零售价*经销商价格系数</td><td>'+deal_with_illegal_json(factorMap.distributorPrice)+'</td><td>'+deal_with_illegal_json(priceMap.distributorPrice)+'</td><td rowspan="2" >'+comToFixed(pri.distributorPrice,2)+'</td>'+
                                '<td rowspan="2" ><input name = "distributorPrice" tag = "ck" type="text"></td><td></td></tr>'+
                            '<tr><td>零售价-经销商价格数值</td><td>'+deal_with_illegal_json(fitMap.distributorPrice)+'</td><td>'+deal_with_illegal_json(fitPriceMap.distributorPrice)+'</td><td></td></tr>'+
                            '<tr><td rowspan="2">自贸区经销价格(元)</td><td>零售价*自贸区经销价格系数</td><td>'+deal_with_illegal_json(factorMap.ftzPrice)+'</td><td>'+deal_with_illegal_json(priceMap.ftzPrice)+'</td><td  rowspan="2" >'+comToFixed(pri.ftzPrice,2)+'</td>'+
                                '<td rowspan="2" ><input name = "ftzPrice" tag = "ck" type="text"></td><td></td></tr>'+
                            '<tr><td>零售价-自贸区经销价格数值</td><td>'+deal_with_illegal_json(fitMap.ftzPrice)+'</td><td>'+deal_with_illegal_json(fitPriceMap.ftzPrice)+'</td><td></td><td></td></tr>'+
                            '<tr><td rowspan="2">Bbc价格(元)</td><td>零售价*Bbc价格系数</td><td>'+deal_with_illegal_json(factorMap.electricityPrices)+'</td><td>'+deal_with_illegal_json(priceMap.electricityPrices)+'</td><td rowspan="2">'+comToFixed(pri.electricityPrices,2)+'</td>'+
                                '<td rowspan="2" ><input name = "electricityPrices" tag = "ck" type="text"></td><td></td></tr>'+
                            '<tr><td>零售价-Bbc价格数值</td><td>'+deal_with_illegal_json(fitMap.electricityPrices)+'</td><td>'+deal_with_illegal_json(fitPriceMap.electricityPrices)+'</td><td></td></tr>'+
                            '<tr><td rowspan="2">KA经销价格(元)</td><td>零售价*KA经销价格系数</td><td>'+deal_with_illegal_json(factorMap.supermarketPrice)+'</td><td>'+deal_with_illegal_json(priceMap.supermarketPrice)+'</td><td rowspan="2">'+comToFixed(pri.supermarketPrice,4)+'</td>'+
                                '<td rowspan="2" ><input name = "supermarketPrice" tag = "ck" type="text"></td><td></td></tr>'+
                            '<tr><td>零售价-KA经销价格数值</td><td>'+deal_with_illegal_json(fitMap.supermarketPrice)+'</td><td>'+deal_with_illegal_json(fitPriceMap.supermarketPrice)+'</td><td></td></tr>'+
                            '<tr><td rowspan="2">VIP价格(元)</td><td>零售价*VIP价格系数</td><td>'+deal_with_illegal_json(factorMap.vipPrice)+'</td><td>'+deal_with_illegal_json(priceMap.vipPrice)+'</td><td rowspan="2">'+comToFixed(pri.vipPrice,2)+'</td>'+
                                '<td rowspan="2" ><input name = "vipPrice" tag = "ck" type="text"></td><td></td></tr>'+
                            '<tr><td>零售价-VIP价格数值</td><td>'+deal_with_illegal_json(fitMap.vipPrice)+'</td><td>'+deal_with_illegal_json(fitPriceMap.vipPrice)+'</td><td></td></tr>';
                    }
                    break;
                    case 'BASE':{
                        url += "/base";
                        height = '320px';
                        content +=
                            '<tr><td rowspan="2">市场最低价</td><td>到仓价*市场最低价系数</td><td>'+deal_with_illegal_json(factorMap.floorPrice)+'</td><td>'+deal_with_illegal_json(priceMap.floorPrice)+'</td><td rowspan="2">'+comToFixed(pri.floorPrice,2)+'</td>'+
                                '<td rowspan="2"><input name = "floorPrice" tag = "ck"  type="text" ></td><td></td></tr>'+
                            '<tr><td>到仓价+市场最低价数值</td><td>'+deal_with_illegal_json(fitMap.floorPrice)+'</td><td>'+deal_with_illegal_json(fitPriceMap.floorPrice)+'</td><td></td></tr>'+
                            '<tr><td rowspan="2">零售价</td><td>到仓价*零售价系数</td><td>'+deal_with_illegal_json(factorMap.proposalRetailPrice)+'</td><td>'+deal_with_illegal_json(priceMap.proposalRetailPrice)+'</td><td rowspan="2" >'+comToFixed(pri.proposalRetailPrice,2)+'</td>'+
                                '<td rowspan="2" ><input name = "proposalRetailPrice" tag = "ck" type="text"></td><td></td></tr>'+
                            '<tr><td>到仓价+零售价数值</td><td>'+deal_with_illegal_json(fitMap.proposalRetailPrice)+'</td><td>'+deal_with_illegal_json(fitPriceMap.proposalRetailPrice)+'</td><td></td></tr>';
                    }
                    break;
                    case 'DIS':{
                        url += "/dis";
                        height = '260px';
                        content +=
                             '<tr><td rowspan="2">经销商价格</td><td>零售价*经销商价格系数</td><td>'+deal_with_illegal_json(factorMap.distributorPrice)+'</td><td>'+deal_with_illegal_json(priceMap.distributorPrice)+'</td><td rowspan="2" >'+comToFixed(pri.distributorPrice,2)+'</td>'+
                                '<td rowspan="2" ><input name = "distributorPrice" tag = "ck" type="text"></td><td></td></tr>'+
                            '<tr><td>零售价-经销商价格数值</td><td>'+deal_with_illegal_json(fitMap.distributorPrice)+'</td><td>'+deal_with_illegal_json(fitPriceMap.distributorPrice)+'</td><td></td></tr>';
                    }
                    break;
                    case 'FTZ':{
                        url += "/ftz";
                        height = '260px';
                        content +=
                            '<tr><td rowspan="2">自贸区经销价格(元)</td><td>零售价*自贸区经销价格系数</td><td>'+deal_with_illegal_json(factorMap.ftzPrice)+'</td><td>'+deal_with_illegal_json(priceMap.ftzPrice)+'</td><td  rowspan="2" >'+comToFixed(pri.ftzPrice,2)+'</td>'+
                            '<td rowspan="2" ><input name = "ftzPrice" tag = "ck" type="text"></td><td></td></tr>'+
                            '<tr><td>零售价-自贸区经销价格数值</td><td>'+deal_with_illegal_json(fitMap.ftzPrice)+'</td><td>'+deal_with_illegal_json(fitPriceMap.ftzPrice)+'</td><td></td><td></td></tr>';
                    }
                    break;
                    case 'ELE':{
                        url += "/ele";
                        height = '260px';
                        content +=
                            '<tr><td rowspan="2">Bbc价格(元)</td><td>零售价*Bbc价格系数</td><td>'+deal_with_illegal_json(factorMap.electricityPrices)+'</td><td>'+deal_with_illegal_json(priceMap.electricityPrices)+'</td><td rowspan="2">'+comToFixed(pri.electricityPrices,2)+'</td>'+
                                '<td rowspan="2" ><input name = "electricityPrices" tag = "ck" type="text"></td><td></td></tr>'+
                            '<tr><td>零售价-Bbc价格数值</td><td>'+deal_with_illegal_json(fitMap.electricityPrices)+'</td><td>'+deal_with_illegal_json(fitPriceMap.electricityPrices)+'</td><td></td></tr>';
                    }
                    break;
                    case 'SUP':{
                        url += "/sup";
                        height = '260px';
                        content +=
                           '<tr><td rowspan="2">KA经销价格(元)</td><td>零售价*KA经销价格系数</td><td>'+deal_with_illegal_json(factorMap.supermarketPrice)+'</td><td>'+deal_with_illegal_json(priceMap.supermarketPrice)+'</td><td rowspan="2">'+comToFixed(pri.supermarketPrice,4)+'</td>'+
                                '<td rowspan="2" ><input name = "supermarketPrice" tag = "ck" type="text"></td><td></td></tr>'+
                            '<tr><td>零售价-KA经销价格数值</td><td>'+deal_with_illegal_json(fitMap.supermarketPrice)+'</td><td>'+deal_with_illegal_json(fitPriceMap.supermarketPrice)+'</td><td></td></tr>';
                    }
                    break;
                    case 'VIP':{
                        url += "/vip";
                        height = '260px';
                        content +=
                            '<tr><td rowspan="2">VIP价格(元)</td><td>零售价*VIP价格系数</td><td>'+deal_with_illegal_json(factorMap.vipPrice)+'</td><td>'+deal_with_illegal_json(priceMap.vipPrice)+'</td><td rowspan="2">'+comToFixed(pri.vipPrice,2)+'</td>'+
                            '<td rowspan="2" ><input name = "vipPrice" tag = "ck" type="text"></td><td></td></tr>'+
                            '<tr><td>零售价-VIP价格数值</td><td>'+deal_with_illegal_json(fitMap.vipPrice)+'</td><td>'+deal_with_illegal_json(fitPriceMap.vipPrice)+'</td><td></td></tr>';
                    }
                }
                content +=  '</tbody></table></div>';
                comSetPrice(email,obj,content,height,pri,url);
            },
            function(e) {
                console.log(e)
            }
        );  
    });
}

//设置价格公共方法
function comSetPrice(email,obj,content,height,pri,url){
    var id = $(obj).attr("id");
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['880px', height],
        content: content,
        btn:['保存','取消'],
        title:'设置价格',
        success:function(){
            $(".defaultPrice_pop").on("blur","input[tag=ck]",function(){
                var node = $(this);
                var price = node.val().trim();
                var parent = node.parent();
                if(price == ""){
                    parent.next().text("")
                    return;
                }
                var title = parent.siblings().eq(0).text();
                var inputName = $(parent).find('input').attr('name');
                if(inputName == 'supermarketPrice') {
                    if(!checkKaPrice(price) || !parseFloat(price)) {
                        layer.msg(title+"格式错误,请输入正确金额(至多四位小数)",{icon:5,time:2000});
                        parent.next().text("");
                        return;
                    }
                }else{
                    if(!checkPrice(price) || !parseFloat(price)) {
                        layer.msg(title+"格式错误,请输入正确金额(至多两位小数)",{icon:5,time:2000});
                        parent.next().text("");
                        return;
                    }
                }
                var oldPrice = parseFloat(parent.prev().text()?parent.prev().text():0.00);
                price = parseFloat(price);
                parent.next().text(parseFloat(price - oldPrice).toFixed(2));
                node.val(price);
            })
        },
        yes: function (index) {
            var flag = true;
            var title = "";
            var kaFlag = true;
            $.each($(".defaultPrice_pop input[tag=ck]"), function (i,item) {
                var price = $(item).val();
                if(price == ""){
                    return true;
                }

                var node = $(this);
                var parent = node.parent();
                var inputName = $(parent).find('input').attr('name');
                if(inputName == 'supermarketPrice') {
                    if(!checkKaPrice(price) || !parseFloat(price)) {
                        title = $(item).parent().siblings().eq(0).text();
                        kaFlag = false;
                    }
                }else{
                    if(!checkPrice(price) || !parseFloat(price)) {
                        title = $(item).parent().siblings().eq(0).text();
                        flag = false;
                    }
                }
            });
            if(!kaFlag) {
                layer.msg(title+"格式错误,请输入正确金额(至多四位小数)",{icon:5,time:2000});
                return;
            }
            if(!flag){
                layer.msg(title+"格式错误,请输入正确金额(至多两位小数)",{icon:5,time:2000});
                return;
            }
            var  changeMap = {};
            $.each($(".defaultPrice_pop input[tag=ck]"),function(i,item){
                var key = $(item).attr("name");
                if($(item).val().trim() != ""){
                    changeMap[key] = $(item).val().trim();
                }
            });
            var node = $($(obj).parent().parent());
            var rowid = Number($(obj).data("rowid"));
            var rowData = $("#price_table").jqGrid("getRowData")[rowid];
            var param = {
                priceIid:id,
                sku:rowData.sku,
                productTitle:rowData.productTitle,
                categoryName:rowData.categoryName,
                categoryId:rowData.categoryId,
                operator:email,
                changeMap:changeMap,
                warehouseId:rowData.disStockId,
                warehouseName:rowData.warehoseName
            }
            ajax_post(url,
                JSON.stringify(param),
                "application/json",
                function(response){
                    if(response.suc){
                        layer.msg(response.msg,{icon:6,time:2000},function(index){
                            $("#price_table").setRowData(id,changeMap);
                            layer.close(index);
                        });
                        layer.close(index);
                    }else{
                        layer.msg(response.msg,{icon:2,time:2000});
                    }
                }, 
                function(XMLHttpRequest, textStatus) {
                    layer.msg("设置价格异常",{icon:2,time:2000});
            });
        },
        cancel: function (index) {
            layer.close(index);
        }

    });
}

function checkPrice (price) {
    var regMoney = /^(0|[1-9][0-9]*)(.[0-9]{1,2})?$/;
    return regMoney.test(price);
}

function checkKaPrice (price) {
    var regMoney = /^(0|[1-9][0-9]*)(.[0-9]{1,4})?$/;
    return regMoney.test(price);
}

//操作日志
function price_log(id){
     //取商品信息
    ajax_get(
        "/product/fixprice/readpricelog",{priceIid : id,tp:type},"",
        function(data) {
            var _logHtml = '<div class="price_log_pop">';
            $.each(data, function (i,item) {
                _logHtml += '<div><em>'+item.operateTimeStr+'</em>管理员<b>'+item.operator+'</b>将'+
                                item.operateDesc+'设置为<d>'+(item.changePrice?item.changePrice:'空')+'</d>。</div>';
            });
            _logHtml += '</div>';
            layer.open({
                type: 1,
                skin: 'layui-layer-rim',
                area: ['540px', '450px'],
                content: _logHtml,
                btn:['确定','取消'],
                title:'操作日志'
            });
        },
        function(e) {
            console.log(e);
        }
    ); 
}
var hasNull = false;
function searchPrice(){
    // 拿到原有的，清除掉
    var postData = $("#price_table").jqGrid("getGridParam", "postData");
    $.each(postData, function (k, v) {
        delete postData[k];
    });
    $("#price_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}

function getColNames(){
    var colNames = ["","商品编号","商品名称","商品分类","类目id","品牌","仓库","仓库id"];
    var colDiff = undefined;
    switch(type){
        case 'TOTAL':{
            colDiff = 
                ["到岸价(元)","到仓价(元)", "市场最低价(元)","零售价(元)",
                "经销商价格(元)","自贸区经销价格(元)","Bbc价格(元)","KA经销价格(元)","VIP价格"];
        }
        break;
        case 'BASE':{
            colDiff = ["到岸价(元)","到仓价(元)", "市场最低价(元)","零售价(元)"];
        }
        break;
        case 'DIS':{
            colDiff = ["零售价(元)","经销商价格(元)"];
        }
        break;
        case 'FTZ':{
            colDiff = ["零售价(元)","自贸区经销价格(元)"];
        }
        break;
        case 'ELE':{
            colDiff = ["零售价(元)","Bbc价格(元)"];
        }
        break;
        case 'SUP':{
            colDiff = ["零售价(元)","KA经销价格(元)"];
        }
        break;
         case 'VIP':{
            colDiff = ["零售价(元)","VIP价格"];
        }
        break;
    }
    return colNames.concat(colDiff);
}

function colMap(name,sort){
    return {name:name,index:name,align:"center",sortable:sort,
        formatter:function(cellvalue, options, rowObject){return comToFixed(cellvalue,2);}};
}

function colMap_with_ka(name,sort){
    return {name:name,index:name,align:"center",sortable:sort,
        formatter:function(cellvalue, options, rowObject){return comToFixed(cellvalue,4);}};
}

function getColModel(){
    var colModel = [{name:"chk",index:"id",align:"center",sortable:false,forzen:true},
            {name:"sku",index:"sku",align:"center",sortable:false,forzen:true},
            {name:"productTitle",index:"productTitle",align:"center",sortable:false},
            {name:"categoryName",index:"categoryName",align:"center",sortable:false},
            {name:"categoryId",index:"categoryId",align:"center",sortable:false,hidden:true},
            {name:"brand",index:"brand",align:"center",sortable:false},
            {name:"warehoseName",index:"warehoseName",align:"center",sortable:false},
             {name:"disStockId",index:"disStockId",align:"center",sortable:false,hidden:true}];
    var colDiff = undefined;
    switch(type){
        case 'TOTAL':{
            colDiff = 
                [colMap("cost",true),
                colMap("arriveWarePrice",true),
                colMap("floorPrice",true),
                colMap("proposalRetailPrice",true),
                colMap("distributorPrice",true),
                colMap("ftzPrice",true),
                colMap("electricityPrices",true),
                colMap_with_ka("supermarketPrice",true),
                colMap("vipPrice",true)];
        }
        break;
        case 'BASE':{
            colDiff = 
                [colMap("cost",true), colMap("arriveWarePrice",true), colMap("floorPrice",true),colMap("proposalRetailPrice",true)];
        }
        break;
        case 'DIS':{
            colDiff =  [colMap("proposalRetailPrice",true), colMap("distributorPrice",true)];
        }
        break;
        case 'FTZ':{
            colDiff = [colMap("proposalRetailPrice",true), colMap("ftzPrice",true)];
        }
        break;
        case 'ELE':{
            colDiff = [colMap("proposalRetailPrice",true), colMap("electricityPrices",true)];
        }
        break;
        case 'SUP':{
            colDiff = [colMap("proposalRetailPrice",true),colMap_with_ka("supermarketPrice",true)];
        }
        break;
         case 'VIP':{
            colDiff = [colMap("proposalRetailPrice",true),colMap("vipPrice",true)];
        }
        break;
    }
    return  colModel.concat(colDiff);
}


// 价格列表查询参数
function viewPriceParam(pageNo){
    var typeId;
    var ctypeFlag = $('#ctypeFlag').val();
    if((type == 'TOTAL'&& ctypeFlag == '1' )|| type == 'ELE') {
        typeId = 3;
    } else {
        typeId = $("#price_type").val();
    }
    var param = {
        key: $("#price_search_key").val().trim(),
        minCost:$("#min_cost").val(),
        maxCost:$("#max_cost").val(),
        minFloorPrice:$("#min_f_price").val(),
        maxFloorPrice:$("#max_f_price").val(),
        minProposalRetailPrice:$("#min_p_price").val(),
        maxProposalRetailPrice:$("#max_p_price").val(),
        minDistributorPrice:$("#min_d_price").val(),
        maxDistributorPrice:$("#max_d_price").val(),
        minElectricityPrices:$("#min_e_price").val(),
        maxElectricityPrices:$("#max_e_price").val(),
        minSupermarketPrice:$("#min_s_price").val(),
        maxSupermarketPrice:$("#max_s_price").val(),
        minArriveWarePrice:$("#min_a_price").val(),
        maxArriveWarePrice:$("#max_a_price").val(),
        minFtzPrice:$("#min_ftz_price").val(),
        maxFtzPrice:$("#max_ftz_price").val(),
        minVipPrice:$("#min_v_price").val(),
        maxVipPrice:$("#max_v_price").val(),
        warehouseId:$("#ware_price_select").val(),
        categoryId:$("#cate_price_select").val(),
        brand:$("#price_brand_select").val(),
        hasNull:hasNull,
        type:type,
        typeId:typeId,
        pageNo:$('#price_table').getGridParam('page'),
        pageSize:$('#price_table').getGridParam('rowNum'),
        sort:$('#price_table').getGridParam("sortname"),
        filter:$('#price_table').getGridParam("sortorder") 
    };
    return  param;
}
function getSetting() {
    var setting = {
        url:"/product/fixprice/read",
        datatype : "json", // 返回的数据类型
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        colNames:getColNames(),
        colModel:getColModel(),
        viewrecords : true,
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#price_pagination",//分页
        pagerpos : "center",
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        caption:"商品价格",//表名称,
        jsonReader:{
            root: "pages.list",  //数据模型
            page: "pages.pageNo",//数据页码
            total: "pages.totalPages",//数据总页码
            records: "pages.totalCount",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        },
        serializeGridData : function(postData) {
            return JSON.stringify(viewPriceParam());
        }
    }
    
    setting.colNames.push("操作")
    setting.colModel.push({name:"opt",index:"id",align:"center",sortable:false})
    setting.gridComplete = function(){
        var ids = $("#price_table").jqGrid("getDataIDs");
        for(var i in ids){
            $("#price_table").jqGrid("setRowData", ids[i], 
                {opt:'<a  id="'+ids[i]+'" data-rowid='+i+' onclick="setPrice(this)">设置价格</a><br /><a  onclick="price_log('+ids[i]+')">操作日志</a>'});
            $("#price_table").jqGrid("setRowData", ids[i],{ chk: '<input id = "checkbox_one" data-id= "'+ids[i]+'"  type="checkbox">'});
        }
        $("#select_curr_all").prop("checked",false);
        var select_flag = $("#select_all").prop("checked");
        if(select_flag){
             $("#price_table #checkbox_one").prop("checked",true);
        }
        $("#price_table").on("click","a,input",function(){
            $(this).parents("td").click();
        });
    }

    return setting;
}

//bbc价格导出
function export_bbc_price() {
    var headName = [];
    var heads = ['sku','productTitle','categoryName',
        'typeName',
        'brand',
        'warehoseName','proposalRetailPrice','distributorPrice', "cloudStock"]
    $.each(heads,function(i,item){
        headName.push("header=" + item);
    });
    headName.push("key=" + $("#price_search_key").val().trim());
    headName.push("minCost=" + $("#min_cost").val());
    headName.push("maxCost=" + $("#max_cost").val());
    headName.push("minFloorPrice="+$("#min_f_price").val());
    headName.push("maxFloorPrice=" + $("#max_f_price").val());
    headName.push("minProposalRetailPrice="+$("#min_p_price").val());
    headName.push("maxProposalRetailPrice="+$("#max_p_price").val());
    headName.push("minDistributorPrice="+$("#min_d_price").val());
    headName.push("maxDistributorPrice="+$("#max_d_price").val());
    headName.push("maxDistributorPrice="+$("#max_d_price").val());
    headName.push("minElectricityPrices="+$("#min_e_price").val());
    headName.push("maxElectricityPrices="+$("#max_e_price").val());
    headName.push("minSupermarketPrice="+$("#min_s_price").val());
    headName.push("maxSupermarketPrice="+$("#max_s_price").val());
    headName.push("minArriveWarePrice="+$("#min_a_price").val());
    headName.push("minArriveWarePrice="+$("#min_a_price").val());
    headName.push("maxArriveWarePrice="+$("#max_a_price").val());
    headName.push("maxArriveWarePrice="+$("#max_a_price").val());
    headName.push("minFtzPrice="+$("#min_ftz_price").val());
    headName.push("maxFtzPrice="+$("#max_ftz_price").val());
    headName.push("minVipPrice="+$("#min_v_price").val());
    headName.push("maxVipPrice="+$("#max_v_price").val());
    headName.push("warehouseId="+$("#ware_price_select").val());
    headName.push("categoryId="+$("#cate_price_select").val());
    headName.push("brand="+$("#price_brand_select").val());
    headName.push("hasNull="+hasNull);
    headName.push("type="+type);
    headName.push("typeId="+$("#price_type").val());
    if(headName.length > 0 ){
        window.location.href = "/product/fixprice/exportBbcPrice?" + headName.join("&");
    }
}
