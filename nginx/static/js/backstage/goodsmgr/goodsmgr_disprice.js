var laypage = undefined, discount = 0;
function initialize_disprice(lp) {
	laypage = lp;
}

//启用和禁用"选中符合搜索/筛选条件的所有商品"复选框
function check_checkbox_use() {
	var cate = $(".dis-price-two #product_category_select").val();
	var wareh = $(".dis-price-two #product_warehouse_select").val();
	var search = $(".dis-price-two #product_search_input").val().trim();
	var node = $(".dis-price-two #choose_search_all")[0];
	if (cate == "" && wareh == "" && search == "" && node.checked) {
		$(node).css("cursor", "not-allowed");
		$(node).attr("disabled", "disabled");
		node.checked = false;
	} else {
		$(node).css("cursor", "default");
		$(node).removeAttr("disabled");
	}
}

function bind_disprice_element_event() {
	//主页面"查看导出明细表"按钮
	$("#look_export_detail_btn").click(function(event) {
		$('.label-change').text("报价单导出明细");
		$('.dis-price-one').hide();
		$('.check-derive-list').hide();
		$('.go-back').show();
		$('.derive-list').show(function() {
			gain_export_detail_list_data(1);
		});
	});

	//主页面"设置利润率折扣"输入框
	$(".dis-price-one .set-discount").blur(function(event) {
		var disc = $(".dis-price-one .set-discount").val();
		if (/(\d+)/.test(disc) && disc > 0 && disc <= 100) {
			//layer.load(1, {shade: 0.5, time: 800});
			discount = (parseFloat(disc / 100)).toFixed(4);
			var count = 0;
			$(".dis-price-one .checkbox-child").each(function(i, item) {
		        count++;
		    });
		    if (count > 0) {
		    	var pro_text = $(".dis-price-one #selected_product_table_datum").val();
				var prolist = pro_text == "" ? [] : JSON.parse(pro_text);
		    	insert_product_list_data_one(prolist);
		    }
		} else {
			layer.msg("请输入0-100之间的数字！", {icon: 0, time: 1000});
		}
	});

	//主页面"添加商品"按钮
	$(".dis-price-one #add_product_btn").click(function(event) {
		var disc = $(".dis-price-one .set-discount").val();
		if (/^(\d+)$/.test(disc) && disc > 0 && disc <= 100) {
			//禁用"选中符合搜索/筛选条件的所有商品"复选框
			$(".dis-price-two #choose_search_all").css("cursor", "not-allowed");
			$(".dis-price-two #choose_search_all").attr("disabled", "disabled");
			$(".dis-price-two #choose_search_all")[0].checked = false;

			$('.dis-price-one').hide();
			$("#look_export_detail_btn").hide();
			$('.dis-price-two').show(function() {
				discount = (parseFloat(disc / 100)).toFixed(4);
				console.log(discount + "<---discount	type--->" + typeof discount);
				//初始化下拉选和商品列表数据
				gain_product_list_data_two(1);
			    init_product_category_select();
			    init_product_warehouse_select();
			});
		} else {
			layer.msg("请设置合法的利润率折扣(0-100之间的数字)", {icon: 0, time: 2000});
		}
	});
	
	//主页面"删除"按钮
	$(".dis-price-one #delete_product_btn").click(function(event) {
		var count = 0;
		$(".dis-price-one .checkbox-child").each(function(i, item) {
			count += item.checked ? 1 : 0;
		});
		if (count > 0) {
			layer.confirm("确认删除选中项？", {icon: 3, title: "提示"}, function(index) {
				layer.close(index);
				var prolist = JSON.parse($(".dis-price-one #selected_product_table_datum").val());
				$(".dis-price-one .checkbox-child").each(function(n, node) {
			        if (node.checked) {
			        	var m;
			        	$.each(prolist, function(i, it) {
			        		if ($(node).attr("value") == it.iid) {
			        			m = i;
			        		}
			        	});
			        	prolist.splice(m, 1);
			        	$(node).parent().parent().remove();
			        }
			    });
			    $(".dis-price-one #selected_product_table_datum").val(JSON.stringify(prolist));
			    $("#selected_product_number").text(prolist.length);
			});
		} else {
			layer.msg("请选择要删除的项", {icon: 0, time: 2000});
		}
	});
	
	//主页面"清空"按钮
	$(".dis-price-one #clear_product_btn").click(function(event) {
		if ($(".dis-price-one .checkbox-child").length > 0) {
			layer.confirm("您是否要清空全部数据？", {icon: 3, title: "提示"}, function(index) {
				layer.close(index);
				var $thead = $(".dis-price-one .user_recharge").children().eq(0).children().eq(0);
				$(".dis-price-one .user_recharge").children().eq(0).empty().append($thead);
				$(".dis-price-one #selected_product_table_datum").val("");
				$("#selected_product_number").text(0);
			});
		} else {
			layer.msg("暂无可清空的项", {icon: 0, time: 2000});
		}
	});
	
	//主页面"导出Excel"按钮
	$(".dis-price-one #export_product_btn").click(function(event) {
		if ($(".dis-price-one .checkbox-child").length > 0) {
			layer.open({
	            type: 1,
	            title: "导出文件名",
	            content: 
	            	"<div class='relevance-pop' id='export_excel_file_name'>" +
		            	"<ul>" +
			            	"<li>Excel名称：<input type='text'  class='relevance-pop-id'/></li>" +
		            	"</ul>" +
				    "</div>",
	            area: ['340px', '200px'],
	            btn: ["保存", "取消"],
	            closeBtn: 1,
	            shadeClose: false,
	            yes: function(index, currdom) {
	                console.log(index + "<--index	currdom-->" + currdom[0]);
	                var excelName = $("#export_excel_file_name").find("input").val();
	                var header = new Array();
					var table_theahs = $(".dis-price-one .user_recharge_header").children();
					$.each(table_theahs, function(i, item) {
						var name = $(item).attr("name");
						if (name != "") {
							header.push(name);
						}
					});
					var iidList = new Array();
					var skuawhid = new Array();
					var prolist = JSON.parse($(".dis-price-one #selected_product_table_datum").val());
					/*$(".dis-price-one .checkbox-child").each(function(i, item) {
				        iidList.push($(item).attr("value"));
				    });*/
					$.each(prolist, function(n, node) {
						iidList.push(node.iid);
						skuawhid.push({
							"sku": node.csku,
							"warehouseId": node.warehouseId,
							"qty":node.qty
						});
					});
				    var params = {
				    	header: header,
				    	iidList: iidList,
				    	discountRate: discount,
				    	excelName: excelName,
				    	skuawhid: skuawhid
				    };
				    layer.close(index);
		            ajax_post("../purchase/savequotrecord", JSON.stringify(params), "application/json",
						function(data) {
							if (data.errorCode == "0") {
								layer.msg('即将导出Excel文件！', {icon: 1, time: 1000}, function() {
									/*var $thead = $(".dis-price-one .user_recharge").children().eq(0).children().eq(0);
									$(".dis-price-one .user_recharge").children().eq(0).empty().append($thead);
									$(".dis-price-one #selected_product_table_datum").val("");*/
									window.location.href = "/purchase/exportquotation?id=" + data.errorInfo;
								});
							} else {
								layer.msg("保存名称失败，请稍后重试！", {icon: 2, time: 2000});
							}
						},
						function(xhr, status) { console.log("error--->" + status); }
					);
	            }
	        });
		} else {
			layer.msg("暂无可导出的项，请添加商品！", {icon: 0, time: 2000});
		}
	});

	//选择商品页面"返回"按钮
	$("#return_disprice_bill_export_btn").click(function(event) {
		$('.label-change').text("报价单导出");
		$('.dis-price-one').show();
		$("#look_export_detail_btn").show();
		$('.dis-price-two').hide();
	});
										
	//选择商品页面"选中符合搜索/筛选条件的所有商品"复选框
	$(".dis-price-two #choose_search_all").click(function(event) {
		check_checkbox_use();
	});
	
	//选择商品页面"商品分类"下拉选
	$(".dis-price-two #product_category_select").change(function(event) {
		gain_product_list_data_two(1);
		check_checkbox_use();
	});
	
	//选择商品页面"所属仓库"下拉选
	$(".dis-price-two #product_warehouse_select").change(function(event) {
		gain_product_list_data_two(1);
		check_checkbox_use();
	});
	
	//选择商品页面"搜索"按钮
	$(".dis-price-two #product_search_btn").click(function(event) {
		gain_product_list_data_two(1);
		check_checkbox_use();
	});
	
	//选择商品页面"每页显示数"下拉选
	$(".dis-price-two .list_pagesize").change(function(event) {
		gain_product_list_data_two(1);
	});
	
	//选择商品页面"添加到列表"按钮
	$(".dis-price-two #save_selected_product").click(function(event) {
		var isAll = $(".dis-price-two #choose_search_all")[0].checked;
		var list = new Array();
		if (isAll) {
			layer.confirm("您已选中符合搜索/筛选条件的所有商品，确认添加吗？", {icon: 3, title: "提示"}, function(index) {
				var params = {
					data: {
						categoryId: $(".dis-price-two #product_category_select").val(),
						warehouseId: $(".dis-price-two #product_warehouse_select").val(),
						title: $(".dis-price-two #product_search_input").val(),
						istatus: "1"
					}
				};
				layer.close(index);
				ajax_post("../product/api/getProducts", JSON.stringify(params), "application/json",
					function(data) {
						list = data.data.result;
						//生成报价单集合
						var build_list = new Array();
						if (list.length > 0) {
							$('.dis-price-one').show();
							$("#look_export_detail_btn").show();
			        		$('.dis-price-two').hide();
							var option = new Array();
							$.each(list,function(index,product){
								//设置起批量
								product.qty = product.batchNumber?product.batchNumber:1;
								if(product.stock > 0){
									build_list.push(product);
								}
							});
				    		insert_product_list_data_one(build_list);
						} else {
							layer.alert("当前符合搜索/筛选条件的所有商品个数为0，请更换搜索/筛选条件重新查询。", {icon: 0});
						}
					},
					function(xhr, status) { console.log("error--->" + status); }
				);
			});
		} else {
			var selected_option = new Array();
			$(".dis-price-two .checkbox-child").each(function(i, item) {
		        if (item.checked) {
		        	selected_option.push({
		        		"sku": $(item).data("sku"),
		        		"whid": $(item).data("whid"),
						"qty": $(item).parent().siblings().find("input.numToSend").val()
		        	});
		        }
		    });
		    if (selected_option.length > 0) {
		    	layer.confirm("确认添加选中项？", {icon: 3, title: "提示"}, function(index) {
		    		layer.close(index);
		    		$('.dis-price-one').show();
		    		$("#look_export_detail_btn").show();
	        		$('.dis-price-two').hide();
	        		//获取临时待选商品数据
					var pro_text = $(".dis-price-two #all_product_table_datum").val();
					var prolist = pro_text == "" ? [] : JSON.parse(pro_text);
					$.each(selected_option, function(i, item) {
						$.each(prolist, function(n, node) {
							if (item.sku == node.csku && item.whid == node.warehouseId) {
								node.qty = item.qty;
								list.push(node);
							}
						});
					});
		    		insert_product_list_data_one(list);
		    	});
		    } else {
		    	layer.msg("请选择要添加的项", {icon: 0, time: 2000});
		    }
		}
	});

	
	//导出明细页面"返回"按钮
	$("#go_back_disprice_bill_export_btn").click(function(event) {
		$('.label-change').text("报价单导出");
		$('.dis-price-one').show();
		$('.check-derive-list').show();
		$('.go-back').hide();
		$('.derive-list').hide();
	});
	
	//导出明细页面"搜索"按钮
	$(".derive-list .excel-search").click(function(event) {
		gain_export_detail_list_data(1);
	});
	
	//导出明细页面"每页显示数"下拉选
	$(".derive-list .list_pagesize").change(function(event) {
		gain_export_detail_list_data(1);
	});
	
	//导出明细页面关联分销商弹出框
	$('.distributor-relevance-pop .distributor-search').find("span").click(function(event) {
		gain_distribution_list_data(1);
	});
}

