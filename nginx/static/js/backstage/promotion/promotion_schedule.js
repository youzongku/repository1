/**商品属性*/
var ATTR_GOODS = "1";
/**购物车属性*/
var ATTR_SHOPPING_CART = "2";
/**用户属性*/
var ATTR_USER = "3";

/**
 * Created by Administrator on 2016/7/25.
 */
/**
 * 更改活动结束时间
 * @param id
 * @param name
 */
function updateEndTime(id,name,createTime) {
	$("#updateEndName").text(name)
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['480px', '230px'],
        content: $('.time_adjust_box'),
        btn:['保存','取消'],
		yes:function (index) {
            if(isnull($("#updateEndTime").val())){
                layer.msg("时间不能为空！",{icon:2,time:2000})
                return;
            }
			if($("#updateEndTime").val()<createTime){
				layer.msg("结束时间不能小于开始时间",{icon:2,time:2000})
                return;
			}
			var data = {
				    "id":id,
					"endTime":$("#updateEndTime").val()
				};
			$.ajax({
				url: "../market/pro/act/time/update",
				type: "POST",
				data: JSON.stringify(data),
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				success: function(response) {
					if(response.success){
						alert(response.resultObject);
						layer.closeAll();
						//getAllActivity(1)
						$("#promotion_schedule_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
					}else{
						alert(response.errorMessage);
					}

				},
				error:function (e) {
					layer.msg(e)
				}
			})
		},
        title:'时间调整'
    });
}
/**
 * 查看详情
 * @param id
 */
function showDetail(id) {
	var data = {
		"proActId": id
	};

	$.ajax({
		url: "../market/pro/act/alldetail",
		type: "POST",
		data: JSON.stringify(data),
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function (response) {
            $("#proAct_name").text(response.name);
            $("#proAct_description").text(response.description);
			// 模式
			var disModeList = response.disModeList;
			var modeNames = new Array()
			for(var i in disModeList){
				modeNames.push(disModeList[i].disModeName)
			}
            $("#proAct_disMode").text(modeNames.toString());
            $("#proAct_startTime").text(new Date(response.startTime).format("yyyy-MM-dd hh:mm:ss"));
            $("#proAct_endTime").text(new Date(response.endTime).format("yyyy-MM-dd hh:mm:ss"));
            $("#proAct_createTime").text(new Date(response.createTime).format("yyyy-MM-dd hh:mm:ss"));
            $("#proAct_updateTime").text(new Date(response.lastUpdateTime).format("yyyy-MM-dd hh:mm:ss"));
            $("#proAct_createUser").text(response.createUser);
			$("#proAct_status").text(getProActStatusStr(response.status));

			var fullActInstDtoList = response.fullActInstDtoList  // 活动实例
			var seq = 0;
			for(var i = 0;i < fullActInstDtoList.length;i++ ){
				seq = seq+1;
				var fullActInstDto = fullActInstDtoList[i];
				var fullCondtInstDtoList = fullActInstDto.fullCondtInstDtoList
				var actInstAndProTypeHtml = '<tr>';
				for(var j = 0;j < fullCondtInstDtoList.length;j++){
					if(j==0){
						var rowCount = fullCondtInstDtoList.length;
						actInstAndProTypeHtml += '<td rowspan="'+rowCount+'">' + seq + '</td>';
						actInstAndProTypeHtml += '<td rowspan="'+rowCount+'">' + fullActInstDto.name + '</td>';
						var description = '';
						// if(!isnull(fullActInstDto.description)){
						//     var arr = fullActInstDto.description.split("=");
						//     description += '<p>条件：'+arr[0]+'</p>';
						//     description += '<p>优惠：'+arr[1]+'</p>';
						// }
						actInstAndProTypeHtml += '<td class="pro_intro" rowspan="'+rowCount+'">'+description+'</td>';
						actInstAndProTypeHtml += '<td rowspan="'+rowCount+'">'+getAttrStr(fullActInstDto.attr+'')+'</td>';
					}

					var fullCondtInstDto = fullCondtInstDtoList[j]
					actInstAndProTypeHtml += '<td class="pro_intro">' + fullCondtInstDto.name + '</td>';
					var isSetV_flag = (fullCondtInstDto.isSetV == 1); // 是否设置了参数
					if(isSetV_flag){
						actInstAndProTypeHtml += '<td class="pro_intro">已设置</td>';
					}else{
						actInstAndProTypeHtml += '<td class="pro_intro" style="color:red">未设置</td>';
					}

					actInstAndProTypeHtml += '<td>'+fullCondtInstDto.priority+'</td>';
					actInstAndProTypeHtml += "<td class='check_para' onclick='showCondtInstDetail("+fullCondtInstDto.id+")'>查看参数设置</td>";

					actInstAndProTypeHtml += '</tr>';
				}

				$("#proAct_actInst_condtInst_tbody").append(actInstAndProTypeHtml);
			}

             //var tr = "";
			 //var number = 0;
			 //var attr;
			 //var fullActInstDtoList = response.fullActInstDtoList; // 活动实例
             //for(var index in fullActInstDtoList){
			 //	var fullActInstDto = fullActInstDtoList[index]
            	// number++;
			 //	tr+="<tr>"
            	// tr+="<td>"+number+"</td>";
			 //	tr+="<td>"+fullActInstDto.name+"</td>";
			 //	tr+="<td>"+fullActInstDto.description+"</td>";
			 //	attr = fullActInstDto.attr + "";
			 //	switch (attr){
			 //		case ATTR_GOODS:
			 //			tr+="<td>商品属性</td>";
			 //			break;
			 //		case ATTR_SHOPPING_CART:
			 //			tr+="<td>购物车属性</td>";
			 //			break;
			 //		case ATTR_USER:
			 //			tr+="<td>用户属性</td>";
			 //			break;
			 //	}
			 //	switch (actInstWithProTypeDto.isSetV){
			 //		case 0:
			 //			tr += "<td>未设置</td>";
			 //			break;
			 //		case 1:
			 //			tr += "<td>已设置</td>";
			 //			break;
			 //	}
			 //	tr+="<td>"+actInstWithProTypeDto.priority+"</td>";
			 //	tr+="<td class='check_para' onclick='showCondtInstDetail("+actInstWithProTypeDto.actInstId+")'>查看参数设置</td>";
			 //}

            $('.promotion_schedule').hide();
            $('.promotion_check_box').show();

		},
		error: function (e) {
			layer.msg(e)
		}
	})
}

