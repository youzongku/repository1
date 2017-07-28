define("sparea", ["jquery", "layer", "laypage", "webuploader", "BbcGrid"], function($, layer, laypage, WebUploader, BbcGrid) {
    var uploader = undefined;

    function init() {
        //gain_table_list_data(1);
        gain_table_list_data_new();
        bind_main_element_event();
        init_webuploader_register();
    }

    function gain_table_list_data(curr) {
        var params = {
            currPage: curr ? curr : 1,
            pageSize: $("#active_pageSize").val(),
            actName: $("#sprice_actname").val().trim(),
            actState: $("#sprice_status").val(),
            startTime: $("#sprice_start_time").val().trim(),
            endTime: $("#sprice_end_time").val().trim(),
            creater: $("#sprice_creater").val().trim(),
        };
        ajax_post("../product/mktool/activities", JSON.stringify(params), "application/json",
            function(data) {
                if (data.suc) {
                    insert_table_list_data(data);
                    init_table_list_pagination(data);
                } else {
                    layer.alert(data.msg, {icon: 2});
                }
            },
            function(xhr, status) {console.error("error--->" + status);}
        );
    }

    function gain_table_list_data_new() {
        var grid = new BbcGrid();
        grid.initTable($("#discount_price_table"), getSetting_discount_price());
    }

    function getSetting_discount_price() {
        var setting = {
            url:"../product/mktool/activities",
            ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
            rownumbers : true, // 是否显示前面的行号
            datatype : "json", // 返回的数据类型
            mtype : "post", // 提交方式
            height : "auto", // 表格宽度
            autowidth : true, // 是否自动调整宽度
            // styleUI: 'Bootstrap',
            colNames:["id","排序","活动名称","活动备注","活动时间","商品数量","创建时间", "创建人","活动状态", "操作"],
            colModel:[{name:"id",index:"id",width:"12%",align:"center",sortable:false,hidden:true},
                {name:"activitySort",index:"activity_sort",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"activityName",index:"activity_name",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"activityRemark",index:"activity_remark",width:"14%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"startTime",index:"start_time",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                    return '<span>' + deal_with_illegal_value(rowObject.startTime) + '</span> 至 <span>' + deal_with_illegal_value(rowObject.endTime) + '</span>';
                }},
                {name:"activityPnum",index:"activity_pnum",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"createTime",index:"create_time",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"createUser",index:"create_user",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"activityStatus",index:"activity_status",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                    return (cellvalue == 1 ? '未开始' : cellvalue == 2 ? '启用中' : cellvalue == 3 ? '已结束' : cellvalue == 4 ? '已禁止' : '---');
                }},
                {name:"activityStatus",index:"detail",width:"12%",align:"center",sortable:false, formatter:function(cellvalue, options, rowObject){
                    var operateHtml =
                        (cellvalue == 1 ? ('<span class="sprice_relate_goods" data-id="' + rowObject.id + '" data-name="' + rowObject.activityName + '" >关联商品</span>') : '')
                        +(cellvalue == 1 ? ('<span class="sprice_open_act" data-id="' + rowObject.id + '" data-name="' + rowObject.activityName + '">开启</span>') : '')
                        +'<span class="sprice_watch_act" data-id="' + rowObject.id + '">查看</span>'
                        + ((cellvalue == 1) ? ('<span class="r_forbidden" data-id="' + rowObject.id + '" data-name="' + rowObject.activityName + '">禁止</span>') : '')
                        + (cellvalue == 1 ? ('<span class="sprice_modify_act" data-id="' + rowObject.id + '">修改</span>') : '');
                    return '<span class=\"special_dose\">' + operateHtml + '</span>';
                }}
            ],
            viewrecords : true,
            rowNum : 10,
            rowList : [ 10, 20, 30 ],
            pager:"#discount_price_pagination",//分页
            caption:"特价专区   ",//表名称
            pagerpos : "center",
            pgbuttons : true,
            autowidth: true,
            rownumbers: true, // 显示行号
            loadtext: "加载中...",
            pgtext : "当前页 {0} 一共{1}页",
            jsonReader:{
                root: "list",  //数据模型
                page: "currPage",//数据页码
                total: "totalPage",//数据总页码
                records: "totalCount",//数据总记录数
                repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
                //cell: "cell",//root 中row 行
                id: "id"//唯一标识
            },
            serializeGridData : function() {
                return JSON.stringify(getSearchParam_sprice_area($(this).jqGrid('getGridParam', 'postData')))
            }
        };
        return setting;
    }

    function getSearchParam_sprice_area(postData) {
        var params = {
            currPage: $('#discount_price_table').getGridParam('page'),
            pageSize: $('#discount_price_table').getGridParam('rowNum'),
            actName: $("#sprice_actname").val().trim(),
            actState: $("#sprice_status").val(),
            startTime: $("#sprice_start_time").val().trim(),
            endTime: $("#sprice_end_time").val().trim(),
            creater: $("#sprice_creater").val().trim(),
            sord:postData.sord,
            sidx:postData.sidx
        };
        return params;
    }


    function insert_table_list_data(data) {
        var itemHTML = '';
        if (!deal_with_illegal_boolean(data.list)) {
            itemHTML = '<tr><td colspan="9" style="color: green;">当前筛选/查询条件下数据列表为空</td></tr>';
        } else {
            var state = 0;
            $.each(data.list, function(i, item) {
                state = item.activityStatus;
                itemHTML +=
                    '<tr class="" data-id="' + item.id + '">' +
                    '<td width="5%">' + deal_with_illegal_value(item.activitySort) + '</td>' +
                    '<td width="7%">' + deal_with_illegal_value(item.activityName) + '</td>' +
                    '<td width="7%">' + deal_with_illegal_value(item.activityRemark) + '</td>' +
                    '<td width="36%"><span>' + deal_with_illegal_value(item.startTime) + '</span> 至 <span>' + deal_with_illegal_value(item.endTime) + '</span></td>' +
                    '<td width="7%">' + deal_with_illegal_value(item.activityPnum) + '</td>' +
                    '<td width="16%">' + deal_with_illegal_value(item.createTime) + '</td>' +
                    '<td width="7%">' + deal_with_illegal_value(item.createUser) + '</td>' +
                    '<td width="7%">' + (state == 1 ? '未开始' : state == 2 ? '启用中' : state == 3 ? '已结束' : state == 4 ? '已禁止' : '---') + '</td>' +
                    '<td width="8%" class="special_dose">' +
                    (state == 1 ? ('<span class="sprice_relate_goods" data-id="' + item.id + '" data-name="' + item.activityName + '">关联商品</span>') : '') +
                    (state == 1 ? ('<span class="sprice_open_act" data-id="' + item.id + '" data-name="' + item.activityName + '">开启</span>') : '') +
                    '<span class="sprice_watch_act" data-id="' + item.id + '">查看</span>' +
                    ((state == 1) ? ('<span class="r_forbidden" data-id="' + item.id + '" data-name="' + item.activityName + '">禁止</span>') : '') +
                    (state == 1 ? ('<span class="sprice_modify_act" data-id="' + item.id + '">修改</span>') : '') +
                    '</td>' +
                    '</tr>';
            });
        }
        $("#sprice_tbody").html(itemHTML);
        $("#activeTotal").text(data.totalCount);
        $("#activePages").text(data.totalPage);
    }

    function init_table_list_pagination(page) {
        if ($("#sprice_pagination")[0] != undefined) {
            $("#sprice_pagination").empty();
            laypage({
                cont: 'sprice_pagination',
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
                    if (!first) {
                        gain_table_list_data(obj.curr);
                    }
                }
            });
        }
    }

    function bind_main_element_event() {
        $("#sprice_actname").on("keyup",function(event){
            var key = event.keyCode || event.which;
            if (key == 13) {
                //gain_table_list_data(1)
                $("#discount_price_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
            }
        })
        $("#sprice_creater").on("keyup",function(event){
            var key = event.keyCode || event.which;
            if (key == 13) {
                //gain_table_list_data(1)
                $("#discount_price_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
            }
        });

        $("#sprice_search_btn").click(function(e) {
            //gain_table_list_data(1);
            $("#discount_price_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
        });

        $("#sprice_relate_search_title").keydown(function (e) {
            if(e.keyCode == 13){
                $("#sprice_relate_goods_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
            }
        });

        $("body").on("change","#sprice_status",function(){
            //postCoupons(null,false);
            $("#discount_price_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
        });


        $("body").on('click', '.sprice_relate_goods', function(e) {
            $(".special_zone").hide();
            $(".zone_relevance").show();
            $(".zone_relevance").first().find("span").eq(3).text("当前活动：" + $(this).data("name"));
            $("#sprice_selected_actid").
                val($(this).data("id"));
            $("#sprice_relate_disprice_left").val("");
            $("#sprice_relate_disprice_right").val("");
            $("#sprice_relate_search_title").val("");
            $("#sprice_relate_checkbox_all")[0].checked = false;
            $("#sprice_relate_checkbox_invert")[0].checked = false;
            $("#sprice_selected_goods_number").text("0");
            $("#sprice_selected_checkbox_all")[0].checked = false;
            $("#sprice_selected_checkbox_invert")[0].checked = false;
            $("#sprice_predis_number").val("");
            //$("#sprice_batch_nolimit")[0].checked = false;
            //$("#sprice_batch_limit")[0].checked = false;
            //$("#sprice_limit_number").val("");
            $("#sprice_selected_table").html("");
            $(".zone_relevance .sure_link").removeAttr("disabled");
            $(".zone_relevance .sure_link").css("background-color", "#141c25");
            init_relate_goods_area();
        });

        $("body").on('click', '.sprice_open_act', function(e) {
            var id = $(this).data("id");
            var name = $(this).data("name");
            layer.confirm("您确定开启活动【" + name + "】吗？", {icon: 3, title: '提示'}, function(index) {
                layer.closeAll();
                ajax_get("../product/mktool/openact?id=" + id + "&t=" + Math.random(), "", "",
                    function(data) {
                        if (data.suc) {
                            layer.msg("开启活动【" + name + "】成功！", {icon: 1, time: 3000}, function() {
                                //gain_table_list_data(1);
                                $("#discount_price_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
                            });
                        } else {
                            layer.msg(data.msg, {icon: 2, time: 5000}, function() {
                                //gain_table_list_data(1);
                                $("#discount_price_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
                            });
                        }
                    },
                    function(xhr, status) { console.log("error--->" + status); }
                );
            });
        });
        $("body").on('click', '.sprice_watch_act', function(e) {
            $(".special_zone").hide();
            $(".zone_examine").show();
            //init_watch_goods_area($(this).data("id"));
            init_watch_goods_area_new($(this).data("id"));
        });
        $("body").on('click', '.r_forbidden', function(e) {
            var id = $(this).data("id");
            var name = $(this).data("name");
            layer.confirm("您确定禁用活动【" + name + "】吗？", {icon: 3, title: '提示'}, function(index) {
                layer.closeAll();
                ajax_get("../product/mktool/closeact?id=" + id + "&t=" + Math.random(), "", "",
                    function(data) {
                        if (data.suc) {
                            layer.msg("禁用活动【" + name + "】成功！", {icon: 1, time: 3000}, function() {
                                //gain_table_list_data(1);
                                $("#discount_price_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
                            });
                        } else {
                            layer.alert(data.msg, {icon: 2});
                        }
                    },
                    function(xhr, status) { console.log("error--->" + status); }
                );
            });
        });
        $("body").on('click', '.sprice_modify_act', function(e) {
            var id = $(this).data("id");
            $(".special_zone").hide();
            $(".zone_add_activity").show();
            $(".zone_add_activity").first().find("span").eq(1).text("编辑活动");
            $("#sprice_addact_form")[0].reset();
            $("#sprice_addact_id").val("");
            $("#sprice_add_or_edit_mark").val("edit");
            $("#sprice_create_btn").text("保存");
            $("#sprice_create_btn").removeAttr("disabled");
            $("#sprice_create_btn").css("background-color", "#00b7ee");
            init_uploader();
            ajax_get("../product/mktool/getactinfo?id=" + id + "&t=" + Math.random(), "", "",
                function(data) {
                    var act = data.info.act;
                    var imgs = data.info.imgs;
                    var pros = data.info.pros;
                    var $form = $("#sprice_addact_form");
                    $("#sprice_addact_id").val(act.id);
                    var imgHTML = '';
                    if (imgs.length > 0) {
                        $.each(imgs, function(i, item) {
                            imgHTML +=
                                '<tr id="uploaded_' + item.id + '">' +
                                '<td style="width: 20%;"><img src="../product/spact/poster?id=' + item.id + '" style="width: 50px; height: 50px;"></td>' +
                                '<td style="width: 20%;">' +
                                    '<input type="button" value="删除" data-id="' + item.id + '" class="sprice_file_delete_btn" style="width: 80px; height: 30px; border-radius: 5px; background-color: #00b7ee; cursor: pointer;">' +
                                '</td>' +
                                '<td style="width: 20%;"></td>' +
                                '</tr>';
                        });
                    }
                    $("#sprice_file_list").children().eq(0).html(imgHTML);
                    $form.find("input[name='activityName']").val(deal_with_illegal_json(act.activityName));
                    $form.find("input[name='showTotitle']")[0].checked = act.showTotitle;
                    $form.find("textarea[name='activityRemark']").val(deal_with_illegal_json(act.activityRemark));
                    $form.find("input[name='startTime']").val(deal_with_illegal_json(act.startTime).substr(0, 11));
                    $form.find("input[name='endTime']").val(deal_with_illegal_json(act.endTime).substr(0, 11));
                    $form.find("input[name='activitySort']").val(deal_with_illegal_json(act.activitySort));
                },
                function(xhr, status) { console.log("error--->" + status); }
            );
        });
        $("#active_pageSize").change(function(e) {
            gain_table_list_data(1);
        });
        $("#sprice_addact").click(function(e) {
            $(".special_zone").hide();
            $(".zone_add_activity").show();
            $(".zone_add_activity").first().find("span").eq(1).text("添加活动");
            $("#sprice_addact_form")[0].reset();
            $("#sprice_addact_id").val("");
            $("#sprice_add_or_edit_mark").val("add");
            $("#sprice_create_btn").text("创建");
            $("#sprice_create_btn").removeAttr("disabled");
            $("#sprice_create_btn").css("background-color", "#00b7ee");
            init_uploader();
        });
        $("#sprice_addact_back").click(function(e) {
            $(".zone_add_activity").hide();
            $(".special_zone").show();
            //gain_table_list_data(1);
            $("#discount_price_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
        });
        $("#sprice_relate_back").click(function(e) {
            $(".zone_relevance").hide();
            $(".special_zone").show();
            //gain_table_list_data(1);
            $("#discount_price_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
        });
        $("#sprice_watch_back").click(function(e) {
            $(".zone_examine").hide();
            $(".special_zone").show();
            //gain_table_list_data(1);
            $("#discount_price_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
        });
        $("#sprice_create_btn").click(function(e) {
            var id = $("#sprice_addact_id").val();
            var mark = $("#sprice_add_or_edit_mark").val();
            var params = create_activity();
            if (params != null) {
                if ($("#sprice_file_list").children().eq(0).children().length == 0) {
                    layer.msg("请选择海报图片", {icon: 0, time: 3000});
                } else {
                    layer.load(1, {shade: 0.5});
                    $("#sprice_create_btn").attr("disabled", true);
                    $("#sprice_create_btn").css("background-color", "grey");
                    isaulogin(function(email) {
                        params.createUser = email;
                        params.id = id;
                        ajax_post("../product/mktool/saveactivity", JSON.stringify(params), "application/json",
                            function(data) {
                                if (data.suc) {
                                    if (mark == "add") {
                                        $("#sprice_addact_id").val(data.id);
                                        $("#sprice_add_or_edit_mark").val("edit");
                                        $.extend(uploader.options.formData, {
                                            actId: data.id,
                                            user: email
                                        });
                                    } else {
                                        $.extend(uploader.options.formData, {
                                            actId: id,
                                            user: email
                                        });
                                    }
                                    if ($("tr[id^='WU_FILE_']").length > 0) {
                                        $("tr[id^='WU_FILE_']").each(function(i, item) {
                                            var fileId = $(item).attr("id");
                                            var file = uploader.getFile(fileId);
                                            if (file.Status == 'queued' || file.Status == 'error' || file.Status == 'interrupt') {
                                                uploader.upload(fileId);
                                                file.Status = WebUploader.File.Status.PROGRESS;
                                            } else {
                                                layer.msg("文件" + file.name + "正在上传或已上传或类型错误", {icon: 0, time: 2000});
                                                return false;
                                            }
                                        });
                                    } else {
                                        layer.closeAll();
                                        layer.msg("活动保存成功！", {icon: 1, time: 2000}, function() {
                                            $("#sprice_addact_back").click();
                                        });
                                    }
                                } else {
                                    layer.closeAll();
                                    layer.alert(data.msg, {icon: 2});
                                    $("#sprice_create_btn").removeAttr("disabled");
                                    $("#sprice_create_btn").css("background-color", "#00b7ee");
                                }
                            },
                            function(xhr, status) {console.error("error--->" + status);}
                        );
                    });
                }
            }
        });
        $("#sprice_goods_category").on('click', 'li', function(e) {
            $(this).addClass("relevance_cur").siblings().removeClass("relevance_cur");
        });
        $("#sprice_relate_search_btn").click(function(e) {
            //gain_select_goods_list_data(1);
            $("#sprice_relate_goods_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
        });
        $("#sprice_relate_pagesize").change(function(e) {
            gain_select_goods_list_data(1);
        });
        $("#sprice_relate_checkbox_all").click(function(e) {
            var mark = this.checked;
            $(".sprice_relate_checkbox_child").each(function(n, node) {
                node.checked = mark;
            });
        });
        $("#sprice_relate_checkbox_invert").click(function(e) {
            var mark = this.checked;
            $(".sprice_relate_checkbox_child").each(function(n, node) {
                if (node.checked) {
                    node.checked = false;
                } else {
                    node.checked = true;
                }
            });
        });
        $(".zone_relevance .r_add").click(function(e) {
            var pros = [], stock, sign = false;
            $(".sprice_relate_checkbox_child").each(function(n, node) {
                if (node.checked) {
                    stock = $(node).data("stock");
                    if (parseInt(stock) > 0) {
                        pros.push({
                            sku: $(node).data("sku"),
                            whid: $(node).data("whid")
                        });
                    } else {
                        sign = true;
                        pros = [];
                        return false;
                    }
                }
            });

            if (sign) {
                layer.msg("选中项中不允许含有库存为0的商品", {icon: 0, time: 2000});
            } else {
                if (pros.length > 0) {

                    $("#sprice_relate_goods").val(JSON.stringify(spriceRelatedGoodsArrays));

                    var list = $("#sprice_relate_goods").val(), goods = [];
                    list = list == '' ? [] : JSON.parse(list);
                    $.each(pros, function(n, node) {
                        $.each(list, function(i, item) {
                            if (node.sku == item.csku && node.whid == item.warehouseId) {
                                goods.push(item);
                                return false;
                            }
                        });
                    });
                    var chose = $("#sprice_selected_goods").val(), flag;
                    chose = chose == '' ? [] : JSON.parse(chose);
                    $.each(goods, function(n, node) {
                        flag = false;
                        $.each(chose, function(i, item) {
                            if (node.csku == item.csku && node.warehouseId == item.warehouseId) {
                                flag = true;
                                return false;
                            }
                        });
                        if (!flag) {
                            chose.push(node);
                        }
                    });
                    insert_selected_goods_list_data(chose);
                    layer.msg("选中项添加完成", {icon: 1, time: 2000});
                } else {
                    layer.msg("请选择要添加的项", {icon: 0, time: 2000});
                }
            }
        });
        $("#sprice_selected_checkbox_all").click(function(e) {
            var mark = this.checked;
            $(".sprice_selected_checkbox_child").each(function(n, node) {
                node.checked = mark;
            });
        });
        $("#sprice_selected_checkbox_invert").click(function(e) {
            //var mark = this.checked;
            $(".sprice_selected_checkbox_child").each(function(n, node) {
                if (node.checked) {
                    node.checked = false;
                } else {
                    node.checked = true;
                }
            });
        });
        $("#sprice_predis_number").blur(function(e) {
            var batch_predis_number = $(this).val().trim(), $tr;
            var batch_predis_unit = $("#sprice_predis_unit").val();
            if (batch_predis_number != '') {
                if (batch_predis_unit == '元' && !/^((\d{1})|([1-9]{1}\d+)|(\d+\.\d{2}))$/.test(batch_predis_number)) {
                    layer.msg("批量设置处含有输入格式错误的优惠金额！", {icon: 0, time: 3000});
                    return;
                }
                if (batch_predis_unit == '%' && !/^((\d{1})|([1-9]{1}\d{1})|(100))$/.test(batch_predis_number)) {
                    layer.msg("批量设置处含有输入格式错误的折扣数！", {icon: 0, time: 3000});
                    return;
                }
                $(".sprice_selected_checkbox_child").each(function(n, node) {
                    $tr = $(node).parent().parent();
                    $tr.find(".sprice_predis_number").val(batch_predis_number);
                    $tr.find(".sprice_predis_unit").find("option[value='" + batch_predis_unit + "']")
                        .attr("selected", true).siblings().removeAttr("selected");
                    $tr.find(".sprice_predis_number").blur();
                });
            }
        });
        $("#sprice_predis_unit").change(function(e) {
            var batch_predis_number = $("#sprice_predis_number").val().trim();
            var batch_predis_unit = $(this).val(), $tr;
            $(".sprice_selected_checkbox_child").each(function(n, node) {
                $tr = $(node).parent().parent();
                if (batch_predis_number != '') {
                    $tr.find(".sprice_predis_number").val(batch_predis_number);
                    $tr.find(".sprice_predis_unit").find("option[value='" + batch_predis_unit + "']")
                        .attr("selected", true).siblings().removeAttr("selected");
                    $tr.find(".sprice_predis_number").blur();
                }
            });
        });
        $("#sprice_batch_nolimit").change(function(e) {
            var batch_nolimit = this.checked, $tr;
            /*if (batch_nolimit) {
                $("#sprice_batch_limit")[0].checked = false;
            }*/
            $(".sprice_selected_checkbox_child").each(function(n, node) {
                $tr = $(node).parent().parent();
                $tr.find(".sprice_single_nolimit")[0].checked = batch_nolimit;
                /*if (batch_nolimit) {
                    $tr.find(".sprice_single_limit")[0].checked = false;
                    $tr.find(".sprice_limit_number").val("");
                }*/
            });
        });
        /*$("#sprice_batch_limit").change(function(e) {
            var batch_limit = this.checked, $tr;
            var batch_limit_number = $(".sprice_limit_number").val().trim();
            if (batch_limit) {
                $("#sprice_batch_nolimit")[0].checked = false;
            }
            $(".sprice_selected_checkbox_child").each(function(n, node) {
                $tr = $(node).parent().parent();
                $tr.find(".sprice_single_limit")[0].checked = batch_limit;
                if (batch_limit) {
                    $tr.find(".sprice_limit_number").val(batch_limit_number);
                    $tr.find(".sprice_single_nolimit")[0].checked = false;
                }
            });
        });*/
        /*$("#sprice_limit_number").blur(function(e) {
            var batch_limit = $("#sprice_batch_limit")[0].checked, $tr;
            var batch_limit_number = $(this).val().trim();
            if (batch_limit_number != '' && !/^(([1-9]{1})|([1-9]{1}\d+))$/.test(batch_limit_number)) {
                layer.msg("批量设置处含有输入格式错误的限购数量！", {icon: 0, time: 3000});
                return;
            }
            if (batch_limit && batch_limit_number != '') {
                $(".sprice_selected_checkbox_child").each(function(n, node) {
                    $tr = $(node).parent().parent();
                    $tr.find(".sprice_limit_number").val(batch_limit_number);
                    $tr.find(".sprice_single_nolimit")[0].checked = false;
                    $tr.find(".sprice_single_limit")[0].checked = true;
                });
            }
        });*/
        $("#sprice_selected_table").on('click', '.r-del', function(e) {
            var id = $(this).data("id");
            var $tr = $(this).parent().parent();
            var $child = $tr.find(".sprice_selected_checkbox_child");
            var chose = $("#sprice_selected_goods").val();
            chose = chose == '' ? [] : JSON.parse(chose);
            $.each(chose, function(i, item) {
                if ($child.data('sku') == item.csku && $child.data('whid') == item.warehouseId) {
                    chose.splice(i, 1);
                    return false;
                }
            });
            console.log("chose----->" + chose.length);
            if (id != "") {
                ajax_get("../product/mktool/delpro?id=" + id + "&t=" + Math.random(), "", "",
                    function (data) {
                        if (data.suc) {
                            layer.msg("删除成功！", {icon: 1, time: 3000});
                            $("#sprice_selected_goods").val(JSON.stringify(chose));
                            $("#sprice_selected_goods_number").text(chose.length);
                            $tr.remove();
                        } else {
                            layer.alert(data.msg, {icon: 2});
                        }
                    },
                    function (xhr, status) {console.log("error--->" + status);}
                );
            } else {
                layer.msg("删除成功！", {icon: 1, time: 3000});
                $("#sprice_selected_goods").val(JSON.stringify(chose));
                $("#sprice_selected_goods_number").text(chose.length);
                $tr.remove();
            }
        });
        $("#sprice_selected_table").on('blur', '.sprice_predis_number', function(e) {
            var single_predis_number = $(this).val().trim();
            var single_predis_unit = $(this).next().val();
            var dis_price = $(this).parent().prev().prev().text();
            var $tr = $(this).parent().parent();
            if (single_predis_number != '') {
                if (single_predis_unit == '元' && !/^((\d{1})|([1-9]{1}\d+)|(\d+\.\d{2}))$/.test(single_predis_number)) {
                    layer.msg("选中项中含有输入格式错误的优惠金额！", {icon: 0, time: 3000});
                    return;
                } else if (single_predis_unit == '%' && !/^((\d{1})|([1-9]{1}\d{1})|(100))$/.test(single_predis_number)) {
                    layer.msg("选中项中含有输入格式错误的折扣数！", {icon: 0, time: 3000});
                    return;
                } else {
                    var profit_rate, special_price, total_cost = $tr.find(".sprice_selected_total_cost").text();
                    if (single_predis_unit == '%') {
                        special_price = parseFloat(dis_price) * parseInt(single_predis_number) / 100;
                    } else {
                        special_price = (parseFloat(dis_price) - parseFloat(single_predis_number));
                    }
                    //对于深圳仓的产品采购价需要减去物流费
                    /*var whid = $tr.find(".sprice_selected_checkbox_child").data("whid");
                    var disFreight = $tr.find(".sprice_selected_checkbox_child").data("df");
                    disFreight = (disFreight == null || disFreight == undefined || disFreight == 'null' || disFreight == '') ? 0 : disFreight;
                    if(whid == 2024 || whid == '2024'){
                        special_price = special_price - parseFloat(disFreight);
                    }*/
                    profit_rate = 1 - parseFloat(total_cost) / parseFloat(special_price);
                    $tr.find(".sprice_predis_price").text(special_price.toFixed(2));
                    $tr.find(".sprice_predis_profit_rate").text(profit_rate.toFixed(2));
                }
            } else {
                $tr.find(".sprice_predis_price").text("");
                $tr.find(".sprice_predis_profit_rate").text("");
            }
        });
        $(".zone_relevance .sure_link").click(function(e) {
            var actId = $("#sprice_selected_actid").val(), curr_node = this;
            var goods = [], $tr, single_predis_number, single_predis_unit, single_nolimit,
                single_limit, single_limit_number, limitedBuy, limitedNum, flag = true,
                single_disprice, single_predis_price, single_predis_profit_rate;
            $(".sprice_selected_checkbox_child").each(function(n, node) {
                if (node.checked) {
                    $tr = $(node).parent().parent();
                    single_predis_number = $tr.find(".sprice_predis_number").val().trim();
                    single_predis_unit = $tr.find(".sprice_predis_unit").val();
                    //single_nolimit = $tr.find(".sprice_single_nolimit")[0].checked;
                    //single_limit = $tr.find(".sprice_single_limit")[0].checked;
                    //single_limit_number = $tr.find(".sprice_limit_number").val().trim();
                    single_disprice = $tr.find(".sprice_selected_disprice").text();
                    single_predis_price = $tr.find(".sprice_predis_price").text();
                    single_predis_profit_rate = $tr.find(".sprice_predis_profit_rate").text();
                    if (single_predis_unit == '元' && !/^((\d{1})|([1-9]{1}\d+)|(\d+\.\d{2}))$/.test(single_predis_number)) {
                        layer.msg("选中项中含有输入格式错误的优惠金额！", {icon: 0, time: 3000});
                        flag = false;
                        return false;
                    }
                    /*if (single_predis_unit == '元' && /^((\d{1})|([1-9]{1}\d+)|(\d+\.\d{2}))$/.test(single_predis_number)
                        && parseFloat(single_disprice) < parseFloat(single_predis_number)) {
                        layer.msg("选中项中含有大于分销价的优惠金额！", {icon: 0, time: 3000});
                        flag = false;
                        return false;
                    }*/
                    if (single_predis_unit == '%' && !/^((\d{1})|([1-9]{1}\d{1})|(100))$/.test(single_predis_number)) {
                        layer.msg("选中项中含有输入格式错误的折扣数！", {icon: 0, time: 3000});
                        flag = false;
                        return false;
                    }
                    if (!/^((\-\d+\.\d{2})|(\d+\.\d{2}))$/.test(single_predis_price)) {
                        layer.msg("选中项中含有未设置或设置格式错误的的折后价！", {icon: 0, time: 3000});
                        flag = false;
                        return false;
                    }
                    if (!/^((\-\d+\.\d{2})|(\d+\.\d{2}))$/.test(single_predis_profit_rate)) {
                        layer.msg("选中项中含有未设置或设置格式错误的折后利润率！", {icon: 0, time: 3000});
                        flag = false;
                        return false;
                    }
                    /*if (single_limit && /^(([1-9]{1})|([1-9]{1}\d+))$/.test(single_limit_number)) {
                        limitedBuy = true;
                        limitedNum = single_limit_number;
                    } else if (single_limit && !/^(([1-9]{1})|([1-9]{1}\d+))$/.test(single_limit_number)) {
                        layer.msg("选中项中含有已设置限购的但输入格式错误的限购数量！", {icon: 0, time: 3000});
                        flag = false;
                        return false;
                    } else if ((!single_limit && !single_nolimit) || (single_limit && single_nolimit)) {
                        layer.msg("选中项中含有未设置的锁定库存！", {icon: 0, time: 3000});
                        flag = false;
                        return false;
                    } else if (single_nolimit && /^(([1-9]{1})|([1-9]{1}\d+))$/.test(single_limit_number)) {
                        layer.msg("选中项中含有未设置的锁定库存！", {icon: 0, time: 3000});
                        flag = false;
                        return false;
                    } else if (single_nolimit && !single_limit && single_limit_number == '') {
                        limitedBuy = false;
                        limitedNum = 0;
                    }*/
                    limitedBuy = false;
                    limitedNum = 0;
                    goods.push({
                        activityId: actId,
                        sku: $(node).data("sku"),
                        warehouseId: $(node).data("whid"),
                        limitedPurchase: limitedBuy,
                        limitedPnum: limitedNum,
                        specialPrice: single_predis_price,
                        predisNumber: single_predis_number,
                        predisUnit: single_predis_unit,
                        predisProfitRate: single_predis_profit_rate
                    });
                }
            });
            if (flag) {
                if (goods.length > 0) {
                    layer.confirm("您确认关联所选商品？", {icon: 3, title: '提示'}, function(index) {
                        layer.closeAll();
                        $(curr_node).attr("disabled", true);
                        $(curr_node).css("background-color", "grey");
                        layer.load(1, {shade: 0.5});
                        isaulogin(function(email) {
                            var params = {
                                actId: actId,
                                user: email,
                                list: goods
                            };
                            ajax_post("../product/mktool/saveactgoods", JSON.stringify(params), 'application/json',
                                function(data) {
                                    layer.closeAll();
                                    if (data.suc) {
                                        layer.alert(data.msg, {icon: 1}, function() {
                                            layer.closeAll();
                                            $("#sprice_relate_back").click();
                                        });
                                    } else {
                                        layer.alert(data.msg, {icon: 2});
                                        $(curr_node).removeAttr("disabled");
                                        $(curr_node).css("background-color", "#141c25");
                                    }
                                },
                                function(xhr, status) {console.error("error--->" + status);}
                            );
                        });
                    });
                } else {
                    layer.msg("请选择要关联的项", {icon: 0, time: 3000});
                }
            }
        });
    }

    function init_webuploader_register() {
        WebUploader.Uploader.register(
            {
                "before-send-file": "beforeSendFile"
            },
            {
                "beforeSendFile": function(file) {
                    var task = WebUploader.Base.Deferred();
                    (new WebUploader.Uploader()).md5File(file, 0, file.size)
                    //MD5值计算实时进度
                    .progress(function(percentage) {
                        console.log("beforeSendFile    [MD5 percentage]----->" + percentage);
                    })
                    //如果读取出错了，则通过reject告诉webuploader文件上传出错。
                    .fail(function() {
                        task.reject();
                        console.log("beforeSendFile    [file MD5 value calculate error]");
                    })
                    //MD5值计算完成
                    .then(function(md5) {
                        console.log("beforeSendFile    " + file.id + "_md5----->" + md5);
                        uploader.options.formData[file.id + "_md5"] = md5;
                        task.resolve();
                        console.log("beforeSendFile    [next step]");
                    });
                    return WebUploader.Base.when(task);
                }
            }
        );
    }

    function init_uploader() {
        $("#sprice_file_list").children().eq(0).empty();
        uploader = WebUploader.create({
            pick : {
                id : '#sprice_select_btn',
                innerHTML : '选择文件',
                multipart : false
            },
            accept : {
                title : '图片文件',
                extensions: 'gif,jpg,jpeg,bmp,png',
                mimeTypes: 'image/*'
            },
            thumb : {
                width: 50,
                height: 50,
                allowMagnify: false,
                crop: true
            },
            compress: false,
            auto : false,
            threads : 1,
            runtimeOrder : 'html5, flash',
            swf : "../js/webuploader/Uploader.swf",
            server : "../product/mktool/actposter",
            method : 'POST',
            fileVal : 'poster',
            formData : {}
        });
        $(".webuploader-pick").css({
            'padding': '8px 10px',
            'height': '18px',
            'width': '60px'
        });
        uploader.on('fileQueued', function(file) {
            file.Status = WebUploader.File.Status.QUEUED;
            var srcValue = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iMjQyIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDI0MiAyMDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzEwMCV4MjAwCkNyZWF0ZWQgd2l0aCBIb2xkZXIuanMgMi42LjAuCkxlYXJuIG1vcmUgYXQgaHR0cDovL2hvbGRlcmpzLmNvbQooYykgMjAxMi0yMDE1IEl2YW4gTWFsb3BpbnNreSAtIGh0dHA6Ly9pbXNreS5jbwotLT48ZGVmcz48c3R5bGUgdHlwZT0idGV4dC9jc3MiPjwhW0NEQVRBWyNob2xkZXJfMTUwZDY2MGUxYzggdGV4dCB7IGZpbGw6I0FBQUFBQTtmb250LXdlaWdodDpib2xkO2ZvbnQtZmFtaWx5OkFyaWFsLCBIZWx2ZXRpY2EsIE9wZW4gU2Fucywgc2Fucy1zZXJpZiwgbW9ub3NwYWNlO2ZvbnQtc2l6ZToxMnB0IH0gXV0+PC9zdHlsZT48L2RlZnM+PGcgaWQ9ImhvbGRlcl8xNTBkNjYwZTFjOCI+PHJlY3Qgd2lkdGg9IjI0MiIgaGVpZ2h0PSIyMDAiIGZpbGw9IiNFRUVFRUUiLz48Zz48dGV4dCB4PSI4OS44NTAwMDAzODE0Njk3MyIgeT0iMTA1LjciPjI0MngyMDA8L3RleHQ+PC9nPjwvZz48L3N2Zz4=';
            uploader.makeThumb( file, function( error, ret ) {
                if ( !error ) {
                    srcValue = ret;
                }
                $("#sprice_file_list").children().eq(0).append(
                    '<tr id="' + file.id + '">' +
                    '<td style="width: 20%;"><img src="' + srcValue + '" style="width: 50px; height: 50px;"></td>' +
                    /*'<td style="width: 10%;">' + file.name + '</td>' +
                    '<td style="width: 20%;">' +
                    '<p>' + WebUploader.Base.formatSize( file.size, 0, ['B', 'KB', 'MB'] ) + '</p>' +
                    '<div class="progress" style="border: 1px solid darkgrey;">' +
                    '<div class="progress-bar progress-bar-info" style="width: 0%;" aria-valuenow="0" aria-valuemax="100" aria-valuemin="0" role="progressbar"></div>' +
                    '</div>' +
                    '</td>' +*/
                    '<td style="width: 20%;">' +
                    '<input type="button" value="移除" data-id="' + file.id + '" class="sprice_file_remove_btn" style="width: 80px; height: 30px; border-radius: 5px; background-color: #00b7ee; cursor: pointer;">' +
                    '</td>' +
                    '<td style="width: 20%;"></td>' +
                    '</tr>'
                );
            });
        });
        uploader.on('uploadProgress', function(file, percentage) {
            /*$('#' + file.id).find('.progress .progress-bar')
                .css('width', percentage * 100 + '%')
                .attr('aria-valuenow', percentage * 100);*/
        });
        uploader.on('uploadAccept', function(block, response) {
            //var file = block.file;
            if (response.suc) {
                // 通过return true来告诉组件，此文件上传成功。
                return true;
            } else {
                // 通过return false来告诉组件，此文件上传有错。
                return false;
            }
        });
        uploader.on('uploadError', function(file) {
            /*$('#' + file.id).find('.progress .progress-bar')
                .css('width', 0 + '%').attr('aria-valuenow', 0);*/
            file.Status = WebUploader.File.Status.ERROR;
            var result = '<span style="color: red;">图片上传失败！</span>';
            $('#' + file.id).children().last().html(result);
        });
        uploader.on('uploadSuccess', function(file, response) {
            /*$('#' + file.id).find('.progress .progress-bar')
             .css('width', 100 + '%').attr('aria-valuenow', 100);*/
            file.Status = WebUploader.File.Status.COMPLETE;
            var result = '<span style="color: green;">图片上传成功！</span>';
            $('#' + file.id).children().last().html(result);
            $('#' + file.id + ' input').hide();
        });
        uploader.on('uploadFinished', function() {
            layer.closeAll();
            var mark = 0, fileId, file;
            $("tr[id^='WU_FILE_']").each(function(i, item) {
                fileId = $(item).attr("id");
                file = uploader.getFile(fileId);
                mark += file.Status == WebUploader.File.Status.ERROR ? 1 : 0;
            });
            if (mark > 0) {
                layer.confirm('活动信息保存成功, 但有海报图片上传失败，忽略并返回活动列表请点击“确定”，重新上传请点击“取消”后再“创建”。', {icon: 3, title: '提示'},
                    function(index) {
                        layer.closeAll();
                        $("#sprice_addact_back").click();
                    },
                    function() {
                        layer.closeAll();
                        $("#sprice_create_btn").removeAttr("disabled");
                        $("#sprice_create_btn").css("background-color", "#00b7ee");
                    }
                );
            } else {
                layer.msg('活动信息保存成功且所有海报图片已上传成功。', {icon: 1, time: 3000}, function() {
                    $("#sprice_addact_back").click();
                });
            }
        });
        $("#sprice_file_list").on('click', '.sprice_file_remove_btn', function(e) {
            console.log("id:" + $(this).data("id"));
            var fileId = $(this).data("id");
            layer.confirm("确认移除当前已选中的海报图片？", {icon: 3}, function() {
                layer.closeAll();
                var file = uploader.getFile(fileId);
                uploader.removeFile(fileId, true);
                file.Status = WebUploader.File.Status.CANCELLED;
                $('#' + fileId).remove();
            });
        });
        $("#sprice_file_list").on('click', '.sprice_file_delete_btn', function(e) {
            var id = $(this).data("id");
            layer.confirm("确认删除当前已存在的海报图片？", {icon: 3}, function() {
                ajax_get('../product/mktool/delposter?id=' + id + "&t=" + Math.random(), null, null,
                    function(data) {
                        if (data.suc) {
                            layer.closeAll();
                            $('#uploaded_' + id).remove();
                        } else {
                            layer.msg(data.msg, {icon: 2, time: 2000});
                        }
                    },
                    function(xhr, state) {console.error("error----->" + state);}
                );
            });
        });
    }

    function create_activity() {
        var activityName = $("#sprice_addact_form input[name='activityName']").val().trim();
        if (activityName == '' || activityName.length > 10) {
            layer.msg("“活动名称”不能为空且要求10字以内！", {icon: 0, time: 2000});
            return null;
        }
        var activityRemark = $("#sprice_addact_form textarea[name='activityRemark']").val().trim();
        if (activityRemark != "" && activityRemark.length > 100) {
            layer.msg("“活动备注”要求100字以内！", {icon: 0, time: 2000});
            return null;
        }
        var time_regexp = /^([1-2]{1}\d{3})\-(([0]{1}[1-9]{1})|([1]{1}[0-2]{1}))\-(([0]{1}[1-9]{1})|([1-2]{1}\d{1})|([3]{1}[0-1]{1}))$/;
        var startTime = $("#sprice_addact_form input[name='startTime']").val().trim();
        if (!time_regexp.test(startTime)) {
            layer.msg("请输入正确格式的“开始时间”！", {icon: 0, time: 2000});
            return null;
        }
        var endTime = $("#sprice_addact_form input[name='endTime']").val().trim();
        if (!time_regexp.test(endTime)) {
            layer.msg("请输入正确格式的“结束时间”！", {icon: 0, time: 2000});
            return null;
        }
        var activitySort = $("#sprice_addact_form input[name='activitySort']").val().trim();
        if (activitySort != '' && !/^([1-9]{1})|([1-9]{1}\d+)$/.test(activitySort)) {
            layer.msg("请输入正确格式的“排序”序号！", {icon: 0, time: 2000});
            return null;
        }
        var now_ms = new Date().getTime();
        var start_ms = new Date(parseInt(startTime.substr(0, 4)),
            parseInt(startTime.substr(5, 2)) - 1, parseInt(startTime.substr(8, 2))).getTime();
        var end_ms = new Date(parseInt(endTime.substr(0, 4)),
            parseInt(endTime.substr(5, 2)) - 1, parseInt(endTime.substr(8, 2))).getTime();
        if (end_ms <= start_ms) {
            layer.msg("“结束时间”必须大于“开始时间”！", {icon: 0, time: 2000});
            return null;
        }
        if (end_ms <= now_ms) {
            layer.msg("“结束时间”必须大于当前时间！", {icon: 0, time: 2000});
            return null;
        }
        return {
            activityName: activityName,
            showTotitle: $("#sprice_addact_form input[name='showTotitle']")[0].checked,
            activityRemark: activityRemark,
            startTime: startTime + ' 00:00:00',
            endTime: endTime + ' 00:00:00',
            activitySort: activitySort
        };
    }

    function init_relate_goods_area() {
        ajax_get("../product/api/realCateQuery?level=1&t=" + Math.random(), "", "",
            function(data) {
                if (data.length > 0) {
                    var optionHTML = '分类：<li class="relevance_cur" value="">全部</li>';
                    $.each(data, function (i, item) {
                        optionHTML += '<li value="' + item.iid + '">' + item.cname + '</li>';
                    });
                    $("#sprice_goods_category").html(optionHTML);
                } else {
                    layer.msg("获取商品类目失败", {icon: 2, time: 2000});
                }
            },
            function(xhr, status) { console.log("error--->" + status); }
        );
        ajax_get("../inventory/queryWarehouse?" + Math.random(), "", "",
            function(data) {
                if (data.length > 0) {
                    var optionHTML = '<option value="">所有仓库</option>';
                    $.each(data, function(i, item) {
                        optionHTML += '<option value="' + item.id + '">' + item.warehouseName + '</option>';
                    });
                    $("#sprice_goods_warehouse").html(optionHTML);
                } else {
                    layer.msg("获取仓库信息失败", {icon: 2, time: 2000});
                }
            },
            function(xhr, status) { console.log("error--->" + status); }
        );
        //gain_select_goods_list_data(1);

        var grid = new BbcGrid();
        grid.initTable($("#sprice_relate_goods_table"), getSetting_sprice_related_goods_area());

        var id = $("#sprice_selected_actid").val();
        ajax_get("../product/mktool/getactinfo?id=" + id + "&t=" + Math.random(), "", "",
            function(data) {
                if (data.suc) {
                    //var act = data.info.act;
                    //var imgs = data.info.imgs;
                    var pros = data.info.pros;
                    insert_selected_goods_list_data(pros);
                } else {
                    layer.alert(data.msg, {icon: 2});
                }
            },
            function(xhr, status) { console.log("error--->" + status); }
        );
    }

    var spriceRelatedGoodsArrays = new Array();
    function getSetting_sprice_related_goods_area() {
        var setting = {
            url:"../product/api/getProducts",
            ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
            rownumbers : true, // 是否显示前面的行号
            datatype : "json", // 返回的数据类型
            mtype : "post", // 提交方式
            height : "auto", // 表格宽度
            autowidth : true, // 是否自动调整宽度
            // styleUI: 'Bootstrap',
            colNames:["id","勾选","商品名称","商品编号","成本价(元)","分销价(元)","市场零售价(元)","分销利润率","云仓库存", "所属仓库"],
            colModel:[{name:"id",index:"id",width:"12%",align:"center",sortable:false,hidden:true},
                {name:"id",index:"sales_order_no",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                    spriceRelatedGoodsArrays.push(rowObject);
                    return '<input type="checkbox" class="sprice_relate_checkbox_child" data-sku="' + rowObject.csku + '" data-whid="' + rowObject.warehouseId + '" data-stock="' + rowObject.stock + '" data-stock="' + rowObject.id + '" data-id="' + options.rowId + '">';
                }},
                {name:"ctitle",index:"sales_order_no",width:"32%",align:"center",sortable:false, formatter:function(cellvalue,options,rowObject){
                    return '<span class=\"relevance_product_link\">' + '<img src="' + rowObject.imageUrl + '">' +
                        '<a href="javascript:;">' + deal_with_illegal_value(rowObject.ctitle) + '</a>' + '</span>';
                }},
                {name:"csku",index:"opt_fee",width:"12%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                        return deal_with_illegal_value(cellvalue);
                }},
                {name:"disTotalCost",index:"ordering_date",width:"14%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"disPrice",index:"stock",width:"12%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return cellvalue.toFixed(2);
                }},
                {name:"localPrice",index:"warehouse_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"disProfitRate",index:"nick_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"stock",index:"nick_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"warehouseName",index:"",width:"12%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return deal_with_illegal_value(cellvalue);
                }}
            ],
            viewrecords : true,
            rowNum : 10,
            rowList : [ 10, 20, 30 ],
            pager:"#sprice_relate_goods_pagination",//分页
            caption:"关联商品",//表名称
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
                records: "data.rows",//数据总记录数
                repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
                //cell: "cell",//root 中row 行
                id: "id"//唯一标识
            },
            serializeGridData : function() {
                return JSON.stringify(getSearchParam_sprice_related_goods_area($(this).jqGrid('getGridParam', 'postData')))
            }
        };
        return setting;
    }

    function getSearchParam_sprice_related_goods_area() {
        var params = {
            data: {
                currPage: $('#sprice_relate_goods_table').getGridParam('page'),
                pageSize: $('#sprice_relate_goods_table').getGridParam('rowNum'),
                categoryId: $("#sprice_goods_category .relevance_cur").attr("value"),
                warehouseId: $("#sprice_goods_warehouse").val(),
                title: $("#sprice_relate_search_title").val().trim(),
                minPrice: $("#sprice_relate_disprice_left").val().trim(),
                maxPrice: $("#sprice_relate_disprice_right").val().trim(),
                istatus: "1"
            }
        };
        return params;
    }

    function gain_select_goods_list_data(curr) {
        var params = {
            data: {
                currPage: curr,
                pageSize: $("#sprice_relate_pagesize").val(),
                categoryId: $("#sprice_goods_category .relevance_cur").attr("value"),
                warehouseId: $("#sprice_goods_warehouse").val(),
                title: $("#sprice_relate_search_title").val().trim(),
                minPrice: $("#sprice_relate_disprice_left").val().trim(),
                maxPrice: $("#sprice_relate_disprice_right").val().trim(),
                istatus: "1"
            }
        };
        ajax_post("../product/api/getProducts", JSON.stringify(params), "application/json",
            function(data) {
                insert_select_goods_list_data(data);
                init_select_goods_list_pagination(data.data);
            },
            function(xhr, status) { console.log("error--->" + status); }
        );
    }

    function init_select_goods_list_pagination(page) {
        if ($("#sprice_relate_pagination")[0] != undefined) {
            $("#sprice_relate_pagination").empty();
            laypage({
                cont: 'sprice_relate_pagination',
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
                    if (!first) {
                        gain_select_goods_list_data(obj.curr);
                    }
                }
            });
        }
    }

    function insert_select_goods_list_data(data) {
        var itemHTML = '';
        if (!deal_with_illegal_boolean(data.data.result)) {
            itemHTML = '<tr><td colspan="9" style="color: green;">当前筛选/查询条件下数据列表为空</td></tr>';
        } else {
            $.each(data.data.result, function(i, item) {
                //对于深圳仓的产品采购价需要减去物流费
                var disPrice = deal_with_illegal_json(item.disPrice) == "" ? parseFloat(0) : parseFloat(item.disPrice);
                var whid = item.warehouseId;
                var disFreight = item.disFreight;
                disFreight = (disFreight == null || disFreight == undefined || disFreight == 'null' || disFreight == '') ? 0 : disFreight;
                if(whid == 2024 || whid == '2024'){
                    disPrice = disPrice - parseFloat(disFreight);
                }
                itemHTML +=
                    '<tr>' +
                    '<td><input type="checkbox" class="sprice_relate_checkbox_child" data-sku="' + item.csku + '" data-whid="' + item.warehouseId + '"></td>' +
                    '<td class="relevance_product_link">' +
                    '<img src="' + item.imageUrl + '">' +
                    '<a href="javascript:;">' + deal_with_illegal_value(item.ctitle) + '</a>' +
                    '</td>' +
                    '<td>' + deal_with_illegal_value(item.csku) + '</td>' +
                    '<td>' + deal_with_illegal_value(item.disTotalCost) + '</td>' +
                    '<td>' + disPrice.toFixed(2) + '</td>' +
                    '<td>' + deal_with_illegal_value(item.localPrice) + '</td>' +
                    '<td>' + deal_with_illegal_value(item.disProfitRate) + '</td>' +
                    '<td class="sprice_relate_stock">' + deal_with_illegal_value(item.stock) + '</td>' +
                    '<td>' + deal_with_illegal_value(item.warehouseName) + '</td>' +
                    '</tr>';
            });
            $("#sprice_relate_goods").val(JSON.stringify(data.data.result));
        }
        $("#sprice_relate_table").html(itemHTML);
        var rows = deal_with_illegal_value(data.data.rows);
        $("#sprice_relate_totalcount").text(rows == "---" ? "0" : rows);
        var pages = deal_with_illegal_value(data.data.totalPage);
        $("#sprice_relate_totalpage").text(pages == "---" ? "0" : pages);
    }


    function insert_selected_goods_list_data(list) {
        console.log(list);
        $("#sprice_selected_goods").val(JSON.stringify(list));
        $("#sprice_selected_goods_number").text(list.length);
        var itemHTML = '';
        $.each(list, function(i, item) {
            //对于深圳仓的产品采购价需要减去物流费
            var disPrice = deal_with_illegal_json(item.disPrice) == "" ? parseFloat(0) : parseFloat(item.disPrice);
            var whid = item.warehouseId;
            var disFreight = item.disFreight;
            disFreight = (disFreight == null || disFreight == undefined || disFreight == 'null' || disFreight == '') ? 0 : disFreight;
            if(whid == 2024 || whid == '2024'){
                disPrice = disPrice - parseFloat(disFreight);
            }
            itemHTML +=
                '<tr>' +
                    '<td><input type="checkbox" class="sprice_selected_checkbox_child" data-sku="' + item.csku + '" data-whid="' + item.warehouseId + '" data-df="' + item.disFreight + '"></td>' +
                    '<td class="relevance_product_link">' +
                    '<a href="javascript:;"><img src="' + item.imageUrl + '"></a>' +
                    '<div>' +
                    '<a href="javascript:;">' + deal_with_illegal_value(item.ctitle) + '</a>' +
                    '<span>商品编号：<span class="selected_sku">' + deal_with_illegal_value(item.csku) + '</span></span>' +
                    '</div>' +
                    '</td>' +
                    '<td class="sprice_selected_total_cost">' + deal_with_illegal_value(item.disTotalCost) + '</td>' +
                    '<td class="sprice_selected_disprice">' + disPrice.toFixed(2) + '</td>' +
                    '<td>' + deal_with_illegal_value(item.disProfitRate) + '</td>' +
                    '<td class="discount-t">' +
                    '<input value="' + deal_with_illegal_json(item.predisNumber) + '" class="searchInput sprice_predis_number">' +
                    '<select class="searchInput sprice_predis_unit">' +
                    '<option value="元" ' + (item.predisUnit == '元' ? 'selected="selected"' : '') + '>元（优惠金额）</option>' +
                    '<option value="%" ' + (item.predisUnit == '%' ? 'selected="selected"' : '') + '>%(请输入0~100的数字,例如95即95折)</option>' +
                    '</select>' +
                    '</td>' +
                    '<td class="sprice_predis_price">' + (isNaN(item.specialPrice) ? '' : parseFloat(deal_with_illegal_json(item.specialPrice)).toFixed(2)) + '</td>' +
                    '<td class="sprice_predis_profit_rate">' + (isNaN(item.predisProfitRate) ? '' : parseFloat(deal_with_illegal_json(item.predisProfitRate)).toFixed(2)) + '</td>' +
                    '<td>' + deal_with_illegal_value(item.warehouseName) + '</td>' +
                    '<td>' + deal_with_illegal_value(item.stock) + '</td>' +
                    '<td>' +
                    '<label><input type="checkbox" class="sprice_single_nolimit" checked="checked">不限制</label>' +
                    //'<label><input type="checkbox" class="sprice_single_nolimit" ' + (item.limitedPurchase != undefined && !item.limitedPurchase ? 'checked="checked"' : '') + '>不限制</label>' +
                    /*'<span>' +
                    '<label><input type="checkbox" class="sprice_single_limit" ' + (item.limitedPurchase ? 'checked="checked"' : '') + '>限购</label>' +
                    '<input value="' + deal_with_illegal_json(item.limitedPnum) + '" class="searchInput sprice_limit_number" style="width: 100px;"/>件' +
                    '</span>' +*/
                    '</td>' +
                    '<td><span class="r-del" data-id="' + deal_with_illegal_json(item.id) + '">删除</span></td>' +
                '</tr>';
        });
        $("#sprice_selected_table").html(itemHTML);
    }

    function init_watch_goods_area(id) {
        ajax_get("../product/mktool/getactinfo?id=" + id + "&t=" + Math.random(), "", "",
            function(data) {
                if (data.suc) {
                    var act = data.info.act;
                    var imgs = data.info.imgs;
                    var pros = data.info.pros;
                    $("#sprice_watch_actname").text(deal_with_illegal_value(act.activityName));
                    $("#sprice_watch_actremark").text(deal_with_illegal_value(act.activityRemark));
                    $("#sprice_watch_actstart").text(deal_with_illegal_value(act.startTime));
                    $("#sprice_watch_actend").text(deal_with_illegal_value(act.endTime));
                    var imgHTML = '';
                    if (imgs.length == 0) {
                        imgHTML = '未设置';
                    } else {
                        $.each(imgs, function(i, item) {
                            imgHTML +=
                                //'<img src="data:image/svg+xml;base64,' + item.base64 + '" class="ac-picture">';
                                '<img src="../product/spact/poster?id=' + item.id + '" class="ac-picture">';
                        });
                    }
                    $("#sprice_watch_actposter").html(imgHTML);
                    var trHTML = '';
                    if (pros.length == 0) {
                        trHTML = '<tr><td colspan="10" style="color: green;">当前活动关联商品数为零</td></tr>';
                    } else {
                        $.each(pros, function(i, item) {
                            trHTML +=
                                '<tr>' +
                                    '<td><input type="checkbox"></td>' +
                                    '<td class="relevance_product_link">' +
                                    '<a href="javascript:;"><img src="' + item.imageUrl + '"></a>' +
                                    '<div>' +
                                    '<a href="javascript:;">' + deal_with_illegal_value(item.ctitle) + '</a>' +
                                    '<span>商品编号：<span>' + deal_with_illegal_value(item.csku) + '</span></span>' +
                                    '</div>' +
                                    '</td>' +
                                    '<td>' + deal_with_illegal_value(item.disTotalCost) + '</td>' +
                                    '<td>' + deal_with_illegal_value(item.disPrice) + '</td>' +
                                    '<td>' + deal_with_illegal_value(item.disProfitRate) + '</td>' +
                                    '<td>' + deal_with_illegal_value(item.specialPrice) + '</td>' +
                                    '<td>' + deal_with_illegal_value(item.predisProfitRate) + '</td>' +
                                    '<td>' + deal_with_illegal_value(item.warehouseName) + '</td>' +
                                    '<td>' + deal_with_illegal_value(item.stock) + '</td>' +
                                    '<td>' + (item.limitedPurchase ? ('限购' + item.limitedPnum + '个') : '不限制') + '</td>' +
                                '</tr>';
                        });
                    }
                    $("#sprice_watch_table").html(trHTML);
                } else {
                    layer.alert(data.msg, {icon: 2});
                }
            },
            function(xhr, status) { console.log("error--->" + status); }
        );
    }

    function init_watch_goods_area_new(id) {
        var grid = new BbcGrid();
        grid.initTable($('#watch_goods_area_table'), getSetting_watch_goods_area());
        $("#watch_goods_area_table").jqGrid("clearGridData");

        ajax_get("../product/mktool/getactinfo?id=" + id + "&t=" + Math.random(), "", "",
            function(data) {
                if (data.suc) {
                    var act = data.info.act;
                    var imgs = data.info.imgs;
                    var pros = data.info.pros;
                    $("#sprice_watch_actname").text(deal_with_illegal_value(act.activityName));
                    $("#sprice_watch_actremark").text(deal_with_illegal_value(act.activityRemark));
                    $("#sprice_watch_actstart").text(deal_with_illegal_value(act.startTime));
                    $("#sprice_watch_actend").text(deal_with_illegal_value(act.endTime));
                    var imgHTML = '';
                    if (imgs.length == 0) {
                        imgHTML = '未设置';
                    } else {
                        $.each(imgs, function(i, item) {
                            imgHTML +=
                                //'<img src="data:image/svg+xml;base64,' + item.base64 + '" class="ac-picture">';
                                '<img src="../product/spact/poster?id=' + item.id + '" class="ac-picture">';
                        });
                    }
                    $("#sprice_watch_actposter").html(imgHTML);
                    var trHTML = '';
                    if (pros.length == 0) {
                        trHTML = '<tr><td colspan="10" style="color: green;">当前活动关联商品数为零</td></tr>';
                    } else {
                        $.each(pros, function(i, item) {
                            //trHTML +=
                            //    '<tr>' +
                            //    '<td><input type="checkbox"></td>' +
                            //    '<td class="relevance_product_link">' +
                            //    '<a href="javascript:;"><img src="' + item.imageUrl + '"></a>' +
                            //    '<div>' +
                            //    '<a href="javascript:;">' + deal_with_illegal_value(item.ctitle) + '</a>' +
                            //    '<span>商品编号：<span>' + deal_with_illegal_value(item.csku) + '</span></span>' +
                            //    '</div>' +
                            //    '</td>' +
                            //    '<td>' + deal_with_illegal_value(item.disTotalCost) + '</td>' +
                            //    '<td>' + deal_with_illegal_value(item.disPrice) + '</td>' +
                            //    '<td>' + deal_with_illegal_value(item.disProfitRate) + '</td>' +
                            //    '<td>' + deal_with_illegal_value(item.specialPrice) + '</td>' +
                            //    '<td>' + deal_with_illegal_value(item.predisProfitRate) + '</td>' +
                            //    '<td>' + deal_with_illegal_value(item.warehouseName) + '</td>' +
                            //    '<td>' + deal_with_illegal_value(item.stock) + '</td>' +
                            //    '<td>' + (item.limitedPurchase ? ('限购' + item.limitedPnum + '个') : '不限制') + '</td>' +
                            //    '</tr>';
                            $("#watch_goods_area_table").jqGrid("addRowData", (i + 1), item, "last");
                        });
                    }
                    //$("#sprice_watch_table").html(trHTML);
                } else {
                    layer.alert(data.msg, {icon: 2});
                }
            },
            function(xhr, status) { console.log("error--->" + status); }
        );
    }

    function getSetting_watch_goods_area() {
        var setting = {
            datatype : "json", // 返回的数据类型
            mtype : "post", // 提交方式
            height : "auto", // 表格宽度
            autowidth : true, // 是否自动调整宽度
            // styleUI: 'Bootstrap',
            colNames:["id","勾选","商品名称","商品编号","成本价(元)","分销价(元)","市场零售价(元)","分销利润率","折后价","折后利润率","所属仓库","云仓库存","锁定库存" ],
            colModel:[{name:"id",index:"id",width:"12%",align:"center",sortable:false,hidden:true},
                {name:"id",index:"sales_order_no",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                    spriceRelatedGoodsArrays.push(rowObject);
                    return '<input type="checkbox" class="sprice_relate_checkbox_child" data-sku="' + rowObject.csku + '" data-whid="' + rowObject.warehouseId + '" data-stock="' + rowObject.stock + '" data-stock="' + rowObject.id + '" data-id="' + options.rowId + '">';
                }},
                {name:"ctitle",index:"sales_order_no",width:"32%",align:"center",sortable:false, formatter:function(cellvalue,options,rowObject){
                    return '<span class=\"relevance_product_link\">' + '<img src="' + rowObject.imageUrl + '">' +
                        '<a href="javascript:;">' + deal_with_illegal_value(rowObject.ctitle) + '</a>' + '</span>';
                }},
                {name:"csku",index:"opt_fee",width:"12%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"disTotalCost",index:"ordering_date",width:"14%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"disPrice",index:"stock",width:"12%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return cellvalue.toFixed(2);
                }},
                {name:"localPrice",index:"warehouse_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"disProfitRate",index:"nick_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"specialPrice",index:"nick_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"predisProfitRate",index:"nick_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"warehouseName",index:"",width:"12%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"stock",index:"nick_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return deal_with_illegal_value(cellvalue);
                }},
                {name:"limitedPurchase",index:"nick_name",width:"12%",align:"center",sortable:false,formatter:function(cellvalue,options,rowObject){
                    return (rowObject.limitedPurchase ? ('限购' + rowObject.limitedPnum + '个') : '不限制')
                }}
            ],
            viewrecords : true,
            caption:"已关联商品",//表名称
            pagerpos : "center",
            pgbuttons : false,
            autowidth: true,
            rownumbers: true
        };
        return setting;
    }

    return {init: init};
});
