/**
 * Created by Administrator on 2016/10/18.
 */
//定义全局变量
var layer = undefined;
var laypage = undefined;

//初始化全局变量
function init_auditModel(layerParam, laypageParam,BbcGrid) {
    //初始化全局变量
    layer = layerParam; 
    laypage = laypageParam;
    var grid = new BbcGrid();
    grid.initTable($("#model_audit_table"),modelSetting());
    $(".viewDetails_return").click(function () {//返回按钮
        $(".details_one_con").hide();
        $(".details_one").show();
        searchModel();
    });
    $("#searchButton").click(function(){//搜索按键
        searchModel();
    });
    $(".search_category .register-mehtod,#status").change(function(){
        searchModel();
    });
}

function insert_applys(list){
    $(".all_table tbody").empty("tr");
    var ulHTML = '';
    $.each(list,function(i,item){
        var statusDesc;
        var operation = "查看详情";
        if (item.status == 0) {
            statusDesc = "待审核";
            operation = "审核";
        } else if (item.status == 1) {
            statusDesc = "审核不通过";
        } else if (item.status == 2) {
            statusDesc = "审核通过"
        } else {
            statusDesc = "已取消";
        }
        ulHTML +=
            '<tr>'+
            '<td>'+item.account+'</td>'+
            '<td>'+deal_with_illegal_value(item.registerDateDesc)+'</td>'+
            '<td>'+item.createDateDesc+'</td>'+
            '<td>'+deal_with_illegal_value(item.remark)+'</td>'+
            '<td>'+(item.isBackRegister ? "自发注册" : "后台注册")+'</td>'+
            '<td>'+item.registerMan+'</td>'+
            '<td>'+statusDesc+'</td>'+
            '<td>' +
              '<div><a class=\"viewDetails detail_'+ item.id+'\" id=\"'+item.id+'\" onclick=\"showDetail(this)\">'+operation+'</a></div>' +
              (item.status == 2 ? '<div><a class=\"viewDetails\" id=\"'+item.id+'\" onclick=\"showOperationLog(this)\">操作日志</a></div>' : "") +
            '</td>'+
            '</tr>';
    });
    $(".all_table tbody").append(ulHTML);
}

