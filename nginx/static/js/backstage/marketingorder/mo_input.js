/**
 * Created by Administrator on 2016/12/26.
 */
var laypage = undefined;
var layer = undefined;
var refresh = false;
var mo_tempProCollection = {};// 临时选中的
var mo_finalProCollection = {};
var needSelectExpirationText = '当前选择的商品存在多个到期日期，请确认是否需要选择商品到期日期<br>' +
    '如果不需要选择，系统将按照现有业务逻辑选择商品<br>' +
    '如果需要选择，请点击选择到期日期，然后逐步选择相应商品的到期日期数量，' +
    '且您已经选择的数量将被重置。';

function initMoInput(laypageArg, layerArg,moInfo,needExpirationDate){
    laypage = laypageArg
    layer = layerArg
    mo_tempProCollection = {}
    mo_finalProCollection = {}
    if(moInfo){// 复制过来的，要数据回显
        moInfoDisplayedBack(moInfo,needExpirationDate)
    }else{
        mo_getProvinces()
        mo_getShippingMethod()
    }
}

/*选择分销商-弹框*/
function selectMoDitributor() {
    layer.open({
        type: 1,
        title: '选择分销商',
        shadeClose: true,
        move: false,
        area: ['580px', '460px'],
        btn: ['确定', '取消'],
        content: $('#mo_distributor_pop_div'),
        yes: function (index) {
            if ($("#mo_distributor_pop_div").find("table tbody").find("input[type='radio']:checked")[0] != undefined) {
                var $radioObj = $("#mo_distributor_pop_div").find("table tbody").find("input[type='radio']:checked");
                // 检查是否切换分销商
                var distributorSelected = $("#mo_distributor_input").val();
                if(distributorSelected && distributorSelected!=$radioObj.val()){
                    // 清除数据
                    cleanMoInputDatas();
                }

                $("#mo_distributor_input").val($radioObj.val());
                $("#mo_distributorMode_hidden").val($radioObj.attr("distributionMode"))
                $("#mo_comsumerType_hidden").attr($radioObj.attr("comsumerType"))

                mo_showSelectedProducts()
            }
            layer.close(index)
        }
    })

    getInnerDistributors(1)
}

// 获取分销商
function getInnerDistributors(currPage){
    $("#mo_distributor_pop_div").find("table tbody").empty();
    $("#mo_distributor_pagination").empty();
    $("#mo_distributor_pop_div").find("input[type='text']").val("")
    var email = "";
    isaulogin(function (email1) {
        email = email1;
    });
    var params = {
        role: 2,
        currPage: currPage == undefined || currPage == 0 ? 1 : currPage,
        pageSize: 10,
        search : $("#marketing_order_search_value").val().trim(),
        email: email
    };
    // notType: 3 分销商不为内部分销商
    ajax_post("../member/relatedMember", JSON.stringify(params), "application/json",
        function(response) {
            if (response.data) {
                var $tbody = $("#mo_distributor_pop_div").find("table tbody");
                var trHTML = '';
                $.each(response.data.list, function(i, item) {
                    trHTML += '<tr>'+
                        '<td><input name="mo_distributor_radio" '+(item.isFrozen?"disabled":"")+' type="radio" ' +
                        'distributionMode="' + item.distributionMode + '" comsumerType="'+item.comsumerType+'" ' +
                        'value="' + item.email + '"/></td>'+ // 单选的
                        '<td>' + deal_with_illegal_value(item.email)+(item.isFrozen?'【<b style="color: red;">冻结</b>】':"") + '</td>'+
                        '<td>' + deal_with_illegal_value(item.nick) + '</td>'+
                        '<td>' + deal_with_illegal_value(item.telphone) + '</td>'+
                        '</tr>';
                });
                $tbody.html(trHTML);
                init_mo_distributor_pagination(response.data.currPage,response.data.totalPage)
            } else{
                layer.msg("获取分销商数据失败！", {icon : 2, time : 2000});
            }
        }
    );
}

// 分销商列表分页条
function init_mo_distributor_pagination(currPage,totalPage) {
    if ($("#mo_distributor_pagination")[0] != undefined) {
        $("#mo_distributor_pagination").empty();
        laypage({
            cont: 'mo_distributor_pagination',
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
                    getInnerDistributors(obj.curr);
                }
            }
        });
    }
}

