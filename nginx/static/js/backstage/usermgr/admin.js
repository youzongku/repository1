//定义全局变量
var layer = undefined;
var laypage = undefined;
var r = undefined;

//初始化后台用户列表
function init_admin(rParam, layerParam, laypageParam, BbcGrid) {
    //初始化全局变量
    layer = layerParam;
    laypage = laypageParam;
    r = rParam;
    init_level_select();//初始化等级菜单
    init_mode_select();//初始化模式下拉选
    var grid = new BbcGrid();
    grid.initTable($("#user_table"),adminSetting());
    //注册等级菜单事件
    $("#level_select,#comsumerType,#distributionMode").change(function(event) {
        searchUser();
    });
    $(".list_content p select").change(function () {
        searchUser();
    });
    $("#searchButton").click(function () {
        searchUser();
    });
    if(r != 2){
        init_role_select();
        $("#save_new_btn").click(function () {
            clear()
        });
        $(".list_content p select").change(function () {
            searchUser();
        });
        $(".edit-pop-list input[type=checkbox]").click(function () {
            $(this).parent().siblings().find("input[type=checkbox]").removeAttr("checked");
        });
        $(".e-dip").click(function () {
            insertOrUpdate_user();
        });
        $("#selectRole").next().click(function () {
            searchUser();
        });
        $("#userForm input[name=email]").blur(function () {
            checkUserName(this);
        });
        $("#user_Jurisdiction input[name='addUserGetMenu']").click(function() {
            checkTree($(this).val());
        });
    }

}

function init_mode_select(){
    ajax_get("/member/getMode?" + Math.random(), {}, undefined,
        function(data) {
            var select =  $("#distributionMode");
            select.empty();
            var optionHTML = '<option  value = "">所有分销渠道</option>';
            $.each(data,function(i,item){
                optionHTML += '<option  value = "'+item.id+'">'+item.disMode+'</option>';
            });
            select.append(optionHTML);
        },function(e){
        }
    );        
}

//初始化等级下拉选数据
function init_level_select() {
    var selectedId = $("#level_select").val();
    ajax_get("../member/getallranks?" + Math.random(), {}, undefined,
        function (data) {
            if (data.suc) {
                var rs = data.data;
                $("#level_select").empty();
                var optionHTML = '';
                if (selectedId != "" && selectedId != null && selectedId != undefined) {
                    optionHTML = '<option value="" data-disc="">请选择</option>';
                }
                else {
                    optionHTML = '<option value="" selected="selected" data-disc="">请选择</option>';
                }
                for (var i in rs) {
                    if (rs[i].id == selectedId) {
                        optionHTML += '<option selected="selected" value="' + rs[i].id + '" data-disc="' + rs[i].discount + '%">' + rs[i].rankName + '级</option>';
                    }
                    else {
                        optionHTML += '<option value="' + rs[i].id + '" data-disc="' + rs[i].discount + '%">' + rs[i].rankName + '级</option>';
                    }
                }
                $("#level_select").append(optionHTML);
            } else if (data.code == 1) {
                window.location.href = "login.html";
            }
        },
        function (xhr, status) {
        }
    );
}

