//定义全局变量
var layer = undefined;
var laypage = undefined;

//解决IEplaceholder不兼容问题
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

	$("body").on("click","#activeButton",function(){
		//postActive(null,false);
		$("#active_table").jqGrid('setGridParam',{page: 1}).trigger('reloadGrid');
	});
	$("body").on("change","select[name = coupons_status]",function(){
		//postCoupons(null,false);
		$("#coupons_table").jqGrid('setGridParam',{page: 1}).trigger('reloadGrid');
	});
	$("body").on("click","#couponsButton",function(){
		//postCoupons(null,false);
		$("#coupons_table").jqGrid('setGridParam',{page: 1}).trigger('reloadGrid');
	});

	$("body").on("click",".p-show",function(){
	    $(".promotion-charge").show();
	    $(".promotion-box").hide();
	});
	$("body").on("click",".back-promotion-code",function(){
		$("#searchInput").val("");
	    $(".promotion-charge").hide();
	    $(".promotion-box").show();
	    $(".add-codepage").hide();
	});
	$("body").on("click",".add-promotion-code",function(){
		$("#searchInput").val("");
	    $(".add-codepage").show();
	    $(".promotion-box").hide();
	    resetForm();
	});
	$("body").on("blur","input[tag=val]",function(){
		var inputs = $("input[tag=val]");
	    var tag = true;
	    var flag = $.each(inputs,function(i,input){
	        if(!input.value){
	            tag = false;
	        }
	    });
	    if(!tag){
	    	$(".add-sure").attr("disabled",true).css("background","#eaeaea").css("color","#000");
	    }else{
	    	$(".add-sure").attr("disabled",false).removeAttr('style').val("确认添加");
	    }
	})

})

