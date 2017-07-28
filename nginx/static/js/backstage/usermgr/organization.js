//定义全局变量
var layer = undefined;
var laypage = undefined;
var selectid = 0;//当前被选中的节点id
var selectlevel = 0;//表示当前被选中的级别
var selectName = undefined;//被选中的节点名字
var parentName = undefined;//父节点名字
var parentId = undefined;//父节点id

//初始化组织架构
function init_organization(layerParam, laypageParam ) {
    //初始化全局变量
    layer = layerParam;
    laypage = laypageParam;
    loadOrganization();
}

//加载组织架构树
function loadOrganization(){
    var zTree;
    var setting = {
        //checkable : true,
        callback: {
            onClick:zTreeOnClick
        },
        view: {
            showIcon: false,
            dblClickExpand: true
        },
        //check: {checkbox
        //    enable: true
        //},

        checkType : { "Y": "p", "N": "s" },
        async: {
            enable: true,
            url:"/member/getOrganization",
            autoParam:["id"]
        }
    };
    var zNodes = [
        {id:0, name:"组织架构",isParent:true}
    ];
    $.fn.zTree.init($("#organization_tree"), setting, zNodes);
}

//组织节点点击所触发的函数
function zTreeOnClick(event, treeId, treeNode) {
    $('.related_operation').show();
    $('.related_distract').hide();
    $(".organizational_table tbody").empty("tr");
    $(".add_structure_pop input").eq(0).val("");
    $(".edit_structure_pop input").eq(0).val("");
    selectlevel = treeNode.level;
    selectid = treeNode.id;
    selectName = treeNode.name;
    var parentNode = treeNode.getParentNode();
    if (parentNode == null){
        parentName = "";
        parentId = undefined;
    } else {
        parentName = parentNode.name;
        parentId = parentNode.id;
    }
    if (selectid == 0) {
        $(".business_message").data("headerId","").hide();
        getSalesman(undefined, 1);
    } else {
        getHeaderInfo();
    }
}

//新增组织架构
$('body').on("click", ".add_structure", function () {
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['460px', '350px'],
        content: $('.add_structure_pop'),
        btn:['保存','取消'],
        title: '新增架构',
        success:function(layero, index){
            $(".layui-layer-content").css('height','263px');
            $(".add_structure_pop .parent_node").val(selectName);
            $(".add_structure_pop .node_name").val("");
            $(".add_structure_pop .node_header").val("");
            $(".add_structure_pop .node_headerTel").val("");
        },
        //i和currdom分别为当前层索引、当前层DOM对象
        yes: function(i, currdom) {
            var name = $(".add_structure_pop .node_name").val().trim();
            var headerName = $(".add_structure_pop .node_header").val().trim();
            var headerTel = $(".add_structure_pop .node_headerTel").val().trim();
            if (undefined == name || "" == name) {
                layer.msg("区域名称不能为空", {icon : 5, time : 2000});
                return;
            }
            if (undefined == headerName || "" == headerName) {
                layer.msg("区域负责人不能为空", {icon : 5, time : 2000});
                return;
            }
            if (undefined == headerTel || "" == headerTel) {
                layer.msg("联系电话不能为空", {icon : 5, time : 2000});
                return;
            }
            if (!checkTel(headerTel)) {
                layer.msg("联系电话格式不正确", {icon : 5, time : 2000});
                return;
            }
            var flag = getOrganizationByName(name,selectid);//验证节点名称的唯一性
            if (!flag) {
                return;
            }
            var params = {
                parentid : selectid,
                name : name,
                headerName : headerName,
                headerTel : headerTel,
                isparent : false,
                nodeType : $("#node_type input[name='nodeType']:checked").attr("tag"),
                level: selectlevel + 1
            };
            ajax_post("../member/saveOrganization", params, undefined,
                function (response) {
                    if (response.suc) {
                        layer.close(i);
                        layer.msg('您的组织区域添加成功！', {icon : 1, time : 2000}, function() {
                            loadOrganization();
                            $(".organizational_table tbody").empty("tr");
                            //每次加载组织树时，就会默认没有选中区域节点，所以全局变量归零
                            selectlevel = 0;
                            selectid = 0;
                            parentName = undefined;
                            selectName = undefined;
                            parentId = undefined;
                            $(".business_message").hide();
                        });
                    } else {
                        layer.msg(response.msg, {icon: 2, time: 1000});
                    }
                },
                function (xhr, status) {
                }
            );
        }
    });
});

//编辑架构
$('body').on("click", ".edit_structure", function () {
    if (selectlevel == 0) {//说明没有选中或者选中的为顶层
        layer.msg("请选中【组织架构栏】以下的区域进行编辑", {icon : 5, time : 2000});
        return;
    }
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['460px', '350px'],
        content: $('.edit_structure_pop'),
        btn:['修改','取消'],
        title: '编辑架构',
        success:function(layero, index){
            $(".edit_structure_pop input").eq(0).val(parentName);
            $(".edit_structure_pop input").eq(1).val($(".curSelectedNode").attr("title"));
            $(".edit_structure_pop input").eq(2).val($(".business_message em").eq(0).html());
            $(".edit_structure_pop input").eq(3).val($(".business_message em").eq(1).html());
        },
        //i和currdom分别为当前层索引、当前层DOM对象
        yes: function(i, currdom) {
            var name = $(".edit_structure_pop input").eq(1).val().trim();
            var headerName = $(".edit_structure_pop input").eq(2).val().trim();
            var headerTel = $(".edit_structure_pop input").eq(3).val().trim();
            var headerId = $(".business_message").data("headerId");
            if (undefined == name || "" == name) {
                layer.msg("区域名称不能为空", {icon : 5, time : 2000});
                return;
            }
            if (undefined == headerName || "" == headerName) {
                layer.msg("区域负责人不能为空", {icon : 5, time : 2000});
                return;
            }
            if (undefined == headerTel || "" == headerTel) {
                layer.msg("联系电话不能为空", {icon : 5, time : 2000});
                return;
            }
            if (!checkTel(headerTel)) {
                layer.msg("联系电话格式不正确", {icon : 5, time : 2000});
                return;
            };
            var flag = true;
            if ($(".curSelectedNode").attr("title") != $(".edit_structure_pop input").eq(1).val()) {
                flag = getOrganizationByName(name, parentId);//验证节点名称的唯一性
            }
            if (!flag) {
                return;
            }
            var params = {
                id : selectid,
                name : name,
                headerName : headerName,
                headerTel : headerTel,
                headerId : headerId
            };
            ajax_post("../member/changeOrganization", params, undefined,
                function (response) {
                    if (response.suc) {
                        layer.close(i);
                        layer.msg('您的组织区域更新成功！', {icon : 1, time : 2000}, function() {
                            loadOrganization();
                            $(".organizational_table tbody").empty("tr");
                            $(".business_message").hide();
                            //每次加载组织树时，就会默认没有选中区域节点，所以全局变量归零
                            selectlevel = 0;
                            selectid = 0;
                            parentName = undefined;
                            selectName = undefined
                            parentId = undefined;
                        });
                    } else {
                        layer.msg(response.msg, {icon: 2, time: 1000});
                    }
                },
                function (xhr, status) {
                }
            );
        }
    });
});

