var layer = undefined;
var laypage = undefined;
var tempProCollection = {};//页面已选发货商品
var posTopAndLeft;
var distributorType = 1;//分销商类型，默认是1（普通）
var provinceIdForFreight = 0;//完税产品计算运费用的省id
var offline = false;
var customer_account = "";
var yjfWxUrl = "";// 易极付微信url
var prov_map,city_map,region_map;

//未提交前保存缓存
function bufferMemory(){
    //记录隐藏元素
    var hideParams = [];
    if($(".outLine").is(":hidden")){
        hideParams.push(".outLine");
        $("#platformNo").val("");
        $("#tradeNo").val("");
    }
    if($(".amount_").is(":hidden")){
        hideParams.push(".amount_");
        $("#actualAmount").val("").removeAttr("data-oral");
        $("#postage").val("");
    }
    if($(".isShow").is(":hidden")){
        hideParams.push(".isShow");
    }
    if($(".freight-methods").is(":hidden")){
        hideParams.push(".freight-methods");
    }
    if($(".des-promotion").is(":hidden")){
        hideParams.push(".des-promotion");
    }
    if($(".site-shop").is(":hidden")){
        hideParams.push(".site-shop");
    }
    var params = {
        data:{
            temp:tempProCollection,
            shops : $("#shop").val(),
            shoplength : $("#shop option").length,
            sitetype : $("#site-type").val(),
            sitename : $(".site-name").val(),
            collectionAccount : $("#collectionAccount").val(),
            platformNo: $("#platformNo").val(),
            tradeNo : $("#tradeNo").val(),
            actualAmount : $("#actualAmount").attr("data-oral"),
            postage : $("#postage").val(),
            buyerID : $("#buyerID").val(),
            remark : $("#remark").val(),
            receiver : $("#receiver").val(),
            tel : $("#tel").val(),
            province : $("#province").find("option:checked").val(),
            city : $("#city").find("option:checked").val(),
            region : $("#region").find("option:checked").val(),
            addr : $("#addr").val(),
            idcard : $("#idcard").val(),
            postCode : $("#postCode").val(),
            orderer : $("#orderer").val(),
            ordererIDCard : $("#ordererIDCard").val(),
            ordererTel : $("#ordererTel").val(),
            promotion : $(".promotion-input").val(),
            reciverChoice : $("#exit_receiver").val(),
            hideHTML: hideParams,
            select_ware : $("input[name=select_ware]:checked").val()
        }
    };
    ajax_post("/sales/saveBufferMemory", JSON.stringify(params), "application/json",
        function (response) {
        },
        function (xhr, status) {
        }
    );
}

//处理缓存数据
function handlebuffer(){
    ajax_get("/sales/getBufferMemory", "", undefined,
        function (response) {
            if(response.code){
                layer.msg(response.msg,{icon:5,time:2000},function(index){
                    layer.close(index);
                    window.location.href = "/personal/login.html";
                });
                return;
            }

            response = JSON.parse(response);
            if (response.suc) {//表示有缓存
                //获得隐藏的元素,该隐藏的隐藏
                var data = JSON.parse(response.data);
                tempProCollection = data.temp;
                var hide = data.hideHTML;
                for(var i = 0; i<hide.length;i++){
                    $(hide[i]+"").hide();
                }
                addproductShow();
                getShopName(data.shoplength == 1 && data.shops == 0 ? undefined : data.shops);
                getShopPlatform(data.sitetype);
                $(".site-name").val(data.sitename);
                $("#platformNo").val(data.platformNo);
                $("#tradeNo").val(data.tradeNo);
                $("#actualAmount").val(data.actualAmount).attr("data-oral", data.actualAmount);
                $("#postage").val(data.postage);
                $("#buyerID").val(data.buyerID);
                $("#remark").val(data.remark);
                initReceiverList(data.reciverChoice);
                initAreaSel(data.province,data.city, data.region);
                $("#exit_receiver").val(data.reciverChoice);
                $("#receiver").val(data.receiver);
                $("#tel").val(data.tel);
                $("#addr").val(data.addr);
                $("#idcard").val(data.idcard);
                $("#postCode").val(data.postCode);
                $("#orderer").val(data.orderer);
                $("#ordererIDCard").val(data.ordererIDCard);
                $("#ordererTel").val(data.ordererTel);
                getFreight();
                $(".promotion-input").val(data.promotion);
                if(data.select_ware){
                    $('input[name=select_ware][value='+data.select_ware+']').prop("checked",true).data("val",1);
                }
            } else {//没缓存就应该清空表单数据，初始化省市区
                resetFormData();
                initReceiverList();
            }
        },
        function (xhr, status) {
        }
    );
}

function initParam(layerParam, laypageParam) {
    layer = layerParam;
    laypage = laypageParam;
    customer_service_account();
}

function customer_service_account() {
    ajax_get("/member/custaccount?" + Math.random(), JSON.stringify(""), "application/json",
        function (data) {
            customer_account = data.account;
            if (!customer_account) {
                layer.open({
                    type: 1,
                    title: "客户须知",
                    content: "<div style='padding:5px;text-align:center'>您未关联客服，请先关联客服，谢谢！</div>",
                    area: ['350px', '145px', '558.5px'],
                    btn: ["联系客服", "取消"],
                    closeBtn: 1, shadeClose: false,
                    //i和currdom分别为当前层索引、当前层DOM对象
                    yes: function (i, currdom) {
                        /*BizQQWPA.addCustom({
                            aty: '0',
                            nameAccount: '2881033425',//营销QQ号
                            selector: 'online'
                        });*/
                    },
                    cancel: function (i) {
                        $("#addodr").hide();
                    },
                    success: function (layero, index) {
                        $(".layui-layer-btn0").attr("id", "online");
                    }
                });
            }
        }
    );
}

/****************************************************以下是客户订单售后流程内容**************************************************************/
/****************************************************以上是客户订单售后流程内容**************************************************************/

//初始化省下拉框
function initAreaSel(pid, cid, rid) {
    ajax_get("/member/getprovs", JSON.stringify(""), "application/json",
        function (data) {
            $("#province").empty();
            $("#city").empty();
            $("#region").empty();
            prov_map = {};
            for (var i = 0; i < data.length; i++) {
                $("#province").append("<option value='" + data[i].id + "' >" + data[i].provinceName + "</option>");
                prov_map[data[i].provinceName.substr(0,data[i].provinceName.length-1)] = data[i].id;
            }
            //省份存在就选中
            if (pid) {
                $("#province").val(pid);
            } else {
                //不存在已选择省份，默认选第一个
                var sel = $("#province").find("option:first").val();
                $("#province").val(sel);
            }
            citySel(cid, rid);
            $("#postCode").val($("#city").find("option[value="+$("#city").val()+"]").attr("code"));
            provinceIdForFreight = $("#province").val();
        },
        function (xhr, status) {
        }
    );
}

//市级下拉框联动
$(".box-right-four").on("change", "#province", function () {
    citySel();
    $("#postCode").val($("#city").find("option[value="+$("#city").val()+"]").attr("code"));
    provinceIdForFreight = $(this).val();
    getFreight();
});

// 选择城市
function citySel(cid, rid) {
    if($("#province").val()){
          ajax_get("/member/getcities", "proId=" + $("#province").val(), "",
            function (data) {
                $("#city").empty();
                city_map = {};
                for (var i = 0; i < data.cities.length; i++) {
                    $("#city").append("<option value='" + data.cities[i].id + "' code='"+data.cities[i].zipCode+"' >" + data.cities[i].cityName + "</option>");
                    city_map[data.cities[i].cityName.substr(0,data.cities[i].cityName.length-1)] = data.cities[i].id;
                }
                //城市存在就选中
                if (cid) {
                    $("#city").val(cid);
                } else {
                    var sel = $("#city").find("option:first").val();
                    $("#city").val(sel);
                }
                regionSel(rid);
            },
            function (xhr, status) {
            }
        );  
    } 
}

//区级下拉框联动
$(".box-right-four").on("change", "#city", function () {
    regionSel();
});
//收货人信息填充到表单
$("body").on("change","#exit_receiver",function(){
    init_receiver(this);
});
//保存收货人
$("body").on("click","#saveRe",function(){
    alter_receiver(this);
});
//删除收货人
$("body").on("click","#deleteRe",function(){
    var rid = $("#exit_receiver").val();
    delete_re(rid);
});
//选择已存在的收货人，填充表单数据
function init_receiver(node){
    var email = $(node).data("email");
    var addrId = $(node).val();
    if(!addrId){
        $("#receiver").val("");
        $("#province").val(1).change();
        $("#tel").val("");
        $("#addr").val("");
        $("#idcard").val("");
        $("#postCode").val("");
        $("#deleteRe").attr("disabled", true).css("background", "#eaeaea").css("color", "#000");
    }else{
        ajax_get("/sales/getRes?email="+email +"&addrId="+addrId,"", "",
            function (data) {
                if(data){
                    var res = data[0];
                    $("#receiver").val(res.receiverName);
                    $("#province").val(res.provinceId).change();
                    $("#city").val(res.cityId).change();
                    $("#region").val(res.areaId);
                    $("#tel").val(res.receiverTel);
                    $("#addr").val(res.receiverAddr);
                    $("#idcard").val(res.receiverIdcard);
                    $("#postCode").val(res.postCode);
                    $("#deleteRe").attr("disabled", false).removeAttr("style");
                }
            }
        );
    }
}
//恢复代码
$(".box-right-four").on("mouseover", ".id-question", function () {
    layer.tips('清关入境需要身份证号', this, {
        tips: [2, '#7AC141'],
        time: 0
    });
})
.on("mouseover", ".pay-question", function () {
    layer.tips('您需要输入您的支付交易号,以方便快速清关入境', this, {
        tips: [2, '#7AC141'],
        time: 0
    });
})
.on("mouseover", ".buy-question", function () {
    layer.tips('买家信息指在分销商平台下单用户的信息', this, {
        tips: [2, '#7AC141'],
        time: 0
    });
})
.on("mouseover", ".sure-question", function () {
    layer.tips('当您的微仓库存充足时,可以勾选此项一键实现提交订单并通知发货.当您只是想录入销售订单但还不准备发货时,勿勾选此项', this, {
        tips: [2, '#7AC141'],
        time: 0
    });
})
.on("mouseover", ".real-pay-question", function () {
    layer.tips('非完税商品实付款填写范围在分销价上浮动100%以内，如若不符合规范，系统会自动更改', this, {
        tips: [2, '#7AC141'],
        time: 0
    });
})
.on("mouseover", ".shop-question", function () {
    layer.tips('新增的店铺请尽量在我的资料==》我的店铺里补全店铺信息，否则物流单上寄件人信息使用平台默认信息', this, {
        tips: [2, '#7AC141'],
        time: 0
    });
})
.on("mouseout", ".id-question,.pay-question,.name-question,.sure-question,.real-pay-question,.buy-question,.shop-question", function () {
    layer.closeAll('tips')
});

// 选择地区
function regionSel(rid) {
    if($("#city").val()){
      ajax_get("/member/getareas", "cityId=" + $("#city").val(), "",
        function (data) {
                $("#region").empty();
                region_map = {};
                for (var i = 0; i < data.areas.length; i++) {
                    $("#region").append("<option value='" + data.areas[i].id + "' >" + data.areas[i].areaName + "</option>");
                    region_map[data.areas[i].areaName.substr(0,data.areas[i].areaName.length-1)] = data.areas[i].id;
                }
                //地区存在就选中
                if (rid) {
                    $("#region").val(rid);
                } else {
                    var sel = $("#region").find("option:first").val();
                    $("#region").val(sel);
                }
            },
            function (xhr, status) {
            }
        );  
    }
    
}

