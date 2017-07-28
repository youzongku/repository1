//定义全局变量
var laypage = undefined;
var layer = undefined;
function init_bill_applys(l, lp){
    layer = l;
    laypage = lp;
}

function show_init_bills(obj,flag,lay){
    laypage = lay;

    var currPage = 1;
    var pageSize = 10;
    if(flag){
        currPage = obj.curr;
    }
    var param = {
        pageSize: pageSize,
        currPage: currPage,
        purpose: $("select[name='purpose']").val(),
        time : $("select[name='purpose_time']").val(),
        sources : 3  //写死为3 只取总记录
    };
    ajax_post("/member/getBills", JSON.stringify(param), "application/json",
        function(data) {
            if (data && data.success) {
                var bills = data.bills;
                initBills(bills);
            }else{
                window.location.href = "login.html";
            }
        },
        function(xhr, status) {
        }
    );
}

function initBills(bills){
    $(".translaste-list").remove();
    $('.nothing').remove();
    if(bills && bills.list.length > 0){
        var billHtml = "";
        var singleHtml = "";
        $.each(bills.list,function(i,bill){
            var credit = bill.creditLimitBalance;
            singleHtml = "<div class='translaste-list' >"+
                "<ul class='translaste-list-header'>" +
                "<li class='six-handle-li0'>"+
                "<span class='handle-plus' onclick='getBillsDetails(this,\""+bill.id+"\",\""+bill.serialNumber+"\","+bill.applyId+",\""+bill.purpose+"\")'>+</span>"+
                "</li>"+
                "<li class='six-handle-li4'>" + show(bill.serialNumber) +"</li>" +
                "<li class='six-handle-li3'>" + show(bill.purposeStr) +"</li>" +
                "<li class='six-handle-li2'>" + show(bill.paymentType) +"</li>" +
                "<li class='six-handle-li5'>" + show(bill.amount) +"</li>" +
                "<li class='six-handle-li6'>" + fmoney(bill.balance,2) +"</li>" +
                "<li class='six-handle-li6'>" + fmoney(credit?credit:0,2) +"</li>" +
                "<li class='six-handle-li1'>" + show(bill.create) +"</li>" +
                "</ul>"+
                "</div>";
            billHtml += singleHtml;
        });
        $(".six-handle-header").after(billHtml);
    }else{
        addLoadGif({
            togBox:".translaste-date",
            togPage:'#pagination_trans',
            hint: '<div class="nothing"><div>暂无交易记录~</div></div>'
        },true);
    }
    init_pagination_trans(bills.totalPage,bills.currPage);
    return creatLoading(true,theDiv);
}

//处理未定义的值、空值、空字符串
function show(value) {
    return (value == undefined || value == null || value == '') ? '——' : value;
}

//初始化分页栏
function init_pagination_trans(total,currPage) {
    scrollPosTop(); //返回顶部
    if ($("#pagination_trans")[0] != undefined) {
        $("#pagination_trans").empty();
        laypage({
            cont: 'pagination_trans',
            pages: total,
            curr: currPage,
            groups: 5,
            skin: 'yahei',
            first: '首页',
            last: '尾页',
            prev: '上一页',
            next: '下一页',
            skip: true,
            jump: function(obj, first) {
                //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                if(!first){
                    show_init_bills(obj,true,laypage);
                }
            }
        });
    }
}