function change_type(obj){
    var parent = $(obj).parent();
    var id = parent.find("input[name='hiddenId']").val();
    var realName = parent.find("input[name='hiddenRealName']").val()=='null'?"---":parent.find("input[name='hiddenRealName']").val();
    var email = parent.find("input[name='hiddenEmail']").val();
    var comsumerType = parent.find("input[name='hiddenComsumerType']").val();
    var dismode = parent.find("input[name='hiddenDisMode']").val();
    var optionHTML = "";
    $.ajax({
        url: "/member/getMode",
        type: "GET",
        data: undefined,
        dataType: "json",
        async:false,
        success: function (data) {
            $.each(data,function(i,item){
               optionHTML += '<option  value = "'+item.id+'" '+(dismode==item.id?'selected="selected"':'')+'>'+item.disMode+'</option>';
            });
        }
    });
    layer.open({
        type: 1,
        title: "更改类型",
        content:
        '<div style="width: 400px; height: 180px; border: 1px solid #ccc; margin-left: auto; margin-right: auto; padding: 10px;">' +
        '<table id="edit_rank_dialog" style="table-layout: fixed; width: 100%; border:">' +
        '<tr style="width: 400px; display: block; margin-top: 20px;">' +
        '<th style="width: 40%; display: inline-block;  text-align: right;">分销商姓名：</th>' +
        '<td style="width: 55%; display: inline-block;  text-align: left;">' +realName+'</td>' +
        '</tr>' +
        '<tr style="width: 400px; display: block; margin-top: 20px;">' +
        '<th style="width: 40%; display: inline-block;  text-align: right;">分销商邮箱：</th>' +
        '<td style="width: 55%; display: inline-block;  text-align: left;">' + email +'</td>' +
        '</tr>' +
        '<tr style="width: 400px; display: block; margin-top: 20px;">' +
        '<th style="width: 40%; display: inline-block;  text-align: right;">分销商类型：</th>' +
        '<td style="width: 55%; display: inline-block;  text-align: left;">' +
        '<select id="dialogComsumerType" style="width: 100px;">'+
        '<option value="1"'+(comsumerType==1?'selected="selected"':'')+'>普通分销商</option><option value="2"'+(comsumerType==2?'selected="selected"':'')+'>合营分销商</option><option value="3"'+(comsumerType==3?'selected="selected"':'')+'>内部分销商</option>'+
        '</select>' +
        '</td>' +
        '</tr>' +
        '<tr style="width: 400px; display: block; margin-top: 20px;">' +
        '<th style="width: 40%; display: inline-block;  text-align: right;">分销商渠道：</th>' +
        '<td style="width: 55%; display: inline-block;  text-align: left;">' +
        '<select id="dialogDisMode" style="width: 100px;">'+
        optionHTML+
        '</select>' +
        '</td>' +
        '</tr>' +
        '</table>' +
        '</div>',
        area: ['460px', '300px'],
        btn: ["确定"],
        closeBtn: 1,
        shadeClose: false,
        //i和currdom分别为当前层索引、当前层DOM对象
        yes: function(i, currdom) {
            var selectedType = $("#dialogComsumerType").val();
            var selectedMode = $("#dialogDisMode").val();
            //用户还存在未还清贷款，则不允许修改更改类型信息
            /*var isRepay = true;
            if(comsumerType != selectedType || selectedMode != dismode){
                $.ajax({
                    url : "../member/checkIsRepayByEmail",
                    data:JSON.stringify({"email":email}),
                    type : "POST",
                    async:false,
                    contentType : "application/json",
                    success : function(response) {
                        isRepay = response;
                    }
                });
            }
            if(isRepay){*/
                 //修改分销商类型,未做修改不发送请求
                if(comsumerType != selectedType || selectedMode != dismode){
                    updateTypeOfUser(id,selectedType,selectedMode,email);
                }
                layer.closeAll();
           /* }else{
                layer.msg("分销商存在未还清额度，不能修改类型", {icon: 2, time: 1000});
            }*/
        }
        //i为当前层索引，无需进行手工关闭。如果不想关闭，return false即可。
    });
}

function setWords(word) {
    return word == "——" ? "" : word;
}

function activating(em){
    layer.open({
        type: 1,
        title: "激活用户",
        content: "<p style='padding: 20px 10px;text-align: center'>该用户当前处于冻结状态，确定要激活该用户吗？</p>",
        area: ['460px', 'auto'],
        btn: ["激活","取消"],
        shadeClose: false,
        yes:function(index){
            ajax_get("/member/unfreeze/"+em,undefined,undefined,
                function(data){
                    if(data.code == 100){
                        layer.msg(data.msg,{icon:6,time:2000});
                        layer.close(index);
                        searchUser();
                    }else{
                        layer.msg(data.msg,{icon:5,time:2000});
                    }
                },function(e){
                    console.log(e);
                    layer.msg("激活用户异常",{icon:5,time:2000});
                }
            )
        }
    });
}