// 商品弹窗
function mo_selectProducts_popup() {
    if(!$("#mo_distributor_input").val()){
        layer.msg("请先选择分销商！", {icon: 2, time: 2000});
        mo_tempProCollection = {}
        mo_finalProCollection = {}
        return;
    }
    layer.open({
            type: 1,
            skin: 'layui-layer-demo', //样式类名
            title: false, //不显示标题
            shift: 2,
            shadeClose: false, //开启遮罩关闭
            closeBtn: 0,
            content: $('#mo_product_pop_div'),
            area: ['870px', '550px'],
            btn: ['确认添加', '取消'],
            yes: function (index1) {
                // 选中符合条件的
                if($("#mo_allMatchedProducts").prop("checked")){
                    var dataParams = getSearchProductsInnerParams()
                    var params = {
                        "data":dataParams
                    };
                    var loading_index = layer.load(1, {shade: 0.5});
                    ajax_post_sync("../product/api/getProducts", JSON.stringify(params), "application/json",
                        function(response) {
                            var productList = response.data.result;
                            if(productList.length>0){
                                for(var i= 0,length=productList.length;i<length;i++){
                                    var pro = productList[i]
                                    if(parseInt(pro.stock)>0) {
                                        mo_tempProCollection[mo_getKey(pro.csku, pro.warehouseId)] =
                                            createProductJson(pro.ctitle,
                                                pro.interBarCode, pro.imgUrl, pro.csku, pro.batchNumber,
                                                pro.warehouseId, pro.warehouseName, pro.disPrice,
                                                pro.stock, pro.stock, (pro.microStock ? pro.microStock : 0),
                                                pro.batchNumber, pro.categoryId, pro.cname)
                                    }
                                }
                            }
                            layer.close(loading_index)
                        }
                    );
                }else{
                    $("#mo_product_table tbody").find("input:checked").each(function(i,e){
                        var $tr = $(e).parents("tr")
                        var sku = $tr.find("td[name='sku']").text()
                        var warehouseId = $tr.find("td[name='warehouseName']").attr("warehouseId")
                        var key = mo_getKey(sku, warehouseId)
                        if(parseInt($tr.find("td[name='stock']").text())>0){
                            mo_tempProCollection[key] = createProductJson(
                                $tr.find("td[name='title']").text(),
                                $tr.find("td[name='interBarCode']").attr("interBarCode"),
                                $tr.find("td[name='title']").attr("imgUrl"), sku,
                                $tr.find("input[type='checkbox']").attr("batchNumber"), warehouseId,
                                $tr.find("td[name='warehouseName']").text(),
                                $tr.find("td[name='disPrice']").text(),
                                $tr.find("td[name='stock']").text(),
                                $tr.find("td[name='stock']").text(),
                                $tr.find("td[name='microStock']").text(),
                                $(e).attr("batchNumber"), $(e).attr("categoryId"),
                                $(e).attr("categoryName"), $tr.find("td[name='marketPrice']").text()
                            )
                        }
                    })
                }

                // 判断是否要选择到期日期
                layer.confirm(needSelectExpirationText, {
                    btn: ['不需要选择到期日期', '选择到期日期'] //按钮
                }, function () {
                    // 不需要选择到期日期
                    mo_showSelectedProducts(false)
                    // 获取运送方式
                    mo_getShippingMethod()
                }, function () {
                    // 选择到期日期
                    mo_showSelectedProducts(true)
                    // 获取运送方式
                    mo_getShippingMethod()
                });
            }
        }
    );
    $("#mo_allMatchedProducts").prop("checked",false)
    $("#mo_product_table thead").find("input[type='checkbox']").prop("checked",false)
    mo_searchProductCategories();
    mo_searchProducts(1);
}

// 全选
function mo_product_selectAll(obj){
    var checked = $(obj).prop("checked")
    $("#mo_product_table tbody").find("input[type='checkbox']").prop("checked",checked);
}

