var layer = undefined;
var laypage = undefined;

function init_after_sale(lay, layd) {
	layer = lay;
	laypage = layd;
	postAfterSale(null,false);
	$("#after_search_box").on("change", "#after_sale_type", function (){
    	postAfterSale(null,false);
	}); 
	$("#after_search_box").on("click", ".searchButton", function (){
    	postAfterSale(null,false);
	}); 
	$(".searchInput").keydown(function (e) {
        if(e.keyCode == 13){
            postAfterSale(null,false);
        }
    }); 
}


// 初始化客户售后单分页
function init_after_sale_pagination(pages, currPage) {
	if ($("#after_sale_pagination")[0] != undefined) {
		$("#after_sale_pagination").empty();
		laypage({
			cont: 'after_sale_pagination',
			pages: pages,
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
				if (!first) {
					 postAfterSale(obj, true);
				}
			}
		});
	}
}
//获取审核状态描述
function getStatusStr(status){
    var stav = status == 1?"待审核":
            status == 2?"审核通过":
                status == 3?"审核拒绝":
                    status == 4?"待用户发货":
                        status == 5?"售后已完成":
                            status == 6?"已退款":
								status == 7?"待平台收货":"审核状态异常";
    return  stav;           
}
// 获取售后类型描述
function getAfterSaleTypeStr(type){
	var afterSaleType = type == 1?"退货":
							type == 2?"退款":"售后类型异常";
	return 	afterSaleType;						
}
function postAfterSale(obj,red){
	isaulogin(function(email) {
		var pageCount = "1";
		var pageSize = $("#afterSale_pageSize").val();
		if (pageSize == undefined || pageSize == "" || pageSize == 0) {
			pageSize = 10;
		}
		if (red) {
			pageCount = obj.curr;
		}
		var applyDateStart = $("#seachTime0").val();
		var applyDateEnd = $("#seachTime1").val();
		var seachflag = $("#searchInput").val().trim();
		var afterSaleType = $("#after_sale_type").val();
		var param = {
			seachFlag: seachflag,
			pageSize: pageSize,
			currPage: pageCount,
			applyDateStart: applyDateStart,
			applyDateEnd: applyDateEnd,
			afterSaleType: afterSaleType
		};
		ajax_post(
			"/sales/queryAfterSale",
			JSON.stringify(param),
			"application/json",
			function(response) {
				$("#afterSale_pageSize").change(function() {
					postAfterSale(null, false);
				});
				var datas = response.datas;
				var total = response.totalCount;
				var currPage = response.currPage;
				var totalPage = response.totalPage;
				$("#afterSaleTotal").text(total);
				$("#afterSalePages").text(totalPage);
				$("#sublit_detail").empty();
				if(total > 0){
					for(var i = 0 ;i < datas.length; i++){
						var main = datas[i].afterSalesOrderMain;
				        var mainHtml  = '<ul class="list_message" id="list_message_'+main.id+'">'+
			        						'<li style="width:20%">'+main.afterSaleOrderNo+'</li>'+
								            '<li style="width:20%">'+deal_with_illegal_value(main.createDateStr)+'</li>'+
								            '<li style="width:20%">'+main.saleOrderNo+'</li>'+
								            '<li style="width:20%">'+deal_with_illegal_value(main.orderingDateStr)+'</li>'+
								            '<li style="width:20%">'+deal_with_illegal_value(main.email)+'</li>'+
									    '</ul>'+
									    '<div class="aftersales-detail hide_after_sales">'+
									    '<div class="product-information " style = "border-bottom: 1px dashed #8C8C8C;padding-bottom: 20px" >'+
		    							'<h3>商品信息</h3>'+
						                '<table id = "aftersales-detail_'+main.id+'">'+
						                    '<tr>'+
						                        '<td style="width:9%">商品编号</td>'+
						                        '<td style="width:20%">商品名称</td>'+
						                        '<td style="width:9%">分销商微仓</td>'+
						                        '<td style="width:9%">采购价格</td>'+
						                        '<td style="width:9%">商品数量</td>'+
						                        '<td style="width:9%">金额小计</td>'+
						                        '<td style="width:7%">售后类型</td>'+
						                        '<td style="width:8%">当前状态</td>'+
						                        '<td style="width:20%">相关操作</td>'+
						                    '</tr>'+
								            '</table>'+
							                '<p>申请备注：<span>'+deal_with_illegal_value(main.remark)+'</span></p>'+
							            '</div>'
							            '</div>';
						$("#sublit_detail").append(mainHtml);
						var details = datas[i].afterSalesOrderDetails;
						var detailHtml = "";
						if(details.length > 0){
							for(var j = 0;j < details.length; j++){
								var detail = details[j];
								var type = detail.afterSaleType;
							    var afterSaleType = getAfterSaleTypeStr(type);
							    var status = detail.status; 
							    var stav = getStatusStr(status);			
							   detailHtml += 
				                    '<tr>'+
				                       '<td style="width:9%">'+detail.sku+'</td>'+
				                        '<td style="width:20%">'+deal_with_illegal_value(detail.productName)+'</td>'+
				                        '<td style="width:9%">'+detail.warehouseName+'</td>'+
				                        '<td style="width:9%">'+parseFloat(detail.purchasePrice).toFixed(2)+'</td>'+
				                        '<td style="width:9%">'+detail.qty+'</td>'+
				                        '<td style="width:9%">'+parseFloat(parseInt(detail.qty)*parseFloat(detail.purchasePrice)).toFixed(2)+'</td>'+
				                        '<td style="width:7%" id = "afterSaleType" data-type = "'+type+'">'+afterSaleType+'</td>'+
				                        '<td style="width:8%">'+stav+'</td>'+
				                        '<td style="width:20%">'+ 
				                            (status == 1?'<span class="aftersales-audit" tag = "'+detail.id+'"  onclick = "after_operationShow(this,\'Ushow\')">审核</span>':'')+
				                            '<span class="aftersales-log" tag = "'+detail.id+'" onclick = "operateInfoShow(this)">查看日志</span>'+
				                        '</td>'+
				                    '</tr>';
								}
							}
							var id ="#aftersales-detail_"+main.id; 
					        $(id).append(detailHtml);
						}		
					}else{
						layer.msg("没有符合条件的售后订单",{icon:5,time:2000});
					}
				init_after_sale_pagination(totalPage,currPage);

			},
			function(XMLHttpRequest, textStatus) {
				layer.msg("获取售后订单失败！",{icon:2,time:2000});
			});
	});
}

 //详情展示
