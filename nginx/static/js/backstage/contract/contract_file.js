var laypage = undefined;
var layer = undefined;
var grid = undefined;
var uploader = undefined;
var updateUploader = undefined;
var cno = undefined;
/*合同管理 start*/
function contractFile() {
    var setting = {
        url: "/product/contract/get",
        datatype: "json", // 返回的数据类型
        ajaxGridOptions: {contentType: 'application/json; charset=utf-8'},
        mtype: "post", // 提交方式
        height: "auto", // 表格宽度
        autowidth: true, // 是否自动调整宽度
        title: "合同文件管理",
        colNames: ['ID', '合同编码', '分销渠道', '分销商账号', '名称', '分销商手机', '合同期限', '业务员工号', '操作'],
        colModel: [
            {name: 'id', index: '', width: '10%', align: 'center', sortable: false, hidedlg: true, hidden: true},
            {name: 'contractNo', index: 'contract_no', width: '10%', align: 'center', sortable: true},
            {name: 'distributionMode', index: '', width: '12%', align: 'center', sortable: false},
            {name: 'account', index: '', width: '12%', align: 'center', sortable: false},
            {name: 'distributionName', index: '', width: '12%', align: "center", sortable: false},
            {name: 'phone', index: '', width: '12%', align: "center", sortable: false},
            {
                name: 'contractStart', index: 'contract_end', width: '14%', align: 'center', sortable: true,
                formatter: function (cellvalue, options, rowObject) {
                    return rowObject.contractStart + " 至 " + rowObject.contractEnd;
                }
            },
            {name: 'bussinessErp', index: '', width: '12%', align: 'center', sortable: false},
            {
                name: 'operation', index: '', width: '16%', align: "center", sortable: false,
                formatter: function (cellvalue, options, rowObject) {
                    return "<div class='operation-a'><a id='contractFileUpdateBtn' onclick='contractNovaBtn(" + options.rowId + ");'>更新合同</a><a onclick='checkAttachment(" + options.rowId + ");'>查看附件</a><a onclick='operationLog(\"" + rowObject.contractNo + "\");'>操作日志</a></div>";
                }
            }
        ],
        viewrecords: true,//是否在浏览导航栏显示记录总数
        rowNum: 15,//每页显示记录数
        loadtext: "加载中...",
        rowList: [15, 20, 25],//用于改变显示行数的下拉列表框的元素数组。
        pager: "contract_pagination",
        pgbuttons : true,
        rownumbers: true, // 显示行号
        pgtext : "当前页 {0} 一共{1}页",
        serializeGridData: function (postData) {
            return JSON.stringify(postData);
        },
        jsonReader: {
            root: "data.result",  //数据模型
            page: "data.currPage",//数据页码
            total: "data.totalPage",//数据总页码
            records: "data.rows",//数据总记录数
            repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
            //cell: "cell",//root 中row 行
            id: "id"//唯一标识
        }
    };
    return setting;
}

function models(lp, l, g) {
    layer = l;
    laypage = lp;
    grid = g;
    ajax_get("/member/getMode?" + Math.random(), {}, undefined,
        function (data) {
            var select = $("#models");
            select.empty();
            var optionHTML = '<option  value = "">所有分销渠道</option>';
            $.each(data, function (i, item) {
                optionHTML += '<option  value = "' + item.id + '">' + item.disMode + '</option>';
            });
            select.append(optionHTML);
        }, function (e) {
        }
    );
    $("#search_").keydown(function (event) {
        var key = event.keyCode || event.which;
        if (key == 13) {
            reloadContractTable();
        }
    });
    $("#search_disturb_input").keydown(function (event) {
        var key = event.keyCode || event.which;
        if (key == 13) {
            getHtmlCode(1);
        }
    });
}