//初始化选择商品页面"商品分类"下拉选
function init_product_category_select() {
	ajax_get("../product/api/realCateQuery?level=1", "", "",
		function(data) {
			var optionHTML = '<option value="">所有商品</option>';
			$.each(data, function(i, item) {
				optionHTML += '<option value="' + item.iid + '">' + item.cname + '</option>';
			});
			$(".dis-price-two #product_category_select").empty().append(optionHTML);
		},
		function(xhr, status) { console.log("error--->" + status); }
	);
}

//初始化选择商品页面"所属仓库"下拉选
function init_product_warehouse_select() {
	ajax_get("/inventory/queryWarehouse?" + Math.random(), "", "",
		function(data) {
			if (data.length > 0) {
				var itemHTML = '<option value="">所有仓库</option>';
				$.each(data, function(i, item) {
					itemHTML += '<option value="' + item.id + '">' + item.warehouseName + '</option>';
				});
				$(".dis-price-two #product_warehouse_select").empty().append(itemHTML);
			} else {
				layer.msg("获取仓库信息失败", {icon: 2, time: 2000});
			}
		},
		function(xhr, status) { console.log("error--->" + status); }
	);
}

//选择商品页面获取商品列表数据
function gain_product_list_data_two(curr) {
	var params = {
		data: {
			currPage: curr == undefined || curr == 0 ? 1 : curr,
			pageSize: $(".dis-price-two .list_pagesize").val(),
			categoryId: $(".dis-price-two #product_category_select").val(),
			warehouseId: $(".dis-price-two #product_warehouse_select").val(),
			title: $(".dis-price-two #product_search_input").val(),
			istatus: "1"
		}
	};
	ajax_post("../product/api/getProducts", JSON.stringify(params), "application/json",
		function(data) {
			insert_product_list_data_two(data.data.result);
			init_product_list_two_pagination(data.data);
			var rows = deal_with_illegal_value(data.data.rows);
			$(".dis-price-two .total_record").text(rows == "---" ? "0" : rows);
			var pages = deal_with_illegal_value(data.data.totalPage);
			$(".dis-price-two .total_pages").text(pages == "---" ? "0" : pages);
		},
		function(xhr, status) { console.log("error--->" + status); }
	);
}

