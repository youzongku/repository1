var layer = undefined;
var laypage = undefined;
var skus = [];
var logsHtmlJson = {
    1:"提交申请",
    3:"平台审核不通过",
    4:"已取消"
}
var passedMsgs = {"passedMsg":"平台审核通过","moneyReturn":"退款至用户余额","finished":"已完成"}

function initParam(layerParam,laypageParam){
    layer = layerParam;
    laypage = laypageParam;
}

function init_header(){
    isnulogin(function(email){
        ajax_get("/inventory/queryWarehouse", "", "application/json",
            function(data) {
                if (data && data.length > 0) {
                    header(data);
                }
            }
        );
    });
}

function init_categorys(){
    isnulogin(function(email){
        ajax_get("/product/api/realCateQuery?level=1", "", "application/json",
            function(data) {
                if (data && data.length > 0) {
                    categorys(data);
                }
            }
        );
    });
}

function init_inventory(obj,flag,lay){
    laypage = lay;
    var currPage = 1;
    var pageSize = 10;
    if(flag){
        currPage = obj.curr;
    }

    var param = {pageSize: pageSize, currPage: currPage, productTitle : $("#title").val()};
    if($("select[name='stock']").val() != ""){
        param.avaliableStock = $("select[name='stock']").val();
    }
    if($("select[name='categorys']").val() != ""){
        param.productCategoryId = $("select[name='categorys']").val();
    }
    if($("select[name='choose']").val() != ""){
        param.warehouseId = $("select[name='choose']").val();
    }
    postIvys(param);
}

// 获取指定分销商的微仓产品信息
function postIvys(param){
    $(".five-2-tr1").siblings().remove();
    $('.nothing').remove();
    ajax_post("/inventory/getIvysAndStorage", JSON.stringify(param), "application/json",
        function(data) {
             if(data.code){
                layer.msg(data.msg,{icon:2,time:2000},function(index){
                    layer.close(index);
                    window.location.href = "/personal/login.html";
                });
            }
            var datas = JSON.parse(data);
            if (datas && datas.data.list.length > 0) {
                var list_p = datas.data.list;
                //根据微仓库存倒序显示
                list_p.sort(function(a,b){
                    return b.avaliableStock - a.avaliableStock;
                })
                content(list_p);
                init_pagination_invent(datas.data.totalPages,datas.data.pageNo);
            }else{
                //loading
                creatLoading(true,theDiv);
                //setTimeout(function(){layer.msg('库存中没有该商品', {icon : 5, time : 1000}, function(index){ layer.close(index); })},1000);
                addLoadGif({
                    togBox:".three-2",
                    togPage:'#pagination_invent',
                    hint: '<div class="nothing"><div>抱歉，库存中没有该商品</div></div>'
                },true);
            }
        },
        function(xhr, status) {
        }
    );
}

function show_categorys_product(catId){
    if(catId){
        ajax_get("/product/api/getSkus?catId="+catId, "", "application/json",
            function(data) {
                if(data && data.length > 0){
                    skus = data;
                    init_inventory(null,false,laypage);
                }else{
                    skus.push("");
                    $("#pagination_invent").empty();
                    $(".five-2-tr1").siblings().remove();
                }
            }
        );
    }else{
        skus = [];
        init_inventory(null,false,laypage);
    }
}

//表格头部微仓下拉值
function header(data){
    $("select[name='choose'] option:eq(0)").siblings().remove();
    var htmlCode = "";
    $.each(data,function(i,item){
        htmlCode += "<option value='"+item.warehouseId+"'>"+item.warehouseName+"</option>";
    });
    $("select[name='choose'] option:eq(0)").after(htmlCode);
}

//表格头部类型下拉值
function categorys(data){
    $("select[name='categorys'] option:eq(0)").siblings().remove();
    var htmlCode = "";
    $.each(data,function(i,item){
        htmlCode += "<option value='"+item.iid+"'>"+item.cname+"</option>";
    });
    $("select[name='categorys'] option:eq(0)").after(htmlCode);
    $("select[name='categorys']").change(function(){
        //show_categorys_product($(this).val());
        init_inventory(null,false,laypage);
    });
}

