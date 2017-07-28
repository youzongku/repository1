//定义全局变量
var layer = undefined;
var laypage = undefined;

//初始化角色列表
function init_role(layerParam, laypageParam ,BbcGrid) {
	//初始化全局变量
	layer = layerParam;
	laypage = laypageParam;
	var grid = new BbcGrid();
	grid.initTable($("#role_table"),roleSetting());
	$(".list_content p select").change(function () {
		searchRoles();
	});
	$(".Add-Roles").click(function () {
		addOrUpdateRole("add", null);
	});
	$(".p-box-top p select").change(function () {
		initMenuTree();
	});
}

//插入角色数据
function insert_ul_role(list) {
	$("ul.list_message").remove();
	var ulHTML = '';
	var sequence = 0;
	for (var i in list) {
		//if(list[i].id != 2) {
			sequence += 1;
			ulHTML += "<tr class=\"list_message\">" +
				"<td style=\"width: 10%;\">"+sequence+"</td>"+
				"<td style=\"width: 10%;display: none\" tag = \"id\">" + deal_with_illegal_value(list[i].id) + "</td>" +
				"<td style=\"width: 20%;\"> " +
				"<span class=\"\" tag = \"name\" >"+deal_with_illegal_value(list[i].name)+"</span> " +
				"<span class=\"\" style='display: none'>"+deal_with_illegal_value(list[i].desc)+"</span> " +
				"</td> ";
			if(list[i].id == 1 || list[i].id == 2) {
				ulHTML += "<td>系统默认</td>"+
                    "<td style=\"height: 50px;\" id=\"reset-IEpadding\"> " +
					"<b class='role-change'><span class=\"Reset-Password\"></span></b> " +
					"<b class='role-forbidden'><span class=\"Reset-Password\"></span></b>" +
					"<b class='role-delet'><span class=\"Reset-Password\"></span></b> " +
					"</td> " +
					"</tr>";
			} else {
				ulHTML += "<td>"+deal_with_illegal_value(list[i].createUser)+"</td>"+
                    "<td> " +
					"<b class=\"role-change\"> " +

					"<span class=\"modifybtn\">修改</span> <span style=\"display: none;\">保存</span> " +
					"</b> " +
					"<b class=\"role-forbidden\" tag=\""+list[i].isactive+"\"><span class=\"forbidden\">"+getState(list[i])+"</span></b> " +
					"<b class=\"role-delet\"> " +
					"<span class=\"delbtn\">删除</span> " +
					"<span style=\"display: none;\">取消</span> " +
					"</b> " +
					"</td> " +
					"</tr>";
			}
		//}
	}
	$(".role-list-content").prepend(ulHTML);
	$(".role-delet span.delbtn").click(function(){
		delete_role(this);
	});
	$(".role-forbidden span.forbidden").click(function(){
		forbidden(this);
	});
	$(".role-change span.modifybtn").click(function(){
		var parentUl = $(this).parent().parent().parent();
		var sid = parseInt(parentUl.find("[tag=id]").text());
		var role = {
			rid : sid
		};
		addOrUpdateRole('update',role);
	});
}
function getState(item){
	return item.isactive == true ? "禁用" : "启用";
}

function forbidden(event){
	var param = {
		rid : $(event).parent().data("id"),
		flag : $(event).parent().attr("tag")
	};
	layer.confirm("您确定"+$(event).text()+"该角色吗？", {icon: 3},
		//i和currdom分别为当前层索引、当前层DOM对象
		function(i, currdom) {
			layer.close(i);
			ajax_post("../member/updrole", JSON.stringify(param), "application/json",
				function(data){
					if(data.suc){
						layer.msg("该角色已被"+$(event).text()+"。", {icon : 1, time : 2000});
						if(param.flag == "true"){
							$(event).parent().attr("tag","false").find("span").text("启用");
						}else if(param.flag == "false"){
							$(event).parent().attr("tag","true").find("span").text("禁用");
						}
					}else if(data.code == "2"){
						window.location.href = "login.html";
					} else {
						layer.alert(data.msg, {icon: 2});
					}
				});
		}
	);
}

function delete_role(event){
	var name = $(event).data("name");
	var sid = $(event).data("id");
	layer.confirm("您确定要删除角色【"+name+"】吗？", {icon: 3},
		//i和currdom分别为当前层索引、当前层DOM对象
		function(i, currdom) {
			layer.close(i);
			ajax_get("../member/delrole?rid=" + sid,"",undefined,
				function(data){
					if(data.suc){
						layer.msg("角色【"+name+"】删除成功。", {icon : 1, time : 2000});
						searchRoles();
					}else if(data.code == "2"){
						window.location.href = "login.html";
					} else {
						layer.alert(data.msg, {icon: 2});
					}
				});
		}
	);
}
//弹出框
function rolesetting(obj, check){
	if(check == 'Ushow') {
		layer.open({
			type:1,
			title:'审核',
			btn:['保存','取消'],
			shadeClose: true,
			content:$(".role-permission-setting"),
			area:['520px','540px'],
			success:function(){
				//加载下拉值
				initSelect();
				//加载菜单树
				initMenuTree();
			},
			yes:function(){
				permission_configure();
				layer.closeAll();
			}
		})
	}
	else if(check == 'Uhide'){
		$(".role-permission-setting").hide();
	}
}