//选择商品页面插入商品列表数据
function insert_product_list_data_two(list) {
	//获取临时已选商品数据
	var pro_text = $(".dis-price-one #selected_product_table_datum").val();
	var prolist = pro_text == "" ? [] : JSON.parse(pro_text);
	
	var $thead = $(".dis-price-two .user_recharge").children().eq(0).children().eq(0);
	var itemHTML = '', rebate = parseFloat(discount);
	$.each(list, function(i, item) {
		//已添加过的商品自动选中，即相同SKU和相同仓库的商品
		var issame = false;
		$.each(prolist, function(j, elem) {
			if (elem.csku == item.csku && elem.warehouseId == item.warehouseId) {
				issame = true;
			}
		});
		
		var after_disprice = 0;
		var costprice = item.disTotalCost!=null ? parseFloat(item.disTotalCost) : 0;
		var disProfitRate = parseFloat(item.disProfitRate);
		if (item.disProfitRate == 0 || item.disProfitRate == "" ||
			item.disProfitRate == null || item.disProfitRate == undefined) {
			//不打折
			after_disprice = item.disPrice;
		} else {
			//打折
			after_disprice = (costprice / (1 - disProfitRate * rebate)).toFixed(2);
		}
		//对于深圳仓的产品采购价需要减去物流费
		var disFreight = (item.disFreight==null||item.disFreight==undefined||item.disFreight=='null')?0:item.disFreight;
		if(item.warehouseId == 2024 || item.warehouseId == '2024'){
			after_disprice = parseFloat(after_disprice - disFreight).toFixed(2);
		}
		var  batchNum = item.batchNumber;
		itemHTML +=
			'<tr class="user_recharge_tr">' +
				'<td width="7%">' +
					'<input type="checkbox" '+(item.stock <= 0?"disabled = 'disabled'":"")+' class="checkbox-child" data-sku="' + item.csku + '" data-whid="' + item.warehouseId + '" ' + (issame ? 'checked="checked"' : '') + '/>' +
				'</td>' +
				'<td width="10%">' + deal_with_illegal_value(item.csku) + '</td>' +
				'<td width="23%" style="text-align: left;">' + deal_with_illegal_value(item.ctitle) + '</td>' +
				'<td width="10%">' + deal_with_illegal_value(item.cname) + '</td>' +
				'<td width="10%">' + deal_with_illegal_value(item.warehouseName) + '</td>' +
				'<td width="8%">' + deal_with_illegal_value(item.stock) + '</td>' +
				"<td class='10%'><a><span class='editNum minus'>-</span></a><input  onkeyup='iptChange(this)'  type='text' class='numToSend'  batchNum = "+batchNum+" value='" + (batchNum?batchNum:1) + "' data-qty = '"+(batchNum?batchNum:1) +"' maxQty='" + item.stock + "'><a><span class='editNum plus'>+</span></a></td>" +
				'<td width="8%">' + deal_with_illegal_value(item.disTotalCost) + '</td>' +
				'<td width="8%" style = "color:red" >' + deal_with_illegal_value(after_disprice) + '</td>' +
				'<td width="8%">' + deal_with_illegal_value(item.localPrice) + '</td>' +
			'</tr>';
	});
	$(".dis-price-two .user_recharge").children().eq(0).empty().append($thead).append(itemHTML);
	//临时存放待选商品数据
	$(".dis-price-two #all_product_table_datum").val(JSON.stringify(list));
	//选择商品页面"全选"复选框
	$(".dis-price-two .choose_all_input").click(function(event) {
		var node = this;
	    $(".dis-price-two .checkbox-child").each(function(i, item) {
	    	if(!$(item).attr("disabled")){
	    		item.checked = node.checked;
	    	}
	    });
	});
	$("span.editNum").click(function() {
		count_num($(this));
	});
}