//表格内容
function content(data){
    var htmlCode = "";
    $.each(data,function(i,item){
        htmlCode += "<tr class='three-2-tr2'>" +
            "<td class='tr2-td1'>" +
            "<div class='inventory-img'><a target='_blank' href='../../product/product-detail.html?sku="+item.sku+"&warehouseId="+item.warehouseId+"'><img class='bbclazy' src='../img/img-load.png' data-original='"+urlReplace(item.imgUrl,item.sku,"",80,80,100)+"'/></a></div>" +
            "<p class='inventory-list'><a target='_blank' href='../../product/product-detail.html?sku="+item.sku+"&warehouseId="+item.warehouseId+"'>"+item.productTitle+"</a>";
        if(item.gift) {
            htmlCode += "<br/><b class='red'>[赠品]</b>";
        }
        htmlCode += "</p>" +
            "</td>" +
            "<td class='tr2-td2'>"+item.sku+"</td>" +
            "<td class='tr2-td3'>"+item.avaliableStock+"</td>" +
			"<td class='tr2-td4' style='display:none'>"+item.warehouseId+"</td>" +
            "<td><p><span></span>"+item.warehouseName+"</p></td>"+
            "<td onclick='openDetail(this)' style='cursor: pointer;'>查看";
        htmlCode += "</td></tr>";
    });
    $(".five-2-tr1").after(htmlCode);
    $(".three-2").show();
    $("img.bbclazy").lazyload({
        effect:'fadeIn',
        threshold:'10'
    });
    //loading
    return creatLoading(true,theDiv);
}

//初始化分页栏
function init_pagination_invent(totalPage,currPage) {
    scrollPosTop();
    if ($("#pagination_invent")[0] != undefined) {
        $("#pagination_invent").empty();
        laypage({
            cont: 'pagination_invent',
            pages: totalPage,
            curr: currPage,
            groups: 5,
            skin: 'yahei',
            first: '首页',
            last: '尾页',
            prev: '上一页',
            next: '下一页',
            skip: true,
            jump: function(obj, first) {
                //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                if(!first){
                    init_inventory(obj,true,laypage);
                }
            }
        });
    }
}


// 展示一个sku的微仓变更历史
function openDetail(obj) {
    var sku = $(obj).parents('tr').find('.tr2-td2').html();
	var warehouseId = $(obj).parents('tr').find('.tr2-td4').html();
    var param = {
        sku: sku,
		warehouseId:warehouseId
    };
    ajax_post("/inventory/getIvyChangeHistory", JSON.stringify(param), "application/json",
        function(data) {
            if (data && data.length > 0) {
                var domContent = '';
                for(var i = 0; i< data.length;i++) {
                    var applyReturnOrderOptHtml = '';
                    // 非赠品才有申请退货操作
                    if(data[i].isGift!=1){
                        var capfee = data[i].capfee ? data[i].capfee.toFixed(2) : "--";
                        applyReturnOrderOptHtml = '<a style="cursor: pointer" inRecordId="'+data[i].id+'" warehouseId="'+data[i].warehouseId+'" ' +
                            'warehouseName="'+data[i].warehouseName+'" qty="'+data[i].qty+'" capfee="'+capfee+'" ' +
                            'isGift="'+data[i].isGift+'" onclick="toApplyReturnOrder(this)">申请退货</a>';
                    }
                    domContent += '<tr>'
                        +'<td>' + (i + 1) + '</td>'
                        + '<td imgUrl="'+data[i].imgUrl+'" title="'+data[i].productTitle+'">' + data[i].productTitle + (data[i].isGift==1?"【<b style='color: red;'>赠</b>】":"")+'</td>'
                        + '<td>' + data[i].sku + '</td>'
						+ '<td>' + data[i].orderNo + '</td>'
						+ '<td>' + deal_with_illegal_value(data[i].purchaseTime) + '</td>'
                        + '<td>' + data[i].purchasePrice.toFixed(2) + '</td>'
                        + '<td>' + data[i].residueNum + '</td>'
                        + '<td>' + data[i].expirationDate + '</td>'
                        + '<td>' + applyReturnOrderOptHtml +' </td>'
                        + '</tr>';
                }

                var content = "<table class='layui-table' style='text-align: center'>" +
                "<thead>" +
                "<tr>" +
                "<th width='60px'>序号</th>" +
                "<th width='180px'>商品名称</th>" +
                "<th width='85px'>商品编号</th>" +
				"<th width='105px'>采购单号</th>" +
				"<th width='139px'>采购时间</th>" +
                "<th width='105px'>采购价格(元)</th>" +
                "<th width='70px'>数量(个)</th>" +
                "<th width='139px'>到期时间</th>" +
                "<th width='110px'>操作</th>" +
                "</tr>" +
                "</thead>" +
                "<tbody>" + domContent + "</tbody>" +
                "</table>";

                layer.open({
                    type: 1
                    ,area: ['983px', '450px']
                    ,title: '微仓商品详情'
                    ,shade: 0.6 //遮罩透明度
                    ,anim: 1 //0-6的动画形式，-1不开启
                    ,content:content
                });
            } else {
                layer.msg('没有对应的微仓库存详情',{icon:2,time:2000},function(index){
                    layer.close(index);
                });
            }
        }
    );
}