// 显示选中的商品
function mo_showSelectedProducts(needExpirationDate){
    $("#selected_products_table tbody").empty()
    layer.closeAll()
    if(needExpirationDate){// 选择到期日期
        // 去查询微仓商品和云仓商品的到期日期
        var selectedProducts = []
        for(var i in mo_tempProCollection){
            selectedProducts.push(mo_tempProCollection[i])
        }
        ajax_post_sync("/sales/mo/expirationdates",JSON.stringify({email:$("#mo_distributor_input").val(),selectedProducts: selectedProducts}), "application/json",function(response){
            var result = response.result;
            if(response.suc && result.length>0){
                for(var i in result){
                    var pro = result[i]
                    var finalKey = mo_getKey(pro.sku, pro.warehouseId, pro.expirationDate)
                    if(!mo_finalProCollection[finalKey]){
                        mo_finalProCollection[finalKey] = pro
                    }
                }
            }else{
                layer.msg("获取商品到期日期失败", {icon: 2,time: 2000});
                mo_tempProCollection = {}
                return
            }
        })
    }else{// 系统默认时间
        for(var k in mo_tempProCollection){
            var pro = mo_tempProCollection[k]
            var finalKey = mo_getKey(pro.sku, pro.warehouseId, pro.expirationDate)
            if(!mo_finalProCollection[finalKey]){
                mo_finalProCollection[finalKey] = pro
            }
        }
    }

    mo_tempProCollection = {}
    var html = ''
    $.each(mo_finalProCollection, function(k, pro){
        var expirationDate = pro.expirationDate ? pro.expirationDate : ""
        html += '<tr>'+
            '<td title="'+pro.title+'">'+
            '<img alt="'+pro.title+'" class="bagproduct-img" src=\"'+urlReplace(pro.imgUrl,pro.sku,"",50,50,100)+'\"/>'+
            '<p style="text-align: left">'+pro.title+'</p>'+
            '</td>'+
            '<td name="interBarCode" interBarCode="'+pro.interBarCode+'">'+deal_with_illegal_value(pro.interBarCode)+'</td>'+
            '<td name="sku">'+pro.sku+'</td>'+
            '<td name="warehouseName" warehouseId="'+pro.warehouseId+'">'+pro.warehouseName+'</td>'+
            '<td name="stock">'+pro.subStock+'</td>'+
            '<td name="expirationDate" expirationDate="' + expirationDate + '">' + deal_with_illegal_value(expirationDate) + '</td>' +
            '<td name="price">'+pro.price+'</td>'+
            '<td class="record_num">'+
            '<span onclick="qtyReducement(this)">－</span>'+
            '<input onblur="mo_inputQty(this)" batchNumber="'+pro.batchNumber+'" style="text-align: center"type="text" value="'+pro.qty+'">'+
            '<span onclick="qtyIncrement(this)">＋</span>'+
            '</td>'+
            '<td name="subtotal">'+(parseFloat(pro.price)*parseInt(pro.qty)).toFixed(2)+'</td>'+
            '<td>'+
            '<span style="cursor: pointer;" onclick="removeProduct(this)">删除</span>'+
            '</td>'+
            '</tr>'
    })

    $("#selected_products_table tbody").html(html)
    moInfo()
}

// 营销单信息
function moInfo(){
    mo_getFreight4AProvince()
    var mo_products_count = 0
    var totalAmountWithOutBbcPostage = 0.00
    var totalAmountWithBbcPostage = 0.00
    var keys = Object.keys(mo_finalProCollection)
    if(keys.length>0){
        $.each(mo_finalProCollection, function(k,pro){
            mo_products_count += parseInt(pro.qty)
            totalAmountWithOutBbcPostage += parseFloat(pro.price) * parseInt(pro.qty)
        })
        // 加上运费
        totalAmountWithBbcPostage = totalAmountWithOutBbcPostage + parseFloat($("#mo_thirdPostfee").text())
    }else{
        // 没有商品，就要将运费设置为0
        $("#mo_thirdPostfee").text(0.00)
    }

    $("#mo_products_count").text(mo_products_count)
    $("#totalAmountWithOutBbcPostage").text(parseFloat(totalAmountWithOutBbcPostage).toFixed(2))
    $("#totalAmountWithBbcPostage").text(totalAmountWithBbcPostage.toFixed(2))
}

// 删除
function removeProduct(obj){
    var $tr = $(obj).parents("tr")
    var warehouseId = $tr.find("td[name='warehouseName']").attr("warehouseId")
    var sku = $tr.find("td[name='sku']").text()
    var expirationDate = $tr.find("td[name='expirationDate']").attr("expirationDate")
    delete mo_finalProCollection[mo_getKey(sku,warehouseId,expirationDate)]
    $tr.remove()
    moInfo()
}

// 查询商品分类
function mo_searchProductCategories(){
    var selectObj = $("#mo_pCategories_select");
    selectObj.empty();
    // 查询商品分类
    ajax_get("/product/api/realCateQuery?level=1", null, null,
        function(data) {
            var cList = data;
            selectObj.append('<option value="" selected>所有商品分类</option>');
            for(var i=0;i<cList.length;i++){
                selectObj.append('<option value="'+cList[i].iid+'">'+cList[i].cname+'</option>');
            }
        }
    );
}

