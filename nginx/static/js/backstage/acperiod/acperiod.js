
var grid,m_id;
function init_payment_management(rParam, layerParam, laypageParam, BbcGrid) {
    //初始化全局变量
    layer = layerParam;
    laypage = laypageParam;
    r = rParam;
    grid = new BbcGrid();
    grid.initTable($("#apm_table"),apmSetting());
    grid.initTable($("#aps_table"),apsSetting())
    grid.initTable($("#charge_off_table"),chargeOffSetting())
    init_func();
}

function init_func(){
    ajax_get("/member/getMode","","",function(data){
        var $select =$("#model_select");
        $select.append("<option value=''>所有分销渠道</option>");
        $.each(data,function(i,item){
            $select.append("<option value='"+item.id+"'>"+item.disMode+"</option>");
        })
    })
    entry_func($("#apm_input"),function(){searchApm()});
    $("#apm_search").click(function(){searchApm()});
    $("#model_select").change(function(){searchApm()});
    $("#state_select").change(function(){searchAps()});
    $("#std_select").change(function(){searchAps()})
    entry_func($("#chargeOff #_searchInput"),function(){searchOrder()});
    $("#chargeOff #_searchButton").click(function(){searchOrder()});
    $("#chargeOff #curr_all").click(function(){
        var $checkOne = $("input[name=checkOne]");
        if($(this).prop("checked")){
            $("#select_all").prop("checked",false);
            $checkOne.prop("checked",true).attr("disabled",true);
        }else{
            $checkOne.prop("checked",false).removeAttr("disabled");
        }
    });
    $("#chargeOff #select_all").click(function(){
        var $checkOne = $("input[name=checkOne]");
        if($(this).prop("checked")){
            $("curr_all").prop("checked",false);
            $checkOne.prop("checked",true).attr("disabled",true);
        }else{
            $checkOne.prop("checked",false).removeAttr("disabled");
        }
    });
}
//重置账期查询数据
function searchApm(){
    $("#apm_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}
//账期搜索数据
function viewApmParams(){
   return  {
       disMode:$("#model_select").val(),
       currPage:$('#apm_table').getGridParam('page'),
       pageSize:$('#apm_table').getGridParam('rowNum'),
       search:$("#apm_input").val().trim(),
       sort:$('#apm_table').getGridParam("sortname"),
       filter:$('#apm_table').getGridParam("sortorder")
   }
 
}


function apmSetting() {
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
                        (rowObject.effectCount > 0?"":"<a onclick='modifyApm("+rowObject.id+");'>修改</a>")+
                        "<a onclick='checkDetails("+rowObject.id+");'>查看详情</a>" +
                        "<a onclick='operationLog("+rowObject.id+");'>操作日志</a>" +
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
            checkDetails(rowObj.id);
        },
        serializeGridData : function(postData) {
            return JSON.stringify(viewApmParams());
        }
    };
    return setting;
}

