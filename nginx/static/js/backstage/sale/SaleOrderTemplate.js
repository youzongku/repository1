var yjfWxUrl = "";
define(['jquery'],function ($) {
    function SaleOrderTemplate(){
    }
    SaleOrderTemplate.prototype = {
        div_sale_order_list_outer : 
            "<div class='list_content' id='sublit_detail'>"+
				"<p>共#{rowCnt}记录，每页显示"+
					"<select>"+
						"<option value='selected'>10</option>"+
						"<option value=''>30</option>"+
						"<option value=''>50</option>"+
					"</select>"+
					"，共#{pageCnt}页"+
				"</p>"+
				"<div id='sale_pagination' style='text-align: center; margin-top: 30px;'>" +
				"</div>"+
            "</div>",

		ul_sale_order_list :
            "<tr class='list_message' id='ul_list_#{orderId}'>"+
				"<td class='add_list_down' onclick=\"showDetail(this,'#{orderId}',true)\"><span>+</span></td>"+
				"<td class='all_number'>#{salesOrderNo}</td>"+
				"<td>#{optFee}</td>"+
				"<td class='d_time' id='d_time' >#{orderingDate}</td>"+
				"<td class='told_time' id='d_time'>#{orderTransDate}</td>"+
				"<td class='s_time' id='d_time'>#{orderSendDate}</td>"+
				"<td class='h_time' id='d_time'>#{confirmReceiveDate}</td>"+
				"<td class='all_status'>#{statusDesc}</td>"+
				"<td class='disturb'>#{customerservice}</td>"+
				"<td class='sale_receiver'>#{receiver}</td>"+
				"<td class='nickName'>#{nickName}</td>"+
				"<td class='disturb'>#{disUser}</td>"+
				"<td class='disturb-type'>#{distributorTypeStr}</td>"+
				"<td class='ware_name' whid='#{wareId}'>#{wareName}</td>"+
				"<td class='createUser'>#{createUser}</td>"+
				"<td class='about_d'>#{operateHtml}</td>"+
            "</tr>",

         ul_wp_sale_order_list : 
            "<tr class='list_message' id='ul_list_#{orderId}'>"+
				"<td class='add_list_down' onclick=\"showDetail(this,'#{orderId}',true)\"><span>+</span></td>"+
				"<td class='all_number'>#{salesOrderNo}</td>"+
				"<td class='d_time' id='d_time' >#{orderingDate}</td>"+
				"<td class='ware_name' whid='#{wareId}'>#{wareName}</td>"+
				"<td class='all_status'>#{statusDesc}</td>"+
				"<td class='all_status'>#{disModeDesc}</td>"+
				"<td class='disturb-type'>#{distributorTypeStr}</td>"+
				"<td class='disturb'>#{customerservice}</td>"+
				"<td class='sale_receiver'>#{receiver}</td>"+
				"<td class='nickName'>#{nickName}</td>"+
				"<td class='disturb'>#{disUser}</td>"+
				"<td class='createUser'>#{createUser}</td>"+
				"<td class='about_d'>#{operateHtml}</td>"+
            "</tr>",

		div_sale_order_detail :
            "<tr>" +
				"<td colspan='17' style='padding: 0'>" +
					"<div class='list_detail detail add_list_down_one' style='display:inline-block;' id='div_detail_#{orderId}'>" +
						"<div style='padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>订单信息</span>" +
							"<div style='text-align: left;'>" +
								"<span class='spliter'>平台名称：#{platformName}</span>" +
								"<span class='spliter'>平台单号：#{platformNo}</span>" +
								"<span class='spliter'>交易号：#{tradeNumber}</span>" +
								"<span class='spliter'>实付款：#{actualAmount}元</span>" +
								"<span class='spliter'>运费：#{postFee}元</span>" +
								"<span class='spliter'>收款账户：#{collectAccount}</span><br>" +
								"<span class='spliter'>备注信息：#{remark}</span>" +
							"</div>" +
						"</div>" +
						"<div style='padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>收件人信息</span>" +
							"<div style='text-align: left;'>" +
								"<span class='spliter'>姓名：#{receiverName}</span>" +
								"<span class='spliter'>手机：#{receiverTel}</span>" +
								"<span class='spliter'>邮编：#{postCode}</span><br>" +
								"<span class='spliter'>身份证：#{receiverId}</span>" +
								"<span class='spliter'>地址：#{receiverAddr}</span>" +
							"</div>" +
						"</div>" +
						"<div style='padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>寄件人信息</span>" +
							"<div style='text-align: left;'>" +
								"<span class='spliter'>姓名：#{senderName}</span>" +
								"<span class='spliter'>手机：#{senderTel}</span>" +
								"<span class='spliter'>地址：#{senderAddress}</span>" +
							"</div>" +
						"</div>" +
						"<div style='padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>订购人信息</span>" +
							"<div style='text-align: left;'>" +
								"<span class='spliter'>订购人姓名：#{orderer}</span>" +
								"<span class='spliter'>订购人身份证：#{ordererIDCard}</span>" +
								"<span class='spliter'>手机：#{ordererTel}</span>" +
								"<span class='spliter'>订购人邮编：#{ordererPostcode}</span>" +
								"<span class='spliter'>客户名称：#{buyerID}</span>" +
							"</div>" +
						"</div>" +
						"<div style='padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>采购进货支付信息</span>"+
							"<div style='text-align: left;'>" +
								"<span class='spliter'>支付交易号：#{purchasePaymentNo}</span>" +
								"<span class='spliter'>支付时间：#{purchasePayDate}</span>" +
								"<span class='spliter'>支付方式：#{purchasePaymentType}</span>" +
							"</div>" +
						"</div>"+
						"<div style='padding: 10px;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>财务二次支付信息</span>" +
							"<div style='text-align: left;'>" +
								"<span class='spliter'>支付人姓名：#{payer}</span>" +
								"<span class='spliter'>支付人身份证：#{payerIDCard}</span>" +
								"<span class='spliter'>支付交易号：#{payNumber}</span>" +
								"<span class='spliter'>支付时间：#{payTime}</span>" +
								"<span class='spliter'>支付方式：#{payType}</span>" +
							"</div>" +
						"</div>" +
						"<hr>" +
						"<p class='bbc_postage_p'>商品信息</p>"+
						"<ul style='font-weight: 800;'>" +
							"<li>商品编号</li>" +
							"<li style='width:25%'>商品名称</li>" +
							"<li>国际条码</li>" +
							"<li>所属仓库</li>" +
							"<li>真实售价(元)</li>"+
							"<li>数量(个)</li>"  +
							"<li>小计(元)</li>"+
						"</ul>"+
						"<div name='not_msite_div' class='bbc_postage'>" +
							"<span>商品总数量：<i>#{totalQty}</i>个</span><span>平台运费：<i>#{bbcPostage}</i>元</span>" +
						"</div>"+
						"<p class='bbc_postage_p'>采购信息</p>"+
						"<ul style='font-weight: 800;' id='sale_order_purchase_info_ul_#{orderId}'>" +
							"<li style='width:20%'>采购订单</li>" +
							"<li style='width:20%'>商品编号</li>" +
							"<li style='width:20%'>均摊价（元）</li>" +
							"<li style='width:20%'>商品数量(个)</li>" +
							"<li style='width:20%'>所含采购金额(元)</li>"+
						"</ul>"+
					"</div>" +
				"</td>" +
			"</tr>"
		,
		div_sale_order_detail_msite :
			"<tr>" +
				"<td colspan='14' style='padding: 0'>" +
					"<div class='list_detail detail add_list_down_one' style='display:inline-block;' id='div_detail_#{orderId}'>"+
						"<div style='padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>订单信息</span>" +
							"<div style='text-align: left;'>"+
								"<span class='spliter'>平台名称：#{platformName}</span>" +
								"<span class='spliter'>平台单号：#{platformNo}</span>" +
								"<span class='spliter'>交易号：#{tradeNumber}</span>" +
								"<span class='spliter'>实付款：#{actualAmount}元</span>" +
								"<span class='spliter'>运费：#{postFee}元</span>"+
								"<span class='spliter'>收款账户：#{collectAccount}</span><br>" +
								"<span class='spliter'>备注信息：#{remark}</span>" +
							"</div>" +
						"</div>"+
						"<div style='padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>收件人信息</span>"+
							"<div style='text-align: left;'>" +
								"<span class='spliter'>姓名：#{receiverName}</span>" +
								"<span class='spliter'>手机：#{receiverTel}</span>" +
								"<span class='spliter'>邮编：#{postCode}</span><br>" +
								"<span class='spliter'>身份证：#{receiverId}</span>" +
								"<span class='spliter'>地址：#{receiverAddr}</span>" +
							"</div>" +
						"</div>"+
						"<div style='padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>寄件人信息</span>"+
							"<div style='text-align: left;'>" +
								"<span class='spliter'>姓名：#{senderName}</span>" +
								"<span class='spliter'>手机：#{senderTel}</span>" +
								"<span class='spliter'>地址：#{senderAddress}</span>" +
							"</div>" +
						"</div>"+
						// M站终端消费者
						"<div id='terminal_consummer_div' style='padding: 10px;border-bottom: 1px dashed #8C8C8C;display:none'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>终端消费者支付信息</span>"+
							"<div style='text-align: left;'>" +
								"<span class='spliter'>支付交易号：#{tcPaymentNo}</span>" +
								"<span class='spliter'>支付时间：#{tcPayDate}</span>" +
								"<span class='spliter'>支付方式：#{tcPaymentType}</span>" +
							"</div>" +
						"</div>"+
						"<div style='padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>采购进货支付信息</span>"+
							"<div style='text-align: left;'>" +
								"<span class='spliter'>支付交易号：#{purchasePaymentNo}</span>" +
								"<span class='spliter'>支付时间：#{purchasePayDate}</span>" +
								"<span class='spliter'>支付方式：#{purchasePaymentType}</span>" +
							"</div>" +
						"</div>"+
						// M站订单补差
						"<div id='compensation_div' style='padding: 10px;border-bottom: 1px dashed #8C8C8C;display:none'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>订单补差支付信息</span>"+
							"<div style='text-align: left;'>" +
								"<span class='spliter'>支付交易号：#{compensationPaymentNo}</span>" +
								"<span class='spliter'>支付时间：#{compensationPayDate}</span>" +
								"<span class='spliter'>支付方式：#{compensationPaymentType}</span>" +
							"</div>" +
						"</div>"+
						"<div style='padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>后台审核支付信息</span>"+
							"<div style='text-align: left;'>" +
								"<span class='spliter'>支付人姓名：#{payer}</span>" +
								"<span class='spliter'>支付人身份证：#{payerIDCard}</span>" +
								"<span class='spliter'>支付交易号：#{payNumber}</span>" +
								"<span class='spliter'>支付时间：#{payTime}</span>" +
								"<span class='spliter'>支付方式：#{payType}</span>" +
							"</div>" +
						"</div>"+
						"<div style='padding: 10px;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>订购信息</span>"+
							"<div style='text-align: left;'>" +
								"<span class='spliter'>订购人姓名：#{orderer}</span>" +
								"<span class='spliter'>订购人身份证：#{ordererIDCard}</span>" +
								"<span class='spliter'>手机号码：#{ordererTel}</span>" +
								"<span class='spliter'>订购人邮编：#{ordererPostcode}</span>" +
								"<span class='spliter'>客户名称：#{buyerID}</span>" +
							"</div>" +
						"</div>" +
						"<hr>"+
						"<p class='bbc_postage_p'>商品信息</p>"+
						"<ul style='font-weight: 800;'>"+
							"<li>商品编号</li>"+
							"<li style='width: 25%'>商品名称</li>"+
							"<li>国际条码</li>"+
							"<li>所属仓库</li>"+
							"<li>真实售价(元)</li>"+
							"<li>数量(个)</li>"+
							"<li>小计(元)</li>"+
						"</ul>"+
						"<div name='msite_div' class='sum_price'>" +
							"终端消费者实际支付：<span>#{mOrderActualAmount}</span>&nbsp;&nbsp;元" +
						"</div>"+
						"<div name='msite_div' class='bbc_postage'>" +
							"商品总数量：<span>#{totalQty}</span>个&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;分销商总成本：<span>#{disPrimeCost}</span>&nbsp;&nbsp;元" +
						"</div>"+
						"<ul style='font-weight: 800;' id='sale_order_purchase_info_ul_#{orderId}'>" +
							"<li style='width:20%'>采购订单</li>" +
							"<li style='width:20%'>商品编号</li>" +
							"<li style='width:20%'>均摊价（元）</li>" +
							"<li style='width:20%'>商品数量(个)</li>" +
							"<li style='width:20%'>所含采购金额(元)</li>"+
						"</ul>"+
					"</div>" +
				"</td>" +
			"</tr>",

		div_sale_order_verify_detail :
            "<tr>" +
				"<td colspan='14' style='padding: 0'>" +
					"<div class='list_detail detail add_list_down_one' style='display:inline-block;' id='div_detail_verify_#{orderId}'>"+
						"<div style='text-align: left;padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span class='spliter'>订单状态：<strong>#{statusDesc}</strong></span>" +
						"</div>"+
						"<div style='padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>订单信息</span>" +
							"<div style='text-align: left;'>"+
								"<span class='spliter'>平台名称：#{platformName}</span>" +
								"<span class='spliter'>平台单号：#{platformNo}</span>" +
								"<span class='spliter'>交易号：#{tradeNumber}</span>" +
								"<span class='spliter'>实付款：#{actualAmount}元</span>" +
								"<span class='spliter'>运费：#{postFee}元</span>"+
								"<span class='spliter'>收款账户：#{collectAccount}</span><br>" +
								"<span class='spliter'>备注信息：#{remark}</span>" +
							"</div>" +
						"</div>"+
						"<div style='padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>收件人信息</span>"+
							"<div style='text-align: left;'>" +
								"<span class='spliter'>姓名：#{receiverName}</span>" +
								"<span class='spliter'>手机：#{receiverTel}</span>" +
								"<span class='spliter'>邮编：#{postCode}</span>" +
								"<span class='spliter'>身份证号：#{receiverId}</span><br>" +
								"<span class='spliter'>收货地址：#{receiverAddr}</span>" +
							"</div>" +
						"</div>"+
						"<div style='padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>寄件人信息</span>"+
							"<div style='text-align: left;'>" +
								"<span class='spliter'>姓名：#{senderName}</span>" +
								"<span class='spliter'>手机：#{senderTel}</span>" +
								"<span class='spliter'>地址：#{senderAddress}</span>" +
							"</div>" +
						"</div>"+
						"<div style='padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>订购人信息</span>"+
							"<div style='text-align: left;'>" +
								"<span class='spliter'>订购人姓名：#{orderer}</span>" +
								"<span class='spliter'>订购人身份证：#{ordererIDCard}</span>" +
								"<span class='spliter'>手机号码：#{ordererTel}</span>" +
								"<span class='spliter'>订购人邮编：#{ordererPostcode}</span>" +
								"<span class='spliter'>客户名称：#{buyerID}</span>" +
							"</div>" +
						"</div>" +
						"<div style='padding: 10px;border-bottom: 1px dashed #8C8C8C;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>采购进货支付信息</span>"+
							"<div style='text-align: left;'>" +
								"<span class='spliter'>支付交易号：#{purchasePaymentNo}</span>" +
								"<span class='spliter'>支付时间：#{purchasePayDate}</span>" +
								"<span class='spliter'>支付方式：#{purchasePaymentType}</span>" +
							"</div>" +
						"</div>"+
						"<div style='padding: 10px;'>" +
							"<span style='text-align:left;padding-bottom: 5px;font-weight: 800'>财务二次支付信息</span>"+
							"<div style='text-align: left;'>" +
								"<span class='spliter'>支付人姓名：#{payer}</span>" +
								"<span class='spliter'>支付人身份证：#{payerIDCard}</span>" +
								"<span class='spliter'>支付交易号：#{payNumber}</span>" +
								"<span class='spliter'>支付时间：#{payTime}</span>" +
								"<span class='spliter'>支付方式：#{payType}</span>" +
							"</div>" +
						"</div>"+
						"<hr>"+
						"<ul style='font-weight: 800;'>"+
							"<li>商品编号</li>"+
							"<li style='width: 25%'>商品名称</li>"+
							"<li>国际条码</li>"+
							"<li>所属仓库</li>"+
							"<li>真实售价(元)</li>"+
							"<li>数量(个)</li>"+
							"<li>小计(元)</li>" +
						"</ul>"+
						"<div class='bbc_postage'>" +
							"商品总数量：<span>#{totalQty}</span>&nbsp;&nbsp;个&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
							"分销平台运费：<span>#{bbcPostage}</span>&nbsp;&nbsp;元" +
						"</div>"+
						"<ul style='font-weight: 800;' id='sale_order_purchase_info_ul_#{orderId}'>" +
							"<li style='width:20%'>采购订单</li>" +
							"<li style='width:20%'>商品编号</li>" +
							"<li style='width:20%'>均摊价（元）</li>" +
							"<li style='width:20%'>商品数量(个)</li>" +
							"<li style='width:20%'>所含采购金额(元)</li>"+
						"</ul>"+
					"</div>" +
				"</td>" +
			"</tr>",

        ul_sale_order_detail : 
            "<ul>"+
			"<li>#{sku}</li>"+
			"<li style='width: 25%'>#{productName}</li>"+
			"<li>#{interBarCode}</li>"+
			"<li>#{warehouseName}</li>"+
			"<li>#{purchasePrice}元</li>"+
			"<li>#{qty}个</li>"+
			"<li>#{subTotal}</li>"+
            "</ul>",

		/*采购信息列表*/
		market_purchas_infor_detail :
			"<ul>"+
			"<li style='width:20%'>#{purchaseInfo_purchaseNo}</li>"+
			"<li style='width:20%'>#{purchaseInfo_sku}</li>"+
			"<li style='width:20%'>#{purchaseInfo_capfee}</li>"+
			"<li style='width:20%'>#{purchaseInfo_qty}个</li>"+
			"<li style='width:20%'>#{purchaseInfo_purchaseAmount}元</li>"+
			"</ul>"
    }
    return SaleOrderTemplate;
});