$('body').on("click", ".promotion_back", function () {
    $('.promotion_schedule').show();
    $('.promotion_check_box').hide();
})

// 查看实例详情
function showCondtInstDetail(condtInstId) {
	var param = {condtInstId: condtInstId};
    $.ajax({
        url: "../market/pro/actinst/condtinst/detail",
        type: "POST",
        data: JSON.stringify(param),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
			var fullCondtInstDto = response; // 条件实例
			var fullPvlgInstDto = fullCondtInstDto.fullPvlgInstDto; // 优惠实例

			// 动态创建条件
			createDynamicCondts("view_dynamic_pro_inst_condition_div", fullCondtInstDto);

			// 动态创建优惠
			createDynamicPvlgs("view_dynamic_pro_inst_pvlg_div", fullPvlgInstDto);

			// 额外设置，因为只是查看，所以不能进行其他操作
			$("#view_dynamic_actInst_condtPvlgInsts_div").find("input[type='text']").each(function(i,e){
				$(e).attr("readonly","readonly")
			})
			$("#view_dynamic_actInst_condtPvlgInsts_div").find("input[type='checkbox']").each(function(i,e){
				$(e).attr("disabled","disabled")
			})
			$("#view_dynamic_actInst_condtPvlgInsts_div").find("button").each(function(i,e){
				$(e).attr("disabled","disabled")
			})
			$("#view_dynamic_actInst_condtPvlgInsts_div").find("select").each(function(i,e){
				$(e).attr("disabled","disabled")
			})
			// 设置可翻倍的样式
			if($("#specify_attr_value_p")[0]){
				$("#specify_attr_value_p").find("input[type='checkbox']").parent().css("margin-right","37px")
			}

			createDynamicDescription('activity_description',fullCondtInstDto)
			// 创建描述
			// var description = view_createDescription();
			// $("#activity_description").html(description);
            layer.open({
                type: 1,
                skin: 'layui-layer-rim',
				area: ['900px', '450px'],   // 900 550
                content: $('.check_parameter_box'),
                btn:['关闭'],
                title:'查看参数设置'
            });
        },
        error:function (e) {

        }
    });
}

