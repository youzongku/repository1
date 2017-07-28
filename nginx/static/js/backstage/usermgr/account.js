
//定义全局变量
var layer = undefined;
var laypage = undefined;

var type = undefined;//区分栏目的标识（1：账户余额，2：账户余额管理）
var BbcGrid = undefined
function init_account_new(layerParam, laypageParam, BbcGridParam,typeParam){
    BbcGrid = BbcGridParam
    type = typeParam
    layer = layerParam;
    laypage = laypageParam;
    selectionModel();
    initAccountsByBbcGrid()
}

// 新的查询列表
function initAccountsByBbcGrid(){
    var grid = new BbcGrid();
    grid.initTable($("#test_account_table"),getSetting());
}
// 条件查询
function getAccountsByConditions(){
    // 拿到原有的，清除掉
    var postData = $("#test_account_table").jqGrid("getGridParam", "postData");
    $.each(postData, function (k, v) {
        delete postData[k];
    });
    var params = {search: $(".search_category .searchInput").val().trim()}
    var distributorType = $(".search_category #account_distributorType").val();
    var distributorMode = $(".search_category #account_distributorMode").val();
    if (distributorMode != 0) {
        params.distributionMode = distributorMode;
    }
    if (distributorType != 0) {
        params.comsumerType = distributorType;
    }
    console.log("账户余额，条件查询参数如下：")
    console.log(params)

    $("#test_account_table").jqGrid('setGridParam',{page:1, postData:params}).trigger('reloadGrid');
}

function init_account(layerParam, laypageParam, typeParam){
    layer = layerParam;
    laypage = laypageParam;
    type = typeParam;
    getAccounts(1);
}

function getAccounts(curr){
    var params = {
        page: (curr == undefined || curr == 0) ? 1 : curr,
        rows: $(".list_content #account_pageSize").val(),
        search: $(".search_category .searchInput").val().trim()
    };
    var distributorType = $(".search_category #account_distributorType").val();
    var distributorMode = $(".search_category #account_distributorMode").val();
    if (distributorMode != 0) {
        params.distributionMode = distributorMode;
    }
    if (distributorType != 0) {
        params.comsumerType = distributorType;
    }
    $.ajax({
        url : "/member/getAllAccount",
        type : "POST",
        data : params,
        dataType : "json",
        success : function(response) {
            if (response) {
                if (response.mark == 2 || response.mark == 3) {
                    insert_account_list(response.data.list);
                    //insert_account_list2(response.data.list);
                    $("#accountTotal").text(response.data.rows);
                    $("#accountPages").text(response.data.totalPage);
                    init_account_pagination(response.data);
                } else if (!response.suc) {
                    window.location.href = "login.html";
                } else if (data.mark == 1){
                    layer.msg("获取分销商失败", {icon : 2, time : 1000});
                }
            }
        }
    });
}

function insert_account_list(list){
    $(".account_table tbody").empty("tr");
    var ulHTML = '';
    $.each(list,function(i,item){
        ulHTML += '<tr>'+
            '<td  class="nick">'+deal_with_illegal_value(item.nick)+'</td>'+
            '<td  class="email">'+deal_with_illegal_value(item.email)+'</td>'+
            '<td>'+deal_with_illegal_value(item.distributionModeDesc)+'</td>'+
            '<td>'+deal_with_illegal_value(item.comsumerTypeName)+'</td>'+
            '<td>'+deal_with_illegal_value(item.realName)+'</td>'+
            '<td class="balance">'+deal_with_illegal_value(item.balance)+'</td>'+
            '<td>'+deal_with_illegal_value(item.frozenAmount)+'</td>'+
            (type == 2 ? ('<td><a class="closeBalance" onclick="reduceMoney(this)">核销余额</a><br /><a accountId='+item.id+' class="operationLog2" onclick="lookHistory(this)">操作日志</a></td>') : '') +
            '</tr>';
    });
    $(".account_table tbody").append(ulHTML);
}

function init_account_pagination(page){
    if ($("#pagination_account")[0] != undefined) {
        $("#pagination_account").empty();
        laypage({
            cont: 'pagination_account',
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
                    getAccounts(obj.curr);
                }
            }
        });
    }
}