function deleteUser(e) {
    var uid = Number($(e).data("id"));
    var uemail = $("#user_table").jqGrid("getRowData",uid).email;
    layer.confirm("您确定要删除用户【" + uemail + "】吗？", {icon: 3},
        //i和currdom分别为当前层索引、当前层DOM对象
        function (i, currdom) {
            layer.close(i);
            if (uid) {
                ajax_get("../member/delUser?uid=" + uid, "", undefined,
                    function (data) {
                        if (data.success) {
                            layer.msg(data.msg, {icon: 1, time: 3000});
                            //ajax_get_admin(1);
                            searchUser();
                        } else if (data.code == "2") {
                            window.location.href = "login.html";
                        } else {
                            layer.alert(data.msg, {icon: 2});
                        }
                    },
                    function (xhr, status) {
                        layer.msg('删除数据失败', {icon: 2, time: 1000});
                    });
            }
        }
    );

}

function resetPws(event) {
    var uid = $(event).data("id");
    layer.confirm("您确定需要重置密码？", {icon: 3},
        //i和currdom分别为当前层索引、当前层DOM对象
        function (i, currdom) {
            ajax_get("../member/resetcipher?userId=" + uid, "", undefined,
                function (data) {
                    if (data.success) {
                        layer.msg(data.msg, {icon: 1, time: 3000});
                    } else {
                        window.location.href = "login.html";
                    }
                });
        }
    );
}

//用户等级操作日志弹出框
function operationlog(obj, open, loginName) {
    var goal = $(".operation-log-pop-box");
    if (open == 'Ushow') {
        var optionLogEmail = loginName;//$(obj).parent().parent().parent().parent().find(".e-mail").text();
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
        $.ajax({
            url: "../member/user/rankhistory",
            type: "GET",
            data: {"email": optionLogEmail},
            dataType: "json",
            success: function (result) {
                //result是服务器返回的json结果
                if (result.suc) {//成功
                    var aData = result.data;
                    var itemHTML = '';
                    for (var i = 0; i < aData.length; i++) {
                        itemHTML +=
                            '<p>' +
                            '<span>' + aData[i].createTime + '</span>' +
                            '<span>' + aData[i].operator + '</span>' +
                            '<span>' + aData[i].operateDesc + '</span>' +
                        '</p>';
                    }
                    if (itemHTML == '') {
                        goal.children().append("该用户账号没有等级变更历史");
                    } else {
                        goal.children().append(itemHTML);
                    }
                }else if(result.code==1){
                    window.location.href = "login.html";
                }else if(result.code==2){
                    layer.msg("请求参数不存在或格式错误", {icon: 2, time: 1000});
                }
            }
        });
    } else if (open == 'Uhide') {
        $(".operation-log-pop").hide();
    }
}
//更新分销商类型
function updateTypeOfUser(id,comsumerType,dismode,email){
    $.ajax({
        url : "../member/updateComsumerInfo",
        data:JSON.stringify({"id":id,"comsumerType":comsumerType,"erpAccount":"","distributionMode":dismode}),
        type : "POST",
        contentType:"application/json",  
        success : function(data) {
            if(data.success){
            	//删除用户的永久额度
                layer.msg("分销商类型修改成功！", {icon : 1, time : 2000},function(){
                    //delForeverCredit(email);
                    searchUser();
                });
            }else{
                layer.msg("更新分销商类型失败！", {icon : 2, time : 2000});
            }
        }
    });
}
//根据邮箱删除分销商永久额度
function delForeverCredit(email){
	$.ajax({
        url : "../member/delCreditByEmail",
        data:JSON.stringify({"email":email}),
        type : "POST",
        contentType:"application/json",        
        async:false,
        success : function(data) {
            console.log("删除用户永久额度结果："+data);
        }
    });
}