function contractFileEnter() {
    $("#contractFile").hide();
    $("#contractFileEnter").show();
    $('#contract_create_form').find("input[name=size]").val(0);
    $('#contract_create_form').find("input[name=cno]").val('');
}
function addRow() {
    $(".contract-upload").append('<ul class="upload-attachment"><li style="width: 418px;"><input name="files" type="file"/></li><li><i class="add-row" onclick="addRow();">+</i><i class="remove-row" onclick="removeRow(this);">x</i></li></ul>');
}
function removeRow(obj) {
    $(obj).parent().parent().remove();
}
function returnButton() {
    reloadContractTable();
    $("#contractFile").show();
    $("#contractFileEnter").hide();
    $("#contractNovation").hide();
    $("#checkAttachment").hide();
}
function updateContract() {
    var options = {
        dataType: 'json',           //html(默认), xml, script, json...接受服务端返回的类型
        clearForm: true,          //成功提交后，清除所有表单元素的值
        resetForm: false,          //成功提交后，重置所有表单元素的值
        timeout: 3000,               //限制请求的时间，当请求大于3秒后，跳出请求
        //提交前的回调函数
        beforeSubmit: function (formData, jqForm, options) {
            var vali = validate(jqForm);
            if (!vali.result) {
                layer.msg(vali.msg, {icon: 5, time: 3000});
                return false;
            }
        },
        //提交成功后的回调函数
        success: function (data, status, xhr, $form) {
            if (data.suc) {
                layer.msg('更新合同成功，正在上传附件！', {icon: 1, time: 3000});
                if(updateUploader.getFiles().length > 0) {
                    cno = data.cno;
                    $('#contract_update_form').find("input[name=cno]").val(data.cno);
                    updateUploader.upload();
                }
            } else {
                layer.msg(data.msg, {icon: 5, time: 3000});
            }
        },
    };
    $("#contract_update_form").ajaxSubmit(options);
}
function saveContract() {
    var options = {
        dataType: 'json',           //html(默认), xml, script, json...接受服务端返回的类型
        clearForm: true,          //成功提交后，清除所有表单元素的值
        resetForm: false,          //成功提交后，重置所有表单元素的值
        timeout: 3000,               //限制请求的时间，当请求大于3秒后，跳出请求
        //提交前的回调函数
        beforeSubmit: function (formData, jqForm, options) {
            var vali = validate(jqForm, true);
            if (!vali.result) {
                layer.msg(vali.msg, {icon: 5, time: 3000});
                return false;
            }
        },
        //提交成功后的回调函数
        success: function (data, status, xhr, $form) {
            if (data.suc) {
                $("#contract_file_table").resetForm();
                layer.msg('录入合同成功，正在上传附件！', {icon: 1, time: 3000});
                if(uploader.getFiles().length > 0) {
                    cno = data.cno;
                    $('#contract_create_form').find("input[name=cno]").val(data.cno);
                    uploader.upload();
                }
            } else {
                layer.msg(data.msg, {icon: 5, time: 3000});
            }
        },
    };
    $("#contract_create_form").ajaxSubmit(options);
}

var types = ['.jpg', '.png', '.gif', '.pdf', '.doc', '.docx', '.xls', '.xlsx', '.txt'];
function validate(form, flag) {
    if (flag) {
        var email = form.find("input[name=email]").val();
        if (!email) {
            return {result: false, msg: "请选择分销商账号"};
        }
        var bussiness = form.find("input[name=bussiness]").val();
        if (!bussiness) {
            return {result: false, msg: "业务员工号不能为空"};
        }
    }
    var start = form.find("input[name=start]").val();
    if (!start) {
        return {result: false, msg: "合同开始时间不能为空"};
    }
    var end = form.find("input[name=end]").val();
    if (!end) {
        return {result: false, msg: "合同结束时间不能为空"};
    }
    //var files = form.find("input[name=files]");
    //if (files && files.length > 0) {
    //    var fix = "";
    //    var name = "";
    //    var obj = {};
    //    var flag = true;
    //    $.each(files, function (i, item) {
    //        name = $(item).val();
    //        fix = name.substr(name.lastIndexOf("."));
    //        if (!types.includes(fix)) {
    //            obj.result = false;
    //            obj.msg = "不支持的上传类型:" + name;
    //            flag = false;
    //            return false;
    //        }
    //    });
    //    if (!flag) {
    //        return obj;
    //    }
    //}
    return {result: true};
}

function contractList() {
    $("#contractFile").show();
    $("#contractFileEnter").hide();
    $("#contractNovation").hide();
}
function trimValue(obj){
    return obj ? obj.trim() : "";
}
function reloadContractTable() {
    var param = {
        search: trimValue($("#search_").val()),
        start: $("#start_").val(),
        end: $("#end_").val(),
        model: $("#models").val()
    };
    $("#contract_file_table").jqGrid('setGridParam', {page: 1, postData: param}).trigger('reloadGrid');
}
/*合同管理 end*/