//初始化后台用户列表
function init_active(layerParam, laypageParam) {
	alert('init_active');
	//初始化全局变量
	layer = layerParam;
	laypage = laypageParam;
	postActive(null,false);
	$("#activeInput").keydown(function (e) {
    if(e.keyCode == 13){
        postActive(null,false);
    }
 	});
	$("#couponsInput").keydown(function (e) {
	    if(e.keyCode == 13){
	        postCoupons(null,false);
	    }
	});
	$(".clear-setting").on("click",function(){
		resetForm();
	})
	$("#export").click(function(){
		exportData(this);
	});
	$("input[tag=val]").mouseout(function(){
		$(this).blur();
	});
	//时间限制
	var start = {
    elem: '#vaildStart',
    format: 'YYYY-MM-DD hh:mm:ss',
    min: laydate.now(0, 'YYYY-MM-DD hh:mm:ss'), //设定最小日期为当前日期
    max: '2099-06-16 23:59:59', //最大日期
    istime: true,
    istoday: false,
    choose: function(datas){
    	 var stringTime = datas;//结束时间要比开始时间大一天
		 var timestamp2 = Date.parse(new Date(stringTime));
		 timestamp2 += 24*60*60*1000
		 var date = new Date(timestamp2);
         end.min = date.format('yyyy-MM-dd hh:mm:ss')	; //开始日选好后，重置结束日的最小日期
         end.start = date.format('yyyy-MM-dd hh:mm:ss') //将结束日的初始值设定为开始日
    }
	};
	var end = {
	    elem: '#vaildEnd',
	    format: 'YYYY-MM-DD hh:mm:ss',
	    min: laydate.now(1, 'YYYY-MM-DD hh:mm:ss'),
	    max: '2099-06-16 23:59:59',
	    istime: true,
	    istoday: false,
	    choose: function(datas){
	    	var stringTime = datas;//开始时间要比结束时间小一天
			var timestamp2 = Date.parse(new Date(stringTime));
			timestamp2 -= 24*60*60*1000
			var date = new Date(timestamp2);
	        start.max = date.format('yyyy-MM-dd hh:mm:ss'); //结束日选好后，重置开始日的最大日期
	    }
	};
	$("body").on("click","#vaildStart",function(){
	    laydate(start);
 	})

    $("body").on("click","#vaildEnd",function(){
		laydate(end);
	})
	Date.prototype.format = function(format) {
       var date = {
              "M+": this.getMonth() + 1,
              "d+": this.getDate(),
              "h+": this.getHours(),
              "m+": this.getMinutes(),
              "s+": this.getSeconds(),
              "q+": Math.floor((this.getMonth() + 3) / 3),
              "S+": this.getMilliseconds()
       };
       if (/(y+)/i.test(format)) {
              format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
       }
       for (var k in date) {
              if (new RegExp("(" + k + ")").test(format)) {
                     format = format.replace(RegExp.$1, RegExp.$1.length == 1
                            ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
              }
       }
       return format;
	}
	
}


function init_active_new(layerParam, laypageParam, BbcGrid) {
	//初始化全局变量
	layer = layerParam;
	laypage = laypageParam;

	//初始化列表
	var grid = new BbcGrid();
	grid.initTable($("#active_table"),getSetting_active());

	$("#activeInput").keydown(function (e) {
		if(e.keyCode == 13){
			//postActive(null,false);
			$("#active_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
		}
	});
	$("#couponsInput").keydown(function (e) {
		if(e.keyCode == 13){
			//postCoupons(null,false);
			$("#coupons_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
		}
	});
	$(".clear-setting").on("click",function(){
		resetForm();
	})
	$("#export").click(function(){
		exportData(this);
	});
	$("input[tag=val]").mouseout(function(){
		$(this).blur();
	});
	//时间限制
	var start = {
		elem: '#vaildStart',
		format: 'YYYY-MM-DD hh:mm:ss',
		min: laydate.now(0, 'YYYY-MM-DD hh:mm:ss'), //设定最小日期为当前日期
		max: '2099-06-16 23:59:59', //最大日期
		istime: true,
		istoday: false,
		choose: function(datas){
			var stringTime = datas;//结束时间要比开始时间大一天
			var timestamp2 = Date.parse(new Date(stringTime));
			timestamp2 += 24*60*60*1000
			var date = new Date(timestamp2);
			end.min = date.format('yyyy-MM-dd hh:mm:ss')	; //开始日选好后，重置结束日的最小日期
			end.start = date.format('yyyy-MM-dd hh:mm:ss') //将结束日的初始值设定为开始日
		}
	};
	var end = {
		elem: '#vaildEnd',
		format: 'YYYY-MM-DD hh:mm:ss',
		min: laydate.now(1, 'YYYY-MM-DD hh:mm:ss'),
		max: '2099-06-16 23:59:59',
		istime: true,
		istoday: false,
		choose: function(datas){
			var stringTime = datas;//开始时间要比结束时间小一天
			var timestamp2 = Date.parse(new Date(stringTime));
			timestamp2 -= 24*60*60*1000
			var date = new Date(timestamp2);
			start.max = date.format('yyyy-MM-dd hh:mm:ss'); //结束日选好后，重置开始日的最大日期
		}
	};
	$("body").on("click","#vaildStart",function(){
		laydate(start);
	})

	$("body").on("click","#vaildEnd",function(){
		laydate(end);
	})
	Date.prototype.format = function(format) {
		var date = {
			"M+": this.getMonth() + 1,
			"d+": this.getDate(),
			"h+": this.getHours(),
			"m+": this.getMinutes(),
			"s+": this.getSeconds(),
			"q+": Math.floor((this.getMonth() + 3) / 3),
			"S+": this.getMilliseconds()
		};
		if (/(y+)/i.test(format)) {
			format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
		}
		for (var k in date) {
			if (new RegExp("(" + k + ")").test(format)) {
				format = format.replace(RegExp.$1, RegExp.$1.length == 1
					? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
			}
		}
		return format;
	}

}

function getSetting_active() {
	var setting = {
		url:"/member/getActive",
		ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
		rownumbers : true, // 是否显示前面的行号
		datatype : "json", // 返回的数据类型
		mtype : "post", // 提交方式
		height : "auto", // 表格宽度
		autowidth : true, // 是否自动调整宽度
		// styleUI: 'Bootstrap',
		colNames:["id","编号","优惠码名称","优惠码面额","门槛金额","发行数量","状态","添加时间", "添加人",  "操作"],
		colModel:[{name:"id",index:"id",width:"12%",align:"center",sortable:false,hidden:true},
			{name:"id",index:"id",width:"12%",align:"center",sortable:true},
			{name:"couponsName",index:"coupons_name",width:"12%",align:"center",sortable:true},
			{name:"couponsCost",index:"coupons_cost",width:"12%",align:"center",sortable:true},
			{name:"thresholdPrice",index:"threshold_price",width:"14%",align:"center",sortable:true},
			{name:"publishQty",index:"publish_qty",width:"12%",align:"center",sortable:true},
			{name:"state",index:"status",width:"12%",align:"center",sortable:true},
			{name:"createDateStr",index:"create_date",width:"12%",align:"center",sortable:true},
			{name:"creater",index:"creater",width:"12%",align:"center",sortable:true},
			{name:"active",index:"",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
				return '<a href="javascript:void(0);" data-id = "' + rowObject.id + '" data-validstart = "'+rowObject.validDateStartStr+'"'+
				' onclick = getCoupons(this)  data-validend = "'+rowObject.validDateEndtStr+'" >查看</a>';
			}}
		],
		viewrecords : true,
		rowNum : 10,
		rowList : [ 10, 20, 30 ],
		pager:"#active_pagination",//分页
		caption:"优惠码",//表名称
		pagerpos : "center",
		pgbuttons : true,
		autowidth: true,
		rownumbers: true, // 显示行号
		loadtext: "加载中...",
		pgtext : "当前页 {0} 一共{1}页",
		jsonReader:{
			root: "data.result",  //数据模型
			page: "data.currPage",//数据页码
			total: "data.totalPage",//数据总页码
			records: "data.totalCount",//数据总记录数
			repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
			//cell: "cell",//root 中row 行
			id: "id"//唯一标识
		},
		serializeGridData : function() {
			return JSON.stringify(getSearchParam_active($(this).jqGrid('getGridParam', 'postData')))
		}
	};
	return setting;
}

function getSearchParam_active(postData) {
	var param = {
		pageSize: $('#active_table').getGridParam('rowNum'),
		currPage: $('#active_table').getGridParam('page'),
		seachSpan: $("#activeInput").val().trim(),
		createStartDate: $("input[name = createStart]").val(),
		createEndDate: $("input[name = createEnd]").val(),
		sord:postData.sord,
		sidx:postData.sidx
	};
	return param;
}

//按下enter进行查询
function getList_active() {
	$("#active_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}

// 初始化客户售后单分页
function init_active_pagination(pages, currPage) {
	if ($("#pagination_active")[0] != undefined) {
		$("#pagination_active").empty();
		laypage({
			cont: 'pagination_active',
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
					 postActive(obj, true);
				}
			}
		});
	}
}

function postActive(obj,red){
	var pageCount = "1";
	var pageSize = $("#active_pageSize").val();
	if (pageSize == undefined || pageSize == "" || pageSize == 0) {
		pageSize = 10;
	}
	if (red) {
		pageCount = obj.curr;
	}
	var seachSpan = $("#activeInput").val().trim();
	var param = {
	    pageSize:pageSize,
	    currPage:pageCount,
	    seachSpan:seachSpan,
    }
    var  createStartDate = $("input[name = createStart]").val();
    var createEndDate = $("input[name = createEnd]").val();
    if(createStartDate){
    	param.createStartDate = createStartDate;
    }
    if(createEndDate){
    	param.createEndDate = createEndDate;
    }
    ajax_post(
    	"/member/getActive ",
		JSON.stringify(param),
		"application/json",
		function(response) {
			$("#active_pageSize").change(function() {
				postActive(null, false);
			});
			if(!response.suc){
				window.location.href = "/backstage/index.html";
			}
			var data = response.data;
			var result = data.result;
			var totalPage = data.totalPage;
			var currPage = data.currPage;
			var totalCount = data.totalCount;
			$("#activeTotal").text(totalCount);
			$("#activePages").text(totalPage);
			var tbody = $("#acitve_table tbody");
			tbody.empty();
			if(totalCount>0){
				for(var i = 0;i < result.length;i++ ){
					var active = result[i];
					var activeHtml =    
						'<tr class="user_recharge_header">'+
	                 	'<td width="8%">'+active.id+'</td>'+
		                '<td width="23%">'+active.couponsName+'</td>'+
		                '<td width="10%">'+active.couponsCost+'</td>'+
		                '<td width="10%">'+active.thresholdPrice+'</td>'+
		                '<td width="8%">'+active.publishQty+'</td>'+
		                '<td width="10%">'+active.state+'</td>'+
		                '<td width="15%">'+active.createDateStr+'</td>'+
		                '<td width="8%">'+active.creater+'</td>'+
		                '<td width="10%" class="p-show">'+
		                '<a href="javascript:void(0);" data-id = "'+active.id+'" data-validstart = "'+active.validDateStartStr+'"'+
		                ' onclick = getCoupons(this)  data-validend = "'+active.validDateEndtStr+'" >查看</a>'+
		                '</td>'+
		            	'</tr>';
		            tbody.append(activeHtml);	
				}
			}else{
				layer.msg("没有符合条件的活动信息",{icon:5,time:2000});
			}
			init_active_pagination(totalPage, currPage);
		},
		function(XMLHttpRequest, textStatus) {
			layer.msg("获取优惠活动数据失败！",{icon:2,time:2000});
		}
	);			
}


function init_coupons_pagination(pages, currPage) {
	if ($("#pagination_coupons")[0] != undefined) {
		$("#pagination_coupons").empty();
		laypage({
			cont: 'pagination_coupons',
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
					 postCoupons(obj, true);
				}
			}
		});
	}
}

var couponGrid;
function init_coupons(BbcGrid) {
	couponGrid = new BbcGrid();
	//couponGrid.initTable($("#coupons_table"),getSetting_coupons());
}

function getCoupons(obj){
	var node = $(obj);
	var id = node.data("id");
	var validDateStartStr = node.data("validstart");
	var validDateEndtStr = node.data("validend");
	$(".p-time").find("#start").text(validDateStartStr);
	$(".p-time").find("#end").text(validDateEndtStr);
	$("#activeId").val(id);
	$(".promotion-charge").show();
    $(".promotion-box").hide();
	$("#searchInput").val("");

	//postCoupons(null,false);
	//$("#coupons_table").jqGrid('setGridParam',{page: 1}).trigger('reloadGrid');
	//var grid = new BbcGrid();
	if(couponGrid) {
		couponGrid.initTable($("#coupons_table"),getSetting_coupons());
		$("#coupons_table").jqGrid('setGridParam',{page: 1}).trigger('reloadGrid');
	}
}


function getSetting_coupons() {
	var setting = {
		url:"/member/getCoupons",
		ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
		rownumbers : true, // 是否显示前面的行号
		datatype : "json", // 返回的数据类型
		mtype : "post", // 提交方式
		height : "auto", // 表格宽度
		autowidth : true, // 是否自动调整宽度
		// styleUI: 'Bootstrap',
		colNames:["id","优惠编号","使用状态","使用人","使用时间","订单编号","订单状态","订单金额", "实际支付金额"],
		colModel:[{name:"id",index:"id",width:"12%",align:"center",sortable:false,hidden:true},
			{name:"couponsNo",index:"sales_order_no",width:"12%",align:"center",sortable:false},
			{name:"state",index:"sales_order_no",width:"12%",align:"center",sortable:false},
			{name:"user",index:"opt_fee",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
					return deal_with_illegal_value(cellvalue)
			}},
			{name:"usageTimeStr",index:"ordering_date",width:"14%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue)
			}},
			{name:"orderNo",index:"stock",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue)
			}},
			{name:"orderState",index:"warehouse_name",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue)
			}},
			{name:"orderAmount",index:"nick_name",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue)
			}},
			{name:"actuallyPaid",index:"nick_name",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue)
			}}
		],
		viewrecords : true,
		rowNum : 10,
		rowList : [ 10, 20, 30 ],
		pager:"#coupons_pagination",//分页
		caption:"优惠码列表",//表名称
		pagerpos : "center",
		pgbuttons : true,
		autowidth: true,
		rownumbers: true, // 显示行号
		loadtext: "加载中...",
		pgtext : "当前页 {0} 一共{1}页",
		jsonReader:{
			root: "data.result",  //数据模型
			page: "data.currPage",//数据页码
			total: "data.totalPage",//数据总页码
			records: "data.totalCount",//数据总记录数
			repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
			//cell: "cell",//root 中row 行
			id: "id"//唯一标识
		},
		serializeGridData : function() {
			return JSON.stringify(getSearchParam_coupons($(this).jqGrid('getGridParam', 'postData')))
		}
	};
	return setting;
}