function initMenuTree(){
	var params = {};
	$.ajax({
		url:"/member/getTree?roleId="+$(".p-box-top p select").val(),
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
				var zNodes = [
					{id:0, name:"所有栏目和按钮",isParent:true}
				];
				$.each(data.list,function(i,item) {
					var node = {
						id:item.id,
						pId:item.parentid,
						name:item.name + "(" + item.description + ")",
						open:false,
						checked:item.checked==null?false:item.checked
					};
					zNodes.push(node);
				});
				$.fn.zTree.init($("#tree"), setting, zNodes);
			} else if (data.code == "2") {
				window.location.href = "login.html";
			} else {
				layer.msg(data.msg, {icon : 2, time : 1000});
			}
		}
	});
}

function initSelect(){
	ajax_get("../member/getRoleNames", "", undefined,
		function(response) {
			if (response.suc) {
				if(response.page){
					$(".p-box-top p select option").remove();
					var htmlCode = "";
					$.each(response.page.list,function(i,item){
						if(item.id != 2 && item.id != 1){
							htmlCode += "<option value=\""+item.id+"\">"+item.name+"</option>";
						}
					});
					$(".p-box-top p select").prepend(htmlCode);
				}
			}else if(response.code == "2"){
                window.location.href = "login.html";
            } else {
                layer.alert(response.msg, {icon: 2});
            }
		},
		function(xhr, status) {
			layer.msg('获取角色数据失败', {icon : 2, time : 1000});
		}
	);
}

function permission_configure() {
    var treeObj = $.fn.zTree.getZTreeObj("tree");
    var nodes = treeObj.getCheckedNodes();
    var menus = [];
    var notExpendMenus = [];
	$.each(nodes, function (i, node) {
		var id = node.id;
		if(id != 0) {
			menus.push(id);
		}
	});
	var params = {roleId:$(".p-box-top p select").val(),select: menus, notExpend: notExpendMenus};
	ajax_post("../member/configure", params, undefined,
		function (response) {
			if (response.success) {
				$(".role-permission-setting").hide();
                layer.msg('权限配置成功', {icon: 1, time: 2000});
			} else {
				layer.msg('配置权限失败,请重新操作', {icon: 2, time: 3000});
			}
		},
		function (xhr, status) {
			layer.msg('配置权限失败', {icon: 2, time: 1000});
		}
	);
}

function addOrUpdateRole(flag,role){
	var title = flag == "add" ? "新增角色" : "修改角色";
	var url = flag == "add" ? "../member/addrole" : "../member/updrole";
	var name = "";
	var desc = "";
	var y_fragment = '<input type="radio" name="is-receive"  value="receiveMessage">';
	var n_fragment = '<input type="radio" name="is-receive"  value="receiveMessage">';
    $.ajax({
        url: "/member/getrole",
        type: 'POST',
        data: JSON.stringify(role == undefined ? {} : role),
        contentType: 'application/json',
        dataType: 'json',
        success: function (data) {
            if (data.suc) {
                name = data.data.roleName;
                desc = data.data.roleDesc;
                if (data.data.ismessage == true){
                    y_fragment = '<input type="radio" name="is-receive" checked="checked" value="receiveMessage">';
                } else {
                    n_fragment = '<input type="radio" name="is-receive" checked="checked" value="receiveMessage">';
                }
            } else if (data.code == "2") {
                window.location.href = "login.html";
            }
            layer.open({
                type: 1,
                title: title,
                content:
                '<div style="width: 400px; height: 140px; border: 1px solid #ccc; margin-left: auto; margin-right: auto; padding: 10px;">' +
                '<table id="edit_user_rank_dialog" style="table-layout: fixed; width: 100%; border:">' +
                '<tr style="width: 400px; display: block; margin-top: 20px;">' +
                '<th style="width: 20%; display: inline-block;  text-align: right;">角色名称：</th>' +
                '<td style="width: 75%; display: inline-block;  text-align: left;">' +
                '<input type="text" name="roleName" value="'+name+'" style="width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                '</td>' +
                '</tr>' +
                '<tr style="width: 400px; display: block; margin-top: 20px;">' +
                '<th style="width: 20%; display: inline-block;  text-align: right;">角色描述：</th>' +
                '<td style="width: 75%; display: inline-block;  text-align: left;">' +
                '<input type="text" name="roleDesc" value="'+desc+'" style="width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                '</td>' +
                '</tr>' +
                '<tr style="width: 400px; display: block; margin-top: 20px;">' +
                '<th style="width: 27%; display: inline-block;  text-align: right;">是否接收短信：</th>' +
                '<td style="display: inline-block;  text-align: left;">是:' +
                y_fragment +
                '</td>' +
                '<td style="display: inline-block;  text-align: left;">否:' +
                n_fragment +
                '</td>' +
                '</tr>' +
                '</table>' +
                '</div>',
                area: ['460px', '260px'],
                btn: ["保存", "取消"],
                closeBtn: 1,
                shadeClose: false,
                //i和currdom分别为当前层索引、当前层DOM对象
                yes: function(i, currdom) {
                    if($("input[name=roleName]").val()){
                        var param = {
                            rid : role == null ? null : role.rid,
                            roleName : $("input[name=roleName]").val(),
                            roleDesc : $("input[name=roleDesc]").val()
                        };
                        if($($("#edit_user_rank_dialog").find("input[name=is-receive]")[0]).prop("checked")){
                            param.ismessage = true;
                        }else{
                            param.ismessage = false;
                        }
                        postRole(url,param);
                        layer.close(i);
                    }else{
                        layer.msg('角色名不能为空', {icon : 2, time : 1000});
                    }

                }
            });
        },
        error: function (XMLHttpRequest, textStatus) {
        }
    });
}