// 缓存要退货的商品详细数据
var toApplyRoProductDetailCache = {}

// 申请退货
function applyReturnOrder(obj){
    var isGift = toApplyRoProductDetailCache['isGift']
    if(isGift==1){
        layer.msg("赠品不能申请退货",{icon:2,time:2000})
        returnFromApplyingRo()
        return;
    }
    // 用户填的金额
    var userExpectTotalReturnAmount = $.trim($("#applyRO_optDiv").find('input[name="userExpectTotalReturnAmount"]').val())
    if(!userExpectTotalReturnAmount){
        layer.msg("请输入金额",{icon:2,time:2000})
        $("#applyRO_optDiv").find('input[name="userExpectTotalReturnAmount"]').focus()
        return;
    }
    if(!isMoneyPattern(userExpectTotalReturnAmount)){
        layer.msg("请输入正确的金额格式",{icon:2,time:2000})
        $("#applyRO_optDiv").find('input[name="userExpectTotalReturnAmount"]').val("")
        $("#applyRO_optDiv").find('input[name="userExpectTotalReturnAmount"]').focus()
        return;
    }
    toApplyRoProductDetailCache['userExpectTotalReturnAmount'] = userExpectTotalReturnAmount
    // 备注
    toApplyRoProductDetailCache['remarks'] = $("#applyRO_optDiv").find('textarea[name="remarks"]').val()

    //询问框
    layer.confirm('确认要申请退货？', {
        btn: ['取消','确认申请'] //按钮
    }, function(index){
        layer.close(index)
    }, function(){
        $(obj).attr("disabled","disabled")
        ajax_post("/purchase/returnod/apply",JSON.stringify(toApplyRoProductDetailCache),"application/json",function (data) {
            if(data.suc){
                $(obj).removeAttr("disabled")
                layer.msg("您的退货申请已提交，请耐心等待审核！",{icon:1,time:2000},function () {
                    cleanApplyRoProductDetailCache()
                    gotoReturnOrderList()
                });
            }else{
                layer.msg(data.msg,{icon:2,time:2000},function () {
                    $(obj).removeAttr("disabled")
                });
            }
        })
    });
}