function getSearchProductsInnerParams(){
    var dataParams = {
        'model':$("#mo_distributorMode_hidden").val(),
        "istatus":1,
        "email":$("#mo_distributor_input").val(),
        "warehouseId":2024 // 固定深圳仓的,
    };
    // 商品分类
    var categoryId = $("#mo_pCategories_select").val()
    if(categoryId){
        dataParams['categoryId']=categoryId;
    }

    var search_product_input_value = $.trim($("#mo_searchProducts_searchText").val());
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
    return dataParams;
}
// 查询商品
function mo_searchProducts(curr){
    var $tbody = $("#mo_product_table tbody")
    $tbody.empty();
    $("#mo_product_pagination").empty();

    if(!curr){
        curr = 1;
    }
    var dataParams = getSearchProductsInnerParams()
    dataParams.pageSize = 10
    dataParams.currPage = curr
    var params = {
        "data":dataParams
    };
    var loading_index = layer.load(1, {shade: 0.5});
    ajax_post("../product/api/getProducts", JSON.stringify(params), "application/json",
        function(response) {
            var productList = response.data.result;
            if(productList.length>0){
                var html = ''
                for(var i= 0,length=productList.length;i<length;i++){
                    var pro = productList[i]
                    // 查看数据
                    var notEnoughStock = pro.stock < 1
                    var disabled = notEnoughStock ? 'disabled' : ''
                    // 已选中过
                    var selectedBefore = !disabled && mo_finalProCollection[mo_getKey(pro.csku, pro.warehouseId)]
                    disabled = selectedBefore ? 'disabled' : ''
                    html += '<tr>'+
                        '<td><input '+disabled+' title="'+(notEnoughStock ? "库存不足" : (selectedBefore?"已选择过":""))+'" categoryId="'+pro.categoryId+'" ' +
                        'categoryName="'+pro.cname+'" batchNumber="'+(pro.batchNumber?pro.batchNumber:1)+'" ' +
                        'style="cursor:pointer" type="checkbox" ' + disabled + '></td>' +
                        '<td name="title" imgUrl="'+pro.imageUrl+'" title="'+pro.ctitle+'">'+pro.ctitle+'</td>'+
                        '<td name="sku">'+pro.csku+'</td>'+
                        '<td name="interBarCode" interBarCode="'+pro.interBarCode+'">'+deal_with_illegal_value(pro.interBarCode)+'</td>'+
                        '<td name="warehouseName" warehouseId="'+pro.warehouseId+'">'+pro.warehouseName+'</td>'+
                        '<td name="stock">'+pro.stock+'</td>'+
                        '<td name="microStock">'+(pro.microStock ? pro.microStock : 0)+'</td>'+
                        '<td name="disPrice">'+deal_with_illegal_value(pro.disPrice)+'</td>'+
                        '<td name="marketPrice">'+pro.localPrice+'</td>'+
                        '</tr>';
                }
                $tbody.html(html);
                init_mo_product_pagination(response.data.currPage,response.data.totalPage);
            }else{
                $("#mo_product_pagination").empty();
                layer.msg("暂无符合条件的商品数据", {icon: 2, time: 2000});
            }
            layer.close(loading_index)
        }
    );
}

// 商品分页
function init_mo_product_pagination(currPage,totalPage) {
    if ($("#mo_product_pagination")[0] != undefined) {
        $("#mo_product_pagination").empty();
        laypage({
            cont: 'mo_product_pagination',
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
                    mo_searchProducts(obj.curr);
                }
            }
        });
    }
}