//根据名称查询架构
function getOrganizationByName(name,paretnId){
    var flag = true;
    var param = {
        name : name,
        parentid : paretnId
    };
    ajax_post_sync("../member/checkOrganization", param, undefined,
        function (response) {
            if (response.code == 4) {
                layer.msg("此节点名称已经存在，请重命名", {icon : 5, time : 2000});
                flag = false
            } else if (response.code == 1 || response.code == 2){
                layer.msg(response.msg, {icon : 5, time : 2000});
                flag = false
            }
        },
        function (xhr, status) {
        }
    );
    return flag;
}

//删除架构
$('body').on("click", ".del_structure", function () {
    if (selectlevel == 0) {//说明没有选中或者选中的为顶层
        layer.msg("请选中【组织架构栏】以下的区域进行删除", {icon : 5, time : 2000});
        return;
    }
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['350px', '180px'],
        content: $('.del_structure_pop'),
        btn:['删除','取消'],
        title: '删除架构',
        success:function(layero, index){
            $(".del_structure_pop b").html(selectName);
        },
        //i和currdom分别为当前层索引、当前层DOM对象
        yes: function(i, currdom) {
            var headerId = $(".business_message").data("headerId");
            var param = {
                id : selectid,
                headerId : headerId,
                parentId : parentId
            };
            ajax_post("../member/removeOrganization", param, undefined,
                function (response) {
                    if (response.suc) {
                        layer.close(i);
                        layer.msg('删除成功！', {icon : 1, time : 2000}, function() {
                            loadOrganization();
                            $(".organizational_table tbody").empty("tr");
                            $(".business_message").hide();
                            //每次加载组织树时，就会默认没有选中区域节点，所以全局变量归零
                            selectlevel = 0;
                            selectid = 0;
                            parentName = undefined;
                            selectName = undefined;
                            parentId = undefined;

                        });
                    } else {
                        layer.msg(response.msg, {icon: 2, time: 1000});
                    }
                },
                function (xhr, status) {
                    console.error("status-->" + status);
                }
            );
        }
    });
});

//新增员工
$('body').on("click", ".new_operation", function () {
    if (selectlevel == 0) {//说明没有选中或者选中的为顶层
        layer.msg("请选中【组织架构栏】以下的区域新增员工", {icon : 5, time : 2000});
        return;
    }
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['460px', '280px'],
        content: $('.new_operation_pop'),
        btn:['保存','取消'],
        title: '新增员工',
        success:function(layero, index){
            $(".new_operation_pop input").eq(0).val("");
            $(".new_operation_pop input").eq(1).val("");
            $(".new_operation_pop input").eq(2).val("");
        },
        //i和currdom分别为当前层索引、当前层DOM对象
        yes: function(i, currdom) {
            var name = $(".new_operation_pop input").eq(0).val().trim();
            var erp = $(".new_operation_pop input").eq(1).val().trim();
            var tel = $(".new_operation_pop input").eq(2).val().trim();
            var headerId = $(".business_message").data("headerId");
            if (undefined == name || "" == name) {
                layer.msg("员工姓名不能为空", {icon : 5, time : 2000});
                return;
            }
            if (undefined == erp || "" == erp) {
                layer.msg("ERP账号不能为空", {icon : 5, time : 2000});
                return;
            }
            if (undefined == tel || "" == tel) {
                layer.msg("联系电话不能为空", {icon : 5, time : 2000});
                return;
            }
            if (!checkTel(tel)) {
                layer.msg("联系电话格式不正确", {icon : 5, time : 2000});
                return;
            }
            var params = {
                name : name,
                erp : erp,
                tel : tel,
                headerId : headerId
            };
            ajax_post("../member/saveSalesMan", params, undefined,
                function (response) {
                    if (response.suc) {
                        layer.close(i);
                        layer.msg('新增员工成功！', {icon : 1, time : 2000}, function() {
                            getSalesman($(".business_message").data("headerId"), 1);
                        });
                    } else {
                        layer.msg(response.msg, {icon: 2, time: 1000});
                    }
                },
                function (xhr, status) {
                }
            );
        }
    });
});