function AfterDetail(param, e) {
    if (param == 1) {
        $(e).css("display", "none");
        $(e).next().css("display", "inline-block");
        $(e).parent().parent().next().css("display", "inline-block");
    } else {
        $(e).css("display", "none");
        $(e).prev().css("display", "inline-block");
        $(e).parent().parent().next().css("display", "none");
    }

}
// 后期改动一条售后单 维护一个商品，隐藏多余，全部显示，暂留，以待业务变动
// '<li class="add_list_down" style="width:6%"  >'+
//     '<span onclick="AfterDetail(1,this)">+</span>'+
//     '<span onclick="AfterDetail(2,this)" id="span_minus" style="display: none">-</span>'+
// '</li>'+

var tag = undefined;
//售后申请审核
function after_operationShow(obj, check) {
	var t = $(obj).attr("tag");
	var state = $(obj).attr("state");
	
	var htmls = "<div class='recharge_check' style='display: block;'>"
		+"<ul><li class='check_all' style='display: block;margin-top: 40px;'>"
		+"<ul><li>审核之前请确认商品信息无误！</li>"
		+"<li>"
		+"<input type='button' value='审核不通过' onclick=\"after_operationShow(this,'fail')\">"
		+"<input type='button' value='审核通过' onclick=\"after_operationShow(this,'suc')\">"
		+"</li></ul></li>"
		+"<li class='aftersale_check_fail check_pages' style='display: none;'>"
		+"<ul><li><span>审核备注：</span></li>"
		+"<li>"
		+"<textarea name='' cols='30' rows='10'></textarea>"
		+"</li></ul>"
		+"<div class='audit_'><input type='hidden' value='3'><input type='button' value='提交' atr='modify' style='cursor: pointer;'></div></li>"
		+"<li class='aftersale_check_suc check_pages' style='display: none;'>"
		+"<ul><li><span>审核备注：</span></li>"
		+"<li>"
		+"<textarea name='' cols='30' rows='10'></textarea>"
		+"</li></ul>"
		+"<div class='audit_'><input type='hidden' value='2'><input type='button' value='提交' atr='modify' style='cursor: pointer;'></div></li>"
		+"</div>";
	if(t){
		tag = t;
	}
	if (check == 'Ushow') {
		layer.open({
				type:1,
				title:'审核',
				btn:false,
				shadeClose: true,
				content:htmls,
				area:['308px','270px'],
				move: false,
				success:function(i, currdom){
					//事件绑定
					$("#operate input[atr='close']").click(function(){
						$(".check_suc").hide();
					});
					//事件绑定
					$("div.audit_ input[atr='modify']").click(function(){
						var status = parseInt($(this).prev().val());
						var remark =  $(this).parent().prev().find("li textarea").val().trim();	
						isaulogin(function(email) {
							var params = {
								id : tag,
								status : status,
								remark : remark,
								email: email
							};
							postModifyDetail(obj,i,params);
						});	
					});
				}
		});
	}else if(check == 'Uhide') {
		$(".recharge_check").hide();
		$('.check_pages').hide();
		$('.check_all').hide();
		$('.recharge_black').hide();
	}else if (check == 'fail') {
		$(obj).parent().parent().parent().hide();
		$(".aftersale_check_fail").show();
	}else if (check == 'suc') {
		$(obj).parent().parent().parent().hide();
		$(".aftersale_check_suc").show();
	}
	
}
//审核
function postModifyDetail(node,index,data){
	var status = data.status;
	var td = $(node).parent();
	ajax_post("/sales/auditAfterSaleDetail", JSON.stringify(data), "application/json",
		function(response) {
			if(response){
				var stav = getStatusStr(status);	 
				td.siblings().eq(7).text(stav);
				layer.msg("审核成功！", {icon: 6, time: 2000});
				$(node).remove();
			}else{
				layer.msg("审核失败！", {icon: 5, time: 2000});
			}
			//关闭当前弹出层
			index.remove();
			// 审核通过：选择是否需要寄回商品
			if(status == 2){
				var params ={
					id : data.id,
					email : data.email
				};
				sendProduct(params,td);
			}

		},
		function(xhr, status) {
			layer.msg("审核异常，请稍后重试！", {icon: 2, time: 2000});
		}
	);
	$('.layui-layer-shade').click();
}
//显示操作记录
function operateInfoShow(obj){
	var id = $(obj).attr("tag");
	var htmlCode;
	var params = {
		detailId : id
	};
	ajax_post("/sales/getAfterSaleORcd",JSON.stringify(params),"application/json",
		function(data){
			data && data.length > 0?htmlCode = showOPHTML(data):htmlCode = "<span style='text-align: center;line-height: 190px '>暂无操作记录！</span>";
			layer.open({
				type: 1,
				title: '操作记录',
				shadeClose: true,
				content: htmlCode,
				move: false,
				area: ['410px','250px']
			});
		},
		function(xhr, status){

		}
	);
}