// 生成营销单
function createMO(){
    if(!$("#mo_distributor_input").val()){
        layer.msg("请选择分销商！", {icon : 2, time : 2000});
        return
    }
    var keys = Object.keys(mo_finalProCollection)
    if(keys.length==0){
        layer.msg("请选择要下单的商品！", {icon : 2, time : 2000});
        return
    }
    var receiver = $.trim($("#mo_receiver").val())
    if(!receiver){
        layer.msg("收货人姓名不能为空！", {icon : 2, time : 2000});
        $("#mo_receiver").focus()
        return
    }
    var receiverTel = $.trim($("#mo_receiverTel").val())
    if(!receiverTel){
        layer.msg("手机号码不能为空！", {icon : 2, time : 2000});
        $("#mo_receiverTel").focus()
        return
    }
    if(!checkTel(receiverTel)){
        layer.msg("手机号码格式不正确！", {icon : 2, time : 2000});
        $("#mo_receiverTel").focus()
        return
    }
    var provinceId = $("#mo_provinceId").val()
    if(!provinceId){
        layer.msg("请选择省！", {icon : 2, time : 2000});
        return
    }
    var cityId = $("#mo_cityId").val()
    if(!cityId){
        layer.msg("请选择市！", {icon : 2, time : 2000});
        return
    }
    var regionId = $("#mo_regionId").val()
    if(!regionId){
        layer.msg("请选择区！", {icon : 2, time : 2000});
        return
    }
    var addressDetail = $.trim($("#mo_addressDetail").val())
    if(!addressDetail){
        layer.msg("详细地址不能为空！", {icon : 2, time : 2000});
        $("#mo_addressDetail").focus()
        return
    }
    if(addressDetail.length<5 || addressDetail.length>120){
        layer.msg("详细地址必须大于5个字符，小于120个字符！", {icon : 2, time : 2000});
        $("#mo_addressDetail").focus()
        return
    }
    var logisticsMode = $("#mo_logisticsType").find("option:selected").text()
    if(!logisticsMode){
        layer.msg("请选择运送方式！", {icon : 2, time : 2000});
        return
    }
    var businessRemark = $("#mo_businessRemark").val()
    if(businessRemark.length>200){
        layer.msg("备注字数不能大于200！", {icon : 2, time : 2000});
        $("#mo_businessRemark").focus()
        return
    }

    // {"data":{"email":"","details":[{"sku":"", "product_name":"xxxx"},{},{}]}}
    var params = {
        email: $("#mo_distributor_input").val(),nickName: $("#mo_nickName_hidden").val(),
        provinceId: provinceId,cityId: cityId,regionId: regionId,
        provinceName: $("#mo_provinceId option:selected").text(),cityName: $("#mo_cityId option:selected").text(),
        regionName: $("#mo_regionId option:selected").text(),addressDetail: addressDetail,
        receiver: receiver, receiverPostcode: $("#mo_receiverPostcode").val(),receiverTel:receiverTel,
        logisticsMode: logisticsMode, logisticsTypeCode: $("#mo_logisticsType").find("option:selected").attr("code"),
        bbcPostage: parseFloat($("#mo_thirdPostfee").text()),
        businessRemark: businessRemark,
        totalAmount: parseFloat($("#totalAmountWithOutBbcPostage").text())
    }
    // 要下单的sku详情
    var details = []
    $.each(mo_finalProCollection, function (k, pro) {
        details.push({
            productName: pro.title,
            interBarCode: pro.interBarCode,
            productImg: pro.imgUrl,
            sku: pro.sku,
            categoryId: pro.categoryId,
            categoryName: pro.categoryName,
            qty: pro.qty,
            disPrice: pro.price,
            warehouseId: pro.warehouseId,
            warehouseName: pro.warehouseName,
            expirationDate: pro.expirationDate
        })
    })
    params['details'] = details
    var loading_index = layer.load(1, {shade: 0.5});
    $('button[ name="createMOBtn"]').attr("disabled","disabled")
    ajax_post("/sales/mo/create", JSON.stringify(params), "application/json",
        function(response) {
             if(response.suc){
                 cleanMoInputDatas()
                 layer.msg(response.msg+"，即将跳转到列表页！", {icon : 1, time : 2000},function(){
                     layer.close(loading_index)
                     $('button[ name="createMOBtn"]').removeAttr("disabled")
                     gotoMOList()
                 });
             }else{
                 layer.close(loading_index)
                 $('button[ name="createMOBtn"]').removeAttr("disabled")
                 layer.msg(response.msg, {icon : 2, time : 2000});
             }
        }
    );
}

// 清除数据
function cleanMoInputDatas(){
    mo_tempProCollection = {}
    mo_finalProCollection = {}
    $("#mo_distributor_input").val("")
    $("#mo_nickName_hidden").val("")
    $("#mo_distributorMode_hidden").val("")
    $("#mo_comsumerType_hidden").val("")
    $("#selected_products_table tbody").empty()
    $("#mo_receiver").val("")
    $("#mo_receiverTel").val("")
    $("#mo_provinceId").empty()
    mo_getProvinces()
    $("#mo_cityId").empty()
    $("#mo_regionId").empty()
    $("#mo_addressDetail").val("")
    $("#mo_receiverPostcode").val("")
    $("#mo_products_count").text(0)
    $("#totalAmountWithOutBbcPostage").text(0.00)
    $("#mo_thirdPostfee").text(0.00)
    $("#totalAmountWithBbcPostage").text(0.00)
    $("#mo_businessRemark").val("")
}

function mo_getProvinces(provinceId,cityId,regionId){
    $("#mo_provinceId").empty();
    $("#mo_cityId").empty();
    $("#mo_regionId").empty();

    ajax_get("/member/getprovs", JSON.stringify(""), "application/json",
        function (data) {
            var html = "<option value='' >请选择省</option>";
            for (var i = 0; i < data.length; i++) {
                if(provinceId && provinceId==data[i].id){
                    html += "<option selected='selected' value='" + data[i].id + "' >" + data[i].provinceName + "</option>";
                }else{
                    html += "<option value='" + data[i].id + "' >" + data[i].provinceName + "</option>";
                }
            }
            $("#mo_provinceId").html(html);

            // 有省的话，要把城市带出来
            if(provinceId){
                mo_getCities(cityId,regionId)
            }
        }
    );
}