function postRole(url,param){
	ajax_post(url, JSON.stringify(param), 'application/json',
		function(data){
			if(data.suc){
				layer.msg('操作成功', {icon : 1, time : 1000});
				searchRoles();
			}else if(data.code == "2"){
				window.location.href = "login.html";
			} else {
                layer.alert(data.msg, {icon: 2});
            }
		},
		function(xhr, status) {
			layer.msg('操作失败', {icon : 2, time : 1000});
		});
}

/*jqGrid-Table*/
function roleSetting() {
	var setting = {
		url:"/member/getroles",
		datatype : "json", // 返回的数据类型
		ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
		mtype : "post", // 提交方式
		height : "auto", // 表格宽度
		autowidth : true, // 是否自动调整宽度
		colNames:["角色名称","创建人","操作"],
		colModel:[
			{name: "name", index: "name",align: "center", sortable: false},
			{name: "id", index: "id",align: "center", sortable: false,
				formatter:function(cellvalue, options, rowObject){
					return cellvalue==1||cellvalue==2?"系统默认": deal_with_illegal_value(rowObject.createUser);
				}},
			{name: "id", index: "id",align: "center", sortable: false,
				formatter:function(cellvalue, options, rowObject){
					return cellvalue==1||cellvalue==2?"" +
					"<b class='role-change'><span class=\"Reset-Password\"></span></b> " +
					"<b class='role-forbidden'><span class=\"Reset-Password\"></span></b>" +
					"<b  class='role-delet'><span class=\"Reset-Password\"></span></b> ":
					"<b class=\"role-change\">" +
						"<span tag='"+cellvalue+"' class=\"modifybtn\">修改</span> <span style=\"display: none;\">保存</span> " +
					"</b> " +
					"<b class=\"role-forbidden\" data-id='"+cellvalue+"' tag=\""+rowObject.isactive+"\"><span class=\"forbidden\">"+getState(rowObject)+"</span></b> " +
					"<b  class=\"role-delet\"> " +
						"<span data-id='"+cellvalue+"' data-name='"+rowObject.name+"' class=\"delbtn\">删除</span> " +
						"<span style=\"display: none;\">取消</span> " +
					"</b> ";
				}}
		],
		viewrecords : true,
		rowNum : 10,
		rowList : [ 10, 20, 30 ],
		pager:"#role_pagination",//分页
		pagerpos : "center",
		pgbuttons : true,
		loadtext: "加载中...",
		pgtext : "当前页 {0} 一共{1}页",
		caption:"角色设置",//表名称,
		rownumbers : true,
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
			return JSON.stringify(getRole());
		},
		gridComplete:function(){
			$("#role_table").on("click","a",function(){
				$(this).parents("td").click();
			});
			$(".role-delet span.delbtn").click(function(){
				delete_role(this);
			});
			$(".role-forbidden span.forbidden").click(function(){
				forbidden(this);
			});
			$(".role-change span.modifybtn").click(function(){
				var sid = parseInt($(this).attr("tag"));
				var role = {
					rid : sid
				};
				addOrUpdateRole('update',role);
			});
		}};
	return setting;
}
function getRole(){
	return {
		currPage: $('#role_table').getGridParam('page'),
		pageSize: $('#role_table').getGridParam('rowNum')
	}
}
function searchRoles(){
	// 拿到原有的，清除掉
	$("#role_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}