// 准备申请退货
function toApplyReturnOrder(obj){
    cleanApplyRoProductDetailCache()
    $(obj).removeAttr("disabled")

    var trObj = $(obj).parent().parent();
    var productTitle = trObj.find("td:eq(1)").attr("title") // 商品名称
    var imgUrl = trObj.find("td:eq(1)").attr("imgUrl") // 商品图像
    var sku = trObj.find("td:eq(2)").text()
    var purchaseOrderNo = trObj.find("td:eq(3)").text() // 采购单号
    var purchaseTime = trObj.find("td:eq(4)").text() // 采购时间
    var purchasePrice = trObj.find("td:eq(5)").text() // 采购价格
    var residueNum = trObj.find("td:eq(6)").text() // 剩余数量
    var expirationDate = trObj.find("td:eq(7)").text() // 过期时间
    var warehouseId = $(obj).attr("warehouseId"); // 仓库id
    var warehouseName = $(obj).attr("warehouseName"); // 仓库名称
    var inRecordId = $(obj).attr("inRecordId");
    var qty = $(obj).attr("qty"); // 采购数量
    var capfee = $(obj).attr("capfee"); // 均摊价
    var isGift = $(obj).attr("isGift"); // 是否是赠品

    toApplyRoProductDetailCache = {} // 先清空数据
    toApplyRoProductDetailCache["productTitle"] = productTitle
    toApplyRoProductDetailCache["imgUrl"] = imgUrl
    toApplyRoProductDetailCache["sku"] = sku
    toApplyRoProductDetailCache["purchaseOrderNo"] = purchaseOrderNo
    toApplyRoProductDetailCache["purchaseTime"] = purchaseTime
    toApplyRoProductDetailCache["purchasePrice"] = purchasePrice
    toApplyRoProductDetailCache["residueNum"] = residueNum
    toApplyRoProductDetailCache["expirationDate"] = expirationDate
    toApplyRoProductDetailCache["warehouseId"] = warehouseId
    toApplyRoProductDetailCache["warehouseName"] = warehouseName
    toApplyRoProductDetailCache["inRecordId"] = inRecordId
    toApplyRoProductDetailCache["qty"] = qty
    toApplyRoProductDetailCache["capfee"] = capfee
    toApplyRoProductDetailCache["isGift"] = isGift

    // 采购信息
    $("#applyRO_purchaseInfoDiv").empty()
    var purchaseInfoHtml = '<p>'+
        '<span>'+purchaseOrderNo+'</span>'+
        '<span>'+purchaseTime+'</span>'+
        '</p>'+
        '<table class="product_info">'+
        '<caption>商品信息</caption>'+
        '<thead>'+
        '<tr>'+
        '<th style="width: 30%">商品名称</th><th style="width: 14%">商品编号</th><th style="width: 14%">采购数量（个）</th>' +
        '<th style="width: 14%">微仓数量（个）</th><th style="width: 14%">采购单价（元）</th><th style="width: 14%">均摊价（元）</th>'+
        '</tr>'+
        '</thead>'+
        '<tbody>'+
        '<tr>'+
        '<td><a class="pro-info-img"><img src="'+imgUrl+'" alt="'+productTitle+'"/></a><p>'+productTitle+'</p></td>'+
        '<td>'+sku+'</td>'+
        '<td>'+qty+'</td>'+
        '<td>'+residueNum+'</td>'+
        '<td>'+purchasePrice+'</td>'+
        '<td>'+capfee+'</td>'+
        '</tr>'+
        '</tbody>'+
        '</table>'
    $("#applyRO_purchaseInfoDiv").html(purchaseInfoHtml)

    // 退货申请
    $("#applyRO_optDiv").find('div[name="productInfoDiv"]').empty()
    var optPurchaseInfoHtml = '<a class="pro-info-img"><img src="'+imgUrl+'" alt="'+productTitle+'"/></a>'+
        '<div><p>'+productTitle+'</p><div><span>'+sku+'</span><span>'+warehouseName+'</span></div></div>'
    $("#applyRO_optDiv").find('div[name="productInfoDiv"]').html(optPurchaseInfoHtml)
    $("#applyRO_optDiv").find('div[name="productInfoExpirationDateDiv"]').empty()
    $("#applyRO_optDiv").find('div[name="productInfoExpirationDateDiv"]').html('到期日期：'+expirationDate)

    setReturnQtyAndAmount(capfee, residueNum, residueNum)

    layer.closeAll()
    $(".send_product_box").hide();
    $("#applyRoDiv").show();
}