//新增分销商---后台注册
$('body').on("click", ".addDistributor", function () {
    if ($(".salesman_list .chooseSalesman").length == 0) {
        layer.msg("请先添加员工", {icon : 6, time : 2000});
        return;
    }
    var choosed = $(".salesman_list input[name='register']:checked");//选择的员工
    if (choosed.length == 0) {
        layer.msg("请先勾选员工", {icon : 6, time : 2000});
        return;
    }
    var salesmanId = choosed.parent().parent().attr("data-id");
    var salesmanErp = choosed.parent().next().next().html();
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['530px', '570px'],
        content: '<form enctype="multipart/form-data" method="post" action="/member/backRegisterApply" id="audit_form" class="modelAduitPopCss organAccountPop">'+
                    '<div>'+
                    '<span><i class="red">*</i>分销渠道：</span>'+
                    '<span>' +
                    '<select class="model">' +
                    '<option>电商</option>' +
                    '<option>经销商</option>' +
                    '</select>' +
                    '</span>'+
                    '</div>'+
                    '<div>'+
                    '<span><i class="red">*</i>手机号：</span>'+
                    '<span>' +
                    '<input class="telphone"  type="text"/>' +
                    '</span>'+
                    '</div>'+
                    '<div>'+
                    '<span><i class="red">*</i>登录密码：</span>'+
                    '<span>' +
                    '<input class="password" type="password"/>' +
                    '<input style="display:none;" name="salesmanErp" type="text" value="'+salesmanErp+'"/>' +
                    '<input style="display:none;" name="salesmanId" type="text" value="'+salesmanId+'"/>' +
                    '</span>'+
                    '</div>'+
                    '<div class="agency" id="tips" style="display: none;"><span style="color: red;margin-left: 80px;">附件上传需小于等于2M</span></div>'+
                    '<div class="agency" style="display: none;">'+
                    '<span><i class="red">*</i>收货授权书<b class="f-12"></b> ：</span>'+
                    '<span>' +
                    '<em class="btn-file file"><sapn>上传附件</sapn><input type="file" name="goods-licence"/></em>' +
                    '</span><a href="../files/通淘国际--收货授权书.docx" style="margin-left: 55px;">模板下载</a>'+
                    '</div>'+
                    '<div class="agency" style="display: none;">'+
                    '<span><i class="red"></i>营业执照<b class="f-12"></b> ：</span>'+
                    '<span>' +
                    '<em class="btn-file file"><sapn>上传附件</sapn><input type="file" name="business-licence"/></em>' +
                    '</span>'+
                    '<label style="margin-left: 19px;"><input style="cursor: pointer;" class="select-lience" type="checkbox">三证合一</labei>' +
                    '</div>'+
                    '<div class="agency common-file" style="display: none;">'+
                    '<span><i class="red"></i>组织机构代码<b class="f-12"></b> ：</span>'+
                    '<span>' +
                    '<em class="btn-file file"><sapn>上传附件</sapn><input type="file" name="organization-code"/></em>' +
                    '</span>'+
                    '</div>'+
                    '<div class="agency common-file" style="display: none;">'+
                    '<span><i class="red"></i>税务登记证<b class="f-12"></b> ：</span>'+
                    '<span>' +
                    '<em class="btn-file file"><sapn>上传附件</sapn><input type="file" name="tax-licence"/></em>' +
                    '</span>'+
                    '</div>'+
                    '<div class="agency" style="display: none;">'+
                    '<span><i class="red"></i>一般纳税人资格证<b class="f-12"></b> ：</span>'+
                    '<span>' +
                    '<em class="btn-file file"><sapn>上传附件</sapn><input type="file" name="taxpayer-licence"/></em>' +
                    '</span>'+
                    '</div>'+
                    '<div class="agency" style="display: none;">'+
                    '<span>食品流通许可证<b class="f-12"></b> ：</span>'+
                    '<span>' +
                    '<em class="btn-file file"><sapn>上传附件</sapn><input type="file" name="food-licence"/></em>' +
                    '</span>'+
                    '</div>'+
                    '<div>'+
                    '<span>发票抬头:</span>'+
                    '<span>' +
                    '<input name="invoiceTitle" placeholder="50字符以内" maxlength="50"/>' +
                    '</span>'+
                    '</div>'+
                    '<div>'+
                    '<span>纳税号:</span>'+
                    '<span>' +
                    '<input name="invoiceTaxNumber" />' +
                    '</span>'+
                    '</div>'+
                    '<div>'+
                    '<span>银行开户行:</span>'+
                    '<span>' +
                    '<input name="invoiceBank" placeholder="20字符以内" maxlength="20" />' +
                    '</span>'+
                    '</div>'+
                    '<div>'+
                    '<span>开户行帐号:</span>'+
                    '<span>' +
                    '<input name="invoiceBankAccount"  />' +
                    '</span>'+
                    '</div>'+
                    '<div>'+
                    '<span>联系电话:</span>'+
                    '<span>' +
                    '<input name="invoiceTel" />' +
                    '</span>'+
                    '</div>'+
                    '<div>'+
                    '<span>公司地址:</span>'+
                    '<span>' +
                    '<input name="invoiceCompanyAddr" maxlength="50" placeholder="50字符以内"/>' +
                    '</span>'+
                    '</div>'+
                    '<div class="agency" style="margin-bottom: 35px;display: none;">' +
                    '<span style="vertical-align: top">备注：</span>' +
                    '<span><textarea name="remark" rows="4" placeholder="不超过500字"></textarea></span>' +
                    '</div>'+
                    '<div>'+
                    '<button style="display: none;" id="register-apply" onclick="registerAgencyUser()" type="button">注册</button>' +
                    '</div>'+
                '</form>',
        btn:['保存','取消'],
        title: '新增员工',
        success:function(layero, index){
            $("#audit_form").on("keyup","input[name=invoiceBankAccount]",function(){
                this.value=this.value.replace(/\D/g,'');
            });
            $("#audit_form .model").change(function(){
                if ($(this).val() == "电商") {
                    $("#audit_form .agency").hide();
                } else {
                    $("#audit_form .agency").show();
                    if ($(".select-lience").prop("checked")) {
                        $(".common-file").hide();
                    }
                }
            });
            $(".select-lience").click(function(){//三证合一单选框
                if ($(this).prop("checked")) {
                    $(".common-file").hide();
                } else {
                    $(".common-file").show();
                }
            });
            $(".agency input[type='file']").change(function(){
                if ($(this)[0].files.length == 0) {
                    $(this).prev().text("上传文件");
                } else {
                    $(this).prev().text(($(this)[0].files[0].name).length > 6 ? ($(this)[0].files[0].name).substr(0,6)+"..." : $(this)[0].files[0].name);
                }
            });
        },
        //i和currdom分别为当前层索引、当前层DOM对象
        yes: function(i, currdom) {
            var tel = $("#audit_form .telphone").val().trim();
            var password = $("#audit_form .password").val().trim();
            if (!checkTel(tel)) {
                layer.msg("请正确填写手机号码", {icon : 5, time : 2000});
                return;
            }
            if((password.search(/[0-9]/) == -1)
                || (password.search(/[A-Z]/) == -1)
                || (password.search(/[a-z]/) == -1)
                || (password.length < 6) || (password.length > 20)) {
                layer.msg('密码必须为6-20个字符，且至少包含数字、大写、小写字母等三种或以上字符！', {icon: 2, time: 2000});
                return;
            }
            var invoiceTel = $("input[name=invoiceTel]").val().trim();
            if(invoiceTel&&!checkTel(invoiceTel)){
                layer.msg("请正确填写联系电话", {icon : 5, time : 2000});
                return;
            }
            var param = {
                "telphone":tel,
                "password":password,
                "salesmanErp":salesmanErp,
                "salesmanId":salesmanId,
                "invoiceTitle":$("input[name=invoiceTitle]").val().trim(),
                "invoiceTaxNumber":$("input[name=invoiceTaxNumber]").val().trim(),
                "invoiceBank":$("input[name=invoiceBank]").val().trim(),
                "invoiceBankAccount":$("input[name=invoiceBankAccount]").val().trim(),
                "invoiceTel":invoiceTel,
                "invoiceCompanyAddr":$("input[name=invoiceCompanyAddr]").val().trim()
            };
            if ($("#audit_form .model").val() == "电商"){
                var shade = layer.load(1, {shade: 0.5});
                ajax_post("/member/backstageRegister", JSON.stringify(param), "application/json",
                    function(response) {
                        response = JSON.parse(response);
                        if (response.suc) {
                            layer.closeAll();
                            layer.msg('后台注册用户成功！', {icon : 1, time : 2000},function(){
                                getSalesman($(".business_message").data("headerId"), 1);
                            });
                        } else {
                            layer.close(shade);
                            layer.msg(response.msg, {icon : 5, time : 2000});
                        }
                    },
                    function(XMLHttpRequest, textStatus) {
                         layer.close(shade);
                    }
                );
            } else {
                $("#register-apply").attr("salesmanId",salesmanId).click();
            }
        }
    });
});

