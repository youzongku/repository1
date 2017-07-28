define("userlevel", ["jquery", "laypage", "layer","BbcGrid"], function($, laypage, layer,BbcGrid) {
    function initialize() {
        bind_element_event();
        init_level_select_data();
        //init_level_list();
        var grid = new BbcGrid();
        grid.initTable($("#level_table"),levelSetting());
    }

    //初始化等级下拉选数据
    function init_level_select_data() {
        ajax_get("../member/getallranks?" + Math.random(), {}, undefined,
            function(data) {
                if (data.suc) {
                    var rs = data.data, number = 0;
                    $(".default-level #level_choose").empty();
                    var optionHTML = '<option value="" selected="selected" data-disc="">请选择</option>';
                    for (var i in rs) {
                        optionHTML += '<option value="' + rs[i].id + '" data-disc="' + rs[i].discount + '%">' + rs[i].rankName + '级</option>';
                        if (rs[i].bdefault) {
                            $(".default-level #level_default").text(rs[i].rankName + "级");
                            $(".default-level #level_default").attr("rid", rs[i].id);
                        } else {
                            number += 1;
                        }
                    }
                    if (number != 0 && number == rs.length) {
                        $(".default-level #level_default").text('未设置');
                        $(".default-level #level_default").attr("rid", 0);
                    }
                    $(".default-level #level_choose").append(optionHTML);
                    
                } else if (data.code == 1) {
                    window.location.href = "login.html";
                }
            },
            function(xhr, status) {
                console.error("error--->" + status);
            }
        );
    }

    //为HTML元素绑定事件
    function bind_element_event() {
        //更改默认等级按钮
        $(".default-level #alter_level_btn").click(function(event) {
            $(".default-level #alter_level_btn").hide();
            $(".default-level #level_default").hide();
            $(".default-level #level_choose").show();
            $(".default-level #save_level_btn").show();
            $(".default-level #cancel_level_btn").show();
        });

        //取消更改默认等级按钮
        $(".default-level #cancel_level_btn").click(function(event) {
            $(".default-level #alter_level_btn").show();
            $(".default-level #level_default").show();
            $(".default-level #level_choose").hide();
            $(".default-level #save_level_btn").hide();
            $(".default-level #cancel_level_btn").hide();
        });

        //保存默认等级按钮
        $(".default-level #save_level_btn").click(function(event) {
            var one = $(".default-level #level_default").attr("rid");
            var two = $(".default-level #level_choose").val();
            if(two == undefined || two == ''){
            	layer.msg("选择的默认等级不能为空", {icon: 2, time: 1000});
            	return;
            }
            if (two != one) {
                var params = {
                    sign: 2,
                    rid: two,
                    isdefault: true
                };
                ajax_post("../member/saverank", params, undefined,
                    function(data) {
                        if (data.suc) {
                            $(".default-level #level_default").attr("rid", two);
                            $(".default-level #level_choose").find("option").each(function(i, item) {
                                if ($(item).attr("value") == two) {
                                    $(".default-level #level_default").text($(item).text());
                                }
                            });
                            $(".default-level #cancel_level_btn").click();
                            init_level_select_data();
                        } else if (data.code == 1) {
                            window.location.href = "login.html";
                        } else if (data.code == 2) {
                            layer.msg("请求参数不存在或格式错误", {icon: 2, time: 1000});
                        } else if (data.code == 3) {
                            layer.msg("保存默认等级失败，请稍后重试", {icon: 2, time: 1000});
                        }
                    },
                    function(xhr, status) {
                        console.error("error--->" + status);
                    }
                );
            }
        });

        //保存新增等级按钮
        $(".news-level #save_new_btn").click(function(event) {
            var $inputs = $(".news-level .edit-pop-list").find("input");
            var name = $inputs.eq(0).val();
            var disc = $inputs.eq(1).val();
            if (name == undefined || name == "" || disc == undefined || disc == "") {
                layer.msg("等级名称或折扣不能为空", {icon: 2, time: 1000});
                return;
            }
            //校验折扣值必须为大于0小于等于100整数
            var type = /^([1-9]\d?|100)$/;
		    var re = new RegExp(type);
		    if(disc.replace("%", "").match(re) == null){
		    	    layer.msg("折扣值必须为大于0小于等于100的整数", {icon: 2, time: 1000});
		    	    return ;
		    	}
            var params = {
                sign: 1,
                rankname: name.replace("级", ""),
                discount: disc.replace("%", "")
            };
            ajax_post("../member/saverank", params, undefined,
                function(data) {
                    if (data.suc) {
                        layer.closeAll();
                        //init_level_list();
                        searchLevel();
                        init_level_select_data();
                    } else if (data.code == 1) {
                        window.location.href = "login.html";
                    } else if (data.code == 2) {
                        layer.msg("请求参数不存在或格式错误", {icon: 2, time: 1000});
                    } else if (data.code == 3) {
                        layer.msg("保存新等级失败，请稍后重试", {icon: 2, time: 1000});
                    } else if (data.code == 4) {
                        layer.msg("等级名称或折扣已存在", {icon: 2, time: 1000});
                    }
                },
                function(xhr, status) {
                    console.error("error--->" + status);
                }
            );
        });
    }
    
    //初始化等级列表数据
    function init_level_list(curr) {
        var params = {
            "currPage": curr == undefined ? 1 : curr,
            "pageSize": 10
        };
        ajax_post("../member/getranks", params, undefined,
            function(data) {
                if (data.suc) {
                    insert_level_list_item(data.page.list);
                    init_rankself_pagination(data.page);
                } else if (data.code == 1) {
                    window.location.href = "login.html";
                } else if (data.code == 2) {
                    layer.msg("请求参数不存在或格式错误", {icon: 2, time: 1000});
                }
            },
            function(xhr, status) {
                console.error("error--->" + status);
            }
        );
    }

    //初始化分等级分页栏
    function init_rankself_pagination(page) {
        if ($(".default-level #rankself_pagination")[0] != undefined) {
            $(".default-level #rankself_pagination").empty();
            laypage({
                cont: 'rankself_pagination',
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
                        init_level_list(obj.curr);
                    }
                }
            });
        }
    }

    //插入等级列表数据
    function insert_level_list_item(list) {
        //序号
        var index = 0;
        var itemHTML = '';
        for (var i in list) {
            index += 1;
            itemHTML +=
                '<tr class="list_message defaute-level-list-content" rid="' + list[i].id + '">' +
                    '<td class="">' + index + '</td>' +
                    '<td style="display: none" class="">' + list[i].id + '</td>' +
                    '<td class="">' + list[i].rankName + '</td>' +
                    '<td class="">' + list[i].discount + '</td>' +
                    '<td class="">' + list[i].userNumber + '</td>' +
                    '<td class="">' +
                        '<div class="level-edit"><a href="javascript:;">编辑</a></div>' +
                        (list[i].userNumber == 0 ? '<div class="operation-log"><a href="javascript:;">删除</a></div>' : '') +
                    '</td>' +
                '</tr>';
        }
        $("#level_listContent").empty().append(itemHTML);
        bind_level_edit_node_event();
    }


    return {
        initialize: initialize
    };
});