/*更新合同 start*/
function contractNovation() {
    var setting = {
        datatype: "local",
        height: "auto", // 表格宽度
        width: 1027,
        title: "更新合同",
        colNames: ['分销渠道', '分销商账号', '名称', '分销商手机', '业务员工号'],
        colModel: [
            {name: 'distributionMode', index: '', width: '20%', align: 'center', sortable: false},
            {name: 'account', index: '', width: '20%', align: 'center', sortable: false},
            {name: 'distributionName', index: '', width: '20%', align: "center", sortable: false},
            {name: 'phone', index: '', width: '20%', align: "center", sortable: false},
            {name: 'bussinessErp', index: '', width: '20%', align: 'center', sortable: false}
        ],
        viewrecords: true,//是否在浏览导航栏显示记录总数
        rowNum: 15,//每页显示记录数
        rowList: [15, 20, 25]//用于改变显示行数的下拉列表框的元素数组。
    };
    return setting;
}
function contractNovaBtn(rowId) {
    var rowObject = $("#contract_file_table").jqGrid('getRowData', rowId);
    grid.initTable($("#contract_novation_table"), contractNovation());
    $("#contract_novation_table").clearGridData();
    $("#contract_novation_table").jqGrid("addRowData", 1, rowObject);
    var d = rowObject.contractStart.split(" 至 ");
    $("#contract_update_form").find("input[name=contractNo]").val(rowObject.contractNo);
    $("#contract_update_form").find("input[name=start]").val(d[0]);
    $("#contract_update_form").find("input[name=cid]").val(rowObject.id);
    $("#contract_update_form").find("input[name=end]").val(d[1]);
    $("#contractFile").hide();
    $("#contractNovation").show();
}
function returnButton2() {
    $("#contractFile").show();
    $("#contractNovation").hide();
    $("#checkAttachment").hide();
}
/*更新合同 end*/

/*查看附件 start*/
function checkAttachment(rowId) {
    $("#contractFile").hide();
    $("#checkAttachment").show();
    var rowObject = $("#contract_file_table").jqGrid('getRowData', rowId);
    var cno = rowObject.contractNo;
    $("#download_attachment").show();
    $("#download_attachment").attr("href", "/product/attachment/download?cno=" + cno + "&" + (new Date()).getTime());
    $("#file_list").empty();
    ajax_get("/product/contract/attachment?cno=" + cno, "", "application/json",
        function (datad) {
            var htmlCode = "";
            if (datad && datad.length > 0) {
                var type = '';
                $.each(datad, function (i, item) {
                    type = item.fileType;
                    if (type == 'pdf') {
                        htmlCode += '<li>' +
                            '<div class="check-att-pic check-att-pic-pdf">' +
                            '<div class="check-att-pic-float">' +
                            '<a onclick="remove(this,' + item.id + ');">删除</a>' +
                            '</div>' +
                            '</div>' +
                            '<div class="check-att-title">' + item.fileName + '</div>' +
                            '</li>';
                    } else if (type == 'doc' || type == 'docx' || type == 'xls' || type == 'xlsx' || type == 'txt') {
                        htmlCode += '<li>' +
                            '<div class="check-att-pic check-att-pic-word">' +
                            '<div class="check-att-pic-float">' +
                            '<a onclick="remove(this,' + item.id + ');">删除</a>' +
                            '</div>' +
                            '</div>' +
                            '<div class="check-att-title">' + item.fileName + '</div>' +
                            '</li>';
                    } else {
                        htmlCode += '<li>' +
                            '<div class="check-att-pic">' +
                            '<img src="/product/attachment/view?aid=' + item.id + '" alt="picture"/>' +
                            '<div class="check-att-pic-float">' +
                            '<a onclick="previewImg2(this);">预览</a>' +
                            '<a onclick="remove(this,' + item.id + ');">删除</a>' +
                            '</div>' +
                            '</div>' +
                            '<div class="check-att-title">' + item.fileName + '</div>' +
                            '</li>';
                    }
                });
            } else {
                $("#download_attachment").hide();
            }
            $("#file_list").prepend(htmlCode);
        },
        function (xhr, status) {
        }
    );


}