//注册成为经销商 
function registerAgencyUser(){
    var salesmanId = $(this).attr("salesmanId");
    var $businessLicence = $("#audit_form").find("input[name='business-licence']");
    if ($businessLicence != undefined && $businessLicence.val() != "") {
        var name = $businessLicence.val();
        if ($businessLicence[0].files[0].size > (2 * 1024 * 1024) ||
            !(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
            layer.msg("营业执照只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
            return false;
        }
    }
    if (!$("#audit_form .select-lience").prop("checked")){//如果没有勾选了三证合一
        var $organizationCode = $("#audit_form").find("input[name='organization-code']");
        if ($organizationCode != undefined && $organizationCode.val() != "") {
            var name = $organizationCode.val();
            if ($organizationCode[0].files[0].size > (2 * 1024 * 1024) ||
                !(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
                layer.msg("组织机构代码只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
                return;
            }
        }
        var $taxLicence = $("#audit_form").find("input[name='tax-licence']");
        if ($taxLicence != undefined && $taxLicence.val() != "") {
            var name = $taxLicence.val();
            if ($taxLicence[0].files[0].size > (2 * 1024 * 1024) ||
                !(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
                layer.msg("税务登记证只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
                return;
            }
        }
    }
    var $taxpayerLicence = $("#audit_form").find("input[name='taxpayer-licence']");
    if ($taxpayerLicence != undefined && $taxpayerLicence.val() != "") {
        var name = $taxpayerLicence.val();
        if ($taxpayerLicence[0].files[0].size > (2 * 1024 * 1024) ||
            !(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
            layer.msg("一般纳税人资格证只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
            return;
        }
    } 
    var $foodLicence = $("#audit_form").find("input[name='food-licence']");
    if ($foodLicence != undefined && $foodLicence.val() != "") {
        var name = $foodLicence.val();
        if ($foodLicence[0].files[0].size > (2 * 1024 * 1024) ||
            !(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
            layer.msg("食品流通许可证只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
            return;
        }
    }
    var $goodsLicence = $("#audit_form").find("input[name='goods-licence']");
    if ($goodsLicence != undefined && $goodsLicence.val() != "") {
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
    } else {
        layer.msg("请上传收货授权书", {icon: 0, time: 2000});
        return;
    }
    var remark = $("#audit_form").find("textarea[name='remark']").val();
    if (remark && remark.length > 500) {
        layer.msg('备注不能超过500字！', {icon : 5, time : 2000});
        return;
    }
    var shade = layer.load(1, {shade: 0.5});
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
            if ($(".select-lience").prop("checked")) {//选中了三合一，组织机构代码和税务登记证就应该不要再上传了
                for (var i = 0;i < formData.length;i++) {
                    if (formData[i].name == "business-licence") {
                        fileValue = formData[i].value;
                    }
                    if (formData[i].name == "organization-code" || formData[i].name == "tax-licence") {
                        formData[i].value = fileValue;
                    }
                }
            }
            formData.push({name:"status",value:2});
            formData.push({name:"account",value:$("#audit_form .telphone").val().trim()});
            formData.push({name:"password",value:$("#audit_form .password").val().trim()});
            formData.push({name:"salesmanId",value:salesmanId});
        },
        //提交成功后的回调函数
        success: function(data,status,xhr,$form){
            data = JSON.parse(data);
            layer.close(shade);
            if (data.suc) {
                layer.msg('后台申请经销商成功！', {icon : 1, time : 2000},function(){
                    layer.closeAll();
                });
            } else {
                layer.msg(data.msg, {icon : 5, time : 2000},function(){
                    this.clearForm = false;
                });
            }
        },
        error: function(xhr, status, error, $form){ layer.close(shade);},
        complete: function(xhr, status, $form){}
    };
    $("#audit_form").ajaxSubmit(options);
}


//编辑员工
$('body').on("click", ".edit_operation", function () {
    var $obj = $(this);
    var oldName = $obj.parent().parent().find(".name").html();
    var oldErp = $obj.parent().parent().find(".erp").html();
    var oldTel = $obj.parent().parent().find(".tel").html();
    var id = $obj.parents(".salesman_list").data("id")//员工id
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['460px', '280px'],
        content: $('.edit_operation_pop'),
        btn:['保存','取消'],
        title: '编辑员工',
        success:function(layero, index){
            $(".edit_operation_pop input").eq(0).val(oldName);
            $(".edit_operation_pop input").eq(1).val(oldErp);
            $(".edit_operation_pop input").eq(2).val(oldTel);
        },
        //i和currdom分别为当前层索引、当前层DOM对象
        yes: function(i, currdom) {
            var name = $(".edit_operation_pop input").eq(0).val().trim();
            var erp = $(".edit_operation_pop input").eq(1).val().trim();
            var tel = $(".edit_operation_pop input").eq(2).val().trim();
            if (undefined == name || "" == name) {
                layer.msg("员工姓名不能为空", {icon : 5, time : 2000});
                return;
            }
            if (undefined == erp || "" == erp) {
                layer.msg("ERP账号不能为空", {icon : 5, time : 2000});
                return;
            }
            if (undefined == tel || "" == tel) {
                layer.msg("联系电话不能为空", {icon : 5, time : 2000});
                return;
            }
            if (!checkTel(tel)) {
                layer.msg("联系电话格式不正确", {icon : 5, time : 2000});
                return;
            }
            var params = {
                id : id,
                name : name,
                oldname : oldName,
                erp : erp,
                tel : tel,
                oldTel : oldTel
            };
            ajax_post("../member/changeSalesMan", params, undefined,
                function (response) {
                    if (response.suc) {
                        layer.close(i);
                        layer.msg('修改员工成功！', {icon : 1, time : 2000}, function() {
                            $obj.parent().parent().find(".name").html(name);
                            $obj.parent().parent().find(".erp").html(erp);
                            $obj.parent().parent().find(".tel").html(tel);
                        });
                    } else {
                        layer.msg(response.msg, {icon: 2, time: 1000});
                    }
                },
                function (xhr, status) {
                }
            );
        }
    });
});

//删除员工
$('body').on("click", ".del_operation", function () {
    var saleName = $(this).parents("tr").find(".name").html();
    var saleId = $(this).parents("tr").data("id");
    var memberCount = parseInt($(this).parents("td").siblings().eq(4).find("a").html());
    var nodeType = $(this).data("type");
    var desc = (nodeType ==2?"员工":"分销商");
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['460px', '180px'],
        content: $('.del_operation_pop'),
        btn:['删除','取消'],
        title: '删除员工',
        success:function(layero, index){
            $(".del_operation_pop b").html(saleName);
        },
        //i和currdom分别为当前层索引、当前层DOM对象
        yes: function(i, currdom) {
            if (memberCount != undefined && memberCount != 0) {
                layer.msg("存在已关联"+desc+"，无法删除该员工", {icon: 2, time: 1000});
                return;
            }
            var param = {
                saleid : saleId
            };
            if ($(".business_message").data("headerId")) {
                param.headerid = $(".business_message").data("headerId");
            }
            ajax_post("../member/removeSalesMan", param, undefined,
                function (response) {
                    if (response.suc) {
                        layer.close(i);
                        layer.msg('删除员工成功！', {icon : 1, time : 2000}, function() {
                            getSalesman($(".business_message").data("headerId"), 1);
                        });
                    } else {
                        layer.msg(response.msg, {icon: 2, time: 1000});
                    }
                },
                function (xhr, status) {
                }
            );
        }
    });
});


//关联分销商
$('body').on("click", ".distract_operation", function () {
    var salesmanId = $(this).parents("tr").data("id");//员工id
    var salesmanErp = $(this).parents(".salesman_list").find(".erp").html();
    var nType = $(this).data("type");
    var red = nType == 2;
    var desc =  (red?"关联员工":"关联分销商");
    var popHtml=
        '<div class="distract_operation_pop structure_pop display">                                    '+
        '<div>                                                                                         '+
        '<input class="searchInput" placeholder="分销账号/名称/手机号"/>                               '+
        '<button class="searchButton">搜索</button>                                                    '+
        '</div>                                                                                        '+
        '<table class="operation_table">                                                               '+
        '<thead>                                                                                       '+
        '<tr>                                                                                          '+
        '<th style="width: 10%">勾选</th>                                                              '+
        '<th style="width: 35%">'+(red?'员工姓名':'分销商账号')+'</th>                                                        '+
        '<th style="width: 20%">'+(red?'ERP账号':'名称')+'</th>                                                              '+
        '<th style="width: 35%">手机号</th>                                                            '+
        '</tr>                                                                                         '+
        '</thead>                                                                                      '+
        '<tbody>                                                                                       '+
        '</tbody>                                                                                      '+
        '</table>                                                                                      '+
        '<div class="clear"></div>                                                                     '+
        '<div id="related_distribution_pagination" style="text-align: center; margin-top: 30px;"></div>'+
        '</div>	 ';
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['580px', '580px'],
        content: popHtml,
        btn:['关联','取消'],
        title: desc,
        success: function() {
            var listPop = $('.distract_operation_pop');
            <!--关联分销商-->
            $(".distract_operation_pop .operation_table").data("salesmanid",salesmanId);//为以后勾选分销商判断做铺垫
            $(".distract_operation_pop").data("salesmanid",salesmanId);
            if(red){
                gain_sm_list(1);
            }else{
                gain_distribution_list(1);
            }
            listPop.show();
            //光联分销商弹出页面-------搜索分销商
            $(".distract_operation_pop").on("click", ".searchButton", function(){
                   if(red) {
                        gain_sm_list(1);
                    }else{
                        gain_distribution_list(1);
                   }
            });

            $(".distract_operation_pop").on("keyup", ".searchInput", function(event){
                var key = event.keyCode || event.which;
                // && $.trim($(obj).val())
                if (key == 13) {
                    if(red) {
                        gain_sm_list(1);
                    }else{
                        gain_distribution_list(1);
                    }
                }
            });
        },
        yes: function(i, currdom) {
            var memberids = "";
            var listPop = $('.distract_operation_pop');
            listPop.find("input[name='distributors']").each(function(n, node) {
                if (node.checked) {
                    memberids += $(node).attr("value") + ",";
                }
            });
            if (memberids == "") {
                layer.msg("请选择"+(red?"员工":"分销商"), {icon : 5, time : 2000});
                return;
            }
            memberids = memberids.substring(0,memberids.length-1);
            var params = {
                salesmanid : salesmanId,
                memberids : memberids,
                salesmanErp : salesmanErp
            };
            var url;
            if(red){
                url = "/member/relatedEmp";
            }else{
                url = "/member/relatedDistributors";
            }
            ajax_post(url, params, undefined,
                function (response) {
                    if (response.suc) {
                        layer.close(i);
                        layer.msg(response.msg, {icon : 1, time : 2000},function(){
                            getSalesman($(".business_message").data("headerId"), 1);
                        });
                    } else {
                        layer.msg(response.msg, {icon : 5, time : 2000});
                    }
                },
                function (xhr, status) {
                }
            );
        }
    });
});


//关联后台账户
$('body').on("click", ".user_operation", function () {
    var $obj = $(this);
    var id = $obj.parents(".salesman_list").data("id");
    var oldAccount = $obj.find("input").eq(0).val();
    var oldworkNo = $obj.find("input").eq(1).val();
    var name = $obj.parents(".salesman_list").find(".name").text();
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['460px', '240px'],
        content: $('.user_operation_pop'),
        btn:['确定','取消'],
        title: '关联后台账户',
        success:function(layero, index){
            $('.user_operation_pop input').eq(0).val(oldAccount);
            $('.user_operation_pop input').eq(1).val(oldworkNo);
        },
        //i和currdom分别为当前层索引、当前层DOM对象
        yes: function(i, currdom) {
            var account = $('.user_operation_pop input').eq(0).val().trim();
            var workNo = $('.user_operation_pop input').eq(1).val().trim();
            if (undefined == account || "" == account) {
                layer.msg("用户名不能为空", {icon : 5, time : 2000});
                return;
            }
            var param = {
                id : id,
                account : account,
                workNo : workNo,
                name : name
            };
            ajax_post("../member/changeSalesMan", param, undefined,
                function (response) {
                    if (response.suc) {
                        layer.close(i);
                        layer.msg('关联后台账户成功！', {icon : 1, time : 2000}, function() {
                            $obj.find("input").eq(0).val(account);
                            $obj.find("input").eq(1).val(workNo);
                        });
                    } else {
                        layer.msg(response.msg, {icon: 2, time: 1000});
                    }
                },
                function (xhr, status) {
                }
            );
        }
    });
});