//导出
function exportData(event){
    var headLi = $(".six-handle-header li");
    var headName = [];
    $.each(headLi,function(i,li){
        if(i>0){
           headName.push("header=" + $(li).attr("name"));
        }
    });
    headName.push("header=serialNumber");
    headName.push("purpose=" + $("select[name='purpose']").val());
    headName.push("time=" + $("select[name='purpose_time']").val());
    if(headName.length > 0 ){
        window.location.href = "/member/exportBills?" + headName.join("&");
    }
}
//检查账户是否被冻结
function bill_checkFrozen() {
    $.ajax({
        url : "/member/checkFrozen?"+(new Date()).getTime(),
        type : "GET",
        dataType : "json",
        async : true,
        contentType : "application/json",
        success : function(data) {
            if(data.code == 100){
                var result = data.obj.result;
                // var credits = data.credits;
                var acPeriond = data.acPeriond;
                  if(result.frozen){
                    //账户被冻结
                    // $(".personal").replaceWith("<div class='frozen'><p>您的账户已被冻结，请联系管理员！</p></div>");
                    $("button[tag=frozen]").show();
                }else{
                     $("button[tag=frozen]").hide();
                }
                //首次访问个人中心
                $("#balance").text(fmoney(result.balance,2));
                var nick = data.nick;
                $("#member").text(nick ? (nick + "("+result.email+")") : result.email);
                if(acPeriond){
                    $("#credit_limit").text(acPeriond.totalLimit);
                    $("#available_credit_limit").text(acPeriond.leftLimit);
                    $("#used_amount").text(acPeriond.usedLimit);
                    $("#startTime").text(acPeriond.startTimeStr);
                    $("#forRefundTime").text(acPeriond.contractPeriodDateStr);
                    $("#lastForRefundTime").text(acPeriond.redLineDateStr);
                    if(acPeriond.state == 4){
                        $("#disable-the").show();
                    }
                }
                $(".time-up:eq(1)").text(result.historys.lastLoginTime);
                $("td.user_name").text(data.nick == "" ? result.email : data.nick);
               /* if(credits[0] != null){
                    creditInformation(credits);
                }else{
                    $(".six-user .user-quota").find("li").eq(2).hide();
                    $($(".six-user .six-user-name")[1]).hide();
                    $(".six-user .limit-validity").hide();
                }*/
            }
        }
    });
}


//请求交易记录明细
function getBillsDetails(obj,id,serNO,applyId,purpose){
    var spanObj = $(obj);
    if(spanObj.html() == "+"){
        spanObj.html("-");
    }else{
        spanObj.html("+");
    }
    // //如果详情li已经存在，不再请求数据
    if($("#translaste-list_"+id).length > 0){
        $("#translaste-list_"+id).toggle();
        return ;
    }
    var currPage = 1;  //此分页参数用于构造请求参数，并非需要分页，目前记录条数不超过 10，并无影响
    var pageSize = 10;
    var param = {
        pageSize: pageSize,
        currPage: currPage,
        serialNumber:serNO, //操作凭证
        applyId:applyId,    //充值申请id
        purpose:purpose,
        son:true  //标识 交易记录子项
    };
    ajax_post("/member/getBills", JSON.stringify(param), "application/json",
        function(data) {
            if (data && data.success) {
                var bills = data.bills;
                 if(bills && bills.list.length > 0){
                    var billHtml = "<div class='translaste-list-body-div' id = 'translaste-list_"+id+"'>";
                    var singleHtml = "";
                    $.each(bills.list,function(i,bill){
                        singleHtml =   "<ul class='six-handle-header translaste-list-body'  >" +
                                       "<li class='six-handle-li0'>&nbsp;</li>"+
                                       "<li class='six-handle-li4'>&nbsp;</li>" +
                                       "<li class='six-handle-li3'>" + show(bill.purposeStr) +"</li>" +
                                       "<li class='six-handle-li2'>" + show(bill.paymentType) +"</li>" +
                                       "<li class='six-handle-li5'>" + show(bill.amount) +"</li>" +
                                       "<li class='six-handle-li6'>" + fmoney(bill.balance,2)+"</li>" +
                                       "<li class='six-handle-li6'>&nbsp;</li>" +
                                       "<li class='six-handle-li1'>" + show(bill.create) +"</li>" +
                                       "</ul>";
                        billHtml += singleHtml;
                    });
                    billHtml += "<div>";
                    spanObj.parent().parent().parent().append(billHtml);
                }else{
                    spanObj.html("+");
                    layer.msg("无交易记录明细",{icon:6,time:2000});
                }
            }else{
                window.location.href = "login.html";
            }
        },
        function(xhr, status) {
        }
    );
}