/**
 * 设置退货数量和金额
 * @param capfee  均摊价
 * @param return  退货数量
 * @param maxReturnQty  最大退货数量，可以为空
 */
function setReturnQtyAndAmount(capfee,returnQty,maxReturnQty){
    // 记录均摊价，方便操作
    if(capfee){
        $("#applyRO_optDiv").find('div[name="returnQtyDiv"]').find("input").attr("capfee",capfee)
    }
    if(maxReturnQty){ // 最大退货数量
        $("#applyRO_optDiv").find('div[name="returnQtyDiv"]').find("input").attr("maxReturnQty",maxReturnQty)
        $("#applyRO_optDiv").find('span[name="reminder"]').html('最多能退'+maxReturnQty+'个')
    }
    $("#applyRO_optDiv").find('div[name="returnQtyDiv"]').find("input").val(returnQty)// 退货数量
    // var expectReturnAmount = (capfee * returnQty).toFixed(2)
    calculateExpectReturnAmount(capfee, returnQty)
    // 修改退货数量
    toApplyRoProductDetailCache["returnQty"] = returnQty
}

// 计算退款金额
function calculateExpectReturnAmount(capfee,returnQty){
    // {"sku":"","warehouseId":2024,"capfee":0.1,returnQty":1,"expirationDate":"2012-01-12"}
    var params = {
        "sku":toApplyRoProductDetailCache["sku"],
        "warehouseId":toApplyRoProductDetailCache["warehouseId"],
        "capfee":capfee,
        "returnQty":returnQty,
        "expirationDate":toApplyRoProductDetailCache["expirationDate"]
    }
    ajax_post("/purchase/returnod/expectReturnAmount",JSON.stringify(params),"application/json",function (data) {
        var expectReturnAmountResult = data;
        if(expectReturnAmountResult.suc){
            // $("#applyRO_optDiv").find('div[name="expectReturnAmountFormulaDiv"]').html("¥："+capfee+"&nbsp;*&nbsp;"+returnQty+"&nbsp;*&nbsp;"+expectReturnAmountResult.coefficient)
            $("#applyRO_optDiv").find('span[name="expectReturnAmount"]').show()
            $("#applyRO_optDiv").find('span[name="expectReturnAmount"]').html('预计可退'+expectReturnAmountResult.returnAmount+'元，<em style="color: red">以实际到账金额为准</em>')
        }else{
            $("#applyRO_optDiv").find('span[name="expectReturnAmount"]').hide()
        }
    })
}

// 输入退货数量
function inputReturnQty(obj){
    var maxReturnQty = $(obj).attr("maxReturnQty")
    var capfee = $(obj).attr("capfee")
    var curQty = $(obj).val()
    var pattern = /^([1-9]\d*)$/;// 正整数
    if (!pattern.test(curQty)) {
        $(obj).val(maxReturnQty);
        setReturnQtyAndAmount(capfee, parseInt(maxReturnQty))
        layer.msg("退货数量必须为正整数",{icon:2,time:2000});
        return;
    }
    if(curQty>maxReturnQty){
        $(obj).val(maxReturnQty);
        setReturnQtyAndAmount(capfee, parseInt(maxReturnQty))
        layer.msg("退货数量不能大于最大退货数量"+maxReturnQty,{icon:2,time:2000});
        return;
    }
    setReturnQtyAndAmount(capfee, parseInt(curQty))
}
// 增加退货数量
function increaseReturnQty(){
    var inputObj = $("#applyRO_optDiv").find('div[name="returnQtyDiv"]').find("input")
    var maxReturnQty = inputObj.attr("maxReturnQty")
    var curQty = inputObj.val();
    if(curQty==maxReturnQty){
        layer.msg("退货数量不能大于最大退货数量"+maxReturnQty,{icon:2,time:2000});
        return;
    }
    var capfee = inputObj.attr("capfee")
    setReturnQtyAndAmount(capfee, parseInt(curQty)+1)
}
// 减少退货数量
function reduceReturnQty(){
    var inputObj = $("#applyRO_optDiv").find('div[name="returnQtyDiv"]').find("input")
    var curQty = inputObj.val();
    if(curQty==1){
        layer.msg("退货数量不能小于1",{icon:2,time:2000});
        return;
    }
    var capfee = inputObj.attr("capfee")
    setReturnQtyAndAmount(capfee, parseInt(curQty)-1)
}