//已关联分销商列表
$('body').on("click", ".related_link", function () {
    $(".related_distract_table tbody").empty("tr");
    $('.related_operation').hide();

    var salesmanid = $(this).parents(".salesman_list").data("id");
    var nodeType = $(this).data("type");
    $(".related_distract .related_distract_table").data("salesmanid",salesmanid);
    var red = (nodeType == 2);
    $('.related_distract').show();
    $('.related_distract').find("#type").val(nodeType);
    if(red){
        $(".related_distract .searchInput").attr("placeholder","ERP账号/员工姓名/手机号");
        $(".related_distract_table th").eq(1).text("ERP账号");
        $(".related_distract_table th").eq(2).text("员工姓名");
        getrelateSalesman(1,salesmanid);
        return;
    }
    $(".related_distract .searchInput").attr("placeholder","分销账号/名称/手机号")
    $(".related_distract_table th").eq(1).text("分销商账号");
    $(".related_distract_table th").eq(2).text("名称");
    var params = {
        salesmanid : salesmanid
    };
    //查询业务人员下的分销商
    ajax_post("../member/getSalesmanMember", params, undefined,
        function (response) {
            if (response.code == 3) {
                var distributeIds = "";
                $.each(response.data,function(i,item){
                    distributeIds += item.memberid + ",";
                });
                distributeIds = distributeIds.substring(0,distributeIds.length-1);
                $(".related_distract .related_distract_table").data("distributeIds",distributeIds);
                $(".related_distract .related_distract_table").data("salesmanid",salesmanid);//为以后取消关联做准备
                getMembers(1,salesmanid);
            } else {
                if (response.code != 4) {
                    layer.msg(response.msg, {icon : 5, time : 2000});
                }
            }
        },
        function (xhr, status) {
        }
    );
});