/*jqGrid-Table*/
function levelSetting() {
    var setting = {
        url:"/member/getranks",
        datatype : "json", // 返回的数据类型
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        colNames:["等级名称","折扣","用户个数","操作"],
/*        '<td class="">' + list[i].rankName + '</td>' +
        '<td class="">' + list[i].discount + '</td>' +
        '<td class="">' + list[i].userNumber + '</td>' +
        '<td class="">' +
        '<div class="level-edit"><a href="javascript:;">编辑</a></div>' +
        (list[i].userNumber == 0 ? '<div class="operation-log"><a href="javascript:;">删除</a></div>' : '') +
        '</td>' +*/
        colModel:[
            {name: "rankName", index: "rankName",align: "center", sortable: false},
            {name: "discount", index: "discount",align: "center", sortable: false},
            {name: "userNumber", index: "userNumber",align: "center", sortable: false},
            {name: "id", index: "id",align: "center", sortable: false,
                formatter:function(cellvalue, options, rowObject){
                    return '<div data-id="'+cellvalue+'" class="level-edit"><a href="javascript:;">编辑</a></div>' +
                        (rowObject.userNumber == 0 ? '<div data-id="'+cellvalue+'" class="operation-log"><a href="javascript:;">删除</a></div>' : '');
                }}
        ],
        viewrecords : true,
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#rankself_pagination",//分页
        pagerpos : "center",
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        caption:"等级设置",//表名称
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
            return JSON.stringify(getLevel());
        },
        gridComplete:function(){
            $("#level_table").on("click","a",function(){
                $(this).parents("td").click();
            });
            bind_level_edit_node_event();
        }};
    return setting;
}

function getLevel(){
    return {
        currPage: $('#level_table').getGridParam('page'),
        pageSize: $('#level_table').getGridParam('rowNum')
    }
}