//展示详情
function showDetail(obj){
    var applyId = $(obj).attr("id");
    $(".apply-detail .submit-audit").attr("id",applyId);
    $(".apply-detail input[type='hidden']").val(applyId);
    $(".be-change").hide().prev().show();
    ajax_get("../member/showDetail?id="+applyId,undefined, undefined,
        function (response) {
            if (response) {
                if (response.suc) {
                    var data = response.data;
                    $(".apply-detail .account").text(data.account);
                    $(".apply-detail input[name='tel']").val(data.account);
                    $(".apply-detail .register-date").text(deal_with_illegal_value(data.registerDateDesc));
                    $(".apply-detail .apply-date").text(data.createDateDesc);
                    $(".apply-detail .apply-remark").text(deal_with_illegal_value(data.applyRemark));
                    $("textarea[name=remark]").val(data.applyRemark);
                    $(".apply-detail .register-method").text(data.isBackRegister ? "后台注册":"自发注册");
                    $(".apply-detail .register-man").text(data.registerMan);
                    $(".apply-detail .salesman_id").val(data.salesmanId == null ? "" : data.salesmanId);
                    var ivo = response.ivo;
                    if(ivo){
                        $("#invoiceTitle").text(deal_with_illegal_value(ivo.invoiceTitle));
                        $("#invoiceTaxNumber").text(deal_with_illegal_value(ivo.invoiceTaxNumber));
                        $("#invoiceBank").text(deal_with_illegal_value(ivo.invoiceBank));
                        $("#invoiceBankAccount").text(deal_with_illegal_value(ivo.invoiceBankAccount));
                        $("#invoiceTel").text(deal_with_illegal_value(ivo.invoiceTel));
                        $("#invoiceCompanyAddr").text(deal_with_illegal_value(ivo.invoiceCompanyAddr));
                        $("input[name=invoiceTitle]").val(ivo.invoiceTitle);
                        $("input[name=invoiceTaxNumber]").val(ivo.invoiceTaxNumber);
                        $("input[name=invoiceBank]").val(ivo.invoiceBank);
                        $("input[name=invoiceBankAccount]").val(ivo.invoiceBankAccount);
                        $("input[name=invoiceTel]").val(ivo.invoiceTel);
                        $("input[name=invoiceCompanyAddr]").val(ivo.invoiceCompanyAddr);
                    }else{
                        $("#invoiceTitle").text("--");
                        $("#invoiceTaxNumber").text("--");
                        $("#invoiceBank").text("--");
                        $("#invoiceBankAccount").text("--");
                        $("#invoiceTel").text("--");
                        $("#invoiceCompanyAddr").text("--");
                        $("input[name=invoiceTitle]").val("");
                        $("input[name=invoiceTaxNumber]").val("");
                        $("input[name=invoiceBank]").val("");
                        $("input[name=invoiceBankAccount]").val("");
                        $("input[name=invoiceTel]").val("");
                        $("input[name=invoiceCompanyAddr]").val("");
                    }
                    var statusdesc = "";
                    if (data.status == 0) {
                        statusdesc = "待审核";
                        $(".apply-detail .submit-audit").show();
                        $(".apply-detail .audit-man").parent().hide();
                        $(".apply-detail .audit-remark").parent().hide();
                        $(".apply-detail .audit-reason").parent().hide();
                        $(".apply-detail .audit-time").parent().hide();
                        $(".apply-detail .handle-files").hide();
                    } else if (data.status == 1) {
                        statusdesc = "审核不通过";
                        $(".apply-detail .submit-audit").hide();
                        $(".apply-detail .audit-man").text(data.auditMan).parent().show();
                        $(".apply-detail .audit-remark").text(deal_with_illegal_value(data.auditRemark)).parent().show();
                        $(".apply-detail .audit-reason").text(data.auditReason).parent().show();
                        $(".apply-detail .audit-time").text(data.updateDateDesc).parent().show();
                        $(".apply-detail .handle-files").hide();
                    } else if (data.status == 2) {
                        statusdesc = "审核通过";
                        $(".apply-detail .submit-audit").hide();
                        $(".apply-detail .audit-man").text(data.auditMan).parent().show();
                        $(".apply-detail .audit-remark").text(deal_with_illegal_value(data.auditRemark)).parent().show();
                        $(".apply-detail .audit-reason").parent().hide();
                        $(".apply-detail .handle-files").show();
                        $(".apply-detail .audit-time").text(data.updateDateDesc).parent().show();
                        $(".apply-detail #change-files").show();
                        $(".apply-detail #sure-change").hide();
                    } else {
                        statusdesc = "已取消";
                        $(".apply-detail .submit-audit").parent().hide();
                        $(".apply-detail .audit-man").parent().hide();
                        $(".apply-detail .audit-remark").parent().hide();
                        $(".apply-detail .audit-reason").parent().hide();
                        $(".apply-detail .handle-files").hide();
                    }
                    //文件展示
                    var html = "";
                    $(".apply-detail .apply-files").empty("p");
                    $.each(data.files,function(i,item){
                        //html += '<a href=../member/watchAuidtFile?id='+item.id+' id=\"'+item.id+'\">'+item.typeDesc+'</a>';
                        //html += '<p class="file-list" id=\"'+item.id+'\"><a href=../member/watchAuidtFile?id='+item.id+' id=\"'+item.id+'\">'+item.typeDesc+'</a><input style="display: none;" type="file" name="'+item.type+'"></p>';
                        if (item.type == "goods-licence") {
                            html += '<p class="file-list" id=\"'+item.id+'\"><span"><a style="color: #333;" href=../member/watchAuidtFile?id='+item.id+' id=\"'+item.id+'\">'+item.typeDesc+'</a></span><input style="display: none;" type="file" name="'+item.type+'"></p>';
                        } else if (item.type == "business-licence"){
                            html += '<p class="file-list" id=\"'+item.id+'\"><span onclick="previewFile(this)">'+item.typeDesc+'</span><input style="display: none;" type="file" name="'+item.type+'"><input style="display: none;" class="select-lience" type="checkbox"><span style="display: none;">三证合一</span></p>';
                        } else {
                            html += '<p class="file-list" id=\"'+item.id+'\"><span onclick="previewFile(this)">'+item.typeDesc+'</span><input style="display: none;" type="file" name="'+item.type+'"></p>';

                        }
                    });
                    $(".apply-detail .apply-files").append(html);
                    $(".apply-detail .status").html(statusdesc);
                    $(".details_one").hide();
                    $(".details_one_con").show();
                    $(".file-list .select-lience").click(function(){//勾选三证合一
                        if ($(this).prop("checked")) {
                            $(".file-list input[name='organization-code']").hide();
                            $(".file-list input[name='tax-licence']").hide();
                        } else {
                            $(".file-list input[name='organization-code']").show();
                            $(".file-list input[name='tax-licence']").show();
                        }

                    })
                } else {
                    layer.msg(response.msg, {icon : 2, time : 2000});
                }
            }
        },
        function (xhr, status) {
        }
    );
}