function searchAps(){
     $("#aps_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}
function viewApsParams(){
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
function checkDetails(id){
    m_id = id;
    init_std();
    searchAps();   
    $("#homepage").hide();
    $("#follower").show();

}
function init_std(){
    $("#std_select").empty().append("<option value=''>请选择账期开始日期</option>");
    ajax_get("/member/ap/s/"+m_id+"/std","","",function(list){
        $.each(list,function(i,item){
            $("#std_select").append("<option >"+item+"</option>");    
        })
    })  
}
function checkDetailsBack(){
    $("#homepage").show();
    $("#follower").hide();
}


function operationLog(id){
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
function getApm(id,callback){
    ajax_get("/member/ap/m/"+id+"/rdo","","",function(response){
        callback(response);
    })
}

function generApmContent(apm,call){
    call(
        '<div id="addPaymentO" class="add-payment">                                   '+
        '   <div>                                                                     '+
        '       <span><b class="red">*</b>分销商账户：</span>                         '+
        (apm?'       <em style="padding-left: 5px;">'+apm.account+'</em>               ':
        '       <input type="text" disabled id="disAccount" />                        '+
        '       <input type="button" value="选择" onclick="chooseDistributor();"/>    ')+
        '   </div>                                                                    '+
        '   <div class="add-payment-two">                                             '+
        '       <div>                                                                 '+
        '           <span><b class="red">*</b>整个账期额度：</span>                   '+
        '           <input id="totalLimit" value="'+(apm?apm.totalLimit:"")+'" type="text"/>&nbsp;元                                      '+
        '       </div>                                                                '+
        '       <div>                                                                 '+
        '           <span><b class="red">*</b>责任人：</span>                         '+
        '           <input type="text" id="dutyOfficer" value="'+(apm?apm.dutyOfficer:"")+'" placeholder="默认为对应业务员ERP账号"/>        '+
        '       </div>                                                                '+
        '   </div>                                                                    '+
        '   <div class="add-payment-two">                                             '+
        '       <div>                                                                 '+
        '           <span><b class="red">*</b>OA审批单号：</span>                     '+
        '           <input id="oaAuditCode" value="'+(apm?apm.oaAuditCode:"")+'" type="text"/>                                              '+
        '       </div>                                                                '+
        '       <div>                                                                 '+
        '           <span>账期合同编号：</span>                                       '+
        '           <input id="contractNo" value="'+(apm?apm.contractNo:"")+'" type="text"/>                                              '+
        '       </div>                                                                '+
        '   </div>                                                                    '+
        '   <div>                                                                     '+
        '       <span><b class="red">*</b>选择账期周期类型：</span>                   '+
        '       <select id="periodType" class="choose_payment">                                       '+
        '           <option value="0" '+(apm&&apm.periodType == 0?"selected":"")+' >按天</option>                                   '+
        '           <option value="1" '+(apm&&apm.periodType == 1?"selected":"")+' >按自然月</option>                               '+
        '       </select>                                                             '+
        '       <input type="text" id="periodLength" value="'+(apm?apm.periodLength:"")+'" style="width: 50px;"/>&nbsp;                       '+
        '       <em class="choose_payment_text" id="pType" style="vertical-align: middle">天</em>'+
        '   </div>                                                                    '+
        '</div>' 
    )
}


//修改账期
function modifyApm(id){
    var m = undefined;
    getApm(id,function(data){
        m = data.obj;
    })
    generApmContent(m,function(content){   
        layer.open({
            type: '1',
            title: '修改账期',
            area: ['760px','auto'],
            content: content,
            shadeClose: false,
            btn: ['确定','取消'],
            success:function(){
                $("#addPaymentO").data("id",id);
                $('#addPaymentO #totalLimit').keyup(function () {
                    this.value =this.value.replace(/\s+/g, "").replace(/[^(\d(\.\d{2})?]/g, "");
                });
                $('#addPaymentO #periodLength').keyup(function (event) {
                    this.value=this.value.replace(/\D/g,'');
                });
                $("#addPaymentO #periodType").change(function(){
                    if(this.value == 0){
                        $("#pType").text("天");
                    }else{
                        $("#pType").text("个自然月");
                    }
                });
            },
            yes: function(index){
                var mdParam = modifyApmParam();
                if(!mdParam){
                    return;
                }
                ajax_post("/member/ap/m/md",JSON.stringify(mdParam),"application/json",
                    function(response){
                        if(response.code == 100){
                            layer.msg(response.msg,{icon:6,time:2000});
                            layer.close(index);
                            searchApm();
                        }else{
                            layer.msg(response.msg,{icon:5,time:2000});
                        }
                    },function(e){
                        layer.msg("修改账期异常",{icon:5,time:2000});
                    }
                );      
            }
        });
    })
}

//修改账期参数
function modifyApmParam(){
    var $node = $("#addPaymentO");
    var totalLimit = $node.find("#totalLimit").val();
    if(!(checkPrice(totalLimit)&&totalLimit>0)){
        layer.msg("账期额度格式错误，请输入正确数字（小数点后最多2位）并且大于零",{icon:5,time:2000});
        return undefined;
    }
    var dutyOfficer = $node.find("#dutyOfficer").val();
    if(!dutyOfficer){
        layer.msg("请填写责任人",{icon:5,time:2000});
        return undefined;
    }
    var oaAuditCode = $node.find("#oaAuditCode").val();
    if(!oaAuditCode){
        layer.msg("OA审批单号",{icon:5,time:2000});
        return undefined;
    }
    var periodLength = $node.find("#periodLength").val();
    if(!(periodLength&&periodLength>0)){
        layer.msg("周期长度不能为空并且大于零",{icon:5,time:2000});
        return undefined;
    }
    var param =  
        {
           "id":$node.data("id"),
           "totalLimit":totalLimit,
           "oaAuditCode": oaAuditCode, 
           "contractNo": $node.find("#contractNo").val(), 
           "dutyOfficer": dutyOfficer, 
           "periodType": $node.find("#periodType").val(), 
           "periodLength": periodLength
        }
    return param;
}
//新增账期
function addPayment(){
    generApmContent(undefined,function(content){
        layer.open({
            type: '1',
            title: '添加账期',
            area: ['760px','auto'],
            content: content,
            shadeClose: false,
            btn: ['下一步','取消'],
            success:function(){
                $('#addPaymentO #totalLimit').keyup(function () {
                    this.value =this.value.replace(/\s+/g, "").replace(/[^(\d(\.\d{2})?]/g, "");
                });
                $('#addPaymentO #periodLength').keyup(function (event) {
                    this.value=this.value.replace(/\D/g,'');
                });
                $("#addPaymentO #periodType").change(function(){
                    if(this.value == 0){
                        $("#pType").text("天");
                    }else{
                        $("#pType").text("个自然月");
                    }
                });
            },
            yes: function(index){
                var param = mParam();
                if(!param){
                    return;
                }
                layer.close(index);
                add_payment_days(param);           
            },
            btn2: function(index){
                layer.close(index);
            }
        });    
    })
}

function mParam(){
    var account = $("#disAccount").val();
    if(!account){
        layer.msg("请添加分销商账户",{icon:5,time:2000});
        return undefined;
    }
    var totalLimit = $("#totalLimit").val();
    if(!(checkPrice(totalLimit)&&totalLimit>0)){
        layer.msg("账期额度格式错误，请输入正确数字（小数点后最多2位）并且大于零",{icon:5,time:2000});
        return undefined;
    }
    var dutyOfficer = $("#dutyOfficer").val();
    if(!dutyOfficer){
        layer.msg("请填写责任人",{icon:5,time:2000});
        return undefined;
    }
    var oaAuditCode = $("#oaAuditCode").val();
    if(!oaAuditCode){
        layer.msg("OA审批单号",{icon:5,time:2000});
        return undefined;
    }
    var periodLength = $("#periodLength").val();
    if(!(periodLength&&periodLength>0)){
        layer.msg("周期长度不能为空并且大于零",{icon:5,time:2000});
        return undefined;
    }
    return {
            "account": account, 
            "totalLimit": totalLimit, 
            "oaAuditCode": oaAuditCode, 
            "contractNo": $("#contractNo").val(), 
            "dutyOfficer": dutyOfficer, 
            "periodType": $("#periodType").val(), 
            "periodLength": periodLength
          }
}

function add_payment_days(mParam){
    generApsContent(undefined,function(content){
        layer.open({
            type: '1',
            title: '添加账期',
            area: ['auto','auto'],
            content: content,
            shadeClose: false,
            btn: ['确认','取消'],
            success:function(){
                aps_func(mParam.periodType,mParam.periodLength,undefined);
            },
            yes:function(index){
                var param = sParam();
                if(!param){
                    return;
                }
                ajax_post("/member/ap/sv",JSON.stringify({"master":mParam,"slave":param}),"application/json",
                    function(response){
                        if(response.code == 100){
                            layer.msg(response.msg,{icon:6,time:2000});
                            layer.close(index);
                            searchApm();
                        }else{
                            layer.msg(response.msg,{icon:5,time:2000});
                        }
                    },function(e){
                        console.log(e);
                        layer.msg("添加账期异常",{icon:5,time:2000})
                    }
                )

            }
        });
    });
}

function sParam(){
    var $node = $("#addPaymentT");
    var startTime = $node.find("#startTime").val();
    if(!startTime){
        layer.msg("开始时间不能为空",{icon:5,time:2000});
        return undefined;
    }
    var redLineDays = $node.find("#redLineDays").val()
    if(!(redLineDays&&redLineDays>0)){
        layer.msg("红线账期天数不能为空并且大于零",{icon:5,time:2000});
        return undefined;
    }
    var performanceEndTime = $node.find("#performanceEndTime").val();
    if(!performanceEndTime){
        layer.msg("业绩周期结束时间不能为空",{icon:5,time:2000});
        return undefined;
    }
    var param = 
        {
            "startTime": startTime, 
            "redLineDays": redLineDays, 
            "performanceEndTime": performanceEndTime
        };
    return param;
        
    
}
function chooseDistributor(){
    var dis_html = 
    '<div id="addDistributor" class="disturb_choice_pop">  '+
    '   <p>                                                        '+
    '       <input id="search_disturb_input" type="text">          '+
    '       <button id="search_disturb_btn">搜索</button>          '+
    '   </p>                                                       '+
    '   <table id="dis_table">                                     '+ 
    '   </table>                                                   '+
    '   <div id="dis_pagination">                            </div>'+
    '</div>';

    layer.open({
        type: '1',
        title: '选择分销商',
        area: ['600px','550px'],
        content: dis_html,
        shadeClose: false,
        btn: ['确定','取消'],
        success:function(){
            grid.initTable($("#dis_table"),disSetting());
            entry_func($("#search_disturb_input"),function(){searchDis()});
            $("#search_disturb_btn").click(function(){searchDis()});
        },
        yes:function(index){
            var $checkDis =  $("input[name=disAccount]:checked");
            if($checkDis.length > 0){
                $("#dutyOfficer").val($checkDis.data("erp"));
                $("#disAccount").val($checkDis.data("email"))
            }
            layer.close(index);
        }
    });
}

function viewDisParams(){
    return {
        role: 2,
        currPage:$('#dis_table').getGridParam('page'),
        pageSize:$('#dis_table').getGridParam('rowNum'),
        search: $("#addDistributor #search_disturb_input").val().trim()
    }
}

function searchDis(){
     $("#dis_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}

function disSetting(){
    return  {
        url:"/member/relatedMember",
        datatype : "json", // 返回的数据类型
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        autowidth : true, // 是否自动调整宽度
        colNames:['勾选', '分销商账号', '昵称','手机号'],
        colModel:[
            {name:'checked',index:'', width:'10%',align:'center', sortable:false,
                formatter:function(cellvalue,options,rowObject){
                    var exitDis = $("#disAccount").val();
                    return '<input type="radio" '+(rowObject.isFrozen?"disabled":"")+' '+(exitDis == rowObject.email?"checked='checked'":"")+' name="disAccount" data-email="'+rowObject.email+'" data-erp="'+rowObject.salesmanErp+'"  >';
                }
            },
            {name:'email',index:'', width:'9%',align:'center', sortable:false,
                formatter:function(cellvalue,options,rowObject){
                    return rowObject.email+ (rowObject.isFrozen?'【<b style="color: red;">冻结</b>】':"");
                }
            },
            {name:'nick',index:'', width:'9%', align:"center", sortable:false},
            {name:'telphone',index:'', width:'9%', align:"center", sortable:false}
        ],
        viewrecords: true,//是否在浏览导航栏显示记录总数
        rowNum:10,//每页显示记录数
        rowList:[10,20,30],//用于改变显示行数的下拉列表框的元素数组。
        pagerpos : "center",
        pager:"#dis_pagination",//分页
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
        caption:"账期管理",//表名称,
        jsonReader:{
            root: "data.list",  //数据模型
            page: "data.currPage",//数据页码
            total: "data.totalPage",//数据总页码
            records: "data.rows",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            id: "id"//唯一标识
        },
        serializeGridData : function(postData) {
            return JSON.stringify(viewDisParams());
        }
    };
}




$('.choose_payment').change(function(){
    var value=$(this).find("option:selected").val();
    if(value=='1'){
        $('.choose_payment_text').text("天");
    }
    if(value=='2'){
        $('.choose_payment_text').text("个自然月");
    }
});

//查看详情-table start
function apsSetting() {
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
                        (rowObject.state == 0?"<a  data-type='"+rowObject.periodType+"' data-length='"+rowObject.periodLength+"' "+
                        "    onclick='modifyAps(this,"+rowObject.id+");'>修改</a>":"")+
                        (rowObject.state == 1?"<a onclick='disablePayment("+rowObject.id+");'>禁用</a>":"") +
                        "<a onclick='operationLogT("+rowObject.id+");'>操作日志</a>" +
                         ((rowObject.isChargeOff||rowObject.state == 2 
                            ||rowObject.state == 3 
                            || rowObject.state == 5)
                            &&!rowObject.hasNext?
                            "<a onclick='openNextPayment("+rowObject.id+");'>开启下一账期</a>":"")+
                        ((rowObject.state == 2 
                            ||rowObject.state == 3 
                            || rowObject.state == 4)&&!rowObject.isChargeOff?
                            "<a onclick='chargeOff("+rowObject.id+");'>核销</a>":"")+
                        (rowObject.isChargeOff?
                            "<a onclick='chargeOffRecord("+rowObject.id+");'>查看核销记录</a>":"")+
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
            return JSON.stringify(viewApsParams());
        }
    };
    return setting;
}

function generApsContent(aps,call){
    call(
        '<div id="addPaymentT" class="add-payment">                                       '+
        '   <div>                                                                         '+
        '       <span><b class="red">*</b>本账期开始时间：</span>                         '+
        '       <input type="text" id="startTime"                                         '+
        'value="'+(aps?aps.startTimeStr:"")+'" '+(aps&&aps.hasPrev?"disabled":"")+' readonly="true"              '+    
        '    placeholder="默认为上一周期的业绩周期结束时间"/>                             '+
        '   </div>                                                                        '+
        '   <div>                                                                         '+
        '       <span><b class="red">*</b>合同账期：</span>                               '+
        '       <input id="contractPeriodDate" type="text"                                '+
        '  value="'+(aps?aps.contractPeriodDateStr:"")+'" disabled  '+    
        '    placeholder="根据开始时间和账期周期计算合同账期"/>                           '+
        '   </div>                                                                        '+
        '   <div>                                                                         '+
        '       <span><b class="red">*</b>红线账期：</span>                               '+
        '       &nbsp;合同账期后<input type="text"                                        '+
        '    value="'+(aps&&aps.redLineDays?aps.redLineDays:"")+'"                             '+
        '   id="redLineDays" style="width: 50px"/>&nbsp;天                                '+
        '   </div>                                                                        '+
        '   <div>                                                                         '+
        '       <span><b class="red">*</b>业绩周期开始时间：</span>                       '+
        '       <input type="text" id="performanceStartTime" disabled                     '+
        '         value="'+(aps?aps.performanceStartTimeStr:"")+'"'+ 
        ' placeholder="默认为账期开始时间"/>                                              '+
        '   </div>                                                                        '+
        '   <div>                                                                         '+
        '       <span><b class="red">*</b>业绩周期结束时间：</span>                       '+
        '       <input type="text" id="performanceEndTime"   readonly="true"              '+
        '          value="'+(aps&&aps.performanceEndTimeStr?aps.performanceEndTimeStr:"")+'"   '+
        '            placeholder="不得超过合同账期"/>                                     '+
        '   </div>                                                                        '+
        '   <div class="red">注：下一账期开始时间为上一账期的业绩周期结束时间</div>       '+
        '</div>'
    )
}

function modifyAps(obj,id){
 ajax_get("/member/ap/s/"+id+"/rdo","","",function(data){
    var aps = data.obj;
    generApsContent(aps,function(content){
        var type = $(obj).data("type");
        var length = $(obj).data("length");
        layer.open({
            type: '1',
            title: '修改账期',
            area: ['auto','auto'],
            content: content,
            shadeClose: false,
            btn: ['确认','取消'],
            success:function(){
                aps_func(type,length,aps);
            },
            yes:function(index){
                var param = sParam();
                if(!param){
                    return;
                }
                param.id = id;
                ajax_post("/member/ap/s/md",JSON.stringify(param),"application/json",
                    function(response){
                        if(response.code == 100){
                            layer.msg(response.msg,{icon:6,time:2000});
                            layer.close(index);
                            searchAps();
                        }else{
                            layer.msg(response.msg,{icon:5,time:2000});
                        }
                    },function(e){
                        console.log(e);
                        layer.msg("添加账期异常",{icon:5,time:2000})
                    }
                )
            }
        });
    }); 
 }) 
}

function mdsParam(){
     var $node = $("#addPaymentT");
    var startTime = $node.find("#startTime").val();
    if(!startTime){
        layer.msg("开始时间不能为空",{icon:5,time:2000});
        return undefined;
    }
    var redLineDays = $node.find("#redLineDays").val()
    if(!(redLineDays&&redLineDays>0)){
        layer.msg("红线账期天数不能为空并且大于零",{icon:5,time:2000});
        return undefined;
    }
    var performanceEndTime = $node.find("#performanceEndTime").val();
    if(!performanceEndTime){
        layer.msg("业绩周期结束时间不能为空",{icon:5,time:2000});
        return undefined;
    }
    var param = 
        {
          "master": mParam, 
          "slave": {
            "startTime": startTime, 
            "redLineDays": redLineDays, 
            "performanceEndTime": performanceEndTime
          }
        }
    return param;
}


//子账期 新增，修改 弹出框回调
function aps_func(type,length,aps){
     $('#addPaymentT #redLineDays').keyup(function (event) {
        this.value=this.value.replace(/\D/g,'');
    });
    //时间限制
    var start = {
        elem: '#startTime',
        format: 'YYYY-MM-DD',
        // min: laydate.now(0, 'YYYY-MM-DD'), //设定最小日期为当前日期
        max: '2099-06-16', //最大日期
        choose: function(datas){
            var timeStr = datas;
            var date1 = new Date(timeStr);
            //业绩周期开始时间 比 开始时间大一天
            var performanceStartTime = new Date(date1.setDate(date1.getDate()+1)).format('yyyy-MM-dd');
            $("#performanceStartTime").val(performanceStartTime);
            var date2 = new Date(timeStr);
            var contractPeriodDate = undefined;
            if(type == 0){
                contractPeriodDate = new Date(
                    date2.setDate(date2.getDate()+ Number(length))
                    ).format('yyyy-MM-dd');
            }else{
                date1.setDate(1);
                contractPeriodDate = 
                new Date(
                    new Date(date1.setMonth(date1.getMonth() + Number(length))).setDate(0)
                ).format('yyyy-MM-dd');
            }
            $("#contractPeriodDate").val(contractPeriodDate);
            end.min = performanceStartTime;
            end.max = contractPeriodDate;
        }
    };
    var end = {
        elem: '#performanceEndTime',
        format: 'YYYY-MM-DD',
        min: aps?aps.performanceStartTimeStr:laydate.now(1, 'YYYY-MM-DD'),
        max: aps?aps.contractPeriodDateStr:'2099-06-16',
        choose: function(datas){
        }
    };
    $("#addPaymentT #startTime").click(function(){laydate(start);});
    $("#addPaymentT #performanceEndTime").click(function(){laydate(end); });
}
function disablePayment(id){
    layer.open({
        type: '1',
        title: '禁用账期',
        area: ['500px','auto'],
        content: "<div style='padding: 20px;'>请确认是否禁用该分销商账期功能，禁用后，本账期将失效且分销商将无法继续使用账期进行支付，但是仍可以进行下单操作。如果需要重新启用账期，请将账期应结款项核销后开启下一账期</div>",
        btn: ['确认','取消'],
        yes:function(index){
            ajax_get("/member/ap/s/"+id+"/stop","","",
                function(data){
                    if(data.code == 100){
                        layer.msg(data.msg,{icon:6,time:2000},function(){
                            layer.close(index);
                            searchAps();
                        });
                    }else{
                        layer.msg(data.msg,{icon:5,time:2000});
                    }
                }
            );
        }
    });
}
function operationLogT(id){
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
function openNextPayment(id){
    ajax_get("/member/ap/s/"+id+"/next","","",function(data){
        var aps = data.obj;
        generApsContent(aps,function(content){
            layer.open({
                type: '1',
                title: '添加账期',
                area: ['auto','auto'],
                content: content,
                btn: ['确认','取消'],
                success:function(){
                    aps_func(aps.periodType,aps.periodLength,aps);
                    //开始时间不可修改
                    $("#addPaymentT #startTime").attr("disabled",true);
                },
                yes:function(index){
                    var  param = sParam();
                    if(!param){
                        return;
                    }
                    param.id = id;
                    ajax_post("/member/ap/s/next",JSON.stringify(param),"application/json",
                        function(response){
                            if(response.code == 100){
                                layer.msg(response.msg,{icon:6,time:2000});
                                layer.close(index);
                                init_std();
                                searchAps();
                            }else{
                                layer.msg(response.msg,{icon:5,time:2000});
                            }
                        },function(e){
                            layer.msg("开启下一账期异常",{icon:5,time:2000});
                        }
                    );
                }
            });     
        })
    });
}

function searchOrder(){
     $("#charge_off_table").jqGrid('setGridParam',{page:1}).trigger('reloadGrid');
}
function viewOrderParams(){
    var $node = $("#chargeOff");
    return {
        id:$node.data("id"),
        std:$node.find("#_dateSearch").val(),
        currPage:$('#charge_off_table').getGridParam('page'),
        pageSize:$('#charge_off_table').getGridParam('rowNum'),
        search: $node.find("#_searchInput").val().trim(),
        sort:$('#charge_off_table').getGridParam("sortname"),
        filter:$('#charge_off_table').getGridParam("sortorder")
    }
}
function chargeOff(id){
   ajax_get("/member/ap/s/"+id+"/rdo","","",
        function(data){
            var aps = data.obj;
            var $node = $("#chargeOff");
            $node.data("id",id);
            $node.find("#_account").text(aps.account);
            $node.find("#_nickName").text(deal_with_illegal_value(aps.nickName));
            $node.find("#_saleMan").text(aps.saleMan);
            $node.find("#_dutyOfficer").text(deal_with_illegal_value(aps.dutyOfficer));
            $node.find("#_rechargeLeft").text(aps.rechargeLeft);
            var $createNode = $node.find("#bill_create");
            if(aps.hasBill){
                $createNode.attr("onclick","chargeOffRead("+id+");").text("查看本期应结款项");
            }else{
                $createNode.attr("onclick","chargeOffCreate();").text("生成本期应结款项");
            }
        }
    )
    $("#curr_all").prop("checked",false);
    $("#select_all").prop("checked",false);
    searchOrder();
    $("#follower").hide();
    $("#chargeOff").show();
}
function chargeOffBack(){
    $("#chargeOff").hide();
    $("#follower").show();
}
function getBill(id,callback){
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
//账单查看
function chargeOffRead(id){
    getBill(id,function(data){
        showBill(data)
    });
}

function chargeOffRecord(id){
     getBill(id,function(data){
        showBillRecord(data)
    });
}

function showBillRecord(data){
    generBillConten(data,function(content){
         layer.open({
            type: '1',
            area: ['760px','auto'],
            content: content,
            btn: ['确定']
        });
    })
}



//查看详情-table end
function chargeOffSetting() {
    var setting = {
        url:"/member/ap/o/rdls",
        datatype : "json", // 返回的数据类型
        ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
        mtype : "post", // 提交方式
        height : "auto", // 表格宽度
        width:1079,
        colNames:['','订单号', '支付时间', '订单金额（元）','账期付款金额（元）'],
        colModel:[
            {name:'screen',index:'', width:'10%',align:'center', sortable:false,
                formatter:function(cellvalue, options, rowObject){
                    return "<input "+($("#select_all").prop("checked")?"checked='checked' disabled":"")+" name='checkOne' data-id='"+rowObject.id+"' type='checkbox'/>";
                }
            },
            {name:'orderNo',index:'', width:'10%',align:'center', sortable:false},
            {name:'payDateStr',index:'pay_date', width:'9%',align:'center', sortable:true},
            {name:'orderAmount',index:'order_amount', width:'9%', align:"center", sortable:true},
            {name:'payAmount',index:'pay_amount', width:'9%', align:"center", sortable:true}
        ],
        viewrecords: true,//是否在浏览导航栏显示记录总数
        rowNum:10,//每页显示记录数
        rowList:[10,20,30],//用于改变显示行数的下拉列表框的元素数组。
        pagerpos : "center",
        pager:"#charge_off_pagination",//分页
        pgbuttons : true,
        loadtext: "加载中...",
        pgtext : "当前页 {0} 一共{1}页",
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
            return JSON.stringify(viewOrderParams());
        }
    };
    return setting;
}
//生成账单
function chargeOffCreate(){
    var $node = $("#chargeOff");
    var param = {
        isAll:$("#select_all").prop("checked"),
        id:$node.data("id"),
        std:$node.find("#_dateSearch").val(),
        search: $node.find("#_searchInput").val().trim()
    }
    if(!param.isAll){
        param.orderIds = [];
        $.each($("#chargeOff input[name=checkOne]:checked"),function(i,item){
            param.orderIds.push($(item).data("id"));
        });
        if(!param.orderIds.length){
            layer.msg("请选择订单",{icon:5,time:2000})
            return;
        }
    }
    ajax_post("/member/ap/b/cre",JSON.stringify(param),"application/json",
        function(response){
            if(response.code==100){
                layer.msg(response.msg,{icon:6,time:2000});
                showBill(response.obj);
                chargeOff(param.id);
            }else{
                layer.msg(response.msg,{icon:5,time:2000});
            }
        },
        function(e){
            console.log(e);
            layer.msg("生成账单异常",{icon:5,time:2000});
        }
    );
}

function showBill(data){
    generBillConten(data,function(content){
        var id = data.slave.id;
        layer.open({
            type: '1',
            area: ['760px','auto'],
            content: content,
            btn: ['重新选择核销订单','核销','取消'],
            btn1: function (index) {
                // TODO 删除账单 
                ajax_get("/member/ap/b/"+id+"/del","","",
                    function(response){
                        if(response.code == 100){
                            layer.close(index);
                            chargeOff(id);
                        }else{
                            layer.msg(response.msg,{icon:5,time:2000});
                        }
                    },function(e){
                        console.log(e);
                    }
                )
            },
            btn2: function (index) {
                ajax_get("/member/ap/b/"+id+"/cof","","",
                    function(data){
                        if(data.code == 100){
                            layer.close(index);
                            layer.msg(data.msg,{icon:6,time:2000}); 
                            searchAps();
                            $("#chargeOff").hide();
                            $("#follower").show();
                        }else{
                            layer.msg(data.msg,{icon:5,time:2000}); 
                        }
                    },function(e){
                        layer.msg("核销账期异常",{icon:5,time:2000});
                    }
                );
            }
        });     
    })
}

function generBillConten(data,call){
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