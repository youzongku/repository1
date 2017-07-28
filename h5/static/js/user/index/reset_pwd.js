/**
 * 菜单页面
 * Created by Administrator on 2016/9/12.
 */
require(['../../lib/common'], function (common) {
    require(['vue','zepto',"frozen"], function (Vue){
    	var vm = new Vue({
            el:'#body_pwd',
            data:{
               account:'',
               old_pwd:'',
               new_pwd:'',
               sure_pwd:'',
               saveSuccess:true,
               saveMsgStyle:'color: red;text-align: center; width: 100%',
               saveMsg:''
            },
            methods:{
                //保存密码
            	save:function(){
                    var PW_RE = /(?!^[0-9]+$)(?!^[a-zA-Z]+$)(?!^[^0-9a-zA-Z]+$)^.{6,20}$/;
                    var oldcode = this.old_pwd;
                    var newcode = this.new_pwd;
                    var surecode =this.sure_pwd;
                    if (!(PW_RE.test(newcode))) {
                        this.saveMsg = '密码必须为6-20个字符，且至少包含数字、字母(区分大小写)等两种或以上字符';
                        this.saveSuccess =false;
                        return;
                    }
                    if (newcode && surecode) {
                        if(newcode != surecode){
                            this.saveMsg = '两次输入的密码不正确，请重新输入！';
                            this.saveSuccess =false;
                            return;
                        }else{
                            var param = {
                                oldcode:oldcode,
                                newcode:newcode,
                                surecode:surecode
                            } 
                            var item = this;
                            ajax_post("/member/resetpwd", JSON.stringify(param), "application/json",
                                function(data) {
                                    var code = data.code;
                                    if (data.success) {
                                        item.saveSuccess =false;
                                        if (code == 1) {
                                            item.saveMsg = '请填写完整参数';
                                        } else if (code == 2) {
                                            item.saveMsg = '两次输入的密码不正确，请重新输入！';
                                        } else if (code == 3) {
                                            item.saveMsg = '旧密码错误，请确认后重新输入。';
                                        } else if (code == 4) {
                                            item.saveSuccess = true;
                                            var dia = $.dialog({
                                                title: '温馨提示',
                                                content: '保存成功，须重新登录',
                                                button: ["确认"]
                                            });
                                            dia.on("dialog:action",function(e){
                                                $.get("/member/adminlogout?" + (new Date()).getTime(), function (response) {
                                                    window.location.href = "/login.html";
                                                });
                                            });
                                        }
                                    } else {
                                        window.location.href = "/login.html";
                                    }
                                }
                            );

                        }
                    }
                }
            }
        });
        isLogin(function (email) {
            vm.account = email;
        });
		
    });
});