//审核
function auditData(){
    layer.open({
        type: 1,
        title: "审核",
        area: ['400px', '240px'],
        shadeClose: true, //点击遮罩关闭
        content: '<p style="text-align: center;padding: 30px 5px;">审核之前请确认该分销商是否符合要求！</p>',
        btn: ['审核通过','审核不通过'],
        scrollbar: false,
        btn1:function(){
            layer.closeAll();
            var content = '<div class="modelAduitPopCss modelAduitYesPop">'+
                '<div>'+
                '<span><i class="red">*</i>业务员：</span>'+
                '<span>'+
                '<select class="salesman"><option>请选择</option></select>'+
                '</span>'+
                '</div>'+
                '<div>'+
                '<span style="vertical-align: top;"><i class="red">*</i>备注：</span>'+
                '<span>'+
                '<textarea class="pass-remark" rows="4" placeholder="1~300字以内"></textarea>'+
                '</span>'+
                '</div>'+
                '</div>';
            layer.open({
                type: 1,
                title: "审核通过",
                area: ['400px', '300px'],
                shadeClose: true, //点击遮罩关闭
                content: content,
                scrollbar: false,
                btn: ['确定', '取消'],
                success: function(){
                    var selectSalesman = $(".apply-detail .salesman_id").val();//在组织架构申请时选择的业务员id
                    var params = {
                        currPage: 1,
                        pageSize: 100000
                    };
                    ajax_post("../member/getSalesmans", params, undefined,
                        function (response) {
                            if (response.suc) {
                                var salesmans = response.page.list;
                                if (salesmans.length == 0) {
                                    layer.msg("暂无相关业务员，请前往组织架构添加", {icon : 6, time : 2000});
                                    return;
                                }
                                $(".modelAduitYesPop .salesman").empty("option");
                                var html = '<option>请选择</option>';
                                $.each(salesmans,function(i,item){
                                    if (item.id == selectSalesman) {
                                        html += '<option  selected=\"selected\" salesmanId="'+item.id+'">'+item.name+'</option>'
                                    } else {
                                        html += '<option salesmanId="'+item.id+'">'+item.name+'</option>'
                                    }
                                });
                                $(".modelAduitYesPop .salesman").append(html);
                            }
                        },
                        function (xhr, status) {
                        }
                    );
                },
                yes: function() {
                    var applyId = $(".apply-detail .submit-audit").attr("id");
                    var salesmanId = $(".modelAduitYesPop .salesman").find("option:selected").attr("salesmanId");
                    var remark = $(".modelAduitYesPop .pass-remark").val().trim();
                    if (!salesmanId) {
                        layer.msg("请先选择业务员", {icon : 6, time : 2000});
                        return;
                    }
                    var param = {
                        id : applyId,
                        salesmanId : salesmanId,
                        status : 2
                    };
                        if (!remark || remark.length > 299) {
                            layer.msg("请填写1-300字以内的备注", {icon : 6, time : 2000});
                            return;
                        }
                        param.remark = remark;
                    audit_operation(param);
                }
            });
        },
        btn2:function(){
            layer.closeAll();
            var content = '<div class="modelAduitPopCss modelAduitNoPop">'+
                '<div>'+
                '<span><i class="red">*</i>审核理由：</span>'+
                '<span>'+
                '<select id="audit-reason">'+
                '<option>请选择</option>'+
                '<option>不符合申请要求</option>'+
                '<option>提供资料不齐全或无效</option>'+
                '</select>'+
                '</span>'+
                '</div>'+
                '<div>'+
                '<span style="vertical-align: top"><i class="red">*</i>备注：</span>'+
                '<span>'+
                '<textarea class="no-pass-remark" rows="4" placeholder="1~300字以内"></textarea>'+
                '</span>'+
                '</div>'+
                '</div>';
            layer.open({
                type: 1,
                title: "审核不通过",
                area: ['400px', '300px'],
                shadeClose: true, //点击遮罩关闭
                content: content,
                scrollbar: false,
                btn: ['确定', '取消'],
                yes: function() {
                    var applyId = $(".apply-detail .submit-audit").attr("id");
                    var remark = $(".modelAduitNoPop .no-pass-remark").val().trim();
                    var reason = $(".modelAduitNoPop #audit-reason").val();
                    if (!remark || remark.length > 299) {
                        layer.msg("请填写1-300字以内的备注", {icon : 6, time : 2000});
                        return;
                    }
                    if (reason == "请选择") {
                        layer.msg("请选择审核理由", {icon : 6, time : 2000});
                        return;
                    }
                    var param = {
                        id : applyId,
                        status : 1,
                        reason : reason,
                        remark : remark
                    };
                    audit_operation(param);
                }
            });
        }
    });
}

