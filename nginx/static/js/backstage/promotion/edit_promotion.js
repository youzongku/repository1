/**
 * Created by Administrator on 2016/7/26.
 */
/**商品属性*/
var ATTR_GOODS = "1";
/**购物车属性*/
var ATTR_SHOPPING_CART = "2";
/**用户属性*/
var ATTR_USER = "3";
var layer = undefined;
var laypage = undefined;
//初始化
function init_edit_pro(layerParam, laypageParam, proActIdParam) {
    //初始化全局变量
    layer = layerParam;
    laypage = laypageParam;
    init(proActIdParam);
}

// 数据回显
function init(proActId){
    update_loadProAct(proActId);
}
// 重置
$("body").on("click",".update_reset_step_button",function(){
    var proActId = $("#pro_act_id").val();
    update_loadProAct(proActId);
});

// 数据回显用的
function update_loadProAct(proActId){
    var param = {"proActId":proActId};
    ajax_post("../market/pro/act/detail", JSON.stringify(param), "application/json",
        function(response) {
            if(!response.success){
                layer.msg("获取促销活动数据失败！",{icon:5,time:2000});
                return;
            }
            var proAct = response.resultObject;
            $("#pro_act_id").val(proAct.id);
            $("#pro_act_name").val(proAct.name);
            $("#pro_act_status").val(proAct.status);
            $("#pro_act_status_str").html("未开始");
            if(proAct.status == 2){
                $("#pro_act_status_str").html("促销中");
            }else if(proAct.status == 4){
                $("#pro_act_status_str").html("暂停");
            }
            $("#pro_act_name_hidden").val(proAct.name);
            $("#pro_act_description").val(proAct.description);
            $("#start_time").val(formateDate(proAct.startTime));
            $("#end_time").val(formateDate(proAct.endTime));

            // 模式
            var modeIds = new Array()
            for(var i in proAct.disModeList){
                modeIds.push(proAct.disModeList[i].disModeId)
            }

            $("#mode_ids_hidden").val(modeIds.toString())

            // 获取mode
            ajax_get("../member/getMode", null, null, function(data){
                if(!isnull(data)){
                    // [{"id": 1,"disMode":"电商" },{"id": 2,"disMode": "经销商"},{"id": 3,"disMode": "商超"},{"id": 4,"disMode": "自贸"}]
                    var mode_html = '';
                    for(var i in data){
                        // mode回显
                        if(containModeId(modeIds,data[i].id)){
                            mode_html += "<input checked type='checkbox' disMode='"+data[i].disMode+"' value='"+data[i].id+"'>"+data[i].disMode;
                        }else{
                            mode_html += "<input type='checkbox' disMode='"+data[i].disMode+"' value='"+data[i].id+"'>"+data[i].disMode;
                        }
                    }
                    $("#mode_span").html(mode_html);
                }
            });
        },
        function(XMLHttpRequest, textStatus) {
            layer.msg("获取促销活动数据失败！",{icon:5,time:2000});
        }
    );
}

function containModeId(proModeIdArray, modeId){
    if(proModeIdArray.length==0){
        return false;
    }
    for(var j in proModeIdArray){
        if(proModeIdArray[j] == modeId){
            return true;
        }
    }
}

function update_laydate_start_time(){
    laydate({
        elem: '#start_time', //需显示日期的元素选择器
        format: 'YYYY-MM-DD hh:mm:ss', //日期格式
        istime: true, //是否开启时间选择
        isclear: true, //是否显示清空
        istoday: true, //是否显示今天
        issure: true, //是否显示确认
        festival: true, //是否显示节日
        min: '2016-01-01 00:00:00', //最小日期
        max: '2099-12-31 23:59:59', //最大日期
        //start: '2014-6-15 23:00:00',    //开始日期
        fixed: false, //是否固定在可视区域
        //zIndex: 99999999, //css z-index
        choose: function (date){
            if(new Date(date)>new Date()){
                if($("#pro_act_status").val() != '4'){
                    $("#pro_act_status").val(1);
                    $("#pro_act_status_str").text("未开始")
                }
            }else if(new Date(date) <= new Date()){
                if($("#pro_act_status").val() != '4') {
                    $("#pro_act_status").val(2);
                    $("#pro_act_status_str").text("促销中")
                }
            }
        }
    });
}
function update_laydate_end_time(){
    laydate({
        elem: '#end_time', //需显示日期的元素选择器
        format: 'YYYY-MM-DD hh:mm:ss', //日期格式
        istime: true, //是否开启时间选择
        isclear: true, //是否显示清空
        istoday: true, //是否显示今天
        issure: true, //是否显示确认
        festival: true, //是否显示节日
        min: '2016-01-01 00:00:00', //最小日期
        max: '2099-12-31 23:59:59', //最大日期
        //start: '2014-6-15 23:00:00',    //开始日期
        fixed: false, //是否固定在可视区域
        //zIndex: 99999999, //css z-index
        choose: function(date){//选择好日期的回调
            var start_time = $("#start_time").val();
            if(isnull(start_time)){
                layer.msg("请选择促销开始时间",{icon:2,time:2000});
                $("#end_time").val("");
                return;
            }
            var now_time = new Date().getTime();
            if(Date.parse(start_time)<now_time && new Date(date).getTime()<now_time){
                layer.msg("不能创建过期的活动", {icon: 2, time: 2000});
                $("#end_time").val("");
                return;
            }
        }
    });
}