function getSearchParam_coupons() {
	var status = $("select[name = coupons_status]").val();
	var seachSpan = $("#couponsInput").val().trim();
	var usedStartDate = $("input[name = usedStart]").val();
	var usedEndDate = $("input[name = usedEnd]").val();
	var activeId = $("#activeId").val();
	var param = {
		pageSize:$('#coupons_table').getGridParam('rowNum'),
		currPage:$('#coupons_table').getGridParam('page'),
		seachSpan:seachSpan,

	}

	if(activeId) {
		param.activeId = activeId;
	}
	if(status){
		param.status = status;
	}
	if(usedStartDate){
		param.usedStartDate = usedStartDate;
	}
	if(usedEndDate){
		param.usedEndDate = usedEndDate;
	}
	return param;
}

//按下enter进行查询
function getList_coupons() {
	$("#coupons_table").jqGrid('setGridParam',{page: 1}).trigger('reloadGrid');
}

//获取活动状态描述
function getIStatusStr(status){
    var stav = status == 0?"待使用":
            		status == 1?"已使用":
            			status == 2?"已作废":"活动状态异常";
    return  stav;           
}

function postCoupons(obj,red){
	var pageCount = "1";
	var pageSize = $("#active_pageSize").val();
	if (pageSize == undefined || pageSize == "" || pageSize == 0) {
		pageSize = 10;
	}
	if (red) {
		pageCount = obj.curr;
	}
	var status = $("select[name = coupons_status]").val();
	var seachSpan = $("#couponsInput").val().trim();
	var usedStartDate = $("input[name = usedStart]").val();
	var usedEndDate = $("input[name = usedEnd]").val();
	var param = {
	    pageSize:pageSize,
	    currPage:pageCount,
	    seachSpan:seachSpan,
	    activeId:$("#activeId").val()
    }
    if(status){
    	param.status = status;
    }
    if(usedStartDate){
    	param.usedStartDate = usedStartDate;
    }
    if(usedEndDate){
    	param.usedEndDate = usedEndDate;
    }
     ajax_post(
    	"/member/getCoupons",
		JSON.stringify(param),
		"application/json",
		function(response) {
			$("#coupons_pageSize").change(function() {
				postCoupons(null, false);
			});
			if(!response.suc){
				window.location.href = "/backstage/index.html";
			}
			var data = response.data;
			var result = data.result;
			var totalPage = data.totalPage;
			var currPage = data.currPage;
			var totalCount = data.totalCount;
			$("#couponsTotal").text(totalCount);
			$("#couponsPages").text(totalPage);
			var tbody = $("#coupons_table tbody");
			tbody.empty();
			if(totalCount>0){
				for(var i = 0;i < result.length;i++ ){
					var coupons = result[i];
					var couponsHtml =    
									'<tr class="user_recharge_header">'+
					                '<td width="15%">'+coupons.couponsNo+'</td>'+
					                '<td width="8%">'+coupons.state+'</td>'+
					                '<td width="15%">'+deal_with_illegal_value(coupons.user)+'</td>'+
					                '<td width="15%">'+deal_with_illegal_value(coupons.usageTimeStr)+'</td>'+
					                '<td width="15%">'+deal_with_illegal_value(coupons.orderNo)+'</td>'+
					                '<td width="10%">'+coupons.orderState+'</td>'+
					                '<td width="8%">'+deal_with_illegal_value(coupons.orderAmount)+'</td>'+
					                '<td width="14%">'+deal_with_illegal_value(coupons.actuallyPaid)+'</td>'+
					            	'</tr>';
		            tbody.append(couponsHtml);	
				}
			}else{
				layer.msg("没有符合条件的优惠码信息",{icon:5,time:2000});
			}
			init_coupons_pagination(totalPage, currPage);
		},
		function(XMLHttpRequest, textStatus) {
			layer.msg("获取优惠码失败！",{icon:2,time:2000});
		}
	);	
}