//商品数量变化时进行判断
function iptChange(obj){
	var quantity = $(obj).val(), sourceQty = parseInt($(obj).data("qty")),maxQty = parseInt($(obj).attr("maxQty"));
	var batchNum = parseInt($(obj).attr("batchNum"));
	if (!/^[1-9]\d*$/.test(quantity)) {
        layer.msg("请输入数字(必须是大于0的整数)", {icon: 2, time: 2000});
        $(obj).val(sourceQty);
        return;
    }
    if(quantity < batchNum){
    	layer.msg("采购数量必须大于或者等于商品起批量【" + batchNum + "】", {icon: 6}, function (index) {
	        layer.close(index);
	    });
	    $(obj).val(batchNum); 
	    return;
    }
    if(quantity > maxQty){
        layer.msg("数量不能超过云仓库存(" +maxQty + ")", {icon: 2, time: 2000});
        $(obj).val(sourceQty);
        return;
    }
    $(obj).data("qty",quantity);
}

//选择商品页面初始化商品数据列表分页栏
function init_product_list_two_pagination(page) {
    if ($("#product_two_pagination")[0] != undefined) {
        $("#product_two_pagination").empty();
        laypage({
            cont: 'product_two_pagination',
            pages: page.totalPage,
            curr: page.currPage,
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
                    gain_product_list_data_two(obj.curr);
                }
            }
        });
    }
}