function getrelateSalesman(curr,empId){
    var salesman;
    if (!empId) {
        salesman = $(".related_distract .related_distract_table").data("salesmanid");
    } else {
        salesman = empId;
    }
    var params = {
        desc: $(".related_distract .searchInput").val(),
        currPage: curr,
        pageSize: $(".list_content_member p select").val(),
        empId: salesman
    }
    //查询业务人员下的分销商
    ajax_post("/member/getSalesmans", params, undefined,
        function (data) {
            if (data.suc) {
                insertMember(data.page.list,2);
                $("#count_member").text(data.page.rows);
                $("#page_count_member").text(data.page.totalPage);
                init_member_pagination(data.page,empId,2);
            }else{
                layer.msg(data.msg, {icon : 5, time : 1000});
            }

        },function(e){

        }
    );
}


//业务人员对应分销商页面-----返回
$('body').on("click", ".back_operation", function () {
    getSalesman($(".business_message").data("headerId"), 1);
    $('.related_operation').show();
    $('.related_distract').hide();
});
//取消关联
$('body').on("click", ".cancel_related", function () {
    var email = $(this).siblings("#email").text();
    var memberid = $(this).parents("tr").data("id");
    var type = $('.related_distract').find("#type").val();
    var desc = type == 2?"员工":"分销商";
    var calHtml =
        '<div class="cancel_related_pop structure_pop" > '+
        '    <div style="text-align: center">                   '+
        '        您确定要取消关联的'+desc+'[<b class="red">'+email+'</b>]吗?'+
        '    <input type="hidden" data-id="'+memberid+'"/>'+
        '    </div>                                             '+
        '</div>                                                 ';
    layer.open({
        type: 1,
        skin: 'layui-layer-rim',
        area: ['460px', '180px'],
        content: calHtml,
        btn:['取消关联','取消'],
        title: '取消关联',
        //i和currdom分别为当前层索引、当前层DOM对象
        yes: function(i, currdom) {
            if(type == 2){
                var id = $('.cancel_related_pop input').data("id");
                ajax_get("/member/cancelEmpRelate?sd="+id,
                    undefined,undefined,
                    function(data){
                        if(data.suc){
                            layer.close(i);
                            layer.msg(data.msg,{icon:6,time:2000},function(){
                                getrelateSalesman(1);
                            });
                        }else{
                            layer.msg(data.msg,{icon:5,time:2000});
                        }
                    },function(e){
                        console.log(e);
                });
                return ;
            }
            var salesmanid = $(".related_distract .related_distract_table").data("salesmanid");//业务人员id
            var params = {
                salesmanid : salesmanid,
                memberid : memberid
            };
            ajax_post("../member/cancleRelated", params, undefined,
                function (response) {
                    if (response.suc) {
                        layer.close(i);
                        layer.msg('取消关联成功！', {icon : 1, time : 2000}, function() {
                            var params = {
                                salesmanid : salesmanid
                            };
                            //查询业务人员下的分销商
                            ajax_post("../member/getSalesmanMember", params, undefined,
                                function (response) {
                                    if (response.code == 3) {
                                        var distributeIds = "";
                                        $.each(response.data,function(i,item){
                                            distributeIds += item.memberid + ",";
                                        });
                                        distributeIds = distributeIds.substring(0,distributeIds.length-1);
                                        $(".related_distract .related_distract_table").data("distributeIds",distributeIds);
                                        getMembers(1,salesmanid);
                                    } else {
                                        $(".related_distract_table tbody").empty("tr");
                                        layer.msg(response.msg, {icon : 5, time : 2000});
                                    }
                                },
                                function (xhr, status) {
                                }
                            );
                        });
                    } else {
                        layer.msg("取消关联失败", {icon : 5, time : 2000});
                    }
                },
                function (xhr, status) {
                }
            );
        }
    });
});