function getPamrams(){
	var couponsName = $("input[name = couponsName ]").val();
	if(!couponsName){
		layer.msg("活动名称不能为空！",{icon:2,time:2000});
		return undefined; 
	}
	var couponsCost = $("input[name = couponsCost ]").val();
	if(!checkMoney(couponsCost)){
		layer.msg("请填写正确格式的活动面额！",{icon:2,time:2000});
		return undefined; 
	}
	var thresholdPrice = $("input[name = thresholdPrice] ").val();
	if(!checkMoney(thresholdPrice)){
		layer.msg("请填写正确格式的使用门槛金额！",{icon:2,time:2000});
		return undefined; 
	}
	var vaildStart = $("input[name = vaildStart]").val();
	var vaildEnd = $("input[name = vaildEnd]").val();
	if(!vaildStart||!vaildEnd){
		layer.msg("使用有效时间不能为空！",{icon:2,time:2000});
		return undefined; 
	}
	var publishQty = $("input[name = publishQty]").val();
	var regNum = /^[1-9]\d*$/
	if(!regNum.test(publishQty)){
		layer.msg("请填写有效的发行数量！",{icon:2,time:2000});
		return undefined; 
	}

	var param ={
		couponsName:couponsName,
		couponsCost:couponsCost,
		thresholdPrice:thresholdPrice,
		validDateEndtStr:vaildEnd,
		validDateStartStr:vaildStart,
		publishQty:publishQty,
		couponsLenght:$("input[name=couponsLenght]").val()
	}
	return param;
}