// 从退货单申请页返回到库存列表
function returnFromApplyingRo(){
    cleanApplyRoProductDetailCache()
    $(".send_product_box").show();
    $("#applyRoDiv").hide();
}

// 查看退货单详情
function showRoDetail(obj){
    $(".send_product_box").hide();
    $("#viewRoDiv").show();

    ajax_post("/purchase/returnod/get",JSON.stringify({returnOrderNo:$(obj).attr("returnOrderNo")}),"application/json",function (data) {
        var returnOrderDto = data.returnOrderDto
        var details = returnOrderDto.details
        var detail = details[0]
        var logs = returnOrderDto.logs

        $("#cancelApplyReturnOrderDiv").empty()
        if(canCancelRo(returnOrderDto.status)){ // 待审核的才可以取消
            var cancelHtml = '<button returnOrderDto="'+returnOrderDto+'" class="btn-hyacinthine-small" onclick="cancelApplyReturnOrder()">取消申请</button>';
            $("#cancelApplyReturnOrderDiv").html(cancelHtml)
        }

        // 采购信息
        $("#viewRO_purchaseInfoDiv").empty()
        var purchaseInfoHtml = '<p>'+
            '<span>'+detail.purchaseOrderNo+'</span>'+
            '<span>'+detail.purchaseTime+'</span>'+
            '</p>'+
            '<table class="product_info">'+
            '<caption>商品信息</caption>'+
            '<thead>'+
            '<tr>'+
            '<th style="width: 30%">商品名称</th><th style="width: 14%">商品编号</th><th style="width: 14%">采购数量（个）</th>' +
            '<th style="width: 14%">微仓数量（个）</th><th style="width: 14%">采购单价（元）</th><th style="width: 14%">均摊价（元）</th>'+
            '</tr>'+
            '</thead>'+
            '<tbody>'+
            '<tr>'+
            '<td><a class="pro-info-img"><img src="'+detail.imgUrl+'" alt="'+detail.productTitle+'"/></a><p>'+detail.productTitle+'</p></td>'+
            '<td>'+detail.sku+'</td>'+
            '<td>'+detail.qty+'</td>'+
            '<td>'+detail.residueNum+'</td>'+
            '<td>'+detail.purchasePrice+'</td>'+
            '<td>'+detail.capfee+'</td>'+
            '</tr>'+
            '</tbody>'+
            '</table>'
        $("#viewRO_purchaseInfoDiv").html(purchaseInfoHtml)

        $("#viewRoInfoDiv").find('div[name="productInfoDiv"]').empty()
        var returnOrderInfoHtml = '<a class="pro-info-img"><img src="'+detail.imgUrl+'" alt="'+detail.productTitle+'"/></a>'+
            '<div><p>'+detail.productTitle+'</p><div><span>'+detail.sku+'</span><span>'+detail.warehouseName+'</span></div></div>'
        $("#viewRoInfoDiv").find('div[name="productInfoDiv"]').html(returnOrderInfoHtml)
        $("#viewRoInfoDiv").find('div[name="productInfoExpirationDateDiv"]').html('到期日期：'+detail.expirationDate)
        $("#viewRoInfoDiv").find('div[name="returnQtyDiv"]').html('数量：'+detail.returnQty)
        $("#viewRoInfoDiv").find('div[name="actualReturnAmountDiv"]').empty()
        if(returnOrderDto.status==2){
            $("#viewRoInfoDiv").find('div[name="actualReturnAmountDiv"]').html('实际退款金额：'+parseFloat(returnOrderDto.actualTotalReturnAmount).toFixed(2)+'元')
        }
        $("#viewRoInfoDiv").find('div[name="remarksDiv"]').html('备注：'+(returnOrderDto.remarks?returnOrderDto.remarks:""))

        // 日志信息
        $("#viewRoStatusDiv").empty()
        var logsSize = logs.length
        if(logsSize>0) {
            var latestLog = logs[0]
            // {"passedMsg":"平台审核通过","moneyReturn":"退款至用户余额","finished":"已完成"}
            var logHtml = ''
            if(latestLog.status==2){
                logHtml += '<h4>状态：'+passedMsgs['finished']+'</h4>'
            }else{
                logHtml += '<h4>状态：'+logsHtmlJson[latestLog.status]+'</h4>'
            }

            for(var i=0;i<logsSize;i++){
                if(logs[i].status==2){
                    logHtml+='<p>'+logs[i].createTimeStr+'　　　'+passedMsgs['passedMsg']+'</p>'
                    logHtml+='<p>'+logs[i].createTimeStr+'　　　'+passedMsgs['moneyReturn']+'</p>'
                }else{
                    logHtml+='<p>'+logs[i].createTimeStr+'　　　'+logsHtmlJson[logs[i].status]+'</p>'
                }
            }
            $("#viewRoStatusDiv").html(logHtml)
        }
    })
}