//相关操作方法
function reason(opt,orderId){
    if(opt == '1') {
    	checReason(orderId);
    }else if(opt == '2'){
    	operateRecord(orderId);
    }else if(opt == '3'){
       deliveryInfo(orderId);
    }else if(opt == 'close'){
        $('.recharge_operation>ul').hide();
        $('.recharge_operation').toggle();
    }
}

//相关操作方法
function checReason(orderId){
	if(orderId != "" && orderId != undefined){
		ajax_post("/sales/manager/showOperateRecordOfOrder", JSON.stringify({"orderId": orderId}), "application/json",
			function(response) {
				if (response.result == true) {
					var content;
					var records = response.operateRecords;
					if(records!= undefined){
						for(var i=0,len=records.length;i<len;i++){
							content = "<li class='recharge_operation_li2'>" +
								"<ul>" +
								"<li>"+records[i].operateTimeStr+"</li>" +
								"<li>"+records[i].resultStr+"原因："+records[i].comment+"</li>" +
								"</ul>" +
								"</li>";
						}
		            }
					$('.recharge_operation').find("ul.check_reason").empty();
		        	$('.recharge_operation').find("ul.check_reason").append(content);
				} 
			},
			function(XMLHttpRequest, textStatus) {
			}
		);
	}

	layer.open({
		type: 1,
		title: '查看原因',
		shadeClose: true,
		move: false,
		area: ['460px','250px'],
		content:$('.recharge_operation'),
		success:function(){
			$('.check_reason').show();
			$('.recharge_operation').show();
		},
		end:function(){
			$('.check_reason').hide();
			$('.recharge_operation').hide();
		}
	})
}