//核减余额
function reduceMoney(obj){
    // var $obj = $(obj);
    // var nick = $(obj).parent().siblings(".nick").text();
    // var email = $(obj).parent().siblings(".email").text();
    // var balance = $(obj).parent().siblings(".balance").text();
    clickThisTr($(obj).parent().parent())

    var accountId = $(obj).attr("accountId")
    var model = $("#test_account_table").jqGrid("getRowData",accountId);
    var nick = model.nick;
    var email = model.email;
    var balance = model.balance;

    var pop = '<div class="close-balance">'+
        '<div class="close-balance1">'+
        '<p>名称：<em>'+nick+'</em></p>'+
        '<p style="margin-top: 16px;">分销商：<em>'+email+'</em></p>'+
        '<p style="margin-top: 16px;">账户余额：￥<em>'+balance+'</em>元</p>'+
        '</div>'+
        '<div>核销金额：<input class="reduceMoney" type="text"/>元</div>'+
        '</div>';
    layer.open({
        type: 1,
        title: '核销余额',
        area: ['350px','auto'],
        shadeClose: true, //点击遮罩关闭
        content: pop,
        btn:['确定','取消'],
        success:function(layero, index){
        },
        //i和currdom分别为当前层索引、当前层DOM对象
        yes: function(i, currdom) {
            var reduceMoney = $(".close-balance .reduceMoney").val();
            if (!/^((\d{1})|([1-9]{1}\d+)|(\d{1}\.\d{1,2})|([1-9]{1}\d+\.\d{1,2}))$/.test(reduceMoney)){
                layer.msg("请输入正确格式的付款金额", {icon: 0, time: 2000});
                return;
            }
            if (parseFloat(reduceMoney)>parseFloat(balance)) {
                layer.msg("该分销商账户余额不足", {icon : 0, time : 2000});
                return;
            }
            var params = {
                email : email,
                reduceAmount : parseFloat(reduceMoney).toFixed(2)
            };
            ajax_post("../member/reduceAccount", JSON.stringify(params), "application/json",
                function (response) {
                    if (response) {
                        if (response) {
                            if (response.suc) {
                                // getAccounts($("#pagination_account .laypage_curr").text());
                                getAccountsByConditions()
                                layer.msg('核销余额成功！', {icon : 1, time : 2000},function(){
                                    layer.close(i);
                                });
                            } else {
                                layer.msg(response.msg, {icon : 5, time : 2000});
                            }
                        }
                    }
                },
                function (xhr, status) {
                }
            );
        }
    })
}

function lookHistory(obj) {
    clickThisTr($(obj).parent().parent())

    var accountId = $(obj).attr("accountId");
    var pop = '<div class="operation-log-pop-box">'+
        '<div class="operation-log-list">'+
        '</div>'+
        '</div>';
    layer.open({
        type: 1,
        title: '操作日志',
        btn: false,
        shadeClose: true,
        content: pop,
        area: ['450px', '300px'],
        move: false
    });
    var goal = $(".operation-log-pop-box");
    ajax_get("../member/getAccountHistory?accountId="+accountId,undefined, undefined,
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
                        goal.children().append("该账户没有核减历史记录");
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
function selectionModel() {
    var obj = $("#account_distributorMode");
    ajax_get("../member/getMode", null, null, function (data) {
        if (data) {
            obj.empty();
            var htmlCode = '<option value="0">所有分销商渠道</option>';
            $.each(data,function(i,item) {
                htmlCode += '<option value="'+item.id+'">'+item.disMode+'</option>';
            });
            obj.prepend(htmlCode);
        }
    });
}
function getSetting() {
    var setting = {
        url:"/member/getAllAccount",
        rownumbers : true, // 是否显示前面的行号
        datatype : "json", // 返回的数据类型
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        // styleUI: 'Bootstrap',
        colNames:["名称","用户账号","分销渠道","分销商类型","姓名","账户余额（元）","冻结金额（元）"],
        colModel:[{name:"nick",index:"nick",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
            return deal_with_illegal_value(cellvalue)
        }},
            {name:"email",index:"email",width:"14%",align:"center",sortable:true},
            {name:"distributionModeDesc",index:"distributionModeDesc",width:"14%",align:"center",sortable:false},
            {name:"comsumerTypeName",index:"comsumerTypeName",width:"12%",align:"center",sortable:false},
            {name:"realName",index:"realName",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                return deal_with_illegal_value(cellvalue)
            }},
            {name:"balance",index:"balance",width:"12%",align:"center",sortable:true},
            {name:"frozenAmount",index:"frozenAmount",width:"12%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                return cellvalue ? cellvalue : 0
            }}
        ],
        viewrecords : true,
        rowNum : 10,
        rowList : [ 10, 20, 30 ],
        pager:"#pagination_account",//分页
        caption:type==2?"账户余额管理":"账户余额",//表名称
        pagerpos : "center",
        pgbuttons : true,
        autowidth: true,
        rownumbers: true, // 显示行号
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        jsonReader:{
            root: "data.list",  //数据模型
            page: "data.currPage",//数据页码
            total: "data.totalPage",//数据总页码
            records: "data.rows",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        }
    }
    if(type==2){
        setting.colNames.push("操作")
        setting.colModel.push({name:"opt",index:"id",width:"12%",align:"center",sortable:false})
        setting.gridComplete = function(){
            var ids = $("#test_account_table").jqGrid("getDataIDs")
            for(var i in ids){
                $("#test_account_table").jqGrid("setRowData", ids[i], {opt:'<a  accountId="'+ids[i]+'" onclick="reduceMoney(this)">核销余额</a><br />' +
                    '<a accountId="'+ids[i]+'" onclick="lookHistory(this)">操作日志</a>'})
            }
        }
    }

    return setting;
}