/*查看附件 end*/

/*操作日志 start*/
function operationLog(cno) {
    layer.open({
        type: 1,
        title: "操作日志",
        content: $("#operationLog"),
        area: ['540px', '460px'],
        btn: ["关闭"],
        shadeClose: false,
        success: function () {
            showContractRecord(cno);
        }
    })
}
function showContractRecord(cno) {
    $("#operationLog").empty();
    ajax_get("/product/contract/oprecord?cno=" + cno, "", "application/json",
        function (datad) {
            var htmlCode = "";
            if (datad && datad.length > 0) {
                $.each(datad, function (i, item) {
                    htmlCode += '<div class="log-pop-con">' +
                        '<span>' + item.opdateStr + '</span>' +
                        '<span>' + item.opuser + ':' + item.comment + '</span>' +
                        '</div>';
                });
            } else {
                htmlCode += '<span>暂无历史操作记录</span>';
            }
            $("#operationLog").prepend(htmlCode);
        },
        function (xhr, status) {
        }
    );
}
/*操作日志 end*/

/*预览 start*/
function previewImg2(obj) {
    var src = $(obj).parent().siblings("img").attr("src");
    layer.open({
        type: 1,
        title: false,
        area: ['800px', '500px'],
        content: '<div class="banner_add_pop big_img_pop" id="addBanner"><img src="' + src + '"></div>'
    });
    $(".layui-layer-content").css("padding", "10px")
}
/*预览 end*/

/*删除 start*/
function remove(obj, aid) {
    layer.confirm("您确定要删除吗？", {icon: 3},
        function () {
            $(obj).parent().parent().parent().remove();
            removeAttachment(aid);
            layer.closeAll();
        }
    );
}
function removeAttachment(aid) {
    $.ajax({
        url: "/product/attachment/delete",
        type: 'POST',
        data: JSON.stringify({attaIds: aid}),
        contentType: "application/json",
        async: true,//是否异步
        dataType: 'json',
        success: function (response) {
            layer.msg(response.msg, {icon: 1, time: 2000});
        }
    });
}
/*删除 end*/

/*选择分销商 start*/
function disturb_choice_pop_div() {
    getHtmlCode(1);
    layer.open({
        type: 1,
        title: "选择分销商",
        area: ['660px', '540px'],
        content: $("#disturb_choice_pop_div"),
        btn: ["确定", "取消"],
        yes: function (i, currdom) {
            var node = $("#member td").find("input[type='radio']:checked");
            $("#contract_create_form input[name=email]").val(node.attr("email"));
            $("#contract_create_form input[name=bussiness]").val(node.attr("bussiness"));
            layer.close(i);
        }
    });
}
function getHtmlCode(curr) {
    var params = {
        currPage: (curr == undefined || curr == 0) ? 1 : curr, pageSize: 10,
        search: trimValue($("#search_disturb_input").val()), notType: 3//分销商不为内部分销商
    };
    ajax_post_sync("/member/relatedMember", JSON.stringify(params), "application/json", function (data) {
        if (data) {
            if (data.mark == 2 || data.mark == 3) {
                var itemHTML = '';
                $.each(data.data.list, function (i, item) {
                    itemHTML +=
                        '<tr>' +
                        '<td style="width: 10%">' +
                        '<input style="cursor: pointer" '+(item.isFrozen?"disabled":"")+' type="radio" name="email" email="' + item.email + '" bussiness="' + deal_with_illegal_value(item.salesmanErp) + '">' +
                        '</td>' +
                        '<td style="width: 35%">' + deal_with_illegal_value(item.email) + (item.isFrozen?'【<b style="color: red;">冻结</b>】':"") +'</td>' +
                        '<td style="width: 20%">' + deal_with_illegal_value(item.nick) + '</td>' +
                        '<td style="width: 35%">' + deal_with_illegal_value(item.telphone) + '</td>' +
                        '</tr>'
                });
                $("#member").empty();
                $("#member").prepend(itemHTML);
                pages(data.data);
            } else if (!data.suc) {
                window.location.href = "login.html";
            } else if (data.mark == 1) {
                layer.msg("获取分销商失败", {icon: 5, time: 1000});
            }
        }
    });
}
function pages(page) {
    $("#distributions_pagination").empty();
    laypage({
        cont: 'distributions_pagination',
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
                getHtmlCode(obj.curr);
            }
        }
    });
}