// 是否可以取消操作
function canCancelRo(status){
    if(!status) return false;
    return status==1
}

// 从退货单详情页返回到退货单列表
function gotoReturnOrderListFromDetail(){
    $(".send_product_box").show();
    $("#viewRoDiv").hide();
}

// tab栏切换
$(document).on("click",".topup_box_tab li",function(){
    var indexInven=$(this).index();
    if(indexInven==0) {
        cleanApplyRoProductDetailCache()
        init_inventory(null,false,laypage);
    } else if(indexInven==1) {
        cleanApplyRoProductDetailCache()
        init_inventory(null,false,laypage);
        getReturnOrders()
    }
    $(this).addClass("topup_box_current").siblings().removeClass("topup_box_current");
    $(".inventory_cont_box").eq(indexInven).removeClass("display").siblings().addClass("display");
});

// 申请完-跳转到退货单列表
function gotoReturnOrderList(){
    cleanApplyRoProductDetailCache()
    $(".send_product_box").show();
    $("#applyRoDiv").hide();
    // 要刷新微仓库存列表页
    init_inventory(null,false,laypage);
    // 选中微仓退货列表
    $(".topup_box_tab li:eq(1)").click()
}

// 情况缓存数据
function cleanApplyRoProductDetailCache(){
    toApplyRoProductDetailCache = {}
    $("#applyRO_optDiv").find('input[name="userExpectTotalReturnAmount"]').val("")
    $("#applyRO_optDiv").find('textarea[name="remarks"]').val("")
}

// 获取退货记录
function getReturnOrders(obj){
    var params = {
        returnOrderNo:$("#ro_returnOrderNo").val(),
        pageSize:10
    }
    if(obj){
        params.currPage = obj.curr;
    }
    if($("#ro_scopeSelect").val()){
        params['dateScope'] = $("#ro_scopeSelect").val()
    }
    ajax_post("/purchase/returnod/list",JSON.stringify(params),"application/json",function (data) {
        insertReturnOrdersContent(data.list)
        init_pagination_returnOrders(data.totalPage,data.currPage)
    })
}

