var layer = undefined;
var laypage = undefined;

function initParam(l,lp){
    layer = l;
    laypage = lp;
}

function showDistributes(obj){
    var popHtml=
        '<div id="distributor_list" class="distract_operation_pop structure_pop display">                                    '+
        '<div>                                                                                         '+
        '<input onkeyup="enter_keyUp(event,this, gain_distribution_list)" class="searchInput" placeholder="分销账号/名称/手机号"/>                               '+
        '<button class="searchButton">搜索</button>                                                    '+
        '</div>                                                                                        '+
        '<table class="operation_table">                                                               '+
        '<thead>                                                                                       '+
        '<tr>                                                                                          '+
        '<th style="width: 10%">勾选</th>                                                              '+
        '<th style="width: 35%">分销商账号</th>                                                        '+
        '<th style="width: 20%">名称</th>                                                              '+
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
        btn:['确定','取消'],
        title: '关联分销商',
        success: function(i,currdom) {
            var listPop = $('.distract_operation_pop');
            gain_distribution_list(1);
            listPop.show();
            //光联分销商弹出页面-------搜索分销商
            $(".distract_operation_pop").on("click", ".searchButton", function(){
                gain_distribution_list(1);
            });
        },
        yes: function(index) {
            var $checked = $("#distributor_list input[type='radio']:checked");
            var email = $checked.parent().siblings(".email").text();
            var nickName = $checked.parents().siblings(".nickName").text();
            var $node = $(obj).parents(".sendApply").find("input[name='selectedEmail']");
            $node.val(email);
            $("form").find("input[accesskey=_nick_name_]").val(nickName);
            layer.close(index);
        }
    });
}

function gain_distribution_list(curr) {
    var params = {
        role: 2,
        currPage: curr == undefined || curr == 0 ? 1 : curr,
        pageSize: 10,
        search: $(".distract_operation_pop .searchInput").val().trim(),
    };
    $.ajax({
        url: "../member/relatedMember",
        type: 'POST',
        data: JSON.stringify(params),
        contentType: "application/json",
        async: true,//是否异步
        dataType: 'json',
        success: function(data) {
            if (data) {
                if (data.mark == 2 || data.mark == 3) {
                    insert_distribution_list(data.data.list);
                    init_distribution_pagination(data.data);
                } else if (!data.suc) {
                    window.location.href = "login.html";
                } else if (data.mark == 1){
                    layer.msg("获取分销商失败", {icon : 2, time : 1000});
                }
            }
        },
        error: function(xhr, status) { console.log("error--->" + status); }
    });
}

function insert_distribution_list(list,type){
    $('.distract_operation_pop .operation_table').find("tbody").empty("tr");
    var itemHTML = '';
    $.each(list, function(i, item) {
        itemHTML +=
            '<tr>'+
            '<td style="width: 10%"><input style="cursor: pointer;"  '+(item.isFrozen?"disabled":"")+' name="distributor" type="radio"  value="' + item.id + '"></td>'+
            '<td style="width: 35%" class="email">' + deal_with_illegal_value(item.email)+(item.isFrozen?'【<b style="color: red;">冻结</b>】':"") + '</td>'+
            '<td style="width: 20%" class="nickName">' + deal_with_illegal_value(item.nick) + '</td>'+
            '<td style="width: 35%" class="distributorTel">' + deal_with_illegal_value(item.telphone) + '</td>'+
            '</tr>'
    });
    $('.distract_operation_pop .operation_table').find("tbody").append(itemHTML);
}

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
                    gain_distribution_list(obj.curr);
                }
            }
        });
    }
}

//提交申请
function submit_recharge(obj){
    var $sendApply = $(obj).parents(".sendApply");
    var type = $sendApply.attr("type");//1：线下充值  2；现金充值
    var options = {
        //url: url,                 //默认是form的action
        //type: type,               //默认是form的method（get or post）
        //dataType: 'json',           //html(默认), xml, script, json...接受服务端返回的类型
        clearForm: true,          //成功提交后，清除所有表单元素的值
        //resetForm: true,          //成功提交后，重置所有表单元素的值
        //target: '#output',          //把服务器返回的内容放入id为output的元素中
        //timeout: 3000,               //限制请求的时间，当请求大于3秒后，跳出请求
        //提交前的回调函数
        beforeSubmit: function(formData, jqForm, options){
            //formData: 数组对象，提交表单时，Form插件会以Ajax方式自动提交这些数据，格式如：[{name:user,value:val },{name:pwd,value:pwd}]
            //jqForm:   jQuery对象，封装了表单的元素
            //options:  options对象
            //比如可以再表单提交前进行表单验证
            var selectedEmail = $sendApply.find("input[name='selectedEmail']").val().trim();
            if (selectedEmail == "") {
                layer.msg("请选择分销商", {icon: 0, time: 2000});
                return false;
            }
            formData.push({"name":"selectedEmail",value:selectedEmail});
            if (type && type == 1) {
                var distributorName =  $sendApply.find("input[name='distributorName']").val().trim();
                if (distributorName == "") {
                    layer.msg("账户开户名不能为空", {icon: 0, time: 2000});
                    return false;
                }
                var transferNumber = $sendApply.find("input[name='transferNumber']").val().trim();
                if(!transferNumber){
                    layer.msg("付款流水号不能为空", {icon: 0, time: 2000});
                    return false;
                }
            }
            var transferAmount = $sendApply.find("input[name='transferAmount']").val().trim();
            if (!/^((\d{1})|([1-9]{1}\d+)|(\d{1}\.\d{1,2})|([1-9]{1}\d+\.\d{1,2}))$/.test(transferAmount)) {
                layer.msg("请输入正确格式的付款金额", {icon: 0, time: 2000});
                return false;
            }

            var $file = $sendApply.find("input[name='image']");
            if ($file.length > 0 && $file.val() != "") {
                var name = $file.val();
                if ($file[0].files[0].size > (2 * 1024 * 1024) ||
                    !(name.indexOf(".jpg") != -1 || name.indexOf(".bmp") != -1 || name.indexOf(".png") != -1)) {
                    layer.msg("付款截图只支持jpg、bmp、png三种格式，且大小不能大于2MB", {icon: 0, time: 2000});
                    return false;
                }
            }
        },
        //提交成功后的回调函数
        success: function(data,status,xhr,$form){
            if (data.success) {
                layer.msg("充值成功", {icon: 1, time: 3000});
                $sendApply.find("#preview").attr("src","");
            } else {
                layer.msg(data.msg, {icon: 5, time: 3000});
            }
        },
        error: function(xhr, status, error, $form){},
        complete: function(xhr, status, $form){}
    };
    $sendApply.ajaxSubmit(options);
}

function readImg(obj){
    preview(obj,$("#preview"));
}