function view_createDescription(){
	var description;
	var matchTypeStr = $("#promotion_select").find("option:selected").text();
	var condt_descriptionArray = new Array();
	// 获取所有的条件所在的<p>标签
	$(".view_dynamic_condt_p").each(function(i,e){
		var optionSelected = $(e).find("select:eq(1)").find("option:selected");
		var jType = optionSelected.attr("jType");// 判断类型：lt,gt,...
		var jTypeName = optionSelected.text();// 判断类型名称：是、非、大于,...
		var cType = parseInt($(e).attr("c_type"));// 标明是哪个条件
		switch (cType){
			case 1:// 商品分类 {"productCategories":[{"categoryId":4700,"categoryName":"钟表首饰"}]}
				var categoryNames = $(e).find("input:eq(0)").val();
				condt_descriptionArray.push("商品分类"+red(jTypeName+categoryNames));
				break;
			case 2:// 指定商品 cType,jType,skus,num
				var skus = $(e).find("input:eq(0)").val();
				var num = 0;
				condt_descriptionArray.push("指定商品"+red(jTypeName+skus));
				if(jType=='y'){// 指定商品选择“是”的时候，有数量
					num = $("#condt_specify_product_num").val();
					var combined = $("#condt_specify_product_combined").prop("checked");
					var doubleUp = $("#condt_specify_product_doubleUp").prop("checked");
					var unit = $("#specify_product_unit_select").val();
					if(unit=='箱'){
						var unitNums = $("#specify_product_unitNums").val();
						condt_descriptionArray.push("箱规分别为"+red(unitNums));
					}
					if(combined){
						condt_descriptionArray.push("数量"+red("共"+num+unit));
						condt_descriptionArray.push("可组合");
					}else{
						condt_descriptionArray.push("数量"+red("各"+num+unit));
						condt_descriptionArray.push("不可组合");
					}
					if(doubleUp){
						condt_descriptionArray.push("可翻倍");
					}else{
						condt_descriptionArray.push("不可翻倍");
					}
				}
				break;
			case 3:// 指定仓库 cType,jType,warehouseIds,warehouseNames
				var warehouseNames = $(e).find("input:eq(0)").val();
				condt_descriptionArray.push("指定仓库"+red(jTypeName+warehouseNames));
				break;
			case 4:// 商品类型
				var categoryOptionSelected = $(e).find("select:eq(1)");
				var productTypeText = categoryOptionSelected.text();
				condt_descriptionArray.push("商品类型"+red(jTypeName+productTypeText));
				break;
			case 5:// 小计金额 cType,jType,minPrice,maxPrice
				var minPrice = $(e).find("input:eq(0)").val();
				var maxPrice;
				if(jType == 'ltv'){// 区间，有两个input的，其他的都是一个input
					maxPrice = $(e).find("input:eq(1)").val();
				}
				if(jType == 'ltv'){
					condt_descriptionArray.push("小计金额范围区间在"+red(minPrice)+"-"+red(maxPrice)+"元之间");
				}else{
					condt_descriptionArray.push("购物车小计金额"+red(jTypeName+minPrice));
				}
				break;
			case 6:// 总商品数量
				var productTotalCount = $(e).find("input:eq(0)").val();
				condt_descriptionArray.push("总商品数量"+red(jTypeName+productTotalCount)+"个");
				break;
			case 7:// 购物车总重量 cType,jType,min,max
				var minWeight = $(e).find("input:eq(0)").val();
				if(jType == 'ltv'){// 区间，有两个input的，其他的都是一个input
					var maxWeight = $(e).find("input:eq(1)").val();
				}
				if(jType == 'ltv'){
					condt_descriptionArray.push("购物车总重量范围在"+red(minWeight)+"kg和"+red(maxWeight)+"kg之间");
				}else{
					condt_descriptionArray.push("购物车总重量"+red(jTypeName+minWeight)+"kg");
				}
				break;
			case 8:// 运送地区 cType,jType,cityIds,cityNames
				var cityNames = $(e).find("input:eq(0)").val();
				condt_descriptionArray.push("运送地区"+red(jTypeName+cityNames));
				break;
			default:
				break;
		}
	});

	var pvlg_descriptionArray = new Array();
	$(".view_dynamic_pvlg_p").each(function(i,e){
		var pType = parseInt($(e).attr("pType"));
		switch (pType){
			case 1://购买X即可免费获得Y（赠品）
				var skus = $(e).find("input:eq(0)").val();
				var num = $(e).find("input:eq(1)").val();
				var unit = $("#specify_donation_product_unit_select").val();
				var combined = $("#condt_specify_product_combined").prop("checked");
				pvlg_descriptionArray.push("送赠品商品编号为"+red(skus));
				if(unit=='箱'){
					var unitNums = $("#specify_product_unitNums").val();
					pvlg_descriptionArray.push("箱规分别为"+red(unitNums));
				}
				if(combined==undefined || !combined){
					var text = skus.indexOf(",")!=-1 ? "各" : "共";
					pvlg_descriptionArray.push(red(text+num+unit)+"。");
				}else{
					pvlg_descriptionArray.push(red("共"+num+unit)+"。");
				}
				break;
			case 2://满减金额
				var reduceMoney = $(e).find("input:eq(0)").val();
				pvlg_descriptionArray.push("购物满减价"+red(reduceMoney)+"元");
				break;
			case 3:// 整个购物车的定额折扣
				var discountNum = $(e).find("input:eq(0)").val();
				pvlg_descriptionArray.push("购物车定额折扣为"+red(discountNum)+"%");
				break;
			default:
				break;
		}
	});

	description = "满足"+matchTypeStr+"条件："+ condt_descriptionArray.join("、")+
		"，将获得以下优惠："+pvlg_descriptionArray.join("，");
	return description;
}