//后台分销商用户信息导出
function exportUser() {
    var headName = [];
    $.each(getColModel(), function (i, e) {
        var name = e.name;
        if (name && name!='opt') {
            headName.push("header=" + name);
        }
    });
    console.log(headName)
    headName.push("search=" + $("#searchInput").val().trim());
    headName.push("sregdate=" + $("#sregdate").val().trim());
    headName.push("eregdate=" + $("#eregdate").val().trim());
    headName.push("slogdate=" + $("#slogdate").val().trim());
    headName.push("elogdate=" + $("#elogdate").val().trim());
    headName.push("rank="+ ($("#level_select")!=undefined?$("#level_select").val():""));
    headName.push("comsumerType=" + $("#comsumerType").val());
    headName.push("distributionMode" + $("#distributionMode").val());
    if (headName.length > 0) {
        window.location.href = "/member/exportUser?" + headName.join("&");
    }
}

function initRoleCheckBox(id) {
    ajax_get("../member/getRoleNames", "", undefined,
        function (response) {
            if (response.suc) {
                if (response.page) {
                    $(".userset li").remove();
                    var htmlCode = "";
                    $.each(response.page.list, function (i, item) {
                        if(item.id != 1 && item.id != 2) {
                            if (id && item.id == id) {
                                htmlCode += "<li><input name='roleId' checked type='checkbox' value='" + item.id + "'>" + item.name + "</li>";
                            } else {
                                htmlCode += "<li><input name='roleId' type='checkbox' value='" + item.id + "'>" + item.name + "</li>";
                            }
                        }
                    });
                    $(".userset").prepend(htmlCode);
                    $(".edit-pop-list input[type=checkbox]").click(function () {
                        $(this).parent().siblings().find("input[type=checkbox]").removeAttr("checked");
                    });
                }
            } else {
                window.location.href = "login.html";
            }
        },
        function (xhr, status) {
            layer.msg('获取角色数据失败', {icon: 2, time: 1000});
        }
    );
    $(".userset input[name='roleId']").click(function() {
        if($("#user_Jurisdiction input[name='addUserGetMenu'][value='1']").is(":checked") == true) {
            checkTree(1);
        }
    });
}

//弹出框
function adminmessage() {
    //是否附加权限，默认选中否按钮 by huchuyin 2016-9-14
    $("#user_Jurisdiction input[name='addUserGetMenu'][value='2']").prop("checked",true).click();
    $("#userForm input[name=email]").removeAttr("disabled");
    $(".edit-pop-list input[name=email]").val("");
    var goal = $(".user-information");
    layer.open({
        type: 1,
        title: '添加账户',
        shadeClose: true,
        content: goal,
        area: ['750px', '620px'],
        move: false,
        btn: ['保存','test', '重置'],
        yes: function () {
            insertOrUpdate_user();
        },
        btn2: function () {},
        btn3: function () {
            $(".edit-pop-list input[name=email]").val("");
            $("#userForm input[type=hidden]").val("");
            clear();
        },
        end: function () {
            $("#userForm input[name=email]").removeAttr("disabled");
        }
    });
    $("div .layui-layer-btn .layui-layer-btn1").hide();
    $(".edit-pop-list input[name=email]").val("");
    $("#userForm input[type=hidden]").val("");
    clear();
    initRoleCheckBox();
}
//清空
function clear() {
    $(".edit-pop-list input[name=workNo]").val("");
    $(".edit-pop-list input[name=telphone]").val("");
    $(".edit-pop-list input[type=checkbox]").removeAttr("checked");
    $("#user_Jurisdiction input[name='addUserGetMenu'][value='2']").prop("checked",true);
    $("#menuTree").html("");
    $('.Jurisdiction_panel').fadeOut(200);
}

function insertOrUpdate_user() {
    //将表单数据转换为json字符串获取
    var param = serializeJSON($("#userForm").serializeArray());
    var addUserGetMenu = $("#user_Jurisdiction input[name='addUserGetMenu']:checked").val();
    if(addUserGetMenu == "1") {
        var treeObj=$.fn.zTree.getZTreeObj("menuTree");
        if(treeObj != undefined && treeObj != null && treeObj != "") {
            var nodes=treeObj.getCheckedNodes(true);
            var value = "";
            for(var i=0;i<nodes.length;i++){
                //获取已选中的值
                value += nodes[i].id + ",";
            }
            //获取权限数据，保存在json字符串中
            param.menuIds = value;
        }
    }
    $.ajax({
        url: "../member/insert",
        data: JSON.stringify(param),
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            if (data.success) {
                $(".user-information").hide();
                $("#userForm input[name=email]").removeAttr("disabled");
                layer.closeAll();
                layer.msg(data.msg, {icon: 1, time: 4000});
                //ajax_get_admin(1);
            } else if(data.code == "2") {
                window.location.href = "login.html";
            } else {
                layer.alert(data.msg, {icon: 2});
            }
        }
    });
}