//主页面插入商品列表数据
function insert_product_list_data_one(list) {
	var $thead = $(".dis-price-one .user_recharge").children().eq(0).children().eq(0);
	
	var pro_text = $(".dis-price-one #selected_product_table_datum").val();
	prolist = pro_text == "" ? [] : JSON.parse(pro_text);
	//新添加的商品要加上已经添加的，并且去除重复
	$.each(list, function(i, item) {
		var is_repeat = false;
		$.each(prolist, function(n, node) {
			if (node.iid == item.iid) {
				//重复的需要更新数量
				node.qty = item.qty;
				is_repeat = true;
			}
		});
		if (!is_repeat) {
			prolist.push(item);
		}
	});
	$("#selected_product_number").text(prolist.length);
	
	var itemHTML = '', rebate = parseFloat(discount);
	$.each(prolist, function(i, item) {
		var after_disprice = 0;
		// var costprice = parseFloat(item.disTotalCost);
		var costprice = item.disTotalCost!=null ? parseFloat(item.disTotalCost) : 0;
		var disProfitRate = parseFloat(item.disProfitRate);
		if (item.disProfitRate == 0 || item.disProfitRate == "" ||
			item.disProfitRate == null || item.disProfitRate == undefined) {
			//不打折
			after_disprice = item.disPrice;
		} else {
			//打折
			after_disprice = (costprice / (1 - disProfitRate * rebate)).toFixed(2);
		}
		//对于深圳仓的产品采购价需要减去物流费
		var disFreight = (item.disFreight==null||item.disFreight==undefined||item.disFreight=='null')?0:item.disFreight;
		if(item.warehouseId == 2024 || item.warehouseId == '2024'){
			after_disprice = parseFloat(after_disprice - disFreight).toFixed(2);
		}
		
		itemHTML +=
			'<tr class="user_recharge_tr" tag="' + item.iid + '">' +
				'<td width="3%">' +
					'<input type="checkbox" class="checkbox-child" value="' + item.iid + '"/>' +
				'</td>' +
				'<td width="5%">' + deal_with_illegal_value(item.cname) + '</td>' +
				'<td width="5%">' + deal_with_illegal_value(item.brand) + '</td>' +
				'<td width="4%">' + deal_with_illegal_value(item.csku) + '</td>' +
				'<td width="7%">' + deal_with_illegal_value(item.interBarCode) + '</td>' +
				'<td width="9%">' + deal_with_illegal_value(item.ctitle) + '</td>' +
				'<td width="4%">' + deal_with_illegal_value(item.disTotalCost) + '</td>' +
				'<td width="4%" style = "color:red" >' + deal_with_illegal_value(after_disprice) + '</td>' +
				'<td width="4%" name="qty">' + deal_with_illegal_value(item.qty) + '</td>' +
				'<td width="4%">' + deal_with_illegal_value(item.localPrice) + '</td>' +
				'<td width="3%">' + deal_with_illegal_value(item.batchNumber) + '</td>' +
				'<td width="3%">' + deal_with_illegal_value(item.packageType) + '</td>' +
				'<td width="5%">' + deal_with_illegal_value(item.originCountry) + '</td>' +
				'<td width="5%">' + deal_with_illegal_value(item.plugType) + '</td>' +
				'<td width="5%">' + deal_with_illegal_value(item.warehouseName) + '</td>' +
				'<td width="7%">' + deal_with_illegal_value(item.productEnterprise) + '</td>' +
				'<td width="9%">' + deal_with_illegal_value(item.componentContent) + '</td>' +
				//保质期（月）
				'<td width="4%">' + deal_with_illegal_value(item.expirationDays?parseInt(parseInt(item.expirationDays)/30):undefined) + '</td>' +
				'<td width="4%">' + deal_with_illegal_value(item.stock) + '</td>' +
				'<td width="4%">' + deal_with_illegal_value(item.packQty) + '</td>' +
			'</tr>';
	});
	//临时存放已选商品数据
	$(".dis-price-one #selected_product_table_datum").val(JSON.stringify(prolist));
	//插入html
	$(".dis-price-one .user_recharge").children().eq(0).empty().append($thead).append(itemHTML);
	//主页面"全选"复选框
	$(".dis-price-one .choose_all_input").click(function(event) {
		var node = this;
	    $(".dis-price-one .checkbox-child").each(function(i, item) {
	        item.checked = node.checked;
	    });
	});
}

