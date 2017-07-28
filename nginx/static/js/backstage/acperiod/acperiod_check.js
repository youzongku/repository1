var grid,m_id;
function init_payment_check(rParam, layerParam, laypageParam, BbcGrid) {
    //初始化全局变量
    layer = layerParam;
    laypage = laypageParam;
    r = rParam;
    grid = new BbcGrid();
    grid.initTable($("#apm_table"),ck_apmSetting());
    grid.initTable($("#aps_table"),ck_apsSetting())
    ck_init_func();
}
function ck_init_func(){
    ajax_get("/member/getMode","","",function(data){
        var $select =$("#model_select");
        $select.append("<option value=''>所有分销渠道</option>");
        $.each(data,function(i,item){
            $select.append("<option value='"+item.id+"'>"+item.disMode+"</option>");
        })
    })
    entry_func($("#apm_input"),function(){ck_searchApm()});
    $("#apm_search").click(function(){ck_searchApm()});
    $("#model_select").change(function(){ck_searchApm()});
    $("#state_select").change(function(){ck_searchAps()});
    $("#std_select").change(function(){ck_searchAps()})
}


//重置账期查询数据
function ck_searchApm(){
    $("#apm_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}
//账期搜索数据
function ck_viewApmParams(){
    return  {
        disMode:$("#model_select").val(),
        currPage:$('#apm_table').getGridParam('page'),
        pageSize:$('#apm_table').getGridParam('rowNum'),
        search:$("#apm_input").val().trim(),
        sort:$('#apm_table').getGridParam("sortname"),
        filter:$('#apm_table').getGridParam("sortorder")
    }
}


function ck_apmSetting() {
    var setting = {
        url:"/member/ap/m/rdls",
        datatype : "json", // 返回的数据类型
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        colNames:['ID','用户名', '名称', '分销渠道','账期额度','账期剩余额度（元）','待还款金额（元）','账期周期','账期合同单号','责任人','OA审批单号','创建人','操作'],
        colModel:[
            {name:'id',index:'', width:'8%',align:'center',sortable:false,hidden:true},
            {name:'account',index:'', width:'8%',align:'center'},
            {name:'nickName',index:'', width:'8%',align:'center'},
            {name:'disModeDesc',index:'', width:'8%', align:"center"},
            {name:'totalLimit',index:'total_limit', width:'8%', align:"center", sortable:true},
            {name:'leftLimit',index:'', width:'10%', align:"center", sortable:false},
            {name:'usedLimit',index:'used_Limit', width:'10%', align:"center", sortable:true},
            {name:'periodDesc',index:'', width:'8%', align:"center", sortable:false},
            {name:'contractNo',index:'', width:'8%', align:"center", sortable:false},
            {name:'dutyOfficer',index:'', width:'8%',align:"center", sortable:false},
            {name:'oaAuditCode',index:'', width:'8%',align:"center", sortable:false},
            {name:'createUser',index:'', width:'8%',align:"center", sortable:false},
            {name:'operation',index:'', width:'8%',align:"center", sortable:false,
                formatter:function(cellvalue, options, rowObject){
                    return "<div class='payment-btn'>" +
                        "<a onclick='ck_checkDetails("+rowObject.id+");'>查看详情</a>" +
                        "<a onclick='ck_operationLog("+rowObject.id+");'>操作日志</a>" +
                        "</div>";
            }}
        ],
        viewrecords: true,//是否在浏览导航栏显示记录总数
        rowNum:10,//每页显示记录数
        rowList:[10,20,30],//用于改变显示行数的下拉列表框的元素数组。
        pagerpos : "center",
        pager:"#apm_pagination",//分页
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        caption:"账期管理",//表名称,
        jsonReader:{
            root: "obj.list",  //数据模型
            page: "obj.currPage",//数据页码
            total: "obj.totalPage",//数据总页码
            records: "obj.rows",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            id: "id"//唯一标识
        },
        ondblClickRow : function(rowid) {
            var rowObj = $("#apm_table").jqGrid('getRowData',rowid);
            ck_checkDetails(rowObj.id);
        },
        serializeGridData : function(postData) {
            return JSON.stringify(ck_viewApmParams());
        }
    };
    return setting;
}

function ck_searchAps(){
     $("#aps_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}
function ck_viewApsParams(){
    return  {
        masterId:m_id,
        currPage:$('#aps_table').getGridParam('page'),
        pageSize:$('#aps_table').getGridParam('rowNum'),
        std:$("#std_select").val(),
        state:$("#state_select").val(),
        sort:$('#aps_table').getGridParam("sortname"),
        filter:$('#aps_table').getGridParam("sortorder")
    };
}


function ck_checkDetails(id){
    m_id = id;
    ck_searchAps();   
    $("#std_select").empty().append("<option value=''>请选择账期开始日期</option>");
    ajax_get("/member/ap/s/"+m_id+"/std","","",function(list){
        $.each(list,function(i,item){
            $("#std_select").append("<option >"+item+"</option>");    
        })
    })
    $("#homepage").hide();
    $("#follower").show();
}

function ck_checkDetailsBack(){
    $("#homepage").show();
    $("#follower").hide();
}

function ck_operationLog(id){
    var logHtml = '<div id="operationLog" class="add-payment">';
    ajax_get("/member/ap/m/"+id+"/rcd","","",function(records){
        $.each(records,function(i,item){
            var createFlag = item.operateType == 1;
            logHtml += 
                '<div>                                              '+
                '   <em>'+item.operateTimeStr+'&nbsp;&nbsp;'+item.operator+'</em>'+(createFlag?"修改账期信息，修改后的账期信息如下：":item.operateDesc)+
                '</div>'+(createFlag?item.operateDesc:'');
        });
    })
    logHtml +='</div>'
    layer.open({
        type: '1',
        title: '操作日志',
        area: ['600px','400px'],
        content: logHtml,
        btn: ['确认']
    });
}

/**
 *  获取账期公共方法
 *  @param id 账期id
 *  @param 回调函数
 */
function ck_getApm(id,callback){
    ajax_get("/member/ap/m/"+id+"/rdo","","",function(response){
        callback(response);
    })
}

//查看详情-table start
function ck_apsSetting() {
    var setting = {
        url:"/member/ap/s/rdls",
        datatype : "json", // 返回的数据类型
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        // autowidth : true, // 是否自动调整宽度
        width:1079,
        colNames:['分销商', '名称', '账期周期','账期开始时间','合同账期','红线账期','业绩周期开始时间','业绩周期结束时间','账期状态','创建人','操作'],
        colModel:[
            {name:'account',index:'', width:'10%',align:'center', sortable:false},
            {name:'nickName',index:'', width:'9%',align:'center', sortable:false},
            {name:'periodDesc',index:'', width:'9%', align:"center", sortable:false},
            {name:'startTimeStr',index:'start_time', width:'9%', align:"center", sortable:true},
            {name:'contractPeriodDateStr',index:'contract_period_date', width:'9%', align:"center", sortable:true},
            {name:'redLineDateStr',index:'red_line_date', width:'9%', align:"center", sortable:true},
            {name:'performanceStartTimeStr',index:'performance_start_time', width:'9%', align:"center", sortable:true},
            {name:'performanceEndTimeStr',index:'performance_end_time', width:'9%',align:"center", sortable:true},
            {name:'stateStr',index:'', width:'9%',align:"center", sortable:false},
            {name:'createUser',index:'', width:'9%',align:"center", sortable:false},
            {name:'operation',index:'', width:'9%',align:"center", sortable:false,
                formatter:function(cellvalue, options, rowObject){
                    return "<div class='payment-btn'>" +
                        "<a onclick='ck_operationLogT("+rowObject.id+");'>操作日志</a>" +
                        (rowObject.isChargeOff?
                            "<a onclick='ck_chargeOffRecord("+rowObject.id+");'>查看核销记录</a>":"")+
                        "</div>";
                }}
        ],
        viewrecords: true,//是否在浏览导航栏显示记录总数
        rowNum:10,//每页显示记录数
        rowList:[10,20,30],//用于改变显示行数的下拉列表框的元素数组。
        pagerpos : "center",
        pager:"#aps_pagination",//分页
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        caption:"账期管理",//表名称,
        jsonReader:{
            root: "obj.list",  //数据模型
            page: "obj.currPage",//数据页码
            total: "obj.totalPage",//数据总页码
            records: "obj.rows",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        },
        serializeGridData : function(postData) {
            return JSON.stringify(ck_viewApsParams());
        }
    };
    return setting;
}

function ck_operationLogT(id){

     var logHtml = '<div id="operationLogT" class="add-payment">';
    ajax_get("/member/ap/s/"+id+"/rcd","","",function(records){
        $.each(records,function(i,item){
            var createFlag = item.operateType == 1;
            logHtml += 
                '<div>                                              '+
                '   <em>'+item.operateTimeStr+'&nbsp;&nbsp;'+item.operator+'</em>'+(createFlag?"修改账期信息，修改后的账期信息如下：":item.operateDesc)+
                '</div>'+(createFlag?item.operateDesc:'');
        });
    })
    logHtml +='</div>';
    layer.open({
        type: '1',
        title: '操作日志',
        area: ['600px','400px'],
        content: logHtml,
        btn: ['确认']
    });
}

function ck_chargeOffRecord(id){
     ck_getBill(id,function(data){
        ck_showBillRecord(data)
    });
}

function ck_getBill(id,callback){
    ajax_get("/member/ap/b/"+id+"/rd","","",
        function(data){
            if(data.code == 100){
                callback(data.obj);
            }else{
                layer.msg(data.msg,{icon:6,time:2000});
            }
        },
        function(e){
            layer.msg("账单查询失败",{icon:5,time:2000})
        }
    )
}


function ck_showBillRecord(data){
    ck_generBillConten(data,function(content){
         layer.open({
            type: '1',
            area: ['760px','auto'],
            content: content,
            btn: ['确定']
        });
    })
}


function ck_generBillConten(data,call){
    var slave = data.slave;
    var orders = data.orders;
    var orderHtml = "";
    $.each(orders,function(i,item){
        orderHtml +=
            '<tr>'+
            '<td>'+item.orderNo+'</td>'+
            '<td>'+item.orderAmount+'</td>'+
            '<td>'+item.payAmount+'</td>'+
            '</tr>';
    });
    call(
        '<div id="chargeOffCreate" class="add-payment">                   '+
        '    <ul class="charge-off-create-pop">                           '+
        '        <li><span>分销商：</span><em>'+slave.account+'</em></li>       '+
        '        <li><span>账期额度：</span><em>'+slave.totalLimit+'</em></li>          '+
        '        <li><span>名称：</span><em>'+deal_with_illegal_value(slave.nickName)+'</em></li>                '+
        '        <li><span>本账期开始时间：</span><em>'+slave.startTimeStr+'</em></li>'+
        '        <li><span>业绩周期开始时间：</span><em>'+slave.performanceStartTimeStr+'</em></li>'+
        '        <li><span>业绩周期结束时间：</span><em>'+slave.performanceEndTimeStr+'</em></li>'+
        '        <li><span>合同账期：</span><em>'+slave.contractPeriodDateStr+'</em></li>'+
        '        <li><span>业务员：</span><em>'+slave.saleMan+'</em></li>              '+
        '        <li><span>红线账期：</span><em>'+slave.redLineDateStr+'</em></li>      '+
        '        <li><span>责任人：</span><em>'+slave.dutyOfficer+'</em></li>              '+
        '        <li><span>账期周期：</span><em>'+slave.periodDesc+'</em></li>            '+
        '    </ul>                                                        '+
        '    <div><span>总已还金额：</span><em>'+data.rechargeLeft+'</em></div>      '+
        '    <table class="record_sendList_table">                        '+
        '        <thead>                                                  '+
        '        <tr>                                                     '+
        '            <th style="width: 40%;">本账期应结订单</th>          '+
        '            <th style="width: 30%;">订单金额</th>                '+
        '            <th style="width: 30%;">账期付款金额</th>            '+
        '        </tr>                                                    '+
        '        </thead>                                                 '+
        '        <tbody class="charge-off-tbody">                         '+
                    orderHtml +  
        '        </tbody>                                                 '+
        '    </table>                                                     '+
        '    <div>                                                        '+
        '        <span>本账期应结账款：</span><em>'+data.totalAmount+'元</em>         '+
        '    </div>                                                       '+
        (data.isChargeOff? 
        '    <div class="red">'+
        '       <div>核销人：<em>'+data.verificationUser+'</em></div>      '+
        '       <div>核销时间：<em>'+data.verificationDateStr+'</em></div>'+
        '   </div>'
        :'    <div class="red">注：未核销的订单将自动推迟到下一账期</div>  ')+
        '</div>'
    );
}