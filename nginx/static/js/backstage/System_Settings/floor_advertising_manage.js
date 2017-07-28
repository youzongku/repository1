/**
 * Created by Administrator on 2016/8/16.
 */
//楼层广告位管理
define("floor_advertising_manage", ["jquery", "layer", "laypage", "webuploader", "BbcGrid"], function ($, layer, laypage, WebUploader, BbcGrid) {
    var uploader_floor = undefined;

    function init() {
        var grid = new BbcGrid();
        grid.initTable($('#floor_advertising_manage_table'), getSetting_floor_advertising_manage());
        //init_webuploader_register_floor_advertising();
    }

    function getSetting_floor_advertising_manage() {
        var setting = {
            url:"../product/banner/getBannerInfo",
            ajaxGridOptions: { contentType: 'application/json; charset=utf-8' },
            rownumbers : true, // 是否显示前面的行号
            datatype : "json", // 返回的数据类型
            mtype : "get", // 提交方式
            height : "auto", // 表格宽度
            autowidth : true, // 是否自动调整宽度
            // styleUI: 'Bootstrap',
            colNames:["id","描述","缩略图","关联类目","链接","状态","操作"],
            colModel:[{name:"id",index:"id",width:"12%",align:"center",sortable:false,hidden:true},
                {name:"describe",index:"describe",width:"12%",align:"center",sortable:true,formatter:function(cellvalue,options,rowObject){
                    return deal_with_illegal_value(cellvalue)
                }},
                {name:"id",index:"id",width:"12%",align:"center",sortable:true,formatter:function(cellvalue,options,rowObject){
                    var random = "&abc"+new Date().getTime()+"="+new Date().getTime();
                    return '<span class="banner_smallPic">' + "<img src='../product/banner/getBannerImg?id=" + rowObject.id + random + "'>" + '</span>';
                }},
                {name:"categoryId",index:"category_id",width:"12%",align:"center",sortable:true,formatter:function(cellvalue,options,rowObject){
                    return formatterCategory(rowObject.parentId,cellvalue);
                }},
                {name:"relatedInterfaceUrl",index:"related_interface_url",width:"14%",align:"center",sortable:true},
                {name:"status",index:"status",width:"14%",align:"center",sortable:true,formatter:function(cellvalue, options, rowObject){
                    var content = '';
                    if (rowObject.status == 1) {
                        content += "<span class='start-using-btn'><i></i>启用中</span>";
                    } else {
                        content += "<span class='forbidden-btn'><i></i>禁用中</span>";
                    }
                    return content;
                }},
                {name:"id",index:"status",width:"12%",align:"center",sortable:false,formatter:function(cellvalue, options, rowObject){
                    return '<i class="compile-icon start_floor_advertising" id="'+ rowObject.id + '" data-parentid="'+ rowObject.parentId + '" data-categoryid="'+ rowObject.categoryId+ '"></i><i class="remove-icon floor_advertising_del" id="'+ rowObject.id +'"></i></div>';
                }}
            ],
            viewrecords : true,
            rowNum : 10,
            rowList : [ 10, 20, 30 ],
            pager:"#floor_advertising_manage_pagination",//分页
            caption:"楼层广告位管理",//表名称
            pagerpos : "center",
            pgbuttons : false,
            autowidth: true,
            rownumbers: true, // 显示行号
            loadtext: "加载中...",
            pgtext : "当前页 {0} 一共{1}页",
            jsonReader:{
                root: "list",  //数据模型
                page: "pageNo",//数据页码
                total: "totalPages",//数据总页码
                records: "totalCount",//数据总记录数
                repeatitems: true,//如果设为false，则jqGrid在解析json时，会根据name(colmodel 指定的name)来搜索对应的数据元素（即可以json中元素可以不按顺序）
                //cell: "cell",//root 中row 行
                id: "id"//唯一标识
            },
            serializeGridData : function() {
                return getSearchParam_floor_advertising_manage($(this).jqGrid('getGridParam', 'postData'));
            }
        };
        return setting;
    }

    function formatterCategory(parentId,categoryId) {
        if(!categoryId) {
            return "";
        }

        var parentCategoryName = '';
        var categoryName = '';
        $.ajax({
            url: "/product/api/cateDetail?vcId="+parentId,
            type: "get",
            contentType: "application/json",
            async:false,
            success: function (data) {
                parentCategoryName = data.name;
            }
        });

        $.ajax({
            url: "/product/api/cateDetail?vcId="+categoryId,
            type: "get",
            contentType: "application/json",
            async:false,
            success: function (data) {
                categoryName = data.name;
            }
        });

        return parentCategoryName + '-' + categoryName;
    }

    function getSearchParam_floor_advertising_manage(postData) {
        var params = {
            pageSize: $('#floor_advertising_manage_table').getGridParam('rowNum'),
            curr: $('#floor_advertising_manage_table').getGridParam('page'),
            status:0,
            type: 2,
            describe: $("#floor_advertising_manage_describe").val(),
            sord:postData.sord,
            sidx:postData.sidx
        };
        return params;
    }


    $('body').on("click", "#add_banner_floor", function () {
        var content = init_addBanner();
        layer.open({
            type: 1,
            skin: 'layui-layer-rim',
            area: ['630px', '430px'],
            content: content,//$('.banner_add_pop')
            btn: ['保存', '取消'],
            yes: function (index, layero) {
                if(uploader_floor.getFiles() == 0){
                    layer.msg("请选择图片！", {icon: 2})
                    return;
                }

                if(uploader_floor.getFiles()[0]._info.width > 258) {
                    layer.msg("图片宽度不能大于258px！", {icon: 2});
                    $('#fileList').empty();
                    uploader_floor.reset();
                    return;
                }

                if(uploader_floor.getFiles()[0]._info.height > 498) {
                    layer.msg("图片高度不能大于498px！", {icon: 2});
                    $('#fileList').empty();
                    uploader_floor.reset();
                    return;
                }

                if (isnull($("#relatedInterfaceUrl").val())) {
                    layer.msg("相关链接不能为空！", {icon: 2})
                    return;
                }
                var url = $("#relatedInterfaceUrl").val();
                var reg = new RegExp('^(http|https|ftp)\://[a-zA-Z0-9\-\.]+\.[a-zA-Z]{2,3}(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\-\._\?\,\'/\\\+&%\$#\=~])*$');
                if(!reg.test(url)){
                    layer.msg("请填写规范的URL信息", {icon: 2})
                    return;
                }
                if (isnull($("input[name='status']:checked").val())) {
                    layer.msg("请勾选状态！", {icon: 2})
                    return;
                }

                if ($("#categoryId").val() == 0) {
                    layer.msg("必须设置二级类目！", {icon: 2})
                    return;
                }

                uploader_floor.options.formData.sort = $("#sort").val();
                uploader_floor.options.formData.relatedInterfaceUrl = $("#relatedInterfaceUrl").val();
                uploader_floor.options.formData.status = $("input[name='status']:checked").val();
                uploader_floor.options.formData.describe = $("#describe").val();
                uploader_floor.options.formData.adType = $("#adType").val();
                uploader_floor.options.formData.categoryId = $("#categoryId").val();
                uploader_floor.options.formData.parentId = $("#parentId").val();
                uploader_floor.upload();
                $("#addBanner").html("");
                layer.close(index);
            }, btn2: function (index, layero) {
                $("#addBanner").html("");
                layer.close(index);
            },

            title: '添加楼层广告图',

            success: function() {
                init_upload_floor_advertising_manage();
                init_floor_advertising_select();
            }
        });
    })
//编辑
    $('body').on("click", ".start_floor_advertising", function () {
        var id = $(this).attr("id");
        var categoryId = $(this).data('categoryid');
        var parentId = $(this).data('parentid');


        // editBannerInit(id);
        layer.open({
            type: 1,
            skin: 'layui-layer-rim',
            area: ['630px', '430px'],
            content: init_addBanner(),//$('.banner_edit_pop')
            btn: ['保存', '取消'],
            yes: function (index, layero) {
                var sort = $("#sort").val();
                var relatedInterfaceUrl =$("#relatedInterfaceUrl").val();
                var status = $("input[name='status']:checked").val();
                var describe = $("#describe").val();

                var adType = $("#adType").val();
                var categoryId = $("#floor_advertising_select_second").val();
                var parentId = $("#floor_advertising_select_first").val();

                if (isnull(sort)) {
                    layer.msg("排序不能为空！", {icon: 2})
                    return;
                }
                if (isnull(relatedInterfaceUrl)) {
                    layer.msg("相关链接不能为空！", {icon: 2})
                    return;
                }
                var reg = new RegExp('^(http|https|ftp)\://[a-zA-Z0-9\-\.]+\.[a-zA-Z]{2,3}(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\-\._\?\,\'/\\\+&%\$#\=~])*$');
                if(!reg.test(relatedInterfaceUrl)){
                    layer.msg("请填写规范的URL信息", {icon: 2})
                    return;
                }
                if (isnull(status)) {
                    layer.msg("请勾选状态！", {icon: 2})
                    return;
                }

                if (!categoryId || categoryId == 0) {
                    layer.msg("必须设置二级类目！", {icon: 2})
                    return;
                }

                if(uploader_floor.getFiles() == 0){
                    $.ajax({
                        url: "../product/banner/upload",
                        type: "POST",
                        data: {
                            bId: id,
                            sort : sort,
                            relatedInterfaceUrl :relatedInterfaceUrl,
                            status : status,
                            describe : describe,
                            adType : adType,
                            categoryId : categoryId,
                            parentId:parentId
                        },
                        dataType: "json",
                        async: false,
                        success: function (response) {
                            layer.msg(response.msg)
                        }
                    })
                    //getAllBanner(1);
                    $("#floor_advertising_manage_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
                    $("#editBanner").html("");
                    layer.close(index);
                    return;
                }


                if(uploader_floor.getFiles()[0]._info.width > 258) {
                    layer.msg("图片宽度不能大于258px！", {icon: 2});
                    $('#fileList').empty();
                    uploader_floor.reset();
                    return;
                }

                if(uploader_floor.getFiles()[0]._info.height > 498) {
                    layer.msg("图片高度不能大于498px！", {icon: 2});
                    $('#fileList').empty();
                    uploader_floor.reset();
                    return;
                }

                uploader_floor.options.formData.bId = id;
                uploader_floor.options.formData.sort = sort;
                uploader_floor.options.formData.relatedInterfaceUrl = relatedInterfaceUrl;
                uploader_floor.options.formData.status = status;
                uploader_floor.options.formData.describe = describe;
                uploader_floor.options.formData.adType = adType;
                uploader_floor.options.formData.categoryId = categoryId;
                uploader_floor.options.formData.parentId = parentId;

                uploader_floor.upload();
                layer.close(index);
            },
            btn2: function (index, layero) {
                $("#editBanner").html("");
                layer.close(index);
            },
            title: '编辑',
            success: function() {
                init_floor_advertising_select(parentId);
                init_floor_advertising_select_second(parentId, categoryId);
                editBannerInit(id);
                init_upload_floor_advertising_manage();
            }
        });
    })

//删除
    $('body').on("click", ".floor_advertising_del", function () {
        var id = $(this).attr("id");
        if (isnull(id)) {
            layer.msg("id为空", {icon: 2});
            return;
        }
        layer.open({
            type: 1,
            skin: 'layui-layer-rim',
            area: ['320px', '180px'],
            content: $('.floor_advertising_del_pop'),
            btn: ['确定', '取消'],
            yes: function (index, layero) {
                $.ajax({
                    url: "../product/banner/deleteBanner",
                    type: "POST",
                    data: {id: id},
                    dataType: "json",
                    async: false,
                    success: function (response) {
                        layer.msg(response.msg, {icon: 1});
                        //getAllBanner(1);
                        $("#floor_advertising_manage_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
                        layer.close(index);
                    }

                })
            },
            title: '删除提示'
        });
    })

    function init_webuploader_register_floor_advertising() {
        WebUploader.Uploader.register(
            {
                "before-send-file": "beforeSendFile"
            },
            {
                "beforeSendFile": function(file) {
                    var task = WebUploader.Base.Deferred();
                    uploader_floor.md5File(file, 0, file.size)
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
                            console.log(uploader_floor.options);
                            uploader_floor.options.formData[file.id + "_md5"] = md5;
                            task.resolve();
                            console.log("beforeSendFile    [next step]");
                        });
                    return WebUploader.Base.when(task);
                }
            }
        );
    }

    function init_upload_floor_advertising_manage() {
        // 初始化Web Uploader
        uploader_floor = WebUploader.create({

            // 选完文件后，是否自动上传。
            auto: false,

            // swf文件路径
            swf: "../js/webuploader/Uploader.swf",

            // 文件接收服务端。
            server: "../product/banner/upload",

            // 选择文件的按钮。可选。
            // 内部根据当前运行是创建，可能是input元素，也可能是flash.
            pick: {id:"#filePicker",multiple:false},
            fileSizeLimit: 1 * 100 * 1024,
            // 只允许选择图片文件。
            accept: {
                title: 'Images',
                extensions: 'gif,jpg,jpeg,bmp,png',
                mimeTypes: 'image/!*',
            },
            method: 'POST',
            fileNumLimit : 1,
            compress: false
        });
        // 当有文件添加进来的时候
        uploader_floor.on('fileQueued', function (file) {
            var $li = $(
                    '<div id="' + file.id + '" class="file-item thumbnail">' +
                    '<img>' +
                    '<div class="info">' + file.name + '</div>' +
                    '</div>'
                ),
                $img = $li.find('img');


            // $list为容器jQuery实例
            $("#fileList").append($li);

            // 创建缩略图
            // 如果为非图片文件，可以不用调用此方法。
            // thumbnailWidth x thumbnailHeight 为 100 x 100
            uploader_floor.makeThumb(file, function (error, src) {
                if (error) {
                    $img.replaceWith('<span>不能预览</span>');
                    return;
                }

                $img.attr('src', src);
            }, 100, 100);

            uploader_floor.md5File( file )

                // 及时显示进度
                .progress(function(percentage) {
                    console.log('Percentage:', percentage);
                })

                // 完成
                .then(function(md5) {
                    uploader_floor.options.formData[file.id + "_md5"] = md5;
                });
        });


        //当某个文件上传到服务端响应后，会派送此事件来询问服务端响应是否有效
        uploader_floor.on('uploadAccept', function (object,ret) {
            if(ret.result) {
                layer.msg(ret.msg, {icon: 1});
                $("#floor_advertising_manage_table").jqGrid('setGridParam', {page:1}).trigger("reloadGrid");
            } else {
                layer.msg(ret.msg, {icon: 2});
            }
        });

        uploader_floor.on("error",function (type){
            if(type == "Q_EXCEED_SIZE_LIMIT"){
                layer.msg("上传文件大小不能超过100kb", {icon: 5})
            }
        });
    }

    //初始化一级类目
    function init_floor_advertising_select(categoryParentId) {
        //获取一级类目id
        $.ajax({
            url: "/product/api/vcQuery",
            type: "post",
            contentType: "application/json",
            data: JSON.stringify({"name":"bbc"}),
            async: true,
            success: function (data) {
                if(data.length > 0){
                    var parentId =  data[0].vcId;
                    getCategory(parentId,function(data){
                        if(data.length > 0){
                            var optionContent = '';
                            for(var index in data) {
                                if (categoryParentId && categoryParentId == data[index].vcId) {
                                    optionContent += '<option selected value="'+ data[index].vcId + '">'+ data[index].name + '</option>';
                                    $('#floor_advertising_select_first').val(data[index].vcId);
                                } else {
                                    optionContent += '<option value="'+ data[index].vcId + '">'+ data[index].name + '</option>';
                                }
                            }

                            $('#floor_advertising_select_first').append(optionContent);
                        }
                    })
                }
            }
        });
    }


    //初始化二级类目
    function init_floor_advertising_select_second(parentId,categoryId) {
        //获取二级类目id
        $.ajax({
            url: "/product/api/vcQueryAll",
            type: "post",
            contentType: "application/json",
            data: JSON.stringify({"parentId":parentId}),
            success: function (data) {
                console.log(data);
                if(data.length > 0){
                    var optionContent = '<option value="0">请选择二级类目</option>';
                    for(var index in data) {
                        if (categoryId && categoryId == data[index].vcId) {
                            optionContent += '<option selected value="'+ data[index].vcId + '">'+ data[index].name + '</option>';
                            $('#floor_advertising_select_second').val(data[index].vcId);
                        } else {
                            optionContent += '<option value="'+ data[index].vcId + '">'+ data[index].name + '</option>';
                        }
                    }
                    $('#floor_advertising_select_second').html(optionContent);
                }
            }
        });
    }

    //关联的一级类目
    $('body').on("change", "#floor_advertising_select_first", function () {
        var parentId = $(this).val();
        $('#parentId').val(parentId);

        if(parentId == 0) {
            $('#floor_advertising_select_second').html('<option value="0">请选择二级类目</option>');
        }

        $.ajax({
            url: "/product/api/vcQueryAll",
            type: "post",
            contentType: "application/json",
            data: JSON.stringify({"parentId":$(this).val()}),
            success: function (data) {
                console.log(data);
                if(data.length > 0){
                    var optionContent = '<option value="0">请选择二级类目</option>';
                    for(var index in data) {
                        //if (categoryId && categoryId == data[index].vcId) {
                        //    optionContent += '<option selected value="'+ data[index].vcId + '">'+ data[index].name + '</option>';
                        //    $('#categoryId').val(data[index].vcId);
                        //} else {
                        //    optionContent += '<option value="'+ data[index].vcId + '">'+ data[index].name + '</option>';
                        //}
                        optionContent += '<option value="'+ data[index].vcId + '">'+ data[index].name + '</option>';
                    }
                    $('#floor_advertising_select_second').html(optionContent);
                }
            }
        });
    });

    //关联的二级类目
    $('body').on("change", "#floor_advertising_select_second", function () {
        $('#categoryId').val($(this).val());
    });

    function editBannerInit(id) {
        $.ajax({
            url: "../product/banner/getBannerById",
            type: "POST",
            data: {id: id},
            dataType: "json",
            async: false,
            success: function (response) {
                if (response.status == 1) {
                    $("#tStatus").attr("checked", true);
                } else {
                    $("#fStatus").attr("checked", true);
                }
                $("#relatedInterfaceUrl").val(response.relatedInterfaceUrl);
                $("#describe").val(response.describe);
                $("#categoryId").val(response.categoryId);
                $("#parentId").val(response.parentId);
            }
        })

    }

    function init_addBanner() {
        var div = '<div class="banner_add_pop" id="addBanner" style="display: block;">';
        div += '<div><em>上传图片<b class="red">*</b>:</em>';
        div += '<div><div id="uploader-demo"><div id="fileList" class="uploader-list"></div><div id="filePicker">选择图片</div></div>';
        div += '<div class="bannerPic_size">建议尺寸(<span>宽:<em>258</em>px</span>&nbsp;&nbsp;<span>高:<em>498</em>px</span>)</div>';
        div += '</div></div>';
        div += '<div> <em>链接<b class="red">*</b>:</em><div><input type="text" id="relatedInterfaceUrl"/></div></div>'
        div +=  '<div style="margin-left:5px;"><em>关联类目<b class="red">*</b>:</em><div><select id="floor_advertising_select_first" style="width:130px"><option value="0">请先选择一级类目</option></select><select id="floor_advertising_select_second" style="width:120px;margin-left:10px"><option value="0">选择二级类目</option></select></div>';
        div += '</div><div><em>状态<b class="red">*</b>:</em><div>';
        div += '<label><input  type="radio" id="tStatus"  name="status" value="1">启用</label>';
        div += '<label><input type="radio" id="fStatus" name="status" value="2">禁用</label>';
        div += '<input type="hidden" id="adType" name="adType" value="2">';
        div += '<input type="hidden" id="categoryId" name="categoryId" value="0">';
        div += '<input type="hidden" id="parentId" name="parentId" value="0">';
        div += '<input type="hidden" id="sort" name="sort" value="0">';
        div += '</div> </div> <div> <em>描述:</em> <div> <textarea id="describe"></textarea> </div> </div>';
        div += '</div>';
        return div;
    }

    function init_editBanner() {
        var div = '<div class="banner_edit_pop" id="editBanner" style="display: block;">';
        div += '<div><em>上传图片<b class="red">*</b>:</em>';
        div += '<div><div id="uploader-demo"><div id="fileList" class="uploader-list"></div><div id="filePicker">选择图片</div></div>';
        div += '<div class="bannerPic_size">图片尺寸(<span>宽:<em>1040</em>px</span>&nbsp;&nbsp;<span>高:<em>350</em>px</span>)</div>';
        div += '</div></div><div> <em>排序<b class="red">*</b>:</em><div><input type="text" id="sort"/></div></div>';
        div += '<div><em>链接<b class="red">*</b>:</em><div><input type="text" id="relatedInterfaceUrl"/></div>';
        div += '</div><div><em>状态<b class="red">*</b>:</em><div>';
        div += '<label><input id="tStatus" type="radio" name="status" value="1">启用</label>';
        div += '<label><input id="fStatus" type="radio" name="status" value="2">禁用</label>';
        div += '</div> </div> <div> <em>描述:</em> <div> <textarea id="describe"></textarea> </div> </div>';
        div += '</div>';
        // $("#editBanner").html("");
        // $("#editBanner").html(div);
        return div;
    }

    function isnull(a) {
        if (a != "" && a != undefined) {
            return false;
        }
        return true
    }

    return {init:init};
});