function searchLevel(){
    // 拿到原有的，清除掉
    $("#level_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}
//等级编辑与删除事件绑定
function bind_level_edit_node_event() {
    var $edit = $(".default-level .level-edit");
    var $canc = $(".default-level .operation-log");
    if ($edit.length > 0) {
        //编辑
        $edit.find("a").click(function(event) {
            var $ul = $(this).parent();
            var rid = $ul.data("id");
            var rankname = $ul.children().eq(2).text();
            var discount = $ul.children().eq(3).text().replace("%","");
            layer.open({
                type: 1,
                title: "等级编辑",
                content:
                '<div style="width: 400px; height: 140px; border: 1px solid #ccc; margin-left: auto; margin-right: auto; padding: 10px;">' +
                '<table id="edit_rank_dialog" style="table-layout: fixed; width: 100%; border:">' +
                '<tr style="width: 400px; display: block; margin-top: 20px;">' +
                '<th style="width: 20%; display: inline-block;  text-align: right;">等级名称：</th>' +
                '<td style="width: 75%; display: inline-block;  text-align: left;">' +
                '<input type="text" name="name" value="' + rankname + '"  style="width: 80%; display: block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                '</td>' +
                '</tr>' +
                '<tr style="width: 400px; display: block; margin-top: 20px;">' +
                '<th style="width: 20%; display: inline-block;  text-align: right;">等级折扣：</th>' +
                '<td style="width: 75%; display: inline-block;  text-align: left;">' +
                '<input type="text" name="disc" value="' + discount + '"  style="width: 30%; display: inline-block; padding: 5px; appearance: textfield; -webkit-appearance: textfield; -moz-appearance: textfield; -ms-appearance: textfield;">' +
                '<span style="display: inline-block;">%</span>'+
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
                    console.log(i + "<--i	currdom-->" + currdom[0]);
                    var name = $("#edit_rank_dialog input[name='name']").val();
                    var disc = $("#edit_rank_dialog input[name='disc']").val();

                    if (name == undefined || name == "") {
                        layer.msg("等级名称不能为空", {icon: 2, time: 1000});
                        return;
                    }
                    //校验折扣值必须为大于0小于等于100整数
                    var type = /^([1-9]\d?|100)$/;
                    var re = new RegExp(type);
                    if(disc.match(re) == null){
                        layer.msg("折扣值必须为大于0小于等于100的整数", {icon: 2, time: 1000});
                        return ;
                    }
                    var checkParams = {
                        rankName:name.replace("级", ""),
                        rid: rid,
                        discount:disc
                    };
                    ajax_post("/member/checkRank", JSON.stringify(checkParams), "application/json",
                        function(response) {
                            //校验成功
                            if(response.suc){
                                //存在相同的折扣或折扣名称给出提示不能提交
                                if(response.data.isFlag){
                                    layer.msg("设置的等级名称或折扣值与系统存在的等级重复，请重新设置", {icon: 2, time: 1000});
                                }else{
                                    var params = {
                                        sign: 2,
                                        rid: rid,
                                        rankname: name.replace("级", ""),
                                        discount: disc.replace("%", "")
                                    };
                                    ajax_post("../member/saverank", params, undefined,
                                        function(data) {
                                            if (data.suc) {
                                                layer.close(i);
                                                //init_level_list();
                                                searchLevel();
                                                init_level_select_data();
                                            } else if (data.code == 1) {
                                                window.location.href = "login.html";
                                            } else if (data.code == 2) {
                                                layer.msg("请求参数不存在或格式错误", {icon: 2, time: 1000});
                                            } else if (data.code == 3) {
                                                layer.msg("保存等级失败，请稍后重试", {icon: 2, time: 1000});
                                            }
                                        },
                                        function(xhr, status) {
                                            console.error("error--->" + status);
                                        }
                                    );
                                }
                            }else{//校验失败
                                layer.msg(response.code, {icon: 2, time: 1000});
                            }
                        },
                        function(xhr, status) {
                            layer.msg("校验等级折扣失败，系统错误", {icon: 2, time: 1000});
                        }
                    );
                }
                //i为当前层索引，无需进行手工关闭。如果不想关闭，return false即可。
                //cancel: function(i) { layer.close(i); }
            });
        });
        //删除
        $canc.find("a").click(function(event) {
            var rid = $(this).parent().data("id");
            layer.confirm("确认删除该等级？", {icon: 3},
                //i和currdom分别为当前层索引、当前层DOM对象
                function(i, currdom) {
                    console.log(i + "<--i	currdom-->" + currdom[0]);
                    layer.close(i);
                    ajax_get("../member/delrank", { rid: rid }, "",
                        function(data) {
                            if (data.suc) {
                                //init_level_list();
                                searchLevel();
                                init_level_select_data();
                            } else if (data.code == 1) {
                                window.location.href = "login.html";
                            } else if (data.code == 2) {
                                layer.msg("请求参数不存在或格式错误", {icon: 2, time: 1000});
                            } else if (data.code == 3) {
                                layer.msg("删除等级失败，请稍后重试", {icon: 2, time: 1000});
                            }
                        },
                        function(xhr, status) {
                            console.error("error--->" + status);
                        }
                    );
                }
                //i为当前层索引，无需进行手工关闭。如果不想关闭，return false即可。
                //function(i) { layer.close(i); }
            );
        });
    }
}