var layer = undefined;
var laypage = undefined;
/**
 * 时间格式化
 * @param fmt
 * @returns {*}
 * @constructor
 */
Date.prototype.Format = function (fmt) { //author: meizz
	var o = {
		"M+": this.getMonth() + 1, //月份
		"d+": this.getDate(), //日
		"h+": this.getHours(), //小时
		"m+": this.getMinutes(), //分
		"s+": this.getSeconds(), //秒
		"q+": Math.floor((this.getMonth() + 3) / 3), //季度
		"S": this.getMilliseconds() //毫秒
	};
	if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	for (var k in o)
		if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}
//初始化活动一览列表
function init_schedule(layerParam, laypageParam ,menuid) {
	//初始化全局变量
	layer = layerParam;
	laypage = laypageParam;
	getAllActivity(1);
}

//初始化活动一览列表
function init_schedule_new(layerParam, laypageParam ,BbcGrid) {
	//初始化全局变量
	layer = layerParam;
	laypage = laypageParam;

	var grid = new BbcGrid();
	grid.initTable($('#promotion_schedule_table'), getSetting_promotion_schedule());
}

function getSetting_promotion_schedule() {
	var setting = {
		url:"../market/pro/activities",
		//ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
		rownumbers : true, // 是否显示前面的行号
		datatype : "json", // 返回的数据类型
		mtype : "get", // 提交方式
		height : "auto", // 表格宽度
		autowidth : true, // 是否自动调整宽度
		// styleUI: 'Bootstrap',
		colNames:["id","促销名称","促销描述","开始日期","结束日期","状态","创建人","创建日期","修改日期","操作"],
		colModel:[{name:"id",index:"id",width:"12%",align:"center",sortable:false,hidden:true},
			{name:"name",index:"name",width:"12%",align:"center",sortable:true},
			{name:"description",index:"description",width:"12%",align:"center",sortable:true},
			{name:"startTime",index:"start_time",width:"14%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
				return formatDateTime(cellvalue);
			}},
			{name:"endTime",index:"end_time",width:"14%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
				return formatDateTime(cellvalue);
			}},
			{name:"status",index:"status",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
				return getProActStatusStr(cellvalue);
			}},
			{name:"createUser",index:"create_user",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
				return deal_with_illegal_value(cellvalue);
			}},
			{name:"createTime",index:"create_time",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
				return formatDateTime(cellvalue);
			}},
			{name:"lastUpdateTime",index:"last_update_time",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
				return formatDateTime(cellvalue);
			}},
			{name:"status",index:"status",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
				var tr = "";
				if(cellvalue == 1){
					tr+="<span class='schedule_change' proActId='"+rowObject.id+"'>修改</span>";
				}else if(cellvalue == 2){
					tr+='<span class="time_adjust" onclick="updateEndTime(\''+rowObject.id+'\',\''+rowObject.name+'\',\''+formatDateTime(rowObject.startTime)+'\')">时间调整</span>';
					// 进行中的可以暂停
					tr+="<span onclick='pauseProAct(this)' proActId='"+rowObject.id+"'>暂停</span>";
				}else if(rowObject.status == 4){
					// 暂停的可以启动和修改
					tr+="<span class='schedule_change' proActId='"+rowObject.id+"'>修改</span>";
					tr+="<span onclick='activateProAct(this)' proActId='"+rowObject.id+"'>启动</span>";
				}
				tr+="<span class='schedule_check' onclick='showDetail("+rowObject.id+")'>查看</span>";

				return '<div class=\"schedule_operate\">' + tr + '</div>';
			}}
		],
		viewrecords : true,
		rowNum : 10,
		rowList : [ 10, 20, 30 ],
		pager:"#promotion_schedule_pagination",//分页
		caption:"促销一览表",//表名称
		pagerpos : "center",
		pgbuttons : true,
		autowidth: true,
		rownumbers: true, // 显示行号
		loadtext: "加载中...",
		pgtext : "当前页 {0} 一共{1}页",
		jsonReader:{
			root: "result",  //数据模型
			page: "currPage",//数据页码
			total: "totalPage",//数据总页码
			records: "rows",//数据总记录数
			repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
			//cell: "cell",//root 中row 行
			id: "id"//唯一标识
		},
		serializeGridData : function() {
			return getSearchParam_promotion_schedule($(this).jqGrid('getGridParam', 'postData'))
		}
	};
	return setting;
}