function saveActive(){
	var param = getPamrams();
	if(param == undefined){
		return;
	}
	console.log(layer);
	//防止重复提交
	$(".add-sure").attr("disabled",true).css("background","#eaeaea").css("color","#000");
	 ajax_post(
    	"/member/saveActive",
		JSON.stringify(param),
		"application/json",
		function(response) {
			if(response.code == 2){
				window.location.href = "/backstage/index.html";
				return;
			}
			if(response.suc){
				console.log(response);
				layer.open({
				  title: '提示',
				  content: '<span>添加成功</span><br><span>优惠编号:&nbsp&nbsp'+response.info+'<span>',
				  btn:false,
				  success:function(layero, index){
				  	setTimeout(function(){//5秒后自动关闭
				  		layer.close(index);
				  	    load_menu_content("16");
				  	},5000);
				  },
				  cancel:function(index){ 
				  	layer.close(index);
				  	load_menu_content("16");
				  }
				});
			}else{
				layer.msg(response.info, {icon : 5, time : 2000});
				$(".add-sure").attr("disabled",false).removeAttr('style').val("确认添加");
			}
		},
		function(XMLHttpRequest, textStatus) {
			$(".add-sure").attr("disabled",false).removeAttr('style').val("确认添加");
			layer.msg("新增优惠活动异常！",{icon:2,time:2000});
		}
	);	
}
function resetForm(){
	var inputs = $(".add-codepage").find("input");
	for(var i = 0 ; i< inputs.length;i++){
		if($(inputs[i]).attr("name")!= "couponsLenght"){
			$(inputs[i]).val("");
		}
	}
	$("input[tag=val]").blur();
}

//导出
function exportData(event){
    //var headTh = $("#coupons_table th");
    var headTh = ['couponsNo','istatus','user','usageTimeStr','orderNo','orderState','orderAmount','actuallyPaid'];
	var headName = [];
    $.each(headTh,function(i,th){
        headName.push("header=" + th);
    });
    var status = $("select[name = coupons_status]").val();
    if(status){
    	headName.push("status=" + status);
    }
    var seachSpan = $("#couponsInput").val().trim();
    if(seachSpan){
    	headName.push("seachSpan=" + seachSpan);
    }
    var usedStartDate = $("input[name = usedStart]").val();
    if(usedStartDate){
    	headName.push("usedStartDate=" + usedStartDate);
    }
	var usedEndDate = $("input[name = usedEnd]").val();
	if(usedEndDate){
		headName.push("usedEndDate=" + usedEndDate);
	}
    headName.push("activeId=" + $("#activeId").val());
    if(headName.length > 0 ){
        window.location.href = "/member/exportCoupons?" + headName.join("&");
    }
}