$("body").on("click",".update_promotion_cate",function(){
    update_prepareDatas4AddProType();
    $('.update_promotion_first_step').hide();
    $('.update_promotion_second_step').show();
    $('.update_promotion_third_step').hide();
});
// 第一步
$('body').on("click", ".update_next_step_button", function () {
    var pro_act_name = $.trim($("#pro_act_name").val());
    var pro_act_description = $("#pro_act_description").val();
    var start_time = $("#start_time").val();
    var end_time = $("#end_time").val();
    var pro_act_status = $("#pro_act_status").val();
    var modeIdArray = new Array();
    var modeNameArray = new Array();
    $("#mode_span").find("input:checked").each(function(i,e){
        modeIdArray.push($(e).val());
        modeNameArray.push($(e).attr("disMode"));
    });
    if (isnull(pro_act_name)) {
        layer.msg("促销名称不能为空", {icon: 2, time: 2000});
        $("#pro_act_name").focus();
        return;
    }
    if(pro_act_name.length>20){
        layer.msg("促销名称不能超过20个字符", {icon: 2, time: 2000});
        $("#pro_act_name").focus();
        return;
    }
    if (isnull(pro_act_description)) {
        layer.msg("描述不能为空", {icon: 2, time: 2000});
        $("#pro_act_description").focus();
        return;
    }
    if(modeIdArray.length==0){
        layer.msg("请选择分销渠道", {icon: 2, time: 2000});
        return;
    }
    if (isnull(start_time)) {
         layer.msg("促销开始时间不能为空", {icon: 2, time: 2000});
         $("#start_time").focus();
         return;
    }
    if (isnull(end_time)) {
        layer.msg("促销结束时间不能为空", {icon: 2, time: 2000});
        $("#end_time").focus();
        return;
    }

    if (Date.parse(start_time) > Date.parse(end_time)) {
        layer.msg("开始时间不能晚于结束时间", {icon: 2, time: 2000});
        return;
    }
    var now_time = new Date().getTime();
    if(Date.parse(start_time)<now_time && Date.parse(end_time)<now_time){
        layer.msg("不能创建过期的活动", {icon: 2, time: 2000});
        return;
    }
    var last_update_user = "";
    isaulogin(function (email) {
        last_update_user = email;
    });
    // 更新促销活动基础信息
    var data = {
        "id" : $("#pro_act_id").val(),
        "name" : pro_act_name,
        "description": pro_act_description,
        "modeIds": modeIdArray.toString(),
        "modeNames": modeNameArray.toString(),
        "startTime" : start_time,
        "endTime":end_time,
        "status":pro_act_status,
        "lastUpdateUser": last_update_user
    };
    $.ajax({
        url: "../market/pro/act/update",
        type: "POST",
        data: JSON.stringify(data),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            if (response.success) {
                // 保存好新改的名字
                $("#pro_act_name_hidden").val(pro_act_name);
                $("#mode_ids_hidden").val(modeIdArray.toString());
                // 添加成功后才下一步
                $('.update_promotion_first_step').hide();
                //$('.update_promotion_second_step').show();
                $('.update_promotion_second_step').hide();
                $('.update_promotion_third_step').show();

                // 加载促销活动的活动实例
                var proActId = $("#pro_act_id").val();
                loadProActi(proActId);
            } else {
                layer.msg(response.errorMessage, {icon: 5, time: 2000});
            }
        },
        error: function () {
            layer.msg("修改失败", {icon: 5, time: 2000});
        },
    });
});

function update_prepareDatas4AddProType(){
    // 条件属性
    getAttrs(function(data){
        var options = "<option value=''>请选择条件属性</option>";
        for(var key in data){
            options += "<option value='"+key+"'>"+data[key]+"</option>";
        }
        $("#pro_type_condt_attr_select").html(options);
    });
    update_loadProTypes();
}