//处理审核
function audit_operation(param){
    ajax_post("../member/auditApply", JSON.stringify(param), "application/json",
        function (response) {
            if (response) {
                response = JSON.parse(response);
                if (response.suc) {
                    layer.msg("审核成功", {icon : 1, time : 2000},function(){
                        layer.closeAll();
                        $(".detail_"+param.id).click();
                    });
                } else {
                    layer.msg(response.msg, {icon : 2, time : 2000});
                }
            }
        },
        function (xhr, status) {
        }
    );
}

function showOperationLog(obj) {
    var applyId = $(obj).attr("id");
    var goal = $(".operation-log-pop-box");
    goal.children().empty();
    layer.open({
        type: 1,
        title: '操作日志',
        btn: false,
        shadeClose: true,
        content: goal,
        area: ['450px', '300px'],
        move: false
    });
    ajax_get("../member/applyFileHistory?applyId="+applyId,undefined, undefined,
        function (response) {
            if (response) {
                if (response.suc) {
                    var aData = response.data;
                    var itemHTML = '';
                    for (var i = 0; i < aData.length; i++) {
                        itemHTML +=
                            '<p>' +
                            '<span>' + aData[i].operateTimeDesc + '</span>' +
                            '<span style="width: 220px;">' + aData[i].opdesc + '</span>'+
                        '</p>';
                    }
                    if (itemHTML == '') {
                        goal.children().append("该申请的文件没有变更历史");
                    } else {
                        goal.children().append(itemHTML);
                    }
                } else {
                    layer.msg(response.msg, {icon : 2, time : 2000});
                }
            }
        },
        function (xhr, status) {
        }
    );
}

//准备改变文件
function readyChangeFile(){
    $("#change-files").hide();
    $("#sure-change").show();
    $(".file-list input[type='file']").show();
    $(".file-list input[type='checkbox']").show();
    $(".file-list input[type='checkbox']").next().show();
    $(".be-change").show().prev().hide();
}