//得到相关负责人信息
function getHeaderInfo(){
    ajax_get("../member/getHeader", {id:selectid}, undefined,
        function (response) {
            if (response.suc) {
                var header = response.data[0];
                $(".business_message em").eq(0).html(header.headerName);
                $(".business_message em").eq(1).html(header.headerTel);
                $(".business_message").data("headerId",header.headerId);
                $(".business_message").show();
                getSalesman(header.headerId, 1);
            } else {
                layer.msg(response.msg, {icon : 5, time : 2000});
            }
        },
        function (xhr, status) {
        }
    );
}

function getSalesman4EnterKey(){
    getSalesman($(".business_message").data("headerId"), 1);
}

//根据负责人得到相关员工信息
function getSalesman(headerId, curr){
    $(".organizational_table tbody").empty("tr");
    var params = {
        currPage: curr,
        pageSize: $(".list_content_salesman p select").val(),
        desc: $(".related_operation .searchInput").val().trim()
    };
    if (headerId) {
        params.id = headerId;
    }
    ajax_post_sync("../member/getSalesmans", params, undefined,
        function (response) {
            if (response.suc) {
                $(".business_message em").eq(2).html(response.page.rows);
                var salesmans = response.page.list;
                insertSalemans(salesmans);
                $("#count_salesman").text(response.page.rows);
                $("#page_count_salesman").text(response.page.totalPage);
                init_pagination_salesman(response.page, headerId);
            } else {
                layer.msg(response.msg, {icon : 5, time : 2000});
            }
        },
        function (xhr, status) {
        }
    );
}

//插入业务人员数据
function insertSalemans(list){
    $(".organizational_table tbody").empty("tr");
    var ulHTML = '';
    $.each(list,function(i,item){
        var nodeType = item.nodeType;
        var red = (nodeType == 2);
        var desc = (red?"关联员工":"关联分销商");
        var count = red?item.salesManCount:item.memberCount;
        $(".organizational_table thead th:eq(4)").text("已"+desc);
        ulHTML +=
            '<tr class="salesman_list" data-id="'+ item.id +'">'+
            '<td style="width: 5%"><input style="cursor: pointer" class="chooseSalesman" type="radio" name="register"></td>'+
            '<td class="name" style="width: 10%">'+ deal_with_illegal_value(item.name) +'</td>'+
            '<td class="erp" style="width: 10%">'+ deal_with_illegal_value(item.erp) +'</td>'+
            '<td class="tel" style="width: 15%">'+ deal_with_illegal_value(item.tel) +'</td>'+
            '<td style="width: 15%"><a href="#" class="red related_link" data-type="'+nodeType+'" >'+ count +'</a></td>'+
            '<td style="width: 45%">'+
            '<span class="edit_operation" style="margin-left: -13px;">编辑</span>'+
            '<span class="distract_operation" data-type="'+nodeType+'">'+desc+'</span>'+
            '<span class="user_operation">关联后台用户<input style="display: none;" value="'+ (item.account == null ? "" : item.account) +'"><input style="display: none;" value="'+ (item.workNo == null ? "" : item.workNo) +'"></span>'+
            '<span class="del_operation" data-type="'+nodeType+'">删除</span>'+
            '</td>'+
            '</tr>';
    });
    $(".organizational_table tbody").append(ulHTML);
}

//初始化分页栏--员工
function init_pagination_salesman(page, headerId) {
    if ($("#pagination_salesman")[0] != undefined) {
        $("#pagination_salesman").empty();
        laypage({
            cont: 'pagination_salesman',
            pages: page.totalPage,
            curr: page.currPage,
            groups: 5,
            skin: '#55ccc8',
            first: '首页',
            last: '尾页',
            prev: '上一页',
            next: '下一页',
            skip: true,
            jump: function (obj, first) {
                //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                if (!first) {
                    getSalesman(headerId,obj.curr);
                }
            }
        });
    }
}