function count_num(srcEle) {
	var qty,batchNum;
	if (srcEle.hasClass("minus")) {
		qty = srcEle.parent().next();
		batchNum = parseInt(qty.attr("batchNum"));
		if (qty.val() > batchNum) {
			qty.val(parseInt(qty.val()) - 1);
		}else{
			layer.msg("采购数量必须大于或者等于商品起批量【" + batchNum + "】", {icon: 6}, function (index) {
                layer.close(index);
            });
		}
	}
	if (srcEle.hasClass("plus")) {
		qty = srcEle.parent().prev();
		if (parseInt(qty.val()) + 1 <= qty.attr("maxQty")) {
			qty.val(parseInt(qty.val()) + 1);
		}else{
			layer.msg("采购数量不能大于云仓库存【" +qty.attr("maxQty")+ "】", {icon: 6}, function (index) {
                layer.close(index);
            });
		}
	}
}

//导出明细页面获取导出明细列表数据
function gain_export_detail_list_data(curr) {
	var params = {
		currPage: curr == undefined || curr == 0 ? 1 : curr,
		pageSize: parseInt($(".derive-list .list_pagesize").val()),
		excelName: $(".derive-list .excel-name").val(),
		disEmail: $(".derive-list .distributor-id").val(),
		madeUser: $(".derive-list .distributor-producer").val(),
		begainDate: $(".derive-list #seachTime0").val(),
		endDate: $(".derive-list #seachTime1").val()
	};
	ajax_post("../purchase/gainquotrecord", JSON.stringify(params), "application/json",
		function(data) {
			if (data.returnMess.errorCode == "0") {
				insert_export_detail_list_data(data.quos);
				var page = {
					currPage: params.currPage,
					totalPage: (data.totalCount % params.pageSize == 0) ? (data.totalCount / params.pageSize) : (parseInt(data.totalCount / params.pageSize) + 1)
				};
				var rows = deal_with_illegal_value(data.totalCount);
				$(".derive-list .total_record").text(rows == "---" ? "0" : rows);
				var pages = deal_with_illegal_value(page.totalPage);
				$(".derive-list .total_pages").text(pages == "---" ? "0" : pages);
				init_export_detail_list_pagination(page);
			} else {
				layer.msg('获取报价单导出明细数据失败，请稍后重试！', {icon: 2, time: 2000});
			}
		},
		function(xhr, status) { console.log("error--->" + status); }
	);
}