function mo_getCities(cityId,regionId){
    var proId = $("#mo_provinceId").val();
    if(!proId)
        return;

    // 选择了省后计算运费
    mo_getFreight4AProvince()

    $("#mo_cityId").empty();
    ajax_get("/member/getcities", "proId=" + proId, "",
        function (data) {
            var html = "<option value=''>请选择市</option>"
            for (var i = 0; i < data.cities.length; i++) {
                if(cityId && cityId==data.cities[i].id){
                    html += "<option selected='selected' value='" + data.cities[i].id + "' code='"+data.cities[i].zipCode+"' >" + data.cities[i].cityName + "</option>"
                }else{
                    html += "<option value='" + data.cities[i].id + "' code='"+data.cities[i].zipCode+"' >" + data.cities[i].cityName + "</option>"
                }
            }
            $("#mo_cityId").html(html)

            // 有市的话，要把区带出来
            if(cityId){
                mo_getAreas(regionId)
            }
        }
    );
}

function mo_getAreas(areaId){
    var cityId = $("#mo_cityId").val()
    if(!cityId)
        return
    $("#mo_receiverPostcode").val($("#mo_cityId option:selected").attr("code"))

    $("#mo_regionId").empty()
    ajax_get("/member/getareas", "cityId=" + cityId, "",
        function (data) {
            var html = "<option value=''>请选择区</option>"
            for (var i = 0; i < data.areas.length; i++) {
                if(areaId && areaId==data.areas[i].id){
                    html += "<option value='" + data.areas[i].id + "' >" + data.areas[i].areaName + "</option>"
                }else{
                    html += "<option selected='selected' value='" + data.areas[i].id + "' >" + data.areas[i].areaName + "</option>"
                }
            }
            $("#mo_regionId").html(html)
        }
    );
}

// 获取运送方式
function mo_getShippingMethod(logisticsTypeCode){
    var keys = Object.keys(mo_finalProCollection)
    if(keys.length>0 && !$("#mo_logisticsType").val()){
        $("#mo_logisticsType").empty()
        ajax_get("/inventory/getShippingMethod?wid="+mo_finalProCollection[keys[0]].warehouseId, "", "",
            function (shipResStr) {
                var shipRes = $.parseJSON(shipResStr);
                if (shipRes.length > 0) {
                    var html = ''
                    for (var i = 0; i < shipRes.length; i++) {
                        if(logisticsTypeCode && logisticsTypeCode==shipRes[i].methodCode){
                            html += "<option value='" + shipRes[i].id + "' code='" + shipRes[i].methodCode + "' selected='selected'>" + shipRes[i].methodName + "</option>"
                        }else{
                            if (shipRes[i].default) {
                                html += "<option value='" + shipRes[i].id + "' code='" + shipRes[i].methodCode + "' selected='selected'>" + shipRes[i].methodName + "</option>"
                            } else {
                                html += "<option value='" + shipRes[i].id + "' code='" + shipRes[i].methodCode + "'>" + shipRes[i].methodName + "</option>"
                            }
                        }
                    }
                    $("#mo_logisticsType").html(html)

                    // 计算运费
                    mo_getFreight4AProvince()
                } else {
                    layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
                }
            },
            function (XMLHttpRequest, textStatus) {
                layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
            }
        );
    }
}

// 获取运费
function mo_getFreight4AProvince(){
    var keys = Object.keys(mo_finalProCollection)
    if(keys.length==0){
        return;
    }
    var provinceId = $("#mo_provinceId").val()
    var shippingCode = $("#mo_logisticsType").find("option:selected").attr("code")
    var warehouseId = mo_finalProCollection[keys[0]].warehouseId;

    // 可以出货，且选择了省和运送方式
    if(provinceId && shippingCode && warehouseId){
        var freightParam = {};
        freightParam.warehouseId = warehouseId;
        freightParam.shippingCode = shippingCode;
        freightParam.countryId = 44;//写死为中国的id
        freightParam.provinceId = provinceId;
        var orderDetails = [];
        $.each(mo_finalProCollection, function (i, pro) {
            if (pro.warehouseId == 2024) {
                orderDetails.push({sku: pro.sku, num: pro.qty});
            }
        })
        freightParam.orderDetails = orderDetails;
        ajax_post("/inventory/getFreight", JSON.stringify(freightParam), "application/json",
            function (freightResStr) {
                var freightRes = freightResStr;
                if (freightRes.result) {
                    //自提标识
                    var bbcPostage = freightRes.msg ? freightRes.msg : 0.00;
                    $("#mo_thirdPostfee").text(bbcPostage)

                    // 营销单总金额(包含运费)
                    $("#totalAmountWithBbcPostage").text((parseFloat($("#totalAmountWithOutBbcPostage").text()) + parseFloat(bbcPostage)).toFixed(2))
                } else {
                    layer.msg("运费获取失败！" + freightRes.msg, {icon: 2, time: 2000});
                }
            },
            function (XMLHttpRequest, textStatus) {
                layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
            }
        );
    }
}

