define("alterinfo", ["jquery", "layer", "webuploader"], function($, layer, WebUploader) {

    //修复layer弹出框位置偏下问题
    function fix_layer_css() {
        $($(".layui-layer")[0]).css({"top" : (parseInt($($(".layui-layer")[0]).offset().top)/2)});
    };

    //获取用户资料
    function init_user_info() {
    	//init_uploader();
        $("#profile").focus(function(e) {profile_count();});
        $("#profile").keyup(function(e) {profile_count();});
        $("a.change-msg").click(function(e) {save_alter_info();});
        ajax_get("/member/infor?" + Math.random(), {}, undefined,
            function(response) {
                if (response.id != undefined) {
                    var nickname = response.nickName;
                    $("input[name='id']").val(response.id);
                    $("input[name='headImg']").val(response.headImg);
                    $("input[name='nickName']").val(nickname);
                    if(nickname) {
                        $("input[name='nickName']").prop("readOnly",true);
                    }
                    $("input[name='realName']").val(response.realName);
                    if (response.gender != undefined && response.gender != "") {
                        $("input[name='gender']").eq(
                            response.gender == "0" ? 0 : (response.gender == "1" ? 1 : 2)
                        )[0].checked = true;
                    }
                    $("#birthday").val(response.birthday);
                    $("#alter_info_form").find("tr").eq(4).children().eq(1).text(response.email);
                    $("input[name='telphone']").val(response.tel);
                    var email = response.email;
                    //判断是否是手机账号
                    var telFlag= checkTel(email);
                    if(telFlag){
                        $(".change-phone").data("email",email).attr("href","javascript:void(0);").click(function(){
                            alter_tel($(this).data("email"));
                        });
                    }
                    $("#registerInviteCode").text(deal_with_illegal2NotSet(response.registerInviteCode));
                    // $("input[name='registerInviteCode']").val(response.registerInviteCode);
                    $("#alter_info_form").find("tr").eq(7).children().eq(1).text(response.selfInviteCode);
                    $("#profile").val(response.profile);
                    init_birth_select();
                } else if (response.errorCode == "2") {
                    window.location.href = "login.html";
                }
            },
            function(xhr, status) {
                layer.alert('获取用户资料出错，请稍候重试', {icon : 5}, function(index){ layer.close(index); });
                init_birth_select();
            }
        );
    }

    function alter_tel(email){
        window.location.href= "/personal/change_phone_new.html?e="+email+"&flag=true";
    }

    //初始化生日年月日下拉选
    function init_birth_select() {
        var currDate = new Date();
        var myDate = new Date();
        var birth = $("#birthday").val();
        if (birth != undefined && birth != "") {
            var year = birth.substring(0, 4);
            var month = birth.substring(5, 7);
            var day = birth.substring(8, 10);
            myDate = new Date(year, month - 1, day);
        }
        $("#calendar").DateSelector({
            ctlYearId: 'idYear',
            ctlMonthId: 'idMonth',
            ctlDayId: 'idDay',
            defYear: myDate.getFullYear(),
            defMonth: (myDate.getMonth()+1),
            defDay: myDate.getDate(),
            minYear: 1900,
            maxYear: currDate.getFullYear()
        });
    }

    //更换头像，该方法已暂停使用
    function init_uploader() {
        var uploader = WebUploader.create({
            pick : {
                id : '#change_head',
                innerHTML : '更换头像',
                multipart : false
            },
            accept : {
                title : '图片文件',
                extensions: 'gif,jpg,jpeg,bmp,png',
                mimeTypes: 'image/*'
            },
            thumb : {
                width: 90,
                height: 90,
                allowMagnify: false,
                crop: true
            },
            auto : false,
            threads : 1,
            runtimeOrder : 'html5, flash',
            swf : "../js/webuploader/Uploader.swf",
            server : "/imaging/imaging/upload",
            method : 'POST',
            fileVal : 'headImg',
            formData : {}
        });
        //调整选择文件按钮样式
        $("div.webuploader-pick").css({"background-color": "#117ad4", "padding": "8px 10px"});

        uploader.on('fileQueued', function(file) {
            var srcValue = 'data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iMjQyIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDI0MiAyMDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzEwMCV4MjAwCkNyZWF0ZWQgd2l0aCBIb2xkZXIuanMgMi42LjAuCkxlYXJuIG1vcmUgYXQgaHR0cDovL2hvbGRlcmpzLmNvbQooYykgMjAxMi0yMDE1IEl2YW4gTWFsb3BpbnNreSAtIGh0dHA6Ly9pbXNreS5jbwotLT48ZGVmcz48c3R5bGUgdHlwZT0idGV4dC9jc3MiPjwhW0NEQVRBWyNob2xkZXJfMTUwZDY2MGUxYzggdGV4dCB7IGZpbGw6I0FBQUFBQTtmb250LXdlaWdodDpib2xkO2ZvbnQtZmFtaWx5OkFyaWFsLCBIZWx2ZXRpY2EsIE9wZW4gU2Fucywgc2Fucy1zZXJpZiwgbW9ub3NwYWNlO2ZvbnQtc2l6ZToxMnB0IH0gXV0+PC9zdHlsZT48L2RlZnM+PGcgaWQ9ImhvbGRlcl8xNTBkNjYwZTFjOCI+PHJlY3Qgd2lkdGg9IjI0MiIgaGVpZ2h0PSIyMDAiIGZpbGw9IiNFRUVFRUUiLz48Zz48dGV4dCB4PSI4OS44NTAwMDAzODE0Njk3MyIgeT0iMTA1LjciPjI0MngyMDA8L3RleHQ+PC9nPjwvZz48L3N2Zz4=';
            uploader.makeThumb( file, function( error, ret ) {
                if ( !error ) {
                    srcValue = ret;
                }
                //console.log(srcValue);
                $("#head_img").attr({"src" : srcValue});
                var upload_btn_html = '<a href="javascript:upload_image(\'' + file.id + '\');" id="upload_head" style="border: 2px solid #4a7cc7; background-color: #4a7cc7; color: white; padding: 6px 10px; left: 300px; top: 134px; text-decoration: none;">上传头像</a>';
                if ($("#upload_head")[0] == undefined) {
                    $("#head_img").parent().parent().append(upload_btn_html);
                } else {
                    var fn = 'javascript:upload_image(\'' + file.id + '\');';
                    $("#upload_head").attr({"href" : fn});
                }
                upload_image = function(fileId) {
                    uploader.upload(fileId);
                };
            });
        });

        uploader.on('uploadBeforeSend', function(object, data, headers) {
            console.log("uploader.options--->" + JSON.stringify(uploader.options));
        });

        uploader.on('uploadAccept', function(block, response) {
            layer.msg(response.error, {icon : 1, time : 2000});
            if (response.error == undefined || response.error == "") {
                //通过return true来告诉组件，此文件上传成功。
                return true;
            } else {
                //通过return false来告诉组件，此文件上传有错。
                return false;
            }
        });

        uploader.on('uploadError', function(file) {
            var info = "图片“" + file.name + "”上传失败";
            layer.alert(info, {icon : 5}, function(index){ layer.close(index); });
            fix_layer_css();
        });

        uploader.on('uploadSuccess', function(file, response) {
            layer.msg(response.error + "，若要头像生效请保存修改！", {icon : 1, time : 2000});
            fix_layer_css();
            $("#upload_head").remove();
            $("input[name='headImg']").val(response.imgPath);
        });
    }

    //简介文本输入域
    function profile_count() {
        var words = $("#profile").val().length;
        if (words <= 500) {
            $("#profile_tip").text("字数不超过500，还可输入字数：" + (500 - words));
        } else {
            $("#profile").val($("#profile").val().substring(0, 500));
        }
    }

    //保存修改资料
    function save_alter_info() {
        var nn = $("input[name='nickName']").val().trim();
        var rn = $("input[name='realName']").val().trim();
        var year = $("select[name='year']").val();
        var month = $("select[name='month']").val();
        var day = $("select[name='day']").val();
        var pf = $("textarea[name='profile']").val();
        var CP_RE = /^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$/;
        var tel = $("input[name='telphone']").val();
        //if (nn.length > 0 && !(/^[a-zA-Z0-9\u4E00-\u9FFF]{3,20}$/.test(nn))) {
        //    layer.msg('请输入合理的名称', {icon : 5, time : 1000});
        //    fix_layer_css();
        //    return;
        //}
        if (nn.length < 3 || nn.length > 20) {
            layer.msg('请输入合理的名称', {icon : 5, time : 1000});
            fix_layer_css();
            return;
        }
        if (rn.length > 0 && !(/^[\u4E00-\u9FFF]{2,10}$/.test(rn))) {
            layer.msg('请输入合理的真实姓名', {icon : 5, time : 1000});
            fix_layer_css();
            return;
        }
        if (!(CP_RE.test(tel))) {
            layer.msg('手机号为空或格式有误，请重新输入', {icon : 5, time : 2000});
            fix_layer_css();
            return;
        }
        // var vdtUserCode = new RegExp(/[a-zA-Z\d]{6}/);
        //过滤空白字符(纯空格、纯换行)
        if (pf.length > 0 && /^\s+$/.test(pf)) {
            $("#profile").val("");
        }
        //生日不能超过当前日期
        var now = new Date();
        if (
            year > now.getFullYear() ||
            (year == now.getFullYear() && month > (now.getMonth() + 1)) ||
            (year == now.getFullYear() && month == (now.getMonth() + 1) && day > now.getDate())
        ) {
            layer.msg('生日不能超过当前日期，请重新选择', {icon : 5, time : 2000});
            fix_layer_css();
            return;
        }

        $.ajax({
            url : "../member/updateinfo",
            type : "POST",
            data : $("#alter_info_form").serialize(),
            dataType : "json",
            success : function(data) {
                if (data.errorCode == "0") {
                    toggle_menu_content(14);
                } else if (data.errorCode == "2") {
                    window.location.href = "login.html";
                } else {
                    layer.alert(data.errorInfo, {icon : 5}, function(index){ layer.close(index); });
                    fix_layer_css();
                }
            }
        });
    }

    return {
        init_user_info: init_user_info
    };
});