//自动匹配收货人信息
//广东省 深圳市 龙岗区 坂田街道桥联东路顺兴楼1003 518116 黄安纳 13603064940        千牛格式
//李心瑞，13999889672，新疆维吾尔自治区 乌鲁木齐市 沙依巴克区 红庙子街道西城街荣和城二期28号楼2单元1701 ，830000      网页格式
function sureMatch(obj){
    var template = $("#address-template").find("option:selected").val();//选择的地址格式
    if (template == 0) {
        layer.msg("请选择地址格式！", {icon: 5, time: 2000});
        return;
    }

    var flag = false;
    if (template == 1) {//淘宝千牛格式
        var waitInfo = $("input[name=waitInfo]").val().trim().split("  ");
        var length = waitInfo.length;
        if (length >= 7) {
            $("#province").val(getAddressId(waitInfo[0],prov_map)).change();
            if($("#province").val()){
                $("#city").val(getAddressId(waitInfo[1],city_map)).change();
                if($("#city").val()){
                    flag = true;
                    $("#region").val(getAddressId(waitInfo[2],region_map));

                }
            }
            $("#receiver").val(waitInfo[length-2]);
            $("#tel").val(waitInfo[length-1]);
            $("#postCode").val(waitInfo[length-3]);
            $("#addr").val(waitInfo[4]);
        }
    } else {//淘宝网页格式
        waitInfo = $("input[name=waitInfo]").val().split("，");
        if (waitInfo.length >= 4) {
            var addrs = waitInfo[2].trim().split(" ");
            if (addrs.length >= 4) {
                $("#province").val(getAddressId(addrs[0],prov_map)).change();
                if($("#province").val()){
                    $("#city").val(getAddressId(addrs[1],city_map)).change();
                    if($("#city").val()){
                        flag = true;
                        $("#region").val(getAddressId(addrs[2],region_map));
                    }
                }
                var address = "";
                $.each(addrs,function(i,item){
                    if(i>=3) {
                        address += item;
                    }
                });
                $("#addr").val(address);
            }
            $("#receiver").val(waitInfo[0]);
            $("#tel").val(waitInfo[1]);
            $("#postCode").val(waitInfo[3]);
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

/**
 * 初始化收件人列表
 * @param addr_id 单选按钮需要勾选的地址id
 */
function initReceiverList(addr_id) {
    // 方法只返回最新添加的一条记录，该操作暂行，为了防止选错
    isnulogin(function (email) {
        ajax_get("/sales/getRes?email=" + email, {}, "",
            function (response) {
                $("#exit_receiver").data("email",email);
                if (response) {
                    $("#exit_receiver").empty();
                    var receHtml = '';
                    for (var i = 0; i < response.length; i++) {
                        receHtml += '<option value = "'+response[i].id+'">'+
                            response[i].receiverName + " " +
                            response[i].provinceName + response[i].cityName + response[i].areaName +
                            response[i].receiverAddr + " " + response[i].receiverTel + " " +
                            (response[i].receiverIdcard ? response[i].receiverIdcard : "") +
                            '</option>';
                    }
                    receHtml += '<option value = "">请选择收货人信息</option>';
                    $("#exit_receiver").append(receHtml);
                    if(addr_id){
                        $("#exit_receiver").val(addr_id).change();
                    }else{
                        $("#exit_receiver").change();
                    }
                }
            },
            function (xhr, status) {
            }
        );
    });
}

/**
 * 收件人信息变更
 * @param node
 */
function alter_receiver(node) {
    var rid = $("#exit_receiver").val();
    var email = $("#exit_receiver").data("email");
    var params = getParamters();
    if(params){
        var title= "新增收件人信息"
        if(rid){
            params.id = rid;
            title = "更新收件人信息";
        }
        params.email = email;
        //防止重复提交
        $(node).attr("disabled", true).css("background", "#eaeaea").css("color", "#000");
        ajax_post("/sales/alterRe", JSON.stringify(params), "application/json",
            function (response) {
                if (response) {
                    layer.msg(title + "成功！", {icon: 1, time: 1000}, function () {
                        initReceiverList(rid);
                    });
                } else {
                    layer.msg(title + "出错，请稍后重试！", {icon: 2, time: 2000});
                }
                $(node).attr("disabled", false).removeAttr("style");
            },
            function (xhr, status) {
                layer.msg(title + "出错，请稍后重试！", {icon: 2, time: 2000});
                $(node).attr("disabled", false).removeAttr("style");
            }
        );
    }
}

//获取表单验证后的参数
function getParamters() {
    var receiverName = $("#receiver").val().trim();
    var receiverTel = $("#tel").val().trim();
    var receiverAddr = $("#addr").val().trim();
    var receiverIdcard = $("#idcard").val().trim();
    var postCode = $("#postCode").val().trim();
    if (receiverName == "") {
        layer.msg('请输入收件人名称', {icon: 2, time: 2000});
        return false;
    }
    if (!receiverAddr || receiverAddr.length < 5) {
        layer.msg('收货地址不规范，请重新填写', {icon: 2, time: 2000});
        return false;
    }
    if (productType &&!checkIDCard(receiverIdcard)) {
        layer.msg('请输入有效的身份证号码', {icon: 2, time: 2000});
        return false;
    }
    if (!checkTel(receiverTel)) {
        layer.msg('请输入有效的手机号码', {icon: 2, time: 2000});
        return false;
    }
    // 邮编非必填
    if (postCode && !checkPost(postCode)) {
        layer.msg('请输入有效的邮政编码', {icon: 2, time: 2000});
        return false;
    }
    return {
        "receiverName": receiverName,
        "receiverTel": receiverTel,
        "receiverAddr": receiverAddr,
        "receiverIdcard": receiverIdcard,
        "provinceId": $("#province").val(),
        "cityId": $("#city").val(),
        "areaId": $("#region").val(),
        "provinceName":$("#province option:selected").text(),
        "cityName":$("#city option:selected").text(),
        "areaName":$("#region option:selected").text(),
        "postCode": postCode
    };
}

/**
 * 收件人删除
 * @param node
 */
function delete_re(rid) {
    layer.confirm("确认删除该收件人？", {icon: 3},
        function (i, currdom) {
            layer.close(i);
            ajax_post("/sales/delRe", JSON.stringify({"rid": rid}), "application/json",
                function (response) {
                    if (response) {
                        initReceiverList();
                    }
                },
                function (xhr, status) {
                    layer.msg("删除收件人出错，请稍后重试！", {icon: 2, time: 2000});
                }
            );
        }
    );
}

/**
 * 获取店铺数据数据sales
 * @param seldId 已选店铺id
 */
function getShopName(seldId) {
    var params = {currPage: 1, pageSize: 9999};
    ajax_post_sync("/member/getstore", params, undefined,
        function (response) {
            $("#shop").empty();
            var shoFlag = false;//判断传入的店铺seldie(店铺id是否存在，有可能在我的店铺里已经被删除)
            if (response.suc) {
                var firstShop = response.page.list[0];
                if (firstShop) {
                    $("#collectionAccount").val(firstShop.shroffAccountNumber);
                    $("#shop").show();
                    $(".site-shop").hide();
                    $(".site-type").css({"margin-right": "5px","margin-left": "5px"});
                } else {
                    $("#shop").hide();
                    $(".site-shop").show();
                    $(".site-type").css({"margin-right": "103px","margin-left": "-4px"});
                }
                var dataMode = "";
                for (var i = 0; i < response.page.list.length; i++) {
                    var shop = response.page.list[i];
                    dataMode = (shop.shroffAccountNumber ? shop.shroffAccountNumber : "") + "," +
                        (shop.keeperName ? shop.keeperName : "") + "," +
                        (shop.idcard ? shop.idcard : "") + "," +
                        (shop.tel ? shop.tel : "") + "," +
                        (shop.zipCode ? shop.zipCode : "");

                    var item = $("<option value='" + shop.id + "' data-mode='" + dataMode + "' shopType='" + shop.type + "'>" + shop.name + "(" + shop.type + ")</option>");
                    $("#shop").append(item);
                    if (seldId == 0 || seldId == shop.id) {
                        shoFlag = true;
                    }
                }
                $("#shop").append($("<option value = '0' data-mode=',' shoptype='其他'>其他</option>"));
            } else if (response.code == "2") {
                window.location.href = "login.html";
            } else {
                layer.msg(response.msg, {icon: 2, time: 2000});
            }
            //存在数据就选中
            if (seldId && shoFlag) {
                $("#shop").val(seldId);
            }
            $("#shop").change();
        },
        function (xhr, status) {
        }
    );
}

//销售订单列表
var saleMap;
function saleOrderList(obj, flag, lay) {
    var currPage = 1;
    var pageSize = $("#pageSizeTemp").val();
    if (flag) {
        currPage = obj.curr;
    }
    //清空销售单列表
    $("#sale_send").find("#sales_div").empty();
    //$("#sale_send").fadeOut(100);
    displayIvyStock();
    //加載订单基本信息
    var params = {"status": $("#sstatus").val(), "desc": $("#desc").val(), "pageSize": pageSize, "currPage": currPage}
    ajax_post("/sales/getsl",JSON.stringify(params),
        "application/json",function (data) {
        if(data.code){
            layer.msg(data.msg,{icon:5,time:2000},function(index){
                layer.close(index);
                window.location.href = "/personal/login.html";
            });
        }
        var saleOrders = data.datas;
        var detailMap = {};
        if (saleOrders.length > 0) {
            for (var i = 0; i < saleOrders.length; i++) {
                var aDetail_html = "<div class='three-2 light-gray purchase-list' id='" + saleOrders[i].id + "'>" +
                    "<ul>" +
                    "<li>订单编号:<span name='orderno'>"+saleOrders[i].sno+"</span></li>" +
                    "<li>平台订单编号:<span>"+deal_with_illegal_value(saleOrders[i].pno)+"</span></li>" +
                    "<li>下单时间:<span>"+saleOrders[i].odate+"</span></li>" +
                    "<li>收货人:<span>"+saleOrders[i].receiver+"</span></li>" +
                    "<li style='display: none'><span name='logistisCode'>"+saleOrders[i].thirdLogistisCode+"</span></li>" +
                    "<li name='totalQty' style='display: none'>商品总数:<span></span></li>" +
                    "</ul>" +
                    "<div class='purchase-table'>" +
                    "<table>" +
                    "<tbody>" +
                    "<tr class='four-2-tr1 bg-gray word-black'>" +
                    "<td class='tr1-td1'>商品名称</td>" +
                    "<td class='tr1-td2'>商品编号</td>" +
                    "<td class='tr1-td3'>发货数量(个)</td>" +
                    "<td class='tr1-td5'>所属仓库</td>" +
                    "<td class='tr1-td6'>订单状态" +
                    "<input type='hidden' name='status' value='"+saleOrders[i].status+"'/>" +
                    "<input type='hidden' name='statusMess' value='"+saleOrders[i].statusMess+"'/>" +
                    "<input type='hidden' name='pNo' value='"+(saleOrders[i].purchaseOrderNo ? saleOrders[i].purchaseOrderNo : '')+"'/>" +
                    "</td>" +
                    "<td class='tr1-td7'>订单操作</td>" +
                    "</tr>" +
                    "</tbody>" +
                    "</table>" +
                    "</div>" +
                    "<div class='searchUD' onclick='CustomerSilder(this)'>" +
                    "<b>" +
                    "<span class='attMs' style='display: inline;'>" +
                    "<span>查看更多</span>" +
                    "</span>" +
                    "<span class='attLs' style='display: none;'>上拉</span>" +
                    "<em></em>" +
                    "</b>" +
                    "</div>" +
                    "<input type='hidden' class='bbcPostage' value='"+saleOrders[i].bbcPostage+"'/>" +
                    "</div>"

                $("#sales_div").append(aDetail_html);
                detailMap[saleOrders[i].id] = mergeSameSkuIntoOne4SaleOrder(saleOrders[i].saleDetails);
                saleOrders[i].saleDetails.confirmReceiptDate = saleOrders[i].confirmReceiptDate;
                saleOrders[i].saleDetails.shFlag = saleOrders[i].shFlag;
                detailMap[saleOrders[i].id].shFlag = saleOrders[i].shFlag;
                saleMap = detailMap;
            }

            // 加载订单详情
            // 遍历列表中的每条记录
            $("div.three-2").each(function () {
                var cur = $(this);
                var sid = cur.attr("id")
                var detail = detailMap[sid];
                if (isnotnull(detail) && detail.length > 0) {
                    var totalQty = 0;
                    for (var i = 0; i < detail.length; i++) {


                        var applyRefundsContent = "";
                        //ajax_post_sync("/sales/selectEffectiveShOrderByDetailOrderId", JSON.stringify({"detailOrderId": detail[i].id}), "application/json",function(data){
                            var count = detail[i].count;
                            var status = detail[i].status;
                            var shOrderId = detail[i].shOrderId;
                            if(count > 0 && status == 5) {
                                applyRefundsContent += "&nbsp;&nbsp;<em class='word-white btn-hyacinthine-small see_detail2' sid='" + shOrderId + "'>已完成</em>";
                            }
                            if(count > 0 && status == 6) {
                                applyRefundsContent += "&nbsp;&nbsp;<em class='word-white btn-hyacinthine-small see_detail2' sid='" + shOrderId + "'>已关闭</em>";
                            }
                            if(count > 0 && status < 5) {
                                applyRefundsContent += "&nbsp;&nbsp;<em class='word-white btn-hyacinthine-small see_detail2' sid='" + shOrderId + "'>退款中</em>";
                            }

                            //console.log(detail[i].id);
                            if(detail.shFlag) {//确认收货7天内
                                if(count == 0) {
                                    applyRefundsContent += "&nbsp;&nbsp;<em class='word-white btn-hyacinthine-small toRefundsApply' data-detailid='"+ detail[i].id +"' data-orderNo='"+  detail[i].salesOrderNo +"' data-productName='"+ detail[i].productName +"' data-productImg='"+ detail[i].productImg+"' data-warehouseName='"
                                        + detail[i].warehouseName + "' data-warehouseId='"+detail[i].warehouseId +"' data-sku='" + detail[i].sku + "' data-qty='" + detail[i].qty +"' data-id='"+sid+"'>申请退款</em>";
                                }
                            }
                        //});


                        totalQty += detail[i].qty;
                        var href = "/product/product-detail.html?sku=" + detail[i].sku + "&warehouseId=" + detail[i].warehouseId
                        var html = "<tr class='three-2-tr2'>" +
                            "<td class='tr2-td1'>" +
                            "<div>" +
                            "<a href='"+href+"' target='_blank'>" +
                            "<img class='bbclazy' src='../img/img-load.png'  data-original='" + urlReplace(detail[i].productImg, detail[i].sku, null, 80, 80, 100) + "'/>" +
                            "</a>" +
                            "</div>" +
                            "<p style='width: 230px;'>" +
                            "<a href='"+href+"' class='pnamelk brk' target='_blank'>"+detail[i].productName+(detail[i].isgift?"【<b style='color: red;'>赠</b>】":"")+"</a>" +
                            "</p>" +
                            "</td>" +
                            "<td class='tr2-td2'><span class='sku'>"+detail[i].sku+"</span></td>" +
                            "<td>" +
                            "<p>" +
                            "<span class='qty blk' isdeducted='"+detail[i].isDeducted+"'>"+detail[i].qty + ' '
                             + applyRefundsContent +
                            "</span>" +
                            "</p>" +
                            "</td>" +
                            "<td class='tr2-td5'>" +
                            "<span class='warehousename'>"+detail[i].warehouseName+"</span>" +
                            "<span class='warehouseid' style='display: none'>"+detail[i].warehouseId+"</span>" +
                            "</td>" +
                            "</tr>";
                        cur.find("tbody").append(html);
                    }
                    cur.find("li[name='totalQty'] span").html(totalQty);
                    var status = cur.find("input[name='status']").val();
                    var statusMess = cur.find("input[name='statusMess']").val();
                    var pNo = cur.find("input[name='pNo']").val();
                    var sNo = cur.find("span[name='orderno']").text();
                    var thirdLogistisCode=cur.find("span[name='logistisCode']").text();
                    // 操作
                    var opt_html = "<td class='tr2-td6' rowspan='" + detail.length + "'>" +
                        "<p class='status'>" + statusMess + "</span></p>" +
                        "<p class='see_detail' sid='"+sid+"'>订单详情</p>" +
                        "</td>" +
                        "<td class='tr2-td7' rowspan='" + detail.length + "'>"

                    //opt_html += "<a href='javascript:;' class='afterApplication'>售后申请</a>";
                    if (status == 1) {//待付款
                        opt_html += "<a href='javascript:;' sid='"+sid+"' purno='" + pNo + "' onclick='initPaymentAddress(this,false)' " +
                            "target='view_window' class='goPay bg-blue word-white btn-hyacinthine-small'>去付款</a>"+
                            "<a href='javascript:;' salesOrderNo='" + sNo + "' onclick='cancelSalesOrder(this)' " +
                            "class='goPay bg-blue word-white btn-hyacinthine-small' style='margin-top: 5px'>取消订单</a>";
                    } else if (status == 103) {//待支付运费
                        opt_html += "<a href='javascript:;' sid='"+sid+"' class='word-white bg-blue btn-hyacinthine-small' " +
                            "onclick = 'initPaymentAddress(this,true)' saleNO='" + sNo + "'>去付款</a>" +
                            "<a href='javascript:;' salesOrderNo='" + sNo + "' onclick='cancelSalesOrder(this)' " +
                            "class='goPay bg-blue word-white btn-hyacinthine-small' style='margin-top: 5px'>取消订单</a>";
                    } else if (status == 2) {//待用户确认
                        opt_html += "<a href='javascript:;' class='informToSend word-white bg-blue btn-hyacinthine-small'>通知发货</a>"+
                            "<a href='javascript:;' salesOrderNo='" + sNo + "' onclick='cancelSalesOrder(this)' " +
                            "class='goPay bg-blue word-white btn-hyacinthine-small' style='margin-top: 5px'>取消订单</a>";
                    } else if (status == 3 || status == 11) {//待客服审核
                        opt_html +=   "<a href='javascript:;' salesOrderNo='" + sNo + "' onclick='cancelSalesOrder(this)' " +
                                "class='goPay bg-blue word-white btn-hyacinthine-small' style='margin-top: 5px'>取消订单</a>";
                        // opt_html += "<a href='javascript:;' class='cancleAudit word-white bg-blue btn-hyacinthine-small'>取消通知发货</a>"
                    } else if (status == 4) {//审核不通过
                        opt_html +=   "<a href='javascript:;' salesOrderNo='" + sNo + "' onclick='cancelSalesOrder(this)' " +
                                "class='goPay bg-blue word-white btn-hyacinthine-small' style='margin-top: 5px'>取消订单</a>";
                    }else if (status == 9) {//待收货
                        opt_html += "<a href='javascript:;' class='receiveConfirm word-white bg-blue btn-hyacinthine-small'>确认收货</a>"+
                                    "<a href='javascript:;' salesOrderNo='" + sNo + "' logistisCode='"+thirdLogistisCode+"' onclick='viewLogisticInfo(this)' " +
                                    "class='goPay bg-blue word-white btn-hyacinthine-small' style='margin-top: 5px'>查看物流</a>";
                    }
                    opt_html += "</td>"
                    cur.find("tbody tr.three-2-tr2:first").append(opt_html);
                }
                CustomerLength(this);
                $(".three-2-box").show();
                $("img.bbclazy").lazyload({
                    effect: 'fadeIn',
                    threshold: '100'
                });
            });
        } else {
            addLoadGif({
                togBox: ".three-2-box",
                togPage: '#pagination_sales',
                hint: '<div class="nothing"><div>抱歉，暂无相关订单</div></div>'
            }, true);
        }
        //初始化分页
        init_pagination_sales(data.totalPage, data.currPage);
    })

    //loading
    return creatLoading(true, theDiv);
}

// 合并相同sku、warehouseId、finalSellingPrice的商品（到期日期导致）
function mergeSameSkuIntoOne4SaleOrder(saleDetails){
    var saleDetailsMerged = new Array();
    var skuWarehouseFinalSellingPrice2Detail = {}
    for(var i in saleDetails){
        var pro = saleDetails[i]
        var finalSellingPrice = pro.finalSellingPrice ? pro.finalSellingPrice : pro.purchasePrice;
        var key = pro.sku+"_"+pro.warehouseId+"_"+finalSellingPrice
        var detail = skuWarehouseFinalSellingPrice2Detail[key]
        if(detail){// 存在，合并数量
            pro.qty = parseInt(pro.qty)+parseInt(detail.qty);
        }
        skuWarehouseFinalSellingPrice2Detail[key] = pro
    }

    $.each(skuWarehouseFinalSellingPrice2Detail,function(k,v){
        saleDetailsMerged.push(v);
    })

    return saleDetailsMerged;
}

// 取消订单：未付款的
function cancelSalesOrder(obj){
    var salesOrderNo = $(obj).attr("salesOrderNo");
    layer.confirm("您确定要取消订单吗？", {icon: 3},
        function (i, currdom) {
            var load_index = layer.load(1, {shade: 0.5});
            ajax_post("/sales/cancelso", JSON.stringify({"so": salesOrderNo}), "application/json",
                function (response) {
                    if(response.suc){
                        var tthis = $(obj).parents("div.three-2")[0];
                        $(tthis).find(".status").html("已关闭");
                        $(tthis).find(".Edit_order").remove();//取消的订单不可再操作
                        $(tthis).find(".informToSend").remove()//通知发货不可再操作
                        $(obj).parent().empty();
                        layer.msg("取消订单成功",{icon:1,time:2000});
                        layer.close(i);
                    }else{
                        layer.msg(response.msg,{icon:5,time:2000});
                    }
                    layer.close(load_index);
                },
                function (xhr, status) {
                    layer.close(load_index);
                }
            );
        }
    );
}

// 查看物流信息
function viewLogisticInfo(obj){
    var salesOrderNo = $(obj).attr("salesOrderNo");
    var thirdLogistisCode=$(obj).attr("logistisCode");
    ajax_post("/sales/getLogisticsTracingInfo ", JSON.stringify({"orderNo": salesOrderNo,"thirdLogistisCode":thirdLogistisCode}), "application/json",
        function (response) {
            if(response.suc){
                var packDatas = response.data;
                var logisticContent="";
                if(packDatas.length>0){
                    var logisticOrderNo=packDatas[0].logisticCode;
                    var logisticName=packDatas[0].logisticName;
                    for(var i=0;i<packDatas.length;i++){
                        var pack=packDatas[i];
                        logisticContent+="<li><span id='logisticTime'>"+pack.acceptTimeStr+" : "+pack.acceptStation+"</span></li>"
                    };
                    var content ="<div id='logistics-box'><ul id='logistics-ul'>"+logisticContent+"</ul></div>"
                    layer.open({
                        type: 1
                        ,area: ['600px', '400px']
                        ,title: '物流单号:'+logisticOrderNo+'  物流名称: '+logisticName
                        ,shade: 0.6 //遮罩透明度
                        ,anim: 1 //0-6的动画形式，-1不开启
                        ,content:content
                    });
                }
            }else{
                layer.msg(response.msg);
            }
        }
    );
}

/**
 * 获取每个订单的微仓库存信息，直观呈现待采购数量
 */
function displayIvyStock() {
    $.ajax({url: "/member/infor?" + Math.random(), type: "get", dataType: "json", async: false,
        success: function (u) {
            //分销商类型
            distributorType = u.comsumerType;
        }
    });
}

//客户订单的下拉
function CustomerLength(obj) {
    var LGH = $(obj).find('table');
    var trLen = $(LGH).find('.three-2-tr2').length;
    if (trLen <= 5) {
        $(LGH).parents('.purchase-list').find('.searchUD').hide();
        $(obj).find('.purchase-table').height(100 * trLen + 40);
    } else {
        $(LGH).parents('.purchase-list').find('.searchUD').show();
        $(obj).find('.purchase-table').height(540);
    }
}

function CustomerSilder(obj) {
    var payH = $(obj).parents(".purchase-list").find(".purchase-table table").height();
    if ($(obj).find("em").hasClass("upBAK") == false) {
        $(obj).parents(".purchase-list").find(".purchase-table").animate({height: payH});//支付页面的
    } else {
        $(obj).parents(".purchase-list").find(".purchase-table").animate({height: 540});
    }
    $(obj).find("em").toggleClass("upBAK");
    $(obj).find(".attMs").toggle();
    $(obj).find(".attLs").toggle();
    var paymentLileng = $(".purchase-list").find(".cartListUL");
    if (paymentLileng.length > 0) {
        $(".searchUD").show();
    }
}

//查看发货单详情
$(".box-right-four").on("click", ".see_detail", function () {
    // order_detial_list是销售单详情div
    goSeeOrderDetail($(this).attr("sid"));
});

// 去看订单详情
function goSeeOrderDetail(sid, refreshList){
    // 跳到详情页
    $("#order_detial_list").fadeIn(200);
    $("#sale_send").fadeOut(200);
    // 是否要刷新列表，默认false的
    if(refreshList){
        $(".close_detail").attr("refreshList","true")
    }
    getOrderDetail(sid)
}

// 查看订单详情
function getOrderDetail(sid){
    var status;
    var saleOrderNo;
    // order_detial_list是销售单详情div
    var orderInfo_h4 = $("#order_detial_list").find("h4:eq(0)");
    ajax_post_sync("/sales/getMain", JSON.stringify({"orderId": sid}), "application/json",function(data){
        $(orderInfo_h4).find("p:eq(0) span").html(data.salesOrderNo);// 订单信息-订单编号
        $(orderInfo_h4).find("p:eq(2) span").html(data.orderingDateStr);// 订单信息-下单时间
        $(orderInfo_h4).find("p:eq(1) span").html(data.statusDesc);// 订单信息-订单状态
        status = data.status
        saleOrderNo = data.salesOrderNo
    })

    //基本信息
    ajax_post("/sales/getBase", JSON.stringify({"orderId": sid}), "application/json",
        function (data) {
            if(data.code){
                layer.msg(data.msg,{icon:5,time:2000},function(index){
                    layer.close(index);
                    window.location.href = "/personal/login.html";
                });
            }
            var logisticsMode = data.logisticsMode;
            //订单信息-物流信息
            ajax_post("/sales/showLogisticsinfo", JSON.stringify({"orderNo": saleOrderNo}), "application/json",
                function (LogisticsinfoRes) {
                    if (LogisticsinfoRes.suc) {
                        var info = logisticsMode;
                        //存在物流信息
                        if (LogisticsinfoRes.data.length > 0) {
                            //目前一个客户订单只会对应一条物流信息LogisticsinfoRes.data[0].cshippingname
                            info = "  物流方式：" + logisticsMode +
                                "  物流单号：" + LogisticsinfoRes.data[0].ctrackingnumber +
                                "  <a href='http://www.kuaidi100.com/all/' style='color:red;display:inline-block;'" +
                                "target='_blank'>快递查询</a>";
                        }
                        $(orderInfo_h4).find("p:eq(3) span").html(info);
                    }
                    // 自提
                    if (data.logisticsTypeCode == "X2") {
                        $(orderInfo_h4).find("span:eq(3)").next().show();
                    } else {
                        $(orderInfo_h4).find("span:eq(3)").next().hide();
                    }
                }
            );
            // --------------------------------------------------------------------------
            var receiver_h4 = $("#order_detial_list").find("h4:eq(1)");// 收货人信息-
            receiver_h4.find("p:eq(0) span").html(data.receiver);// 收货人信息- 收货人信息
            receiver_h4.find("p:eq(1) span").html(data.tel);// 收货人信息-手机号
            receiver_h4.find("p:eq(2) span").html(data.address);// 收货人信息-地址
            receiver_h4.find("p:eq(3) span").html(data.postCode);// 收货人信息-邮编
            receiver_h4.find("p:eq(4) span").html((data.idcard ? data.idcard : "---"))// 收货人信息-身份证号
            // --------------------------------------------------------------------------
            var basicInfo_spans = $("#order_detial_list").find("h4:eq(3)").find("span");// 基本信息
            $(basicInfo_spans[2]).html(data.orderActualAmount != null ? fmoney(data.orderActualAmount, 2)+"元":"0元");// 基本信息 - 实付款
            $(basicInfo_spans[3]).html(data.orderPostage != null ? fmoney(data.orderPostage, 2)+"元":"0元");// 基本信息 - 运费
            $(basicInfo_spans[4]).html(data.buyerID ? data.buyerID : "---");// 基本信息 - 客户名称
            $(basicInfo_spans[5]).html(deal_with_illegal_value(data.remark));// 基本信息 - 备注
            // --------------------------------------------------------------------------
            var buyer_spans = $("#order_detial_list").find("h4:eq(4)").find("span");// 买家信息
            $(buyer_spans[0]).text(data.orderer ? data.orderer : "---");// 买家信息 - 姓名
            $(buyer_spans[1]).text(data.ordererTel ? data.ordererTel : "---");// 买家信息 - 手机号
            $(buyer_spans[2]).text(data.ordererIDCard ? data.ordererIDCard : "---");// 买家信息 - 身份证号
            // --------------------------------------------------------------------------
            var shipperInfo_spans = $("#order_detial_list").find("h4:eq(2)").find("span");// 寄件人信息
            // 平台店铺
            ajax_post("/member/getstoreById", JSON.stringify({"sid": data.shopId}), "application/json",
                function (getstoreRes) {
                    var senderAddress = getstoreRes.provinceName?getstoreRes.provinceName:"";
                    senderAddress += getstoreRes.cityName?getstoreRes.cityName:"";
                    senderAddress += getstoreRes.areaName?getstoreRes.areaName:"";
                    senderAddress += getstoreRes.addr?getstoreRes.addr:"";
                    if (getstoreRes.otherPlatform) {
                        $(basicInfo_spans[0]).html(getstoreRes.shopName + "(" + getstoreRes.otherPlatform + ")");// 基本信息-平台店铺
                    } else {
                        $(basicInfo_spans[0]).html(getstoreRes.shopName ? getstoreRes.shopName + "(" + getstoreRes.platformName + ")" : "");
                    }
                    $(basicInfo_spans[1]).html(deal_with_illegal_value(getstoreRes.shroffAccountNumber));// 基本信息-收款账号
                    // --------------------------------------------------------------------------
                    $(shipperInfo_spans[0]).text(getstoreRes.keeperName ? getstoreRes.keeperName : "唐义和");// 寄件人信息-寄件人姓名
                    $(shipperInfo_spans[1]).text(getstoreRes.telphone ? getstoreRes.telphone : "13689528832");// 寄件人信息-寄件人电话
                    $(shipperInfo_spans[2]).text(senderAddress ? senderAddress : '平湖镇平湖街道平安大道乾隆物流园2期3楼');// 寄件人信息-寄件人地址
                }
            );
            // 发货单详情   内部分销商显示最终售价   分销商类型，默认是1：普通
            var bbcPostage = data.bbcPostage;
            ajax_post("/sales/getsdl", JSON.stringify({"orderId": sid}), "application/json",
                function (datad) {
                    datad = mergeSameSkuIntoOne4SaleOrder(datad);
                    if (datad.length > 0) {
                        var productList = $("#order_detial_list").find("h4:eq(5)")
                        //清空原始数据
                        productList.find("ul:gt(0)").remove();
                        productList.find("p").remove();
                        var qtyTotal = 0
                        for (var i = 0; i < datad.length; i++) {
                            var img_p_url = datad[i].productImg;
                            var finalSellingPrice = datad[i].finalSellingPrice ? datad[i].finalSellingPrice : datad[i].purchasePrice;
                            qtyTotal = qtyTotal + datad[i].qty
                            var href = '../../product/product-detail.html?sku=' + datad[i].sku + '&warehouseId=' + datad[i].warehouseId
                            var html = "<ul style='text-align: center;'>" +
                                "<li style='width:70px;height:70px;'>" +
                                "<a target='_blank' href='"+href+"' style='display: inline-block;'>" +
                                "<img src='" + urlReplace(img_p_url, datad[i].sku) + "' style='width:70px;border:1px solid #ccc;'>" +
                                "</a>" +
                                "</li>" +
                                "<li style='width:330px;text-align: left;margin-left: 20px'>" +
                                "<a href='"+href+"' style='font-size:12px;line-height: 22px;'>" + datad[i].productName+ (datad[i].isgift?"【<b style='color: red;'>赠</b>】":"")+ "</a>" +
                                "</li>" +
                                "<li style='width:150px;line-height:80px;'>"+datad[i].sku+"</li>" +
                                "<li style='width:150px;line-height:80px;'>"+datad[i].warehouseName+"</li>" +
                                "<li style='width:150px;line-height:80px;'>"+finalSellingPrice.toFixed(2)+"</li>" +
                                "<li style='width:150px;line-height:80px;'>"+datad[i].qty+"</li>" +
                                "</ul>";
                            productList.append(html);

                        }
                        productList.append("<p style='text-align: right; background: #f4f4f4; padding-bottom: 10px;'>" +
                            "<span style='margin-right: 30px;'>商品总数：<i style='padding: 0 5px;'>" + qtyTotal + "</i>个</span>" +
                            "<span style='margin-right: 30px;'>平台运费：<i style='padding: 0 5px;'>" + deal_with_illegal_value(bbcPostage ? fmoney(bbcPostage, 2) : null) + "</i>元</span></p>")
                        // 优惠
                        if (data.couponsCode) {
                            ajax_post("/member/getCoupons ", JSON.stringify({"seachSpan": data.couponsCode}), "application/json",
                                function (response) {
                                    var coup = response.data.result[0];
                                    $(".promotion-subbox").show();
                                    var p1 = $("div.promotion-subbox").find("div.promotion-sub div:eq(0) p:eq(0)");
                                    p1.find("span:eq(0)").text(fmoney(coup.actuallyPaid ? coup.actuallyPaid : 0.00, 2)+"元");
                                    p1.find("span:eq(1)").text(fmoney(coup.orderAmount?coup.orderAmount:0),2);
                                    p1.find("span:eq(2)").text(fmoney(data.couponsAmount ? data.couponsAmount : 0.00, 2));
                                }
                            );
                        }
                    }
                }
            );

            // 要非取消状态才有采购信息
            if(status!=5){
                // 查询采购信息
                ajax_post("/sales/purchaseInfo", JSON.stringify({"orderId": sid}), "application/json",
                    function (response) {
                        var purchaseInfo_h4 = $("#order_detial_list").find("h4:eq(6)")
                        //清空原始数据
                        purchaseInfo_h4.empty().show()
                        var historySaleDetailList = response.historySaleDetailList
                        var html = '采购信息<ul><li>采购订单</li><li>商品编号</li><li>商品数量(个)</li><li>所含采购金额(元)</li><li>支付状态</li></ul>'
                        if (historySaleDetailList.length > 0) {
                            historySaleDetailList = mergeSameSkuIntoOne4PurchaseInfo(historySaleDetailList)
                            for(var i in historySaleDetailList){
                                var historyDetail = historySaleDetailList[i];
                                var purchasePrice = historyDetail.purchasePrice;
                                var totalPurchasePrice = parseFloat(purchasePrice) * parseFloat(historyDetail.qty);
                                var purchaseOrderNo = historyDetail.purchaseOrderNo
                                if(purchaseOrderNo==null || purchaseOrderNo==undefined) purchaseOrderNo=''
                                html += '<ul>'+
                                    '<li>'+purchaseOrderNo+'</li>'+
                                    '<li>'+historyDetail.sku+'</li>'+
                                    '<li>'+historyDetail.qty+'</li>'+
                                    '<li>'+totalPurchasePrice.toFixed(2)+'</li>'+
                                    '<li>已付款</li>'+
                                    '</ul>'
                            }
                        }else{
                            html += '<div style="text-align: center">暂无采购数据</div>'
                        }

                        html += '<p>'
                        if((status==1 || status==103) && parseFloat(response.amountToBePaid) > 0){ // 有才显示，没有就隐藏
                            html += '<span>待付款：<i>'+parseFloat(response.amountToBePaid).toFixed(2)+'</i>元</span>'
                        }
                        if(parseFloat(response.purchaseAmountTotal) > 0){
                            html += '<span>采购总计：<i>'+parseFloat(response.purchaseAmountTotal).toFixed(2)+'</i>元</span>'
                        }
                        html += '</p>'
                        purchaseInfo_h4.append(html);
                    }
                );
            }else{
                $("#order_detial_list").find("h4:eq(6)").empty().hide()
            }
        }
    );
}

// 合并相同purchaseOrderNo、sku、warehouseId、purchasePrice（到期日期导致）
function mergeSameSkuIntoOne4PurchaseInfo(historySaleDetailList){
    var historySaleDetailsMerged = new Array();
    var pNoSkuWarehousePurchasePrice2Detail = {}
    for(var i in historySaleDetailList){
        var pro = historySaleDetailList[i]
        var purchasePrice = pro.purchasePrice ? pro.purchasePrice : 0.00;
        var key = pro.sku+"_"+pro.warehouseId+"_"+purchasePrice
        if(pro.purchaseOrderNo){
            key = pro.purchaseOrderNo+"_"+key
        }
        var detail = pNoSkuWarehousePurchasePrice2Detail[key]
        if(detail){// 存在，合并数量
            pro.qty = parseInt(pro.qty)+parseInt(detail.qty);
        }
        pNoSkuWarehousePurchasePrice2Detail[key] = pro
    }

    $.each(pNoSkuWarehousePurchasePrice2Detail,function(k,v){
        historySaleDetailsMerged.push(v);
    })

    return historySaleDetailsMerged;
}

//取消通知发货
$(".box-right-four").on("click", ".cancleAudit", function () {
    var tthis = $(this).parents("div.three-2")[0];
    var node = this;
    btn_switch(node,true);
    layer.confirm("确定取消通知发货?", {icon: 3},
        function (i, currdom) {
            layer.close(i);
            var load_index = layer.load(1, {shade: 0.5});
            ajax_post("/sales/cancelNotification", JSON.stringify({"id": tthis.id}), "application/json",
            function (data) {
                if (data.suc) {//
                    layer.msg('所选订单已取消审核', {icon: 6, time: 2000}, function (index) {
                        $("#sstatus").val(2);
                        saleOrderList(null, false);
                        $(".sales-current").removeClass();
                        $($("#statusli li")[3]).addClass("sales-current");
                        btn_switch(node,false);
                        layer.close(index);
                        layer.close(load_index);
                    });
                }else{
                    layer.msg(data.msg, {icon: 6, time: 2000}, function (index) {
                        layer.close(load_index);
                        btn_switch(node,false);
                    });
                }
            },
            function (xhr, status) {
                btn_switch(node,false);
            }
        );
    });
});

//通知发货，修改订单为“待审核”状态
$(".box-right-four").on("click", ".informToSend", function () {
    var tthis = $(this).parents("div.three-2")[0];
    var node = this;
    btn_switch(node,true);
    layer.confirm("确定通知发货?", {icon: 3},
        function (i, currdom) {
            layer.close(i);
            var load_index = layer.load(1, {shade: 0.5});
            ajax_post("/sales/informShipping", JSON.stringify({"id": tthis.id}), "application/json",
                function (datad) {
                    if(datad.code){
                        layer.msg(datad.msg,{icon:5,time:2000},function(index){
                            layer.close(index);
                            window.location.href = "/personal/login.html";
                        });
                    }
                    if (datad) {
                        layer.msg('所选订单已通知发货，排队审核中，请耐心等候', {icon: 6, time: 3500}, function (index) {
                            $("#sstatus").val(3);
                            saleOrderList(null, false);
                            $(".sales-current").removeClass();
                            $($("#statusli li")[4]).addClass("sales-current");
                            btn_switch(node,false);
                            layer.close(index);
                            layer.close(load_index);
                        });
                    }else{
                        btn_switch(node,false);
                    }
                },
                function (xhr, status) {
                    btn_switch(node,false);
                }
            );
        }
    );
});

//通知发货，修改订单为“待审核”状态
$(".box-right-four").on("click", ".receiveConfirm", function () {
    var oriEle = $(this);
    var tthis = $(this).parents("div.three-2")[0];

    layer.confirm("您确定要确认收货吗？", {icon: 3},
        function (i, currdom) {
            layer.close(i);
            ajax_post("/sales/confirmReceivement", JSON.stringify({"id": tthis.id}), "application/json",
                function (datad) {
                    if(datad.code){
                        window.location.href = "/personal/login.html";
                    }
                    if (datad) {
                        layer.msg('确认收货成功！', {icon: 6, time: 3500}, function (index) {
                            layer.close(index);
                        });
                        $(tthis).find(".status").html("已收货");
                        var edit = oriEle.parent();
                        $(edit).empty();

                        //申请退款
                        var detail = saleMap[tthis.id];
                        if(detail && detail.length >0) {
                            for(var i = 0;i<detail.length;i++) {
                                var content = "&nbsp;&nbsp;<em class='word-white btn-hyacinthine-small toRefundsApply' data-detailid='"+ detail[i].id +"' data-orderNo='"+  detail[i].salesOrderNo +"' data-productName='"+ detail[i].productName +"' data-productImg='"+ detail[i].productImg+"' data-warehouseName='"
                                    + detail[i].warehouseName + "' data-warehouseId='"+detail[i].warehouseId +"' data-sku='" + detail[i].sku + "' data-qty='" + detail[i].qty +"' data-id='"+detail[i].salesOrderId+"'>申请退款</em>";
                                var target = $(tthis).find('.three-2-tr2')[i];
                                $(target).find('.qty').append(content);
                            }
                        }
                    }
                },
                function (xhr, status) {
                }
            );
        }
    );
});

//根据状态查询销售订单
$(".box-right-four").on("click", "#statusli li", function () {
    $("#sstatus").val(($(this).data("status")));
    if($("#sstatus").val() == "ats"){
        $(".three-2-box").hide();
        $("#after-div").show();
        xsshSaleOrderList(null, false);
        return;
    }
    $("#after-div").hide();
    $(".three-2-box").show();

    if ($("#sstatus").val() == 9) {
        $(".logisticsExport").show();
    } else {
        $(".logisticsExport").hide();
    }
    saleOrderList(null, false);
});

//根据单号或者收货人查询订单
$(".box-right-four").on("click", "#search", function () {
    var status = $(".sales-current").data('status');
    if(status == 'ats') {
       xsshSaleOrderList(null,false);
        return;
    }
    saleOrderList(null, false);
});

//清空订单数据
function resetFormData() {
    //清空集合
    tempProCollection = {};
    var holder = $("#pro4send tbody");
    var msg = "<tr><td colspan='8' style='text-align: center;padding: 10px;'>暂无已选商品</td></tr>";
    holder.empty().append(msg);
    $(".isShow").hide();
    $(".outLine").hide();
    $(".amount_").hide();
    $("#platformNo").val("");
    $("#tradeNo").val("");
    $("#actualAmount").val("").removeAttr("data-oral");
    $("#postage").val("");
    $("#remark").val("");
    $("#isNotify").prop("checked", true);
    $("#orderer").val("");
    $("#ordererIDCard").val("");
    $("#ordererTel").val("");
    $("#ordererPostcode").val("");
    $("#buyerID").val("");
    //清空物流方式与费用
    $(".freight-methods").hide();
    $(".freight-methods").find("select").empty();
    $(".freight-methods").find("#freight").text(0);
    //情空店铺平台与店铺名称
    getShopPlatform();
    getShopName();
    $(".site-name").val("");
    //清空优惠码
    $(".promotion-input").val("");
    $(".des-promotion").hide();
    $(".des-promotion span").html(0);
    $(".promotion_total").find("span").eq(1).html(0);
    $(".promotion_total").find("span").eq(2).html(0);
    $(".promotion_total").find("span").eq(3).html(0);
    $(".promotion_total").find("span").eq(4).html(0);
    couponsCost = 0;
    coupons = undefined;
    $("#ck").prop("checked", false);
    initAreaSel();
}

//清除缓存数据
function clearBufferData(){
    ajax_get("/sales/clearBufferMemory", "", undefined, function (response) {}, function (xhr, status) {});
}

//取消新增/编辑发货单，取消查看详情
$(".box-right-four").on("click", ".close_detail", function () {
    $("#order_detial_list").fadeOut(200);
    $("#edit_detail").fadeOut(200);
    $("#sale_send").fadeIn(200);
    $(".promotion-code").hide();
    $(".promotion-subbox").hide();
    resetFormData();

    // 点击返回时，是否需要刷新列表
    if($(this).attr("refreshList")){
        $(this).removeAttr("refreshList")
        var currPage = 1
        if($("#pagination_sales").find(".laypage_curr").text())
            currPage = parseInt($("#pagination_sales").find(".laypage_curr").text())
        saleOrderList({curr:currPage}, false);
    }
});

$(".box-right-four").on("click", "#close_detail", function () {
    bufferMemory()//存入缓存
    $("#order_detial_list").fadeOut(200);
    $("#edit_detail").fadeOut(200);
    $("#sale_send").fadeIn(200);
    $(".promotion-code-box").hide();
    $(".promotion-subbox").hide();
});

//获取发货单详情，编辑发货单
$(".box-right-four").on("click", ".Edit_order", function () {
    $("#edit_detail").fadeIn(200);
    $("#sale_send").fadeOut(200);
    $("#updsalor").attr("disabled", false).removeAttr("style").val("提交订单");
    var orderdiv = $(this).parents("div.three-2")[0];
    getOrderDetailForUpdate(orderdiv);
});

//更新前，查看订单详情
function getOrderDetailForUpdate(orderdiv) {
    isnulogin(function (email) {
        $("#orderId").val(orderdiv.id);
        ajax_post("/sales/getBase", JSON.stringify({"orderId": orderdiv.id}), "application/json",
            function (data) {
                if(data.code){
                    window.location.href = "/personal/login.html";
                }
                //基本信息
                getShopName(data.shopId);
                $("#platformNo").val(data.platformOrderNo);
                $("#actualAmount").val(data.orderActualAmount).attr("data-oral", data.orderActualAmount);
                $("#postage").val(data.orderPostage);
                $("#tradeNo").val(data.tradeNo);
                $("#remark").val(data.remark);
                $("#orderer").val(data.orderer);
                $("#ordererIDCard").val(data.ordererIDCard);
                $("#ordererTel").val(data.ordererTel);
                $("#ordererPostcode").val(data.ordererPostcode);
                initReceiverList(data.addrId);

                //发货单详情
                ajax_post("/sales/getsdl", JSON.stringify({"orderId": orderdiv.id}), "application/json",
                    function (datad) {
                        if (datad.length > 0) {
                            var holder = $("#pro4send tbody");
                            //如果存在已选商品，则展示
                            holder.empty();
                            for (var i = 0; i < datad.length; i++) {
                                var pro = {
                                    sku: datad[i].sku,
                                    imgurl: datad[i].productImg,
                                    title: datad[i].productName,
                                    price: datad[i].purchasePrice,
                                    qty: datad[i].qty,
                                    marketPrice: datad[i].marketPrice,
                                    wareName: datad[i].warehouseName,
                                    wareId: datad[i].warehouseId,
                                    finalSellingPrice: datad[i].finalSellingPrice,
                                    listFlag: true,
                                    maxqty: 0
                                }
                                // 发请求获取云仓微仓库存
                                var param = {pageSize: "10", currPage: "1", title: pro.sku, warehouseId: pro.wareId};
                                ajax_post_sync("/product/api/getProducts",JSON.stringify({data: param}),"application/json",function (data) {
                                    var reData = data.data;
                                    if (reData.totalPage > 0) {
                                        var result = reData.result;
                                        for (var j in result) {
                                            if (result[j].csku == pro.sku) {
                                                pro.maxqty += result[j].stock;
                                            }
                                        }
                                    }
                                    //发请求获取微仓库存
                                    var microParam = {pageSize: pageSize, currPage: pageCount, skus: [pro.sku]};
                                    ajax_post_sync("/inventory/getIvysAndStorage",JSON.stringify(microParam), "application/json",
                                        function(response){
                                            var datas = JSON.parse(response);
                                            for (var k = 0; k < datas.data.list.length; k++) {
                                                var cur = datas.data.list[k];
                                                if (cur.sku == pro.sku) {
                                                    pro.maxqty += cur.avaliableStock;
                                                }
                                            }
                                        }
                                    )
                                })
                                tempProCollection[pro.sku] = pro;
                            }

                            for (var k in tempProCollection) {
                                var pro = tempProCollection[k];
                                holder.append("<tr style='line-height: initial;width:35%'>" +
                                    "<td>" +
                                    "<a target='_blank' href='../../product/product-detail.html?sku=" + pro.sku + "&warehouseId=" + pro.wareId + "' style='display: inline-block;'>" +
                                    "<img style='width:80px;height: 80px' src='" + urlReplace(pro.imgurl, pro.sku) + "'/>" +
                                    "</a>" +
                                    "<a target='_blank' href='../../product/product-detail.html?sku=" + pro.sku + "&warehouseId=" + pro.wareId + "' style='display: inline-block;'>" +
                                    "<span>" + pro.title + "</span>" +
                                    "</a>" +
                                    "</td><td>" + pro.sku + "</td>" +
                                    "<td>" + pro.price + "</td>" +
                                    "<td>" +
                                    "<input style='width:80px;text-align:center' class='finalSellingPrice' placeholder='请如实填写' " + (pro.finalSellingPrice ? "value ='" + pro.finalSellingPrice + "'" : "") + "/>" +
                                    "</td>" +
                                    "<td style = 'width:10%'>" +
                                    "<span class = 'minusNum' onclick = 'editNum(this," + pro.maxqty + ")'>-</span>" +
                                    "<input class = 'pro_qty' value = '" + pro.qty + "'/>" +
                                    "<span class = 'plusNum' onclick ='editNum(this," + pro.maxqty + ")'>+</span>" +
                                    "</td>" +
                                    "<td id='" + pro.wareId + "'>" + pro.wareName + "</td>" +
                                    "<td>" + (parseFloat(pro.price) * parseInt(pro.qty)).toFixed(2) + "</td>" +
                                    "<td>" +
                                    "<span class='delpro'>" +
                                    "<a href='javascript:;' style='display: block;width: 100%;height: 100%'>删除</a>" +
                                    "</span>" +
                                    "<input type='hidden' class='marketPrice' value='" + pro.marketPrice + "'>" +
                                    "</td>" +
                                    "</tr>");
                            }
                            readyList();
                        }
                    },
                    function (xhr, status) {
                    }
                );
            },
            function (xhr, status) {
            }
        );
    });
}

/**
 * 提交/变更发货单
 */
$(".box-right-four").on("click", "#updsalor", function () {
    //校验
    if (!validationFrom() || (productType && !validationOrderer()) ) {
        return;
    }
    var code = $(".promotion-input").val().trim();
    if(code){
        if(!validationCode(code)){
            return;
        }
    }else{
        couponsCost = 0.00;
        $('.promotion-code-box').hide();
    } 
    //防止重复提交
    $("#updsalor").attr("disabled", true).css("background", "#eaeaea").css("color", "#000");

    isnulogin(function (email) {
        var shop_id = $("#shop").val();
        //说明选择的是其他店铺，这里就需要进行店铺添加操作
        if (shop_id == "0"){
            shop_id = incrementShop();
            if(!shop_id){
                layer.msg("新增店铺失败", {icon : 2, time : 3000});
                return;
            }
        }

        var email = email
        //收货地址要有空格
        var address = $("#province option:selected").text() + " " + $("#city option:selected").text() + " " + $("#region option:selected").text() + " " + $("#addr").val().trim();
        var details = [];
        $("#pro4send tbody").find("tr").each(function () {
            var pro = {
                sku: $(this).find("td:eq(2)").text(),
                num: $(this).find("td:eq(5)").find(".pro_qty").val(),
                productName: $(this).find("td:eq(1)").find("span").text(),
                productImg: $(this).find("td:eq(1)").find("img").attr("src"),
                purchasePrice: $(this).find("td:eq(3)").text(),
                marketPrice: $(this).find(".marketPrice").val(),
                warehouseId: $(this).find("td:eq(6)").attr("id"),
                warehouseName: $(this).find("td:eq(6)").text(),
                finalSellingPrice: $(this).find("td:eq(4)").find("input").val()
            }
            details.push(pro);
        });

        var total = 0;
        var postfrei = $("#freight").text();
        $.each(tempProCollection, function (i, item) {
            if (parseInt(item.stock) < parseInt(item.qty)) {
                total += item.price * (item.qty - item.stock);
            }
        });
        // 采购金额加上运费
        var cost = parseFloat(total + parseFloat(postfrei ? postfrei : 0)).toFixed(2);
        var param = {
            "isNotified": $("#isNotify").prop("checked") ? 1 : 0,
            "email" : email,
            "tradeNo": $("#tradeNo").val(),
            "platformOrderNo":$("#platformNo").val(),
            "orderPostage" : $("#postage").val(),
            "warehouseId" : details[0].warehouseId,
            "warehouseName" : details[0].warehouseName,
            "address" : address,
            "receiver": $("#receiver").val(),
            "telphone" : $("#tel").val(),
            "idcard": $("#idcard").val(),
            "postCode": $("#postCode").val(),
            "orderActualAmount": $("#actualAmount").attr("data-oral"),// 实付款
            "remark": $("#remark").val(),
            "orderer": $("#orderer").val(),
            "ordererIDCard": $("#ordererIDCard").val().toUpperCase(),
            "ordererTel": $("#ordererTel").val(),
            "buyerID": $("#buyerID").val(),
            "collectAccount": $("#collectionAccount").val(),//收款账号
            "LogisticsTypeCode": $(".freight-methods").find("select option:selected").attr("code"),// 运送方式
            "logisticsMode": $(".freight-methods").find("select option:selected").text(),// 运送方式中文描述
            "createUser": email,
            "isBack": false,
            "provinceId" : $("#province option:selected").val(),
            "skuList" : details,
            "shopId": shop_id,
            "couponsCode": $(".promotion-input").val(),// 优惠码
             "purchaseTotal": cost
        };
        var loading_index = layer.load(1, {shade: 0.5});
        ajax_post_sync("/sales/postOrder",JSON.stringify(param),"application/json",function (response) {
            //删除缓存数据
            clearBufferData();
            layer.close(loading_index);
            if(response.code == 108) {
                layer.msg("订单已生成：" + response.msg, {icon: 6, time: 2000},function(index){
                    $("#edit_detail").fadeOut(200);
                    $("#sale_send").fadeIn(200);
                    $("#statusli").find("li[data-status='0']").click();
                    $(window).scrollTop(0);
                    $(".promotion-code").hide();
                    resetFormData();
                    layer.close(index);
                });
            } else {
                layer.msg(response.msg, {icon: 5, time: 2000});
                if(response.code == 101) {
                    $("#updsalor").attr("disabled", false).removeAttr("style").val("提交订单");
                }
            }
        },function(e){
            layer.close(loading_index);
        })
    });
});

// 支付运费
function payBbcPostage(obj, paidPrama) {
    isnulogin(function (email) {
        if (obj) {
            paidPrama = {}
            var id = "#" + $(obj).data("id");
            paidPrama.salesOrderNo = $(id).find(".orderno").text();
            paidPrama.bbcPostage = $(id).find(".bbcPostage").val();
            paidPrama.mainId = $(obj).data("id");
        }
        $.ajax({url: "/member/account?" + (new Date()).getTime(), type: "get", dataType: "json",
            success: function (data) {
                if (data.errorCode == "0") {
                    //运费支付 弹出窗
                    var bbcpostfee = '<div class="suborder-pay" >' +
                        '<h3>你购买的商品为完税商品，需要支付运费￥<span>' + paidPrama.bbcPostage + '</span>元，立即支付吗？</h3>' +
                        '<div class="suborder-pay-content" style = "width:70%">' +
                        '<div>' +
                        '<span>可支付余额：</span>' +
                        '<p>￥<span>' + fmoney(parseFloat(data.balance),2) + '</span><a href="#" style ="display:' + (eval(paidPrama.bbcPostage) > eval(data.balance) ? "block" : "none") + '">您当前的余额不足，请立即充值！</a></p>' +
                        '</div>' +
                        '<div>' +
                        '<span>使用余额支付：</span>' +
                        '<p><b class="red">￥<span class = "totalAmount">' + paidPrama.bbcPostage + '</span></b></p>' +
                        '</div>' +
                        '<div>' +
                        '<span>余额支付密码：</span>' +
                        '<p><input id = "paid_pwd"  placeholder="请输入支付密码" type="text" onfocus="this.type=\'password\'" style = "width:180px"/></p>' +
                        '</div>' +
                        '<div style="display: none;">' +
	                        '<span style="width: 111px;">验证码：</span>' +
	                        '<p><input type="text" placeholder="请输入验证码" id="pay_captcha" name="pay_captcha" style="width: 180px;">'+
	                        '<img id="wb_pay_img" src="" onclick="this.src=\'/member/getcaptcha?v=\'+Math.random();"  style="display: inline-block; height: 27px; width: 92px; margin-left: 10px; vertical-align: -8px;cursor: pointer">'+
	                        '</p>' +
                        '</div>' +
                        '</div>' +
                        '</div>';
                    layer.open({
                        type: 1,
                        title: '运费结算',
                        area: ['590px', '300px'],
                        shade: 0,
                        content: bbcpostfee,
                        btn: ['确定', '取消'],
                        yes: function (index) {
                            //余额支付
                            if (!$("#paid_pwd").val()) {
                                layer.msg("请输入密码", {icon: 5, time: 2000});
                            }
                            var paycode = $("#paid_pwd").val();
                            var traAm = $(".red .totalAmount").text();
                            var salesOrderNo = paidPrama.salesOrderNo;
                            //验证码
							var payCaptcha = $("#pay_captcha").val();
                            var param = {
                                email: email,
                                transferAmount: traAm,
                                transferNumber: salesOrderNo,//销售订单号
                                paycode: paycode,
                                applyType: "5",  //运费支付状态码
                                //验证码 by huchuyin 2016-10-8
								payCaptcha:payCaptcha==undefined?"":payCaptcha
                            };
                            ajax_post("/member/balancePayment",JSON.stringify(param),"application/json",function (data) {
                                if (data.code == "1") {
                                    window.location.href = "login.html";
                                } else if (data.code == "4") { // 支付成功
                                    var state = 2;
                                    //判断是否通知发货
                                    if(paidPrama.isNotified == 1){
                                        state = 3;
                                    }   
                                    // 支付成功 更新订单状态
                                    ajax_post("/sales/updStu", JSON.stringify({
                                            "id": paidPrama.mainId, "status": state, "actualPay":traAm
                                        }), "application/json",
                                        function (updStuRes) {
                                            try{
                                                //同步支付信息到客户订单
                                                ajax_post_sync("/sales/updPaymentInfo",
                                                    JSON.stringify({"sid":paidPrama.mainId,"payDate":new Date().format('yyyy-MM-dd hh:mm:ss'),"payType":"system"}),
                                                    "application/json",
                                                    function(datad) {}
                                                );
                                            }catch(e){
                                            }
                                            layer.msg(data.info + "<br>订单状态已更新", {icon: 6, time: 3000}, function () {
                                                layer.close(index);
                                                if (obj) {
                                                    $(obj).removeAttr("onclick").addClass("informToSend").text("通知发货");
                                                    $(obj).parent().siblings().find(".status").text("待确认");
                                                } else {
                                                    showorderList();
                                                    $(".promotion-code-box").hide();
                                                    // 看订单详情
                                                    goSeeOrderDetail(paidPrama.mainId)
                                                }
                                            });
                                        },
                                        function (xhr, status) {
                                        }
                                    );
                                } else {
                                    layer.msg(data.info, {icon: 5, time: 3000});
                                    //验证码处理 by huchuyin 2016-10-8
                                    initPayCaptcha();
                                }
                            })
                        },
                        cancel: function (index) {
                            layer.close(index);
                            if (!obj) {
                                showorderList();
                            }
                        }
                    });
                    //验证码处理 by huchuyin 2016-10-8
					initPayCaptcha();
                }
            },
            error: function () {
                layer.msg("获取支付信息失败！", {icon: 5, time: 2000});
            }
        });
    });
}

function showorderList() {
    $("#sstatus").val(0)
    $("#search").click();
    $("#edit_detail").fadeOut(200);
    //清空内容
    $("#edit_detail").find("input").val("");
    // $("#edit_detail").find(".stockInfo").empty();
    $("#remark").val("");
    tempProCollection = {};
    $("#sale_send").fadeIn(200);
}

//初始化分页栏
function init_pagination_sales(total, currPage) {
    scrollPosTop();
    if ($("#pagination_sales")[0] != undefined) {
        $("#pagination_sales").empty();
        laypage({cont: 'pagination_sales', pages: total, curr: currPage, groups: 5, skin: 'yahei',
            first: '首页', last: '尾页', prev: '上一页', next: '下一页', skip: true,
            jump: function (obj, first) {
                if (!first) {
                    saleOrderList(obj, true, laypage);
                }
            }
        });
    }
}

/**
 * 金额校验
 * @param numVal 被校验的值
 * @param msg 被校验项的核心提示，“运费”，“总金额”等等
 * @returns {boolean}
 */
function numValidation(numVal, msg) {
    var reg = /^[0-9]+(.[0-9]{1,2})?$/;
    if (numVal) {
        if (!reg.test(numVal)) {
            layer.msg("请输入有效的[<b style='color:#E74C3C'>" + msg + "</b>]（至多两位小数）", {icon: 2, time: 2500});
            return false;
        }
        return true;
    } else {
        layer.msg("请输入有效的[<b style='color:#E74C3C'>" + msg + "</b>]（至多两位小数）", {icon: 2, time: 2500});
        return false;
    }
}


/**
 * 金额校验
 * @param numVal 被校验的值
 * @param msg 被校验项的核心提示，“运费”，“总金额”等等
 * @returns {boolean}
 */
function numValidation_new(numVal, msg) {
    var reg = /^[0-9]+(.[0-9]{1,4})?$/;
    if (numVal) {
        if (!reg.test(numVal)) {
            layer.msg("请输入有效的[<b style='color:#E74C3C'>" + msg + "</b>]（至多四位小数）", {icon: 2, time: 2500});
            return false;
        }
        return true;
    } else {
        layer.msg("请输入有效的[<b style='color:#E74C3C'>" + msg + "</b>]（至多四位小数）", {icon: 2, time: 2500});
        return false;
    }
}

//校验订购人信息
function validationOrderer() {
    var orderer = $("#orderer").val().trim();
    if (!orderer) {
        layer.msg('需要为客户订单添加买家姓名', {icon: 2, time: 2000});
        return false;
    }
    var ordererIDCard = $("#ordererIDCard").val().trim().toUpperCase();
    if (!ordererIDCard) {
        layer.msg('需要为客户订单添加买家身份证', {icon: 2, time: 2000});
        return false;
    }
    if (!checkIDCard(ordererIDCard)) {
        layer.msg('买家身份证格式不正确，请重新输入', {icon: 2, time: 2000});
        return false;
    }
    var ordererTel = $("#ordererTel").val().trim();
    if (!ordererTel) {
        layer.msg('需要为客户订单添加买家手机号', {icon: 2, time: 2000});
        return false;
    }
    if (!checkTel(ordererTel)) {
        layer.msg('买家手机号格式不正确，请重新输入', {icon: 2, time: 2000});
        return false;
    }
    return true;
}

//提交之前的保单校验
function validationFrom() {
    var shop = $("#shop").val();
    if (!shop) {
        layer.msg('平台店铺不可为空，请前往我的店铺添加', {icon: 2, time: 2000});
        return false;
    }
    if (shop == "0") {//表明平台店铺选择的是其他
        var shoptype =  $("#site-type").val();
        var shopname = $(".site-name").val();
        if (shoptype.trim() == "" || shopname.trim() == ""){
            layer.msg('平台店铺信息不完善，请完善店铺信息', {icon: 2, time: 2000});
            return false;
        } else {//验证填写的店铺信息在系统中是否已经存在
            var shopFlag = false;
            $.ajax({
                url: "/member/checkShopName",
                type: 'POST',
                data: {shopName:shopname,type:shoptype},
                async: false,//是否异步
                dataType: 'json',
                success: function (data) {
                    if (data.code && data.code == 1) {//说明没有查到相关店铺信息
                        shopFlag = true;
                    } else if (data.suc) {
                        layer.msg("该店铺已存在", {icon : 2, time : 2000});
                    } else {
                        layer.msg(data.msg, {icon : 2, time : 2000});
                    }
                },
                error: function (XMLHttpRequest, textStatus) {
                }
            });
            if (!shopFlag) {
                return false;
            }
        }
    }
    var shopType = $("#shop").find("option[value='" + shop + "']").attr("shopType");
    var platfomNo = $("#platformNo").val().trim();
    if(distributorType == 3){
        if (!platfomNo){
            layer.msg('平台订单号不能为空', {icon: 2, time: 2000});
            return false;
        }
    }
    var tradeNo = $("#tradeNo").val().trim();
    var regTradeNo = /^[0-9a-zA-Z-]+$/;
    if(distributorType == 3){
        if (!regTradeNo.test(tradeNo)){
            layer.msg('交易号格式错误，请输入数字与英文字母', {icon: 2, time: 2000});
            return false;
        }
    }
    var actualAmount = $("#actualAmount").attr("data-oral");
    var postage = $("#postage").val().trim();
    var skus = Object.keys(tempProCollection);
    if (skus.length == 0) {
        layer.msg('请选择商品', {icon: 2, time: 2000});
        return false;
    }
    var wareId = tempProCollection[skus[0]].wareId;
    if(wareId != 2024){
        if (!numValidation(actualAmount, "实付款")) {
            return false;
        }
        if (!numValidation(postage, "运费")) {
            return false;
        }
    }else{
        if(distributorType == 3){
            if (!numValidation(actualAmount, "实付款")) {
                return false;
            }
            if (!numValidation(postage, "运费")) {
                return false;
            }
        }
    }
    var msg = "";
    var flag = true;
    $.each(skus,function(i,item){
        var pro = tempProCollection[item];
        if( Number(pro.batchNum) >Number(pro.qty)){
            flag =false;
            msg += "[<b style='color:#E74C3C'>" + pro.sku + "</b>]"
        }
    });
    if(!flag){
        layer.msg(msg+"的数量必须大于起批量",{icon:5,time:2000});
        return false;
    }

    //每个发货产品必须有真实售价
    var fspFlag = true;
    $("#pro4send tbody").find("tr").each(function () {
        var tthis = $(this);
        var curfsp = tthis.find(".finalSellingPrice").val();
        if (!numValidation_new(curfsp, "真实单价")) {
            fspFlag = false;
            tthis.find(".finalSellingPrice").focus();
            return false;
        }
    });
    //收货人姓名验证
    var receiver = $("#receiver").val();
    if (!receiver) {
        layer.msg('收货人不能为空', {icon: 2, time: 2000});
        return false;
    }
    //收货人手机验证
    var tel = $("#tel").val();
    if (!checkTel(tel)) {
        layer.msg('收货人手机号码有格式错误，请输入有效手机号码', {icon: 2, time: 2000});
        return false;
    }
    //收货地址验证
    var addr = $("#addr").val();
    if (!addr || addr.length < 5) {
        layer.msg('收货地址不规范，请重新填写', {icon: 2, time: 2000});
        return false;
    }
    //收货人身份证验证
    var idcard = $("#idcard").val();
    if (productType && !checkIDCard(idcard)) {
        layer.msg('请输入有效的收货人身份证', {icon: 2, time: 2000});
        return false;
    }
    var postCode = $("#postCode").val();
    if (postCode && !checkPost(postCode)) {
        layer.msg('请输入有效的收货地址邮政编码', {icon: 2, time: 2000});
        return false;
    }
    //收货人邮编验证
    if (!fspFlag) {
        return false;
    }
    return true;
};
//选择当前状态
function salesSelected(obj) {
    $(".sales-current").removeClass();
    $(obj).addClass("sales-current");
}

/*************************************************下述脚本为销售发货订单中的采购进货功能所用***********************************************************/

//勾选产品，则暂时保存
//点击checkbox调用获取商品信息方法
$(".box-right-four").on("click", ".seldPro", function () {
    var goal = $(this);
    getProInfo(goal);
});

var productType = true;//跨境商品标示，false标示完税
// 选中要发货的商品后获取商品信息
function getProInfo(obj) {
    if (obj.prop('checked')) {
        //选中某个产品
        var pro = {};
        pro.sku = obj.parent().siblings().eq(1).text();
        pro.imgurl = obj.parent().siblings().eq(0).find("img").attr("src");
        pro.title = obj.parent().siblings().eq(0).find("a").text();
        pro.price = obj.parent().siblings().eq(2).text();
        pro.marketPrice = obj.parent().siblings().eq(3).text();
        pro.qty = obj.parent().siblings().eq(7).find("input.numToSend").val();
        pro.wareName = obj.parent().siblings().eq(6).text();
        pro.wareId = obj.parent().siblings().eq(6).attr("name");
        pro.disTotalCost = obj.parent().find("input[name=disTotalCost]").val();
        pro.finalSellingPrice = undefined;
        pro.stock = obj.parent().siblings().eq(5).text();
        pro.listFlag = false;// 发货列表展示标示  在列表中true，不在false
        pro.maxqty = (parseInt(obj.parent().siblings().eq(4).text()) < 0 ? 0 : parseInt(obj.parent().siblings().eq(4).text())) + parseInt(obj.parent().siblings().eq(5).text());
        pro.batchNum = obj.parent().siblings().eq(7).find("input.numToSend").attr("batchNum");
        var skus = Object.keys(tempProCollection);
        for (var i in skus) {
            if (tempProCollection[skus[i]].wareId != pro.wareId) {
                layer.msg('不同仓库商品请分开下单，系统自动拆单正在开发中!', {icon: 6}, function (index) {
                    layer.close(index);
                });
                obj.attr("checked", false);
                return;
            }
        }

        // 根据仓库来决定是否显示
        showdiv(pro.wareId);
        if (tempProCollection[pro.sku]) {
            pro.finalSellingPrice = tempProCollection[pro.sku].finalSellingPrice;
            if (tempProCollection[pro.sku].listFlag) {
                pro.listFlag = true;
            }
        }
        tempProCollection[pro.sku] = pro;
    } else {
        var pro = tempProCollection[obj.parent().siblings().eq(1).text()];
        //取消选中某个产品,且该商品不在列表中,从集合中删除
        if (pro && !pro.listFlag) {
            delete tempProCollection[pro.sku];
        }
    }
    countPrice();
}

//根据商品类型判断是否显示
function showdiv(wareId){
    if (wareId == 2024) {
        productType = false;
        if (offline) {
            $("p[class=outLine]").hide();
            $("p[class=amount_]").hide();
        } else {
            $("p[class=outLine]").show();
            $("p[class=amount_]").show();
        }
        $("h3[tag=order]").hide();
        $("p[tag=order]").hide();
        $("div[tag=order]").hide();
    } else {
        $("p[class=outLine]").show();
        $("h3[tag=order]").show();
        $("p[class=amount_]").show();
        $("p[tag=order]").show();
        $("div[tag=order]").show();
        productType = true;
    }
}
/**
 * 待选弹窗居中
 * @param aim
 */
function MakeCenter(aim) {
    var winHeight = $(window).height();
    var winWidth = $(window).width() - 24;
    var maxHeight = 1314;
    var aimHeight = winHeight > maxHeight ? maxHeight : winHeight - 50;
    var aimWidth = aim.width();
    var box = {
        boxTop: (winHeight - aimHeight) / 2,
        boxLeft: (winWidth - aimWidth) / 2
    };
    aim.height(aimHeight);
    aim.css({
        top: box.boxTop,
        left: box.boxLeft
    });
    return box;
}

/**
 * 待选列表展示
 */
$(".box-right-four").on("click", ".add-detail-pro", function () {
    isChange = true;
    var proSelect = $('.pro-to-select');
    var index = layer.load(1, {
        shade: [0.1, '#fff'] //0.1透明度的白色背景
    });
    posTopAndLeft = new MakeCenter(proSelect);
    proSelect.load("purchase.html", function (response, status, xhr) {
        require(["laypage", "layer", "goods"], function (laypage, layer) {
            init_catgs_forSaleOrder();
            layer.close(index);
            $('.recharge_black').fadeIn(300);
            proSelect.fadeIn(300);
            $('.check-mine-price').hide();
            $(".show-seld-detail-pro").show();
            init_goods_forSaleOrder(laypage, layer);
            $(".closeFixed").show();
            //禁用滚动
            proSelect.parents().find("body").css("overflow-y","hidden"); 
        });
        $('.pro-to-select').find('.title').html('添加商品');
    });
});

//定义fixed部分位置方法
function makePosit() {
    $('.purchase-product-search').css({
        'left': posTopAndLeft.boxLeft + 1 + 'px'
    });
    $('.search-bg').css({
        'left': posTopAndLeft.boxLeft + 1 + 'px'
    });
    if (!isDown) {
        $('.showFixed').css({
            'left': posTopAndLeft.boxLeft + boxWidth - 185 + 'px'
        });
        $('.closeFixed').css({
            'left': posTopAndLeft.boxLeft + boxWidth - 45 + 'px'
        });
    } else {
        $('.showFixed').css({
            'right': '88px'
        });
        $('.closeFixed').css({
            'right': '14px'
        });
    }
}

//当屏幕大小改变时弹出窗口内fixed部分定位
var isChange = false;
var boxWidth = $('.pro-to-select').width();
window.onresize = function () {
    var winWidth = $(window).width();
    if (winWidth <= 1000 && isDown == false) {
        makePosit();
        return;
    }
    posTopAndLeft = new MakeCenter($('.pro-to-select'));
    if (winWidth > 1000) {
        makePosit()
    }
};

//当鼠标滚轮滚动时,悬浮窗部分内容固定位置
var isDown = true;
$('.pro-to-select').scroll(function () {
    if ($(this).scrollTop() < 120 && isDown) {
        contentFixed(false);
        return;
    }
    if ($(this).scrollTop() == 0 && isDown == false) {
        contentFixed(true);
        return;
    }
});

//固定方法
function contentFixed(goBack) {
    var searchBox = $('.purchase-product-search'),
        purList = $("#purchase_list"),
        showFixed = $('.showFixed'),
        closeFixed = $('.closeFixed');

    if (!goBack) {
        purList.append("<div class='search-bg'></div>");
        $('.search-bg').css({
            'width': purList.width(),
            'top': posTopAndLeft.boxTop + 1,
            'left': posTopAndLeft.boxLeft + 1,
            'box-shadow': '0 0 20px #cccccc',
            '-moz-box-shadow': '0 0 20px #cccccc',
            '-webkit-box-shadow': '0 0 20px #cccccc'
        });
        searchBox.css({
            'position': 'fixed',
            'width': searchBox.width(),
            'top': posTopAndLeft.boxTop + 170,
            'z-index': 100
        }).stop().animate({
            'top': posTopAndLeft.boxTop + 10,
            'width': '735px'
        }, 150);
        showFixed.css({
            'position': 'fixed',
            'top': posTopAndLeft.boxTop + 9,
            'right': posTopAndLeft.boxLeft + 90,
            'z-index': 100
        });
        closeFixed.css({
            'position': 'fixed',
            'top': posTopAndLeft.boxTop + 11,
            'left': posTopAndLeft.boxLeft + boxWidth - 45,
            'z-index': 100
        });
        isDown = false;
        //$('.pro-to-select').stop().animate({'scrollTop': '120px'}, 50,function(){
        //    isDown = false;
        //});
    } else {
        purList.find('.search-bg').remove();
        $('.search-bg').removeAttr('style').stop(true, false).show();
        searchBox.removeAttr('style').stop(true, false).show();
        showFixed.removeAttr('style').stop(true, false).show();
        closeFixed.removeAttr('style').stop(true, false).show();
        isDown = true;
    }
}

/**
 * 已选列表展示
 */
$(".box-right-four").on("click", ".show-seld-detail-pro", function () {
    addproductShow();
});

//在客户订单添加
function addproductShow(){
    $(".pro-to-select").fadeOut();
    $(".pro-to-select").parents().find("body").css("overflow-y","scroll");
    $(".show-seld-detail-pro").hide();
    $(".purchase-product tbody").empty();
    //停止滚动事件
    if(jQuery.jStorage) {
        jQuery.jStorage.set("stop",true);
    }
    $(".closeFixed").hide();
    $('.recharge_black').fadeOut();
    var holder = $("#pro4send tbody");
    //如果存在已选商品，则展示
    if (Object.keys(tempProCollection).length > 0) {
        var skus = Object.keys(tempProCollection);
        productType = tempProCollection[skus[0]].wareId == 2024 ? false : true;
        for (var i in skus) {
            if (tempProCollection[skus[i]].wareId != tempProCollection[skus[0]].wareId) {
                layer.msg('不同仓库商品请分开下单，系统自动拆单正在开发中!', {icon: 6}, function (index) {
                    layer.close(index);
                });
                return;
            }
        }
        holder.empty();
        var index = 1;
        for (var k in tempProCollection) {
            var pro = tempProCollection[k];
            pro.listFlag = true;
            var finalSellingPrice = pro.finalSellingPrice;
            if (!finalSellingPrice) {
               finalSellingPrice = pro.wareId == 2024 ? pro.price : "";
            }
            finalSellingPrice = parseFloat(finalSellingPrice).toFixed(4);
            holder.append("<tr>" +
                "<td>"+index+"</td>" +
                "<td style='text-align: left;padding: 10px 0;'>" +
                "<a target='_blank' href='../../product/product-detail.html?sku=" + pro.sku +
                "&warehouseId=" + pro.wareId + "' class='inline-blo' style='width: 70px;'>" +
                "<img class='bbclazy' src='" + pro.imgurl + "'/>" +
                "</a>" +
                "<a style='padding-left: 20px;width: 250px;' target='_blank' class='vertical-top inline-blo' " +
                "href='../../product/product-detail.html?sku=" + pro.sku + "&warehouseId=" + pro.wareId + "' class='inline-blo'>" +
                "<span style='font-size: 12px;'>" + pro.title + "</span></a>" +
                "</td>" +
                "<td>" + pro.sku + "</td>" +
                "<td>" + pro.price + "</td>" +
                "<td>" +
                "<input style='width:80px;text-align:center;font-size: 12px' " +
                "class='finalSellingPrice' placeholder='请如实填写' value='" + finalSellingPrice + "' /></td>" +
                "<td style = 'width:10%'>" +
                "<span class = 'minusNum' onclick = 'editNum(this," + pro.maxqty + ")'>-</span>" +
                "<input class = 'pro_qty' style='font-size: 12px' batchNum = '"+pro.batchNum+"' value = '" + pro.qty + "'/>" +
                "<span class = 'plusNum' onclick ='editNum(this," + pro.maxqty + ")'>+</span>" +
                "</td>" +
                "<td id='" + pro.wareId + "'>" + pro.wareName + "</td>" +
                "<td>" + (parseFloat(pro.price) * parseInt(pro.qty)).toFixed(2) + "</td>" +
                "<td>" +
                "<span class='delpro'>" +
                "<a href='javascript:;' style='display: block;width: 100%;height: 100%;font-size: 12px;'>删除</a>" +
                "</span>" +
                "<input type='hidden' class='marketPrice' value='" + pro.marketPrice + "'>" +
                "</td>" +
                "</tr>");
            index += 1;
        }
        //构造列表
        readyList();
    }else{
        holder.empty();
    }
    //获取仓库对应的可选物流方式，目前只需要对深圳仓计算运费
    getFreight();
    $("#shop").change();
}

/**
 * 获取运费
 */
function getFreight() {
    var skus = Object.keys(tempProCollection);
    if (provinceIdForFreight == 0) {
        countPrice();
        return;
    }
    if (skus.length > 0) {
        var target = $(".freight-methods").find("select");
        //物流方式已添加，则选择既存物流方式
        if (target.children().length > 0) {
            //添加完毕，获取默认选中的物流方式的运费
            var freightParam = {};
            freightParam.warehouseId = tempProCollection[skus[0]].wareId;
            // freightParam.shippingMethodId = target.val();
            freightParam.shippingCode = target.find("option:selected").attr("code")
            var orderDetails = [];
            for (var k in tempProCollection) {
                var pro = tempProCollection[k];
                orderDetails.push({sku: pro.sku, num: pro.qty, costPrice: pro.disTotalCost});
            }
            freightParam.orderDetails = orderDetails;
            freightParam.countryId = 44;//写死为中国的id
            freightParam.provinceId = provinceIdForFreight;

            getFreightByProvince(freightParam); 
        } else {//物流方式未添加，则需要先获取物流方式，再进行计算
            //省份已选择的情况下再获取运费
            if (provinceIdForFreight != 0) {
                ajax_get("/inventory/getShippingMethod?wid=" + tempProCollection[skus[0]].wareId, "", "",
                    function (shipResStr) {
                        if(shipResStr.code){
                            window.location.href = "/personal/login.html";
                        }
                        var shipRes = $.parseJSON(shipResStr);
                        if (shipRes.length > 0) {
                            $(".freight-methods").css("display", "inline-block");
                            for (var i = 0; i < shipRes.length; i++) {
                                if (shipRes[i].default) {
                                    target.append("<option value='" + shipRes[i].id + "' code='" + shipRes[i].methodCode + "' selected='selected'>" + shipRes[i].methodName + "</option>")
                                } else {
                                    target.append("<option value='" + shipRes[i].id + "' code='" + shipRes[i].methodCode + "'>" + shipRes[i].methodName + "</option>")
                                }
                            }

                            //添加完毕，获取默认选中的物流方式的运费
                            var freightParam = {};
                            freightParam.warehouseId = tempProCollection[skus[0]].wareId;
                            freightParam.shippingCode = target.find("option:selected").attr("code");
                            var orderDetails = [];
                            for (var k in tempProCollection) {
                                var pro = tempProCollection[k];
                                orderDetails.push({sku: pro.sku, num: pro.qty, costPrice: pro.disTotalCost});
                            }
                            freightParam.orderDetails = orderDetails;
                            freightParam.countryId = 44;//写死为中国的id
                            freightParam.provinceId = provinceIdForFreight;
                            getFreightByProvince(freightParam);
                        } else {
                            layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
                            $("#updsalor").attr("disabled", true).css("background", "#eaeaea").css("color", "#000");
                        }
                    },
                    function (XMLHttpRequest, textStatus) {
                        layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
                    }
                );
            }
        }
    }else {
        $(".freight-methods").hide();
        $(".freight-methods").find("select").empty();
        $(".freight-methods").find("#freight").text(0);
        countPrice();
    }
}

//实时更新运费 用于提交时的更新
function real_time_freight() {
    var code = $(".freight-methods").find("select").find("option:selected").attr("code");
    return get_post_fee(code);
}

//根据物流code等参数 获取运费
function get_post_fee(code, flag) {
    var skus = Object.keys(tempProCollection);
    var orderDetails = [];
    for (var k in tempProCollection) {
        var pro = tempProCollection[k];
        orderDetails.push({sku:pro.sku, num:pro.qty, costPrice:pro.disTotalCost});
    }
    //自提标识
    var freightParam = {
        warehouseId: tempProCollection[skus[0]].wareId,
        shippingCode: code,
        orderDetails: orderDetails,
        countryId: 44,//写死为中国的id
        provinceId: provinceIdForFreight
    }
    var fee = 0;
    ajax_post_sync("/inventory/getFreight", JSON.stringify(freightParam), "application/json",
        function (freightResStr) {
            if(freightResStr.code){
                window.location.href = "/personal/login.html";
            }
            var freightRes = freightResStr;
            if (freightRes.result) {
                //运费
                fee = freightRes.msg ;
                fee = fee?parseFloat(fee).toFixed(2):0.00;
                if (flag) {
                    $("#updsalor").attr("disabled", false).removeAttr("style").val("提交订单");
                }
            } else {
                layer.msg("运费获取失败！" + freightRes.msg, {icon: 2, time: 2000});
                if (flag) {
                    $("#updsalor").attr("disabled", true).css("background", "#eaeaea").css("color", "#000");
                }
                fee = undefined;
            }
        },
        function (XMLHttpRequest, textStatus){
            layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
            if (flag) {
                $("#updsalor").attr("disabled", true).css("background", "#eaeaea").css("color", "#000");
            }
            fee = undefined;
        }
    )
    return fee;
}

/**
 * 根据上分ID获取运费
 * @param freightParam
 */
function getFreightByProvince(freightParam) {
    $(".myself-take").hide();
    ajax_post("/inventory/getFreight", JSON.stringify(freightParam), "application/json",
        function (freightResStr) {
            if(freightResStr.code){
                window.location.href = "/personal/login.html";
            }
            var freightRes = freightResStr;
            if (freightRes.result) {
                //自提标识
                var ztflag = (freightParam.shippingCode == "X2");
                if (ztflag) {
                    $(".myself-take").show();
                }
                var fee = freightRes.msg?freightRes.msg:0.00;
                $("#freight").text(parseFloat(fee).toFixed(2));
                countPrice();
                $("#updsalor").attr("disabled", false).removeAttr("style").val("提交订单");
            } else {
                layer.msg("运费获取失败！" + freightRes.msg, {icon: 2, time: 2000});
                $("#updsalor").attr("disabled", true).css("background", "#eaeaea").css("color", "#000");
            }
        },
        function (XMLHttpRequest, textStatus) {
            layer.msg("物流方式获取失败！", {icon: 2, time: 2000});
        }
    );
}

/**
 * 物流方式下拉框变动，运费联动
 */
$(".box-right-four").on("change", ".freight-methods select", function () {
    getFreight();
});

//发货列表加载完添加事件
function readyList() {
    //编辑真实售价时，将数据带入集合中
    $("#pro4send tbody").on("blur", ".finalSellingPrice", function () {
        var item = $(this);
        var sku = item.parent().siblings().eq(2).text();
        var pro = tempProCollection[sku];
        var finalSellingPrice = item.val().trim();
        item.val(finalSellingPrice);
        pro.finalSellingPrice = finalSellingPrice;
    });
    //发货列表修改发货数量
    $("#pro4send tbody").on("blur", ".pro_qty", function () {
        var item = $(this);
        var qty = item.val().trim();
        var sku = item.parent().siblings().eq(2).text();
        var pro = tempProCollection[sku];
        var maxqty = pro.maxqty;
        var batchNum = pro.batchNum;
        var regNum = /^[1-9]\d*$/;
        if (!regNum.test(qty)) {
            item.val(batchNum)
            layer.msg('请输入有效数字', {icon: 6}, function (index) {
                layer.close(index);
            });
        } else if (parseInt(qty) < batchNum) {
            layer.msg("购买数量必须大于或者等于商品起批量【" + batchNum + "】", {icon: 6}, function (index) {
                     layer.close(index);
            });
            item.val(batchNum);
        } else if (eval(qty) > eval(maxqty)) {
            layer.msg('发货数量不可超过[' + maxqty + ']', {icon: 6}, function (index) {
                layer.close(index);
            });
            item.val(maxqty);
        }
        pro.qty = item.val().trim();
        item.val(pro.qty);
        item.parent().siblings().eq(6).html((parseFloat(pro.price) * parseInt(pro.qty)).toFixed(2));
        getFreight();
    });
    var skus = Object.keys(tempProCollection);
    var wareId = tempProCollection[skus[0]].wareId;
    if (wareId == 2024) {
        if(distributorType != 3){
            out_dis_delivery(true);
        }else{
            out_dis_delivery(false);
        }
    } else {
        if( distributorType == 3){
            out_dis_delivery(false);
        }
        $("#actualAmount_span").css("display", "");
        $("#postage_span").css("display", "");
    }
}

/**
 * 关闭待选弹窗
 */
function closeBox() {
    $(".pro-to-select").fadeOut();
    $(".show-seld-detail-pro").hide();
    $(".purchase-product tbody").empty();
    $(".closeFixed").hide();
    $('.recharge_black').fadeOut();
    $(".pro-to-select").parents().find("body").css("overflow-y","scroll");
    //停止滚动时间
    jQuery.jStorage.set("stop",true);
    isChange = false;
    var skus = Object.keys(tempProCollection);
    for (var i in skus) {
        if (!tempProCollection[skus[i]].listFlag) {
            //关闭时删除选中未确定商品
            delete tempProCollection[skus[i]];
        }
    }
}

$(".box-right-four").on("click", ".closeFixed", function () {
    closeBox();
});

$("body").on("click", ".recharge_black", function () {
    if (isChange == true) {
        closeBox();
    }
});

/**
 * 待选列表数量增减
 */
$(".box-right-four").on("click", ".purchase-product-content .editNum", function () {
    var editNum = $(this);
    var goal,batchNum,qty,cloudqty;
    cloudqty = parseInt(editNum.parent().parent().siblings().eq(4).text()) < 0 ? 0 : parseInt(editNum.parent().parent().siblings().eq(4).text());
    var maxqty = cloudqty + parseInt(editNum.parent().parent().siblings().eq(5).text());
    if (editNum.hasClass("minus")) {
        qty = editNum.parent().next();
        batchNum = parseInt(qty.attr("batchNum"));
        if (isNaN(qty.val())) {
            qty.val(batchNum);
            layer.msg('请输入有效数字', {icon: 6}, function (index) {
                layer.close(index);
            });
        } else {
            if (parseInt(qty.val()) > batchNum) {
                qty.val(parseInt(qty.val()) - 1)
            } else {
                layer.msg("购买数量必须大于或者等于商品起批量【" + batchNum + "】", {icon: 6}, function (index) {
                    layer.close(index);
                });
            }
        }
    } else {
        qty = editNum.parent().prev();
        batchNum = parseInt(qty.attr("batchNum"));
        if (isNaN(qty.val())) {
            qty.val(batchNum)
            layer.msg('请输入有效数字', {icon: 6}, function (index) {
                layer.close(index);
            });
            //不可大于云仓与微仓库存总和
        } else if (eval(qty.val()) >= eval(maxqty)) {
            layer.msg('发货数量不可超过[' + maxqty + ']', {icon: 6}, function (index) {
                layer.close(index);
            });
        } else {
            qty.val(parseInt(qty.val()) + 1)
        }
    }
    //调用获取商品信息方法
    goal = $(this).parent().parent().prev().find('input[type="checkbox"]');
    //勾选时可修改，避免被作为取消勾选而删除
    if (goal.prop('checked')) {
        getProInfo(goal);
    }
});

//发货列表点击 加减发货数量
function editNum(obj, maxqty) {
    var editNum = $(obj),batchNum,qty;
    if (editNum.hasClass("minusNum")) {
        qty = editNum.next();
        batchNum = parseInt(qty.attr("batchNum"));
        if (isNaN(qty.val())) {
            qty.val(batchNum)
            layer.msg('请输入有效数字', {icon: 6}, function (index) {
                layer.close(index);
            });
        } else {
            if (parseInt(qty.val()) > batchNum) {
                qty.val(parseInt(qty.val()) - 1)
            } else {
                 layer.msg("购买数量必须大于或者等于商品起批量【" + batchNum + "】", {icon: 6}, function (index) {
                     layer.close(index);
                 });
            }
        }
    } else {
        qty = editNum.prev();
        batchNum = parseInt(qty.attr("batchNum"));
        if (isNaN(qty.val())) {
            qty.val(batchNum)
            layer.msg('请输入有效数字', {icon: 6}, function (index) {
                layer.close(index);
            });
            //不可大于云仓与微仓库存总和
        } else if (eval(qty.val()) >= eval(maxqty)) {
            layer.msg('发货数量不可超过[' + maxqty + ']', {icon: 6}, function (index) {
                layer.close(index);
            });
        } else {
            qty.val(parseInt(qty.val()) + 1)
        }
    }
    var sku = editNum.parent().siblings().eq(2).text();
    var pro = tempProCollection[sku];
    pro.qty = qty.val();
    editNum.parent().siblings().eq(6).html((parseFloat(pro.price) * parseInt(pro.qty)).toFixed(2));
    //TODO 数量编辑
    getFreight();
}

function countPrice() {
    $(".des-promotion span.red:eq(0)").text(couponsCost);
    if (coupons) {
        $(".des-promotion span.red:eq(1)").text(coupons.thresholdPrice);
    }
    var total = 0;
    var postfrei = $("#freight").text();
    $.each(tempProCollection, function (i, item) {
        if (parseInt(item.stock) < parseInt(item.qty)) {
            total += item.price * (item.qty - item.stock);
        }
    });
    $(".promotion-price .red:eq(1)").text(parseFloat(total).toFixed(2));
    $(".promotion-price .red:eq(2)").text(couponsCost);
    $(".promotion-price .red:eq(3)").text(postfrei);
    var cost = parseFloat(total - couponsCost + parseFloat(postfrei ? postfrei : 0)).toFixed(2);
    $(".promotion-price .red:eq(0)").text(parseFloat((cost < 0 ? 0 : cost)).toFixed(2));
}

/**
 * 数量编辑
 */
$(".box-right-four").on("blur", ".purchase-product-content .numToSend", function () {
    var item = $(this);
    var qty = item.val().trim();
    var cloudqty = parseInt(item.parent().siblings().eq(4).text()) < 0 ? 0 : parseInt(item.parent().siblings().eq(4).text());
    var maxqty = cloudqty + parseInt(item.parent().siblings().eq(5).text());
    var batchNum = parseInt(item.attr("batchNum")); 
    var regNum = /^[1-9]\d*$/;
    if (!regNum.test(qty)) {
        item.val(batchNum)
        layer.msg('请输入有效数字', {icon: 6}, function (index) {
            layer.close(index);
        });
    } else if (parseInt(qty) < batchNum) {
        layer.msg("购买数量必须大于或者等于商品起批量【" + batchNum + "】", {icon: 6}, function (index) {
            layer.close(index);
        });
        item.val(batchNum);
    } else if (eval(qty) > eval(maxqty)) {
        layer.msg('发货数量不可超过[' + maxqty + ']', {icon: 6}, function (index) {
            layer.close(index);
        });
        item.val(maxqty);
    } else {
        item.val(qty);
    }
    var goal = $(this).parent().prev().find('input[type="checkbox"]');
    //勾选时可修改，避免被作为取消勾选而删除
    if (goal.prop('checked')) {
        getProInfo(goal);
    }
});

/**
 * 删除已选记录中的产品
 */
$(".box-right-four").on("click", ".delpro", function () {
    $(this).parent().parent().remove();
    delete tempProCollection[$(this).parent().siblings().eq(2).text()];
    getFreight();
    deleteCount();
});

//删除数据时，初始化数据
function deleteCount(){
    var holder = $("#pro4send tbody");
    var skus = Object.keys(tempProCollection);
    if (skus.length < 1) {
        var msg = "<tr><td colspan='8' style='text-align: center;'>暂无已选商品</td></tr>";
        holder.append(msg);
        $(".freight-methods").hide();
        $(".freight-methods").find("select").empty();
        $(".freight-methods").find("#freight").text(0);
        $("input[name=select_ware]").prop("checked",false);
    }
    countPrice();
}


/*********************线下店铺验证**************************/
function init_shop_info(obj) {
    if (obj.value != "") {
        if (obj.value == 0) {
            $(".site-shop").show();
        } else {
            $(".site-shop").hide();
        }
        var opt = undefined, mode = obj.value;
        $.each($(obj).children(), function (i, item) {
            if (mode == item.value) opt = item;
        });
        var data = $(opt).attr("data-mode").split(",");
        //如果店铺为线下店铺则不显示平台订单号以及交易号
        var shopType = $(opt).attr("shopType");
        if (shopType != undefined && shopType == '线下店铺') {
            if (!productType) {
                $(".outLine").show();
                $(".amount_").show();
            }else{
                $(".outLine").hide();
                $("#platformNo").val("");
                $("#tradeNo").val("");
            }
            if (distributorType == 3) {
                $(".outLine").show();
            }
            offline = true;
        } else {
            var exist = false;
            $.each(tempProCollection, function (i, item) {
                exist = true;
                return false;
            });
            if (exist) {
                if (distributorType == 3) {
                    $(".outLine").show();
                    $("#platformNo_span").show();
                    $("#tradeNo_span").show();
                } else {
                    if (productType) {
                        $(".outLine").hide();
                        $("#platformNo").val("");
                        $("#tradeNo").val("");
                    } else {
                        $(".outLine").show();
                    }
                }
                $(".amount_").show();
                if (productType) {
                    $(".isShow").show();
                }
            } else {
                $(".outLine").hide();
                $("#platformNo").val("");
                $("#tradeNo").val("");
                $(".isShow").hide();
                $(".amount_").hide();
                $("#actualAmount").val("").removeAttr("data-oral");
                $("#postage").val("");
            }
            offline = false;
        }
        $("#collectionAccount").val(data[0]);
    } else {
        $("#platformNo_span").css("display", "");
        $("#tradeNo_span").css("display", "");
        $("#collectionAccount").val("");
    }
    if (Object.keys(tempProCollection).length) {
        readyList();
    }
}

//获取店铺平台
function getShopPlatform(type){
    $("#site-type").empty();
    ajax_get("/member/getshopplat", {}, undefined,
        function (response) {
            var optionHTML = '<option value="">请选择平台店铺</option>';
            for (var i in response) {
                optionHTML += '<option value="' + response[i].id + '">' + response[i].shopPlatform + '</option>';
            }
            $("#site-type").append(optionHTML);
            $("#site-type").change();
        },
        function (xhr, status) {
            layer.msg('获取店铺平台数据出错，请稍候重试', {icon: 2, time: 2000});
        }
    );
    if (type) {
        $("#site-type").val(type);
    }
}

//外部分销商 线上线下店铺 完税商品
function out_dis_delivery(flag){
    if(!flag){
        $("#platformNo_span").show();
        $("#tradeNo_span").show();
        //$(".date_").show();
        $("#actualAmount_span").show();
        $("#postage_span").show();
    }else{
        $("#platformNo_span").hide();
        $("#tradeNo_span").hide();
        //$(".date_").hide();
        $("#actualAmount_span").hide();
        $("#postage_span").hide();
    }
}

function initPaymentAddress(e, postflag) {
    isnulogin(function (email) {
        if (postflag) {//运费支付标识
            var saleNO = $(e).attr("saleNO");
            var param = {status: 0, email: email, pageSize: 10, currPage: 1, desc: saleNO};
            ajax_post("/sales/getsl",JSON.stringify(param),"application/json",
                function (data) {
                    if(data.code){
                        window.location.href = "/personal/login.html";
                    }
                    var result = data.datas;
                    if (result.length > 0) {
                        var order = result[0];
                        var id = order.id;
                        var coupons = order.couponsAmount;
                        var sumPrice = parseFloat(order.bbcPostage - (coupons ? coupons : 0)).toFixed(2);
                        var productName = encodeURIComponent("运费支付");
                        var p = [{"name": productName}];
                        var shengpayUrl = "";
                        var paidPrama = {
                            bbcPostage: sumPrice,
                            mainId: id,
                            salesOrderNo: saleNO,
                            distributor: distributorType,
                            isNotified:order.isNotified
                        };
                        var alipayUrl = "../../payment/alipay.html?purno=" + id + "&tradeNo=" + saleNO + "&productName=" + encodeURIComponent(productName) + "&sumPrice=" + sumPrice + "&postflag=true";
                        // var wxpayUrl = "../../payment/wechat.html?one=" + id + "&two=" + saleNO + "&three=" + encodeURIComponent(productName) + "&four=" + sumPrice + "&postflag=true";
                        var weinParam = {
                            id:id,
                            orderNo: saleNO,
                            orderDes: '运费支付',
                            totalPrice: sumPrice,
                            postflag:true
                        }
                        var callParam = {
                            func:'goSeeOrderDetail',
                            isFront:true,
                            saleOrderId:id
                        }
                        var wxpayUrl = "javascript:loadweixinPay("+JSON.stringify(weinParam)+","+JSON.stringify(callParam)+");";
                        var yjpayUrl = "../../payment/yijipay.html?one=" + id + "&two=" + saleNO + "&three=" + sumPrice + "&four=" + encodeURIComponent(encodeURIComponent("运费支付")) + "&five=ONLINEBANK&six=PC&postflag=true";
                        //yjfWxUrl = "../../payment/yjfWx.html?one=" + id + "&two=" + saleNO + "&three=" + sumPrice + "&four=" + encodeURIComponent(JSON.stringify(p)) + "&service=commonWchatTradeRedirect&postflag=true";
                        yjfWxUrl = "../../payment/yijipay.html?one=" + id + "&two=" + saleNO + "&three=" + sumPrice + "&four=" + encodeURIComponent(encodeURIComponent("运费支付")) + "&five=THIRDSCANPAY&six=PC&postflag=true";
                        paymentType(tradeNo, alipayUrl, wxpayUrl, yjpayUrl, undefined, shengpayUrl, paidPrama);
                    } else {
                        layer.msg("该客户订单数据不存在！", {icon: 5, time: 2000});
                    }
                },
                function (XMLHttpRequest, textStatus) {
                    layer.msg("获取客户订单数据失败！", {icon: 5, time: 2000});
                }
            )
        } else {
            var tradeNo = $(e).attr("purno");
            var sid = $(e).attr("sid");
            var balanceUrl = "../../product/paid-sure.html?purno=" + tradeNo + "";
            var param = {email: email, pageSize: "10",pageCount: "0", seachFlag: tradeNo};
            ajax_post("/purchase/viewpurchase",JSON.stringify(param),"application/json",function (data) {
                if (data.returnMess.errorCode == "0") {
                    var order = data.orders[0];
                    var id = order.id;
                    var bbcPostage = order.bbcPostage;
                    var couponsAmount = order.couponsAmount;
                    var sumPrice = parseFloat(order.purchaseDiscountAmount + (bbcPostage ? bbcPostage : 0)).toFixed(2);
                    sumPrice = parseFloat(sumPrice - (couponsAmount ? couponsAmount : 0)).toFixed(2);
                    var productName = encodeURIComponent(order.details[0].productName);
                    var p = [{"name": productName}];
                    var shengpayUrl = "";
                    var alipayUrl = "../../payment/alipay.html?purno=" + id + "&tradeNo=" + tradeNo + "&productName=" + encodeURIComponent(productName) + "&sumPrice=" + sumPrice;
                    var weinParam = {
                        id:id,
                        orderNo: tradeNo,
                        orderDes: order.details[0].productName,
                        totalPrice: sumPrice
                    }
                    var callParam = {
                        func:'goSeeOrderDetail',
                        isFront:true,
                        saleOrderId:sid
                    }
                    var wxpayUrl = "javascript:loadweixinPay("+JSON.stringify(weinParam)+","+JSON.stringify(callParam)+");";
                    // var wxpayUrl = "../../payment/wechat.html?one=" + id + "&two=" + tradeNo + "&three=" + encodeURIComponent(productName) + "&four=" + sumPrice;
                    var yjpayUrl = "../../payment/yijipay.html?one=" + id + "&two=" + tradeNo + "&three=" + sumPrice + "&four=" + encodeURIComponent(encodeURIComponent("销售单支付")) + "&five=ONLINEBANK&six=PC";
                    //yjfWxUrl = "../../payment/yjfWx.html?one=" + id + "&two=" + tradeNo + "&three=" + sumPrice + "&four=" + encodeURIComponent(JSON.stringify(p)) + "&service=commonWchatTradeRedirect";
                    yjfWxUrl = "../../payment/yijipay.html?one=" + id + "&two=" + tradeNo + "&three=" + sumPrice + "&four=" + encodeURIComponent(encodeURIComponent("销售单支付")) + "&five=THIRDSCANPAY&six=PC";
                    paymentType(tradeNo, alipayUrl, wxpayUrl, yjpayUrl, balanceUrl, shengpayUrl);
                }
            });
        }
    });
}

//选择支付方式
function paymentType(tradeNo, alipayUrl, wxpayUrl, yjpayUrl, balanceUrl, shengpayUrl, paidPrama) {
     var payHtml = '<option value="">请选择支付方式</option>' ;
     ajax_get("/member/method?purpose=3","","",function(data){
        if(data.suc){
            $.each(data.list,function(i,item){
                payHtml += '<option value="'+item.key+'">'+item.name+'</option>';
            });      
        }else{
            payHtml += '<option value="balance">余额支付</option>';
        }
    });

    layer.open({
        type: 1,
        title: '支付方式',
        content: '<div id="select_pay_method_dialog" style="padding: 30px 60px;">' +
        '<select style="font-size: 16px; padding: 6px;">' + payHtml + '</select>' +
        '</div>',
        move: false,
        btn: ['确定', '取消'],
        yes: function () {
            var res = true;
            var resMsg = "";
            //change by zbc 支付前先锁库
            if (!paidPrama) {
                orderLock(tradeNo,function(red){
                    res = red;
                });
            }
            if (!res) {
                return;
            }
            var mode = $("#select_pay_method_dialog select").val();
            if (mode == "zhifubao") {
                layer.closeAll();
                window.open(alipayUrl, "_blank");
                query_payment_result_url_one = "/payment/qryAlipayResult";
                confirm_pay_result_dialog(tradeNo, alipayUrl, wxpayUrl, yjpayUrl, balanceUrl, shengpayUrl, paidPrama);
            } else if (mode == "weixin") {
                layer.closeAll();
                window.location.href = wxpayUrl;
                query_payment_result_url_one = "/payment/wechat/getpayresult";
            } else if (mode == "easy") {
                layer.closeAll();
                window.open(yjpayUrl, "_blank");
                query_payment_result_url_one = "/payment/yijipay/getpayresult";
                confirm_pay_result_dialog(tradeNo, alipayUrl, wxpayUrl, yjpayUrl, balanceUrl, shengpayUrl, paidPrama);
            } else if (mode == "balance") {
                if (paidPrama) {
                    payBbcPostage(null, paidPrama);// 支付运费
                } else {
                    window.open(balanceUrl, "_blank");
                    confirm_pay_result_dialog(tradeNo, alipayUrl, wxpayUrl, yjpayUrl, balanceUrl, shengpayUrl, paidPrama);
                }
                layer.closeAll();
            } else if (mode == "easy-wx") {
                layer.closeAll();
                window.open(yjfWxUrl, "_blank");
                query_payment_result_url_one = "/payment/yijipay/getpayresult";
                confirm_pay_result_dialog(tradeNo,alipayUrl, wxpayUrl, yjpayUrl,balanceUrl,shengpayUrl);
             } else {
                query_payment_result_url_one = "";
                layer.msg("请选择支付方式", {icon: 0, time: 1000});
            }
        },
        cancel: function (index) {
            layer.close(index);
            if (paidPrama) {
                $(".close_detail").click();
            }
            $("#statusli").find("li[data-status='0']").click();
        }
    });
}

//确认支付结果弹出框
function confirm_pay_result_dialog(tradeNo, alipayUrl, wxpayUrl, yjpayUrl, balanceUrl, shengpayUrl, paidPrama) {
    layer.open({
        type: 1,
        title: '确认支付结果',
        content: '',
        move: false,
        btn: ['支付成功', '选择其他支付方式'],
        area: ["400", "140"],
        btn1: function () {
            layer.msg('订单校验中，请稍后查询。', {icon: 6, time: 3000});
            layer.closeAll();
            saleOrderList();
        },
        btn2: function () {
            layer.closeAll();
            paymentType(tradeNo, alipayUrl, wxpayUrl, yjpayUrl, balanceUrl, shengpayUrl, paidPrama);
        }
    });
}

//load 微信支付弹出窗
function loadweixinPay(param,callback){
    var winxinHtml = $('#winxin_content');
    winxinHtml.load("../payment/wechat.html", function (response, status, xhr) {
        require(["wechat"], function (wechat) {
            var url = '../personal/personal.html';
            $('.modal').fadeIn(300);
            init_win_payment(param,url,layer,callback);
        });
    });
}

//优惠金额
var couponsCost = 0;
var coupons = undefined;
// 验证优惠码
function validationCode(code) {
    var flag = false;
    var total = 0;
    if (!code) {
        $(".des-promotion span.red:eq(0)").text('0');
        $(".des-promotion span.red:eq(1)").text('0');
        var postfrei = $("#freight").text();
        $.each(tempProCollection, function (i, item) {
            if (parseInt(item.stock) < parseInt(item.qty)) {
                total += item.price * (item.qty - item.stock);
            }
        });
        $(".promotion-price .red:eq(1)").text(parseFloat(total).toFixed(2));
        $(".promotion-price .red:eq(2)").text('0');
        $(".promotion-price .red:eq(3)").text(postfrei);
        var cost = parseFloat(total + parseFloat(postfrei ? postfrei : 0)).toFixed(2);
        $(".promotion-price .red:eq(0)").text(parseFloat(cost).toFixed(2));
        layer.msg('请填写正确的优惠码。', {icon: 6, time: 2000});
        return flag;
    }
    var postfrei = $("#freight").text();
    $.each(tempProCollection, function (i, item) {
        if (parseInt(item.stock) < parseInt(item.qty)) {
            total += item.price * (item.qty - item.stock);
        }
    });
    var cost = parseFloat(total + parseFloat(postfrei ? postfrei : 0)).toFixed(2);
    $.ajax({
        url: "/member/getCouponsInfo?couponsNo=" + code + "&orderAmount=" + cost,
        type: "get",
        dataType: "json",
        contentType: "application/json",
        async: false,
        success: function (data) {
            if (data.suc) {
                coupons = data.active;
                couponsCost = coupons.couponsCost;
                $(".des-promotion").show();
                flag = true;
            } else {
                layer.msg(data.info, {icon: 6, time: 2000});
                couponsCost = 0.00;
                flag = false;
            }
            countPrice();
        }
    });
    return flag;
}

//优惠码定位
$.fn.submitBtn = function () {
    var closed = false;
    var box = $('.promotion-code-box');
    var pcd = $(".promotion-code");
    $('.promotion-close').click(function () {
        pcd.css({
            'position': 'relative',
            'width': box.width(),
            'left': 0
        });
        closed = true;
    });
    if (box.offset().top > ($(window).height() + pcd.height())) {
        pcd.css({
            'position': 'fixed',
            'width': box.width(),
            'left': box.offset().left
        })
    }
    var position = function () {
        $(window).scroll(function () {
            if ($('#edit_detail').css('display') == 'block' && closed == false) {
                $('.promotion-code-box').fadeIn(200);
                var posTopBox = box.offset().top;
                if ($(this).scrollTop() >= posTopBox - $(window).height() + pcd.height()) {
                    pcd.css({
                        'position': 'relative',
                        'width': box.width(),
                        'left': 0
                    });
                } else {
                    if (pcd.css('position') !== 'fixed') {
                        pcd.css({
                            'position': 'fixed',
                            'width': box.width(),
                            'left': box.offset().left
                        })
                    }
                }
            }
        });
    };
    return $(this).each(function () {
        position($(this));
    });
};

//收货人信息带入买家信息
function addBuyerMsg(node) {
    if ($(node).prop("checked")) {
        //收货人姓名验证
        var receiver = $("#receiver").val();
        if (!receiver) {
            layer.msg('收货人不能为空', {icon: 2, time: 2000});
            $(node).removeAttr("checked");
            return;
        }
        //收货人手机验证
        var tel = $("#tel").val();
        if (!checkTel(tel)) {
            layer.msg('收货人手机号码有格式错误，请输入有效手机号码', {icon: 2, time: 2000});
            $(node).removeAttr("checked");
            return;
        }
        //收货人身份证验证
        var idcard = $("#idcard").val();
        if (productType && !checkIDCard(idcard)) {
            layer.msg('请输入有效的收货人身份证', {icon: 2, time: 2000});
            $(node).removeAttr("checked");
            return;
        }
        $("#orderer").val(receiver);
        $("#ordererIDCard").val(idcard);
        $("#ordererTel").val(tel);
    } else {
        $("#orderer").val("");
        $("#ordererIDCard").val("");
        $("#ordererTel").val("");
    }
}

//店铺添加操作
function incrementShop(){
    var id = 0;
    var shoptype =  $("#site-type").val();
    var shopname = $(".site-name").val();
    ajax_post_sync("/member/addstore", {name:shopname,type:shoptype}, undefined,
        function(response) {
            if (response.suc) {
                //查询出此店铺id
                ajax_post_sync("/member/getstore", {shopName:shopname,type:shoptype}, undefined,
                    function(response) {
                        if (response.suc) {
                            id = response.page.list[0].id;
                        }
                    },
                    function(xhr, status) {
                        layer.msg('查询店铺出错，请稍候重试', {icon : 2, time : 2000});
                    }
                );
            } else if (response.code == "2") {
                window.location.href = "login.html";
            } else {
                layer.msg(response.msg, {icon : 2, time : 2000});
            }
        },
        function(xhr, status) {
            layer.msg('添加店铺出错，请稍候重试', {icon : 2, time : 2000});
        }
    );
    return id;
}

//物流导出功能
function logisticsExport(){
    layer.open({
        type: 1,
        title: "物流导出",
        content: $("#add-logisticsExport-box"),
        area: ['500px', '300px'],
        btn: ['确定','取消'],
        skin: 'layui-layer-demo',
        shadeClose: true,
        yes: function(i, currdom) {
            isnulogin(function (email){
                var currPage = $(".laypage_curr").html() == undefined ? 1 : $(".laypage_curr").html();
                var pageSize = $("#pageSizeTemp").val();
                var status = $(".sales-current").data("status");
                var param =[];
                param.push("email=" + email);
                param.push("status=" + status);
                var select = $("#add-logisticsExport-box select").find("option:selected").val();
                if (select == 1) {
                    param.push("currPage=" + currPage);
                    param.push("pageSize=" + pageSize);
                }
                var fields = $("input[name=field]:checked");
                if(!fields || fields.length <= 0) {
                    layer.msg('请选择需要导出的字段。', {icon : 1, time : 2000});
                    return;
                }
                $.each(fields, function (i, item) {
                    param.push("header="+$(item).val());
                });
                if (param.length > 0) {
                    window.location.href = "/sales/exportLogisticsInfo?" + param.join("&");
                }
            });
        }
    });
}

//全选微仓中的商品
function init_microware(){
    ajax_get("/inventory/warehousing/queryMicroWarehouse", undefined, "",
        function (data) {
            if(data.code){
                layer.msg(data.msg,{icon:5,time:2000},function(index){
                    layer.close(index);
                    window.location.href = "/personal/login.html";
                });
            }
            $(".select-warehouse-product").empty("label");
            var wareHtml = ' 我的微仓:&nbsp;&nbsp;';
            $.each(data,function(i,item){
                wareHtml += '<label><input type="radio" onclick="select_ware(this)" data-val=0 value="'+item.warehouseId+'" name="select_ware">全选'+item.warehouseName+'商品</label>';
            });
            $(".select-warehouse-product").append(wareHtml);
        },
        function(e){
        }
    );
}

// 选择出库
function select_ware(obj){
    var node = $(obj);
    var radioCheck= node.data("val"); 
    if(radioCheck){  
        node.prop("checked",false);  
        node.data("val",0);
    }else{
        tempProCollection = {};
        var list = node.parent().siblings().find("input[name=select_ware]");
        $.each(list,function(i,item){
           $(item).data("val",0);
        }) 
        node.prop("checked",true); 
        node.data("val",1);
        var index = layer.load(1, {shade: 0.9,time: 10*1000});
        addCloudPro(null,null,node.val(),index);
    }
}

function addCloudPro(skulist,skuQty,wareId,index){
     ajax_get("/member/infor?" + Math.random(), "", "application/json",
        function (response) {
            var sparam = {
                data:{
                    email : response.email,
                    warehouseId:wareId,
                    model:response.distributionMode
                }
            }
            ajax_post("/product/mirc-inventory",JSON.stringify(sparam),"application/json",function (data) {
                var reData = data.data;
                var result = reData.result;
                if(result && result.length <= 0) {
                    deleteCount();
                } else {
                    var disPrice,pro,img_url;
                    $.each(result,function(i,item){
                        disPrice = item.disPrice;
                        if(item.isSpecial){
                            disPrice = item.specialSale;
                        }
                        if(disPrice) {
                            disPrice = parseFloat(disPrice).toFixed(2);
                            img_url= item.imageUrl?item.imageUrl:"../../img/IW71-4-1a3c.jpg";
                            pro = {
                                sku: item.csku,
                                imgurl: urlReplace(img_url, item.csku,"",80,80,100),
                                title: item.ctitle,
                                price: disPrice,
                                marketPrice: item.localPrice,
                                qty: item.microStock,
                                wareName: item.warehouseName,
                                wareId: item.warehouseId,
                                disTotalCost: item.disTotalCost,
                                finalSellingPrice: undefined,
                                stock: item.microStock,
                                listFlag: false,// 发货列表展示标示  在列表中true，不在false
                                maxqty: item.stock + item.microStock,
                                batchNum: item.batchNumber?item.batchNumber:1
                            }
                            tempProCollection[pro.sku] = pro;
                        }
                    });
                }
                showdiv(wareId);
                addproductShow();
                layer.close(index);
            })
        },function(e){
        }
    );    
}

/**
 * 处理验证码
 * Created by huchuyin 2016-10-7 
 */
function initPayCaptcha() {
	ajax_get("/member/getDisAccountInfo?"+(new Date()).getTime(), {}, undefined,
		function(data) {
			if (data.suc) {
				if (data.msg && data.msg.inputErrorNumTimes >= 3) {
					$("#wb_pay_img").click();
					$("#wb_pay_img").parents("div").show();
				} else {
					$("#wb_pay_img").parent().parent().hide();
				}
            } else if (data.code == 2) {
                window.location.href = "login.html";
            } 
		},
		function(xhr, status) {
			layer.msg("系统异常", {icon : 2, time : 3000});
		}
	);
}

/*退款/售后 start*/
function applicationDrawbackBack1(){
    $("#applyApplicationDrawback").hide();
    $("#sale_send").hide();
    $("#after-div").show();
    xsshSaleOrderList(null, false);
}

function applicationDrawbackBack2(){
    $("#applicationDrawback").hide();
    $("#sale_send").hide();

    $("#sstatus").val(10);
    saleOrderList(null, false);
    $(".sales-current").removeClass();
    $($("#statusli li")[5]).addClass("sales-current");
}

var id=0;
function PreviewImage(imgFile){
    var pattern=/(\.*.jpg$)|(\.*.png$)|(\.*.jpeg$)|(\.*.gif$)|(\.*.bmp$)/;
    if(!pattern.test(imgFile.value)){
        alert("系统仅支持jpg/png/jpeg/gif/bmp格式的照片！");
        imgFile.focus();
    }else{
        var path,a;
        id+=1;
        a=$("img").length;
        a++;
        if(a<=2){
            $(".img-cont").append("<span><span id='"+id+"'><img src=''></span><em>-</em></span>");
        }
        if(a>=2){
            $("input").attr("disabled","disabled").css("cursor","not-allowed");
        }
        if(document.all){
            imgFile.select();
            path = document.selection.createRange().text;
            document.getElementById(id).innerHTML="";
            document.getElementById(id).style.filter="progid:DXImageTransfrom.Microsoft.AlphaImageLoader(enabled='true',sizingMethod='scale',src=\""+path+"\")";
        }else{
            path = URL.createObjectURL(imgFile.files[0]);
            document.getElementById(id).innerHTML="<img src='"+path+"' width='80' height='80'>";
        }
    }
}
$(".img-cont").off("mouseenter","span").on("mouseenter","span",function(){
    var that=this;
    var dom=$(that).children("em");
    dom.off("click");
    dom.on("click",function(){
        dom.parent().remove();
        $("input").removeAttr("disabled","disabled").css("cursor","default");
    });
});
$("#file_upload").change(function() {
    var $file = $(this);
    var fileObj = $file[0];
    var windowURL = window.URL || window.webkitURL;
    var dataURL;
    var $img = $("#imgBox").prepend('<img class="img-cont" id="preview">');
    if(fileObj && fileObj.files && fileObj.files[0]){
        dataURL = windowURL.createObjectURL(fileObj.files[0]);
        $img.children(".img-cont").attr('src',dataURL);
    }else{
        dataURL = $file.val();
        var imgObj = document.getElementById("preview");
        imgObj.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)";
        imgObj.filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = dataURL;

    }
});

/*退款/售后 end*/



//售后单列表
function xsshSaleOrderList(obj, flag, lay) {
    var currPage = 1;
    var pageSize = $("#pageSizeTemp").val();
    if (flag) {
        currPage = obj.curr;
    }

    //清空销售单列表
    $('#sale_send').find("#after-div").empty();

    //加載售后单基本信息
    var params = {"pageSize": pageSize, "currPage": currPage,"shOrderNo": $("#desc").val()};
    ajax_post("/sales/getXssh",JSON.stringify(params),
        "application/json",function (data) {
            if(data.code){
                layer.msg(data.msg,{icon:5,time:2000},function(index){
                    layer.close(index);
                    window.location.href = "/personal/login.html";
                });
            }

            var ds = data.datas;
            if (ds.length > 0) {
                for (var i = 0; i < ds.length; i++) {
                    var date = new Date();
                    var aDetail_html = "<div class='three-2 light-gray purchase-list' id='" + ds[i].id + "'>" +
                        "<ul>" +
                        "<li>订单编号:<span name='orderno'>"+ds[i].shOrderNo+"</span></li>" +
                        //"<li>平台订单编号:<span>"+deal_with_illegal_value(ds[i].xsOrderNo)+"</span></li>" +
                        "<li>申请时间:<span>"+ ds[i].createTime +"</span></li>" +
                        //"<li>收货人:<span>"+ds[i].disName+"</span></li>" +
                        "</ul>";

                    aDetail_html += "<div class='purchase-table' style='height: 140px;'>";
                    aDetail_html += "<table>"
                        + "<tbody>"
                        + "<tr class='four-2-tr1 bg-gray word-black'>"
                        + "<td class='tr1-td1'>商品名称</td>"
                        + "<td class='tr1-td2'>商品编号</td>"
                        + "<td class='tr1-td3'>发货数量(个)</td>"
                        + "<td class='tr1-td5'>所属仓库</td>"
                        + "<td class='tr1-td6'>订单状态</td>"
                        + "<td class='tr1-td7'>订单操作</td>"
                        + "</tr>"
                        + "<tr class='three-2-tr2'>";
                    aDetail_html += "<td class='tr2-td1'>"
                    + "<div>"
                    + "<a href='/product/product-detail.html?sku=" + ds[i].sku + "&warehouseId=" + ds[i].warehouseId +"' target='_blank'>"
                    +  '<img class="bbclazy" src="'+  ds[i].productImg +'" data-original="'+ urlReplace(ds[i].productImg, ds[i].sku, null, 80, 80, 100) +'"' + ' style="display: inline-block;"></a>'
                    + "</div>"
                    + "<p style='width: 230px;'>"
                    +  "<a href='/product/product-detail.html?sku=IF109-2&amp;warehouseId=2024' class='pnamelk brk' target='_blank'>"+ ds[i].productName +"</a>"
                    + "</p>"
                    + "</td>";

                    aDetail_html += "<td class='tr2-td2'><span class='sku'>"+ ds[i].sku +"</span></td>";
                    aDetail_html += "<td> <p><span class='qty blk'>"+ ds[i].demandQty +"</span></p> </td>";
                    aDetail_html += "<td class='tr2-td5'> <span class='warehousename'>" + ds[i].warehouseName +"</span> </td>";
                    aDetail_html += "<td class='tr2-td6' rowspan='1'> <p>" + formatStatus(ds[i].status) + "</p>"
                    + "<p class='see_detail2' sid='"+ ds[i].id + "'>查看详情</p>"
                    +  "</td>";

                    if(ds[i].status < 5) {
                        aDetail_html += "<td class='tr2-td7' rowspan='1'>"
                            + "<a href='javascript:;' data-id='"+ ds[i].id + "' class='btn-hyacinthine-small btn-hyacinthine-small_cancle' style='margin-top: 5px'>取消申请</a>"
                            + "</td>";
                    }

                    aDetail_html += "</tr></tbody></table></div>";

                    $("#after-div").append(aDetail_html);

                    $("img.bbclazy").lazyload({
                        effect: 'fadeIn',
                        threshold: '100'
                    });
                }
            } else {
                addLoadGif({
                    togBox: "#after-div",
                    togPage: '#pagination_sh_sales',
                    hint: '<div class="nothing"><div>抱歉，暂无相关订单</div></div>'
                }, true);
            }
            //初始化分页
            init_pagination_xssh_sales(data.totalPage, data.currPage);
            $('#pagination_xssh_sales').show();
        })

    //loading
    return creatLoading(true, theDiv);
}

function formatStatus(cellValue) {
    if(cellValue == 1) {
        return '待审核';
    }

    if(cellValue == 2) {
        return '待审核';
    }

    if(cellValue == 3) {
        return '待寄回商品';
    }

    if(cellValue == 4) {
        return '待平台收货';
    }

    if(cellValue == 5) {
        return '退款完成';
    }

    if(cellValue == 6) {
        return '售后关闭';
    }
}

//初始化分页栏
function init_pagination_xssh_sales(total, currPage) {
    scrollPosTop();
    if ($("#pagination_xssh_sales")[0] != undefined) {
        $("#pagination_xssh_sales").empty();
        laypage({cont: 'pagination_xssh_sales', pages: total, curr: currPage, groups: 5, skin: 'yahei',
            first: '首页', last: '尾页', prev: '上一页', next: '下一页', skip: true,
            jump: function (obj, first) {
                if (!first) {
                    xsshSaleOrderList(obj, true, laypage);
                }
            }
        });
    }
}

function cancelRequest(){
    layer.open({
        type: 1,
        title: "取消售后申请",
        area: ["400px","auto"],
        content: "<div style='padding: 20px;'>请确定取消申请：</div>",
        btn: ["确定","取消"],
        yes: function (index) {
            ajax_post("/sales/cancleSaleOrderRefundsApply",JSON.stringify({"id": $('#applicationDrawback #SH_ORDER_ID').val()}),"application/json",function (data) {
                if(data.result) {
                    layer.msg("已取消申请！",{icon:1,time:2000},function () {
                        window.location.reload();
                        layer.close(index);
                    });
                }else{
                    layer.msg(data.msg,{icon:5,time:2000},function () {
                        layer.close(index);
                    });
                }
            });
        }
    });
}
function returnGoods(){
    layer.open({
        type: 1,
        title: "寄回商品",
        area: ["320px","auto"],
        content: $(".return-goods"),
        btn: ["确定","取消"],
        yes: function (index) {
            var company = $('.return-goods').find('input[name="company"]').val();
            var expressCode = $('.return-goods').find('input[name="expressCode"]').val();

            if(!company) {
                layer.msg('请填写公司信息！',{icon:5,time:2000},function () {

                });
                return;
            }

            if(!expressCode) {
                layer.msg('请填写快递单号！',{icon:5,time:2000},function () {

                });
                return;
            }

            ajax_post("/sales/saleOrderRefundsApplyLogistics",JSON.stringify({"id":$('#applicationDrawback #SH_ORDER_ID').val(),"company":company,"expressCode":expressCode}),"application/json",function (data) {
                if(data.result) {
                    layer.msg("提交物流信息成功！！",{icon:1,time:2000},function () {
                        $("#applicationDrawback").fadeOut(200);
                        $("#sale_send").fadeIn(200);
                        $("#sstatus").val(10);
                        saleOrderList(null, false);
                        $(".sales-current").removeClass();
                        $($("#statusli li")[5]).addClass("sales-current");
                        layer.close(index);
                    });
                }else{
                    layer.msg(data.msg,{icon:5,time:2000},function () {
                        layer.close(index);
                    });
                }
            });
        }
    })
}

$(".box-right-four").on("click", ".btn-hyacinthine-small_cancle", function () {
    var id = $(this).data('id');
    layer.open({
        type: 1,
        title: "取消售后申请",
        area: ["400px","auto"],
        content: "<div style='padding: 20px;'>请确定取消申请：</div>",
        btn: ["确定","取消"],
        yes: function (index) {
            ajax_post("/sales/cancleSaleOrderRefundsApply",JSON.stringify({"id": id}),"application/json",function (data) {
                if(data.result) {
                    layer.msg("已取消申请！",{icon:1,time:2000},function () {
                        $("#sstatus").val(10);
                        saleOrderList(null, false);
                        $(".sales-current").removeClass();
                        $($("#statusli li")[5]).addClass("sales-current");
                        layer.close(index);
                    });
                }else{
                    layer.msg(data.msg,{icon:5,time:2000},function () {
                        layer.close(index);
                    });
                }
            });
        }
    });
});

//查看售后单详情
$(".box-right-four").on("click", ".see_detail2", function () {
    goSeeShOrderDetail($(this).attr("sid"));
});

function goSeeShOrderDetail(sid) {
    // 跳到详情页
    $("#applicationDrawback").fadeIn(200);
    $("#sale_send").fadeOut(200);
    getShOrderDetail(sid)
}

function getShOrderDetail(sid){
    var status;
    var saleOrderNo;
    var sku;

    $('#applicationDrawback .purchase-list:eq(0)').empty();
    $('#applicationDrawback .purchase-list:eq(1)').empty();
    ajax_post_sync("/sales/getSalesOrderRefundsById", JSON.stringify({"id": sid}), "application/json",function(data){
        if(data.code){
            layer.msg(data.msg,{icon:5,time:2000},function(index){
                layer.close(index);
                window.location.href = "/personal/login.html";
            });
        }

        $('#applicationDrawback #SH_ORDER_ID').val(data.id);
        $('#SH_ORDER_NO_SPAN').html(data.shOrderNo);
        $('#SH_ORDRE_QTY_SAPN').find("em:eq(0)").html(data.demandQty);
        $('#SH_ORDER_MONEY_SPAN').find("em:eq(0)").html(data.demandAmount);
        $('#SH_ORDER_QADESC_SPAN').html(data.qaDesc);


        saleOrderNo = data.saleOrderNo;
        sku = data.sku;

        $('#applicationDrawback .return-flow-con').empty();

        switch (data.status) {
            case 1:
                $('#SH_ORDER_RESULT').html('待审核');
                $('#detail_btn_cancle_apply').show();
                $('#detail_btn_return_product').hide();

                var content1 = '<li>'
                    + '<span>' + deal_with_illegal_value(data.createTime) + '</span>'
                    + '<span>提交申请</span>'
                    + '</li>';
                $('#applicationDrawback .return-flow-con').append(content1);
            break;
            case 2:
                $('#SH_ORDER_RESULT').html('待审核');
                $('#detail_btn_return_product').hide();
                $('#detail_btn_cancle_apply').show();

                var content1 = '<li>'
                    + '<span>' + deal_with_illegal_value(data.createTime) + '</span>'
                    + '<span>提交申请</span>'
                    + '</li>';
                $('#applicationDrawback .return-flow-con').append(content1);

                if(data.financeConfirmTime) {
                    var content2 = '<li>'
                        + '<span>' + deal_with_illegal_value(data.financeConfirmTime) + '</span>'
                        + '<span>平台审核通过</span>'
                        + '</li>';
                    $('#applicationDrawback .return-flow-con').append(content2);
                }
                $('#applicationDrawback .return-flow-chat>img:eq(1)').removeClass("hide").siblings().addClass("hide");
                break;
            case 3:
                $('#SH_ORDER_RESULT').html('待寄回商品');
                var content1 = '<li>'
                    + '<span>' + deal_with_illegal_value(data.createTime) + '</span>'
                    + '<span>提交申请</span>'
                    + '</li>';
                $('#applicationDrawback .return-flow-con').append(content1);

                var content2 = '<li>'
                    + '<span>' + deal_with_illegal_value(data.financeConfirmTime) + '</span>'
                    + '<span>平台审核通过</span>'
                    + '</li>';
                $('#applicationDrawback .return-flow-con').append(content2);


                $('#applicationDrawback .return-flow-chat>img:eq(2)').removeClass("hide").siblings().addClass("hide");
                $('#detail_btn_cancle_apply').hide();
                $('#detail_btn_return_product').show();
                break;
            case 4:
                $('#SH_ORDER_RESULT').html('待平台收货');
                $('#applicationDrawback .return-flow-chat>img:eq(2)').removeClass("hide").siblings().addClass("hide");

                var content1 = '<li>'
                    + '<span>' + deal_with_illegal_value(data.createTime) + '</span>'
                    + '<span>提交申请</span>'
                    + '</li>';
                $('#applicationDrawback .return-flow-con').append(content1);

                var content2 = '<li>'
                    + '<span>' + deal_with_illegal_value(data.financeConfirmTime) + '</span>'
                    + '<span>平台审核通过</span>'
                    + '</li>';
                $('#applicationDrawback .return-flow-con').append(content2);

                var content3 = '<li>'
                    + '<span>' + deal_with_illegal_value(data.sendProductTime) + '</span>'
                    + '<span>寄回商品</span>'
                    + '</li>';
                $('#applicationDrawback .return-flow-con').append(content3);

                $('#applicationDrawback .return-flow-chat>img:eq(2)').removeClass("hide").siblings().addClass("hide");
                $('#detail_btn_return_product').hide();
                $('#detail_btn_cancle_apply').show();
                break;
            case 5:
                $('#SH_ORDER_RESULT').html('售后已完成');

                var content1 = '<li>'
                    + '<span>' + deal_with_illegal_value(data.createTime) + '</span>'
                    + '<span>提交申请</span>'
                    + '</li>';
                $('#applicationDrawback .return-flow-con').append(content1);

                var content2 = '<li>'
                    + '<span>' + deal_with_illegal_value(data.financeConfirmTime) + '</span>'
                    + '<span>平台审核通过</span>'
                    + '</li>';
                $('#applicationDrawback .return-flow-con').append(content2);

                var content3 = '<li>'
                    + '<span>' + deal_with_illegal_value(data.sendProductTime) + '</span>'
                    + '<span>寄回商品</span>'
                    + '</li>';
                $('#applicationDrawback .return-flow-con').append(content3);

                var content4 = '<li>'
                    + '<span>' + deal_with_illegal_value(data.receivedProductTime) + '</span>'
                    + '<span>平台收货无误，退款至用户余额</span>'
                    + '</li>';
                $('#applicationDrawback .return-flow-con').append(content4);

                $('#applicationDrawback .return-flow-chat>img:eq(3)').removeClass("hide").siblings().addClass("hide");
                $('#detail_btn_cancle_apply').hide();
                $('#detail_btn_return_product').hide();
                break;
            case 6:
                $('#SH_ORDER_RESULT').html('申请关闭');

                var content1 = '<li>'
                                + '<span>' + deal_with_illegal_value(data.createTime) + '</span>'
                                + '<span>提交申请</span>'
                                + '</li>';
                $('#applicationDrawback .return-flow-con').append(content1);

                var content2 = '<li>'
                    + '<span>' + deal_with_illegal_value(data.updateTime) + '</span>'
                    + '<span>售后关闭</span>'
                    + '</li>';
                $('#applicationDrawback .return-flow-con').append(content2);

                $('#detail_btn_cancle_apply').hide();
                $('#detail_btn_return_product').hide();
                $('#applicationDrawback .return-flow-chat>img:eq(1)').removeClass("hide").siblings().addClass("hide");
                break;
        }

        //附件信息
        $('#imgBox').empty();
        ajax_post("/sales/getShAttachmentListByShOrderId", JSON.stringify({"id":sid}),"application/json",function (data) {
            if(data.result){
                if(data.data.length > 0) {
                    var ds = data.data;
                    for(var i =0; i< ds.length;i++) {
                        var spanContent =
                            '<img src="/sales/getShAttachmentImgById?id='+ ds[i].id + '" class="preview-img" alt="暂无图片" title="点击预览大图" onclick="previewImg(this)">';
                        $('#imgBox').append(spanContent);
                    }
                }
            }
        });


        //商品信息
        var cateHtml = '<div class="purchase-table" style="height: 140px;">'
            + '<table>'
            + '<thead>'
            + '<tr class="four-2-tr1 bg-gray word-black">'
            + '<th class="tr1-td1">商品名称</th>'
            + '<th class="tr1-td2">商品编号</th>'
            + '<th class="tr1-td3">发货数量(个)</th>'
            + '<th class="tr1-td5">所属仓库</th>'
            + '</tr>'
            + '</thead>'
            + '<tbody>'
            + '<tr class="three-2-tr2">';
        cateHtml += '<td class="tr2-td1">'
            + '<div>'
            + '<a href="../../product/product-detail.html?sku=' + data.sku + '&warehouseId=' + data.warehouseId + '" target="_blank">'
            + '<img class="bbclazy" src="'+  data.productImg +'" data-original="'+ urlReplace(data.productImg, data.sku, null, 80, 80, 100) +'"' + '></a>'
            + '</div>'
            + '<p style="width: 230px;">'
            + '<a href="../../product/product-detail.html?sku=' + data.sku + '&warehouseId=' + data.warehouseId + '" class="pnamelk brk" target="_blank">' + data.productName+ '</a>'
            + '</p>'
            + '</td>';
        cateHtml += '<td class="tr2-td2"><span class="sku">'+ data.sku+ '</span></td>';
        cateHtml += '<td> <p><span class="qty blk">'+ data.demandQty+'</span></p> </td>';
        cateHtml += '<td class="tr2-td5"> <span class="warehousename">' + data.warehouseName+'</span> </td>';
        cateHtml += '</tr> </tbody> </table> </div>';
        $('#applicationDrawback .purchase-list:eq(0)').html(cateHtml);
        $("img.bbclazy").lazyload({
            effect: 'fadeIn',
            threshold: '100'
        });


        //采购信息
        ajax_post("/sales/getShOrderDetails", JSON.stringify({"id": $('#applicationDrawback #SH_ORDER_ID').val()}), "application/json",
            function (data) {
                if(data.data.length > 0) {
                    var ds = data.data;
                    for(var i = 0;i < ds.length;i++) {
                        //采购信息
                        var purchaseCateHtml = '<ul>'
                            + '<li>采购编号:<span>'+ ds[i].purchaseOrderNo+'</span></li>'
                            + '</ul>';

                        purchaseCateHtml += '<div class="purchase-table">'
                            + '<table>'
                            + '<thead>'
                            + '<tr class="four-2-tr1 bg-gray word-black">'
                            + '<th style="width: 30%">商品编号</th>'
                            + '<th style="width: 30%">采购单价（元）</th>'
                            + '<th style="width: 30%">均摊价（元）</th>'
                            + '<th style="width: 30%">数量（个）</th>'
                            + '</tr>'
                            + '</thead>'
                            + '<tbody>'
                            + '<tr class="three-2-tr2">';

                        purchaseCateHtml += '<td>'+ ds[i].sku + '</td>'
                            + '<td>'+ ds[i].purchasePrice + '</td>'
                            + '<td>'+ ds[i].capfee+'</td>'
                            + '<td>'+ ds[i].qty +'</td>';

                        purchaseCateHtml += '</tr> </tbody> </table> </div>';
                        $('#applicationDrawback .purchase-list:eq(1)').append(purchaseCateHtml);
                    }
                }
        });
    });
}
/*退款/售后 end*/


define("sales", ["jquery", "layer", "webuploader"], function ($, layer,WebUploader) {

    $(".box-right-four").on("click", ".toRefundsApply", function () {
        var $wrap = $('#uploader'),

        // 图片容器
            $queue = $( '<ul class="filelist"></ul>' )
                .appendTo( $wrap.find( '.queueList' ) ),

        // 状态栏，包括进度和控制按钮
            $statusBar = $wrap.find( '.statusBar' ),

        // 文件总体选择信息。
            $info = $statusBar.find( '.info' ),

        // 上传按钮
            $upload = $wrap.find( 'uploadBtn2' ),

        // 没选择文件之前的内容。
            $placeHolder = $wrap.find( '.placeholder2' ),

            $progress = $statusBar.find( '.progress' ).hide(),

        // 添加的文件数量
            fileCount = 0,

        // 添加的文件总大小
            fileSize = 0,

        // 优化retina, 在retina下这个值是2
            ratio = window.devicePixelRatio || 1,

        // 缩略图大小
            thumbnailWidth = 110 * ratio,
            thumbnailHeight = 110 * ratio,

        // 可能有pedding, ready, uploading, confirm, done.
            state = 'pedding',

        // 所有文件的进度信息，key为file id
            percentages = {},
        // 判断浏览器是否支持图片的base64
            isSupportBase64 = ( function() {
                var data = new Image();
                var support = true;
                data.onload = data.onerror = function() {
                    if( this.width != 1 || this.height != 1 ) {
                        support = false;
                    }
                }
                data.src = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==";
                return support;
            } )(),

        // 检测是否已经安装flash，检测flash的版本
            flashVersion = ( function() {
                var version;

                try {
                    version = navigator.plugins[ 'Shockwave Flash' ];
                    version = version.description;
                } catch ( ex ) {
                    try {
                        version = new ActiveXObject('ShockwaveFlash.ShockwaveFlash')
                            .GetVariable('$version');
                    } catch ( ex2 ) {
                        version = '0.0';
                    }
                }
                version = version.match( /\d+/g );
                return parseFloat( version[ 0 ] + '.' + version[ 1 ], 10 );
            } )(),

            supportTransition = (function(){
                var s = document.createElement('p').style,
                    r = 'transition' in s ||
                        'WebkitTransition' in s ||
                        'MozTransition' in s ||
                        'msTransition' in s ||
                        'OTransition' in s;
                s = null;
                return r;
            })(),

        // WebUploader实例
            uploader;

        if ( !WebUploader.Uploader.support('flash') && WebUploader.browser.ie ) {

            // flash 安装了但是版本过低。
            if (flashVersion) {
                (function(container) {
                    window['expressinstallcallback'] = function( state ) {
                        switch(state) {
                            case 'Download.Cancelled':
                                alert('您取消了更新！')
                                break;

                            case 'Download.Failed':
                                alert('安装失败')
                                break;

                            default:
                                alert('安装已成功，请刷新！');
                                break;
                        }
                        delete window['expressinstallcallback'];
                    };

                    var swf = './expressInstall.swf';
                    // insert flash object
                    var html = '<object type="application/' +
                        'x-shockwave-flash" data="' +  swf + '" ';

                    if (WebUploader.browser.ie) {
                        html += 'classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" ';
                    }

                    html += 'width="100%" height="100%" style="outline:0">'  +
                        '<param name="movie" value="' + swf + '" />' +
                        '<param name="wmode" value="transparent" />' +
                        '<param name="allowscriptaccess" value="always" />' +
                        '</object>';

                    container.html(html);

                })($wrap);

                // 压根就没有安转。
            } else {
                $wrap.html('<a href="http://www.adobe.com/go/getflashplayer" target="_blank" border="0"><img alt="get flash player" src="http://www.adobe.com/macromedia/style_guide/images/160x41_Get_Flash_Player.jpg" /></a>');
            }

            return;
        } else if (!WebUploader.Uploader.support()) {
            alert( 'Web Uploader 不支持您的浏览器！');
            return;
        }

        // 实例化
        uploader = WebUploader.create({
            pick: {
                id: '#filePicker',
                label: '+'
            },
            formData: {
                uid: 123
            },
            dnd: '#dndArea',
            paste: '#uploader',
            swf: '../../dist/Uploader.swf',
            chunked: false,
            chunkSize: 512 * 1024,
            server: '/sales/saleOrderRefundsApply',
            // runtimeOrder: 'flash',

             accept: {
                 title: 'Images',
                 extensions: 'jpg,jpeg,bmp,png',
                 mimeTypes: 'image/*'
             },

            // 禁掉全局的拖拽功能。这样不会出现图片拖进页面的时候，把图片打开。
            disableGlobalDnd: true,
            fileNumLimit: 5,
            fileSingleSizeLimit: 2 * 1024 * 1024    // 2 M
        });

        // 拖拽时不接受 js, txt 文件。
        uploader.on( 'dndAccept', function( items ) {
            var denied = false,
                len = items.length,
                i = 0,
            // 修改js类型
                unAllowed = 'text/plain;application/javascript ';

            for ( ; i < len; i++ ) {
                // 如果在列表里面
                if ( ~unAllowed.indexOf( items[ i ].type ) ) {
                    denied = true;
                    break;
                }
            }

            return !denied;
        });
        uploader.on('dialogOpen', function() {
        });
        // 添加“添加文件”的按钮，
        uploader.addButton({
            id: '#filePicker2',
            label: '继续添加'
        });
        uploader.on('ready', function() {
            window.uploader = uploader;
        });
        // 当有文件添加进来时执行，负责view的创建
        function addFile( file ) {
            var $li = $( '<li id="' + file.id + '">' +
                    '<p class="title">' + file.name + '</p>' +
                    '<p class="imgWrap"></p>'+
                    '<p class="progress"><span></span></p>' +
                    '</li>' ),

                $btns = $('<div class="file-panel">' +
                    '<span class="cancel">删除</span>' +
                    '<span class="rotateRight">向右旋转</span>' +
                    '<span class="rotateLeft">向左旋转</span></div>').appendTo( $li ),
                $prgress = $li.find('p.progress span'),
                $wrap = $li.find( 'p.imgWrap' ),
                $info = $('<p class="error"></p>'),

                showError = function( code ) {
                    switch( code ) {
                        case 'exceed_size':
                            text = '文件大小超出';
                            break;

                        case 'interrupt':
                            text = '上传暂停';
                            break;

                        default:
                            text = '上传失败，请重试';
                            break;
                    }

                    $info.text( text ).appendTo( $li );
                };

            if ( file.getStatus() === 'invalid' ) {
                showError( file.statusText );
            } else {
                // @todo lazyload
                $wrap.text( '预览中' );
                uploader.makeThumb( file, function( error, src ) {
                    var img;

                    if ( error ) {
                        $wrap.text( '不能预览' );
                        return;
                    }

                    if( isSupportBase64 ) {
                        img = $('<img onclick="previewImg(this);" src="'+src+'">');
                        $wrap.empty().append( img );
                    } else {
                        $.ajax('../../server/preview.php', {
                            method: 'POST',
                            data: src,
                            dataType:'json'
                        }).done(function( response ) {
                            if (response.result) {
                                img = $('<img src="'+response.result+'">');
                                $wrap.empty().append( img );
                            } else {
                                $wrap.text("预览出错");
                            }
                        });
                    }
                }, thumbnailWidth, thumbnailHeight );

                percentages[ file.id ] = [ file.size, 0 ];
                file.rotation = 0;
            }

            file.on('statuschange', function( cur, prev ) {
                if ( prev === 'progress' ) {
                    $prgress.hide().width(0);
                } else if ( prev === 'queued' ) {
                    $li.off( 'mouseenter mouseleave' );
                    $btns.remove();
                }

                // 成功
                if ( cur === 'error' || cur === 'invalid' ) {
                    showError( file.statusText );
                    percentages[ file.id ][ 1 ] = 1;
                } else if ( cur === 'interrupt' ) {
                    showError( 'interrupt' );
                } else if ( cur === 'queued' ) {
                    $info.remove();
                    $prgress.css('display', 'block');
                    percentages[ file.id ][ 1 ] = 0;
                } else if ( cur === 'progress' ) {
                    $info.remove();
                    $prgress.css('display', 'block');
                } else if ( cur === 'complete' ) {
                    $prgress.hide().width(0);
                    $li.append( '<span class="success"></span>' );
                }

                $li.removeClass( 'state-' + prev ).addClass( 'state-' + cur );
            });

            $li.on( 'mouseenter', function() {
                $btns.stop().animate({height: 30});
            });

            $li.on( 'mouseleave', function() {
                $btns.stop().animate({height: 0});
            });

            $btns.on( 'click', 'span', function() {
                var index = $(this).index(),
                    deg;

                switch ( index ) {
                    case 0:
                        uploader.removeFile( file );
                        if(uploader.getFiles().length == 1) {
                            uploader.reset();
                        }
                        return;

                    case 1:
                        file.rotation += 90;
                        break;

                    case 2:
                        file.rotation -= 90;
                        break;
                }

                if ( supportTransition ) {
                    deg = 'rotate(' + file.rotation + 'deg)';
                    $wrap.css({
                        '-webkit-transform': deg,
                        '-mos-transform': deg,
                        '-o-transform': deg,
                        'transform': deg
                    });
                } else {
                    $wrap.css( 'filter', 'progid:DXImageTransform.Microsoft.BasicImage(rotation='+ (~~((file.rotation/90)%4 + 4)%4) +')');
                }
            });
            $li.appendTo( $queue );
        }

        // 负责view的销毁
        function removeFile( file ) {
            var $li = $('#'+file.id);

            delete percentages[ file.id ];
            updateTotalProgress();
            $li.off().find('.file-panel').off().end().remove();
        }

        function updateTotalProgress() {
            var loaded = 0,
                total = 0,
                spans = $progress.children(),
                percent;

            $.each( percentages, function( k, v ) {
                total += v[ 0 ];
                loaded += v[ 0 ] * v[ 1 ];
            } );

            percent = total ? loaded / total : 0;


            spans.eq( 0 ).text( Math.round( percent * 100 ) + '%' );
            spans.eq( 1 ).css( 'width', Math.round( percent * 100 ) + '%' );
            updateStatus();
        }

        function updateStatus() {
            var text = '', stats;

            if ( state === 'ready' ) {
                text = '选中' + fileCount + '张图片，共' +
                    WebUploader.formatSize( fileSize ) + '。';
            } else if ( state === 'confirm' ) {
                stats = uploader.getStats();
                if ( stats.uploadFailNum ) {
                    text = '已成功上传' + stats.successNum+ '张照片至XX相册，'+
                        stats.uploadFailNum + '张照片上传失败，<a class="retry" href="#">重新上传</a>失败图片或<a class="ignore" href="#">忽略</a>'
                }

            } else {
                stats = uploader.getStats();
                text = '共' + fileCount + '张（' +
                    WebUploader.formatSize( fileSize )  +
                    '），已上传' + stats.successNum + '张';

                if ( stats.uploadFailNum ) {
                    text += '，失败' + stats.uploadFailNum + '张';
                }
            }

            $info.html( text );
        }

        function setState( val ) {
            var file, stats;

            if ( val === state ) {
                return;
            }

            $upload.removeClass( 'state-' + state );
            $upload.addClass( 'state-' + val );
            state = val;

            switch ( state ) {
                case 'pedding':
                    $placeHolder.removeClass( 'element-invisible' );
                    $queue.hide();
                    $statusBar.addClass( 'element-invisible' );
                    uploader.refresh();
                    break;

                case 'ready':
                    $placeHolder.addClass( 'element-invisible' );
                    $( '#filePicker2' ).removeClass( 'element-invisible');
                    $queue.show();
                    $statusBar.removeClass('element-invisible');
                    uploader.refresh();
                    break;

                case 'uploading':
                    $( '#filePicker2' ).addClass( 'element-invisible' );
                    $progress.show();
                    $upload.text( '暂停上传' );
                    break;

                case 'paused':
                    $progress.show();
                    $upload.text( '继续上传' );
                    break;

                case 'confirm':
                    $progress.hide();
                    $( '#filePicker2' ).removeClass( 'element-invisible' );
                    $upload.text( '开始上传' );

                    stats = uploader.getStats();
                    if ( stats.successNum && !stats.uploadFailNum ) {
                        setState( 'finish' );
                        return;
                    }
                    break;
                case 'finish':
                    stats = uploader.getStats();
                    if ( stats.successNum ) {

                    } else {
                        // 没有成功的图片，重设
                        state = 'done';
                        location.reload();
                    }
                    break;
            }

            updateStatus();
        }

        uploader.onUploadProgress = function( file, percentage ) {
            var $li = $('#'+file.id),
                $percent = $li.find('.progress span');

            $percent.css( 'width', percentage * 100 + '%' );
            percentages[ file.id ][ 1 ] = percentage;
            updateTotalProgress();
        };

        uploader.onFileQueued = function( file ) {
            fileCount++;
            fileSize += file.size;

            if ( fileCount === 1 ) {
                $placeHolder.addClass( 'element-invisible' );
                $statusBar.show();
            }

            addFile( file );
            setState( 'ready' );
            updateTotalProgress();
        };

        uploader.onFileDequeued = function( file ) {
            fileCount--;
            fileSize -= file.size;

            if ( !fileCount ) {
                setState( 'pedding' );
            }

            removeFile( file );
            updateTotalProgress();

        };

        uploader.on( 'all', function( type ) {
            var stats;
            switch( type ) {
                case 'uploadFinished':
                    setState( 'confirm' );
                    break;

                case 'startUpload':
                    setState( 'uploading' );
                    break;

                case 'stopUpload':
                    setState( 'paused' );
                    break;

            }
        });

        //当某个文件上传到服务端响应后，会派送此事件来询问服务端响应是否有效
        uploader.on('uploadAccept', function (object,ret) {
            if (ret.success) {
                layer.msg("您的退货申请已经成功提交，请耐心等待审核！.", {icon : 6, time : 3000},function(){
                    // 跳到详情页
                    $("#applicationDrawback").fadeIn(200);
                    $("#applyApplicationDrawback").fadeOut(200);
                    $("#sale_send").fadeOut(200);
                    getShOrderDetail(ret.id)
                });
            } else {
                layer.msg(ret.msg, {icon : 2, time : 2000});
            }
        });

        uploader.onError = function( code ) {
            console.log(code);
            if(code == "F_DUPLICATE"){
                layer.msg("请不要重复选择文件！", {icon : 2, time : 2000});
            } else if(code == 'Q_EXCEED_NUM_LIMIT') {
                layer.msg("最多可上传5张图片！", {icon : 2, time : 2000});
            } else if(code == 'F_EXCEED_SIZE'){
                layer.msg("附件大小不能超过2M！", {icon : 2, time : 2000});
            } else if(code == 'Q_TYPE_DENIED') {
                layer.msg("附件只支持bmp,jpg,png,jpeg格式!", {icon : 2, time : 2000});
            }
        };

        $upload.on('click', function() {
            if ( $(this).hasClass( 'disabled' ) ) {
                return false;
            }

            if ( state === 'ready' ) {
                uploader.upload();
            } else if ( state === 'paused' ) {
                uploader.upload();
            } else if ( state === 'uploading' ) {
                uploader.stop();
            }
        });

        $info.on( 'click', '.retry', function() {
            uploader.retry();
        } );

        $info.on( 'click', '.ignore', function() {
            alert( 'todo' );
        } );

        $upload.addClass( 'state-' + state );
        updateTotalProgress();
    });
});

function refundsApply() {
    //表单验证
    var qty = $("#refunds-apply-form").find("input[name='qty']").val();
    var money = $("#refunds-apply-form").find("input[name='money']").val().trim();
    var desc = $("#refunds-apply-form").find("textarea[name='desc']").val().trim();
    var max = $("#refunds-apply-form").find("#MAX_QTY").html();
    var xsOrderNo = $("#hidden_orderNo").val();
    var orderId = $("#hidden_orderId").val();
    var warehouseId = $("#hidden_warehouseId").val();
    var warehouseName = $("#hidden_warehouseName").val();
    var productImg = $("#hidden_productImg").val();
    var productName = $("#hidden_productName").val();
    var sku = $("#hidden_sku").val();
    var detailOrderId = $('#hidden_detailOrderId').val();

    if (!qty) {
        layer.msg("请输入退货个数!", {icon: 5, time: 1000});
        return;
    }

    if (parseInt(qty) > parseInt(max)) {
        layer.msg("退货个数不能大于" + max + "个!", {icon: 5, time: 1000});
        return;
    }

    if (!money || money == '' || money.length == 0) {
        layer.msg("请输入退款金额!", {icon: 5, time: 1000});
        return;
    }


    var length = money.indexOf('.');
    if (length > 0) {
        if (((money.length - 1) - length) > 2) {
            layer.msg("金额小数点后只能保留两位!", {icon: 2, time: 1000});
            return;
        }
    }

    if (!desc) {
        layer.msg("请填写退款理由!", {icon: 2, time: 1000});
        return;
    }

    if (desc.length > 1000) {
        layer.msg("退款理由不能超过1000字!", {icon: 2, time: 1000});
        return;
    }

    $('#refunds-apply').attr('disabled', 'disabled');

    if (uploader.getFiles() == 0) {
        isnulogin(function (email) {
            $('#refunds-apply-form').ajaxSubmit({
                type: 'post',
                url: '/sales/saleOrderRefundsApply',
                data: {
                    qty: qty,
                    money: money,
                    desc: desc,
                    xsOrderNo: xsOrderNo,
                    email: email,
                    sku: sku,
                    orderId: orderId,
                    warehouseId: warehouseId,
                    warehouseName: warehouseName,
                    productImg: productImg,
                    productName: productName
                },
                success: function (ret) {
                    if (ret.success) {
                        layer.msg("您的退货申请已经成功提交，请耐心等待审核！.", {icon: 6, time: 3000}, function () {
                            // 跳到详情页
                            $("#applicationDrawback").fadeIn(200);
                            $("#applyApplicationDrawback").fadeOut(200);
                            $("#sale_send").fadeOut(200);
                            getShOrderDetail(ret.id)
                        });
                    } else {
                        layer.msg(ret.msg, {icon: 2, time: 2000});
                    }
                }
            });
        });
        return;
    }

    isnulogin(function (email) {
        uploader.options.formData.qty = qty;
        uploader.options.formData.money = money;
        uploader.options.formData.desc = desc;
        uploader.options.formData.xsOrderNo = xsOrderNo;
        uploader.options.formData.email = email;
        uploader.options.formData.sku = sku;
        uploader.options.formData.orderId = orderId;
        uploader.options.formData.warehouseId = warehouseId;
        uploader.options.formData.warehouseName = warehouseName;
        uploader.options.formData.productImg = productImg;
        uploader.options.formData.productName = productName;
        uploader.options.formData.detailOrderId = detailOrderId;

        uploader.upload();
    });
}

$(".box-right-four").on("click", ".toRefundsApply", function () {
    $("#refunds-apply").removeAttr('disabled');
    console.log($(this).data('id'));
    console.log($(this).data('detailid'));
    $("#applyApplicationDrawback").fadeIn(200);
    $("#sale_send").fadeOut(200);
    $('.filelist').empty();

    var sid = $(this).data('id');
    var did = $(this).data('detailid');
    var sku = $(this).data('sku');
    var qty = $(this).data('qty');
    var warehouseId = $(this).data('warehouseid');
    var warehouseName = $(this).data('warehousename');
    var productImg = $(this).data('productimg');
    var productName = $(this).data('productname');
    var xsOrderNo = $(this).data('orderno');


    $("#applyApplicationDrawback #hidden_orderId").val(sid);
    $("#applyApplicationDrawback #hidden_detailOrderId").val(did);
    $('#applyApplicationDrawback #MAX_QTY').html(qty);
    $('#applyApplicationDrawback .refund-area').val('');
    $('#hidden_sku').val(sku);
    $('#hidden_warehouseId').val(warehouseId);
    $('#hidden_warehouseName').val(warehouseName);
    $('#hidden_orderId').val(sid);
    $('#hidden_productImg').val(productImg);
    $('#hidden_productName').val(productName);
    $('#hidden_orderNo').val(xsOrderNo);

    $('#applyApplicationDrawback .purchase-list:eq(0)').empty();
    $('#applyApplicationDrawback .purchase-list:eq(1)').empty();
    //商品信息
    ajax_post_sync("/sales/getsdl", JSON.stringify({"orderId": sid}), "application/json",
        function (datad) {
            if (datad.length > 0) {

                for (var i = 0; i < datad.length; i++) {
                    if(datad[i].sku == sku) {
                        var url_img = "/product/api/getUrl?sku=" + datad[i].sku;
                        $.ajax({url: url_img, type: "get", async: false,
                            success: function (data) {
                                var img_p_url = data ? data : "../../img/IW71-4-1a3c.jpg";

                                var cateHtml = '<div class="purchase-table" style="height: 140px;">'
                                    + '<table>'
                                    + '<thead>'
                                    + '<tr class="four-2-tr1 bg-gray word-black">'
                                    + '<th class="tr1-td1">商品名称</th>'
                                    + '<th class="tr1-td2">商品编号</th>'
                                    + '<th class="tr1-td3">发货数量(个)</th>'
                                    + '<th class="tr1-td5">所属仓库</th>'
                                    + '</tr>'
                                    + '</thead>'
                                    + '<tbody>'
                                    + '<tr class="three-2-tr2">';
                                cateHtml += '<td class="tr2-td1">'
                                    + '<div>'
                                    + '<a href="../../product/product-detail.html?sku=' + datad[i].sku + '&warehouseId=' + datad[i].warehouseId + '" target="_blank">'
                                    +  '<img class="bbclazy" src="'+  datad[i].productImg +'" data-original="'+ urlReplace(datad[i].productImg, datad[i].sku, null, 80, 80, 100) +'"' + '/>'
                                    + '</a>'
                                    + '</div>'
                                    + '<p style="width: 230px;">'
                                    + '<a href="../../product/product-detail.html?sku=' + datad[i].sku + '&warehouseId=' + datad[i].warehouseId + '" class="pnamelk brk" target="_blank">' + datad[i].productName+ '</a>'
                                    + '</p>'
                                    + '</td>';
                                cateHtml += '<td class="tr2-td2"><span class="sku">'+ datad[i].sku+ '</span></td>';
                                cateHtml += '<td> <p><span class="qty blk">'+ datad[i].qty+'</span></p> </td>';
                                cateHtml += '<td class="tr2-td5"> <span class="warehousename">' + datad[i].warehouseName+'</span> </td>';
                                cateHtml += '</tr> </tbody> </table> </div>';
                                $('#applyApplicationDrawback .purchase-list:eq(0)').html(cateHtml);

                                $("img.bbclazy").lazyload({
                                    effect: 'fadeIn',
                                    threshold: '100'
                                });
                            }
                        });
                    }
                }
            }
        }
    );

    //采购信息
    ajax_post_sync("/sales/purchaseInfo", JSON.stringify({"orderId": sid}), "application/json",
        function (data) {
            if (data) {
                var historySaleDetailList = data.historySaleDetailList;
                if (historySaleDetailList.length > 0) {
                    for (var i = 0; i < historySaleDetailList.length; i++) {
                        if (sku == historySaleDetailList[i].sku) {
                            //采购信息
                            var purchaseCateHtml = '<ul>'
                                + '<li>采购编号:<span>' + historySaleDetailList[i].purchaseOrderNo + '</span></li>'
                                + '</ul>';

                            purchaseCateHtml += '<div class="purchase-table">'
                                + '<table>'
                                + '<thead>'
                                + '<tr class="four-2-tr1 bg-gray word-black">'
                                + '<th style="width: 30%">商品编号</th>'
                                + '<th style="width: 30%">采购单价（元）</th>'
                                + '<th style="width: 30%">均摊价（元）</th>'
                                + '<th style="width: 30%">数量（个）</th>'
                                + '</tr>'
                                + '</thead>'
                                + '<tbody>'
                                + '<tr class="three-2-tr2">';

                            purchaseCateHtml += '<td>' + historySaleDetailList[i].sku + '</td>'
                                + '<td>' + historySaleDetailList[i].purchasePrice + '</td>'
                                + '<td>' + historySaleDetailList[i].capFee + '</td>'
                                + '<td>' + historySaleDetailList[i].qty + '</td>';

                            purchaseCateHtml += '</tr> </tbody> </table> </div>';

                            $('#applyApplicationDrawback .purchase-list:eq(1)').append(purchaseCateHtml);
                        }
                    }
                }
            }
        }
    );
});


// 直接输入数量
$("#sh-saleOrder-button-qty").keyup(function(){
    var stock = $('#applyApplicationDrawback #MAX_QTY').html();
    var num = 1;
    var qty = $.trim($("#sh-saleOrder-button-qty").val());
    if(qty==''){
        layer.msg("数量不能为空！",{icon:2,time:2000});
        $("#sh-saleOrder-button-qty").val(num);
        return;
    }

    var pattern = /^[1-9]\d*$/;
    if(!pattern.test(qty)){
        layer.msg("数量只能为正整数！",{icon:2,time:2000});
        $("#sh-saleOrder-button-qty").val(qty.substring(0,qty.length-1)==''?num:qty.substring(0,qty.length-1));
    }

    if(parseInt(qty) < parseInt(num)) {
        layer.msg("数量不得低于1",{icon:2,time:2000});
        $("#cart-button-qty_new").val(num);
    }

    if(parseInt(qty)>parseInt(stock)){
        layer.msg("数量将不能超过："+stock ,{icon:2,time:2000});
        $("#sh-saleOrder-button-qty").val(stock);
    }
});

//数量加减
$("#shSaleOrder-button-qty-sub_new").click(function(){
    var num = 1;
    if(parseInt($("#sh-saleOrder-button-qty").val()) < parseInt(num)) {
        layer.msg("数量不得低于1",{icon:2,time:2000});
        return;
    }
    if($("#sh-saleOrder-button-qty").val() == 1){
        return;
    }
    $("#sh-saleOrder-button-qty").val(parseInt($("#sh-saleOrder-button-qty").val()) - 1);
});

$("#shSaleOrder-button-qty-add_new").click(function(){
    var stock = $('#applyApplicationDrawback #MAX_QTY').html();
    var sum = parseInt($("#sh-saleOrder-button-qty").val()) + 1;
    if(parseInt(sum)>parseInt(stock)){
        layer.msg("数量不能超过："+stock,{icon:2,time:2000});
        return;
    }
    $("#sh-saleOrder-button-qty").val(sum);
});

// 金额判断
$("#DEMAND_INPUT_MONEY").keyup(function(){
    var money = $.trim($("#applyApplicationDrawback #DEMAND_INPUT_MONEY").val());
    if(isNaN(money)) {
        $("#applyApplicationDrawback #DEMAND_INPUT_MONEY").val('');
        layer.msg("请输入正确的金额",{icon:2,time:2000});
    }
});