function showOPHTML(data){
	var htmlCode = "";
	$.each(data, function (i,item) {
		htmlCode += "<div class='aftersales-log-lit' >"+
						"<ul class = 'aftersales_lit' style = 'width:400px'>" +
							"<li style = 'margin-right:5px'>"+item.operateTimeStr+"</li>" +
							"<li style = 'margin-right:5px'>"+item.email+"设置售后单状态为："+item.operateStr+"</li>" +
						"</ul>"+
					"</div>" ;
	});
	return htmlCode;
}
function sendProduct(data,node){
	layer.confirm(
	'是否需要寄回商品?',
	{
	icon: 3,
	title:'审核通过',
	btn : ['是', '否'],
	closeBtn : 0,
	yes : function(index){
		// 状态4 ：待用户发货
		var status = 4;
		data.status = status;
		ajax_post("/sales/auditAfterSaleDetail", JSON.stringify(data), "application/json",
		function(response) {
			if(response){
				var stav = getStatusStr(status);	 
				node.siblings().eq(7).text(stav);
				layer.msg("审核成功！", {icon: 6, time: 2000});
			}else{
				layer.msg("审核失败！", {icon: 5, time: 2000});
			}
		},
		function(xhr, status) {
			layer.msg("审核异常，请稍后重试！", {icon: 2, time: 2000});
		});
        layer.close(index);
	},
	cancel : function(index){ 
		// 状态5 ：售后已完成 
		var type = node.parent().find("#afterSaleType").data("type");
		var status = 5;
		if(type == 2){
			// 状态6 ：已退款 
			status = 6;
		}
		data.status = status;
		ajax_post("/sales/auditAfterSaleDetail", JSON.stringify(data), "application/json",
		function(response) {
			if(response){
				var stav = getStatusStr(status);	 
				node.siblings().eq(7).text(stav);
				layer.msg("审核成功！", {icon: 6, time: 2000});
			}else{
				layer.msg("审核失败！", {icon: 5, time: 2000});
			}
		},
		function(xhr, status) {
			layer.msg("审核异常，请稍后重试！", {icon: 2, time: 2000});
		});
        layer.close(index);
	}	
	}
);
}