function insertReturnOrdersContent(list){
    $("#returnOrdersDiv").find("div[name='returnOrders']").empty()
    $("#returnOrdersDiv").find("div[name='pagination_returnOrders']").empty()
    if(!list || list.length==0){
        var html = '<div class="three-2 purchase-list light-gray" name="returnOrder" style="text-align: center">暂无退货记录</div>'
        $("#returnOrdersDiv").find("div[name='returnOrders']").append(html)
        return;
    }

    var html = ''
    for(var i=0; i<list.length; i++){
        // 商品详情
        var details = list[i].details
        var detail = details[0]
        html += '<div class="three-2 purchase-list light-gray"><ul>'+
        '<li>退货单号：'+list[i].returnOrderNo+'</li>'+
        '<li>申请时间：'+list[i].applicationTimeStr+'</li>'+
        '</ul>'+
        '<div class="purchase-table">'+
        '<table>'+
        '<tbody>'+
        '<tr class="three-2-tr1 bg-gray word-black">'+
        '<td class="tr1-td1" style="width: 44%;">商品名称</td>'+
        '<td class="tr1-td2" style="width: 14%;">商品编号</td>'+
        '<td class="tr1-td4" style="width: 14%;">数量(个)</td>'+
        '<td class="tr1-td7" style="width: 14%;">状态</td>'+
        '<td class="tr1-td8" style="width: 14%;">操作</td>'+
        '</tr>'+
        '<tr class="three-2-tr2">'+
        '<td class="tr2-td1 td-border">'+
        '<div class="list-img">'+
        '<a target="_blank" href=""><img class="bbclazy" src="'+detail.imgUrl+'" style="display: inline;"></a>'+
        '</div>'+
        '<span class="list-order"><a target="_blank" href="">'+detail.productTitle+'</a></span>'+
        '</td>'+
        '<td class="tr2-td2 td-border">'+detail.sku+'</td>'+
        '<td class="tr2-td4 td-border">'+detail.returnQty+'</td>'+
        '<td class="tr2-td7">'+
        '<p class="lineh-27">'+list[i].statusStr+'</p>'+
        '<p class="tr2-td7p">' +
            '<a returnOrderNo="'+list[i].returnOrderNo+'" href="javascript:;" class="btn-green-small" onclick="showRoDetail(this)">查看详情</a></p>'+
        '</td>'+
        '<td class="tr2-td8 tr2-td8a">'
        if(canCancelRo(list[i].status)){// 待审核的才可以取消
            html += '<a returnOrderNo="'+list[i].returnOrderNo+'" href="javascript:;" class="pay-agian btn-hyacinthine-small" onclick="cancelApplyReturnOrder(this)">取消申请</a>'
        }
        html += '</td>'+
        '</tr>'+
        '</tbody>'+
        '</table>'+
        '</div>' +
        '</div>'
    }
    $("#returnOrdersDiv").find("div[name='returnOrders']").html(html)
}

//初始化分页栏
function init_pagination_returnOrders(totalPage,currPage) {
    if ($("#returnOrdersDiv").find("div[name='pagination_returnOrders']")[0] != undefined) {
        $("#returnOrdersDiv").find("div[name='pagination_returnOrders']").empty();
        laypage({
            cont: $("#returnOrdersDiv").find("div[name='pagination_returnOrders']"),
            pages: totalPage,
            curr: currPage,
            groups: 5,
            skin: 'yahei',
            first: '首页',
            last: '尾页',
            prev: '上一页',
            next: '下一页',
            skip: true,
            jump: function(obj, first) {
                //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                if(!first){
                    getReturnOrders(obj);
                }
            }
        });
    }
}

//取消申请
function cancelApplyReturnOrder(obj){
    //询问框
    layer.confirm('确定要取消申请？', {icon: 3, title:'提示'}, function(index){
        var params = {returnOrderNo:$(obj).attr("returnOrderNo")}
        $(obj).attr("disabled","disabled")
        ajax_post("/purchase/returnod/cancel",JSON.stringify(params),"application/json",function (data) {
            if(data.suc) {
                layer.msg(data.msg,{icon:1,time:2000},function () {
                    $(obj).remove()
                    getReturnOrders()
                });
            }else{
                layer.msg(data.msg,{icon:2,time:2000},function () {
                    $(obj).removeAttr("disabled")
                })
            }
        })
        layer.close(index);
    });
}
