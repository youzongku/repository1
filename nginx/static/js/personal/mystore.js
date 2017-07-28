define("mystore", ["jquery", "layer", "laypage"], function ($, layer, laypage) {
    //初始化省下拉框
    function initAreaSel(pid, cid, rid,type) {
        if (type && type == 1) {//1表示总店的操作
            ajax_get("/member/getprovs", JSON.stringify(""), "application/json",
                function (data) {
                    $("#province").empty();
                    $("#city").empty();
                    $("#region").empty();
                    for (var i = 0; i < data.length; i++) {
                        var item = $("<option value='" + data[i].id + "' >" + data[i].provinceName + "</option>");
                        $("#province").append(item);
                    }
                    //省份存在就选中
                    if (pid) {
                        $("#province").val(pid);
                    } else {
                        //不存在已选择省份，默认选第一个
                        var sel = $("#province").find("option:first").val();
                        $("#province").val(sel);
                    }
                    citySel(cid, rid,type);
                },
                function (xhr, status) {
                }
            );
        } else {//2表示分店的操作
            ajax_get("/member/getprovs", JSON.stringify(""), "application/json",
                function (data) {
                    $("#part-province").empty();
                    $("#part-city").empty();
                    $("#part-region").empty();
                    for (var i = 0; i < data.length; i++) {
                        var item = $("<option value='" + data[i].id + "' >" + data[i].provinceName + "</option>");
                        $("#part-province").append(item);
                    }
                    //省份存在就选中
                    if (pid) {
                        $("#part-province").val(pid);
                    } else {
                        //不存在已选择省份，默认选第一个
                        var sel = $("#part-province").find("option:first").val();
                        $("#part-province").val(sel);
                    }
                    citySel(cid, rid,type);
                },
                function (xhr, status) {
                }
            );
        }
    }

    //市级下拉框联动 表示总店操作
    $("body").on("change", "#province", function () {
        citySel(undefined,undefined,1);
    });

    //表示分店操作
    $("body").on("change", "#part-province", function () {
        citySel(undefined,undefined,2);
    });

    function citySel(cid, rid,type) {
        if (type && type == 1){
            ajax_get("/member/getcities", "proId=" + $("#province").val(), "",
                function (data) {
                    $("#city").empty();
                    for (var i = 0; i < data.cities.length; i++) {
                        var item = $("<option value='" + data.cities[i].id + "' >" + data.cities[i].cityName + "</option>");
                        $("#city").append(item);
                    }
                    //城市存在就选中
                    if (cid) {
                        $("#city").val(cid);
                    } else {
                        var sel = $("#city").find("option:first").val();
                        $("#city").val(sel);
                    }
                    regionSel(rid,type);
                },
                function (xhr, status) {
                }
            );
        } else {
            ajax_get("/member/getcities", "proId=" + $("#part-province").val(), "",
                function (data) {
                    $("#part-city").empty();
                    for (var i = 0; i < data.cities.length; i++) {
                        var item = $("<option value='" + data.cities[i].id + "' >" + data.cities[i].cityName + "</option>");
                        $("#part-city").append(item);
                    }
                    //城市存在就选中
                    if (cid) {
                        $("#part-city").val(cid);
                    } else {
                        var sel = $("#part-city").find("option:first").val();
                        $("#part-city").val(sel);
                    }
                    regionSel(rid,type);
                },
                function (xhr, status) {
                }
            );
        }
    }

    //区级下拉框联动 总店操作
    $("body").on("change", "#city", function () {
        regionSel(undefined,1);
    });

    //分店操作
    $("body").on("change", "#part-city", function () {
        regionSel(undefined,2);
    });

    //格式化店铺链接 总店
    $("body").on("blur", "#alter_store input[name='uri']", function () {
        var node = $(this);
        formatUrl(node);
    });

    //格式化店铺链接 分店
    $("body").on("blur", "#add-shop-box input[name='part-uri']", function () {
        var node = $(this);
        formatUrl(node);
    });

    function formatUrl(obj) {
        var url = obj.val().trim();
        var regex = /(https?:\/\/)?(\w+\.?)+(\/[a-zA-Z0-9\?%=_\-\+\/]+)?/gi;
        url = url.replace(regex, function (match, capture) {
            if (capture) {
                return match;
            }
            else {
                return 'http://' + match;
            }
        });
        obj.val(url);
    }

    function regionSel(rid,type) {
        if (type && type ==1) {//1表示总店的操作
            ajax_get("/member/getareas", "cityId=" + $("#city").val(), "",
                function (data) {
                    $("#region").empty();
                    var oitem = $("<option value='0' >其它地区</option>");
                    $("#region").append(oitem);
                    for (var i = 0; i < data.areas.length; i++) {
                        var item = $("<option value='" + data.areas[i].id + "' >" + data.areas[i].areaName + "</option>");
                        $("#region").append(item);
                    }
                    //地区存在就选中
                    if (rid) {
                        $("#region").val(rid);
                    } else {
                        $("#region").val(0);
                        // var sel = $("#region").find("option:first").val();
                        // $("#region").val(sel);
                    }
                },
                function (xhr, status) {
                }
            );
        } else {
            ajax_get("/member/getareas", "cityId=" + $("#part-city").val(), "",
                function (data) {
                    $("#part-region").empty();
                    var oitem = $("<option value='0' >其它地区</option>");
                    $("#part-region").append(oitem);
                    for (var i = 0; i < data.areas.length; i++) {
                        var item = $("<option value='" + data.areas[i].id + "' >" + data.areas[i].areaName + "</option>");
                        $("#part-region").append(item);
                    }
                    //地区存在就选中
                    if (rid) {
                        $("#part-region").val(rid);
                    } else {
                        $("#part-region").val(0);
                        // var sel = $("#region").find("option:first").val();
                        // $("#region").val(sel);
                    }
                },
                function (xhr, status) {
                }
            );
        }
    }

    //获取店铺列表数据
    function ajax_get_store(curr) {
        var params = {
            currPage: curr,
            pageSize: 10,
            parentId: 0
        };
        ajax_post("/member/getstore", params, undefined,
            function (response) {
                if (response.suc) {
                    insert_store_tr(response.page.list);
                    init_pagination_store(response.page);
                } else if (response.code == "2") {
                    window.location.href = "login.html";
                } else {
                    layer.msg(response.msg, {icon: 2, time: 2000});
                }
                //添加店铺按钮事件绑定
                $("button.add_shops").click(function (event) {
                    add_store();
                });
            },
            function (xhr, status) {
                layer.msg('获取店铺列表数据出错，请稍候重试', {icon: 2, time: 2000});
            }
        );
    }

    //新建店铺表格行数据
    function insert_store_tr(list) {
        var trNode = $("#store_list").children('thead');
        var trHTML = "";
        for (var i in list) {
            if (list[i].pfid && list[i].pfid == 12) {//12代指线下店铺
            trHTML +=
                '<tr data-sid="' + deal_with_illegal_value(list[i].id) + '" data-pfid="' + deal_with_illegal_value(list[i].pfid) + '">' +
                '<td class="store-name">' + deal_with_illegal_value(list[i].name) + '</td>' +
                '<td class="store-url"><a href="' + list[i].uri + '" target="_blank" title="' + deal_with_illegal_value(list[i].uri) + '">进入店铺</a></td>' +
                '<td class="store-terrace">' + deal_with_illegal_value(list[i].type) + '</td>' +
                '<td class="shroff-account-number" data-number = "' + list[i].shroffAccountNumber + '">' + deal_with_illegal_value(list[i].shroffAccountNumber) + '</td>' +
                '<td class="boss-name">' + deal_with_illegal_value(list[i].keeperName) + '</td>' +
                '<td class="boss-tel">' + deal_with_illegal_value(list[i].tel) + '</td>' +
                '<td class="boss-name" data-idcard = "' + list[i].idcard + '">' + deal_with_illegal_value(list[i].idcard) + '</td>' +
                '<td class="boss-tel" data-zipCode = "' + list[i].zipCode + '">' + deal_with_illegal_value(list[i].zipCode) + '</td>' +
                '<td class="store-address"" id="' + deal_with_illegal_value(list[i].provinceId) + ',' + deal_with_illegal_value(list[i].cityId) + ',' + deal_with_illegal_value(list[i].areaId) + '">' + deal_with_illegal_value(list[i].provinceName) + deal_with_illegal_value(list[i].cityName) + (list[i].areaName ? list[i].areaName : "") + '<span id="addr" style="display: inline-block;">' + deal_with_illegal_value(list[i].addr) + '</span></span></td>' +
                '<td class="store-handle" >' +
                '<input type="button" value="修改" class="modify_shop_btn btn-small"/>' +
                '<input type="button" value="删除" style="margin-top: 5px;" class="delete_shop_btn btn-small"/>' +
                '</td>' +
                '</tr>';
            } else {
                trHTML +=
                    '<tr data-sid="' + deal_with_illegal_value(list[i].id) + '" data-pfid="' + deal_with_illegal_value(list[i].pfid) + '">' +
                    '<td class="store-name">' + deal_with_illegal_value(list[i].name) + '</td>' +
                    '<td class="store-url"><a href="' + list[i].uri + '" target="_blank" title="' + deal_with_illegal_value(list[i].uri) + '">进入店铺</a></td>' +
                    '<td class="store-terrace">' + deal_with_illegal_value(list[i].type) + '</td>' +
                    '<td class="shroff-account-number" data-number = "' + list[i].shroffAccountNumber + '">' + deal_with_illegal_value(list[i].shroffAccountNumber) + '</td>' +
                    '<td class="boss-name">' + deal_with_illegal_value(list[i].keeperName) + '</td>' +
                    '<td class="boss-tel">' + deal_with_illegal_value(list[i].tel) + '</td>' +
                    '<td class="boss-name" data-idcard = "' + list[i].idcard + '">' + deal_with_illegal_value(list[i].idcard) + '</td>' +
                    '<td class="boss-tel" data-zipCode = "' + list[i].zipCode + '">' + deal_with_illegal_value(list[i].zipCode) + '</td>' +
                    '<td class="store-address"" id="' + deal_with_illegal_value(list[i].provinceId) + ',' + deal_with_illegal_value(list[i].cityId) + ',' + deal_with_illegal_value(list[i].areaId) + '">' + deal_with_illegal_value(list[i].provinceName) + deal_with_illegal_value(list[i].cityName) + (list[i].areaName ? list[i].areaName : "") + '<span id="addr" style="display: inline-block;">' + deal_with_illegal_value(list[i].addr) + '</span></span></td>' +
                    '<td class="store-handle" >' +
                    '<input type = "hidden" id = "clientid" value = "'+ (list[i].clientid?list[i].clientid:"")+'">'+
                    '<input type = "hidden" id = "clientsecret" value = "'+(list[i].clientsecret?list[i].clientsecret:"")+'">'+
                     '<input type = "hidden" id = "shopAccount" value = "'+(list[i].shopAccount?list[i].shopAccount:"")+'">'+
                    '<input type="button" value="修改" class="modify_shop_btn btn-small"/>' +
                    '<input type="button" value="删除" style="margin-top: 5px;" class="delete_shop_btn btn-small"/>' +
                    '</td>' +
                    '</tr>';
            }
        }
        $("#store_list").empty().append(trNode).append(trHTML);

        //////////////////////////////////////////查看分店//////////////////////////////////
        $(".check-distract").click(function (event){
            watchPartshopInf(1,this);
        });

        //修改店铺按钮事件绑定---主店操作
        $("input.modify_shop_btn").click(function (event) {
            alter_store(this);
        });

        //删除店铺按钮事件绑定----主店操作
        $("input.delete_shop_btn").click(function (event) {
            delete_store(this);
        });
    }

    //初始化分页栏
    function init_pagination_store(page) {
        if ($("#pagination_store")[0] != undefined) {
            $("#pagination_store").empty();
            laypage({
                cont: 'pagination_store',
                pages: page.totalPage,
                curr: page.currPage,
                groups: 5,
                skin: 'yahei',
                first: '首页',
                last: '尾页',
                prev: '上一页',
                next: '下一页',
                skip: true,
                jump: function (obj, first) {
                    //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                    if (!first) {
                        ajax_get_store(obj.curr);
                    }
                }
            });
        }
    }

    //弹出框店铺类型下拉选事件
    function dialog_store_type_change() {
        var type = $("#alter_store select[name='type']").val();
        if (type == 10) {
            $("#alter_store input[name='other']").css("display", "inline-block");
        } else {
            $("#alter_store input[name='other']").css("display", "none");
        }
         //有赞商城
        if(type == 13){
            $("#alter_store #app_key_tr").css("display", "block");
            $("#alter_store #app_secret_tr").css("display", "block");
            $("#alter_store #url_tr").css("display", "none");
            $("#alter_store #accredit").css("display", "none");
            $("#alter_store #shop_account_tr").css("display", "block");
            $("#app_key_show").removeClass("pdd_appkey-show").removeClass("appkey-show").addClass("yz_appkey-show");
            //京东商城
        }else if(type == 4){
            $("#alter_store #app_key_tr").css("display", "block");
            $("#alter_store #app_secret_tr").css("display", "block");
            $("#alter_store #url_tr").css("display", "block");
            $("#alter_store #accredit").css("display", "block");
            $("#alter_store #shop_account_tr").css("display", "none");
            $("#app_key_show").removeClass("yz_appkey-show").removeClass("pdd_appkey-show").addClass("appkey-show");
        }else if(type == 14){
            $("#alter_store #app_key_tr").css("display", "block");
            $("#alter_store #app_secret_tr").css("display", "block");
            $("#alter_store #url_tr").css("display", "none");
            $("#alter_store #accredit").css("display", "block");
            $("#alter_store #shop_account_tr").css("display", "none");
            $("#app_key_show").removeClass("appkey-show").removeClass("yz_appkey-show").addClass("pdd_appkey-show");
        }else{
            $("#alter_store #app_key_tr").css("display", "none");
            $("#alter_store #app_secret_tr").css("display", "none");
            $("#alter_store #url_tr").css("display", "none");
            $("#alter_store #accredit").css("display", "none");
            $("#alter_store #shop_account_tr").css("display", "none");
        }
    }

	//新增店铺----主店操作
	function add_store() {
		generateContent(new Object(), function(content) {
			layer.open({
				type: 1,
				title: "新增店铺",
				content: content,
				area: ['610px', '500px'],
				btn: ["保存", "取消"],
				closeBtn: 1,
				shadeClose: false,
				success: function(layero, index){
					initAreaSel(undefined,undefined,undefined,1);
				},
				//i和currdom分别为当前层索引、当前层DOM对象
				yes: function(i, currdom) {
					console.log(i + "<--i	currdom-->" + currdom[0]);
					var params = getParamters();
					//保存新增店铺信息
					if (params != undefined) {
                        $(".layui-layer-btn0").hide();
						var shopType = params.name == null ? null : parseInt(params.type);
						var cheParam = {
							shopName: params.name,
							type: shopType
						}
						var flag = false;
						//检查店铺名称在系统中是否已经存在
						$.ajax({
							url: "/member/checkShopName",
							type: 'POST',
							data: cheParam,
							async: false,//是否异步
							dataType: 'json',
							success: function (data) {
								if (data.code && data.code == 1) {//说明没有查到相关店铺信息
									flag = true;
								} else if (data.suc) {
									layer.msg("该店铺已存在", {icon : 2, time : 2000});
                                    $(".layui-layer-btn0").show();
								} else {
									layer.msg(data.msg, {icon : 2, time : 2000});
                                    $(".layui-layer-btn0").show();
								}
							},
							error: function (XMLHttpRequest, textStatus) {
                                $(".layui-layer-btn0").show();
							}
						});
						if(flag == true){
							ajax_post("/member/addstore", params, undefined,
								function(response) {
									if (response.suc) {
										layer.close(i);
										layer.msg('您的新店铺添加成功！', {icon : 1, time : 2000}, function() {
											ajax_get_store(1);
										});
									} else if (response.code == "2") {
										window.location.href = "login.html";
									} else {
										layer.msg(response.msg, {icon : 2, time : 2000});
                                        $(".layui-layer-btn0").show();
									}
								},
								function(xhr, status) {
									layer.msg('添加店铺出错，请稍候重试', {icon : 2, time : 2000});
                                    $(".layui-layer-btn0").show();
								}
							);
						}
					}
				}
				//i为当前层索引，无需进行手工关闭。如果不想关闭，return false即可。
				//cancel: function(i) { layer.close(i); }
			});
			//弹出窗口店铺类型下拉选事件绑定
			$("select[name='type']").change(function(event) {
				dialog_store_type_change();
			});
		});
	}

	//修改店铺信息---主店操作
	function alter_store(node) {
		var $tds = $(node).parent().parent().children();
		var sid = $(node).parent().parent().data("sid");
		var store = new Object();
		store.pfid = $(node).parent().parent().data("pfid");
		store.name = $tds.eq(0).text();
		store.link = $($tds.eq(1)).find("a").attr("href");
		store.type = $tds.eq(2).text();
		store.shroffAccountNumber = $tds.eq(3).data("number");
		store.keeperName = $tds.eq(4).text()
		store.tel = $tds.eq(5).text();
		store.idcard = $tds.eq(6).data("idcard");
		store.zipCode = $tds.eq(7).data("zipCode");
		store.addr = $tds.eq(8).find("#addr").text();
        store.clientid = $(node).parent().find("#clientid").val();
        store.clientsecret = $(node).parent().find("#clientsecret").val();
        store.shopAccount = $(node).parent().find("#shopAccount").val();
        store.sid = sid;
		var addrIds = $tds.eq(8).attr("id").split(",");
		generateContent(store, function(content) {
			layer.open({
				type: 1,
				title: "修改店铺信息",
				content: content,
				area: ['600px', '500px'],
				btn: ["保存", "取消"],
				closeBtn: 1,
				shadeClose: false,
				success: function(layero, index){
                    if (parseInt(addrIds[0])) {
                        initAreaSel(addrIds[0],addrIds[1],addrIds[2],1);
                    } else {
                        initAreaSel(undefined,undefined,undefined,1);
                    }
                    $("#accredit").click(function(){
                        accredit(this);
                    })
				},
				//i和currdom分别为当前层索引、当前层DOM对象
				yes: function(i, currdom) {
					console.log(i + "<--i	currdom-->" + currdom[0]);
					var params = getParamters();
                    params.oldType = store.pfid;
					//更新店铺信息
					if (params != undefined) {
                        $(".layui-layer-btn0").hide();
                        var shopType = params.type == null ? null : parseInt(params.type);
						var cheParam = {
							shopName: params.name,
							type: shopType
						}
						var flag = false;
						flag =  store.pfid == shopType && store.name ==params.name ? true : false;
						//检查店铺名称在系统中是否已经存在
						if (!flag) {
							$.ajax({
								url: "/member/checkShopName",
								type: 'POST',
								data: cheParam,
								async: false,//是否异步
								dataType: 'json',
								success: function (data) {
									if (data.code && data.code == 1) {//说明没有查到相关店铺信息
										flag = true;
									} else if (data.suc) {
										layer.msg("该店铺已存在", {icon : 2, time : 2000});
                                        $(".layui-layer-btn0").show();
									} else {
										layer.msg(data.msg, {icon : 2, time : 2000});
                                        $(".layui-layer-btn0").show();
									}
								},
								error: function (XMLHttpRequest, textStatus) {
                                    $(".layui-layer-btn0").show();
								}
							});
						}
						params.sid = sid;
						if (flag == true){
							ajax_post("../member/alterstore", params, undefined,
								function(response) {
									if (response.suc) {
										layer.close(i);
										var curr = $("#pagination .laypage_curr").text();
										ajax_get_store(curr == "" ? 1 : curr);
									} else if (response.code == "2") {
										window.location.href = "login.html";
									} else {
										layer.msg(response.msg, {icon : 2, time : 2000});
                                        $(".layui-layer-btn0").show();
									}
								},
								function(xhr, status) {
									layer.msg("修改店铺信息出错，请稍后重试！", {icon : 2, time : 2000});
                                    $(".layui-layer-btn0").show();
								}
							);
						}
					}
				}
				//i为当前层索引，无需进行手工关闭。如果不想关闭，return false即可。
				//cancel: function(i) { layer.close(i); }
			});
			//弹出窗口店铺类型下拉选事件绑定
			$("select[name='type']").change(function(event) {
				dialog_store_type_change();
			});
		});
	}

    //获取表单验证后的参数------主店的修改和添加操作
    function getParamters() {
        var name = $("#alter_store input[name='name']").val().trim();
        var uri = $("#alter_store input[name='uri']").val().trim();
        var type = $("#alter_store select[name='type']").val();
        var other = $("#alter_store input[name='other']").val().trim();
        var tel = $("#alter_store input[name='tel']").val().trim().replace(/\s/g, "");
        // var areaAddr = $('#province option:selected').text() + " " + $('#city option:selected').text() + " " + $('#region option:selected').text();
        var addr = $("#alter_store input[name='addr']").val().trim();
        var keeperName = $("#alter_store input[name='keeperName']").val().trim();
        var shroffAccountNumber = $("#alter_store input[name='shroffAccountNumber']").val();
        var idcard = $("#alter_store input[name='idcard']").val().trim();
        var zipCode = $("#alter_store input[name='zipCode']").val().trim();
        var clientid = $("#alter_store input[name='app_key']").val().trim();
        var clientsecret = $("#alter_store input[name='app_secret']").val().trim(); 
        var shopAccount = $("#alter_store input[name='shopAccount']").val().trim();
        //手机和电话验证
        var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$|(0[1-9][0-9])-(\d{7,8})$|(0[1-9]\d{2}-(\d{7,8}))$/;
        if (name == "") {
            layer.msg('请输入店铺名称', {icon: 2, time: 2000});
            return undefined;
        }
        if (type == "" || (type == "10" && other == "")) {
            layer.msg('请选择或输入店铺类型', {icon: 2, time: 2000});
            return undefined;
        }
        if (keeperName == "") {
            layer.msg('请输入店主姓名', {icon: 2, time: 2000});
            return undefined;
        }
        if (tel == "") {
            layer.msg('请输入店铺联系电话', {icon: 2, time: 2000});
            return undefined;
        }
        if (!CP_RE.test(tel)) {
            layer.msg("输入手机号码或者固定电话格式错误", {icon: 2, time: 2000});
            return undefined;
        }
        if (idcard && !checkIDCard(idcard)) {
            layer.msg('请输入有效的身份证', {icon: 2, time: 2000});
            return undefined;
        }
        if (zipCode && !checkPost(zipCode)) {
            layer.msg('请输入有效的邮政编码', {icon: 2, time: 2000});
            return undefined;
        }
        if (addr == "") {
            layer.msg('请输入详细地址', {icon: 2, time: 2000});
            return undefined;
        }
        return {
            name: name,
            uri: uri,
            tel: tel,
            type: type,
            other: other,
            provinceId: $("#province").val(),
            cityId: $("#city").val(),
            areaId: $("#region").val(),
            provinceName: $("#province").find("option:selected").html(),
            cityName: $("#city").find("option:selected").html(),
            areaName: $("#region").find("option:selected").html(),
            addr: addr,
            shroffAccountNumber: shroffAccountNumber,
            keeperName: keeperName,
            idcard: idcard,
            zipCode: zipCode,
            clientid:clientid,
            clientsecret:clientsecret,
            shopAccount:shopAccount
        };
    }

    //获取弹出框HTML内容
    function generateContent(store, call) {
        ajax_get("/member/getshopplat", {}, undefined,
            function (response) {
                var optionHTML = '';
                for (var i in response) {
                    optionHTML += '<option value="' + response[i].id + '" ' + (store.pfid == response[i].id ? 'selected="selected"' : '') + '>' + response[i].shopPlatform + '</option>';
                }
                call(
                    '<div style="width: 560px; height: 630px; border: 1px solid #ccc; margin-left: auto; margin-right: auto; padding: 10px;">' +
                    '<table id="alter_store" style="table-layout: fixed; width: 100%; border:">' +
                    '<tr style="width: 550px; display: block; margin-top: 20px;">' +
                    '<th style="width: 20%; display: inline-block;  text-align: right;"><font style="color:#e4393c">*</font>店铺名称：</th>' +
                    '<td style="width: 75%; display: inline-block;  text-align: left;">' +
                    '<input type="text" name="name" value="' + (store.name == undefined ? '' : store.name) + '"  style="width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                    '</td>' +
                    '</tr>' +
                    '<tr style="width: 550px; display: block; margin-top: 20px;">' +
                    '<th style="width: 20%; display: inline-block;  text-align: right;"><font style="color:#e4393c">*</font>店铺平台：</th>' +
                    '<td style="width: 75%; display: inline-block;  text-align: left;">' +
                    '<select name="type" style="width: 35%; margin-right: 10px; padding: 4px; ">' +
                    '<option value="">请选择店铺平台</option>' +
                    optionHTML +
                    '</select>' +
                    '<input type="text" name="other" value="' + (store.pfid == undefined || store.pfid != 10 ? '' : store.type) + '" placeholder="请输入其他店铺平台" style="width: 46%; display: ' + (store.pfid == undefined || store.pfid != 10 ? 'none' : 'inline-block') + '; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                    '</td>' +
                    '</tr>' +
                    '<tr style="width: 550px; display: block; margin-top: 20px;">' +
                    '<th style="width: 20%; display: inline-block;  text-align: right;"><font style="color:#e4393c">*</font>店主姓名：</th>' +
                    '<td style="width: 75%; display: inline-block;  text-align: left;">' +
                    '<input type="text" name="keeperName" value="' + (store.keeperName == undefined ? '' : store.keeperName) + '"  style="width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                    '</td>' +
                    '</tr>' +
                    '<tr style="width: 550px; display: block; margin-top: 20px;">' +
                    '<th style="width: 20%; display: inline-block;  text-align: right;"><font style="color:#e4393c">*</font>联系电话：</th>' +
                    '<td style="width: 75%; display: inline-block;  text-align: left;">' +
                    '<input type="text" name="tel" value="' + (store.tel == undefined ? '' : store.tel) + '"  style="width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                    '</td>' +
                    '</tr>' +
                    '<tr style="width: 550px; display: block; margin-top: 20px;">' +
                    '<th style="width: 20%; display: inline-block;  text-align: right;"><font style="color:#e4393c">*</font>详细地址：</th>' +
                    '<td style="width: 75%; display: inline-block;  text-align: left;">' +
                    '<select id="province" style="width: 28%;padding: 5px;"></select><select id="city" style="width: 28%; padding: 5px;"></select><select id="region" style="width: 27.5%; padding: 5px;"></select>' +
                    '<input placeholder="请填写详细地址，无需重复填写省市区" type="text" name="addr" value="' + (store.addr == undefined ? '' : store.addr) + '"  style="margin-top:5px;width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                    '</td>' +
                    '</tr>' +
                    '<tr style="width: 550px; margin-top: 20px;'+(store.pfid == 13?"display:block;":"display:none;")+'" id = "shop_account_tr">' +
                    '<th style="width: 20%; display: inline-block;  text-align: right;">店铺账号：</th>' +
                    '<td style="width: 75%; display: inline-block;  text-align: left;position: relative;">' +
                    '<input type="text" name="shopAccount" value="' + (store.shopAccount == undefined ? '' : store.shopAccount) + '"  style="width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                    '<i class="sure-question shop_account_show" style="right:40px;top:7px">?</i>'+
                    '</td>' +
                    '</tr>' +
                    '<tr style="width: 550px; margin-top: 20px;'+(store.pfid == 4?"display:block;":"display:none;")+'" id = "url_tr">' +
                    '<th style="width: 20%; display: inline-block;  text-align: right;"><font style="color:#e4393c"></font>回调URL：</th>' +
                    '<td style="width: 75%; display: inline-block;  text-align: left; position: relative;">' +
                    '<input type="text" name="callback_uri" value="https://www.tomtop.com.cn/member/callbackSuccess" disabled="true" style="width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                    '<i class="sure-question URL-show" style="right:40px;top:7px">?</i>'+
                    '</td>' +
                    '</tr>' +
                    '<tr style="width: 550px; margin-top: 20px;'+(store.pfid == 4||store.pfid == 13 || store.pfid == 14?"display:block;":"display:none;")+'" id = "app_key_tr">' +
                    '<th style="width: 20%; display: inline-block;  text-align: right;"><font style="color:#e4393c"></font>Appkey：</th>' +
                    '<td style="width: 75%; display: inline-block;  text-align: left; position: relative;">' +
                    '<input type="text" name="app_key" value="' + (store.clientid != null ? store.clientid : '') + '"  style="width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                    '<i  id = "app_key_show" class="sure-question '+(store.pfid == 13?"yz_appkey-show":store.pfid == 4?"appkey-show":"pdd_appkey-show")+'" style="right:40px;top:7px">?</i>'+
                    '</td>' +
                    '</tr>' +
                    '<tr style="width: 550px;margin-top: 20px;'+(store.pfid == 4||store.pfid == 13||store.pfid == 14?"display:block;":"display:none;")+'" id = "app_secret_tr">' +
                    '<th style="width: 20%; display: inline-block;  text-align: right;"><font style="color:#e4393c"></font>App Secret：</th>' +
                    '<td style="width: 75%; display: inline-block;  text-align: left; position: relative;">' +
                    '<input type="text" name="app_secret"  value="' + (store.clientsecret != null ? store.clientsecret : '') + '" style="width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                    '<input type = "hidden" name = "response_type" value = "code" >'+
                    '<a href="javascript:void(0);" id = "accredit" data-pfid = "'+store.pfid+'" data-sid = "'+ store.sid +'" style="position: absolute;right: -38px;top: 5px;font-size: 12px;color: #117ad4;'+(store.pfid == 4 && store.sid ?"display:block":"display:none")+'">点击这里进行授权</a>'+
                    '</td>' +
                    '</tr>' +
                    '<tr style="width: 550px; display: block; margin-top: 20px;">' +
                    '<th style="width: 20%; display: inline-block;  text-align: right;"><font style="color:#e4393c"></font>店铺链接：</th>' +
                    '<td style="width: 75%; display: inline-block;  text-align: left;">' +
                    '<input type="text" name="uri" value="' + (store.link == undefined ? '' : store.link) + '"  style="width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                    '</td>' +
                    '</tr>' +
                    '<tr style="width: 550px; display: block; margin-top: 20px;">' +
                    '<th style="width: 20%; display: inline-block;  text-align: right;"><font style="color:#e4393c"></font>收款账户：</th>' +
                    '<td style="width: 75%; display: inline-block;  text-align: left;">' +
                    '<input type="text" name="shroffAccountNumber" value="' + (store.shroffAccountNumber == undefined ? '' : store.shroffAccountNumber) + '"  style="width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                    '</td>' +
                    '</tr>' +
                    '<tr style="width: 550px; display: block; margin-top: 20px;">' +
                    '<th style="width: 20%; display: inline-block;  text-align: right;"><font style="color:#e4393c"></font>身份证：</th>' +
                    '<td style="width: 75%; display: inline-block;  text-align: left;">' +
                    '<input type="text" name="idcard" value="' + (store.idcard == undefined ? '' : store.idcard) + '"  style="width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                    '</td>' +
                    '</tr>' +
                    '<tr style="width: 550px; display: block; margin-top: 20px;">' +
                    '<th style="width: 20%; display: inline-block;  text-align: right;"><font style="color:#e4393c"></font>邮编：</th>' +
                    '<td style="width: 75%; display: inline-block;  text-align: left;">' +
                    '<input type="text" name="zipCode" value="' + (store.zipCode == undefined ? '' : store.zipCode) + '"  style="width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                    '</td>' +
                    '</tr>' +
                    '</table>' +
                    '</div>'
                );
            },
            function (xhr, status) {
                layer.msg('获取店铺平台数据出错，请稍候重试', {icon: 2, time: 2000});
            }
        );
    }

    //删除店铺信息
    function delete_store(node) {
        var sid = $(node).parent().parent().data("sid");
        layer.confirm("确认删除该店铺？", {icon: 3},
            //i和currdom分别为当前层索引、当前层DOM对象
            function (i, currdom) {
                console.log(i + "<--i	currdom-->" + currdom[0]);
                layer.close(i);
                ajax_get("../member/delstore", {sid: sid}, undefined,
                    function (response) {
                        if (response.suc) {
                            var curr = $("#pagination .laypage_curr").text();
                            ajax_get_store(curr == "" ? 1 : curr);
                        } else if (response.code == "2") {
                            window.location.href = "login.html";
                        } else {
                            layer.msg(response.msg, {icon: 2, time: 2000});
                        }
                    },
                    function (xhr, status) {
                        layer.msg("删除店铺信息出错，请稍后重试！", {icon: 2, time: 2000});
                    }
                );
            }
            //i为当前层索引，无需进行手工关闭。如果不想关闭，return false即可。
            //function(i) { layer.close(i); }
        );
    }

    $("body").on("click",".p-reason",function(){
        layer.alert('站点名称与其他分销商冲突。', {
            title: "原因",
            icon: 2
        })
    });

    $("body").on("mouseover", ".URL-show", function () {
        layer.tips('请将该回调地址填入京东授权页面的回调URL里', this, {
            tips: [2, '#7AC141'],
            time: 0
        });
    })
    .on("mouseover", ".shop_account_show", function () {
        layer.tips('有赞店铺拉取订单时需要您填写登录后台账号时的手机号，即店铺账号', this, {
            tips: [2, '#7AC141'],
            time: 0
        });
    })
    .on("mouseover", ".appkey-show", function () {
        layer.tips('请根据http://jos.jd.com/里商家自研应用的文档步骤说明申请到AppKey、App Secret并填写在这里', this, {
            tips: [2, '#7AC141'],
            time: 0
        });
    })
    .on("mouseover", ".yz_appkey-show", function () {
        layer.tips('请根据http://open.youzan.com/里接入需知步骤文档申请到AppKey、App Secret填入这里', this, {
            tips: [2, '#7AC141'],
            time: 0
        });
    })
    .on("mouseover", ".pdd_appkey-show", function () {
        layer.tips('拼多多API.....', this, {
            tips: [2, '#7AC141'],
            time: 0
        });
    })
    .on("mouseout", ".id-question,.pay-question,.name-question,.sure-question,.real-pay-question", function () {
        layer.closeAll('tips')
    });

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<分店操作<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //添加分店
    $("body").on("click",".add_shop_btn",function(){
        var $add_shop_btn = $(this);
        var headshop = $($add_shop_btn.parents(".store-handle").siblings()[0]).html();
        var platform = $($add_shop_btn.parents(".store-handle").siblings()[2]).html();
        $("#add-shop-box").find("div").eq(1).html("");//清空弹出框数据  --平台
        $("#add-shop-box").find("div").eq(3).html("");//清空弹出框数据  --总店名称
        var parentId = $add_shop_btn.parents("tr").data("sid");
        var platformId = $add_shop_btn.parents("tr").data("pfid");
        $("#add-shop-box input[name='part-name']").val("");
        $("#add-shop-box input[name='part-keeperName']").val("");
        $("#add-shop-box input[name='part-tel']").val("");
        $("#add-shop-box input[name='part-uri']").val("");
        $("#add-shop-box input[name='part-addr']").val("");
        layer.open({
            type: 1,
            title: "添加分店",
            area: ['600px', '510px'],
            btn: ['保存','取消'],
            skin: 'layui-layer-demo',
            shadeClose: true,
            content: $("#add-shop-box"),
            success: function(layero, index){
                $("#add-shop-box").find("div").eq(1).html(platform);
                $("#add-shop-box").find("div").eq(3).html(headshop);
                initAreaSel(undefined,undefined,undefined,2);
            },
            yes: function(i, currdom) {
                var params = getParamtersforPart();
                //保存新增分店店铺信息
                if (params != undefined) {
                    $(".layui-layer-btn0").hide();
                    platformId = platformId == null ? null : parseInt(platformId);
                    var cheParam = {
                        shopName : params.name,
                        type : platformId
                    }
                    var flag = false;
                    //检查店铺名称在系统中是否已经存在
                    $.ajax({
                        url: "/member/checkShopName",
                        type: 'POST',
                        data: cheParam,
                        async: false,//是否异步
                        dataType: 'json',
                        success: function (data) {
                            if (data.code && data.code == 1) {//说明没有查到相关店铺信息
                                flag = true;
                            } else if (data.suc) {
                                layer.msg("该店铺已存在", {icon : 2, time : 2000});
                                $(".layui-layer-btn0").show();
                            } else {
                                layer.msg(data.msg, {icon : 2, time : 2000});
                                $(".layui-layer-btn0").show();
                            }
                        },
                        error: function (XMLHttpRequest, textStatus) {
                            $(".layui-layer-btn0").show();
                        }
                    });
                    if(flag == true){
                        params.type = platformId;
                        params.parentId = parentId;
                        ajax_post("/member/addstore", params, undefined,
                            function(response) {
                                if (response.suc) {
                                    layer.close(i);
                                    layer.msg('您的'+headshop+'新分店添加成功！', {icon : 1, time : 2000}, function() {
                                    });
                                } else if (response.code == "2") {
                                    window.location.href = "login.html";
                                } else {
                                    layer.msg(response.msg, {icon : 2, time : 2000});
                                    $(".layui-layer-btn0").show();
                                }
                            },
                            function(xhr, status) {
                                layer.msg('添加新的分店店铺出错，请稍候重试', {icon : 2, time : 2000});
                                $(".layui-layer-btn0").show();
                            }
                        );
                    }
                }
            }
        });
    });

    //点击授权
    function accredit(node){
        var id = $(node).data("sid"),
        app_key = $("input[name = app_key ]").val(),
        app_secret = $("input[name = app_secret]").val(),
        platform = $("#alter_store").find("select[name=type]").val();
        if(!app_key || !app_secret){  
           layer.msg('请填写完整信息', {icon: 5, time: 2000});
           return; 
        }
        var params = {
            shopId:id,
            clientid:app_key,
            clientsecret:app_secret,
            platform:platform
        };
        ajax_post("/member/jd/code", JSON.stringify(params), "application/json",
            function (response) {
                if(response.success){
                    var tr = "tr[data-sid="+id+"]";
                    $(tr).find("#clientid").val(app_key);
                    $(tr).find("#clientsecret").val(app_secret);
                    window.open(response.msg, "_blank");
                }else{
                    layer.msg(response.msg,{icon:5,time:2000});
                }
            },
            function (xhr, status) {
                layer.msg('获取授权链接失败', {icon: 2, time: 2000});
            }
        );
    }

    //查看分店信息
    function watchPartshopInf(curr,obj){
        $("#mine-phone-table tbody").empty();
        $("#check_despart").show();
        $("#mine_shops").hide();
        $("title").text("查看分店 - TOMTOP Supply Chain");
        var $this = $(obj);
        var parentShopName = $($this.siblings()[0]).html();
        var parentId = $this.parents("tr").data("sid");
        var trNodepart = $("#mine-phone-table").children('thead');
        var trHtmlpart = "<tbody>";
        var addr;
        var params = {
            currPage: curr,
            pageSize: 10,
            parentId: parentId
        };
        ajax_post("/member/getstore", params, undefined,
            function (response) {
                if (response.suc) {
                    var partlist = response.page.list;
                    for (var i in partlist){
                        addr = "";
                        addr = deal_with_illegal_value(partlist[i].provinceName) + deal_with_illegal_value(partlist[i].cityName) + deal_with_illegal_value(partlist[i].areaName) + deal_with_illegal_value(partlist[i].addr)
                        trHtmlpart +=
                            '<tr class="branchshop">'
                            +'<td style="width: 10%;" data-sid="' + deal_with_illegal_value(partlist[i].id) + '" data-pfid="' + deal_with_illegal_value(partlist[i].pfid) + '"">'+deal_with_illegal_value(partlist[i].id)+'</td>'
                            +'<td style="width: 10%;" class="partstore-name">' + deal_with_illegal_value(partlist[i].name) + '</td>'
                            +'<td style="width: 28%;" class="partstore-addr" id="'+ deal_with_illegal_value(partlist[i].provinceId) + ',' + deal_with_illegal_value(partlist[i].cityId) + ',' + deal_with_illegal_value(partlist[i].areaId) +'">' + addr + '</td>'
                            +'<td style="width: 20%;" class="partstore-time">' + deal_with_illegal_value(partlist[i].createDateStr) + '</td>'
                            +'<td style="width: 10%;" class="partstore-keeperName">' + deal_with_illegal_value(partlist[i].keeperName) + '</td>'
                            +'<td style="width: 10%;" class="partstore-tel">' + deal_with_illegal_value(partlist[i].tel) + '</td>'
                            +'<td style="width: 12%;" class="check-edit">'
                            +'<span class="part-modify">编辑</span>'
                            +'<span class="part-delete">删除</span>'
                            +'<span class="part-type" style="display: none">'+deal_with_illegal_value(partlist[i].type)+'</span>'
                            +'<span class="parent-name" style="display: none">'+deal_with_illegal_value(parentShopName)+'</span>'
                            +'<span class="part-uri" style="display: none">'+deal_with_illegal_value(partlist[i].uri)+'</span>'
                            +'<span class="part-detailAddr" style="display: none">'+deal_with_illegal_value(partlist[i].addr)+'</span>'
                            +'</td></tr>'
                    }
                    trHtmlpart += "</tbody>";
                    $("#mine-phone-table").empty().append(trNodepart).append(trHtmlpart);
                    init_pagination_partstore(response.page,obj);
                    //编辑分店信息
                    $("#check_despart .part-modify").click(function(){
                        partshopModify(this,obj);
                    });
                    //删除分店信息
                    $("#check_despart .part-delete").click(function(){
                        partshopDelete(this,obj);
                    });
                } else if (response.code == "2") {
                    window.location.href = "login.html";
                } else {
                    layer.msg(response.msg, {icon: 2, time: 2000});
                }
            },
            function (xhr, status) {
                layer.msg('获取店铺列表数据出错，请稍候重试', {icon: 2, time: 2000});
            }
        );

    }

    //分店编辑 obj:this,watchShop:触发查看分店功能的选择器
    function partshopModify(obj,watchShop){
        //获得初始化的数据
        var store = new Object();
        store.platformId = $(obj).parents(".branchshop").find("td").eq(0).data("pfid");
        store.sid = $(obj).parents(".branchshop").find("td").eq(0).data("sid");
        store.type = $(obj).parents(".check-edit").find(".part-type").html();
        store.parentShopName = $(obj).parents(".check-edit").find(".parent-name").html();
        store.shopName = $(obj).parents(".branchshop").find(".partstore-name").html();
        store.keeperName = $(obj).parents(".branchshop").find(".partstore-keeperName").html();
        store.tel = $(obj).parents(".branchshop").find(".partstore-tel").html();
        store.uri = $(obj).parents(".check-edit").find(".part-uri").html();
        store.detailAddr = $(obj).parents(".check-edit").find(".part-detailAddr").html();
        var partaddrIds = $(obj).parents(".branchshop").find(".partstore-addr").attr("id").split(",");
        $("#add-shop-box").find("div").eq(1).html("");//清空弹出框数据  --平台
        $("#add-shop-box").find("div").eq(3).html("");//清空弹出框数据  --总店名称
        $("#add-shop-box input[name='part-name']").val("");
        $("#add-shop-box input[name='part-keeperName']").val("");
        $("#add-shop-box input[name='part-tel']").val("");
        $("#add-shop-box input[name='part-uri']").val("");
        layer.open({
            type: 1,
            title: "修改分店",
            content: $("#add-shop-box"),
            area: ['600px', '510px'],
            btn: ['保存','取消'],
            skin: 'layui-layer-demo',
            shadeClose: true,
            success: function(layero, index){
                //将初始化数据填入相应的输入栏
                $("#add-shop-box").find("div").eq(1).html(store.type);
                $("#add-shop-box").find("div").eq(3).html(store.parentShopName);
                $("#add-shop-box input[name='part-name']").val(store.shopName);
                $("#add-shop-box input[name='part-keeperName']").val(store.keeperName);
                $("#add-shop-box input[name='part-tel']").val(store.tel);
                $("#add-shop-box input[name='part-uri']").val(store.uri);
                $("#add-shop-box input[name='part-addr']").val(store.detailAddr);
                initAreaSel(partaddrIds[0],partaddrIds[1],partaddrIds[2],2);
            },
            yes: function(i, currdom) {
                var params = getParamtersforPart();//表示初始化之后，用户修改的信息
                //保存新增分店店铺信息
                if (params != undefined) {
                    $(".layui-layer-btn0").hide();
                    store.platformId = store.platformId == null ? null : parseInt(store.platformId);
                    var cheParam = {
                        shopName : params.name,
                        type : store.platformId
                    }
                    var flag = false;
                    flag = store.shopName == params.name ? true : false;
                    //检查店铺名称在系统中是否已经存在
                    if (!flag) {
                        $.ajax({
                            url: "/member/checkShopName",
                            type: 'POST',
                            data: cheParam,
                            async: false,//是否异步
                            dataType: 'json',
                            success: function (data) {
                                if (data.code && data.code == 1) {//说明没有查到相关店铺信息
                                    flag = true;
                                } else if (data.suc) {
                                    layer.msg("该店铺已存在", {icon: 2, time: 2000});
                                    $(".layui-layer-btn0").show();
                                } else {
                                    layer.msg(data.msg, {icon: 2, time: 2000});
                                    $(".layui-layer-btn0").show();
                                }
                            },
                            error: function (XMLHttpRequest, textStatus) {
                                $(".layui-layer-btn0").show();
                            }
                        });
                    }
                    params.sid = store.sid;
                    if (flag == true){
                        ajax_post("../member/alterstore", params, undefined,
                            function(response) {
                                if (response.suc) {
                                    layer.close(i);
                                    var curr = $("#pagination .laypage_curr").text();
                                    watchPartshopInf(curr == "" ? 1 : curr,watchShop);
                                } else if (response.code == "2") {
                                    window.location.href = "login.html";
                                } else {
                                    layer.msg(response.msg, {icon : 2, time : 2000});
                                    $(".layui-layer-btn0").show();
                                }
                            },
                            function(xhr, status) {
                                layer.msg("修改店铺信息出错，请稍后重试！", {icon : 2, time : 2000});
                                $(".layui-layer-btn0").show();
                            }
                        );
                    }
                }
            }
        });
    }

    //分店删除
    function partshopDelete(obj,watchShop){
        var $this = $(obj);
        var sid = $this.parents(".branchshop").find("td").eq(0).data("sid");
            layer.confirm("确认删除该店铺？", {icon: 3},
                //i和currdom分别为当前层索引、当前层DOM对象
                function (i, currdom) {
                    console.log(i + "<--i	currdom-->" + currdom[0]);
                    layer.close(i);
                    ajax_get("../member/delstore", {sid: sid}, undefined,
                        function (response) {
                            if (response.suc) {
                                var curr = $("#pagination .laypage_curr").text();
                                watchPartshopInf(curr == "" ? 1 : curr,watchShop);
                            } else if (response.code == "2") {
                                window.location.href = "login.html";
                            } else {
                                layer.msg(response.msg, {icon: 2, time: 2000});
                            }
                        },
                        function (xhr, status) {
                            layer.msg("删除店铺信息出错，请稍后重试！", {icon: 2, time: 2000});
                        }
                    );
                }
                //i为当前层索引，无需进行手工关闭。如果不想关闭，return false即可。
                //function(i) { layer.close(i); }
            );
    }

    //获取表单验证参数-----分店
    function getParamtersforPart(){
        var name = $("#add-shop-box input[name='part-name']").val().trim();
        var keeperName = $("#add-shop-box input[name='part-keeperName']").val().trim();
        var tel = $("#add-shop-box input[name='part-tel']").val().trim();
        // var areaAddr = $('#part-province option:selected').text() + " " + $('#part-city option:selected').text() + " " + $('#part-region option:selected').text();
        var addr = $("#add-shop-box input[name='part-addr']").val().trim();
        var uri = $("#add-shop-box input[name='part-uri']").val().trim();
        //手机和电话验证
        var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$|(0[1-9][0-9])-(\d{7,8})$|(0[1-9]\d{2}-(\d{7,8}))$/;
        if (name == "") {
            layer.msg('请输入店铺名称', {icon: 2, time: 2000});
            return undefined;
        }
        if (keeperName == "") {
            layer.msg('请输入店主姓名', {icon: 2, time: 2000});
            return undefined;
        }
        if (tel == "") {
            layer.msg('请输入店铺联系电话', {icon: 2, time: 2000});
            return undefined;
        }
        if (!CP_RE.test(tel)) {
            layer.msg("输入手机号码或者固定电话格式错误", {icon: 2, time: 2000});
            return undefined;
        }
        if (addr == "") {
            layer.msg('请输入详细地址', {icon: 2, time: 2000});
            return undefined;
        }
        return {
            name: name,
            uri: uri,
            tel: tel,
            type: 12,
            provinceId: $("#part-province").val(),
            cityId: $("#part-city").val(),
            areaId: $("#part-region").val(),
            provinceName: $("#part-province").find("option:selected").html(),
            cityName: $("#part-city").find("option:selected").html(),
            areaName: $("#part-region").find("option:selected").html(),
            addr: addr,
            keeperName: keeperName
        };
    }

    //初始化分页栏
    function init_pagination_partstore(page,watchShop) {
        if ($("#pagination_partshop")[0] != undefined) {
            $("#pagination_partshop").empty();
            laypage({
                cont: 'pagination_partshop',
                pages: page.totalPage,
                curr: page.currPage,
                groups: 5,
                skin: 'yahei',
                first: '首页',
                last: '尾页',
                prev: '上一页',
                next: '下一页',
                skip: true,
                jump: function (obj, first) {
                    //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                    if (!first) {
                        watchPartshopInf(obj.curr,watchShop);
                    }
                }
            });
        }
    }

    return {
        ajax_get_store: ajax_get_store
    };
});