function init_role_select() {
    ajax_get("../member/getRoleNames", "", undefined,
        function (response) {
            if (response.suc) {
                if (response.page) {
                    $("#selectRole option:eq(0)").siblings().remove();
                    var htmlCode = "";
                    $.each(response.page.list, function (i, item) {
                        if (item.id != 2) {
                            htmlCode += "<option value='" + item.id + "'>" + item.name + "</option>";
                        }
                    });
                    $("#selectRole option:eq(0)").after(htmlCode);
                    $("#selectRole").change(function () {
                        searchUser();
                    });
                }
            } else {
                window.location.href = "login.html";
            }
        },
        function (xhr, status) {
            layer.msg('获取角色数据失败', {icon: 2, time: 1000});
        }
    );
}

function checkUserName(e) {
    var email = $(e).val();
    var url = "/member/checkEmail?" + (new Date()).getTime();
    $.ajax({
        url: url,
        type: "GET",
        data: {email: email},
        async: false,
        success: function (data) {
            if (data) {
                layer.msg('邮箱已存在，请重新输入', {icon: 2, time: 2000});
                $("#userForm input[name=email]").focus();
                return true;
            }
            return false;
        }
    });
}


/**
 * 点击是否附加权限，处理权限树
 * @param change
 * Created by huchuyin 2016-9-14
 */
function checkTree(change){
    if(change==1){
        var roleId = $(".userset input[name='roleId']:checked").val();
        if(roleId ==undefined || roleId == null || roleId == "") {
            layer.msg('请选择用户角色', {icon: 2, time: 2000});
            $("#user_Jurisdiction input[name='addUserGetMenu'][value='2']").prop("checked",true);
            $("#menuTree").html("");
            $('.Jurisdiction_panel').fadeOut(200);
            return false;
        }
        if(roleId == "2") {
            layer.msg('此角色不能有附加权限', {icon: 2, time: 2000});
            $("#user_Jurisdiction input[name='addUserGetMenu'][value='2']").prop("checked",true);
            $("#menuTree").html("");
            $('.Jurisdiction_panel').fadeOut(200);
            return false;
        }
        var params = {
            roleId:roleId
        };
        $.ajax({
            url: "../member/getMenuList",
            type: "POST",
            data: JSON.stringify(params),
            contentType: "application/json",
            async: false,
            dataType: 'json',
            success: function (data) {
                if (data.success) {
                    var setting = {
                        view: {
                            selectedMulti: false
                        },
                        check: {
                            enable: true
                        },
                        data: {
                            simpleData: {
                                enable: true
                            }
                        }
                    };
                    var zNodes =[];
                    $.each(data.list,function(i,item) {
                        var node = {
                            id:item.id,
                            pId:item.parentid,
                            name:item.name,
                            open:true,
                            checked:item.checked==null?false:item.checked
                        };
                        zNodes.push(node);
                    });
                    $.fn.zTree.init($("#menuTree"), setting, zNodes);
                } else if (data.code == "2") {
                    window.location.href = "login.html";
                } else {
                    layer.msg(data.msg, {icon : 2, time : 1000});
                }
            }
        });
        $('.Jurisdiction_panel').fadeIn(200);
    } else {
        $("#menuTree").html("");
        $('.Jurisdiction_panel').fadeOut(200);
    }
}

/**
 * 点击更新时，获取用户权限树
 * Created by huchuyin 2016-9-14
 */