//关联分销商弹出框获取分销商用户列表数据
function gain_distribution_list(curr) {
    var salesmanid = $(".distract_operation_pop").data("salesmanid");
    var params = {
        role: 2,
        currPage: curr == undefined || curr == 0 ? 1 : curr,
        pageSize: 10,
        search: $(".distract_operation_pop .searchInput").val().trim(),
        salesmanid:salesmanid
    };
    $.ajax({
        url: "../member/getAllUsers",
        type: 'POST',
        data: params,
        async: false,//是否异步
        dataType: 'json',
        success: function(data) {
            if (data.suc) {
                insert_distribution_list(data.page.list);
                init_distribution_pagination(data.page);
            } else if (data.code == "2") {
                window.location.href = "login.html";
            } else {
                layer.msg(data.msg, {icon : 5, time : 1000});
            }
        },
        error: function(xhr, status) { console.log("error--->" + status); }
    });
}
// add by zbc 获取员工数据 查询所有未被关联的员工
function gain_sm_list(curr){
    var salesmanid = $(".distract_operation_pop").data("salesmanid");
    var params = {
        currPage: curr == undefined || curr == 0 ? 1 : curr,
        pageSize: 10,
        desc: $(".distract_operation_pop .searchInput").val().trim(),
        nodeType:1,
        //未被关联
        notRelation:true
    };
    $.ajax({
        url: "../member/getSalesmans",
        type: 'POST',
        data: params,
        async: false,//是否异步
        dataType: 'json',
        success: function(data) {
            if (data.suc) {
                insert_distribution_list(data.page.list,2);
                init_distribution_pagination(data.page,2);
            } else if (data.code == "2") {
                window.location.href = "login.html";
            } else {
                layer.msg(data.msg, {icon : 5, time : 1000});
            }
        },
        error: function(xhr, status) { console.log("error--->" + status); }
    });
}
//关联分销商弹出页面------插入分销商数据
function insert_distribution_list(list,type){
    $('.distract_operation_pop .operation_table').find("tbody").empty("tr");
    var itemHTML = '';
    $.each(list, function(i, item) {
        if(type != 2){
            itemHTML +=
                '<tr>'+
                '<td style="width: 10%"><input style="cursor: pointer;" type="checkbox" name="distributors" value="' + item.id + '"></td>'+
                '<td style="width: 35%">' + deal_with_illegal_value(item.email) + '</td>'+
                '<td style="width: 20%">' + deal_with_illegal_value(item.nickName) + '</td>'+
                '<td style="width: 35%">' + deal_with_illegal_value(item.telphone) + '</td>'+
                '</tr>'
        }else{
            itemHTML +=
                '<tr>'+
                '<td style="width: 10%"><input style="cursor: pointer;" type="checkbox" name="distributors" value="' + item.id + '"></td>'+
                '<td style="width: 35%">' + deal_with_illegal_value(item.name) + '</td>'+
                '<td style="width: 20%">' + deal_with_illegal_value(item.erp) + '</td>'+
                '<td style="width: 35%">' + deal_with_illegal_value(item.tel) + '</td>'+
                '</tr>'
        }
    });
    $('.distract_operation_pop .operation_table').find("tbody").append(itemHTML);
}

//关联分销商弹出页面------分销商分页
function init_distribution_pagination(page,type){
    if ($("#related_distribution_pagination")[0] != undefined) {
        $("#related_distribution_pagination").empty();
        laypage({
            cont: 'related_distribution_pagination',
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
                $("#related_distribution_pagination .laypage_total").find("input[type='number']").css("width","40px");
                if(!first){
                    if(type == 2){
                       gain_sm_list(obj.curr);
                    }else{
                        gain_distribution_list(obj.curr);
                    }
                }
            }
        });
    }
}

//得到每个员工所关联的分销商 param:{curr:当前页，distributeIds:员工所对应的分销商id}
//在指定的分销商ids里查询得到分销商
function getMembers(curr, salesmanid){
    var salesman;
    if (!salesmanid) {
        salesman = $(".related_distract .related_distract_table").data("salesmanid");
    } else {
        salesman = salesmanid;
    }
    var params = {
        desc: $(".related_distract .searchInput").val(),
        currPage: curr,
        pageSize: $(".list_content_member p select").val(),
        mids : $(".related_distract .related_distract_table").data("distributeIds"),
        salesmanid: salesman
    }
    ajax_post("../member/getDistributorDetail", params, undefined,
        function (data) {
            if (data.suc) {
                insertMember(data.page.list);
                $("#count_member").text(data.page.rows);
                $("#page_count_member").text(data.page.totalPage);
                init_member_pagination(data.page,salesman);
            } else {
                layer.msg(data.msg, {icon : 5, time : 2000});
            }
        },
        function (xhr, status) {
        }
    );
}

//每个员工对应的分销商信息
function insertMember(list,type){
    $(".related_distract_table tbody").empty("tr");
    var ulHTML = '';
    var sequence = 0;
    var red = (type ==2);
    $.each(list,function(i,item){
        sequence += 1;
        ulHTML +=
            '<tr data-id="' + item.id +'">'+
            '<td style="width: 10%">'+ deal_with_illegal_value(sequence) +'</td>'+
            '<td style="width: 25%"  id ="email">'+ deal_with_illegal_value(red?item.erp:item.email) +'</td>'+
            '<td style="width: 20%">'+ deal_with_illegal_value(red?item.name:item.nickName) +'</td>'+
            '<td style="width: 25%">'+ deal_with_illegal_value(red?item.tel:item.telphone) +'</td>'+
            '<td style="width: 20%" class="cancel_related">取消关联</td>'+
            '</tr>';
    });
    $(".related_distract_table tbody").append(ulHTML);
}

//每个员工对应的分销商信息 分页
function init_member_pagination(page,salesman,type){
    if ($("#pagination_member")[0] != undefined) {
        $("#pagination_member").empty();
        laypage({
            cont: 'pagination_member',
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
                    if(type == 2){
                        getrelateSalesman(obj.curr,salesman);
                    }else{
                        getMembers(obj.curr,salesman);
                    }
                }
            }
        });
    }
}

function getMemberList4RelatedDistract(){
    var type = $('.related_distract').find("#type").val();
    if(type == 2){
        getrelateSalesman(1);
    }else{
        getMembers(1);
    }
}

function getMemberList4RelatedOperation(){
    var type = $('.related_operation').find("#type").val();
    if(type == 2){
        getrelateSalesman(1);
    }else{
        getMembers(1);
    }
}