//导出明细页面插入导出明细列表数据
function insert_export_detail_list_data(list) {
	var $thead = $(".derive-list .excel-list").children().eq(0).children().eq(0);
	var itemHTML = '';
	$.each(list, function(i, item) {
		itemHTML +=
			'<tr class="excel-list-tr" data-id="' + item.id + '">' +
				'<td style="min-width: 8%">' + deal_with_illegal_value(item.id) + '</td>' +
				'<td style="width: 7%">' + deal_with_illegal_value((parseFloat(item.discountRate) * 100).toFixed(0)) + '%</td>' +
				'<td style="width: 16%">' + deal_with_illegal_value(item.excelName) + '</td>' +
				'<td style="width: 16%">' + deal_with_illegal_value(item.disEmail) + '</td>' +
				'<td style="width: 11%">' + deal_with_illegal_value(item.createDateStr) + '</td>' +
				'<td style="width: 9%">' + deal_with_illegal_value(item.madeUser) + '</td>' +
				'<td style="width: 8%">' + (item.isBuildOrder ? '已' : '未') + '生成</td>' +
				'<td style="width: 22%">' +
					'<ul class="excel-btn">' +
						'<li><a href="javascript:;" class="download_export_excel">下载Excel</a></li>' +
						(item.disEmail == null || item.disEmail == '' ?
						'<li><a href="javascript:;" class="correlate_export_record">关联分销商</a></li>' :
						'<li><a href="javascript:;" class="uncorrelate_export_record">取消关联</a></li>'
						) +
						'<li><a href="javascript:;" class="copy_export_record">复制报价单</a></li>' +
					'</ul>' +
				'</td>' +
			'</tr>';
	});
	$(".derive-list .excel-list").children().eq(0).empty().append($thead).append(itemHTML);
	bind_export_detail_list_operate_event();
}

//导出明细页面初始化导出明细列表分页栏
function init_export_detail_list_pagination(page) {
	if ($("#export_detail_pagination")[0] != undefined) {
        $("#export_detail_pagination").empty();
        laypage({
            cont: 'export_detail_pagination',
            pages: page.totalPage,
            curr: page.currPage,
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
                    gain_export_detail_list_data(obj.curr);
                }
            }
        });
    }
}

//导出明细页面导出明细列表操作事件
function bind_export_detail_list_operate_event() {
	//导出明细页面导出明细列表复制报价单
	$('.copy_export_record').click(function() {
		var id = $(this).parent().parent().parent().parent().data("id");
		layer.open({
	        type: 1,
	        title: "复制报价单",
	        content: 
	        	"<div class='relevance-pop' id='export_excel_file_name'>" +
	            	"<ul>" +
		            	"<li>Excel名称：<input type='text'  class='relevance-pop-id'/></li>" +
	            	"</ul>" +
			    "</div>",
	        area: ['340px', '200px'],
	        btn: ["确定", "取消"],
	        closeBtn: 1,
	        shadeClose: false,
	        yes: function(index, currdom) {
	            console.log(index + "<--index	currdom-->" + currdom[0]);
	            var excelName = $("#export_excel_file_name").find("input").val();
			    var params = {
			    	id: id,
			    	excelName: excelName
			    };
			    layer.close(index);
	            ajax_post("../purchase/copyDisQuo", JSON.stringify(params), "application/json",
					function(data) {
						if (data.errorCode == "0") {
							/*layer.msg('即将导出Excel文件！', {icon: 1, time: 1000}, function() {
								//var $thead = $(".dis-price-one .user_recharge").children().eq(0).children().eq(0);
								//$(".dis-price-one .user_recharge").children().eq(0).empty().append($thead);
								//$(".dis-price-one #selected_product_table_datum").val("");
								window.location.href = "/purchase/exportquotation?id=" + data.errorInfo;
							});*/
							layer.msg('复制报价单成功！', {icon: 1, time: 1000}, function() {
								gain_export_detail_list_data(1);
							});
						} else {
							layer.msg("保存名称失败，请稍后重试！", {icon: 2, time: 2000});
						}
					},
					function(xhr, status) { console.log("error--->" + status); }
				);
	        }
	    });
	});
	
	//导出明细页面导出明细列表下载Excel
	$('.download_export_excel').click(function(event) {
		var id = $(this).parent().parent().parent().parent().data("id");
		window.location.href = "/purchase/exportquotation?id=" + id;
	});
	
	//导出明细页面导出明细列表关联分销商
	$('.correlate_export_record').click(function(event) {
		var id = $(this).parent().parent().parent().parent().data("id");
		var excelName = $(this).parent().parent().parent().parent().children().eq(2).text();
		$('.distributor-relevance-pop .distributor-names').find("em").text(excelName);
		$('.distributor-relevance-pop .distributor-search').find("input").val("");
		gain_distribution_list_data(1);
	    var listPop = $('.distributor-relevance-pop');
		layer.open({
            type: 1,
            title: "关联分销商",
            content: listPop,
            area: ['550px', '625px'],
            btn: ["确定关联", "取消"],
            closeBtn: 1,
            shadeClose: false,
			success: function() {
				listPop.show();
			},
			end: function() {
				listPop.hide();
			},
            yes: function(i, currdom) {
                console.log(i + "<--i	currdom-->" + currdom[0]);
                var disEmail = "";
                listPop.find("input[name='distributors']").each(function(n, node) {
                	if (node.checked) {
                		disEmail = $(node).attr("value");
                	}
                });
                var params = {
                	id: id,
                	disEmail: disEmail,
                	remark: 1
                };
                layer.close(i);
                ajax_post("../purchase/savequotrecord", JSON.stringify(params), "application/json",
					function(data) {
						if (data.errorCode == "0") {
							layer.msg('关联分销商成功！', {icon: 1, time: 1000}, function() {
								gain_export_detail_list_data(1);
							});
						} else {
							layer.msg(data.errorInfo, {icon: 2, time: 2000});
						}
					},
					function(xhr, status) { console.log("error--->" + status); }
				);
            }
        });

	});
	
	//导出明细页面导出明细列表取消关联分销商
	$('.uncorrelate_export_record').click(function(event) {
		var id = $(this).parent().parent().parent().parent().data("id");
		layer.confirm("确认取消关联分销商？", {icon: 3, title: "提示"}, function(index) {
			var params = {
	        	id: id,
	        	disEmail: "",
	            remark: 0
	        };
	        layer.close(index);
	        ajax_post("../purchase/savequotrecord", JSON.stringify(params), "application/json",
				function(data) {
					if (data.errorCode == "0") {
						layer.msg('取消关联分销商成功！', {icon: 1, time: 1000}, function() {
							gain_export_detail_list_data(1);
						});
					} else {
						layer.msg(data.errorInfo, {icon: 2, time: 2000});
					}
				},
				function(xhr, status) { console.log("error--->" + status); }
			);
		});
	});
}