function getMemberMenuTree(memberId) {
    $('.Jurisdiction_panel').css("width","400px");
    $('.Jurisdiction_panel').fadeIn(200);
    var params = {
        memberId:memberId
    };
    $.ajax({
        url: "../member/getMemMenuList",
        type: "POST",
        data: JSON.stringify(params),
        contentType: "application/json",
        async: false,
        dataType: 'json',
        success: function (data) {
            if (data.success) {
                var setting = {
                    view: {
                        selectedMulti: false
                    },
                    check: {
                        enable: true
                    },
                    data: {
                        simpleData: {
                            enable: true
                        }
                    }
                };
                var zNodes =[];
                $.each(data.list,function(i,item) {
                    var node = {
                        id:item.id,
                        pId:item.parentid,
                        name:item.name + "(" + item.description + ")",
                        open:true,
                        checked:item.checked==null?false:item.checked
                    };
                    zNodes.push(node);
                });
                $.fn.zTree.init($("#menuTree"), setting, zNodes);
            } else if (data.code == "2") {
                window.location.href = "login.html";
            } else {
                layer.msg(data.msg, {icon : 2, time : 1000});
            }
        }
    });
}

function adminSetting() {
    var setting = {
        url:"/member/getUsers",
        datatype : "json", // 返回的数据类型
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        colNames:getColNames(),
        colModel:getColModel(),
        viewrecords : true,
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#user_pagination",//分页
        pagerpos : "center",
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        caption: r == 2?"普通用户":"后台用户",//表名称,
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
            return JSON.stringify(getUserParams());
        },
        gridComplete:function(){
            $("#user_table").on("click","a",function(){
                $(this).parents("td").click();
            });
            $(".amended-user-information.reset").click(function(){
                resetPws(this);
            });
            $(".amended-user-information.modify").click(function(){
                var obj = $(this);
                var rowdata = $("#user_table").jqGrid("getRowData",Number(obj.data("id")));
                $("#userForm input[name=workNo]").val(setWords(rowdata.workNo));
                $("#userForm input[name=email]").val(setWords(rowdata.email));
                $("#userForm input[name=email]").attr("disabled","disabled");
                $("#userForm input[name=telphone]").val(setWords(rowdata.telphone));
                $("#userForm input[name=id]").val(Number(obj.data("id")));
                initRoleCheckBox(obj.attr("tag"));
                var width = '750px';
                if("是"==rowdata.ifAddPermision) {
                    $("#user_Jurisdiction input[name='addUserGetMenu'][value='1']").prop("checked",true);
                    getMemberMenuTree(obj.data("id"));
                    width = '850px';
                } else {
                    $("#user_Jurisdiction input[name='addUserGetMenu'][value='2']").prop("checked",true);
                    $("#menuTree").html("");
                    $('.Jurisdiction_panel').fadeOut(200);
                }
                //$(".user-information").show();
                var goal = $(".user-information");
                layer.open({
                    type: 1,
                    title: '编辑账户',
                    shadeClose: true,
                    content: goal,
                    area: [width, '620px'],
                    move: false,
                    btn:['保存','test','重置'],
                    yes:function(){
                        insertOrUpdate_user();
                    },
                    btn2:function(){},
                    btn3:function(){
                        clear();
                    }
                })
        })
            $(".Reset-delet").click(function(){
                deleteUser(this);
            });
    }};
    if(r != 2){
        setting.rownumbers = true; // 显示行号
    }
    return setting;
}

function getColNames(){
    if(r == 2){
        return ["名称","手机号","用户名","业务员","注册时间","最后登录时间", "分销商类型","分销渠道","注册邀请码","用户邀请码","注册方式","注册人","账户状态","操作"];
    }else{
        return ["用户名","工号","权限","电话","创建人","是否有附加权限","重置密码","修改用户信息","删除"]
    }
}