//修改申请文件
function changeFiles(){
    var $businessLicence = $(".file-list").find("input[name='business-licence']");
    if ($businessLicence.length != 0 && $businessLicence.val() != "") {
        var name = $businessLicence.val();
        if ($businessLicence[0].files[0].size > (2 * 1024 * 1024) ||
            !(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
            layer.msg("营业执照只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
            return false;
        }
    }
    var $organizationCode = $(".file-list").find("input[name='organization-code']");
    if ($organizationCode.length != 0 && $organizationCode.val() != "") {
        var name = $organizationCode.val();
        if ($organizationCode[0].files[0].size > (2 * 1024 * 1024) ||
            !(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
            layer.msg("组织机构代码只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
            return;
        }
    }
    var $taxLicence = $(".file-list").find("input[name='tax-licence']");
    if ($taxLicence.length != 0 && $taxLicence.val() != "") {
        var name = $taxLicence.val();
        if ($taxLicence[0].files[0].size > (2 * 1024 * 1024) ||
            !(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
            layer.msg("税务登记证只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
            return;
        }
    }
    var $taxpayerLicence = $(".file-list").find("input[name='taxpayer-licence']");
    if ($taxpayerLicence.length != 0 && $taxpayerLicence.val() != "") {
        var name = $taxpayerLicence.val();
        if ($taxpayerLicence[0].files[0].size > (2 * 1024 * 1024) ||
            !(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
            layer.msg("一般纳税人资格证只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
            return;
        }
    }
    var $foodLicence = $(".file-list").find("input[name='food-licence']");
    if ($foodLicence.length != 0 && $foodLicence.val() != "") {
        var name = $foodLicence.val();
        if ($foodLicence[0].files[0].size > (2 * 1024 * 1024) ||
            !(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
            layer.msg("食品流通许可证只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
            return;
        }
    }
    var $goodsLicence = $(".file-list").find("input[name='goods-licence']");
    if ($goodsLicence.length != 0 && $goodsLicence.val() != "") {
        var name = $goodsLicence.val();
        if ($goodsLicence[0].files[0].size > (2 * 1024 * 1024) ||
            !(name.indexOf(".doc") != -1 || name.indexOf(".docx") != -1
                || name.indexOf(".dot") != -1 || name.indexOf(".dotx") != -1
                || name.indexOf(".docm") != -1 || name.indexOf(".bmp") != -1
                || name.indexOf(".gif") != -1 || name.indexOf(".jpeg") != -1
                || name.indexOf(".jpg") != -1 || name.indexOf(".tiff") != -1
                || name.indexOf(".png") != -1 || name.indexOf(".pdf") != -1
            )) {
            layer.msg("收货授权书只支持pdf、doc、docx、dot、dotx、docm、bmp、gif、jpeg（jpg）、tiff、png 十一种格式，且大小不能大于2MB", {icon: 0, time: 2000});
            return;
        }
    }
    var invoiceTel = $("input[name=invoiceTel]").val();
    if(invoiceTel&&!checkTel(invoiceTel)){
        layer.msg("请正确填写联系电话", {icon : 5, time : 2000});
        return;
    }
    var options = {
        //url: url,                 //默认是form的action
        //type: type,               //默认是form的method（get or post）
        //dataType: json,           //html(默认), xml, script, json...接受服务端返回的类型
        //clearForm: true,          //成功提交后，清除所有表单元素的值
        //resetForm: true,          //成功提交后，重置所有表单元素的值
        //target: '#output',          //把服务器返回的内容放入id为output的元素中
        //timeout: 3000,               //限制请求的时间，当请求大于3秒后，跳出请求
        //提交前的回调函数
        beforeSubmit: function(formData, jqForm, options){
            //formData: 数组对象，提交表单时，Form插件会以Ajax方式自动提交这些数据，格式如：[{name:user,value:val },{name:pwd,value:pwd}]
            //jqForm:   jQuery对象，封装了表单的元素
            //options:  options对象
            //比如可以再表单提交前进行表单验证

            var fileValue = undefined;
            for (var i = 0;i < formData.length;i++) {
                if (formData[i].name == "business-licence") {
                    fileValue = formData[i].value;
                }
            }
            if ($(".select-lience").prop("checked")) {//选中了三合一，组织机构代码、税务登记证和营业执照一样
                for (var i = 0;i < formData.length;i++) {
                    if (formData[i].name == "business-licence") {
                        fileValue = formData[i].value;
                    }
                    if (formData[i].name == "organization-code" || formData[i].name == "tax-licence") {
                        formData[i].value = fileValue;
                    }
                }
                formData.push({name:"isCombine",value:true});
            } else {
                formData.push({name:"isCombine",value:false});
            }
        },
        //提交成功后的回调函数
        success: function(data,status,xhr,$form){
            data = JSON.parse(data);
            if (data.suc) {
                layer.msg("修改申请文件成功", {icon : 6, time : 4000},function(){
                    $(".detail_"+$("#files-form input[name='applyId']").val()).click();
                });
            } else {
                this.clearForm = false;
                layer.msg(data.msg, {icon : 5, time : 2000});
            }
        },
        error: function(xhr, status, error, $form){},
        complete: function(xhr, status, $form){}
    };
    $("#files-form").ajaxSubmit(options);
}

//预览文件
function previewFile(obj){
    var fileId = $(obj).parents(".file-list").attr("id");
    var random = "&abc"+new Date().getTime()+"="+new Date().getTime();
    layer.open({
        type: 1,
        title: false,
        area: ['660px', 'auto'],
        content: '<div class="banner_add_pop" id="addBanner" style="display: block;height: 440px;text-align: center;"><img style="height: 100%" src="../member/watchAuidtFile?id='+fileId+random+'"></div>'
    });
    $(".layui-layer-content").css("padding","10px")
}


function modelSetting() {
    var setting = {
        url:"/member/getApplys",
        datatype : "json", // 返回的数据类型
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        colNames:[
            "手机号","注册时间","申请时间","备注","注册方式","注册人","状态","操作"
        ],
        colModel:[{name:"account",align:"center",sortable:false},
            {name:"registerDateDesc",index:"register_date",align:"center",sortable:true},
            {name:"createDateDesc",index:"create_date",align:"center",sortable:true},
            {name:"applyRemark",align:"center",sortable:false},
            {name:"isBackRegister",align:"center",sortable:false,
                formatter:function(cellvalue, options, rowObject){
                    return cellvalue?"后台注册":"自发注册";
                }
            },
            {name:"registerMan",align:"center",sortable:false},
            {name:"statusDesc",align:"center",sortable:false},
            {name:"opt",align:"center",sortable:false ,formatter:function(cellvalue, options, item){
                var operation = item.status == 0?"审核":"查看详情";
                return  '<div><a class=\"viewDetails detail_'+ item.id+'\" id=\"'+item.id+'\" onclick=\"showDetail(this)\">'+operation+'</a></div>' +
                    (item.status == 2 ? '<div><a class=\"viewDetails\" id=\"'+item.id+'\" onclick=\"showOperationLog(this)\">操作日志</a></div>' : "");
                }
            }
        ],
        viewrecords : true,
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#pagination_audit",//分页
        pagerpos : "center",
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        caption:"模式审核",//表名称
        jsonReader:{
            root: "page.list",  //数据模型
            page: "page.currPage",//数据页码
            total: "page.totalPage",//数据总页码
            records: "page.rows",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        },
        serializeGridData : function(postData) {
            return JSON.stringify($.extend(postData,getModelAuditParams()));
        },
        gridComplete:function(){
            $("#model_audit_table").on("click","a",function(){
                $(this).parents("td").click();
            });
        }
    }
    return setting;
}

function getModelAuditParams(){
    var params = {
        currPage : $('#model_audit_table').getGridParam('page'),
        pageSize : $('#model_audit_table').getGridParam('rowNum'),
        search : $("#searchInput").val().trim(),
        sregdate : $("#sregdate").val().trim(),
        eregdate : $("#eregdate").val().trim(),
        sapplydate : $("#sapplydate").val().trim(),
        eapplydate : $("#eapplydate").val().trim()
    };
    var registeMethod = $(".search_category .register-mehtod").val();
    if (registeMethod == 1) {
        params.isbackRegister = false;
    } else if (registeMethod == 2) {
        params.isbackRegister = true;
    }
    var status = $(".search_category #status").val();
    if (status != -1) {
        params.status = status;
    }
    return params;
}

function searchModel(){
    // 拿到原有的，清除掉
    $("#model_audit_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}