function getSearchParam_promotion_schedule(postData) {
	var params = {
		"name":$("#promotion_name").val(),
		"startTime":$("#startTime").val(),
		"endTime":$("#endTime").val(),
		"status":$("#shcedule_status option:selected").val(),
		"pageSize" : $('#promotion_schedule_table').getGridParam('rowNum'),
		"curr":$('#promotion_schedule_table').getGridParam('page'),
		sord:postData.sord,
		sidx:postData.sidx
	};
	return params;
}

//活动状态
$("#shcedule_status").change(function(){
	$("#promotion_schedule_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
});

//查询按钮
$('.searchButton').click(function(){
	$("#promotion_schedule_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
});

//促销名称输入框
function getList_promotion_schedule() {
	$("#promotion_schedule_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
}

/**
 * 页面初始化得到所有的活动
 * @param curr
 */
function getAllActivity(curr){
	var params = {
		"name":$("#promotion_name").val(),
		"startTime":$("#startTime").val(),
		"endTime":$("#endTime").val(),
		"status":$("#shcedule_status option:selected").val(),
	    "pageSize" : 10,
	    "curr":curr ? curr : 1
	}
	$.ajax({
        url: "../market/pro/activities",
        type: "GET",
        data: params,
        dataType: "json",
        success: function(response) {
			var tr = "<tr>";
			var result = response.result;
			var number = ((response.currPage-1)*response.pageSize);
			for(var index in result){
				number++;
			    tr+="<td style='width: 4%;'>"+number+"</td>";
				tr+="<td style='width: 9%;'>"+result[index].name+"</td>";
				tr+="<td style='width: 22%;' class='pro_intro'>"+result[index].description+"</td>";
				tr+="<td style='width: 12%;' class='schedule_time'>"+formatDateTime(result[index].startTime)+"</td>";
				tr+="<td style='width: 12%;' class='schedule_time'>"+formatDateTime(result[index].endTime)+"</td>";
				tr+="<td style='width: 6%;'>"+getProActStatusStr(result[index].status)+"</td>";

				if(isnull(result[index].createUser)) {
					tr += "<td style='width: 6%;'></td>";
				}else{
					tr += "<td style='width: 6%;'>" + result[index].createUser + "</td>";
				}
				tr+="<td style='width: 12%;' class='schedule_time'>"+formatDateTime(result[index].createTime)+"</td>";
                tr+="<td style='width: 12%;' class='schedule_time'>"+formatDateTime(result[index].lastUpdateTime)+"</td>";
				tr+="<td style='width: 6%;' class='schedule_operate'>";
				if(result[index].status == 1){
					tr+="<span class='schedule_change' proActId='"+result[index].id+"'>修改</span>";
				}else if(result[index].status == 2){
					tr+='<span class="time_adjust" onclick="updateEndTime(\''+result[index].id+'\',\''+result[index].name+'\',\''+formatDateTime(result[index].startTime)+'\')">时间调整</span>';
					// 进行中的可以暂停
					tr+="<span onclick='pauseProAct(this)' proActId='"+result[index].id+"'>暂停</span>";
				}else if(result[index].status == 4){
					// 暂停的可以启动和修改
					tr+="<span class='schedule_change' proActId='"+result[index].id+"'>修改</span>";
					tr+="<span onclick='activateProAct(this)' proActId='"+result[index].id+"'>启动</span>";
				}
				tr+="<span class='schedule_check' onclick='showDetail("+result[index].id+")'>查看</span>";
				tr+="</td></tr>"
			}
			$("#promotion_browse").html(tr);
			init_schedule_pagination(response)
		},
    });
}
// 暂停活动
function pauseProAct(obj){
	$(obj).attr("disabled","disabled")
	var proActId = $(obj).attr("proActId");
	var param = {proActId:proActId};
	ajax_post("../market/pro/act/pause", JSON.stringify(param), "application/json",
		function(response) {
			if(response.suc){
				layer.msg("暂停活动成功！",{icon:1,time:2000},function(){
					//getAllActivity(1);
					$("#promotion_schedule_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
				});
			}else{
				layer.msg("暂停活动失败，请稍后再试！",{icon:5,time:2000},function(){
					//getAllActivity(1);
					$("#promotion_schedule_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
				});
			}
		},
		function(XMLHttpRequest, textStatus) {
			layer.msg("暂停活动失败，请稍后再试！",{icon:5,time:2000});
		}
	);
}

function activateProAct(obj){
	$(obj).attr("disabled","disabled")
	var proActId = $(obj).attr("proActId");
	var param = {proActId:proActId};
	ajax_post("../market/pro/act/activate", JSON.stringify(param), "application/json",
		function(response) {
			if(response.suc){
				layer.msg("启动活动成功！",{icon:1,time:2000},function(){
					//getAllActivity(1);
					$("#promotion_schedule_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
				});
			}else{
				layer.msg("启动活动失败，请稍后再试！",{icon:5,time:2000},function(){
					//getAllActivity(1);
					$("#promotion_schedule_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
				});
			}
		},
		function(XMLHttpRequest, textStatus) {
			layer.msg("启动活动失败，请稍后再试！",{icon:5,time:2000});
		}
	);
}

function getProActStatusStr(status){
	switch(status){
		case 1:
			return "未开始";
		case 2:
			return "促销中";
		case 3:
			return "已过期";
		case 4:
			return "暂停";
	}
}

$("body").on("click",".schedule_change",function(){
	// 跳到编辑页面，菜单样式也要改变
    var pro_act_id = $(this).attr("proActId");
	var new_pro_act_menu = $('p[class="back-current"]').parent().find("p:eq(1)");
	$("p[onclick^='load_menu_content']").removeClass("back-current");
	new_pro_act_menu.addClass("back-current");
	load_menu_content(21,{'edit_pro_type':'edit_pro','pro_act_id':pro_act_id});
});

/**
 * 分页控件
 * @param page
 */
	function init_schedule_pagination(page) {
	if ($("#schedule_pagination")[0] != undefined) {
		$("#schedule_pagination").empty();
		laypage({
			cont: 'schedule_pagination',
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
					getAllActivity(obj.curr);
				}
			}
		});
	}

}

function red(text){
	return "<b style='color: red'>"+text+"</b>"
}

/**
 * 判断data是否为null/空字符串/undefined
 * @param data
 * @returns {Boolean}
 */
function isnull(data) {
	if (data == null || data == undefined || data == "") {
		return true;
	} else {
		return false;
	}
}

/**
 * 获取属性对应的字符串
 * @param attr
 * @returns {*}
 */
function getAttrStr(attr){
	if(isnull(attr)){
		return "未知属性";
	}
	var attrStr;
	switch (attr){
		case ATTR_GOODS:
			attrStr = "商品属性";
			break;
		case ATTR_SHOPPING_CART:
			attrStr = "购物车属性";
			break;
		case ATTR_USER:
			attrStr = "用户属性";
			break;
		default:
			attrStr = "未知属性";
			break;
	}
	return attrStr;
}