// 跳转到列表页
function gotoMOList(){
    var mo_list_menu = $('p[class="back-current"]').parent().find("p:eq(0)");
    $("p[onclick^='load_menu_content']").removeClass("back-current");
    mo_list_menu.addClass("back-current");
    load_menu_content(58);// 进入列表页
}

function qtyIncrement(obj){
    var inputObj = $(obj).parent().find("input")
    var originQty = parseInt(inputObj.val())
    var $tr = $(obj).parents("tr");
    var sku = $tr.find("td[name='sku']").text()
    var warehouseId = $tr.find("td[name='warehouseName']").attr("warehouseId")
    var expirationDate = $tr.find("td[name='expirationDate']").attr("expirationDate")
    var stock = $tr.find("td[name='stock']").text()

    if(originQty == parseInt(stock)){
        layer.msg("数量不能大于云仓库存！", {icon : 2, time : 2000});
        return
    }

    ++originQty
    $(obj).parent().find("input").val(originQty)

    mo_recalculate($tr, sku, warehouseId, expirationDate,originQty)
}

function qtyReducement(obj){
    var inputObj = $(obj).parent().find("input");
    var originQty = parseInt(inputObj.val())
    var $tr = $(obj).parents("tr");
    var sku = $tr.find("td[name='sku']").text()
    var warehouseId = $tr.find("td[name='warehouseName']").attr("warehouseId")
    var expirationDate = $tr.find("td[name='expirationDate']").attr("expirationDate")
    var batchNumber = inputObj.attr("batchNumber")

    if(originQty == batchNumber){
        layer.msg("数量不能小于起批量"+batchNumber+"！", {icon : 2, time : 2000});
        return
    }

    --originQty
    inputObj.val(originQty)

    mo_recalculate($tr, sku, warehouseId, expirationDate,originQty)
}

function mo_inputQty(obj){
    var inputNum = $.trim($(obj).val())
    var batchNumber = $(obj).attr("batchNumber")
    var $tr = $(obj).parents("tr");
    var sku = $tr.find("td[name='sku']").text()
    var warehouseId = $tr.find("td[name='warehouseName']").attr("warehouseId")
    var expirationDate = $tr.find("td[name='expirationDate']").attr("expirationDate")
    var stock = $tr.find("td[name='stock']").text()

    if(!inputNum){
        layer.msg("数量不能为空！", {icon : 2, time : 2000});
        mo_recalculate($tr, sku, warehouseId, expirationDate,batchNumber)
        return;
    }

    var pattern = /^([1-9]\d*)$/;// 正整数
    if (!pattern.test(inputNum)) {
        layer.msg("数量必须是整数！", {icon : 2, time : 2000});
        mo_recalculate($tr, sku, warehouseId, expirationDate,batchNumber)
        return;
    }

    if(parseInt(inputNum) < parseInt(batchNumber)){
        layer.msg("数量不能小于起批量"+batchNumber+"！", {icon : 2, time : 2000});
        mo_recalculate($tr, sku, warehouseId, expirationDate,batchNumber)
        return;
    }

    if(parseInt(inputNum) > parseInt(stock)){
        layer.msg("数量不能大于云仓库存！", {icon : 2, time : 2000});
        mo_recalculate($tr, sku, warehouseId, expirationDate,stock)
        return;
    }

    mo_recalculate($tr, sku, warehouseId, expirationDate,inputNum)
}

function mo_recalculate($tr, sku,warehouseId, expirationDate,inputNum){
    var key = mo_getKey(sku, warehouseId, expirationDate)
    var pro = mo_finalProCollection[key]
    if(pro){
        // 重新计算金额
        $tr.find("td[name='subtotal']").text(
            (parseInt(inputNum) * parseFloat(pro.price)).toFixed(2)
        )
        pro.qty = parseInt(inputNum)
    }

    moInfo()
}

// 查看erp库存
$("body").on("click","#show_erpsto",function(){
    show_erpsto();
});