//查看操作记录
function operateRecord(orderId){
	if(orderId != "" && orderId != undefined){
		$('.recharge_operation').find("ul.operation_record").empty();
		ajax_post("/sales/manager/showOperateRecordOfOrder", JSON.stringify({"orderId": orderId}), "application/json",
			function(response) {
				if (response.result == true) {
					var content = "" ;
					var records = response.operateRecords;
					if(records!= undefined && records.length > 0){
						for(var i=0,len=records.length;i<len;i++){
							var desc;
							if(records[i].operateType == 7){
								desc = "("+records[i].email+")" + records[i].comment;
							}else{
								desc = records[i].email + records[i].operateStr + records[i].resultStr;
								if(records[i].comment){
									desc += ",备注:"+records[i].comment;
								}
							}
							content += "<li class='recharge_operation_li2'>" +
								"<ul>" +
								"<li>"+records[i].operateTimeStr+"</li>" +
								"<li>"+desc+"</li>" +
								"</ul>" +
								"</li>";
						}
		            } else {
						content = "<span style='text-align: center;line-height: 190px '>暂无操作记录！</span>";
					}
		        	$('.recharge_operation').find("ul.operation_record").append(content);
				} 
			}, function(XMLHttpRequest, textStatus) {}
		);
	}

	layer.open({
		type: 1,
		title: '操作记录',
		shadeClose: true,
		move: false,
		area: ['460px','250px'],
		content:$('.recharge_operation'),
		success:function(){
			$('.operation_record').show();
			$('.recharge_operation').show();
		},
		end:function(){
			$('.operation_record').hide();
			$('.recharge_operation').hide();
		}
	})
}