// 加载促销类型列表
function update_loadProTypes(obj,red){
    var currPage = "1";
    var pageSize = $("#proType_pageSize").val();
    if (pageSize == undefined || pageSize == "" || pageSize == 0) {
        pageSize = 10;
    }
    if (red) {
        currPage = obj.curr;
    }
    var param = {
        curr:currPage,
        pageSize:pageSize,
        proTypeName:$("#pro_type_name").val(),
        attr:$("#pro_type_condt_attr_select").val()
    };
    ajax_post("../market/pro/load/protypes", param, undefined,
        function(response) {
            var result = response.result;
            var totalPage = response.totalPage;
            var currPage = response.currPage;
            var totalCount = response.rows;
            $("#update_proTypeTotal").text(totalCount);
            $("#update_proTypePages").text(totalPage);
            var tbody = $("#pro_type_table tbody");
            tbody.empty();
            if(totalCount>0){
                var seq = (currPage-1) * pageSize;
                for(var i = 0;i < result.length;i++ ){
                    seq++;
                    var proType = result[i];
                    var proTypeHtml = '';
                    proTypeHtml += '<tr>';
                    proTypeHtml += '<td>'+seq+'</td>';
                    proTypeHtml += '<td>'+proType.name+'</td>';
                    var description = '';
                    if(!isnull(proType.description)){
                        var arr = proType.description.split("=");
                        description += '<p>条件：'+arr[0]+'</p>';
                        description += '<p>优惠：'+arr[1]+'</p>';
                    }
                    proTypeHtml += '<td class="pro_intro">'+description+'</td>';
                    proTypeHtml += '<td>'+getAttrStr(proType.attr+'')+'</td>';
                    proTypeHtml += '<td class="select_operate">';
                    proTypeHtml += '    <span class="select_add select_update" pro_type_id="'+proType.id+'">添加</span>';
                    proTypeHtml += '</td>';
                    proTypeHtml += '</tr>';
                    tbody.append(proTypeHtml);
                }
            }else{
                layer.msg("没有符合条件的促销类型信息",{icon:5,time:2000});
            }
            update_init_pro_type_pagination(totalPage, currPage);
        },
        function(XMLHttpRequest, textStatus) {
            layer.msg("获取促销类型数据失败！",{icon:5,time:2000});
        }
    );
}

//初始化促销类型分页栏
function update_init_pro_type_pagination(pages, currPage) {
    if ($("#update_proType_pagination")[0] != undefined) {
        $("#update_proType_pagination").empty();
        laypage({
            cont: 'update_proType_pagination',
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
                    update_loadProTypes(obj, true);
                }
            }
        });
    }
}

// 添加选择的促销类型
$('body').on("click", ".select_update", function () {
    // 点击添加促销类型
    var pro_type_id = $(this).attr("pro_type_id");
    // 将促销类型和促销活动绑定，形成一个促销活动实例
    var proActId = $("#pro_act_id").val();
    var param = {
        proActId: proActId, // 促销活动id
        proTypeId: pro_type_id
    };
    // 创建活动实例
    ajax_post("../market/pro/actinst/create", JSON.stringify(param), "application/json",
        function(response) {
            if(response.success){
                // 第3步
                loadProActi(proActId);
                $('.update_promotion_second_step').hide();
                $('.update_promotion_third_step').show();
            }
        },
        function(XMLHttpRequest, textStatus) {
            layer.msg("添加促销类型失败！",{icon:5,time:2000});
        }
    );
});

// 最后创建促销活动
$('body').on("click", ".update_promotion", function () {
    var pro_act_id = $("#pro_act_id").val();
    var pro_act_name = $("#pro_act_name_hidden").val();
    var param = {proActId:pro_act_id,proActName:pro_act_name};
    ajax_post("../market/pro/act/update/notdel", JSON.stringify(param), "application/json",
        function(response) {
            if(!response.success){
                layer.msg(response.errorMessage,{icon:5,time:2000});
                return;
            }
            layer.msg("修改成功，即将进入列表页",{icon:1,time:2000},function(){
                gotoProActList();
            });
        },
        function(XMLHttpRequest, textStatus) {
            layer.msg("修改失败！",{icon:5,time:2000});
        }
    );
});

// 给促销类型设置参数弹窗
$('.content-R-twentytwo').off("click", ".set_dynamic_parameter")
$('.content-R-twentytwo').on("click", ".set_dynamic_parameter", function () {
    $('.update_promotion_third_step').show();
    //$('.subtotal_parameter_box').show();
    var condtInstId = $(this).attr("condtInstId")
    doSetDynamicCondtsPvlgValue(condtInstId)
});