define("contract_file", ["jquery",  "webuploader","md5"], function ($, WebUploader,md5) {
    $('body').on('click', '#contractFileEnterBtn', function(){
        $('#uploader').empty();
        $('#uploader').append('<div id="picker">选择文件</div><ul id="theList"></ul>')
        var userInfo = {userId:"kazaff", md5:""};   //用户会话信息
        var chunkSize = 2000 * 1024;        //分块大小
        var uniqueFileName = null;          //文件唯一标识符
        var md5Mark = null;

        var backEndUrl = '/product/contract/uploadContractAttachment';

        WebUploader.Uploader.register({
            "before-send-file": "beforeSendFile"
            , "before-send": "beforeSend"
            , "after-send-file": "afterSendFile"
        }, {
            beforeSendFile: function(file){
                //秒传验证
                var task = new $.Deferred();
                var start = new Date().getTime();
                (new WebUploader.Uploader()).md5File(file, 0, 10*1024*1024).progress(function(percentage){
                    console.log(percentage);
                }).then(function(val){
                    console.log("总耗时: "+((new Date().getTime()) - start)/1000);

                    md5Mark = val;
                    uniqueFileName = val;
                    userInfo.md5 = val;

                    $.ajax({
                        type: "POST"
                        , url: backEndUrl
                        , data: {
                            status: "md5Check"
                            , md5: val
                            , fileName:file.name
                            , cno:$('#contract_create_form').find("input[name=cno]").val()
                        }
                        , cache: false
                        , timeout: 1000
                        , dataType: "json"
                    }).then(function(data, textStatus, jqXHR){
                        if(data.ifExist){   //若存在，这返回失败给WebUploader，表明该文件不需要上传
                            task.reject();

                            uploader.skipFile(file);
                            file.path = data.path;
                            UploadComlate(file);
                        }else{
                            task.resolve();
                        }
                    }, function(jqXHR, textStatus, errorThrown){    //任何形式的验证失败，都触发重新上传
                        task.resolve();
                    });
                });
                return $.when(task);
            }
            , beforeSend: function(block){
                //分片验证是否已传过，用于断点续传
                var task = new $.Deferred();
                $.ajax({
                    type: "POST"
                    , url: backEndUrl
                    , data: {
                        status: "chunkCheck"
                        , fileName: block.file.fileName
                        , name: uniqueFileName
                        , chunkIndex: block.chunk
                        , size: block.end - block.start
                    }
                    , cache: false
                    , timeout: 1000
                    , dataType: "json"
                }).then(function(data, textStatus, jqXHR){
                    if(data.ifExist){   //若存在，返回失败给WebUploader，表明该分块不需要上传
                        task.reject();
                    }else{
                        task.resolve();
                    }
                }, function(jqXHR, textStatus, errorThrown){    //任何形式的验证失败，都触发重新上传
                    task.resolve();
                });

                return $.when(task);
            }
            , afterSendFile: function(file){
                var chunksTotal = 0;
                if((chunksTotal = Math.ceil(file.size/chunkSize)) > 1){
                    //合并请求
                    var task = new $.Deferred();
                    $.ajax({
                        type: "POST"
                        , url: backEndUrl
                        , data: {
                            status: "chunksMerge"
                            , cno:$('#contract_create_form').find("input[name=cno]").val()
                            , fileName: file.name
                            , name: uniqueFileName
                            , chunks: chunksTotal
                            , ext: file.ext
                            , md5: md5Mark
                        }
                        , cache: false
                        , dataType: "json"
                    }).then(function(data, textStatus, jqXHR){

                        //todo 检查响应是否正常

                        task.resolve();
                        file.path = data.path;
                        UploadComlate(file);

                    }, function(jqXHR, textStatus, errorThrown){
                        task.reject();
                    });

                    return $.when(task);
                }else{
                    UploadComlate(file);
                }
            }
        });

        uploader = WebUploader.create({
            swf: "../js/webuploader/Uploader.swf"
            , server: backEndUrl
            , pick: "#picker"
            , resize: false
            , dnd: "#theList"
            , paste: document.body
            , disableGlobalDnd: true
            , thumb: {
                width: 100
                , height: 100
                , quality: 70
                , allowMagnify: true
                , crop: true
                //, type: "image/jpeg"
            }
            ,accept: {//指定接受哪些类型的文件
                title: 'filetype',    //文字描述
                extensions: 'jpg,png,gif,pdf,doc,docx,xls,xlsx,txt',
                mimeTypes:'image/*,application/pdf,application/zip,application/msword,application/excel,application/pdf'
            }
            , compress: false
            , prepareNextFile: true
            , chunked: true
            , chunkSize: chunkSize
            , threads: true
            , formData: function(){return $.extend(true, {}, {md5:uniqueFileName,cno: $('#contract_create_form').find("input[name=cno]").val()});}
            , fileNumLimit: 10
            , fileSingleSizeLimit: 1000 * 1024 * 1024
            , duplicate: true
        });

        uploader.on("fileQueued", function(file){
            console.log('fileQueued');
            $('#contract_create_form').find("input[name=size]").val(uploader.getFiles().length);
            $("#theList").append( "<div id='"+  file.id + "' class='item'>" +
                "<h4 class='info'>" + file.name + "</h4>" +
            "<p class='percentage'>等待上传...</p>" +
                "</div>" );
        });

        uploader.on('uploadFinished', function () {
            console.log('uploadFinished');
            reloadContractTable();
            contractList();
        });

        uploader.on("uploadProgress", function(file, percentage){
            console.log('uploadProgress-----------------');
            $("#" + file.id + " .percentage").text(percentage * 100 + "%");
        });

        uploader.onError = function( code ) {
            console.log(code);
            if(code == "F_DUPLICATE"){
                layer.msg("请不要重复选择文件！", {icon : 2, time : 2000});
            } else if(code == 'Q_TYPE_DENIED') {
                layer.msg("附件只支持.jpg, .png, .gif, .pdf, .doc, .docx, .xls, .xlsx, .txt格式!", {icon : 2, time : 2000});
            }
        };

        function UploadComlate(file){
            console.log(file);

            $("#" + file.id + " .percentage").text("上传完毕");
            $(".itemStop").hide();
            $(".itemUpload").hide();
            $(".itemDel").hide();
        }
    });

    $('body').on('click', '#contractFileUpdateBtn', function(){
        $('#updateUploader').empty();
        $('#updateUploader').append('<div id="updatePicker">选择文件</div><ul id="UpdateList"></ul>')
        var userInfo = {userId:"kazaff", md5:""};   //用户会话信息
        var chunkSize = 2000 * 1024;        //分块大小
        var uniqueFileName = null;          //文件唯一标识符
        var md5Mark = null;

        var backEndUrl = '/product/contract/uploadContractAttachment';

        WebUploader.Uploader.register({
            "before-send-file": "beforeSendFile"
            , "before-send": "beforeSend"
            , "after-send-file": "afterSendFile"
        }, {
            beforeSendFile: function(file){
                //秒传验证
                var task = new $.Deferred();
                var start = new Date().getTime();
                (new WebUploader.Uploader()).md5File(file, 0, 10*1024*1024).progress(function(percentage){
                    console.log(percentage);
                }).then(function(val){
                    console.log("总耗时: "+((new Date().getTime()) - start)/1000);

                    md5Mark = val;
                    uniqueFileName = val;
                    userInfo.md5 = val;

                    $.ajax({
                        type: "POST"
                        , url: backEndUrl
                        , data: {
                            status: "md5Check"
                            , md5: val
                            , fileName:file.name
                            , cno:$('#contract_update_form').find("input[name=cno]").val()
                        }
                        , cache: false
                        , timeout: 1000
                        , dataType: "json"
                    }).then(function(data, textStatus, jqXHR){
                        if(data.ifExist){   //若存在，这返回失败给WebUploader，表明该文件不需要上传
                            task.reject();

                            updateUploader.skipFile(file);
                            file.path = data.path;
                            UploadComlate(file);
                        }else{
                            task.resolve();
                        }
                    }, function(jqXHR, textStatus, errorThrown){    //任何形式的验证失败，都触发重新上传
                        task.resolve();
                    });
                });
                return $.when(task);
            }
            , beforeSend: function(block){
                //分片验证是否已传过，用于断点续传
                var task = new $.Deferred();
                $.ajax({
                    type: "POST"
                    , url: backEndUrl
                    , data: {
                        status: "chunkCheck"
                        , fileName: block.file.fileName
                        , name: uniqueFileName
                        , chunkIndex: block.chunk
                        , size: block.end - block.start
                    }
                    , cache: false
                    , timeout: 1000
                    , dataType: "json"
                }).then(function(data, textStatus, jqXHR){
                    if(data.ifExist){   //若存在，返回失败给WebUploader，表明该分块不需要上传
                        task.reject();
                    }else{
                        task.resolve();
                    }
                }, function(jqXHR, textStatus, errorThrown){    //任何形式的验证失败，都触发重新上传
                    task.resolve();
                });

                return $.when(task);
            }
            , afterSendFile: function(file){
                var chunksTotal = 0;
                if((chunksTotal = Math.ceil(file.size/chunkSize)) > 1){
                    //合并请求
                    var task = new $.Deferred();
                    $.ajax({
                        type: "POST"
                        , url: backEndUrl
                        , data: {
                            status: "chunksMerge"
                            , cno:$('#contract_update_form').find("input[name=cno]").val()
                            , fileName: file.name
                            , name: uniqueFileName
                            , chunks: chunksTotal
                            , ext: file.ext
                            , md5: md5Mark
                        }
                        , cache: false
                        , dataType: "json"
                    }).then(function(data, textStatus, jqXHR){

                        //todo 检查响应是否正常

                        task.resolve();
                        file.path = data.path;
                        UploadComlate(file);

                    }, function(jqXHR, textStatus, errorThrown){
                        task.reject();
                    });

                    return $.when(task);
                }else{
                    UploadComlate(file);
                }
            }
        });

        updateUploader = WebUploader.create({
            swf: "../js/webuploader/Uploader.swf"
            , server: backEndUrl
            , pick: "#updatePicker"
            , resize: false
            , dnd: "#UpdateList"
            , paste: document.body
            , disableGlobalDnd: true
            , thumb: {
                width: 100
                , height: 100
                , quality: 70
                , allowMagnify: true
                , crop: true
                //, type: "image/jpeg"
            }
            ,accept: {//指定接受哪些类型的文件
                title: 'filetype',    //文字描述
                title: 'filetype',    //文字描述
                extensions: 'jpg,png,gif,pdf,doc,docx,xls,xlsx,txt',
                mimeTypes:'image/*,application/pdf,application/zip,application/msword,application/excel,application/pdf'
            }
            , compress: false
            , prepareNextFile: true
            , chunked: true
            , chunkSize: chunkSize
            , threads: true
            , formData: function(){return $.extend(true, {}, {md5:uniqueFileName,cno: $('#contract_update_form').find("input[name=cno]").val()});}
            , fileNumLimit: 10
            , fileSingleSizeLimit: 1000 * 1024 * 1024
            , duplicate: true
        });

        updateUploader.on("fileQueued", function(file){
            console.log('fileQueued');
            $('#contract_update_form').find("input[name=size]").val(updateUploader.getFiles().length);
            $("#UpdateList").append( "<div id='"+  file.id + "' class='item'>" +
                "<h4 class='info'>" + file.name + "</h4>" +
                "<p class='percentageUpdate'>等待上传...</p>" +
                "</div>" );
        });

        updateUploader.on('uploadFinished', function () {
            console.log('uploadFinished');
            reloadContractTable();
            contractList();
        });

        updateUploader.on("uploadProgress", function(file, percentage){
            console.log('uploadProgress-----------------');
            $("#" + file.id + " .percentageUpdate").text(percentage * 100 + "%");
        });

        updateUploader.onError = function( code ) {
            console.log(code);
            if(code == "F_DUPLICATE"){
                layer.msg("请不要重复选择文件！", {icon : 2, time : 2000});
            } else if(code == 'Q_TYPE_DENIED') {
                layer.msg("附件只支持.jpg, .png, .gif, .pdf, .doc, .docx, .xls, .xlsx, .txt格式!", {icon : 2, time : 2000});
            }
        };


        function UploadComlate(file){
            console.log(file);

            $("#" + file.id + " .percentage").text("上传完毕");
            $(".itemStop").hide();
            $(".itemUpload").hide();
            $(".itemDel").hide();
        }
    });
});
/*选择分销商 end*/