//物流信息查询
function deliveryInfo(orderNo){
	if(orderNo != "" && orderNo != undefined){
		ajax_post("/sales/showLogisticsinfo", JSON.stringify({"orderNo": orderNo}), "application/json",
			function(response) {
				if (response.suc == true) {
					var content = "<li class='recharge_operation_li1'>物流信息</li>";
					var deliveryInfos = response.data;
					if(deliveryInfos!= undefined){
						for(var i=0,len=deliveryInfos.length;i<len;i++){
							content+="<li class='recharge_operation_li2'>" +
								"<ul>" +
								"<li>商品编号：</li>" +
								"<li>"+deliveryInfos[i].csku+"</li>" +
								"</ul>" +
								"<ul>" +
								"<li>物流公司：</li>" +
								"<li>"+deliveryInfos[i].cshippingtype+"</li>" +
								"</ul></li>" +
								"<li class='recharge_operation_li2'>" +
								"<ul>" +
								"<li>物流单号：</li>" +
								"<li><a href=''>"+deliveryInfos[i].ctrackingnumber+"</a></li>" +
								"</ul>" +
								"</li>";
						}
		            }
					$('.recharge_operation').find("ul.logistics_info").empty();
		        	$('.recharge_operation').find("ul.logistics_info").append(content);
				} 
			}, function(XMLHttpRequest, textStatus) {}
		);
	}
	$('.logistics_info').show();
    $('.recharge_operation').toggle();
}