// 查看erp库存
function show_erpsto(){
    var keys = Object.keys(mo_finalProCollection)
    if(!keys.length){
        layer.msg("请选择商品",{icon:5,time:2000});
        return;
    }

    var skuMap = {}, skus = [];
    $.each(mo_finalProCollection,function(k,item){
        var sku = item.sku;
        if(skuMap[sku]){
            skuMap[sku].qty = parseInt(skuMap[sku].qty) + parseInt(item.qty)// 数量叠加
        }else{
            skuMap[sku] = {
                sku: sku,
                title: item.title,
                warehouseName: item.warehouseName,
                qty: item.qty,
                erpStock: 0,
                short: true,
                interBarCode: item.interBarCode
            }
        }
        skus.push(sku);
    });
    erpStoHtml(erpSto({warehouseId: mo_finalProCollection[keys[0]].warehouseId, skus: skus}, skuMap));
}

// 退货单数据回显
function moInfoDisplayedBack(moInfo,needExpirationDate){
    if(moInfo){
        console.log("复制营销单，数据回显")
        var email = moInfo.email
        var disMode = moInfo.disMode
        var distributorType = moInfo.distributorType
        var detailList = moInfo.detailList // 详情
        // 第一步：选择分销商
        $("#mo_distributor_input").val(email);
        $("#mo_distributorMode_hidden").val(disMode)
        $("#mo_comsumerType_hidden").val(distributorType)
        // 第二步：选择商品
        var sku2Detail = {} // 防止sku重复
        for(var i in detailList){
            sku2Detail[detailList[i].sku] = detailList[i]
        }
        var params = {"data": {'model':disMode, "istatus":1, "email":email, "warehouseId":2024, "skuList":Object.keys(sku2Detail)}};
        var loading_index = layer.load(1, {shade: 0.5});
        var productInfoJson = {}
        ajax_post_sync("../product/api/getProducts", JSON.stringify(params), "application/json",
            function(response) {
                var productList = response.data.result;
                if(productList.length>0){
                    for(var i= 0,length=productList.length;i<length;i++){
                        var pro = productList[i]
                        productInfoJson[mo_getKey(pro.csku, pro.warehouseId)] = pro
                    }
                }
                layer.close(loading_index)
            }
        );
        $.each(productInfoJson, function(i,pro){
            mo_tempProCollection[mo_getKey(pro.csku, pro.warehouseId)] = {
                title: pro.ctitle,
                interBarCode: pro.interBarCode ? pro.interBarCode : "",
                imgUrl: pro.imageUrl ? pro.imageUrl : "",
                sku: pro.csku,
                qty: (pro.batchNumber ? pro.batchNumber : 1),
                warehouseId: pro.warehouseId,
                warehouseName: pro.warehouseName,
                price: pro.disPrice,
                stock: pro.stock,
                subStock: pro.stock,
                microStock: (pro.microStock ? pro.microStock : 0),
                batchNumber: (pro.batchNumber ? pro.batchNumber : 1),
                categoryId: pro.categoryId,
                categoryName: pro.cname,
                marketPrice: pro.localPrice
            }
        })
        mo_showSelectedProducts(needExpirationDate)

        // 第三步：收货信息
        $("#mo_receiver").val(moInfo.receiver) // 收货人姓名
        $("#mo_receiverTel").val(moInfo.receiverTel) // 手机号码
        mo_getProvinces(moInfo.provinceId,moInfo.cityId,moInfo.regionId) // 收货地址
        $("#mo_addressDetail").val(moInfo.addressDetail)
        $("#mo_receiverPostcode").val(moInfo.receiverPostcode) // 邮政编码
        // 运送方式
        var logisticsTypeCode = moInfo.logisticsTypeCode
        mo_getShippingMethod(logisticsTypeCode)

        // 备注
        $("#mo_businessRemark").val(moInfo.businessRemark)
    }
}

// 参数：titleArg, interBarCodeArg, imgUrlArg, skuArg, qtyArg, warehouseIdArg, warehouseNameArg, priceArg, stockArg, microStockArg, batchNumberArg, categoryIdArg, categoryNameArg
function createProductJson(titleArg, interBarCodeArg, imgUrlArg, skuArg, qtyArg, warehouseIdArg, warehouseNameArg, priceArg,
                           stockArg, subStockArg, microStockArg, batchNumberArg, categoryIdArg, categoryNameArg, marketPriceArg){
    return {
        title: titleArg, interBarCode: interBarCodeArg, imgUrl: imgUrlArg, sku: skuArg, qty: qtyArg,
        warehouseId: warehouseIdArg, warehouseName: warehouseNameArg, price: priceArg, stock: stockArg, subStock: subStockArg,
        microStock: microStockArg, batchNumber: batchNumberArg, categoryId: categoryIdArg, categoryName: categoryNameArg,
        marketPrice: marketPriceArg
    }
}

function mo_getKey(sku, warehouseId, expirationDate){
    var key = sku
    if(warehouseId){
        key = key + "_" + warehouseId
    }
    if(expirationDate){
        key = key + "_" + expirationDate
    }
    return key;
}