function getColModel(){
    if(r == 2){
        return [{name:"nick",align:"center",sortable:false},
            {name:"telphone",align:"center",sortable:false},
            {name:"loginName",align:"center",sortable:false},
            {name:"salesmanErp",index:"salesman_erp",align:"center",sortable:true},
            {name:"createTime",index:"create_date",align:"center",sortable:true},
            {name:"login",index:"lastLoginDate",align:"center",sortable:true},
            {name:"comsumerTypeName",align:"center",sortable:false},
            {name:"distributionModeDesc",align:"center",sortable:false},
            {name:"registerInviteCode",align:"center",sortable:false},
            {name:"selfInviteCode",align:"center",sortable:false},
            {name:"isBackRegister",align:"center",sortable:false,
                formatter:function(cellvalue, options, rowObject){
                    return cellvalue ? "后台注册" : "自发注册";
                }
            },
            {name:"registerMan",align:"center",sortable:false},
            {name:"isFrozen",align:"center",sortable:false,
                formatter:function(cellvalue, options, rowObject){
                    return cellvalue ? "账户冻结" : "正常";
                }
            },
            {name:"opt",align:"center",sortable:false ,formatter:function(cellvalue, options, item){
                return "<a herf='javascript:;' style='display: block;' onclick=\"operationlog(this,\'Ushow\',\'"+item.loginName+"\')\">操作日志</a>" +
                    (item.isFrozen?"<a herf='javascript:;' style='display: block;' onclick=\"activating(\'"+item.email+"\')\">激活</a>":"")+
                    "<a href='javascript:;' style='display: block;' onclick=\"change_type(this)\" >更改类型</a>" +
                    "<input type='hidden' name='hiddenRealName' value='" + item.realName + "'/>" +
                    "<input type='hidden' name='hiddenEmail' value='" + item.email + "'/>" +
                    "<input type='hidden' name='hiddenId' value='" + item.id + "'/>" +
                    "<input type='hidden' name='hiddenComsumerType' value='" + item.comsumerType + "'/>" +
                    "<input type='hidden' name='hiddenDisMode' value='" + item.distributionMode + "'/>" +
                    "</div>";
            }
            }]
    }else{
        return [
            {name:"email",align:"center",sortable:false},
            {name:"workNo",align:"center",sortable:false},
            {name:"role",align:"center",sortable:false},
            {name:"telphone",align:"center",sortable:false},
            {name:"createUser",align:"center",sortable:false},
            {name:"ifAddPermision",align:"center",sortable:false,
                formatter:function(cellvalue, options, rowObject){return cellvalue?"是":"否"}},
            {name:"id",align:"center",sortable:false,
                formatter:function(cellvalue, options, rowObject){
                    return '<div data-id="'+rowObject.id+'" class="'+(rowObject.id==1?"Reset-Password":"amended-user-information reset")+'"></div>';
                }},
            {name:"id",align:"center",sortable:false,
                formatter:function(cellvalue, options, rowObject){
                    return '<div data-id="'+rowObject.id+'" tag ="'+rowObject.roleId+'"  class="'+(rowObject.id==1?"Reset-Password":"amended-user-information modify")+'"></div>' ;
                }},
            {name:"id",align:"center",sortable:false,
                formatter:function(cellvalue, options, rowObject){
                    return '<div data-id="'+rowObject.id+'" class="'+(rowObject.id==1?"Reset-Password":"Reset-delet")+'"></div>';
                }}
        ]
    }
}

function getUserParams(){
    var params = {
        role: r,
        rank: $("#level_select")!=undefined?$("#level_select").val():undefined,
        currPage: $('#user_table').getGridParam('page'),
        pageSize: $('#user_table').getGridParam('rowNum'),
        fromFlag:2,
        sort:$('#user_table').getGridParam("sortname"),
        filter:$('#user_table').getGridParam("sortorder")
    };
    if (r == 2) {
        params.search = $("#searchInput").val();
        params.sregdate = $("#sregdate").val().trim();
        params.eregdate = $("#eregdate").val().trim();
        params.slogdate = $("#slogdate").val().trim();
        params.elogdate = $("#elogdate").val().trim();
        params.rank = $("#level_select")!=undefined?$("#level_select").val():"";
        params.comsumerType = $("#comsumerType").val();
        params.distributionMode = $("#distributionMode").val();
        params.fromFlag = 1;
    }else{
        params.workNo = $("#searchInput").val();
        params.role = $("#selectRole").val();
    }
    return params;
}

function searchUser(){
    // 拿到原有的，清除掉
    $("#user_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}