//导出明细页面关联分销商弹出框获取分销商用户列表数据
function gain_distribution_list_data(curr) {
	var params = {
		role: 2,
		currPage: curr == undefined || curr == 0 ? 1 : curr,
		pageSize: 10,
		search: $('.distributor-relevance-pop .distributor-search').find("input").val()
	};
	$.ajax({
        url: "../member/getUsers",
        type: 'POST',
        data: params,
        async: false,//是否异步
        dataType: 'json',
        success: function(data) {
            if (data.suc) {
				insert_distribution_list_data(data.page.list);
				init_distribution_list_pagination(data.page);
			} else if (data.code == "2") {
				window.location.href = "login.html";
			} else {
				layer.msg(data.msg, {icon : 2, time : 1000});
			}
        },
        error: function(xhr, status) { console.log("error--->" + status); }
    });
}

//导出明细页面关联分销商弹出框插入分销商用户列表数据
function insert_distribution_list_data(list) {
	var $th = $('.distributor-relevance-pop .distributor-name-list').children().eq(0);
	var itemHTML = '';
	$.each(list, function(i, item) {
		itemHTML +=
			'<ul>' +
                '<li class="distributor-name-list1"><input type="radio" name="distributors" value="' + item.email + '"/></li>' +
                '<li class="distributor-name-list3">' + deal_with_illegal_value(item.email) + '</li>' +
                '<li class="distributor-name-list2">' + deal_with_illegal_value(item.realName) + '</li>' +
                '<li class="distributor-name-list4">' + deal_with_illegal_value(item.telphone) + '</li>' +
            '</ul>';
	});
	$('.distributor-relevance-pop .distributor-name-list').empty().append($th).append(itemHTML);
}

//导出明细页面关联分销商弹出框初始化分销商用户列表分页栏
function init_distribution_list_pagination(page) {
	if ($("#export_detail_distribution_pagination")[0] != undefined) {
        $("#export_detail_distribution_pagination").empty();
        laypage({
            cont: 'export_detail_distribution_pagination',
            pages: page.totalPage,
            curr: page.currPage,
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
                    gain_distribution_list_data(obj.curr);
                }
            }
        });
    }
}
